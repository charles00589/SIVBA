package com.facebook.react.uimanager.drawable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.core.view.ViewCompat;
import com.facebook.react.uimanager.FloatUtil;
import com.facebook.react.uimanager.LengthPercentage;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.Spacing;
import com.facebook.react.uimanager.ViewProps;
import com.facebook.react.uimanager.style.BorderColors;
import com.facebook.react.uimanager.style.BorderInsets;
import com.facebook.react.uimanager.style.BorderRadiusProp;
import com.facebook.react.uimanager.style.BorderRadiusStyle;
import com.facebook.react.uimanager.style.BorderStyle;
import com.facebook.react.uimanager.style.ColorEdges;
import com.facebook.react.uimanager.style.ComputedBorderRadius;
import com.facebook.react.uimanager.style.CornerRadii;
import com.facebook.react.uimanager.style.LogicalEdge;
import java.util.Locale;
import kotlin.Deprecated;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.comparisons.ComparisonsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.jvm.internal.Reflection;
import kotlin.math.MathKt;
import kotlin.properties.ObservableProperty;
import kotlin.properties.ReadWriteProperty;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;

/* compiled from: BorderDrawable.kt */
@Metadata(d1 = {"\u0000Ä\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u001d\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\f\b\u0000\u0018\u00002\u00020\u0001B7\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\b\u0010\b\u001a\u0004\u0018\u00010\t\u0012\b\u0010\n\u001a\u0004\u0018\u00010\u000b¢\u0006\u0004\b\f\u0010\rJ)\u0010\u0018\u001a\u0010\u0012\u0006\u0012\u0004\u0018\u00010\u001a\u0012\u0004\u0012\u0002H\u001b0\u0019\"\u0004\b\u0000\u0010\u001b2\u0006\u0010\u001c\u001a\u0002H\u001bH\u0002¢\u0006\u0002\u0010\u001dJ\b\u0010G\u001a\u00020HH\u0016J\u0010\u0010I\u001a\u00020H2\u0006\u0010J\u001a\u00020KH\u0014J\u0010\u0010L\u001a\u00020H2\u0006\u0010M\u001a\u00020-H\u0016J\u0012\u0010N\u001a\u00020H2\b\u0010O\u001a\u0004\u0018\u00010PH\u0016J\b\u0010Q\u001a\u00020-H\u0017J\u0010\u0010R\u001a\u00020H2\u0006\u0010S\u001a\u00020TH\u0016J\u0018\u0010U\u001a\u00020/2\u0006\u0010V\u001a\u00020/2\u0006\u0010\u0004\u001a\u00020/H\u0002J\u0016\u0010W\u001a\u00020H2\u0006\u0010X\u001a\u00020-2\u0006\u0010Y\u001a\u00020/J\u0018\u0010\u0012\u001a\u00020H2\u0006\u0010Z\u001a\u00020[2\b\u0010\\\u001a\u0004\u0018\u00010]J\u0010\u0010!\u001a\u00020H2\b\u0010^\u001a\u0004\u0018\u00010_J\u001d\u0010`\u001a\u00020H2\u0006\u0010X\u001a\u00020a2\b\u0010b\u001a\u0004\u0018\u00010-¢\u0006\u0002\u0010cJ\u000e\u0010d\u001a\u00020-2\u0006\u0010X\u001a\u00020aJ\u0006\u0010e\u001a\u00020HJ\u0010\u0010f\u001a\u00020H2\u0006\u0010S\u001a\u00020TH\u0002J\u0010\u0010g\u001a\u00020H2\u0006\u0010S\u001a\u00020TH\u0002JH\u0010h\u001a\u00020-2\u0006\u0010i\u001a\u00020-2\u0006\u0010j\u001a\u00020-2\u0006\u0010k\u001a\u00020-2\u0006\u0010l\u001a\u00020-2\u0006\u0010m\u001a\u00020-2\u0006\u0010n\u001a\u00020-2\u0006\u0010o\u001a\u00020-2\u0006\u0010p\u001a\u00020-H\u0002JX\u0010q\u001a\u00020H2\u0006\u0010S\u001a\u00020T2\u0006\u0010r\u001a\u00020-2\u0006\u0010s\u001a\u00020/2\u0006\u0010t\u001a\u00020/2\u0006\u0010u\u001a\u00020/2\u0006\u0010v\u001a\u00020/2\u0006\u0010w\u001a\u00020/2\u0006\u0010x\u001a\u00020/2\u0006\u0010y\u001a\u00020/2\u0006\u0010z\u001a\u00020/H\u0002J\b\u0010{\u001a\u00020DH\u0002J\b\u0010|\u001a\u00020/H\u0002J\b\u0010}\u001a\u00020HH\u0002J\u0010\u0010}\u001a\u00020H2\u0006\u0010\u0004\u001a\u00020-H\u0002J\u001a\u0010~\u001a\u0004\u0018\u00010\u007f2\u0006\u0010^\u001a\u00020\u000b2\u0006\u0010\u0004\u001a\u00020/H\u0002Jb\u0010\u0080\u0001\u001a\u00020H2\b\u0010\u0081\u0001\u001a\u00030\u0082\u00012\b\u0010\u0083\u0001\u001a\u00030\u0082\u00012\b\u0010\u0084\u0001\u001a\u00030\u0082\u00012\b\u0010\u0085\u0001\u001a\u00030\u0082\u00012\b\u0010\u0086\u0001\u001a\u00030\u0082\u00012\b\u0010\u0087\u0001\u001a\u00030\u0082\u00012\b\u0010\u0088\u0001\u001a\u00030\u0082\u00012\b\u0010\u0089\u0001\u001a\u00030\u0082\u00012\u0007\u0010\u008a\u0001\u001a\u00020?H\u0002J\t\u0010\u008b\u0001\u001a\u00020HH\u0002J\u001a\u0010\u008c\u0001\u001a\u00020-2\u0006\u0010b\u001a\u00020-2\u0007\u0010\u008d\u0001\u001a\u00020-H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u001c\u0010\u0006\u001a\u0004\u0018\u00010\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\u0011\"\u0004\b\u0012\u0010\u0013R\u001c\u0010\b\u001a\u0004\u0018\u00010\tX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0015\"\u0004\b\u0016\u0010\u0017R/\u0010\n\u001a\u0004\u0018\u00010\u000b2\b\u0010\u001e\u001a\u0004\u0018\u00010\u000b8F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b#\u0010$\u001a\u0004\b\u001f\u0010 \"\u0004\b!\u0010\"R\u0012\u0010%\u001a\u0004\u0018\u00010&X\u0082\u000e¢\u0006\u0004\n\u0002\u0010'R\u000e\u0010(\u001a\u00020)X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010*\u001a\u0004\u0018\u00010+X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010,\u001a\u00020-X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010.\u001a\u00020/X\u0082D¢\u0006\u0002\n\u0000R\u0010\u00100\u001a\u0004\u0018\u000101X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u00102\u001a\u000203X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u00104\u001a\u000205X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u00106\u001a\u0004\u0018\u000101X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u00107\u001a\u0004\u0018\u000101X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u00108\u001a\u0004\u0018\u000101X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u00109\u001a\u0004\u0018\u000101X\u0082\u000e¢\u0006\u0002\n\u0000R\"\u0010;\u001a\u0004\u0018\u0001012\b\u0010:\u001a\u0004\u0018\u000101@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b<\u0010=R\u0010\u0010>\u001a\u0004\u0018\u00010?X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010@\u001a\u0004\u0018\u00010?X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010A\u001a\u0004\u0018\u00010?X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010B\u001a\u0004\u0018\u00010?X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010C\u001a\u0004\u0018\u00010DX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010E\u001a\u0004\u0018\u00010DX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010F\u001a\u0004\u0018\u00010DX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u008e\u0001"}, d2 = {"Lcom/facebook/react/uimanager/drawable/BorderDrawable;", "Landroid/graphics/drawable/Drawable;", "context", "Landroid/content/Context;", ViewProps.BORDER_WIDTH, "Lcom/facebook/react/uimanager/Spacing;", ViewProps.BORDER_RADIUS, "Lcom/facebook/react/uimanager/style/BorderRadiusStyle;", "borderInsets", "Lcom/facebook/react/uimanager/style/BorderInsets;", "borderStyle", "Lcom/facebook/react/uimanager/style/BorderStyle;", "<init>", "(Landroid/content/Context;Lcom/facebook/react/uimanager/Spacing;Lcom/facebook/react/uimanager/style/BorderRadiusStyle;Lcom/facebook/react/uimanager/style/BorderInsets;Lcom/facebook/react/uimanager/style/BorderStyle;)V", "getBorderWidth", "()Lcom/facebook/react/uimanager/Spacing;", "getBorderRadius", "()Lcom/facebook/react/uimanager/style/BorderRadiusStyle;", "setBorderRadius", "(Lcom/facebook/react/uimanager/style/BorderRadiusStyle;)V", "getBorderInsets", "()Lcom/facebook/react/uimanager/style/BorderInsets;", "setBorderInsets", "(Lcom/facebook/react/uimanager/style/BorderInsets;)V", "invalidatingAndPathChange", "Lkotlin/properties/ReadWriteProperty;", "", "T", "initialValue", "(Ljava/lang/Object;)Lkotlin/properties/ReadWriteProperty;", "<set-?>", "getBorderStyle", "()Lcom/facebook/react/uimanager/style/BorderStyle;", "setBorderStyle", "(Lcom/facebook/react/uimanager/style/BorderStyle;)V", "borderStyle$delegate", "Lkotlin/properties/ReadWriteProperty;", "borderColors", "Lcom/facebook/react/uimanager/style/BorderColors;", "[Ljava/lang/Integer;", "computedBorderColors", "Lcom/facebook/react/uimanager/style/ColorEdges;", "computedBorderRadius", "Lcom/facebook/react/uimanager/style/ComputedBorderRadius;", "borderAlpha", "", "gapBetweenPaths", "", "pathForBorder", "Landroid/graphics/Path;", "borderPaint", "Landroid/graphics/Paint;", "needUpdatePath", "", "pathForSingleBorder", "pathForOutline", "centerDrawPath", "outerClipPathForBorderRadius", "value", "innerClipPathForBorderRadius", "getInnerClipPathForBorderRadius", "()Landroid/graphics/Path;", "innerBottomLeftCorner", "Landroid/graphics/PointF;", "innerBottomRightCorner", "innerTopLeftCorner", "innerTopRightCorner", "innerClipTempRectForBorderRadius", "Landroid/graphics/RectF;", "outerClipTempRectForBorderRadius", "tempRectForCenterDrawPath", "invalidateSelf", "", "onBoundsChange", "bounds", "Landroid/graphics/Rect;", "setAlpha", "alpha", "setColorFilter", "colorFilter", "Landroid/graphics/ColorFilter;", "getOpacity", "draw", "canvas", "Landroid/graphics/Canvas;", "getInnerBorderRadius", "computedRadius", "setBorderWidth", ViewProps.POSITION, "width", "property", "Lcom/facebook/react/uimanager/style/BorderRadiusProp;", "radius", "Lcom/facebook/react/uimanager/LengthPercentage;", "style", "", "setBorderColor", "Lcom/facebook/react/uimanager/style/LogicalEdge;", ViewProps.COLOR, "(Lcom/facebook/react/uimanager/style/LogicalEdge;Ljava/lang/Integer;)V", "getBorderColor", "invalidateSelfAndUpdatePath", "drawRectangularBorders", "drawRoundedBorders", "fastBorderCompatibleColorOrZero", "borderLeft", "borderTop", "borderRight", "borderBottom", "colorLeft", "colorTop", "colorRight", "colorBottom", "drawQuadrilateral", "fillColor", "x1", "y1", "x2", "y2", "x3", "y3", "x4", "y4", "computeBorderInsets", "getFullBorderWidth", "updatePathEffect", "getPathEffect", "Landroid/graphics/PathEffect;", "getEllipseIntersectionWithLine", "ellipseBoundsLeft", "", "ellipseBoundsTop", "ellipseBoundsRight", "ellipseBoundsBottom", "lineStartX", "lineStartY", "lineEndX", "lineEndY", "result", "updatePath", "multiplyColorAlpha", "rawAlpha", "ReactAndroid_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes.dex */
public final class BorderDrawable extends Drawable {
    static final /* synthetic */ KProperty<Object>[] $$delegatedProperties = {Reflection.mutableProperty1(new MutablePropertyReference1Impl(BorderDrawable.class, "borderStyle", "getBorderStyle()Lcom/facebook/react/uimanager/style/BorderStyle;", 0))};
    private int borderAlpha;
    private Integer[] borderColors;
    private BorderInsets borderInsets;
    private final Paint borderPaint;
    private BorderRadiusStyle borderRadius;

    /* renamed from: borderStyle$delegate, reason: from kotlin metadata */
    private final ReadWriteProperty borderStyle;
    private final Spacing borderWidth;
    private Path centerDrawPath;
    private ColorEdges computedBorderColors;
    private ComputedBorderRadius computedBorderRadius;
    private final Context context;
    private final float gapBetweenPaths;
    private PointF innerBottomLeftCorner;
    private PointF innerBottomRightCorner;
    private Path innerClipPathForBorderRadius;
    private RectF innerClipTempRectForBorderRadius;
    private PointF innerTopLeftCorner;
    private PointF innerTopRightCorner;
    private boolean needUpdatePath;
    private Path outerClipPathForBorderRadius;
    private RectF outerClipTempRectForBorderRadius;
    private Path pathForBorder;
    private Path pathForOutline;
    private Path pathForSingleBorder;
    private RectF tempRectForCenterDrawPath;

    /* compiled from: BorderDrawable.kt */
    @Metadata(k = 3, mv = {2, 0, 0}, xi = 48)
    /* loaded from: classes.dex */
    public /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[BorderStyle.values().length];
            try {
                iArr[BorderStyle.SOLID.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                iArr[BorderStyle.DASHED.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                iArr[BorderStyle.DOTTED.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            $EnumSwitchMapping$0 = iArr;
        }
    }

    private final int fastBorderCompatibleColorOrZero(int borderLeft, int borderTop, int borderRight, int borderBottom, int colorLeft, int colorTop, int colorRight, int colorBottom) {
        int i = (borderBottom > 0 ? colorBottom : -1) & (borderLeft > 0 ? colorLeft : -1) & (borderTop > 0 ? colorTop : -1) & (borderRight > 0 ? colorRight : -1);
        if (borderLeft <= 0) {
            colorLeft = 0;
        }
        if (borderTop <= 0) {
            colorTop = 0;
        }
        int i2 = colorLeft | colorTop;
        if (borderRight <= 0) {
            colorRight = 0;
        }
        int i3 = i2 | colorRight;
        if (borderBottom <= 0) {
            colorBottom = 0;
        }
        if (i == (i3 | colorBottom)) {
            return i;
        }
        return 0;
    }

    private final int multiplyColorAlpha(int color, int rawAlpha) {
        if (rawAlpha == 255) {
            return color;
        }
        if (rawAlpha == 0) {
            return color & ViewCompat.MEASURED_SIZE_MASK;
        }
        return (color & ViewCompat.MEASURED_SIZE_MASK) | ((((color >>> 24) * ((rawAlpha + (rawAlpha >> 7)) >> 7)) >> 8) << 24);
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
    }

    public final Spacing getBorderWidth() {
        return this.borderWidth;
    }

    public final BorderRadiusStyle getBorderRadius() {
        return this.borderRadius;
    }

    public final void setBorderRadius(BorderRadiusStyle borderRadiusStyle) {
        this.borderRadius = borderRadiusStyle;
    }

    public final BorderInsets getBorderInsets() {
        return this.borderInsets;
    }

    public final void setBorderInsets(BorderInsets borderInsets) {
        this.borderInsets = borderInsets;
    }

    public BorderDrawable(Context context, Spacing spacing, BorderRadiusStyle borderRadiusStyle, BorderInsets borderInsets, BorderStyle borderStyle) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
        this.borderWidth = spacing;
        this.borderRadius = borderRadiusStyle;
        this.borderInsets = borderInsets;
        this.borderStyle = invalidatingAndPathChange(borderStyle);
        this.computedBorderColors = new ColorEdges(0, 0, 0, 0, 15, null);
        this.borderAlpha = 255;
        this.gapBetweenPaths = 0.8f;
        this.borderPaint = new Paint(1);
        this.needUpdatePath = true;
    }

    private final <T> ReadWriteProperty<Object, T> invalidatingAndPathChange(final T initialValue) {
        return new ObservableProperty<T>(initialValue) { // from class: com.facebook.react.uimanager.drawable.BorderDrawable$invalidatingAndPathChange$1
            @Override // kotlin.properties.ObservableProperty
            protected void afterChange(KProperty<?> property, T oldValue, T newValue) {
                Intrinsics.checkNotNullParameter(property, "property");
                if (Intrinsics.areEqual(oldValue, newValue)) {
                    return;
                }
                this.needUpdatePath = true;
                this.invalidateSelf();
            }
        };
    }

    public final BorderStyle getBorderStyle() {
        return (BorderStyle) this.borderStyle.getValue(this, $$delegatedProperties[0]);
    }

    public final void setBorderStyle(BorderStyle borderStyle) {
        this.borderStyle.setValue(this, $$delegatedProperties[0], borderStyle);
    }

    public final Path getInnerClipPathForBorderRadius() {
        return this.innerClipPathForBorderRadius;
    }

    @Override // android.graphics.drawable.Drawable
    public void invalidateSelf() {
        this.needUpdatePath = true;
        super.invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect bounds) {
        Intrinsics.checkNotNullParameter(bounds, "bounds");
        super.onBoundsChange(bounds);
        this.needUpdatePath = true;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        this.borderAlpha = alpha;
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    @Deprecated(message = "Deprecated in Java")
    public int getOpacity() {
        if (ComparisonsKt.maxOf(Color.alpha(multiplyColorAlpha(this.computedBorderColors.getLeft(), this.borderAlpha)), Color.alpha(multiplyColorAlpha(this.computedBorderColors.getTop(), this.borderAlpha)), Color.alpha(multiplyColorAlpha(this.computedBorderColors.getRight(), this.borderAlpha)), Color.alpha(multiplyColorAlpha(this.computedBorderColors.getBottom(), this.borderAlpha))) == 0) {
            return -2;
        }
        return ComparisonsKt.minOf(Color.alpha(multiplyColorAlpha(this.computedBorderColors.getLeft(), this.borderAlpha)), Color.alpha(multiplyColorAlpha(this.computedBorderColors.getTop(), this.borderAlpha)), Color.alpha(multiplyColorAlpha(this.computedBorderColors.getRight(), this.borderAlpha)), Color.alpha(multiplyColorAlpha(this.computedBorderColors.getBottom(), this.borderAlpha))) == 255 ? -1 : -3;
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        ColorEdges colorEdges;
        Intrinsics.checkNotNullParameter(canvas, "canvas");
        updatePathEffect();
        Integer[] numArr = this.borderColors;
        if (numArr == null || (colorEdges = BorderColors.m447resolveimpl(numArr, getLayoutDirection(), this.context)) == null) {
            colorEdges = this.computedBorderColors;
        }
        this.computedBorderColors = colorEdges;
        BorderRadiusStyle borderRadiusStyle = this.borderRadius;
        if (borderRadiusStyle != null && borderRadiusStyle.hasRoundedBorders()) {
            drawRoundedBorders(canvas);
        } else {
            drawRectangularBorders(canvas);
        }
    }

    private final float getInnerBorderRadius(float computedRadius, float borderWidth) {
        return RangesKt.coerceAtLeast(computedRadius - borderWidth, 0.0f);
    }

    public final void setBorderWidth(int position, float width) {
        Spacing spacing = this.borderWidth;
        if (FloatUtil.floatsEqual(spacing != null ? Float.valueOf(spacing.getRaw(position)) : null, Float.valueOf(width))) {
            return;
        }
        Spacing spacing2 = this.borderWidth;
        if (spacing2 != null) {
            spacing2.set(position, width);
        }
        if (position == 0 || position == 1 || position == 2 || position == 3 || position == 4 || position == 5 || position == 8) {
            this.needUpdatePath = true;
        }
        invalidateSelf();
    }

    public final void setBorderRadius(BorderRadiusProp property, LengthPercentage radius) {
        Intrinsics.checkNotNullParameter(property, "property");
        BorderRadiusStyle borderRadiusStyle = this.borderRadius;
        if (Intrinsics.areEqual(radius, borderRadiusStyle != null ? borderRadiusStyle.get(property) : null)) {
            return;
        }
        BorderRadiusStyle borderRadiusStyle2 = this.borderRadius;
        if (borderRadiusStyle2 != null) {
            borderRadiusStyle2.set(property, radius);
        }
        this.needUpdatePath = true;
        invalidateSelf();
    }

    public final void setBorderStyle(String style) {
        BorderStyle valueOf;
        if (style == null) {
            valueOf = null;
        } else {
            String upperCase = style.toUpperCase(Locale.ROOT);
            Intrinsics.checkNotNullExpressionValue(upperCase, "toUpperCase(...)");
            valueOf = BorderStyle.valueOf(upperCase);
        }
        setBorderStyle(valueOf);
        this.needUpdatePath = true;
        invalidateSelf();
    }

    public final void setBorderColor(LogicalEdge position, Integer color) {
        Intrinsics.checkNotNullParameter(position, "position");
        Integer[] numArr = this.borderColors;
        if (numArr == null) {
            numArr = BorderColors.m443constructorimpl$default(null, 1, null);
        }
        this.borderColors = numArr;
        if (numArr != null) {
            numArr[position.ordinal()] = color;
        }
        this.needUpdatePath = true;
        invalidateSelf();
    }

    public final int getBorderColor(LogicalEdge position) {
        Integer num;
        Intrinsics.checkNotNullParameter(position, "position");
        Integer[] numArr = this.borderColors;
        return (numArr == null || (num = numArr[position.ordinal()]) == null) ? ViewCompat.MEASURED_STATE_MASK : num.intValue();
    }

    public final void invalidateSelfAndUpdatePath() {
        this.needUpdatePath = true;
        invalidateSelf();
    }

    private final void drawRectangularBorders(Canvas canvas) {
        RectF computeBorderInsets = computeBorderInsets();
        int roundToInt = MathKt.roundToInt(computeBorderInsets.left);
        int roundToInt2 = MathKt.roundToInt(computeBorderInsets.top);
        int roundToInt3 = MathKt.roundToInt(computeBorderInsets.right);
        int roundToInt4 = MathKt.roundToInt(computeBorderInsets.bottom);
        if (roundToInt > 0 || roundToInt3 > 0 || roundToInt2 > 0 || roundToInt4 > 0) {
            Rect bounds = getBounds();
            Intrinsics.checkNotNullExpressionValue(bounds, "getBounds(...)");
            int i = bounds.left;
            int i2 = bounds.top;
            int fastBorderCompatibleColorOrZero = fastBorderCompatibleColorOrZero(roundToInt, roundToInt2, roundToInt3, roundToInt4, this.computedBorderColors.getLeft(), this.computedBorderColors.getTop(), this.computedBorderColors.getRight(), this.computedBorderColors.getBottom());
            if (fastBorderCompatibleColorOrZero != 0) {
                if (Color.alpha(fastBorderCompatibleColorOrZero) != 0) {
                    int i3 = bounds.right;
                    int i4 = bounds.bottom;
                    this.borderPaint.setColor(multiplyColorAlpha(fastBorderCompatibleColorOrZero, this.borderAlpha));
                    this.borderPaint.setStyle(Paint.Style.STROKE);
                    Path path = new Path();
                    this.pathForSingleBorder = path;
                    if (roundToInt > 0) {
                        path.reset();
                        int roundToInt5 = MathKt.roundToInt(computeBorderInsets.left);
                        updatePathEffect(roundToInt5);
                        this.borderPaint.setStrokeWidth(roundToInt5);
                        Path path2 = this.pathForSingleBorder;
                        if (path2 != null) {
                            path2.moveTo(i + (roundToInt5 / 2), i2);
                        }
                        Path path3 = this.pathForSingleBorder;
                        if (path3 != null) {
                            path3.lineTo(i + (roundToInt5 / 2), i4);
                        }
                        Path path4 = this.pathForSingleBorder;
                        if (path4 != null) {
                            canvas.drawPath(path4, this.borderPaint);
                        }
                    }
                    if (roundToInt2 > 0) {
                        Path path5 = this.pathForSingleBorder;
                        if (path5 != null) {
                            path5.reset();
                        }
                        int roundToInt6 = MathKt.roundToInt(computeBorderInsets.top);
                        updatePathEffect(roundToInt6);
                        this.borderPaint.setStrokeWidth(roundToInt6);
                        Path path6 = this.pathForSingleBorder;
                        if (path6 != null) {
                            path6.moveTo(i, i2 + (roundToInt6 / 2));
                        }
                        Path path7 = this.pathForSingleBorder;
                        if (path7 != null) {
                            path7.lineTo(i3, i2 + (roundToInt6 / 2));
                        }
                        Path path8 = this.pathForSingleBorder;
                        if (path8 != null) {
                            canvas.drawPath(path8, this.borderPaint);
                        }
                    }
                    if (roundToInt3 > 0) {
                        Path path9 = this.pathForSingleBorder;
                        if (path9 != null) {
                            path9.reset();
                        }
                        int roundToInt7 = MathKt.roundToInt(computeBorderInsets.right);
                        updatePathEffect(roundToInt7);
                        this.borderPaint.setStrokeWidth(roundToInt7);
                        Path path10 = this.pathForSingleBorder;
                        if (path10 != null) {
                            path10.moveTo(i3 - (roundToInt7 / 2), i2);
                        }
                        Path path11 = this.pathForSingleBorder;
                        if (path11 != null) {
                            path11.lineTo(i3 - (roundToInt7 / 2), i4);
                        }
                        Path path12 = this.pathForSingleBorder;
                        if (path12 != null) {
                            canvas.drawPath(path12, this.borderPaint);
                        }
                    }
                    if (roundToInt4 > 0) {
                        Path path13 = this.pathForSingleBorder;
                        if (path13 != null) {
                            path13.reset();
                        }
                        int roundToInt8 = MathKt.roundToInt(computeBorderInsets.bottom);
                        updatePathEffect(roundToInt8);
                        this.borderPaint.setStrokeWidth(roundToInt8);
                        Path path14 = this.pathForSingleBorder;
                        if (path14 != null) {
                            path14.moveTo(i, i4 - (roundToInt8 / 2));
                        }
                        Path path15 = this.pathForSingleBorder;
                        if (path15 != null) {
                            path15.lineTo(i3, i4 - (roundToInt8 / 2));
                        }
                        Path path16 = this.pathForSingleBorder;
                        if (path16 != null) {
                            canvas.drawPath(path16, this.borderPaint);
                            return;
                        }
                        return;
                    }
                    return;
                }
                return;
            }
            this.borderPaint.setAntiAlias(false);
            int width = bounds.width();
            int height = bounds.height();
            if (roundToInt > 0) {
                float f = i;
                float f2 = i2;
                float f3 = i + roundToInt;
                drawQuadrilateral(canvas, this.computedBorderColors.getLeft(), f, f2, f3, i2 + roundToInt2, f3, r0 - roundToInt4, f, i2 + height);
            }
            if (roundToInt2 > 0) {
                float f4 = i;
                float f5 = i2;
                float f6 = i + roundToInt;
                float f7 = i2 + roundToInt2;
                drawQuadrilateral(canvas, this.computedBorderColors.getTop(), f4, f5, f6, f7, r0 - roundToInt3, f7, i + width, f5);
            }
            if (roundToInt3 > 0) {
                int i5 = i + width;
                float f8 = i5;
                float f9 = i5 - roundToInt3;
                drawQuadrilateral(canvas, this.computedBorderColors.getRight(), f8, i2, f8, i2 + height, f9, r7 - roundToInt4, f9, i2 + roundToInt2);
            }
            if (roundToInt4 > 0) {
                int i6 = i2 + height;
                float f10 = i6;
                float f11 = i6 - roundToInt4;
                int bottom = this.computedBorderColors.getBottom();
                drawQuadrilateral(canvas, bottom, i, f10, i + width, f10, r8 - roundToInt3, f11, i + roundToInt, f11);
            }
            this.borderPaint.setAntiAlias(true);
        }
    }

    private final void drawRoundedBorders(Canvas canvas) {
        PointF pointF;
        PointF pointF2;
        PointF pointF3;
        PointF pointF4;
        float f;
        float f2;
        float f3;
        PointF pointF5;
        PointF pointF6;
        CornerRadii topLeft;
        CornerRadii pixelFromDIP;
        CornerRadii topLeft2;
        CornerRadii pixelFromDIP2;
        updatePath();
        canvas.save();
        Path path = this.outerClipPathForBorderRadius;
        if (path == null) {
            throw new IllegalStateException("Required value was null.".toString());
        }
        canvas.clipPath(path);
        RectF computeBorderInsets = computeBorderInsets();
        float f4 = 0.0f;
        if (computeBorderInsets.top > 0.0f || computeBorderInsets.bottom > 0.0f || computeBorderInsets.left > 0.0f || computeBorderInsets.right > 0.0f) {
            float fullBorderWidth = getFullBorderWidth();
            int borderColor = getBorderColor(LogicalEdge.ALL);
            if (computeBorderInsets.top != fullBorderWidth || computeBorderInsets.bottom != fullBorderWidth || computeBorderInsets.left != fullBorderWidth || computeBorderInsets.right != fullBorderWidth || this.computedBorderColors.getLeft() != borderColor || this.computedBorderColors.getTop() != borderColor || this.computedBorderColors.getRight() != borderColor || this.computedBorderColors.getBottom() != borderColor) {
                this.borderPaint.setStyle(Paint.Style.FILL);
                if (Build.VERSION.SDK_INT >= 26) {
                    Path path2 = this.innerClipPathForBorderRadius;
                    if (path2 == null) {
                        throw new IllegalStateException("Required value was null.".toString());
                    }
                    canvas.clipOutPath(path2);
                } else {
                    Path path3 = this.innerClipPathForBorderRadius;
                    if (path3 == null) {
                        throw new IllegalStateException("Required value was null.".toString());
                    }
                    canvas.clipPath(path3, Region.Op.DIFFERENCE);
                }
                RectF rectF = this.outerClipTempRectForBorderRadius;
                if (rectF == null) {
                    throw new IllegalStateException("Required value was null.".toString());
                }
                float f5 = rectF.left;
                float f6 = rectF.right;
                float f7 = rectF.top;
                float f8 = rectF.bottom;
                PointF pointF7 = this.innerTopLeftCorner;
                if (pointF7 == null) {
                    throw new IllegalStateException("Required value was null.".toString());
                }
                PointF pointF8 = this.innerTopRightCorner;
                if (pointF8 == null) {
                    throw new IllegalStateException("Required value was null.".toString());
                }
                PointF pointF9 = this.innerBottomLeftCorner;
                if (pointF9 == null) {
                    throw new IllegalStateException("Required value was null.".toString());
                }
                PointF pointF10 = this.innerBottomRightCorner;
                if (pointF10 == null) {
                    throw new IllegalStateException("Required value was null.".toString());
                }
                if (computeBorderInsets.left > 0.0f) {
                    float f9 = f7 - this.gapBetweenPaths;
                    float f10 = pointF7.x;
                    float f11 = pointF7.y - this.gapBetweenPaths;
                    float f12 = pointF9.x;
                    float f13 = pointF9.y;
                    float f14 = this.gapBetweenPaths;
                    pointF = pointF10;
                    pointF2 = pointF9;
                    pointF3 = pointF8;
                    pointF4 = pointF7;
                    f = f8;
                    f2 = f7;
                    f3 = f6;
                    drawQuadrilateral(canvas, this.computedBorderColors.getLeft(), f5, f9, f10, f11, f12, f13 + f14, f5, f8 + f14);
                } else {
                    pointF = pointF10;
                    pointF2 = pointF9;
                    pointF3 = pointF8;
                    pointF4 = pointF7;
                    f = f8;
                    f2 = f7;
                    f3 = f6;
                }
                if (computeBorderInsets.top > 0.0f) {
                    PointF pointF11 = pointF4;
                    PointF pointF12 = pointF3;
                    pointF5 = pointF12;
                    drawQuadrilateral(canvas, this.computedBorderColors.getTop(), f5 - this.gapBetweenPaths, f2, pointF11.x - this.gapBetweenPaths, pointF11.y, pointF12.x + this.gapBetweenPaths, pointF12.y, f3 + this.gapBetweenPaths, f2);
                } else {
                    pointF5 = pointF3;
                }
                if (computeBorderInsets.right > 0.0f) {
                    float f15 = f2 - this.gapBetweenPaths;
                    PointF pointF13 = pointF5;
                    float f16 = pointF13.x;
                    float f17 = pointF13.y - this.gapBetweenPaths;
                    PointF pointF14 = pointF;
                    float f18 = pointF14.x;
                    float f19 = pointF14.y;
                    float f20 = this.gapBetweenPaths;
                    pointF6 = pointF14;
                    drawQuadrilateral(canvas, this.computedBorderColors.getRight(), f3, f15, f16, f17, f18, f19 + f20, f3, f + f20);
                } else {
                    pointF6 = pointF;
                }
                if (computeBorderInsets.bottom > 0.0f) {
                    float f21 = f5 - this.gapBetweenPaths;
                    PointF pointF15 = pointF2;
                    float f22 = pointF15.x - this.gapBetweenPaths;
                    float f23 = pointF15.y;
                    PointF pointF16 = pointF6;
                    drawQuadrilateral(canvas, this.computedBorderColors.getBottom(), f21, f, f22, f23, pointF16.x + this.gapBetweenPaths, pointF16.y, f3 + this.gapBetweenPaths, f);
                }
            } else if (fullBorderWidth > 0.0f) {
                this.borderPaint.setColor(multiplyColorAlpha(borderColor, this.borderAlpha));
                this.borderPaint.setStyle(Paint.Style.STROKE);
                this.borderPaint.setStrokeWidth(fullBorderWidth);
                ComputedBorderRadius computedBorderRadius = this.computedBorderRadius;
                if (computedBorderRadius != null && computedBorderRadius.isUniform()) {
                    RectF rectF2 = this.tempRectForCenterDrawPath;
                    if (rectF2 != null) {
                        ComputedBorderRadius computedBorderRadius2 = this.computedBorderRadius;
                        float horizontal = ((computedBorderRadius2 == null || (topLeft2 = computedBorderRadius2.getTopLeft()) == null || (pixelFromDIP2 = topLeft2.toPixelFromDIP()) == null) ? 0.0f : pixelFromDIP2.getHorizontal()) - (computeBorderInsets.left * 0.5f);
                        ComputedBorderRadius computedBorderRadius3 = this.computedBorderRadius;
                        if (computedBorderRadius3 != null && (topLeft = computedBorderRadius3.getTopLeft()) != null && (pixelFromDIP = topLeft.toPixelFromDIP()) != null) {
                            f4 = pixelFromDIP.getVertical();
                        }
                        canvas.drawRoundRect(rectF2, horizontal, f4 - (computeBorderInsets.top * 0.5f), this.borderPaint);
                    }
                } else {
                    Path path4 = this.centerDrawPath;
                    if (path4 == null) {
                        throw new IllegalStateException("Required value was null.".toString());
                    }
                    canvas.drawPath(path4, this.borderPaint);
                }
            }
        }
        canvas.restore();
    }

    private final void drawQuadrilateral(Canvas canvas, int fillColor, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        if (fillColor == 0) {
            return;
        }
        if (this.pathForBorder == null) {
            this.pathForBorder = new Path();
        }
        this.borderPaint.setColor(multiplyColorAlpha(fillColor, this.borderAlpha));
        Path path = this.pathForBorder;
        if (path != null) {
            path.reset();
        }
        Path path2 = this.pathForBorder;
        if (path2 != null) {
            path2.moveTo(x1, y1);
        }
        Path path3 = this.pathForBorder;
        if (path3 != null) {
            path3.lineTo(x2, y2);
        }
        Path path4 = this.pathForBorder;
        if (path4 != null) {
            path4.lineTo(x3, y3);
        }
        Path path5 = this.pathForBorder;
        if (path5 != null) {
            path5.lineTo(x4, y4);
        }
        Path path6 = this.pathForBorder;
        if (path6 != null) {
            path6.lineTo(x1, y1);
        }
        Path path7 = this.pathForBorder;
        if (path7 != null) {
            canvas.drawPath(path7, this.borderPaint);
        }
    }

    private final RectF computeBorderInsets() {
        RectF resolve;
        BorderInsets borderInsets = this.borderInsets;
        if (borderInsets != null && (resolve = borderInsets.resolve(getLayoutDirection(), this.context)) != null) {
            return new RectF(Float.isNaN(resolve.left) ? 0.0f : PixelUtil.INSTANCE.dpToPx(resolve.left), Float.isNaN(resolve.top) ? 0.0f : PixelUtil.INSTANCE.dpToPx(resolve.top), Float.isNaN(resolve.right) ? 0.0f : PixelUtil.INSTANCE.dpToPx(resolve.right), Float.isNaN(resolve.bottom) ? 0.0f : PixelUtil.INSTANCE.dpToPx(resolve.bottom));
        }
        return new RectF(0.0f, 0.0f, 0.0f, 0.0f);
    }

    private final float getFullBorderWidth() {
        Spacing spacing = this.borderWidth;
        float raw = spacing != null ? spacing.getRaw(8) : Float.NaN;
        if (Float.isNaN(raw)) {
            return 0.0f;
        }
        return raw;
    }

    private final void updatePathEffect() {
        BorderStyle borderStyle = getBorderStyle();
        if (borderStyle != null) {
            this.borderPaint.setPathEffect(getBorderStyle() != null ? getPathEffect(borderStyle, getFullBorderWidth()) : null);
        }
    }

    private final void updatePathEffect(int borderWidth) {
        BorderStyle borderStyle = getBorderStyle();
        if (borderStyle != null) {
            this.borderPaint.setPathEffect(getBorderStyle() != null ? getPathEffect(borderStyle, borderWidth) : null);
        }
    }

    private final PathEffect getPathEffect(BorderStyle style, float borderWidth) {
        int i = WhenMappings.$EnumSwitchMapping$0[style.ordinal()];
        if (i == 1) {
            return null;
        }
        if (i == 2) {
            float f = borderWidth * 3;
            return new DashPathEffect(new float[]{f, f, f, f}, 0.0f);
        }
        if (i != 3) {
            throw new NoWhenBranchMatchedException();
        }
        return new DashPathEffect(new float[]{borderWidth, borderWidth, borderWidth, borderWidth}, 0.0f);
    }

    private final void getEllipseIntersectionWithLine(double ellipseBoundsLeft, double ellipseBoundsTop, double ellipseBoundsRight, double ellipseBoundsBottom, double lineStartX, double lineStartY, double lineEndX, double lineEndY, PointF result) {
        double d = 2;
        double d2 = (ellipseBoundsLeft + ellipseBoundsRight) / d;
        double d3 = (ellipseBoundsTop + ellipseBoundsBottom) / d;
        double d4 = lineStartX - d2;
        double d5 = lineStartY - d3;
        double abs = Math.abs(ellipseBoundsRight - ellipseBoundsLeft) / d;
        double abs2 = Math.abs(ellipseBoundsBottom - ellipseBoundsTop) / d;
        double d6 = ((lineEndY - d3) - d5) / ((lineEndX - d2) - d4);
        double d7 = d5 - (d4 * d6);
        double d8 = abs2 * abs2;
        double d9 = abs * abs;
        double d10 = d8 + (d9 * d6 * d6);
        double d11 = d * abs * abs * d7 * d6;
        double d12 = d * d10;
        double sqrt = ((-d11) / d12) - Math.sqrt(((-(d9 * ((d7 * d7) - d8))) / d10) + Math.pow(d11 / d12, 2.0d));
        double d13 = (d6 * sqrt) + d7;
        double d14 = sqrt + d2;
        double d15 = d13 + d3;
        if (Double.isNaN(d14) || Double.isNaN(d15)) {
            return;
        }
        result.x = (float) d14;
        result.y = (float) d15;
    }

    private final void updatePath() {
        ComputedBorderRadius computedBorderRadius;
        CornerRadii cornerRadii;
        CornerRadii cornerRadii2;
        CornerRadii cornerRadii3;
        CornerRadii cornerRadii4;
        float f;
        Path path;
        RectF rectF;
        RectF rectF2;
        RectF rectF3;
        RectF rectF4;
        RectF rectF5;
        RectF rectF6;
        Path path2;
        Path path3;
        CornerRadii bottomRight;
        CornerRadii bottomLeft;
        CornerRadii topRight;
        CornerRadii topLeft;
        if (this.needUpdatePath) {
            this.needUpdatePath = false;
            Path path4 = this.innerClipPathForBorderRadius;
            if (path4 == null) {
                path4 = new Path();
            }
            this.innerClipPathForBorderRadius = path4;
            Path path5 = this.outerClipPathForBorderRadius;
            if (path5 == null) {
                path5 = new Path();
            }
            this.outerClipPathForBorderRadius = path5;
            this.pathForOutline = new Path();
            RectF rectF7 = this.innerClipTempRectForBorderRadius;
            if (rectF7 == null) {
                rectF7 = new RectF();
            }
            this.innerClipTempRectForBorderRadius = rectF7;
            RectF rectF8 = this.outerClipTempRectForBorderRadius;
            if (rectF8 == null) {
                rectF8 = new RectF();
            }
            this.outerClipTempRectForBorderRadius = rectF8;
            RectF rectF9 = this.tempRectForCenterDrawPath;
            if (rectF9 == null) {
                rectF9 = new RectF();
            }
            this.tempRectForCenterDrawPath = rectF9;
            Path path6 = this.innerClipPathForBorderRadius;
            if (path6 != null) {
                path6.reset();
                Unit unit = Unit.INSTANCE;
            }
            Path path7 = this.outerClipPathForBorderRadius;
            if (path7 != null) {
                path7.reset();
                Unit unit2 = Unit.INSTANCE;
            }
            RectF rectF10 = this.innerClipTempRectForBorderRadius;
            if (rectF10 != null) {
                rectF10.set(getBounds());
                Unit unit3 = Unit.INSTANCE;
            }
            RectF rectF11 = this.outerClipTempRectForBorderRadius;
            if (rectF11 != null) {
                rectF11.set(getBounds());
                Unit unit4 = Unit.INSTANCE;
            }
            RectF rectF12 = this.tempRectForCenterDrawPath;
            if (rectF12 != null) {
                rectF12.set(getBounds());
                Unit unit5 = Unit.INSTANCE;
            }
            RectF computeBorderInsets = computeBorderInsets();
            if (Color.alpha(this.computedBorderColors.getLeft()) != 0 || Color.alpha(this.computedBorderColors.getTop()) != 0 || Color.alpha(this.computedBorderColors.getRight()) != 0 || Color.alpha(this.computedBorderColors.getBottom()) != 0) {
                RectF rectF13 = this.innerClipTempRectForBorderRadius;
                if (rectF13 != null) {
                    rectF13.top = rectF13 != null ? rectF13.top + computeBorderInsets.top : 0.0f;
                    Unit unit6 = Unit.INSTANCE;
                }
                RectF rectF14 = this.innerClipTempRectForBorderRadius;
                if (rectF14 != null) {
                    rectF14.bottom = rectF14 != null ? rectF14.bottom - computeBorderInsets.bottom : 0.0f;
                    Unit unit7 = Unit.INSTANCE;
                }
                RectF rectF15 = this.innerClipTempRectForBorderRadius;
                if (rectF15 != null) {
                    rectF15.left = rectF15 != null ? rectF15.left + computeBorderInsets.left : 0.0f;
                    Unit unit8 = Unit.INSTANCE;
                }
                RectF rectF16 = this.innerClipTempRectForBorderRadius;
                if (rectF16 != null) {
                    rectF16.right = rectF16 != null ? rectF16.right - computeBorderInsets.right : 0.0f;
                    Unit unit9 = Unit.INSTANCE;
                }
            }
            RectF rectF17 = this.tempRectForCenterDrawPath;
            if (rectF17 != null) {
                rectF17.top = rectF17 != null ? rectF17.top + (computeBorderInsets.top * 0.5f) : 0.0f;
                Unit unit10 = Unit.INSTANCE;
            }
            RectF rectF18 = this.tempRectForCenterDrawPath;
            if (rectF18 != null) {
                rectF18.bottom = rectF18 != null ? rectF18.bottom - (computeBorderInsets.bottom * 0.5f) : 0.0f;
                Unit unit11 = Unit.INSTANCE;
            }
            RectF rectF19 = this.tempRectForCenterDrawPath;
            if (rectF19 != null) {
                rectF19.left = rectF19 != null ? rectF19.left + (computeBorderInsets.left * 0.5f) : 0.0f;
                Unit unit12 = Unit.INSTANCE;
            }
            RectF rectF20 = this.tempRectForCenterDrawPath;
            if (rectF20 != null) {
                rectF20.right = rectF20 != null ? rectF20.right - (computeBorderInsets.right * 0.5f) : 0.0f;
                Unit unit13 = Unit.INSTANCE;
            }
            BorderRadiusStyle borderRadiusStyle = this.borderRadius;
            if (borderRadiusStyle != null) {
                int layoutDirection = getLayoutDirection();
                Context context = this.context;
                RectF rectF21 = this.outerClipTempRectForBorderRadius;
                float pxToDp = rectF21 != null ? PixelUtil.INSTANCE.pxToDp(rectF21.width()) : 0.0f;
                RectF rectF22 = this.outerClipTempRectForBorderRadius;
                computedBorderRadius = borderRadiusStyle.resolve(layoutDirection, context, pxToDp, rectF22 != null ? PixelUtil.INSTANCE.pxToDp(rectF22.height()) : 0.0f);
            } else {
                computedBorderRadius = null;
            }
            this.computedBorderRadius = computedBorderRadius;
            if (computedBorderRadius == null || (topLeft = computedBorderRadius.getTopLeft()) == null || (cornerRadii = topLeft.toPixelFromDIP()) == null) {
                cornerRadii = new CornerRadii(0.0f, 0.0f);
            }
            ComputedBorderRadius computedBorderRadius2 = this.computedBorderRadius;
            if (computedBorderRadius2 == null || (topRight = computedBorderRadius2.getTopRight()) == null || (cornerRadii2 = topRight.toPixelFromDIP()) == null) {
                cornerRadii2 = new CornerRadii(0.0f, 0.0f);
            }
            ComputedBorderRadius computedBorderRadius3 = this.computedBorderRadius;
            if (computedBorderRadius3 == null || (bottomLeft = computedBorderRadius3.getBottomLeft()) == null || (cornerRadii3 = bottomLeft.toPixelFromDIP()) == null) {
                cornerRadii3 = new CornerRadii(0.0f, 0.0f);
            }
            ComputedBorderRadius computedBorderRadius4 = this.computedBorderRadius;
            if (computedBorderRadius4 == null || (bottomRight = computedBorderRadius4.getBottomRight()) == null || (cornerRadii4 = bottomRight.toPixelFromDIP()) == null) {
                cornerRadii4 = new CornerRadii(0.0f, 0.0f);
            }
            float innerBorderRadius = getInnerBorderRadius(cornerRadii.getHorizontal(), computeBorderInsets.left);
            float innerBorderRadius2 = getInnerBorderRadius(cornerRadii.getVertical(), computeBorderInsets.top);
            float innerBorderRadius3 = getInnerBorderRadius(cornerRadii2.getHorizontal(), computeBorderInsets.right);
            float innerBorderRadius4 = getInnerBorderRadius(cornerRadii2.getVertical(), computeBorderInsets.top);
            float innerBorderRadius5 = getInnerBorderRadius(cornerRadii4.getHorizontal(), computeBorderInsets.right);
            float innerBorderRadius6 = getInnerBorderRadius(cornerRadii4.getVertical(), computeBorderInsets.bottom);
            float innerBorderRadius7 = getInnerBorderRadius(cornerRadii3.getHorizontal(), computeBorderInsets.left);
            float innerBorderRadius8 = getInnerBorderRadius(cornerRadii3.getVertical(), computeBorderInsets.bottom);
            RectF rectF23 = this.innerClipTempRectForBorderRadius;
            if (rectF23 != null && (path3 = this.innerClipPathForBorderRadius) != null) {
                path3.addRoundRect(rectF23, new float[]{innerBorderRadius, innerBorderRadius2, innerBorderRadius3, innerBorderRadius4, innerBorderRadius5, innerBorderRadius6, innerBorderRadius7, innerBorderRadius8}, Path.Direction.CW);
                Unit unit14 = Unit.INSTANCE;
            }
            RectF rectF24 = this.outerClipTempRectForBorderRadius;
            if (rectF24 != null && (path2 = this.outerClipPathForBorderRadius) != null) {
                path2.addRoundRect(rectF24, new float[]{cornerRadii.getHorizontal(), cornerRadii.getVertical(), cornerRadii2.getHorizontal(), cornerRadii2.getVertical(), cornerRadii4.getHorizontal(), cornerRadii4.getVertical(), cornerRadii3.getHorizontal(), cornerRadii3.getVertical()}, Path.Direction.CW);
                Unit unit15 = Unit.INSTANCE;
            }
            Spacing spacing = this.borderWidth;
            float f2 = spacing != null ? spacing.get(8) / 2.0f : 0.0f;
            Path path8 = this.pathForOutline;
            if (path8 != null) {
                RectF rectF25 = new RectF(getBounds());
                float horizontal = cornerRadii.getHorizontal() + f2;
                float vertical = cornerRadii.getVertical() + f2;
                float horizontal2 = cornerRadii2.getHorizontal() + f2;
                float vertical2 = cornerRadii2.getVertical() + f2;
                float horizontal3 = cornerRadii4.getHorizontal() + f2;
                float vertical3 = cornerRadii4.getVertical() + f2;
                float horizontal4 = cornerRadii3.getHorizontal() + f2;
                float vertical4 = cornerRadii3.getVertical() + f2;
                f = innerBorderRadius2;
                path8.addRoundRect(rectF25, new float[]{horizontal, vertical, horizontal2, vertical2, horizontal3, vertical3, horizontal4, vertical4}, Path.Direction.CW);
                Unit unit16 = Unit.INSTANCE;
            } else {
                f = innerBorderRadius2;
            }
            ComputedBorderRadius computedBorderRadius5 = this.computedBorderRadius;
            if (computedBorderRadius5 == null || !computedBorderRadius5.isUniform()) {
                Path path9 = this.centerDrawPath;
                if (path9 == null) {
                    path9 = new Path();
                }
                this.centerDrawPath = path9;
                if (path9 != null) {
                    path9.reset();
                    Unit unit17 = Unit.INSTANCE;
                }
                RectF rectF26 = this.tempRectForCenterDrawPath;
                if (rectF26 != null && (path = this.centerDrawPath) != null) {
                    path.addRoundRect(rectF26, new float[]{cornerRadii.getHorizontal() - (computeBorderInsets.left * 0.5f), cornerRadii.getVertical() - (computeBorderInsets.top * 0.5f), cornerRadii2.getHorizontal() - (computeBorderInsets.right * 0.5f), cornerRadii2.getVertical() - (computeBorderInsets.top * 0.5f), cornerRadii4.getHorizontal() - (computeBorderInsets.right * 0.5f), cornerRadii4.getVertical() - (computeBorderInsets.bottom * 0.5f), cornerRadii3.getHorizontal() - (computeBorderInsets.left * 0.5f), cornerRadii3.getVertical() - (computeBorderInsets.bottom * 0.5f)}, Path.Direction.CW);
                    Unit unit18 = Unit.INSTANCE;
                }
            }
            RectF rectF27 = this.innerClipTempRectForBorderRadius;
            RectF rectF28 = this.outerClipTempRectForBorderRadius;
            if (rectF27 == null || rectF28 == null) {
                return;
            }
            PointF pointF = this.innerTopLeftCorner;
            if (pointF == null) {
                pointF = new PointF();
            }
            this.innerTopLeftCorner = pointF;
            if (pointF != null) {
                pointF.x = rectF27.left;
                Unit unit19 = Unit.INSTANCE;
            }
            PointF pointF2 = this.innerTopLeftCorner;
            if (pointF2 != null) {
                pointF2.y = rectF27.top;
                Unit unit20 = Unit.INSTANCE;
            }
            PointF pointF3 = this.innerTopLeftCorner;
            if (pointF3 != null) {
                float f3 = 2;
                rectF = rectF28;
                rectF2 = rectF27;
                getEllipseIntersectionWithLine(rectF27.left, rectF27.top, rectF27.left + (innerBorderRadius * f3), rectF27.top + (f3 * f), rectF28.left, rectF28.top, rectF27.left, rectF27.top, pointF3);
                Unit unit21 = Unit.INSTANCE;
                Unit unit22 = Unit.INSTANCE;
            } else {
                rectF = rectF28;
                rectF2 = rectF27;
            }
            PointF pointF4 = this.innerBottomLeftCorner;
            if (pointF4 == null) {
                pointF4 = new PointF();
            }
            this.innerBottomLeftCorner = pointF4;
            RectF rectF29 = rectF2;
            if (pointF4 != null) {
                pointF4.x = rectF29.left;
                Unit unit23 = Unit.INSTANCE;
            }
            PointF pointF5 = this.innerBottomLeftCorner;
            if (pointF5 != null) {
                pointF5.y = rectF29.bottom;
                Unit unit24 = Unit.INSTANCE;
            }
            PointF pointF6 = this.innerBottomLeftCorner;
            if (pointF6 != null) {
                float f4 = 2;
                rectF4 = rectF;
                rectF3 = rectF29;
                getEllipseIntersectionWithLine(rectF29.left, rectF29.bottom - (innerBorderRadius8 * f4), rectF29.left + (f4 * innerBorderRadius7), rectF29.bottom, r11.left, r11.bottom, rectF29.left, rectF29.bottom, pointF6);
                Unit unit25 = Unit.INSTANCE;
                Unit unit26 = Unit.INSTANCE;
            } else {
                rectF3 = rectF29;
                rectF4 = rectF;
            }
            PointF pointF7 = this.innerTopRightCorner;
            if (pointF7 == null) {
                pointF7 = new PointF();
            }
            this.innerTopRightCorner = pointF7;
            RectF rectF30 = rectF3;
            if (pointF7 != null) {
                pointF7.x = rectF30.right;
                Unit unit27 = Unit.INSTANCE;
            }
            PointF pointF8 = this.innerTopRightCorner;
            if (pointF8 != null) {
                pointF8.y = rectF30.top;
                Unit unit28 = Unit.INSTANCE;
            }
            PointF pointF9 = this.innerTopRightCorner;
            if (pointF9 != null) {
                float f5 = 2;
                rectF6 = rectF4;
                rectF5 = rectF30;
                getEllipseIntersectionWithLine(rectF30.right - (innerBorderRadius3 * f5), rectF30.top, rectF30.right, rectF30.top + (f5 * innerBorderRadius4), r11.right, r11.top, rectF30.right, rectF30.top, pointF9);
                Unit unit29 = Unit.INSTANCE;
                Unit unit30 = Unit.INSTANCE;
            } else {
                rectF5 = rectF30;
                rectF6 = rectF4;
            }
            PointF pointF10 = this.innerBottomRightCorner;
            if (pointF10 == null) {
                pointF10 = new PointF();
            }
            this.innerBottomRightCorner = pointF10;
            RectF rectF31 = rectF5;
            if (pointF10 != null) {
                pointF10.x = rectF31.right;
                Unit unit31 = Unit.INSTANCE;
            }
            PointF pointF11 = this.innerBottomRightCorner;
            if (pointF11 != null) {
                pointF11.y = rectF31.bottom;
                Unit unit32 = Unit.INSTANCE;
            }
            PointF pointF12 = this.innerBottomRightCorner;
            if (pointF12 != null) {
                float f6 = 2;
                RectF rectF32 = rectF6;
                getEllipseIntersectionWithLine(rectF31.right - (innerBorderRadius5 * f6), rectF31.bottom - (f6 * innerBorderRadius6), rectF31.right, rectF31.bottom, rectF32.right, rectF32.bottom, rectF31.right, rectF31.bottom, pointF12);
                Unit unit33 = Unit.INSTANCE;
                Unit unit34 = Unit.INSTANCE;
            }
        }
    }
}
