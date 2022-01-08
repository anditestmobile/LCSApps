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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import id.co.lcs.apps.R;
import id.co.lcs.apps.constants.Constants;
import id.co.lcs.apps.databinding.ActivityInventoryMenuBinding;
import id.co.lcs.apps.helper.Helper;
import id.co.lcs.apps.model.StockDetailsReponse;
import id.co.lcs.apps.model.StockDetailsRequest;

public class InventoryMenuActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {
    ActivityInventoryMenuBinding binding;
    private int FLAG_SCAN = 1;
    private EditText edt, edtBin;
    private Button btnNext, btnScan;
    private String wh;
    int itemSelected = 0;
    public int PARAM = 0;
    private String barcode = "" , itemCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInventoryMenuBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        init();
        initialize();
        Glide.with(this)
                .load(getImage("no_profile"))
                .circleCrop()
                .into(binding.imgProfile);

    }

    private void initialize() {
        binding.txtUsername.setText(user.getFirstName());
        wh = (String) Helper.getItemParam(Constants.WAREHOUSE);
        binding.txtWh.setText(wh);

        binding.btnSS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WMStockActivity.class);
                startActivity(intent);
            }
        });

        binding.btnTR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TransferRequestActivity.class);
                startActivity(intent);
//                showDialogScan();
            }
        });
        binding.btnGR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), GoodReceiptActivity.class);
                startActivity(intent);
            }
        });
        binding.btnGRS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helper.removeItemParam(Constants.PO_NUMBER);
                Intent intent = new Intent(getApplicationContext(), GoodReceiptSupplierActivity.class);
                startActivity(intent);
            }
        });
        binding.btnSTK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String[] singleChoiceItems = (String[]) Helper.getItemParam(Constants.LIST_BIN);
//
//                new androidx.appcompat.app.AlertDialog.Builder(InventoryMenuActivity.this)
//                        .setTitle("Select Bin ")
//                        .setSingleChoiceItems(singleChoiceItems, itemSelected, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int selectedIndex) {
//                                itemSelected = selectedIndex;
//                            }
//                        })
//                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                //set what would happen when positive button is clicked
//                                Helper.setItemParam(Constants.BIN, singleChoiceItems[itemSelected]);
//                                Intent intent = new Intent(getApplicationContext(), StockTakeActivity.class);
//                                startActivity(intent);
//                            }
//                        })
//                        .setNegativeButton("Cancel", null)
//                        .show();
                showDialogBin();
//                Toast.makeText(getApplicationContext(), "Coming soon", Toast.LENGTH_LONG).show();
            }
        });

        binding.layoutProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InventoryMenuActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    public void showDialogBin() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_bin, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog a = dialogBuilder.create();
        edtBin = (EditText) dialogView.findViewById(R.id.edtBatch);
        final ImageButton imgBtnClose = (ImageButton) dialogView.findViewById(R.id.imgDelete);
        final ImageButton imgBtnReset = (ImageButton) dialogView.findViewById(R.id.imgReset);
        btnNext = (Button) dialogView.findViewById(R.id.btnNext);

        btnNext.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (edtBin.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please input bin first", Toast.LENGTH_LONG).show();
                } else {
                    a.dismiss();
                    Helper.setItemParam(Constants.BIN, edtBin.getText().toString());
                    Intent intent = new Intent(getApplicationContext(), StockTakeActivity.class);
                    startActivity(intent);
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
                edtBin.setText("");
            }
        });
        a.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
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
        if (FLAG_SCAN == 0) {
            edt.setHint(R.string.hint_strg_bin);
        } else {
            edt.setHint(R.string.hint_mat_num);
        }
        final ImageButton imgBtnClose = (ImageButton) dialogView.findViewById(R.id.imgDelete);
        final ImageButton imgBtnReset = (ImageButton) dialogView.findViewById(R.id.imgReset);
        btnNext = (Button) dialogView.findViewById(R.id.btnNext);
        btnScan = (Button) dialogView.findViewById(R.id.btnScan);

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
                    } else{
                        Toast.makeText(getApplicationContext(), "Please Input/Scan Product name first", Toast.LENGTH_LONG).show();
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
//                    Intent intent = new Intent(getApplicationContext(), FormTRActivity.class);
//                    startActivity(intent);
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

    private class RequestUrl extends AsyncTask<Void, Void, StockDetailsReponse> {

        @Override
        protected StockDetailsReponse doInBackground(Void... voids) {
            try {
                if (PARAM == 0) {
                    String URL_STOCK_DETAILS = Constants.API_PREFIX + Constants.API_STOCK_DETAILS;
                    final String url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_STOCK_DETAILS);
                    StockDetailsRequest sd = new StockDetailsRequest();
                    sd.setBarcode(barcode);
                    sd.setItemCode(itemCode);
//                    sd.setWh(Helper.getItemParam(Constants.WAREHOUSE).toString());
                    return (StockDetailsReponse) Helper.postWebservice(url, sd, StockDetailsReponse.class);
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
        protected void onPostExecute(StockDetailsReponse stockDetails) {
            if (PARAM == 0) {
                getProgressDialog().dismiss();
                if(stockDetails != null){
                    if(stockDetails.getStatusCode() == 1) {
                        Helper.setItemParam(Constants.STOCK_DETAILS, stockDetails.getResponseData().get(0));
                        Intent intent = new Intent(getApplicationContext(), FormTRActivity.class);
                        startActivity(intent);
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

            }
        }

    }

}