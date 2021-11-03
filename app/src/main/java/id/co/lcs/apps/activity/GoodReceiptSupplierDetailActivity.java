package id.co.lcs.apps.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import id.co.lcs.apps.R;
import id.co.lcs.apps.adapter.GoodReceiptDetailSupplierAdapter;
import id.co.lcs.apps.constants.Constants;
import id.co.lcs.apps.databinding.ActivityGoodReceiptSupplierDetailBinding;
import id.co.lcs.apps.helper.Helper;
import id.co.lcs.apps.model.Batch;
import id.co.lcs.apps.model.GRPOPostingDetail;
import id.co.lcs.apps.model.GRPOPostingRequest;
import id.co.lcs.apps.model.GRPostingResponse;
import id.co.lcs.apps.model.GoodReceipt;
import id.co.lcs.apps.model.GoodReceiptDetail;
import id.co.lcs.apps.model.GoodReceiptSupplier;
import id.co.lcs.apps.model.GoodReceiptSupplierDetail;
import id.co.lcs.apps.model.SerialNumber;
import id.co.lcs.apps.model.SerialNumberInternal;
import id.co.lcs.apps.service.SessionManager;
import id.co.lcs.apps.utils.Utils;

public class GoodReceiptSupplierDetailActivity extends BaseActivity {
    private ActivityGoodReceiptSupplierDetailBinding binding;
    private GoodReceiptDetailSupplierAdapter adapter;
    private GoodReceiptSupplier gr;
    private List<GoodReceiptSupplierDetail> grDetailList;
    private BottomSheetDialog bottomSheetDialog;
    private int PARAM = 0;
    private GRPOPostingRequest request = new GRPOPostingRequest();
    private EditText edtVendor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGoodReceiptSupplierDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        init();
        initialize();
    }

    private void initialize() {
        gr = (GoodReceiptSupplier) Helper.getItemParam(Constants.LIST_DETAIL_SUPPLIER_GR);
        grDetailList = gr.getGrSupplierDetail();

        adapter = new GoodReceiptDetailSupplierAdapter(GoodReceiptSupplierDetailActivity.this, grDetailList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(GoodReceiptSupplierDetailActivity.this);

        binding.rcGR.setLayoutManager(layoutManager);

        binding.rcGR.setAdapter(adapter);
        binding.txtVendorNo.setText(gr.getCardCode());
        binding.txtVendorName.setText(gr.getCardName());
        binding.txtPONumber.setText(gr.getPoNumber() == null ? "-" : gr.getPoNumber());
        binding.txtDocDate.setText(gr.getDocDate());
        binding.txtWH.setText(Helper.getItemParam(Constants.WAREHOUSE).toString());
        binding.txtTotalLItems.setText(String.valueOf(gr.getGrSupplierDetail().size()));
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
                showDialogCustomer();
            }
        });
    }

    public void showDialogConfirmation() {
        if (bottomSheetDialog == null || !bottomSheetDialog.isShowing()) {
            bottomSheetDialog = Utils.showBottomSheetDialog(GoodReceiptSupplierDetailActivity.this, R.layout.bsheet_confirmation, (itemView, bottomSheet) -> {
                TextView txtTitle = itemView.findViewById(R.id.labelTitle);
                Button btnNo = itemView.findViewById(R.id.btnSkip);
                Button btnYes = itemView.findViewById(R.id.btnEmail);
                int i = 0;
                for (GoodReceiptSupplierDetail detail : gr.getGrSupplierDetail()) {
                    if (detail.isChecked()) {
                        i++;
                    }
                }
                gr.setTotalCheckBox(i);
                txtTitle.setText("Total check items : " + String.valueOf(gr.getTotalCheckBox())
                        + "\nTotal line items : " + String.valueOf(gr.getGrSupplierDetail().size())
                        + "\nAre you sure to proceed this Goods Receipt?");
                btnYes.setText("Yes");

                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean flag = checkBatchnSN();
                        if (flag) {
                            request.setCardCode(gr.getCardCode());
                            request.setCardName(gr.getCardName());
                            request.setNumAtCard(edtVendor.getText().toString());
//                            request.setDocDate(Helper.changeFormatDate("dd-MM-yyyy", "yyyyMMdd", gr.getDocDate()));
                            request.setDocDate(Helper.todayDate1("yyyyMMdd"));
                            request.setDocDueDate(Helper.changeFormatDate("dd-MM-yyyy", "yyyyMMdd", gr.getDocDueDate()));
                            int FLAG = 1;
                            List<GRPOPostingDetail> listTemp = new ArrayList<>();
                            GRPOPostingDetail temp = new GRPOPostingDetail();
                            for (GoodReceiptSupplierDetail i : gr.getGrSupplierDetail()) {
                                if (!i.getQty().trim().isEmpty()) {
                                    if (Integer.parseInt(i.getQty()) == 0 || !i.isChecked()) {
                                        continue;
                                    }
                                    temp = new GRPOPostingDetail();
                                    if (!i.getItemType().equals("None")) {
                                        if (i.getItemType().equals("Batch")) {
                                            if (i.getBatch() != null && i.getBatch().size() != 0) {
                                                temp.setBatch(i.getBatch());
                                            } else {
                                                FLAG = 2;
                                                break;
                                            }
                                        } else {
                                            if (i.getSerialNo() != null && i.getSerialNo().size() != 0) {
                                                temp.setSerialNo(i.getSerialNo());
                                            } else {
                                                FLAG = 2;
                                                break;
                                            }
                                        }
                                    }
                                    temp.setItem(i.getItem());
                                    temp.setItemNumber(i.getItemNumber());
                                    temp.setDocEntry(i.getDocEntry());
                                    temp.setLineNum(i.getLineNum());
                                    temp.setObjectType(i.getObjectType());
                                    temp.setQty(Double.parseDouble(i.getQty()) - i.getRejectedQty() - i.getShortageQty());
                                    listTemp.add(temp);
                                } else {
                                    FLAG = 0;
                                    break;
                                }
                            }
//                        request.setGrPODetails(gr.getGrSupplierDetail());
                            if (FLAG == 1) {
                                if (listTemp != null && listTemp.size() != 0) {
                                    request.setGrPODetails(listTemp);
                                    PARAM = 0;
                                    new RequestUrl().execute();
                                    getProgressDialog().show();
                                } else {
                                    bottomSheetDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "All quantity is 0", Toast.LENGTH_SHORT).show();
                                }
                            } else if (FLAG == 0) {
                                bottomSheetDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Quantity cannot empty or must be number", Toast.LENGTH_SHORT).show();
                            } else {
                                bottomSheetDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Serial/Batch Number cannot empty", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            bottomSheetDialog.dismiss();
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

    private boolean checkBatchnSN() {
        boolean result = true;
        for (GoodReceiptSupplierDetail temp : grDetailList) {
            if (temp.getItemType().equals("Batch") && temp.isChecked()) {
                if (temp.getBatch() == null || temp.getBatch().size() == 0) {
                    Toast.makeText(getApplicationContext(), "Batch Number cannot empty", Toast.LENGTH_LONG).show();
                    result = false;
                    break;
                } else if (temp.getBatch().get(0).getBatch().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Batch Number cannot empty", Toast.LENGTH_LONG).show();
                    result = false;
                    break;
                }
            } else if (temp.getItemType().equals("Serial") && temp.isChecked()) {
                if (temp.getSerialNo() == null || temp.getSerialNo().size() == 0) {
                    Toast.makeText(getApplicationContext(), "Serial Number cannot empty", Toast.LENGTH_LONG).show();
                    result = false;
                    break;
                } else {
                    for (SerialNumber sn : temp.getSerialNo()) {
                        if (sn.getSerialNo().trim().isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Serial Number cannot empty", Toast.LENGTH_LONG).show();
                            result = false;
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }

    private void filter(String text) {
        ArrayList<GoodReceiptSupplierDetail> filteredList = new ArrayList<>();
        for (GoodReceiptSupplierDetail item : grDetailList) {
            if (item.getItem().toLowerCase().contains(text.toLowerCase())
                    || item.getItemNumber().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.filterList(filteredList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Helper.getItemParam(Constants.POSITION_GR) != null) {
            Helper.removeItemParam(Constants.POSITION_GR);
            gr = (GoodReceiptSupplier) Helper.getItemParam(Constants.LIST_DETAIL_SUPPLIER_GR);
            grDetailList = gr.getGrSupplierDetail();
//            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(GoodReceiptSupplierDetailActivity.this)
                .setMessage("Are you sure you want to leave, you will lose your data?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(GoodReceiptSupplierDetailActivity.this, GoodReceiptSupplierActivity.class);
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

    public void changeRejectedQTY(int position, double qty) {
        gr.getGrSupplierDetail().get(position).setRejectedQty(qty);
    }

    public void changeShortageQTY(int position, double qty) {
        gr.getGrSupplierDetail().get(position).setShortageQty(qty);
    }

    public void changeBatch(GoodReceiptSupplierDetail data, String toString) {
        int position = 0;
        for(GoodReceiptSupplierDetail temp : gr.getGrSupplierDetail()){
            if(temp.getDocEntry().equals(data.getDocEntry()) && temp.getItemNumber().equals(data.getItemNumber())){
                break;
            }
            position++;
        }
        List<Batch> temp = new ArrayList<>();
        temp.add(new Batch(toString, gr.getGrSupplierDetail().get(position).getQty()));
        gr.getGrSupplierDetail().get(position).setBatch(temp);
    }

    public void changeSN(int position) {
        Helper.setItemParam(Constants.POSITION_GR, String.valueOf(position));
        Intent intent = new Intent(getApplicationContext(), SerialNumberActivity.class);
        startActivity(intent);
    }

    public void changeQTY(String qty, GoodReceiptSupplierDetail grSupplierDetail) {
        if (qty.isEmpty()) {
            qty = "0";
        }
        int position = 0;
        for (GoodReceiptSupplierDetail temp : gr.getGrSupplierDetail()) {
            if (temp.getItemNumber().equals(grSupplierDetail.getItemNumber()) && temp.getLineNum().equals(grSupplierDetail.getLineNum())) {
                break;
            }
            position++;
        }
        int actualQty = (int) Double.parseDouble(gr.getGrSupplierDetail().get(position).getGrQty());
        int grQty = (int) Double.parseDouble(qty);
            gr.getGrSupplierDetail().get(position).setQty(qty);
            if (gr.getGrSupplierDetail().get(position).getItemType().equals("Batch")) {
                if (gr.getGrSupplierDetail().get(position).getBatch() != null && gr.getGrSupplierDetail().get(position).getBatch().size() != 0) {
                    gr.getGrSupplierDetail().get(position).getBatch().get(0).setQty(qty);
                }
            } else if (gr.getGrSupplierDetail().get(position).getItemType().equals("Serial")) {
                if (gr.getGrSupplierDetail().get(position).getSerialNo() != null && gr.getGrSupplierDetail().get(position).getSerialNo().size() != 0) {
                    if (!gr.getGrSupplierDetail().get(position).getQty().equals(qty)) {
                        gr.getGrSupplierDetail().get(position).setSerialNo(null);
                        grDetailList = gr.getGrSupplierDetail();
                    }
                }
            }
    }

    public void changeCheckLineItems(boolean isChecked) {
        if (isChecked) {
            gr.setTotalCheckBox(gr.getTotalCheckBox() + 1);
        } else {
            gr.setTotalCheckBox(gr.getTotalCheckBox() - 1);
        }
    }

    private class RequestUrl extends AsyncTask<Void, Void, GRPostingResponse> {

        @Override
        protected GRPostingResponse doInBackground(Void... voids) {
            try {
                if (PARAM == 0) {
                    String URL_GR_PO_POSTING = Constants.API_PREFIX + Constants.API_GR_PO_POSTING;
                    final String url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_GR_PO_POSTING);
                    return (GRPostingResponse) Helper.postWebservice(url, request, GRPostingResponse.class);
                } else {
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
                        Toast.makeText(getApplicationContext(), result.getStatusMessage() + ", " + result.getResponseData().getErrorMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR) != null) {
                        Toast.makeText(getApplicationContext(), Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR).toString(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), result.getStatusMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            } else {

            }
        }

    }

    private void onDone(GRPostingResponse result) {
        new AlertDialog.Builder(GoodReceiptSupplierDetailActivity.this)
                .setCancelable(false)
                .setMessage(result.getStatusMessage() + ", Document Number : " + result.getResponseData().getDocNum())
                .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).show();
    }

    public void showDialogCustomer() {
        if (bottomSheetDialog == null || !bottomSheetDialog.isShowing()) {
            bottomSheetDialog = Utils.showBottomSheetDialog(this, R.layout.bsheet_vendor, (itemView, bottomSheet) -> {
                Button btnNo = itemView.findViewById(R.id.btnSkip);
                Button btnYes = itemView.findViewById(R.id.btnEmail);
                edtVendor = itemView.findViewById(R.id.edtVendor);

//                txtTitle.setText("Are you sure want to checkout?\nSales order confirmation will be emailed to you.");
                btnYes.setText("Confirm");

                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (edtVendor.getText().toString().isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Please fill Vendor Ref No.", Toast.LENGTH_SHORT).show();
                        } else {
                            bottomSheet.dismiss();
                            showDialogConfirmation();
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
}