package com.facebook.react.uimanager.drawable;

import android.content.Context;
import android.graphics.Outline;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import com.facebook.react.modules.appstate.AppStateModule;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.ViewProps;
import com.facebook.react.uimanager.style.BorderInsets;
import com.facebook.react.uimanager.style.BorderRadiusStyle;
import com.facebook.react.uimanager.style.ComputedBorderRadius;
import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: CompositeBackgroundDrawable.kt */
@Metadata(d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u001f\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0000\u0018\u0000 72\u00020\u0001:\u00017B\u008f\u0001\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u0007\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\t\u0012\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u000b\u0012\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\r\u0012\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\u0005\u0012\u000e\b\u0002\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00050\u0007\u0012\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u0011\u0012\n\b\u0002\u0010\u0012\u001a\u0004\u0018\u00010\u0013\u0012\n\b\u0002\u0010\u0014\u001a\u0004\u0018\u00010\u0015¢\u0006\u0004\b\u0016\u0010\u0017J\u0010\u0010.\u001a\u00020\u00002\b\u0010\b\u001a\u0004\u0018\u00010\tJ\u0010\u0010/\u001a\u00020\u00002\b\u0010\n\u001a\u0004\u0018\u00010\u000bJ\"\u00100\u001a\u00020\u00002\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u00072\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00050\u0007J\u000e\u00101\u001a\u00020\u00002\u0006\u0010\f\u001a\u00020\rJ\u000e\u00102\u001a\u00020\u00002\u0006\u0010\u0010\u001a\u00020\u0011J\u0010\u00103\u001a\u00020\u00002\b\u00104\u001a\u0004\u0018\u00010\u0005J\u0010\u0010$\u001a\u0002052\u0006\u0010\u0010\u001a\u000206H\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u0017\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0013\u0010\b\u001a\u0004\u0018\u00010\t¢\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001dR\u0013\u0010\n\u001a\u0004\u0018\u00010\u000b¢\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001fR\u0013\u0010\f\u001a\u0004\u0018\u00010\r¢\u0006\b\n\u0000\u001a\u0004\b \u0010!R\u0013\u0010\u000e\u001a\u0004\u0018\u00010\u0005¢\u0006\b\n\u0000\u001a\u0004\b\"\u0010\u0019R\u0017\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00050\u0007¢\u0006\b\n\u0000\u001a\u0004\b#\u0010\u001bR\u0013\u0010\u0010\u001a\u0004\u0018\u00010\u0011¢\u0006\b\n\u0000\u001a\u0004\b$\u0010%R\u001c\u0010\u0012\u001a\u0004\u0018\u00010\u0013X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b&\u0010'\"\u0004\b(\u0010)R\u001c\u0010\u0014\u001a\u0004\u0018\u00010\u0015X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b*\u0010+\"\u0004\b,\u0010-¨\u00068"}, d2 = {"Lcom/facebook/react/uimanager/drawable/CompositeBackgroundDrawable;", "Landroid/graphics/drawable/LayerDrawable;", "context", "Landroid/content/Context;", "originalBackground", "Landroid/graphics/drawable/Drawable;", "outerShadows", "", "cssBackground", "Lcom/facebook/react/uimanager/drawable/CSSBackgroundDrawable;", AppStateModule.APP_STATE_BACKGROUND, "Lcom/facebook/react/uimanager/drawable/BackgroundDrawable;", "border", "Lcom/facebook/react/uimanager/drawable/BorderDrawable;", "feedbackUnderlay", "innerShadows", "outline", "Lcom/facebook/react/uimanager/drawable/OutlineDrawable;", "borderInsets", "Lcom/facebook/react/uimanager/style/BorderInsets;", ViewProps.BORDER_RADIUS, "Lcom/facebook/react/uimanager/style/BorderRadiusStyle;", "<init>", "(Landroid/content/Context;Landroid/graphics/drawable/Drawable;Ljava/util/List;Lcom/facebook/react/uimanager/drawable/CSSBackgroundDrawable;Lcom/facebook/react/uimanager/drawable/BackgroundDrawable;Lcom/facebook/react/uimanager/drawable/BorderDrawable;Landroid/graphics/drawable/Drawable;Ljava/util/List;Lcom/facebook/react/uimanager/drawable/OutlineDrawable;Lcom/facebook/react/uimanager/style/BorderInsets;Lcom/facebook/react/uimanager/style/BorderRadiusStyle;)V", "getOriginalBackground", "()Landroid/graphics/drawable/Drawable;", "getOuterShadows", "()Ljava/util/List;", "getCssBackground", "()Lcom/facebook/react/uimanager/drawable/CSSBackgroundDrawable;", "getBackground", "()Lcom/facebook/react/uimanager/drawable/BackgroundDrawable;", "getBorder", "()Lcom/facebook/react/uimanager/drawable/BorderDrawable;", "getFeedbackUnderlay", "getInnerShadows", "getOutline", "()Lcom/facebook/react/uimanager/drawable/OutlineDrawable;", "getBorderInsets", "()Lcom/facebook/react/uimanager/style/BorderInsets;", "setBorderInsets", "(Lcom/facebook/react/uimanager/style/BorderInsets;)V", "getBorderRadius", "()Lcom/facebook/react/uimanager/style/BorderRadiusStyle;", "setBorderRadius", "(Lcom/facebook/react/uimanager/style/BorderRadiusStyle;)V", "withNewCssBackground", "withNewBackground", "withNewShadows", "withNewBorder", "withNewOutline", "withNewFeedbackUnderlay", "newUnderlay", "", "Landroid/graphics/Outline;", "Companion", "ReactAndroid_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes.dex */
public final class CompositeBackgroundDrawable extends LayerDrawable {

    /* renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private final BackgroundDrawable background;
    private final BorderDrawable border;
    private BorderInsets borderInsets;
    private BorderRadiusStyle borderRadius;
    private final Context context;
    private final CSSBackgroundDrawable cssBackground;
    private final Drawable feedbackUnderlay;
    private final List<Drawable> innerShadows;
    private final Drawable originalBackground;
    private final List<Drawable> outerShadows;
    private final OutlineDrawable outline;

    public final Drawable getOriginalBackground() {
        return this.originalBackground;
    }

    public /* synthetic */ CompositeBackgroundDrawable(Context context, Drawable drawable, List list, CSSBackgroundDrawable cSSBackgroundDrawable, BackgroundDrawable backgroundDrawable, BorderDrawable borderDrawable, Drawable drawable2, List list2, OutlineDrawable outlineDrawable, BorderInsets borderInsets, BorderRadiusStyle borderRadiusStyle, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(context, (i & 2) != 0 ? null : drawable, (i & 4) != 0 ? CollectionsKt.emptyList() : list, (i & 8) != 0 ? null : cSSBackgroundDrawable, (i & 16) != 0 ? null : backgroundDrawable, (i & 32) != 0 ? null : borderDrawable, (i & 64) != 0 ? null : drawable2, (i & 128) != 0 ? CollectionsKt.emptyList() : list2, (i & 256) != 0 ? null : outlineDrawable, (i & 512) != 0 ? null : borderInsets, (i & 1024) == 0 ? borderRadiusStyle : null);
    }

    public final List<Drawable> getOuterShadows() {
        return this.outerShadows;
    }

    public final CSSBackgroundDrawable getCssBackground() {
        return this.cssBackground;
    }

    public final BackgroundDrawable getBackground() {
        return this.background;
    }

    public final BorderDrawable getBorder() {
        return this.border;
    }

    public final Drawable getFeedbackUnderlay() {
        return this.feedbackUnderlay;
    }

    public final List<Drawable> getInnerShadows() {
        return this.innerShadows;
    }

    public final OutlineDrawable getOutline() {
        return this.outline;
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

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    /* JADX WARN: Multi-variable type inference failed */
    public CompositeBackgroundDrawable(Context context, Drawable drawable, List<? extends Drawable> outerShadows, CSSBackgroundDrawable cSSBackgroundDrawable, BackgroundDrawable backgroundDrawable, BorderDrawable borderDrawable, Drawable drawable2, List<? extends Drawable> innerShadows, OutlineDrawable outlineDrawable, BorderInsets borderInsets, BorderRadiusStyle borderRadiusStyle) {
        super(INSTANCE.createLayersArray(drawable, outerShadows, cSSBackgroundDrawable, backgroundDrawable, borderDrawable, drawable2, innerShadows, outlineDrawable));
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(outerShadows, "outerShadows");
        Intrinsics.checkNotNullParameter(innerShadows, "innerShadows");
        this.context = context;
        this.originalBackground = drawable;
        this.outerShadows = outerShadows;
        this.cssBackground = cSSBackgroundDrawable;
        this.background = backgroundDrawable;
        this.border = borderDrawable;
        this.feedbackUnderlay = drawable2;
        this.innerShadows = innerShadows;
        this.outline = outlineDrawable;
        this.borderInsets = borderInsets;
        this.borderRadius = borderRadiusStyle;
        setPaddingMode(1);
    }

    public final CompositeBackgroundDrawable withNewCssBackground(CSSBackgroundDrawable cssBackground) {
        return new CompositeBackgroundDrawable(this.context, this.originalBackground, this.outerShadows, cssBackground, this.background, this.border, this.feedbackUnderlay, this.innerShadows, this.outline, this.borderInsets, this.borderRadius);
    }

    public final CompositeBackgroundDrawable withNewBackground(BackgroundDrawable background) {
        return new CompositeBackgroundDrawable(this.context, this.originalBackground, this.outerShadows, this.cssBackground, background, this.border, this.feedbackUnderlay, this.innerShadows, this.outline, this.borderInsets, this.borderRadius);
    }

    public final CompositeBackgroundDrawable withNewShadows(List<? extends Drawable> outerShadows, List<? extends Drawable> innerShadows) {
        Intrinsics.checkNotNullParameter(outerShadows, "outerShadows");
        Intrinsics.checkNotNullParameter(innerShadows, "innerShadows");
        return new CompositeBackgroundDrawable(this.context, this.originalBackground, outerShadows, this.cssBackground, this.background, this.border, this.feedbackUnderlay, innerShadows, this.outline, this.borderInsets, this.borderRadius);
    }

    public final CompositeBackgroundDrawable withNewBorder(BorderDrawable border) {
        Intrinsics.checkNotNullParameter(border, "border");
        return new CompositeBackgroundDrawable(this.context, this.originalBackground, this.outerShadows, this.cssBackground, this.background, border, this.feedbackUnderlay, this.innerShadows, this.outline, this.borderInsets, this.borderRadius);
    }

    public final CompositeBackgroundDrawable withNewOutline(OutlineDrawable outline) {
        Intrinsics.checkNotNullParameter(outline, "outline");
        return new CompositeBackgroundDrawable(this.context, this.originalBackground, this.outerShadows, this.cssBackground, this.background, this.border, this.feedbackUnderlay, this.innerShadows, outline, this.borderInsets, this.borderRadius);
    }

    public final CompositeBackgroundDrawable withNewFeedbackUnderlay(Drawable newUnderlay) {
        return new CompositeBackgroundDrawable(this.context, this.originalBackground, this.outerShadows, this.cssBackground, this.background, this.border, newUnderlay, this.innerShadows, this.outline, this.borderInsets, this.borderRadius);
    }

    @Override // android.graphics.drawable.LayerDrawable, android.graphics.drawable.Drawable
    public void getOutline(Outline outline) {
        Intrinsics.checkNotNullParameter(outline, "outline");
        BorderRadiusStyle borderRadiusStyle = this.borderRadius;
        if (borderRadiusStyle != null && borderRadiusStyle.hasRoundedBorders()) {
            Path path = new Path();
            BorderRadiusStyle borderRadiusStyle2 = this.borderRadius;
            ComputedBorderRadius resolve = borderRadiusStyle2 != null ? borderRadiusStyle2.resolve(getLayoutDirection(), this.context, getBounds().width(), getBounds().height()) : null;
            BorderInsets borderInsets = this.borderInsets;
            RectF resolve2 = borderInsets != null ? borderInsets.resolve(getLayoutDirection(), this.context) : null;
            if (resolve != null) {
                path.addRoundRect(new RectF(getBounds()), new float[]{PixelUtil.INSTANCE.dpToPx(resolve.getTopLeft().getHorizontal() + (resolve2 != null ? resolve2.left : 0.0f)), PixelUtil.INSTANCE.dpToPx(resolve.getTopLeft().getVertical() + (resolve2 != null ? resolve2.top : 0.0f)), PixelUtil.INSTANCE.dpToPx(resolve.getTopRight().getHorizontal() + (resolve2 != null ? resolve2.right : 0.0f)), PixelUtil.INSTANCE.dpToPx(resolve.getTopRight().getVertical() + (resolve2 != null ? resolve2.top : 0.0f)), PixelUtil.INSTANCE.dpToPx(resolve.getBottomRight().getHorizontal() + (resolve2 != null ? resolve2.right : 0.0f)), PixelUtil.INSTANCE.dpToPx(resolve.getBottomRight().getVertical() + (resolve2 != null ? resolve2.bottom : 0.0f)), PixelUtil.INSTANCE.dpToPx(resolve.getBottomLeft().getHorizontal() + (resolve2 != null ? resolve2.left : 0.0f)), PixelUtil.INSTANCE.dpToPx(resolve.getBottomLeft().getVertical() + (resolve2 != null ? resolve2.bottom : 0.0f))}, Path.Direction.CW);
            }
            if (Build.VERSION.SDK_INT >= 30) {
                outline.setPath(path);
                return;
            } else {
                outline.setConvexPath(path);
                return;
            }
        }
        outline.setRect(getBounds());
    }

    /* compiled from: CompositeBackgroundDrawable.kt */
    @Metadata(d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003Jm\u0010\u0004\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00060\u00052\b\u0010\u0007\u001a\u0004\u0018\u00010\u00062\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00060\t2\b\u0010\n\u001a\u0004\u0018\u00010\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u00062\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00060\t2\b\u0010\u0012\u001a\u0004\u0018\u00010\u0013H\u0002¢\u0006\u0002\u0010\u0014¨\u0006\u0015"}, d2 = {"Lcom/facebook/react/uimanager/drawable/CompositeBackgroundDrawable$Companion;", "", "<init>", "()V", "createLayersArray", "", "Landroid/graphics/drawable/Drawable;", "originalBackground", "outerShadows", "", "cssBackground", "Lcom/facebook/react/uimanager/drawable/CSSBackgroundDrawable;", AppStateModule.APP_STATE_BACKGROUND, "Lcom/facebook/react/uimanager/drawable/BackgroundDrawable;", "border", "Lcom/facebook/react/uimanager/drawable/BorderDrawable;", "feedbackUnderlay", "innerShadows", "outline", "Lcom/facebook/react/uimanager/drawable/OutlineDrawable;", "(Landroid/graphics/drawable/Drawable;Ljava/util/List;Lcom/facebook/react/uimanager/drawable/CSSBackgroundDrawable;Lcom/facebook/react/uimanager/drawable/BackgroundDrawable;Lcom/facebook/react/uimanager/drawable/BorderDrawable;Landroid/graphics/drawable/Drawable;Ljava/util/List;Lcom/facebook/react/uimanager/drawable/OutlineDrawable;)[Landroid/graphics/drawable/Drawable;", "ReactAndroid_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
    /* loaded from: classes.dex */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final Drawable[] createLayersArray(Drawable originalBackground, List<? extends Drawable> outerShadows, CSSBackgroundDrawable cssBackground, BackgroundDrawable background, BorderDrawable border, Drawable feedbackUnderlay, List<? extends Drawable> innerShadows, OutlineDrawable outline) {
            ArrayList arrayList = new ArrayList();
            if (originalBackground != null) {
                arrayList.add(originalBackground);
            }
            arrayList.addAll(CollectionsKt.asReversed(outerShadows));
            if (cssBackground != null) {
                arrayList.add(cssBackground);
            }
            if (background != null) {
                arrayList.add(background);
            }
            if (border != null) {
                arrayList.add(border);
            }
            if (feedbackUnderlay != null) {
                arrayList.add(feedbackUnderlay);
            }
            arrayList.addAll(CollectionsKt.asReversed(innerShadows));
            if (outline != null) {
                arrayList.add(outline);
            }
            return (Drawable[]) arrayList.toArray(new Drawable[0]);
        }
    }
}
