package com.facebook.react.devsupport;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.facebook.react.devsupport.interfaces.DevSupportManager;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: RedBoxDialogSurfaceDelegate.kt */
@Metadata(d1 = {"\u0000)\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000*\u0001\u0000\b\n\u0018\u00002\u00020\u0001J\u0018\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016J\u0012\u0010\b\u001a\u00020\t2\b\u0010\n\u001a\u0004\u0018\u00010\u000bH\u0014¨\u0006\f"}, d2 = {"com/facebook/react/devsupport/RedBoxDialogSurfaceDelegate$show$2", "Landroid/app/Dialog;", "onKeyUp", "", "keyCode", "", NotificationCompat.CATEGORY_EVENT, "Landroid/view/KeyEvent;", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "ReactAndroid_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes.dex */
public final class RedBoxDialogSurfaceDelegate$show$2 extends Dialog {
    final /* synthetic */ RedBoxDialogSurfaceDelegate this$0;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public RedBoxDialogSurfaceDelegate$show$2(Activity activity, RedBoxDialogSurfaceDelegate redBoxDialogSurfaceDelegate, int i) {
        super(activity, i);
        this.this$0 = redBoxDialogSurfaceDelegate;
    }

    @Override // android.app.Dialog, android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        DoubleTapReloadRecognizer doubleTapReloadRecognizer;
        DevSupportManager devSupportManager;
        DevSupportManager devSupportManager2;
        Intrinsics.checkNotNullParameter(event, "event");
        if (keyCode == 82) {
            devSupportManager2 = this.this$0.devSupportManager;
            devSupportManager2.showDevOptionsDialog();
            return true;
        }
        doubleTapReloadRecognizer = this.this$0.doubleTapReloadRecognizer;
        if (doubleTapReloadRecognizer.didDoubleTapR(keyCode, getCurrentFocus())) {
            devSupportManager = this.this$0.devSupportManager;
            devSupportManager.handleReloadJS();
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override // android.app.Dialog
    protected void onCreate(Bundle savedInstanceState) {
        RedBoxContentView redBoxContentView;
        Window window = getWindow();
        if (window == null) {
            throw new IllegalStateException("Required value was null.".toString());
        }
        window.setBackgroundDrawable(new ColorDrawable(ViewCompat.MEASURED_STATE_MASK));
        final int systemBars = WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout();
        redBoxContentView = this.this$0.redBoxContentView;
        if (redBoxContentView == null) {
            throw new IllegalStateException("Required value was null.".toString());
        }
        ViewCompat.setOnApplyWindowInsetsListener(redBoxContentView, new OnApplyWindowInsetsListener() { // from class: com.facebook.react.devsupport.RedBoxDialogSurfaceDelegate$show$2$$ExternalSyntheticLambda0
            @Override // androidx.core.view.OnApplyWindowInsetsListener
            public final WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat) {
                WindowInsetsCompat onCreate$lambda$0;
                onCreate$lambda$0 = RedBoxDialogSurfaceDelegate$show$2.onCreate$lambda$0(systemBars, view, windowInsetsCompat);
                return onCreate$lambda$0;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final WindowInsetsCompat onCreate$lambda$0(int i, View view, WindowInsetsCompat windowInsetsCompat) {
        Intrinsics.checkNotNullParameter(view, "view");
        Intrinsics.checkNotNullParameter(windowInsetsCompat, "windowInsetsCompat");
        Insets insets = windowInsetsCompat.getInsets(i);
        Intrinsics.checkNotNullExpressionValue(insets, "getInsets(...)");
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        Intrinsics.checkNotNull(layoutParams, "null cannot be cast to non-null type android.widget.FrameLayout.LayoutParams");
        ((FrameLayout.LayoutParams) layoutParams).setMargins(insets.left, insets.top, insets.right, insets.bottom);
        return WindowInsetsCompat.CONSUMED;
    }
}
