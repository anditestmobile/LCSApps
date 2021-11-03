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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import id.co.lcs.apps.R;
import id.co.lcs.apps.adapter.TransferRequestAdapter;
import id.co.lcs.apps.constants.Constants;
import id.co.lcs.apps.databinding.ActivityTransferRequestBinding;
import id.co.lcs.apps.helper.Helper;
import id.co.lcs.apps.model.StockDetailsRequest;
import id.co.lcs.apps.model.StockTRReponse;
import id.co.lcs.apps.model.TPItem;
import id.co.lcs.apps.model.TRStock;
import id.co.lcs.apps.model.TransferPostingRequest;
import id.co.lcs.apps.model.WSMessageResponse;
import id.co.lcs.apps.model.WMStock;

public class TransferRequestActivity extends BaseActivity {
    ActivityTransferRequestBinding binding;
    private TransferRequestAdapter adapter;
    public List<TRStock> results = new ArrayList<>();
    private RelativeLayout r2;
    private RecyclerView ll1;
    private int FLAG_SCAN = 1;
    private EditText edt;
    private Button btnNext, btnScan;
    private EditText edtSearch, edtStorageBin;
    public int PARAM = 0;
    private String barcode = "", itemCode = "";
    public String fromWH;
    private WMStock stock = new WMStock();
    private StockTRReponse stockDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransferRequestBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        init();
        initialize();
        showDialogScan();
    }

    private void initialize() {
        binding.txtToWH.setText(Helper.getItemParam(Constants.WAREHOUSE).toString());
        List<String> warehouse = (List<String>) Helper.getItemParam(Constants.LIST_WAREHOUSE);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, warehouse);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spFromWH.setAdapter(dataAdapter);
        fromWH = warehouse.get(0);
        binding.spFromWH.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                fromWH = item;
                results = new ArrayList<>();
                if (!barcode.isEmpty() || !itemCode.isEmpty()) {
                    PARAM = 1;
                    new RequestUrl().execute();
                    getProgressDialog().show();
                }
            }
        });

        edtSearch = (EditText) findViewById(R.id.edtSearch);
        edtStorageBin = (EditText) findViewById(R.id.edtStorageBin);
        ll1 = (RecyclerView) findViewById(R.id.ll1);
        r2 = (RelativeLayout) findViewById(R.id.r2);
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
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(TransferRequestActivity.this)
                        .setMessage("Are you sure to transfer?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PARAM = 0;
                                new RequestUrl().execute();
                                getProgressDialog().show();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
            }
        });
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                doSearch(edtSearch.getText().toString().toLowerCase(Locale.getDefault()));
            }
        });
        binding.txtDelAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(TransferRequestActivity.this)
                        .setMessage("Are you sure to delete all item?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                results.clear();
                                adapter.notifyDataSetChanged();
                                binding.r5.setVisibility(View.GONE);
                                binding.txtEmpty.setVisibility(View.VISIBLE);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();

            }
        });

