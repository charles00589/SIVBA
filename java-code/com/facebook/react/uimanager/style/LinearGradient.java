package com.facebook.react.uimanager.style;

import android.content.Context;
import android.graphics.Shader;
import androidx.core.graphics.ColorUtils;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.react.bridge.ColorPropConverter;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.uimanager.FloatUtil;
import com.facebook.react.uimanager.LengthPercentage;
import com.facebook.react.uimanager.LengthPercentageType;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.ViewProps;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Pair;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: LinearGradient.kt */
@Metadata(d1 = {"\u0000t\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0014\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0000\u0018\u00002\u00020\u0001:\u0001*B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tJ\u0016\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0013J \u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0012\u001a\u00020\u00162\u0006\u0010\u0014\u001a\u00020\u0016H\u0002J,\u0010\u0019\u001a\u000e\u0012\u0004\u0012\u00020\u001b\u0012\u0004\u0012\u00020\u001b0\u001a2\u0006\u0010\u001c\u001a\u00020\u00162\u0006\u0010\u0014\u001a\u00020\u00132\u0006\u0010\u0012\u001a\u00020\u0013H\u0002J3\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u001f0\u001e2\u0016\u0010\f\u001a\u0012\u0012\u0004\u0012\u00020\u000e0\rj\b\u0012\u0004\u0012\u00020\u000e`\u000f2\u0006\u0010 \u001a\u00020\u0013H\u0002¢\u0006\u0002\u0010!J!\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u001f0#2\f\u0010$\u001a\b\u0012\u0004\u0012\u00020\u001f0\u001eH\u0002¢\u0006\u0002\u0010%J!\u0010&\u001a\u0004\u0018\u00010\u00132\b\u0010'\u001a\u0004\u0018\u00010(2\u0006\u0010 \u001a\u00020\u0013H\u0002¢\u0006\u0002\u0010)R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004¢\u0006\u0002\n\u0000R\u001e\u0010\f\u001a\u0012\u0012\u0004\u0012\u00020\u000e0\rj\b\u0012\u0004\u0012\u00020\u000e`\u000fX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006+"}, d2 = {"Lcom/facebook/react/uimanager/style/LinearGradient;", "", "directionMap", "Lcom/facebook/react/bridge/ReadableMap;", "colorStopsArray", "Lcom/facebook/react/bridge/ReadableArray;", "context", "Landroid/content/Context;", "<init>", "(Lcom/facebook/react/bridge/ReadableMap;Lcom/facebook/react/bridge/ReadableArray;Landroid/content/Context;)V", "direction", "Lcom/facebook/react/uimanager/style/LinearGradient$Direction;", "colorStops", "Ljava/util/ArrayList;", "Lcom/facebook/react/uimanager/style/ColorStop;", "Lkotlin/collections/ArrayList;", "getShader", "Landroid/graphics/Shader;", "width", "", "height", "getAngleForKeyword", "", "keyword", "Lcom/facebook/react/uimanager/style/LinearGradient$Direction$Keywords;", "endPointsFromAngle", "Lkotlin/Pair;", "", "angle", "getFixedColorStops", "", "Lcom/facebook/react/uimanager/style/ProcessedColorStop;", "gradientLineLength", "(Ljava/util/ArrayList;F)[Lcom/facebook/react/uimanager/style/ProcessedColorStop;", "processColorTransitionHints", "", "originalStops", "([Lcom/facebook/react/uimanager/style/ProcessedColorStop;)Ljava/util/List;", "resolveColorStopPosition", ViewProps.POSITION, "Lcom/facebook/react/uimanager/LengthPercentage;", "(Lcom/facebook/react/uimanager/LengthPercentage;F)Ljava/lang/Float;", "Direction", "ReactAndroid_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes.dex */
public final class LinearGradient {
    private final ArrayList<ColorStop> colorStops;
    private final ReadableArray colorStopsArray;
    private final Context context;
    private final Direction direction;

    /* compiled from: LinearGradient.kt */
    @Metadata(k = 3, mv = {2, 0, 0}, xi = 48)
    /* loaded from: classes.dex */
    public /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;
        public static final /* synthetic */ int[] $EnumSwitchMapping$1;

        static {
            int[] iArr = new int[Direction.Keywords.values().length];
            try {
                iArr[Direction.Keywords.TO_TOP_RIGHT.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                iArr[Direction.Keywords.TO_BOTTOM_RIGHT.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                iArr[Direction.Keywords.TO_TOP_LEFT.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                iArr[Direction.Keywords.TO_BOTTOM_LEFT.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            $EnumSwitchMapping$0 = iArr;
            int[] iArr2 = new int[LengthPercentageType.values().length];
            try {
                iArr2[LengthPercentageType.POINT.ordinal()] = 1;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                iArr2[LengthPercentageType.PERCENT.ordinal()] = 2;
            } catch (NoSuchFieldError unused6) {
            }
            $EnumSwitchMapping$1 = iArr2;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:30:0x0046. Please report as an issue. */
    public LinearGradient(ReadableMap directionMap, ReadableArray colorStopsArray, Context context) {
        Direction.Keywords keywords;
        Direction.Keyword keyword;
        Intrinsics.checkNotNullParameter(directionMap, "directionMap");
        Intrinsics.checkNotNullParameter(colorStopsArray, "colorStopsArray");
        Intrinsics.checkNotNullParameter(context, "context");
        this.colorStopsArray = colorStopsArray;
        this.context = context;
        String string = directionMap.getString("type");
        if (!Intrinsics.areEqual(string, "angle")) {
            if (!Intrinsics.areEqual(string, "keyword")) {
                throw new IllegalArgumentException("Invalid direction type: " + string);
            }
            String string2 = directionMap.getString("value");
            if (string2 != null) {
                switch (string2.hashCode()) {
                    case -1849920841:
                        if (string2.equals("to bottom left")) {
                            keywords = Direction.Keywords.TO_BOTTOM_LEFT;
                            keyword = new Direction.Keyword(keywords);
                            break;
                        }
                        break;
                    case -1507310228:
                        if (string2.equals("to bottom right")) {
                            keywords = Direction.Keywords.TO_BOTTOM_RIGHT;
                            keyword = new Direction.Keyword(keywords);
                            break;
                        }
                        break;
                    case -1359525897:
                        if (string2.equals("to top left")) {
                            keywords = Direction.Keywords.TO_TOP_LEFT;
                            keyword = new Direction.Keyword(keywords);
                            break;
                        }
                        break;
                    case 810031148:
                        if (string2.equals("to top right")) {
                            keywords = Direction.Keywords.TO_TOP_RIGHT;
                            keyword = new Direction.Keyword(keywords);
                            break;
                        }
                        break;
                }
            }
            throw new IllegalArgumentException("Invalid linear gradient direction keyword: " + directionMap.getString("value"));
        }
        keyword = new Direction.Angle(directionMap.getDouble("value"));
        this.direction = keyword;
        ArrayList<ColorStop> arrayList = new ArrayList<>(this.colorStopsArray.size());
        int size = this.colorStopsArray.size();
        for (int i = 0; i < size; i++) {
            ReadableMap map = this.colorStopsArray.getMap(i);
            if (map != null) {
                arrayList.add(new ColorStop((!map.hasKey(ViewProps.COLOR) || map.isNull(ViewProps.COLOR)) ? null : map.getType(ViewProps.COLOR) == ReadableType.Map ? ColorPropConverter.getColor(map.getMap(ViewProps.COLOR), this.context) : Integer.valueOf(map.getInt(ViewProps.COLOR)), LengthPercentage.INSTANCE.setFromDynamic(map.getDynamic(ViewProps.POSITION))));
            }
        }
        this.colorStops = arrayList;
    }

    /* compiled from: LinearGradient.kt */
    @Metadata(d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b2\u0018\u00002\u00020\u0001:\u0003\u0004\u0005\u0006B\t\b\u0004¢\u0006\u0004\b\u0002\u0010\u0003\u0082\u0001\u0002\u0007\b¨\u0006\t"}, d2 = {"Lcom/facebook/react/uimanager/style/LinearGradient$Direction;", "", "<init>", "()V", "Angle", "Keywords", "Keyword", "Lcom/facebook/react/uimanager/style/LinearGradient$Direction$Angle;", "Lcom/facebook/react/uimanager/style/LinearGradient$Direction$Keyword;", "ReactAndroid_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
    /* loaded from: classes.dex */
    private static abstract class Direction {
        public /* synthetic */ Direction(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        /* compiled from: LinearGradient.kt */
        @Metadata(d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\b\u001a\u00020\u0003HÆ\u0003J\u0013\u0010\t\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÆ\u0001J\u0013\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\rHÖ\u0003J\t\u0010\u000e\u001a\u00020\u000fHÖ\u0001J\t\u0010\u0010\u001a\u00020\u0011HÖ\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u0012"}, d2 = {"Lcom/facebook/react/uimanager/style/LinearGradient$Direction$Angle;", "Lcom/facebook/react/uimanager/style/LinearGradient$Direction;", "value", "", "<init>", "(D)V", "getValue", "()D", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "", "ReactAndroid_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
        /* loaded from: classes.dex */
        public static final /* data */ class Angle extends Direction {
            private final double value;

            public static /* synthetic */ Angle copy$default(Angle angle, double d, int i, Object obj) {
                if ((i & 1) != 0) {
                    d = angle.value;
                }
                return angle.copy(d);
            }

            /* renamed from: component1, reason: from getter */
            public final double getValue() {
                return this.value;
            }

            public final Angle copy(double value) {
                return new Angle(value);
            }

            public boolean equals(Object other) {
                if (this == other) {
                    return true;
                }
                return (other instanceof Angle) && Double.compare(this.value, ((Angle) other).value) == 0;
            }

            public int hashCode() {
                return Double.hashCode(this.value);
            }

            public String toString() {
                return "Angle(value=" + this.value + ")";
            }

            public Angle(double d) {
                super(null);
                this.value = d;
            }

            public final double getValue() {
                return this.value;
            }
        }

        private Direction() {
        }

        /* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
        /* JADX WARN: Unknown enum class pattern. Please report as an issue! */
        /* compiled from: LinearGradient.kt */
        @Metadata(d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0007\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007¨\u0006\b"}, d2 = {"Lcom/facebook/react/uimanager/style/LinearGradient$Direction$Keywords;", "", "<init>", "(Ljava/lang/String;I)V", "TO_TOP_RIGHT", "TO_BOTTOM_RIGHT", "TO_TOP_LEFT", "TO_BOTTOM_LEFT", "ReactAndroid_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
        /* loaded from: classes.dex */
        public static final class Keywords {
            private static final /* synthetic */ EnumEntries $ENTRIES;
            private static final /* synthetic */ Keywords[] $VALUES;
            public static final Keywords TO_TOP_RIGHT = new Keywords("TO_TOP_RIGHT", 0);
            public static final Keywords TO_BOTTOM_RIGHT = new Keywords("TO_BOTTOM_RIGHT", 1);
            public static final Keywords TO_TOP_LEFT = new Keywords("TO_TOP_LEFT", 2);
            public static final Keywords TO_BOTTOM_LEFT = new Keywords("TO_BOTTOM_LEFT", 3);

            private static final /* synthetic */ Keywords[] $values() {
                return new Keywords[]{TO_TOP_RIGHT, TO_BOTTOM_RIGHT, TO_TOP_LEFT, TO_BOTTOM_LEFT};
            }

            public static EnumEntries<Keywords> getEntries() {
                return $ENTRIES;
            }

            private Keywords(String str, int i) {
            }

            static {
                Keywords[] $values = $values();
                $VALUES = $values;
                $ENTRIES = EnumEntriesKt.enumEntries($values);
            }

            public static Keywords valueOf(String str) {
                return (Keywords) Enum.valueOf(Keywords.class, str);
            }

            public static Keywords[] values() {
                return (Keywords[]) $VALUES.clone();
            }
        }

        /* compiled from: LinearGradient.kt */
        @Metadata(d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\b\u001a\u00020\u0003HÆ\u0003J\u0013\u0010\t\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÆ\u0001J\u0013\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\rHÖ\u0003J\t\u0010\u000e\u001a\u00020\u000fHÖ\u0001J\t\u0010\u0010\u001a\u00020\u0011HÖ\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u0012"}, d2 = {"Lcom/facebook/react/uimanager/style/LinearGradient$Direction$Keyword;", "Lcom/facebook/react/uimanager/style/LinearGradient$Direction;", "value", "Lcom/facebook/react/uimanager/style/LinearGradient$Direction$Keywords;", "<init>", "(Lcom/facebook/react/uimanager/style/LinearGradient$Direction$Keywords;)V", "getValue", "()Lcom/facebook/react/uimanager/style/LinearGradient$Direction$Keywords;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "", "ReactAndroid_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
        /* loaded from: classes.dex */
        public static final /* data */ class Keyword extends Direction {
            private final Keywords value;

            public static /* synthetic */ Keyword copy$default(Keyword keyword, Keywords keywords, int i, Object obj) {
                if ((i & 1) != 0) {
                    keywords = keyword.value;
                }
                return keyword.copy(keywords);
            }

            /* renamed from: component1, reason: from getter */
            public final Keywords getValue() {
                return this.value;
            }

            public final Keyword copy(Keywords value) {
                Intrinsics.checkNotNullParameter(value, "value");
                return new Keyword(value);
            }

            public boolean equals(Object other) {
                if (this == other) {
                    return true;
                }
                return (other instanceof Keyword) && this.value == ((Keyword) other).value;
            }

            public int hashCode() {
                return this.value.hashCode();
            }

            public String toString() {
                return "Keyword(value=" + this.value + ")";
            }

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            public Keyword(Keywords value) {
                super(null);
                Intrinsics.checkNotNullParameter(value, "value");
                this.value = value;
            }

            public final Keywords getValue() {
                return this.value;
            }
        }
    }

    public final Shader getShader(float width, float height) {
        double angleForKeyword;
        Direction direction = this.direction;
        if (direction instanceof Direction.Angle) {
            angleForKeyword = ((Direction.Angle) direction).getValue();
        } else {
            if (!(direction instanceof Direction.Keyword)) {
                throw new NoWhenBranchMatchedException();
            }
            angleForKeyword = getAngleForKeyword(((Direction.Keyword) direction).getValue(), width, height);
        }
        Pair<float[], float[]> endPointsFromAngle = endPointsFromAngle(angleForKeyword, height, width);
        float[] component1 = endPointsFromAngle.component1();
        float[] component2 = endPointsFromAngle.component2();
        float f = component2[0] - component1[0];
        float f2 = component2[1] - component1[1];
        List<ProcessedColorStop> processColorTransitionHints = processColorTransitionHints(getFixedColorStops(this.colorStops, (float) Math.sqrt((f * f) + (f2 * f2))));
        int[] iArr = new int[processColorTransitionHints.size()];
        float[] fArr = new float[processColorTransitionHints.size()];
        int i = 0;
        for (Object obj : processColorTransitionHints) {
            int i2 = i + 1;
            if (i < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            ProcessedColorStop processedColorStop = (ProcessedColorStop) obj;
            Integer color = processedColorStop.getColor();
            if (color != null && processedColorStop.getPosition() != null) {
                iArr[i] = color.intValue();
                fArr[i] = processedColorStop.getPosition().floatValue();
            }
            i = i2;
        }
        return new android.graphics.LinearGradient(component1[0], component1[1], component2[0], component2[1], iArr, fArr, Shader.TileMode.CLAMP);
    }

    private final double getAngleForKeyword(Direction.Keywords keyword, double width, double height) {
        double degrees;
        double d;
        int i;
        int i2 = WhenMappings.$EnumSwitchMapping$0[keyword.ordinal()];
        if (i2 == 1) {
            return 90 - Math.toDegrees(Math.atan(width / height));
        }
        if (i2 != 2) {
            if (i2 == 3) {
                degrees = Math.toDegrees(Math.atan(width / height));
                i = RotationOptions.ROTATE_270;
            } else {
                if (i2 != 4) {
                    throw new NoWhenBranchMatchedException();
                }
                degrees = Math.toDegrees(Math.atan(height / width));
                i = RotationOptions.ROTATE_180;
            }
            d = i;
        } else {
            degrees = Math.toDegrees(Math.atan(width / height));
            d = 90;
        }
        return degrees + d;
    }

    private final Pair<float[], float[]> endPointsFromAngle(double angle, float height, float width) {
        float[] fArr;
        double d = 360;
        double d2 = angle % d;
        if (d2 < 0.0d) {
            d2 += d;
        }
        if (d2 == 0.0d) {
            return new Pair<>(new float[]{0.0f, height}, new float[]{0.0f, 0.0f});
        }
        if (d2 == 90.0d) {
            return new Pair<>(new float[]{0.0f, 0.0f}, new float[]{width, 0.0f});
        }
        if (d2 == 180.0d) {
            return new Pair<>(new float[]{0.0f, 0.0f}, new float[]{0.0f, height});
        }
        if (d2 == 270.0d) {
            return new Pair<>(new float[]{width, 0.0f}, new float[]{0.0f, 0.0f});
        }
        float tan = (float) Math.tan(Math.toRadians(90 - d2));
        float f = (-1) / tan;
        float f2 = 2;
        float f3 = height / f2;
        float f4 = width / f2;
        if (d2 < 90.0d) {
            fArr = new float[]{f4, f3};
        } else if (d2 < 180.0d) {
            fArr = new float[]{f4, -f3};
        } else if (d2 < 270.0d) {
            fArr = new float[]{-f4, -f3};
        } else {
            fArr = new float[]{-f4, f3};
        }
        float f5 = fArr[1] - (fArr[0] * f);
        float f6 = f5 / (tan - f);
        float f7 = (f * f6) + f5;
        return new Pair<>(new float[]{f4 - f6, f3 + f7}, new float[]{f4 + f6, f3 - f7});
    }

    private final ProcessedColorStop[] getFixedColorStops(ArrayList<ColorStop> colorStops, float gradientLineLength) {
        Float position;
        int size = colorStops.size();
        ProcessedColorStop[] processedColorStopArr = new ProcessedColorStop[size];
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            processedColorStopArr[i2] = new ProcessedColorStop(null, null, 3, null);
        }
        Float resolveColorStopPosition = resolveColorStopPosition(colorStops.get(0).getPosition(), gradientLineLength);
        float floatValue = resolveColorStopPosition != null ? resolveColorStopPosition.floatValue() : 0.0f;
        int size2 = colorStops.size();
        int i3 = 0;
        boolean z = false;
        while (i3 < size2) {
            ColorStop colorStop = colorStops.get(i3);
            Intrinsics.checkNotNullExpressionValue(colorStop, "get(...)");
            ColorStop colorStop2 = colorStop;
            Float resolveColorStopPosition2 = resolveColorStopPosition(colorStop2.getPosition(), gradientLineLength);
            if (resolveColorStopPosition2 == null) {
                if (i3 == 0) {
                    resolveColorStopPosition2 = Float.valueOf(0.0f);
                } else {
                    resolveColorStopPosition2 = i3 == colorStops.size() - 1 ? Float.valueOf(1.0f) : null;
                }
            }
            if (resolveColorStopPosition2 != null) {
                Float valueOf = Float.valueOf(Math.max(resolveColorStopPosition2.floatValue(), floatValue));
                processedColorStopArr[i3] = new ProcessedColorStop(colorStop2.getColor(), valueOf);
                floatValue = valueOf.floatValue();
            } else {
                z = true;
            }
            i3++;
        }
        if (z) {
            for (int i4 = 1; i4 < size; i4++) {
                Float position2 = processedColorStopArr[i4].getPosition();
                if (position2 != null) {
                    int i5 = i4 - i;
                    int i6 = i5 - 1;
                    if (i6 > 0 && (position = processedColorStopArr[i].getPosition()) != null) {
                        float floatValue2 = (position2.floatValue() - position.floatValue()) / i5;
                        if (1 <= i6) {
                            int i7 = 1;
                            while (true) {
                                int i8 = i + i7;
                                processedColorStopArr[i8] = new ProcessedColorStop(colorStops.get(i8).getColor(), Float.valueOf(position.floatValue() + (i7 * floatValue2)));
                                if (i7 == i6) {
                                    break;
                                }
                                i7++;
                            }
                        }
                    }
                    i = i4;
                }
            }
        }
        return processedColorStopArr;
    }

    private final List<ProcessedColorStop> processColorTransitionHints(ProcessedColorStop[] originalStops) {
        int i;
        int i2;
        ProcessedColorStop[] processedColorStopArr = originalStops;
        List<ProcessedColorStop> mutableList = ArraysKt.toMutableList(originalStops);
        int i3 = 1;
        int length = processedColorStopArr.length - 1;
        int i4 = 1;
        int i5 = 0;
        while (i4 < length) {
            if (processedColorStopArr[i4].getColor() == null && (i2 = i4 + i5) >= i3) {
                int i6 = i2 - 1;
                Float position = mutableList.get(i6).getPosition();
                int i7 = i2 + 1;
                Float position2 = mutableList.get(i7).getPosition();
                Float position3 = mutableList.get(i2).getPosition();
                if (position != null && position2 != null && position3 != null) {
                    float floatValue = position3.floatValue() - position.floatValue();
                    float floatValue2 = position2.floatValue() - position3.floatValue();
                    float floatValue3 = position2.floatValue() - position.floatValue();
                    Integer color = mutableList.get(i6).getColor();
                    Integer color2 = mutableList.get(i7).getColor();
                    if (FloatUtil.floatsEqual(floatValue, floatValue2)) {
                        mutableList.remove(i2);
                        i5--;
                    } else if (FloatUtil.floatsEqual(floatValue, 0.0f)) {
                        mutableList.get(i2).setColor(color2);
                    } else if (FloatUtil.floatsEqual(floatValue2, 0.0f)) {
                        mutableList.get(i2).setColor(color);
                    } else {
                        ArrayList arrayList = new ArrayList(9);
                        if (floatValue > floatValue2) {
                            int i8 = 0;
                            while (i8 < 7) {
                                arrayList.add(new ProcessedColorStop(null, Float.valueOf(position.floatValue() + (((i8 + 7.0f) / 13.0f) * floatValue))));
                                i8++;
                                length = length;
                            }
                            i = length;
                            arrayList.add(new ProcessedColorStop(null, Float.valueOf(position3.floatValue() + (0.33333334f * floatValue2))));
                            arrayList.add(new ProcessedColorStop(null, Float.valueOf(position3.floatValue() + (floatValue2 * 0.6666667f))));
                        } else {
                            i = length;
                            arrayList.add(new ProcessedColorStop(null, Float.valueOf(position.floatValue() + (0.33333334f * floatValue))));
                            arrayList.add(new ProcessedColorStop(null, Float.valueOf(position.floatValue() + (0.6666667f * floatValue))));
                            for (int i9 = 0; i9 < 7; i9++) {
                                arrayList.add(new ProcessedColorStop(null, Float.valueOf(position3.floatValue() + ((i9 / 13.0f) * floatValue2))));
                            }
                        }
                        double log = Math.log(0.5d) / ((float) Math.log(floatValue / floatValue3));
                        Iterator it = arrayList.iterator();
                        Intrinsics.checkNotNullExpressionValue(it, "iterator(...)");
                        while (it.hasNext()) {
                            Object next = it.next();
                            Intrinsics.checkNotNullExpressionValue(next, "next(...)");
                            ProcessedColorStop processedColorStop = (ProcessedColorStop) next;
                            if (processedColorStop.getPosition() != null) {
                                float pow = (float) Math.pow((processedColorStop.getPosition().floatValue() - position.floatValue()) / floatValue3, log);
                                if (!Float.isInfinite(pow) && !Float.isNaN(pow) && !Float.isNaN(pow) && color != null) {
                                    int intValue = color.intValue();
                                    if (color2 != null) {
                                        processedColorStop.setColor(Integer.valueOf(ColorUtils.blendARGB(intValue, color2.intValue(), pow)));
                                    }
                                }
                            }
                        }
                        mutableList.remove(i2);
                        mutableList.addAll(i2, arrayList);
                        i5 += 8;
                        i4++;
                        processedColorStopArr = originalStops;
                        length = i;
                        i3 = 1;
                    }
                }
            }
            i = length;
            i4++;
            processedColorStopArr = originalStops;
            length = i;
            i3 = 1;
        }
        return mutableList;
    }

    private final Float resolveColorStopPosition(LengthPercentage position, float gradientLineLength) {
        if (position == null) {
            return null;
        }
        int i = WhenMappings.$EnumSwitchMapping$1[position.getType().ordinal()];
        if (i == 1) {
            return Float.valueOf(PixelUtil.toPixelFromDIP(position.resolve(0.0f)) / gradientLineLength);
        }
        if (i != 2) {
            throw new NoWhenBranchMatchedException();
        }
        return Float.valueOf(position.resolve(1.0f));
    }
}
