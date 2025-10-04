import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';
import {
  ApiResponse,
  ClientConfig,
  CatalogizerError,
  AuthenticationError,
  NetworkError,
  ValidationError,
} from '../types';

export class HttpClient {
  private client: AxiosInstance;
  private config: ClientConfig;
  private authToken?: string;

  constructor(config: ClientConfig) {
    this.config = config;
    this.client = axios.create({
      baseURL: config.baseURL,
      timeout: config.timeout || 30000,
      headers: {
        'Content-Type': 'application/json',
        ...config.headers,
      },
    });

    this.setupInterceptors();
  }

  private setupInterceptors(): void {
    // Request interceptor to add auth token
    this.client.interceptors.request.use(
      (config) => {
        if (this.authToken) {
          config.headers.Authorization = `Bearer ${this.authToken}`;
        }
        return config;
      },
      (error) => Promise.reject(error)
    );

    // Response interceptor to handle errors
    this.client.interceptors.response.use(
      (response) => response,
      async (error) => {
        const originalRequest = error.config;

        if (error.response?.status === 401 && !originalRequest._retry) {
          originalRequest._retry = true;

          // Try to refresh token if possible
          if (this.onTokenRefresh) {
            try {
              const newToken = await this.onTokenRefresh();
              if (newToken) {
                this.setAuthToken(newToken);
                originalRequest.headers.Authorization = `Bearer ${newToken}`;
                return this.client(originalRequest);
              }
            } catch (refreshError) {
              // Token refresh failed, user needs to login again
              if (this.onAuthenticationError) {
                this.onAuthenticationError();
              }
            }
          }
        }

        return Promise.reject(this.handleError(error));
      }
    );
  }

  private handleError(error: any): CatalogizerError {
    if (!error.response) {
      // Network error
      return new NetworkError('Network connection failed');
    }

    const { status, data } = error.response;
    const message = data?.message || data?.error || error.message || 'Request failed';

    switch (status) {
      case 400:
        return new ValidationError(message);
      case 401:
        return new AuthenticationError(message);
      case 403:
        return new CatalogizerError('Access forbidden', status, 'FORBIDDEN');
      case 404:
        return new CatalogizerError('Resource not found', status, 'NOT_FOUND');
      case 500:
        return new CatalogizerError('Internal server error', status, 'SERVER_ERROR');
      default:
        return new CatalogizerError(message, status);
    }
  }

  public setAuthToken(token: string): void {
    this.authToken = token;
  }

  public clearAuthToken(): void {
    this.authToken = undefined;
  }

  public getAuthToken(): string | undefined {
    return this.authToken;
  }

  // Callback handlers
  public onTokenRefresh?: () => Promise<string | null>;
  public onAuthenticationError?: () => void;

  // HTTP methods
  public async get<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.client.get<ApiResponse<T>>(url, config);
    return this.extractData(response);
  }

  public async post<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.client.post<ApiResponse<T>>(url, data, config);
    return this.extractData(response);
  }

  public async put<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.client.put<ApiResponse<T>>(url, data, config);
    return this.extractData(response);
  }

  public async patch<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.client.patch<ApiResponse<T>>(url, data, config);
    return this.extractData(response);
  }

  public async delete<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.client.delete<ApiResponse<T>>(url, config);
    return this.extractData(response);
  }

  private extractData<T>(response: AxiosResponse<ApiResponse<T>>): T {
    const { data: responseData } = response;

    // If the response has a success field and it's false, throw an error
    if (responseData.success === false) {
      throw new CatalogizerError(
        responseData.error || responseData.message || 'Request failed',
        responseData.status
      );
    }

    // Return the data field if it exists, otherwise return the entire response data
    return responseData.data !== undefined ? responseData.data : (responseData as unknown as T);
  }

  // Stream methods for file downloads/uploads
  public async downloadStream(url: string, config?: AxiosRequestConfig): Promise<ArrayBuffer> {
    const response = await this.client.get(url, {
      ...config,
      responseType: 'arraybuffer',
    });
    return response.data;
  }

  public async uploadFile(url: string, file: File | Buffer, config?: AxiosRequestConfig): Promise<any> {
    const formData = new FormData();
    formData.append('file', file);

    return this.client.post(url, formData, {
      ...config,
      headers: {
        ...config?.headers,
        'Content-Type': 'multipart/form-data',
      },
    });
  }

  // Retry mechanism
  public async withRetry<T>(
    operation: () => Promise<T>,
    maxAttempts: number = this.config.retryAttempts || 3,
    delay: number = this.config.retryDelay || 1000
  ): Promise<T> {
    let lastError: Error;

    for (let attempt = 1; attempt <= maxAttempts; attempt++) {
      try {
        return await operation();
      } catch (error) {
        lastError = error as Error;

        // Don't retry on authentication or validation errors
        if (error instanceof AuthenticationError || error instanceof ValidationError) {
          throw error;
        }

        if (attempt === maxAttempts) {
          break;
        }

        // Wait before retrying
        await new Promise(resolve => setTimeout(resolve, delay * attempt));
      }
    }

    throw lastError!;
  }

  // Update configuration
  public updateConfig(newConfig: Partial<ClientConfig>): void {
    this.config = { ...this.config, ...newConfig };

    if (newConfig.baseURL) {
      this.client.defaults.baseURL = newConfig.baseURL;
    }

    if (newConfig.timeout) {
      this.client.defaults.timeout = newConfig.timeout;
    }

    if (newConfig.headers) {
      this.client.defaults.headers = {
        ...this.client.defaults.headers,
        ...newConfig.headers,
      };
    }
  }
}