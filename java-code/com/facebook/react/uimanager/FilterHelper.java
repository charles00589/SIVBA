package com.facebook.react.uimanager;

import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import androidx.core.view.ViewCompat;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import java.util.Map;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: FilterHelper.kt */
@Metadata(d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0012\bÁ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0014\u0010\u0004\u001a\u0004\u0018\u00010\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u0007J\u0014\u0010\b\u001a\u0004\u0018\u00010\t2\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u0007J\u0012\u0010\n\u001a\u00020\u000b2\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u0007J\u001c\u0010\f\u001a\u0004\u0018\u00010\u00052\u0006\u0010\r\u001a\u00020\u000e2\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u0005J\u001a\u0010\u0010\u001a\u00020\u00052\u0006\u0010\u0011\u001a\u00020\u000e2\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u0005J\u0010\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0011\u001a\u00020\u000eH\u0002J\u001a\u0010\u0014\u001a\u00020\u00052\u0006\u0010\u0011\u001a\u00020\u000e2\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u0005J2\u0010\u0015\u001a\u00020\u00052\u0006\u0010\u0016\u001a\u00020\u000e2\u0006\u0010\u0017\u001a\u00020\u000e2\u0006\u0010\u0018\u001a\u00020\u000e2\u0006\u0010\u0019\u001a\u00020\u001a2\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u0005J\u001a\u0010\u001b\u001a\u00020\u00052\u0006\u0010\u001c\u001a\u00020\u001d2\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u0005J\u000e\u0010\u001e\u001a\u00020\u00132\u0006\u0010\u0011\u001a\u00020\u000eJ\u001a\u0010\u001f\u001a\u00020\u00052\u0006\u0010\u0011\u001a\u00020\u000e2\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u0005J\u0010\u0010 \u001a\u00020\u00132\u0006\u0010\u0011\u001a\u00020\u000eH\u0002J\u001a\u0010!\u001a\u00020\u00052\u0006\u0010\u0011\u001a\u00020\u000e2\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u0005J\u0010\u0010\"\u001a\u00020\u00132\u0006\u0010\u0011\u001a\u00020\u000eH\u0002J\u001a\u0010#\u001a\u00020\u00052\u0006\u0010\u0011\u001a\u00020\u000e2\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u0005J\u0010\u0010$\u001a\u00020\u00132\u0006\u0010\u0011\u001a\u00020\u000eH\u0002J\u001a\u0010%\u001a\u00020\u00052\u0006\u0010\u0011\u001a\u00020\u000e2\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u0005J\u0010\u0010&\u001a\u00020\u00132\u0006\u0010\u0011\u001a\u00020\u000eH\u0002J\u001a\u0010'\u001a\u00020\u00052\u0006\u0010\u0011\u001a\u00020\u000e2\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u0005J\u0010\u0010(\u001a\u00020\u00132\u0006\u0010\u0011\u001a\u00020\u000eH\u0002J\u001a\u0010)\u001a\u00020\u00052\u0006\u0010\u0011\u001a\u00020\u000e2\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u0005J\u0010\u0010*\u001a\u00020\u00132\u0006\u0010\u0011\u001a\u00020\u000eH\u0002J\u001c\u0010+\u001a\u00020\u00052\u0006\u0010,\u001a\u00020\u00132\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u0005H\u0002J\u0015\u0010-\u001a\u00020\u000e2\u0006\u0010\r\u001a\u00020\u000eH\u0000¢\u0006\u0002\b.¨\u0006/"}, d2 = {"Lcom/facebook/react/uimanager/FilterHelper;", "", "<init>", "()V", "parseFilters", "Landroid/graphics/RenderEffect;", "filters", "Lcom/facebook/react/bridge/ReadableArray;", "parseColorMatrixFilters", "Landroid/graphics/ColorMatrixColorFilter;", "isOnlyColorMatrixFilters", "", "createBlurEffect", "sigma", "", "chainedEffects", "createBrightnessEffect", "amount", "createBrightnessColorMatrix", "Landroid/graphics/ColorMatrix;", "createOpacityEffect", "createDropShadowEffect", "offsetX", "offsetY", "blurRadius", ViewProps.COLOR, "", "parseAndCreateDropShadowEffect", "filterValues", "Lcom/facebook/react/bridge/ReadableMap;", "createOpacityColorMatrix", "createContrastEffect", "createContrastColorMatrix", "createGrayscaleEffect", "createGrayscaleColorMatrix", "createSepiaEffect", "createSepiaColorMatrix", "createSaturateEffect", "createSaturateColorMatrix", "createHueRotateEffect", "createHueRotateColorMatrix", "createInvertEffect", "createInvertColorMatrix", "createColorMatrixEffect", "colorMatrix", "sigmaToRadius", "sigmaToRadius$ReactAndroid_release", "ReactAndroid_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes.dex */
public final class FilterHelper {
    public static final FilterHelper INSTANCE = new FilterHelper();

    private FilterHelper() {
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:10:0x0027. Please report as an issue. */
    @JvmStatic
    public static final RenderEffect parseFilters(ReadableArray filters) {
        RenderEffect renderEffect = null;
        if (filters == null) {
            return null;
        }
        int size = filters.size();
        for (int i = 0; i < size; i++) {
            ReadableMap map = filters.getMap(i);
            if (map == null) {
                throw new IllegalStateException("Required value was null.".toString());
            }
            Map.Entry<String, Object> next = map.getEntryIterator().next();
            String key = next.getKey();
            switch (key.hashCode()) {
                case -2114203985:
                    if (!key.equals("saturate")) {
                        throw new IllegalArgumentException("Invalid filter name: " + key);
                    }
                    FilterHelper filterHelper = INSTANCE;
                    Object value = next.getValue();
                    Intrinsics.checkNotNull(value, "null cannot be cast to non-null type kotlin.Double");
                    renderEffect = filterHelper.createSaturateEffect((float) ((Double) value).doubleValue(), renderEffect);
                case -1267206133:
                    if (!key.equals(ViewProps.OPACITY)) {
                        throw new IllegalArgumentException("Invalid filter name: " + key);
                    }
                    FilterHelper filterHelper2 = INSTANCE;
                    Object value2 = next.getValue();
                    Intrinsics.checkNotNull(value2, "null cannot be cast to non-null type kotlin.Double");
                    renderEffect = filterHelper2.createOpacityEffect((float) ((Double) value2).doubleValue(), renderEffect);
                case -1183703082:
                    if (!key.equals("invert")) {
                        throw new IllegalArgumentException("Invalid filter name: " + key);
                    }
                    FilterHelper filterHelper3 = INSTANCE;
                    Object value3 = next.getValue();
                    Intrinsics.checkNotNull(value3, "null cannot be cast to non-null type kotlin.Double");
                    renderEffect = filterHelper3.createInvertEffect((float) ((Double) value3).doubleValue(), renderEffect);
                case -905411385:
                    if (!key.equals("grayscale")) {
                        throw new IllegalArgumentException("Invalid filter name: " + key);
                    }
                    FilterHelper filterHelper4 = INSTANCE;
                    Object value4 = next.getValue();
                    Intrinsics.checkNotNull(value4, "null cannot be cast to non-null type kotlin.Double");
                    renderEffect = filterHelper4.createGrayscaleEffect((float) ((Double) value4).doubleValue(), renderEffect);
                case -566947070:
                    if (!key.equals("contrast")) {
                        throw new IllegalArgumentException("Invalid filter name: " + key);
                    }
                    FilterHelper filterHelper5 = INSTANCE;
                    Object value5 = next.getValue();
                    Intrinsics.checkNotNull(value5, "null cannot be cast to non-null type kotlin.Double");
                    renderEffect = filterHelper5.createContrastEffect((float) ((Double) value5).doubleValue(), renderEffect);
                case 3027047:
                    if (!key.equals("blur")) {
                        throw new IllegalArgumentException("Invalid filter name: " + key);
                    }
                    FilterHelper filterHelper6 = INSTANCE;
                    Object value6 = next.getValue();
                    Intrinsics.checkNotNull(value6, "null cannot be cast to non-null type kotlin.Double");
                    renderEffect = filterHelper6.createBlurEffect((float) ((Double) value6).doubleValue(), renderEffect);
                case 109324790:
                    if (!key.equals("sepia")) {
                        throw new IllegalArgumentException("Invalid filter name: " + key);
                    }
                    FilterHelper filterHelper7 = INSTANCE;
                    Object value7 = next.getValue();
                    Intrinsics.checkNotNull(value7, "null cannot be cast to non-null type kotlin.Double");
                    renderEffect = filterHelper7.createSepiaEffect((float) ((Double) value7).doubleValue(), renderEffect);
                case 648162385:
                    if (!key.equals("brightness")) {
                        throw new IllegalArgumentException("Invalid filter name: " + key);
                    }
                    FilterHelper filterHelper8 = INSTANCE;
                    Object value8 = next.getValue();
                    Intrinsics.checkNotNull(value8, "null cannot be cast to non-null type kotlin.Double");
                    renderEffect = filterHelper8.createBrightnessEffect((float) ((Double) value8).doubleValue(), renderEffect);
                case 650888307:
                    if (!key.equals("hueRotate")) {
                        throw new IllegalArgumentException("Invalid filter name: " + key);
                    }
                    FilterHelper filterHelper9 = INSTANCE;
                    Object value9 = next.getValue();
                    Intrinsics.checkNotNull(value9, "null cannot be cast to non-null type kotlin.Double");
                    renderEffect = filterHelper9.createHueRotateEffect((float) ((Double) value9).doubleValue(), renderEffect);
                case 906978543:
                    if (!key.equals("dropShadow")) {
                        throw new IllegalArgumentException("Invalid filter name: " + key);
                    }
                    FilterHelper filterHelper10 = INSTANCE;
                    Object value10 = next.getValue();
                    Intrinsics.checkNotNull(value10, "null cannot be cast to non-null type com.facebook.react.bridge.ReadableMap");
                    renderEffect = filterHelper10.parseAndCreateDropShadowEffect((ReadableMap) value10, renderEffect);
                default:
                    throw new IllegalArgumentException("Invalid filter name: " + key);
            }
        }
        return renderEffect;
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:9:0x003a. Please report as an issue. */
    @JvmStatic
    public static final ColorMatrixColorFilter parseColorMatrixFilters(ReadableArray filters) {
        ColorMatrix createSaturateColorMatrix;
        if (filters == null) {
            return null;
        }
        ColorMatrix colorMatrix = new ColorMatrix();
        int size = filters.size();
        for (int i = 0; i < size; i++) {
            ReadableMap map = filters.getMap(i);
            if (map == null) {
                throw new IllegalStateException("Required value was null.".toString());
            }
            Map.Entry<String, Object> next = map.getEntryIterator().next();
            String key = next.getKey();
            Object value = next.getValue();
            Intrinsics.checkNotNull(value, "null cannot be cast to non-null type kotlin.Double");
            float doubleValue = (float) ((Double) value).doubleValue();
            switch (key.hashCode()) {
                case -2114203985:
                    if (!key.equals("saturate")) {
                        throw new IllegalArgumentException("Invalid color matrix filter: " + key);
                    }
                    createSaturateColorMatrix = INSTANCE.createSaturateColorMatrix(doubleValue);
                    colorMatrix.preConcat(createSaturateColorMatrix);
                case -1267206133:
                    if (!key.equals(ViewProps.OPACITY)) {
                        throw new IllegalArgumentException("Invalid color matrix filter: " + key);
                    }
                    createSaturateColorMatrix = INSTANCE.createOpacityColorMatrix(doubleValue);
                    colorMatrix.preConcat(createSaturateColorMatrix);
                case -1183703082:
                    if (!key.equals("invert")) {
                        throw new IllegalArgumentException("Invalid color matrix filter: " + key);
                    }
                    createSaturateColorMatrix = INSTANCE.createInvertColorMatrix(doubleValue);
                    colorMatrix.preConcat(createSaturateColorMatrix);
                case -905411385:
                    if (!key.equals("grayscale")) {
                        throw new IllegalArgumentException("Invalid color matrix filter: " + key);
                    }
                    createSaturateColorMatrix = INSTANCE.createGrayscaleColorMatrix(doubleValue);
                    colorMatrix.preConcat(createSaturateColorMatrix);
                case -566947070:
                    if (!key.equals("contrast")) {
                        throw new IllegalArgumentException("Invalid color matrix filter: " + key);
                    }
                    createSaturateColorMatrix = INSTANCE.createContrastColorMatrix(doubleValue);
                    colorMatrix.preConcat(createSaturateColorMatrix);
                case 109324790:
                    if (!key.equals("sepia")) {
                        throw new IllegalArgumentException("Invalid color matrix filter: " + key);
                    }
                    createSaturateColorMatrix = INSTANCE.createSepiaColorMatrix(doubleValue);
                    colorMatrix.preConcat(createSaturateColorMatrix);
                case 648162385:
                    if (!key.equals("brightness")) {
                        throw new IllegalArgumentException("Invalid color matrix filter: " + key);
                    }
                    createSaturateColorMatrix = INSTANCE.createBrightnessColorMatrix(doubleValue);
                    colorMatrix.preConcat(createSaturateColorMatrix);
                case 650888307:
                    if (!key.equals("hueRotate")) {
                        throw new IllegalArgumentException("Invalid color matrix filter: " + key);
                    }
                    createSaturateColorMatrix = INSTANCE.createHueRotateColorMatrix(doubleValue);
                    colorMatrix.preConcat(createSaturateColorMatrix);
                default:
                    throw new IllegalArgumentException("Invalid color matrix filter: " + key);
            }
        }
        return new ColorMatrixColorFilter(colorMatrix);
    }

    @JvmStatic
    public static final boolean isOnlyColorMatrixFilters(ReadableArray filters) {
        if (filters == null || filters.size() == 0) {
            return false;
        }
        int size = filters.size();
        for (int i = 0; i < size; i++) {
            ReadableMap map = filters.getMap(i);
            Intrinsics.checkNotNull(map);
            String key = map.getEntryIterator().next().getKey();
            if (Intrinsics.areEqual(key, "blur") || Intrinsics.areEqual(key, "dropShadow")) {
                return false;
            }
        }
        return true;
    }

    public static /* synthetic */ RenderEffect createBlurEffect$default(FilterHelper filterHelper, float f, RenderEffect renderEffect, int i, Object obj) {
        if ((i & 2) != 0) {
            renderEffect = null;
        }
        return filterHelper.createBlurEffect(f, renderEffect);
    }

    public final RenderEffect createBlurEffect(float sigma, RenderEffect chainedEffects) {
        if (sigma <= 0.5d) {
            return null;
        }
        float sigmaToRadius$ReactAndroid_release = sigmaToRadius$ReactAndroid_release(sigma);
        if (chainedEffects == null) {
            return RenderEffect.createBlurEffect(sigmaToRadius$ReactAndroid_release, sigmaToRadius$ReactAndroid_release, Shader.TileMode.DECAL);
        }
        return RenderEffect.createBlurEffect(sigmaToRadius$ReactAndroid_release, sigmaToRadius$ReactAndroid_release, chainedEffects, Shader.TileMode.DECAL);
    }

    public static /* synthetic */ RenderEffect createBrightnessEffect$default(FilterHelper filterHelper, float f, RenderEffect renderEffect, int i, Object obj) {
        if ((i & 2) != 0) {
            renderEffect = null;
        }
        return filterHelper.createBrightnessEffect(f, renderEffect);
    }

    public final RenderEffect createBrightnessEffect(float amount, RenderEffect chainedEffects) {
        return createColorMatrixEffect(createBrightnessColorMatrix(amount), chainedEffects);
    }

    private final ColorMatrix createBrightnessColorMatrix(float amount) {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setScale(amount, amount, amount, 1.0f);
        return colorMatrix;
    }

    public static /* synthetic */ RenderEffect createOpacityEffect$default(FilterHelper filterHelper, float f, RenderEffect renderEffect, int i, Object obj) {
        if ((i & 2) != 0) {
            renderEffect = null;
        }
        return filterHelper.createOpacityEffect(f, renderEffect);
    }

    public final RenderEffect createOpacityEffect(float amount, RenderEffect chainedEffects) {
        return createColorMatrixEffect(createOpacityColorMatrix(amount), chainedEffects);
    }

    public static /* synthetic */ RenderEffect createDropShadowEffect$default(FilterHelper filterHelper, float f, float f2, float f3, int i, RenderEffect renderEffect, int i2, Object obj) {
        if ((i2 & 16) != 0) {
            renderEffect = null;
        }
        return filterHelper.createDropShadowEffect(f, f2, f3, i, renderEffect);
    }

    public final RenderEffect createDropShadowEffect(float offsetX, float offsetY, float blurRadius, int color, RenderEffect chainedEffects) {
        RenderEffect createOffsetEffect;
        RenderEffect renderEffect;
        if (chainedEffects == null) {
            renderEffect = RenderEffect.createOffsetEffect(0.0f, 0.0f);
            createOffsetEffect = RenderEffect.createOffsetEffect(offsetX, offsetY);
        } else {
            RenderEffect createOffsetEffect2 = RenderEffect.createOffsetEffect(0.0f, 0.0f, chainedEffects);
            createOffsetEffect = RenderEffect.createOffsetEffect(offsetX, offsetY, chainedEffects);
            renderEffect = createOffsetEffect2;
        }
        RenderEffect createColorFilterEffect = RenderEffect.createColorFilterEffect(new BlendModeColorFilter(color, BlendMode.SRC_IN), createOffsetEffect);
        Intrinsics.checkNotNullExpressionValue(createColorFilterEffect, "createColorFilterEffect(...)");
        RenderEffect createBlurEffect = RenderEffect.createBlurEffect(blurRadius, blurRadius, createColorFilterEffect, Shader.TileMode.DECAL);
        Intrinsics.checkNotNullExpressionValue(createBlurEffect, "createBlurEffect(...)");
        RenderEffect createBlendModeEffect = RenderEffect.createBlendModeEffect(createBlurEffect, renderEffect, BlendMode.SRC_OVER);
        Intrinsics.checkNotNullExpressionValue(createBlendModeEffect, "createBlendModeEffect(...)");
        return createBlendModeEffect;
    }

    public static /* synthetic */ RenderEffect parseAndCreateDropShadowEffect$default(FilterHelper filterHelper, ReadableMap readableMap, RenderEffect renderEffect, int i, Object obj) {
        if ((i & 2) != 0) {
            renderEffect = null;
        }
        return filterHelper.parseAndCreateDropShadowEffect(readableMap, renderEffect);
    }

    public final RenderEffect parseAndCreateDropShadowEffect(ReadableMap filterValues, RenderEffect chainedEffects) {
        Intrinsics.checkNotNullParameter(filterValues, "filterValues");
        return createDropShadowEffect(PixelUtil.INSTANCE.dpToPx(filterValues.getDouble("offsetX")), PixelUtil.INSTANCE.dpToPx(filterValues.getDouble("offsetY")), filterValues.hasKey("standardDeviation") ? sigmaToRadius$ReactAndroid_release((float) filterValues.getDouble("standardDeviation")) : 0.0f, filterValues.hasKey(ViewProps.COLOR) ? filterValues.getInt(ViewProps.COLOR) : ViewCompat.MEASURED_STATE_MASK, chainedEffects);
    }

    public final ColorMatrix createOpacityColorMatrix(float amount) {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setScale(1.0f, 1.0f, 1.0f, amount);
        return colorMatrix;
    }

    public static /* synthetic */ RenderEffect createContrastEffect$default(FilterHelper filterHelper, float f, RenderEffect renderEffect, int i, Object obj) {
        if ((i & 2) != 0) {
            renderEffect = null;
        }
        return filterHelper.createContrastEffect(f, renderEffect);
    }

    public final RenderEffect createContrastEffect(float amount, RenderEffect chainedEffects) {
        return createColorMatrixEffect(createContrastColorMatrix(amount), chainedEffects);
    }

    private final ColorMatrix createContrastColorMatrix(float amount) {
        float f = 255 * ((-(amount / 2.0f)) + 0.5f);
        return new ColorMatrix(new float[]{amount, 0.0f, 0.0f, 0.0f, f, 0.0f, amount, 0.0f, 0.0f, f, 0.0f, 0.0f, amount, 0.0f, f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f});
    }

    public static /* synthetic */ RenderEffect createGrayscaleEffect$default(FilterHelper filterHelper, float f, RenderEffect renderEffect, int i, Object obj) {
        if ((i & 2) != 0) {
            renderEffect = null;
        }
        return filterHelper.createGrayscaleEffect(f, renderEffect);
    }

    public final RenderEffect createGrayscaleEffect(float amount, RenderEffect chainedEffects) {
        return createColorMatrixEffect(createGrayscaleColorMatrix(amount), chainedEffects);
    }

    private final ColorMatrix createGrayscaleColorMatrix(float amount) {
        float f = 1 - amount;
        float f2 = 0.7152f - (f * 0.7152f);
        float f3 = 0.0722f - (f * 0.0722f);
        float f4 = 0.2126f - (f * 0.2126f);
        return new ColorMatrix(new float[]{(0.7874f * f) + 0.2126f, f2, f3, 0.0f, 0.0f, f4, (0.2848f * f) + 0.7152f, f3, 0.0f, 0.0f, f4, f2, (f * 0.9278f) + 0.0722f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f});
    }

    public static /* synthetic */ RenderEffect createSepiaEffect$default(FilterHelper filterHelper, float f, RenderEffect renderEffect, int i, Object obj) {
        if ((i & 2) != 0) {
            renderEffect = null;
        }
        return filterHelper.createSepiaEffect(f, renderEffect);
    }

    public final RenderEffect createSepiaEffect(float amount, RenderEffect chainedEffects) {
        return createColorMatrixEffect(createSepiaColorMatrix(amount), chainedEffects);
    }

    private final ColorMatrix createSepiaColorMatrix(float amount) {
        float f = 1 - amount;
        return new ColorMatrix(new float[]{(0.607f * f) + 0.393f, 0.769f - (f * 0.769f), 0.189f - (f * 0.189f), 0.0f, 0.0f, 0.349f - (f * 0.349f), (0.314f * f) + 0.686f, 0.168f - (f * 0.168f), 0.0f, 0.0f, 0.272f - (f * 0.272f), 0.534f - (f * 0.534f), (f * 0.869f) + 0.131f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f});
    }

    public static /* synthetic */ RenderEffect createSaturateEffect$default(FilterHelper filterHelper, float f, RenderEffect renderEffect, int i, Object obj) {
        if ((i & 2) != 0) {
            renderEffect = null;
        }
        return filterHelper.createSaturateEffect(f, renderEffect);
    }

    public final RenderEffect createSaturateEffect(float amount, RenderEffect chainedEffects) {
        return createColorMatrixEffect(createSaturateColorMatrix(amount), chainedEffects);
    }

    private final ColorMatrix createSaturateColorMatrix(float amount) {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(amount);
        return colorMatrix;
    }

    public static /* synthetic */ RenderEffect createHueRotateEffect$default(FilterHelper filterHelper, float f, RenderEffect renderEffect, int i, Object obj) {
        if ((i & 2) != 0) {
            renderEffect = null;
        }
        return filterHelper.createHueRotateEffect(f, renderEffect);
    }

    public final RenderEffect createHueRotateEffect(float amount, RenderEffect chainedEffects) {
        return createColorMatrixEffect(createHueRotateColorMatrix(amount), chainedEffects);
    }

    private final ColorMatrix createHueRotateColorMatrix(float amount) {
        double radians = Math.toRadians(amount);
        float cos = (float) Math.cos(radians);
        float sin = (float) Math.sin(radians);
        float f = 0.715f - (cos * 0.715f);
        float f2 = sin * 0.715f;
        float f3 = 0.072f - (cos * 0.072f);
        float f4 = 0.213f - (cos * 0.213f);
        return new ColorMatrix(new float[]{((cos * 0.787f) + 0.213f) - (sin * 0.213f), f - f2, (sin * 0.928f) + f3, 0.0f, 0.0f, (0.143f * sin) + f4, (0.285f * cos) + 0.715f + (0.14f * sin), f3 - (0.283f * sin), 0.0f, 0.0f, f4 - (0.787f * sin), f + f2, (cos * 0.928f) + 0.072f + (sin * 0.072f), 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f});
    }

    public static /* synthetic */ RenderEffect createInvertEffect$default(FilterHelper filterHelper, float f, RenderEffect renderEffect, int i, Object obj) {
        if ((i & 2) != 0) {
            renderEffect = null;
        }
        return filterHelper.createInvertEffect(f, renderEffect);
    }

    public final RenderEffect createInvertEffect(float amount, RenderEffect chainedEffects) {
        return createColorMatrixEffect(createInvertColorMatrix(amount), chainedEffects);
    }

    private final ColorMatrix createInvertColorMatrix(float amount) {
        float f = 1 - (2 * amount);
        float f2 = amount * 255;
        return new ColorMatrix(new float[]{f, 0.0f, 0.0f, 0.0f, f2, 0.0f, f, 0.0f, 0.0f, f2, 0.0f, 0.0f, f, 0.0f, f2, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f});
    }

    static /* synthetic */ RenderEffect createColorMatrixEffect$default(FilterHelper filterHelper, ColorMatrix colorMatrix, RenderEffect renderEffect, int i, Object obj) {
        if ((i & 2) != 0) {
            renderEffect = null;
        }
        return filterHelper.createColorMatrixEffect(colorMatrix, renderEffect);
    }

    private final RenderEffect createColorMatrixEffect(ColorMatrix colorMatrix, RenderEffect chainedEffects) {
        if (chainedEffects == null) {
            RenderEffect createColorFilterEffect = RenderEffect.createColorFilterEffect(new ColorMatrixColorFilter(colorMatrix));
            Intrinsics.checkNotNull(createColorFilterEffect);
            return createColorFilterEffect;
        }
        RenderEffect createColorFilterEffect2 = RenderEffect.createColorFilterEffect(new ColorMatrixColorFilter(colorMatrix), chainedEffects);
        Intrinsics.checkNotNull(createColorFilterEffect2);
        return createColorFilterEffect2;
    }

    public final float sigmaToRadius$ReactAndroid_release(float sigma) {
        float pixelFromDIP = PixelUtil.toPixelFromDIP(sigma);
        if (pixelFromDIP > 0.5f) {
            return (pixelFromDIP - 0.5f) / 0.57735f;
        }
        return 0.0f;
    }
}
