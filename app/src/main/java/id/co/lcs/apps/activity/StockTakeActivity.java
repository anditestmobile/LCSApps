package id.co.lcs.apps.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

import id.co.lcs.apps.R;
import id.co.lcs.apps.adapter.StockTakeAdapter;
import id.co.lcs.apps.constants.Constants;
import id.co.lcs.apps.databinding.ActivityStockTakeBinding;
import id.co.lcs.apps.helper.Helper;
import id.co.lcs.apps.model.StockDetailsReponse;
import id.co.lcs.apps.model.StockDetailsRequest;
import id.co.lcs.apps.model.StockTake;
import id.co.lcs.apps.model.StockTakeRequest;
import id.co.lcs.apps.model.StockTakeResponse;
import id.co.lcs.apps.model.WMStock;
import id.co.lcs.apps.utils.Utils;
import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.core.ViewFinderView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class StockTakeActivity extends BaseActivity implements ZXingScannerView.ResultHandler{
    private ActivityStockTakeBinding binding;
    private ZXingScannerView mScannerView;
    private Handler handler = new Handler();
    private boolean isStarted = false;
    private StockTakeAdapter adapter;
    private List<StockTake> stockTakeList = new ArrayList<>();
    private BottomSheetDialog bottomSheetDialog;
    private String wh, bin;
    private int PARAM = 0;
    private EditText edtBin;
    private StockDetailsReponse stockResponse;
    private String scan;
    private List<WMStock> searchResults = new ArrayList<>();
    private StockTakeResponse st;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        binding = ActivityStockTakeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        init();
        initialize();
    }

    private void initialize() {
        wh = "01";
        bin = (String) Helper.getItemParam(Constants.BIN).toString();
        binding.txtWH.setText(wh);
        binding.txtBIN.setText(bin);
        ViewGroup contentFrame = findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(getApplicationContext()) {
            @Override
            protected IViewFinder createViewFinderView(Context context) {
                return new CustomViewFinderView(context);
            }
        };
        contentFrame.addView(mScannerView);
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stockTakeList != null && stockTakeList.size() != 0) {
                    showDialogConfirmation();
                }else{
                    Snackbar.make(v, "No item found", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        binding.btnDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stockTakeList != null && stockTakeList.size() != 0) {
                    showDialogConfirmationRemove(null, 0, 1);
                }else{
                    Snackbar.make(v, "No item found", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        binding.imgInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(StockTakeActivity.this);
                builder.setTitle("Input Item Code");
                final EditText input = new EditText(StockTakeActivity.this);
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        scan = input.getText().toString();
                        PARAM = 1;
                        new RequestUrl().execute();
                        getProgressDialog().show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
        mScannerView.stopCamera();
    }

    public void showDialogConfirmation() {
        if (bottomSheetDialog == null || !bottomSheetDialog.isShowing()) {
            bottomSheetDialog = Utils.showBottomSheetDialog(StockTakeActivity.this, R.layout.bsheet_confirmation, (itemView, bottomSheet) -> {
                TextView txtTitle = itemView.findViewById(R.id.labelTitle);
                Button btnNo = itemView.findViewById(R.id.btnSkip);
                Button btnYes = itemView.findViewById(R.id.btnEmail);
                txtTitle.setText("Do you want to proceed this Item?");
                btnYes.setText("Submit");

                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Toast.makeText(getApplicationContext(), "Good Receipt Submit!", Toast.LENGTH_SHORT).show();
//                        bottomSheetDialog.dismiss();
//                        onBackPressed();
                        PARAM = 0;
                        new RequestUrl().execute();
                        getProgressDialog().show();
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
        scan = resultText;
        PARAM = 1;
        new RequestUrl().execute();
        getProgressDialog().show();
//        setData(resultText);
//        restartCamera();
    }

    public void setData(List<WMStock> stock){
        if(stockTakeList != null && stockTakeList.size() != 0) {
            int FLAG = 0;
            for (StockTake data : stockTakeList) {
                if (data.getItemCode().equals(stock.get(0).getIdMaterial())) {
                    data.setQty(data.getQty() + 1);
                    FLAG = 1;
                    break;
                }
            }
            if(FLAG == 0){
                stockTakeList.add(new StockTake(stock.get(0).getIdMaterial(),1,stock.get(0).getDesc()));
            }
            adapter.notifyDataSetChanged();
        }else{
            binding.rcStockTake.setVisibility(View.VISIBLE);
            binding.txtMsg.setVisibility(View.GONE);
            stockTakeList.add(new StockTake(stock.get(0).getIdMaterial(),1,stock.get(0).getDesc()));
            adapter = new StockTakeAdapter(this, stockTakeList);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(StockTakeActivity.this);

            binding.rcStockTake.setLayoutManager(layoutManager);

            binding.rcStockTake.setAdapter(adapter);
        }
        restartCamera();
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
        if (!isStarted) {
            progress.show();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScannerView.setResultHandler(StockTakeActivity.this);
                    mScannerView.startCamera();
                    progress.dismiss();
                    isStarted = true;
                }
            },1000);
        } else {
            mScannerView.stopCamera();
            progress.show();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScannerView.setResultHandler(StockTakeActivity.this);
                    mScannerView.startCamera();
                    progress.dismiss();
                    isStarted = true;
                }
            },1000);
        }
