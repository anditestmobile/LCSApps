package id.co.lcs.apps.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import id.co.lcs.apps.interfaces.MultipleCallback;
import id.co.lcs.apps.R;

/**
 * Created by TED on 16-Jul-20
 */
public class Utils {
    public static BottomSheetDialog showBottomSheetDialog(Activity activity, int resId, MultipleCallback<View, BottomSheetDialog> initView) {
        View view = LayoutInflater.from(activity).inflate(resId, activity.findViewById(android.R.id.content), false);
        BottomSheetDialog dialog = new BottomSheetDialog(activity);
        dialog.setOnShowListener(dialog1 -> {
            FrameLayout bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
            if (bottomSheet == null)
                return;
            bottomSheet.setBackgroundResource(0);
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        dialog.setContentView(view);
        initView.onCallback(view, dialog);
        dialog.show();
        return dialog;
    }

    public static ProgressDialog createProgressDialog(final Context mContext) {
        ProgressDialog dialog = new ProgressDialog(mContext);
        try {
            dialog.show();
        } catch (WindowManager.BadTokenException ignore) {

        }
        dialog.setCancelable(false);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        dialog.setContentView(R.layout.progress_bar);
        dialog.dismiss();
        // dialog.setMessage(Message);
        return dialog;
    }

    public interface OnBackDialogPressed {
        void onBackPressed();
    }

    public static <T> T linkedTreeMapToGSON(Object object, Class<T> responseType) {
        final Gson gson = new GsonBuilder().setExclusionStrategies(new AnnotationExclusionStrategy()).create();

        String json = gson.toJson(object);

        return gson.fromJson(json, responseType);
    }
}
