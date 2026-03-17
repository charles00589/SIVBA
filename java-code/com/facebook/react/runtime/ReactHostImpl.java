package com.facebook.react.runtime;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.facebook.common.logging.FLog;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.MemoryPressureRouter;
import com.facebook.react.ReactHost;
import com.facebook.react.ReactInstanceEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.JSBundleLoader;
import com.facebook.react.bridge.JavaScriptContextHolder;
import com.facebook.react.bridge.MemoryPressureListener;
import com.facebook.react.bridge.NativeArray;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactMarker;
import com.facebook.react.bridge.ReactMarkerConstants;
import com.facebook.react.bridge.ReactNoCrashBridgeNotAllowedSoftException;
import com.facebook.react.bridge.ReactNoCrashSoftException;
import com.facebook.react.bridge.ReactSoftExceptionLogger;
import com.facebook.react.bridge.RuntimeExecutor;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.bridge.queue.QueueThreadExceptionHandler;
import com.facebook.react.bridge.queue.ReactQueueConfiguration;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.common.build.ReactBuildConfig;
import com.facebook.react.devsupport.DefaultDevSupportManagerFactory;
import com.facebook.react.devsupport.DevSupportManagerBase;
import com.facebook.react.devsupport.DevSupportManagerFactory;
import com.facebook.react.devsupport.InspectorFlags;
import com.facebook.react.devsupport.inspector.InspectorNetworkHelper;
import com.facebook.react.devsupport.inspector.InspectorNetworkRequestListener;
import com.facebook.react.devsupport.interfaces.BundleLoadCallback;
import com.facebook.react.devsupport.interfaces.DevSupportManager;
import com.facebook.react.devsupport.interfaces.PackagerStatusCallback;
import com.facebook.react.fabric.ComponentFactory;
import com.facebook.react.fabric.FabricUIManager;
import com.facebook.react.interfaces.TaskInterface;
import com.facebook.react.interfaces.fabric.ReactSurface;
import com.facebook.react.internal.featureflags.ReactNativeFeatureFlags;
import com.facebook.react.modules.appearance.AppearanceModule;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.modules.systeminfo.AndroidInfoHelpers;
import com.facebook.react.runtime.BridgelessAtomicRef;
import com.facebook.react.runtime.ReactHostImpl;
import com.facebook.react.runtime.internal.bolts.Continuation;
import com.facebook.react.runtime.internal.bolts.Task;
import com.facebook.react.runtime.internal.bolts.TaskCompletionSource;
import com.facebook.react.turbomodule.core.interfaces.CallInvokerHolder;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.events.BlackHoleEventDispatcher;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.views.imagehelper.ResourceDrawableIdHelper;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

