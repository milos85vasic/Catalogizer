package com.catalogizer.android;

import dagger.hilt.InstallIn;
import dagger.hilt.codegen.OriginatingElement;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.internal.GeneratedEntryPoint;

@OriginatingElement(
    topLevelClass = CatalogizerApplication.class
)
@GeneratedEntryPoint
@InstallIn(SingletonComponent.class)
public interface CatalogizerApplication_GeneratedInjector {
  void injectCatalogizerApplication(CatalogizerApplication catalogizerApplication);
}
