package com.facebook.react.animated;

import com.facebook.react.bridge.JavaOnlyArray;
import com.facebook.react.bridge.JavaOnlyMap;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.ViewProps;
import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: TransformAnimatedNode.kt */
@Metadata(d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\b\u0000\u0018\u00002\u00020\u0001:\u0003\u0011\u0012\u0013B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u000e\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eJ\b\u0010\u000f\u001a\u00020\u0010H\u0016R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0018\u0010\b\u001a\f\u0012\b\u0012\u00060\nR\u00020\u00000\tX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0014"}, d2 = {"Lcom/facebook/react/animated/TransformAnimatedNode;", "Lcom/facebook/react/animated/AnimatedNode;", "config", "Lcom/facebook/react/bridge/ReadableMap;", "nativeAnimatedNodesManager", "Lcom/facebook/react/animated/NativeAnimatedNodesManager;", "<init>", "(Lcom/facebook/react/bridge/ReadableMap;Lcom/facebook/react/animated/NativeAnimatedNodesManager;)V", "transformConfigs", "", "Lcom/facebook/react/animated/TransformAnimatedNode$TransformConfig;", "collectViewUpdates", "", "propsMap", "Lcom/facebook/react/bridge/JavaOnlyMap;", "prettyPrint", "", "TransformConfig", "AnimatedTransformConfig", "StaticTransformConfig", "ReactAndroid_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes.dex */
public final class TransformAnimatedNode extends AnimatedNode {
    private final NativeAnimatedNodesManager nativeAnimatedNodesManager;
    private final List<TransformConfig> transformConfigs;

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r4v3, types: [com.facebook.react.animated.TransformAnimatedNode$StaticTransformConfig] */
    /* JADX WARN: Type inference failed for: r4v4 */
    /* JADX WARN: Type inference failed for: r4v6, types: [com.facebook.react.animated.TransformAnimatedNode$AnimatedTransformConfig] */
    public TransformAnimatedNode(ReadableMap config, NativeAnimatedNodesManager nativeAnimatedNodesManager) {
        ArrayList arrayList;
        ?? staticTransformConfig;
        Intrinsics.checkNotNullParameter(config, "config");
        Intrinsics.checkNotNullParameter(nativeAnimatedNodesManager, "nativeAnimatedNodesManager");
        this.nativeAnimatedNodesManager = nativeAnimatedNodesManager;
        ReadableArray array = config.getArray("transforms");
        if (array == null) {
            arrayList = CollectionsKt.emptyList();
        } else {
            int size = array.size();
            ArrayList arrayList2 = new ArrayList(size);
            for (int i = 0; i < size; i++) {
                ReadableMap map = array.getMap(i);
                if (map == null) {
                    throw new IllegalStateException("Required value was null.".toString());
                }
                String string = map.getString("property");
                if (Intrinsics.areEqual(map.getString("type"), "animated")) {
                    staticTransformConfig = new AnimatedTransformConfig();
                    staticTransformConfig.setProperty(string);
                    staticTransformConfig.setNodeTag(map.getInt("nodeTag"));
                } else {
                    staticTransformConfig = new StaticTransformConfig();
                    staticTransformConfig.setProperty(string);
                    staticTransformConfig.setValue(map.getDouble("value"));
                }
                arrayList2.add((TransformConfig) staticTransformConfig);
            }
            arrayList = arrayList2;
        }
        this.transformConfigs = arrayList;
    }

    public final void collectViewUpdates(JavaOnlyMap propsMap) {
        double value;
        Intrinsics.checkNotNullParameter(propsMap, "propsMap");
        int size = this.transformConfigs.size();
        ArrayList arrayList = new ArrayList(size);
        for (int i = 0; i < size; i++) {
            TransformConfig transformConfig = this.transformConfigs.get(i);
            if (transformConfig instanceof AnimatedTransformConfig) {
                AnimatedNode nodeById = this.nativeAnimatedNodesManager.getNodeById(((AnimatedTransformConfig) transformConfig).getNodeTag());
                if (nodeById == null) {
                    throw new IllegalArgumentException("Mapped style node does not exist");
                }
                if (nodeById instanceof ValueAnimatedNode) {
                    value = ((ValueAnimatedNode) nodeById).getValue();
                } else {
                    throw new IllegalArgumentException("Unsupported type of node used as a transform child node " + nodeById.getClass());
                }
            } else {
                Intrinsics.checkNotNull(transformConfig, "null cannot be cast to non-null type com.facebook.react.animated.TransformAnimatedNode.StaticTransformConfig");
                value = ((StaticTransformConfig) transformConfig).getValue();
            }
            arrayList.add(JavaOnlyMap.INSTANCE.of(transformConfig.getProperty(), Double.valueOf(value)));
        }
        propsMap.putArray(ViewProps.TRANSFORM, JavaOnlyArray.INSTANCE.from(arrayList));
    }

    @Override // com.facebook.react.animated.AnimatedNode
    public String prettyPrint() {
        return "TransformAnimatedNode[" + this.tag + "]: transformConfigs: " + this.transformConfigs;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: TransformAnimatedNode.kt */
    @Metadata(d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0005\b\u0092\u0004\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003R\u001c\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\t¨\u0006\n"}, d2 = {"Lcom/facebook/react/animated/TransformAnimatedNode$TransformConfig;", "", "<init>", "(Lcom/facebook/react/animated/TransformAnimatedNode;)V", "property", "", "getProperty", "()Ljava/lang/String;", "setProperty", "(Ljava/lang/String;)V", "ReactAndroid_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
    /* loaded from: classes.dex */
    public class TransformConfig {
        private String property;

        public TransformConfig() {
        }

        public final String getProperty() {
            return this.property;
        }

        public final void setProperty(String str) {
            this.property = str;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: TransformAnimatedNode.kt */
    @Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0005\b\u0082\u0004\u0018\u00002\u00060\u0001R\u00020\u0002B\u0007¢\u0006\u0004\b\u0003\u0010\u0004R\u001a\u0010\u0005\u001a\u00020\u0006X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\n¨\u0006\u000b"}, d2 = {"Lcom/facebook/react/animated/TransformAnimatedNode$AnimatedTransformConfig;", "Lcom/facebook/react/animated/TransformAnimatedNode$TransformConfig;", "Lcom/facebook/react/animated/TransformAnimatedNode;", "<init>", "(Lcom/facebook/react/animated/TransformAnimatedNode;)V", "nodeTag", "", "getNodeTag", "()I", "setNodeTag", "(I)V", "ReactAndroid_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
    /* loaded from: classes.dex */
    public final class AnimatedTransformConfig extends TransformConfig {
        private int nodeTag;

        public AnimatedTransformConfig() {
            super();
        }

        public final int getNodeTag() {
            return this.nodeTag;
        }

        public final void setNodeTag(int i) {
            this.nodeTag = i;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: TransformAnimatedNode.kt */
    @Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\u0005\b\u0082\u0004\u0018\u00002\u00060\u0001R\u00020\u0002B\u0007¢\u0006\u0004\b\u0003\u0010\u0004R\u001a\u0010\u0005\u001a\u00020\u0006X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\n¨\u0006\u000b"}, d2 = {"Lcom/facebook/react/animated/TransformAnimatedNode$StaticTransformConfig;", "Lcom/facebook/react/animated/TransformAnimatedNode$TransformConfig;", "Lcom/facebook/react/animated/TransformAnimatedNode;", "<init>", "(Lcom/facebook/react/animated/TransformAnimatedNode;)V", "value", "", "getValue", "()D", "setValue", "(D)V", "ReactAndroid_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
    /* loaded from: classes.dex */
    public final class StaticTransformConfig extends TransformConfig {
        private double value;

        public StaticTransformConfig() {
            super();
        }

        public final double getValue() {
            return this.value;
        }

        public final void setValue(double d) {
            this.value = d;
        }
    }
}
