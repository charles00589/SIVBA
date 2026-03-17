package com.facebook.react.views.text;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.text.BoringLayout;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextDirectionHeuristics;
import android.text.TextPaint;
import androidx.core.util.Preconditions;
import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.ReactNoCrashSoftException;
import com.facebook.react.bridge.ReactSoftExceptionLogger;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.common.build.ReactBuildConfig;
import com.facebook.react.common.mapbuffer.MapBuffer;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.ViewProps;
import com.facebook.react.views.text.internal.span.CustomLetterSpacingSpan;
import com.facebook.react.views.text.internal.span.CustomLineHeightSpan;
import com.facebook.react.views.text.internal.span.CustomStyleSpan;
import com.facebook.react.views.text.internal.span.ReactAbsoluteSizeSpan;
import com.facebook.react.views.text.internal.span.ReactBackgroundColorSpan;
import com.facebook.react.views.text.internal.span.ReactForegroundColorSpan;
import com.facebook.react.views.text.internal.span.ReactOpacitySpan;
import com.facebook.react.views.text.internal.span.ReactStrikethroughSpan;
import com.facebook.react.views.text.internal.span.ReactTagSpan;
import com.facebook.react.views.text.internal.span.ReactTextPaintHolderSpan;
import com.facebook.react.views.text.internal.span.ReactUnderlineSpan;
import com.facebook.react.views.text.internal.span.SetSpanOperation;
import com.facebook.react.views.text.internal.span.ShadowStyleSpan;
import com.facebook.react.views.text.internal.span.TextInlineViewPlaceholderSpan;
import com.facebook.yoga.YogaConstants;
import com.facebook.yoga.YogaMeasureMode;
import com.facebook.yoga.YogaMeasureOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: classes.dex */
public class TextLayoutManager {
    public static final short AS_KEY_BASE_ATTRIBUTES = 4;
    public static final short AS_KEY_CACHE_ID = 3;
    public static final short AS_KEY_FRAGMENTS = 2;
    public static final short AS_KEY_HASH = 0;
    public static final short AS_KEY_STRING = 1;
    private static final boolean DEFAULT_ADJUST_FONT_SIZE_TO_FIT = false;
    private static final boolean DEFAULT_INCLUDE_FONT_PADDING = true;
    private static final boolean ENABLE_MEASURE_LOGGING;
    public static final short FR_KEY_HEIGHT = 4;
    public static final short FR_KEY_IS_ATTACHMENT = 2;
    public static final short FR_KEY_REACT_TAG = 1;
    public static final short FR_KEY_STRING = 0;
    public static final short FR_KEY_TEXT_ATTRIBUTES = 5;
    public static final short FR_KEY_WIDTH = 3;
    private static final String INLINE_VIEW_PLACEHOLDER = "0";
    public static final short PA_KEY_ADJUST_FONT_SIZE_TO_FIT = 3;
    public static final short PA_KEY_ELLIPSIZE_MODE = 1;
    public static final short PA_KEY_HYPHENATION_FREQUENCY = 5;
    public static final short PA_KEY_INCLUDE_FONT_PADDING = 4;
    public static final short PA_KEY_MAXIMUM_FONT_SIZE = 7;
    public static final short PA_KEY_MAX_NUMBER_OF_LINES = 0;
    public static final short PA_KEY_MINIMUM_FONT_SIZE = 6;
    public static final short PA_KEY_TEXT_BREAK_STRATEGY = 2;
    private static final String TAG;
    private static final ConcurrentHashMap<Integer, Spannable> sTagToSpannableCache;
    private static final ThreadLocal<TextPaint> sTextPaintInstance;

    static {
        boolean z = ReactBuildConfig.DEBUG;
        ENABLE_MEASURE_LOGGING = false;
        TAG = "TextLayoutManager";
        sTextPaintInstance = new ThreadLocal<TextPaint>() { // from class: com.facebook.react.views.text.TextLayoutManager.1
            /* JADX INFO: Access modifiers changed from: protected */
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.lang.ThreadLocal
            public TextPaint initialValue() {
                return new TextPaint(1);
            }
        };
        sTagToSpannableCache = new ConcurrentHashMap<>();
    }

