package com.catalogizer.android;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\u0018\u00002\u00020\u00012\u00020\u0002B\u0005\u00a2\u0006\u0002\u0010\u0003J\b\u0010\u000e\u001a\u00020\u000fH\u0016R\u001b\u0010\u0004\u001a\u00020\u00058FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\b\u0010\t\u001a\u0004\b\u0006\u0010\u0007R\u0014\u0010\n\u001a\u00020\u000b8VX\u0096\u0004\u00a2\u0006\u0006\u001a\u0004\b\f\u0010\r\u00a8\u0006\u0010"}, d2 = {"Lcom/catalogizer/android/CatalogizerApplication;", "Landroid/app/Application;", "Landroidx/work/Configuration$Provider;", "()V", "dependencyContainer", "Lcom/catalogizer/android/DependencyContainer;", "getDependencyContainer", "()Lcom/catalogizer/android/DependencyContainer;", "dependencyContainer$delegate", "Lkotlin/Lazy;", "workManagerConfiguration", "Landroidx/work/Configuration;", "getWorkManagerConfiguration", "()Landroidx/work/Configuration;", "onCreate", "", "app_debug"})
public final class CatalogizerApplication extends android.app.Application implements androidx.work.Configuration.Provider {
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy dependencyContainer$delegate = null;
    
    public CatalogizerApplication() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.catalogizer.android.DependencyContainer getDependencyContainer() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public androidx.work.Configuration getWorkManagerConfiguration() {
        return null;
    }
    
    @java.lang.Override()
    public void onCreate() {
    }
}