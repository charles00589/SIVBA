package com.facebook.imagepipeline.backends.okhttp3;

import android.os.Looper;
import com.facebook.imagepipeline.producers.BaseProducerContextCallbacks;
import java.util.concurrent.Executor;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.Call;

/* compiled from: OkHttpNetworkFetcher.kt */
@Metadata(d1 = {"\u0000\u0011\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000*\u0001\u0000\b\n\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H\u0016¨\u0006\u0004"}, d2 = {"com/facebook/imagepipeline/backends/okhttp3/OkHttpNetworkFetcher$fetchWithRequest$1", "Lcom/facebook/imagepipeline/producers/BaseProducerContextCallbacks;", "onCancellationRequested", "", "imagepipeline-okhttp3_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes.dex */
public final class OkHttpNetworkFetcher$fetchWithRequest$1 extends BaseProducerContextCallbacks {
    final /* synthetic */ Call $call;
    final /* synthetic */ OkHttpNetworkFetcher this$0;

    /* JADX INFO: Access modifiers changed from: package-private */
    public OkHttpNetworkFetcher$fetchWithRequest$1(Call call, OkHttpNetworkFetcher okHttpNetworkFetcher) {
        this.$call = call;
        this.this$0 = okHttpNetworkFetcher;
    }

    @Override // com.facebook.imagepipeline.producers.BaseProducerContextCallbacks, com.facebook.imagepipeline.producers.ProducerContextCallbacks
    public void onCancellationRequested() {
        Executor executor;
        if (Intrinsics.areEqual(Looper.myLooper(), Looper.getMainLooper())) {
            executor = this.this$0.cancellationExecutor;
            final Call call = this.$call;
            executor.execute(new Runnable() { // from class: com.facebook.imagepipeline.backends.okhttp3.OkHttpNetworkFetcher$fetchWithRequest$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    Call.this.cancel();
                }
            });
            return;
        }
        this.$call.cancel();
    }
}
