package com.facebook.react.defaults;

import android.app.Application;
import android.content.Context;
import com.facebook.react.JSEngineResolutionAlgorithm;
import com.facebook.react.ReactHost;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.ReactPackageTurboModuleManagerDelegate;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.UIManager;
import com.facebook.react.bridge.UIManagerProvider;
import com.facebook.react.common.annotations.UnstableReactNativeAPI;
import com.facebook.react.common.build.ReactBuildConfig;
import com.facebook.react.defaults.DefaultTurboModuleManagerDelegate;
import com.facebook.react.fabric.ComponentFactory;
import com.facebook.react.fabric.FabricUIManagerProviderImpl;
import com.facebook.react.runtime.JSCInstance;
import com.facebook.react.runtime.JSRuntimeFactory;
import com.facebook.react.runtime.cxxreactpackage.CxxReactPackage;
import com.facebook.react.runtime.hermes.HermesInstance;
import com.facebook.react.uimanager.ViewManager;
import com.facebook.react.uimanager.ViewManagerRegistry;
import com.facebook.react.uimanager.ViewManagerResolver;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: DefaultReactNativeHost.kt */
@Metadata(d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b&\u0018\u00002\u00020\u0001B\u0011\b\u0004\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\n\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u0014J\n\u0010\b\u001a\u0004\u0018\u00010\tH\u0014J\n\u0010\n\u001a\u0004\u0018\u00010\u000bH\u0014J\b\u0010\f\u001a\u00020\rH\u0016J!\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\n\b\u0002\u0010\u0017\u001a\u0004\u0018\u00010\u0018H\u0001¢\u0006\u0002\b\u0019R\u0014\u0010\u000e\u001a\u00020\u000f8TX\u0094\u0004¢\u0006\u0006\u001a\u0004\b\u000e\u0010\u0010R\u0016\u0010\u0011\u001a\u0004\u0018\u00010\u000f8TX\u0094\u0004¢\u0006\u0006\u001a\u0004\b\u0011\u0010\u0012¨\u0006\u001a"}, d2 = {"Lcom/facebook/react/defaults/DefaultReactNativeHost;", "Lcom/facebook/react/ReactNativeHost;", "application", "Landroid/app/Application;", "<init>", "(Landroid/app/Application;)V", "getReactPackageTurboModuleManagerDelegateBuilder", "Lcom/facebook/react/ReactPackageTurboModuleManagerDelegate$Builder;", "getUIManagerProvider", "Lcom/facebook/react/bridge/UIManagerProvider;", "getJSEngineResolutionAlgorithm", "Lcom/facebook/react/JSEngineResolutionAlgorithm;", "clear", "", "isNewArchEnabled", "", "()Z", "isHermesEnabled", "()Ljava/lang/Boolean;", "toReactHost", "Lcom/facebook/react/ReactHost;", "context", "Landroid/content/Context;", "jsRuntimeFactory", "Lcom/facebook/react/runtime/JSRuntimeFactory;", "toReactHost$ReactAndroid_release", "ReactAndroid_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes.dex */
public abstract class DefaultReactNativeHost extends ReactNativeHost {
    protected Boolean isHermesEnabled() {
        return null;
    }

    /* renamed from: isNewArchEnabled */
    protected boolean getIsNewArchEnabled() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public DefaultReactNativeHost(Application application) {
        super(application);
        Intrinsics.checkNotNullParameter(application, "application");
    }

    @Override // com.facebook.react.ReactNativeHost
    protected ReactPackageTurboModuleManagerDelegate.Builder getReactPackageTurboModuleManagerDelegateBuilder() {
        if (getIsNewArchEnabled()) {
            return new DefaultTurboModuleManagerDelegate.Builder();
        }
        return null;
    }

    @Override // com.facebook.react.ReactNativeHost
    protected UIManagerProvider getUIManagerProvider() {
        if (getIsNewArchEnabled()) {
            return new UIManagerProvider() { // from class: com.facebook.react.defaults.DefaultReactNativeHost$$ExternalSyntheticLambda0
                @Override // com.facebook.react.bridge.UIManagerProvider
                public final UIManager createUIManager(ReactApplicationContext reactApplicationContext) {
                    UIManager uIManagerProvider$lambda$0;
                    uIManagerProvider$lambda$0 = DefaultReactNativeHost.getUIManagerProvider$lambda$0(DefaultReactNativeHost.this, reactApplicationContext);
                    return uIManagerProvider$lambda$0;
                }
            };
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final UIManager getUIManagerProvider$lambda$0(final DefaultReactNativeHost defaultReactNativeHost, ReactApplicationContext reactApplicationContext) {
        ViewManagerRegistry viewManagerRegistry;
        Intrinsics.checkNotNullParameter(reactApplicationContext, "reactApplicationContext");
        ComponentFactory componentFactory = new ComponentFactory();
        DefaultComponentsRegistry.register(componentFactory);
        if (defaultReactNativeHost.getLazyViewManagersEnabled()) {
            viewManagerRegistry = new ViewManagerRegistry(new ViewManagerResolver() { // from class: com.facebook.react.defaults.DefaultReactNativeHost$getUIManagerProvider$1$viewManagerRegistry$1
                @Override // com.facebook.react.uimanager.ViewManagerResolver
                public ViewManager getViewManager(String viewManagerName) {
                    Intrinsics.checkNotNullParameter(viewManagerName, "viewManagerName");
                    return DefaultReactNativeHost.this.getReactInstanceManager().createViewManager(viewManagerName);
                }

                @Override // com.facebook.react.uimanager.ViewManagerResolver
                public Collection<String> getViewManagerNames() {
                    return DefaultReactNativeHost.this.getReactInstanceManager().getViewManagerNames();
                }
            });
        } else {
            viewManagerRegistry = new ViewManagerRegistry(defaultReactNativeHost.getReactInstanceManager().getOrCreateViewManagers(reactApplicationContext));
        }
        return new FabricUIManagerProviderImpl(componentFactory, viewManagerRegistry).createUIManager(reactApplicationContext);
    }

    @Override // com.facebook.react.ReactNativeHost
    protected JSEngineResolutionAlgorithm getJSEngineResolutionAlgorithm() {
        Boolean isHermesEnabled = isHermesEnabled();
        if (Intrinsics.areEqual((Object) isHermesEnabled, (Object) true)) {
            return JSEngineResolutionAlgorithm.HERMES;
        }
        if (Intrinsics.areEqual((Object) isHermesEnabled, (Object) false)) {
            return JSEngineResolutionAlgorithm.JSC;
        }
        if (isHermesEnabled == null) {
            return null;
        }
        throw new NoWhenBranchMatchedException();
    }

    @Override // com.facebook.react.ReactNativeHost
    public void clear() {
        super.clear();
        DefaultReactHost.INSTANCE.invalidate$ReactAndroid_release();
    }

    public static /* synthetic */ ReactHost toReactHost$ReactAndroid_release$default(DefaultReactNativeHost defaultReactNativeHost, Context context, JSRuntimeFactory jSRuntimeFactory, int i, Object obj) {
        if (obj != null) {
            throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: toReactHost");
        }
        if ((i & 2) != 0) {
            jSRuntimeFactory = null;
        }
        return defaultReactNativeHost.toReactHost$ReactAndroid_release(context, jSRuntimeFactory);
    }

    @UnstableReactNativeAPI
    public final ReactHost toReactHost$ReactAndroid_release(Context context, JSRuntimeFactory jsRuntimeFactory) {
        ReactHost defaultReactHost;
        Intrinsics.checkNotNullParameter(context, "context");
        if (jsRuntimeFactory == null) {
            jsRuntimeFactory = Intrinsics.areEqual((Object) isHermesEnabled(), (Object) false) ? new JSCInstance() : new HermesInstance();
        }
        JSRuntimeFactory jSRuntimeFactory = jsRuntimeFactory;
        List<ReactPackage> packages = getPackages();
        Intrinsics.checkNotNullExpressionValue(packages, "getPackages(...)");
        String jSMainModuleName = getJSMainModuleName();
        Intrinsics.checkNotNullExpressionValue(jSMainModuleName, "getJSMainModuleName(...)");
        String bundleAssetName = getBundleAssetName();
        if (bundleAssetName == null) {
            bundleAssetName = "index";
        }
        defaultReactHost = DefaultReactHost.getDefaultReactHost(context, (List<? extends ReactPackage>) packages, (r14 & 4) != 0 ? "index" : jSMainModuleName, (r14 & 8) == 0 ? bundleAssetName : "index", (r14 & 16) != 0 ? null : getJSBundleFile(), (r14 & 32) == 0 ? jSRuntimeFactory : null, (r14 & 64) != 0 ? ReactBuildConfig.DEBUG : getUseDeveloperSupport(), (List<? extends Function1<? super ReactContext, ? extends CxxReactPackage>>) ((r14 & 128) != 0 ? CollectionsKt.emptyList() : null));
        return defaultReactHost;
    }
}
