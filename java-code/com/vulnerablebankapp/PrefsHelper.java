package com.vulnerablebankapp;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.autofill.HintConstants;

/* loaded from: classes.dex */
public class PrefsHelper {
    private static final String PREFS_NAME = "VulnBankPrefs";

    public static void storeSecrets(Context context) {
        SharedPreferences.Editor edit = context.getSharedPreferences(PREFS_NAME, 0).edit();
        edit.putString(HintConstants.AUTOFILL_HINT_USERNAME, "admin");
        edit.putString(HintConstants.AUTOFILL_HINT_PASSWORD, "admin123");
        edit.putString("balance", "$999999");
        edit.putString("debug_flag", "FLAG{HardcodedSecretsAreBad}");
        edit.apply();
    }
}
