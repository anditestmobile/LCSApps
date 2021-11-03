package id.co.lcs.apps.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

import id.co.lcs.apps.R;
import id.co.lcs.apps.adapter.GoodReceiptDetailAdapter;
import id.co.lcs.apps.constants.Constants;
import id.co.lcs.apps.databinding.ActivityGoodReceiptDetailBinding;
import id.co.lcs.apps.helper.Helper;
import id.co.lcs.apps.model.Batch;
import id.co.lcs.apps.model.BatchInternal;
import id.co.lcs.apps.model.GRPostingResponse;
import id.co.lcs.apps.model.GRTRPostingDetail;
import id.co.lcs.apps.model.GRTRPostingRequest;
import id.co.lcs.apps.model.GoodReceipt;
import id.co.lcs.apps.model.GoodReceiptDetail;
import id.co.lcs.apps.model.SerialAndBatchRequest;
import id.co.lcs.apps.model.SerialAndBatchResponse;
import id.co.lcs.apps.model.SerialNumber;
import id.co.lcs.apps.model.SerialNumberInternal;
import id.co.lcs.apps.utils.Utils;

public class GoodReceiptDetailActivity extends BaseActivity {
    private ActivityGoodReceiptDetailBinding binding;
    private GoodReceiptDetailAdapter adapter;
    private GoodReceipt gr;
    private List<GoodReceiptDetail> grDetailList;
    private BottomSheetDialog bottomSheetDialog;
    private int PARAM = 0;
    private GRTRPostingRequest request = new GRTRPostingRequest();
    private SerialAndBatchResponse sbResponse = new SerialAndBatchResponse();
    private SerialAndBatchRequest sbReq = new SerialAndBatchRequest();
    private int pos = 0;
    private int posBatch = 0;
    private String chooseBatch;
    private int posChooseSN = 0;
    private List<SerialNumberInternal> serialNumberList = new ArrayList<>();
    private List<Integer> posSN = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGoodReceiptDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        init();
        initialize();
    }

    private void initialize() {
        gr = (GoodReceipt) Helper.getItemParam(Constants.LIST_DETAIL_GR);
        grDetailList = gr.getGrDetail();
        adapter = new GoodReceiptDetailAdapter(GoodReceiptDetailActivity.this, grDetailList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(GoodReceiptDetailActivity.this);

        binding.rcGR.setLayoutManager(layoutManager);

        binding.rcGR.setAdapter(adapter);
        binding.txtTRNo.setText(gr.getTrNo());
        binding.txtFromWH.setText(gr.getFromWh());
        binding.txtToWH.setText(gr.getToWh());
        binding.txtTotalLItems.setText(String.valueOf(gr.getGrDetail().size()));
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
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogConfirmation();
            }
        });
    }

    public void getDataBatchOrSerial(GoodReceiptDetail details) {
        if(details.getItemType().equals("Batch")) {
            for (int i = 0; i < grDetailList.size(); i++) {
                if (grDetailList.get(i).getItemNumber().equals(details.getItemNumber()) && grDetailList.get(i).getDocNum().equals(details.getDocNum())) {
                    posBatch = i;
                }
            }
            int total = Integer.parseInt(grDetailList.get(posBatch).getQty());
            int totalTemp = 0;
            if (grDetailList.get(posBatch).getBatchInternals() != null && grDetailList.get(posBatch).getBatchInternals().size() != 0) {
                for (BatchInternal bi : grDetailList.get(posBatch).getBatchInternals()) {
                    totalTemp += Integer.parseInt(bi.getBatchQty());
                }
                if (totalTemp == total) {
                    Toast.makeText(getApplicationContext(), "Batch quantity already maximum", Toast.LENGTH_SHORT).show();
                } else {
                    checkDataBatchorSerial(grDetailList.get(posBatch));
                }
            } else {
                checkDataBatchorSerial(grDetailList.get(posBatch));
            }
        }else{
            for (int i = 0; i < grDetailList.size(); i++) {
                if (grDetailList.get(i).getItemNumber().equals(details.getItemNumber()) && grDetailList.get(i).getDocNum().equals(details.getDocNum())) {
                    pos = i;
                }
            }
            int total = Integer.parseInt(grDetailList.get(pos).getQty());
            int totalTemp = 0;
            if (grDetailList.get(pos).getBatchInternals() != null && grDetailList.get(pos).getBatchInternals().size() != 0) {
                for (BatchInternal bi : grDetailList.get(pos).getBatchInternals()) {
                    totalTemp += Integer.parseInt(bi.getBatchQty());
                }
                if (totalTemp == total) {
                    Toast.makeText(getApplicationContext(), "Batch quantity already maximum", Toast.LENGTH_SHORT).show();
                } else {
                    checkDataBatchorSerial(grDetailList.get(pos));
                }
            } else {
                checkDataBatchorSerial(grDetailList.get(pos));
            }
        }
    }

    public void checkDataBatchorSerial(GoodReceiptDetail details){
        sbReq = new SerialAndBatchRequest();
        sbReq.setItemCode(details.getItemNumber());
        sbReq.setItemType(details.getItemType());
        sbReq.setWhsCode(gr.getFromWh());
        PARAM = 1;
        new RequestUrl().execute();
        getProgressDialog().show();
    }

    public void showDialogConfirmation() {
        if (bottomSheetDialog == null || !bottomSheetDialog.isShowing()) {
            bottomSheetDialog = Utils.showBottomSheetDialog(GoodReceiptDetailActivity.this, R.layout.bsheet_confirmation, (itemView, bottomSheet) -> {
                TextView txtTitle = itemView.findViewById(R.id.labelTitle);
                Button btnNo = itemView.findViewById(R.id.btnSkip);
                Button btnYes = itemView.findViewById(R.id.btnEmail);
                int i=0;
                for(GoodReceiptDetail detail : gr.getGrDetail()){
                    if(detail.isChecked()){
                        i++;
                    }
                }
                gr.setTotalCheckBox(i);
                txtTitle.setText("Total check items : " + String.valueOf(gr.getTotalCheckBox())
                        + "\nTotal line items : " + String.valueOf(gr.getGrDetail().size())
                        + "\nAre you sure to proceed this Goods Receipt?");

                btnYes.setText("Submit");

                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        request.setDocNum(gr.getTrNo());
                        request.setFromWH(gr.getFromWh());
                        request.setToWH(gr.getToWh());
                        request.setDocDate(Helper.changeFormatDate("dd-MM-yyyy", "yyyyMMdd", gr.getDocDate()));
                        request.setDocDueDate(Helper.changeFormatDate("dd-MM-yyyy", "yyyyMMdd", gr.getDocDueDate()));
                        int FLAG = 1;
                        List<GRTRPostingDetail> listTemp = new ArrayList<>();
                        GRTRPostingDetail temp = new GRTRPostingDetail();
                        for (GoodReceiptDetail grTemp : gr.getGrDetail()) {
                            if (!grTemp.getQty().trim().isEmpty()) {
                                if(Integer.parseInt(grTemp.getQty()) == 0 || !grTemp.isChecked()){
                                    continue;
                                }
                                temp = new GRTRPostingDetail();
                                if (!grTemp.getItemType().equals("None")) {
                                    if (grTemp.getItemType().equals("Batch")) {
                                        if (grTemp.getBatchInternals() != null && grTemp.getBatchInternals().size() != 0) {
                                            for (BatchInternal bi : grDetailList.get(posBatch).getBatchInternals()) {
                                                if(bi.getBatchQty().equals("0")) {
                                                    FLAG = 3;
                                                    break;
                                                }
                                            }
                                            if(FLAG != 3) {
                                                List<Batch> listBatch = new ArrayList<>();
                                                Batch b = new Batch(grTemp.getBatchInternals().get(0).getBatch(), grTemp.getQty());
                                                listBatch.add(b);
                                                temp.setBatch(listBatch);
                                            }
                                        } else {
                                            FLAG = 2;
                                            break;
                                        }
                                    } else {
                                        List<SerialNumber> listSN = new ArrayList<>();
                                        if (grTemp.getSerialNumberInternals() != null && grTemp.getSerialNumberInternals().size() != 0) {
                                            for (SerialNumberInternal si : grTemp.getSerialNumberInternals()) {
                                                SerialNumber sn = new SerialNumber(si.getSerialNo(), String.valueOf(1));
                                                listSN.add(sn);
                                            }
                                        } else {
                                            FLAG = 2;
                                            break;
                                        }
                                        temp.setSerialNo(listSN);
                                    }
                                }
                                temp.setItem(grTemp.getItemName());
                                temp.setItemNumber(grTemp.getItemNumber());
                                temp.setFromWH(gr.getFromWh());
                                temp.setToWH(gr.getToWh());
                                temp.setDocEntry(grTemp.getDocEntry());
                                temp.setLineNum(grTemp.getLineNum());
                                temp.setObjectType(grTemp.getObjectType());
                                temp.setQty(Double.parseDouble(grTemp.getQty()));
                                listTemp.add(temp);
                            } else {
                                FLAG = 0;
                                break;
                            }
                        }

                        if (FLAG == 1) {
                            if(listTemp != null && listTemp.size() != 0) {
                                request.setGrTRDetails(listTemp);
                                PARAM = 0;
                                new RequestUrl().execute();
                                getProgressDialog().show();
                            }else{
                                bottomSheetDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "All quantity is 0", Toast.LENGTH_SHORT).show();
                            }
                        } else if (FLAG == 0) {
                            bottomSheetDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Quantity cannot empty or must be number", Toast.LENGTH_SHORT).show();
                        } else if(FLAG == 2) {
                            bottomSheetDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Serial/Batch Number cannot empty", Toast.LENGTH_SHORT).show();
                        }else {
                            bottomSheetDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Batch quantity cannot empty", Toast.LENGTH_SHORT).show();
                        }

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

    private void filter(String text) {
        ArrayList<GoodReceiptDetail> filteredList = new ArrayList<>();
        for (GoodReceiptDetail item : grDetailList) {
            if (item.getItemName().toLowerCase().contains(text.toLowerCase())
                    || item.getItemNumber().toLowerCase().contains(text.toLowerCase())
                    || item.getBarcode().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
        }
        adapter.filterList(filteredList);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void changeQTY(String qty, GoodReceiptDetail grDetail) {
        if (qty.isEmpty()) {
            qty = "0";
        }
        int position = 0;
        for(GoodReceiptDetail temp : gr.getGrDetail()){
            if(temp.getDocNum().equals(grDetail.getDocNum()) && temp.getItemNumber().equals(grDetail.getItemNumber())){
               break;
            }
            position++;
        }
        int actualQty = (int) Double.parseDouble(gr.getGrDetail().get(position).getGrQty());
        int grQty = (int) Double.parseDouble(qty);
        if (grQty <= actualQty) {
            if (gr.getGrDetail().get(position).getItemType().equals("Batch")) {
                if (gr.getGrDetail().get(position).getBatchInternals() != null && gr.getGrDetail().get(position).getBatchInternals().size() != 0) {
                    Toast.makeText(getApplicationContext(), "You need to clear all your batch first, before you change your quantity", Toast.LENGTH_SHORT).show();
                    adapter.notifyItemChanged(position);
                    closeKeyboard();
                }else{
                    gr.getGrDetail().get(position).setQty(qty);
                }
            } else if (gr.getGrDetail().get(position).getItemType().equals("Serial")) {
                if (gr.getGrDetail().get(position).getSerialNumberInternals() != null && gr.getGrDetail().get(position).getSerialNumberInternals().size() != 0) {
                    Toast.makeText(getApplicationContext(), "You need to clear all your batch first, before you change your quantity", Toast.LENGTH_SHORT).show();
                    adapter.notifyItemChanged(position);
                    closeKeyboard();
                }else{
                    gr.getGrDetail().get(position).setQty(qty);
                }
            } else {
                grDetailList = gr.getGrDetail();
                gr.getGrDetail().get(position).setQty(qty);
            }
        } else {
            Toast.makeText(getApplicationContext(), "GR Quantity cannot bigger then Actual Quantity(" + gr.getGrDetail().get(position).getGrQty() + ")", Toast.LENGTH_SHORT).show();
            if (gr.getGrDetail().get(position).getItemType().equals("Serial")) {
                if (gr.getGrDetail().get(position).getSerialNumberInternals() != null && gr.getGrDetail().get(position).getSerialNumberInternals().size() != 0) {
                    gr.getGrDetail().get(position).setSerialNumberInternals(null);
                    grDetailList = gr.getGrDetail();
                }
                gr.getGrDetail().get(position).setQty(gr.getGrDetail().get(position).getGrQty());
                adapter.notifyDataSetChanged();
            } else {
                if (gr.getGrDetail().get(position).getBatchInternals() != null && gr.getGrDetail().get(position).getBatchInternals().size() != 0) {
                    gr.getGrDetail().get(position).getBatchInternals().get(0).setQty(gr.getGrDetail().get(position).getGrQty());
                }
                gr.getGrDetail().get(position).setQty(gr.getGrDetail().get(position).getGrQty());
                adapter.notifyDataSetChanged();
            }
        }
    }

    public void changeBatchQTY(String qty, int batchPos, GoodReceiptDetail data) {
        if(!qty.equals("")){
            int pos = 0;
            for(GoodReceiptDetail temp : gr.getGrDetail()){
                if(temp.getDocNum().equals(data.getDocNum()) && temp.getItemNumber().equals(data.getItemNumber())){
                    break;
                }
                pos++;
            }
            grDetailList.get(pos).getBatchInternals().get(batchPos).setBatchQty(qty);
        }
    }

    public void deleteBatchQTY(int batchPos, GoodReceiptDetail data) {
        new AlertDialog.Builder(GoodReceiptDetailActivity.this)
                .setMessage("Are you sure to delete this batch no.?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int pos = 0;
                        for(GoodReceiptDetail temp : gr.getGrDetail()){
                            if(temp.getDocNum().equals(data.getDocNum()) && temp.getItemNumber().equals(data.getItemNumber())){
                                break;
                            }
                            pos++;
                        }
                        gr.getGrDetail().get(pos).getBatchInternals().remove(batchPos);
                        grDetailList = gr.getGrDetail();
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    public void deleteSerialQTY(int serialPos, GoodReceiptDetail data) {
        new AlertDialog.Builder(GoodReceiptDetailActivity.this)
                .setMessage("Are you sure to delete this serial no.?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int pos = 0;
                        for(GoodReceiptDetail temp : gr.getGrDetail()){
                            if(temp.getDocNum().equals(data.getDocNum()) && temp.getItemNumber().equals(data.getItemNumber())){
                                break;
                            }
                            pos++;
                        }
                        gr.getGrDetail().get(pos).getSerialNumberInternals().remove(serialPos);
                        grDetailList = gr.getGrDetail();
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    public void changeCheckLineItems(boolean isChecked) {
        if(isChecked){
            gr.setTotalCheckBox(gr.getTotalCheckBox() + 1);
        }else{
            gr.setTotalCheckBox(gr.getTotalCheckBox() - 1);
        }
    }

    private class RequestUrl extends AsyncTask<Void, Void, GRPostingResponse> {

        @Override
        protected GRPostingResponse doInBackground(Void... voids) {
            try {
                if (PARAM == 0) {
                    String URL_GR_INTERNAL_POSTING = Constants.API_PREFIX + Constants.API_GR_INTERNAL_POSTING;
                    final String url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_GR_INTERNAL_POSTING);
                    return (GRPostingResponse) Helper.postWebservice(url, request, GRPostingResponse.class);
                } else {
                    String API_GR_SN_BATCH_INTERNAL = Constants.API_PREFIX + Constants.API_GR_SN_BATCH_INTERNAL;
                    final String url = Helper.getItemParam(Constants.BASE_URL).toString().concat(API_GR_SN_BATCH_INTERNAL);
                    sbResponse = (SerialAndBatchResponse) Helper.postWebservice(url, sbReq, SerialAndBatchResponse.class);
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
        protected void onPostExecute(GRPostingResponse result) {
            if (PARAM == 0) {
                getProgressDialog().dismiss();
                bottomSheetDialog.dismiss();
                if (result != null) {
                    if (result.getStatusCode() == 1) {
                        onDone(result);
                    } else {
                        onFailed(result);
//                        Toast.makeText(getApplicationContext(), result.getStatusMessage() + ", " + result.getResponseData().getErrorMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR) != null) {
                        Toast.makeText(getApplicationContext(), Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR).toString(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), result.getStatusMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                getProgressDialog().dismiss();
                if (sbResponse != null) {
                    if (sbResponse.getStatusCode() == 1) {
                        if (sbReq.getItemType().equals("Batch")) {
                            setBatchDialog();
                        } else {
                            setSerialDialog();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), sbResponse.getStatusMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR) != null) {
                        Toast.makeText(getApplicationContext(), Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR).toString(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), sbResponse.getStatusMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

    }

    private void setSerialDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        ScrollView scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout layout = new LinearLayout(this);
        layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layout.setOrientation(LinearLayout.VERTICAL);

        int qty = (int) Double.parseDouble(gr.getGrDetail().get(pos).getQty());
        serialNumberList = new ArrayList<>();
        posSN = new ArrayList<>();
        for (int i = 0; i < qty; i++) {
            posSN.add(0);
            final MaterialSpinner input = new MaterialSpinner(this);
            input.setId(i);
            List<String> listSN = new ArrayList<>();
            for (SerialNumberInternal si : sbResponse.getResponseData().get(0).getSerialDetails()) {
                listSN.add(si.getSerialNo() + " - " + Helper.changeDateFomat("dd-MM-yyyy", "dd/MM/yyyy", si.getAdmDate()));
            }
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listSN);

            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            input.setAdapter(dataAdapter);

            if (grDetailList.get(pos).getSerialNumberInternals() != null && grDetailList.get(pos).getSerialNumberInternals().size() != 0) {
                for (int j = 0; j < listSN.size(); j++) {
                    if (grDetailList.get(pos).getSerialNumberInternals().get(i).getSerialNo().equals(listSN.get(j).split(" - ")[0])) {
                        input.setSelectedIndex(j);
                        posSN.set(input.getId(), j);
                        break;
                    }
                }
            }
            input.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

                @Override
                public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
//                        posChooseSN = position;
                    posSN.set(input.getId(), position);
                }
            });
            layout.addView(input);
//            }
        }
        scrollView.addView(layout);
        alertDialog.setView(scrollView);
        alertDialog.setTitle("Please Choose Serial Number");
        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        final AlertDialog dialog = alertDialog.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean FLAG = true;
                ArrayList<SerialNumberInternal> temp = new ArrayList<>();
                for (int i = 0; i < posSN.size(); i++) {
                    for (int j = 0; j < posSN.size(); j++) {
                        if (posSN.get(i) == posSN.get(j) && i != j) {
                            FLAG = false;
                            break;
                        }
                    }
                    if (FLAG) {
                        temp.add(sbResponse.getResponseData().get(0).getSerialDetails().get(posSN.get(i)));
                    } else {
                        break;
                    }
                }
                if (FLAG) {
                    gr.getGrDetail().get(pos).setSerialNumberInternals(temp);
                    grDetailList = gr.getGrDetail();
                    adapter.notifyItemChanged(pos);
                    dialog.dismiss();
                } else {
                    Toast.makeText(getApplicationContext(), "Serial Number cannot same, please choose different Serial Number", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setBatchDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        final MaterialSpinner input = new MaterialSpinner(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(5, 5, 5, 5);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        List<String> listBatch = new ArrayList<>();
        int flagQty = 0;
        for (BatchInternal bi : sbResponse.getResponseData().get(0).getBatchDetails()) {
            if (grDetailList.get(posBatch).getBatchInternals() != null && grDetailList.get(posBatch).getBatchInternals().size() != 0) {
                int FLAG = 0;
                for (BatchInternal temp : grDetailList.get(posBatch).getBatchInternals()) {
                    if(temp.getBatchQty().equals("0")) {
                        FLAG = 2;
                        break;
                    }else if(temp.getBatchQty().equals(grDetailList.get(posBatch).getGrQty())){
                        FLAG = 3;
                        break;
                    }else{
                        if (bi.getBatch().equals(temp.getBatch())) {
                            FLAG = 1;
                            break;
                        }
                    }
                }
                if (FLAG == 0) {
                    listBatch.add(bi.getBatch() + " - " + Helper.changeDateFomat("dd-MM-yyyy", "dd/MM/yyyy", bi.getAdmDate()));
                }else if(FLAG == 2){
                    flagQty = 1;
                    break;
                }else if(FLAG == 3){
                    flagQty = 2;
                }
            } else {
                listBatch.add(bi.getBatch() + " - " + Helper.changeDateFomat("dd-MM-yyyy", "dd/MM/yyyy", bi.getAdmDate()));
            }
        }
        if(flagQty == 0) {
            if (listBatch.size() != 0) {
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listBatch);

                // Drop down layout style - list view with radio button
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                input.setAdapter(dataAdapter);
                chooseBatch = listBatch.get(0);
                input.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

                    @Override
                    public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                        chooseBatch = item;
                    }
                });

                alertDialog.setTitle("Please Choose Batch Number");
                alertDialog.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ArrayList<BatchInternal> temp = gr.getGrDetail().get(posBatch).getBatchInternals();
                                if (temp == null) {
                                    temp = new ArrayList<>();
                                }
                                for (BatchInternal bi : sbResponse.getResponseData().get(0).getBatchDetails()) {
                                    String[] b = chooseBatch.split(" - ");
                                    if (b[0].equals(bi.getBatch()) && Helper.changeFormatDate("dd/MM/yyyy", "dd-MM-yyyy", b[1]).equals(bi.getAdmDate())) {
                                        temp.add(bi);
                                    }
                                }
                                gr.getGrDetail().get(posBatch).setBatchInternals(temp);
                                grDetailList = gr.getGrDetail();
                                adapter.notifyDataSetChanged();
                            }
                        });

                alertDialog.setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();
            } else {
                Toast.makeText(getApplicationContext(), "You already choose all batch", Toast.LENGTH_SHORT).show();
            }
        }else if(flagQty == 1){
            Toast.makeText(getApplicationContext(), "Batch quantity cannot zero", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), "Batch quantity already maximum", Toast.LENGTH_SHORT).show();
        }
    }

    private void onFailed(GRPostingResponse result) {
        new AlertDialog.Builder(GoodReceiptDetailActivity.this)
                .setCancelable(false)
                .setMessage(result.getStatusMessage() + ", " + result.getResponseData().getErrorMessage())
                .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    private void onDone(GRPostingResponse result) {
        new AlertDialog.Builder(GoodReceiptDetailActivity.this)
                .setCancelable(false)
                .setMessage(result.getStatusMessage() + ", Document Number : " + result.getResponseData().getDocNum())
                .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).show();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(GoodReceiptDetailActivity.this)
                .setMessage("Are you sure you want to leave, you will lose your data?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(GoodReceiptDetailActivity.this, GoodReceiptActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }
    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}