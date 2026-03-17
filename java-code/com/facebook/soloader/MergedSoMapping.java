package com.facebook.soloader;

import javax.annotation.Nullable;

/* loaded from: classes.dex */
class MergedSoMapping {
    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public static String mapLibName(String str) {
        return null;
    }

    MergedSoMapping() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void invokeJniOnload(String str) {
        throw new IllegalArgumentException("Unknown library: " + str);
    }
}
