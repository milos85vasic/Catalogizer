import { HttpClient } from '../utils/http';
import {
  LoginRequest,
  LoginResponse,
  RegisterRequest,
  AuthStatus,
  User,
  UpdateProfileRequest,
  ChangePasswordRequest,
} from '../types';

export class AuthService {
  constructor(private http: HttpClient) {}

  /**
   * Authenticate user with username and password
   */
  public async login(credentials: LoginRequest): Promise<LoginResponse> {
    const response = await this.http.post<LoginResponse>('/auth/login', credentials);

    // Set the auth token for future requests
    this.http.setAuthToken(response.token);

    return response;
  }

  /**
   * Register a new user account
   */
  public async register(userData: RegisterRequest): Promise<User> {
    return this.http.post<User>('/auth/register', userData);
  }

  /**
   * Logout the current user
   */
  public async logout(): Promise<void> {
    try {
      await this.http.post<void>('/auth/logout');
    } finally {
      // Always clear the token, even if the request fails
      this.http.clearAuthToken();
    }
  }

  /**
   * Get current authentication status
   */
  public async getStatus(): Promise<AuthStatus> {
    return this.http.get<AuthStatus>('/auth/status');
  }

  /**
   * Refresh the authentication token
   */
  public async refreshToken(): Promise<{ token: string; expires_in: number }> {
    const response = await this.http.post<{ token: string; expires_in: number }>('/auth/refresh');

    // Update the auth token
    this.http.setAuthToken(response.token);

    return response;
  }

  /**
   * Get current user profile
   */
  public async getProfile(): Promise<User> {
    return this.http.get<User>('/auth/profile');
  }

  /**
   * Update user profile
   */
  public async updateProfile(updates: UpdateProfileRequest): Promise<User> {
    return this.http.put<User>('/auth/profile', updates);
  }

  /**
   * Change user password
   */
  public async changePassword(passwordData: ChangePasswordRequest): Promise<void> {
    return this.http.post<void>('/auth/password', passwordData);
  }

  /**
   * Request password reset
   */
  public async requestPasswordReset(email: string): Promise<void> {
    return this.http.post<void>('/auth/password/reset', { email });
  }

  /**
   * Reset password with token
   */
  public async resetPassword(token: string, newPassword: string): Promise<void> {
    return this.http.post<void>('/auth/password/reset/confirm', {
      token,
      password: newPassword,
    });
  }

  /**
   * Verify email address
   */
  public async verifyEmail(token: string): Promise<void> {
    return this.http.post<void>('/auth/email/verify', { token });
  }

  /**
   * Resend email verification
   */
  public async resendEmailVerification(): Promise<void> {
    return this.http.post<void>('/auth/email/verify/resend');
  }

  /**
   * Check if a username is available
   */
  public async checkUsernameAvailability(username: string): Promise<{ available: boolean }> {
    return this.http.get<{ available: boolean }>(`/auth/username/check?username=${encodeURIComponent(username)}`);
  }

  /**
   * Check if an email is available
   */
  public async checkEmailAvailability(email: string): Promise<{ available: boolean }> {
    return this.http.get<{ available: boolean }>(`/auth/email/check?email=${encodeURIComponent(email)}`);
  }

  /**
   * Get user permissions
   */
  public async getPermissions(): Promise<string[]> {
    return this.http.get<string[]>('/auth/permissions');
  }

  /**
   * Check if user has specific permission
   */
  public async hasPermission(permission: string): Promise<boolean> {
    const permissions = await this.getPermissions();
    return permissions.includes(permission) || permissions.includes('admin:system');
  }

  /**
   * Generate API key for the current user
   */
  public async generateApiKey(name: string): Promise<{ key: string; name: string; created_at: string }> {
    return this.http.post<{ key: string; name: string; created_at: string }>('/auth/api-keys', { name });
  }

  /**
   * List user's API keys
   */
  public async listApiKeys(): Promise<Array<{ id: number; name: string; created_at: string; last_used?: string }>> {
    return this.http.get<Array<{ id: number; name: string; created_at: string; last_used?: string }>>('/auth/api-keys');
  }

  /**
   * Revoke an API key
   */
  public async revokeApiKey(keyId: number): Promise<void> {
    return this.http.delete<void>(`/auth/api-keys/${keyId}`);
  }
}