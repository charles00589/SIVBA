package com.facebook.react.modules.network;

import com.facebook.common.util.UriUtil;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import java.net.SocketTimeoutException;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ResponseUtil.kt */
@Metadata(d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0003\n\u0002\b\u0006\bÀ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J*\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000bH\u0007J4\u0010\r\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u00072\u0006\u0010\b\u001a\u00020\t2\b\u0010\u000e\u001a\u0004\u0018\u00010\u000f2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000bH\u0007J*\u0010\u0010\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000bH\u0007J$\u0010\u0011\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u00072\u0006\u0010\b\u001a\u00020\t2\b\u0010\u000e\u001a\u0004\u0018\u00010\u000fH\u0007J$\u0010\u0011\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u00072\u0006\u0010\b\u001a\u00020\t2\b\u0010\u000e\u001a\u0004\u0018\u00010\u0012H\u0007J.\u0010\u0013\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u00072\u0006\u0010\b\u001a\u00020\t2\b\u0010\u0014\u001a\u0004\u0018\u00010\u000f2\b\u0010\u0015\u001a\u0004\u0018\u00010\u0016H\u0007J\u001a\u0010\u0017\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u00072\u0006\u0010\b\u001a\u00020\tH\u0007J6\u0010\u0018\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u0019\u001a\u00020\t2\b\u0010\u001a\u001a\u0004\u0018\u00010\u00122\b\u0010\u001b\u001a\u0004\u0018\u00010\u000fH\u0007¨\u0006\u001c"}, d2 = {"Lcom/facebook/react/modules/network/ResponseUtil;", "", "<init>", "()V", "onDataSend", "", "reactContext", "Lcom/facebook/react/bridge/ReactApplicationContext;", "requestId", "", "progress", "", "total", "onIncrementalDataReceived", UriUtil.DATA_SCHEME, "", "onDataReceivedProgress", "onDataReceived", "Lcom/facebook/react/bridge/WritableMap;", "onRequestError", "error", "e", "", "onRequestSuccess", "onResponseReceived", "statusCode", "headers", "url", "ReactAndroid_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes.dex */
public final class ResponseUtil {
    public static final ResponseUtil INSTANCE = new ResponseUtil();

    private ResponseUtil() {
    }

    @JvmStatic
    public static final void onDataSend(ReactApplicationContext reactContext, int requestId, long progress, long total) {
        if (reactContext != null) {
            WritableArray createArray = Arguments.createArray();
            createArray.pushInt(requestId);
            createArray.pushInt((int) progress);
            createArray.pushInt((int) total);
            Unit unit = Unit.INSTANCE;
            reactContext.emitDeviceEvent("didSendNetworkData", createArray);
        }
    }

    @JvmStatic
    public static final void onIncrementalDataReceived(ReactApplicationContext reactContext, int requestId, String data, long progress, long total) {
        if (reactContext != null) {
            WritableArray createArray = Arguments.createArray();
            createArray.pushInt(requestId);
            createArray.pushString(data);
            createArray.pushInt((int) progress);
            createArray.pushInt((int) total);
            Unit unit = Unit.INSTANCE;
            reactContext.emitDeviceEvent("didReceiveNetworkIncrementalData", createArray);
        }
    }

    @JvmStatic
    public static final void onDataReceivedProgress(ReactApplicationContext reactContext, int requestId, long progress, long total) {
        if (reactContext != null) {
            WritableArray createArray = Arguments.createArray();
            createArray.pushInt(requestId);
            createArray.pushInt((int) progress);
            createArray.pushInt((int) total);
            Unit unit = Unit.INSTANCE;
            reactContext.emitDeviceEvent("didReceiveNetworkDataProgress", createArray);
        }
    }

    @JvmStatic
    public static final void onDataReceived(ReactApplicationContext reactContext, int requestId, String data) {
        if (reactContext != null) {
            WritableArray createArray = Arguments.createArray();
            createArray.pushInt(requestId);
            createArray.pushString(data);
            Unit unit = Unit.INSTANCE;
            reactContext.emitDeviceEvent("didReceiveNetworkData", createArray);
        }
    }

    @JvmStatic
    public static final void onDataReceived(ReactApplicationContext reactContext, int requestId, WritableMap data) {
        if (reactContext != null) {
            WritableArray createArray = Arguments.createArray();
            createArray.pushInt(requestId);
            createArray.pushMap(data);
            Unit unit = Unit.INSTANCE;
            reactContext.emitDeviceEvent("didReceiveNetworkData", createArray);
        }
    }

    @JvmStatic
    public static final void onRequestError(ReactApplicationContext reactContext, int requestId, String error, Throwable e) {
        if (reactContext != null) {
            WritableArray createArray = Arguments.createArray();
            createArray.pushInt(requestId);
            createArray.pushString(error);
            if (Intrinsics.areEqual(e != null ? e.getClass() : null, SocketTimeoutException.class)) {
                createArray.pushBoolean(true);
            }
            Unit unit = Unit.INSTANCE;
            reactContext.emitDeviceEvent("didCompleteNetworkResponse", createArray);
        }
    }

    @JvmStatic
    public static final void onRequestSuccess(ReactApplicationContext reactContext, int requestId) {
        if (reactContext != null) {
            WritableArray createArray = Arguments.createArray();
            createArray.pushInt(requestId);
            createArray.pushNull();
            Unit unit = Unit.INSTANCE;
            reactContext.emitDeviceEvent("didCompleteNetworkResponse", createArray);
        }
    }

    @JvmStatic
    public static final void onResponseReceived(ReactApplicationContext reactContext, int requestId, int statusCode, WritableMap headers, String url) {
        if (reactContext != null) {
            WritableArray createArray = Arguments.createArray();
            createArray.pushInt(requestId);
            createArray.pushInt(statusCode);
            createArray.pushMap(headers);
            createArray.pushString(url);
            Unit unit = Unit.INSTANCE;
            reactContext.emitDeviceEvent("didReceiveNetworkResponse", createArray);
        }
    }
}
