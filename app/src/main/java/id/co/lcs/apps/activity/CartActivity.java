package id.co.lcs.apps.activity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import id.co.lcs.apps.R;
import id.co.lcs.apps.adapter.CartAdapter;
import id.co.lcs.apps.constants.Constants;
import id.co.lcs.apps.databinding.ActivityCartBinding;
import id.co.lcs.apps.helper.Helper;
import id.co.lcs.apps.helper.QRCodeHelper;
import id.co.lcs.apps.model.Customer;
import id.co.lcs.apps.model.CustomerRequest;
import id.co.lcs.apps.model.CustomerResponse;
import id.co.lcs.apps.model.ProductDetail;
import id.co.lcs.apps.adapter.ProductDetailAdapter;
import id.co.lcs.apps.model.Product;
import id.co.lcs.apps.model.SalesOrderDetails;
import id.co.lcs.apps.model.SalesOrderRequest;
import id.co.lcs.apps.model.SalesOrderResponse;
import id.co.lcs.apps.model.StockDetailsReponse;
import id.co.lcs.apps.model.StockDetailsRequest;
import id.co.lcs.apps.model.WMDetailStock;
import id.co.lcs.apps.model.WMStock;
import id.co.lcs.apps.model.WSMessageResponse;
import id.co.lcs.apps.utils.Utils;

