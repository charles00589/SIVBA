package com.facebook.react.views.view;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.Dynamic;
import com.facebook.react.bridge.DynamicFromObject;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.common.ReactConstants;
import com.facebook.react.internal.featureflags.ReactNativeFeatureFlags;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.modules.appstate.AppStateModule;
import com.facebook.react.uimanager.BackgroundStyleApplicator;
import com.facebook.react.uimanager.LengthPercentage;
import com.facebook.react.uimanager.LengthPercentageType;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.PointerEvents;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.ViewProps;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.annotations.ReactPropGroup;
import com.facebook.react.uimanager.common.ViewUtil;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.uimanager.style.BackgroundImageLayer;
import com.facebook.react.uimanager.style.BorderRadiusProp;
import com.facebook.react.uimanager.style.BorderStyle;
import com.facebook.react.uimanager.style.LogicalEdge;
import java.util.ArrayList;
import java.util.Map;
import kotlin.Deprecated;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.TuplesKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ReactViewManager.kt */
@Metadata(d1 = {"\u0000\\\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b!\n\u0002\u0010%\n\u0002\b\b\b\u0017\u0018\u0000 N2\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001NB\u0007¢\u0006\u0004\b\u0003\u0010\u0004J\u0018\u0010\u0005\u001a\u00020\u00022\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u0002H\u0014J\u0018\u0010\t\u001a\u00020\n2\u0006\u0010\b\u001a\u00020\u00022\u0006\u0010\u000b\u001a\u00020\fH\u0017J\u0018\u0010\r\u001a\u00020\n2\u0006\u0010\b\u001a\u00020\u00022\u0006\u0010\u000e\u001a\u00020\fH\u0017J\u001a\u0010\u000f\u001a\u00020\n2\u0006\u0010\b\u001a\u00020\u00022\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011H\u0017J\u0018\u0010\u0012\u001a\u00020\n2\u0006\u0010\b\u001a\u00020\u00022\u0006\u0010\u0013\u001a\u00020\u0014H\u0017J\u0018\u0010\u0015\u001a\u00020\n2\u0006\u0010\b\u001a\u00020\u00022\u0006\u0010\u0013\u001a\u00020\u0014H\u0017J\u0018\u0010\u0016\u001a\u00020\n2\u0006\u0010\b\u001a\u00020\u00022\u0006\u0010\u0013\u001a\u00020\u0014H\u0017J\u0018\u0010\u0017\u001a\u00020\n2\u0006\u0010\b\u001a\u00020\u00022\u0006\u0010\u0013\u001a\u00020\u0014H\u0017J\u0018\u0010\u0018\u001a\u00020\n2\u0006\u0010\b\u001a\u00020\u00022\u0006\u0010\u0013\u001a\u00020\u0014H\u0017J \u0010\u0019\u001a\u00020\n2\u0006\u0010\b\u001a\u00020\u00022\u0006\u0010\u001a\u001a\u00020\u00142\u0006\u0010\u001b\u001a\u00020\u001cH\u0017J \u0010\u0019\u001a\u00020\n2\u0006\u0010\b\u001a\u00020\u00022\u0006\u0010\u001a\u001a\u00020\u00142\u0006\u0010\u001d\u001a\u00020\u001eH\u0017J\u001a\u0010\u001f\u001a\u00020\n2\u0006\u0010\b\u001a\u00020\u00022\b\u0010 \u001a\u0004\u0018\u00010!H\u0017J\u0018\u0010\"\u001a\u00020\n2\u0006\u0010\b\u001a\u00020\u00022\u0006\u0010#\u001a\u00020\u001cH\u0017J\u0014\u0010$\u001a\u00020\u0014*\u00020%2\u0006\u0010&\u001a\u00020!H\u0002J\u001a\u0010'\u001a\u00020\n2\u0006\u0010\b\u001a\u00020\u00022\b\u0010(\u001a\u0004\u0018\u00010!H\u0017J\u001a\u0010)\u001a\u00020\n2\u0006\u0010\b\u001a\u00020\u00022\b\u0010*\u001a\u0004\u0018\u00010%H\u0017J\u001a\u0010+\u001a\u00020\n2\u0006\u0010\b\u001a\u00020\u00022\b\u0010,\u001a\u0004\u0018\u00010%H\u0017J\u0018\u0010-\u001a\u00020\n2\u0006\u0010\b\u001a\u00020\u00022\u0006\u0010.\u001a\u00020\fH\u0017J \u0010/\u001a\u00020\n2\u0006\u0010\b\u001a\u00020\u00022\u0006\u0010\u001a\u001a\u00020\u00142\u0006\u00100\u001a\u00020\u001eH\u0017J'\u00101\u001a\u00020\n2\u0006\u0010\b\u001a\u00020\u00022\u0006\u0010\u001a\u001a\u00020\u00142\b\u00102\u001a\u0004\u0018\u00010\u0014H\u0017¢\u0006\u0002\u00103J\u0018\u00104\u001a\u00020\n2\u0006\u0010\b\u001a\u00020\u00022\u0006\u00105\u001a\u00020\fH\u0017J\u0018\u00106\u001a\u00020\n2\u0006\u0010\b\u001a\u00020\u00022\u0006\u00107\u001a\u00020\fH\u0017J\u0018\u00108\u001a\u00020\n2\u0006\u0010\b\u001a\u00020\u00022\u0006\u00109\u001a\u00020\fH\u0017J\u001a\u0010:\u001a\u00020\n2\u0006\u0010\b\u001a\u00020\u00022\b\u0010;\u001a\u0004\u0018\u00010!H\u0017J\u0018\u0010<\u001a\u00020\n2\u0006\u0010\b\u001a\u00020\u00022\u0006\u0010=\u001a\u00020!H\u0017J\u0018\u0010>\u001a\u00020\n2\u0006\u0010\b\u001a\u00020\u00022\u0006\u0010?\u001a\u00020\u001eH\u0016J$\u0010@\u001a\u00020\n2\u0006\u0010\b\u001a\u00020\u00022\b\u0010A\u001a\u0004\u0018\u00010\u00112\b\u0010B\u001a\u0004\u0018\u00010\u0011H\u0014J\b\u0010C\u001a\u00020!H\u0016J\u0010\u0010D\u001a\u00020\u00022\u0006\u0010E\u001a\u00020\u0007H\u0016J\u0014\u0010F\u001a\u000e\u0012\u0004\u0012\u00020!\u0012\u0004\u0012\u00020\u00140GH\u0016J\"\u0010H\u001a\u00020\n2\u0006\u0010I\u001a\u00020\u00022\u0006\u0010J\u001a\u00020\u00142\b\u0010K\u001a\u0004\u0018\u00010\u0011H\u0017J\"\u0010H\u001a\u00020\n2\u0006\u0010I\u001a\u00020\u00022\u0006\u0010J\u001a\u00020!2\b\u0010K\u001a\u0004\u0018\u00010\u0011H\u0016J\u001a\u0010L\u001a\u00020\n2\u0006\u0010I\u001a\u00020\u00022\b\u0010K\u001a\u0004\u0018\u00010\u0011H\u0002J\u001a\u0010M\u001a\u00020\n2\u0006\u0010I\u001a\u00020\u00022\b\u0010K\u001a\u0004\u0018\u00010\u0011H\u0002¨\u0006O"}, d2 = {"Lcom/facebook/react/views/view/ReactViewManager;", "Lcom/facebook/react/views/view/ReactClippingViewManager;", "Lcom/facebook/react/views/view/ReactViewGroup;", "<init>", "()V", "prepareToRecycleView", "reactContext", "Lcom/facebook/react/uimanager/ThemedReactContext;", "view", "setAccessible", "", "accessible", "", "setTVPreferredFocus", "hasTVPreferredFocus", "setBackgroundImage", "backgroundImage", "Lcom/facebook/react/bridge/ReadableArray;", "nextFocusDown", "viewId", "", "nextFocusForward", "nextFocusLeft", "nextFocusRight", "nextFocusUp", "setBorderRadius", "index", "rawBorderRadius", "Lcom/facebook/react/bridge/Dynamic;", ViewProps.BORDER_RADIUS, "", "setBorderStyle", "borderStyle", "", "setHitSlop", "hitSlop", "px", "Lcom/facebook/react/bridge/ReadableMap;", "key", "setPointerEvents", "pointerEventsStr", "setNativeBackground", AppStateModule.APP_STATE_BACKGROUND, "setNativeForeground", "foreground", "setNeedsOffscreenAlphaCompositing", ViewProps.NEEDS_OFFSCREEN_ALPHA_COMPOSITING, "setBorderWidth", "width", "setBorderColor", ViewProps.COLOR, "(Lcom/facebook/react/views/view/ReactViewGroup;ILjava/lang/Integer;)V", "setCollapsable", ViewProps.COLLAPSABLE, "setCollapsableChildren", ViewProps.COLLAPSABLE_CHILDREN, "setFocusable", "focusable", "setOverflow", ViewProps.OVERFLOW, "setBackfaceVisibility", "backfaceVisibility", "setOpacity", ViewProps.OPACITY, "setTransformProperty", "transforms", ViewProps.TRANSFORM_ORIGIN, "getName", "createViewInstance", "context", "getCommandsMap", "", "receiveCommand", "root", "commandId", "args", "handleSetPressed", "handleHotspotUpdate", "Companion", "ReactAndroid_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
@ReactModule(name = "RCTView")
/* loaded from: classes.dex */
public class ReactViewManager extends ReactClippingViewManager<ReactViewGroup> {
    private static final int CMD_HOTSPOT_UPDATE = 1;
    private static final int CMD_SET_PRESSED = 2;
    private static final String HOTSPOT_UPDATE_KEY = "hotspotUpdate";
    public static final String REACT_CLASS = "RCTView";
    private static final int[] SPACING_TYPES = {8, 0, 2, 1, 3, 4, 5, 9, 10, 11};

    /* compiled from: ReactViewManager.kt */
    @Metadata(k = 3, mv = {2, 0, 0}, xi = 48)
    /* loaded from: classes.dex */
    public /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[ReadableType.values().length];
            try {
                iArr[ReadableType.Map.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                iArr[ReadableType.Number.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                iArr[ReadableType.Null.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            $EnumSwitchMapping$0 = iArr;
        }
    }

    @ReactProp(name = ViewProps.COLLAPSABLE)
    public void setCollapsable(ReactViewGroup view, boolean collapsable) {
        Intrinsics.checkNotNullParameter(view, "view");
    }

    @ReactProp(name = ViewProps.COLLAPSABLE_CHILDREN)
    public void setCollapsableChildren(ReactViewGroup view, boolean collapsableChildren) {
        Intrinsics.checkNotNullParameter(view, "view");
    }

    public ReactViewManager() {
        if (ReactNativeFeatureFlags.enableViewRecyclingForView()) {
            setupViewRecycling();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.facebook.react.uimanager.BaseViewManager, com.facebook.react.uimanager.ViewManager
    public ReactViewGroup prepareToRecycleView(ThemedReactContext reactContext, ReactViewGroup view) {
        Intrinsics.checkNotNullParameter(reactContext, "reactContext");
        Intrinsics.checkNotNullParameter(view, "view");
        ReactViewGroup reactViewGroup = (ReactViewGroup) super.prepareToRecycleView(reactContext, (ThemedReactContext) view);
        if (reactViewGroup != null) {
            reactViewGroup.recycleView();
        }
        return view;
    }

    @ReactProp(name = "accessible")
    public void setAccessible(ReactViewGroup view, boolean accessible) {
        Intrinsics.checkNotNullParameter(view, "view");
        view.setFocusable(accessible);
    }

    @ReactProp(name = "hasTVPreferredFocus")
    public void setTVPreferredFocus(ReactViewGroup view, boolean hasTVPreferredFocus) {
        Intrinsics.checkNotNullParameter(view, "view");
        if (hasTVPreferredFocus) {
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.requestFocus();
        }
    }

    @ReactProp(customType = "BackgroundImage", name = ViewProps.BACKGROUND_IMAGE)
    public void setBackgroundImage(ReactViewGroup view, ReadableArray backgroundImage) {
        Intrinsics.checkNotNullParameter(view, "view");
        ReactViewGroup reactViewGroup = view;
        if (ViewUtil.getUIManagerType(reactViewGroup) == 2) {
            if (backgroundImage != null && backgroundImage.size() > 0) {
                ArrayList arrayList = new ArrayList(backgroundImage.size());
                int size = backgroundImage.size();
                for (int i = 0; i < size; i++) {
                    ReadableMap map = backgroundImage.getMap(i);
                    Context context = view.getContext();
                    Intrinsics.checkNotNullExpressionValue(context, "getContext(...)");
                    arrayList.add(new BackgroundImageLayer(map, context));
                }
                BackgroundStyleApplicator.setBackgroundImage(reactViewGroup, arrayList);
                return;
            }
            BackgroundStyleApplicator.setBackgroundImage(reactViewGroup, null);
        }
    }

    @ReactProp(defaultInt = -1, name = "nextFocusDown")
    public void nextFocusDown(ReactViewGroup view, int viewId) {
        Intrinsics.checkNotNullParameter(view, "view");
        view.setNextFocusDownId(viewId);
    }

    @ReactProp(defaultInt = -1, name = "nextFocusForward")
    public void nextFocusForward(ReactViewGroup view, int viewId) {
        Intrinsics.checkNotNullParameter(view, "view");
        view.setNextFocusForwardId(viewId);
    }

    @ReactProp(defaultInt = -1, name = "nextFocusLeft")
    public void nextFocusLeft(ReactViewGroup view, int viewId) {
        Intrinsics.checkNotNullParameter(view, "view");
        view.setNextFocusLeftId(viewId);
    }

    @ReactProp(defaultInt = -1, name = "nextFocusRight")
    public void nextFocusRight(ReactViewGroup view, int viewId) {
        Intrinsics.checkNotNullParameter(view, "view");
        view.setNextFocusRightId(viewId);
    }

    @ReactProp(defaultInt = -1, name = "nextFocusUp")
    public void nextFocusUp(ReactViewGroup view, int viewId) {
        Intrinsics.checkNotNullParameter(view, "view");
        view.setNextFocusUpId(viewId);
    }

    @ReactPropGroup(names = {ViewProps.BORDER_RADIUS, ViewProps.BORDER_TOP_LEFT_RADIUS, ViewProps.BORDER_TOP_RIGHT_RADIUS, ViewProps.BORDER_BOTTOM_RIGHT_RADIUS, ViewProps.BORDER_BOTTOM_LEFT_RADIUS, ViewProps.BORDER_TOP_START_RADIUS, ViewProps.BORDER_TOP_END_RADIUS, ViewProps.BORDER_BOTTOM_START_RADIUS, ViewProps.BORDER_BOTTOM_END_RADIUS, ViewProps.BORDER_END_END_RADIUS, ViewProps.BORDER_END_START_RADIUS, ViewProps.BORDER_START_END_RADIUS, ViewProps.BORDER_START_START_RADIUS})
    public void setBorderRadius(ReactViewGroup view, int index, Dynamic rawBorderRadius) {
        Intrinsics.checkNotNullParameter(view, "view");
        Intrinsics.checkNotNullParameter(rawBorderRadius, "rawBorderRadius");
        LengthPercentage fromDynamic = LengthPercentage.INSTANCE.setFromDynamic(rawBorderRadius);
        ReactViewGroup reactViewGroup = view;
        if (ViewUtil.getUIManagerType(reactViewGroup) != 2 && fromDynamic != null && fromDynamic.getType() == LengthPercentageType.PERCENT) {
            fromDynamic = null;
        }
        BackgroundStyleApplicator.setBorderRadius(reactViewGroup, BorderRadiusProp.values()[index], fromDynamic);
    }

    @Deprecated(message = "Don't use setBorderRadius(view, int, Float) as it was deprecated in React Native 0.75.0.", replaceWith = @ReplaceWith(expression = "setBorderRadius(view, index, DynamicFromObject(borderRadius)", imports = {}))
    public void setBorderRadius(ReactViewGroup view, int index, float borderRadius) {
        Intrinsics.checkNotNullParameter(view, "view");
        setBorderRadius(view, index, new DynamicFromObject(Float.valueOf(borderRadius)));
    }

    @ReactProp(name = "borderStyle")
    public void setBorderStyle(ReactViewGroup view, String borderStyle) {
        Intrinsics.checkNotNullParameter(view, "view");
        BackgroundStyleApplicator.setBorderStyle(view, borderStyle == null ? null : BorderStyle.INSTANCE.fromString(borderStyle));
    }

    @ReactProp(name = "hitSlop")
    public void setHitSlop(ReactViewGroup view, Dynamic hitSlop) {
        Intrinsics.checkNotNullParameter(view, "view");
        Intrinsics.checkNotNullParameter(hitSlop, "hitSlop");
        int i = WhenMappings.$EnumSwitchMapping$0[hitSlop.getType().ordinal()];
        if (i == 1) {
            ReadableMap asMap = hitSlop.asMap();
            view.setHitSlopRect(new Rect(px(asMap, ViewProps.LEFT), px(asMap, ViewProps.TOP), px(asMap, ViewProps.RIGHT), px(asMap, ViewProps.BOTTOM)));
        } else if (i == 2) {
            int dpToPx = (int) PixelUtil.INSTANCE.dpToPx(hitSlop.asDouble());
            view.setHitSlopRect(new Rect(dpToPx, dpToPx, dpToPx, dpToPx));
        } else {
            if (i == 3) {
                view.setHitSlopRect(null);
                return;
            }
            FLog.w(ReactConstants.TAG, "Invalid type for 'hitSlop' value " + hitSlop.getType());
            view.setHitSlopRect(null);
        }
    }

    private final int px(ReadableMap readableMap, String str) {
        if (readableMap.hasKey(str)) {
            return (int) PixelUtil.INSTANCE.dpToPx(readableMap.getDouble(str));
        }
        return 0;
    }

    @ReactProp(name = ViewProps.POINTER_EVENTS)
    public void setPointerEvents(ReactViewGroup view, String pointerEventsStr) {
        Intrinsics.checkNotNullParameter(view, "view");
        view.setPointerEvents(PointerEvents.INSTANCE.parsePointerEvents(pointerEventsStr));
    }

    @ReactProp(name = "nativeBackgroundAndroid")
    public void setNativeBackground(ReactViewGroup view, ReadableMap background) {
        Drawable drawable;
        Intrinsics.checkNotNullParameter(view, "view");
        if (background != null) {
            Context context = view.getContext();
            Intrinsics.checkNotNullExpressionValue(context, "getContext(...)");
            drawable = ReactDrawableHelper.createDrawableFromJSDescription(context, background);
        } else {
            drawable = null;
        }
        BackgroundStyleApplicator.setFeedbackUnderlay(view, drawable);
    }

    @ReactProp(name = "nativeForegroundAndroid")
    public void setNativeForeground(ReactViewGroup view, ReadableMap foreground) {
        Drawable drawable;
        Intrinsics.checkNotNullParameter(view, "view");
        if (foreground != null) {
            Context context = view.getContext();
            Intrinsics.checkNotNullExpressionValue(context, "getContext(...)");
            drawable = ReactDrawableHelper.createDrawableFromJSDescription(context, foreground);
        } else {
            drawable = null;
        }
        view.setForeground(drawable);
    }

    @ReactProp(name = ViewProps.NEEDS_OFFSCREEN_ALPHA_COMPOSITING)
    public void setNeedsOffscreenAlphaCompositing(ReactViewGroup view, boolean needsOffscreenAlphaCompositing) {
        Intrinsics.checkNotNullParameter(view, "view");
        view.setNeedsOffscreenAlphaCompositing(needsOffscreenAlphaCompositing);
    }

    @ReactPropGroup(defaultFloat = Float.NaN, names = {ViewProps.BORDER_WIDTH, ViewProps.BORDER_LEFT_WIDTH, ViewProps.BORDER_RIGHT_WIDTH, ViewProps.BORDER_TOP_WIDTH, ViewProps.BORDER_BOTTOM_WIDTH, ViewProps.BORDER_START_WIDTH, ViewProps.BORDER_END_WIDTH})
    public void setBorderWidth(ReactViewGroup view, int index, float width) {
        Intrinsics.checkNotNullParameter(view, "view");
        BackgroundStyleApplicator.setBorderWidth(view, LogicalEdge.values()[index], Float.valueOf(width));
    }

    @ReactPropGroup(customType = "Color", names = {ViewProps.BORDER_COLOR, ViewProps.BORDER_LEFT_COLOR, ViewProps.BORDER_RIGHT_COLOR, ViewProps.BORDER_TOP_COLOR, ViewProps.BORDER_BOTTOM_COLOR, ViewProps.BORDER_START_COLOR, ViewProps.BORDER_END_COLOR, ViewProps.BORDER_BLOCK_COLOR, ViewProps.BORDER_BLOCK_END_COLOR, ViewProps.BORDER_BLOCK_START_COLOR})
    public void setBorderColor(ReactViewGroup view, int index, Integer color) {
        Intrinsics.checkNotNullParameter(view, "view");
        BackgroundStyleApplicator.setBorderColor(view, LogicalEdge.INSTANCE.fromSpacingType(SPACING_TYPES[index]), color);
    }

    @ReactProp(name = "focusable")
    public void setFocusable(final ReactViewGroup view, boolean focusable) {
        Intrinsics.checkNotNullParameter(view, "view");
        if (focusable) {
            view.setOnClickListener(new View.OnClickListener() { // from class: com.facebook.react.views.view.ReactViewManager$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    ReactViewManager.setFocusable$lambda$2(ReactViewGroup.this, view2);
                }
            });
            view.setFocusable(true);
        } else {
            view.setOnClickListener(null);
            view.setClickable(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void setFocusable$lambda$2(ReactViewGroup reactViewGroup, View view) {
        Context context = reactViewGroup.getContext();
        Intrinsics.checkNotNull(context, "null cannot be cast to non-null type com.facebook.react.bridge.ReactContext");
        EventDispatcher eventDispatcherForReactTag = UIManagerHelper.getEventDispatcherForReactTag((ReactContext) context, reactViewGroup.getId());
        if (eventDispatcherForReactTag != null) {
            eventDispatcherForReactTag.dispatchEvent(new ViewGroupClickEvent(UIManagerHelper.getSurfaceId(reactViewGroup.getContext()), reactViewGroup.getId()));
        }
    }

    @ReactProp(name = ViewProps.OVERFLOW)
    public void setOverflow(ReactViewGroup view, String overflow) {
        Intrinsics.checkNotNullParameter(view, "view");
        view.setOverflow(overflow);
    }

    @ReactProp(name = "backfaceVisibility")
    public void setBackfaceVisibility(ReactViewGroup view, String backfaceVisibility) {
        Intrinsics.checkNotNullParameter(view, "view");
        Intrinsics.checkNotNullParameter(backfaceVisibility, "backfaceVisibility");
        view.setBackfaceVisibility(backfaceVisibility);
    }

    @Override // com.facebook.react.uimanager.BaseViewManager
    public void setOpacity(ReactViewGroup view, float opacity) {
        Intrinsics.checkNotNullParameter(view, "view");
        view.setOpacityIfPossible(opacity);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.facebook.react.uimanager.BaseViewManager
    public void setTransformProperty(ReactViewGroup view, ReadableArray transforms, ReadableArray transformOrigin) {
        Intrinsics.checkNotNullParameter(view, "view");
        super.setTransformProperty((ReactViewManager) view, transforms, transformOrigin);
        view.setBackfaceVisibilityDependantOpacity();
    }

    @Override // com.facebook.react.uimanager.ViewManager, com.facebook.react.bridge.NativeModule
    public String getName() {
        return "RCTView";
    }

    @Override // com.facebook.react.uimanager.ViewManager
    public ReactViewGroup createViewInstance(ThemedReactContext context) {
        Intrinsics.checkNotNullParameter(context, "context");
        return new ReactViewGroup(context);
    }

    @Override // com.facebook.react.uimanager.ViewManager
    public Map<String, Integer> getCommandsMap() {
        return MapsKt.mutableMapOf(TuplesKt.to(HOTSPOT_UPDATE_KEY, 1), TuplesKt.to("setPressed", 2));
    }

    @Override // com.facebook.react.uimanager.ViewManager
    @Deprecated(message = "Use receiveCommand(View, String, ReadableArray)", replaceWith = @ReplaceWith(expression = "receiveCommand(root, commandIdString, args)", imports = {}))
    public void receiveCommand(ReactViewGroup root, int commandId, ReadableArray args) {
        Intrinsics.checkNotNullParameter(root, "root");
        if (commandId == 1) {
            handleHotspotUpdate(root, args);
        } else {
            if (commandId != 2) {
                return;
            }
            handleSetPressed(root, args);
        }
    }

    @Override // com.facebook.react.uimanager.ViewManager
    public void receiveCommand(ReactViewGroup root, String commandId, ReadableArray args) {
        Intrinsics.checkNotNullParameter(root, "root");
        Intrinsics.checkNotNullParameter(commandId, "commandId");
        if (Intrinsics.areEqual(commandId, HOTSPOT_UPDATE_KEY)) {
            handleHotspotUpdate(root, args);
        } else if (Intrinsics.areEqual(commandId, "setPressed")) {
            handleSetPressed(root, args);
        }
    }

    private final void handleSetPressed(ReactViewGroup root, ReadableArray args) {
        if (args == null || args.size() != 1) {
            throw new JSApplicationIllegalArgumentException("Illegal number of arguments for 'setPressed' command");
        }
        root.setPressed(args.getBoolean(0));
    }

    private final void handleHotspotUpdate(ReactViewGroup root, ReadableArray args) {
        if (args == null || args.size() != 2) {
            throw new JSApplicationIllegalArgumentException("Illegal number of arguments for 'updateHotspot' command");
        }
        root.drawableHotspotChanged(PixelUtil.INSTANCE.dpToPx(args.getDouble(0)), PixelUtil.INSTANCE.dpToPx(args.getDouble(1)));
    }
}
