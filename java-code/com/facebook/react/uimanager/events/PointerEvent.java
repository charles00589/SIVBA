package com.facebook.react.uimanager.events;

import android.view.MotionEvent;
import androidx.core.util.Pools;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactSoftExceptionLogger;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.TouchTargetHelper;
import com.facebook.react.uimanager.events.Event;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
public class PointerEvent extends Event<PointerEvent> {
    private static final Pools.SynchronizedPool<PointerEvent> EVENTS_POOL = new Pools.SynchronizedPool<>(6);
    private static final int POINTER_EVENTS_POOL_SIZE = 6;
    private static final String TAG = "PointerEvent";
    private static final short UNSET_COALESCING_KEY = -1;
    private short mCoalescingKey = -1;
    private Event.EventAnimationDriverMatchSpec mEventAnimationDriverMatchSpec;
    private String mEventName;
    private PointerEventState mEventState;
    private MotionEvent mMotionEvent;
    private List<WritableMap> mPointersEventData;

    public static PointerEvent obtain(String str, int i, PointerEventState pointerEventState, MotionEvent motionEvent) {
        PointerEvent acquire = EVENTS_POOL.acquire();
        if (acquire == null) {
            acquire = new PointerEvent();
        }
        acquire.init(str, i, pointerEventState, (MotionEvent) Assertions.assertNotNull(motionEvent), (short) 0);
        return acquire;
    }

    public static PointerEvent obtain(String str, int i, PointerEventState pointerEventState, MotionEvent motionEvent, short s) {
        PointerEvent acquire = EVENTS_POOL.acquire();
        if (acquire == null) {
            acquire = new PointerEvent();
        }
        acquire.init(str, i, pointerEventState, (MotionEvent) Assertions.assertNotNull(motionEvent), s);
        return acquire;
    }

    private void init(String str, int i, PointerEventState pointerEventState, MotionEvent motionEvent, short s) {
        super.init(pointerEventState.getSurfaceId(), i, motionEvent.getEventTime());
        this.mEventName = str;
        this.mMotionEvent = MotionEvent.obtain(motionEvent);
        this.mCoalescingKey = s;
        this.mEventState = pointerEventState;
    }

    private PointerEvent() {
    }

    @Override // com.facebook.react.uimanager.events.Event
    public String getEventName() {
        return this.mEventName;
    }

    private boolean isClickEvent() {
        return this.mEventName.equals(PointerEventHelper.CLICK);
    }

    @Override // com.facebook.react.uimanager.events.Event
    public void dispatch(RCTEventEmitter rCTEventEmitter) {
        if (this.mMotionEvent == null) {
            ReactSoftExceptionLogger.logSoftException(TAG, new IllegalStateException("Cannot dispatch a Pointer that has no MotionEvent; the PointerEvehas been recycled"));
            return;
        }
        if (this.mPointersEventData == null) {
            this.mPointersEventData = createPointersEventData();
        }
        List<WritableMap> list = this.mPointersEventData;
        if (list == null) {
            return;
        }
        boolean z = list.size() > 1;
        for (WritableMap writableMap : this.mPointersEventData) {
            if (z) {
                writableMap = writableMap.copy();
            }
            rCTEventEmitter.receiveEvent(getViewTag(), this.mEventName, writableMap);
        }
    }

    @Override // com.facebook.react.uimanager.events.Event
    public Event.EventAnimationDriverMatchSpec getEventAnimationDriverMatchSpec() {
        if (this.mEventAnimationDriverMatchSpec == null) {
            this.mEventAnimationDriverMatchSpec = new Event.EventAnimationDriverMatchSpec() { // from class: com.facebook.react.uimanager.events.PointerEvent.1
                @Override // com.facebook.react.uimanager.events.Event.EventAnimationDriverMatchSpec
                public boolean match(int i, String str) {
                    if (!str.equals(PointerEvent.this.mEventName)) {
                        return false;
                    }
                    if (!PointerEventHelper.isBubblingEvent(str)) {
                        return PointerEvent.this.getViewTag() == i;
                    }
                    Iterator<TouchTargetHelper.ViewTarget> it = PointerEvent.this.mEventState.getHitPathForActivePointer().iterator();
                    while (it.hasNext()) {
                        if (it.next().getViewId() == i) {
                            return true;
                        }
                    }
                    return false;
                }
            };
        }
        return this.mEventAnimationDriverMatchSpec;
    }

    @Override // com.facebook.react.uimanager.events.Event
    public void onDispose() {
        this.mPointersEventData = null;
        MotionEvent motionEvent = this.mMotionEvent;
        this.mMotionEvent = null;
        if (motionEvent != null) {
            motionEvent.recycle();
        }
        try {
            EVENTS_POOL.release(this);
        } catch (IllegalStateException e) {
            ReactSoftExceptionLogger.logSoftException(TAG, e);
        }
    }