public class CartActivity extends BaseActivity {
    private ActivityCartBinding binding;
    private CartAdapter cartAdapter;
    private ArrayList<Product> productArrayList = new ArrayList<>();
    private ArrayList<ProductDetail> productDetailArrayList = new ArrayList<>();
    private ArrayList<ProductDetail> productDetailArrayListMore = new ArrayList<>();
    private BottomSheetDialog bottomSheetDialog;
    private ProductDetailAdapter productDetailAdapter;
    private double total = 0;
    private boolean FLAG_CHECKALL = true;
    private StockDetailsReponse sdResponse = new StockDetailsReponse();
    private boolean[] expanded = {false};
    private Product dataInventory;
    private String itemCode;
    private int PARAM = 0;
    private SalesOrderRequest soRequest;
    private List<Integer> pos = new ArrayList<>();
    private SalesOrderResponse wsResponse;
    private EditText edtCompName, edtMobileNumber, edtShipTo, edtRemarks;
    private AutoCompleteTextView edtCustomerCode;
    private CustomerResponse listCust;
    private int FLAG_CUST = 0;
    private String textCust;
    private SalesOrderRequest updateCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCartBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle(null);
        init();
        initData();

    }

    private void initData() {
        updateCart = (SalesOrderRequest) Helper.getItemParam(Constants.QUOTATION_HISTORY);
        if (updateCart != null && !Helper.UPDATE_CART) {
            Helper.removeItemParam(Constants.LIST_CART);
            ArrayList<Product> listProduct = new ArrayList<>();
            ArrayList<ProductDetail> listProductDetail = new ArrayList<>();

            for (SalesOrderDetails data : updateCart.getSoItem()) {
                listProductDetail.add(new ProductDetail("Product Name:", data.getItemName()));
                Product p = new Product(data.getItemCode(), data.getItemName(), data.getCategories(), data.getImgUrl()
                        , "0", Double.parseDouble(data.getPrice())*Integer.parseInt(data.getQty().split("\\.")[0])
                        , 0, 0, 0, Integer.parseInt(data.getQty().split("\\.")[0])
                        , false, true, listProductDetail, null);
                listProduct.add(p);
            }
            Helper.setItemParam(Constants.LIST_CART, listProduct);
            Helper.UPDATE_CART = true;
        }
        if(updateCart != null){
            binding.btnCheckout.setText("UPDATE");
        }
        productArrayList = (ArrayList<Product>) Helper.getItemParam(Constants.LIST_CART);
        if (productArrayList == null || productArrayList.size() == 0) {
            binding.rvCart.setVisibility(View.GONE);
            binding.rootCheckout.setVisibility(View.GONE);
            binding.checkAll.setVisibility(View.GONE);
            binding.l1.setVisibility(View.VISIBLE);
        } else {
            binding.rvCart.setVisibility(View.VISIBLE);
            binding.l1.setVisibility(View.GONE);
            binding.checkAll.setVisibility(View.VISIBLE);
            binding.rootCheckout.setVisibility(View.VISIBLE);
            for (Product product : productArrayList) {
                product.setProductDetailArrayList(initProductDetail(product));
                if (product.isStatusCheckout()) {
                    total = total + (product.getQty() * product.getPrice());
                } else {
                    if (total != 0) {
                        total = total - (product.getQty() * product.getPrice());
                    }
                }
                if (product.getQty() == 0) {
                    product.setQty(1);
                }
            }
            binding.txtTotal.setText("S$" + String.format("%.2f", total));
        }

        binding.checkAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (productArrayList != null) {
                    if (FLAG_CHECKALL) {
                        for (Product product : productArrayList) {
                            product.setStatusCheckout(b);
                        }
                        cartAdapter.notifyDataSetChanged();
                        countTotal();
                        FLAG_CHECKALL = true;
                    } else {
                        boolean checkAll = true;
                        for (Product product : productArrayList) {
                            if (!product.isStatusCheckout()) {
                                checkAll = false;
                                break;
                            }
                        }
                        if (checkAll) {
                            binding.checkAll.setChecked(true);

                        } else {
                            binding.checkAll.setChecked(false);
                        }
                        FLAG_CHECKALL = true;

                    }
                }
            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.rvCart.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCart.setHasFixedSize(true);

        cartAdapter = new CartAdapter(this, productArrayList);
        binding.rvCart.setAdapter(cartAdapter);

        binding.btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                showDialogConfirmationCheckOut();
                int FLAG = 0;

                for (Product product : productArrayList) {
                    if (product.isStatusCheckout() && product.getQty() == 0) {
                        FLAG = 1;
                        break;
                    }
                }
                if (FLAG == 0) {
//                    showDialogConfirmationCheckOut();
                    showDialogCustomer();
                } else {
                    Toast.makeText(getApplicationContext(), "Quantity cannot empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.checkAll.setChecked(true);
    }


    public void showMoreDetail(Product data) {
        productDetailArrayListMore = new ArrayList<>();
        ProductDetail detail = new ProductDetail();

        detail.setLabel("Categories");
        detail.setValue(data.getProductCategory());
        productDetailArrayListMore.add(detail);

        detail = new ProductDetail();
        detail.setLabel("Product Code");
        detail.setValue(data.getProductCode());
        productDetailArrayListMore.add(detail);

        detail = new ProductDetail();
        List<WMDetailStock> dataStock = new ArrayList<>();
//        dataStock  = sdResponse.getResponseData().get(0).getDetailStocks();
        for (WMStock stock : sdResponse.getResponseData()) {
            if (stock.getIdMaterial().equals(itemCode)) {
                dataStock = stock.getDetailStocks();
                break;
            }
        }
        String detailStock = null;
        int temp = 0;
        for (int i = 0; i < dataStock.size(); i++) {
            if (dataStock.get(i).getAvailableQty().equals("0")) {
                continue;
            }
            if (temp != 0) {
                detailStock = detailStock + "\nWhs " + dataStock.get(i).getWhs() + " : " + dataStock.get(i).getAvailableQty();
            } else {
                detailStock = "Whs " + dataStock.get(i).getWhs() + " : " + dataStock.get(i).getAvailableQty();
            }
            temp++;
        }
        if (detailStock == null) {
            detailStock = "No Stock";
        }
        detail.setLabel("Inventory Details");
        detail.setValue(detailStock);
        productDetailArrayListMore.add(detail);
    }

    public ArrayList<ProductDetail> initProductDetail(Product data) {

        productDetailArrayList = new ArrayList<>();

        ProductDetail detail = new ProductDetail();
        detail.setLabel("Product Name:");
        detail.setValue(data.getProductName());
        productDetailArrayList.add(detail);

        return productDetailArrayList;
    }

    public void updateTotalChart(Product data) {
        countTotal();
        FLAG_CHECKALL = false;
        binding.checkAll.setChecked(data.isStatusCheckout());
        FLAG_CHECKALL = true;
        Helper.setItemParam(Constants.LIST_CART, productArrayList);
    }

    private void countTotal() {
        total = 0;
        for (Product product : productArrayList) {
            if (product.isStatusCheckout()) {
                total = total + (product.getQty() * product.getPrice());
            } else {
                if (total != 0) {
                    total = total - (product.getQty() * product.getPrice());
                }
            }
        }
        if (!binding.txtTotal.getText().equals("s$" + String.format("%.2f", total))) {
            binding.txtTotal.setText("$" + String.format("%.2f", total));
        }
    }

    public void showDialogConfirmationRemove(String message, Product data, int position) {
        if (bottomSheetDialog == null || !bottomSheetDialog.isShowing()) {
            bottomSheetDialog = Utils.showBottomSheetDialog(this, R.layout.bsheet_confirmation, (itemView, bottomSheet) -> {

                ImageView imgProduct = itemView.findViewById(R.id.imgProduct);
                TextView txtTitle = itemView.findViewById(R.id.labelTitle);
                TextView txtProductName = itemView.findViewById(R.id.txtProductName);
                TextView edtAmount = itemView.findViewById(R.id.edtAmount);
                TextView txtPrice = itemView.findViewById(R.id.txtPrice);
                Button btnNo = itemView.findViewById(R.id.btnSkip);
                Button btnYes = itemView.findViewById(R.id.btnEmail);

//                imgProduct.setVisibility(View.GONE);
//                txtProductName.setVisibility(View.GONE);
                edtAmount.setVisibility(View.GONE);
//                txtPrice.setVisibility(View.GONE);


                Glide.with(this)
                        .load(data.getImageUrl())
                        .into(imgProduct);
                txtTitle.setText(message);
                txtProductName.setText(data.getProductName());
                btnYes.setText("Remove");

                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        productArrayList.remove(position);
                        cartAdapter.notifyDataSetChanged();
                        if (productArrayList.size() == 0) {
                            binding.rvCart.setVisibility(View.GONE);
                            binding.rootCheckout.setVisibility(View.GONE);
                            binding.l1.setVisibility(View.VISIBLE);
                            binding.checkAll.setVisibility(View.GONE);
                        }
                        Helper.setItemParam(Constants.LIST_CART, productArrayList);
//                        Toast.makeText(CartActivity.this, "Removed!", Toast.LENGTH_SHORT).show();
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

    public void showDialogCustomer() {
        if (bottomSheetDialog == null || !bottomSheetDialog.isShowing()) {
            bottomSheetDialog = Utils.showBottomSheetDialog(this, R.layout.bsheet_customer, (itemView, bottomSheet) -> {
                Button btnNo = itemView.findViewById(R.id.btnSkip);
                Button btnYes = itemView.findViewById(R.id.btnEmail);
                edtCompName = itemView.findViewById(R.id.edtCompanyName);
                edtMobileNumber = itemView.findViewById(R.id.edtMobileNumber);
                edtCustomerCode = itemView.findViewById(R.id.edtCustomerCode);
                edtShipTo = itemView.findViewById(R.id.edtShipTo);
                edtRemarks = itemView.findViewById(R.id.edtRemarks);

                if(updateCart.getQuotation() != null){
                    edtCustomerCode.setText(updateCart.getCustomerCode());
                    edtMobileNumber.setText(updateCart.getPhone());
                    edtCompName.setText(updateCart.getCompName());
                    edtShipTo.setText(updateCart.getShipTo());
                    edtRemarks.setText(updateCart.getRemarks());
                    edtCustomerCode.setEnabled(false);
                    edtMobileNumber.setEnabled(false);
                    edtCompName.setEnabled(false);
                    edtRemarks.setEnabled(false);
                }
                btnYes.setText("Confirm");

                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (edtCustomerCode.getText().toString().isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Please fill customer code", Toast.LENGTH_SHORT).show();
                        } else if (edtCompName.getText().toString().isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Please fill company name", Toast.LENGTH_SHORT).show();
                        } else if (edtMobileNumber.getText().toString().isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Please fill mobile number", Toast.LENGTH_SHORT).show();
                        } else if (edtShipTo.getText().toString().isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Please fill Ship To", Toast.LENGTH_SHORT).show();
                        } else if (edtRemarks.getText().toString().isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Please fill Remarks", Toast.LENGTH_SHORT).show();
                        } else {
                            bottomSheet.dismiss();
                            showDialogConfirmationCheckOut();
                        }
                    }
                });

                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                    }
                });

                edtCustomerCode.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (listCust != null && listCust.getResponseData().size() != 0) {
                            for (Customer temp : listCust.getResponseData()) {
                                if ((temp.getCustomerCode() + "-" + temp.getName()).equals(editable.toString())) {
                                    edtCompName.setText(temp.getCompanyName());
                                    FLAG_CUST = 1;
                                }
                            }
                        }
                        if (FLAG_CUST == 0 && editable.toString().length() >= 3) {
                            edtCompName.setText("");
                            textCust = editable.toString();
                            PARAM = 2;
                            new RequestUrl().execute();
                        } else {
                            FLAG_CUST = 0;
                        }
                    }
                });
            });
        }
    }

    public void showDialogAfterCheckout() {
        if (bottomSheetDialog == null || !bottomSheetDialog.isShowing()) {
            bottomSheetDialog = Utils.showBottomSheetDialog(this, R.layout.bsheet_after_checkout, (itemView, bottomSheet) -> {
                Button btnEmail = itemView.findViewById(R.id.btnEmail);
                Button btnScanSO = itemView.findViewById(R.id.btnScanSO);
                Button btnSkip = itemView.findViewById(R.id.btnSkip);


//                txtTitle.setText("Are you sure want to checkout?\nSales order confirmation will be emailed to you.");
//                btnYes.setText("Confirm");

                btnEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheet.dismiss();
                        Toast.makeText(CartActivity.this, "Order sent to your email", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                });

                btnScanSO.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        bottomSheet.dismiss();
                        Intent intent = new Intent(CartActivity.this, SoActivity.class);
                        startActivity(intent);
                    }
                });

                btnSkip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(CartActivity.this, "Order sent", Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                        onBackPressed();
                    }
                });
            });
        }
    }

    public void showDialogConfirmationCheckOut() {
        if (bottomSheetDialog == null || !bottomSheetDialog.isShowing()) {
            bottomSheetDialog = Utils.showBottomSheetDialog(this, R.layout.bsheet_confirmation, (itemView, bottomSheet) -> {

                ImageView imgAlert = itemView.findViewById(R.id.imgAlert);
                imgAlert.setVisibility(View.VISIBLE);
                ImageView imgProduct = itemView.findViewById(R.id.imgProduct);
                TextView txtTitle = itemView.findViewById(R.id.labelTitle);
                TextView txtProductName = itemView.findViewById(R.id.txtProductName);
                TextView edtAmount = itemView.findViewById(R.id.edtAmount);
                TextView txtPrice = itemView.findViewById(R.id.txtPrice);
                Button btnNo = itemView.findViewById(R.id.btnSkip);
                Button btnYes = itemView.findViewById(R.id.btnEmail);

                imgProduct.setVisibility(View.GONE);
                txtProductName.setVisibility(View.GONE);
                edtAmount.setVisibility(View.GONE);
                txtPrice.setVisibility(View.GONE);

                if(updateCart == null) {
                    txtTitle.setText("Are you sure want to checkout?");
                    btnYes.setText("CheckOut");
                }else{
                    txtTitle.setText("Are you sure want to update this quotation?");
                    btnYes.setText("Update");
                }

                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(updateCart == null) {
                            setDataInsert();
                        }else{
                            setDataUpdate();
                        }
                        PARAM = 1;
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

    private void setDataInsert() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar delDate = Helper.todayDate();
        delDate.add(Calendar.DATE, 2);
        soRequest = new SalesOrderRequest();
        soRequest.setCustomerCode(edtCustomerCode.getText().toString().split("-")[1]);
        soRequest.setName(edtCompName.getText().toString());
        soRequest.setUserId(user.getFirstName());
        soRequest.setDate(Helper.todayDate1("yyyyMMdd"));
        soRequest.setCompName(edtCompName.getText().toString());
        soRequest.setPhone(edtMobileNumber.getText().toString());
        soRequest.setShipTo(edtShipTo.getText().toString());
        soRequest.setRemarks(edtRemarks.getText().toString());
        soRequest.setDelDate(sdf.format(delDate.getTime()));
        SalesOrderDetails soDetails = new SalesOrderDetails();
        List<SalesOrderDetails> listSO = new ArrayList<>();
        for (int i = 0; i < productArrayList.size(); i++) {
            if (productArrayList.get(i).isStatusCheckout()) {
                pos.add(i);
                soDetails.setItemCode(productArrayList.get(i).getProductCode());
                soDetails.setItemName(productArrayList.get(i).getProductName());
                soDetails.setPrice(String.valueOf(productArrayList.get(i).getPrice()));
                soDetails.setQty(String.valueOf(productArrayList.get(i).getQty()));
                soDetails.setWarehouse("01");
                listSO.add(soDetails);
            }
        }
        soRequest.setSoItem(listSO);
    }

    private void setDataUpdate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar delDate = Helper.todayDate();
        delDate.add(Calendar.DATE, 2);
        soRequest = new SalesOrderRequest();
        soRequest.setQuotation(updateCart.getQuotation());
        soRequest.setCustomerCode(edtCustomerCode.getText().toString().split("-")[1]);
        soRequest.setName(edtCompName.getText().toString());
        soRequest.setUserId(user.getFirstName());
        soRequest.setDate(Helper.todayDate1("yyyyMMdd"));
        soRequest.setCompName(edtCompName.getText().toString());
        soRequest.setPhone(edtMobileNumber.getText().toString());
        soRequest.setShipTo(edtShipTo.getText().toString());
        soRequest.setRemarks(edtRemarks.getText().toString());
        soRequest.setDelDate(sdf.format(delDate.getTime()));
        SalesOrderDetails soDetails = new SalesOrderDetails();
        List<SalesOrderDetails> listSO = new ArrayList<>();
        for (int i = 0; i < productArrayList.size(); i++) {
            if (productArrayList.get(i).isStatusCheckout()) {
                pos.add(i);
                soDetails.setItemCode(productArrayList.get(i).getProductCode());
                soDetails.setItemName(productArrayList.get(i).getProductName());
                soDetails.setPrice(String.valueOf(productArrayList.get(i).getPrice()));
                soDetails.setQty(String.valueOf(productArrayList.get(i).getQty()));
                soDetails.setWarehouse("01");
                listSO.add(soDetails);
            }
        }
        soRequest.setSoItem(listSO);
    }


    public void showDialogAddToCart(Product data) {
        if (bottomSheetDialog == null || !bottomSheetDialog.isShowing()) {
            bottomSheetDialog = Utils.showBottomSheetDialog(this, R.layout.bsheet_add_to_cart, (itemView, bottomSheet) -> {
                ImageView imgProduct = itemView.findViewById(R.id.imgProduct);
                ImageView imgBarcode = itemView.findViewById(R.id.imgBarcode);
                Button btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
                btnAddToCart.setText("DISMISS");
                TextView txtPrice = itemView.findViewById(R.id.txtPrice);
                TextView labelTitle = itemView.findViewById(R.id.labelTitle);
                RecyclerView rvProductDetail = itemView.findViewById(R.id.rvProductDetail);
                TextView txtShowMore = itemView.findViewById(R.id.txtShowMore);
                TextView labelBarcode = itemView.findViewById(R.id.labelBarcode);

                TextView edtAmount = itemView.findViewById(R.id.edtAmount);
                ImageView btnAdd = itemView.findViewById(R.id.btnAdd);
                ImageView btnRemove = itemView.findViewById(R.id.btnRemove);
                edtAmount.setVisibility(View.GONE);
                btnAdd.setVisibility(View.GONE);
                btnRemove.setVisibility(View.GONE);

                showMoreDetail(data);

                double price = data.getPrice();
                txtPrice.setText("S$" + String.format("%.2f", price));

                final int[] amount = {1};
                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (amount[0] >= 1) {
                            amount[0]++;
                            edtAmount.setText(String.valueOf(amount[0]));
                            txtPrice.setText("S$" + String.format("%.2f", amount[0] * price));
                        }

                    }
                });

                btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (amount[0] != 1) {
                            amount[0]--;
                            edtAmount.setText(String.valueOf(amount[0]));
                            txtPrice.setText("S$" + String.format("%.2f", amount[0] * price));
                        }

                    }
                });

                Glide.with(this)
                        .load(data.getImageUrl())
                        .into(imgProduct);


                labelTitle.setText(data.getProductName());
