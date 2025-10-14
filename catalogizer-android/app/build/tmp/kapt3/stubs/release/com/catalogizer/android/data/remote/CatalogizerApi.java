package com.catalogizer.android.data.remote;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u008c\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u0006\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0005\bf\u0018\u00002\u00020\u0001J\u001e\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0007J\u001e\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0007J\u001e\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\n\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\fJ\u001e\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u000e\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0007J\u0014\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00100\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0011J \u0010\u0012\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00010\u00130\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0011J\u001a\u0010\u0015\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00170\u00160\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0011J \u0010\u0018\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00010\u00130\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0011J*\u0010\u0019\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00140\u00130\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0007J$\u0010\u001a\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001b0\u00160\u00032\b\b\u0001\u0010\u000e\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0007J(\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u001d0\u00032\b\b\u0003\u0010\u001e\u001a\u00020\u001f2\b\b\u0003\u0010 \u001a\u00020\u001fH\u00a7@\u00a2\u0006\u0002\u0010!J\u001e\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u00170\u00032\b\b\u0001\u0010\u000e\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0007J\u0014\u0010#\u001a\b\u0012\u0004\u0012\u00020$0\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0011J \u0010%\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00010\u00130\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0011J$\u0010&\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00170\u00160\u00032\b\b\u0003\u0010\u001e\u001a\u00020\u001fH\u00a7@\u00a2\u0006\u0002\u0010\'J\u0014\u0010(\u001a\b\u0012\u0004\u0012\u00020)0\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0011J*\u0010*\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00010\u00130\u00032\b\b\u0001\u0010\u000e\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0007J$\u0010+\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00170\u00160\u00032\b\b\u0003\u0010\u001e\u001a\u00020\u001fH\u00a7@\u00a2\u0006\u0002\u0010\'J \u0010,\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00010\u00130\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0011J \u0010-\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00010\u00130\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0011J*\u0010.\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00140\u00130\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0007J \u0010/\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00010\u00130\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0011J \u00100\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00010\u00130\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0011J$\u00101\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00170\u00160\u00032\b\b\u0001\u00102\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0007J \u00103\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00010\u00130\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0011J(\u00104\u001a\b\u0012\u0004\u0012\u00020\u001d0\u00032\b\b\u0003\u0010\u001e\u001a\u00020\u001f2\b\b\u0003\u0010 \u001a\u00020\u001fH\u00a7@\u00a2\u0006\u0002\u0010!J\u001e\u00105\u001a\b\u0012\u0004\u0012\u0002060\u00032\b\b\u0001\u00107\u001a\u000208H\u00a7@\u00a2\u0006\u0002\u00109J\u0014\u0010:\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0011J4\u0010;\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u000e\u001a\u00020\u00062\u0014\b\u0001\u0010<\u001a\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00010\u0013H\u00a7@\u00a2\u0006\u0002\u0010=J\u001e\u0010>\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u000e\u001a\u00020\u0014H\u00a7@\u00a2\u0006\u0002\u0010?J*\u0010@\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00140\u00130\u00032\b\b\u0001\u0010\u000e\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0007J\u001e\u0010A\u001a\b\u0012\u0004\u0012\u00020)0\u00032\b\b\u0001\u0010B\u001a\u00020CH\u00a7@\u00a2\u0006\u0002\u0010DJ\u001e\u0010E\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0007J\u001e\u0010F\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0007J\u0088\u0001\u0010G\u001a\b\u0012\u0004\u0012\u00020\u001d0\u00032\n\b\u0003\u0010H\u001a\u0004\u0018\u00010\u00142\n\b\u0003\u0010I\u001a\u0004\u0018\u00010\u00142\n\b\u0003\u0010J\u001a\u0004\u0018\u00010\u001f2\n\b\u0003\u0010K\u001a\u0004\u0018\u00010\u001f2\n\b\u0003\u0010L\u001a\u0004\u0018\u00010M2\n\b\u0003\u0010N\u001a\u0004\u0018\u00010\u00142\n\b\u0003\u0010O\u001a\u0004\u0018\u00010\u00142\n\b\u0003\u0010P\u001a\u0004\u0018\u00010\u00142\b\b\u0003\u0010\u001e\u001a\u00020\u001f2\b\b\u0003\u0010 \u001a\u00020\u001fH\u00a7@\u00a2\u0006\u0002\u0010QJ4\u0010R\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u000e\u001a\u00020\u00062\u0014\b\u0001\u0010S\u001a\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00010\u0013H\u00a7@\u00a2\u0006\u0002\u0010=J(\u0010T\u001a\b\u0012\u0004\u0012\u00020\u00170\u00032\b\b\u0001\u0010\u000e\u001a\u00020\u00062\b\b\u0001\u0010U\u001a\u00020\u0017H\u00a7@\u00a2\u0006\u0002\u0010VJ\u001e\u0010W\u001a\b\u0012\u0004\u0012\u00020)0\u00032\b\b\u0001\u0010X\u001a\u00020YH\u00a7@\u00a2\u0006\u0002\u0010ZJ4\u0010[\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\u0014\b\u0001\u0010\\\u001a\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00010\u0013H\u00a7@\u00a2\u0006\u0002\u0010=J4\u0010]\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u000e\u001a\u00020\u00062\u0014\b\u0001\u0010\\\u001a\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00010\u0013H\u00a7@\u00a2\u0006\u0002\u0010=\u00a8\u0006^"}, d2 = {"Lcom/catalogizer/android/data/remote/CatalogizerApi;", "", "addToFavorites", "Lretrofit2/Response;", "", "mediaId", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "addToWatchlist", "changePassword", "changePasswordRequest", "Lcom/catalogizer/android/data/models/ChangePasswordRequest;", "(Lcom/catalogizer/android/data/models/ChangePasswordRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteMedia", "id", "getAuthStatus", "Lcom/catalogizer/android/data/models/AuthStatus;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getChartsData", "", "", "getContinueWatching", "", "Lcom/catalogizer/android/data/models/MediaItem;", "getDashboardData", "getDownloadUrl", "getExternalMetadata", "Lcom/catalogizer/android/data/models/ExternalMetadata;", "getFavorites", "Lcom/catalogizer/android/data/models/MediaSearchResponse;", "limit", "", "offset", "(IILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getMediaById", "getMediaStats", "Lcom/catalogizer/android/data/models/MediaStats;", "getPermissions", "getPopularMedia", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getProfile", "Lcom/catalogizer/android/data/models/User;", "getQualityInfo", "getRecentMedia", "getSMBHealth", "getSMBStatus", "getStreamUrl", "getSystemHealth", "getSystemStatus", "getUpdatedMedia", "since", "getUserPreferences", "getWatchlist", "login", "Lcom/catalogizer/android/data/models/LoginResponse;", "loginRequest", "Lcom/catalogizer/android/data/models/LoginRequest;", "(Lcom/catalogizer/android/data/models/LoginRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "logout", "rateMedia", "ratingData", "(JLjava/util/Map;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "reconnectSMBSource", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "refreshMetadata", "register", "registerRequest", "Lcom/catalogizer/android/data/models/RegisterRequest;", "(Lcom/catalogizer/android/data/models/RegisterRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "removeFromFavorites", "removeFromWatchlist", "searchMedia", "query", "mediaType", "yearMin", "yearMax", "ratingMin", "", "quality", "sortBy", "sortOrder", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/Integer;Ljava/lang/Double;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setFavoriteStatus", "favoriteData", "updateMedia", "mediaItem", "(JLcom/catalogizer/android/data/models/MediaItem;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateProfile", "updateProfileRequest", "Lcom/catalogizer/android/data/models/UpdateProfileRequest;", "(Lcom/catalogizer/android/data/models/UpdateProfileRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateUserWatchProgress", "progressData", "updateWatchProgress", "app_release"})
public abstract interface CatalogizerApi {
    
    @retrofit2.http.POST(value = "auth/login")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object login(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.catalogizer.android.data.models.LoginRequest loginRequest, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.catalogizer.android.data.models.LoginResponse>> $completion);
    
    @retrofit2.http.POST(value = "auth/register")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object register(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.catalogizer.android.data.models.RegisterRequest registerRequest, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.catalogizer.android.data.models.User>> $completion);
    
    @retrofit2.http.POST(value = "auth/logout")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object logout(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<kotlin.Unit>> $completion);
    
    @retrofit2.http.GET(value = "auth/profile")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getProfile(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.catalogizer.android.data.models.User>> $completion);
    
    @retrofit2.http.PUT(value = "auth/profile")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateProfile(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.catalogizer.android.data.models.UpdateProfileRequest updateProfileRequest, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.catalogizer.android.data.models.User>> $completion);
    
    @retrofit2.http.POST(value = "auth/change-password")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object changePassword(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.catalogizer.android.data.models.ChangePasswordRequest changePasswordRequest, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<kotlin.Unit>> $completion);
    
    @retrofit2.http.GET(value = "auth/status")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getAuthStatus(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.catalogizer.android.data.models.AuthStatus>> $completion);
    
    @retrofit2.http.GET(value = "auth/permissions")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getPermissions(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<java.util.Map<java.lang.String, java.lang.Object>>> $completion);
    
    @retrofit2.http.GET(value = "media/search")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object searchMedia(@retrofit2.http.Query(value = "query")
    @org.jetbrains.annotations.Nullable()
    java.lang.String query, @retrofit2.http.Query(value = "media_type")
    @org.jetbrains.annotations.Nullable()
    java.lang.String mediaType, @retrofit2.http.Query(value = "year_min")
    @org.jetbrains.annotations.Nullable()
    java.lang.Integer yearMin, @retrofit2.http.Query(value = "year_max")
    @org.jetbrains.annotations.Nullable()
    java.lang.Integer yearMax, @retrofit2.http.Query(value = "rating_min")
    @org.jetbrains.annotations.Nullable()
    java.lang.Double ratingMin, @retrofit2.http.Query(value = "quality")
    @org.jetbrains.annotations.Nullable()
    java.lang.String quality, @retrofit2.http.Query(value = "sort_by")
    @org.jetbrains.annotations.Nullable()
    java.lang.String sortBy, @retrofit2.http.Query(value = "sort_order")
    @org.jetbrains.annotations.Nullable()
    java.lang.String sortOrder, @retrofit2.http.Query(value = "limit")
    int limit, @retrofit2.http.Query(value = "offset")
    int offset, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.catalogizer.android.data.models.MediaSearchResponse>> $completion);
    
    @retrofit2.http.GET(value = "media/{id}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getMediaById(@retrofit2.http.Path(value = "id")
    long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.catalogizer.android.data.models.MediaItem>> $completion);
    
    @retrofit2.http.GET(value = "media/{id}/metadata")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getExternalMetadata(@retrofit2.http.Path(value = "id")
    long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<java.util.List<com.catalogizer.android.data.models.ExternalMetadata>>> $completion);
    
    @retrofit2.http.POST(value = "media/{id}/refresh")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object refreshMetadata(@retrofit2.http.Path(value = "id")
    long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<java.util.Map<java.lang.String, java.lang.String>>> $completion);
    
    @retrofit2.http.GET(value = "media/{id}/quality")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getQualityInfo(@retrofit2.http.Path(value = "id")
    long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<java.util.Map<java.lang.String, java.lang.Object>>> $completion);
    
    @retrofit2.http.GET(value = "media/stats")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getMediaStats(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.catalogizer.android.data.models.MediaStats>> $completion);
    
    @retrofit2.http.GET(value = "media/recent")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getRecentMedia(@retrofit2.http.Query(value = "limit")
    int limit, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<java.util.List<com.catalogizer.android.data.models.MediaItem>>> $completion);
    
    @retrofit2.http.GET(value = "media/popular")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getPopularMedia(@retrofit2.http.Query(value = "limit")
    int limit, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<java.util.List<com.catalogizer.android.data.models.MediaItem>>> $completion);
    
    @retrofit2.http.DELETE(value = "media/{id}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteMedia(@retrofit2.http.Path(value = "id")
    long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<kotlin.Unit>> $completion);
    
    @retrofit2.http.PUT(value = "media/{id}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateMedia(@retrofit2.http.Path(value = "id")
    long id, @retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.catalogizer.android.data.models.MediaItem mediaItem, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.catalogizer.android.data.models.MediaItem>> $completion);
    
    @retrofit2.http.PUT(value = "media/{id}/progress")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateWatchProgress(@retrofit2.http.Path(value = "id")
    long id, @retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, ? extends java.lang.Object> progressData, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<kotlin.Unit>> $completion);
    
    @retrofit2.http.PUT(value = "media/{id}/favorite")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object setFavoriteStatus(@retrofit2.http.Path(value = "id")
    long id, @retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, ? extends java.lang.Object> favoriteData, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<kotlin.Unit>> $completion);
    
    @retrofit2.http.PUT(value = "media/{id}/rating")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object rateMedia(@retrofit2.http.Path(value = "id")
    long id, @retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, ? extends java.lang.Object> ratingData, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<kotlin.Unit>> $completion);
    
    @retrofit2.http.GET(value = "user/preferences")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getUserPreferences(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<java.util.Map<java.lang.String, java.lang.Object>>> $completion);
    
    @retrofit2.http.GET(value = "media/updated")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getUpdatedMedia(@retrofit2.http.Query(value = "since")
    long since, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<java.util.List<com.catalogizer.android.data.models.MediaItem>>> $completion);
    
    @retrofit2.http.GET(value = "smb/sources/status")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getSMBStatus(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<java.util.Map<java.lang.String, java.lang.Object>>> $completion);
    
    @retrofit2.http.POST(value = "smb/sources/{id}/reconnect")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object reconnectSMBSource(@retrofit2.http.Path(value = "id")
    @org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<kotlin.Unit>> $completion);
    
    @retrofit2.http.GET(value = "smb/health")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getSMBHealth(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<java.util.Map<java.lang.String, java.lang.Object>>> $completion);
    
    @retrofit2.http.GET(value = "analytics/dashboard")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getDashboardData(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<java.util.Map<java.lang.String, java.lang.Object>>> $completion);
    
    @retrofit2.http.GET(value = "analytics/charts")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getChartsData(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<java.util.Map<java.lang.String, java.lang.Object>>> $completion);
    
    @retrofit2.http.GET(value = "health")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getSystemHealth(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<java.util.Map<java.lang.String, java.lang.Object>>> $completion);
    
    @retrofit2.http.GET(value = "status")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getSystemStatus(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<java.util.Map<java.lang.String, java.lang.Object>>> $completion);
    
    @retrofit2.http.GET(value = "stream/{mediaId}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getStreamUrl(@retrofit2.http.Path(value = "mediaId")
    long mediaId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<java.util.Map<java.lang.String, java.lang.String>>> $completion);
    
    @retrofit2.http.GET(value = "download/{mediaId}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getDownloadUrl(@retrofit2.http.Path(value = "mediaId")
    long mediaId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<java.util.Map<java.lang.String, java.lang.String>>> $completion);
    
    @retrofit2.http.GET(value = "user/favorites")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getFavorites(@retrofit2.http.Query(value = "limit")
    int limit, @retrofit2.http.Query(value = "offset")
    int offset, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.catalogizer.android.data.models.MediaSearchResponse>> $completion);
    
    @retrofit2.http.POST(value = "user/favorites/{mediaId}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object addToFavorites(@retrofit2.http.Path(value = "mediaId")
    long mediaId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<kotlin.Unit>> $completion);
    
    @retrofit2.http.DELETE(value = "user/favorites/{mediaId}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object removeFromFavorites(@retrofit2.http.Path(value = "mediaId")
    long mediaId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<kotlin.Unit>> $completion);
    
    @retrofit2.http.GET(value = "user/watchlist")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getWatchlist(@retrofit2.http.Query(value = "limit")
    int limit, @retrofit2.http.Query(value = "offset")
    int offset, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.catalogizer.android.data.models.MediaSearchResponse>> $completion);
    
    @retrofit2.http.POST(value = "user/watchlist/{mediaId}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object addToWatchlist(@retrofit2.http.Path(value = "mediaId")
    long mediaId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<kotlin.Unit>> $completion);
    
    @retrofit2.http.DELETE(value = "user/watchlist/{mediaId}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object removeFromWatchlist(@retrofit2.http.Path(value = "mediaId")
    long mediaId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<kotlin.Unit>> $completion);
    
    @retrofit2.http.PUT(value = "user/progress/{mediaId}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateUserWatchProgress(@retrofit2.http.Path(value = "mediaId")
    long mediaId, @retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, ? extends java.lang.Object> progressData, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<kotlin.Unit>> $completion);
    
    @retrofit2.http.GET(value = "user/continue-watching")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getContinueWatching(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<java.util.List<com.catalogizer.android.data.models.MediaItem>>> $completion);
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
    }
}