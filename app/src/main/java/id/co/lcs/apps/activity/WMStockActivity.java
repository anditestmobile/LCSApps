package id.co.lcs.apps.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import id.co.lcs.apps.R;
import id.co.lcs.apps.adapter.CommitedAdapter;
import id.co.lcs.apps.adapter.WMStockRecyclerAdapter;
import id.co.lcs.apps.constants.Constants;
import id.co.lcs.apps.databinding.ActivityWmstockBinding;
import id.co.lcs.apps.helper.Helper;
import id.co.lcs.apps.model.Category;
import id.co.lcs.apps.model.CommitedResponse;
import id.co.lcs.apps.model.ProductResponse;
import id.co.lcs.apps.model.SearchWithCategory;
import id.co.lcs.apps.model.StockDetailsReponse;
import id.co.lcs.apps.model.StockDetailsRequest;
import id.co.lcs.apps.model.TRStock;
import id.co.lcs.apps.model.WMDetailStock;
import id.co.lcs.apps.model.WMStock;

public class WMStockActivity extends BaseActivity {
    ActivityWmstockBinding binding;
    private WMStockRecyclerAdapter adapter = null;
    private List<WMStock> searchResults = new ArrayList<>();
    private List<WMDetailStock> detailStocks = new ArrayList<>();
    private RelativeLayout r2;
    private EditText edt;
    private int FLAG_SCAN = 1;
    private Button btnNext, btnScan;
    public int PARAM = 0;
    private String barcode = "" , itemCode = "";
    private CommitedResponse cr = new CommitedResponse();
    private String wh = null;
    private CommitedAdapter commitedAdapter = null;
    private String cmItemCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWmstockBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        init();
        initialize();
        showDialogScan();
    }

    private void initialize() {
        binding.imgScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogScan();
            }
        });
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
//        setData();
    }

    private void setData() {
        if(detailStocks != null && detailStocks.size() != 0) {
            binding.clSS.setVisibility(View.VISIBLE);
            binding.emptyLayout.setVisibility(View.GONE);
            binding.txtProductName.setText(searchResults.get(0).getDesc());
            binding.txtProductCode.setText(searchResults.get(0).getIdMaterial());
            binding.txtPrice.setText("S$"+searchResults.get(0).getPrice());
            adapter = new WMStockRecyclerAdapter(this, detailStocks);

            binding.rcSS.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(this);
            binding.rcSS.setLayoutManager(llm);
            binding.rcSS.setAdapter(adapter);
            binding.edtSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                    doSearch(binding.edtSearch.getText().toString().toLowerCase(Locale.getDefault()));
                }
            });
        }else{
            binding.clSS.setVisibility(View.GONE);
            binding.emptyLayout.setVisibility(View.VISIBLE);
        }

    }

    private void doSearch(String s) {
        if(detailStocks != null && detailStocks.size() != 0)
            filter(s);
    }

    private void filter(String charText) {
        ArrayList<WMDetailStock> filteredListStock = new ArrayList<>();
        for (WMDetailStock wp : detailStocks) {
            if (wp.getWhs().toLowerCase(Locale.getDefault()).contains(charText)) {
                filteredListStock.add(wp);
            }
        }
        adapter.filterList(filteredListStock);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Helper.getItemParam(Constants.POS_MENU) != null) {
            FLAG_SCAN = (int) Helper.getItemParam(Constants.POS_MENU);
            showDialog();
        }
    }


    public void showDialogScan() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_button, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog b = dialogBuilder.create();
        Button btnBarcode = (Button) dialogView.findViewById(R.id.btnBarcode);
        Button btnProductCode = (Button) dialogView.findViewById(R.id.btnProductCode);
        btnBarcode.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                FLAG_SCAN = 0;
                b.dismiss();
                showDialog();
            }
        });
        btnProductCode.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                FLAG_SCAN = 1;
                b.dismiss();
                showDialog();
            }
        });
        b.show();
    }

    public void showDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_scan, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog a = dialogBuilder.create();
        edt = (EditText) dialogView.findViewById(R.id.edtBatch);
        final ImageButton imgBtnClose = (ImageButton) dialogView.findViewById(R.id.imgDelete);
        final ImageButton imgBtnReset = (ImageButton) dialogView.findViewById(R.id.imgReset);
        btnNext = (Button) dialogView.findViewById(R.id.btnNext);
        btnScan = (Button) dialogView.findViewById(R.id.btnScan);
        if (FLAG_SCAN == 0) {
            edt.setHint(R.string.hint_strg_bin);
        } else {
            edt.setHint(R.string.hint_mat_num);
            btnScan.setVisibility(View.GONE);
        }


        if (Helper.getItemParam(Constants.BARCODERESULT) != null) {
            edt.setText(Helper.getItemParam(Constants.BARCODERESULT).toString());
            btnNext.setEnabled(true);
            setButton(btnNext, "NEXT", R.drawable.btn_blue);
        } else {
            btnNext.setEnabled(false);
            setButton(btnNext, "NEXT", R.drawable.btn_grey);
        }

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                a.dismiss();
                Helper.setItemParam(Constants.POS_MENU, FLAG_SCAN);
                Intent intent = new Intent(getApplicationContext(), ScanActivity.class);
                startActivity(intent);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (edt.getText().toString().equals("")) {
                    if (FLAG_SCAN == 0) {
                        Toast.makeText(getApplicationContext(), "Please Input/Scan Barcode first", Toast.LENGTH_LONG).show();
                    } else if (FLAG_SCAN == 1) {
                        Toast.makeText(getApplicationContext(), "Please Input/Scan Product Code first", Toast.LENGTH_LONG).show();
                    }
                } else {
                    a.dismiss();
                    Helper.removeItemParam(Constants.POS_MENU);
                    Helper.removeItemParam(Constants.BARCODERESULT);
                    if(FLAG_SCAN == 0){
                        barcode = edt.getText().toString();
                        itemCode = "";
                    }else{
                        itemCode = edt.getText().toString();
                        barcode = "";
                    }
                    PARAM = 0;
                    new RequestUrl().execute();
                    getProgressDialog().show();
//                    insertDummyData();
                }
            }
        });
        imgBtnClose.setOnClickListener(new View.OnClickListener()

        {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                a.dismiss();
            }
        });
        imgBtnReset.setOnClickListener(new View.OnClickListener()

        {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                edt.setText("");
            }
        });
        edt.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                if (!s.toString().trim().equals("")) {
                    btnNext.setEnabled(true);
                    setButton(btnNext, "NEXT", R.drawable.btn_blue);
                } else {
                    btnNext.setEnabled(false);
                    setButton(btnNext, "NEXT", R.drawable.btn_grey);
                }
            }
        });
        a.show();
    }

    public void showDetail(WMDetailStock data) {
        wh = data.getWhs();
        cmItemCode = searchResults.get(0).getIdMaterial();
        PARAM = 1;
        new RequestUrl().execute();
        getProgressDialog().show();
    }

    private class RequestUrl extends AsyncTask<Void, Void, StockDetailsReponse> {

        @Override
        protected StockDetailsReponse doInBackground(Void... voids) {
            try {
                if (PARAM == 0) {
                    String URL_STOCK_DETAILS = Constants.API_PREFIX + Constants.API_GET_STOCK;
                    final String url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_STOCK_DETAILS);
                    StockDetailsRequest sd = new StockDetailsRequest();
                    sd.setBarcode(barcode);
                    sd.setItemCode(itemCode);
//                    sd.setWh(Helper.getItemParam(Constants.WAREHOUSE).toString());
                    return (StockDetailsReponse) Helper.postWebservice(url, sd, StockDetailsReponse.class);
                }else{
                    String URL_COMMITED = Constants.API_PREFIX + Constants.API_GET_COMMITED_QTY;
                    final String url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_COMMITED);
                    StockDetailsRequest sd = new StockDetailsRequest();
                    sd.setWh(wh);
                    sd.setItemCode(cmItemCode);
                    cr = (CommitedResponse) Helper.postWebservice(url, sd, CommitedResponse.class);
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
        protected void onPostExecute(StockDetailsReponse stockDetails) {
            if (PARAM == 0) {
                getProgressDialog().dismiss();
                if(stockDetails != null){
                    if(stockDetails.getStatusCode() == 1) {
                        searchResults = new ArrayList<>();
                        searchResults = stockDetails.getResponseData();
                        detailStocks = searchResults.get(0).getDetailStocks();
                        setData();
                    }else{
                        Toast.makeText(getApplicationContext(), stockDetails.getStatusMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR) != null) {
                        Toast.makeText(getApplicationContext(), Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR).toString(), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), stockDetails.getStatusMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }else{
                getProgressDialog().dismiss();
                if(cr != null){
                    if(cr.getStatusCode() == 1) {
                        setDataCM();
                    }else{
                        Toast.makeText(getApplicationContext(), cr.getStatusMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR) != null) {
                        Toast.makeText(getApplicationContext(), Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR).toString(), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), cr.getStatusMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

    }

    private void setDataCM() {
        commitedAdapter = new CommitedAdapter(this, cr.getResponseData());
        CustomDialog customDialog = new CustomDialog(this, commitedAdapter);
        customDialog.show();
        customDialog.setCanceledOnTouchOutside(false);
    }

}