//                txtPrice.setText("$88.0");
                txtShowMore.setText(Html.fromHtml("<u>" + "Show more" + "</u>"));
                txtShowMore.setVisibility(View.INVISIBLE);
                txtShowMore.setEnabled(false);

                /*product detail*/
                initProductDetail(data);

                rvProductDetail.setLayoutManager(new LinearLayoutManager(this));
                rvProductDetail.setHasFixedSize(true);

                productDetailAdapter = new ProductDetailAdapter(this, productDetailArrayListMore);
                rvProductDetail.setAdapter(productDetailAdapter);
                rvProductDetail.setItemAnimator(new DefaultItemAnimator());
                rvProductDetail.setHasFixedSize(false);
                rvProductDetail.setNestedScrollingEnabled(false);


                btnAddToCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheet.dismiss();
                    }
                });


            });
        }
    }

    public void getInventoryStock(Product data) {
        itemCode = data.getProductCode();
        dataInventory = data;
        PARAM = 0;
        new RequestUrl().execute();
        getProgressDialog().show();
    }

    public void showPreview(Product data) {
        Helper.setItemParam(Constants.IMAGE_URL, data.getImageUrl());
        Intent intent = new Intent(CartActivity.this, PreviewImageActivity.class);
        startActivity(intent);
    }

    private class RequestUrl extends AsyncTask<Void, Void, StockDetailsReponse> {

        @Override
        protected StockDetailsReponse doInBackground(Void... voids) {
            try {
                if (PARAM == 0) {
                    String URL_STOCK_DETAILS = Constants.API_PREFIX + Constants.API_GET_STOCK;
                    final String url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_STOCK_DETAILS);
                    StockDetailsRequest sd = new StockDetailsRequest();
                    sd.setBarcode("");
                    sd.setItemCode(itemCode);
                    return (StockDetailsReponse) Helper.postWebservice(url, sd, StockDetailsReponse.class);
                } else if (PARAM == 1) {
                    String URL_SALES_ORDER = Constants.API_PREFIX + Constants.API_SALES_ORDER;
                    final String url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_SALES_ORDER);
                    wsResponse = (SalesOrderResponse) Helper.postWebservice(url, soRequest, SalesOrderResponse.class);
                    return null;
                } else {
                    String URL_CUSTOMER = Constants.API_PREFIX + Constants.API_GET_CUSTOMER;
                    final String url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_CUSTOMER);
                    CustomerRequest customerRequest = new CustomerRequest();
                    customerRequest.setCustomer(textCust);
                    listCust = (CustomerResponse) Helper.postWebservice(url, customerRequest, CustomerResponse.class);
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
        protected void onPostExecute(StockDetailsReponse response) {
            if (PARAM == 0) {
                sdResponse = new StockDetailsReponse();
                getProgressDialog().dismiss();
                if (response != null) {
                    if (response.getStatusCode() == 1) {
                        sdResponse = response;
                        showDialogAddToCart(dataInventory);
                    } else {
                        Toast.makeText(getApplicationContext(), response.getStatusMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR) != null) {
                        Toast.makeText(getApplicationContext(), Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR).toString(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), response.getStatusMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (PARAM == 1) {
                getProgressDialog().dismiss();
                if (wsResponse != null) {
                    if (wsResponse.getStatusCode() == 1) {
                        onDone();
                        bottomSheetDialog.dismiss();
                    } else {
                        if (wsResponse.getResponseData().getErrorMessage().contains("Invalid BP code")) {
                            Toast.makeText(getApplicationContext(), soRequest.getCustomerCode() + ", Invalid BP code", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), wsResponse.getStatusMessage() + ", " + wsResponse.getResponseData().getErrorMessage(), Toast.LENGTH_LONG).show();
                        }
                        bottomSheetDialog.dismiss();
                    }
                } else {
                    if (Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR) != null) {
                        Toast.makeText(getApplicationContext(), Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR).toString(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), wsResponse.getStatusMessage(), Toast.LENGTH_SHORT).show();
                    }
                    bottomSheetDialog.dismiss();
                }
            } else {
                if (listCust != null) {
                    if (listCust.getStatusCode() == 1) {
                        setCustomer(listCust);
                    }
//                    else {
//                        Toast.makeText(getApplicationContext(), listCust.getStatusMessage(), Toast.LENGTH_SHORT).show();
//                    }
                }
//                else {
//                    if (Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR) != null) {
//                        Toast.makeText(getApplicationContext(), Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR).toString(), Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(getApplicationContext(), listCust.getStatusMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }
            }
        }

    }

    private void setCustomer(CustomerResponse listCust) {
        String[] cust = new String[listCust.getResponseData().size()];
        for (int i = 0; i < listCust.getResponseData().size(); i++) {
            cust[i] = listCust.getResponseData().get(i).getCustomerCode() + "-" + listCust.getResponseData().get(i).getName();
        }

        ArrayAdapter adapter = new
                ArrayAdapter(this, R.layout.list_customer, R.id.item, cust);
        edtCustomerCode.setAdapter(adapter);
        edtCustomerCode.showDropDown();
    }

    private void onDone() {
        List<Product> pos = new ArrayList<>();
        for (int i = 0; i < productArrayList.size(); i++) {
            if (productArrayList.get(i).isStatusCheckout()) {
                pos.add(productArrayList.get(i));
            }
        }
        if (pos.size() != 0) {
            for (Product temp : pos) {
                productArrayList.remove(temp);
            }
        }
        Helper.setItemParam(Constants.LIST_CART, productArrayList);
        Helper.setItemParam(Constants.BAR_CODE, wsResponse.getResponseData().getDocNum());
        Intent intent = new Intent(CartActivity.this, BarcodeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