    public static void setCachedSpannableForTag(int i, Spannable spannable) {
        if (ENABLE_MEASURE_LOGGING) {
            FLog.e(TAG, "Set cached spannable for tag[" + i + "]: " + spannable.toString());
        }
        sTagToSpannableCache.put(Integer.valueOf(i), spannable);
    }

    public static void deleteCachedSpannableForTag(int i) {
        if (ENABLE_MEASURE_LOGGING) {
            FLog.e(TAG, "Delete cached spannable for tag[" + i + "]");
        }
        sTagToSpannableCache.remove(Integer.valueOf(i));
    }

    public static boolean isRTL(MapBuffer mapBuffer) {
        if (!mapBuffer.contains(2)) {
            return false;
        }
        MapBuffer mapBuffer2 = mapBuffer.getMapBuffer(2);
        if (mapBuffer2.getCount() == 0) {
            return false;
        }
        MapBuffer mapBuffer3 = mapBuffer2.getMapBuffer(0).getMapBuffer(5);
        return mapBuffer3.contains(23) && TextAttributeProps.getLayoutDirection(mapBuffer3.getString(23)) == 1;
    }

    private static String getTextAlignmentAttr(MapBuffer mapBuffer) {
        if (!mapBuffer.contains(2)) {
            return null;
        }
        MapBuffer mapBuffer2 = mapBuffer.getMapBuffer(2);
        if (mapBuffer2.getCount() != 0) {
            MapBuffer mapBuffer3 = mapBuffer2.getMapBuffer(0).getMapBuffer(5);
            if (mapBuffer3.contains(12)) {
                return mapBuffer3.getString(12);
            }
        }
        return null;
    }

    private static int getTextJustificationMode(String str) {
        if (Build.VERSION.SDK_INT < 26) {
            return -1;
        }
        return (str == null || !str.equals("justified")) ? 0 : 1;
    }

