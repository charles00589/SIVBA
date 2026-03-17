package com.facebook.react.modules.image;

import android.net.Uri;
import android.util.SparseArray;
import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.fbreact.specs.NativeImageLoaderAndroidSpec;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.BaseJavaModule;
import com.facebook.react.bridge.GuardedAsyncTask;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.modules.fresco.ReactNetworkImageRequest;
import com.facebook.react.views.image.ReactCallerContextFactory;
import com.facebook.react.views.imagehelper.ImageSource;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ImageLoaderModule.kt */
@ReactModule(name = "ImageLoader")
@Metadata(d1 = {"\u0000n\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0007\b\u0007\u0018\u0000 42\u00020\u00012\u00020\u0002:\u00014B\u0011\b\u0016\u0012\u0006\u0010\u0003\u001a\u00020\u0004¢\u0006\u0004\b\u0005\u0010\u0006B\u001b\b\u0016\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\b¢\u0006\u0004\b\u0005\u0010\tB!\b\u0016\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\u0006\u0010\f\u001a\u00020\r¢\u0006\u0004\b\u0005\u0010\u000eJ\u001a\u0010\u001c\u001a\u00020\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u001f2\u0006\u0010 \u001a\u00020!H\u0017J$\u0010\"\u001a\u00020\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u001f2\b\u0010#\u001a\u0004\u0018\u00010$2\u0006\u0010 \u001a\u00020!H\u0017J\"\u0010%\u001a\u00020\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u001f2\u0006\u0010&\u001a\u00020'2\u0006\u0010 \u001a\u00020!H\u0016J\u0010\u0010(\u001a\u00020\u001d2\u0006\u0010)\u001a\u00020'H\u0016J\u0018\u0010*\u001a\u00020\u001d2\u0006\u0010+\u001a\u00020,2\u0006\u0010 \u001a\u00020!H\u0017J \u0010-\u001a\u00020\u001d2\u0006\u0010)\u001a\u00020.2\u000e\u0010/\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00140\u0013H\u0002J\u001a\u00100\u001a\f\u0012\u0006\u0012\u0004\u0018\u00010\u0014\u0018\u00010\u00132\u0006\u0010)\u001a\u00020.H\u0002J\b\u00101\u001a\u00020\u001dH\u0016J\b\u00102\u001a\u00020\u001dH\u0016J\b\u00103\u001a\u00020\u001dH\u0016R\u0010\u0010\u000f\u001a\u0004\u0018\u00010\u000bX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u001c\u0010\u0011\u001a\u0010\u0012\f\u0012\n\u0012\u0006\u0012\u0004\u0018\u00010\u00140\u00130\u0012X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\f\u001a\u0004\u0018\u00010\rX\u0082\u000e¢\u0006\u0002\n\u0000R\u0018\u0010\u0007\u001a\u0004\u0018\u00010\b8BX\u0082\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R$\u0010\n\u001a\u00020\u000b2\u0006\u0010\u0017\u001a\u00020\u000b8B@BX\u0082\u000e¢\u0006\f\u001a\u0004\b\u0018\u0010\u0019\"\u0004\b\u001a\u0010\u001b¨\u00065"}, d2 = {"Lcom/facebook/react/modules/image/ImageLoaderModule;", "Lcom/facebook/fbreact/specs/NativeImageLoaderAndroidSpec;", "Lcom/facebook/react/bridge/LifecycleEventListener;", "reactContext", "Lcom/facebook/react/bridge/ReactApplicationContext;", "<init>", "(Lcom/facebook/react/bridge/ReactApplicationContext;)V", "callerContext", "", "(Lcom/facebook/react/bridge/ReactApplicationContext;Ljava/lang/Object;)V", "imagePipeline", "Lcom/facebook/imagepipeline/core/ImagePipeline;", "callerContextFactory", "Lcom/facebook/react/views/image/ReactCallerContextFactory;", "(Lcom/facebook/react/bridge/ReactApplicationContext;Lcom/facebook/imagepipeline/core/ImagePipeline;Lcom/facebook/react/views/image/ReactCallerContextFactory;)V", "_imagePipeline", "enqueuedRequestMonitor", "enqueuedRequests", "Landroid/util/SparseArray;", "Lcom/facebook/datasource/DataSource;", "Ljava/lang/Void;", "getCallerContext", "()Ljava/lang/Object;", "value", "getImagePipeline", "()Lcom/facebook/imagepipeline/core/ImagePipeline;", "setImagePipeline", "(Lcom/facebook/imagepipeline/core/ImagePipeline;)V", "getSize", "", "uriString", "", BaseJavaModule.METHOD_TYPE_PROMISE, "Lcom/facebook/react/bridge/Promise;", "getSizeWithHeaders", "headers", "Lcom/facebook/react/bridge/ReadableMap;", "prefetchImage", "requestIdAsDouble", "", "abortRequest", "requestId", "queryCache", "uris", "Lcom/facebook/react/bridge/ReadableArray;", "registerRequest", "", "request", "removeRequest", "onHostResume", "onHostPause", "onHostDestroy", "Companion", "ReactAndroid_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes.dex */
public final class ImageLoaderModule extends NativeImageLoaderAndroidSpec implements LifecycleEventListener {
    private static final String ERROR_GET_SIZE_FAILURE = "E_GET_SIZE_FAILURE";
    private static final String ERROR_INVALID_URI = "E_INVALID_URI";
    private static final String ERROR_PREFETCH_FAILURE = "E_PREFETCH_FAILURE";
    public static final String NAME = "ImageLoader";
    private ImagePipeline _imagePipeline;
    private final Object callerContext;
    private ReactCallerContextFactory callerContextFactory;
    private final Object enqueuedRequestMonitor;
    private final SparseArray<DataSource<Void>> enqueuedRequests;

    @Override // com.facebook.react.bridge.LifecycleEventListener
    public void onHostPause() {
    }

    @Override // com.facebook.react.bridge.LifecycleEventListener
    public void onHostResume() {
    }

    private final Object getCallerContext() {
        Object orCreateCallerContext;
        ReactCallerContextFactory reactCallerContextFactory = this.callerContextFactory;
        return (reactCallerContextFactory == null || (orCreateCallerContext = reactCallerContextFactory.getOrCreateCallerContext("", "")) == null) ? this.callerContext : orCreateCallerContext;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final ImagePipeline getImagePipeline() {
        ImagePipeline imagePipeline = this._imagePipeline;
        if (imagePipeline != null) {
            return imagePipeline;
        }
        ImagePipeline imagePipeline2 = Fresco.getImagePipeline();
        Intrinsics.checkNotNullExpressionValue(imagePipeline2, "getImagePipeline(...)");
        return imagePipeline2;
    }

    private final void setImagePipeline(ImagePipeline imagePipeline) {
        this._imagePipeline = imagePipeline;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public ImageLoaderModule(ReactApplicationContext reactContext) {
        super(reactContext);
        Intrinsics.checkNotNullParameter(reactContext, "reactContext");
        this.enqueuedRequestMonitor = new Object();
        this.enqueuedRequests = new SparseArray<>();
        this.callerContext = this;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public ImageLoaderModule(ReactApplicationContext reactContext, Object obj) {
        super(reactContext);
        Intrinsics.checkNotNullParameter(reactContext, "reactContext");
        this.enqueuedRequestMonitor = new Object();
        this.enqueuedRequests = new SparseArray<>();
        this.callerContext = obj;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public ImageLoaderModule(ReactApplicationContext reactContext, ImagePipeline imagePipeline, ReactCallerContextFactory callerContextFactory) {
        super(reactContext);
        Intrinsics.checkNotNullParameter(reactContext, "reactContext");
        Intrinsics.checkNotNullParameter(imagePipeline, "imagePipeline");
        Intrinsics.checkNotNullParameter(callerContextFactory, "callerContextFactory");
        this.enqueuedRequestMonitor = new Object();
        this.enqueuedRequests = new SparseArray<>();
        this.callerContextFactory = callerContextFactory;
        setImagePipeline(imagePipeline);
        this.callerContext = null;
    }

    @Override // com.facebook.fbreact.specs.NativeImageLoaderAndroidSpec
    @ReactMethod
    public void getSize(String uriString, final Promise promise) {
        Intrinsics.checkNotNullParameter(promise, "promise");
        String str = uriString;
        if (str == null || str.length() == 0) {
            promise.reject(ERROR_INVALID_URI, "Cannot get the size of an image for an empty URI");
            return;
        }
        ReactApplicationContext reactApplicationContext = getReactApplicationContext();
        Intrinsics.checkNotNullExpressionValue(reactApplicationContext, "getReactApplicationContext(...)");
        ImageRequest build = ImageRequestBuilder.newBuilderWithSource(new ImageSource(reactApplicationContext, uriString, 0.0d, 0.0d, null, 28, null).getUri()).build();
        Intrinsics.checkNotNullExpressionValue(build, "build(...)");
        getImagePipeline().fetchDecodedImage(build, getCallerContext()).subscribe(new BaseDataSubscriber<CloseableReference<CloseableImage>>() { // from class: com.facebook.react.modules.image.ImageLoaderModule$getSize$dataSubscriber$1
            @Override // com.facebook.datasource.BaseDataSubscriber
            protected void onNewResultImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                Intrinsics.checkNotNullParameter(dataSource, "dataSource");
                if (dataSource.isFinished()) {
                    CloseableReference<CloseableImage> result = dataSource.getResult();
                    try {
                        if (result == null) {
                            Promise.this.reject("E_GET_SIZE_FAILURE", "Failed to get the size of the image");
                            return;
                        }
                        try {
                            CloseableImage closeableImage = result.get();
                            Intrinsics.checkNotNullExpressionValue(closeableImage, "get(...)");
                            CloseableImage closeableImage2 = closeableImage;
                            WritableMap createMap = Arguments.createMap();
                            Intrinsics.checkNotNullExpressionValue(createMap, "createMap(...)");
                            createMap.putInt("width", closeableImage2.getWidth());
                            createMap.putInt("height", closeableImage2.getHeight());
                            Promise.this.resolve(createMap);
                        } catch (Exception e) {
                            Promise.this.reject("E_GET_SIZE_FAILURE", e);
                        }
                    } finally {
                        CloseableReference.closeSafely(result);
                    }
                }
            }

            @Override // com.facebook.datasource.BaseDataSubscriber
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                Intrinsics.checkNotNullParameter(dataSource, "dataSource");
                Promise.this.reject("E_GET_SIZE_FAILURE", dataSource.getFailureCause());
            }
        }, CallerThreadExecutor.getInstance());
    }

    @Override // com.facebook.fbreact.specs.NativeImageLoaderAndroidSpec
    @ReactMethod
    public void getSizeWithHeaders(String uriString, ReadableMap headers, final Promise promise) {
        Intrinsics.checkNotNullParameter(promise, "promise");
        String str = uriString;
        if (str == null || str.length() == 0) {
            promise.reject(ERROR_INVALID_URI, "Cannot get the size of an image for an empty URI");
            return;
        }
        ReactApplicationContext reactApplicationContext = getReactApplicationContext();
        Intrinsics.checkNotNullExpressionValue(reactApplicationContext, "getReactApplicationContext(...)");
        ImageRequestBuilder newBuilderWithSource = ImageRequestBuilder.newBuilderWithSource(new ImageSource(reactApplicationContext, uriString, 0.0d, 0.0d, null, 28, null).getUri());
        Intrinsics.checkNotNullExpressionValue(newBuilderWithSource, "newBuilderWithSource(...)");
        getImagePipeline().fetchDecodedImage(ReactNetworkImageRequest.Companion.fromBuilderWithHeaders$default(ReactNetworkImageRequest.INSTANCE, newBuilderWithSource, headers, null, 4, null), getCallerContext()).subscribe(new BaseDataSubscriber<CloseableReference<CloseableImage>>() { // from class: com.facebook.react.modules.image.ImageLoaderModule$getSizeWithHeaders$dataSubscriber$1
            @Override // com.facebook.datasource.BaseDataSubscriber
            protected void onNewResultImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                Intrinsics.checkNotNullParameter(dataSource, "dataSource");
                if (dataSource.isFinished()) {
                    CloseableReference<CloseableImage> result = dataSource.getResult();
                    try {
                        if (result == null) {
                            Promise.this.reject("E_GET_SIZE_FAILURE", "Failed to get the size of the image");
                            return;
                        }
                        try {
                            CloseableImage closeableImage = result.get();
                            Intrinsics.checkNotNullExpressionValue(closeableImage, "get(...)");
                            CloseableImage closeableImage2 = closeableImage;
                            WritableMap createMap = Arguments.createMap();
                            Intrinsics.checkNotNullExpressionValue(createMap, "createMap(...)");
                            createMap.putInt("width", closeableImage2.getWidth());
                            createMap.putInt("height", closeableImage2.getHeight());
                            Promise.this.resolve(createMap);
                        } catch (Exception e) {
                            Promise.this.reject("E_GET_SIZE_FAILURE", e);
                        }
                    } finally {
                        CloseableReference.closeSafely(result);
                    }
                }
            }

            @Override // com.facebook.datasource.BaseDataSubscriber
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                Intrinsics.checkNotNullParameter(dataSource, "dataSource");
                Promise.this.reject("E_GET_SIZE_FAILURE", dataSource.getFailureCause());
            }
        }, CallerThreadExecutor.getInstance());
    }

    @Override // com.facebook.fbreact.specs.NativeImageLoaderAndroidSpec
    public void prefetchImage(String uriString, double requestIdAsDouble, final Promise promise) {
        Intrinsics.checkNotNullParameter(promise, "promise");
        final int i = (int) requestIdAsDouble;
        String str = uriString;
        if (str == null || str.length() == 0) {
            promise.reject(ERROR_INVALID_URI, "Cannot prefetch an image for an empty URI");
            return;
        }
        ImageRequest build = ImageRequestBuilder.newBuilderWithSource(Uri.parse(uriString)).build();
        Intrinsics.checkNotNullExpressionValue(build, "build(...)");
        DataSource<Void> prefetchToDiskCache = getImagePipeline().prefetchToDiskCache(build, getCallerContext());
        BaseDataSubscriber<Void> baseDataSubscriber = new BaseDataSubscriber<Void>() { // from class: com.facebook.react.modules.image.ImageLoaderModule$prefetchImage$prefetchSubscriber$1
            @Override // com.facebook.datasource.BaseDataSubscriber
            protected void onNewResultImpl(DataSource<Void> dataSource) {
                Intrinsics.checkNotNullParameter(dataSource, "dataSource");
                if (dataSource.isFinished()) {
                    try {
                        try {
                            ImageLoaderModule.this.removeRequest(i);
                            promise.resolve(true);
                        } catch (Exception e) {
                            promise.reject("E_PREFETCH_FAILURE", e);
                        }
                    } finally {
                        dataSource.close();
                    }
                }
            }

            @Override // com.facebook.datasource.BaseDataSubscriber
            protected void onFailureImpl(DataSource<Void> dataSource) {
                Intrinsics.checkNotNullParameter(dataSource, "dataSource");
                try {
                    ImageLoaderModule.this.removeRequest(i);
                    promise.reject("E_PREFETCH_FAILURE", dataSource.getFailureCause());
                } finally {
                    dataSource.close();
                }
            }
        };
        registerRequest(i, prefetchToDiskCache);
        prefetchToDiskCache.subscribe(baseDataSubscriber, CallerThreadExecutor.getInstance());
    }

    @Override // com.facebook.fbreact.specs.NativeImageLoaderAndroidSpec
    public void abortRequest(double requestId) {
        DataSource<Void> removeRequest = removeRequest((int) requestId);
        if (removeRequest != null) {
            removeRequest.close();
        }
    }

    /* JADX WARN: Type inference failed for: r1v0, types: [com.facebook.react.modules.image.ImageLoaderModule$queryCache$1] */
    @Override // com.facebook.fbreact.specs.NativeImageLoaderAndroidSpec
    @ReactMethod
    public void queryCache(final ReadableArray uris, final Promise promise) {
        Intrinsics.checkNotNullParameter(uris, "uris");
        Intrinsics.checkNotNullParameter(promise, "promise");
        final ReactApplicationContext reactApplicationContext = getReactApplicationContext();
        new GuardedAsyncTask<Void, Void>(uris, promise, reactApplicationContext) { // from class: com.facebook.react.modules.image.ImageLoaderModule$queryCache$1
            final /* synthetic */ Promise $promise;
            final /* synthetic */ ReadableArray $uris;

            /* JADX INFO: Access modifiers changed from: package-private */
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(reactApplicationContext);
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.facebook.react.bridge.GuardedAsyncTask
            public void doInBackgroundGuarded(Void... params) {
                ImagePipeline imagePipeline;
                Intrinsics.checkNotNullParameter(params, "params");
                WritableMap createMap = Arguments.createMap();
                Intrinsics.checkNotNullExpressionValue(createMap, "createMap(...)");
                imagePipeline = ImageLoaderModule.this.getImagePipeline();
                int size = this.$uris.size();
                for (int i = 0; i < size; i++) {
                    String string = this.$uris.getString(i);
                    String str = string;
                    if (str != null && str.length() != 0) {
                        Uri parse = Uri.parse(string);
                        if (imagePipeline.isInBitmapMemoryCache(parse)) {
                            createMap.putString(string, "memory");
                        } else if (imagePipeline.isInDiskCacheSync(parse)) {
                            createMap.putString(string, "disk");
                        }
                    }
                }
                this.$promise.resolve(createMap);
            }
        }.executeOnExecutor(GuardedAsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    private final void registerRequest(int requestId, DataSource<Void> request) {
        synchronized (this.enqueuedRequestMonitor) {
            this.enqueuedRequests.put(requestId, request);
            Unit unit = Unit.INSTANCE;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final DataSource<Void> removeRequest(int requestId) {
        DataSource<Void> dataSource;
        synchronized (this.enqueuedRequestMonitor) {
            dataSource = this.enqueuedRequests.get(requestId);
            this.enqueuedRequests.remove(requestId);
        }
        return dataSource;
    }

    @Override // com.facebook.react.bridge.LifecycleEventListener
    public void onHostDestroy() {
        synchronized (this.enqueuedRequestMonitor) {
            int size = this.enqueuedRequests.size();
            for (int i = 0; i < size; i++) {
                DataSource<Void> valueAt = this.enqueuedRequests.valueAt(i);
                Intrinsics.checkNotNullExpressionValue(valueAt, "valueAt(...)");
                valueAt.close();
            }
            this.enqueuedRequests.clear();
            Unit unit = Unit.INSTANCE;
        }
    }
}
