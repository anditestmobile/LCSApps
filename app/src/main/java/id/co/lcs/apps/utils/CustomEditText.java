package id.co.lcs.apps.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

public class CustomEditText extends androidx.appcompat.widget.AppCompatEditText {

    public CustomEditText(Context context) {
        super(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onSelectionChanged(int start, int end) {

        CharSequence text = getText();
        setSelectAllOnFocus(true);
        selectAll();
//        if (text != null) {
//            if (start != text.length() || end != text.length()) {
//                setSelection(text.length(), text.length());
//                return;
//            }
//
//        }

        super.onSelectionChanged(start, end);
    }

}
