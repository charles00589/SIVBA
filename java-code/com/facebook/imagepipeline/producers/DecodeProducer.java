package com.facebook.imagepipeline.producers;

import android.graphics.Bitmap;
import android.net.Uri;
import androidx.core.app.NotificationCompat;
import androidx.core.os.EnvironmentCompat;
import com.facebook.common.internal.ImmutableMap;
import com.facebook.common.internal.Supplier;
import com.facebook.common.logging.FLog;
import com.facebook.common.memory.ByteArrayPool;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.statfs.StatFsHelper;
import com.facebook.common.util.ExceptionWithNoStacktrace;
import com.facebook.common.util.UriUtil;
import com.facebook.imageformat.DefaultImageFormats;
import com.facebook.imageformat.ImageFormat;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.imagepipeline.core.CloseableReferenceFactory;
import com.facebook.imagepipeline.core.DownsampleMode;
import com.facebook.imagepipeline.decoder.DecodeException;
import com.facebook.imagepipeline.decoder.ImageDecoder;
import com.facebook.imagepipeline.decoder.ProgressiveJpegConfig;
import com.facebook.imagepipeline.decoder.ProgressiveJpegParser;
import com.facebook.imagepipeline.image.CloseableBitmap;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.image.ImmutableQualityInfo;
import com.facebook.imagepipeline.image.QualityInfo;
import com.facebook.imagepipeline.producers.DecodeProducer;
import com.facebook.imagepipeline.producers.JobScheduler;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.systrace.FrescoSystrace;
import com.facebook.imagepipeline.transcoder.DownsampleUtil;
import com.facebook.imageutils.BitmapUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: DecodeProducer.kt */
@Metadata(d1 = {"\u0000l\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u001a\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u0000 =2\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00030\u00020\u0001:\u0004:;<=Bw\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\u0006\u0010\f\u001a\u00020\r\u0012\u0006\u0010\u000e\u001a\u00020\u000f\u0012\u0006\u0010\u0010\u001a\u00020\u000f\u0012\u000e\u0010\u0011\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00120\u0001\u0012\u0006\u0010\u0013\u001a\u00020\u0014\u0012\u0006\u0010\u0015\u001a\u00020\u0016\u0012\b\u0010\u0017\u001a\u0004\u0018\u00010\u0018\u0012\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u000f0\u001a¢\u0006\u0004\b\u001b\u0010\u001cJ$\u00104\u001a\u0002052\u0012\u00106\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00030\u0002072\u0006\u00108\u001a\u000209H\u0016R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001eR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010 R\u0011\u0010\b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b!\u0010\"R\u0011\u0010\n\u001a\u00020\u000b¢\u0006\b\n\u0000\u001a\u0004\b#\u0010$R\u0011\u0010\f\u001a\u00020\r¢\u0006\b\n\u0000\u001a\u0004\b%\u0010&R\u0011\u0010\u000e\u001a\u00020\u000f¢\u0006\b\n\u0000\u001a\u0004\b'\u0010(R\u0011\u0010\u0010\u001a\u00020\u000f¢\u0006\b\n\u0000\u001a\u0004\b)\u0010(R\u0019\u0010\u0011\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00120\u0001¢\u0006\b\n\u0000\u001a\u0004\b*\u0010+R\u0011\u0010\u0013\u001a\u00020\u0014¢\u0006\b\n\u0000\u001a\u0004\b,\u0010-R\u0011\u0010\u0015\u001a\u00020\u0016¢\u0006\b\n\u0000\u001a\u0004\b.\u0010/R\u0013\u0010\u0017\u001a\u0004\u0018\u00010\u0018¢\u0006\b\n\u0000\u001a\u0004\b0\u00101R\u0017\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u000f0\u001a¢\u0006\b\n\u0000\u001a\u0004\b2\u00103¨\u0006>"}, d2 = {"Lcom/facebook/imagepipeline/producers/DecodeProducer;", "Lcom/facebook/imagepipeline/producers/Producer;", "Lcom/facebook/common/references/CloseableReference;", "Lcom/facebook/imagepipeline/image/CloseableImage;", "byteArrayPool", "Lcom/facebook/common/memory/ByteArrayPool;", "executor", "Ljava/util/concurrent/Executor;", "imageDecoder", "Lcom/facebook/imagepipeline/decoder/ImageDecoder;", "progressiveJpegConfig", "Lcom/facebook/imagepipeline/decoder/ProgressiveJpegConfig;", "downsampleMode", "Lcom/facebook/imagepipeline/core/DownsampleMode;", "downsampleEnabledForNetwork", "", "decodeCancellationEnabled", "inputProducer", "Lcom/facebook/imagepipeline/image/EncodedImage;", "maxBitmapDimension", "", "closeableReferenceFactory", "Lcom/facebook/imagepipeline/core/CloseableReferenceFactory;", "reclaimMemoryRunnable", "Ljava/lang/Runnable;", "recoverFromDecoderOOM", "Lcom/facebook/common/internal/Supplier;", "<init>", "(Lcom/facebook/common/memory/ByteArrayPool;Ljava/util/concurrent/Executor;Lcom/facebook/imagepipeline/decoder/ImageDecoder;Lcom/facebook/imagepipeline/decoder/ProgressiveJpegConfig;Lcom/facebook/imagepipeline/core/DownsampleMode;ZZLcom/facebook/imagepipeline/producers/Producer;ILcom/facebook/imagepipeline/core/CloseableReferenceFactory;Ljava/lang/Runnable;Lcom/facebook/common/internal/Supplier;)V", "getByteArrayPool", "()Lcom/facebook/common/memory/ByteArrayPool;", "getExecutor", "()Ljava/util/concurrent/Executor;", "getImageDecoder", "()Lcom/facebook/imagepipeline/decoder/ImageDecoder;", "getProgressiveJpegConfig", "()Lcom/facebook/imagepipeline/decoder/ProgressiveJpegConfig;", "getDownsampleMode", "()Lcom/facebook/imagepipeline/core/DownsampleMode;", "getDownsampleEnabledForNetwork", "()Z", "getDecodeCancellationEnabled", "getInputProducer", "()Lcom/facebook/imagepipeline/producers/Producer;", "getMaxBitmapDimension", "()I", "getCloseableReferenceFactory", "()Lcom/facebook/imagepipeline/core/CloseableReferenceFactory;", "getReclaimMemoryRunnable", "()Ljava/lang/Runnable;", "getRecoverFromDecoderOOM", "()Lcom/facebook/common/internal/Supplier;", "produceResults", "", "consumer", "Lcom/facebook/imagepipeline/producers/Consumer;", "context", "Lcom/facebook/imagepipeline/producers/ProducerContext;", "ProgressiveDecoder", "LocalImagesProgressiveDecoder", "NetworkImagesProgressiveDecoder", "Companion", "imagepipeline_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes.dex */
public final class DecodeProducer implements Producer<CloseableReference<CloseableImage>> {

    /* renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private static final int DECODE_EXCEPTION_MESSAGE_NUM_HEADER_BYTES = 10;
    public static final String ENCODED_IMAGE_SIZE = "encodedImageSize";
    public static final String EXTRA_BITMAP_BYTES = "byteCount";
    public static final String EXTRA_BITMAP_SIZE = "bitmapSize";
    public static final String EXTRA_HAS_GOOD_QUALITY = "hasGoodQuality";
    public static final String EXTRA_IMAGE_FORMAT_NAME = "imageFormat";
    public static final String EXTRA_IS_FINAL = "isFinal";
    private static final int MAX_BITMAP_SIZE = 104857600;
    public static final String NON_FATAL_DECODE_ERROR = "non_fatal_decode_error";
    public static final String PRODUCER_NAME = "DecodeProducer";
    public static final String REQUESTED_IMAGE_SIZE = "requestedImageSize";
    public static final String SAMPLE_SIZE = "sampleSize";
    private final ByteArrayPool byteArrayPool;
    private final CloseableReferenceFactory closeableReferenceFactory;
    private final boolean decodeCancellationEnabled;
    private final boolean downsampleEnabledForNetwork;
    private final DownsampleMode downsampleMode;
    private final Executor executor;
    private final ImageDecoder imageDecoder;
    private final Producer<EncodedImage> inputProducer;
    private final int maxBitmapDimension;
    private final ProgressiveJpegConfig progressiveJpegConfig;
    private final Runnable reclaimMemoryRunnable;
    private final Supplier<Boolean> recoverFromDecoderOOM;

    public DecodeProducer(ByteArrayPool byteArrayPool, Executor executor, ImageDecoder imageDecoder, ProgressiveJpegConfig progressiveJpegConfig, DownsampleMode downsampleMode, boolean z, boolean z2, Producer<EncodedImage> inputProducer, int i, CloseableReferenceFactory closeableReferenceFactory, Runnable runnable, Supplier<Boolean> recoverFromDecoderOOM) {
        Intrinsics.checkNotNullParameter(byteArrayPool, "byteArrayPool");
        Intrinsics.checkNotNullParameter(executor, "executor");
        Intrinsics.checkNotNullParameter(imageDecoder, "imageDecoder");
        Intrinsics.checkNotNullParameter(progressiveJpegConfig, "progressiveJpegConfig");
        Intrinsics.checkNotNullParameter(downsampleMode, "downsampleMode");
        Intrinsics.checkNotNullParameter(inputProducer, "inputProducer");
        Intrinsics.checkNotNullParameter(closeableReferenceFactory, "closeableReferenceFactory");
        Intrinsics.checkNotNullParameter(recoverFromDecoderOOM, "recoverFromDecoderOOM");
        this.byteArrayPool = byteArrayPool;
        this.executor = executor;
        this.imageDecoder = imageDecoder;
        this.progressiveJpegConfig = progressiveJpegConfig;
        this.downsampleMode = downsampleMode;
        this.downsampleEnabledForNetwork = z;
        this.decodeCancellationEnabled = z2;
        this.inputProducer = inputProducer;
        this.maxBitmapDimension = i;
        this.closeableReferenceFactory = closeableReferenceFactory;
        this.reclaimMemoryRunnable = runnable;
        this.recoverFromDecoderOOM = recoverFromDecoderOOM;
    }

    public final ByteArrayPool getByteArrayPool() {
        return this.byteArrayPool;
    }

    public final Executor getExecutor() {
        return this.executor;
    }

    public final ImageDecoder getImageDecoder() {
        return this.imageDecoder;
    }

    public final ProgressiveJpegConfig getProgressiveJpegConfig() {
        return this.progressiveJpegConfig;
    }

    public final DownsampleMode getDownsampleMode() {
        return this.downsampleMode;
    }

    public final boolean getDownsampleEnabledForNetwork() {
        return this.downsampleEnabledForNetwork;
    }

    public final boolean getDecodeCancellationEnabled() {
        return this.decodeCancellationEnabled;
    }

    public final Producer<EncodedImage> getInputProducer() {
        return this.inputProducer;
    }

    public final int getMaxBitmapDimension() {
        return this.maxBitmapDimension;
    }

    public final CloseableReferenceFactory getCloseableReferenceFactory() {
        return this.closeableReferenceFactory;
    }

    public final Runnable getReclaimMemoryRunnable() {
        return this.reclaimMemoryRunnable;
    }

    public final Supplier<Boolean> getRecoverFromDecoderOOM() {
        return this.recoverFromDecoderOOM;
    }

    @Override // com.facebook.imagepipeline.producers.Producer
    public void produceResults(Consumer<CloseableReference<CloseableImage>> consumer, ProducerContext context) {
        Intrinsics.checkNotNullParameter(consumer, "consumer");
        Intrinsics.checkNotNullParameter(context, "context");
        FrescoSystrace frescoSystrace = FrescoSystrace.INSTANCE;
        if (!FrescoSystrace.isTracing()) {
            ImageRequest imageRequest = context.getImageRequest();
            this.inputProducer.produceResults((UriUtil.isNetworkUri(imageRequest.getSourceUri()) || ImageRequestBuilder.isCustomNetworkUri(imageRequest.getSourceUri())) ? new NetworkImagesProgressiveDecoder(this, consumer, context, new ProgressiveJpegParser(this.byteArrayPool), this.progressiveJpegConfig, this.decodeCancellationEnabled, this.maxBitmapDimension) : new LocalImagesProgressiveDecoder(this, consumer, context, this.decodeCancellationEnabled, this.maxBitmapDimension), context);
            return;
        }
        FrescoSystrace.beginSection("DecodeProducer#produceResults");
        try {
            ImageRequest imageRequest2 = context.getImageRequest();
            this.inputProducer.produceResults((UriUtil.isNetworkUri(imageRequest2.getSourceUri()) || ImageRequestBuilder.isCustomNetworkUri(imageRequest2.getSourceUri())) ? new NetworkImagesProgressiveDecoder(this, consumer, context, new ProgressiveJpegParser(this.byteArrayPool), this.progressiveJpegConfig, this.decodeCancellationEnabled, this.maxBitmapDimension) : new LocalImagesProgressiveDecoder(this, consumer, context, this.decodeCancellationEnabled, this.maxBitmapDimension), context);
            Unit unit = Unit.INSTANCE;
        } finally {
            FrescoSystrace.endSection();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: DecodeProducer.kt */
    @Metadata(d1 = {"\u0000z\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\u0003\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010$\n\u0000\n\u0002\u0010\t\n\u0002\b\u0010\b¢\u0004\u0018\u00002\u0016\u0012\u0006\u0012\u0004\u0018\u00010\u0002\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00040\u00030\u0001B3\u0012\u0012\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00040\u00030\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\n\u0012\u0006\u0010\u000b\u001a\u00020\f¢\u0006\u0004\b\r\u0010\u000eJ\u0010\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020\u0002H\u0002J\u001a\u0010 \u001a\u00020\u001e2\b\u0010!\u001a\u0004\u0018\u00010\u00022\u0006\u0010\"\u001a\u00020\fH\u0016J\u0010\u0010#\u001a\u00020\u001e2\u0006\u0010$\u001a\u00020%H\u0014J\u0010\u0010&\u001a\u00020\u001e2\u0006\u0010'\u001a\u00020(H\u0016J\b\u0010)\u001a\u00020\u001eH\u0016J\u001a\u0010*\u001a\u00020\n2\b\u0010+\u001a\u0004\u0018\u00010\u00022\u0006\u0010\"\u001a\u00020\fH\u0014J \u0010,\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020\u00022\u0006\u0010\"\u001a\u00020\f2\u0006\u0010\u0018\u001a\u00020\fH\u0002J\"\u0010-\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u001f\u001a\u00020\u00022\u0006\u0010.\u001a\u00020\f2\u0006\u0010/\u001a\u000200H\u0002J\"\u00101\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020\u00022\b\u00102\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0018\u001a\u00020\fH\u0002JX\u00103\u001a\u0010\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u0010\u0018\u0001042\b\u00102\u001a\u0004\u0018\u00010\u00042\u0006\u00105\u001a\u0002062\u0006\u0010/\u001a\u0002002\u0006\u00107\u001a\u00020\n2\u0006\u00108\u001a\u00020\u00102\u0006\u00109\u001a\u00020\u00102\u0006\u0010:\u001a\u00020\u00102\u0006\u0010;\u001a\u00020\u0010H\u0002J\u0010\u0010<\u001a\u00020\u001e2\u0006\u0010=\u001a\u00020\nH\u0002J\u001a\u0010>\u001a\u00020\u001e2\b\u0010?\u001a\u0004\u0018\u00010\u00042\u0006\u0010\"\u001a\u00020\fH\u0002J\u0010\u0010@\u001a\u00020\u001e2\u0006\u0010'\u001a\u00020(H\u0002J\b\u0010A\u001a\u00020\u001eH\u0002J\u0010\u0010B\u001a\u00020\f2\u0006\u0010\u001f\u001a\u00020\u0002H$R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082D¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0014X\u0082\u0004¢\u0006\u0002\n\u0000R\u0012\u0010\u0015\u001a\u00020\n8\u0002@\u0002X\u0083\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0017X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u0018\u001a\u00020\fX\u0084\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\u001a\"\u0004\b\u001b\u0010\u001cR\u0012\u0010C\u001a\u000200X¤\u0004¢\u0006\u0006\u001a\u0004\bD\u0010E¨\u0006F"}, d2 = {"Lcom/facebook/imagepipeline/producers/DecodeProducer$ProgressiveDecoder;", "Lcom/facebook/imagepipeline/producers/DelegatingConsumer;", "Lcom/facebook/imagepipeline/image/EncodedImage;", "Lcom/facebook/common/references/CloseableReference;", "Lcom/facebook/imagepipeline/image/CloseableImage;", "consumer", "Lcom/facebook/imagepipeline/producers/Consumer;", "producerContext", "Lcom/facebook/imagepipeline/producers/ProducerContext;", "decodeCancellationEnabled", "", "maxBitmapDimension", "", "<init>", "(Lcom/facebook/imagepipeline/producers/DecodeProducer;Lcom/facebook/imagepipeline/producers/Consumer;Lcom/facebook/imagepipeline/producers/ProducerContext;ZI)V", "TAG", "", "producerListener", "Lcom/facebook/imagepipeline/producers/ProducerListener2;", "imageDecodeOptions", "Lcom/facebook/imagepipeline/common/ImageDecodeOptions;", "isFinished", "jobScheduler", "Lcom/facebook/imagepipeline/producers/JobScheduler;", "lastScheduledScanNumber", "getLastScheduledScanNumber", "()I", "setLastScheduledScanNumber", "(I)V", "maybeIncreaseSampleSize", "", "encodedImage", "onNewResultImpl", "newResult", NotificationCompat.CATEGORY_STATUS, "onProgressUpdateImpl", "progress", "", "onFailureImpl", "t", "", "onCancellationImpl", "updateDecodeJob", "ref", "doDecode", "internalDecode", "length", "quality", "Lcom/facebook/imagepipeline/image/QualityInfo;", "setImageExtras", "image", "getExtraMap", "", "queueTime", "", "isFinal", "imageFormatName", "encodedImageSize", "requestImageSize", "sampleSize", "maybeFinish", "shouldFinish", "handleResult", "decodedImage", "handleError", "handleCancellation", "getIntermediateImageEndOffset", "qualityInfo", "getQualityInfo", "()Lcom/facebook/imagepipeline/image/QualityInfo;", "imagepipeline_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
    /* loaded from: classes.dex */
    public abstract class ProgressiveDecoder extends DelegatingConsumer<EncodedImage, CloseableReference<CloseableImage>> {
        private final String TAG;
        private final ImageDecodeOptions imageDecodeOptions;
        private boolean isFinished;
        private final JobScheduler jobScheduler;
        private int lastScheduledScanNumber;
        private final ProducerContext producerContext;
        private final ProducerListener2 producerListener;
        final /* synthetic */ DecodeProducer this$0;

        protected abstract int getIntermediateImageEndOffset(EncodedImage encodedImage);

        protected abstract QualityInfo getQualityInfo();

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ProgressiveDecoder(final DecodeProducer decodeProducer, Consumer<CloseableReference<CloseableImage>> consumer, ProducerContext producerContext, final boolean z, final int i) {
            super(consumer);
            Intrinsics.checkNotNullParameter(consumer, "consumer");
            Intrinsics.checkNotNullParameter(producerContext, "producerContext");
            this.this$0 = decodeProducer;
            this.producerContext = producerContext;
            this.TAG = "ProgressiveDecoder";
            this.producerListener = producerContext.getProducerListener();
            ImageDecodeOptions imageDecodeOptions = producerContext.getImageRequest().getImageDecodeOptions();
            Intrinsics.checkNotNullExpressionValue(imageDecodeOptions, "getImageDecodeOptions(...)");
            this.imageDecodeOptions = imageDecodeOptions;
            this.jobScheduler = new JobScheduler(decodeProducer.getExecutor(), new JobScheduler.JobRunnable() { // from class: com.facebook.imagepipeline.producers.DecodeProducer$ProgressiveDecoder$$ExternalSyntheticLambda0
                @Override // com.facebook.imagepipeline.producers.JobScheduler.JobRunnable
                public final void run(EncodedImage encodedImage, int i2) {
                    DecodeProducer.ProgressiveDecoder._init_$lambda$2(DecodeProducer.ProgressiveDecoder.this, decodeProducer, i, encodedImage, i2);
                }
            }, imageDecodeOptions.minDecodeIntervalMs);
            producerContext.addCallbacks(new BaseProducerContextCallbacks() { // from class: com.facebook.imagepipeline.producers.DecodeProducer.ProgressiveDecoder.1
                @Override // com.facebook.imagepipeline.producers.BaseProducerContextCallbacks, com.facebook.imagepipeline.producers.ProducerContextCallbacks
                public void onIsIntermediateResultExpectedChanged() {
                    if (ProgressiveDecoder.this.producerContext.isIntermediateResultExpected()) {
                        ProgressiveDecoder.this.jobScheduler.scheduleJob();
                    }
                }

                @Override // com.facebook.imagepipeline.producers.BaseProducerContextCallbacks, com.facebook.imagepipeline.producers.ProducerContextCallbacks
                public void onCancellationRequested() {
                    if (z) {
                        ProgressiveDecoder.this.handleCancellation();
                    }
                }
            });
        }

        protected final int getLastScheduledScanNumber() {
            return this.lastScheduledScanNumber;
        }

        protected final void setLastScheduledScanNumber(int i) {
            this.lastScheduledScanNumber = i;
        }

        private final void maybeIncreaseSampleSize(EncodedImage encodedImage) {
            if (encodedImage.getImageFormat() != DefaultImageFormats.JPEG) {
                return;
            }
            encodedImage.setSampleSize(DownsampleUtil.determineSampleSizeJPEG(encodedImage, BitmapUtil.getPixelSizeForBitmapConfig(this.imageDecodeOptions.bitmapConfig), DecodeProducer.MAX_BITMAP_SIZE));
        }

        @Override // com.facebook.imagepipeline.producers.BaseConsumer
        public void onNewResultImpl(EncodedImage newResult, int status) {
            FrescoSystrace frescoSystrace = FrescoSystrace.INSTANCE;
            if (!FrescoSystrace.isTracing()) {
                boolean isLast = BaseConsumer.isLast(status);
                if (isLast) {
                    if (newResult == null) {
                        boolean areEqual = Intrinsics.areEqual(this.producerContext.getExtra("cached_value_found"), (Object) true);
                        if (!this.producerContext.getImagePipelineConfig().getExperiments().getCancelDecodeOnCacheMiss() || this.producerContext.getLowestPermittedRequestLevel() == ImageRequest.RequestLevel.FULL_FETCH || areEqual) {
                            handleError(new ExceptionWithNoStacktrace("Encoded image is null."));
                            return;
                        }
                    } else if (!newResult.isValid()) {
                        handleError(new ExceptionWithNoStacktrace("Encoded image is not valid."));
                        return;
                    }
                }
                if (updateDecodeJob(newResult, status)) {
                    boolean statusHasFlag = BaseConsumer.statusHasFlag(status, 4);
                    if (isLast || statusHasFlag || this.producerContext.isIntermediateResultExpected()) {
                        this.jobScheduler.scheduleJob();
                        return;
                    }
                    return;
                }
                return;
            }
            FrescoSystrace.beginSection("DecodeProducer#onNewResultImpl");
            try {
                boolean isLast2 = BaseConsumer.isLast(status);
                if (isLast2) {
                    if (newResult == null) {
                        boolean areEqual2 = Intrinsics.areEqual(this.producerContext.getExtra("cached_value_found"), (Object) true);
                        if (!this.producerContext.getImagePipelineConfig().getExperiments().getCancelDecodeOnCacheMiss() || this.producerContext.getLowestPermittedRequestLevel() == ImageRequest.RequestLevel.FULL_FETCH || areEqual2) {
                            handleError(new ExceptionWithNoStacktrace("Encoded image is null."));
                            return;
                        }
                    } else if (!newResult.isValid()) {
                        handleError(new ExceptionWithNoStacktrace("Encoded image is not valid."));
                        return;
                    }
                }
                if (updateDecodeJob(newResult, status)) {
                    boolean statusHasFlag2 = BaseConsumer.statusHasFlag(status, 4);
                    if (isLast2 || statusHasFlag2 || this.producerContext.isIntermediateResultExpected()) {
                        this.jobScheduler.scheduleJob();
                    }
                    Unit unit = Unit.INSTANCE;
                }
            } finally {
                FrescoSystrace.endSection();
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.facebook.imagepipeline.producers.DelegatingConsumer, com.facebook.imagepipeline.producers.BaseConsumer
        public void onProgressUpdateImpl(float progress) {
            super.onProgressUpdateImpl(progress * 0.99f);
        }

        @Override // com.facebook.imagepipeline.producers.DelegatingConsumer, com.facebook.imagepipeline.producers.BaseConsumer
        public void onFailureImpl(Throwable t) {
            Intrinsics.checkNotNullParameter(t, "t");
            handleError(t);
        }

        @Override // com.facebook.imagepipeline.producers.DelegatingConsumer, com.facebook.imagepipeline.producers.BaseConsumer
        public void onCancellationImpl() {
            handleCancellation();
        }

        protected boolean updateDecodeJob(EncodedImage ref, int status) {
            return this.jobScheduler.updateJob(ref, status);
        }

        /* JADX WARN: Can't wrap try/catch for region: R(8:29|30|(9:(13:34|(11:38|39|40|41|43|44|(1:46)|47|48|49|50)|63|39|40|41|43|44|(0)|47|48|49|50)|(11:38|39|40|41|43|44|(0)|47|48|49|50)|43|44|(0)|47|48|49|50)|64|63|39|40|41) */
        /* JADX WARN: Code restructure failed: missing block: B:61:0x0168, code lost:
        
            r0 = e;
         */
        /* JADX WARN: Code restructure failed: missing block: B:62:0x0169, code lost:
        
            r2 = null;
         */
        /* JADX WARN: Removed duplicated region for block: B:46:0x0144  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        private final void doDecode(EncodedImage encodedImage, int status, int lastScheduledScanNumber) {
            int size;
            QualityInfo qualityInfo;
            QualityInfo qualityInfo2;
            CloseableImage internalDecode;
            int i = status;
            if ((encodedImage.getImageFormat() != DefaultImageFormats.JPEG && BaseConsumer.isNotLast(status)) || this.isFinished || !EncodedImage.isValid(encodedImage)) {
                return;
            }
            if (Intrinsics.areEqual(encodedImage.getImageFormat(), DefaultImageFormats.GIF) && DecodeProducer.INSTANCE.isTooBig(encodedImage, this.imageDecodeOptions)) {
                IllegalStateException illegalStateException = new IllegalStateException("Image is too big to attempt decoding: w = " + encodedImage.getWidth() + ", h = " + encodedImage.getHeight() + ", pixel config = " + this.imageDecodeOptions.bitmapConfig + ", max bitmap size = 104857600");
                this.producerListener.onProducerFinishWithFailure(this.producerContext, DecodeProducer.PRODUCER_NAME, illegalStateException, null);
                handleError(illegalStateException);
                return;
            }
            ImageFormat imageFormat = encodedImage.getImageFormat();
            Intrinsics.checkNotNullExpressionValue(imageFormat, "getImageFormat(...)");
            String name = imageFormat.getName();
            String str = EnvironmentCompat.MEDIA_UNKNOWN;
            String str2 = name == null ? EnvironmentCompat.MEDIA_UNKNOWN : name;
            String str3 = encodedImage.getWidth() + "x" + encodedImage.getHeight();
            String valueOf = String.valueOf(encodedImage.getSampleSize());
            boolean isLast = BaseConsumer.isLast(status);
            boolean z = isLast && !BaseConsumer.statusHasFlag(i, 8);
            boolean statusHasFlag = BaseConsumer.statusHasFlag(i, 4);
            ResizeOptions resizeOptions = this.producerContext.getImageRequest().getResizeOptions();
            if (resizeOptions != null) {
                str = resizeOptions.width + "x" + resizeOptions.height;
            }
            String str4 = str;
            try {
                long queuedTime = this.jobScheduler.getQueuedTime();
                String uri = this.producerContext.getImageRequest().getSourceUri().toString();
                Intrinsics.checkNotNullExpressionValue(uri, "toString(...)");
                try {
                    try {
                        if (!z && !statusHasFlag) {
                            size = getIntermediateImageEndOffset(encodedImage);
                            if (!z && !statusHasFlag) {
                                qualityInfo = getQualityInfo();
                                qualityInfo2 = qualityInfo;
                                this.producerListener.onProducerStart(this.producerContext, DecodeProducer.PRODUCER_NAME);
                                Intrinsics.checkNotNull(qualityInfo2);
                                internalDecode = internalDecode(encodedImage, size, qualityInfo2);
                                if (encodedImage.getSampleSize() != 1) {
                                    i |= 16;
                                }
                                this.producerListener.onProducerFinishWithSuccess(this.producerContext, DecodeProducer.PRODUCER_NAME, getExtraMap(internalDecode, queuedTime, qualityInfo2, isLast, str2, str3, str4, valueOf));
                                setImageExtras(encodedImage, internalDecode, lastScheduledScanNumber);
                                handleResult(internalDecode, i);
                                return;
                            }
                            qualityInfo = ImmutableQualityInfo.FULL_QUALITY;
                            qualityInfo2 = qualityInfo;
                            this.producerListener.onProducerStart(this.producerContext, DecodeProducer.PRODUCER_NAME);
                            Intrinsics.checkNotNull(qualityInfo2);
                            internalDecode = internalDecode(encodedImage, size, qualityInfo2);
                            if (encodedImage.getSampleSize() != 1) {
                            }
                            this.producerListener.onProducerFinishWithSuccess(this.producerContext, DecodeProducer.PRODUCER_NAME, getExtraMap(internalDecode, queuedTime, qualityInfo2, isLast, str2, str3, str4, valueOf));
                            setImageExtras(encodedImage, internalDecode, lastScheduledScanNumber);
                            handleResult(internalDecode, i);
                            return;
                        }
                        if (!z) {
                            qualityInfo = getQualityInfo();
                            qualityInfo2 = qualityInfo;
                            this.producerListener.onProducerStart(this.producerContext, DecodeProducer.PRODUCER_NAME);
                            Intrinsics.checkNotNull(qualityInfo2);
                            internalDecode = internalDecode(encodedImage, size, qualityInfo2);
                            if (encodedImage.getSampleSize() != 1) {
                            }
                            this.producerListener.onProducerFinishWithSuccess(this.producerContext, DecodeProducer.PRODUCER_NAME, getExtraMap(internalDecode, queuedTime, qualityInfo2, isLast, str2, str3, str4, valueOf));
                            setImageExtras(encodedImage, internalDecode, lastScheduledScanNumber);
                            handleResult(internalDecode, i);
                            return;
                        }
                        if (encodedImage.getSampleSize() != 1) {
                        }
                        this.producerListener.onProducerFinishWithSuccess(this.producerContext, DecodeProducer.PRODUCER_NAME, getExtraMap(internalDecode, queuedTime, qualityInfo2, isLast, str2, str3, str4, valueOf));
                        setImageExtras(encodedImage, internalDecode, lastScheduledScanNumber);
                        handleResult(internalDecode, i);
                        return;
                    } catch (Exception e) {
                        e = e;
                        CloseableImage closeableImage = internalDecode;
                        Intrinsics.checkNotNull(qualityInfo2);
                        this.producerListener.onProducerFinishWithFailure(this.producerContext, DecodeProducer.PRODUCER_NAME, e, getExtraMap(closeableImage, queuedTime, qualityInfo2, isLast, str2, str3, str4, valueOf));
                        handleError(e);
                        return;
                    }
                    Intrinsics.checkNotNull(qualityInfo2);
                    internalDecode = internalDecode(encodedImage, size, qualityInfo2);
                } catch (DecodeException e2) {
                    EncodedImage encodedImage2 = e2.getEncodedImage();
                    FLog.w(this.TAG, "%s, {uri: %s, firstEncodedBytes: %s, length: %d}", e2.getMessage(), uri, encodedImage2.getFirstBytesAsHexString(10), Integer.valueOf(encodedImage2.getSize()));
                    throw e2;
                }
                size = encodedImage.getSize();
                qualityInfo = ImmutableQualityInfo.FULL_QUALITY;
                qualityInfo2 = qualityInfo;
                this.producerListener.onProducerStart(this.producerContext, DecodeProducer.PRODUCER_NAME);
            } finally {
                EncodedImage.closeSafely(encodedImage);
            }
        }

        private final CloseableImage internalDecode(EncodedImage encodedImage, int length, QualityInfo quality) {
            boolean z = this.this$0.getReclaimMemoryRunnable() != null && this.this$0.getRecoverFromDecoderOOM().get().booleanValue();
            try {
                return this.this$0.getImageDecoder().decode(encodedImage, length, quality, this.imageDecodeOptions);
            } catch (OutOfMemoryError e) {
                if (!z) {
                    throw e;
                }
                Runnable reclaimMemoryRunnable = this.this$0.getReclaimMemoryRunnable();
                if (reclaimMemoryRunnable != null) {
                    reclaimMemoryRunnable.run();
                }
                System.gc();
                return this.this$0.getImageDecoder().decode(encodedImage, length, quality, this.imageDecodeOptions);
            }
        }

        private final void setImageExtras(EncodedImage encodedImage, CloseableImage image, int lastScheduledScanNumber) {
            this.producerContext.putExtra("encoded_width", Integer.valueOf(encodedImage.getWidth()));
            this.producerContext.putExtra("encoded_height", Integer.valueOf(encodedImage.getHeight()));
            this.producerContext.putExtra("encoded_size", Integer.valueOf(encodedImage.getSize()));
            this.producerContext.putExtra("image_color_space", encodedImage.getColorSpace());
            if (image instanceof CloseableBitmap) {
                this.producerContext.putExtra("bitmap_config", String.valueOf(((CloseableBitmap) image).getUnderlyingBitmap().getConfig()));
            }
            if (image != null) {
                image.putExtras(this.producerContext.getExtras());
            }
            this.producerContext.putExtra("last_scan_num", Integer.valueOf(lastScheduledScanNumber));
        }

        private final Map<String, String> getExtraMap(CloseableImage image, long queueTime, QualityInfo quality, boolean isFinal, String imageFormatName, String encodedImageSize, String requestImageSize, String sampleSize) {
            Map<String, Object> extras;
            Object obj;
            String str = null;
            if (!this.producerListener.requiresExtraMap(this.producerContext, DecodeProducer.PRODUCER_NAME)) {
                return null;
            }
            String valueOf = String.valueOf(queueTime);
            String valueOf2 = String.valueOf(quality.isOfGoodEnoughQuality());
            String valueOf3 = String.valueOf(isFinal);
            if (image != null && (extras = image.getExtras()) != null && (obj = extras.get("non_fatal_decode_error")) != null) {
                str = obj.toString();
            }
            String str2 = str;
            if (image instanceof CloseableStaticBitmap) {
                Bitmap underlyingBitmap = ((CloseableStaticBitmap) image).getUnderlyingBitmap();
                Intrinsics.checkNotNullExpressionValue(underlyingBitmap, "getUnderlyingBitmap(...)");
                String str3 = underlyingBitmap.getWidth() + "x" + underlyingBitmap.getHeight();
                HashMap hashMap = new HashMap(8);
                hashMap.put("bitmapSize", str3);
                hashMap.put("queueTime", valueOf);
                hashMap.put("hasGoodQuality", valueOf2);
                hashMap.put("isFinal", valueOf3);
                hashMap.put("encodedImageSize", encodedImageSize);
                hashMap.put("imageFormat", imageFormatName);
                hashMap.put("requestedImageSize", requestImageSize);
                hashMap.put("sampleSize", sampleSize);
                hashMap.put("byteCount", new StringBuilder().append(underlyingBitmap.getByteCount()).toString());
                if (str2 != null) {
                    hashMap.put("non_fatal_decode_error", str2);
                }
                return ImmutableMap.copyOf((Map) hashMap);
            }
            HashMap hashMap2 = new HashMap(7);
            hashMap2.put("queueTime", valueOf);
            hashMap2.put("hasGoodQuality", valueOf2);
            hashMap2.put("isFinal", valueOf3);
            hashMap2.put("encodedImageSize", encodedImageSize);
            hashMap2.put("imageFormat", imageFormatName);
            hashMap2.put("requestedImageSize", requestImageSize);
            hashMap2.put("sampleSize", sampleSize);
            if (str2 != null) {
                hashMap2.put("non_fatal_decode_error", str2);
            }
            return ImmutableMap.copyOf((Map) hashMap2);
        }

        private final void maybeFinish(boolean shouldFinish) {
            synchronized (this) {
                if (shouldFinish) {
                    if (!this.isFinished) {
                        getConsumer().onProgressUpdate(1.0f);
                        this.isFinished = true;
                        Unit unit = Unit.INSTANCE;
                        this.jobScheduler.clearJob();
                    }
                }
            }
        }

        private final void handleResult(CloseableImage decodedImage, int status) {
            CloseableReference<CloseableImage> create = this.this$0.getCloseableReferenceFactory().create(decodedImage);
            try {
                maybeFinish(BaseConsumer.isLast(status));
                getConsumer().onNewResult(create, status);
            } finally {
                CloseableReference.closeSafely(create);
            }
        }

        private final void handleError(Throwable t) {
            maybeFinish(true);
            getConsumer().onFailure(t);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final void handleCancellation() {
            maybeFinish(true);
            getConsumer().onCancellation();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static final void _init_$lambda$2(ProgressiveDecoder this$0, DecodeProducer this$1, int i, EncodedImage encodedImage, int i2) {
            Intrinsics.checkNotNullParameter(this$0, "this$0");
            Intrinsics.checkNotNullParameter(this$1, "this$1");
            if (encodedImage != null) {
                ImageRequest imageRequest = this$0.producerContext.getImageRequest();
                this$0.producerContext.putExtra("image_format", encodedImage.getImageFormat().getName());
                Uri sourceUri = imageRequest.getSourceUri();
                encodedImage.setSource(sourceUri != null ? sourceUri.toString() : null);
                DownsampleMode downsampleOverride = imageRequest.getDownsampleOverride();
                if (downsampleOverride == null) {
                    downsampleOverride = this$1.getDownsampleMode();
                }
                boolean statusHasFlag = BaseConsumer.statusHasFlag(i2, 16);
                if ((downsampleOverride == DownsampleMode.ALWAYS || (downsampleOverride == DownsampleMode.AUTO && !statusHasFlag)) && (this$1.getDownsampleEnabledForNetwork() || !UriUtil.isNetworkUri(imageRequest.getSourceUri()))) {
                    RotationOptions rotationOptions = imageRequest.getRotationOptions();
                    Intrinsics.checkNotNullExpressionValue(rotationOptions, "getRotationOptions(...)");
                    encodedImage.setSampleSize(DownsampleUtil.determineSampleSize(rotationOptions, imageRequest.getResizeOptions(), encodedImage, i));
                }
                if (this$0.producerContext.getImagePipelineConfig().getExperiments().getDownsampleIfLargeBitmap()) {
                    this$0.maybeIncreaseSampleSize(encodedImage);
                }
                this$0.doDecode(encodedImage, i2, this$0.lastScheduledScanNumber);
            }
        }
    }

    /* compiled from: DecodeProducer.kt */
    @Metadata(d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0082\u0004\u0018\u00002\u00060\u0001R\u00020\u0002B3\u0012\u0012\u0010\u0003\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00060\u00050\u0004\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\n\u0012\u0006\u0010\u000b\u001a\u00020\f¢\u0006\u0004\b\r\u0010\u000eJ\u001a\u0010\u000f\u001a\u00020\n2\b\u0010\u0010\u001a\u0004\u0018\u00010\u00112\u0006\u0010\u0012\u001a\u00020\fH\u0014J\u0010\u0010\u0013\u001a\u00020\f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0014R\u0014\u0010\u0014\u001a\u00020\u00158TX\u0094\u0004¢\u0006\u0006\u001a\u0004\b\u0016\u0010\u0017¨\u0006\u0018"}, d2 = {"Lcom/facebook/imagepipeline/producers/DecodeProducer$LocalImagesProgressiveDecoder;", "Lcom/facebook/imagepipeline/producers/DecodeProducer$ProgressiveDecoder;", "Lcom/facebook/imagepipeline/producers/DecodeProducer;", "consumer", "Lcom/facebook/imagepipeline/producers/Consumer;", "Lcom/facebook/common/references/CloseableReference;", "Lcom/facebook/imagepipeline/image/CloseableImage;", "producerContext", "Lcom/facebook/imagepipeline/producers/ProducerContext;", "decodeCancellationEnabled", "", "maxBitmapDimension", "", "<init>", "(Lcom/facebook/imagepipeline/producers/DecodeProducer;Lcom/facebook/imagepipeline/producers/Consumer;Lcom/facebook/imagepipeline/producers/ProducerContext;ZI)V", "updateDecodeJob", "encodedImage", "Lcom/facebook/imagepipeline/image/EncodedImage;", NotificationCompat.CATEGORY_STATUS, "getIntermediateImageEndOffset", "qualityInfo", "Lcom/facebook/imagepipeline/image/QualityInfo;", "getQualityInfo", "()Lcom/facebook/imagepipeline/image/QualityInfo;", "imagepipeline_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
    /* loaded from: classes.dex */
    private final class LocalImagesProgressiveDecoder extends ProgressiveDecoder {
        final /* synthetic */ DecodeProducer this$0;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public LocalImagesProgressiveDecoder(DecodeProducer decodeProducer, Consumer<CloseableReference<CloseableImage>> consumer, ProducerContext producerContext, boolean z, int i) {
            super(decodeProducer, consumer, producerContext, z, i);
            Intrinsics.checkNotNullParameter(consumer, "consumer");
            Intrinsics.checkNotNullParameter(producerContext, "producerContext");
            this.this$0 = decodeProducer;
        }

        @Override // com.facebook.imagepipeline.producers.DecodeProducer.ProgressiveDecoder
        protected synchronized boolean updateDecodeJob(EncodedImage encodedImage, int status) {
            return BaseConsumer.isNotLast(status) ? false : super.updateDecodeJob(encodedImage, status);
        }

        @Override // com.facebook.imagepipeline.producers.DecodeProducer.ProgressiveDecoder
        protected int getIntermediateImageEndOffset(EncodedImage encodedImage) {
            Intrinsics.checkNotNullParameter(encodedImage, "encodedImage");
            return encodedImage.getSize();
        }

        @Override // com.facebook.imagepipeline.producers.DecodeProducer.ProgressiveDecoder
        protected QualityInfo getQualityInfo() {
            QualityInfo of = ImmutableQualityInfo.of(0, false, false);
            Intrinsics.checkNotNullExpressionValue(of, "of(...)");
            return of;
        }
    }

    /* compiled from: DecodeProducer.kt */
    @Metadata(d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0082\u0004\u0018\u00002\u00060\u0001R\u00020\u0002BC\u0012\u0012\u0010\u0003\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00060\u00050\u0004\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\n\u0012\u0006\u0010\u000b\u001a\u00020\f\u0012\u0006\u0010\r\u001a\u00020\u000e\u0012\u0006\u0010\u000f\u001a\u00020\u0010¢\u0006\u0004\b\u0011\u0010\u0012J\u001a\u0010\u0017\u001a\u00020\u000e2\b\u0010\u0018\u001a\u0004\u0018\u00010\u00192\u0006\u0010\u001a\u001a\u00020\u0010H\u0014J\u0010\u0010\u001b\u001a\u00020\u00102\u0006\u0010\u0018\u001a\u00020\u0019H\u0014R\u0011\u0010\t\u001a\u00020\n¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u000b\u001a\u00020\f¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0014\u0010\u001c\u001a\u00020\u001d8TX\u0094\u0004¢\u0006\u0006\u001a\u0004\b\u001e\u0010\u001f¨\u0006 "}, d2 = {"Lcom/facebook/imagepipeline/producers/DecodeProducer$NetworkImagesProgressiveDecoder;", "Lcom/facebook/imagepipeline/producers/DecodeProducer$ProgressiveDecoder;", "Lcom/facebook/imagepipeline/producers/DecodeProducer;", "consumer", "Lcom/facebook/imagepipeline/producers/Consumer;", "Lcom/facebook/common/references/CloseableReference;", "Lcom/facebook/imagepipeline/image/CloseableImage;", "producerContext", "Lcom/facebook/imagepipeline/producers/ProducerContext;", "progressiveJpegParser", "Lcom/facebook/imagepipeline/decoder/ProgressiveJpegParser;", "progressiveJpegConfig", "Lcom/facebook/imagepipeline/decoder/ProgressiveJpegConfig;", "decodeCancellationEnabled", "", "maxBitmapDimension", "", "<init>", "(Lcom/facebook/imagepipeline/producers/DecodeProducer;Lcom/facebook/imagepipeline/producers/Consumer;Lcom/facebook/imagepipeline/producers/ProducerContext;Lcom/facebook/imagepipeline/decoder/ProgressiveJpegParser;Lcom/facebook/imagepipeline/decoder/ProgressiveJpegConfig;ZI)V", "getProgressiveJpegParser", "()Lcom/facebook/imagepipeline/decoder/ProgressiveJpegParser;", "getProgressiveJpegConfig", "()Lcom/facebook/imagepipeline/decoder/ProgressiveJpegConfig;", "updateDecodeJob", "encodedImage", "Lcom/facebook/imagepipeline/image/EncodedImage;", NotificationCompat.CATEGORY_STATUS, "getIntermediateImageEndOffset", "qualityInfo", "Lcom/facebook/imagepipeline/image/QualityInfo;", "getQualityInfo", "()Lcom/facebook/imagepipeline/image/QualityInfo;", "imagepipeline_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
    /* loaded from: classes.dex */
    private final class NetworkImagesProgressiveDecoder extends ProgressiveDecoder {
        private final ProgressiveJpegConfig progressiveJpegConfig;
        private final ProgressiveJpegParser progressiveJpegParser;
        final /* synthetic */ DecodeProducer this$0;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public NetworkImagesProgressiveDecoder(DecodeProducer decodeProducer, Consumer<CloseableReference<CloseableImage>> consumer, ProducerContext producerContext, ProgressiveJpegParser progressiveJpegParser, ProgressiveJpegConfig progressiveJpegConfig, boolean z, int i) {
            super(decodeProducer, consumer, producerContext, z, i);
            Intrinsics.checkNotNullParameter(consumer, "consumer");
            Intrinsics.checkNotNullParameter(producerContext, "producerContext");
            Intrinsics.checkNotNullParameter(progressiveJpegParser, "progressiveJpegParser");
            Intrinsics.checkNotNullParameter(progressiveJpegConfig, "progressiveJpegConfig");
            this.this$0 = decodeProducer;
            this.progressiveJpegParser = progressiveJpegParser;
            this.progressiveJpegConfig = progressiveJpegConfig;
            setLastScheduledScanNumber(0);
        }

        public final ProgressiveJpegParser getProgressiveJpegParser() {
            return this.progressiveJpegParser;
        }

        public final ProgressiveJpegConfig getProgressiveJpegConfig() {
            return this.progressiveJpegConfig;
        }

        @Override // com.facebook.imagepipeline.producers.DecodeProducer.ProgressiveDecoder
        protected synchronized boolean updateDecodeJob(EncodedImage encodedImage, int status) {
            if (encodedImage == null) {
                return false;
            }
            boolean updateDecodeJob = super.updateDecodeJob(encodedImage, status);
            if ((BaseConsumer.isNotLast(status) || BaseConsumer.statusHasFlag(status, 8)) && !BaseConsumer.statusHasFlag(status, 4) && EncodedImage.isValid(encodedImage) && encodedImage.getImageFormat() == DefaultImageFormats.JPEG) {
                if (!this.progressiveJpegParser.parseMoreData(encodedImage)) {
                    return false;
                }
                int bestScanNumber = this.progressiveJpegParser.getBestScanNumber();
                if (bestScanNumber <= getLastScheduledScanNumber()) {
                    return false;
                }
                if (bestScanNumber < this.progressiveJpegConfig.getNextScanNumberToDecode(getLastScheduledScanNumber()) && !this.progressiveJpegParser.isEndMarkerRead()) {
                    return false;
                }
                setLastScheduledScanNumber(bestScanNumber);
            }
            return updateDecodeJob;
        }

        @Override // com.facebook.imagepipeline.producers.DecodeProducer.ProgressiveDecoder
        protected int getIntermediateImageEndOffset(EncodedImage encodedImage) {
            Intrinsics.checkNotNullParameter(encodedImage, "encodedImage");
            return this.progressiveJpegParser.getBestScanEndOffset();
        }

        @Override // com.facebook.imagepipeline.producers.DecodeProducer.ProgressiveDecoder
        protected QualityInfo getQualityInfo() {
            QualityInfo qualityInfo = this.progressiveJpegConfig.getQualityInfo(this.progressiveJpegParser.getBestScanNumber());
            Intrinsics.checkNotNullExpressionValue(qualityInfo, "getQualityInfo(...)");
            return qualityInfo;
        }
    }

    /* compiled from: DecodeProducer.kt */
    @Metadata(d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u000b\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0017H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0007X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000¨\u0006\u0018"}, d2 = {"Lcom/facebook/imagepipeline/producers/DecodeProducer$Companion;", "", "<init>", "()V", "PRODUCER_NAME", "", "DECODE_EXCEPTION_MESSAGE_NUM_HEADER_BYTES", "", "MAX_BITMAP_SIZE", "EXTRA_BITMAP_SIZE", "EXTRA_HAS_GOOD_QUALITY", "EXTRA_IS_FINAL", "EXTRA_IMAGE_FORMAT_NAME", "EXTRA_BITMAP_BYTES", "ENCODED_IMAGE_SIZE", "REQUESTED_IMAGE_SIZE", "SAMPLE_SIZE", "NON_FATAL_DECODE_ERROR", "isTooBig", "", "encodedImage", "Lcom/facebook/imagepipeline/image/EncodedImage;", "imageDecodeOptions", "Lcom/facebook/imagepipeline/common/ImageDecodeOptions;", "imagepipeline_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
    /* loaded from: classes.dex */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final boolean isTooBig(EncodedImage encodedImage, ImageDecodeOptions imageDecodeOptions) {
            return (((long) encodedImage.getWidth()) * ((long) encodedImage.getHeight())) * ((long) BitmapUtil.getPixelSizeForBitmapConfig(imageDecodeOptions.bitmapConfig)) > StatFsHelper.DEFAULT_DISK_RED_LEVEL_IN_BYTES;
        }
    }
}
