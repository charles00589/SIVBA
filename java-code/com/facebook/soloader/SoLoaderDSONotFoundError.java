package com.facebook.soloader;

import android.content.Context;
import com.facebook.react.views.textinput.ReactEditTextInputConnectionWrapper;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public class SoLoaderDSONotFoundError extends SoLoaderULError {
    public SoLoaderDSONotFoundError(String str) {
        super(str);
    }

    public SoLoaderDSONotFoundError(String str, String str2) {
        super(str, str2);
    }

    public static SoLoaderDSONotFoundError create(String str, @Nullable Context context, SoSource[] soSourceArr) {
        StringBuilder append = new StringBuilder("couldn't find DSO to load: ").append(str);
        append.append("\n\texisting SO sources: ");
        for (int i = 0; i < soSourceArr.length; i++) {
            append.append("\n\t\tSoSource ").append(i).append(": ").append(soSourceArr[i].toString());
        }
        if (context != null) {
            append.append("\n\tNative lib dir: ").append(context.getApplicationInfo().nativeLibraryDir).append(ReactEditTextInputConnectionWrapper.NEWLINE_RAW_VALUE);
        }
        return new SoLoaderDSONotFoundError(str, append.toString());
    }
}
