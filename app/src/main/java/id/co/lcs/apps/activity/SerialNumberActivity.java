package id.co.lcs.apps.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

import id.co.lcs.apps.R;
import id.co.lcs.apps.adapter.SerialNumberAdapter;
import id.co.lcs.apps.adapter.StockTakeAdapter;
import id.co.lcs.apps.constants.Constants;
import id.co.lcs.apps.databinding.ActivitySerialNumberBinding;
import id.co.lcs.apps.helper.Helper;
import id.co.lcs.apps.model.GoodReceiptSupplier;
import id.co.lcs.apps.model.SerialNumber;
import id.co.lcs.apps.model.StockTake;
import id.co.lcs.apps.utils.Utils;
import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.core.ViewFinderView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class SerialNumberActivity extends BaseActivity implements ZXingScannerView.ResultHandler{
    private ActivitySerialNumberBinding binding;
    private ZXingScannerView mScannerView;
    private Handler handler = new Handler();
    private boolean isStarted = false;
    private SerialNumberAdapter adapter;
    private List<SerialNumber> serialNumberList = new ArrayList<>();
    private GoodReceiptSupplier gr;
    private int pos;
    private BottomSheetDialog bottomSheetDialog;
    private int posSN = 0;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        binding = ActivitySerialNumberBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        init();
        initialize();
    }

    private void initialize() {
        pos = Integer.parseInt(Helper.getItemParam(Constants.POSITION_GR).toString());
        gr = (GoodReceiptSupplier) Helper.getItemParam(Constants.LIST_DETAIL_SUPPLIER_GR);
        ViewGroup contentFrame = findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this) {
            @Override
            protected IViewFinder createViewFinderView(Context context) {
                return new CustomViewFinderView(context);
            }
        };
        contentFrame.addView(mScannerView);
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean FLAG = true;
                for(SerialNumber sn : serialNumberList){
                    if(sn.getSerialNo().trim().isEmpty()){
                        FLAG = false;
                        break;
                    }
                }
                if(FLAG) {
                    showDialogConfirmation();
                }else{
                    Snackbar.make(v, "Serial Number cannot empty", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        setData(null);
    }

    public void showDialogConfirmation() {
        if (bottomSheetDialog == null || !bottomSheetDialog.isShowing()) {
            bottomSheetDialog = Utils.showBottomSheetDialog(SerialNumberActivity.this, R.layout.bsheet_confirmation, (itemView, bottomSheet) -> {
                TextView txtTitle = itemView.findViewById(R.id.labelTitle);
                Button btnNo = itemView.findViewById(R.id.btnSkip);
                Button btnYes = itemView.findViewById(R.id.btnEmail);
                txtTitle.setText("Do you want to save all serial number?");
                btnYes.setText("Save");

                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        gr.getGrSupplierDetail().get(pos).setSerialNo(serialNumberList);
                        Helper.setItemParam(Constants.LIST_DETAIL_SUPPLIER_GR, gr);
//                        Toast.makeText(getApplicationContext(), "Good Receipt Submit!", Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                        onBackPressed();
                    }
                });

                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                    }
                });
            });
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int PERMISSION_ALL = 1;
            String[] PERMISSIONS = {
//                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
//                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.CAMERA
            };

            if (!hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            }else{
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
        setDataSN(resultText);
        restartCamera();
    }

    public void setDataSN(String resultText) {
        serialNumberList.get(posSN).setSerialNo(resultText);
        adapter.notifyItemChanged(posSN);
    }

    public void changeSN(String resultText, int pos) {
        serialNumberList.get(pos).setSerialNo(resultText);
    }

    public void setData(String resultText){
        binding.rcSN.setVisibility(View.VISIBLE);
        binding.txtMsg.setVisibility(View.GONE);
        int qty =(int) Double.parseDouble(gr.getGrSupplierDetail().get(pos).getQty());
        serialNumberList = gr.getGrSupplierDetail().get(pos).getSerialNo();
        if(serialNumberList == null || serialNumberList.size() == 0) {
            serialNumberList = new ArrayList<>();
            for(int i=0; i<qty; i++){
                serialNumberList.add(new SerialNumber("", String.valueOf(1)));
            }
        }
        adapter = new SerialNumberAdapter(this, serialNumberList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SerialNumberActivity.this);

        binding.rcSN.setLayoutManager(layoutManager);

        binding.rcSN.setAdapter(adapter);
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
                        mScannerView.setResultHandler(SerialNumberActivity.this);
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
                        mScannerView.setResultHandler(SerialNumberActivity.this);
                        mScannerView.startCamera();
                        progress.dismiss();
                        isStarted = true;
                    }
                },2000);
            }
        } else {
            mScannerView.setResultHandler(SerialNumberActivity.this);
            mScannerView.startCamera();
            progress.dismiss();
        }
    }

    public void changeSNPosition(int position) {
        posSN = position;
    }

    public void changeSN(int position) {

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



    public void restartCamera(){
        mScannerView.stopCamera(); //mScannerView = new ZXingScannerView(getActivity());
        startCamera();
    }



}
