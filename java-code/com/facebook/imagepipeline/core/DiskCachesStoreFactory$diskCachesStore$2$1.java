package com.facebook.imagepipeline.core;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.cache.disk.FileCache;
import com.facebook.common.internal.ImmutableMap;
import com.facebook.common.memory.PooledByteBufferFactory;
import com.facebook.common.memory.PooledByteStreams;
import com.facebook.imagepipeline.cache.BufferedDiskCache;
import com.facebook.imagepipeline.cache.ImageCacheStatsTracker;
import com.facebook.imagepipeline.memory.PoolFactory;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.LazyThreadSafetyMode;
import kotlin.Metadata;
import kotlin.collections.MapsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: DiskCachesStoreFactory.kt */
@Metadata(d1 = {"\u0000/\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004*\u0001\u0000\b\n\u0018\u00002\u00020\u0001R\u001b\u0010\u0002\u001a\u00020\u00038VX\u0096\u0084\u0002¢\u0006\f\n\u0004\b\u0006\u0010\u0007\u001a\u0004\b\u0004\u0010\u0005R\u001b\u0010\b\u001a\u00020\t8VX\u0096\u0084\u0002¢\u0006\f\n\u0004\b\f\u0010\u0007\u001a\u0004\b\n\u0010\u000bR\u001b\u0010\r\u001a\u00020\u00038VX\u0096\u0084\u0002¢\u0006\f\n\u0004\b\u000f\u0010\u0007\u001a\u0004\b\u000e\u0010\u0005R\u001b\u0010\u0010\u001a\u00020\t8VX\u0096\u0084\u0002¢\u0006\f\n\u0004\b\u0012\u0010\u0007\u001a\u0004\b\u0011\u0010\u000bR'\u0010\u0013\u001a\u000e\u0012\u0004\u0012\u00020\u0015\u0012\u0004\u0012\u00020\u00030\u00148VX\u0096\u0084\u0002¢\u0006\f\n\u0004\b\u0018\u0010\u0007\u001a\u0004\b\u0016\u0010\u0017R'\u0010\u0019\u001a\u000e\u0012\u0004\u0012\u00020\u0015\u0012\u0004\u0012\u00020\t0\u001a8VX\u0096\u0084\u0002¢\u0006\f\n\u0004\b\u001d\u0010\u0007\u001a\u0004\b\u001b\u0010\u001c¨\u0006\u001e"}, d2 = {"com/facebook/imagepipeline/core/DiskCachesStoreFactory$diskCachesStore$2$1", "Lcom/facebook/imagepipeline/core/DiskCachesStore;", "mainFileCache", "Lcom/facebook/cache/disk/FileCache;", "getMainFileCache", "()Lcom/facebook/cache/disk/FileCache;", "mainFileCache$delegate", "Lkotlin/Lazy;", "mainBufferedDiskCache", "Lcom/facebook/imagepipeline/cache/BufferedDiskCache;", "getMainBufferedDiskCache", "()Lcom/facebook/imagepipeline/cache/BufferedDiskCache;", "mainBufferedDiskCache$delegate", "smallImageFileCache", "getSmallImageFileCache", "smallImageFileCache$delegate", "smallImageBufferedDiskCache", "getSmallImageBufferedDiskCache", "smallImageBufferedDiskCache$delegate", "dynamicFileCaches", "", "", "getDynamicFileCaches", "()Ljava/util/Map;", "dynamicFileCaches$delegate", "dynamicBufferedDiskCaches", "Lcom/facebook/common/internal/ImmutableMap;", "getDynamicBufferedDiskCaches", "()Lcom/facebook/common/internal/ImmutableMap;", "dynamicBufferedDiskCaches$delegate", "imagepipeline_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes.dex */
public final class DiskCachesStoreFactory$diskCachesStore$2$1 implements DiskCachesStore {

    /* renamed from: dynamicBufferedDiskCaches$delegate, reason: from kotlin metadata */
    private final Lazy dynamicBufferedDiskCaches;

    /* renamed from: dynamicFileCaches$delegate, reason: from kotlin metadata */
    private final Lazy dynamicFileCaches;

    /* renamed from: mainBufferedDiskCache$delegate, reason: from kotlin metadata */
    private final Lazy mainBufferedDiskCache;

    /* renamed from: mainFileCache$delegate, reason: from kotlin metadata */
    private final Lazy mainFileCache;

    /* renamed from: smallImageBufferedDiskCache$delegate, reason: from kotlin metadata */
    private final Lazy smallImageBufferedDiskCache;

    /* renamed from: smallImageFileCache$delegate, reason: from kotlin metadata */
    private final Lazy smallImageFileCache;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DiskCachesStoreFactory$diskCachesStore$2$1(final DiskCachesStoreFactory diskCachesStoreFactory) {
        this.mainFileCache = LazyKt.lazy(LazyThreadSafetyMode.SYNCHRONIZED, new Function0() { // from class: com.facebook.imagepipeline.core.DiskCachesStoreFactory$diskCachesStore$2$1$$ExternalSyntheticLambda0
            @Override // kotlin.jvm.functions.Function0
            public final Object invoke() {
                FileCache mainFileCache_delegate$lambda$0;
                mainFileCache_delegate$lambda$0 = DiskCachesStoreFactory$diskCachesStore$2$1.mainFileCache_delegate$lambda$0(DiskCachesStoreFactory.this);
                return mainFileCache_delegate$lambda$0;
            }
        });
        this.mainBufferedDiskCache = LazyKt.lazy(LazyThreadSafetyMode.SYNCHRONIZED, new Function0() { // from class: com.facebook.imagepipeline.core.DiskCachesStoreFactory$diskCachesStore$2$1$$ExternalSyntheticLambda1
            @Override // kotlin.jvm.functions.Function0
            public final Object invoke() {
                BufferedDiskCache mainBufferedDiskCache_delegate$lambda$1;
                mainBufferedDiskCache_delegate$lambda$1 = DiskCachesStoreFactory$diskCachesStore$2$1.mainBufferedDiskCache_delegate$lambda$1(DiskCachesStoreFactory$diskCachesStore$2$1.this, diskCachesStoreFactory);
                return mainBufferedDiskCache_delegate$lambda$1;
            }
        });
        this.smallImageFileCache = LazyKt.lazy(LazyThreadSafetyMode.SYNCHRONIZED, new Function0() { // from class: com.facebook.imagepipeline.core.DiskCachesStoreFactory$diskCachesStore$2$1$$ExternalSyntheticLambda2
            @Override // kotlin.jvm.functions.Function0
            public final Object invoke() {
                FileCache smallImageFileCache_delegate$lambda$2;
                smallImageFileCache_delegate$lambda$2 = DiskCachesStoreFactory$diskCachesStore$2$1.smallImageFileCache_delegate$lambda$2(DiskCachesStoreFactory.this);
                return smallImageFileCache_delegate$lambda$2;
            }
        });
        this.smallImageBufferedDiskCache = LazyKt.lazy(LazyThreadSafetyMode.SYNCHRONIZED, new Function0() { // from class: com.facebook.imagepipeline.core.DiskCachesStoreFactory$diskCachesStore$2$1$$ExternalSyntheticLambda3
            @Override // kotlin.jvm.functions.Function0
            public final Object invoke() {
                BufferedDiskCache smallImageBufferedDiskCache_delegate$lambda$3;
                smallImageBufferedDiskCache_delegate$lambda$3 = DiskCachesStoreFactory$diskCachesStore$2$1.smallImageBufferedDiskCache_delegate$lambda$3(DiskCachesStoreFactory$diskCachesStore$2$1.this, diskCachesStoreFactory);
                return smallImageBufferedDiskCache_delegate$lambda$3;
            }
        });
        this.dynamicFileCaches = LazyKt.lazy(LazyThreadSafetyMode.SYNCHRONIZED, new Function0() { // from class: com.facebook.imagepipeline.core.DiskCachesStoreFactory$diskCachesStore$2$1$$ExternalSyntheticLambda4
            @Override // kotlin.jvm.functions.Function0
            public final Object invoke() {
                Map dynamicFileCaches_delegate$lambda$7;
                dynamicFileCaches_delegate$lambda$7 = DiskCachesStoreFactory$diskCachesStore$2$1.dynamicFileCaches_delegate$lambda$7(DiskCachesStoreFactory.this, this);
                return dynamicFileCaches_delegate$lambda$7;
            }
        });
        this.dynamicBufferedDiskCaches = LazyKt.lazy(LazyThreadSafetyMode.SYNCHRONIZED, new Function0() { // from class: com.facebook.imagepipeline.core.DiskCachesStoreFactory$diskCachesStore$2$1$$ExternalSyntheticLambda5
            @Override // kotlin.jvm.functions.Function0
            public final Object invoke() {
                ImmutableMap dynamicBufferedDiskCaches_delegate$lambda$9;
                dynamicBufferedDiskCaches_delegate$lambda$9 = DiskCachesStoreFactory$diskCachesStore$2$1.dynamicBufferedDiskCaches_delegate$lambda$9(DiskCachesStoreFactory$diskCachesStore$2$1.this, diskCachesStoreFactory);
                return dynamicBufferedDiskCaches_delegate$lambda$9;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final FileCache mainFileCache_delegate$lambda$0(DiskCachesStoreFactory this$0) {
        FileCacheFactory fileCacheFactory;
        DiskCacheConfig diskCacheConfig;
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        fileCacheFactory = this$0.fileCacheFactory;
        diskCacheConfig = this$0.mainDiskCacheConfig;
        return fileCacheFactory.get(diskCacheConfig);
    }

    @Override // com.facebook.imagepipeline.core.DiskCachesStore
    public FileCache getMainFileCache() {
        return (FileCache) this.mainFileCache.getValue();
    }

    @Override // com.facebook.imagepipeline.core.DiskCachesStore
    public BufferedDiskCache getMainBufferedDiskCache() {
        return (BufferedDiskCache) this.mainBufferedDiskCache.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final BufferedDiskCache mainBufferedDiskCache_delegate$lambda$1(DiskCachesStoreFactory$diskCachesStore$2$1 this$0, DiskCachesStoreFactory this$1) {
        PoolFactory poolFactory;
        int i;
        PoolFactory poolFactory2;
        ExecutorSupplier executorSupplier;
        ExecutorSupplier executorSupplier2;
        ImageCacheStatsTracker imageCacheStatsTracker;
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(this$1, "this$1");
        FileCache mainFileCache = this$0.getMainFileCache();
        poolFactory = this$1.poolFactory;
        i = this$1.memoryChunkType;
        PooledByteBufferFactory pooledByteBufferFactory = poolFactory.getPooledByteBufferFactory(i);
        Intrinsics.checkNotNullExpressionValue(pooledByteBufferFactory, "getPooledByteBufferFactory(...)");
        poolFactory2 = this$1.poolFactory;
        PooledByteStreams pooledByteStreams = poolFactory2.getPooledByteStreams();
        Intrinsics.checkNotNullExpressionValue(pooledByteStreams, "getPooledByteStreams(...)");
        executorSupplier = this$1.executorSupplier;
        Executor ioBoundExecutor = executorSupplier.getIoBoundExecutor();
        Intrinsics.checkNotNullExpressionValue(ioBoundExecutor, "forLocalStorageRead(...)");
        executorSupplier2 = this$1.executorSupplier;
        Executor forLocalStorageWrite = executorSupplier2.forLocalStorageWrite();
        Intrinsics.checkNotNullExpressionValue(forLocalStorageWrite, "forLocalStorageWrite(...)");
        imageCacheStatsTracker = this$1.imageCacheStatsTracker;
        return new BufferedDiskCache(mainFileCache, pooledByteBufferFactory, pooledByteStreams, ioBoundExecutor, forLocalStorageWrite, imageCacheStatsTracker);
    }

    @Override // com.facebook.imagepipeline.core.DiskCachesStore
    public FileCache getSmallImageFileCache() {
        return (FileCache) this.smallImageFileCache.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final FileCache smallImageFileCache_delegate$lambda$2(DiskCachesStoreFactory this$0) {
        FileCacheFactory fileCacheFactory;
        DiskCacheConfig diskCacheConfig;
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        fileCacheFactory = this$0.fileCacheFactory;
        diskCacheConfig = this$0.smallImageDiskCacheConfig;
        return fileCacheFactory.get(diskCacheConfig);
    }

    @Override // com.facebook.imagepipeline.core.DiskCachesStore
    public BufferedDiskCache getSmallImageBufferedDiskCache() {
        return (BufferedDiskCache) this.smallImageBufferedDiskCache.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final BufferedDiskCache smallImageBufferedDiskCache_delegate$lambda$3(DiskCachesStoreFactory$diskCachesStore$2$1 this$0, DiskCachesStoreFactory this$1) {
        PoolFactory poolFactory;
        int i;
        PoolFactory poolFactory2;
        ExecutorSupplier executorSupplier;
        ExecutorSupplier executorSupplier2;
        ImageCacheStatsTracker imageCacheStatsTracker;
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(this$1, "this$1");
        FileCache smallImageFileCache = this$0.getSmallImageFileCache();
        poolFactory = this$1.poolFactory;
        i = this$1.memoryChunkType;
        PooledByteBufferFactory pooledByteBufferFactory = poolFactory.getPooledByteBufferFactory(i);
        Intrinsics.checkNotNullExpressionValue(pooledByteBufferFactory, "getPooledByteBufferFactory(...)");
        poolFactory2 = this$1.poolFactory;
        PooledByteStreams pooledByteStreams = poolFactory2.getPooledByteStreams();
        Intrinsics.checkNotNullExpressionValue(pooledByteStreams, "getPooledByteStreams(...)");
        executorSupplier = this$1.executorSupplier;
        Executor ioBoundExecutor = executorSupplier.getIoBoundExecutor();
        Intrinsics.checkNotNullExpressionValue(ioBoundExecutor, "forLocalStorageRead(...)");
        executorSupplier2 = this$1.executorSupplier;
        Executor forLocalStorageWrite = executorSupplier2.forLocalStorageWrite();
        Intrinsics.checkNotNullExpressionValue(forLocalStorageWrite, "forLocalStorageWrite(...)");
        imageCacheStatsTracker = this$1.imageCacheStatsTracker;
        return new BufferedDiskCache(smallImageFileCache, pooledByteBufferFactory, pooledByteStreams, ioBoundExecutor, forLocalStorageWrite, imageCacheStatsTracker);
    }

    @Override // com.facebook.imagepipeline.core.DiskCachesStore
    public Map<String, FileCache> getDynamicFileCaches() {
        return (Map) this.dynamicFileCaches.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Map dynamicFileCaches_delegate$lambda$7(DiskCachesStoreFactory this$0, DiskCachesStoreFactory$diskCachesStore$2$1 this$1) {
        Map map;
        FileCacheFactory fileCacheFactory;
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(this$1, "this$1");
        map = this$0.dynamicDiskCacheConfigMap;
        if (map == null) {
            return MapsKt.emptyMap();
        }
        LinkedHashMap linkedHashMap = new LinkedHashMap(MapsKt.mapCapacity(map.size()));
        for (Map.Entry entry : map.entrySet()) {
            Object key = entry.getKey();
            DiskCacheConfig diskCacheConfig = (DiskCacheConfig) entry.getValue();
            fileCacheFactory = this$0.fileCacheFactory;
            linkedHashMap.put(key, fileCacheFactory.get(diskCacheConfig));
        }
        return linkedHashMap;
    }

    @Override // com.facebook.imagepipeline.core.DiskCachesStore
    public ImmutableMap<String, BufferedDiskCache> getDynamicBufferedDiskCaches() {
        Object value = this.dynamicBufferedDiskCaches.getValue();
        Intrinsics.checkNotNullExpressionValue(value, "getValue(...)");
        return (ImmutableMap) value;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final ImmutableMap dynamicBufferedDiskCaches_delegate$lambda$9(DiskCachesStoreFactory$diskCachesStore$2$1 this$0, DiskCachesStoreFactory this$1) {
        PoolFactory poolFactory;
        int i;
        PoolFactory poolFactory2;
        ExecutorSupplier executorSupplier;
        ExecutorSupplier executorSupplier2;
        ImageCacheStatsTracker imageCacheStatsTracker;
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(this$1, "this$1");
        Map<String, FileCache> dynamicFileCaches = this$0.getDynamicFileCaches();
        LinkedHashMap linkedHashMap = new LinkedHashMap(MapsKt.mapCapacity(dynamicFileCaches.size()));
        Iterator<T> it = dynamicFileCaches.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            Object key = entry.getKey();
            FileCache fileCache = (FileCache) entry.getValue();
            poolFactory = this$1.poolFactory;
            i = this$1.memoryChunkType;
            PooledByteBufferFactory pooledByteBufferFactory = poolFactory.getPooledByteBufferFactory(i);
            Intrinsics.checkNotNullExpressionValue(pooledByteBufferFactory, "getPooledByteBufferFactory(...)");
            poolFactory2 = this$1.poolFactory;
            PooledByteStreams pooledByteStreams = poolFactory2.getPooledByteStreams();
            Intrinsics.checkNotNullExpressionValue(pooledByteStreams, "getPooledByteStreams(...)");
            executorSupplier = this$1.executorSupplier;
            Executor ioBoundExecutor = executorSupplier.getIoBoundExecutor();
            Intrinsics.checkNotNullExpressionValue(ioBoundExecutor, "forLocalStorageRead(...)");
            executorSupplier2 = this$1.executorSupplier;
            Executor forLocalStorageWrite = executorSupplier2.forLocalStorageWrite();
            Intrinsics.checkNotNullExpressionValue(forLocalStorageWrite, "forLocalStorageWrite(...)");
            imageCacheStatsTracker = this$1.imageCacheStatsTracker;
            linkedHashMap.put(key, new BufferedDiskCache(fileCache, pooledByteBufferFactory, pooledByteStreams, ioBoundExecutor, forLocalStorageWrite, imageCacheStatsTracker));
        }
        return ImmutableMap.copyOf((Map) linkedHashMap);
    }
}
