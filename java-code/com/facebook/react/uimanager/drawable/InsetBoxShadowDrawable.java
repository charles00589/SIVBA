package com.facebook.react.uimanager.drawable;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import com.facebook.react.uimanager.FilterHelper;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.ViewProps;
import com.facebook.react.uimanager.style.BorderInsets;
import com.facebook.react.uimanager.style.BorderRadiusStyle;
import com.facebook.react.uimanager.style.ComputedBorderRadius;
import com.facebook.react.uimanager.style.CornerRadii;
import java.util.ArrayList;
import kotlin.Deprecated;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.math.MathKt;
import kotlin.ranges.RangesKt;

/* compiled from: InsetBoxShadowDrawable.kt */
@Metadata(d1 = {"\u0000V\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0001\u0018\u00002\u00020\u0001BO\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0007\u0012\u0006\u0010\t\u001a\u00020\u0007\u0012\u0006\u0010\n\u001a\u00020\u0007\u0012\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\f\u0012\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u000e¢\u0006\u0004\b\u000f\u0010\u0010J\u0010\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u0005H\u0016J\u0012\u0010\u001e\u001a\u00020\u001c2\b\u0010\u001f\u001a\u0004\u0018\u00010 H\u0016J\b\u0010!\u001a\u00020\u0005H\u0017J\u0010\u0010\"\u001a\u00020\u001c2\u0006\u0010#\u001a\u00020$H\u0016J\n\u0010%\u001a\u0004\u0018\u00010&H\u0002J\n\u0010'\u001a\u0004\u0018\u00010(H\u0002J\u001f\u0010)\u001a\u00020\u00072\u0006\u0010*\u001a\u00020\u00072\b\u0010+\u001a\u0004\u0018\u00010\u0007H\u0002¢\u0006\u0002\u0010,R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u001c\u0010\u000b\u001a\u0004\u0018\u00010\fX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u0012\"\u0004\b\u0013\u0010\u0014R\u001c\u0010\r\u001a\u0004\u0018\u00010\u000eX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0015\u0010\u0016\"\u0004\b\u0017\u0010\u0018R\u000e\u0010\u0019\u001a\u00020\u001aX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006-"}, d2 = {"Lcom/facebook/react/uimanager/drawable/InsetBoxShadowDrawable;", "Landroid/graphics/drawable/Drawable;", "context", "Landroid/content/Context;", ViewProps.SHADOW_COLOR, "", "offsetX", "", "offsetY", "blurRadius", "spread", "borderInsets", "Lcom/facebook/react/uimanager/style/BorderInsets;", ViewProps.BORDER_RADIUS, "Lcom/facebook/react/uimanager/style/BorderRadiusStyle;", "<init>", "(Landroid/content/Context;IFFFFLcom/facebook/react/uimanager/style/BorderInsets;Lcom/facebook/react/uimanager/style/BorderRadiusStyle;)V", "getBorderInsets", "()Lcom/facebook/react/uimanager/style/BorderInsets;", "setBorderInsets", "(Lcom/facebook/react/uimanager/style/BorderInsets;)V", "getBorderRadius", "()Lcom/facebook/react/uimanager/style/BorderRadiusStyle;", "setBorderRadius", "(Lcom/facebook/react/uimanager/style/BorderRadiusStyle;)V", "shadowPaint", "Landroid/graphics/Paint;", "setAlpha", "", "alpha", "setColorFilter", "colorFilter", "Landroid/graphics/ColorFilter;", "getOpacity", "draw", "canvas", "Landroid/graphics/Canvas;", "computeBorderRadii", "Lcom/facebook/react/uimanager/style/ComputedBorderRadius;", "computeBorderInsets", "Landroid/graphics/RectF;", "innerRadius", "radius", "borderInset", "(FLjava/lang/Float;)F", "ReactAndroid_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes.dex */
public final class InsetBoxShadowDrawable extends Drawable {
    private final float blurRadius;
    private BorderInsets borderInsets;
    private BorderRadiusStyle borderRadius;
    private final Context context;
    private final float offsetX;
    private final float offsetY;
    private final int shadowColor;
    private final Paint shadowPaint;
    private final float spread;

    public /* synthetic */ InsetBoxShadowDrawable(Context context, int i, float f, float f2, float f3, float f4, BorderInsets borderInsets, BorderRadiusStyle borderRadiusStyle, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this(context, i, f, f2, f3, f4, (i2 & 64) != 0 ? null : borderInsets, (i2 & 128) != 0 ? null : borderRadiusStyle);
    }

    public final BorderInsets getBorderInsets() {
        return this.borderInsets;
    }

    public final void setBorderInsets(BorderInsets borderInsets) {
        this.borderInsets = borderInsets;
    }

    public final BorderRadiusStyle getBorderRadius() {
        return this.borderRadius;
    }

    public final void setBorderRadius(BorderRadiusStyle borderRadiusStyle) {
        this.borderRadius = borderRadiusStyle;
    }

    public InsetBoxShadowDrawable(Context context, int i, float f, float f2, float f3, float f4, BorderInsets borderInsets, BorderRadiusStyle borderRadiusStyle) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
        this.shadowColor = i;
        this.offsetX = f;
        this.offsetY = f2;
        this.blurRadius = f3;
        this.spread = f4;
        this.borderInsets = borderInsets;
        this.borderRadius = borderRadiusStyle;
        Paint paint = new Paint();
        paint.setColor(i);
        float sigmaToRadius$ReactAndroid_release = FilterHelper.INSTANCE.sigmaToRadius$ReactAndroid_release(f3 * 0.5f);
        if (sigmaToRadius$ReactAndroid_release > 0.0f) {
            paint.setMaskFilter(new BlurMaskFilter(sigmaToRadius$ReactAndroid_release, BlurMaskFilter.Blur.NORMAL));
        }
        this.shadowPaint = paint;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        this.shadowPaint.setAlpha(MathKt.roundToInt((alpha / 255.0f) * (Color.alpha(this.shadowColor) / 255.0f) * 255.0f));
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.shadowPaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    @Deprecated(message = "Deprecated in Java")
    public int getOpacity() {
        int alpha = this.shadowPaint.getAlpha();
        if (alpha == 255) {
            return -1;
        }
        return (1 > alpha || alpha >= 255) ? -2 : -3;
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        float[] fArr;
        float[] fArr2;
        float[] fArr3;
        Intrinsics.checkNotNullParameter(canvas, "canvas");
        ComputedBorderRadius computeBorderRadii = computeBorderRadii();
        RectF computeBorderInsets = computeBorderInsets();
        RectF rectF = new RectF(getBounds().left + (computeBorderInsets != null ? computeBorderInsets.left : 0.0f), getBounds().top + (computeBorderInsets != null ? computeBorderInsets.top : 0.0f), getBounds().right - (computeBorderInsets != null ? computeBorderInsets.right : 0.0f), getBounds().bottom - (computeBorderInsets != null ? computeBorderInsets.bottom : 0.0f));
        float[] fArr4 = null;
        if (computeBorderRadii != null) {
            fArr4 = new float[]{innerRadius(computeBorderRadii.getTopLeft().getHorizontal(), computeBorderInsets != null ? Float.valueOf(computeBorderInsets.left) : null), innerRadius(computeBorderRadii.getTopLeft().getVertical(), computeBorderInsets != null ? Float.valueOf(computeBorderInsets.top) : null), innerRadius(computeBorderRadii.getTopRight().getHorizontal(), computeBorderInsets != null ? Float.valueOf(computeBorderInsets.right) : null), innerRadius(computeBorderRadii.getTopRight().getVertical(), computeBorderInsets != null ? Float.valueOf(computeBorderInsets.top) : null), innerRadius(computeBorderRadii.getBottomRight().getHorizontal(), computeBorderInsets != null ? Float.valueOf(computeBorderInsets.right) : null), innerRadius(computeBorderRadii.getBottomRight().getVertical(), computeBorderInsets != null ? Float.valueOf(computeBorderInsets.bottom) : null), innerRadius(computeBorderRadii.getBottomLeft().getHorizontal(), computeBorderInsets != null ? Float.valueOf(computeBorderInsets.left) : null), innerRadius(computeBorderRadii.getBottomLeft().getVertical(), computeBorderInsets != null ? Float.valueOf(computeBorderInsets.bottom) : null)};
        }
        float dpToPx = PixelUtil.INSTANCE.dpToPx(this.offsetX);
        float dpToPx2 = PixelUtil.INSTANCE.dpToPx(this.offsetY);
        float dpToPx3 = PixelUtil.INSTANCE.dpToPx(this.spread);
        RectF rectF2 = new RectF(rectF);
        rectF2.inset(dpToPx3, dpToPx3);
        rectF2.offset(dpToPx, dpToPx2);
        float sigmaToRadius$ReactAndroid_release = FilterHelper.INSTANCE.sigmaToRadius$ReactAndroid_release(this.blurRadius);
        RectF rectF3 = new RectF(rectF);
        float f = -sigmaToRadius$ReactAndroid_release;
        rectF3.inset(f, f);
        if (dpToPx3 < 0.0f) {
            rectF3.inset(dpToPx3, dpToPx3);
        }
        RectF rectF4 = new RectF(rectF3);
        rectF4.offset(-dpToPx, -dpToPx2);
        rectF3.union(rectF4);
        int save = canvas.save();
        if (fArr4 != null) {
            Path path = new Path();
            path.addRoundRect(rectF, fArr4, Path.Direction.CW);
            canvas.clipPath(path);
            ArrayList arrayList = new ArrayList(fArr4.length);
            for (float f2 : fArr4) {
                arrayList.add(Float.valueOf(BoxShadowBorderRadiusKt.adjustRadiusForSpread(f2, -dpToPx3)));
            }
            float[] floatArray = CollectionsKt.toFloatArray(arrayList);
            fArr3 = InsetBoxShadowDrawableKt.ZERO_RADII;
            canvas.drawDoubleRoundRect(rectF3, fArr3, rectF2, floatArray, this.shadowPaint);
        } else {
            canvas.clipRect(rectF);
            fArr = InsetBoxShadowDrawableKt.ZERO_RADII;
            fArr2 = InsetBoxShadowDrawableKt.ZERO_RADII;
            canvas.drawDoubleRoundRect(rectF3, fArr, rectF2, fArr2, this.shadowPaint);
        }
        canvas.restoreToCount(save);
    }

    private final ComputedBorderRadius computeBorderRadii() {
        BorderRadiusStyle borderRadiusStyle = this.borderRadius;
        ComputedBorderRadius resolve = borderRadiusStyle != null ? borderRadiusStyle.resolve(getLayoutDirection(), this.context, PixelUtil.INSTANCE.pxToDp(getBounds().width()), PixelUtil.INSTANCE.pxToDp(getBounds().height())) : null;
        if (resolve == null || !resolve.hasRoundedBorders()) {
            return null;
        }
        return new ComputedBorderRadius(new CornerRadii(PixelUtil.INSTANCE.dpToPx(resolve.getTopLeft().getHorizontal()), PixelUtil.INSTANCE.dpToPx(resolve.getTopLeft().getVertical())), new CornerRadii(PixelUtil.INSTANCE.dpToPx(resolve.getTopRight().getHorizontal()), PixelUtil.INSTANCE.dpToPx(resolve.getTopRight().getVertical())), new CornerRadii(PixelUtil.INSTANCE.dpToPx(resolve.getBottomLeft().getHorizontal()), PixelUtil.INSTANCE.dpToPx(resolve.getBottomLeft().getVertical())), new CornerRadii(PixelUtil.INSTANCE.dpToPx(resolve.getBottomRight().getHorizontal()), PixelUtil.INSTANCE.dpToPx(resolve.getBottomRight().getVertical())));
    }

    private final RectF computeBorderInsets() {
        RectF resolve;
        BorderInsets borderInsets = this.borderInsets;
        if (borderInsets == null || (resolve = borderInsets.resolve(getLayoutDirection(), this.context)) == null) {
            return null;
        }
        return new RectF(PixelUtil.INSTANCE.dpToPx(resolve.left), PixelUtil.INSTANCE.dpToPx(resolve.top), PixelUtil.INSTANCE.dpToPx(resolve.right), PixelUtil.INSTANCE.dpToPx(resolve.bottom));
    }

    private final float innerRadius(float radius, Float borderInset) {
        return RangesKt.coerceAtLeast(radius - (borderInset != null ? borderInset.floatValue() : 0.0f), 0.0f);
    }
}
