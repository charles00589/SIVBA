package com.vulnerablebankapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import androidx.autofill.HintConstants;
import com.facebook.react.ReactActivity;
import com.facebook.react.ReactActivityDelegate;
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint;
import com.facebook.react.defaults.DefaultReactActivityDelegate;
import kotlin.Metadata;

/* compiled from: MainActivity.kt */
@Metadata(d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0004\u001a\u00020\u0005H\u0014J\u0012\u0010\u0006\u001a\u00020\u00072\b\u0010\b\u001a\u0004\u0018\u00010\tH\u0014J\b\u0010\n\u001a\u00020\u000bH\u0014¨\u0006\f"}, d2 = {"Lcom/vulnerablebankapp/MainActivity;", "Lcom/facebook/react/ReactActivity;", "<init>", "()V", "getMainComponentName", "", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "createReactActivityDelegate", "Lcom/facebook/react/ReactActivityDelegate;", "app_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes.dex */
public final class MainActivity extends ReactActivity {
    @Override // com.facebook.react.ReactActivity
    protected String getMainComponentName() {
        return "VulnerableBankApp";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.facebook.react.ReactActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Secrets", Secrets.HARDCODED_ADMIN_JWT);
        SharedPreferences.Editor edit = getSharedPreferences("VulnBankPrefs", 0).edit();
        edit.putString(HintConstants.AUTOFILL_HINT_USERNAME, "admin");
        edit.putString(HintConstants.AUTOFILL_HINT_PASSWORD, "admin123");
        edit.putString("balance", "$999999");
        edit.putString("debug_flag", "FLAG{HardcodedSecretsAreBad}");
        edit.apply();
    }

    @Override // com.facebook.react.ReactActivity
    protected ReactActivityDelegate createReactActivityDelegate() {
        return new DefaultReactActivityDelegate(this, getMainComponentName(), DefaultNewArchitectureEntryPoint.getFabricEnabled());
    }
}
