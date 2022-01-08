package id.co.lcs.apps.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import id.co.lcs.apps.activity.PreviewImageActivity;
import id.co.lcs.apps.activity.ProductListActivity;
import id.co.lcs.apps.adapter.ProductListAdapter;
import id.co.lcs.apps.constants.Constants;
import id.co.lcs.apps.databinding.ActivityProductBinding;
import id.co.lcs.apps.databinding.FragmentBestBuyBinding;
import id.co.lcs.apps.databinding.FragmentWishlistBinding;
import id.co.lcs.apps.helper.Helper;
import id.co.lcs.apps.model.Category;
import id.co.lcs.apps.model.Product;
import id.co.lcs.apps.model.ProductDetail;
import id.co.lcs.apps.adapter.ProductDetailAdapter;
import id.co.lcs.apps.adapter.BestBuyAdapter;
import id.co.lcs.apps.R;
import id.co.lcs.apps.model.ProductResponse;
import id.co.lcs.apps.model.StockDetailsReponse;
import id.co.lcs.apps.model.StockDetailsRequest;
import id.co.lcs.apps.model.WMDetailStock;
import id.co.lcs.apps.model.WMStock;
import id.co.lcs.apps.utils.Utils;

/**
 * A fragment representing a list of Items.
 */
public class BestBuyFragment extends BaseFragment {
    private FragmentBestBuyBinding binding;
    private View rootView;
    private BestBuyAdapter bestBuyAdapter;
    private ArrayList<Category> categoryArrayList = new ArrayList<>();
    private ArrayList<Product> productArrayList = new ArrayList<>();
    private ArrayList<ProductDetail> productDetailArrayList = new ArrayList<>();
    private ArrayList<Product> allProductArrayList = new ArrayList<>();
    private ProductDetailAdapter productDetailAdapter;
    public int PARAM = 0;
    private GridLayoutManager manager;
    private String itemCode;
    private TextView showMore;
    private StockDetailsReponse sdResponse = new StockDetailsReponse();
    private boolean[] expanded = {false};
    private Product dataInventory;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBestBuyBinding.inflate(inflater, container, false);
        rootView = binding.getRoot();
        init();
        getData();
        manager = new GridLayoutManager(getActivity(), 2);
        binding.rvCart.setLayoutManager(manager);
        binding.rvCart.setHasFixedSize(true);
        binding.rvCart.setNestedScrollingEnabled(false);

        binding.pullToRefresh.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light)
        );

        binding.pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
                // To keep animation for 1 seconds
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Stop animation (This will be after 1 seconds)
                        binding.pullToRefresh.setRefreshing(false);
                    }
                }, 1000); // Delay in millis
            }
        });
