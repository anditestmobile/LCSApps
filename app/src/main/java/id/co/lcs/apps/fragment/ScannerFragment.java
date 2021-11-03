package id.co.lcs.apps.fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.zxing.Result;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;

import id.co.lcs.apps.R;
import id.co.lcs.apps.activity.PreviewImageActivity;
import id.co.lcs.apps.constants.Constants;
import id.co.lcs.apps.helper.Helper;
import id.co.lcs.apps.model.Category;
import id.co.lcs.apps.model.CategoryResponse;
import id.co.lcs.apps.model.Product;
import id.co.lcs.apps.model.ProductDetail;
import id.co.lcs.apps.adapter.ProductDetailAdapter;
import id.co.lcs.apps.model.ProductResponse;
import id.co.lcs.apps.model.SearchWithProductCode;
import id.co.lcs.apps.utils.Utils;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerFragment extends BaseFragment implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private ArrayList<Product> productArrayList = new ArrayList<>();
    private ArrayList<ProductDetail> productDetailArrayList = new ArrayList<>();
    private ProductDetailAdapter productDetailAdapter;
    private Product product = new Product();
    public int PARAM = 0;
    public String barcodeResult="";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mScannerView = new ZXingScannerView(getActivity());

        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET};


        Permissions.check(getContext(), permissions, null, null, new PermissionHandler() {
            @Override
            public void onGranted() {
                new Handler().postDelayed(() -> {
                    onResume();
                }, 1000);
            }


            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied(context, deniedPermissions);
            }
        });
        return mScannerView;
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        /*Log.v(TAG, rawResult.getText()); // Prints scan results
        Log.v(TAG, rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)*/

        // If you would like to resume scanning, call this method below:
        barcodeResult = rawResult.getText();
        PARAM = 0;
        new RequestUrl().execute();
        getProgressDialog().show();
    }

    private class RequestUrl extends AsyncTask<Void, Void, ProductResponse> {

        @Override
        protected ProductResponse doInBackground(Void... voids) {
            try {
                if (PARAM == 0) {
                        String URL_PRODUCT = Constants.API_PREFIX + Constants.API_PRODUCT_CODE_SEARCH;
                    final String url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_PRODUCT);
                    SearchWithProductCode sc = new SearchWithProductCode();
                    sc.setProductCode(barcodeResult);
                    return (ProductResponse) Helper.postWebservice(url, sc, ProductResponse.class);
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
        protected void onPostExecute(ProductResponse p) {
            if (PARAM == 0) {
                getProgressDialog().dismiss();
                if(p != null){
                    if(p.getStatusCode() == 1) {
                        productArrayList = new ArrayList<>();
                        productArrayList = p.getResponseData();
                        product = productArrayList.get(0);
                        showDialogAddToCart(product);
                    }else{
                        Toast.makeText(getActivity(), p.getStatusMessage(), Toast.LENGTH_SHORT).show();
                        resumeScanner();
                    }
                } else {
                    if (Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR) != null) {
                        Toast.makeText(getActivity(), Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR).toString(), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getActivity(), p.getStatusMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }else{

            }
        }

    }

    private void resumeScanner() {
        mScannerView.resumeCameraPreview(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    public void showPreview(Product data){
        Helper.setItemParam(Constants.IMAGE_URL, data.getImageUrl());
        Intent intent = new Intent(getActivity(), PreviewImageActivity.class);
        startActivity(intent);
    }

    public void showDialogAddToCart(Product data) {
        if (bottomSheetDialog == null || !bottomSheetDialog.isShowing()) {
            bottomSheetDialog = Utils.showBottomSheetDialog(getActivity(), R.layout.bsheet_add_to_cart, (itemView, bottomSheet) -> {
                bottomSheet.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        resumeScanner();
                    }
                });
                ImageView imgProduct = itemView.findViewById(R.id.imgProduct);
                ImageView imgBarcode = itemView.findViewById(R.id.imgBarcode);
                Button btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
                TextView txtPrice = itemView.findViewById(R.id.txtPrice);
                TextView labelTitle = itemView.findViewById(R.id.labelTitle);
                RecyclerView rvProductDetail = itemView.findViewById(R.id.rvProductDetail);
                TextView txtShowMore = itemView.findViewById(R.id.txtShowMore);
                TextView labelBarcode = itemView.findViewById(R.id.labelBarcode);
                EditText edtAmount = itemView.findViewById(R.id.edtAmount);
                ImageView btnAdd = itemView.findViewById(R.id.btnAdd);
                ImageView btnRemove = itemView.findViewById(R.id.btnRemove);


                imgProduct.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        showPreview(data);
                        return false;
                    }
                });

                double price = data.getPrice();
                txtPrice.setText("$" + Helper.doubleToStringNoDecimal(price));

                final int[] amount = {1};
                data.setQty(1);
                data.setPrice(price);
                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (amount[0] >= 1) {
                            amount[0]++;
                            edtAmount.setText(String.valueOf(amount[0]));
                            txtPrice.setText("$" + Helper.doubleToStringNoDecimal(amount[0] * price));

                            data.setQty(amount[0]);
                            data.setPrice(Double.parseDouble(String.valueOf(amount[0] * price)));
                        }

                    }
                });

                btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (amount[0] != 1) {
                            amount[0]--;
                            edtAmount.setText(String.valueOf(amount[0]));
                            txtPrice.setText("$" + Helper.doubleToStringNoDecimal(amount[0] * price));
                            data.setQty(amount[0]);
                            data.setPrice(Double.parseDouble(String.valueOf(amount[0] * price)));
                        }

                    }
                });

                edtAmount.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if(!editable.toString().isEmpty()) {
                            txtPrice.setText("$" + Helper.doubleToStringNoDecimal(Integer.parseInt(editable.toString()) * data.getPrice()));
                            data.setQty(Integer.parseInt(editable.toString().trim()));
                        }
                    }
                });

                Glide.with(getContext())
                        .load(data.getImageUrl())
                        .into(imgProduct);


                labelTitle.setText(data.getProductName());
                txtShowMore.setText(Html.fromHtml("<u>" + "Show more" + "</u>"));

                /*product detail*/
                initProductDetail(data.getProductCategory(),data.getProductCode());

                rvProductDetail.setLayoutManager(new LinearLayoutManager(getContext()));
                rvProductDetail.setHasFixedSize(true);

                productDetailAdapter = new ProductDetailAdapter(this.getContext(), productDetailArrayList);
                rvProductDetail.setAdapter(productDetailAdapter);
                rvProductDetail.setItemAnimator(new DefaultItemAnimator());
                rvProductDetail.setHasFixedSize(false);
                rvProductDetail.setNestedScrollingEnabled(false);

                btnAddToCart.setOnClickListener(v -> {
                    if(edtAmount.getText().toString().trim().isEmpty()){
                        Toast.makeText(getActivity(), "Quantity cannot empty", Toast.LENGTH_SHORT).show();
                    }else {
                        bottomSheet.dismiss();
                        showDialogConfirmation(data);
                    }
                });

                final boolean[] expanded = {false};
                txtShowMore.setOnClickListener(view -> {

                    if (!expanded[0]) {
                        Glide.with(getContext())
                                .load("https://laptopnesia.com/wp-content/uploads/2019/01/Cara-Membuat-Barcode-5-1.jpg")
                                .into(imgBarcode);
//                        labelBarcode.setVisibility(View.VISIBLE);
//                        imgBarcode.setVisibility(View.VISIBLE);
                        txtShowMore.setText(Html.fromHtml("<u>" + "Show less" + "</u>"));

                        showMoreDetail(data);
                        expanded[0] = true;
                    } else {
                        int i[] = {2};
                        if (productDetailArrayList.size() - 1 == i[i.length - 1]) {
                            for (int j = i.length - 1; j >= 0; j--) {
                                productDetailArrayList.remove(i[j]);
                                productDetailAdapter.notifyItemRemoved(i[j]);
                            }
//                            labelBarcode.setVisibility(View.GONE);
//                            imgBarcode.setVisibility(View.GONE);
                            txtShowMore.setText(Html.fromHtml("<u>" + "Show more" + "</u>"));
                            expanded[0] = false;
                        }


                    }

                });
            });
        }
    }

    private void showMoreDetail(Product data) {
        ProductDetail detail = new ProductDetail();
        String detailStock = "";
//        for(int i=0; i<2; i++){
//            if(i != 0){
//                detailStock = detailStock + "\nWarehouse " + i + " : " + 1+i;
//            }else{
//                detailStock = "Warehouse " + i + " : " + 1+i;
//            }
//
//        }
        detail.setValue("In Stock : " + data.getInStock());
        detail.setLabel("Inventory Details");
//        detail.setValue(detailStock);
        productDetailArrayList.add(detail);
        productDetailAdapter.notifyItemInserted(3);
    }

    private void initProductDetail(String productCategory, String productCode) {
        productDetailArrayList = new ArrayList<>();
        ProductDetail detail = new ProductDetail();
        detail.setLabel("Categories");
        detail.setValue(productCategory);
        productDetailArrayList.add(detail);

        detail = new ProductDetail();
        detail.setLabel("Product Code");
        detail.setValue(productCode);
        productDetailArrayList.add(detail);
    }


