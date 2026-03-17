package com.facebook.drawee.backends.pipeline;

import android.content.Context;
import com.facebook.common.logging.FLog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.core.NativeCodeSetup;
import com.facebook.imagepipeline.systrace.FrescoSystrace;
import com.facebook.soloader.nativeloader.NativeLoader;
import com.facebook.soloader.nativeloader.SystemDelegate;
import java.lang.reflect.InvocationTargetException;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public class Fresco {
    private static final Class<?> TAG = Fresco.class;

    @Nullable
    private static PipelineDraweeControllerBuilderSupplier sDraweeControllerBuilderSupplier = null;
    private static volatile boolean sIsInitialized = false;

    private Fresco() {
    }

    public static void initialize(Context context) {
        initialize(context, null, null);
    }

    public static void initialize(Context context, @Nullable ImagePipelineConfig imagePipelineConfig) {
        initialize(context, imagePipelineConfig, null);
    }

    public static void initialize(Context context, @Nullable ImagePipelineConfig imagePipelineConfig, @Nullable DraweeConfig draweeConfig) {
        initialize(context, imagePipelineConfig, draweeConfig, true);
    }

    /* JADX WARN: Code restructure failed: missing block: B:15:0x004d, code lost:
    
        if (com.facebook.imagepipeline.systrace.FrescoSystrace.isTracing() != false) goto L35;
     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x008d, code lost:
    
        com.facebook.imagepipeline.systrace.FrescoSystrace.endSection();
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x007c, code lost:
    
        if (com.facebook.imagepipeline.systrace.FrescoSystrace.isTracing() == false) goto L48;
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x006d, code lost:
    
        if (com.facebook.imagepipeline.systrace.FrescoSystrace.isTracing() == false) goto L48;
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x008b, code lost:
    
        if (com.facebook.imagepipeline.systrace.FrescoSystrace.isTracing() == false) goto L48;
     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x005e, code lost:
    
        if (com.facebook.imagepipeline.systrace.FrescoSystrace.isTracing() == false) goto L48;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static void initialize(Context context, @Nullable ImagePipelineConfig imagePipelineConfig, @Nullable DraweeConfig draweeConfig, boolean z) {
        if (FrescoSystrace.isTracing()) {
            FrescoSystrace.beginSection("Fresco#initialize");
        }
        if (sIsInitialized) {
            FLog.w(TAG, "Fresco has already been initialized! `Fresco.initialize(...)` should only be called 1 single time to avoid memory leaks!");
        } else {
            sIsInitialized = true;
        }
        NativeCodeSetup.setUseNativeCode(z);
        if (!NativeLoader.isInitialized()) {
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.beginSection("Fresco.initialize->SoLoader.init");
            }
            try {
                try {
                    try {
                        try {
                            try {
                                Class.forName("com.facebook.imagepipeline.nativecode.NativeCodeInitializer").getMethod("init", Context.class).invoke(null, context);
                            } catch (IllegalAccessException unused) {
                                NativeLoader.initIfUninitialized(new SystemDelegate());
                            }
                        } catch (InvocationTargetException unused2) {
                            NativeLoader.initIfUninitialized(new SystemDelegate());
                        }
                    } catch (ClassNotFoundException unused3) {
                        NativeLoader.initIfUninitialized(new SystemDelegate());
                    }
                } catch (NoSuchMethodException unused4) {
                    NativeLoader.initIfUninitialized(new SystemDelegate());
                }
            } catch (Throwable th) {
                if (FrescoSystrace.isTracing()) {
                    FrescoSystrace.endSection();
                }
                throw th;
            }
        }
        Context applicationContext = context.getApplicationContext();
        if (imagePipelineConfig == null) {
            ImagePipelineFactory.initialize(applicationContext);
        } else {
            ImagePipelineFactory.initialize(imagePipelineConfig);
        }
        initializeDrawee(applicationContext, draweeConfig);
        if (FrescoSystrace.isTracing()) {
            FrescoSystrace.endSection();
        }
    }

    private static void initializeDrawee(Context context, @Nullable DraweeConfig draweeConfig) {
        if (FrescoSystrace.isTracing()) {
            FrescoSystrace.beginSection("Fresco.initializeDrawee");
        }
        PipelineDraweeControllerBuilderSupplier pipelineDraweeControllerBuilderSupplier = new PipelineDraweeControllerBuilderSupplier(context, draweeConfig);
        sDraweeControllerBuilderSupplier = pipelineDraweeControllerBuilderSupplier;
        SimpleDraweeView.initialize(pipelineDraweeControllerBuilderSupplier);
        if (FrescoSystrace.isTracing()) {
            FrescoSystrace.endSection();
        }
    }

    public static PipelineDraweeControllerBuilderSupplier getDraweeControllerBuilderSupplier() {
        return sDraweeControllerBuilderSupplier;
    }

    public static PipelineDraweeControllerBuilder newDraweeControllerBuilder() {
        return sDraweeControllerBuilderSupplier.get();
    }

    public static ImagePipelineFactory getImagePipelineFactory() {
        return ImagePipelineFactory.getInstance();
    }

    public static ImagePipeline getImagePipeline() {
        return getImagePipelineFactory().getImagePipeline();
    }

    public static void shutDown() {
        sDraweeControllerBuilderSupplier = null;
        SimpleDraweeView.shutDown();
        ImagePipelineFactory.shutDown();
    }

    public static boolean hasBeenInitialized() {
        return sIsInitialized;
    }
}
