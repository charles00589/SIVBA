package com.facebook.react.soloader;

import com.facebook.soloader.ExternalSoMapping;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: OpenSourceMergedSoMapping.kt */
@Metadata(d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0015\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0005H\u0016J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u0005H\u0016J\t\u0010\n\u001a\u00020\u000bH\u0086 J\t\u0010\f\u001a\u00020\u000bH\u0086 J\t\u0010\r\u001a\u00020\u000bH\u0086 J\t\u0010\u000e\u001a\u00020\u000bH\u0086 J\t\u0010\u000f\u001a\u00020\u000bH\u0086 J\t\u0010\u0010\u001a\u00020\u000bH\u0086 J\t\u0010\u0011\u001a\u00020\u000bH\u0086 J\t\u0010\u0012\u001a\u00020\u000bH\u0086 J\t\u0010\u0013\u001a\u00020\u000bH\u0086 J\t\u0010\u0014\u001a\u00020\u000bH\u0086 J\t\u0010\u0015\u001a\u00020\u000bH\u0086 J\t\u0010\u0016\u001a\u00020\u000bH\u0086 J\t\u0010\u0017\u001a\u00020\u000bH\u0086 J\t\u0010\u0018\u001a\u00020\u000bH\u0086 J\t\u0010\u0019\u001a\u00020\u000bH\u0086 J\t\u0010\u001a\u001a\u00020\u000bH\u0086 J\t\u0010\u001b\u001a\u00020\u000bH\u0086 J\t\u0010\u001c\u001a\u00020\u000bH\u0086 J\t\u0010\u001d\u001a\u00020\u000bH\u0086 J\t\u0010\u001e\u001a\u00020\u000bH\u0086 J\t\u0010\u001f\u001a\u00020\u000bH\u0086 ¨\u0006 "}, d2 = {"Lcom/facebook/react/soloader/OpenSourceMergedSoMapping;", "Lcom/facebook/soloader/ExternalSoMapping;", "<init>", "()V", "mapLibName", "", "input", "invokeJniOnload", "", "libraryName", "libfabricjni_so", "", "libhermes_executor_so", "libhermesinstancejni_so", "libhermestooling_so", "libjscexecutor_so", "libjscinstance_so", "libjscruntime_so", "libjsctooling_so", "libjsijniprofiler_so", "libjsinspector_so", "libmapbufferjni_so", "libreact_devsupportjni_so", "libreact_featureflagsjni_so", "libreact_newarchdefaults_so", "libreactnative_so", "libreactnativeblob_so", "libreactnativejni_so", "librninstance_so", "libturbomodulejsijni_so", "libuimanagerjni_so", "libyoga_so", "ReactAndroid_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes.dex */
public final class OpenSourceMergedSoMapping implements ExternalSoMapping {
    public static final OpenSourceMergedSoMapping INSTANCE = new OpenSourceMergedSoMapping();

    public final native int libfabricjni_so();

    public final native int libhermes_executor_so();

    public final native int libhermesinstancejni_so();

    public final native int libhermestooling_so();

    public final native int libjscexecutor_so();

    public final native int libjscinstance_so();

    public final native int libjscruntime_so();

    public final native int libjsctooling_so();

    public final native int libjsijniprofiler_so();

    public final native int libjsinspector_so();

    public final native int libmapbufferjni_so();

    public final native int libreact_devsupportjni_so();

    public final native int libreact_featureflagsjni_so();

    public final native int libreact_newarchdefaults_so();

    public final native int libreactnative_so();

    public final native int libreactnativeblob_so();

    public final native int libreactnativejni_so();

    public final native int librninstance_so();

    public final native int libturbomodulejsijni_so();

    public final native int libuimanagerjni_so();

    public final native int libyoga_so();

    private OpenSourceMergedSoMapping() {
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0009. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:13:0x0091 A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:26:0x0068 A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:5:0x00c0 A[ORIG_RETURN, RETURN] */
    @Override // com.facebook.soloader.ExternalSoMapping
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public String mapLibName(String input) {
        Intrinsics.checkNotNullParameter(input, "input");
        switch (input.hashCode()) {
            case -1793638007:
                return input.equals("mapbufferjni") ? "reactnative" : input;
            case -1624070447:
                if (!input.equals("rninstance")) {
                    return input;
                }
                break;
            case -1570429553:
                if (!input.equals("reactnativejni")) {
                    return input;
                }
                break;
            case -1438915853:
                if (!input.equals("reactnativeblob")) {
                    return input;
                }
                break;
            case -1382694412:
                if (!input.equals("react_featureflagsjni")) {
                    return input;
                }
                break;
            case -616737073:
                return !input.equals("jscinstance") ? input : "jsctooling";
            case -579037304:
                if (!input.equals("react_newarchdefaults")) {
                    return input;
                }
                break;
            case -49345041:
                if (!input.equals("turbomodulejsijni")) {
                    return input;
                }
                break;
            case 3714672:
                if (!input.equals("yoga")) {
                    return input;
                }
                break;
            case 65536138:
                return !input.equals("hermesinstancejni") ? input : "hermestooling";
            case 86183502:
                if (!input.equals("jsijniprofiler")) {
                    return input;
                }
                break;
            case 352552524:
                if (!input.equals("hermes_executor")) {
                    return input;
                }
                break;
            case 688235659:
                if (!input.equals("react_devsupportjni")) {
                    return input;
                }
                break;
            case 716617324:
                if (!input.equals("uimanagerjni")) {
                    return input;
                }
                break;
            case 871152397:
                if (!input.equals("jscexecutor")) {
                    return input;
                }
                break;
            case 1236065886:
                if (!input.equals("jscruntime")) {
                    return input;
                }
                break;
            case 1590431694:
                if (!input.equals("jsinspector")) {
                    return input;
                }
                break;
            case 2016911584:
                if (!input.equals("fabricjni")) {
                    return input;
                }
                break;
            default:
                return input;
        }
    }

    @Override // com.facebook.soloader.ExternalSoMapping
    public void invokeJniOnload(String libraryName) {
        Intrinsics.checkNotNullParameter(libraryName, "libraryName");
        switch (libraryName.hashCode()) {
            case -1793638007:
                if (libraryName.equals("mapbufferjni")) {
                    libmapbufferjni_so();
                    return;
                }
                return;
            case -1624070447:
                if (libraryName.equals("rninstance")) {
                    librninstance_so();
                    return;
                }
                return;
            case -1570429553:
                if (libraryName.equals("reactnativejni")) {
                    libreactnativejni_so();
                    return;
                }
                return;
            case -1454983728:
                if (libraryName.equals("jsctooling")) {
                    libjsctooling_so();
                    return;
                }
                return;
            case -1438915853:
                if (libraryName.equals("reactnativeblob")) {
                    libreactnativeblob_so();
                    return;
                }
                return;
            case -1382694412:
                if (libraryName.equals("react_featureflagsjni")) {
                    libreact_featureflagsjni_so();
                    return;
                }
                return;
            case -1033318826:
                if (libraryName.equals("reactnative")) {
                    libreactnative_so();
                    return;
                }
                return;
            case -616737073:
                if (libraryName.equals("jscinstance")) {
                    libjscinstance_so();
                    return;
                }
                return;
            case -579037304:
                if (libraryName.equals("react_newarchdefaults")) {
                    libreact_newarchdefaults_so();
                    return;
                }
                return;
            case -49345041:
                if (libraryName.equals("turbomodulejsijni")) {
                    libturbomodulejsijni_so();
                    return;
                }
                return;
            case 3714672:
                if (libraryName.equals("yoga")) {
                    libyoga_so();
                    return;
                }
                return;
            case 65536138:
                if (libraryName.equals("hermesinstancejni")) {
                    libhermesinstancejni_so();
                    return;
                }
                return;
            case 86183502:
                if (libraryName.equals("jsijniprofiler")) {
                    libjsijniprofiler_so();
                    return;
                }
                return;
            case 352552524:
                if (libraryName.equals("hermes_executor")) {
                    libhermes_executor_so();
                    return;
                }
                return;
            case 614482404:
                if (libraryName.equals("hermestooling")) {
                    libhermestooling_so();
                    return;
                }
                return;
            case 688235659:
                if (libraryName.equals("react_devsupportjni")) {
                    libreact_devsupportjni_so();
                    return;
                }
                return;
            case 716617324:
                if (libraryName.equals("uimanagerjni")) {
                    libuimanagerjni_so();
                    return;
                }
                return;
            case 871152397:
                if (libraryName.equals("jscexecutor")) {
                    libjscexecutor_so();
                    return;
                }
                return;
            case 1236065886:
                if (libraryName.equals("jscruntime")) {
                    libjscruntime_so();
                    return;
                }
                return;
            case 1590431694:
                if (libraryName.equals("jsinspector")) {
                    libjsinspector_so();
                    return;
                }
                return;
            case 2016911584:
                if (libraryName.equals("fabricjni")) {
                    libfabricjni_so();
                    return;
                }
                return;
            default:
                return;
        }
    }
}
