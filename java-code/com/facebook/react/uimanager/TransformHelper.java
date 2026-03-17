package com.facebook.react.uimanager;

import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.common.ReactConstants;

/* loaded from: classes.dex */
public class TransformHelper {
    private static ThreadLocal<double[]> sHelperMatrix = new ThreadLocal<double[]>() { // from class: com.facebook.react.uimanager.TransformHelper.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.lang.ThreadLocal
        public double[] initialValue() {
            return new double[16];
        }
    };

    private static double convertToRadians(ReadableMap readableMap, String str) {
        double d;
        boolean z = true;
        if (readableMap.getType(str) == ReadableType.String) {
            String string = readableMap.getString(str);
            if (string.endsWith("rad")) {
                string = string.substring(0, string.length() - 3);
            } else if (string.endsWith("deg")) {
                string = string.substring(0, string.length() - 3);
                z = false;
            }
            d = Float.parseFloat(string);
        } else {
            d = readableMap.getDouble(str);
        }
        return z ? d : MatrixMathHelper.degreesToRadians(d);
    }

    @Deprecated(forRemoval = true, since = "0.75")
    public static void processTransform(ReadableArray readableArray, double[] dArr) {
        processTransform(readableArray, dArr, 0.0f, 0.0f, null, false);
    }

    @Deprecated(forRemoval = true, since = "0.75")
    public static void processTransform(ReadableArray readableArray, double[] dArr, float f, float f2, ReadableArray readableArray2) {
        processTransform(readableArray, dArr, f, f2, readableArray2, false);
    }

    public static void processTransform(ReadableArray readableArray, double[] dArr, float f, float f2, ReadableArray readableArray2, boolean z) {
        int i;
        int i2;
        int i3;
        double d;
        double d2;
        double d3;
        double d4;
        double[] dArr2 = sHelperMatrix.get();
        MatrixMathHelper.resetIdentityMatrix(dArr);
        float[] translateForTransformOrigin = getTranslateForTransformOrigin(f, f2, readableArray2, z);
        if (translateForTransformOrigin != null) {
            MatrixMathHelper.resetIdentityMatrix(dArr2);
            MatrixMathHelper.applyTranslate3D(dArr2, translateForTransformOrigin[0], translateForTransformOrigin[1], translateForTransformOrigin[2]);
            MatrixMathHelper.multiplyInto(dArr, dArr, dArr2);
        }
        int i4 = 16;
        if (readableArray.size() == 16 && readableArray.getType(0) == ReadableType.Number) {
            MatrixMathHelper.resetIdentityMatrix(dArr2);
            for (int i5 = 0; i5 < readableArray.size(); i5++) {
                dArr2[i5] = readableArray.getDouble(i5);
            }
            MatrixMathHelper.multiplyInto(dArr, dArr, dArr2);
        } else {
            int size = readableArray.size();
            int i6 = 0;
            while (i6 < size) {
                ReadableMap map = readableArray.getMap(i6);
                String nextKey = map.keySetIterator().nextKey();
                MatrixMathHelper.resetIdentityMatrix(dArr2);
                if ("matrix".equals(nextKey)) {
                    ReadableArray array = map.getArray(nextKey);
                    for (int i7 = 0; i7 < i4; i7++) {
                        dArr2[i7] = array.getDouble(i7);
                    }
                } else if ("perspective".equals(nextKey)) {
                    MatrixMathHelper.applyPerspective(dArr2, map.getDouble(nextKey));
                } else if ("rotateX".equals(nextKey)) {
                    MatrixMathHelper.applyRotateX(dArr2, convertToRadians(map, nextKey));
                } else if ("rotateY".equals(nextKey)) {
                    MatrixMathHelper.applyRotateY(dArr2, convertToRadians(map, nextKey));
                } else {
                    if ("rotate".equals(nextKey) || "rotateZ".equals(nextKey)) {
                        i = i6;
                        i2 = i4;
                        i3 = size;
                        MatrixMathHelper.applyRotateZ(dArr2, convertToRadians(map, nextKey));
                    } else if ("scale".equals(nextKey)) {
                        double d5 = map.getDouble(nextKey);
                        MatrixMathHelper.applyScaleX(dArr2, d5);
                        MatrixMathHelper.applyScaleY(dArr2, d5);
                    } else if (ViewProps.SCALE_X.equals(nextKey)) {
                        MatrixMathHelper.applyScaleX(dArr2, map.getDouble(nextKey));
                    } else if (ViewProps.SCALE_Y.equals(nextKey)) {
                        MatrixMathHelper.applyScaleY(dArr2, map.getDouble(nextKey));
                    } else {
                        i3 = size;
                        if ("translate".equals(nextKey)) {
                            ReadableArray array2 = map.getArray(nextKey);
                            if (array2.getType(0) == ReadableType.String && z) {
                                d3 = parseTranslateValue(array2.getString(0), f);
                            } else {
                                d3 = array2.getDouble(0);
                            }
                            if (array2.getType(1) == ReadableType.String && z) {
                                d4 = parseTranslateValue(array2.getString(1), f2);
                            } else {
                                d4 = array2.getDouble(1);
                            }
                            i = i6;
                            i2 = 16;
                            MatrixMathHelper.applyTranslate3D(dArr2, d3, d4, array2.size() > 2 ? array2.getDouble(2) : 0.0d);
                        } else {
                            i = i6;
                            i2 = 16;
                            if (ViewProps.TRANSLATE_X.equals(nextKey)) {
                                if (map.getType(nextKey) == ReadableType.String && z) {
                                    d2 = parseTranslateValue(map.getString(nextKey), f);
                                } else {
                                    d2 = map.getDouble(nextKey);
                                }
                                MatrixMathHelper.applyTranslate2D(dArr2, d2, 0.0d);
                            } else if (ViewProps.TRANSLATE_Y.equals(nextKey)) {
                                if (map.getType(nextKey) == ReadableType.String && z) {
                                    d = parseTranslateValue(map.getString(nextKey), f2);
                                } else {
                                    d = map.getDouble(nextKey);
                                }
                                MatrixMathHelper.applyTranslate2D(dArr2, 0.0d, d);
                            } else if ("skewX".equals(nextKey)) {
                                MatrixMathHelper.applySkewX(dArr2, convertToRadians(map, nextKey));
                            } else if ("skewY".equals(nextKey)) {
                                MatrixMathHelper.applySkewY(dArr2, convertToRadians(map, nextKey));
                            } else {
                                FLog.w(ReactConstants.TAG, "Unsupported transform type: " + nextKey);
                            }
                        }
                    }
                    MatrixMathHelper.multiplyInto(dArr, dArr, dArr2);
                    i6 = i + 1;
                    size = i3;
                    i4 = i2;
                }
                i = i6;
                i2 = i4;
                i3 = size;
                MatrixMathHelper.multiplyInto(dArr, dArr, dArr2);
                i6 = i + 1;
                size = i3;
                i4 = i2;
            }
        }
        if (translateForTransformOrigin != null) {
            MatrixMathHelper.resetIdentityMatrix(dArr2);
            MatrixMathHelper.applyTranslate3D(dArr2, -translateForTransformOrigin[0], -translateForTransformOrigin[1], -translateForTransformOrigin[2]);
            MatrixMathHelper.multiplyInto(dArr, dArr, dArr2);
        }
    }

