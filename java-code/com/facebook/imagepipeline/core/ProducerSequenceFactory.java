package com.facebook.imagepipeline.core;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Build;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.media.MediaUtils;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.producers.AddImageTransformMetaDataProducer;
import com.facebook.imagepipeline.producers.BitmapMemoryCacheGetProducer;
import com.facebook.imagepipeline.producers.BitmapMemoryCacheKeyMultiplexProducer;
import com.facebook.imagepipeline.producers.BitmapMemoryCacheProducer;
import com.facebook.imagepipeline.producers.BitmapPrepareProducer;
import com.facebook.imagepipeline.producers.BitmapProbeProducer;
import com.facebook.imagepipeline.producers.BranchOnSeparateImagesProducer;
import com.facebook.imagepipeline.producers.CustomProducerSequenceFactory;
import com.facebook.imagepipeline.producers.DataFetchProducer;
import com.facebook.imagepipeline.producers.DecodeProducer;
import com.facebook.imagepipeline.producers.DelayProducer;
import com.facebook.imagepipeline.producers.DiskCacheReadProducer;
import com.facebook.imagepipeline.producers.DiskCacheWriteProducer;
import com.facebook.imagepipeline.producers.EncodedCacheKeyMultiplexProducer;
import com.facebook.imagepipeline.producers.EncodedProbeProducer;
import com.facebook.imagepipeline.producers.LocalAssetFetchProducer;
import com.facebook.imagepipeline.producers.LocalContentUriFetchProducer;
import com.facebook.imagepipeline.producers.LocalFileFetchProducer;
import com.facebook.imagepipeline.producers.LocalResourceFetchProducer;
import com.facebook.imagepipeline.producers.LocalThumbnailBitmapSdk29Producer;
import com.facebook.imagepipeline.producers.LocalVideoThumbnailProducer;
import com.facebook.imagepipeline.producers.NetworkFetcher;
import com.facebook.imagepipeline.producers.PartialDiskCacheProducer;
import com.facebook.imagepipeline.producers.PostprocessedBitmapMemoryCacheProducer;
import com.facebook.imagepipeline.producers.PostprocessorProducer;
import com.facebook.imagepipeline.producers.Producer;
import com.facebook.imagepipeline.producers.QualifiedResourceFetchProducer;
import com.facebook.imagepipeline.producers.RemoveImageTransformMetaDataProducer;
import com.facebook.imagepipeline.producers.ResizeAndRotateProducer;
import com.facebook.imagepipeline.producers.SwallowResultProducer;
import com.facebook.imagepipeline.producers.ThreadHandoffProducerQueue;
import com.facebook.imagepipeline.producers.ThrottlingProducer;
import com.facebook.imagepipeline.producers.ThumbnailBranchProducer;
import com.facebook.imagepipeline.producers.ThumbnailProducer;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.systrace.FrescoSystrace;
import com.facebook.imagepipeline.transcoder.ImageTranscoderFactory;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ProducerSequenceFactory.kt */
@Metadata(d1 = {"\u0000\u0084\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010%\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0014\n\u0002\u0018\u0002\n\u0002\b-\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u000e\u0018\u0000 \u0084\u00012\u00020\u0001:\u0002\u0084\u0001B\u008b\u0001\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\n\u0010\u0006\u001a\u0006\u0012\u0002\b\u00030\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\t\u0012\u0006\u0010\u000b\u001a\u00020\f\u0012\u0006\u0010\r\u001a\u00020\u000e\u0012\u0006\u0010\u000f\u001a\u00020\t\u0012\u0006\u0010\u0010\u001a\u00020\t\u0012\u0006\u0010\u0011\u001a\u00020\t\u0012\u0006\u0010\u0012\u001a\u00020\u0013\u0012\u0006\u0010\u0014\u001a\u00020\t\u0012\u0006\u0010\u0015\u001a\u00020\t\u0012\u0006\u0010\u0016\u001a\u00020\t\u0012\u000e\u0010\u0017\u001a\n\u0012\u0004\u0012\u00020\u0019\u0018\u00010\u0018¢\u0006\u0004\b\u001a\u0010\u001bJ\u001a\u00100\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002010\u001f0\u001e2\u0006\u00102\u001a\u000203J\u0016\u0010@\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010(0\u001e2\u0006\u00102\u001a\u000203J\u001a\u0010A\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020 0\u001f0\u001e2\u0006\u00102\u001a\u000203J\u0016\u0010B\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010(0\u001e2\u0006\u00102\u001a\u000203J\u001c\u0010C\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020 0\u001f0\u001e2\u0006\u00102\u001a\u000203H\u0002J\u0018\u0010Q\u001a\b\u0012\u0004\u0012\u00020H0\u001e2\n\u0010\u0006\u001a\u0006\u0012\u0002\b\u00030\u0007J\"\u0010s\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020 0\u001f0\u001e2\f\u0010t\u001a\b\u0012\u0004\u0012\u00020H0\u001eH\u0002J;\u0010s\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020 0\u001f0\u001e2\f\u0010t\u001a\b\u0012\u0004\u0012\u00020H0\u001e2\u0012\u0010u\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020H0w0vH\u0002¢\u0006\u0002\u0010xJ \u0010y\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020 0\u001f0\u001e2\f\u0010t\u001a\b\u0012\u0004\u0012\u00020H0\u001eJ\u001c\u0010z\u001a\b\u0012\u0004\u0012\u00020H0\u001e2\f\u0010t\u001a\b\u0012\u0004\u0012\u00020H0\u001eH\u0002J\u001c\u0010{\u001a\b\u0012\u0004\u0012\u00020H0\u001e2\f\u0010t\u001a\b\u0012\u0004\u0012\u00020H0\u001eH\u0002J(\u0010|\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020 0\u001f0\u001e2\u0012\u0010t\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020 0\u001f0\u001eH\u0002J5\u0010}\u001a\b\u0012\u0004\u0012\u00020H0\u001e2\f\u0010t\u001a\b\u0012\u0004\u0012\u00020H0\u001e2\u0012\u0010u\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020H0w0vH\u0002¢\u0006\u0002\u0010xJ'\u0010~\u001a\b\u0012\u0004\u0012\u00020H0\u001e2\u0012\u0010u\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020H0w0vH\u0002¢\u0006\u0002\u0010\u007fJ)\u0010\u0080\u0001\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020 0\u001f0\u001e2\u0012\u0010t\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020 0\u001f0\u001eH\u0002J%\u0010\u0081\u0001\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010(0\u001e2\u0012\u0010t\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020 0\u001f0\u001eH\u0002J)\u0010\u0082\u0001\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020 0\u001f0\u001e2\u0012\u0010t\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020 0\u001f0\u001eH\u0002J)\u0010\u0083\u0001\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020 0\u001f0\u001e2\u0012\u0010t\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020 0\u001f0\u001eH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0012\u0010\u0006\u001a\u0006\u0012\u0002\b\u00030\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u0016\u0010\u0017\u001a\n\u0012\u0004\u0012\u00020\u0019\u0018\u00010\u0018X\u0082\u0004¢\u0006\u0002\n\u0000RH\u0010\u001c\u001a&\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020 0\u001f0\u001e\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020 0\u001f0\u001e0\u001d8\u0006@\u0006X\u0087\u000e¢\u0006\u0014\n\u0000\u0012\u0004\b!\u0010\"\u001a\u0004\b#\u0010$\"\u0004\b%\u0010&RD\u0010'\u001a\"\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020 0\u001f0\u001e\u0012\f\u0012\n\u0012\u0006\u0012\u0004\u0018\u00010(0\u001e0\u001d8\u0006@\u0006X\u0087\u000e¢\u0006\u0014\n\u0000\u0012\u0004\b)\u0010\"\u001a\u0004\b*\u0010$\"\u0004\b+\u0010&RH\u0010,\u001a&\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020 0\u001f0\u001e\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020 0\u001f0\u001e0\u001d8\u0006@\u0006X\u0087\u000e¢\u0006\u0014\n\u0000\u0012\u0004\b-\u0010\"\u001a\u0004\b.\u0010$\"\u0004\b/\u0010&R'\u00104\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002010\u001f0\u001e8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b7\u00108\u001a\u0004\b5\u00106R-\u00109\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002010\u001f0\u001e8FX\u0087\u0084\u0002¢\u0006\u0012\n\u0004\b<\u00108\u0012\u0004\b:\u0010\"\u001a\u0004\b;\u00106R'\u0010=\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002010\u001f0\u001e8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b?\u00108\u001a\u0004\b>\u00106R'\u0010D\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020 0\u001f0\u001e8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\bF\u00108\u001a\u0004\bE\u00106R#\u0010G\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010H0\u001e8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\bJ\u00108\u001a\u0004\bI\u00106R#\u0010K\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010(0\u001e8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\bM\u00108\u001a\u0004\bL\u00106R!\u0010N\u001a\b\u0012\u0004\u0012\u00020H0\u001e8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\bP\u00108\u001a\u0004\bO\u00106R#\u0010R\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010(0\u001e8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\bT\u00108\u001a\u0004\bS\u00106R#\u0010U\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010H0\u001e8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\bW\u00108\u001a\u0004\bV\u00106R#\u0010X\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010H0\u001e8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\bZ\u00108\u001a\u0004\bY\u00106R'\u0010[\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020 0\u001f0\u001e8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b]\u00108\u001a\u0004\b\\\u00106R'\u0010^\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020 0\u001f0\u001e8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b`\u00108\u001a\u0004\b_\u00106R'\u0010a\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020 0\u001f0\u001e8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\bc\u00108\u001a\u0004\bb\u00106R'\u0010d\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020 0\u001f0\u001e8GX\u0086\u0084\u0002¢\u0006\f\n\u0004\bf\u00108\u001a\u0004\be\u00106R'\u0010g\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020 0\u001f0\u001e8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\bi\u00108\u001a\u0004\bh\u00106R'\u0010j\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020 0\u001f0\u001e8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\bl\u00108\u001a\u0004\bk\u00106R'\u0010m\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020 0\u001f0\u001e8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\bo\u00108\u001a\u0004\bn\u00106R'\u0010p\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020 0\u001f0\u001e8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\br\u00108\u001a\u0004\bq\u00106¨\u0006\u0085\u0001"}, d2 = {"Lcom/facebook/imagepipeline/core/ProducerSequenceFactory;", "", "contentResolver", "Landroid/content/ContentResolver;", "producerFactory", "Lcom/facebook/imagepipeline/core/ProducerFactory;", "networkFetcher", "Lcom/facebook/imagepipeline/producers/NetworkFetcher;", "resizeAndRotateEnabledForNetwork", "", "webpSupportEnabled", "threadHandoffProducerQueue", "Lcom/facebook/imagepipeline/producers/ThreadHandoffProducerQueue;", "downsampleMode", "Lcom/facebook/imagepipeline/core/DownsampleMode;", "useBitmapPrepareToDraw", "partialImageCachingEnabled", "diskCacheEnabled", "imageTranscoderFactory", "Lcom/facebook/imagepipeline/transcoder/ImageTranscoderFactory;", "isEncodedMemoryCacheProbingEnabled", "isDiskCacheProbingEnabled", "allowDelay", "customProducerSequenceFactories", "", "Lcom/facebook/imagepipeline/producers/CustomProducerSequenceFactory;", "<init>", "(Landroid/content/ContentResolver;Lcom/facebook/imagepipeline/core/ProducerFactory;Lcom/facebook/imagepipeline/producers/NetworkFetcher;ZZLcom/facebook/imagepipeline/producers/ThreadHandoffProducerQueue;Lcom/facebook/imagepipeline/core/DownsampleMode;ZZZLcom/facebook/imagepipeline/transcoder/ImageTranscoderFactory;ZZZLjava/util/Set;)V", "postprocessorSequences", "", "Lcom/facebook/imagepipeline/producers/Producer;", "Lcom/facebook/common/references/CloseableReference;", "Lcom/facebook/imagepipeline/image/CloseableImage;", "getPostprocessorSequences$annotations", "()V", "getPostprocessorSequences", "()Ljava/util/Map;", "setPostprocessorSequences", "(Ljava/util/Map;)V", "closeableImagePrefetchSequences", "Ljava/lang/Void;", "getCloseableImagePrefetchSequences$annotations", "getCloseableImagePrefetchSequences", "setCloseableImagePrefetchSequences", "bitmapPrepareSequences", "getBitmapPrepareSequences$annotations", "getBitmapPrepareSequences", "setBitmapPrepareSequences", "getEncodedImageProducerSequence", "Lcom/facebook/common/memory/PooledByteBuffer;", "imageRequest", "Lcom/facebook/imagepipeline/request/ImageRequest;", "networkFetchEncodedImageProducerSequence", "getNetworkFetchEncodedImageProducerSequence", "()Lcom/facebook/imagepipeline/producers/Producer;", "networkFetchEncodedImageProducerSequence$delegate", "Lkotlin/Lazy;", "localFileFetchEncodedImageProducerSequence", "getLocalFileFetchEncodedImageProducerSequence$annotations", "getLocalFileFetchEncodedImageProducerSequence", "localFileFetchEncodedImageProducerSequence$delegate", "localContentUriFetchEncodedImageProducerSequence", "getLocalContentUriFetchEncodedImageProducerSequence", "localContentUriFetchEncodedImageProducerSequence$delegate", "getEncodedImagePrefetchProducerSequence", "getDecodedImageProducerSequence", "getDecodedImagePrefetchProducerSequence", "getBasicDecodedImageSequence", "networkFetchSequence", "getNetworkFetchSequence", "networkFetchSequence$delegate", "backgroundNetworkFetchToEncodedMemorySequence", "Lcom/facebook/imagepipeline/image/EncodedImage;", "getBackgroundNetworkFetchToEncodedMemorySequence", "backgroundNetworkFetchToEncodedMemorySequence$delegate", "networkFetchToEncodedMemoryPrefetchSequence", "getNetworkFetchToEncodedMemoryPrefetchSequence", "networkFetchToEncodedMemoryPrefetchSequence$delegate", "commonNetworkFetchToEncodedMemorySequence", "getCommonNetworkFetchToEncodedMemorySequence", "commonNetworkFetchToEncodedMemorySequence$delegate", "newCommonNetworkFetchToEncodedMemorySequence", "localFileFetchToEncodedMemoryPrefetchSequence", "getLocalFileFetchToEncodedMemoryPrefetchSequence", "localFileFetchToEncodedMemoryPrefetchSequence$delegate", "backgroundLocalFileFetchToEncodeMemorySequence", "getBackgroundLocalFileFetchToEncodeMemorySequence", "backgroundLocalFileFetchToEncodeMemorySequence$delegate", "backgroundLocalContentUriFetchToEncodeMemorySequence", "getBackgroundLocalContentUriFetchToEncodeMemorySequence", "backgroundLocalContentUriFetchToEncodeMemorySequence$delegate", "localImageFileFetchSequence", "getLocalImageFileFetchSequence", "localImageFileFetchSequence$delegate", "localVideoFileFetchSequence", "getLocalVideoFileFetchSequence", "localVideoFileFetchSequence$delegate", "localContentUriFetchSequence", "getLocalContentUriFetchSequence", "localContentUriFetchSequence$delegate", "localThumbnailBitmapSdk29FetchSequence", "getLocalThumbnailBitmapSdk29FetchSequence", "localThumbnailBitmapSdk29FetchSequence$delegate", "qualifiedResourceFetchSequence", "getQualifiedResourceFetchSequence", "qualifiedResourceFetchSequence$delegate", "localResourceFetchSequence", "getLocalResourceFetchSequence", "localResourceFetchSequence$delegate", "localAssetFetchSequence", "getLocalAssetFetchSequence", "localAssetFetchSequence$delegate", "dataFetchSequence", "getDataFetchSequence", "dataFetchSequence$delegate", "newBitmapCacheGetToLocalTransformSequence", "inputProducer", "thumbnailProducers", "", "Lcom/facebook/imagepipeline/producers/ThumbnailProducer;", "(Lcom/facebook/imagepipeline/producers/Producer;[Lcom/facebook/imagepipeline/producers/ThumbnailProducer;)Lcom/facebook/imagepipeline/producers/Producer;", "newBitmapCacheGetToDecodeSequence", "newEncodedCacheMultiplexToTranscodeSequence", "newDiskCacheSequence", "newBitmapCacheGetToBitmapCacheSequence", "newLocalTransformationsSequence", "newLocalThumbnailProducer", "([Lcom/facebook/imagepipeline/producers/ThumbnailProducer;)Lcom/facebook/imagepipeline/producers/Producer;", "getPostprocessorSequence", "getDecodedImagePrefetchSequence", "getBitmapPrepareSequence", "getDelaySequence", "Companion", "imagepipeline_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes.dex */
public final class ProducerSequenceFactory {

    /* renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private final boolean allowDelay;

    /* renamed from: backgroundLocalContentUriFetchToEncodeMemorySequence$delegate, reason: from kotlin metadata */
    private final Lazy backgroundLocalContentUriFetchToEncodeMemorySequence;

    /* renamed from: backgroundLocalFileFetchToEncodeMemorySequence$delegate, reason: from kotlin metadata */
    private final Lazy backgroundLocalFileFetchToEncodeMemorySequence;

    /* renamed from: backgroundNetworkFetchToEncodedMemorySequence$delegate, reason: from kotlin metadata */
    private final Lazy backgroundNetworkFetchToEncodedMemorySequence;
    private Map<Producer<CloseableReference<CloseableImage>>, Producer<CloseableReference<CloseableImage>>> bitmapPrepareSequences;
    private Map<Producer<CloseableReference<CloseableImage>>, Producer<Void>> closeableImagePrefetchSequences;

    /* renamed from: commonNetworkFetchToEncodedMemorySequence$delegate, reason: from kotlin metadata */
    private final Lazy commonNetworkFetchToEncodedMemorySequence;
    private final ContentResolver contentResolver;
    private final Set<CustomProducerSequenceFactory> customProducerSequenceFactories;

    /* renamed from: dataFetchSequence$delegate, reason: from kotlin metadata */
    private final Lazy dataFetchSequence;
    private final boolean diskCacheEnabled;
    private final DownsampleMode downsampleMode;
    private final ImageTranscoderFactory imageTranscoderFactory;
    private final boolean isDiskCacheProbingEnabled;
    private final boolean isEncodedMemoryCacheProbingEnabled;

    /* renamed from: localAssetFetchSequence$delegate, reason: from kotlin metadata */
    private final Lazy localAssetFetchSequence;

    /* renamed from: localContentUriFetchEncodedImageProducerSequence$delegate, reason: from kotlin metadata */
    private final Lazy localContentUriFetchEncodedImageProducerSequence;

    /* renamed from: localContentUriFetchSequence$delegate, reason: from kotlin metadata */
    private final Lazy localContentUriFetchSequence;

    /* renamed from: localFileFetchEncodedImageProducerSequence$delegate, reason: from kotlin metadata */
    private final Lazy localFileFetchEncodedImageProducerSequence;

    /* renamed from: localFileFetchToEncodedMemoryPrefetchSequence$delegate, reason: from kotlin metadata */
    private final Lazy localFileFetchToEncodedMemoryPrefetchSequence;

    /* renamed from: localImageFileFetchSequence$delegate, reason: from kotlin metadata */
    private final Lazy localImageFileFetchSequence;

    /* renamed from: localResourceFetchSequence$delegate, reason: from kotlin metadata */
    private final Lazy localResourceFetchSequence;

    /* renamed from: localThumbnailBitmapSdk29FetchSequence$delegate, reason: from kotlin metadata */
    private final Lazy localThumbnailBitmapSdk29FetchSequence;

    /* renamed from: localVideoFileFetchSequence$delegate, reason: from kotlin metadata */
    private final Lazy localVideoFileFetchSequence;

    /* renamed from: networkFetchEncodedImageProducerSequence$delegate, reason: from kotlin metadata */
    private final Lazy networkFetchEncodedImageProducerSequence;

    /* renamed from: networkFetchSequence$delegate, reason: from kotlin metadata */
    private final Lazy networkFetchSequence;

    /* renamed from: networkFetchToEncodedMemoryPrefetchSequence$delegate, reason: from kotlin metadata */
    private final Lazy networkFetchToEncodedMemoryPrefetchSequence;
    private final NetworkFetcher<?> networkFetcher;
    private final boolean partialImageCachingEnabled;
    private Map<Producer<CloseableReference<CloseableImage>>, Producer<CloseableReference<CloseableImage>>> postprocessorSequences;
    private final ProducerFactory producerFactory;

    /* renamed from: qualifiedResourceFetchSequence$delegate, reason: from kotlin metadata */
    private final Lazy qualifiedResourceFetchSequence;
    private final boolean resizeAndRotateEnabledForNetwork;
    private final ThreadHandoffProducerQueue threadHandoffProducerQueue;
    private final boolean useBitmapPrepareToDraw;
    private final boolean webpSupportEnabled;

    public static /* synthetic */ void getBitmapPrepareSequences$annotations() {
    }

    public static /* synthetic */ void getCloseableImagePrefetchSequences$annotations() {
    }

    public static /* synthetic */ void getLocalFileFetchEncodedImageProducerSequence$annotations() {
    }

    public static /* synthetic */ void getPostprocessorSequences$annotations() {
    }

    /* JADX WARN: Multi-variable type inference failed */
    public ProducerSequenceFactory(ContentResolver contentResolver, ProducerFactory producerFactory, NetworkFetcher<?> networkFetcher, boolean z, boolean z2, ThreadHandoffProducerQueue threadHandoffProducerQueue, DownsampleMode downsampleMode, boolean z3, boolean z4, boolean z5, ImageTranscoderFactory imageTranscoderFactory, boolean z6, boolean z7, boolean z8, Set<? extends CustomProducerSequenceFactory> set) {
        Intrinsics.checkNotNullParameter(contentResolver, "contentResolver");
        Intrinsics.checkNotNullParameter(producerFactory, "producerFactory");
        Intrinsics.checkNotNullParameter(networkFetcher, "networkFetcher");
        Intrinsics.checkNotNullParameter(threadHandoffProducerQueue, "threadHandoffProducerQueue");
        Intrinsics.checkNotNullParameter(downsampleMode, "downsampleMode");
        Intrinsics.checkNotNullParameter(imageTranscoderFactory, "imageTranscoderFactory");
        this.contentResolver = contentResolver;
        this.producerFactory = producerFactory;
        this.networkFetcher = networkFetcher;
        this.resizeAndRotateEnabledForNetwork = z;
        this.webpSupportEnabled = z2;
        this.threadHandoffProducerQueue = threadHandoffProducerQueue;
        this.downsampleMode = downsampleMode;
        this.useBitmapPrepareToDraw = z3;
        this.partialImageCachingEnabled = z4;
        this.diskCacheEnabled = z5;
        this.imageTranscoderFactory = imageTranscoderFactory;
        this.isEncodedMemoryCacheProbingEnabled = z6;
        this.isDiskCacheProbingEnabled = z7;
        this.allowDelay = z8;
        this.customProducerSequenceFactories = set;
        this.postprocessorSequences = new LinkedHashMap();
        this.closeableImagePrefetchSequences = new LinkedHashMap();
        this.bitmapPrepareSequences = new LinkedHashMap();
        this.networkFetchEncodedImageProducerSequence = LazyKt.lazy(new Function0() { // from class: com.facebook.imagepipeline.core.ProducerSequenceFactory$$ExternalSyntheticLambda0
            @Override // kotlin.jvm.functions.Function0
            public final Object invoke() {
                RemoveImageTransformMetaDataProducer networkFetchEncodedImageProducerSequence_delegate$lambda$2;
                networkFetchEncodedImageProducerSequence_delegate$lambda$2 = ProducerSequenceFactory.networkFetchEncodedImageProducerSequence_delegate$lambda$2(ProducerSequenceFactory.this);
                return networkFetchEncodedImageProducerSequence_delegate$lambda$2;
            }
        });
        this.localFileFetchEncodedImageProducerSequence = LazyKt.lazy(new Function0() { // from class: com.facebook.imagepipeline.core.ProducerSequenceFactory$$ExternalSyntheticLambda17
            @Override // kotlin.jvm.functions.Function0
            public final Object invoke() {
                RemoveImageTransformMetaDataProducer localFileFetchEncodedImageProducerSequence_delegate$lambda$4;
                localFileFetchEncodedImageProducerSequence_delegate$lambda$4 = ProducerSequenceFactory.localFileFetchEncodedImageProducerSequence_delegate$lambda$4(ProducerSequenceFactory.this);
                return localFileFetchEncodedImageProducerSequence_delegate$lambda$4;
            }
        });
        this.localContentUriFetchEncodedImageProducerSequence = LazyKt.lazy(new Function0() { // from class: com.facebook.imagepipeline.core.ProducerSequenceFactory$$ExternalSyntheticLambda1
            @Override // kotlin.jvm.functions.Function0
            public final Object invoke() {
                RemoveImageTransformMetaDataProducer localContentUriFetchEncodedImageProducerSequence_delegate$lambda$6;
                localContentUriFetchEncodedImageProducerSequence_delegate$lambda$6 = ProducerSequenceFactory.localContentUriFetchEncodedImageProducerSequence_delegate$lambda$6(ProducerSequenceFactory.this);
                return localContentUriFetchEncodedImageProducerSequence_delegate$lambda$6;
            }
        });
        this.networkFetchSequence = LazyKt.lazy(new Function0() { // from class: com.facebook.imagepipeline.core.ProducerSequenceFactory$$ExternalSyntheticLambda2
            @Override // kotlin.jvm.functions.Function0
            public final Object invoke() {
                Producer networkFetchSequence_delegate$lambda$11;
                networkFetchSequence_delegate$lambda$11 = ProducerSequenceFactory.networkFetchSequence_delegate$lambda$11(ProducerSequenceFactory.this);
                return networkFetchSequence_delegate$lambda$11;
            }
        });
        this.backgroundNetworkFetchToEncodedMemorySequence = LazyKt.lazy(new Function0() { // from class: com.facebook.imagepipeline.core.ProducerSequenceFactory$$ExternalSyntheticLambda3
            @Override // kotlin.jvm.functions.Function0
            public final Object invoke() {
                Producer backgroundNetworkFetchToEncodedMemorySequence_delegate$lambda$13;
                backgroundNetworkFetchToEncodedMemorySequence_delegate$lambda$13 = ProducerSequenceFactory.backgroundNetworkFetchToEncodedMemorySequence_delegate$lambda$13(ProducerSequenceFactory.this);
                return backgroundNetworkFetchToEncodedMemorySequence_delegate$lambda$13;
            }
        });
        this.networkFetchToEncodedMemoryPrefetchSequence = LazyKt.lazy(new Function0() { // from class: com.facebook.imagepipeline.core.ProducerSequenceFactory$$ExternalSyntheticLambda4
            @Override // kotlin.jvm.functions.Function0
            public final Object invoke() {
                SwallowResultProducer networkFetchToEncodedMemoryPrefetchSequence_delegate$lambda$15;
                networkFetchToEncodedMemoryPrefetchSequence_delegate$lambda$15 = ProducerSequenceFactory.networkFetchToEncodedMemoryPrefetchSequence_delegate$lambda$15(ProducerSequenceFactory.this);
                return networkFetchToEncodedMemoryPrefetchSequence_delegate$lambda$15;
            }
        });
        this.commonNetworkFetchToEncodedMemorySequence = LazyKt.lazy(new Function0() { // from class: com.facebook.imagepipeline.core.ProducerSequenceFactory$$ExternalSyntheticLambda5
            @Override // kotlin.jvm.functions.Function0
            public final Object invoke() {
                Producer commonNetworkFetchToEncodedMemorySequence_delegate$lambda$17;
                commonNetworkFetchToEncodedMemorySequence_delegate$lambda$17 = ProducerSequenceFactory.commonNetworkFetchToEncodedMemorySequence_delegate$lambda$17(ProducerSequenceFactory.this);
                return commonNetworkFetchToEncodedMemorySequence_delegate$lambda$17;
            }
        });
        this.localFileFetchToEncodedMemoryPrefetchSequence = LazyKt.lazy(new Function0() { // from class: com.facebook.imagepipeline.core.ProducerSequenceFactory$$ExternalSyntheticLambda6
            @Override // kotlin.jvm.functions.Function0
            public final Object invoke() {
                SwallowResultProducer localFileFetchToEncodedMemoryPrefetchSequence_delegate$lambda$20;
                localFileFetchToEncodedMemoryPrefetchSequence_delegate$lambda$20 = ProducerSequenceFactory.localFileFetchToEncodedMemoryPrefetchSequence_delegate$lambda$20(ProducerSequenceFactory.this);
                return localFileFetchToEncodedMemoryPrefetchSequence_delegate$lambda$20;
            }
        });
        this.backgroundLocalFileFetchToEncodeMemorySequence = LazyKt.lazy(new Function0() { // from class: com.facebook.imagepipeline.core.ProducerSequenceFactory$$ExternalSyntheticLambda7
            @Override // kotlin.jvm.functions.Function0
            public final Object invoke() {
                Producer backgroundLocalFileFetchToEncodeMemorySequence_delegate$lambda$22;
                backgroundLocalFileFetchToEncodeMemorySequence_delegate$lambda$22 = ProducerSequenceFactory.backgroundLocalFileFetchToEncodeMemorySequence_delegate$lambda$22(ProducerSequenceFactory.this);
                return backgroundLocalFileFetchToEncodeMemorySequence_delegate$lambda$22;
            }
        });
        this.backgroundLocalContentUriFetchToEncodeMemorySequence = LazyKt.lazy(new Function0() { // from class: com.facebook.imagepipeline.core.ProducerSequenceFactory$$ExternalSyntheticLambda8
            @Override // kotlin.jvm.functions.Function0
            public final Object invoke() {
                Producer backgroundLocalContentUriFetchToEncodeMemorySequence_delegate$lambda$24;
                backgroundLocalContentUriFetchToEncodeMemorySequence_delegate$lambda$24 = ProducerSequenceFactory.backgroundLocalContentUriFetchToEncodeMemorySequence_delegate$lambda$24(ProducerSequenceFactory.this);
                return backgroundLocalContentUriFetchToEncodeMemorySequence_delegate$lambda$24;
            }
        });
        this.localImageFileFetchSequence = LazyKt.lazy(new Function0() { // from class: com.facebook.imagepipeline.core.ProducerSequenceFactory$$ExternalSyntheticLambda9
            @Override // kotlin.jvm.functions.Function0
            public final Object invoke() {
                Producer localImageFileFetchSequence_delegate$lambda$25;
                localImageFileFetchSequence_delegate$lambda$25 = ProducerSequenceFactory.localImageFileFetchSequence_delegate$lambda$25(ProducerSequenceFactory.this);
                return localImageFileFetchSequence_delegate$lambda$25;
            }
        });
        this.localVideoFileFetchSequence = LazyKt.lazy(new Function0() { // from class: com.facebook.imagepipeline.core.ProducerSequenceFactory$$ExternalSyntheticLambda10
            @Override // kotlin.jvm.functions.Function0
            public final Object invoke() {
                Producer localVideoFileFetchSequence_delegate$lambda$26;
                localVideoFileFetchSequence_delegate$lambda$26 = ProducerSequenceFactory.localVideoFileFetchSequence_delegate$lambda$26(ProducerSequenceFactory.this);
                return localVideoFileFetchSequence_delegate$lambda$26;
            }
        });
        this.localContentUriFetchSequence = LazyKt.lazy(new Function0() { // from class: com.facebook.imagepipeline.core.ProducerSequenceFactory$$ExternalSyntheticLambda11
            @Override // kotlin.jvm.functions.Function0
            public final Object invoke() {
                Producer localContentUriFetchSequence_delegate$lambda$27;
                localContentUriFetchSequence_delegate$lambda$27 = ProducerSequenceFactory.localContentUriFetchSequence_delegate$lambda$27(ProducerSequenceFactory.this);
                return localContentUriFetchSequence_delegate$lambda$27;
            }
        });
        this.localThumbnailBitmapSdk29FetchSequence = LazyKt.lazy(new Function0() { // from class: com.facebook.imagepipeline.core.ProducerSequenceFactory$$ExternalSyntheticLambda12
            @Override // kotlin.jvm.functions.Function0
            public final Object invoke() {
                Producer localThumbnailBitmapSdk29FetchSequence_delegate$lambda$28;
                localThumbnailBitmapSdk29FetchSequence_delegate$lambda$28 = ProducerSequenceFactory.localThumbnailBitmapSdk29FetchSequence_delegate$lambda$28(ProducerSequenceFactory.this);
                return localThumbnailBitmapSdk29FetchSequence_delegate$lambda$28;
            }
        });
        this.qualifiedResourceFetchSequence = LazyKt.lazy(new Function0() { // from class: com.facebook.imagepipeline.core.ProducerSequenceFactory$$ExternalSyntheticLambda13
            @Override // kotlin.jvm.functions.Function0
            public final Object invoke() {
                Producer qualifiedResourceFetchSequence_delegate$lambda$29;
                qualifiedResourceFetchSequence_delegate$lambda$29 = ProducerSequenceFactory.qualifiedResourceFetchSequence_delegate$lambda$29(ProducerSequenceFactory.this);
                return qualifiedResourceFetchSequence_delegate$lambda$29;
            }
        });
        this.localResourceFetchSequence = LazyKt.lazy(new Function0() { // from class: com.facebook.imagepipeline.core.ProducerSequenceFactory$$ExternalSyntheticLambda14
            @Override // kotlin.jvm.functions.Function0
            public final Object invoke() {
                Producer localResourceFetchSequence_delegate$lambda$30;
                localResourceFetchSequence_delegate$lambda$30 = ProducerSequenceFactory.localResourceFetchSequence_delegate$lambda$30(ProducerSequenceFactory.this);
                return localResourceFetchSequence_delegate$lambda$30;
            }
        });
        this.localAssetFetchSequence = LazyKt.lazy(new Function0() { // from class: com.facebook.imagepipeline.core.ProducerSequenceFactory$$ExternalSyntheticLambda15
            @Override // kotlin.jvm.functions.Function0
            public final Object invoke() {
                Producer localAssetFetchSequence_delegate$lambda$31;
                localAssetFetchSequence_delegate$lambda$31 = ProducerSequenceFactory.localAssetFetchSequence_delegate$lambda$31(ProducerSequenceFactory.this);
                return localAssetFetchSequence_delegate$lambda$31;
            }
        });
        this.dataFetchSequence = LazyKt.lazy(new Function0() { // from class: com.facebook.imagepipeline.core.ProducerSequenceFactory$$ExternalSyntheticLambda16
            @Override // kotlin.jvm.functions.Function0
            public final Object invoke() {
                Producer dataFetchSequence_delegate$lambda$32;
                dataFetchSequence_delegate$lambda$32 = ProducerSequenceFactory.dataFetchSequence_delegate$lambda$32(ProducerSequenceFactory.this);
                return dataFetchSequence_delegate$lambda$32;
            }
        });
    }

    public final Map<Producer<CloseableReference<CloseableImage>>, Producer<CloseableReference<CloseableImage>>> getPostprocessorSequences() {
        return this.postprocessorSequences;
    }

    public final void setPostprocessorSequences(Map<Producer<CloseableReference<CloseableImage>>, Producer<CloseableReference<CloseableImage>>> map) {
        Intrinsics.checkNotNullParameter(map, "<set-?>");
        this.postprocessorSequences = map;
    }

    public final Map<Producer<CloseableReference<CloseableImage>>, Producer<Void>> getCloseableImagePrefetchSequences() {
        return this.closeableImagePrefetchSequences;
    }

    public final void setCloseableImagePrefetchSequences(Map<Producer<CloseableReference<CloseableImage>>, Producer<Void>> map) {
        Intrinsics.checkNotNullParameter(map, "<set-?>");
        this.closeableImagePrefetchSequences = map;
    }

    public final Map<Producer<CloseableReference<CloseableImage>>, Producer<CloseableReference<CloseableImage>>> getBitmapPrepareSequences() {
        return this.bitmapPrepareSequences;
    }

    public final void setBitmapPrepareSequences(Map<Producer<CloseableReference<CloseableImage>>, Producer<CloseableReference<CloseableImage>>> map) {
        Intrinsics.checkNotNullParameter(map, "<set-?>");
        this.bitmapPrepareSequences = map;
    }

    public final Producer<CloseableReference<PooledByteBuffer>> getEncodedImageProducerSequence(ImageRequest imageRequest) {
        Producer<CloseableReference<PooledByteBuffer>> networkFetchEncodedImageProducerSequence;
        Intrinsics.checkNotNullParameter(imageRequest, "imageRequest");
        FrescoSystrace frescoSystrace = FrescoSystrace.INSTANCE;
        if (!FrescoSystrace.isTracing()) {
            INSTANCE.validateEncodedImageRequest(imageRequest);
            Uri sourceUri = imageRequest.getSourceUri();
            Intrinsics.checkNotNullExpressionValue(sourceUri, "getSourceUri(...)");
            int sourceUriType = imageRequest.getSourceUriType();
            if (sourceUriType == 0) {
                return getNetworkFetchEncodedImageProducerSequence();
            }
            if (sourceUriType == 2 || sourceUriType == 3) {
                return getLocalFileFetchEncodedImageProducerSequence();
            }
            if (sourceUriType == 4) {
                return getLocalContentUriFetchEncodedImageProducerSequence();
            }
            Set<CustomProducerSequenceFactory> set = this.customProducerSequenceFactories;
            if (set != null) {
                Iterator<CustomProducerSequenceFactory> it = set.iterator();
                while (it.hasNext()) {
                    Producer<CloseableReference<PooledByteBuffer>> customEncodedImageSequence = it.next().getCustomEncodedImageSequence(imageRequest, this, this.producerFactory, this.threadHandoffProducerQueue);
                    if (customEncodedImageSequence != null) {
                        return customEncodedImageSequence;
                    }
                }
            }
            throw new IllegalArgumentException("Unsupported uri scheme for encoded image fetch! Uri is: " + INSTANCE.getShortenedUriString(sourceUri));
        }
        FrescoSystrace.beginSection("ProducerSequenceFactory#getEncodedImageProducerSequence");
        try {
            INSTANCE.validateEncodedImageRequest(imageRequest);
            Uri sourceUri2 = imageRequest.getSourceUri();
            Intrinsics.checkNotNullExpressionValue(sourceUri2, "getSourceUri(...)");
            int sourceUriType2 = imageRequest.getSourceUriType();
            if (sourceUriType2 == 0) {
                networkFetchEncodedImageProducerSequence = getNetworkFetchEncodedImageProducerSequence();
            } else if (sourceUriType2 == 2 || sourceUriType2 == 3) {
                networkFetchEncodedImageProducerSequence = getLocalFileFetchEncodedImageProducerSequence();
            } else {
                if (sourceUriType2 != 4) {
                    Set<CustomProducerSequenceFactory> set2 = this.customProducerSequenceFactories;
                    if (set2 != null) {
                        Iterator<CustomProducerSequenceFactory> it2 = set2.iterator();
                        while (it2.hasNext()) {
                            Producer<CloseableReference<PooledByteBuffer>> customEncodedImageSequence2 = it2.next().getCustomEncodedImageSequence(imageRequest, this, this.producerFactory, this.threadHandoffProducerQueue);
                            if (customEncodedImageSequence2 != null) {
                                return customEncodedImageSequence2;
                            }
                        }
                    }
                    throw new IllegalArgumentException("Unsupported uri scheme for encoded image fetch! Uri is: " + INSTANCE.getShortenedUriString(sourceUri2));
                }
                networkFetchEncodedImageProducerSequence = getLocalContentUriFetchEncodedImageProducerSequence();
            }
            return networkFetchEncodedImageProducerSequence;
        } finally {
            FrescoSystrace.endSection();
        }
    }

    public final Producer<CloseableReference<PooledByteBuffer>> getNetworkFetchEncodedImageProducerSequence() {
        return (Producer) this.networkFetchEncodedImageProducerSequence.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final RemoveImageTransformMetaDataProducer networkFetchEncodedImageProducerSequence_delegate$lambda$2(ProducerSequenceFactory this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        FrescoSystrace frescoSystrace = FrescoSystrace.INSTANCE;
        if (!FrescoSystrace.isTracing()) {
            return new RemoveImageTransformMetaDataProducer(this$0.getBackgroundNetworkFetchToEncodedMemorySequence());
        }
        FrescoSystrace.beginSection("ProducerSequenceFactory#getNetworkFetchEncodedImageProducerSequence:init");
        try {
            return new RemoveImageTransformMetaDataProducer(this$0.getBackgroundNetworkFetchToEncodedMemorySequence());
        } finally {
            FrescoSystrace.endSection();
        }
    }

    public final Producer<CloseableReference<PooledByteBuffer>> getLocalFileFetchEncodedImageProducerSequence() {
        return (Producer) this.localFileFetchEncodedImageProducerSequence.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final RemoveImageTransformMetaDataProducer localFileFetchEncodedImageProducerSequence_delegate$lambda$4(ProducerSequenceFactory this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        FrescoSystrace frescoSystrace = FrescoSystrace.INSTANCE;
        if (!FrescoSystrace.isTracing()) {
            return new RemoveImageTransformMetaDataProducer(this$0.getBackgroundLocalFileFetchToEncodeMemorySequence());
        }
        FrescoSystrace.beginSection("ProducerSequenceFactory#getLocalFileFetchEncodedImageProducerSequence:init");
        try {
            return new RemoveImageTransformMetaDataProducer(this$0.getBackgroundLocalFileFetchToEncodeMemorySequence());
        } finally {
            FrescoSystrace.endSection();
        }
    }

    public final Producer<CloseableReference<PooledByteBuffer>> getLocalContentUriFetchEncodedImageProducerSequence() {
        return (Producer) this.localContentUriFetchEncodedImageProducerSequence.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final RemoveImageTransformMetaDataProducer localContentUriFetchEncodedImageProducerSequence_delegate$lambda$6(ProducerSequenceFactory this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        FrescoSystrace frescoSystrace = FrescoSystrace.INSTANCE;
        if (!FrescoSystrace.isTracing()) {
            return new RemoveImageTransformMetaDataProducer(this$0.getBackgroundLocalContentUriFetchToEncodeMemorySequence());
        }
        FrescoSystrace.beginSection("ProducerSequenceFactory#getLocalContentUriFetchEncodedImageProducerSequence:init");
        try {
            return new RemoveImageTransformMetaDataProducer(this$0.getBackgroundLocalContentUriFetchToEncodeMemorySequence());
        } finally {
            FrescoSystrace.endSection();
        }
    }

    public final Producer<Void> getEncodedImagePrefetchProducerSequence(ImageRequest imageRequest) {
        Intrinsics.checkNotNullParameter(imageRequest, "imageRequest");
        Companion companion = INSTANCE;
        companion.validateEncodedImageRequest(imageRequest);
        int sourceUriType = imageRequest.getSourceUriType();
        if (sourceUriType == 0) {
            return getNetworkFetchToEncodedMemoryPrefetchSequence();
        }
        if (sourceUriType == 2 || sourceUriType == 3) {
            return getLocalFileFetchToEncodedMemoryPrefetchSequence();
        }
        Uri sourceUri = imageRequest.getSourceUri();
        Intrinsics.checkNotNullExpressionValue(sourceUri, "getSourceUri(...)");
        throw new IllegalArgumentException("Unsupported uri scheme for encoded image fetch! Uri is: " + companion.getShortenedUriString(sourceUri));
    }

    public final Producer<CloseableReference<CloseableImage>> getDecodedImageProducerSequence(ImageRequest imageRequest) {
        Intrinsics.checkNotNullParameter(imageRequest, "imageRequest");
        FrescoSystrace frescoSystrace = FrescoSystrace.INSTANCE;
        if (!FrescoSystrace.isTracing()) {
            Producer<CloseableReference<CloseableImage>> basicDecodedImageSequence = getBasicDecodedImageSequence(imageRequest);
            if (imageRequest.getPostprocessor() != null) {
                basicDecodedImageSequence = getPostprocessorSequence(basicDecodedImageSequence);
            }
            if (this.useBitmapPrepareToDraw) {
                basicDecodedImageSequence = getBitmapPrepareSequence(basicDecodedImageSequence);
            }
            return (!this.allowDelay || imageRequest.getDelayMs() <= 0) ? basicDecodedImageSequence : getDelaySequence(basicDecodedImageSequence);
        }
        FrescoSystrace.beginSection("ProducerSequenceFactory#getDecodedImageProducerSequence");
        try {
            Producer<CloseableReference<CloseableImage>> basicDecodedImageSequence2 = getBasicDecodedImageSequence(imageRequest);
            if (imageRequest.getPostprocessor() != null) {
                basicDecodedImageSequence2 = getPostprocessorSequence(basicDecodedImageSequence2);
            }
            if (this.useBitmapPrepareToDraw) {
                basicDecodedImageSequence2 = getBitmapPrepareSequence(basicDecodedImageSequence2);
            }
            if (this.allowDelay && imageRequest.getDelayMs() > 0) {
                basicDecodedImageSequence2 = getDelaySequence(basicDecodedImageSequence2);
            }
            return basicDecodedImageSequence2;
        } finally {
            FrescoSystrace.endSection();
        }
    }

    public final Producer<Void> getDecodedImagePrefetchProducerSequence(ImageRequest imageRequest) {
        Intrinsics.checkNotNullParameter(imageRequest, "imageRequest");
        Producer<CloseableReference<CloseableImage>> basicDecodedImageSequence = getBasicDecodedImageSequence(imageRequest);
        if (this.useBitmapPrepareToDraw) {
            basicDecodedImageSequence = getBitmapPrepareSequence(basicDecodedImageSequence);
        }
        return getDecodedImagePrefetchSequence(basicDecodedImageSequence);
    }

    private final Producer<CloseableReference<CloseableImage>> getBasicDecodedImageSequence(ImageRequest imageRequest) {
        Producer<CloseableReference<CloseableImage>> networkFetchSequence;
        FrescoSystrace frescoSystrace = FrescoSystrace.INSTANCE;
        if (!FrescoSystrace.isTracing()) {
            Uri sourceUri = imageRequest.getSourceUri();
            Intrinsics.checkNotNullExpressionValue(sourceUri, "getSourceUri(...)");
            if (sourceUri == null) {
                throw new IllegalStateException("Uri is null.".toString());
            }
            int sourceUriType = imageRequest.getSourceUriType();
            if (sourceUriType == 0) {
                return getNetworkFetchSequence();
            }
            switch (sourceUriType) {
                case 2:
                    return imageRequest.getLoadThumbnailOnlyForAndroidSdkAboveQ() ? getLocalThumbnailBitmapSdk29FetchSequence() : getLocalVideoFileFetchSequence();
                case 3:
                    return imageRequest.getLoadThumbnailOnlyForAndroidSdkAboveQ() ? getLocalThumbnailBitmapSdk29FetchSequence() : getLocalImageFileFetchSequence();
                case 4:
                    return imageRequest.getLoadThumbnailOnlyForAndroidSdkAboveQ() ? getLocalThumbnailBitmapSdk29FetchSequence() : MediaUtils.isVideo(this.contentResolver.getType(sourceUri)) ? getLocalVideoFileFetchSequence() : getLocalContentUriFetchSequence();
                case 5:
                    return getLocalAssetFetchSequence();
                case 6:
                    return getLocalResourceFetchSequence();
                case 7:
                    return getDataFetchSequence();
                case 8:
                    return getQualifiedResourceFetchSequence();
                default:
                    Set<CustomProducerSequenceFactory> set = this.customProducerSequenceFactories;
                    if (set != null) {
                        Iterator<CustomProducerSequenceFactory> it = set.iterator();
                        while (it.hasNext()) {
                            Producer<CloseableReference<CloseableImage>> customDecodedImageSequence = it.next().getCustomDecodedImageSequence(imageRequest, this, this.producerFactory, this.threadHandoffProducerQueue, this.isEncodedMemoryCacheProbingEnabled, this.isDiskCacheProbingEnabled);
                            if (customDecodedImageSequence != null) {
                                return customDecodedImageSequence;
                            }
                        }
                    }
                    throw new IllegalArgumentException("Unsupported uri scheme! Uri is: " + INSTANCE.getShortenedUriString(sourceUri));
            }
        }
        FrescoSystrace.beginSection("ProducerSequenceFactory#getBasicDecodedImageSequence");
        try {
            Uri sourceUri2 = imageRequest.getSourceUri();
            Intrinsics.checkNotNullExpressionValue(sourceUri2, "getSourceUri(...)");
            if (sourceUri2 == null) {
                throw new IllegalStateException("Uri is null.".toString());
            }
            int sourceUriType2 = imageRequest.getSourceUriType();
            if (sourceUriType2 != 0) {
                switch (sourceUriType2) {
                    case 2:
                        if (!imageRequest.getLoadThumbnailOnlyForAndroidSdkAboveQ()) {
                            networkFetchSequence = getLocalVideoFileFetchSequence();
                            break;
                        } else {
                            return getLocalThumbnailBitmapSdk29FetchSequence();
                        }
                    case 3:
                        if (!imageRequest.getLoadThumbnailOnlyForAndroidSdkAboveQ()) {
                            networkFetchSequence = getLocalImageFileFetchSequence();
                            break;
                        } else {
                            return getLocalThumbnailBitmapSdk29FetchSequence();
                        }
                    case 4:
                        if (!imageRequest.getLoadThumbnailOnlyForAndroidSdkAboveQ()) {
                            if (!MediaUtils.isVideo(this.contentResolver.getType(sourceUri2))) {
                                networkFetchSequence = getLocalContentUriFetchSequence();
                                break;
                            } else {
                                return getLocalVideoFileFetchSequence();
                            }
                        } else {
                            return getLocalThumbnailBitmapSdk29FetchSequence();
                        }
                    case 5:
                        networkFetchSequence = getLocalAssetFetchSequence();
                        break;
                    case 6:
                        networkFetchSequence = getLocalResourceFetchSequence();
                        break;
                    case 7:
                        networkFetchSequence = getDataFetchSequence();
                        break;
                    case 8:
                        networkFetchSequence = getQualifiedResourceFetchSequence();
                        break;
                    default:
                        Set<CustomProducerSequenceFactory> set2 = this.customProducerSequenceFactories;
                        if (set2 != null) {
                            Iterator<CustomProducerSequenceFactory> it2 = set2.iterator();
                            while (it2.hasNext()) {
                                Producer<CloseableReference<CloseableImage>> customDecodedImageSequence2 = it2.next().getCustomDecodedImageSequence(imageRequest, this, this.producerFactory, this.threadHandoffProducerQueue, this.isEncodedMemoryCacheProbingEnabled, this.isDiskCacheProbingEnabled);
                                if (customDecodedImageSequence2 != null) {
                                    return customDecodedImageSequence2;
                                }
                            }
                        }
                        throw new IllegalArgumentException("Unsupported uri scheme! Uri is: " + INSTANCE.getShortenedUriString(sourceUri2));
                }
            } else {
                networkFetchSequence = getNetworkFetchSequence();
            }
            return networkFetchSequence;
        } finally {
            FrescoSystrace.endSection();
        }
    }

    public final Producer<CloseableReference<CloseableImage>> getNetworkFetchSequence() {
        return (Producer) this.networkFetchSequence.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Producer networkFetchSequence_delegate$lambda$11(ProducerSequenceFactory this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        FrescoSystrace frescoSystrace = FrescoSystrace.INSTANCE;
        if (!FrescoSystrace.isTracing()) {
            return this$0.newBitmapCacheGetToDecodeSequence(this$0.getCommonNetworkFetchToEncodedMemorySequence());
        }
        FrescoSystrace.beginSection("ProducerSequenceFactory#getNetworkFetchSequence:init");
        try {
            return this$0.newBitmapCacheGetToDecodeSequence(this$0.getCommonNetworkFetchToEncodedMemorySequence());
        } finally {
            FrescoSystrace.endSection();
        }
    }

    public final Producer<EncodedImage> getBackgroundNetworkFetchToEncodedMemorySequence() {
        Object value = this.backgroundNetworkFetchToEncodedMemorySequence.getValue();
        Intrinsics.checkNotNullExpressionValue(value, "getValue(...)");
        return (Producer) value;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Producer backgroundNetworkFetchToEncodedMemorySequence_delegate$lambda$13(ProducerSequenceFactory this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        FrescoSystrace frescoSystrace = FrescoSystrace.INSTANCE;
        if (!FrescoSystrace.isTracing()) {
            return this$0.producerFactory.newBackgroundThreadHandoffProducer(this$0.getCommonNetworkFetchToEncodedMemorySequence(), this$0.threadHandoffProducerQueue);
        }
        FrescoSystrace.beginSection("ProducerSequenceFactory#getBackgroundNetworkFetchToEncodedMemorySequence:init");
        try {
            return this$0.producerFactory.newBackgroundThreadHandoffProducer(this$0.getCommonNetworkFetchToEncodedMemorySequence(), this$0.threadHandoffProducerQueue);
        } finally {
            FrescoSystrace.endSection();
        }
    }

    public final Producer<Void> getNetworkFetchToEncodedMemoryPrefetchSequence() {
        Object value = this.networkFetchToEncodedMemoryPrefetchSequence.getValue();
        Intrinsics.checkNotNullExpressionValue(value, "getValue(...)");
        return (Producer) value;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final SwallowResultProducer networkFetchToEncodedMemoryPrefetchSequence_delegate$lambda$15(ProducerSequenceFactory this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        FrescoSystrace frescoSystrace = FrescoSystrace.INSTANCE;
        if (!FrescoSystrace.isTracing()) {
            return this$0.producerFactory.newSwallowResultProducer(this$0.getBackgroundNetworkFetchToEncodedMemorySequence());
        }
        FrescoSystrace.beginSection("ProducerSequenceFactory#getNetworkFetchToEncodedMemoryPrefetchSequence");
        try {
            return this$0.producerFactory.newSwallowResultProducer(this$0.getBackgroundNetworkFetchToEncodedMemorySequence());
        } finally {
            FrescoSystrace.endSection();
        }
    }

    public final Producer<EncodedImage> getCommonNetworkFetchToEncodedMemorySequence() {
        return (Producer) this.commonNetworkFetchToEncodedMemorySequence.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Producer commonNetworkFetchToEncodedMemorySequence_delegate$lambda$17(ProducerSequenceFactory this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        FrescoSystrace frescoSystrace = FrescoSystrace.INSTANCE;
        if (!FrescoSystrace.isTracing()) {
            return this$0.newCommonNetworkFetchToEncodedMemorySequence(this$0.networkFetcher);
        }
        FrescoSystrace.beginSection("ProducerSequenceFactory#getCommonNetworkFetchToEncodedMemorySequence");
        try {
            return this$0.newCommonNetworkFetchToEncodedMemorySequence(this$0.networkFetcher);
        } finally {
            FrescoSystrace.endSection();
        }
    }

    public final synchronized Producer<EncodedImage> newCommonNetworkFetchToEncodedMemorySequence(NetworkFetcher<?> networkFetcher) {
        Intrinsics.checkNotNullParameter(networkFetcher, "networkFetcher");
        FrescoSystrace frescoSystrace = FrescoSystrace.INSTANCE;
        boolean z = true;
        if (!FrescoSystrace.isTracing()) {
            Producer<EncodedImage> newNetworkFetchProducer = this.producerFactory.newNetworkFetchProducer(networkFetcher);
            Intrinsics.checkNotNullExpressionValue(newNetworkFetchProducer, "newNetworkFetchProducer(...)");
            AddImageTransformMetaDataProducer newAddImageTransformMetaDataProducer = ProducerFactory.newAddImageTransformMetaDataProducer(newEncodedCacheMultiplexToTranscodeSequence(newNetworkFetchProducer));
            Intrinsics.checkNotNullExpressionValue(newAddImageTransformMetaDataProducer, "newAddImageTransformMetaDataProducer(...)");
            AddImageTransformMetaDataProducer addImageTransformMetaDataProducer = newAddImageTransformMetaDataProducer;
            ProducerFactory producerFactory = this.producerFactory;
            if (!this.resizeAndRotateEnabledForNetwork || this.downsampleMode == DownsampleMode.NEVER) {
                z = false;
            }
            return producerFactory.newResizeAndRotateProducer(addImageTransformMetaDataProducer, z, this.imageTranscoderFactory);
        }
        FrescoSystrace.beginSection("ProducerSequenceFactory#createCommonNetworkFetchToEncodedMemorySequence");
        try {
            Producer<EncodedImage> newNetworkFetchProducer2 = this.producerFactory.newNetworkFetchProducer(networkFetcher);
            Intrinsics.checkNotNullExpressionValue(newNetworkFetchProducer2, "newNetworkFetchProducer(...)");
            AddImageTransformMetaDataProducer newAddImageTransformMetaDataProducer2 = ProducerFactory.newAddImageTransformMetaDataProducer(newEncodedCacheMultiplexToTranscodeSequence(newNetworkFetchProducer2));
            Intrinsics.checkNotNullExpressionValue(newAddImageTransformMetaDataProducer2, "newAddImageTransformMetaDataProducer(...)");
            AddImageTransformMetaDataProducer addImageTransformMetaDataProducer2 = newAddImageTransformMetaDataProducer2;
            ProducerFactory producerFactory2 = this.producerFactory;
            if (!this.resizeAndRotateEnabledForNetwork || this.downsampleMode == DownsampleMode.NEVER) {
                z = false;
            }
            return producerFactory2.newResizeAndRotateProducer(addImageTransformMetaDataProducer2, z, this.imageTranscoderFactory);
        } finally {
            FrescoSystrace.endSection();
        }
    }

    public final Producer<Void> getLocalFileFetchToEncodedMemoryPrefetchSequence() {
        Object value = this.localFileFetchToEncodedMemoryPrefetchSequence.getValue();
        Intrinsics.checkNotNullExpressionValue(value, "getValue(...)");
        return (Producer) value;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final SwallowResultProducer localFileFetchToEncodedMemoryPrefetchSequence_delegate$lambda$20(ProducerSequenceFactory this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        FrescoSystrace frescoSystrace = FrescoSystrace.INSTANCE;
        if (!FrescoSystrace.isTracing()) {
            return this$0.producerFactory.newSwallowResultProducer(this$0.getBackgroundLocalFileFetchToEncodeMemorySequence());
        }
        FrescoSystrace.beginSection("ProducerSequenceFactory#getLocalFileFetchToEncodedMemoryPrefetchSequence:init");
        try {
            return this$0.producerFactory.newSwallowResultProducer(this$0.getBackgroundLocalFileFetchToEncodeMemorySequence());
        } finally {
            FrescoSystrace.endSection();
        }
    }

    public final Producer<EncodedImage> getBackgroundLocalFileFetchToEncodeMemorySequence() {
        Object value = this.backgroundLocalFileFetchToEncodeMemorySequence.getValue();
        Intrinsics.checkNotNullExpressionValue(value, "getValue(...)");
        return (Producer) value;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Producer backgroundLocalFileFetchToEncodeMemorySequence_delegate$lambda$22(ProducerSequenceFactory this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        FrescoSystrace frescoSystrace = FrescoSystrace.INSTANCE;
        if (!FrescoSystrace.isTracing()) {
            LocalFileFetchProducer newLocalFileFetchProducer = this$0.producerFactory.newLocalFileFetchProducer();
            Intrinsics.checkNotNullExpressionValue(newLocalFileFetchProducer, "newLocalFileFetchProducer(...)");
            return this$0.producerFactory.newBackgroundThreadHandoffProducer(this$0.newEncodedCacheMultiplexToTranscodeSequence(newLocalFileFetchProducer), this$0.threadHandoffProducerQueue);
        }
        FrescoSystrace.beginSection("ProducerSequenceFactory#getBackgroundLocalFileFetchToEncodeMemorySequence");
        try {
            LocalFileFetchProducer newLocalFileFetchProducer2 = this$0.producerFactory.newLocalFileFetchProducer();
            Intrinsics.checkNotNullExpressionValue(newLocalFileFetchProducer2, "newLocalFileFetchProducer(...)");
            return this$0.producerFactory.newBackgroundThreadHandoffProducer(this$0.newEncodedCacheMultiplexToTranscodeSequence(newLocalFileFetchProducer2), this$0.threadHandoffProducerQueue);
        } finally {
            FrescoSystrace.endSection();
        }
    }

    public final Producer<EncodedImage> getBackgroundLocalContentUriFetchToEncodeMemorySequence() {
        Object value = this.backgroundLocalContentUriFetchToEncodeMemorySequence.getValue();
        Intrinsics.checkNotNullExpressionValue(value, "getValue(...)");
        return (Producer) value;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Producer backgroundLocalContentUriFetchToEncodeMemorySequence_delegate$lambda$24(ProducerSequenceFactory this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        FrescoSystrace frescoSystrace = FrescoSystrace.INSTANCE;
        if (!FrescoSystrace.isTracing()) {
            LocalContentUriFetchProducer newLocalContentUriFetchProducer = this$0.producerFactory.newLocalContentUriFetchProducer();
            Intrinsics.checkNotNullExpressionValue(newLocalContentUriFetchProducer, "newLocalContentUriFetchProducer(...)");
            return this$0.producerFactory.newBackgroundThreadHandoffProducer(this$0.newEncodedCacheMultiplexToTranscodeSequence(newLocalContentUriFetchProducer), this$0.threadHandoffProducerQueue);
        }
        FrescoSystrace.beginSection("ProducerSequenceFactory#getBackgroundLocalContentUriFetchToEncodeMemorySequence:init");
        try {
            LocalContentUriFetchProducer newLocalContentUriFetchProducer2 = this$0.producerFactory.newLocalContentUriFetchProducer();
            Intrinsics.checkNotNullExpressionValue(newLocalContentUriFetchProducer2, "newLocalContentUriFetchProducer(...)");
            return this$0.producerFactory.newBackgroundThreadHandoffProducer(this$0.newEncodedCacheMultiplexToTranscodeSequence(newLocalContentUriFetchProducer2), this$0.threadHandoffProducerQueue);
        } finally {
            FrescoSystrace.endSection();
        }
    }

    public final Producer<CloseableReference<CloseableImage>> getLocalImageFileFetchSequence() {
        return (Producer) this.localImageFileFetchSequence.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Producer localImageFileFetchSequence_delegate$lambda$25(ProducerSequenceFactory this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        LocalFileFetchProducer newLocalFileFetchProducer = this$0.producerFactory.newLocalFileFetchProducer();
        Intrinsics.checkNotNullExpressionValue(newLocalFileFetchProducer, "newLocalFileFetchProducer(...)");
        return this$0.newBitmapCacheGetToLocalTransformSequence(newLocalFileFetchProducer);
    }

    public final Producer<CloseableReference<CloseableImage>> getLocalVideoFileFetchSequence() {
        return (Producer) this.localVideoFileFetchSequence.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Producer localVideoFileFetchSequence_delegate$lambda$26(ProducerSequenceFactory this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        LocalVideoThumbnailProducer newLocalVideoThumbnailProducer = this$0.producerFactory.newLocalVideoThumbnailProducer();
        Intrinsics.checkNotNullExpressionValue(newLocalVideoThumbnailProducer, "newLocalVideoThumbnailProducer(...)");
        return this$0.newBitmapCacheGetToBitmapCacheSequence(newLocalVideoThumbnailProducer);
    }

    public final Producer<CloseableReference<CloseableImage>> getLocalContentUriFetchSequence() {
        return (Producer) this.localContentUriFetchSequence.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Producer localContentUriFetchSequence_delegate$lambda$27(ProducerSequenceFactory this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        LocalContentUriFetchProducer newLocalContentUriFetchProducer = this$0.producerFactory.newLocalContentUriFetchProducer();
        Intrinsics.checkNotNullExpressionValue(newLocalContentUriFetchProducer, "newLocalContentUriFetchProducer(...)");
        return this$0.newBitmapCacheGetToLocalTransformSequence(newLocalContentUriFetchProducer, new ThumbnailProducer[]{this$0.producerFactory.newLocalContentUriThumbnailFetchProducer(), this$0.producerFactory.newLocalExifThumbnailProducer()});
    }

    public final Producer<CloseableReference<CloseableImage>> getLocalThumbnailBitmapSdk29FetchSequence() {
        return (Producer) this.localThumbnailBitmapSdk29FetchSequence.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Producer localThumbnailBitmapSdk29FetchSequence_delegate$lambda$28(ProducerSequenceFactory this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        if (Build.VERSION.SDK_INT >= 29) {
            LocalThumbnailBitmapSdk29Producer newLocalThumbnailBitmapSdk29Producer = this$0.producerFactory.newLocalThumbnailBitmapSdk29Producer();
            Intrinsics.checkNotNullExpressionValue(newLocalThumbnailBitmapSdk29Producer, "newLocalThumbnailBitmapSdk29Producer(...)");
            return this$0.newBitmapCacheGetToBitmapCacheSequence(newLocalThumbnailBitmapSdk29Producer);
        }
        throw new Throwable("Unreachable exception. Just to make linter happy for the lazy block.");
    }

    public final Producer<CloseableReference<CloseableImage>> getQualifiedResourceFetchSequence() {
        return (Producer) this.qualifiedResourceFetchSequence.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Producer qualifiedResourceFetchSequence_delegate$lambda$29(ProducerSequenceFactory this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        QualifiedResourceFetchProducer newQualifiedResourceFetchProducer = this$0.producerFactory.newQualifiedResourceFetchProducer();
        Intrinsics.checkNotNullExpressionValue(newQualifiedResourceFetchProducer, "newQualifiedResourceFetchProducer(...)");
        return this$0.newBitmapCacheGetToLocalTransformSequence(newQualifiedResourceFetchProducer);
    }

    public final Producer<CloseableReference<CloseableImage>> getLocalResourceFetchSequence() {
        return (Producer) this.localResourceFetchSequence.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Producer localResourceFetchSequence_delegate$lambda$30(ProducerSequenceFactory this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        LocalResourceFetchProducer newLocalResourceFetchProducer = this$0.producerFactory.newLocalResourceFetchProducer();
        Intrinsics.checkNotNullExpressionValue(newLocalResourceFetchProducer, "newLocalResourceFetchProducer(...)");
        return this$0.newBitmapCacheGetToLocalTransformSequence(newLocalResourceFetchProducer);
    }

    public final Producer<CloseableReference<CloseableImage>> getLocalAssetFetchSequence() {
        return (Producer) this.localAssetFetchSequence.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Producer localAssetFetchSequence_delegate$lambda$31(ProducerSequenceFactory this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        LocalAssetFetchProducer newLocalAssetFetchProducer = this$0.producerFactory.newLocalAssetFetchProducer();
        Intrinsics.checkNotNullExpressionValue(newLocalAssetFetchProducer, "newLocalAssetFetchProducer(...)");
        return this$0.newBitmapCacheGetToLocalTransformSequence(newLocalAssetFetchProducer);
    }

    public final Producer<CloseableReference<CloseableImage>> getDataFetchSequence() {
        return (Producer) this.dataFetchSequence.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Producer dataFetchSequence_delegate$lambda$32(ProducerSequenceFactory this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        DataFetchProducer newDataFetchProducer = this$0.producerFactory.newDataFetchProducer();
        Intrinsics.checkNotNullExpressionValue(newDataFetchProducer, "newDataFetchProducer(...)");
        return this$0.newBitmapCacheGetToDecodeSequence(this$0.producerFactory.newResizeAndRotateProducer(ProducerFactory.newAddImageTransformMetaDataProducer(newDataFetchProducer), true, this$0.imageTranscoderFactory));
    }

    private final Producer<CloseableReference<CloseableImage>> newBitmapCacheGetToLocalTransformSequence(Producer<EncodedImage> inputProducer) {
        return newBitmapCacheGetToLocalTransformSequence(inputProducer, new ThumbnailProducer[]{this.producerFactory.newLocalExifThumbnailProducer()});
    }

    private final Producer<CloseableReference<CloseableImage>> newBitmapCacheGetToLocalTransformSequence(Producer<EncodedImage> inputProducer, ThumbnailProducer<EncodedImage>[] thumbnailProducers) {
        return newBitmapCacheGetToDecodeSequence(newLocalTransformationsSequence(newEncodedCacheMultiplexToTranscodeSequence(inputProducer), thumbnailProducers));
    }

    public final Producer<CloseableReference<CloseableImage>> newBitmapCacheGetToDecodeSequence(Producer<EncodedImage> inputProducer) {
        Intrinsics.checkNotNullParameter(inputProducer, "inputProducer");
        FrescoSystrace frescoSystrace = FrescoSystrace.INSTANCE;
        if (!FrescoSystrace.isTracing()) {
            DecodeProducer newDecodeProducer = this.producerFactory.newDecodeProducer(inputProducer);
            Intrinsics.checkNotNullExpressionValue(newDecodeProducer, "newDecodeProducer(...)");
            return newBitmapCacheGetToBitmapCacheSequence(newDecodeProducer);
        }
        FrescoSystrace.beginSection("ProducerSequenceFactory#newBitmapCacheGetToDecodeSequence");
        try {
            DecodeProducer newDecodeProducer2 = this.producerFactory.newDecodeProducer(inputProducer);
            Intrinsics.checkNotNullExpressionValue(newDecodeProducer2, "newDecodeProducer(...)");
            return newBitmapCacheGetToBitmapCacheSequence(newDecodeProducer2);
        } finally {
            FrescoSystrace.endSection();
        }
    }

    private final Producer<EncodedImage> newEncodedCacheMultiplexToTranscodeSequence(Producer<EncodedImage> inputProducer) {
        if (this.diskCacheEnabled) {
            inputProducer = newDiskCacheSequence(inputProducer);
        }
        Producer<EncodedImage> newEncodedMemoryCacheProducer = this.producerFactory.newEncodedMemoryCacheProducer(inputProducer);
        Intrinsics.checkNotNullExpressionValue(newEncodedMemoryCacheProducer, "newEncodedMemoryCacheProducer(...)");
        if (this.isDiskCacheProbingEnabled) {
            EncodedProbeProducer newEncodedProbeProducer = this.producerFactory.newEncodedProbeProducer(newEncodedMemoryCacheProducer);
            Intrinsics.checkNotNullExpressionValue(newEncodedProbeProducer, "newEncodedProbeProducer(...)");
            EncodedCacheKeyMultiplexProducer newEncodedCacheKeyMultiplexProducer = this.producerFactory.newEncodedCacheKeyMultiplexProducer(newEncodedProbeProducer);
            Intrinsics.checkNotNullExpressionValue(newEncodedCacheKeyMultiplexProducer, "newEncodedCacheKeyMultiplexProducer(...)");
            return newEncodedCacheKeyMultiplexProducer;
        }
        EncodedCacheKeyMultiplexProducer newEncodedCacheKeyMultiplexProducer2 = this.producerFactory.newEncodedCacheKeyMultiplexProducer(newEncodedMemoryCacheProducer);
        Intrinsics.checkNotNullExpressionValue(newEncodedCacheKeyMultiplexProducer2, "newEncodedCacheKeyMultiplexProducer(...)");
        return newEncodedCacheKeyMultiplexProducer2;
    }

    private final Producer<EncodedImage> newDiskCacheSequence(Producer<EncodedImage> inputProducer) {
        DiskCacheWriteProducer newDiskCacheWriteProducer;
        DiskCacheWriteProducer newDiskCacheWriteProducer2;
        FrescoSystrace frescoSystrace = FrescoSystrace.INSTANCE;
        if (!FrescoSystrace.isTracing()) {
            if (this.partialImageCachingEnabled) {
                PartialDiskCacheProducer newPartialDiskCacheProducer = this.producerFactory.newPartialDiskCacheProducer(inputProducer);
                Intrinsics.checkNotNullExpressionValue(newPartialDiskCacheProducer, "newPartialDiskCacheProducer(...)");
                newDiskCacheWriteProducer2 = this.producerFactory.newDiskCacheWriteProducer(newPartialDiskCacheProducer);
            } else {
                newDiskCacheWriteProducer2 = this.producerFactory.newDiskCacheWriteProducer(inputProducer);
            }
            Intrinsics.checkNotNull(newDiskCacheWriteProducer2);
            DiskCacheReadProducer newDiskCacheReadProducer = this.producerFactory.newDiskCacheReadProducer(newDiskCacheWriteProducer2);
            Intrinsics.checkNotNullExpressionValue(newDiskCacheReadProducer, "newDiskCacheReadProducer(...)");
            return newDiskCacheReadProducer;
        }
        FrescoSystrace.beginSection("ProducerSequenceFactory#newDiskCacheSequence");
        try {
            if (this.partialImageCachingEnabled) {
                PartialDiskCacheProducer newPartialDiskCacheProducer2 = this.producerFactory.newPartialDiskCacheProducer(inputProducer);
                Intrinsics.checkNotNullExpressionValue(newPartialDiskCacheProducer2, "newPartialDiskCacheProducer(...)");
                newDiskCacheWriteProducer = this.producerFactory.newDiskCacheWriteProducer(newPartialDiskCacheProducer2);
            } else {
                newDiskCacheWriteProducer = this.producerFactory.newDiskCacheWriteProducer(inputProducer);
            }
            Intrinsics.checkNotNull(newDiskCacheWriteProducer);
            DiskCacheReadProducer newDiskCacheReadProducer2 = this.producerFactory.newDiskCacheReadProducer(newDiskCacheWriteProducer);
            Intrinsics.checkNotNullExpressionValue(newDiskCacheReadProducer2, "newDiskCacheReadProducer(...)");
            return newDiskCacheReadProducer2;
        } finally {
            FrescoSystrace.endSection();
        }
    }

    private final Producer<CloseableReference<CloseableImage>> newBitmapCacheGetToBitmapCacheSequence(Producer<CloseableReference<CloseableImage>> inputProducer) {
        BitmapMemoryCacheProducer newBitmapMemoryCacheProducer = this.producerFactory.newBitmapMemoryCacheProducer(inputProducer);
        Intrinsics.checkNotNullExpressionValue(newBitmapMemoryCacheProducer, "newBitmapMemoryCacheProducer(...)");
        BitmapMemoryCacheKeyMultiplexProducer newBitmapMemoryCacheKeyMultiplexProducer = this.producerFactory.newBitmapMemoryCacheKeyMultiplexProducer(newBitmapMemoryCacheProducer);
        Intrinsics.checkNotNullExpressionValue(newBitmapMemoryCacheKeyMultiplexProducer, "newBitmapMemoryCacheKeyMultiplexProducer(...)");
        Producer<CloseableReference<CloseableImage>> newBackgroundThreadHandoffProducer = this.producerFactory.newBackgroundThreadHandoffProducer(newBitmapMemoryCacheKeyMultiplexProducer, this.threadHandoffProducerQueue);
        Intrinsics.checkNotNullExpressionValue(newBackgroundThreadHandoffProducer, "newBackgroundThreadHandoffProducer(...)");
        if (this.isEncodedMemoryCacheProbingEnabled || this.isDiskCacheProbingEnabled) {
            BitmapMemoryCacheGetProducer newBitmapMemoryCacheGetProducer = this.producerFactory.newBitmapMemoryCacheGetProducer(newBackgroundThreadHandoffProducer);
            Intrinsics.checkNotNullExpressionValue(newBitmapMemoryCacheGetProducer, "newBitmapMemoryCacheGetProducer(...)");
            BitmapProbeProducer newBitmapProbeProducer = this.producerFactory.newBitmapProbeProducer(newBitmapMemoryCacheGetProducer);
            Intrinsics.checkNotNullExpressionValue(newBitmapProbeProducer, "newBitmapProbeProducer(...)");
            return newBitmapProbeProducer;
        }
        BitmapMemoryCacheGetProducer newBitmapMemoryCacheGetProducer2 = this.producerFactory.newBitmapMemoryCacheGetProducer(newBackgroundThreadHandoffProducer);
        Intrinsics.checkNotNullExpressionValue(newBitmapMemoryCacheGetProducer2, "newBitmapMemoryCacheGetProducer(...)");
        return newBitmapMemoryCacheGetProducer2;
    }

    private final Producer<EncodedImage> newLocalTransformationsSequence(Producer<EncodedImage> inputProducer, ThumbnailProducer<EncodedImage>[] thumbnailProducers) {
        AddImageTransformMetaDataProducer newAddImageTransformMetaDataProducer = ProducerFactory.newAddImageTransformMetaDataProducer(inputProducer);
        Intrinsics.checkNotNullExpressionValue(newAddImageTransformMetaDataProducer, "newAddImageTransformMetaDataProducer(...)");
        ThrottlingProducer newThrottlingProducer = this.producerFactory.newThrottlingProducer(this.producerFactory.newResizeAndRotateProducer(newAddImageTransformMetaDataProducer, true, this.imageTranscoderFactory));
        Intrinsics.checkNotNullExpressionValue(newThrottlingProducer, "newThrottlingProducer(...)");
        BranchOnSeparateImagesProducer newBranchOnSeparateImagesProducer = ProducerFactory.newBranchOnSeparateImagesProducer(newLocalThumbnailProducer(thumbnailProducers), newThrottlingProducer);
        Intrinsics.checkNotNullExpressionValue(newBranchOnSeparateImagesProducer, "newBranchOnSeparateImagesProducer(...)");
        return newBranchOnSeparateImagesProducer;
    }

    private final Producer<EncodedImage> newLocalThumbnailProducer(ThumbnailProducer<EncodedImage>[] thumbnailProducers) {
        ThumbnailBranchProducer newThumbnailBranchProducer = this.producerFactory.newThumbnailBranchProducer(thumbnailProducers);
        Intrinsics.checkNotNullExpressionValue(newThumbnailBranchProducer, "newThumbnailBranchProducer(...)");
        ResizeAndRotateProducer newResizeAndRotateProducer = this.producerFactory.newResizeAndRotateProducer(newThumbnailBranchProducer, true, this.imageTranscoderFactory);
        Intrinsics.checkNotNullExpressionValue(newResizeAndRotateProducer, "newResizeAndRotateProducer(...)");
        return newResizeAndRotateProducer;
    }

    private final synchronized Producer<CloseableReference<CloseableImage>> getPostprocessorSequence(Producer<CloseableReference<CloseableImage>> inputProducer) {
        PostprocessedBitmapMemoryCacheProducer postprocessedBitmapMemoryCacheProducer;
        postprocessedBitmapMemoryCacheProducer = this.postprocessorSequences.get(inputProducer);
        if (postprocessedBitmapMemoryCacheProducer == null) {
            PostprocessorProducer newPostprocessorProducer = this.producerFactory.newPostprocessorProducer(inputProducer);
            Intrinsics.checkNotNullExpressionValue(newPostprocessorProducer, "newPostprocessorProducer(...)");
            postprocessedBitmapMemoryCacheProducer = this.producerFactory.newPostprocessorBitmapMemoryCacheProducer(newPostprocessorProducer);
            this.postprocessorSequences.put(inputProducer, postprocessedBitmapMemoryCacheProducer);
        }
        return postprocessedBitmapMemoryCacheProducer;
    }

    private final synchronized Producer<Void> getDecodedImagePrefetchSequence(Producer<CloseableReference<CloseableImage>> inputProducer) {
        SwallowResultProducer swallowResultProducer;
        swallowResultProducer = this.closeableImagePrefetchSequences.get(inputProducer);
        if (swallowResultProducer == null) {
            swallowResultProducer = this.producerFactory.newSwallowResultProducer(inputProducer);
            this.closeableImagePrefetchSequences.put(inputProducer, swallowResultProducer);
        }
        return swallowResultProducer;
    }

    private final synchronized Producer<CloseableReference<CloseableImage>> getBitmapPrepareSequence(Producer<CloseableReference<CloseableImage>> inputProducer) {
        BitmapPrepareProducer bitmapPrepareProducer;
        bitmapPrepareProducer = this.bitmapPrepareSequences.get(inputProducer);
        if (bitmapPrepareProducer == null) {
            bitmapPrepareProducer = this.producerFactory.newBitmapPrepareProducer(inputProducer);
            this.bitmapPrepareSequences.put(inputProducer, bitmapPrepareProducer);
        }
        return bitmapPrepareProducer;
    }

    private final synchronized Producer<CloseableReference<CloseableImage>> getDelaySequence(Producer<CloseableReference<CloseableImage>> inputProducer) {
        DelayProducer newDelayProducer;
        newDelayProducer = this.producerFactory.newDelayProducer(inputProducer);
        Intrinsics.checkNotNullExpressionValue(newDelayProducer, "newDelayProducer(...)");
        return newDelayProducer;
    }

    /* compiled from: ProducerSequenceFactory.kt */
    @Metadata(d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0002J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0002¨\u0006\f"}, d2 = {"Lcom/facebook/imagepipeline/core/ProducerSequenceFactory$Companion;", "", "<init>", "()V", "validateEncodedImageRequest", "", "imageRequest", "Lcom/facebook/imagepipeline/request/ImageRequest;", "getShortenedUriString", "", "uri", "Landroid/net/Uri;", "imagepipeline_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
    /* loaded from: classes.dex */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final void validateEncodedImageRequest(ImageRequest imageRequest) {
            Preconditions.checkArgument(Boolean.valueOf(imageRequest.getLowestPermittedRequestLevel().getValue() <= ImageRequest.RequestLevel.ENCODED_MEMORY_CACHE.getValue()));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final String getShortenedUriString(Uri uri) {
            String uri2 = uri.toString();
            Intrinsics.checkNotNullExpressionValue(uri2, "toString(...)");
            if (uri2.length() <= 30) {
                return uri2;
            }
            String substring = uri2.substring(0, 30);
            Intrinsics.checkNotNullExpressionValue(substring, "substring(...)");
            return substring + "...";
        }
    }
}