//        edtSearch.setText("");
//        edtSearch.setVisibility(View.VISIBLE);
//        doSearch(edtSearch.getText().toString().toLowerCase(Locale.getDefault()));
//        setData();
    }

    private void setData() {
        if (results != null && results.size() != 0) {
            for (TRStock temp : stockDetails.getResponseData()) {
                int flag = 0;
                for (TRStock stock : results) {
                    if (stock.getIdMaterial().equals(temp.getIdMaterial())) {
                        Toast.makeText(getApplicationContext(), "Product already inside the list", Toast.LENGTH_LONG).show();
                        stock = temp;
                        flag = 1;
                        break;
                    }
                }
                if (flag == 0)
                    results.add(temp);
            }
            adapter.notifyDataSetChanged();
            getProgressDialog().dismiss();
        } else {
            results = new ArrayList<>();
            results.addAll(stockDetails.getResponseData());
            adapter = new TransferRequestAdapter(this, results);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TransferRequestActivity.this, LinearLayoutManager.VERTICAL, true);

            binding.ll1.setLayoutManager(layoutManager);

            binding.ll1.setAdapter(adapter);
            getProgressDialog().dismiss();
        }
    }

    private void doSearch(String s) {
        if (results != null && results.size() != 0)
            filter(s);
    }

    private void filter(String charText) {
        ArrayList<TRStock> filteredListStock = new ArrayList<>();
        for (TRStock wp : results) {
            if (wp.getIdMaterial().toLowerCase(Locale.getDefault()).contains(charText)
                    || wp.getDesc().toLowerCase(Locale.getDefault()).contains(charText)) {
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
        Button btnProductName = (Button) dialogView.findViewById(R.id.btnProductCode);
        btnBarcode.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                FLAG_SCAN = 0;
                b.dismiss();
                showDialog();
            }
        });
        btnProductName.setOnClickListener(new View.OnClickListener() {

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
                    } else {
                        Toast.makeText(getApplicationContext(), "Please Input/Scan Product code first", Toast.LENGTH_LONG).show();
                    }
                } else {
                    a.dismiss();
                    Helper.removeItemParam(Constants.POS_MENU);
                    Helper.removeItemParam(Constants.BARCODERESULT);
                    if (FLAG_SCAN == 0) {
                        barcode = edt.getText().toString();
                        itemCode = "";
                    } else {
                        itemCode = edt.getText().toString();
                        barcode = "";
                    }
                    PARAM = 1;
                    new RequestUrl().execute();
                    getProgressDialog().show();
//                    Intent intent = new Intent(getApplicationContext(), FormTRActivity.class);
//                    startActivity(intent);
                }
            }
        });
        imgBtnClose.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                a.dismiss();
            }
        });
        imgBtnReset.setOnClickListener(new View.OnClickListener() {

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

    public void deleteItem(TRStock data, int position) {
        new AlertDialog.Builder(TransferRequestActivity.this)
                .setMessage("Are you sure to delete this item?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        results.remove(position);
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

    public void changeQTY(String toString, int position) {
        results.get(position).setAvailableQty(toString);
    }

    private class RequestUrl extends AsyncTask<Void, Void, WSMessageResponse> {

        @Override
        protected WSMessageResponse doInBackground(Void... voids) {
            try {
                if (PARAM == 0) {
                    String URL_TRANSFER_POSTING = Constants.API_PREFIX + Constants.API_TRANSFER_POSTING;
                    final String url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_TRANSFER_POSTING);
                    TransferPostingRequest tp = new TransferPostingRequest();
                    tp.setDate(Helper.todayDate1("yyyyMMdd"));
                    tp.setToWH(Helper.getItemParam(Constants.WAREHOUSE).toString());
                    tp.setFromWH(fromWH);
                    List<TPItem> list = new ArrayList<>();
                    for (TRStock ws : results) {
                        TPItem tpItem = new TPItem();
                        tpItem.setProductCode(ws.getIdMaterial());
                        tpItem.setQty(Integer.parseInt(ws.getAvailableQty()));
                        list.add(tpItem);
                    }
                    tp.setTpItems(list);
                    return (WSMessageResponse) Helper.postWebservice(url, tp, WSMessageResponse.class);
                } else {
                    String URL_STOCK_DETAILS = Constants.API_PREFIX + Constants.API_STOCK_DETAILS;
                    final String url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_STOCK_DETAILS);
                    StockDetailsRequest sd = new StockDetailsRequest();
                    sd.setBarcode(barcode);
                    sd.setItemCode(itemCode);
                    sd.setWh(fromWH);
                    stockDetails = (StockTRReponse) Helper.postWebservice(url, sd, StockTRReponse.class);
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
        protected void onPostExecute(WSMessageResponse tp) {
            if (PARAM == 0) {
                getProgressDialog().dismiss();
                if (tp != null) {
                    if (tp.getStatusCode() == 1) {
                        onDone(tp);
//                        Toast.makeText(getApplicationContext(), tp.getStatusMessage()+ ", Document Number : " + tp.getResponseData().getDocNum(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), tp.getStatusMessage() + ", " + tp.getResponseData().getErrorMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR) != null) {
                        Toast.makeText(getApplicationContext(), Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR).toString(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), tp.getStatusMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                if (stockDetails != null) {
                    if (stockDetails.getStatusCode() == 1) {
                        binding.r5.setVisibility(View.VISIBLE);
                        binding.txtEmpty.setVisibility(View.GONE);
                        setData();
                    } else {
                        getProgressDialog().dismiss();
                        Toast.makeText(getApplicationContext(), stockDetails.getStatusMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    getProgressDialog().dismiss();
                    if (Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR) != null) {
                        Toast.makeText(getApplicationContext(), Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR).toString(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), stockDetails.getStatusMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

    }

    private void onDone(WSMessageResponse tp) {
        new AlertDialog.Builder(TransferRequestActivity.this)
                .setCancelable(false)
                .setMessage(tp.getStatusMessage() + ", Document Number : " + tp.getResponseData().getDocNum())
                .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                    }
                }).show();
    }

}
