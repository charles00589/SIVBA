package com.facebook.react.views.scroll;

import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import androidx.core.app.NotificationCompat;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import com.facebook.react.R;
import com.facebook.react.bridge.AssertionException;
import com.facebook.react.bridge.ReactSoftExceptionLogger;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.ReactAccessibilityDelegate;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ReactScrollViewAccessibilityDelegate.kt */
@Metadata(d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0016J\u0018\u0010\f\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\r\u001a\u00020\u000eH\u0016J\u0018\u0010\u000f\u001a\u00020\u00072\u0006\u0010\u0010\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0002J\u0018\u0010\u0011\u001a\u00020\u00072\u0006\u0010\u0010\u001a\u00020\t2\u0006\u0010\r\u001a\u00020\u000eH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0012"}, d2 = {"Lcom/facebook/react/views/scroll/ReactScrollViewAccessibilityDelegate;", "Landroidx/core/view/AccessibilityDelegateCompat;", "<init>", "()V", "TAG", "", "onInitializeAccessibilityEvent", "", "host", "Landroid/view/View;", NotificationCompat.CATEGORY_EVENT, "Landroid/view/accessibility/AccessibilityEvent;", "onInitializeAccessibilityNodeInfo", "info", "Landroidx/core/view/accessibility/AccessibilityNodeInfoCompat;", "onInitializeAccessibilityEventInternal", "view", "onInitializeAccessibilityNodeInfoInternal", "ReactAndroid_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes.dex */
public final class ReactScrollViewAccessibilityDelegate extends AccessibilityDelegateCompat {
    private final String TAG;

    public ReactScrollViewAccessibilityDelegate() {
        Intrinsics.checkNotNullExpressionValue("ReactScrollViewAccessibilityDelegate", "getSimpleName(...)");
        this.TAG = "ReactScrollViewAccessibilityDelegate";
    }

    @Override // androidx.core.view.AccessibilityDelegateCompat
    public void onInitializeAccessibilityEvent(View host, AccessibilityEvent event) {
        Intrinsics.checkNotNullParameter(host, "host");
        Intrinsics.checkNotNullParameter(event, "event");
        super.onInitializeAccessibilityEvent(host, event);
        if (host instanceof ReactAccessibleScrollView) {
            onInitializeAccessibilityEventInternal(host, event);
            return;
        }
        ReactSoftExceptionLogger.logSoftException(this.TAG, new AssertionException("ReactScrollViewAccessibilityDelegate should only be used with ReactAccessibleScrollView, not with class: " + host.getClass().getSimpleName()));
    }

    @Override // androidx.core.view.AccessibilityDelegateCompat
    public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfoCompat info) {
        Intrinsics.checkNotNullParameter(host, "host");
        Intrinsics.checkNotNullParameter(info, "info");
        super.onInitializeAccessibilityNodeInfo(host, info);
        if (host instanceof ReactAccessibleScrollView) {
            onInitializeAccessibilityNodeInfoInternal(host, info);
            return;
        }
        ReactSoftExceptionLogger.logSoftException(this.TAG, new AssertionException("ReactScrollViewAccessibilityDelegate should only be used with ReactAccessibleScrollView, not with class: " + host.getClass().getSimpleName()));
    }

    /* JADX WARN: Multi-variable type inference failed */
    private final void onInitializeAccessibilityEventInternal(View view, AccessibilityEvent event) {
        Object tag = view.getTag(R.id.accessibility_collection);
        Integer num = null;
        ReadableMap readableMap = tag instanceof ReadableMap ? (ReadableMap) tag : null;
        if (readableMap == null) {
            return;
        }
        event.setItemCount(readableMap.getInt("itemCount"));
        ViewGroup viewGroup = view instanceof ViewGroup ? (ViewGroup) view : null;
        View childAt = viewGroup != null ? viewGroup.getChildAt(0) : null;
        ViewGroup viewGroup2 = childAt instanceof ViewGroup ? (ViewGroup) childAt : null;
        if (viewGroup2 == null) {
            return;
        }
        int childCount = viewGroup2.getChildCount();
        Integer num2 = null;
        for (int i = 0; i < childCount; i++) {
            View childAt2 = viewGroup2.getChildAt(i);
            if (!(view instanceof ReactAccessibleScrollView)) {
                return;
            }
            Intrinsics.checkNotNull(childAt2);
            boolean isPartiallyScrolledInView = ((ReactAccessibleScrollView) view).isPartiallyScrolledInView(childAt2);
            Object tag2 = childAt2.getTag(R.id.accessibility_collection_item);
            Intrinsics.checkNotNull(tag2, "null cannot be cast to non-null type com.facebook.react.bridge.ReadableMap");
            ReadableMap readableMap2 = (ReadableMap) tag2;
            if (!(childAt2 instanceof ViewGroup)) {
                return;
            }
            ((ViewGroup) childAt2).getChildCount();
            if (isPartiallyScrolledInView) {
                if (num == null) {
                    num = Integer.valueOf(readableMap2.getInt("itemIndex"));
                }
                num2 = Integer.valueOf(readableMap2.getInt("itemIndex"));
            }
            if (num != null && num2 != null) {
                event.setFromIndex(num.intValue());
                event.setToIndex(num2.intValue());
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private final void onInitializeAccessibilityNodeInfoInternal(View view, AccessibilityNodeInfoCompat info) {
        ReactAccessibilityDelegate.AccessibilityRole fromViewTag = ReactAccessibilityDelegate.AccessibilityRole.fromViewTag(view);
        if (fromViewTag != null) {
            ReactAccessibilityDelegate.setRole(info, fromViewTag, view.getContext());
        }
        Object tag = view.getTag(R.id.accessibility_collection);
        ReadableMap readableMap = tag instanceof ReadableMap ? (ReadableMap) tag : null;
        if (readableMap != null) {
            info.setCollectionInfo(AccessibilityNodeInfoCompat.CollectionInfoCompat.obtain(readableMap.getInt("rowCount"), readableMap.getInt("columnCount"), readableMap.getBoolean("hierarchical")));
        }
        if (view instanceof ReactAccessibleScrollView) {
            info.setScrollable(((ReactAccessibleScrollView) view).getScrollEnabled());
        }
    }
}
