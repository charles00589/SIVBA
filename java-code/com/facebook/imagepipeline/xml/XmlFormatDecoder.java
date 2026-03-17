package com.facebook.imagepipeline.xml;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import androidx.core.content.res.ResourcesCompat;
import com.facebook.common.logging.FLog;
import com.facebook.common.util.UriUtil;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.facebook.imagepipeline.decoder.ImageDecoder;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.DefaultCloseableXml;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.image.QualityInfo;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

/* compiled from: XmlFormatDecoder.kt */
@Metadata(d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010%\n\u0002\u0010\u000e\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0000\u0018\u0000 \u00182\u00020\u0001:\u0001\u0018B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J*\u0010\n\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\t2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0016J\u0010\u0010\u0013\u001a\u00020\t2\u0006\u0010\u0014\u001a\u00020\bH\u0002J\u0010\u0010\u0015\u001a\u00020\t2\u0006\u0010\u0016\u001a\u00020\u0017H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\t0\u0007X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0019"}, d2 = {"Lcom/facebook/imagepipeline/xml/XmlFormatDecoder;", "Lcom/facebook/imagepipeline/decoder/ImageDecoder;", "resources", "Landroid/content/res/Resources;", "<init>", "(Landroid/content/res/Resources;)V", "resourceIdCache", "", "", "", "decode", "Lcom/facebook/imagepipeline/image/CloseableImage;", "encodedImage", "Lcom/facebook/imagepipeline/image/EncodedImage;", "length", "qualityInfo", "Lcom/facebook/imagepipeline/image/QualityInfo;", "options", "Lcom/facebook/imagepipeline/common/ImageDecodeOptions;", "getXmlResourceId", "xmlResourceName", "parseImageSourceResourceId", "xmlResource", "Landroid/net/Uri;", "Companion", "imagepipeline_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes.dex */
public final class XmlFormatDecoder implements ImageDecoder {
    private static final String TAG = "XmlFormatDecoder";
    private final Map<String, Integer> resourceIdCache;
    private final Resources resources;

    public XmlFormatDecoder(Resources resources) {
        Intrinsics.checkNotNullParameter(resources, "resources");
        this.resources = resources;
        this.resourceIdCache = new ConcurrentHashMap();
    }

    @Override // com.facebook.imagepipeline.decoder.ImageDecoder
    public CloseableImage decode(EncodedImage encodedImage, int length, QualityInfo qualityInfo, ImageDecodeOptions options) {
        Intrinsics.checkNotNullParameter(encodedImage, "encodedImage");
        Intrinsics.checkNotNullParameter(qualityInfo, "qualityInfo");
        Intrinsics.checkNotNullParameter(options, "options");
        try {
            String source = encodedImage.getSource();
            if (source == null) {
                throw new IllegalStateException("No source in encoded image".toString());
            }
            Drawable drawable = ResourcesCompat.getDrawable(this.resources, getXmlResourceId(source), null);
            return drawable != null ? new DefaultCloseableXml(drawable) : null;
        } catch (Throwable th) {
            FLog.e(TAG, "Cannot decode xml", th);
            return null;
        }
    }

    private final int getXmlResourceId(String xmlResourceName) {
        Map<String, Integer> map = this.resourceIdCache;
        Integer num = map.get(xmlResourceName);
        if (num == null) {
            Uri parse = Uri.parse(xmlResourceName);
            Intrinsics.checkNotNullExpressionValue(parse, "parse(...)");
            num = Integer.valueOf(parseImageSourceResourceId(parse));
            map.put(xmlResourceName, num);
        }
        return num.intValue();
    }

    private final int parseImageSourceResourceId(Uri xmlResource) {
        Integer intOrNull;
        if (UriUtil.isLocalResourceUri(xmlResource) || UriUtil.isQualifiedResourceUri(xmlResource)) {
            List<String> pathSegments = xmlResource.getPathSegments();
            Intrinsics.checkNotNullExpressionValue(pathSegments, "getPathSegments(...)");
            String str = (String) CollectionsKt.lastOrNull((List) pathSegments);
            if (str == null || (intOrNull = StringsKt.toIntOrNull(str)) == null) {
                throw new IllegalStateException(("Unable to read resource ID from " + xmlResource.getPath()).toString());
            }
            return intOrNull.intValue();
        }
        throw new IllegalStateException(("Unsupported uri " + xmlResource).toString());
    }
}
