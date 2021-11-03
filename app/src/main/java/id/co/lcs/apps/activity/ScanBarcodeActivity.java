package id.co.lcs.apps.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.zxing.Result;

import id.co.lcs.apps.R;
import id.co.lcs.apps.constants.Constants;
import id.co.lcs.apps.databinding.ActivityCartBinding;
import id.co.lcs.apps.databinding.ActivityScanBarcodeBinding;
import id.co.lcs.apps.helper.Helper;
import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.core.ViewFinderView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanBarcodeActivity extends BaseActivity implements ZXingScannerView.ResultHandler{
    private ActivityScanBarcodeBinding binding;
    private static final int PERMISSION_CAMERA = 1000;
    private ZXingScannerView mScannerView;
    private Handler handler = new Handler();
    private boolean isStarted = false;
    private int PARAM = 0;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        binding = ActivityScanBarcodeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle(null);
        init();
        initialize();

    }

    private void initialize() {
        PARAM = (int) Helper.getItemParam(Constants.PARAM_POS_SCAN);
        if(PARAM == 1){
            binding.edtBarcode.setHint("Input barcode here");
            binding.txtTitle.setText("SCAN BARCODE");
        }else{
            binding.edtBarcode.setHint("Input Quotation number here");
            binding.txtTitle.setText("SCAN QUOTATION NUMBER");
        }
        mScannerView = new ZXingScannerView(this) {
            @Override
            protected IViewFinder createViewFinderView(Context context) {
                return new CustomViewFinderView(context);
            }
        };
        binding.contentFrame.addView(mScannerView);
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ) {
                String[] permissions = {Manifest.permission.CAMERA};
                requestPermissions(permissions, PERMISSION_CAMERA);
            } else {
                startCamera();
            }
        } else {
            startCamera();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        requestPermission();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        searchData(rawResult.getText());
    }

    private void searchData(String resultText) {
        Toast.makeText(ScanBarcodeActivity.this, "Order sent", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ScanBarcodeActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            setToast("Permission denied...!");
        }
    }

    private void startCamera() {
        if (Build.VERSION.SDK_INT < 23) {
            if (!isStarted) {
                progress.show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mScannerView.setResultHandler(ScanBarcodeActivity.this);
                        mScannerView.startCamera();
                        progress.dismiss();
                        isStarted = true;
                    }
                },2000);
            } else {
                mScannerView.stopCamera();
                progress.show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mScannerView.setResultHandler(ScanBarcodeActivity.this);
                        mScannerView.startCamera();
                        progress.dismiss();
                        isStarted = true;
                    }
                },2000);
            }
        } else {
            mScannerView.setResultHandler(ScanBarcodeActivity.this);
            mScannerView.startCamera();
            progress.dismiss();
        }
    }

    private static class CustomViewFinderView extends ViewFinderView {
        public static final String TRADE_MARK_TEXT = "Scanning...";
        public static final int TRADE_MARK_TEXT_SIZE_SP = 20;
        public final Paint PAINT = new Paint();

        public CustomViewFinderView(Context context) {
            super(context);
            init();
        }

        public CustomViewFinderView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        private void init() {
            PAINT.setColor(Color.WHITE);
            PAINT.setAntiAlias(true);
            float textPixelSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, TRADE_MARK_TEXT_SIZE_SP, getResources().getDisplayMetrics());
            PAINT.setTextSize(textPixelSize);
            setSquareViewFinder(true);
        }

        @Override
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            drawTradeMark(canvas);
        }

        private void drawTradeMark(Canvas canvas) {
            Rect framingRect = getFramingRect();
            float tradeMarkTop;
            float tradeMarkLeft;
            if (framingRect != null) {
                tradeMarkTop = framingRect.bottom + PAINT.getTextSize() + 10;
                tradeMarkLeft = framingRect.left;
            } else {
                tradeMarkTop = 10;
                tradeMarkLeft = canvas.getHeight() - PAINT.getTextSize() - 10;
            }
            canvas.drawText(TRADE_MARK_TEXT, tradeMarkLeft, tradeMarkTop, PAINT);
        }
    }
}
