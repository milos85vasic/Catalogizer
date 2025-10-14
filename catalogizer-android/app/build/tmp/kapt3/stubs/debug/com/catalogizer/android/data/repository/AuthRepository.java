package com.catalogizer.android.data.repository;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000d\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u001a\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u0000 O2\u00020\u0001:\u0001OB\u001d\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\u0002\u0010\u0007J\u001e\u0010\u001c\u001a\u00020\u000e2\u0006\u0010\u001d\u001a\u00020\n2\u0006\u0010\u001e\u001a\u00020\nH\u0086@\u00a2\u0006\u0002\u0010\u001fJ$\u0010 \u001a\b\u0012\u0004\u0012\u00020\"0!2\u0006\u0010#\u001a\u00020\n2\u0006\u0010$\u001a\u00020\nH\u0086@\u00a2\u0006\u0002\u0010\u001fJ\u0014\u0010%\u001a\b\u0012\u0004\u0012\u00020&0!H\u0086@\u00a2\u0006\u0002\u0010\'J\u000e\u0010(\u001a\u00020\"H\u0082@\u00a2\u0006\u0002\u0010\'J\u0014\u0010)\u001a\b\u0012\u0004\u0012\u00020\u00110!H\u0086@\u00a2\u0006\u0002\u0010\'J\u000e\u0010*\u001a\u00020\nH\u0086@\u00a2\u0006\u0002\u0010\'J\u000e\u0010+\u001a\u00020\nH\u0086@\u00a2\u0006\u0002\u0010\'J\u0010\u0010,\u001a\u0004\u0018\u00010\nH\u0086@\u00a2\u0006\u0002\u0010\'J\u0016\u0010-\u001a\u00020\u000e2\u0006\u0010.\u001a\u00020\nH\u0086@\u00a2\u0006\u0002\u0010/J\u000e\u0010\u0013\u001a\u00020\u000eH\u0086@\u00a2\u0006\u0002\u0010\'J\u000e\u00100\u001a\u00020\u000eH\u0086@\u00a2\u0006\u0002\u0010\'J.\u00101\u001a\b\u0012\u0004\u0012\u0002020!2\u0006\u00103\u001a\u00020\n2\u0006\u00104\u001a\u00020\n2\b\b\u0002\u0010\u0018\u001a\u00020\u000eH\u0086@\u00a2\u0006\u0002\u00105J\u0014\u00106\u001a\b\u0012\u0004\u0012\u00020\"0!H\u0086@\u00a2\u0006\u0002\u0010\'J\f\u00107\u001a\b\u0012\u0004\u0012\u00020\u000e0\tJ\u0014\u00108\u001a\b\u0012\u0004\u0012\u00020\n0!H\u0086@\u00a2\u0006\u0002\u0010\'J<\u00109\u001a\b\u0012\u0004\u0012\u00020\u00110!2\u0006\u00103\u001a\u00020\n2\u0006\u0010:\u001a\u00020\n2\u0006\u00104\u001a\u00020\n2\u0006\u0010;\u001a\u00020\n2\u0006\u0010<\u001a\u00020\nH\u0086@\u00a2\u0006\u0002\u0010=J\u001e\u0010>\u001a\u00020\"2\u0006\u0010?\u001a\u0002022\u0006\u0010\u0018\u001a\u00020\u000eH\u0082@\u00a2\u0006\u0002\u0010@J\u0016\u0010A\u001a\u00020\"2\u0006\u0010B\u001a\u00020\u0011H\u0082@\u00a2\u0006\u0002\u0010CJ\u0016\u0010D\u001a\u00020\"2\u0006\u0010E\u001a\u00020\u000eH\u0086@\u00a2\u0006\u0002\u0010FJ\u0016\u0010G\u001a\u00020\"2\u0006\u0010H\u001a\u00020\u000eH\u0086@\u00a2\u0006\u0002\u0010FJ\u0016\u0010I\u001a\u00020\"2\u0006\u0010J\u001a\u00020\nH\u0086@\u00a2\u0006\u0002\u0010/J\u001c\u0010K\u001a\b\u0012\u0004\u0012\u00020\u00110!2\u0006\u0010L\u001a\u00020MH\u0086@\u00a2\u0006\u0002\u0010NR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\b\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\n0\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0017\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\fR\u0019\u0010\u0010\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00110\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\fR\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u000e0\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\fR\u000e\u0010\u0014\u001a\u00020\u0015X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\u0016\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\n0\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\fR\u0017\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u000e0\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\fR\u0019\u0010\u001a\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\n0\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\f\u00a8\u0006P"}, d2 = {"Lcom/catalogizer/android/data/repository/AuthRepository;", "", "api", "Lcom/catalogizer/android/data/remote/CatalogizerApi;", "dataStore", "Landroidx/datastore/core/DataStore;", "Landroidx/datastore/preferences/core/Preferences;", "(Lcom/catalogizer/android/data/remote/CatalogizerApi;Landroidx/datastore/core/DataStore;)V", "authToken", "Lkotlinx/coroutines/flow/Flow;", "", "getAuthToken", "()Lkotlinx/coroutines/flow/Flow;", "biometricEnabled", "", "getBiometricEnabled", "currentUser", "Lcom/catalogizer/android/data/models/User;", "getCurrentUser", "isAuthenticated", "json", "Lkotlinx/serialization/json/Json;", "refreshToken", "getRefreshToken", "rememberMe", "getRememberMe", "serverUrl", "getServerUrl", "canAccess", "resource", "action", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "changePassword", "Lcom/catalogizer/android/data/remote/ApiResult;", "", "currentPassword", "newPassword", "checkAuthStatus", "Lcom/catalogizer/android/data/models/AuthStatus;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "clearAuthData", "getProfile", "getUserDisplayName", "getUserInitials", "getValidToken", "hasPermission", "permission", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "isTokenValid", "login", "Lcom/catalogizer/android/data/models/LoginResponse;", "username", "password", "(Ljava/lang/String;Ljava/lang/String;ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "logout", "observeTokenExpiry", "refreshAuthToken", "register", "email", "firstName", "lastName", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "saveAuthData", "loginResponse", "(Lcom/catalogizer/android/data/models/LoginResponse;ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "saveUser", "user", "(Lcom/catalogizer/android/data/models/User;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setBiometricEnabled", "enabled", "(ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setRememberMe", "remember", "setServerUrl", "url", "updateProfile", "updateRequest", "Lcom/catalogizer/android/data/models/UpdateProfileRequest;", "(Lcom/catalogizer/android/data/models/UpdateProfileRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "Companion", "app_debug"})
public final class AuthRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.catalogizer.android.data.remote.CatalogizerApi api = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences> dataStore = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.serialization.json.Json json = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> AUTH_TOKEN_KEY = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> REFRESH_TOKEN_KEY = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> USER_KEY = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Long> TOKEN_EXPIRY_KEY = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> SERVER_URL_KEY = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> REMEMBER_ME_KEY = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> BIOMETRIC_ENABLED_KEY = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.lang.String> authToken = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.lang.String> refreshToken = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<com.catalogizer.android.data.models.User> currentUser = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.lang.Boolean> isAuthenticated = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.lang.String> serverUrl = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.lang.Boolean> rememberMe = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.lang.Boolean> biometricEnabled = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.catalogizer.android.data.repository.AuthRepository.Companion Companion = null;
    
    @javax.inject.Inject()
    public AuthRepository(@org.jetbrains.annotations.NotNull()
    com.catalogizer.android.data.remote.CatalogizerApi api, @org.jetbrains.annotations.NotNull()
    androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences> dataStore) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.lang.String> getAuthToken() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.lang.String> getRefreshToken() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<com.catalogizer.android.data.models.User> getCurrentUser() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.lang.Boolean> isAuthenticated() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.lang.String> getServerUrl() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.lang.Boolean> getRememberMe() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.lang.Boolean> getBiometricEnabled() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object login(@org.jetbrains.annotations.NotNull()
    java.lang.String username, @org.jetbrains.annotations.NotNull()
    java.lang.String password, boolean rememberMe, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.catalogizer.android.data.remote.ApiResult<com.catalogizer.android.data.models.LoginResponse>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object register(@org.jetbrains.annotations.NotNull()
    java.lang.String username, @org.jetbrains.annotations.NotNull()
    java.lang.String email, @org.jetbrains.annotations.NotNull()
    java.lang.String password, @org.jetbrains.annotations.NotNull()
    java.lang.String firstName, @org.jetbrains.annotations.NotNull()
    java.lang.String lastName, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.catalogizer.android.data.remote.ApiResult<com.catalogizer.android.data.models.User>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object logout(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.catalogizer.android.data.remote.ApiResult<kotlin.Unit>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object refreshAuthToken(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.catalogizer.android.data.remote.ApiResult<java.lang.String>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getProfile(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.catalogizer.android.data.remote.ApiResult<com.catalogizer.android.data.models.User>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object updateProfile(@org.jetbrains.annotations.NotNull()
    com.catalogizer.android.data.models.UpdateProfileRequest updateRequest, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.catalogizer.android.data.remote.ApiResult<com.catalogizer.android.data.models.User>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object changePassword(@org.jetbrains.annotations.NotNull()
    java.lang.String currentPassword, @org.jetbrains.annotations.NotNull()
    java.lang.String newPassword, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.catalogizer.android.data.remote.ApiResult<kotlin.Unit>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object checkAuthStatus(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.catalogizer.android.data.remote.ApiResult<com.catalogizer.android.data.models.AuthStatus>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object setServerUrl(@org.jetbrains.annotations.NotNull()
    java.lang.String url, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object setBiometricEnabled(boolean enabled, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object setRememberMe(boolean remember, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object hasPermission(@org.jetbrains.annotations.NotNull()
    java.lang.String permission, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object canAccess(@org.jetbrains.annotations.NotNull()
    java.lang.String resource, @org.jetbrains.annotations.NotNull()
    java.lang.String action, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
    
    private final java.lang.Object saveAuthData(com.catalogizer.android.data.models.LoginResponse loginResponse, boolean rememberMe, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final java.lang.Object saveUser(com.catalogizer.android.data.models.User user, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final java.lang.Object clearAuthData(kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object isTokenValid(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object isAuthenticated(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getValidToken(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getUserDisplayName(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getUserInitials(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.lang.Boolean> observeTokenExpiry() {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00070\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00070\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000e"}, d2 = {"Lcom/catalogizer/android/data/repository/AuthRepository$Companion;", "", "()V", "AUTH_TOKEN_KEY", "Landroidx/datastore/preferences/core/Preferences$Key;", "", "BIOMETRIC_ENABLED_KEY", "", "REFRESH_TOKEN_KEY", "REMEMBER_ME_KEY", "SERVER_URL_KEY", "TOKEN_EXPIRY_KEY", "", "USER_KEY", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}