//        initData();
        return rootView;
    }

    private void getData() {
        binding.emptyLayout.setVisibility(View.GONE);
        binding.bottomProgressBar.setVisibility(View.GONE);
        productArrayList = new ArrayList<>();
        allProductArrayList = new ArrayList<>();
        PARAM = 0;
        new RequestUrl().execute();
        getProgressDialog().show();
    }
    public void getInventoryStock(Product data) {
        itemCode = data.getProductCode();
        dataInventory = data;
        PARAM = 1;
        new RequestUrl().execute();
        getProgressDialog().show();
    }

    private class RequestUrl extends AsyncTask<Void, Void, ProductResponse> {

        @Override
        protected ProductResponse doInBackground(Void... voids) {
            try {
                if (PARAM == 0) {
                    String URL_BEST_BUY = Constants.API_PREFIX + Constants.API_PRODUCT_BESTBUY;
                    final String url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_BEST_BUY);
                    return (ProductResponse) Helper.getWebserviceWithoutHeaders(url, ProductResponse.class);
                }else{
                    String URL_STOCK_DETAILS = Constants.API_PREFIX + Constants.API_GET_STOCK;
                    final String url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_STOCK_DETAILS);
                    StockDetailsRequest sd = new StockDetailsRequest();
                    sd.setBarcode("");
                    sd.setItemCode(itemCode);
                    sdResponse = (StockDetailsReponse) Helper.postWebservice(url, sd, StockDetailsReponse.class);
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
        protected void onPostExecute(ProductResponse product) {
            if (PARAM == 0) {
                getProgressDialog().dismiss();
                if(product != null){
                    if(product.getStatusCode() == 1) {
                        productArrayList = new ArrayList<>();
                        productArrayList = product.getResponseData();
                        binding.txtFromWhen.setText(product.getSalesPeriod());
                        setDataResponse();
                    }else{
                        binding.emptyLayout.setVisibility(View.VISIBLE);
                        binding.rvCart.setVisibility(View.GONE);
                        binding.txtFromWhen.setVisibility(View.GONE);
                    }
                } else {
                    if (Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR) != null) {
                        Toast.makeText(getActivity(), Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR).toString(), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getActivity(), product.getStatusMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }else{
                getProgressDialog().dismiss();
                if(sdResponse != null){
                    if(sdResponse.getStatusCode() == 1) {
                        showDialogAddToCart(dataInventory);
                    }else{
                        Toast.makeText(getActivity(), sdResponse.getStatusMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR) != null) {
                        Toast.makeText(getActivity(), Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR).toString(), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getActivity(), sdResponse.getStatusMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

    }

    private void setDataResponse() {
//        if (index == 1) {
            allProductArrayList = productArrayList;
            if (allProductArrayList == null) {
                allProductArrayList = new ArrayList();
            }

            if (allProductArrayList.size() != 0) {
                bestBuyAdapter = new BestBuyAdapter(this, allProductArrayList);
                binding.rvCart.setAdapter(bestBuyAdapter);
                binding.emptyLayout.setVisibility(View.GONE);
                binding.rvCart.setVisibility(View.VISIBLE);
                binding.txtFromWhen.setVisibility(View.VISIBLE);
            } else {
                binding.emptyLayout.setVisibility(View.VISIBLE);
                binding.rvCart.setVisibility(View.GONE);
                binding.txtFromWhen.setVisibility(View.GONE);
            }

    }

    public void showPreview(Product data){
        Helper.setItemParam(Constants.IMAGE_URL, data.getImageUrl());
        Intent intent = new Intent(getActivity(), PreviewImageActivity.class);
        startActivity(intent);
    }

    private void showMoreDetail(List<WMDetailStock> data) {
        ProductDetail detail = new ProductDetail();
        expanded[0] = true;
        showMore.setText(Html.fromHtml("<u>" + "Show less" + "</u>"));
        String detailStock = null;
        int temp = 0;
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getAvailableQty().equals("0")) {
                continue;
            }
            if (temp != 0) {
                detailStock = detailStock + "\nWhs " + data.get(i).getWhs() + " : " + data.get(i).getAvailableQty();
            } else {
                detailStock = "Whs " + data.get(i).getWhs() + " : " + data.get(i).getAvailableQty();
            }
            temp++;
        }
        if (detailStock == null) {
            detailStock = "No Stock";
        }
        detail.setLabel("Inventory Details");
        detail.setValue(detailStock);
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

    public void showDialogAddToCart(Product data) {
        if (bottomSheetDialog == null || !bottomSheetDialog.isShowing()) {
            bottomSheetDialog = Utils.showBottomSheetDialog(getActivity(), R.layout.bsheet_add_to_cart, (itemView, bottomSheet) -> {
                ImageView imgProduct = itemView.findViewById(R.id.imgProduct);
                ImageView imgBarcode = itemView.findViewById(R.id.imgBarcode);
                Button btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
                TextView txtPrice = itemView.findViewById(R.id.txtPrice);
                TextView labelTitle = itemView.findViewById(R.id.labelTitle);
                RecyclerView rvProductDetail = itemView.findViewById(R.id.rvProductDetail);
                showMore = itemView.findViewById(R.id.txtShowMore);
                TextView labelBarcode = itemView.findViewById(R.id.labelBarcode);
                EditText edtAmount = itemView.findViewById(R.id.edtAmount);
                ImageView btnAdd = itemView.findViewById(R.id.btnAdd);
                ImageView btnRemove = itemView.findViewById(R.id.btnRemove);
                TextView txtUOM = itemView.findViewById(R.id.txtUOM);
                if(data.getUomName() != null) {
                    txtUOM.setText(data.getUomName());
                }
//                int temp = 0;
//                for(WMDetailStock stock : sdResponse.getResponseData().get(0).getDetailStocks()){
//                    if(stock.getAvailableQty().equals("0")) {
//                        continue;
//                    }else{
//                        temp++;
//                    }
//                }
//                if(temp == 0){
//                    btnAddToCart.setEnabled(false);
//                }else{
//                    btnAddToCart.setEnabled(true);
//                }
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

                Glide.with(getActivity())
                        .applyDefaultRequestOptions(new RequestOptions()
                                .placeholder(R.drawable.ic_no_image)
                                .error(R.drawable.ic_no_image))
                        .asBitmap()
                        .load(data.getImageUrl())
                        .into(imgProduct);


                labelTitle.setText(data.getProductName());
                showMore.setText(Html.fromHtml("<u>" + "Show more" + "</u>"));

                /*product detail*/
                initProductDetail(data.getProductCategory(),data.getProductCode());

                rvProductDetail.setLayoutManager(new LinearLayoutManager(getActivity()));
                rvProductDetail.setHasFixedSize(true);

                productDetailAdapter = new ProductDetailAdapter(getActivity(), productDetailArrayList);
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


                expanded = new boolean[]{false};
                showMore.setOnClickListener(view -> {
                    if (!expanded[0]) {
                        expanded[0] = true;
                        showMore.setText(Html.fromHtml("<u>" + "Show less" + "</u>"));
                        for(WMStock stock : sdResponse.getResponseData()){
                            if(stock.getIdMaterial().equals(itemCode)){
                                showMoreDetail(stock.getDetailStocks());
                                break;
                            }
                        }
                    }else {
                        int i[] = {2};
                        if (productDetailArrayList.size() - 1 == i[i.length - 1]) {
                            for (int j = i.length - 1; j >= 0; j--) {
                                productDetailArrayList.remove(i[j]);
                                productDetailAdapter.notifyItemRemoved(i[j]);
                            }
                            showMore.setText(Html.fromHtml("<u>" + "Show more" + "</u>"));
                            expanded[0] = false;
                        }


                    }

                });

            });
        }
    }

}