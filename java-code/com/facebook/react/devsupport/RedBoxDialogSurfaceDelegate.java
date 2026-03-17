package com.facebook.react.devsupport;

import android.app.Activity;
import android.app.Dialog;
import com.facebook.common.logging.FLog;
import com.facebook.fbreact.specs.NativeRedBoxSpec;
import com.facebook.react.R;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.common.ReactConstants;
import com.facebook.react.common.SurfaceDelegate;
import com.facebook.react.devsupport.interfaces.DevSupportManager;
import com.facebook.react.devsupport.interfaces.RedBoxHandler;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: RedBoxDialogSurfaceDelegate.kt */
@Metadata(d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0006\b\u0000\u0018\u0000 \u00162\u00020\u0001:\u0001\u0016B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0010\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fH\u0016J\b\u0010\u0010\u001a\u00020\u0011H\u0016J\b\u0010\u0012\u001a\u00020\rH\u0016J\b\u0010\u0013\u001a\u00020\rH\u0016J\b\u0010\u0014\u001a\u00020\rH\u0016J\b\u0010\u0015\u001a\u00020\u0011H\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0017"}, d2 = {"Lcom/facebook/react/devsupport/RedBoxDialogSurfaceDelegate;", "Lcom/facebook/react/common/SurfaceDelegate;", "devSupportManager", "Lcom/facebook/react/devsupport/interfaces/DevSupportManager;", "<init>", "(Lcom/facebook/react/devsupport/interfaces/DevSupportManager;)V", "doubleTapReloadRecognizer", "Lcom/facebook/react/devsupport/DoubleTapReloadRecognizer;", "dialog", "Landroid/app/Dialog;", "redBoxContentView", "Lcom/facebook/react/devsupport/RedBoxContentView;", "createContentView", "", "appKey", "", "isContentViewReady", "", "destroyContentView", "show", "hide", "isShowing", "Companion", "ReactAndroid_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes.dex */
public final class RedBoxDialogSurfaceDelegate implements SurfaceDelegate {

    /* renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private final DevSupportManager devSupportManager;
    private Dialog dialog;
    private final DoubleTapReloadRecognizer doubleTapReloadRecognizer;
    private RedBoxContentView redBoxContentView;

    public RedBoxDialogSurfaceDelegate(DevSupportManager devSupportManager) {
        Intrinsics.checkNotNullParameter(devSupportManager, "devSupportManager");
        this.devSupportManager = devSupportManager;
        this.doubleTapReloadRecognizer = new DoubleTapReloadRecognizer();
    }

    @Override // com.facebook.react.common.SurfaceDelegate
    public void createContentView(String appKey) {
        Intrinsics.checkNotNullParameter(appKey, "appKey");
        RedBoxHandler redBoxHandler = this.devSupportManager.getRedBoxHandler();
        Activity currentActivity = this.devSupportManager.getCurrentActivity();
        if (currentActivity == null || currentActivity.isFinishing()) {
            String lastErrorTitle = this.devSupportManager.getLastErrorTitle();
            if (lastErrorTitle == null) {
                lastErrorTitle = "N/A";
            }
            FLog.e(ReactConstants.TAG, "Unable to launch redbox because react activity is not available, here is the error that redbox would've displayed: " + lastErrorTitle);
            return;
        }
        RedBoxContentView redBoxContentView = new RedBoxContentView(currentActivity, this.devSupportManager, redBoxHandler);
        redBoxContentView.init();
        this.redBoxContentView = redBoxContentView;
    }

    @Override // com.facebook.react.common.SurfaceDelegate
    public boolean isContentViewReady() {
        return this.redBoxContentView != null;
    }

    @Override // com.facebook.react.common.SurfaceDelegate
    public void destroyContentView() {
        this.redBoxContentView = null;
    }

    @Override // com.facebook.react.common.SurfaceDelegate
    public void show() {
        String lastErrorTitle = this.devSupportManager.getLastErrorTitle();
        Activity currentActivity = this.devSupportManager.getCurrentActivity();
        if (currentActivity == null || currentActivity.isFinishing()) {
            ReactContext currentReactContext = this.devSupportManager.getCurrentReactContext();
            if (currentReactContext == null) {
                if (lastErrorTitle == null) {
                    lastErrorTitle = "N/A";
                }
                FLog.e(ReactConstants.TAG, "Unable to launch redbox because react activity and react context is not available, here is the error that redbox would've displayed: " + lastErrorTitle);
                return;
            }
            INSTANCE.runAfterHostResume(currentReactContext, new Runnable() { // from class: com.facebook.react.devsupport.RedBoxDialogSurfaceDelegate$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    RedBoxDialogSurfaceDelegate.this.show();
                }
            });
            return;
        }
        RedBoxContentView redBoxContentView = this.redBoxContentView;
        if ((redBoxContentView != null ? redBoxContentView.getContext() : null) != currentActivity) {
            createContentView(NativeRedBoxSpec.NAME);
        }
        RedBoxContentView redBoxContentView2 = this.redBoxContentView;
        if (redBoxContentView2 != null) {
            redBoxContentView2.refreshContentView();
        }
        if (this.dialog == null) {
            RedBoxDialogSurfaceDelegate$show$2 redBoxDialogSurfaceDelegate$show$2 = new RedBoxDialogSurfaceDelegate$show$2(currentActivity, this, R.style.Theme_Catalyst_RedBox);
            redBoxDialogSurfaceDelegate$show$2.requestWindowFeature(1);
            RedBoxContentView redBoxContentView3 = this.redBoxContentView;
            if (redBoxContentView3 != null) {
                redBoxDialogSurfaceDelegate$show$2.setContentView(redBoxContentView3);
                this.dialog = redBoxDialogSurfaceDelegate$show$2;
            } else {
                throw new IllegalStateException("Required value was null.".toString());
            }
        }
        Dialog dialog = this.dialog;
        if (dialog != null) {
            dialog.show();
        }
    }

    @Override // com.facebook.react.common.SurfaceDelegate
    public void hide() {
        try {
            Dialog dialog = this.dialog;
            if (dialog != null) {
                dialog.dismiss();
            }
        } catch (IllegalArgumentException e) {
            FLog.e(ReactConstants.TAG, "RedBoxDialogSurfaceDelegate: error while dismissing dialog: ", e);
        }
        destroyContentView();
        this.dialog = null;
    }

    @Override // com.facebook.react.common.SurfaceDelegate
    public boolean isShowing() {
        Dialog dialog = this.dialog;
        return dialog != null && dialog.isShowing();
    }

    /* compiled from: RedBoxDialogSurfaceDelegate.kt */
    @Metadata(d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0002¨\u0006\n"}, d2 = {"Lcom/facebook/react/devsupport/RedBoxDialogSurfaceDelegate$Companion;", "", "<init>", "()V", "runAfterHostResume", "", "reactContext", "Lcom/facebook/react/bridge/ReactContext;", "runnable", "Ljava/lang/Runnable;", "ReactAndroid_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
    /* loaded from: classes.dex */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final void runAfterHostResume(final ReactContext reactContext, final Runnable runnable) {
            reactContext.addLifecycleEventListener(new LifecycleEventListener() { // from class: com.facebook.react.devsupport.RedBoxDialogSurfaceDelegate$Companion$runAfterHostResume$1
                @Override // com.facebook.react.bridge.LifecycleEventListener
                public void onHostDestroy() {
                }

                @Override // com.facebook.react.bridge.LifecycleEventListener
                public void onHostPause() {
                }

                @Override // com.facebook.react.bridge.LifecycleEventListener
                public void onHostResume() {
                    runnable.run();
                    reactContext.removeLifecycleEventListener(this);
                }
            });
        }
    }
}
