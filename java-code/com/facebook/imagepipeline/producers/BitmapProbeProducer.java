package com.facebook.imagepipeline.producers;

import com.facebook.cache.common.CacheKey;
import com.facebook.common.internal.Supplier;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.cache.BoundedLinkedHashSet;
import com.facebook.imagepipeline.cache.BufferedDiskCache;
import com.facebook.imagepipeline.cache.CacheKeyFactory;
import com.facebook.imagepipeline.cache.MemoryCache;
import com.facebook.imagepipeline.core.DiskCachesStore;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.systrace.FrescoSystrace;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public class BitmapProbeProducer implements Producer<CloseableReference<CloseableImage>> {
    public static final String PRODUCER_NAME = "BitmapProbeProducer";
    private final CacheKeyFactory mCacheKeyFactory;
    private final BoundedLinkedHashSet<CacheKey> mDiskCacheHistory;
    private final Supplier<DiskCachesStore> mDiskCachesStoreSupplier;
    private final MemoryCache<CacheKey, PooledByteBuffer> mEncodedMemoryCache;
    private final BoundedLinkedHashSet<CacheKey> mEncodedMemoryCacheHistory;
    private final Producer<CloseableReference<CloseableImage>> mInputProducer;

    public BitmapProbeProducer(MemoryCache<CacheKey, PooledByteBuffer> memoryCache, Supplier<DiskCachesStore> supplier, CacheKeyFactory cacheKeyFactory, BoundedLinkedHashSet<CacheKey> boundedLinkedHashSet, BoundedLinkedHashSet<CacheKey> boundedLinkedHashSet2, Producer<CloseableReference<CloseableImage>> producer) {
        this.mEncodedMemoryCache = memoryCache;
        this.mDiskCachesStoreSupplier = supplier;
        this.mCacheKeyFactory = cacheKeyFactory;
        this.mEncodedMemoryCacheHistory = boundedLinkedHashSet;
        this.mDiskCacheHistory = boundedLinkedHashSet2;
        this.mInputProducer = producer;
    }

    @Override // com.facebook.imagepipeline.producers.Producer
    public void produceResults(Consumer<CloseableReference<CloseableImage>> consumer, ProducerContext producerContext) {
        try {
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.beginSection("BitmapProbeProducer#produceResults");
            }
            ProducerListener2 producerListener = producerContext.getProducerListener();
            producerListener.onProducerStart(producerContext, getProducerName());
            ProbeConsumer probeConsumer = new ProbeConsumer(consumer, producerContext, this.mEncodedMemoryCache, this.mDiskCachesStoreSupplier, this.mCacheKeyFactory, this.mEncodedMemoryCacheHistory, this.mDiskCacheHistory);
            producerListener.onProducerFinishWithSuccess(producerContext, PRODUCER_NAME, null);
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.beginSection("mInputProducer.produceResult");
            }
            this.mInputProducer.produceResults(probeConsumer, producerContext);
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.endSection();
            }
        } finally {
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.endSection();
            }
        }
    }

    /* loaded from: classes.dex */
    private static class ProbeConsumer extends DelegatingConsumer<CloseableReference<CloseableImage>, CloseableReference<CloseableImage>> {
        private final CacheKeyFactory mCacheKeyFactory;
        private final BoundedLinkedHashSet<CacheKey> mDiskCacheHistory;
        private final Supplier<DiskCachesStore> mDiskCachesStoreSupplier;
        private final MemoryCache<CacheKey, PooledByteBuffer> mEncodedMemoryCache;
        private final BoundedLinkedHashSet<CacheKey> mEncodedMemoryCacheHistory;
        private final ProducerContext mProducerContext;

        public ProbeConsumer(Consumer<CloseableReference<CloseableImage>> consumer, ProducerContext producerContext, MemoryCache<CacheKey, PooledByteBuffer> memoryCache, Supplier<DiskCachesStore> supplier, CacheKeyFactory cacheKeyFactory, BoundedLinkedHashSet<CacheKey> boundedLinkedHashSet, BoundedLinkedHashSet<CacheKey> boundedLinkedHashSet2) {
            super(consumer);
            this.mProducerContext = producerContext;
            this.mEncodedMemoryCache = memoryCache;
            this.mDiskCachesStoreSupplier = supplier;
            this.mCacheKeyFactory = cacheKeyFactory;
            this.mEncodedMemoryCacheHistory = boundedLinkedHashSet;
            this.mDiskCacheHistory = boundedLinkedHashSet2;
        }

        @Override // com.facebook.imagepipeline.producers.BaseConsumer
        public void onNewResultImpl(@Nullable CloseableReference<CloseableImage> closeableReference, int i) {
            boolean isTracing;
            BufferedDiskCache mainBufferedDiskCache;
            try {
                if (FrescoSystrace.isTracing()) {
                    FrescoSystrace.beginSection("BitmapProbeProducer#onNewResultImpl");
                }
                if (!isNotLast(i) && closeableReference != null && !statusHasAnyFlag(i, 8)) {
                    ImageRequest imageRequest = this.mProducerContext.getImageRequest();
                    CacheKey encodedCacheKey = this.mCacheKeyFactory.getEncodedCacheKey(imageRequest, this.mProducerContext.getCallerContext());
                    String str = (String) this.mProducerContext.getExtra("origin");
                    if (str != null && str.equals("memory_bitmap")) {
                        if (this.mProducerContext.getImagePipelineConfig().getExperiments().getIsEncodedMemoryCacheProbingEnabled() && !this.mEncodedMemoryCacheHistory.contains(encodedCacheKey)) {
                            this.mEncodedMemoryCache.probe(encodedCacheKey);
                            this.mEncodedMemoryCacheHistory.add(encodedCacheKey);
                        }
                        if (this.mProducerContext.getImagePipelineConfig().getExperiments().getIsDiskCacheProbingEnabled() && !this.mDiskCacheHistory.contains(encodedCacheKey)) {
                            boolean z = imageRequest.getCacheChoice() == ImageRequest.CacheChoice.SMALL;
                            DiskCachesStore diskCachesStore = this.mDiskCachesStoreSupplier.get();
                            if (z) {
                                mainBufferedDiskCache = diskCachesStore.getSmallImageBufferedDiskCache();
                            } else {
                                mainBufferedDiskCache = diskCachesStore.getMainBufferedDiskCache();
                            }
                            mainBufferedDiskCache.addKeyForAsyncProbing(encodedCacheKey);
                            this.mDiskCacheHistory.add(encodedCacheKey);
                        }
                    }
                    getConsumer().onNewResult(closeableReference, i);
                    if (isTracing) {
                        return;
                    } else {
                        return;
                    }
                }
                getConsumer().onNewResult(closeableReference, i);
                if (FrescoSystrace.isTracing()) {
                    FrescoSystrace.endSection();
                }
            } finally {
                if (FrescoSystrace.isTracing()) {
                    FrescoSystrace.endSection();
                }
            }
        }
    }

    protected String getProducerName() {
        return PRODUCER_NAME;
    }
}
