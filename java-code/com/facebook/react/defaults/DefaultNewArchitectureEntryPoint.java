package com.facebook.react.defaults;

import com.facebook.react.common.ReleaseLevel;
import com.facebook.react.common.annotations.VisibleForTesting;
import com.facebook.react.internal.featureflags.ReactNativeFeatureFlags;
import com.facebook.react.internal.featureflags.ReactNativeFeatureFlagsOverrides_RNOSS_Canary_Android;
import com.facebook.react.internal.featureflags.ReactNativeFeatureFlagsOverrides_RNOSS_Experimental_Android;
import com.facebook.react.internal.featureflags.ReactNativeFeatureFlagsOverrides_RNOSS_Stable_Android;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: DefaultNewArchitectureEntryPoint.kt */
@Metadata(d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0011\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J&\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\r2\b\b\u0002\u0010\u000f\u001a\u00020\rH\u0007J,\u0010\u001e\u001a\u000e\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020 0\u001f2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\r2\u0006\u0010\u000f\u001a\u00020\rH\u0007R\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\tR\u000e\u0010\u0010\u001a\u00020\rX\u0082\u000e¢\u0006\u0002\n\u0000R\u001a\u0010\u000e\u001a\u00020\r8FX\u0087\u0004¢\u0006\f\u0012\u0004\b\u0011\u0010\u0003\u001a\u0004\b\u0012\u0010\u0013R\u000e\u0010\u0014\u001a\u00020\rX\u0082\u000e¢\u0006\u0002\n\u0000R\u001a\u0010\f\u001a\u00020\r8FX\u0087\u0004¢\u0006\f\u0012\u0004\b\u0015\u0010\u0003\u001a\u0004\b\u0016\u0010\u0013R\u000e\u0010\u0017\u001a\u00020\rX\u0082\u000e¢\u0006\u0002\n\u0000R\u001a\u0010\u0018\u001a\u00020\r8FX\u0087\u0004¢\u0006\f\u0012\u0004\b\u0019\u0010\u0003\u001a\u0004\b\u001a\u0010\u0013R\u000e\u0010\u001b\u001a\u00020\rX\u0082\u000e¢\u0006\u0002\n\u0000R\u001a\u0010\u000f\u001a\u00020\r8FX\u0087\u0004¢\u0006\f\u0012\u0004\b\u001c\u0010\u0003\u001a\u0004\b\u001d\u0010\u0013¨\u0006!"}, d2 = {"Lcom/facebook/react/defaults/DefaultNewArchitectureEntryPoint;", "", "<init>", "()V", "releaseLevel", "Lcom/facebook/react/common/ReleaseLevel;", "getReleaseLevel", "()Lcom/facebook/react/common/ReleaseLevel;", "setReleaseLevel", "(Lcom/facebook/react/common/ReleaseLevel;)V", "load", "", "turboModulesEnabled", "", "fabricEnabled", "bridgelessEnabled", "privateFabricEnabled", "getFabricEnabled$annotations", "getFabricEnabled", "()Z", "privateTurboModulesEnabled", "getTurboModulesEnabled$annotations", "getTurboModulesEnabled", "privateConcurrentReactEnabled", "concurrentReactEnabled", "getConcurrentReactEnabled$annotations", "getConcurrentReactEnabled", "privateBridgelessEnabled", "getBridgelessEnabled$annotations", "getBridgelessEnabled", "isConfigurationValid", "Lkotlin/Pair;", "", "ReactAndroid_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes.dex */
public final class DefaultNewArchitectureEntryPoint {
    private static boolean privateBridgelessEnabled;
    private static boolean privateConcurrentReactEnabled;
    private static boolean privateFabricEnabled;
    private static boolean privateTurboModulesEnabled;
    public static final DefaultNewArchitectureEntryPoint INSTANCE = new DefaultNewArchitectureEntryPoint();
    private static ReleaseLevel releaseLevel = ReleaseLevel.STABLE;

    /* compiled from: DefaultNewArchitectureEntryPoint.kt */
    @Metadata(k = 3, mv = {2, 0, 0}, xi = 48)
    /* loaded from: classes.dex */
    public /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[ReleaseLevel.values().length];
            try {
                iArr[ReleaseLevel.EXPERIMENTAL.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                iArr[ReleaseLevel.CANARY.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                iArr[ReleaseLevel.STABLE.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            $EnumSwitchMapping$0 = iArr;
        }
    }

    @JvmStatic
    public static /* synthetic */ void getBridgelessEnabled$annotations() {
    }

    @JvmStatic
    public static /* synthetic */ void getConcurrentReactEnabled$annotations() {
    }

    @JvmStatic
    public static /* synthetic */ void getFabricEnabled$annotations() {
    }

    @JvmStatic
    public static /* synthetic */ void getTurboModulesEnabled$annotations() {
    }

    @JvmStatic
    public static final void load() {
        load$default(false, false, false, 7, null);
    }

    @JvmStatic
    public static final void load(boolean z) {
        load$default(z, false, false, 6, null);
    }

    @JvmStatic
    public static final void load(boolean z, boolean z2) {
        load$default(z, z2, false, 4, null);
    }

    private DefaultNewArchitectureEntryPoint() {
    }

    public final ReleaseLevel getReleaseLevel() {
        return releaseLevel;
    }

    public final void setReleaseLevel(ReleaseLevel releaseLevel2) {
        Intrinsics.checkNotNullParameter(releaseLevel2, "<set-?>");
        releaseLevel = releaseLevel2;
    }

    public static /* synthetic */ void load$default(boolean z, boolean z2, boolean z3, int i, Object obj) {
        if ((i & 1) != 0) {
            z = true;
        }
        if ((i & 2) != 0) {
            z2 = true;
        }
        if ((i & 4) != 0) {
            z3 = true;
        }
        load(z, z2, z3);
    }

    @JvmStatic
    public static final void load(boolean turboModulesEnabled, boolean fabricEnabled, boolean bridgelessEnabled) {
        Pair<Boolean, String> isConfigurationValid = INSTANCE.isConfigurationValid(turboModulesEnabled, fabricEnabled, bridgelessEnabled);
        boolean booleanValue = isConfigurationValid.component1().booleanValue();
        String component2 = isConfigurationValid.component2();
        if (!booleanValue) {
            throw new IllegalStateException(component2.toString());
        }
        int i = WhenMappings.$EnumSwitchMapping$0[releaseLevel.ordinal()];
        if (i == 1) {
            ReactNativeFeatureFlags.override(new ReactNativeFeatureFlagsOverrides_RNOSS_Experimental_Android());
        } else if (i == 2) {
            ReactNativeFeatureFlags.override(new ReactNativeFeatureFlagsOverrides_RNOSS_Canary_Android());
        } else {
            if (i != 3) {
                throw new NoWhenBranchMatchedException();
            }
            ReactNativeFeatureFlags.override(new ReactNativeFeatureFlagsOverrides_RNOSS_Stable_Android(fabricEnabled, bridgelessEnabled, turboModulesEnabled));
        }
        privateFabricEnabled = fabricEnabled;
        privateTurboModulesEnabled = turboModulesEnabled;
        privateConcurrentReactEnabled = fabricEnabled;
        privateBridgelessEnabled = bridgelessEnabled;
        DefaultSoLoader.INSTANCE.maybeLoadSoLibrary();
    }

    public static final boolean getFabricEnabled() {
        return privateFabricEnabled;
    }

    public static final boolean getTurboModulesEnabled() {
        return privateTurboModulesEnabled;
    }

    public static final boolean getConcurrentReactEnabled() {
        return privateConcurrentReactEnabled;
    }

    public static final boolean getBridgelessEnabled() {
        return privateBridgelessEnabled;
    }

    @VisibleForTesting
    public final Pair<Boolean, String> isConfigurationValid(boolean turboModulesEnabled, boolean fabricEnabled, boolean bridgelessEnabled) {
        if (fabricEnabled && !turboModulesEnabled) {
            return TuplesKt.to(false, "fabricEnabled=true requires turboModulesEnabled=true (is now false) - Please update your DefaultNewArchitectureEntryPoint.load() parameters.");
        }
        if (bridgelessEnabled && (!turboModulesEnabled || !fabricEnabled)) {
            return TuplesKt.to(false, "bridgelessEnabled=true requires (turboModulesEnabled=true AND fabricEnabled=true) - Please update your DefaultNewArchitectureEntryPoint.load() parameters.");
        }
        return TuplesKt.to(true, "");
    }
}