/* loaded from: classes.dex */
public class ReactHostImpl implements ReactHost {
    private static final int BRIDGELESS_MARKER_INSTANCE_KEY = 1;
    private static final String TAG = "ReactHost";
    private static final AtomicInteger mCounter = new AtomicInteger(0);
    private final AtomicReference<Activity> mActivity;
    private final boolean mAllowPackagerServerAccess;
    private final Set<ReactSurfaceImpl> mAttachedSurfaces;
    private final Executor mBGExecutor;
    private final List<Function0<Unit>> mBeforeDestroyListeners;
    private final BridgelessAtomicRef<BridgelessReactContext> mBridgelessReactContextRef;
    private final BridgelessReactStateTracker mBridgelessReactStateTracker;
    private final ComponentFactory mComponentFactory;
    private final Context mContext;
    private final BridgelessAtomicRef<Task<ReactInstance>> mCreateReactInstanceTaskRef;
    private DefaultHardwareBackBtnHandler mDefaultHardwareBackBtnHandler;
    private Task<Void> mDestroyTask;
    private DevSupportManager mDevSupportManager;
    private volatile boolean mHostInvalidated;
    private final int mId;
    private final AtomicReference<WeakReference<Activity>> mLastUsedActivity;
    private MemoryPressureListener mMemoryPressureListener;
    private final MemoryPressureRouter mMemoryPressureRouter;
    private final ReactHostDelegate mReactHostDelegate;
    private ReactHostInspectorTarget mReactHostInspectorTarget;
    private ReactInstance mReactInstance;
    private final List<ReactInstanceEventListener> mReactInstanceEventListeners;
    private final ReactLifecycleStateManager mReactLifecycleStateManager;
    private Task<ReactInstance> mReloadTask;
    private Task<Void> mStartTask;
    private final Executor mUIExecutor;
    private final boolean mUseDevSupport;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public interface ReactInstanceCalback {
        void then(ReactInstance reactInstance);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public interface ReactInstanceTaskUnwrapper {
        ReactInstance unwrap(Task<ReactInstance> task, String str);
    }

    public ReactHostImpl(Context context, ReactHostDelegate reactHostDelegate, ComponentFactory componentFactory, boolean z, boolean z2) {
        this(context, reactHostDelegate, componentFactory, Executors.newSingleThreadExecutor(), Task.UI_THREAD_EXECUTOR, z, z2);
    }

    public ReactHostImpl(Context context, ReactHostDelegate reactHostDelegate, ComponentFactory componentFactory, Executor executor, Executor executor2, boolean z, boolean z2) {
        this(context, reactHostDelegate, componentFactory, executor, executor2, z, z2, null);
    }

    public ReactHostImpl(Context context, ReactHostDelegate reactHostDelegate, ComponentFactory componentFactory, Executor executor, Executor executor2, boolean z, boolean z2, DevSupportManagerFactory devSupportManagerFactory) {
        this.mAttachedSurfaces = new HashSet();
        this.mCreateReactInstanceTaskRef = new BridgelessAtomicRef<>(Task.forResult(null));
        this.mBridgelessReactContextRef = new BridgelessAtomicRef<>();
        this.mActivity = new AtomicReference<>();
        this.mLastUsedActivity = new AtomicReference<>(new WeakReference(null));
        BridgelessReactStateTracker bridgelessReactStateTracker = new BridgelessReactStateTracker(ReactBuildConfig.DEBUG);
        this.mBridgelessReactStateTracker = bridgelessReactStateTracker;
        this.mReactLifecycleStateManager = new ReactLifecycleStateManager(bridgelessReactStateTracker);
        this.mId = mCounter.getAndIncrement();
        this.mReactInstanceEventListeners = new CopyOnWriteArrayList();
        this.mBeforeDestroyListeners = new CopyOnWriteArrayList();
        this.mHostInvalidated = false;
        this.mStartTask = null;
        this.mReloadTask = null;
        this.mDestroyTask = null;
        this.mContext = context;
        this.mReactHostDelegate = reactHostDelegate;
        this.mComponentFactory = componentFactory;
        this.mBGExecutor = executor;
        this.mUIExecutor = executor2;
        this.mMemoryPressureRouter = new MemoryPressureRouter(context);
        this.mAllowPackagerServerAccess = z;
        this.mUseDevSupport = z2;
        this.mDevSupportManager = (devSupportManagerFactory == null ? new DefaultDevSupportManagerFactory() : devSupportManagerFactory).create(context.getApplicationContext(), new ReactHostImplDevHelper(this), reactHostDelegate.getJsMainModulePath(), true, null, null, 2, null, null, null, null, z2);
    }

    @Override // com.facebook.react.ReactHost
    public LifecycleState getLifecycleState() {
        return this.mReactLifecycleStateManager.getLifecycleState();
    }

    @Override // com.facebook.react.ReactHost
    public TaskInterface<Void> start() {
        return Task.call(new Callable() { // from class: com.facebook.react.runtime.ReactHostImpl$$ExternalSyntheticLambda38
            @Override // java.util.concurrent.Callable
            public final Object call() {
                Task orCreateStartTask;
                orCreateStartTask = ReactHostImpl.this.getOrCreateStartTask();
                return orCreateStartTask;
            }
        }, this.mBGExecutor).continueWithTask(new ReactHostImpl$$ExternalSyntheticLambda11());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TaskInterface<Void> prerenderSurface(final ReactSurfaceImpl reactSurfaceImpl) {
        final String str = "prerenderSurface(surfaceId = " + reactSurfaceImpl.getSurfaceID() + ")";
        log(str, "Schedule");
        attachSurface(reactSurfaceImpl);
        return callAfterGetOrCreateReactInstance(str, new ReactInstanceCalback() { // from class: com.facebook.react.runtime.ReactHostImpl$$ExternalSyntheticLambda39
            @Override // com.facebook.react.runtime.ReactHostImpl.ReactInstanceCalback
            public final void then(ReactInstance reactInstance) {
                ReactHostImpl.this.lambda$prerenderSurface$0(str, reactSurfaceImpl, reactInstance);
            }
        }, this.mBGExecutor);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$prerenderSurface$0(String str, ReactSurfaceImpl reactSurfaceImpl, ReactInstance reactInstance) {
        log(str, "Execute");
        reactInstance.prerenderSurface(reactSurfaceImpl);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TaskInterface<Void> startSurface(final ReactSurfaceImpl reactSurfaceImpl) {
        final String str = "startSurface(surfaceId = " + reactSurfaceImpl.getSurfaceID() + ")";
        log(str, "Schedule");
        attachSurface(reactSurfaceImpl);
        return callAfterGetOrCreateReactInstance(str, new ReactInstanceCalback() { // from class: com.facebook.react.runtime.ReactHostImpl$$ExternalSyntheticLambda2
            @Override // com.facebook.react.runtime.ReactHostImpl.ReactInstanceCalback
            public final void then(ReactInstance reactInstance) {
                ReactHostImpl.this.lambda$startSurface$1(str, reactSurfaceImpl, reactInstance);
            }
        }, this.mBGExecutor);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startSurface$1(String str, ReactSurfaceImpl reactSurfaceImpl, ReactInstance reactInstance) {
        log(str, "Execute");
        reactInstance.startSurface(reactSurfaceImpl);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TaskInterface<Void> stopSurface(final ReactSurfaceImpl reactSurfaceImpl) {
        final String str = "stopSurface(surfaceId = " + reactSurfaceImpl.getSurfaceID() + ")";
        log(str, "Schedule");
        detachSurface(reactSurfaceImpl);
        return callWithExistingReactInstance(str, new ReactInstanceCalback() { // from class: com.facebook.react.runtime.ReactHostImpl$$ExternalSyntheticLambda8
            @Override // com.facebook.react.runtime.ReactHostImpl.ReactInstanceCalback
            public final void then(ReactInstance reactInstance) {
                ReactHostImpl.this.lambda$stopSurface$2(str, reactSurfaceImpl, reactInstance);
            }
        }, this.mBGExecutor).makeVoid();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$stopSurface$2(String str, ReactSurfaceImpl reactSurfaceImpl, ReactInstance reactInstance) {
        log(str, "Execute");
        reactInstance.stopSurface(reactSurfaceImpl);
    }

    @Override // com.facebook.react.ReactHost
    public void onHostResume(Activity activity, DefaultHardwareBackBtnHandler defaultHardwareBackBtnHandler) {
        this.mDefaultHardwareBackBtnHandler = defaultHardwareBackBtnHandler;
        onHostResume(activity);
    }

    @Override // com.facebook.react.ReactHost
    public void onHostResume(Activity activity) {
        log("onHostResume(activity)");
        setCurrentActivity(activity);
        ReactContext currentReactContext = getCurrentReactContext();
        maybeEnableDevSupport(true);
        this.mReactLifecycleStateManager.moveToOnHostResume(currentReactContext, getCurrentActivity());
    }

    @Override // com.facebook.react.ReactHost
    public void onHostLeaveHint(Activity activity) {
        log("onUserLeaveHint(activity)");
        ReactContext currentReactContext = getCurrentReactContext();
        if (currentReactContext != null) {
            currentReactContext.onUserLeaveHint(activity);
        }
    }

    @Override // com.facebook.react.ReactHost
    public void onHostPause(Activity activity) {
        log("onHostPause(activity)");
        ReactContext currentReactContext = getCurrentReactContext();
        Activity currentActivity = getCurrentActivity();
        if (currentActivity != null) {
            Assertions.assertCondition(activity == currentActivity, "Pausing an activity that is not the current activity, this is incorrect! Current activity: " + currentActivity.getClass().getSimpleName() + " Paused activity: " + (activity == null ? "null" : activity.getClass().getSimpleName()));
        }
        maybeEnableDevSupport(false);
        this.mDefaultHardwareBackBtnHandler = null;
        this.mReactLifecycleStateManager.moveToOnHostPause(currentReactContext, currentActivity);
    }

    @Override // com.facebook.react.ReactHost
    public void onHostPause() {
        log("onHostPause()");
        ReactContext currentReactContext = getCurrentReactContext();
        maybeEnableDevSupport(false);
        this.mDefaultHardwareBackBtnHandler = null;
        this.mReactLifecycleStateManager.moveToOnHostPause(currentReactContext, getCurrentActivity());
    }

    @Override // com.facebook.react.ReactHost
    public void onHostDestroy() {
        log("onHostDestroy()");
        maybeEnableDevSupport(false);
        moveToHostDestroy(getCurrentReactContext());
    }

    @Override // com.facebook.react.ReactHost
    public void onHostDestroy(Activity activity) {
        log("onHostDestroy(activity)");
        if (getCurrentActivity() == activity) {
            maybeEnableDevSupport(false);
            moveToHostDestroy(getCurrentReactContext());
        }
    }

    private void maybeEnableDevSupport(boolean z) {
        if (this.mUseDevSupport) {
            this.mDevSupportManager.setDevSupportEnabled(z);
        }
    }

    @Override // com.facebook.react.ReactHost
    public ReactContext getCurrentReactContext() {
        return this.mBridgelessReactContextRef.getNullable();
    }

    @Override // com.facebook.react.ReactHost
    public DevSupportManager getDevSupportManager() {
        return (DevSupportManager) Assertions.assertNotNull(this.mDevSupportManager);
    }

    @Override // com.facebook.react.ReactHost
    public ReactSurface createSurface(Context context, String str, Bundle bundle) {
        ReactSurfaceImpl reactSurfaceImpl = new ReactSurfaceImpl(context, str, bundle);
        ReactSurfaceView reactSurfaceView = new ReactSurfaceView(context, reactSurfaceImpl);
        reactSurfaceView.setShouldLogContentAppeared(true);
        reactSurfaceImpl.attachView(reactSurfaceView);
        reactSurfaceImpl.attach(this);
        return reactSurfaceImpl;
    }

    @Override // com.facebook.react.ReactHost
    public MemoryPressureRouter getMemoryPressureRouter() {
        return this.mMemoryPressureRouter;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isInstanceInitialized() {
        return this.mReactInstance != null;
    }

    @Override // com.facebook.react.ReactHost
    public boolean onBackPressed() {
        DeviceEventManagerModule deviceEventManagerModule;
        UiThreadUtil.assertOnUiThread();
        ReactInstance reactInstance = this.mReactInstance;
        if (reactInstance == null || (deviceEventManagerModule = (DeviceEventManagerModule) reactInstance.getNativeModule(DeviceEventManagerModule.class)) == null) {
            return false;
        }
        deviceEventManagerModule.emitHardwareBackPressed();
        return true;
    }

    @Override // com.facebook.react.ReactHost
    public ReactQueueConfiguration getReactQueueConfiguration() {
        ReactInstance reactInstance = this.mReactInstance;
        if (reactInstance != null) {
            return reactInstance.getReactQueueConfiguration();
        }
        return null;
    }

    @Override // com.facebook.react.ReactHost
    public void addReactInstanceEventListener(ReactInstanceEventListener reactInstanceEventListener) {
        this.mReactInstanceEventListeners.add(reactInstanceEventListener);
    }

    @Override // com.facebook.react.ReactHost
    public void removeReactInstanceEventListener(ReactInstanceEventListener reactInstanceEventListener) {
        this.mReactInstanceEventListeners.remove(reactInstanceEventListener);
    }

    @Override // com.facebook.react.ReactHost
    public TaskInterface<Void> reload(final String str) {
        return Task.call(new Callable() { // from class: com.facebook.react.runtime.ReactHostImpl$$ExternalSyntheticLambda0
            @Override // java.util.concurrent.Callable
            public final Object call() {
                Task lambda$reload$5;
                lambda$reload$5 = ReactHostImpl.this.lambda$reload$5(str);
                return lambda$reload$5;
            }
        }, this.mBGExecutor).continueWithTask(new ReactHostImpl$$ExternalSyntheticLambda11());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Task lambda$reload$5(final String str) throws Exception {
        Task<Void> makeVoid;
        if (this.mDestroyTask != null) {
            log("reload()", "Waiting for destroy to finish, before reloading React Native.");
            makeVoid = this.mDestroyTask.continueWithTask(new Continuation() { // from class: com.facebook.react.runtime.ReactHostImpl$$ExternalSyntheticLambda23
                @Override // com.facebook.react.runtime.internal.bolts.Continuation
                public final Object then(Task task) {
                    Task lambda$reload$3;
                    lambda$reload$3 = ReactHostImpl.this.lambda$reload$3(str, task);
                    return lambda$reload$3;
                }
            }, this.mBGExecutor).makeVoid();
        } else {
            makeVoid = getOrCreateReloadTask(str).makeVoid();
        }
        return makeVoid.continueWithTask(new Continuation() { // from class: com.facebook.react.runtime.ReactHostImpl$$ExternalSyntheticLambda24
            @Override // com.facebook.react.runtime.internal.bolts.Continuation
            public final Object then(Task task) {
                Task lambda$reload$4;
                lambda$reload$4 = ReactHostImpl.this.lambda$reload$4(task);
                return lambda$reload$4;
            }
        }, this.mBGExecutor);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Task lambda$reload$3(String str, Task task) throws Exception {
        return getOrCreateReloadTask(str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Task lambda$reload$4(Task task) throws Exception {
        if (!task.isFaulted()) {
            return task;
        }
        Exception error = task.getError();
        if (this.mUseDevSupport) {
            this.mDevSupportManager.handleException(error);
        } else {
            this.mReactHostDelegate.handleInstanceException(error);
        }
        return getOrCreateDestroyTask("Reload failed", error);
    }

    private void setPausedInDebuggerMessage(String str) {
        if (str == null) {
            this.mDevSupportManager.hidePausedInDebuggerOverlay();
        } else {
            this.mDevSupportManager.showPausedInDebuggerOverlay(str, new DevSupportManager.PausedInDebuggerOverlayCommandListener() { // from class: com.facebook.react.runtime.ReactHostImpl.1
                @Override // com.facebook.react.devsupport.interfaces.DevSupportManager.PausedInDebuggerOverlayCommandListener
                public void onResume() {
                    UiThreadUtil.assertOnUiThread();
                    if (ReactHostImpl.this.mReactHostInspectorTarget != null) {
                        ReactHostImpl.this.mReactHostInspectorTarget.sendDebuggerResumeCommand();
                    }
                }
            });
        }
    }

    private Map<String, String> getHostMetadata() {
        return AndroidInfoHelpers.getInspectorHostMetadata(this.mContext);
    }

    private void loadNetworkResource(String str, InspectorNetworkRequestListener inspectorNetworkRequestListener) {
        InspectorNetworkHelper.loadNetworkResource(str, inspectorNetworkRequestListener);
    }

    @Override // com.facebook.react.ReactHost
    public TaskInterface<Void> destroy(String str, Exception exc, final Function1<? super Boolean, Unit> function1) {
        return ((Task) destroy(str, exc)).continueWith(new Continuation<Void, Void>() { // from class: com.facebook.react.runtime.ReactHostImpl.2
            @Override // com.facebook.react.runtime.internal.bolts.Continuation
            public Void then(Task<Void> task) throws Exception {
                function1.invoke(Boolean.valueOf(task.isCompleted() && !task.isFaulted()));
                return null;
            }
        });
    }

    @Override // com.facebook.react.ReactHost
    public TaskInterface<Void> destroy(final String str, final Exception exc) {
        return Task.call(new Callable() { // from class: com.facebook.react.runtime.ReactHostImpl$$ExternalSyntheticLambda35
            @Override // java.util.concurrent.Callable
            public final Object call() {
                Task lambda$destroy$7;
                lambda$destroy$7 = ReactHostImpl.this.lambda$destroy$7(str, exc);
                return lambda$destroy$7;
            }
        }, this.mBGExecutor).continueWithTask(new ReactHostImpl$$ExternalSyntheticLambda11());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Task lambda$destroy$7(final String str, final Exception exc) throws Exception {
        if (this.mReloadTask != null) {
            log("destroy()", "Reloading React Native. Waiting for reload to finish before destroying React Native.");
            return this.mReloadTask.continueWithTask(new Continuation() { // from class: com.facebook.react.runtime.ReactHostImpl$$ExternalSyntheticLambda40
                @Override // com.facebook.react.runtime.internal.bolts.Continuation
                public final Object then(Task task) {
                    Task lambda$destroy$6;
                    lambda$destroy$6 = ReactHostImpl.this.lambda$destroy$6(str, exc, task);
                    return lambda$destroy$6;
                }
            }, this.mBGExecutor);
        }
        return getOrCreateDestroyTask(str, exc);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Task lambda$destroy$6(String str, Exception exc, Task task) throws Exception {
        return getOrCreateDestroyTask(str, exc);
    }

    private MemoryPressureListener createMemoryPressureListener(ReactInstance reactInstance) {
        final WeakReference weakReference = new WeakReference(reactInstance);
        return new MemoryPressureListener() { // from class: com.facebook.react.runtime.ReactHostImpl$$ExternalSyntheticLambda25
            @Override // com.facebook.react.bridge.MemoryPressureListener
            public final void handleMemoryPressure(int i) {
                ReactHostImpl.this.lambda$createMemoryPressureListener$9(weakReference, i);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createMemoryPressureListener$9(final WeakReference weakReference, final int i) {
        this.mBGExecutor.execute(new Runnable() { // from class: com.facebook.react.runtime.ReactHostImpl$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                ReactHostImpl.lambda$createMemoryPressureListener$8(weakReference, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$createMemoryPressureListener$8(WeakReference weakReference, int i) {
        ReactInstance reactInstance = (ReactInstance) weakReference.get();
        if (reactInstance != null) {
            reactInstance.handleMemoryPressure(i);
        }
    }

    Activity getCurrentActivity() {
        return this.mActivity.get();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Activity getLastUsedActivity() {
        WeakReference<Activity> weakReference = this.mLastUsedActivity.get();
        if (weakReference != null) {
            return weakReference.get();
        }
        return null;
    }

    private void setCurrentActivity(Activity activity) {
        this.mActivity.set(activity);
        if (activity != null) {
            this.mLastUsedActivity.set(new WeakReference<>(activity));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public EventDispatcher getEventDispatcher() {
        ReactInstance reactInstance = this.mReactInstance;
        if (reactInstance == null) {
            return BlackHoleEventDispatcher.get();
        }
        return reactInstance.getEventDispatcher();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public FabricUIManager getUIManager() {
        ReactInstance reactInstance = this.mReactInstance;
        if (reactInstance == null) {
            return null;
        }
        return reactInstance.getUIManager();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public <T extends NativeModule> boolean hasNativeModule(Class<T> cls) {
        ReactInstance reactInstance = this.mReactInstance;
        if (reactInstance != null) {
            return reactInstance.hasNativeModule(cls);
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Collection<NativeModule> getNativeModules() {
        ReactInstance reactInstance = this.mReactInstance;
        if (reactInstance != null) {
            return reactInstance.getNativeModules();
        }
        return new ArrayList();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public <T extends NativeModule> T getNativeModule(Class<T> cls) {
        if (cls == UIManagerModule.class) {
            ReactSoftExceptionLogger.logSoftExceptionVerbose(TAG, new ReactNoCrashBridgeNotAllowedSoftException("getNativeModule(UIManagerModule.class) cannot be called when the bridge is disabled"));
        }
        ReactInstance reactInstance = this.mReactInstance;
        if (reactInstance != null) {
            return (T) reactInstance.getNativeModule(cls);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public NativeModule getNativeModule(String str) {
        ReactInstance reactInstance = this.mReactInstance;
        if (reactInstance != null) {
            return reactInstance.getNativeModule(str);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RuntimeExecutor getRuntimeExecutor() {
        ReactInstance reactInstance = this.mReactInstance;
        if (reactInstance != null) {
            return reactInstance.getBufferedRuntimeExecutor();
        }
        raiseSoftException("getRuntimeExecutor()", "Tried to get runtime executor while instance is not ready");
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CallInvokerHolder getJSCallInvokerHolder() {
        ReactInstance reactInstance = this.mReactInstance;
        if (reactInstance != null) {
            return reactInstance.getJSCallInvokerHolder();
        }
        raiseSoftException("getJSCallInvokerHolder()", "Tried to get JSCallInvokerHolder while instance is not ready");
        return null;
    }

    @Override // com.facebook.react.ReactHost
    public void onActivityResult(Activity activity, int i, int i2, Intent intent) {
        String str = "onActivityResult(activity = \"" + activity + "\", requestCode = \"" + i + "\", resultCode = \"" + i2 + "\", data = \"" + intent + "\")";
        ReactContext currentReactContext = getCurrentReactContext();
        if (currentReactContext != null) {
            currentReactContext.onActivityResult(activity, i, i2, intent);
        } else {
            raiseSoftException(str, "Tried to access onActivityResult while context is not ready");
        }
    }

    @Override // com.facebook.react.ReactHost
    public void onWindowFocusChange(boolean z) {
        String str = "onWindowFocusChange(hasFocus = \"" + z + "\")";
        ReactContext currentReactContext = getCurrentReactContext();
        if (currentReactContext != null) {
            currentReactContext.onWindowFocusChange(z);
        } else {
            raiseSoftException(str, "Tried to access onWindowFocusChange while context is not ready");
        }
    }

    @Override // com.facebook.react.ReactHost
    public void onNewIntent(Intent intent) {
        DeviceEventManagerModule deviceEventManagerModule;
        String str = "onNewIntent(intent = \"" + intent + "\")";
        ReactContext currentReactContext = getCurrentReactContext();
        if (currentReactContext != null) {
            String action = intent.getAction();
            Uri data = intent.getData();
            if (data != null && (("android.intent.action.VIEW".equals(action) || "android.nfc.action.NDEF_DISCOVERED".equals(action)) && (deviceEventManagerModule = (DeviceEventManagerModule) currentReactContext.getNativeModule(DeviceEventManagerModule.class)) != null)) {
                deviceEventManagerModule.emitNewIntentReceived(data);
            }
            currentReactContext.onNewIntent(getCurrentActivity(), intent);
            return;
        }
        raiseSoftException(str, "Tried to access onNewIntent while context is not ready");
    }

    @Override // com.facebook.react.ReactHost
    public void onConfigurationChanged(Context context) {
        AppearanceModule appearanceModule;
        ReactContext currentReactContext = getCurrentReactContext();
        if (currentReactContext == null || (appearanceModule = (AppearanceModule) currentReactContext.getNativeModule(AppearanceModule.class)) == null) {
            return;
        }
        appearanceModule.onConfigurationChanged(context);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public JavaScriptContextHolder getJavaScriptContextHolder() {
        ReactInstance reactInstance = this.mReactInstance;
        if (reactInstance != null) {
            return reactInstance.getJavaScriptContextHolder();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultHardwareBackBtnHandler getDefaultBackButtonHandler() {
        return new DefaultHardwareBackBtnHandler() { // from class: com.facebook.react.runtime.ReactHostImpl$$ExternalSyntheticLambda44
            @Override // com.facebook.react.modules.core.DefaultHardwareBackBtnHandler
            public final void invokeDefaultOnBackPressed() {
                ReactHostImpl.this.lambda$getDefaultBackButtonHandler$10();
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getDefaultBackButtonHandler$10() {
        UiThreadUtil.assertOnUiThread();
        DefaultHardwareBackBtnHandler defaultHardwareBackBtnHandler = this.mDefaultHardwareBackBtnHandler;
        if (defaultHardwareBackBtnHandler != null) {
            defaultHardwareBackBtnHandler.invokeDefaultOnBackPressed();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Task<Boolean> loadBundle(final JSBundleLoader jSBundleLoader) {
        log("loadBundle()", "Schedule");
        return callWithExistingReactInstance("loadBundle()", new ReactInstanceCalback() { // from class: com.facebook.react.runtime.ReactHostImpl$$ExternalSyntheticLambda4
            @Override // com.facebook.react.runtime.ReactHostImpl.ReactInstanceCalback
            public final void then(ReactInstance reactInstance) {
                ReactHostImpl.this.lambda$loadBundle$11(jSBundleLoader, reactInstance);
            }
        }, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadBundle$11(JSBundleLoader jSBundleLoader, ReactInstance reactInstance) {
        log("loadBundle()", "Execute");
        reactInstance.loadJSBundle(jSBundleLoader);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Task<Boolean> registerSegment(final int i, final String str, final Callback callback) {
        final String str2 = "registerSegment(segmentId = \"" + i + "\", path = \"" + str + "\")";
        log(str2, "Schedule");
        return callWithExistingReactInstance(str2, new ReactInstanceCalback() { // from class: com.facebook.react.runtime.ReactHostImpl$$ExternalSyntheticLambda9
            @Override // com.facebook.react.runtime.ReactHostImpl.ReactInstanceCalback
            public final void then(ReactInstance reactInstance) {
                ReactHostImpl.this.lambda$registerSegment$12(str2, i, str, callback, reactInstance);
            }
        }, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$registerSegment$12(String str, int i, String str2, Callback callback, ReactInstance reactInstance) {
        log(str, "Execute");
        reactInstance.registerSegment(i, str2);
        ((Callback) Assertions.assertNotNull(callback)).invoke(new Object[0]);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void handleHostException(Exception exc) {
        String str = "handleHostException(message = \"" + exc.getMessage() + "\")";
        log(str);
        if (this.mUseDevSupport) {
            this.mDevSupportManager.handleException(exc);
        } else {
            this.mReactHostDelegate.handleInstanceException(exc);
        }
        destroy(str, exc);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Task<Boolean> callFunctionOnModule(final String str, final String str2, final NativeArray nativeArray) {
        return callWithExistingReactInstance("callFunctionOnModule(\"" + str + "\", \"" + str2 + "\")", new ReactInstanceCalback() { // from class: com.facebook.react.runtime.ReactHostImpl$$ExternalSyntheticLambda19
            @Override // com.facebook.react.runtime.ReactHostImpl.ReactInstanceCalback
            public final void then(ReactInstance reactInstance) {
                reactInstance.callFunctionOnModule(str, str2, nativeArray);
            }
        }, null);
    }

    void attachSurface(ReactSurfaceImpl reactSurfaceImpl) {
        log("attachSurface(surfaceId = " + reactSurfaceImpl.getSurfaceID() + ")");
        synchronized (this.mAttachedSurfaces) {
            this.mAttachedSurfaces.add(reactSurfaceImpl);
        }
    }

    void detachSurface(ReactSurfaceImpl reactSurfaceImpl) {
        log("detachSurface(surfaceId = " + reactSurfaceImpl.getSurfaceID() + ")");
        synchronized (this.mAttachedSurfaces) {
            this.mAttachedSurfaces.remove(reactSurfaceImpl);
        }
    }

    boolean isSurfaceAttached(ReactSurfaceImpl reactSurfaceImpl) {
        boolean contains;
        synchronized (this.mAttachedSurfaces) {
            contains = this.mAttachedSurfaces.contains(reactSurfaceImpl);
        }
        return contains;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isSurfaceWithModuleNameAttached(String str) {
        synchronized (this.mAttachedSurfaces) {
            Iterator<ReactSurfaceImpl> it = this.mAttachedSurfaces.iterator();
            while (it.hasNext()) {
                if (it.next().getModuleName().equals(str)) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override // com.facebook.react.ReactHost
    public void addBeforeDestroyListener(Function0<Unit> function0) {
        this.mBeforeDestroyListeners.add(function0);
    }

    @Override // com.facebook.react.ReactHost
    public void removeBeforeDestroyListener(Function0<Unit> function0) {
        this.mBeforeDestroyListeners.remove(function0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Task<Void> getOrCreateStartTask() {
        if (this.mStartTask == null) {
            log("getOrCreateStartTask()", "Schedule");
            if (ReactBuildConfig.DEBUG) {
                Assertions.assertCondition(ReactNativeFeatureFlags.enableBridgelessArchitecture(), "enableBridgelessArchitecture FeatureFlag must be set to start ReactNative.");
                Assertions.assertCondition(ReactNativeFeatureFlags.enableFabricRenderer(), "enableFabricRenderer FeatureFlag must be set to start ReactNative.");
                Assertions.assertCondition(ReactNativeFeatureFlags.useTurboModules(), "useTurboModules FeatureFlag must be set to start ReactNative.");
            }
            this.mStartTask = waitThenCallGetOrCreateReactInstanceTask().continueWithTask(new Continuation() { // from class: com.facebook.react.runtime.ReactHostImpl$$ExternalSyntheticLambda26
                @Override // com.facebook.react.runtime.internal.bolts.Continuation
                public final Object then(Task task) {
                    Task lambda$getOrCreateStartTask$15;
                    lambda$getOrCreateStartTask$15 = ReactHostImpl.this.lambda$getOrCreateStartTask$15(task);
                    return lambda$getOrCreateStartTask$15;
                }
            }, this.mBGExecutor);
        }
        return this.mStartTask;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Task lambda$getOrCreateStartTask$15(final Task task) throws Exception {
        if (task.isFaulted()) {
            Exception error = task.getError();
            if (this.mUseDevSupport) {
                this.mDevSupportManager.handleException(error);
            } else {
                this.mReactHostDelegate.handleInstanceException(error);
            }
            return getOrCreateDestroyTask("getOrCreateStartTask() failure: " + task.getError().getMessage(), task.getError()).continueWithTask(new Continuation() { // from class: com.facebook.react.runtime.ReactHostImpl$$ExternalSyntheticLambda32
                @Override // com.facebook.react.runtime.internal.bolts.Continuation
                public final Object then(Task task2) {
                    Task forError;
                    forError = Task.forError(Task.this.getError());
                    return forError;
                }
            }).makeVoid();
        }
        return task.makeVoid();
    }

    private void moveToHostDestroy(ReactContext reactContext) {
        this.mReactLifecycleStateManager.moveToOnHostDestroy(reactContext);
        setCurrentActivity(null);
    }

    private void raiseSoftException(String str, String str2) {
        raiseSoftException(str, str2, null);
    }

    private void raiseSoftException(String str, String str2, Throwable th) {
        String str3 = "raiseSoftException(" + str + ")";
        log(str3, str2);
        ReactSoftExceptionLogger.logSoftException(TAG, new ReactNoCrashSoftException(str3 + ": " + str2, th));
    }

    private Task<Boolean> callWithExistingReactInstance(String str, final ReactInstanceCalback reactInstanceCalback, Executor executor) {
        final String str2 = "callWithExistingReactInstance(" + str + ")";
        if (executor == null) {
            executor = Task.IMMEDIATE_EXECUTOR;
        }
        return this.mCreateReactInstanceTaskRef.get().onSuccess(new Continuation() { // from class: com.facebook.react.runtime.ReactHostImpl$$ExternalSyntheticLambda18
            @Override // com.facebook.react.runtime.internal.bolts.Continuation
            public final Object then(Task task) {
                Boolean lambda$callWithExistingReactInstance$16;
                lambda$callWithExistingReactInstance$16 = ReactHostImpl.this.lambda$callWithExistingReactInstance$16(str2, reactInstanceCalback, task);
                return lambda$callWithExistingReactInstance$16;
            }
        }, executor);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$callWithExistingReactInstance$16(String str, ReactInstanceCalback reactInstanceCalback, Task task) throws Exception {
        ReactInstance reactInstance = (ReactInstance) task.getResult();
        if (reactInstance == null) {
            raiseSoftException(str, "Execute: reactInstance is null. Dropping work.");
            return Boolean.FALSE;
        }
        reactInstanceCalback.then(reactInstance);
        return Boolean.TRUE;
    }

    private Task<Void> callAfterGetOrCreateReactInstance(String str, final ReactInstanceCalback reactInstanceCalback, Executor executor) {
        final String str2 = "callAfterGetOrCreateReactInstance(" + str + ")";
        if (executor == null) {
            executor = Task.IMMEDIATE_EXECUTOR;
        }
        return getOrCreateReactInstance().onSuccess(new Continuation() { // from class: com.facebook.react.runtime.ReactHostImpl$$ExternalSyntheticLambda22
            @Override // com.facebook.react.runtime.internal.bolts.Continuation
            public final Object then(Task task) {
                Object lambda$callAfterGetOrCreateReactInstance$17;
                lambda$callAfterGetOrCreateReactInstance$17 = ReactHostImpl.this.lambda$callAfterGetOrCreateReactInstance$17(str2, reactInstanceCalback, task);
                return lambda$callAfterGetOrCreateReactInstance$17;
            }
        }, executor).continueWith(new Continuation() { // from class: com.facebook.react.runtime.ReactHostImpl$$ExternalSyntheticLambda33
            @Override // com.facebook.react.runtime.internal.bolts.Continuation
            public final Object then(Task task) {
                Void lambda$callAfterGetOrCreateReactInstance$18;
                lambda$callAfterGetOrCreateReactInstance$18 = ReactHostImpl.this.lambda$callAfterGetOrCreateReactInstance$18(task);
                return lambda$callAfterGetOrCreateReactInstance$18;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Object lambda$callAfterGetOrCreateReactInstance$17(String str, ReactInstanceCalback reactInstanceCalback, Task task) throws Exception {
        ReactInstance reactInstance = (ReactInstance) task.getResult();
        if (reactInstance == null) {
            raiseSoftException(str, "Execute: reactInstance is null. Dropping work.");
            return null;
        }
        reactInstanceCalback.then(reactInstance);
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Void lambda$callAfterGetOrCreateReactInstance$18(Task task) throws Exception {
        if (!task.isFaulted()) {
            return null;
        }
        handleHostException(task.getError());
        return null;
    }

    private BridgelessReactContext getOrCreateReactContext() {
        return this.mBridgelessReactContextRef.getOrCreate(new BridgelessAtomicRef.Provider() { // from class: com.facebook.react.runtime.ReactHostImpl$$ExternalSyntheticLambda3
            @Override // com.facebook.react.runtime.BridgelessAtomicRef.Provider
            public final Object get() {
                BridgelessReactContext lambda$getOrCreateReactContext$19;
                lambda$getOrCreateReactContext$19 = ReactHostImpl.this.lambda$getOrCreateReactContext$19();
                return lambda$getOrCreateReactContext$19;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ BridgelessReactContext lambda$getOrCreateReactContext$19() {
        log("getOrCreateReactContext()", "Creating BridgelessReactContext");
        return new BridgelessReactContext(this.mContext, this);
    }

    private Task<ReactInstance> getOrCreateReactInstance() {
        return Task.call(new Callable() { // from class: com.facebook.react.runtime.ReactHostImpl$$ExternalSyntheticLambda7
            @Override // java.util.concurrent.Callable
            public final Object call() {
                Task waitThenCallGetOrCreateReactInstanceTask;
                waitThenCallGetOrCreateReactInstanceTask = ReactHostImpl.this.waitThenCallGetOrCreateReactInstanceTask();
                return waitThenCallGetOrCreateReactInstanceTask;
            }
        }, this.mBGExecutor).continueWithTask(new ReactHostImpl$$ExternalSyntheticLambda11());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Task<ReactInstance> waitThenCallGetOrCreateReactInstanceTask() {
        return waitThenCallGetOrCreateReactInstanceTaskWithRetries(0, 4);
    }

    private Task<ReactInstance> waitThenCallGetOrCreateReactInstanceTaskWithRetries(final int i, final int i2) {
        if (this.mReloadTask != null) {
            log("waitThenCallGetOrCreateReactInstanceTaskWithRetries", "React Native is reloading. Return reload task.");
            return this.mReloadTask;
        }
        if (this.mDestroyTask != null) {
            if (i < i2) {
                log("waitThenCallGetOrCreateReactInstanceTaskWithRetries", "React Native is tearing down.Wait for teardown to finish, before trying again (try count = " + i + ").");
                return this.mDestroyTask.onSuccessTask(new Continuation() { // from class: com.facebook.react.runtime.ReactHostImpl$$ExternalSyntheticLambda10
                    @Override // com.facebook.react.runtime.internal.bolts.Continuation
                    public final Object then(Task task) {
                        Task lambda$waitThenCallGetOrCreateReactInstanceTaskWithRetries$20;
                        lambda$waitThenCallGetOrCreateReactInstanceTaskWithRetries$20 = ReactHostImpl.this.lambda$waitThenCallGetOrCreateReactInstanceTaskWithRetries$20(i, i2, task);
                        return lambda$waitThenCallGetOrCreateReactInstanceTaskWithRetries$20;
                    }
                }, this.mBGExecutor);
            }
            raiseSoftException("waitThenCallGetOrCreateReactInstanceTaskWithRetries", "React Native is tearing down. Not wait for teardown to finish: reached max retries.");
        }
        return getOrCreateReactInstanceTask();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Task lambda$waitThenCallGetOrCreateReactInstanceTaskWithRetries$20(int i, int i2, Task task) throws Exception {
        return waitThenCallGetOrCreateReactInstanceTaskWithRetries(i + 1, i2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class CreationResult {
        final ReactContext mContext;
        final ReactInstance mInstance;
        final boolean mIsReloading;

        private CreationResult(ReactInstance reactInstance, ReactContext reactContext, boolean z) {
            this.mInstance = reactInstance;
            this.mContext = reactContext;
            this.mIsReloading = z;
        }
    }

    private Task<ReactInstance> getOrCreateReactInstanceTask() {
        log("getOrCreateReactInstanceTask()");
        return this.mCreateReactInstanceTaskRef.getOrCreate(new BridgelessAtomicRef.Provider() { // from class: com.facebook.react.runtime.ReactHostImpl$$ExternalSyntheticLambda21
            @Override // com.facebook.react.runtime.BridgelessAtomicRef.Provider
            public final Object get() {
                Task lambda$getOrCreateReactInstanceTask$25;
                lambda$getOrCreateReactInstanceTask$25 = ReactHostImpl.this.lambda$getOrCreateReactInstanceTask$25();
                return lambda$getOrCreateReactInstanceTask$25;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Task lambda$getOrCreateReactInstanceTask$25() {
        log("getOrCreateReactInstanceTask()", "Start");
        Assertions.assertCondition(!this.mHostInvalidated, "Cannot start a new ReactInstance on an invalidated ReactHost");
        ReactMarker.logMarker(ReactMarkerConstants.REACT_BRIDGELESS_LOADING_START, 1);
        Task<TContinuationResult> onSuccess = getJsBundleLoader().onSuccess(new Continuation() { // from class: com.facebook.react.runtime.ReactHostImpl$$ExternalSyntheticLambda41
            @Override // com.facebook.react.runtime.internal.bolts.Continuation
            public final Object then(Task task) {
                ReactHostImpl.CreationResult lambda$getOrCreateReactInstanceTask$22;
                lambda$getOrCreateReactInstanceTask$22 = ReactHostImpl.this.lambda$getOrCreateReactInstanceTask$22(task);
                return lambda$getOrCreateReactInstanceTask$22;
            }
        }, this.mBGExecutor);
        onSuccess.onSuccess(new Continuation() { // from class: com.facebook.react.runtime.ReactHostImpl$$ExternalSyntheticLambda42
            @Override // com.facebook.react.runtime.internal.bolts.Continuation
            public final Object then(Task task) {
                ReactInstance lambda$getOrCreateReactInstanceTask$23;
                lambda$getOrCreateReactInstanceTask$23 = ReactHostImpl.this.lambda$getOrCreateReactInstanceTask$23(task);
                return lambda$getOrCreateReactInstanceTask$23;
            }
        }, this.mUIExecutor);
        return onSuccess.onSuccess(new Continuation() { // from class: com.facebook.react.runtime.ReactHostImpl$$ExternalSyntheticLambda43
            @Override // com.facebook.react.runtime.internal.bolts.Continuation
            public final Object then(Task task) {
                ReactInstance reactInstance;
                reactInstance = ((ReactHostImpl.CreationResult) task.getResult()).mInstance;
                return reactInstance;
            }
        }, Task.IMMEDIATE_EXECUTOR);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ CreationResult lambda$getOrCreateReactInstanceTask$22(Task task) throws Exception {
        JSBundleLoader jSBundleLoader = (JSBundleLoader) task.getResult();
        BridgelessReactContext orCreateReactContext = getOrCreateReactContext();
        DevSupportManager devSupportManager = getDevSupportManager();
        orCreateReactContext.setJSExceptionHandler(devSupportManager);
        log("getOrCreateReactInstanceTask()", "Creating ReactInstance");
        ReactInstance reactInstance = new ReactInstance(orCreateReactContext, this.mReactHostDelegate, this.mComponentFactory, devSupportManager, new QueueThreadExceptionHandler() { // from class: com.facebook.react.runtime.ReactHostImpl$$ExternalSyntheticLambda36
            @Override // com.facebook.react.bridge.queue.QueueThreadExceptionHandler
            public final void handleException(Exception exc) {
                ReactHostImpl.this.handleHostException(exc);
            }
        }, this.mUseDevSupport, getOrCreateReactHostInspectorTarget());
        this.mReactInstance = reactInstance;
        MemoryPressureListener createMemoryPressureListener = createMemoryPressureListener(reactInstance);
        this.mMemoryPressureListener = createMemoryPressureListener;
        this.mMemoryPressureRouter.addMemoryPressureListener(createMemoryPressureListener);
        reactInstance.initializeEagerTurboModules();
        log("getOrCreateReactInstanceTask()", "Loading JS Bundle");
        reactInstance.loadJSBundle(jSBundleLoader);
        log("getOrCreateReactInstanceTask()", "Calling DevSupportManagerBase.onNewReactContextCreated(reactContext)");
        devSupportManager.onNewReactContextCreated(orCreateReactContext);
        orCreateReactContext.runOnJSQueueThread(new Runnable() { // from class: com.facebook.react.runtime.ReactHostImpl$$ExternalSyntheticLambda37
            @Override // java.lang.Runnable
            public final void run() {
                ReactMarker.logMarker(ReactMarkerConstants.REACT_BRIDGELESS_LOADING_END, 1);
            }
        });
        return new CreationResult(reactInstance, orCreateReactContext, this.mReloadTask != null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ ReactInstance lambda$getOrCreateReactInstanceTask$23(Task task) throws Exception {
        ReactInstance reactInstance = ((CreationResult) task.getResult()).mInstance;
        ReactContext reactContext = ((CreationResult) task.getResult()).mContext;
        boolean z = ((CreationResult) task.getResult()).mIsReloading;
        boolean z2 = this.mReactLifecycleStateManager.getLifecycleState() == LifecycleState.RESUMED;
        if (z && !z2) {
            this.mReactLifecycleStateManager.moveToOnHostResume(reactContext, getCurrentActivity());
        } else {
            this.mReactLifecycleStateManager.resumeReactContextIfHostResumed(reactContext, getCurrentActivity());
        }
        log("getOrCreateReactInstanceTask()", "Executing ReactInstanceEventListeners");
        for (ReactInstanceEventListener reactInstanceEventListener : this.mReactInstanceEventListeners) {
            if (reactInstanceEventListener != null) {
                reactInstanceEventListener.onReactContextInitialized(reactContext);
            }
        }
        return reactInstance;
    }

    private Task<JSBundleLoader> getJsBundleLoader() {
        log("getJSBundleLoader()");
        if (this.mUseDevSupport && this.mAllowPackagerServerAccess) {
            return isMetroRunning().onSuccessTask(new Continuation() { // from class: com.facebook.react.runtime.ReactHostImpl$$ExternalSyntheticLambda5
                @Override // com.facebook.react.runtime.internal.bolts.Continuation
                public final Object then(Task task) {
                    Task lambda$getJsBundleLoader$26;
                    lambda$getJsBundleLoader$26 = ReactHostImpl.this.lambda$getJsBundleLoader$26(task);
                    return lambda$getJsBundleLoader$26;
                }
            }, this.mBGExecutor);
        }
        if (ReactBuildConfig.DEBUG) {
            FLog.d(TAG, "Packager server access is disabled in this environment");
        }
        return Task.call(new Callable() { // from class: com.facebook.react.runtime.ReactHostImpl$$ExternalSyntheticLambda6
            @Override // java.util.concurrent.Callable
            public final Object call() {
                JSBundleLoader lambda$getJsBundleLoader$27;
                lambda$getJsBundleLoader$27 = ReactHostImpl.this.lambda$getJsBundleLoader$27();
                return lambda$getJsBundleLoader$27;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Task lambda$getJsBundleLoader$26(Task task) throws Exception {
        if (((Boolean) task.getResult()).booleanValue()) {
            return loadJSBundleFromMetro();
        }
        return Task.forResult(this.mReactHostDelegate.getJsBundleLoader());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ JSBundleLoader lambda$getJsBundleLoader$27() throws Exception {
        return this.mReactHostDelegate.getJsBundleLoader();
    }

    private Task<Boolean> isMetroRunning() {
        log("isMetroRunning()");
        final TaskCompletionSource taskCompletionSource = new TaskCompletionSource();
        getDevSupportManager().isPackagerRunning(new PackagerStatusCallback() { // from class: com.facebook.react.runtime.ReactHostImpl$$ExternalSyntheticLambda34
            @Override // com.facebook.react.devsupport.interfaces.PackagerStatusCallback
            public final void onPackagerStatusFetched(boolean z) {
                ReactHostImpl.this.lambda$isMetroRunning$28(taskCompletionSource, z);
            }
        });
        return taskCompletionSource.getTask();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$isMetroRunning$28(TaskCompletionSource taskCompletionSource, boolean z) {
        log("isMetroRunning()", "Async result = " + z);
        taskCompletionSource.setResult(Boolean.valueOf(z));
    }

    private Task<JSBundleLoader> loadJSBundleFromMetro() {
        log("loadJSBundleFromMetro()");
        final TaskCompletionSource taskCompletionSource = new TaskCompletionSource();
        final DevSupportManagerBase devSupportManagerBase = (DevSupportManagerBase) getDevSupportManager();
        final String devServerBundleURL = devSupportManagerBase.getDevServerHelper().getDevServerBundleURL((String) Assertions.assertNotNull(devSupportManagerBase.getJSAppBundleName()));
        devSupportManagerBase.reloadJSFromServer(devServerBundleURL, new BundleLoadCallback() { // from class: com.facebook.react.runtime.ReactHostImpl.3
            @Override // com.facebook.react.devsupport.interfaces.BundleLoadCallback
            public void onSuccess() {
                ReactHostImpl.this.log("loadJSBundleFromMetro()", "Creating BundleLoader");
                taskCompletionSource.setResult(JSBundleLoader.createCachedBundleFromNetworkLoader(devServerBundleURL, devSupportManagerBase.getDownloadedJSBundleFile()));
            }

            @Override // com.facebook.react.devsupport.interfaces.BundleLoadCallback
            public void onError(Exception exc) {
                taskCompletionSource.setError(exc);
            }
        });
        return taskCompletionSource.getTask();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void log(String str, String str2) {
        this.mBridgelessReactStateTracker.enterState("ReactHost{" + this.mId + "}." + str + ": " + str2);
    }

    private void log(String str) {
        this.mBridgelessReactStateTracker.enterState("ReactHost{" + this.mId + "}." + str);
    }

    private void stopAttachedSurfaces(String str, ReactInstance reactInstance) {
        log(str, "Stopping all React Native surfaces");
        synchronized (this.mAttachedSurfaces) {
            for (ReactSurfaceImpl reactSurfaceImpl : this.mAttachedSurfaces) {
                reactInstance.stopSurface(reactSurfaceImpl);
                reactSurfaceImpl.clear();
            }
        }
    }

    private void startAttachedSurfaces(String str, ReactInstance reactInstance) {
        log(str, "Restarting previously running React Native Surfaces");
        synchronized (this.mAttachedSurfaces) {
            Iterator<ReactSurfaceImpl> it = this.mAttachedSurfaces.iterator();
            while (it.hasNext()) {
                reactInstance.startSurface(it.next());
            }
        }
    }

    private ReactInstanceTaskUnwrapper createReactInstanceUnwrapper(final String str, final String str2, final String str3) {
        return new ReactInstanceTaskUnwrapper() { // from class: com.facebook.react.runtime.ReactHostImpl$$ExternalSyntheticLambda20
            @Override // com.facebook.react.runtime.ReactHostImpl.ReactInstanceTaskUnwrapper
            public final ReactInstance unwrap(Task task, String str4) {
                ReactInstance lambda$createReactInstanceUnwrapper$29;
                lambda$createReactInstanceUnwrapper$29 = ReactHostImpl.this.lambda$createReactInstanceUnwrapper$29(str, str3, str2, task, str4);
                return lambda$createReactInstanceUnwrapper$29;
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ ReactInstance lambda$createReactInstanceUnwrapper$29(String str, String str2, String str3, Task task, String str4) {
        ReactInstance reactInstance = (ReactInstance) task.getResult();
        ReactInstance reactInstance2 = this.mReactInstance;
        String str5 = "Stage: " + str4;
        String str6 = str + " reason: " + str2;
        if (task.isFaulted()) {
            raiseSoftException(str3, str + ": ReactInstance task faulted. " + str5 + ". " + ("Fault reason: " + task.getError().getMessage()) + ". " + str6);
            return reactInstance2;
        }
        if (task.isCancelled()) {
            raiseSoftException(str3, str + ": ReactInstance task cancelled. " + str5 + ". " + str6);
            return reactInstance2;
        }
        if (reactInstance == null) {
            raiseSoftException(str3, str + ": ReactInstance task returned null. " + str5 + ". " + str6);
            return reactInstance2;
        }
        if (reactInstance2 != null && reactInstance != reactInstance2) {
            raiseSoftException(str3, str + ": Detected two different ReactInstances. Returning old. " + str5 + ". " + str6);
        }
        return reactInstance;
    }

    private Task<ReactInstance> getOrCreateReloadTask(final String str) {
        log("getOrCreateReloadTask()");
        raiseSoftException("getOrCreateReloadTask()", str);
        final ReactInstanceTaskUnwrapper createReactInstanceUnwrapper = createReactInstanceUnwrapper("Reload", "getOrCreateReloadTask()", str);
        if (this.mReloadTask == null) {
            log("getOrCreateReloadTask()", "Resetting createReactInstance task ref");
            this.mReloadTask = this.mCreateReactInstanceTaskRef.getAndReset().continueWithTask(new Continuation() { // from class: com.facebook.react.runtime.ReactHostImpl$$ExternalSyntheticLambda12
                @Override // com.facebook.react.runtime.internal.bolts.Continuation
                public final Object then(Task task) {
                    Task lambda$getOrCreateReloadTask$30;
                    lambda$getOrCreateReloadTask$30 = ReactHostImpl.this.lambda$getOrCreateReloadTask$30(createReactInstanceUnwrapper, str, task);
                    return lambda$getOrCreateReloadTask$30;
                }
            }, this.mUIExecutor).continueWithTask(new Continuation() { // from class: com.facebook.react.runtime.ReactHostImpl$$ExternalSyntheticLambda13
                @Override // com.facebook.react.runtime.internal.bolts.Continuation
                public final Object then(Task task) {
                    Task lambda$getOrCreateReloadTask$31;
                    lambda$getOrCreateReloadTask$31 = ReactHostImpl.this.lambda$getOrCreateReloadTask$31(createReactInstanceUnwrapper, task);
                    return lambda$getOrCreateReloadTask$31;
                }
            }, this.mBGExecutor).continueWithTask(new Continuation() { // from class: com.facebook.react.runtime.ReactHostImpl$$ExternalSyntheticLambda14
                @Override // com.facebook.react.runtime.internal.bolts.Continuation
                public final Object then(Task task) {
                    Task lambda$getOrCreateReloadTask$32;
                    lambda$getOrCreateReloadTask$32 = ReactHostImpl.this.lambda$getOrCreateReloadTask$32(createReactInstanceUnwrapper, task);
                    return lambda$getOrCreateReloadTask$32;
                }
            }, this.mUIExecutor).continueWithTask(new Continuation() { // from class: com.facebook.react.runtime.ReactHostImpl$$ExternalSyntheticLambda15
                @Override // com.facebook.react.runtime.internal.bolts.Continuation
                public final Object then(Task task) {
                    Task lambda$getOrCreateReloadTask$33;
                    lambda$getOrCreateReloadTask$33 = ReactHostImpl.this.lambda$getOrCreateReloadTask$33(createReactInstanceUnwrapper, task);
                    return lambda$getOrCreateReloadTask$33;
                }
            }, this.mBGExecutor).continueWithTask(new Continuation() { // from class: com.facebook.react.runtime.ReactHostImpl$$ExternalSyntheticLambda16
                @Override // com.facebook.react.runtime.internal.bolts.Continuation
                public final Object then(Task task) {
                    Task lambda$getOrCreateReloadTask$34;
                    lambda$getOrCreateReloadTask$34 = ReactHostImpl.this.lambda$getOrCreateReloadTask$34(createReactInstanceUnwrapper, task);
                    return lambda$getOrCreateReloadTask$34;
                }
            }, this.mBGExecutor).continueWithTask(new Continuation() { // from class: com.facebook.react.runtime.ReactHostImpl$$ExternalSyntheticLambda17
                @Override // com.facebook.react.runtime.internal.bolts.Continuation
                public final Object then(Task task) {
                    Task lambda$getOrCreateReloadTask$35;
                    lambda$getOrCreateReloadTask$35 = ReactHostImpl.this.lambda$getOrCreateReloadTask$35(str, task);
                    return lambda$getOrCreateReloadTask$35;
                }
            }, this.mBGExecutor);
        }
        return this.mReloadTask;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Task lambda$getOrCreateReloadTask$30(ReactInstanceTaskUnwrapper reactInstanceTaskUnwrapper, String str, Task task) throws Exception {
        log("getOrCreateReloadTask()", "Starting React Native reload");
        ReactInstance unwrap = reactInstanceTaskUnwrapper.unwrap(task, "1: Starting reload");
        unregisterInstanceFromInspector(unwrap);
        BridgelessReactContext nullable = this.mBridgelessReactContextRef.getNullable();
        if (nullable == null) {
            raiseSoftException("getOrCreateReloadTask()", "ReactContext is null. Reload reason: " + str);
        }
        if (nullable != null && this.mReactLifecycleStateManager.getLifecycleState() == LifecycleState.RESUMED) {
            log("getOrCreateReloadTask()", "Calling ReactContext.onHostPause()");
            nullable.onHostPause();
        }
        return Task.forResult(unwrap);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Task lambda$getOrCreateReloadTask$31(ReactInstanceTaskUnwrapper reactInstanceTaskUnwrapper, Task task) throws Exception {
        ReactInstance unwrap = reactInstanceTaskUnwrapper.unwrap(task, "2: Surface shutdown");
        if (unwrap == null) {
            raiseSoftException("getOrCreateReloadTask()", "Skipping surface shutdown: ReactInstance null");
            return task;
        }
        stopAttachedSurfaces("getOrCreateReloadTask()", unwrap);
        return task;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Task lambda$getOrCreateReloadTask$32(ReactInstanceTaskUnwrapper reactInstanceTaskUnwrapper, Task task) throws Exception {
        reactInstanceTaskUnwrapper.unwrap(task, "3: Destroying ReactContext");
        Iterator<Function0<Unit>> it = this.mBeforeDestroyListeners.iterator();
        while (it.hasNext()) {
            it.next().invoke();
        }
        if (this.mMemoryPressureListener != null) {
            log("getOrCreateReloadTask()", "Removing memory pressure listener");
            this.mMemoryPressureRouter.removeMemoryPressureListener(this.mMemoryPressureListener);
        }
        BridgelessReactContext nullable = this.mBridgelessReactContextRef.getNullable();
        if (nullable != null) {
            log("getOrCreateReloadTask()", "Resetting ReactContext ref");
            this.mBridgelessReactContextRef.reset();
            log("getOrCreateReloadTask()", "Destroying ReactContext");
            nullable.destroy();
        }
        if (this.mUseDevSupport && nullable != null) {
            log("getOrCreateReloadTask()", "Calling DevSupportManager.onReactInstanceDestroyed(reactContext)");
            this.mDevSupportManager.onReactInstanceDestroyed(nullable);
        }
        return task;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Task lambda$getOrCreateReloadTask$33(ReactInstanceTaskUnwrapper reactInstanceTaskUnwrapper, Task task) throws Exception {
        ReactInstance unwrap = reactInstanceTaskUnwrapper.unwrap(task, "4: Destroying ReactInstance");
        if (unwrap == null) {
            raiseSoftException("getOrCreateReloadTask()", "Skipping ReactInstance.destroy(): ReactInstance null");
        } else {
            log("getOrCreateReloadTask()", "Resetting ReactInstance ptr");
            this.mReactInstance = null;
            log("getOrCreateReloadTask()", "Destroying ReactInstance");
            unwrap.destroy();
        }
        log("getOrCreateReloadTask()", "Resetting start task ref");
        this.mStartTask = null;
        return getOrCreateReactInstanceTask();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Task lambda$getOrCreateReloadTask$34(ReactInstanceTaskUnwrapper reactInstanceTaskUnwrapper, Task task) throws Exception {
        ReactInstance unwrap = reactInstanceTaskUnwrapper.unwrap(task, "5: Restarting surfaces");
        if (unwrap == null) {
            raiseSoftException("getOrCreateReloadTask()", "Skipping surface restart: ReactInstance null");
            return task;
        }
        startAttachedSurfaces("getOrCreateReloadTask()", unwrap);
        return task;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Task lambda$getOrCreateReloadTask$35(String str, Task task) throws Exception {
        if (task.isFaulted()) {
            raiseSoftException("getOrCreateReloadTask()", "Error during reload. ReactInstance task faulted. Fault reason: " + task.getError().getMessage() + ". Reload reason: " + str, task.getError());
        }
        if (task.isCancelled()) {
            raiseSoftException("getOrCreateReloadTask()", "Error during reload. ReactInstance task cancelled. Reload reason: " + str);
        }
        log("getOrCreateReloadTask()", "Resetting reload task ref");
        this.mReloadTask = null;
        return task;
    }

    private Task<Void> getOrCreateDestroyTask(final String str, Exception exc) {
        log("getOrCreateDestroyTask()");
        raiseSoftException("getOrCreateDestroyTask()", str, exc);
        final ReactInstanceTaskUnwrapper createReactInstanceUnwrapper = createReactInstanceUnwrapper("Destroy", "getOrCreateDestroyTask()", str);
        if (this.mDestroyTask == null) {
            log("getOrCreateDestroyTask()", "Resetting createReactInstance task ref");
            this.mDestroyTask = this.mCreateReactInstanceTaskRef.getAndReset().continueWithTask(new Continuation() { // from class: com.facebook.react.runtime.ReactHostImpl$$ExternalSyntheticLambda27
                @Override // com.facebook.react.runtime.internal.bolts.Continuation
                public final Object then(Task task) {
                    Task lambda$getOrCreateDestroyTask$36;
                    lambda$getOrCreateDestroyTask$36 = ReactHostImpl.this.lambda$getOrCreateDestroyTask$36(createReactInstanceUnwrapper, str, task);
                    return lambda$getOrCreateDestroyTask$36;
                }
            }, this.mUIExecutor).continueWithTask(new Continuation() { // from class: com.facebook.react.runtime.ReactHostImpl$$ExternalSyntheticLambda28
                @Override // com.facebook.react.runtime.internal.bolts.Continuation
                public final Object then(Task task) {
                    Task lambda$getOrCreateDestroyTask$37;
                    lambda$getOrCreateDestroyTask$37 = ReactHostImpl.this.lambda$getOrCreateDestroyTask$37(createReactInstanceUnwrapper, task);
                    return lambda$getOrCreateDestroyTask$37;
                }
            }, this.mBGExecutor).continueWithTask(new Continuation() { // from class: com.facebook.react.runtime.ReactHostImpl$$ExternalSyntheticLambda29
                @Override // com.facebook.react.runtime.internal.bolts.Continuation
                public final Object then(Task task) {
                    Task lambda$getOrCreateDestroyTask$38;
                    lambda$getOrCreateDestroyTask$38 = ReactHostImpl.this.lambda$getOrCreateDestroyTask$38(createReactInstanceUnwrapper, str, task);
                    return lambda$getOrCreateDestroyTask$38;
                }
            }, this.mUIExecutor).continueWithTask(new Continuation() { // from class: com.facebook.react.runtime.ReactHostImpl$$ExternalSyntheticLambda30
                @Override // com.facebook.react.runtime.internal.bolts.Continuation
                public final Object then(Task task) {
                    Task lambda$getOrCreateDestroyTask$39;
                    lambda$getOrCreateDestroyTask$39 = ReactHostImpl.this.lambda$getOrCreateDestroyTask$39(createReactInstanceUnwrapper, task);
                    return lambda$getOrCreateDestroyTask$39;
                }
            }, this.mBGExecutor).continueWith(new Continuation() { // from class: com.facebook.react.runtime.ReactHostImpl$$ExternalSyntheticLambda31
                @Override // com.facebook.react.runtime.internal.bolts.Continuation
                public final Object then(Task task) {
                    Void lambda$getOrCreateDestroyTask$40;
                    lambda$getOrCreateDestroyTask$40 = ReactHostImpl.this.lambda$getOrCreateDestroyTask$40(str, task);
                    return lambda$getOrCreateDestroyTask$40;
                }
            });
        }
        return this.mDestroyTask;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Task lambda$getOrCreateDestroyTask$36(ReactInstanceTaskUnwrapper reactInstanceTaskUnwrapper, String str, Task task) throws Exception {
        ReactHostInspectorTarget reactHostInspectorTarget;
        log("getOrCreateDestroyTask()", "Starting React Native destruction");
        ReactInstance unwrap = reactInstanceTaskUnwrapper.unwrap(task, "1: Starting destroy");
        unregisterInstanceFromInspector(unwrap);
        if (this.mHostInvalidated && (reactHostInspectorTarget = this.mReactHostInspectorTarget) != null) {
            reactHostInspectorTarget.close();
            this.mReactHostInspectorTarget = null;
        }
        if (this.mUseDevSupport) {
            log("getOrCreateDestroyTask()", "DevSupportManager cleanup");
            this.mDevSupportManager.stopInspector();
        }
        BridgelessReactContext nullable = this.mBridgelessReactContextRef.getNullable();
        if (nullable == null) {
            raiseSoftException("getOrCreateDestroyTask()", "ReactContext is null. Destroy reason: " + str);
        }
        log("getOrCreateDestroyTask()", "Move ReactHost to onHostDestroy()");
        this.mReactLifecycleStateManager.moveToOnHostDestroy(nullable);
        return Task.forResult(unwrap);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Task lambda$getOrCreateDestroyTask$37(ReactInstanceTaskUnwrapper reactInstanceTaskUnwrapper, Task task) throws Exception {
        ReactInstance unwrap = reactInstanceTaskUnwrapper.unwrap(task, "2: Stopping surfaces");
        if (unwrap == null) {
            raiseSoftException("getOrCreateDestroyTask()", "Skipping surface shutdown: ReactInstance null");
            return task;
        }
        stopAttachedSurfaces("getOrCreateDestroyTask()", unwrap);
        synchronized (this.mAttachedSurfaces) {
            this.mAttachedSurfaces.clear();
        }
        return task;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Task lambda$getOrCreateDestroyTask$38(ReactInstanceTaskUnwrapper reactInstanceTaskUnwrapper, String str, Task task) throws Exception {
        reactInstanceTaskUnwrapper.unwrap(task, "3: Destroying ReactContext");
        Iterator<Function0<Unit>> it = this.mBeforeDestroyListeners.iterator();
        while (it.hasNext()) {
            it.next().invoke();
        }
        BridgelessReactContext nullable = this.mBridgelessReactContextRef.getNullable();
        if (nullable == null) {
            raiseSoftException("getOrCreateDestroyTask()", "ReactContext is null. Destroy reason: " + str);
        }
        log("getOrCreateDestroyTask()", "Destroying MemoryPressureRouter");
        this.mMemoryPressureRouter.destroy(this.mContext);
        if (nullable != null) {
            log("getOrCreateDestroyTask()", "Resetting ReactContext ref");
            this.mBridgelessReactContextRef.reset();
            log("getOrCreateDestroyTask()", "Destroying ReactContext");
            nullable.destroy();
        }
        setCurrentActivity(null);
        ResourceDrawableIdHelper.getInstance().clear();
        return task;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Task lambda$getOrCreateDestroyTask$39(ReactInstanceTaskUnwrapper reactInstanceTaskUnwrapper, Task task) throws Exception {
        ReactInstance unwrap = reactInstanceTaskUnwrapper.unwrap(task, "4: Destroying ReactInstance");
        if (unwrap == null) {
            raiseSoftException("getOrCreateDestroyTask()", "Skipping ReactInstance.destroy(): ReactInstance null");
        } else {
            log("getOrCreateDestroyTask()", "Resetting ReactInstance ptr");
            this.mReactInstance = null;
            log("getOrCreateDestroyTask()", "Destroying ReactInstance");
            unwrap.destroy();
        }
        log("getOrCreateDestroyTask()", "Resetting start task ref");
        this.mStartTask = null;
        log("getOrCreateDestroyTask()", "Resetting destroy task ref");
        this.mDestroyTask = null;
        return task;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Void lambda$getOrCreateDestroyTask$40(String str, Task task) throws Exception {
        if (task.isFaulted()) {
            raiseSoftException("getOrCreateDestroyTask()", "React destruction failed. ReactInstance task faulted. Fault reason: " + task.getError().getMessage() + ". Destroy reason: " + str, task.getError());
        }
        if (!task.isCancelled()) {
            return null;
        }
        raiseSoftException("getOrCreateDestroyTask()", "React destruction failed. ReactInstance task cancelled. Destroy reason: " + str);
        return null;
    }

    private ReactHostInspectorTarget getOrCreateReactHostInspectorTarget() {
        if (this.mReactHostInspectorTarget == null && InspectorFlags.getFuseboxEnabled()) {
            this.mReactHostInspectorTarget = new ReactHostInspectorTarget(this);
        }
        return this.mReactHostInspectorTarget;
    }

    private void unregisterInstanceFromInspector(ReactInstance reactInstance) {
        if (reactInstance != null) {
            if (InspectorFlags.getFuseboxEnabled()) {
                ReactHostInspectorTarget reactHostInspectorTarget = this.mReactHostInspectorTarget;
                Assertions.assertCondition(reactHostInspectorTarget != null && reactHostInspectorTarget.isValid(), "Host inspector target destroyed before instance was unregistered");
            }
            reactInstance.unregisterFromInspector();
        }
    }

    @Override // com.facebook.react.ReactHost
    public void invalidate() {
        FLog.d(TAG, "ReactHostImpl.invalidate()");
        this.mHostInvalidated = true;
        destroy("ReactHostImpl.invalidate()", null);
    }
}
