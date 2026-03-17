package com.facebook.imagepipeline.platform;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.ColorSpace;
import android.graphics.Rect;
import android.os.Build;
import androidx.core.util.Pools;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.logging.FLog;
import com.facebook.common.memory.DecodeBufferHelper;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.references.ResourceReleaser;
import com.facebook.common.streams.LimitedInputStream;
import com.facebook.common.streams.TailAppendingInputStream;
import com.facebook.imagepipeline.bitmaps.SimpleBitmapReleaser;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.memory.BitmapPool;
import com.facebook.imagepipeline.memory.DummyBitmapPool;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public abstract class DefaultDecoder implements PlatformDecoder {
    private boolean mAvoidPoolGet;
    private boolean mAvoidPoolRelease;
    private final BitmapPool mBitmapPool;
    final Pools.Pool<ByteBuffer> mDecodeBuffers;

    @Nullable
    private final PreverificationHelper mPreverificationHelper;
    private static final Class<?> TAG = DefaultDecoder.class;
    private static final byte[] EOI_TAIL = {-1, -39};

    public abstract int getBitmapSize(int i, int i2, BitmapFactory.Options options);

    public DefaultDecoder(BitmapPool bitmapPool, Pools.Pool<ByteBuffer> pool, PlatformDecoderOptions platformDecoderOptions) {
        this.mPreverificationHelper = Build.VERSION.SDK_INT >= 26 ? new PreverificationHelper() : null;
        this.mBitmapPool = bitmapPool;
        if (bitmapPool instanceof DummyBitmapPool) {
            this.mAvoidPoolGet = platformDecoderOptions.getAvoidPoolGet();
            this.mAvoidPoolRelease = platformDecoderOptions.getAvoidPoolRelease();
        }
        this.mDecodeBuffers = pool;
    }

    @Override // com.facebook.imagepipeline.platform.PlatformDecoder
    @Nullable
    public CloseableReference<Bitmap> decodeFromEncodedImage(EncodedImage encodedImage, Bitmap.Config config, @Nullable Rect rect) {
        return decodeFromEncodedImageWithColorSpace(encodedImage, config, rect, null);
    }

    @Override // com.facebook.imagepipeline.platform.PlatformDecoder
    @Nullable
    public CloseableReference<Bitmap> decodeJPEGFromEncodedImage(EncodedImage encodedImage, Bitmap.Config config, @Nullable Rect rect, int i) {
        return decodeJPEGFromEncodedImageWithColorSpace(encodedImage, config, rect, i, null);
    }

    @Override // com.facebook.imagepipeline.platform.PlatformDecoder
    @Nullable
    public CloseableReference<Bitmap> decodeFromEncodedImageWithColorSpace(EncodedImage encodedImage, Bitmap.Config config, @Nullable Rect rect, @Nullable ColorSpace colorSpace) {
        BitmapFactory.Options decodeOptionsForStream = getDecodeOptionsForStream(encodedImage, config, this.mAvoidPoolGet);
        boolean z = decodeOptionsForStream.inPreferredConfig != Bitmap.Config.ARGB_8888;
        try {
            return decodeFromStream((InputStream) Preconditions.checkNotNull(encodedImage.getInputStream()), decodeOptionsForStream, rect, colorSpace);
        } catch (RuntimeException e) {
            if (z) {
                return decodeFromEncodedImageWithColorSpace(encodedImage, Bitmap.Config.ARGB_8888, rect, colorSpace);
            }
            throw e;
        }
    }

    @Override // com.facebook.imagepipeline.platform.PlatformDecoder
    @Nullable
    public CloseableReference<Bitmap> decodeJPEGFromEncodedImageWithColorSpace(EncodedImage encodedImage, Bitmap.Config config, @Nullable Rect rect, int i, @Nullable ColorSpace colorSpace) {
        boolean isCompleteAt = encodedImage.isCompleteAt(i);
        BitmapFactory.Options decodeOptionsForStream = getDecodeOptionsForStream(encodedImage, config, this.mAvoidPoolGet);
        InputStream inputStream = encodedImage.getInputStream();
        Preconditions.checkNotNull(inputStream);
        if (encodedImage.getSize() > i) {
            inputStream = new LimitedInputStream(inputStream, i);
        }
        if (!isCompleteAt) {
            inputStream = new TailAppendingInputStream(inputStream, EOI_TAIL);
        }
        boolean z = decodeOptionsForStream.inPreferredConfig != Bitmap.Config.ARGB_8888;
        try {
            try {
                CloseableReference<Bitmap> decodeFromStream = decodeFromStream(inputStream, decodeOptionsForStream, rect, colorSpace);
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return decodeFromStream;
            } catch (RuntimeException e2) {
                if (z) {
                    CloseableReference<Bitmap> decodeJPEGFromEncodedImageWithColorSpace = decodeJPEGFromEncodedImageWithColorSpace(encodedImage, Bitmap.Config.ARGB_8888, rect, i, colorSpace);
                    try {
                        inputStream.close();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                    return decodeJPEGFromEncodedImageWithColorSpace;
                }
                throw e2;
            }
        } catch (Throwable th) {
            try {
                inputStream.close();
            } catch (IOException e4) {
                e4.printStackTrace();
            }
            throw th;
        }
    }

    @Nullable
    protected CloseableReference<Bitmap> decodeStaticImageFromStream(InputStream inputStream, BitmapFactory.Options options, @Nullable Rect rect) {
        return decodeFromStream(inputStream, options, rect, null);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:18:0x0060  */
    /* JADX WARN: Removed duplicated region for block: B:23:0x0074  */
    /* JADX WARN: Removed duplicated region for block: B:31:0x008a A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:38:0x00c0 A[Catch: all -> 0x00f1, RuntimeException -> 0x00f3, IllegalArgumentException -> 0x00fc, TRY_LEAVE, TryCatch #5 {RuntimeException -> 0x00f3, blocks: (B:25:0x007c, B:28:0x0086, B:36:0x009d, B:38:0x00c0, B:60:0x00b1, B:65:0x00b9, B:66:0x00bc), top: B:24:0x007c, outer: #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:41:0x00cb A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:50:0x00e1  */
    /* JADX WARN: Removed duplicated region for block: B:52:0x00ea  */
    /* JADX WARN: Type inference failed for: r0v1, types: [int] */
    /* JADX WARN: Type inference failed for: r0v7 */
    /* JADX WARN: Type inference failed for: r0v8 */
    @Nullable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private CloseableReference<Bitmap> decodeFromStream(InputStream inputStream, BitmapFactory.Options options, @Nullable Rect rect, @Nullable ColorSpace colorSpace) {
        Bitmap bitmap;
        ByteBuffer acquire;
        Bitmap bitmap2;
        BitmapRegionDecoder bitmapRegionDecoder;
        PreverificationHelper preverificationHelper;
        Preconditions.checkNotNull(inputStream);
        int i = options.outWidth;
        int i2 = options.outHeight;
        ?? r0 = i;
        if (rect != null) {
            int width = rect.width() / options.inSampleSize;
            i2 = rect.height() / options.inSampleSize;
            r0 = width;
        }
        boolean z = Build.VERSION.SDK_INT >= 26 && (preverificationHelper = this.mPreverificationHelper) != null && preverificationHelper.shouldUseHardwareBitmapConfig(options.inPreferredConfig);
        BitmapRegionDecoder bitmapRegionDecoder2 = null;
        try {
            try {
                try {
                    if (rect == null && z) {
                        options.inMutable = false;
                    } else {
                        if (rect != null && z) {
                            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                        }
                        if (!this.mAvoidPoolGet) {
                            Bitmap bitmap3 = this.mBitmapPool.get(getBitmapSize(r0, i2, options));
                            bitmap = bitmap3;
                            if (bitmap3 == null) {
                                throw new NullPointerException("BitmapPool.get returned null");
                            }
                            options.inBitmap = bitmap;
                            if (Build.VERSION.SDK_INT >= 26) {
                                if (colorSpace == null) {
                                    colorSpace = ColorSpace.get(ColorSpace.Named.SRGB);
                                }
                                options.inPreferredColorSpace = colorSpace;
                            }
                            acquire = this.mDecodeBuffers.acquire();
                            if (acquire == null) {
                                acquire = ByteBuffer.allocate(DecodeBufferHelper.getRecommendedDecodeBufferSize());
                            }
                            options.inTempStorage = acquire.array();
                            if (rect != null && bitmap != 0) {
                                try {
                                    if (options.inPreferredConfig != null) {
                                        try {
                                            bitmap.reconfigure(r0, i2, options.inPreferredConfig);
                                            bitmapRegionDecoder = BitmapRegionDecoder.newInstance(inputStream, true);
                                            if (bitmapRegionDecoder != null) {
                                                try {
                                                    bitmap2 = bitmapRegionDecoder.decodeRegion(rect, options);
                                                } catch (IOException unused) {
                                                    FLog.e(TAG, "Could not decode region %s, decoding full bitmap instead.", rect);
                                                    if (bitmapRegionDecoder != null) {
                                                        bitmapRegionDecoder.recycle();
                                                    }
                                                    bitmap2 = null;
                                                    if (bitmap2 == null) {
                                                    }
                                                    this.mDecodeBuffers.release(acquire);
                                                    if (bitmap == 0) {
                                                    }
                                                    if (!this.mAvoidPoolRelease) {
                                                    }
                                                }
                                            } else {
                                                bitmap2 = null;
                                            }
                                            if (bitmapRegionDecoder != null) {
                                                bitmapRegionDecoder.recycle();
                                            }
                                        } catch (IOException unused2) {
                                            bitmapRegionDecoder = null;
                                        } catch (Throwable th) {
                                            th = th;
                                            if (bitmapRegionDecoder2 != null) {
                                                bitmapRegionDecoder2.recycle();
                                            }
                                            throw th;
                                        }
                                        if (bitmap2 == null) {
                                            bitmap2 = BitmapFactory.decodeStream(inputStream, null, options);
                                        }
                                        this.mDecodeBuffers.release(acquire);
                                        if (bitmap == 0 && bitmap != bitmap2) {
                                            this.mBitmapPool.release(bitmap);
                                            if (bitmap2 != null) {
                                                bitmap2.recycle();
                                            }
                                            throw new IllegalStateException();
                                        }
                                        if (!this.mAvoidPoolRelease) {
                                            return CloseableReference.of(bitmap2, NoOpResourceReleaser.INSTANCE);
                                        }
                                        return CloseableReference.of(bitmap2, this.mBitmapPool);
                                    }
                                } catch (Throwable th2) {
                                    th = th2;
                                    bitmapRegionDecoder2 = r0;
                                }
                            }
                            bitmap2 = null;
                            if (bitmap2 == null) {
                            }
                            this.mDecodeBuffers.release(acquire);
                            if (bitmap == 0) {
                            }
                            if (!this.mAvoidPoolRelease) {
                            }
                        }
                    }
                    options.inTempStorage = acquire.array();
                    if (rect != null) {
                        if (options.inPreferredConfig != null) {
                        }
                    }
                    bitmap2 = null;
                    if (bitmap2 == null) {
                    }
                    this.mDecodeBuffers.release(acquire);
                    if (bitmap == 0) {
                    }
                    if (!this.mAvoidPoolRelease) {
                    }
                } catch (Throwable th3) {
                    this.mDecodeBuffers.release(acquire);
                    throw th3;
                }
            } catch (RuntimeException e) {
                if (bitmap != 0) {
                    this.mBitmapPool.release(bitmap);
                }
                throw e;
            }
        } catch (IllegalArgumentException e2) {
            if (bitmap != 0) {
                this.mBitmapPool.release(bitmap);
            }
            try {
                inputStream.reset();
                Bitmap decodeStream = BitmapFactory.decodeStream(inputStream);
                if (decodeStream == null) {
                    throw e2;
                }
                CloseableReference<Bitmap> of = CloseableReference.of(decodeStream, SimpleBitmapReleaser.getInstance());
                this.mDecodeBuffers.release(acquire);
                return of;
            } catch (IOException unused3) {
                throw e2;
            }
        }
        bitmap = 0;
        options.inBitmap = bitmap;
        if (Build.VERSION.SDK_INT >= 26) {
        }
        acquire = this.mDecodeBuffers.acquire();
        if (acquire == null) {
        }
    }

    private static BitmapFactory.Options getDecodeOptionsForStream(EncodedImage encodedImage, Bitmap.Config config, boolean z) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = encodedImage.getSampleSize();
        options.inJustDecodeBounds = true;
        options.inDither = true;
        boolean z2 = Build.VERSION.SDK_INT >= 26 && config == Bitmap.Config.HARDWARE;
        if (!z2) {
            options.inPreferredConfig = config;
        }
        options.inMutable = true;
        if (!z) {
            BitmapFactory.decodeStream(encodedImage.getInputStream(), null, options);
            if (options.outWidth == -1 || options.outHeight == -1) {
                throw new IllegalArgumentException();
            }
        }
        if (z2) {
            options.inPreferredConfig = config;
        }
        options.inJustDecodeBounds = false;
        return options;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class NoOpResourceReleaser implements ResourceReleaser<Bitmap> {
        private static final NoOpResourceReleaser INSTANCE = new NoOpResourceReleaser();

        @Override // com.facebook.common.references.ResourceReleaser
        public void release(Bitmap bitmap) {
        }

        private NoOpResourceReleaser() {
        }
    }
}