    private static double parseTranslateValue(String str, double d) {
        try {
            if (str.endsWith("%")) {
                return (Double.parseDouble(str.substring(0, str.length() - 1)) * d) / 100.0d;
            }
            return Double.parseDouble(str);
        } catch (NumberFormatException unused) {
            FLog.w(ReactConstants.TAG, "Invalid translate value: " + str);
            return 0.0d;
        }
    }

    private static float[] getTranslateForTransformOrigin(float f, float f2, ReadableArray readableArray, boolean z) {
        if (readableArray == null) {
            return null;
        }
        if (f2 == 0.0f && f == 0.0f) {
            return null;
        }
        float f3 = f / 2.0f;
        float f4 = f2 / 2.0f;
        float[] fArr = new float[3];
        fArr[0] = f3;
        fArr[1] = f4;
        fArr[2] = 0.0f;
        int i = 0;
        while (i < readableArray.size() && i < 3) {
            int i2 = AnonymousClass2.$SwitchMap$com$facebook$react$bridge$ReadableType[readableArray.getType(i).ordinal()];
            if (i2 == 1) {
                fArr[i] = (float) readableArray.getDouble(i);
            } else if (i2 == 2 && z) {
                String string = readableArray.getString(i);
                if (string.endsWith("%")) {
                    fArr[i] = ((i == 0 ? f : f2) * Float.parseFloat(string.substring(0, string.length() - 1))) / 100.0f;
                }
            }
            i++;
        }
        return new float[]{(-f3) + fArr[0], (-f4) + fArr[1], fArr[2]};
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.facebook.react.uimanager.TransformHelper$2, reason: invalid class name */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$facebook$react$bridge$ReadableType;

        static {
            int[] iArr = new int[ReadableType.values().length];
            $SwitchMap$com$facebook$react$bridge$ReadableType = iArr;
            try {
                iArr[ReadableType.Number.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$facebook$react$bridge$ReadableType[ReadableType.String.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }
}
