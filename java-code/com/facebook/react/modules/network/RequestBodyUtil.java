package com.facebook.react.modules.network;

import android.content.Context;
import android.net.Uri;
import android.util.Base64;
import com.facebook.common.logging.FLog;
import com.facebook.common.util.UriUtil;
import com.facebook.react.common.ReactConstants;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.List;
import java.util.ListIterator;
import java.util.zip.GZIPOutputStream;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.io.CloseableKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.ByteString;
import okio.DeprecatedUpgrade;
import okio.Source;

/* compiled from: RequestBodyUtil.kt */
@Metadata(d1 = {"\u0000\\\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\bÀ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\b\u001a\u00020\t2\b\u0010\n\u001a\u0004\u0018\u00010\u0005H\u0007J\u001a\u0010\u000b\u001a\u0004\u0018\u00010\f2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0005H\u0007J\u0018\u0010\u0010\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u0011\u001a\u00020\u0012H\u0002J\u001c\u0010\u0013\u001a\u0004\u0018\u00010\u00142\b\u0010\u0015\u001a\u0004\u0018\u00010\u00162\u0006\u0010\u0017\u001a\u00020\u0005H\u0007J\u0010\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001bH\u0002J\u001a\u0010\u001c\u001a\u00020\u00142\b\u0010\u0015\u001a\u0004\u0018\u00010\u00162\u0006\u0010\u001d\u001a\u00020\fH\u0007J\u0018\u0010\u001e\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020\u00142\u0006\u0010!\u001a\u00020\"H\u0007J\u0012\u0010#\u001a\u0004\u0018\u00010\u00142\u0006\u0010$\u001a\u00020\u0005H\u0007R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000¨\u0006%"}, d2 = {"Lcom/facebook/react/modules/network/RequestBodyUtil;", "", "<init>", "()V", "CONTENT_ENCODING_GZIP", "", "NAME", "TEMP_FILE_SUFFIX", "isGzipEncoding", "", "encodingType", "getFileInputStream", "Ljava/io/InputStream;", "context", "Landroid/content/Context;", "fileContentUriStr", "getDownloadFileInputStream", "uri", "Landroid/net/Uri;", "createGzip", "Lokhttp3/RequestBody;", "mediaType", "Lokhttp3/MediaType;", "body", "closeQuietly", "", "source", "Lokio/Source;", "create", "inputStream", "createProgressRequest", "Lcom/facebook/react/modules/network/ProgressRequestBody;", "requestBody", "listener", "Lcom/facebook/react/modules/network/ProgressListener;", "getEmptyBody", "method", "ReactAndroid_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes.dex */
public final class RequestBodyUtil {
    private static final String CONTENT_ENCODING_GZIP = "gzip";
    public static final RequestBodyUtil INSTANCE = new RequestBodyUtil();
    private static final String NAME = "RequestBodyUtil";
    private static final String TEMP_FILE_SUFFIX = "temp";

    private RequestBodyUtil() {
    }

    @JvmStatic
    public static final boolean isGzipEncoding(String encodingType) {
        return StringsKt.equals(CONTENT_ENCODING_GZIP, encodingType, true);
    }

    @JvmStatic
    public static final InputStream getFileInputStream(Context context, String fileContentUriStr) {
        List emptyList;
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(fileContentUriStr, "fileContentUriStr");
        try {
            Uri parse = Uri.parse(fileContentUriStr);
            String scheme = parse.getScheme();
            if (scheme != null && StringsKt.startsWith$default(scheme, UriUtil.HTTP_SCHEME, false, 2, (Object) null)) {
                RequestBodyUtil requestBodyUtil = INSTANCE;
                Intrinsics.checkNotNull(parse);
                return requestBodyUtil.getDownloadFileInputStream(context, parse);
            }
            if (StringsKt.startsWith$default(fileContentUriStr, "data:", false, 2, (Object) null)) {
                List<String> split = new Regex(",").split(fileContentUriStr, 0);
                if (!split.isEmpty()) {
                    ListIterator<String> listIterator = split.listIterator(split.size());
                    while (listIterator.hasPrevious()) {
                        if (listIterator.previous().length() != 0) {
                            emptyList = CollectionsKt.take(split, listIterator.nextIndex() + 1);
                            break;
                        }
                    }
                }
                emptyList = CollectionsKt.emptyList();
                return new ByteArrayInputStream(Base64.decode(((String[]) emptyList.toArray(new String[0]))[1], 0));
            }
            return context.getContentResolver().openInputStream(parse);
        } catch (Exception e) {
            FLog.e(ReactConstants.TAG, "Could not retrieve file for contentUri " + fileContentUriStr, e);
            return null;
        }
    }

    private final InputStream getDownloadFileInputStream(Context context, Uri uri) throws IOException {
        File createTempFile = File.createTempFile(NAME, TEMP_FILE_SUFFIX, context.getApplicationContext().getCacheDir());
        createTempFile.deleteOnExit();
        URL url = new URL(uri.toString());
        ReadableByteChannel fileOutputStream = new FileOutputStream(createTempFile);
        try {
            FileOutputStream fileOutputStream2 = fileOutputStream;
            fileOutputStream = url.openStream();
            try {
                fileOutputStream = Channels.newChannel(fileOutputStream);
                try {
                    fileOutputStream2.getChannel().transferFrom(fileOutputStream, 0L, Long.MAX_VALUE);
                    FileInputStream fileInputStream = new FileInputStream(createTempFile);
                    CloseableKt.closeFinally(fileOutputStream, null);
                    CloseableKt.closeFinally(fileOutputStream, null);
                    CloseableKt.closeFinally(fileOutputStream, null);
                    return fileInputStream;
                } finally {
                }
            } finally {
            }
        } finally {
        }
    }

    @JvmStatic
    public static final RequestBody createGzip(MediaType mediaType, String body) {
        Intrinsics.checkNotNullParameter(body, "body");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            GZIPOutputStream gZIPOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            byte[] bytes = body.getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes, "getBytes(...)");
            gZIPOutputStream.write(bytes);
            gZIPOutputStream.close();
            RequestBody.Companion companion = RequestBody.INSTANCE;
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            Intrinsics.checkNotNullExpressionValue(byteArray, "toByteArray(...)");
            return RequestBody.Companion.create$default(companion, mediaType, byteArray, 0, 0, 12, (Object) null);
        } catch (IOException unused) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void closeQuietly(Source source) {
        try {
            source.close();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception unused) {
        }
    }

    @JvmStatic
    public static final RequestBody create(final MediaType mediaType, final InputStream inputStream) {
        Intrinsics.checkNotNullParameter(inputStream, "inputStream");
        return new RequestBody() { // from class: com.facebook.react.modules.network.RequestBodyUtil$create$1
            @Override // okhttp3.RequestBody
            /* renamed from: contentType, reason: from getter */
            public MediaType get$mediaType() {
                return MediaType.this;
            }

            @Override // okhttp3.RequestBody
            public long contentLength() {
                try {
                    return inputStream.available();
                } catch (IOException unused) {
                    return 0L;
                }
            }

            @Override // okhttp3.RequestBody
            public void writeTo(BufferedSink sink) throws IOException {
                Intrinsics.checkNotNullParameter(sink, "sink");
                Source source = null;
                try {
                    source = DeprecatedUpgrade.getOkio().source(inputStream);
                    sink.writeAll(source);
                } finally {
                    if (source != null) {
                        RequestBodyUtil.INSTANCE.closeQuietly(source);
                    }
                }
            }
        };
    }

    @JvmStatic
    public static final ProgressRequestBody createProgressRequest(RequestBody requestBody, ProgressListener listener) {
        Intrinsics.checkNotNullParameter(requestBody, "requestBody");
        Intrinsics.checkNotNullParameter(listener, "listener");
        return new ProgressRequestBody(requestBody, listener);
    }

    @JvmStatic
    public static final RequestBody getEmptyBody(String method) {
        Intrinsics.checkNotNullParameter(method, "method");
        int hashCode = method.hashCode();
        if (hashCode != 79599) {
            if (hashCode != 2461856) {
                if (hashCode != 75900968 || !method.equals("PATCH")) {
                    return null;
                }
            } else if (!method.equals("POST")) {
                return null;
            }
        } else if (!method.equals("PUT")) {
            return null;
        }
        return RequestBody.INSTANCE.create((MediaType) null, ByteString.EMPTY);
    }
}
