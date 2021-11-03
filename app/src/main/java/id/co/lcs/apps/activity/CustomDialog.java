package id.co.lcs.apps.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import id.co.lcs.apps.R;
import id.co.lcs.apps.adapter.CommitedAdapter;

public class CustomDialog extends Dialog implements View.OnClickListener {


    public CustomDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public CustomDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    public Activity activity;
    public Dialog dialog;
    public Button btnOk;
    RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    CommitedAdapter adapter;


    public CustomDialog(Activity a, CommitedAdapter adapter) {
        super(a);
        this.activity = a;
        this.adapter = adapter;
        setupLayout();
    }

    private void setupLayout() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_commited);
        btnOk = (Button) findViewById(R.id.btnOk);
        recyclerView = findViewById(R.id.rcCm);
        mLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(mLayoutManager);


        recyclerView.setAdapter(adapter);
        btnOk.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOk:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}