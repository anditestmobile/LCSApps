package id.co.lcs.apps.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import id.co.lcs.apps.adapter.GoodReceiptAdapter;
import id.co.lcs.apps.adapter.QuotationAdapter;
import id.co.lcs.apps.adapter.QuotationHistoryAdapter;
import id.co.lcs.apps.constants.Constants;
import id.co.lcs.apps.databinding.ActivityGoodReceiptBinding;
import id.co.lcs.apps.databinding.ActivityQuotationHistoryBinding;
import id.co.lcs.apps.helper.Helper;
import id.co.lcs.apps.model.GRDetailResponse;
import id.co.lcs.apps.model.GRRequest;
import id.co.lcs.apps.model.GRResponse;
import id.co.lcs.apps.model.GoodReceipt;
import id.co.lcs.apps.model.GoodReceiptDetail;
import id.co.lcs.apps.model.QuotationRequest;
import id.co.lcs.apps.model.QuotationResponse;
import id.co.lcs.apps.model.SalesOrderDetails;
import id.co.lcs.apps.model.SalesOrderRequest;

public class QuotationHistoryActivity extends BaseActivity {
    private ActivityQuotationHistoryBinding binding;
    private QuotationHistoryAdapter adapter;
    private SalesOrderRequest quotation;
    private List<SalesOrderRequest> quotationList;
    private int PARAM = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuotationHistoryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        init();
        initialize();
    }

    private void initialize() {
        quotationList = new ArrayList<>();

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
    }

    private void getData() {
        PARAM = 0;
        new RequestUrl().execute();
        getProgressDialog().show();
    }

//    private void setDetail() {
//        grDetailList = new ArrayList<>();
//        grDetailList.add(new GoodReceiptDetail("01","CARACTERE, GIFTBOXED Cup X2, Nutmeg, 13,5 x 7 x 7 cm - 8cl",5,0,0));
//        grDetailList.add(new GoodReceiptDetail("02","CARACTERE, Gourmet Plate, Cardamom, Ø 14 cm - H. 5 cm - 35 cl",3,0,0));
//        grDetailList.add(new GoodReceiptDetail("03","CARACTERE, Plate, White, Ø 30cm-H 2cm",6,0,0));
//        grDetailList.add(new GoodReceiptDetail("04","CARACTERE, Saucer, Cinnamon, 13.5 x 8.3 x 1.2cm",1,0,0));
//    }

    private void filter(String text) {
        ArrayList<SalesOrderRequest> filteredList = new ArrayList<>();
        for (SalesOrderRequest item : quotationList) {
            if (item.getQuotation().toLowerCase().contains(text.toLowerCase())
                    || item.getDate().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.filterList(filteredList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        quotationList = new ArrayList<>();
        getData();

    }

    public void showDetail(SalesOrderRequest data) {
        quotation = data;
        Helper.setItemParam(Constants.QUOTATION_HISTORY, quotation);
        Intent intent = new Intent(getApplicationContext(), QuotationActivity.class);
        startActivity(intent);
    }

    private class RequestUrl extends AsyncTask<Void, Void, QuotationResponse> {

        @Override
        protected QuotationResponse doInBackground(Void... voids) {
            try {
                if (PARAM == 0) {
                    String URL_QUO = Constants.API_PREFIX + Constants.API_GET_QUOTATION;
                    final String url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_QUO);
                    QuotationRequest c = new QuotationRequest();
                    c.setQuotation("");
                    c.setDate("");
                    return (QuotationResponse) Helper.postWebservice(url, c, QuotationResponse.class);
                }else{
//                    String URL_GR = Constants.API_PREFIX + Constants.API_GR_PO_DETAILS;
//                    final String url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_GR);
//                    GRDetailRequest c = new GRDetailRequest();
//                    c.setGrNo(goodReceipt.getGRNumber());
//                    grDetail = (GRDetailResponse) Helper.postWebservice(url, c, GRDetailResponse.class);
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
        protected void onPostExecute(QuotationResponse quo) {
            if (PARAM == 0) {
                getProgressDialog().dismiss();
                if(quo != null){
                    if(quo.getStatusCode() == 1) {
                        quotationList = new ArrayList<>();
                        quotationList = quo.getResponseData();
                        setData();
                    }else{
                        Toast.makeText(getApplicationContext(), quo.getStatusMessage(), Toast.LENGTH_SHORT).show();
                        binding.emptyLayout.setVisibility(View.VISIBLE);
                        binding.clGR.setVisibility(View.GONE);
                    }
                } else {
                    binding.emptyLayout.setVisibility(View.VISIBLE);
                    binding.clGR.setVisibility(View.GONE);
                    if (Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR) != null) {
                        Toast.makeText(getApplicationContext(), Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR).toString(), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), quo.getStatusMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }else{
//                getProgressDialog().dismiss();
//                if(grDetail != null){
//                    if(grDetail.getStatusCode() == 1) {
//                        goodReceipt.setGrDetail(grDetail.getResponseData());
//                        Helper.setItemParam(Constants.LIST_DETAIL_SUPPLIER_GR, goodReceipt);
//                        Intent intent = new Intent(getApplicationContext(), GoodReceiptDetailActivity.class);
//                        startActivity(intent);
//                    }else{
//                        Toast.makeText(getApplicationContext(), grDetail.getStatusMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    if (Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR) != null) {
//                        Toast.makeText(getApplicationContext(), Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR).toString(), Toast.LENGTH_SHORT).show();
//                    }else{
//                        Toast.makeText(getApplicationContext(), gr.getStatusMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }
            }
        }

    }

    private void setData() {
//        for(SalesOrderRequest quo : quotationList){
//            gr.setTrNo(gr.getGrDetail().get(0).getDocNum());
//            for(SalesOrderDetails quoDetail : quo.getSoItem()){
//                quoDetail.setGrQty(grDetail.getQty());
//                quoDetail.setChecked(false);
//                quoDetail.setExpand(false);
//            }
//            quo.setTotalCheckBox(0);
//        }
        binding.emptyLayout.setVisibility(View.GONE);
        binding.clGR.setVisibility(View.VISIBLE);
        adapter = new QuotationHistoryAdapter(QuotationHistoryActivity.this,quotationList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(QuotationHistoryActivity.this);

        binding.rcGR.setLayoutManager(layoutManager);

        binding.rcGR.setAdapter(adapter);
    }

}