//        if (Build.VERSION.SDK_INT < 23) {
//
//        } else {
//            mScannerView.setResultHandler(StockTakeActivity.this);
//            mScannerView.startCamera();
//            mScannerView.setAutoFocus(true);
//            progress.dismiss();
//        }
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

    public void showDialogConfirmationRemove(StockTake data, int position, int FLAG_DELETE) {
        if (bottomSheetDialog == null || !bottomSheetDialog.isShowing()) {
            bottomSheetDialog = Utils.showBottomSheetDialog(StockTakeActivity.this, R.layout.bsheet_confirmation, (itemView, bottomSheet) -> {
                TextView txtTitle = itemView.findViewById(R.id.labelTitle);
                Button btnNo = itemView.findViewById(R.id.btnSkip);
                Button btnYes = itemView.findViewById(R.id.btnEmail);
                btnYes.setText("DELETE");
                if(FLAG_DELETE == 0) {
                    txtTitle.setText("Are you sure to delete this item?");
                }else{
                    txtTitle.setText("Are you sure to delete all item?");
                }

                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(FLAG_DELETE == 0) {
                            stockTakeList.remove(position);
                            adapter.notifyDataSetChanged();
                            if(stockTakeList.size() == 0){
                                binding.rcStockTake.setVisibility(View.GONE);
                                binding.txtMsg.setVisibility(View.VISIBLE);
                            }
                        }else{
                            stockTakeList.clear();
                            adapter.notifyDataSetChanged();
                            binding.rcStockTake.setVisibility(View.GONE);
                            binding.txtMsg.setVisibility(View.VISIBLE);
                        }
                        bottomSheetDialog.dismiss();
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


    private class RequestUrl extends AsyncTask<Void, Void, StockTakeResponse> {

        @Override
        protected StockTakeResponse doInBackground(Void... voids) {
            try {
                if (PARAM == 0) {
                    String URL_STOCK_TAKE = Constants.API_PREFIX + Constants.API_STOCK_TAKE;
                    final String url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_STOCK_TAKE);
                    StockTakeRequest str = new StockTakeRequest();
                    str.setName(user.getFirstName());
                    str.setWhsCode(wh);
                    str.setBinLocation(bin);
                    str.setCountingDate(Helper.todayDate1("yyyyMMdd"));
                    str.setStockTake(stockTakeList);
                    return (StockTakeResponse) Helper.postWebservice(url, str, StockTakeResponse.class);
                } else {
                    String URL_STOCK_DETAILS = Constants.API_PREFIX + Constants.API_GET_STOCK;
                    final String url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_STOCK_DETAILS);
                    StockDetailsRequest sd = new StockDetailsRequest();
                    sd.setBarcode(scan);
                    sd.setItemCode("");
                    sd.setWh(wh);
                    stockResponse = (StockDetailsReponse) Helper.postWebservice(url, sd, StockDetailsReponse.class);
                    return null;
                }
            } catch (Exception ex) {
                if (ex.getMessage() != null) {
                    Helper.setItemParam(Constants.INTERNAL_SERVER_ERROR, ex.getMessage());
                }
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(StockTakeResponse stock) {
            if (PARAM == 0) {
                st = stock;
                getProgressDialog().dismiss();
                bottomSheetDialog.dismiss();
                if (st != null) {
                    if (st.getStatusCode() == 1) {
                        onDone(st);
                    } else {
                        if (st.getResponseData().getErrorMessage() != null) {
                            Toast.makeText(getApplicationContext(), st.getStatusMessage() + ", " + st.getResponseData().getErrorMessage(), Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(), "Failed, please check on system", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR) != null) {
                        Toast.makeText(getApplicationContext(), Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR).toString(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), st.getStatusMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                getProgressDialog().dismiss();
                if(stockResponse != null){
                    if(stockResponse.getStatusCode() == 1) {
                        searchResults = new ArrayList<>();
                        searchResults = stockResponse.getResponseData();
                        setData(searchResults);
                    }else{
                        Toast.makeText(getApplicationContext(), stockResponse.getStatusMessage(), Toast.LENGTH_SHORT).show();
                        restartCamera();
                    }
                } else {
                    if (Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR) != null) {
                        Toast.makeText(getApplicationContext(), Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR).toString(), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), stockResponse.getStatusMessage(), Toast.LENGTH_SHORT).show();
                    }
                    restartCamera();
                }
            }
        }
    }

    private void onDone(StockTakeResponse stockTakeResponse) {

        new AlertDialog.Builder(StockTakeActivity.this)
                .setCancelable(false)
                .setMessage(st.getStatusMessage() + ", Document Number : " + st.getResponseData().getDocNum() + "\n"
                        + "Do you want to proceed next bin or end stock take?")
                .setNegativeButton("End", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showDialogBin();
                    }
                }).show();
    }

    public void showDialogBin() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_bin, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog a = dialogBuilder.create();
        a.setCancelable(false);
        edtBin = (EditText) dialogView.findViewById(R.id.edtBatch);
        final ImageButton imgBtnClose = (ImageButton) dialogView.findViewById(R.id.imgDelete);
        final ImageButton imgBtnReset = (ImageButton) dialogView.findViewById(R.id.imgReset);
        Button btnNext = (Button) dialogView.findViewById(R.id.btnNext);

        btnNext.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (edtBin.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please input bin first", Toast.LENGTH_LONG).show();
                } else {
                    a.dismiss();
                    Helper.setItemParam(Constants.BIN, edtBin.getText().toString());
                    Intent intent = new Intent(getApplicationContext(), StockTakeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });
        imgBtnClose.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                onDone(st);
                a.dismiss();
            }
        });
        imgBtnReset.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                edtBin.setText("");
            }
        });
        a.show();
    }
}
