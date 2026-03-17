package com.facebook.react.devsupport;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.SpannedString;
import android.text.method.LinkMovementMethod;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.facebook.common.logging.FLog;
import com.facebook.react.R;
import com.facebook.react.common.ReactConstants;
import com.facebook.react.devsupport.interfaces.DevSupportManager;
import com.facebook.react.devsupport.interfaces.ErrorType;
import com.facebook.react.devsupport.interfaces.RedBoxHandler;
import com.facebook.react.devsupport.interfaces.StackFrame;
import com.facebook.react.uimanager.ViewProps;
import java.util.concurrent.Executor;
import kotlin.Deprecated;
import kotlin.Metadata;
import kotlin.TuplesKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Regex;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONObject;

/* compiled from: RedBoxContentView.kt */
@Metadata(d1 = {"\u0000\u0082\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0004\b\u0001\u0018\u00002\u00020\u00012\u00020\u0002:\u0002./B#\u0012\b\u0010\u0003\u001a\u0004\u0018\u00010\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\b¢\u0006\u0004\b\t\u0010\nJ\u0006\u0010\u001b\u001a\u00020\u001cJ!\u0010\u001d\u001a\u00020\u001c2\u0006\u0010\u001e\u001a\u00020\u001f2\f\u0010 \u001a\b\u0012\u0004\u0012\u00020\"0!¢\u0006\u0002\u0010#J\u0006\u0010$\u001a\u00020\u001cJ.\u0010%\u001a\u00020\u001c2\f\u0010&\u001a\b\u0012\u0002\b\u0003\u0018\u00010'2\u0006\u0010(\u001a\u00020\u00142\u0006\u0010)\u001a\u00020*2\u0006\u0010+\u001a\u00020,H\u0016J\u0006\u0010-\u001a\u00020\u001cR\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0014X\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0016X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0018X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u001aX\u0082\u0004¢\u0006\u0002\n\u0000¨\u00060"}, d2 = {"Lcom/facebook/react/devsupport/RedBoxContentView;", "Landroid/widget/LinearLayout;", "Landroid/widget/AdapterView$OnItemClickListener;", "context", "Landroid/content/Context;", "devSupportManager", "Lcom/facebook/react/devsupport/interfaces/DevSupportManager;", "redBoxHandler", "Lcom/facebook/react/devsupport/interfaces/RedBoxHandler;", "<init>", "(Landroid/content/Context;Lcom/facebook/react/devsupport/interfaces/DevSupportManager;Lcom/facebook/react/devsupport/interfaces/RedBoxHandler;)V", "stackView", "Landroid/widget/ListView;", "reportButton", "Landroid/widget/Button;", "reportTextView", "Landroid/widget/TextView;", "loadingIndicator", "Landroid/widget/ProgressBar;", "lineSeparator", "Landroid/view/View;", "isReporting", "", "reportCompletedListener", "Lcom/facebook/react/devsupport/interfaces/RedBoxHandler$ReportCompletedListener;", "reportButtonOnClickListener", "Landroid/view/View$OnClickListener;", "init", "", "setExceptionDetails", "title", "", StackTraceHelper.STACK_KEY, "", "Lcom/facebook/react/devsupport/interfaces/StackFrame;", "(Ljava/lang/String;[Lcom/facebook/react/devsupport/interfaces/StackFrame;)V", "resetReporting", "onItemClick", "parent", "Landroid/widget/AdapterView;", "view", ViewProps.POSITION, "", "id", "", "refreshContentView", "StackAdapter", "OpenStackFrameTask", "ReactAndroid_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes.dex */
public final class RedBoxContentView extends LinearLayout implements AdapterView.OnItemClickListener {
    private final DevSupportManager devSupportManager;
    private boolean isReporting;
    private View lineSeparator;
    private ProgressBar loadingIndicator;
    private final RedBoxHandler redBoxHandler;
    private Button reportButton;
    private final View.OnClickListener reportButtonOnClickListener;
    private final RedBoxHandler.ReportCompletedListener reportCompletedListener;
    private TextView reportTextView;
    private ListView stackView;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public RedBoxContentView(Context context, DevSupportManager devSupportManager, RedBoxHandler redBoxHandler) {
        super(context);
        Intrinsics.checkNotNullParameter(devSupportManager, "devSupportManager");
        this.devSupportManager = devSupportManager;
        this.redBoxHandler = redBoxHandler;
        this.reportCompletedListener = new RedBoxHandler.ReportCompletedListener() { // from class: com.facebook.react.devsupport.RedBoxContentView$reportCompletedListener$1
            @Override // com.facebook.react.devsupport.interfaces.RedBoxHandler.ReportCompletedListener
            public void onReportSuccess(SpannedString spannedString) {
                Button button;
                ProgressBar progressBar;
                TextView textView;
                RedBoxContentView.this.isReporting = false;
                button = RedBoxContentView.this.reportButton;
                TextView textView2 = null;
                if (button == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("reportButton");
                    button = null;
                }
                button.setEnabled(true);
                progressBar = RedBoxContentView.this.loadingIndicator;
                if (progressBar == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("loadingIndicator");
                    progressBar = null;
                }
                progressBar.setVisibility(8);
                textView = RedBoxContentView.this.reportTextView;
                if (textView == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("reportTextView");
                } else {
                    textView2 = textView;
                }
                textView2.setText(spannedString);
            }

            @Override // com.facebook.react.devsupport.interfaces.RedBoxHandler.ReportCompletedListener
            public void onReportError(SpannedString spannedString) {
                Button button;
                ProgressBar progressBar;
                TextView textView;
                RedBoxContentView.this.isReporting = false;
                button = RedBoxContentView.this.reportButton;
                TextView textView2 = null;
                if (button == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("reportButton");
                    button = null;
                }
                button.setEnabled(true);
                progressBar = RedBoxContentView.this.loadingIndicator;
                if (progressBar == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("loadingIndicator");
                    progressBar = null;
                }
                progressBar.setVisibility(8);
                textView = RedBoxContentView.this.reportTextView;
                if (textView == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("reportTextView");
                } else {
                    textView2 = textView;
                }
                textView2.setText(spannedString);
            }
        };
        this.reportButtonOnClickListener = new View.OnClickListener() { // from class: com.facebook.react.devsupport.RedBoxContentView$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                RedBoxContentView.reportButtonOnClickListener$lambda$0(RedBoxContentView.this, view);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void reportButtonOnClickListener$lambda$0(RedBoxContentView redBoxContentView, View view) {
        RedBoxHandler redBoxHandler = redBoxContentView.redBoxHandler;
        if (redBoxHandler == null || !redBoxHandler.isReportEnabled() || redBoxContentView.isReporting) {
            return;
        }
        redBoxContentView.isReporting = true;
        TextView textView = redBoxContentView.reportTextView;
        Button button = null;
        if (textView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("reportTextView");
            textView = null;
        }
        textView.setText("Reporting...");
        TextView textView2 = redBoxContentView.reportTextView;
        if (textView2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("reportTextView");
            textView2 = null;
        }
        textView2.setVisibility(0);
        ProgressBar progressBar = redBoxContentView.loadingIndicator;
        if (progressBar == null) {
            Intrinsics.throwUninitializedPropertyAccessException("loadingIndicator");
            progressBar = null;
        }
        progressBar.setVisibility(0);
        View view2 = redBoxContentView.lineSeparator;
        if (view2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("lineSeparator");
            view2 = null;
        }
        view2.setVisibility(0);
        Button button2 = redBoxContentView.reportButton;
        if (button2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("reportButton");
        } else {
            button = button2;
        }
        button.setEnabled(false);
        String lastErrorTitle = redBoxContentView.devSupportManager.getLastErrorTitle();
        if (lastErrorTitle == null) {
            throw new IllegalStateException("Required value was null.".toString());
        }
        StackFrame[] lastErrorStack = redBoxContentView.devSupportManager.getLastErrorStack();
        if (lastErrorStack == null) {
            throw new IllegalStateException("Required value was null.".toString());
        }
        String sourceUrl = redBoxContentView.devSupportManager.getSourceUrl();
        if (sourceUrl == null) {
            throw new IllegalStateException("Required value was null.".toString());
        }
        RedBoxHandler redBoxHandler2 = redBoxContentView.redBoxHandler;
        Context context = view.getContext();
        Intrinsics.checkNotNullExpressionValue(context, "getContext(...)");
        redBoxHandler2.reportRedbox(context, lastErrorTitle, lastErrorStack, sourceUrl, redBoxContentView.reportCompletedListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: RedBoxContentView.kt */
    @Metadata(d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0002\u0018\u0000 \u001c2\u00020\u0001:\u0002\u001b\u001cB\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005¢\u0006\u0004\b\u0007\u0010\bJ\b\u0010\n\u001a\u00020\u000bH\u0016J\u0010\u0010\f\u001a\u00020\u000b2\u0006\u0010\r\u001a\u00020\u000eH\u0016J\b\u0010\u000f\u001a\u00020\u000eH\u0016J\u0010\u0010\u0010\u001a\u00020\u00112\u0006\u0010\r\u001a\u00020\u000eH\u0016J\u0010\u0010\u0012\u001a\u00020\u00132\u0006\u0010\r\u001a\u00020\u000eH\u0016J\b\u0010\u0014\u001a\u00020\u000eH\u0016J\u0010\u0010\u0015\u001a\u00020\u000e2\u0006\u0010\r\u001a\u00020\u000eH\u0016J\"\u0010\u0016\u001a\u00020\u00172\u0006\u0010\r\u001a\u00020\u000e2\b\u0010\u0018\u001a\u0004\u0018\u00010\u00172\u0006\u0010\u0019\u001a\u00020\u001aH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0016\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\t¨\u0006\u001d"}, d2 = {"Lcom/facebook/react/devsupport/RedBoxContentView$StackAdapter;", "Landroid/widget/BaseAdapter;", "title", "", StackTraceHelper.STACK_KEY, "", "Lcom/facebook/react/devsupport/interfaces/StackFrame;", "<init>", "(Ljava/lang/String;[Lcom/facebook/react/devsupport/interfaces/StackFrame;)V", "[Lcom/facebook/react/devsupport/interfaces/StackFrame;", "areAllItemsEnabled", "", "isEnabled", ViewProps.POSITION, "", "getCount", "getItem", "", "getItemId", "", "getViewTypeCount", "getItemViewType", "getView", "Landroid/view/View;", "convertView", "parent", "Landroid/view/ViewGroup;", "FrameViewHolder", "Companion", "ReactAndroid_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
    /* loaded from: classes.dex */
    public static final class StackAdapter extends BaseAdapter {
        private static final int VIEW_TYPE_COUNT = 2;
        private static final int VIEW_TYPE_STACKFRAME = 1;
        private static final int VIEW_TYPE_TITLE = 0;
        private final StackFrame[] stack;
        private final String title;

        @Override // android.widget.BaseAdapter, android.widget.ListAdapter
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public int getItemViewType(int position) {
            return position == 0 ? 0 : 1;
        }

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public int getViewTypeCount() {
            return 2;
        }

        @Override // android.widget.BaseAdapter, android.widget.ListAdapter
        public boolean isEnabled(int position) {
            return position > 0;
        }

        /* compiled from: RedBoxContentView.kt */
        @Metadata(d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0002\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005R\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\n\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\t¨\u0006\f"}, d2 = {"Lcom/facebook/react/devsupport/RedBoxContentView$StackAdapter$FrameViewHolder;", "", "v", "Landroid/view/View;", "<init>", "(Landroid/view/View;)V", "methodView", "Landroid/widget/TextView;", "getMethodView", "()Landroid/widget/TextView;", "fileView", "getFileView", "ReactAndroid_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
        /* loaded from: classes.dex */
        private static final class FrameViewHolder {
            private final TextView fileView;
            private final TextView methodView;

            public FrameViewHolder(View v) {
                Intrinsics.checkNotNullParameter(v, "v");
                View findViewById = v.findViewById(R.id.rn_frame_method);
                Intrinsics.checkNotNullExpressionValue(findViewById, "findViewById(...)");
                this.methodView = (TextView) findViewById;
                View findViewById2 = v.findViewById(R.id.rn_frame_file);
                Intrinsics.checkNotNullExpressionValue(findViewById2, "findViewById(...)");
                this.fileView = (TextView) findViewById2;
            }

            public final TextView getMethodView() {
                return this.methodView;
            }

            public final TextView getFileView() {
                return this.fileView;
            }
        }

        public StackAdapter(String title, StackFrame[] stack) {
            Intrinsics.checkNotNullParameter(title, "title");
            Intrinsics.checkNotNullParameter(stack, "stack");
            this.title = title;
            this.stack = stack;
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return this.stack.length + 1;
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            return position == 0 ? this.title : this.stack[position - 1];
        }

        @Override // android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView;
            Intrinsics.checkNotNullParameter(parent, "parent");
            if (position == 0) {
                if (convertView != null) {
                    textView = (TextView) convertView;
                } else {
                    View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.redbox_item_title, parent, false);
                    Intrinsics.checkNotNull(inflate, "null cannot be cast to non-null type android.widget.TextView");
                    textView = (TextView) inflate;
                }
                textView.setText(new Regex("\\x1b\\[[0-9;]*m").replace(this.title, ""));
                return textView;
            }
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.redbox_item_frame, parent, false);
                Intrinsics.checkNotNull(convertView);
                convertView.setTag(new FrameViewHolder(convertView));
            }
            StackFrame stackFrame = this.stack[position - 1];
            Object tag = convertView != null ? convertView.getTag() : null;
            Intrinsics.checkNotNull(tag, "null cannot be cast to non-null type com.facebook.react.devsupport.RedBoxContentView.StackAdapter.FrameViewHolder");
            FrameViewHolder frameViewHolder = (FrameViewHolder) tag;
            frameViewHolder.getMethodView().setText(stackFrame.getMethod());
            frameViewHolder.getFileView().setText(StackTraceHelper.formatFrameSource(stackFrame));
            frameViewHolder.getMethodView().setTextColor(stackFrame.isCollapsed() ? -5592406 : -1);
            frameViewHolder.getFileView().setTextColor(stackFrame.isCollapsed() ? -8355712 : -5000269);
            return convertView;
        }
    }

    /* compiled from: RedBoxContentView.kt */
    @Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0011\n\u0002\b\u0003\b\u0002\u0018\u0000 \f2\u001a\u0012\u0006\u0012\u0004\u0018\u00010\u0002\u0012\u0006\u0012\u0004\u0018\u00010\u0003\u0012\u0006\u0012\u0004\u0018\u00010\u00030\u0001:\u0001\fB\u000f\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J'\u0010\b\u001a\u0004\u0018\u00010\u00032\u0016\u0010\t\u001a\f\u0012\b\b\u0001\u0012\u0004\u0018\u00010\u00020\n\"\u0004\u0018\u00010\u0002H\u0015¢\u0006\u0002\u0010\u000bR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\r"}, d2 = {"Lcom/facebook/react/devsupport/RedBoxContentView$OpenStackFrameTask;", "Landroid/os/AsyncTask;", "Lcom/facebook/react/devsupport/interfaces/StackFrame;", "Ljava/lang/Void;", "devSupportManager", "Lcom/facebook/react/devsupport/interfaces/DevSupportManager;", "<init>", "(Lcom/facebook/react/devsupport/interfaces/DevSupportManager;)V", "doInBackground", "stackFrames", "", "([Lcom/facebook/react/devsupport/interfaces/StackFrame;)Ljava/lang/Void;", "Companion", "ReactAndroid_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
    /* loaded from: classes.dex */
    private static final class OpenStackFrameTask extends AsyncTask<StackFrame, Void, Void> {

        /* renamed from: Companion, reason: from kotlin metadata */
        public static final Companion INSTANCE = new Companion(null);
        private static final MediaType JSON = MediaType.INSTANCE.m2132deprecated_parse("application/json; charset=utf-8");
        private final DevSupportManager devSupportManager;

        public OpenStackFrameTask(DevSupportManager devSupportManager) {
            Intrinsics.checkNotNullParameter(devSupportManager, "devSupportManager");
            this.devSupportManager = devSupportManager;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        @Deprecated(message = "Deprecated in Java")
        public Void doInBackground(StackFrame... stackFrames) {
            Intrinsics.checkNotNullParameter(stackFrames, "stackFrames");
            try {
                String uri = Uri.parse(this.devSupportManager.getSourceUrl()).buildUpon().path("/open-stack-frame").query(null).build().toString();
                Intrinsics.checkNotNullExpressionValue(uri, "toString(...)");
                OkHttpClient okHttpClient = new OkHttpClient();
                for (StackFrame stackFrame : stackFrames) {
                    Companion companion = INSTANCE;
                    if (stackFrame == null) {
                        throw new IllegalStateException("Required value was null.".toString());
                    }
                    String jSONObject = companion.stackFrameToJson(stackFrame).toString();
                    Intrinsics.checkNotNullExpressionValue(jSONObject, "toString(...)");
                    okHttpClient.newCall(new Request.Builder().url(uri).post(RequestBody.INSTANCE.create(JSON, jSONObject)).build()).execute();
                }
            } catch (Exception e) {
                FLog.e(ReactConstants.TAG, "Could not open stack frame", e);
            }
            return null;
        }

        /* compiled from: RedBoxContentView.kt */
        @Metadata(d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0002R\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\n"}, d2 = {"Lcom/facebook/react/devsupport/RedBoxContentView$OpenStackFrameTask$Companion;", "", "<init>", "()V", "JSON", "Lokhttp3/MediaType;", "stackFrameToJson", "Lorg/json/JSONObject;", "frame", "Lcom/facebook/react/devsupport/interfaces/StackFrame;", "ReactAndroid_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
        /* loaded from: classes.dex */
        public static final class Companion {
            public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
                this();
            }

            private Companion() {
            }

            /* JADX INFO: Access modifiers changed from: private */
            public final JSONObject stackFrameToJson(StackFrame frame) {
                return new JSONObject(MapsKt.mapOf(TuplesKt.to("file", frame.getFile()), TuplesKt.to("methodName", frame.getMethod()), TuplesKt.to("lineNumber", Integer.valueOf(frame.getLine())), TuplesKt.to("column", Integer.valueOf(frame.getColumn()))));
            }
        }
    }

    public final void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.redbox_view, this);
        ListView listView = (ListView) findViewById(R.id.rn_redbox_stack);
        listView.setOnItemClickListener(this);
        this.stackView = listView;
        ((Button) findViewById(R.id.rn_redbox_reload_button)).setOnClickListener(new View.OnClickListener() { // from class: com.facebook.react.devsupport.RedBoxContentView$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                RedBoxContentView.init$lambda$2(RedBoxContentView.this, view);
            }
        });
        ((Button) findViewById(R.id.rn_redbox_dismiss_button)).setOnClickListener(new View.OnClickListener() { // from class: com.facebook.react.devsupport.RedBoxContentView$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                RedBoxContentView.init$lambda$3(RedBoxContentView.this, view);
            }
        });
        RedBoxHandler redBoxHandler = this.redBoxHandler;
        if (redBoxHandler == null || !redBoxHandler.isReportEnabled()) {
            return;
        }
        this.loadingIndicator = (ProgressBar) findViewById(R.id.rn_redbox_loading_indicator);
        this.lineSeparator = findViewById(R.id.rn_redbox_line_separator);
        TextView textView = (TextView) findViewById(R.id.rn_redbox_report_label);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setHighlightColor(0);
        this.reportTextView = textView;
        Button button = (Button) findViewById(R.id.rn_redbox_report_button);
        button.setOnClickListener(this.reportButtonOnClickListener);
        this.reportButton = button;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void init$lambda$2(RedBoxContentView redBoxContentView, View view) {
        redBoxContentView.devSupportManager.handleReloadJS();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void init$lambda$3(RedBoxContentView redBoxContentView, View view) {
        redBoxContentView.devSupportManager.hideRedboxDialog();
    }

    public final void setExceptionDetails(String title, StackFrame[] stack) {
        Intrinsics.checkNotNullParameter(title, "title");
        Intrinsics.checkNotNullParameter(stack, "stack");
        ListView listView = this.stackView;
        if (listView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("stackView");
            listView = null;
        }
        listView.setAdapter((ListAdapter) new StackAdapter(title, stack));
    }

    public final void resetReporting() {
        RedBoxHandler redBoxHandler = this.redBoxHandler;
        if (redBoxHandler == null || redBoxHandler.isReportEnabled()) {
            this.isReporting = false;
            TextView textView = this.reportTextView;
            Button button = null;
            if (textView == null) {
                Intrinsics.throwUninitializedPropertyAccessException("reportTextView");
                textView = null;
            }
            textView.setVisibility(8);
            ProgressBar progressBar = this.loadingIndicator;
            if (progressBar == null) {
                Intrinsics.throwUninitializedPropertyAccessException("loadingIndicator");
                progressBar = null;
            }
            progressBar.setVisibility(8);
            View view = this.lineSeparator;
            if (view == null) {
                Intrinsics.throwUninitializedPropertyAccessException("lineSeparator");
                view = null;
            }
            view.setVisibility(8);
            Button button2 = this.reportButton;
            if (button2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("reportButton");
                button2 = null;
            }
            button2.setVisibility(0);
            Button button3 = this.reportButton;
            if (button3 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("reportButton");
            } else {
                button = button3;
            }
            button.setEnabled(true);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intrinsics.checkNotNullParameter(view, "view");
        OpenStackFrameTask openStackFrameTask = new OpenStackFrameTask(this.devSupportManager);
        Executor executor = AsyncTask.THREAD_POOL_EXECUTOR;
        StackFrame[] stackFrameArr = new StackFrame[1];
        ListView listView = this.stackView;
        if (listView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("stackView");
            listView = null;
        }
        Object item = listView.getAdapter().getItem(position);
        Intrinsics.checkNotNull(item, "null cannot be cast to non-null type com.facebook.react.devsupport.interfaces.StackFrame");
        stackFrameArr[0] = item;
        openStackFrameTask.executeOnExecutor(executor, stackFrameArr);
    }

    public final void refreshContentView() {
        String lastErrorTitle = this.devSupportManager.getLastErrorTitle();
        StackFrame[] lastErrorStack = this.devSupportManager.getLastErrorStack();
        if (lastErrorStack == null) {
            lastErrorStack = new StackFrame[0];
        }
        ErrorType lastErrorType = this.devSupportManager.getLastErrorType();
        if (lastErrorType == null) {
            throw new IllegalStateException("Required value was null.".toString());
        }
        Pair<String, StackFrame[]> processErrorCustomizers = this.devSupportManager.processErrorCustomizers(Pair.create(lastErrorTitle, lastErrorStack));
        if (processErrorCustomizers == null) {
            throw new IllegalStateException("Required value was null.".toString());
        }
        Object first = processErrorCustomizers.first;
        Intrinsics.checkNotNullExpressionValue(first, "first");
        Object second = processErrorCustomizers.second;
        Intrinsics.checkNotNullExpressionValue(second, "second");
        setExceptionDetails((String) first, (StackFrame[]) second);
        RedBoxHandler redBoxHandler = this.devSupportManager.getRedBoxHandler();
        if (redBoxHandler != null) {
            redBoxHandler.handleRedbox(lastErrorTitle, lastErrorStack, lastErrorType);
            resetReporting();
        }
    }
}
