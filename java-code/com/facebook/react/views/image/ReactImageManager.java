package com.facebook.react.views.image;

import android.content.Context;
import android.graphics.PorterDuff;
import com.facebook.common.logging.FLog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.AbstractDraweeControllerBuilder;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.ReactConstants;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.BackgroundStyleApplicator;
import com.facebook.react.uimanager.LengthPercentage;
import com.facebook.react.uimanager.LengthPercentageType;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewProps;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.annotations.ReactPropGroup;
import com.facebook.react.uimanager.style.BorderRadiusProp;
import com.facebook.react.uimanager.style.LogicalEdge;
import java.util.LinkedHashMap;
import java.util.Map;
import kotlin.Deprecated;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.TuplesKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ReactImageManager.kt */
@ReactModule(name = ReactImageManager.REACT_CLASS)
@Metadata(d1 = {"\u0000l\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0000\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\u0007\n\u0002\u0010\b\n\u0002\b\u0018\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010%\n\u0002\b\u0003\b\u0007\u0018\u0000 D2\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001DB=\b\u0007\u0012\u001a\b\u0002\u0010\u0003\u001a\u0014\u0012\u0002\b\u0003\u0012\u0002\b\u0003\u0012\u0002\b\u0003\u0012\u0002\b\u0003\u0018\u00010\u0004\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b¢\u0006\u0004\b\t\u0010\nB-\b\u0017\u0012\u0018\u0010\u0003\u001a\u0014\u0012\u0002\b\u0003\u0012\u0002\b\u0003\u0012\u0002\b\u0003\u0012\u0002\b\u0003\u0018\u00010\u0004\u0012\b\u0010\u000b\u001a\u0004\u0018\u00010\f¢\u0006\u0004\b\t\u0010\rB7\b\u0017\u0012\u0018\u0010\u0003\u001a\u0014\u0012\u0002\b\u0003\u0012\u0002\b\u0003\u0012\u0002\b\u0003\u0012\u0002\b\u0003\u0018\u00010\u0004\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u0012\b\u0010\u000b\u001a\u0004\u0018\u00010\f¢\u0006\u0004\b\t\u0010\u000eJ\u0010\u0010\u000f\u001a\u00020\u00022\u0006\u0010\u0010\u001a\u00020\u0011H\u0016J\b\u0010\u0012\u001a\u00020\u0013H\u0016J\u0018\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00022\u0006\u0010\u0017\u001a\u00020\u0018H\u0007J\u001a\u0010\u0019\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00022\b\u0010\u001a\u001a\u0004\u0018\u00010\u001bH\u0007J\u001a\u0010\u001c\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00022\b\u0010\u001a\u001a\u0004\u0018\u00010\u001bH\u0007J\u0018\u0010\u001d\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00022\u0006\u0010\u001e\u001a\u00020\u001fH\u0007J\u001a\u0010 \u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00022\b\u0010!\u001a\u0004\u0018\u00010\u0013H\u0007J\u001a\u0010\"\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00022\b\u0010#\u001a\u0004\u0018\u00010\u0013H\u0007J\u001a\u0010$\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00022\b\u0010#\u001a\u0004\u0018\u00010\u0013H\u0007J\u001f\u0010%\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00022\b\u0010&\u001a\u0004\u0018\u00010'H\u0007¢\u0006\u0002\u0010(J\u001f\u0010)\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00022\b\u0010*\u001a\u0004\u0018\u00010'H\u0007¢\u0006\u0002\u0010(J\u0018\u0010+\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00022\u0006\u0010,\u001a\u00020\u001fH\u0007J \u0010-\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00022\u0006\u0010.\u001a\u00020'2\u0006\u0010/\u001a\u00020\u001fH\u0007J\u001a\u00100\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00022\b\u00101\u001a\u0004\u0018\u00010\u0013H\u0007J\u001a\u00102\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00022\b\u00103\u001a\u0004\u0018\u00010\u0013H\u0007J\u0018\u00104\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00022\u0006\u00105\u001a\u00020\u001fH\u0007J\u001f\u00106\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00022\b\u00107\u001a\u0004\u0018\u00010'H\u0007¢\u0006\u0002\u0010(J\u0018\u00108\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00022\u0006\u00109\u001a\u00020\u0018H\u0007J\u0018\u0010:\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00022\u0006\u0010;\u001a\u00020'H\u0007J\u0018\u0010<\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00022\u0006\u0010=\u001a\u00020\u0018H\u0007J\u001a\u0010>\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00022\b\u0010?\u001a\u0004\u0018\u00010@H\u0007J\u0014\u0010A\u001a\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u00020\f0BH\u0016J\u0010\u0010C\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0002H\u0014R \u0010\u0003\u001a\u0014\u0012\u0002\b\u0003\u0012\u0002\b\u0003\u0012\u0002\b\u0003\u0012\u0002\b\u0003\u0018\u00010\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u000b\u001a\u0004\u0018\u00010\fX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006E"}, d2 = {"Lcom/facebook/react/views/image/ReactImageManager;", "Lcom/facebook/react/uimanager/SimpleViewManager;", "Lcom/facebook/react/views/image/ReactImageView;", "draweeControllerBuilder", "Lcom/facebook/drawee/controller/AbstractDraweeControllerBuilder;", "globalImageLoadListener", "Lcom/facebook/react/views/image/GlobalImageLoadListener;", "callerContextFactory", "Lcom/facebook/react/views/image/ReactCallerContextFactory;", "<init>", "(Lcom/facebook/drawee/controller/AbstractDraweeControllerBuilder;Lcom/facebook/react/views/image/GlobalImageLoadListener;Lcom/facebook/react/views/image/ReactCallerContextFactory;)V", "callerContext", "", "(Lcom/facebook/drawee/controller/AbstractDraweeControllerBuilder;Ljava/lang/Object;)V", "(Lcom/facebook/drawee/controller/AbstractDraweeControllerBuilder;Lcom/facebook/react/views/image/GlobalImageLoadListener;Ljava/lang/Object;)V", "createViewInstance", "context", "Lcom/facebook/react/uimanager/ThemedReactContext;", "getName", "", "setAccessible", "", "view", "accessible", "", "setSrc", "sources", "Lcom/facebook/react/bridge/ReadableArray;", "setSource", "setBlurRadius", "blurRadius", "", "setInternal_AnalyticsTag", "analyticTag", "setDefaultSource", "source", "setLoadingIndicatorSource", "setBorderColor", ViewProps.BORDER_COLOR, "", "(Lcom/facebook/react/views/image/ReactImageView;Ljava/lang/Integer;)V", "setOverlayColor", "overlayColor", "setBorderWidth", ViewProps.BORDER_WIDTH, "setBorderRadius", "index", ViewProps.BORDER_RADIUS, "setResizeMode", ViewProps.RESIZE_MODE, "setResizeMethod", ViewProps.RESIZE_METHOD, "setResizeMultiplier", "resizeMultiplier", "setTintColor", "tintColor", "setProgressiveRenderingEnabled", ViewProps.ENABLED, "setFadeDuration", "durationMs", "setLoadHandlersRegistered", "shouldNotifyLoadEvents", "setHeaders", "headers", "Lcom/facebook/react/bridge/ReadableMap;", "getExportedCustomDirectEventTypeConstants", "", "onAfterUpdateTransaction", "Companion", "ReactAndroid_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes.dex */
public final class ReactImageManager extends SimpleViewManager<ReactImageView> {
    private static final String ON_ERROR = "onError";
    private static final String ON_LOAD = "onLoad";
    private static final String ON_LOAD_END = "onLoadEnd";
    private static final String ON_LOAD_START = "onLoadStart";
    private static final String ON_PROGRESS = "onProgress";
    public static final String REACT_CLASS = "RCTImageView";
    private static final String REGISTRATION_NAME = "registrationName";
    private Object callerContext;
    private final ReactCallerContextFactory callerContextFactory;
    private final AbstractDraweeControllerBuilder<?, ?, ?, ?> draweeControllerBuilder;
    private final GlobalImageLoadListener globalImageLoadListener;

    public ReactImageManager() {
        this(null, null, null, 7, null);
    }

    public ReactImageManager(AbstractDraweeControllerBuilder<?, ?, ?, ?> abstractDraweeControllerBuilder) {
        this(abstractDraweeControllerBuilder, null, null, 6, null);
    }

    public ReactImageManager(AbstractDraweeControllerBuilder<?, ?, ?, ?> abstractDraweeControllerBuilder, GlobalImageLoadListener globalImageLoadListener) {
        this(abstractDraweeControllerBuilder, globalImageLoadListener, null, 4, null);
    }

    public /* synthetic */ ReactImageManager(AbstractDraweeControllerBuilder abstractDraweeControllerBuilder, GlobalImageLoadListener globalImageLoadListener, ReactCallerContextFactory reactCallerContextFactory, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((AbstractDraweeControllerBuilder<?, ?, ?, ?>) ((i & 1) != 0 ? null : abstractDraweeControllerBuilder), (i & 2) != 0 ? null : globalImageLoadListener, (i & 4) != 0 ? null : reactCallerContextFactory);
    }

    public ReactImageManager(AbstractDraweeControllerBuilder<?, ?, ?, ?> abstractDraweeControllerBuilder, GlobalImageLoadListener globalImageLoadListener, ReactCallerContextFactory reactCallerContextFactory) {
        this.draweeControllerBuilder = abstractDraweeControllerBuilder;
        this.globalImageLoadListener = globalImageLoadListener;
        this.callerContextFactory = reactCallerContextFactory;
    }

    @Deprecated(message = "Use the constructor with ReactCallerContextFactory instead", replaceWith = @ReplaceWith(expression = "ReactImageManager(draweeControllerBuilder, globalImageLoadListener, callerContextFactory)", imports = {}))
    public ReactImageManager(AbstractDraweeControllerBuilder<?, ?, ?, ?> abstractDraweeControllerBuilder, Object obj) {
        this(abstractDraweeControllerBuilder, (GlobalImageLoadListener) null, (ReactCallerContextFactory) null);
        this.callerContext = obj;
    }

    @Deprecated(message = "Use the constructor with ReactCallerContextFactory instead", replaceWith = @ReplaceWith(expression = "ReactImageManager(draweeControllerBuilder, globalImageLoadListener, callerContextFactory)", imports = {}))
    public ReactImageManager(AbstractDraweeControllerBuilder<?, ?, ?, ?> abstractDraweeControllerBuilder, GlobalImageLoadListener globalImageLoadListener, Object obj) {
        this(abstractDraweeControllerBuilder, globalImageLoadListener, (ReactCallerContextFactory) null);
        this.callerContext = obj;
    }

    @Override // com.facebook.react.uimanager.ViewManager
    public ReactImageView createViewInstance(ThemedReactContext context) {
        Intrinsics.checkNotNullParameter(context, "context");
        Object obj = this.callerContext;
        if (obj == null) {
            ReactCallerContextFactory reactCallerContextFactory = this.callerContextFactory;
            obj = reactCallerContextFactory != null ? reactCallerContextFactory.getOrCreateCallerContext(context.getModuleName(), null) : null;
        }
        ThemedReactContext themedReactContext = context;
        PipelineDraweeControllerBuilder pipelineDraweeControllerBuilder = this.draweeControllerBuilder;
        if (pipelineDraweeControllerBuilder == null) {
            pipelineDraweeControllerBuilder = Fresco.newDraweeControllerBuilder();
        }
        Intrinsics.checkNotNull(pipelineDraweeControllerBuilder);
        return new ReactImageView(themedReactContext, pipelineDraweeControllerBuilder, this.globalImageLoadListener, obj);
    }

    @Override // com.facebook.react.uimanager.ViewManager, com.facebook.react.bridge.NativeModule
    public String getName() {
        return REACT_CLASS;
    }

    @ReactProp(name = "accessible")
    public final void setAccessible(ReactImageView view, boolean accessible) {
        Intrinsics.checkNotNullParameter(view, "view");
        view.setFocusable(accessible);
    }

    @ReactProp(name = "src")
    public final void setSrc(ReactImageView view, ReadableArray sources) {
        Intrinsics.checkNotNullParameter(view, "view");
        setSource(view, sources);
    }

    @ReactProp(name = "source")
    public final void setSource(ReactImageView view, ReadableArray sources) {
        Intrinsics.checkNotNullParameter(view, "view");
        view.setSource(sources);
    }

    @ReactProp(name = "blurRadius")
    public final void setBlurRadius(ReactImageView view, float blurRadius) {
        Intrinsics.checkNotNullParameter(view, "view");
        view.setBlurRadius(blurRadius);
    }

    @ReactProp(name = "internal_analyticTag")
    public final void setInternal_AnalyticsTag(ReactImageView view, String analyticTag) {
        Intrinsics.checkNotNullParameter(view, "view");
        ReactCallerContextFactory reactCallerContextFactory = this.callerContextFactory;
        if (reactCallerContextFactory != null) {
            Context context = view.getContext();
            Intrinsics.checkNotNull(context, "null cannot be cast to non-null type com.facebook.react.uimanager.ThemedReactContext");
            view.updateCallerContext(reactCallerContextFactory.getOrCreateCallerContext(((ThemedReactContext) context).getModuleName(), analyticTag));
        }
    }

    @ReactProp(name = "defaultSource")
    public final void setDefaultSource(ReactImageView view, String source) {
        Intrinsics.checkNotNullParameter(view, "view");
        view.setDefaultSource(source);
    }

    @ReactProp(name = "loadingIndicatorSrc")
    public final void setLoadingIndicatorSource(ReactImageView view, String source) {
        Intrinsics.checkNotNullParameter(view, "view");
        view.setLoadingIndicatorSource(source);
    }

    @ReactProp(customType = "Color", name = ViewProps.BORDER_COLOR)
    public final void setBorderColor(ReactImageView view, Integer borderColor) {
        Intrinsics.checkNotNullParameter(view, "view");
        BackgroundStyleApplicator.setBorderColor(view, LogicalEdge.ALL, borderColor);
    }

    @ReactProp(customType = "Color", name = "overlayColor")
    public final void setOverlayColor(ReactImageView view, Integer overlayColor) {
        Intrinsics.checkNotNullParameter(view, "view");
        if (overlayColor == null) {
            view.setOverlayColor(0);
        } else {
            view.setOverlayColor(overlayColor.intValue());
        }
    }

    @ReactProp(name = ViewProps.BORDER_WIDTH)
    public final void setBorderWidth(ReactImageView view, float borderWidth) {
        Intrinsics.checkNotNullParameter(view, "view");
        BackgroundStyleApplicator.setBorderWidth(view, LogicalEdge.ALL, Float.valueOf(borderWidth));
    }

    @ReactPropGroup(defaultFloat = Float.NaN, names = {ViewProps.BORDER_RADIUS, ViewProps.BORDER_TOP_LEFT_RADIUS, ViewProps.BORDER_TOP_RIGHT_RADIUS, ViewProps.BORDER_BOTTOM_RIGHT_RADIUS, ViewProps.BORDER_BOTTOM_LEFT_RADIUS})
    public final void setBorderRadius(ReactImageView view, int index, float borderRadius) {
        Intrinsics.checkNotNullParameter(view, "view");
        BackgroundStyleApplicator.setBorderRadius(view, BorderRadiusProp.values()[index], Float.isNaN(borderRadius) ? null : new LengthPercentage(borderRadius, LengthPercentageType.POINT));
    }

    @ReactProp(name = ViewProps.RESIZE_MODE)
    public final void setResizeMode(ReactImageView view, String resizeMode) {
        Intrinsics.checkNotNullParameter(view, "view");
        view.setScaleType(ImageResizeMode.toScaleType(resizeMode));
        view.setTileMode(ImageResizeMode.toTileMode(resizeMode));
    }

    /* JADX WARN: Code restructure failed: missing block: B:15:0x0034, code lost:
    
        if (r3.equals("auto") == false) goto L22;
     */
    @ReactProp(name = ViewProps.RESIZE_METHOD)
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void setResizeMethod(ReactImageView view, String resizeMethod) {
        Intrinsics.checkNotNullParameter(view, "view");
        if (resizeMethod != null) {
            switch (resizeMethod.hashCode()) {
                case -934437708:
                    if (resizeMethod.equals("resize")) {
                        view.setResizeMethod(ImageResizeMethod.RESIZE);
                        return;
                    }
                    break;
                case 3005871:
                    break;
                case 3387192:
                    if (resizeMethod.equals(ViewProps.NONE)) {
                        view.setResizeMethod(ImageResizeMethod.NONE);
                        return;
                    }
                    break;
                case 109250890:
                    if (resizeMethod.equals("scale")) {
                        view.setResizeMethod(ImageResizeMethod.SCALE);
                        return;
                    }
                    break;
            }
            view.setResizeMethod(ImageResizeMethod.AUTO);
            FLog.w(ReactConstants.TAG, "Invalid resize method: '" + resizeMethod + "'");
            return;
        }
        view.setResizeMethod(ImageResizeMethod.AUTO);
    }

    @ReactProp(name = "resizeMultiplier")
    public final void setResizeMultiplier(ReactImageView view, float resizeMultiplier) {
        Intrinsics.checkNotNullParameter(view, "view");
        if (resizeMultiplier < 0.01f) {
            FLog.w(ReactConstants.TAG, "Invalid resize multiplier: '" + resizeMultiplier + "'");
        }
        view.setResizeMultiplier(resizeMultiplier);
    }

    @ReactProp(customType = "Color", name = "tintColor")
    public final void setTintColor(ReactImageView view, Integer tintColor) {
        Intrinsics.checkNotNullParameter(view, "view");
        if (tintColor == null) {
            view.clearColorFilter();
        } else {
            view.setColorFilter(tintColor.intValue(), PorterDuff.Mode.SRC_IN);
        }
    }

    @ReactProp(name = "progressiveRenderingEnabled")
    public final void setProgressiveRenderingEnabled(ReactImageView view, boolean enabled) {
        Intrinsics.checkNotNullParameter(view, "view");
        view.setProgressiveRenderingEnabled(enabled);
    }

    @ReactProp(name = "fadeDuration")
    public final void setFadeDuration(ReactImageView view, int durationMs) {
        Intrinsics.checkNotNullParameter(view, "view");
        view.setFadeDuration(durationMs);
    }

    @ReactProp(name = "shouldNotifyLoadEvents")
    public final void setLoadHandlersRegistered(ReactImageView view, boolean shouldNotifyLoadEvents) {
        Intrinsics.checkNotNullParameter(view, "view");
        view.setShouldNotifyLoadEvents(shouldNotifyLoadEvents);
    }

    @ReactProp(name = "headers")
    public final void setHeaders(ReactImageView view, ReadableMap headers) {
        Intrinsics.checkNotNullParameter(view, "view");
        if (headers != null) {
            view.setHeaders(headers);
        }
    }

    @Override // com.facebook.react.uimanager.BaseViewManager, com.facebook.react.uimanager.ViewManager
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        LinkedHashMap exportedCustomDirectEventTypeConstants = super.getExportedCustomDirectEventTypeConstants();
        if (exportedCustomDirectEventTypeConstants == null) {
            exportedCustomDirectEventTypeConstants = new LinkedHashMap();
        }
        exportedCustomDirectEventTypeConstants.put(ImageLoadEvent.INSTANCE.eventNameForType(4), MapsKt.mapOf(TuplesKt.to(REGISTRATION_NAME, ON_LOAD_START)));
        exportedCustomDirectEventTypeConstants.put(ImageLoadEvent.INSTANCE.eventNameForType(5), MapsKt.mapOf(TuplesKt.to(REGISTRATION_NAME, ON_PROGRESS)));
        exportedCustomDirectEventTypeConstants.put(ImageLoadEvent.INSTANCE.eventNameForType(2), MapsKt.mapOf(TuplesKt.to(REGISTRATION_NAME, ON_LOAD)));
        exportedCustomDirectEventTypeConstants.put(ImageLoadEvent.INSTANCE.eventNameForType(1), MapsKt.mapOf(TuplesKt.to(REGISTRATION_NAME, ON_ERROR)));
        exportedCustomDirectEventTypeConstants.put(ImageLoadEvent.INSTANCE.eventNameForType(3), MapsKt.mapOf(TuplesKt.to(REGISTRATION_NAME, ON_LOAD_END)));
        return exportedCustomDirectEventTypeConstants;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.facebook.react.uimanager.BaseViewManager, com.facebook.react.uimanager.ViewManager
    public void onAfterUpdateTransaction(ReactImageView view) {
        Intrinsics.checkNotNullParameter(view, "view");
        super.onAfterUpdateTransaction((ReactImageManager) view);
        view.maybeUpdateView();
    }
}