    private List<WritableMap> createW3CPointerEvents() {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < this.mMotionEvent.getPointerCount(); i++) {
            arrayList.add(createW3CPointerEvent(i));
        }
        return arrayList;
    }

    private void addModifierKeyData(WritableMap writableMap, int i) {
        writableMap.putBoolean("ctrlKey", (i & 4096) != 0);
        writableMap.putBoolean("shiftKey", (i & 1) != 0);
        writableMap.putBoolean("altKey", (i & 2) != 0);
        writableMap.putBoolean("metaKey", (i & 65536) != 0);
    }

    private WritableMap createW3CPointerEvent(int i) {
        WritableMap createMap = Arguments.createMap();
        int pointerId = this.mMotionEvent.getPointerId(i);
        createMap.putDouble("pointerId", pointerId);
        String w3CPointerType = PointerEventHelper.getW3CPointerType(this.mMotionEvent.getToolType(i));
        createMap.putString("pointerType", w3CPointerType);
        createMap.putBoolean("isPrimary", !isClickEvent() && (this.mEventState.supportsHover(pointerId) || pointerId == this.mEventState.mPrimaryPointerId));
        float[] fArr = this.mEventState.getEventCoordinatesByPointerId().get(Integer.valueOf(pointerId));
        double dIPFromPixel = PixelUtil.toDIPFromPixel(fArr[0]);
        double dIPFromPixel2 = PixelUtil.toDIPFromPixel(fArr[1]);
        createMap.putDouble("clientX", dIPFromPixel);
        createMap.putDouble("clientY", dIPFromPixel2);
        float[] fArr2 = this.mEventState.getScreenCoordinatesByPointerId().get(Integer.valueOf(pointerId));
        double dIPFromPixel3 = PixelUtil.toDIPFromPixel(fArr2[0]);
        double dIPFromPixel4 = PixelUtil.toDIPFromPixel(fArr2[1]);
        createMap.putDouble("screenX", dIPFromPixel3);
        createMap.putDouble("screenY", dIPFromPixel4);
        createMap.putDouble("x", dIPFromPixel);
        createMap.putDouble("y", dIPFromPixel2);
        createMap.putDouble("pageX", dIPFromPixel);
        createMap.putDouble("pageY", dIPFromPixel2);
        float[] fArr3 = this.mEventState.getOffsetByPointerId().get(Integer.valueOf(pointerId));
        createMap.putDouble("offsetX", PixelUtil.toDIPFromPixel(fArr3[0]));
        createMap.putDouble("offsetY", PixelUtil.toDIPFromPixel(fArr3[1]));
        createMap.putInt("target", getViewTag());
        createMap.putDouble("timestamp", getTimestampMs());
        createMap.putInt("detail", 0);
        createMap.putDouble("tiltX", 0.0d);
        createMap.putDouble("tiltY", 0.0d);
        createMap.putInt("twist", 0);
        if (w3CPointerType.equals(PointerEventHelper.POINTER_TYPE_MOUSE) || isClickEvent()) {
            createMap.putDouble("width", 1.0d);
            createMap.putDouble("height", 1.0d);
        } else {
            double dIPFromPixel5 = PixelUtil.toDIPFromPixel(this.mMotionEvent.getTouchMajor(i));
            createMap.putDouble("width", dIPFromPixel5);
            createMap.putDouble("height", dIPFromPixel5);
        }
        int buttonState = this.mMotionEvent.getButtonState();
        createMap.putInt("button", PointerEventHelper.getButtonChange(w3CPointerType, this.mEventState.getLastButtonState(), buttonState));
        createMap.putInt("buttons", PointerEventHelper.getButtons(this.mEventName, w3CPointerType, buttonState));
        createMap.putDouble("pressure", isClickEvent() ? 0.0d : PointerEventHelper.getPressure(createMap.getInt("buttons"), this.mEventName));
        createMap.putDouble("tangentialPressure", 0.0d);
        addModifierKeyData(createMap, this.mMotionEvent.getMetaState());
        return createMap;
    }

    private List<WritableMap> createPointersEventData() {
        int actionIndex = this.mMotionEvent.getActionIndex();
        String str = this.mEventName;
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -1786514288:
                if (str.equals(PointerEventHelper.POINTER_ENTER)) {
                    c = 0;
                    break;
                }
                break;
            case -1780335505:
                if (str.equals(PointerEventHelper.POINTER_LEAVE)) {
                    c = 1;
                    break;
                }
                break;
            case -1304584214:
                if (str.equals(PointerEventHelper.POINTER_DOWN)) {
                    c = 2;
                    break;
                }
                break;
            case -1304316135:
                if (str.equals(PointerEventHelper.POINTER_MOVE)) {
                    c = 3;
                    break;
                }
                break;
            case -1304250340:
                if (str.equals(PointerEventHelper.POINTER_OVER)) {
                    c = 4;
                    break;
                }
                break;
            case -1065042973:
                if (str.equals(PointerEventHelper.POINTER_UP)) {
                    c = 5;
                    break;
                }
                break;
            case -992108237:
                if (str.equals(PointerEventHelper.CLICK)) {
                    c = 6;
                    break;
                }
                break;
            case 383186882:
                if (str.equals(PointerEventHelper.POINTER_CANCEL)) {
                    c = 7;
                    break;
                }
                break;
            case 1343400710:
                if (str.equals(PointerEventHelper.POINTER_OUT)) {
                    c = '\b';
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
            case 1:
            case 2:
            case 4:
            case 5:
            case 6:
            case '\b':
                return Arrays.asList(createW3CPointerEvent(actionIndex));
            case 3:
            case 7:
                return createW3CPointerEvents();
            default:
                return null;
        }
    }

    @Override // com.facebook.react.uimanager.events.Event
    public short getCoalescingKey() {
        return this.mCoalescingKey;
    }

    @Override // com.facebook.react.uimanager.events.Event
    public void dispatchModern(RCTModernEventEmitter rCTModernEventEmitter) {
        if (this.mMotionEvent == null) {
            ReactSoftExceptionLogger.logSoftException(TAG, new IllegalStateException("Cannot dispatch a Pointer that has no MotionEvent; the PointerEvehas been recycled"));
            return;
        }
        if (this.mPointersEventData == null) {
            this.mPointersEventData = createPointersEventData();
        }
        List<WritableMap> list = this.mPointersEventData;
        if (list == null) {
            return;
        }
        boolean z = list.size() > 1;
        for (WritableMap writableMap : this.mPointersEventData) {
            if (z) {
                writableMap = writableMap.copy();
            }
            WritableMap writableMap2 = writableMap;
            int surfaceId = getSurfaceId();
            int viewTag = getViewTag();
            String str = this.mEventName;
            short s = this.mCoalescingKey;
            rCTModernEventEmitter.receiveEvent(surfaceId, viewTag, str, s != -1, s, writableMap2, PointerEventHelper.getEventCategory(str));
        }
    }

    /* loaded from: classes.dex */
    public static class PointerEventState {
        private int mActivePointerId;
        private Map<Integer, float[]> mEventCoordinatesByPointerId;
        private Map<Integer, List<TouchTargetHelper.ViewTarget>> mHitPathByPointerId;
        private Set<Integer> mHoveringPointerIds;
        private int mLastButtonState;
        private Map<Integer, float[]> mOffsetByPointerId;
        private int mPrimaryPointerId;
        private Map<Integer, float[]> mScreenCoordinatesByPointerId;
        private int mSurfaceId;

        public PointerEventState(int i, int i2, int i3, int i4, Map<Integer, float[]> map, Map<Integer, List<TouchTargetHelper.ViewTarget>> map2, Map<Integer, float[]> map3, Map<Integer, float[]> map4, Set<Integer> set) {
            this.mPrimaryPointerId = i;
            this.mActivePointerId = i2;
            this.mLastButtonState = i3;
            this.mSurfaceId = i4;
            this.mOffsetByPointerId = map;
            this.mHitPathByPointerId = map2;
            this.mEventCoordinatesByPointerId = map3;
            this.mScreenCoordinatesByPointerId = map4;
            this.mHoveringPointerIds = new HashSet(set);
        }

        public int getLastButtonState() {
            return this.mLastButtonState;
        }

        public int getPrimaryPointerId() {
            return this.mPrimaryPointerId;
        }

        public int getSurfaceId() {
            return this.mSurfaceId;
        }

        public int getActivePointerId() {
            return this.mActivePointerId;
        }

        public boolean supportsHover(int i) {
            return this.mHoveringPointerIds.contains(Integer.valueOf(i));
        }

        public Set<Integer> getHoveringPointerIds() {
            return this.mHoveringPointerIds;
        }

        public final Map<Integer, float[]> getOffsetByPointerId() {
            return this.mOffsetByPointerId;
        }

        public final Map<Integer, List<TouchTargetHelper.ViewTarget>> getHitPathByPointerId() {
            return this.mHitPathByPointerId;
        }

        public final Map<Integer, float[]> getEventCoordinatesByPointerId() {
            return this.mEventCoordinatesByPointerId;
        }

        public final Map<Integer, float[]> getScreenCoordinatesByPointerId() {
            return this.mScreenCoordinatesByPointerId;
        }

        public final List<TouchTargetHelper.ViewTarget> getHitPathForActivePointer() {
            return this.mHitPathByPointerId.get(Integer.valueOf(this.mActivePointerId));
        }
    }
}
