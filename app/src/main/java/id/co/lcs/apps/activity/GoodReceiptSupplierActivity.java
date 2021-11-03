package id.co.lcs.apps.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import id.co.lcs.apps.adapter.GoodReceiptSupplierAdapter;
import id.co.lcs.apps.constants.Constants;
import id.co.lcs.apps.databinding.ActivityGoodReceiptSupplierBinding;
import id.co.lcs.apps.helper.Helper;
import id.co.lcs.apps.model.GRDetailResponse;
import id.co.lcs.apps.model.GRSupplierRequest;
import id.co.lcs.apps.model.GRSupplierResponse;
import id.co.lcs.apps.model.GoodReceiptDetail;
import id.co.lcs.apps.model.GoodReceiptSupplier;
import id.co.lcs.apps.model.GoodReceiptSupplierDetail;

public class GoodReceiptSupplierActivity extends BaseActivity {
    private ActivityGoodReceiptSupplierBinding binding;
    private GoodReceiptSupplierAdapter adapter;
    private GoodReceiptSupplier goodReceipt;
    private List<GoodReceiptSupplier> grList;
    private List<GoodReceiptSupplierDetail> grSupplierDetails;
    private int PARAM = 0;
    private GRDetailResponse grDetail = new GRDetailResponse();
    private String poNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGoodReceiptSupplierBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        init();
        initialize();
    }

    private void initialize() {
        binding.txtWH.setText(Helper.getItemParam(Constants.WAREHOUSE).toString());
        grList = new ArrayList<>();
//        getData();


        binding.edtSearchGR.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.inputPO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        if(Helper.getItemParam(Constants.PO_NUMBER) == null) {
            showDialog();
        }
    }

    private void showDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(GoodReceiptSupplierActivity.this);

        final EditText input = new EditText(GoodReceiptSupplierActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setHint("Input PO Number here...");
        lp.setMargins(5,5,5,5);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        poNumber = input.getText().toString();
                        if (poNumber.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "PO Number cannot empty", Toast.LENGTH_SHORT).show();
                        }else{
                            Helper.setItemParam(Constants.PO_NUMBER, poNumber);
                            getData();
                        }
                    }
                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }


    private void getData() {
        PARAM = 0;
        new RequestUrl().execute();
        getProgressDialog().show();
    }

    private void filter(String text) {
        ArrayList<GoodReceiptSupplier> filteredList = new ArrayList<>();
        for (GoodReceiptSupplier item : grList) {
            if (item.getCardCode().toLowerCase().contains(text.toLowerCase())
                    || item.getPoNumber().toLowerCase().contains(text.toLowerCase())
                    || item.getDocDate().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.filterList(filteredList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Helper.getItemParam(Constants.PO_NUMBER) != null) {
            poNumber = Helper.getItemParam(Constants.PO_NUMBER).toString();
            getData();
        }
    }

    public void showDetail(GoodReceiptSupplier data) {
        goodReceipt = data;
        Helper.setItemParam(Constants.LIST_DETAIL_SUPPLIER_GR, goodReceipt);
        Intent intent = new Intent(getApplicationContext(), GoodReceiptSupplierDetailActivity.class);
        startActivity(intent);
//        PARAM = 1;
//        new RequestUrl().execute();
//        getProgressDialog().show();
    }

    private class RequestUrl extends AsyncTask<Void, Void, GRSupplierResponse> {

        @Override
        protected GRSupplierResponse doInBackground(Void... voids) {
            try {
                if (PARAM == 0) {
                    String URL_GR_SUPPLIERS = Constants.API_PREFIX + Constants.API_GR_PO_SUPPLIERS;
                    final String url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_GR_SUPPLIERS);
                    GRSupplierRequest c = new GRSupplierRequest();
                    c.setDocNum(poNumber);
                    return (GRSupplierResponse) Helper.postWebservice(url, c, GRSupplierResponse.class);
                }else{
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
        protected void onPostExecute(GRSupplierResponse gr) {
            if (PARAM == 0) {
                getProgressDialog().dismiss();
                if(gr != null){
                    if(gr.getStatusCode() == 1) {
                        grList = new ArrayList<>();
                        grList = gr.getResponseData();
                        setData();
                    }else{
                        Toast.makeText(getApplicationContext(), gr.getStatusMessage(), Toast.LENGTH_SHORT).show();
                        binding.emptyLayout.setVisibility(View.VISIBLE);
                        binding.clGR.setVisibility(View.GONE);
                    }
                } else {
                    binding.emptyLayout.setVisibility(View.VISIBLE);
                    binding.clGR.setVisibility(View.GONE);
                    if (Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR) != null) {
                        Toast.makeText(getApplicationContext(), Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR).toString(), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), gr.getStatusMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }else{

            }
        }

    }

    private void setData() {
        for(GoodReceiptSupplier gr : grList){
            gr.setPoNumber(poNumber);
            gr.setTotalCheckBox(0);
            for(GoodReceiptSupplierDetail grDetail : gr.getGrSupplierDetail()){
                grDetail.setGrQty(grDetail.getQty());
                grDetail.setChecked(false);
            }
        }
        binding.emptyLayout.setVisibility(View.GONE);
        binding.clGR.setVisibility(View.VISIBLE);
        adapter = new GoodReceiptSupplierAdapter(GoodReceiptSupplierActivity.this,grList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(GoodReceiptSupplierActivity.this);

        binding.rcGR.setLayoutManager(layoutManager);

        binding.rcGR.setAdapter(adapter);
    }

}