//    private void initData() {
//        productArrayList = new ArrayList<>();
//
//        productArrayList = new ArrayList<>();
//        Product product1 = new Product();
//        product1.setProductName("Selleys All Clear 75g");
//        product1.setProductCategory("Co-polymer Sealants (DIY)");
//        product1.setProductDetailArrayList(productDetailArrayList);
//        for (int i=0 ; i<3; i++){
//            productArrayList.add(product1);
//        }
//
//    }
//
//
//    public void showMoreDetail() {
//        ProductDetail detail = new ProductDetail();
//        detail.setLabel("Sizes");
//        detail.setValue("75g");
//        productDetailArrayList.add(detail);
//
//        detail = new ProductDetail();
//        detail.setLabel("Inner");
//        detail.setValue("12");
//        productDetailArrayList.add(detail);
//
//        detail = new ProductDetail();
//        detail.setLabel("Outer");
//        detail.setValue("144");
//        productDetailArrayList.add(detail);
//
//        detail = new ProductDetail();
//        detail.setLabel("EAN code");
//        detail.setValue("9555086301240");
//        productDetailArrayList.add(detail);
//
//        detail = new ProductDetail();
//        detail.setLabel("Item code");
//        detail.setValue("76SEL-0000106-1E");
//        productDetailArrayList.add(detail);
//
//    }
//
//    public void initProductDetail() {
//        productDetailArrayList = new ArrayList<>();
//        ProductDetail detail = new ProductDetail();
//        detail.setLabel("Product Name:");
//        detail.setValue("Selleys All Clear 75g");
//        productDetailArrayList.add(detail);
//
//        detail = new ProductDetail();
//        detail.setLabel("Categories");
//        detail.setValue("Co-polymer Sealants (DIY)");
//        productDetailArrayList.add(detail);
//
//        detail = new ProductDetail();
//        detail.setLabel("Brand");
//        detail.setValue("Selleys");
//        productDetailArrayList.add(detail);
//
//
//        detail = new ProductDetail();
//        detail.setLabel("Colour available");
//        detail.setValue("Crystal Clear");
//        productDetailArrayList.add(detail);
//    }

}