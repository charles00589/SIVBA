package com.facebook.imagepipeline.cache;

import android.net.Uri;
import com.facebook.cache.common.CacheKey;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.Postprocessor;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public class DefaultCacheKeyFactory implements CacheKeyFactory {

    @Nullable
    private static DefaultCacheKeyFactory sInstance = null;
    private static boolean sShouldRemoveCallerContextFromCacheKey = false;

    protected Uri getCacheKeySourceUri(Uri uri) {
        return uri;
    }

    protected DefaultCacheKeyFactory() {
    }

    public static synchronized DefaultCacheKeyFactory getInstance() {
        DefaultCacheKeyFactory defaultCacheKeyFactory;
        synchronized (DefaultCacheKeyFactory.class) {
            if (sInstance == null) {
                sInstance = new DefaultCacheKeyFactory();
            }
            defaultCacheKeyFactory = sInstance;
        }
        return defaultCacheKeyFactory;
    }

    public static void setShouldRemoveCallerContextFromCacheKey(boolean z) {
        sShouldRemoveCallerContextFromCacheKey = z;
    }

    @Override // com.facebook.imagepipeline.cache.CacheKeyFactory
    public CacheKey getBitmapCacheKey(ImageRequest imageRequest, @Nullable Object obj) {
        BitmapMemoryCacheKey bitmapMemoryCacheKey = new BitmapMemoryCacheKey(getCacheKeySourceUri(imageRequest.getSourceUri()).toString(), imageRequest.getResizeOptions(), imageRequest.getRotationOptions(), imageRequest.getImageDecodeOptions(), null, null);
        if (sShouldRemoveCallerContextFromCacheKey) {
            bitmapMemoryCacheKey.setCallerContext(null);
        } else {
            bitmapMemoryCacheKey.setCallerContext(obj);
        }
        return bitmapMemoryCacheKey;
    }

    @Override // com.facebook.imagepipeline.cache.CacheKeyFactory
    public CacheKey getPostprocessedBitmapCacheKey(ImageRequest imageRequest, @Nullable Object obj) {
        CacheKey cacheKey;
        String str;
        Postprocessor postprocessor = imageRequest.getPostprocessor();
        if (postprocessor != null) {
            CacheKey cacheKey2 = postprocessor.getCacheKey();
            str = postprocessor.getClass().getName();
            cacheKey = cacheKey2;
        } else {
            cacheKey = null;
            str = null;
        }
        BitmapMemoryCacheKey bitmapMemoryCacheKey = new BitmapMemoryCacheKey(getCacheKeySourceUri(imageRequest.getSourceUri()).toString(), imageRequest.getResizeOptions(), imageRequest.getRotationOptions(), imageRequest.getImageDecodeOptions(), cacheKey, str);
        if (sShouldRemoveCallerContextFromCacheKey) {
            bitmapMemoryCacheKey.setCallerContext(null);
        } else {
            bitmapMemoryCacheKey.setCallerContext(obj);
        }
        return bitmapMemoryCacheKey;
    }

    @Override // com.facebook.imagepipeline.cache.CacheKeyFactory
    public CacheKey getEncodedCacheKey(ImageRequest imageRequest, @Nullable Object obj) {
        return getEncodedCacheKey(imageRequest, imageRequest.getSourceUri(), obj);
    }

    @Override // com.facebook.imagepipeline.cache.CacheKeyFactory
    public CacheKey getEncodedCacheKey(ImageRequest imageRequest, Uri uri, @Nullable Object obj) {
        return new SimpleCacheKey(getCacheKeySourceUri(uri).toString());
    }
}
