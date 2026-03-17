package com.facebook.react.uimanager.events;

import android.view.MotionEvent;
import androidx.core.app.NotificationCompat;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.systrace.Systrace;
import java.util.Iterator;
import kotlin.Deprecated;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.ArrayIteratorKt;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: TouchesHelper.kt */
@Metadata(d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u000b\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u001d\u0010\u0010\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00120\u00112\u0006\u0010\u0013\u001a\u00020\u0014H\u0002¢\u0006\u0002\u0010\u0015J\u0018\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u0014H\u0007J\u0018\u0010\u001b\u001a\u00020\u00172\u0006\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u0013\u001a\u00020\u0014H\u0007J%\u0010\u001e\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020!2\u000e\u0010\"\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00120\u0011H\u0002¢\u0006\u0002\u0010#R\u0016\u0010\u0004\u001a\u00020\u00058\u0006X\u0087D¢\u0006\b\n\u0000\u0012\u0004\b\u0006\u0010\u0003R\u000e\u0010\u0007\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000¨\u0006$"}, d2 = {"Lcom/facebook/react/uimanager/events/TouchesHelper;", "", "<init>", "()V", "TARGET_KEY", "", "getTARGET_KEY$annotations", "TARGET_SURFACE_KEY", "CHANGED_TOUCHES_KEY", "TOUCHES_KEY", "PAGE_X_KEY", "PAGE_Y_KEY", "TIMESTAMP_KEY", "POINTER_IDENTIFIER_KEY", "LOCATION_X_KEY", "LOCATION_Y_KEY", "createPointersArray", "", "Lcom/facebook/react/bridge/WritableMap;", NotificationCompat.CATEGORY_EVENT, "Lcom/facebook/react/uimanager/events/TouchEvent;", "(Lcom/facebook/react/uimanager/events/TouchEvent;)[Lcom/facebook/react/bridge/WritableMap;", "sendTouchesLegacy", "", "rctEventEmitter", "Lcom/facebook/react/uimanager/events/RCTEventEmitter;", "touchEvent", "sendTouchEvent", "eventEmitter", "Lcom/facebook/react/uimanager/events/RCTModernEventEmitter;", "getWritableArray", "Lcom/facebook/react/bridge/WritableArray;", "copyObjects", "", "objects", "(Z[Lcom/facebook/react/bridge/WritableMap;)Lcom/facebook/react/bridge/WritableArray;", "ReactAndroid_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes.dex */
public final class TouchesHelper {
    private static final String CHANGED_TOUCHES_KEY = "changedTouches";
    private static final String LOCATION_X_KEY = "locationX";
    private static final String LOCATION_Y_KEY = "locationY";
    private static final String PAGE_X_KEY = "pageX";
    private static final String PAGE_Y_KEY = "pageY";
    private static final String POINTER_IDENTIFIER_KEY = "identifier";
    private static final String TARGET_SURFACE_KEY = "targetSurface";
    private static final String TIMESTAMP_KEY = "timestamp";
    private static final String TOUCHES_KEY = "touches";
    public static final TouchesHelper INSTANCE = new TouchesHelper();
    public static final String TARGET_KEY = "target";

    /* compiled from: TouchesHelper.kt */
    @Metadata(k = 3, mv = {2, 0, 0}, xi = 48)
    /* loaded from: classes.dex */
    public /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[TouchEventType.values().length];
            try {
                iArr[TouchEventType.START.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                iArr[TouchEventType.END.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                iArr[TouchEventType.MOVE.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                iArr[TouchEventType.CANCEL.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            $EnumSwitchMapping$0 = iArr;
        }
    }

    @Deprecated(message = "Not used in New Architecture")
    public static /* synthetic */ void getTARGET_KEY$annotations() {
    }

    private TouchesHelper() {
    }

    private final WritableMap[] createPointersArray(TouchEvent event) {
        MotionEvent motionEvent = event.getMotionEvent();
        WritableMap[] writableMapArr = new WritableMap[motionEvent.getPointerCount()];
        float x = motionEvent.getX() - event.getViewX();
        float y = motionEvent.getY() - event.getViewY();
        int pointerCount = motionEvent.getPointerCount();
        for (int i = 0; i < pointerCount; i++) {
            WritableMap createMap = Arguments.createMap();
            createMap.putDouble(PAGE_X_KEY, PixelUtil.INSTANCE.pxToDp(motionEvent.getX(i)));
            createMap.putDouble(PAGE_Y_KEY, PixelUtil.INSTANCE.pxToDp(motionEvent.getY(i)));
            float x2 = motionEvent.getX(i) - x;
            float y2 = motionEvent.getY(i) - y;
            createMap.putDouble(LOCATION_X_KEY, PixelUtil.INSTANCE.pxToDp(x2));
            createMap.putDouble(LOCATION_Y_KEY, PixelUtil.INSTANCE.pxToDp(y2));
            createMap.putInt(TARGET_SURFACE_KEY, event.getSurfaceId());
            createMap.putInt(TARGET_KEY, event.getViewTag());
            createMap.putDouble(TIMESTAMP_KEY, event.getTimestampMs());
            createMap.putDouble(POINTER_IDENTIFIER_KEY, motionEvent.getPointerId(i));
            writableMapArr[i] = createMap;
        }
        return writableMapArr;
    }

    @JvmStatic
    public static final void sendTouchesLegacy(RCTEventEmitter rctEventEmitter, TouchEvent touchEvent) {
        Intrinsics.checkNotNullParameter(rctEventEmitter, "rctEventEmitter");
        Intrinsics.checkNotNullParameter(touchEvent, "touchEvent");
        TouchEventType touchEventType = touchEvent.getTouchEventType();
        TouchesHelper touchesHelper = INSTANCE;
        WritableArray writableArray = touchesHelper.getWritableArray(false, touchesHelper.createPointersArray(touchEvent));
        MotionEvent motionEvent = touchEvent.getMotionEvent();
        WritableArray createArray = Arguments.createArray();
        if (touchEventType == TouchEventType.MOVE || touchEventType == TouchEventType.CANCEL) {
            int pointerCount = motionEvent.getPointerCount();
            for (int i = 0; i < pointerCount; i++) {
                createArray.pushInt(i);
            }
        } else if (touchEventType == TouchEventType.START || touchEventType == TouchEventType.END) {
            createArray.pushInt(motionEvent.getActionIndex());
        } else {
            throw new RuntimeException("Unknown touch type: " + touchEventType);
        }
        String jSEventName = TouchEventType.INSTANCE.getJSEventName(touchEventType);
        Intrinsics.checkNotNull(createArray);
        rctEventEmitter.receiveTouches(jSEventName, writableArray, createArray);
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x00a5 A[Catch: all -> 0x00ec, TryCatch #0 {all -> 0x00ec, blocks: (B:3:0x002c, B:11:0x0050, B:12:0x0098, B:13:0x009f, B:15:0x00a5, B:17:0x00ad, B:19:0x00cd, B:26:0x0055, B:27:0x005a, B:28:0x005b, B:29:0x0061, B:31:0x0067, B:33:0x006b, B:35:0x0071, B:40:0x0076, B:41:0x0084, B:43:0x008e, B:44:0x0094), top: B:2:0x002c }] */
    @JvmStatic
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static final void sendTouchEvent(RCTModernEventEmitter eventEmitter, TouchEvent event) {
        WritableMap[] writableMapArr;
        WritableMap[] writableMapArr2;
        Iterator it;
        WritableMap writableMap;
        Intrinsics.checkNotNullParameter(eventEmitter, "eventEmitter");
        Intrinsics.checkNotNullParameter(event, "event");
        Systrace.beginSection(0L, "TouchesHelper.sentTouchEventModern(" + event.getEventName() + ")");
        try {
            TouchEventType touchEventType = event.getTouchEventType();
            MotionEvent motionEvent = event.getMotionEvent();
            WritableMap[] createPointersArray = INSTANCE.createPointersArray(event);
            int i = WhenMappings.$EnumSwitchMapping$0[touchEventType.ordinal()];
            if (i == 1) {
                int actionIndex = motionEvent.getActionIndex();
                WritableMap[] writableMapArr3 = new WritableMap[1];
                WritableMap writableMap2 = createPointersArray[actionIndex];
                writableMapArr3[0] = writableMap2 != null ? writableMap2.copy() : null;
                writableMapArr = writableMapArr3;
            } else if (i == 2) {
                int actionIndex2 = motionEvent.getActionIndex();
                WritableMap writableMap3 = createPointersArray[actionIndex2];
                createPointersArray[actionIndex2] = null;
                writableMapArr = new WritableMap[]{writableMap3};
            } else {
                if (i != 3) {
                    if (i != 4) {
                        throw new NoWhenBranchMatchedException();
                    }
                    writableMapArr2 = new WritableMap[0];
                    writableMapArr = createPointersArray;
                    it = ArrayIteratorKt.iterator(writableMapArr);
                    while (it.hasNext()) {
                        WritableMap writableMap4 = (WritableMap) it.next();
                        if (writableMap4 != null) {
                            WritableMap copy = writableMap4.copy();
                            TouchesHelper touchesHelper = INSTANCE;
                            WritableArray writableArray = touchesHelper.getWritableArray(true, writableMapArr);
                            WritableArray writableArray2 = touchesHelper.getWritableArray(true, writableMapArr2);
                            copy.putArray(CHANGED_TOUCHES_KEY, writableArray);
                            copy.putArray(TOUCHES_KEY, writableArray2);
                            writableMap = copy;
                        } else {
                            writableMap = null;
                        }
                        eventEmitter.receiveEvent(event.getSurfaceId(), event.getViewTag(), event.getEventName(), event.canCoalesce(), 0, writableMap, event.getEventCategory());
                    }
                }
                writableMapArr = new WritableMap[createPointersArray.length];
                for (int i2 = 0; i2 < createPointersArray.length; i2++) {
                    WritableMap writableMap5 = createPointersArray[i2];
                    writableMapArr[i2] = writableMap5 != null ? writableMap5.copy() : null;
                }
            }
            writableMapArr2 = createPointersArray;
            it = ArrayIteratorKt.iterator(writableMapArr);
            while (it.hasNext()) {
            }
        } finally {
            Systrace.endSection(0L);
        }
    }

    private final WritableArray getWritableArray(boolean copyObjects, WritableMap[] objects) {
        WritableArray createArray = Arguments.createArray();
        for (WritableMap writableMap : objects) {
            if (writableMap != null) {
                if (copyObjects) {
                    writableMap = writableMap.copy();
                }
                createArray.pushMap(writableMap);
            }
        }
        Intrinsics.checkNotNull(createArray);
        return createArray;
    }
}