    private static Layout.Alignment getTextAlignment(MapBuffer mapBuffer, Spannable spannable, String str) {
        boolean z = isRTL(mapBuffer) != TextDirectionHeuristics.FIRSTSTRONG_LTR.isRtl(spannable, 0, spannable.length());
        Layout.Alignment alignment = z ? Layout.Alignment.ALIGN_OPPOSITE : Layout.Alignment.ALIGN_NORMAL;
        if (str == null) {
            return alignment;
        }
        if (str.equals("center")) {
            return Layout.Alignment.ALIGN_CENTER;
        }
        return str.equals(ViewProps.RIGHT) ? z ? Layout.Alignment.ALIGN_NORMAL : Layout.Alignment.ALIGN_OPPOSITE : alignment;
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x0023, code lost:
    
        if (r4 != false) goto L6;
     */
    /* JADX WARN: Code restructure failed: missing block: B:3:0x0019, code lost:
    
        if (r4 != false) goto L5;
     */
    /* JADX WARN: Code restructure failed: missing block: B:5:?, code lost:
    
        return 5;
     */
    /* JADX WARN: Code restructure failed: missing block: B:8:?, code lost:
    
        return 3;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static int getTextGravity(MapBuffer mapBuffer, Spannable spannable, int i) {
        Layout.Alignment textAlignment = getTextAlignment(mapBuffer, spannable, getTextAlignmentAttr(mapBuffer));
        boolean isRtl = TextDirectionHeuristics.FIRSTSTRONG_LTR.isRtl(spannable, 0, spannable.length());
        if (textAlignment != Layout.Alignment.ALIGN_NORMAL) {
            if (textAlignment != Layout.Alignment.ALIGN_OPPOSITE) {
                if (textAlignment == Layout.Alignment.ALIGN_CENTER) {
                    return 1;
                }
                return i;
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:57:0x0090, code lost:
    
        r21.add(new com.facebook.react.views.text.internal.span.SetSpanOperation(r6, r8, new com.facebook.react.views.text.internal.span.ReactClickableSpan(r10)));
     */
    /* JADX WARN: Removed duplicated region for block: B:20:0x00a1  */
    /* JADX WARN: Removed duplicated region for block: B:23:0x00b4  */
    /* JADX WARN: Removed duplicated region for block: B:26:0x00cd  */
    /* JADX WARN: Removed duplicated region for block: B:29:0x00e8  */
    /* JADX WARN: Removed duplicated region for block: B:38:0x0137  */
    /* JADX WARN: Removed duplicated region for block: B:41:0x0148  */
    /* JADX WARN: Removed duplicated region for block: B:50:0x018f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private static void buildSpannableFromFragments(Context context, MapBuffer mapBuffer, SpannableStringBuilder spannableStringBuilder, List<SetSpanOperation> list) {
        int count = mapBuffer.getCount();
        int i = 0;
        int i2 = 0;
        while (i2 < count) {
            MapBuffer mapBuffer2 = mapBuffer.getMapBuffer(i2);
            int length = spannableStringBuilder.length();
            TextAttributeProps fromMapBuffer = TextAttributeProps.fromMapBuffer(mapBuffer2.getMapBuffer(5));
            spannableStringBuilder.append((CharSequence) TextTransform.apply(mapBuffer2.getString(i), fromMapBuffer.mTextTransform));
            int length2 = spannableStringBuilder.length();
            int i3 = mapBuffer2.contains(1) ? mapBuffer2.getInt(1) : -1;
            if (mapBuffer2.contains(2) && mapBuffer2.getBoolean(2)) {
                list.add(new SetSpanOperation(spannableStringBuilder.length() - INLINE_VIEW_PLACEHOLDER.length(), spannableStringBuilder.length(), new TextInlineViewPlaceholderSpan(i3, (int) PixelUtil.toPixelFromSP(mapBuffer2.getDouble(3)), (int) PixelUtil.toPixelFromSP(mapBuffer2.getDouble(4)))));
            } else if (length2 >= length) {
                if (fromMapBuffer.mRole != null) {
                    if (fromMapBuffer.mIsColorSet) {
                        list.add(new SetSpanOperation(length, length2, new ReactForegroundColorSpan(fromMapBuffer.mColor)));
                    }
                    if (fromMapBuffer.mIsBackgroundColorSet) {
                        list.add(new SetSpanOperation(length, length2, new ReactBackgroundColorSpan(fromMapBuffer.mBackgroundColor)));
                    }
                    if (!Float.isNaN(fromMapBuffer.getOpacity())) {
                        list.add(new SetSpanOperation(length, length2, new ReactOpacitySpan(fromMapBuffer.getOpacity())));
                    }
                    if (!Float.isNaN(fromMapBuffer.getLetterSpacing())) {
                        list.add(new SetSpanOperation(length, length2, new CustomLetterSpacingSpan(fromMapBuffer.getLetterSpacing())));
                    }
                    list.add(new SetSpanOperation(length, length2, new ReactAbsoluteSizeSpan(fromMapBuffer.mFontSize)));
                    if (fromMapBuffer.mFontStyle == -1 || fromMapBuffer.mFontWeight != -1 || fromMapBuffer.mFontFamily != null) {
                        list.add(new SetSpanOperation(length, length2, new CustomStyleSpan(fromMapBuffer.mFontStyle, fromMapBuffer.mFontWeight, fromMapBuffer.mFontFeatureSettings, fromMapBuffer.mFontFamily, context.getAssets())));
                    }
                    if (fromMapBuffer.mIsUnderlineTextDecorationSet) {
                        list.add(new SetSpanOperation(length, length2, new ReactUnderlineSpan()));
                    }
                    if (fromMapBuffer.mIsLineThroughTextDecorationSet) {
                        list.add(new SetSpanOperation(length, length2, new ReactStrikethroughSpan()));
                    }
                    if ((fromMapBuffer.mTextShadowOffsetDx == 0.0f || fromMapBuffer.mTextShadowOffsetDy != 0.0f || fromMapBuffer.mTextShadowRadius != 0.0f) && Color.alpha(fromMapBuffer.mTextShadowColor) != 0) {
                        list.add(new SetSpanOperation(length, length2, new ShadowStyleSpan(fromMapBuffer.mTextShadowOffsetDx, fromMapBuffer.mTextShadowOffsetDy, fromMapBuffer.mTextShadowRadius, fromMapBuffer.mTextShadowColor)));
                    }
                    if (!Float.isNaN(fromMapBuffer.getEffectiveLineHeight())) {
                        list.add(new SetSpanOperation(length, length2, new CustomLineHeightSpan(fromMapBuffer.getEffectiveLineHeight())));
                    }
                    list.add(new SetSpanOperation(length, length2, new ReactTagSpan(i3)));
                } else {
                    if (fromMapBuffer.mIsColorSet) {
                    }
                    if (fromMapBuffer.mIsBackgroundColorSet) {
                    }
                    if (!Float.isNaN(fromMapBuffer.getOpacity())) {
                    }
                    if (!Float.isNaN(fromMapBuffer.getLetterSpacing())) {
                    }
                    list.add(new SetSpanOperation(length, length2, new ReactAbsoluteSizeSpan(fromMapBuffer.mFontSize)));
                    if (fromMapBuffer.mFontStyle == -1) {
                    }
                    list.add(new SetSpanOperation(length, length2, new CustomStyleSpan(fromMapBuffer.mFontStyle, fromMapBuffer.mFontWeight, fromMapBuffer.mFontFeatureSettings, fromMapBuffer.mFontFamily, context.getAssets())));
                    if (fromMapBuffer.mIsUnderlineTextDecorationSet) {
                    }
                    if (fromMapBuffer.mIsLineThroughTextDecorationSet) {
                    }
                    if (fromMapBuffer.mTextShadowOffsetDx == 0.0f) {
                    }
                    list.add(new SetSpanOperation(length, length2, new ShadowStyleSpan(fromMapBuffer.mTextShadowOffsetDx, fromMapBuffer.mTextShadowOffsetDy, fromMapBuffer.mTextShadowRadius, fromMapBuffer.mTextShadowColor)));
                    if (!Float.isNaN(fromMapBuffer.getEffectiveLineHeight())) {
                    }
                    list.add(new SetSpanOperation(length, length2, new ReactTagSpan(i3)));
                }
            }
            i2++;
            i = 0;
        }
    }

    public static Spannable getOrCreateSpannableForText(Context context, MapBuffer mapBuffer, ReactTextViewManagerCallback reactTextViewManagerCallback) {
        if (mapBuffer.contains(3)) {
            return sTagToSpannableCache.get(Integer.valueOf(mapBuffer.getInt(3)));
        }
        return createSpannableFromAttributedString(context, mapBuffer, reactTextViewManagerCallback);
    }

    private static Spannable createSpannableFromAttributedString(Context context, MapBuffer mapBuffer, ReactTextViewManagerCallback reactTextViewManagerCallback) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        ArrayList arrayList = new ArrayList();
        buildSpannableFromFragments(context, mapBuffer.getMapBuffer(2), spannableStringBuilder, arrayList);
        for (int i = 0; i < arrayList.size(); i++) {
            ((SetSpanOperation) arrayList.get((arrayList.size() - i) - 1)).execute(spannableStringBuilder, i);
        }
        if (reactTextViewManagerCallback != null) {
            reactTextViewManagerCallback.onPostProcessSpannable(spannableStringBuilder);
        }
        return spannableStringBuilder;
    }

    private static Layout createLayout(Spannable spannable, BoringLayout.Metrics metrics, float f, YogaMeasureMode yogaMeasureMode, boolean z, int i, int i2, Layout.Alignment alignment, int i3, TextPaint textPaint) {
        int i4;
        int length = spannable.length();
        boolean z2 = yogaMeasureMode == YogaMeasureMode.UNDEFINED || f < 0.0f;
        float desiredWidth = metrics == null ? Layout.getDesiredWidth(spannable, textPaint) : Float.NaN;
        boolean isRtl = TextDirectionHeuristics.FIRSTSTRONG_LTR.isRtl(spannable, 0, length);
        if (metrics == null && (z2 || (!YogaConstants.isUndefined(desiredWidth) && desiredWidth <= f))) {
            if (yogaMeasureMode == YogaMeasureMode.EXACTLY) {
                desiredWidth = f;
            }
            StaticLayout.Builder textDirection = StaticLayout.Builder.obtain(spannable, 0, length, textPaint, (int) Math.ceil(desiredWidth)).setAlignment(alignment).setLineSpacing(0.0f, 1.0f).setIncludePad(z).setBreakStrategy(i).setHyphenationFrequency(i2).setTextDirection(isRtl ? TextDirectionHeuristics.RTL : TextDirectionHeuristics.LTR);
            if (Build.VERSION.SDK_INT >= 28) {
                textDirection.setUseLineSpacingFromFallbacks(true);
            }
            return textDirection.build();
        }
        if (metrics != null && (z2 || metrics.width <= f)) {
            int i5 = metrics.width;
            if (yogaMeasureMode == YogaMeasureMode.EXACTLY) {
                i5 = (int) Math.ceil(f);
            }
            if (metrics.width < 0) {
                ReactSoftExceptionLogger.logSoftException(TAG, new ReactNoCrashSoftException("Text width is invalid: " + metrics.width));
                i4 = 0;
            } else {
                i4 = i5;
            }
            return BoringLayout.make(spannable, textPaint, i4, alignment, 1.0f, 0.0f, metrics, z);
        }
        StaticLayout.Builder textDirection2 = StaticLayout.Builder.obtain(spannable, 0, length, textPaint, (int) Math.ceil(f)).setAlignment(alignment).setLineSpacing(0.0f, 1.0f).setIncludePad(z).setBreakStrategy(i).setHyphenationFrequency(i2).setTextDirection(isRtl ? TextDirectionHeuristics.RTL : TextDirectionHeuristics.LTR);
        if (Build.VERSION.SDK_INT >= 26) {
            textDirection2.setJustificationMode(i3);
        }
        if (Build.VERSION.SDK_INT >= 28) {
            textDirection2.setUseLineSpacingFromFallbacks(true);
        }
        return textDirection2.build();
    }

    private static void updateTextPaint(TextPaint textPaint, TextAttributeProps textAttributeProps, Context context) {
        textPaint.reset();
        textPaint.setAntiAlias(true);
        if (textAttributeProps.getEffectiveFontSize() != -1) {
            textPaint.setTextSize(textAttributeProps.getEffectiveFontSize());
        }
        if (textAttributeProps.getFontStyle() != -1 || textAttributeProps.getFontWeight() != -1 || textAttributeProps.getFontFamily() != null) {
            Typeface applyStyles = ReactTypefaceUtils.applyStyles(null, textAttributeProps.getFontStyle(), textAttributeProps.getFontWeight(), textAttributeProps.getFontFamily(), context.getAssets());
            textPaint.setTypeface(applyStyles);
            if (textAttributeProps.getFontStyle() == -1 || textAttributeProps.getFontStyle() == applyStyles.getStyle()) {
                return;
            }
            int fontStyle = textAttributeProps.getFontStyle() & (~applyStyles.getStyle());
            textPaint.setFakeBoldText((fontStyle & 1) != 0);
            textPaint.setTextSkewX((fontStyle & 2) != 0 ? -0.25f : 0.0f);
            return;
        }
        textPaint.setTypeface(null);
    }

    private static Layout createLayout(Context context, MapBuffer mapBuffer, MapBuffer mapBuffer2, float f, float f2, ReactTextViewManagerCallback reactTextViewManagerCallback) {
        TextPaint textPaint;
        Spannable orCreateSpannableForText = getOrCreateSpannableForText(context, mapBuffer, reactTextViewManagerCallback);
        if (mapBuffer.contains(3)) {
            textPaint = ((ReactTextPaintHolderSpan[]) orCreateSpannableForText.getSpans(0, 0, ReactTextPaintHolderSpan.class))[0].getTextPaint();
        } else {
            TextAttributeProps fromMapBuffer = TextAttributeProps.fromMapBuffer(mapBuffer.getMapBuffer(4));
            TextPaint textPaint2 = (TextPaint) Preconditions.checkNotNull(sTextPaintInstance.get());
            updateTextPaint(textPaint2, fromMapBuffer, context);
            textPaint = textPaint2;
        }
        BoringLayout.Metrics isBoring = BoringLayout.isBoring(orCreateSpannableForText, textPaint);
        int textBreakStrategy = TextAttributeProps.getTextBreakStrategy(mapBuffer2.getString(2));
        boolean z = mapBuffer2.contains(4) ? mapBuffer2.getBoolean(4) : true;
        int hyphenationFrequency = TextAttributeProps.getHyphenationFrequency(mapBuffer2.getString(5));
        boolean z2 = mapBuffer2.contains(3) ? mapBuffer2.getBoolean(3) : false;
        int i = mapBuffer2.contains(0) ? mapBuffer2.getInt(0) : -1;
        String textAlignmentAttr = getTextAlignmentAttr(mapBuffer);
        Layout.Alignment textAlignment = getTextAlignment(mapBuffer, orCreateSpannableForText, textAlignmentAttr);
        int textJustificationMode = getTextJustificationMode(textAlignmentAttr);
        if (z2) {
            adjustSpannableFontToFit(orCreateSpannableForText, f, YogaMeasureMode.EXACTLY, f2, YogaMeasureMode.UNDEFINED, mapBuffer2.contains(6) ? mapBuffer2.getDouble(6) : Double.NaN, i, z, textBreakStrategy, hyphenationFrequency, textAlignment, textJustificationMode, textPaint);
        }
        return createLayout(orCreateSpannableForText, isBoring, f, YogaMeasureMode.EXACTLY, z, textBreakStrategy, hyphenationFrequency, textAlignment, textJustificationMode, textPaint);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void adjustSpannableFontToFit(Spannable spannable, float f, YogaMeasureMode yogaMeasureMode, float f2, YogaMeasureMode yogaMeasureMode2, double d, int i, boolean z, int i2, int i3, Layout.Alignment alignment, int i4, TextPaint textPaint) {
        BoringLayout.Metrics isBoring = BoringLayout.isBoring(spannable, textPaint);
        Layout createLayout = createLayout(spannable, isBoring, f, yogaMeasureMode, z, i2, i3, alignment, i4, textPaint);
        int pixelFromDIP = (int) (Double.isNaN(d) ? PixelUtil.toPixelFromDIP(4.0f) : d);
        int i5 = 0;
        int i6 = pixelFromDIP;
        for (ReactAbsoluteSizeSpan reactAbsoluteSizeSpan : (ReactAbsoluteSizeSpan[]) spannable.getSpans(0, spannable.length(), ReactAbsoluteSizeSpan.class)) {
            i6 = Math.max(i6, reactAbsoluteSizeSpan.getSize());
        }
        int i7 = i6;
        while (i7 > pixelFromDIP) {
            if ((i == -1 || i == 0 || createLayout.getLineCount() <= i) && ((yogaMeasureMode2 == YogaMeasureMode.UNDEFINED || createLayout.getHeight() <= f2) && (spannable.length() != 1 || createLayout.getLineWidth(i5) <= f))) {
                return;
            }
            int max = i7 - Math.max(1, (int) PixelUtil.toPixelFromDIP(1.0f));
            float f3 = max / i6;
            float f4 = pixelFromDIP;
            textPaint.setTextSize(Math.max(textPaint.getTextSize() * f3, f4));
            ReactAbsoluteSizeSpan[] reactAbsoluteSizeSpanArr = (ReactAbsoluteSizeSpan[]) spannable.getSpans(i5, spannable.length(), ReactAbsoluteSizeSpan.class);
            int length = reactAbsoluteSizeSpanArr.length;
            int i8 = i5;
            while (i8 < length) {
                ReactAbsoluteSizeSpan reactAbsoluteSizeSpan2 = reactAbsoluteSizeSpanArr[i8];
                spannable.setSpan(new ReactAbsoluteSizeSpan((int) Math.max(reactAbsoluteSizeSpan2.getSize() * f3, f4)), spannable.getSpanStart(reactAbsoluteSizeSpan2), spannable.getSpanEnd(reactAbsoluteSizeSpan2), spannable.getSpanFlags(reactAbsoluteSizeSpan2));
                spannable.removeSpan(reactAbsoluteSizeSpan2);
                i8++;
                f3 = f3;
                reactAbsoluteSizeSpanArr = reactAbsoluteSizeSpanArr;
            }
            if (isBoring != null) {
                isBoring = BoringLayout.isBoring(spannable, textPaint);
            }
            createLayout = createLayout(spannable, isBoring, f, yogaMeasureMode, z, i2, i3, alignment, i4, textPaint);
            i7 = max;
            i6 = i6;
            i5 = 0;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:28:0x0087, code lost:
    
        if (r5 > r21) goto L41;
     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x00a7, code lost:
    
        if (r3 > r23) goto L51;
     */
    /* JADX WARN: Removed duplicated region for block: B:31:0x0091  */
    /* JADX WARN: Removed duplicated region for block: B:34:0x009b  */
    /* JADX WARN: Removed duplicated region for block: B:41:0x00b3  */
    /* JADX WARN: Removed duplicated region for block: B:63:0x0132  */
    /* JADX WARN: Removed duplicated region for block: B:67:0x0135  */
    /* JADX WARN: Removed duplicated region for block: B:87:0x018c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static long measureText(Context context, MapBuffer mapBuffer, MapBuffer mapBuffer2, float f, YogaMeasureMode yogaMeasureMode, float f2, YogaMeasureMode yogaMeasureMode2, ReactTextViewManagerCallback reactTextViewManagerCallback, float[] fArr) {
        int lineCount;
        float f3;
        float f4;
        int i;
        char c;
        float secondaryHorizontal;
        float f5;
        boolean z;
        char c2;
        float lineWidth;
        Layout createLayout = createLayout(context, mapBuffer, mapBuffer2, f, f2, reactTextViewManagerCallback);
        Spannable spannable = (Spannable) createLayout.getText();
        if (spannable == null) {
            return 0L;
        }
        int i2 = 0;
        int i3 = -1;
        int i4 = mapBuffer2.contains(0) ? mapBuffer2.getInt(0) : -1;
        if (i4 == -1 || i4 == 0) {
            lineCount = createLayout.getLineCount();
        } else {
            lineCount = Math.min(i4, createLayout.getLineCount());
        }
        char c3 = '\n';
        boolean z2 = true;
        if (yogaMeasureMode != YogaMeasureMode.EXACTLY) {
            f3 = 0.0f;
            int i5 = 0;
            while (true) {
                if (i5 < lineCount) {
                    boolean z3 = spannable.length() > 0 && spannable.charAt(createLayout.getLineEnd(i5) - 1) == '\n';
                    if (!z3 && i5 + 1 < createLayout.getLineCount()) {
                        f3 = f;
                        break;
                    }
                    float lineMax = z3 ? createLayout.getLineMax(i5) : createLayout.getLineWidth(i5);
                    if (lineMax > f3) {
                        f3 = lineMax;
                    }
                    i5++;
                } else {
                    break;
                }
            }
            if (yogaMeasureMode == YogaMeasureMode.AT_MOST) {
            }
            if (Build.VERSION.SDK_INT > 29) {
                f3 = (float) Math.ceil(f3);
            }
            if (yogaMeasureMode2 != YogaMeasureMode.EXACTLY) {
                f4 = createLayout.getLineBottom(lineCount - 1);
                if (yogaMeasureMode2 == YogaMeasureMode.AT_MOST) {
                }
                i = 0;
                int i6 = 0;
                while (i < spannable.length()) {
                    int nextSpanTransition = spannable.nextSpanTransition(i, spannable.length(), TextInlineViewPlaceholderSpan.class);
                    TextInlineViewPlaceholderSpan[] textInlineViewPlaceholderSpanArr = (TextInlineViewPlaceholderSpan[]) spannable.getSpans(i, nextSpanTransition, TextInlineViewPlaceholderSpan.class);
                    int length = textInlineViewPlaceholderSpanArr.length;
                    int i7 = i2;
                    while (i7 < length) {
                        TextInlineViewPlaceholderSpan textInlineViewPlaceholderSpan = textInlineViewPlaceholderSpanArr[i7];
                        int spanStart = spannable.getSpanStart(textInlineViewPlaceholderSpan);
                        int lineForOffset = createLayout.getLineForOffset(spanStart);
                        if (createLayout.getEllipsisCount(lineForOffset) <= 0 || spanStart < createLayout.getLineStart(lineForOffset) + createLayout.getEllipsisStart(lineForOffset) || spanStart >= createLayout.getLineEnd(lineForOffset)) {
                            float width = textInlineViewPlaceholderSpan.getWidth();
                            float height = textInlineViewPlaceholderSpan.getHeight();
                            boolean isRtlCharAt = createLayout.isRtlCharAt(spanStart);
                            boolean z4 = createLayout.getParagraphDirection(lineForOffset) == i3;
                            if (spanStart == spannable.length() - 1) {
                                if (spannable.length() > 0) {
                                    c2 = '\n';
                                    if (spannable.charAt(createLayout.getLineEnd(lineForOffset) - 1) == '\n') {
                                        lineWidth = createLayout.getLineMax(lineForOffset);
                                        f5 = !z4 ? f3 - lineWidth : createLayout.getLineRight(lineForOffset) - width;
                                        c = c2;
                                    }
                                } else {
                                    c2 = '\n';
                                }
                                lineWidth = createLayout.getLineWidth(lineForOffset);
                                if (!z4) {
                                }
                                c = c2;
                            } else {
                                c = '\n';
                                if (z4 == isRtlCharAt) {
                                    secondaryHorizontal = createLayout.getPrimaryHorizontal(spanStart);
                                } else {
                                    secondaryHorizontal = createLayout.getSecondaryHorizontal(spanStart);
                                }
                                float lineRight = (!z4 || isRtlCharAt) ? secondaryHorizontal : f3 - (createLayout.getLineRight(lineForOffset) - secondaryHorizontal);
                                f5 = isRtlCharAt ? lineRight - width : lineRight;
                            }
                            int i8 = i6 * 2;
                            fArr[i8] = PixelUtil.toDIPFromPixel(createLayout.getLineBaseline(lineForOffset) - height);
                            z = true;
                            fArr[i8 + 1] = PixelUtil.toDIPFromPixel(f5);
                            i6++;
                        } else {
                            c = c3;
                            z = z2;
                        }
                        i7++;
                        z2 = z;
                        i2 = 0;
                        c3 = c;
                        i3 = -1;
                    }
                    i = nextSpanTransition;
                }
                float dIPFromPixel = PixelUtil.toDIPFromPixel(f3);
                float dIPFromPixel2 = PixelUtil.toDIPFromPixel(f4);
                if (ENABLE_MEASURE_LOGGING) {
                    FLog.e(TAG, "TextMeasure call ('" + ((Object) spannable) + "'): w: " + f3 + " px - h: " + f4 + " px - w : " + dIPFromPixel + " sp - h: " + dIPFromPixel2 + " sp");
                }
                return YogaMeasureOutput.make(dIPFromPixel, dIPFromPixel2);
            }
            f4 = f2;
            i = 0;
            int i62 = 0;
            while (i < spannable.length()) {
            }
            float dIPFromPixel3 = PixelUtil.toDIPFromPixel(f3);
            float dIPFromPixel22 = PixelUtil.toDIPFromPixel(f4);
            if (ENABLE_MEASURE_LOGGING) {
            }
            return YogaMeasureOutput.make(dIPFromPixel3, dIPFromPixel22);
        }
        f3 = f;
        if (Build.VERSION.SDK_INT > 29) {
        }
        if (yogaMeasureMode2 != YogaMeasureMode.EXACTLY) {
        }
        f4 = f2;
        i = 0;
        int i622 = 0;
        while (i < spannable.length()) {
        }
        float dIPFromPixel32 = PixelUtil.toDIPFromPixel(f3);
        float dIPFromPixel222 = PixelUtil.toDIPFromPixel(f4);
        if (ENABLE_MEASURE_LOGGING) {
        }
        return YogaMeasureOutput.make(dIPFromPixel32, dIPFromPixel222);
    }

    public static WritableArray measureLines(Context context, MapBuffer mapBuffer, MapBuffer mapBuffer2, float f, float f2) {
        Layout createLayout = createLayout(context, mapBuffer, mapBuffer2, f, f2, null);
        return FontMetricsUtil.getFontMetrics(createLayout.getText(), createLayout, (TextPaint) Preconditions.checkNotNull(sTextPaintInstance.get()), context);
    }
}
