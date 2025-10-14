package com.catalogizer.android.data.models;

@kotlinx.serialization.Serializable()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000l\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u0006\n\u0002\b\b\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\bA\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0087\b\u0018\u0000 r2\u00020\u0001:\u0002qrB\u00f5\u0001\b\u0011\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0001\u0010\b\u001a\u0004\u0018\u00010\u0007\u0012\b\u0010\t\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\n\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0001\u0010\u000b\u001a\u0004\u0018\u00010\u0007\u0012\b\u0010\f\u001a\u0004\u0018\u00010\r\u0012\b\u0010\u000e\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0001\u0010\u000f\u001a\u0004\u0018\u00010\u0005\u0012\b\u0010\u0010\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0001\u0010\u0011\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0001\u0010\u0012\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0001\u0010\u0013\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0001\u0010\u0014\u001a\u0004\u0018\u00010\u0007\u0012\u0010\b\u0001\u0010\u0015\u001a\n\u0012\u0004\u0012\u00020\u0017\u0018\u00010\u0016\u0012\u000e\u0010\u0018\u001a\n\u0012\u0004\u0012\u00020\u0019\u0018\u00010\u0016\u0012\u0006\u0010\u001a\u001a\u00020\u001b\u0012\u0006\u0010\u001c\u001a\u00020\r\u0012\b\u0010\u001d\u001a\u0004\u0018\u00010\u0007\u0012\u0006\u0010\u001e\u001a\u00020\u001b\u0012\b\u0010\u001f\u001a\u0004\u0018\u00010 \u00a2\u0006\u0002\u0010!B\u00e3\u0001\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0007\u0012\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\r\u0012\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0011\u001a\u00020\u0007\u0012\n\b\u0002\u0010\u0012\u001a\u0004\u0018\u00010\u0007\u0012\u0006\u0010\u0013\u001a\u00020\u0007\u0012\u0006\u0010\u0014\u001a\u00020\u0007\u0012\u0010\b\u0002\u0010\u0015\u001a\n\u0012\u0004\u0012\u00020\u0017\u0018\u00010\u0016\u0012\u0010\b\u0002\u0010\u0018\u001a\n\u0012\u0004\u0012\u00020\u0019\u0018\u00010\u0016\u0012\b\b\u0002\u0010\u001a\u001a\u00020\u001b\u0012\b\b\u0002\u0010\u001c\u001a\u00020\r\u0012\n\b\u0002\u0010\u001d\u001a\u0004\u0018\u00010\u0007\u0012\b\b\u0002\u0010\u001e\u001a\u00020\u001b\u00a2\u0006\u0002\u0010\"J\t\u0010I\u001a\u00020\u0005H\u00c6\u0003J\u0010\u0010J\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010-J\t\u0010K\u001a\u00020\u0007H\u00c6\u0003J\u000b\u0010L\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\t\u0010M\u001a\u00020\u0007H\u00c6\u0003J\t\u0010N\u001a\u00020\u0007H\u00c6\u0003J\u0011\u0010O\u001a\n\u0012\u0004\u0012\u00020\u0017\u0018\u00010\u0016H\u00c6\u0003J\u0011\u0010P\u001a\n\u0012\u0004\u0012\u00020\u0019\u0018\u00010\u0016H\u00c6\u0003J\t\u0010Q\u001a\u00020\u001bH\u00c6\u0003J\t\u0010R\u001a\u00020\rH\u00c6\u0003J\u000b\u0010S\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\t\u0010T\u001a\u00020\u0007H\u00c6\u0003J\t\u0010U\u001a\u00020\u001bH\u00c6\u0003J\t\u0010V\u001a\u00020\u0007H\u00c6\u0003J\u0010\u0010W\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010-J\u000b\u0010X\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\u000b\u0010Y\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\u0010\u0010Z\u001a\u0004\u0018\u00010\rH\u00c6\u0003\u00a2\u0006\u0002\u0010>J\u000b\u0010[\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\u0010\u0010\\\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003\u00a2\u0006\u0002\u00104J\u00f8\u0001\u0010]\u001a\u00020\u00002\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00072\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\r2\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0011\u001a\u00020\u00072\n\b\u0002\u0010\u0012\u001a\u0004\u0018\u00010\u00072\b\b\u0002\u0010\u0013\u001a\u00020\u00072\b\b\u0002\u0010\u0014\u001a\u00020\u00072\u0010\b\u0002\u0010\u0015\u001a\n\u0012\u0004\u0012\u00020\u0017\u0018\u00010\u00162\u0010\b\u0002\u0010\u0018\u001a\n\u0012\u0004\u0012\u00020\u0019\u0018\u00010\u00162\b\b\u0002\u0010\u001a\u001a\u00020\u001b2\b\b\u0002\u0010\u001c\u001a\u00020\r2\n\b\u0002\u0010\u001d\u001a\u0004\u0018\u00010\u00072\b\b\u0002\u0010\u001e\u001a\u00020\u001bH\u00c6\u0001\u00a2\u0006\u0002\u0010^J\t\u0010_\u001a\u00020\u0003H\u00d6\u0001J\u0013\u0010`\u001a\u00020\u001b2\b\u0010a\u001a\u0004\u0018\u00010bH\u00d6\u0003J\t\u0010c\u001a\u00020\u0003H\u00d6\u0001J\t\u0010d\u001a\u00020\u0007H\u00d6\u0001J&\u0010e\u001a\u00020f2\u0006\u0010g\u001a\u00020\u00002\u0006\u0010h\u001a\u00020i2\u0006\u0010j\u001a\u00020kH\u00c1\u0001\u00a2\u0006\u0002\blJ\u0019\u0010m\u001a\u00020f2\u0006\u0010n\u001a\u00020o2\u0006\u0010p\u001a\u00020\u0003H\u00d6\u0001R\u001e\u0010\u000b\u001a\u0004\u0018\u00010\u00078\u0006X\u0087\u0004\u00a2\u0006\u000e\n\u0000\u0012\u0004\b#\u0010$\u001a\u0004\b%\u0010&R\u001c\u0010\u0013\u001a\u00020\u00078\u0006X\u0087\u0004\u00a2\u0006\u000e\n\u0000\u0012\u0004\b\'\u0010$\u001a\u0004\b(\u0010&R\u0013\u0010\n\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b)\u0010&R\u001c\u0010\u0011\u001a\u00020\u00078\u0006X\u0087\u0004\u00a2\u0006\u000e\n\u0000\u0012\u0004\b*\u0010$\u001a\u0004\b+\u0010&R\u0015\u0010\u0010\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\n\n\u0002\u0010.\u001a\u0004\b,\u0010-R$\u0010\u0015\u001a\n\u0012\u0004\u0012\u00020\u0017\u0018\u00010\u00168\u0006X\u0087\u0004\u00a2\u0006\u000e\n\u0000\u0012\u0004\b/\u0010$\u001a\u0004\b0\u00101R \u0010\u000f\u001a\u0004\u0018\u00010\u00058\u0006X\u0087\u0004\u00a2\u0006\u0010\n\u0002\u00105\u0012\u0004\b2\u0010$\u001a\u0004\b3\u00104R\u0016\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b6\u00107R\u0016\u0010\u001e\u001a\u00020\u001b8\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u00108R\u0016\u0010\u001a\u001a\u00020\u001b8\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u00108R\u0018\u0010\u001d\u001a\u0004\u0018\u00010\u00078\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b9\u0010&R\u001c\u0010\b\u001a\u00020\u00078\u0006X\u0087\u0004\u00a2\u0006\u000e\n\u0000\u0012\u0004\b:\u0010$\u001a\u0004\b;\u0010&R\u0013\u0010\u000e\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b<\u0010&R\u0015\u0010\f\u001a\u0004\u0018\u00010\r\u00a2\u0006\n\n\u0002\u0010?\u001a\u0004\b=\u0010>R\u001e\u0010\u0012\u001a\u0004\u0018\u00010\u00078\u0006X\u0087\u0004\u00a2\u0006\u000e\n\u0000\u0012\u0004\b@\u0010$\u001a\u0004\bA\u0010&R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\bB\u0010&R\u001c\u0010\u0014\u001a\u00020\u00078\u0006X\u0087\u0004\u00a2\u0006\u000e\n\u0000\u0012\u0004\bC\u0010$\u001a\u0004\bD\u0010&R\u0019\u0010\u0018\u001a\n\u0012\u0004\u0012\u00020\u0019\u0018\u00010\u0016\u00a2\u0006\b\n\u0000\u001a\u0004\bE\u00101R\u0016\u0010\u001c\u001a\u00020\r8\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\bF\u0010GR\u0015\u0010\t\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\n\n\u0002\u0010.\u001a\u0004\bH\u0010-\u00a8\u0006s"}, d2 = {"Lcom/catalogizer/android/data/models/MediaItem;", "Landroid/os/Parcelable;", "seen1", "", "id", "", "title", "", "mediaType", "year", "description", "coverImage", "rating", "", "quality", "fileSize", "duration", "directoryPath", "smbPath", "createdAt", "updatedAt", "externalMetadata", "", "Lcom/catalogizer/android/data/models/ExternalMetadata;", "versions", "Lcom/catalogizer/android/data/models/MediaVersion;", "isFavorite", "", "watchProgress", "lastWatched", "isDownloaded", "serializationConstructorMarker", "Lkotlinx/serialization/internal/SerializationConstructorMarker;", "(IJLjava/lang/String;Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Double;Ljava/lang/String;Ljava/lang/Long;Ljava/lang/Integer;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Ljava/util/List;ZDLjava/lang/String;ZLkotlinx/serialization/internal/SerializationConstructorMarker;)V", "(JLjava/lang/String;Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Double;Ljava/lang/String;Ljava/lang/Long;Ljava/lang/Integer;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Ljava/util/List;ZDLjava/lang/String;Z)V", "getCoverImage$annotations", "()V", "getCoverImage", "()Ljava/lang/String;", "getCreatedAt$annotations", "getCreatedAt", "getDescription", "getDirectoryPath$annotations", "getDirectoryPath", "getDuration", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getExternalMetadata$annotations", "getExternalMetadata", "()Ljava/util/List;", "getFileSize$annotations", "getFileSize", "()Ljava/lang/Long;", "Ljava/lang/Long;", "getId", "()J", "()Z", "getLastWatched", "getMediaType$annotations", "getMediaType", "getQuality", "getRating", "()Ljava/lang/Double;", "Ljava/lang/Double;", "getSmbPath$annotations", "getSmbPath", "getTitle", "getUpdatedAt$annotations", "getUpdatedAt", "getVersions", "getWatchProgress", "()D", "getYear", "component1", "component10", "component11", "component12", "component13", "component14", "component15", "component16", "component17", "component18", "component19", "component2", "component20", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "(JLjava/lang/String;Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Double;Ljava/lang/String;Ljava/lang/Long;Ljava/lang/Integer;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Ljava/util/List;ZDLjava/lang/String;Z)Lcom/catalogizer/android/data/models/MediaItem;", "describeContents", "equals", "other", "", "hashCode", "toString", "write$Self", "", "self", "output", "Lkotlinx/serialization/encoding/CompositeEncoder;", "serialDesc", "Lkotlinx/serialization/descriptors/SerialDescriptor;", "write$Self$app_release", "writeToParcel", "parcel", "Landroid/os/Parcel;", "flags", "$serializer", "Companion", "app_release"})
@kotlinx.parcelize.Parcelize()
@androidx.room.Entity(tableName = "media_items")
@androidx.room.TypeConverters(value = {com.catalogizer.android.data.local.Converters.class})
public final class MediaItem implements android.os.Parcelable {
    @androidx.room.PrimaryKey()
    private final long id = 0L;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String title = null;
    @androidx.room.ColumnInfo(name = "media_type")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String mediaType = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer year = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String description = null;
    @androidx.room.ColumnInfo(name = "cover_image")
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String coverImage = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Double rating = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String quality = null;
    @androidx.room.ColumnInfo(name = "file_size")
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Long fileSize = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer duration = null;
    @androidx.room.ColumnInfo(name = "directory_path")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String directoryPath = null;
    @androidx.room.ColumnInfo(name = "smb_path")
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String smbPath = null;
    @androidx.room.ColumnInfo(name = "created_at")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String createdAt = null;
    @androidx.room.ColumnInfo(name = "updated_at")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String updatedAt = null;
    @androidx.room.ColumnInfo(name = "external_metadata")
    @org.jetbrains.annotations.Nullable()
    private final java.util.List<com.catalogizer.android.data.models.ExternalMetadata> externalMetadata = null;
    @org.jetbrains.annotations.Nullable()
    private final java.util.List<com.catalogizer.android.data.models.MediaVersion> versions = null;
    @androidx.room.ColumnInfo(name = "is_favorite")
    private final boolean isFavorite = false;
    @androidx.room.ColumnInfo(name = "watch_progress")
    private final double watchProgress = 0.0;
    @androidx.room.ColumnInfo(name = "last_watched")
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String lastWatched = null;
    @androidx.room.ColumnInfo(name = "is_downloaded")
    private final boolean isDownloaded = false;
    @org.jetbrains.annotations.NotNull()
    public static final com.catalogizer.android.data.models.MediaItem.Companion Companion = null;
    
    public MediaItem(long id, @org.jetbrains.annotations.NotNull()
    java.lang.String title, @org.jetbrains.annotations.NotNull()
    java.lang.String mediaType, @org.jetbrains.annotations.Nullable()
    java.lang.Integer year, @org.jetbrains.annotations.Nullable()
    java.lang.String description, @org.jetbrains.annotations.Nullable()
    java.lang.String coverImage, @org.jetbrains.annotations.Nullable()
    java.lang.Double rating, @org.jetbrains.annotations.Nullable()
    java.lang.String quality, @org.jetbrains.annotations.Nullable()
    java.lang.Long fileSize, @org.jetbrains.annotations.Nullable()
    java.lang.Integer duration, @org.jetbrains.annotations.NotNull()
    java.lang.String directoryPath, @org.jetbrains.annotations.Nullable()
    java.lang.String smbPath, @org.jetbrains.annotations.NotNull()
    java.lang.String createdAt, @org.jetbrains.annotations.NotNull()
    java.lang.String updatedAt, @org.jetbrains.annotations.Nullable()
    java.util.List<com.catalogizer.android.data.models.ExternalMetadata> externalMetadata, @org.jetbrains.annotations.Nullable()
    java.util.List<com.catalogizer.android.data.models.MediaVersion> versions, boolean isFavorite, double watchProgress, @org.jetbrains.annotations.Nullable()
    java.lang.String lastWatched, boolean isDownloaded) {
        super();
    }
    
    public final long getId() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getTitle() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getMediaType() {
        return null;
    }
    
    @kotlinx.serialization.SerialName(value = "media_type")
    @java.lang.Deprecated()
    public static void getMediaType$annotations() {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getYear() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getDescription() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getCoverImage() {
        return null;
    }
    
    @kotlinx.serialization.SerialName(value = "cover_image")
    @java.lang.Deprecated()
    public static void getCoverImage$annotations() {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double getRating() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getQuality() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long getFileSize() {
        return null;
    }
    
    @kotlinx.serialization.SerialName(value = "file_size")
    @java.lang.Deprecated()
    public static void getFileSize$annotations() {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getDuration() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getDirectoryPath() {
        return null;
    }
    
    @kotlinx.serialization.SerialName(value = "directory_path")
    @java.lang.Deprecated()
    public static void getDirectoryPath$annotations() {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getSmbPath() {
        return null;
    }
    
    @kotlinx.serialization.SerialName(value = "smb_path")
    @java.lang.Deprecated()
    public static void getSmbPath$annotations() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getCreatedAt() {
        return null;
    }
    
    @kotlinx.serialization.SerialName(value = "created_at")
    @java.lang.Deprecated()
    public static void getCreatedAt$annotations() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getUpdatedAt() {
        return null;
    }
    
    @kotlinx.serialization.SerialName(value = "updated_at")
    @java.lang.Deprecated()
    public static void getUpdatedAt$annotations() {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.List<com.catalogizer.android.data.models.ExternalMetadata> getExternalMetadata() {
        return null;
    }
    
    @kotlinx.serialization.SerialName(value = "external_metadata")
    @java.lang.Deprecated()
    public static void getExternalMetadata$annotations() {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.List<com.catalogizer.android.data.models.MediaVersion> getVersions() {
        return null;
    }
    
    public final boolean isFavorite() {
        return false;
    }
    
    public final double getWatchProgress() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getLastWatched() {
        return null;
    }
    
    public final boolean isDownloaded() {
        return false;
    }
    
    public final long component1() {
        return 0L;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component10() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component11() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component12() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component13() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component14() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.List<com.catalogizer.android.data.models.ExternalMetadata> component15() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.List<com.catalogizer.android.data.models.MediaVersion> component16() {
        return null;
    }
    
    public final boolean component17() {
        return false;
    }
    
    public final double component18() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component19() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    public final boolean component20() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component4() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component5() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component6() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double component7() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component8() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.catalogizer.android.data.models.MediaItem copy(long id, @org.jetbrains.annotations.NotNull()
    java.lang.String title, @org.jetbrains.annotations.NotNull()
    java.lang.String mediaType, @org.jetbrains.annotations.Nullable()
    java.lang.Integer year, @org.jetbrains.annotations.Nullable()
    java.lang.String description, @org.jetbrains.annotations.Nullable()
    java.lang.String coverImage, @org.jetbrains.annotations.Nullable()
    java.lang.Double rating, @org.jetbrains.annotations.Nullable()
    java.lang.String quality, @org.jetbrains.annotations.Nullable()
    java.lang.Long fileSize, @org.jetbrains.annotations.Nullable()
    java.lang.Integer duration, @org.jetbrains.annotations.NotNull()
    java.lang.String directoryPath, @org.jetbrains.annotations.Nullable()
    java.lang.String smbPath, @org.jetbrains.annotations.NotNull()
    java.lang.String createdAt, @org.jetbrains.annotations.NotNull()
    java.lang.String updatedAt, @org.jetbrains.annotations.Nullable()
    java.util.List<com.catalogizer.android.data.models.ExternalMetadata> externalMetadata, @org.jetbrains.annotations.Nullable()
    java.util.List<com.catalogizer.android.data.models.MediaVersion> versions, boolean isFavorite, double watchProgress, @org.jetbrains.annotations.Nullable()
    java.lang.String lastWatched, boolean isDownloaded) {
        return null;
    }
    
    @java.lang.Override()
    public int describeContents() {
        return 0;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
    
    @kotlin.jvm.JvmStatic()
    public static final void write$Self$app_release(@org.jetbrains.annotations.NotNull()
    com.catalogizer.android.data.models.MediaItem self, @org.jetbrains.annotations.NotNull()
    kotlinx.serialization.encoding.CompositeEncoder output, @org.jetbrains.annotations.NotNull()
    kotlinx.serialization.descriptors.SerialDescriptor serialDesc) {
    }
    
    @java.lang.Override()
    public void writeToParcel(@org.jetbrains.annotations.NotNull()
    android.os.Parcel parcel, int flags) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c7\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0003J\u0018\u0010\b\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\n0\tH\u00d6\u0001\u00a2\u0006\u0002\u0010\u000bJ\u0011\u0010\f\u001a\u00020\u00022\u0006\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\u0019\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0002H\u00d6\u0001R\u0014\u0010\u0004\u001a\u00020\u00058VX\u00d6\u0005\u00a2\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\u0014"}, d2 = {"com/catalogizer/android/data/models/MediaItem.$serializer", "Lkotlinx/serialization/internal/GeneratedSerializer;", "Lcom/catalogizer/android/data/models/MediaItem;", "()V", "descriptor", "Lkotlinx/serialization/descriptors/SerialDescriptor;", "getDescriptor", "()Lkotlinx/serialization/descriptors/SerialDescriptor;", "childSerializers", "", "Lkotlinx/serialization/KSerializer;", "()[Lkotlinx/serialization/KSerializer;", "deserialize", "decoder", "Lkotlinx/serialization/encoding/Decoder;", "serialize", "", "encoder", "Lkotlinx/serialization/encoding/Encoder;", "value", "app_release"})
    @java.lang.Deprecated()
    public static final class $serializer implements kotlinx.serialization.internal.GeneratedSerializer<com.catalogizer.android.data.models.MediaItem> {
        @org.jetbrains.annotations.NotNull()
        public static final com.catalogizer.android.data.models.MediaItem.$serializer INSTANCE = null;
        
        private $serializer() {
            super();
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public kotlinx.serialization.KSerializer<?>[] childSerializers() {
            return null;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public com.catalogizer.android.data.models.MediaItem deserialize(@org.jetbrains.annotations.NotNull()
        kotlinx.serialization.encoding.Decoder decoder) {
            return null;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public kotlinx.serialization.descriptors.SerialDescriptor getDescriptor() {
            return null;
        }
        
        @java.lang.Override()
        public void serialize(@org.jetbrains.annotations.NotNull()
        kotlinx.serialization.encoding.Encoder encoder, @org.jetbrains.annotations.NotNull()
        com.catalogizer.android.data.models.MediaItem value) {
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public kotlinx.serialization.KSerializer<?>[] typeParametersSerializers() {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004H\u00c6\u0001\u00a8\u0006\u0006"}, d2 = {"Lcom/catalogizer/android/data/models/MediaItem$Companion;", "", "()V", "serializer", "Lkotlinx/serialization/KSerializer;", "Lcom/catalogizer/android/data/models/MediaItem;", "app_release"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final kotlinx.serialization.KSerializer<com.catalogizer.android.data.models.MediaItem> serializer() {
            return null;
        }
    }
}