package com.facebook.imagepipeline.bitmaps;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.webp.BitmapCreator;
import com.facebook.imageformat.DefaultImageFormats;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.memory.FlexByteArrayPool;
import com.facebook.imagepipeline.memory.PoolFactory;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: HoneycombBitmapCreator.kt */
@Metadata(d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \u00112\u00020\u0001:\u0001\u0011B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J \u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\r2\u0006\u0010\u000f\u001a\u00020\u0010H\u0016R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0012"}, d2 = {"Lcom/facebook/imagepipeline/bitmaps/HoneycombBitmapCreator;", "Lcom/facebook/common/webp/BitmapCreator;", "poolFactory", "Lcom/facebook/imagepipeline/memory/PoolFactory;", "<init>", "(Lcom/facebook/imagepipeline/memory/PoolFactory;)V", "jpegGenerator", "Lcom/facebook/imagepipeline/bitmaps/EmptyJpegGenerator;", "flexByteArrayPool", "Lcom/facebook/imagepipeline/memory/FlexByteArrayPool;", "createNakedBitmap", "Landroid/graphics/Bitmap;", "width", "", "height", "bitmapConfig", "Landroid/graphics/Bitmap$Config;", "Companion", "imagepipeline_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes.dex */
public final class HoneycombBitmapCreator implements BitmapCreator {

    /* renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private final FlexByteArrayPool flexByteArrayPool;
    private final EmptyJpegGenerator jpegGenerator;

    public HoneycombBitmapCreator(PoolFactory poolFactory) {
        Intrinsics.checkNotNullParameter(poolFactory, "poolFactory");
        this.jpegGenerator = new EmptyJpegGenerator(poolFactory.getPooledByteBufferFactory());
        FlexByteArrayPool flexByteArrayPool = poolFactory.getFlexByteArrayPool();
        Intrinsics.checkNotNullExpressionValue(flexByteArrayPool, "getFlexByteArrayPool(...)");
        this.flexByteArrayPool = flexByteArrayPool;
    }

    @Override // com.facebook.common.webp.BitmapCreator
    public Bitmap createNakedBitmap(int width, int height, Bitmap.Config bitmapConfig) {
        EncodedImage encodedImage;
        Intrinsics.checkNotNullParameter(bitmapConfig, "bitmapConfig");
        CloseableReference<PooledByteBuffer> generate = this.jpegGenerator.generate((short) width, (short) height);
        Intrinsics.checkNotNullExpressionValue(generate, "generate(...)");
        try {
            encodedImage = new EncodedImage(generate);
            try {
                encodedImage.setImageFormat(DefaultImageFormats.JPEG);
                BitmapFactory.Options bitmapFactoryOptions = INSTANCE.getBitmapFactoryOptions(encodedImage.getSampleSize(), bitmapConfig);
                int size = generate.get().size();
                PooledByteBuffer pooledByteBuffer = generate.get();
                Intrinsics.checkNotNullExpressionValue(pooledByteBuffer, "get(...)");
                CloseableReference<byte[]> closeableReference = this.flexByteArrayPool.get(size + 2);
                byte[] bArr = closeableReference.get();
                Intrinsics.checkNotNullExpressionValue(bArr, "get(...)");
                byte[] bArr2 = bArr;
                pooledByteBuffer.read(0, bArr2, 0, size);
                Bitmap decodeByteArray = BitmapFactory.decodeByteArray(bArr2, 0, size, bitmapFactoryOptions);
                if (decodeByteArray == null) {
                    throw new IllegalStateException("Required value was null.".toString());
                }
                decodeByteArray.setHasAlpha(true);
                decodeByteArray.eraseColor(0);
                CloseableReference.closeSafely((CloseableReference<?>) closeableReference);
                EncodedImage.closeSafely(encodedImage);
                CloseableReference.closeSafely(generate);
                return decodeByteArray;
            } catch (Throwable th) {
                th = th;
                CloseableReference.closeSafely((CloseableReference<?>) null);
                EncodedImage.closeSafely(encodedImage);
                CloseableReference.closeSafely(generate);
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            encodedImage = null;
        }
    }

    /* compiled from: HoneycombBitmapCreator.kt */
    @Metadata(d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0002¨\u0006\n"}, d2 = {"Lcom/facebook/imagepipeline/bitmaps/HoneycombBitmapCreator$Companion;", "", "<init>", "()V", "getBitmapFactoryOptions", "Landroid/graphics/BitmapFactory$Options;", "sampleSize", "", "bitmapConfig", "Landroid/graphics/Bitmap$Config;", "imagepipeline_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
    /* loaded from: classes.dex */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final BitmapFactory.Options getBitmapFactoryOptions(int sampleSize, Bitmap.Config bitmapConfig) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inDither = true;
            options.inPreferredConfig = bitmapConfig;
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inSampleSize = sampleSize;
            options.inMutable = true;
            return options;
        }
    }
}
