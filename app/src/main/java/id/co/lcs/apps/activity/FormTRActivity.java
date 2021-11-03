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
import android.widget.Toast;

import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

import id.co.lcs.apps.R;
import id.co.lcs.apps.constants.Constants;
import id.co.lcs.apps.databinding.ActivityFormTrBinding;
import id.co.lcs.apps.helper.Helper;
import id.co.lcs.apps.model.StockDetailsReponse;
import id.co.lcs.apps.model.StockDetailsRequest;
import id.co.lcs.apps.model.TPItem;
import id.co.lcs.apps.model.TransferPostingRequest;
import id.co.lcs.apps.model.WSMessageResponse;
import id.co.lcs.apps.model.WMStock;

public class FormTRActivity extends BaseActivity {
	ActivityFormTrBinding binding;
	WMStock stock = new WMStock();
	public int PARAM = 0;
	public String toWH;
	private EditText edt;
	private Button btnNext, btnScan;
	private String barcode = "" , itemCode = "";
	private int FLAG_SCAN = 1;
	private StockDetailsReponse stockDetails;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = ActivityFormTrBinding.inflate(getLayoutInflater());
		View view = binding.getRoot();
		setContentView(view);
		init();
		initialize();
	}

	private void initialize() {
		stock = (WMStock) Helper.getItemParam(Constants.STOCK_DETAILS);
		binding.txtMat.setText(stock.getIdMaterial());
		binding.txtDescMat.setText(stock.getDesc());
//		binding.edtQty.setText(stock.getAvailableQty());
		// TODO Auto-generated method stub
		List<String> warehouse = (List<String>) Helper.getItemParam(Constants.LIST_WAREHOUSE);

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, warehouse);

		// Drop down layout style - list view with radio button
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// attaching data adapter to spinner
		binding.spWarehouse.setAdapter(dataAdapter);
		toWH = warehouse.get(0);
		binding.spWarehouse.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

			@Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
				toWH =  item;
			}
		});

		binding.btnSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (binding.edtQty.getText().toString().trim().equals("")) {
				Toast.makeText(getApplicationContext(),
						"Quantity cannot empty", Toast.LENGTH_SHORT).show();
				}  else {
					dialogOk();
				}
			}
		});

		binding.btnClear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				binding.edtQty.setText("");
			}
		});

		binding.btnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onBackPressed();
			}
		});
		binding.txtFromBin.setText(Helper.getItemParam(Constants.WAREHOUSE).toString());

		binding.btnNext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
	}

	public void dialogOk() {
		AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
		builder1.setMessage("Are you sure want to transfer this product?");
		builder1.setCancelable(true);

		builder1.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						PARAM = 0;
						new RequestUrl().execute();
						getProgressDialog().show();
						dialog.cancel();
					}
				});

		builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});

		AlertDialog alert11 = builder1.create();
		alert11.show();
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
					tp.setToWH(toWH);
					tp.setFromWH(Helper.getItemParam(Constants.WAREHOUSE).toString());
					TPItem tpItem = new TPItem();
					tpItem.setProductCode(stock.getIdMaterial());
					tpItem.setQty(Integer.parseInt(binding.edtQty.getText().toString()));
					List<TPItem> list = new ArrayList<>();
					list.add(tpItem);
					tp.setTpItems(list);
					return (WSMessageResponse) Helper.postWebservice(url, tp, WSMessageResponse.class);
				}else{
					String URL_STOCK_DETAILS = Constants.API_PREFIX + Constants.API_STOCK_DETAILS;
					final String url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_STOCK_DETAILS);
					StockDetailsRequest sd = new StockDetailsRequest();
					sd.setBarcode(barcode);
					sd.setItemCode(itemCode);
//					sd.setWh(Helper.getItemParam(Constants.WAREHOUSE).toString());
					stockDetails = (StockDetailsReponse) Helper.postWebservice(url, sd, StockDetailsReponse.class);
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
				if(tp != null){
					if(tp.getStatusCode() == 1) {
						Toast.makeText(getApplicationContext(), tp.getStatusMessage()+ ", Document Number : " + tp.getResponseData().getDocNum(), Toast.LENGTH_SHORT).show();
						onBackPressed();
					}else{
						Toast.makeText(getApplicationContext(), tp.getStatusMessage(), Toast.LENGTH_SHORT).show();
					}
				} else {
					if (Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR) != null) {
						Toast.makeText(getApplicationContext(), Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR).toString(), Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(getApplicationContext(), tp.getStatusMessage(), Toast.LENGTH_SHORT).show();
					}
				}
			}else{

			}
		}

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
					PARAM = 1;
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

}