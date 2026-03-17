package com.facebook.react.devsupport.inspector;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.io.CloseableKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/* compiled from: InspectorNetworkHelper.kt */
@Metadata(d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\bÀ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0007R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082.¢\u0006\u0002\n\u0000¨\u0006\f"}, d2 = {"Lcom/facebook/react/devsupport/inspector/InspectorNetworkHelper;", "", "<init>", "()V", "client", "Lokhttp3/OkHttpClient;", "loadNetworkResource", "", "url", "", "listener", "Lcom/facebook/react/devsupport/inspector/InspectorNetworkRequestListener;", "ReactAndroid_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes.dex */
public final class InspectorNetworkHelper {
    public static final InspectorNetworkHelper INSTANCE = new InspectorNetworkHelper();
    private static OkHttpClient client;

    private InspectorNetworkHelper() {
    }

    @JvmStatic
    public static final void loadNetworkResource(String url, final InspectorNetworkRequestListener listener) {
        Intrinsics.checkNotNullParameter(url, "url");
        Intrinsics.checkNotNullParameter(listener, "listener");
        if (client == null) {
            client = new OkHttpClient.Builder().connectTimeout(10L, TimeUnit.SECONDS).writeTimeout(10L, TimeUnit.SECONDS).readTimeout(0L, TimeUnit.MINUTES).build();
        }
        try {
            Request build = new Request.Builder().url(url).build();
            OkHttpClient okHttpClient = client;
            if (okHttpClient == null) {
                Intrinsics.throwUninitializedPropertyAccessException("client");
                okHttpClient = null;
            }
            okHttpClient.newCall(build).enqueue(new Callback() { // from class: com.facebook.react.devsupport.inspector.InspectorNetworkHelper$loadNetworkResource$1
                @Override // okhttp3.Callback
                public void onFailure(Call call, IOException e) {
                    Intrinsics.checkNotNullParameter(call, "call");
                    Intrinsics.checkNotNullParameter(e, "e");
                    if (call.getCanceled()) {
                        return;
                    }
                    InspectorNetworkRequestListener.this.onError(e.getMessage());
                }

                @Override // okhttp3.Callback
                public void onResponse(Call call, Response response) {
                    Intrinsics.checkNotNullParameter(call, "call");
                    Intrinsics.checkNotNullParameter(response, "response");
                    Headers headers = response.getHeaders();
                    HashMap hashMap = new HashMap();
                    for (String str : headers.names()) {
                        hashMap.put(str, headers.get(str));
                    }
                    InspectorNetworkRequestListener.this.onHeaders(response.getCode(), hashMap);
                    try {
                        InputStream body = response.getBody();
                        InspectorNetworkRequestListener inspectorNetworkRequestListener = InspectorNetworkRequestListener.this;
                        try {
                            ResponseBody responseBody = body;
                            if (responseBody != null) {
                                byte[] bArr = new byte[1024];
                                body = responseBody.byteStream();
                                try {
                                    InputStream inputStream = body;
                                    while (true) {
                                        int read = inputStream.read(bArr);
                                        if (read == -1) {
                                            break;
                                        } else {
                                            inspectorNetworkRequestListener.onData(new String(bArr, 0, read, Charsets.UTF_8));
                                        }
                                    }
                                    Unit unit = Unit.INSTANCE;
                                    CloseableKt.closeFinally(body, null);
                                } finally {
                                }
                            }
                            inspectorNetworkRequestListener.onCompletion();
                            Unit unit2 = Unit.INSTANCE;
                            CloseableKt.closeFinally(body, null);
                        } finally {
                        }
                    } catch (IOException e) {
                        InspectorNetworkRequestListener.this.onError(e.getMessage());
                    }
                }
            });
        } catch (IllegalArgumentException unused) {
            listener.onError("Not a valid URL: " + url);
        }
    }
}
