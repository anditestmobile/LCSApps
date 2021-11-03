package id.co.lcs.apps.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import id.co.lcs.apps.R;
import id.co.lcs.apps.adapter.PointOfSalesAdapter;
import id.co.lcs.apps.adapter.ProductDetailAdapter;
import id.co.lcs.apps.adapter.ProductListAdapter;
import id.co.lcs.apps.constants.Constants;
import id.co.lcs.apps.databinding.ActivityPosBinding;
import id.co.lcs.apps.helper.Helper;
import id.co.lcs.apps.model.Category;
import id.co.lcs.apps.model.Product;
import id.co.lcs.apps.model.ProductDetail;
import id.co.lcs.apps.model.ProductResponse;
import id.co.lcs.apps.model.Search;
import id.co.lcs.apps.model.SearchWithCategory;
import id.co.lcs.apps.model.StockDetailsReponse;
import id.co.lcs.apps.model.StockDetailsRequest;
import id.co.lcs.apps.model.WMDetailStock;
import id.co.lcs.apps.utils.Utils;

/**
 * Created by TED on 17-Jul-20
 */
public class PointOfSalesActivity extends BaseActivity {
    private ActivityPosBinding binding;
    private ArrayList<Category> categoryArrayList = new ArrayList<>();
    private ArrayList<Product> productArrayList = new ArrayList<>();
    private ArrayList<ProductDetail> productDetailArrayList = new ArrayList<>();
    private ArrayList<Product> allProductArrayList = new ArrayList<>();
    private PointOfSalesAdapter posAdapter;
    private ProductDetailAdapter productDetailAdapter;
    private Category category = new Category();
    private String errorMessage;
    private List<HashMap<String, Object>> data = new ArrayList<>();
    LinearLayoutManager manager;
    private boolean loading = false;
    private int index = 1;
    Object[] categoryID;
    public int PARAM = 0;
    public Menu menu;
    public String searchWithCat = "";
    private String itemCode;
    private TextView showMore;
    private StockDetailsReponse sdResponse = new StockDetailsReponse();
    private boolean[] expanded = {false};
    private Product dataInventory;
    private BroadcastReceiver broadcastReceiver;
    private TextView textCartItemCount;
    private boolean isRotate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPosBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        init();

        manager = new LinearLayoutManager(this);
        binding.rvCart.setLayoutManager(manager);
        binding.rvCart.setHasFixedSize(true);
        binding.rvCart.setNestedScrollingEnabled(false);
        getData();

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle(null);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRotate = ViewAnimation.rotateFab(v, !isRotate);
                if(isRotate){
                    ViewAnimation.showIn(binding.fabSO);
                    ViewAnimation.showIn(binding.fabBC);
                }else{
                    ViewAnimation.showOut(binding.fabSO);
                    ViewAnimation.showOut(binding.fabBC);
                }
            }
        });

        binding.fabSO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.setItemParam(Constants.PARAM_POS_SCAN, 0);
                Intent intent = new Intent(PointOfSalesActivity.this, ScanBarcodeActivity.class);
                startActivity(intent);
            }
        });
        binding.fabBC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.setItemParam(Constants.PARAM_POS_SCAN, 1);
                Intent intent = new Intent(PointOfSalesActivity.this, ScanBarcodeActivity.class);
                startActivity(intent);
            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PointOfSalesActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        // Scheme colors for animation
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
    }

    public void checkWishList(Product data, boolean wishList) {
        for (Category item : categoryArrayList) {
            for (Product productItem : item.getProductArrayList()) {
                if (productItem.getProductName().equals(data.getProductName())) {
                    productItem.setWishList(wishList);
                }
            }
        }
        Helper.setItemParam(Constants.LIST_PRODUCT, categoryArrayList);
    }
    public void getInventoryStock(Product data) {
        itemCode = data.getProductCode();
        dataInventory = data;
        PARAM = 2;
        new RequestUrl().execute();
        getProgressDialog().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pos, menu);
        MenuItem mSearch = menu.findItem(R.id.appSearchBar);
        MenuItem mCart = menu.findItem(R.id.appCheckout);
        View actionCart = mCart.getActionView();
        textCartItemCount = actionCart.findViewById(R.id.cart_badge);
        View actionView = mSearch.getActionView();
        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setMaxWidth(Integer.MAX_VALUE);

        mSearchView.setQueryHint("Search");
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchWithCat = query;
                PARAM = 1;
                new RequestUrl().execute();
                getProgressDialog().show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (mSearchView.getQuery().length() == 0) {
                    searchWithCat = "";
                    PARAM = 0;
                    new RequestUrl().execute();
                    getProgressDialog().show();
                }
                return true;
            }
        });
        return true;
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

    public void showPreview(Product data) {
        Helper.setItemParam(Constants.IMAGE_URL, data.getImageUrl());
        Intent intent = new Intent(getApplicationContext(), PreviewImageActivity.class);
        startActivity(intent);
    }

    public void showDialogAddToCart(Product data) {
        if (bottomSheetDialog == null || !bottomSheetDialog.isShowing()) {
            bottomSheetDialog = Utils.showBottomSheetDialog(this, R.layout.bsheet_add_to_cart, (itemView, bottomSheet) -> {
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
                int temp = 0;
                for(WMDetailStock stock : sdResponse.getResponseData().get(0).getDetailStocks()){
                    if(stock.getAvailableQty().equals("0")) {
                        continue;
                    }else{
                        temp++;
                    }
                }
                if(temp == 0){
                    btnAddToCart.setEnabled(false);
                }else{
                    btnAddToCart.setEnabled(true);
                }

                imgProduct.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        showPreview(data);
                        return false;
                    }
                });

                double price = data.getPrice();
                txtPrice.setText("$" + price);

                final int[] amount = {1};
                data.setQty(1);
                data.setPrice(price);
                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (amount[0] >= 1) {
                            amount[0]++;
                            edtAmount.setText(String.valueOf(amount[0]));
                            txtPrice.setText("S$" + String.format("%.2f", amount[0] * price));
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
                            txtPrice.setText("S$" + String.format("%.2f", amount[0] * price));
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
                        if (!editable.toString().isEmpty()) {
                            txtPrice.setText("S$" + Integer.parseInt(editable.toString()) * data.getPrice());
                            data.setQty(Integer.parseInt(editable.toString().trim()));
                        }
                    }
                });

                Glide.with(getApplicationContext())
                        .applyDefaultRequestOptions(new RequestOptions()
                                .placeholder(R.drawable.ic_no_image)
                                .error(R.drawable.ic_no_image))
                        .asBitmap()
                        .load(data.getImageUrl())
                        .into(imgProduct);


                labelTitle.setText(data.getProductName());
                showMore.setText(Html.fromHtml("<u>" + "Show more" + "</u>"));

                /*product detail*/
                initProductDetail(data.getProductCategory(), data.getProductCode());

                rvProductDetail.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                rvProductDetail.setHasFixedSize(true);

                productDetailAdapter = new ProductDetailAdapter(this, productDetailArrayList);
                rvProductDetail.setAdapter(productDetailAdapter);
                rvProductDetail.setItemAnimator(new DefaultItemAnimator());
                rvProductDetail.setHasFixedSize(false);
                rvProductDetail.setNestedScrollingEnabled(false);


                btnAddToCart.setOnClickListener(v -> {
                    if (edtAmount.getText().toString().trim().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Quantity cannot empty", Toast.LENGTH_SHORT).show();
                    } else {
                        bottomSheet.dismiss();
                        addToCart(data);
                    }
                });

                expanded = new boolean[]{false};
                showMore.setOnClickListener(view -> {

                    if (!expanded[0]) {
                        expanded[0] = true;
                        showMore.setText(Html.fromHtml("<u>" + "Show less" + "</u>"));
                        showMoreDetail(sdResponse.getResponseData().get(0).getDetailStocks());
                    } else {

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

    private void addToCart(Product data) {
//        Toast.makeText(getApplicationContext(), "Added to cart!", Toast.LENGTH_SHORT).show();
        ArrayList<Product> listProduct = (ArrayList<Product>) Helper.getItemParam(Constants.LIST_CART_POS);
        if(listProduct == null){
            listProduct = new ArrayList<>();
            data.setStatusCheckout(true);
            listProduct.add(data);
            Helper.setItemParam(Constants.LIST_CART_POS, listProduct);
            sentCartPOS(String.valueOf(listProduct.size()));
        }else {
            boolean flag = true;
            for (Product product : listProduct) {
                if (product.getProductName().equals(data.getProductName())) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                data.setStatusCheckout(true);
                listProduct.add(data);
                sentCartPOS(String.valueOf(listProduct.size()));
            }
            Helper.setItemParam(Constants.LIST_CART, listProduct);
        }
    }

    public void sentCartPOS(String qty){
        Intent broadcast = new Intent();
        broadcast.setAction("CART_POS");
        broadcast.putExtra("message", qty);
        getApplicationContext().sendBroadcast(broadcast);
    }

    private class RequestUrl extends AsyncTask<Void, Void, ProductResponse> {

        @Override
        protected ProductResponse doInBackground(Void... voids) {
            try {
                if (PARAM == 0) {
                    String URL_PRODUCT = Constants.API_PREFIX + Constants.API_PRODUCT_DETAILS_INDEX;
                    final String url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_PRODUCT);
                    Category c = new Category();
                    c.setCategory("");
                    c.setIndex(index);
                    return (ProductResponse) Helper.postWebservice(url, c, ProductResponse.class);
                } else if (PARAM == 1) {
                    String URL_SEARCH_PRODUCT = Constants.API_PREFIX + Constants.API_PRODUCT_DETAILS_SEARCH;
                    final String url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_SEARCH_PRODUCT);
                    Search s = new Search();
                    s.setSearch(searchWithCat);
                    s.setIndex(index);
                    return (ProductResponse) Helper.postWebservice(url, s, ProductResponse.class);
                } else {
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
                if (product != null) {
                    if (product.getStatusCode() == 1) {
                        productArrayList = new ArrayList<>();
                        productArrayList = product.getResponseData();
                        setDataResponse();
                    } else {
                        loading = false;
                        binding.bottomProgressBar.setVisibility(View.GONE);
                    }
                } else {
                    if (Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR) != null) {
                        Toast.makeText(getApplicationContext(), Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR).toString(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), product.getStatusMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (PARAM == 1) {
                index = 1;
                getProgressDialog().dismiss();
                if (product != null) {
                    if (product.getStatusCode() == 1) {
                        productArrayList = new ArrayList<>();
                        productArrayList = product.getResponseData();
                        setDataResponse();
                    } else {
                        loading = false;
                        binding.bottomProgressBar.setVisibility(View.GONE);
                    }
                } else {
                    if (Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR) != null) {
                        Toast.makeText(getApplicationContext(), Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR).toString(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), product.getStatusMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                getProgressDialog().dismiss();
                if (sdResponse != null) {
                    if (sdResponse.getStatusCode() == 1) {
                        showDialogAddToCart(dataInventory);
                    } else {
                        Toast.makeText(getApplicationContext(), sdResponse.getStatusMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR) != null) {
                        Toast.makeText(getApplicationContext(), Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR).toString(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), sdResponse.getStatusMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

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

    private void setDataResponse() {
        if (index == 1) {
            allProductArrayList = productArrayList;
            if (allProductArrayList == null) {
                allProductArrayList = new ArrayList();
            }

            if (allProductArrayList.size() != 0) {
                posAdapter = new PointOfSalesAdapter(this, allProductArrayList);
                binding.rvCart.setAdapter(posAdapter);
                binding.emptyLayout.setVisibility(View.GONE);
                binding.rvCart.setVisibility(View.VISIBLE);
            } else {
                binding.emptyLayout.setVisibility(View.VISIBLE);
                binding.rvCart.setVisibility(View.GONE);
//                Toast.makeText(getApplicationContext(), "Ops something went wrong..", Toast.LENGTH_SHORT).show();
            }
            loading = false;
        } else {
            final int positionStart = allProductArrayList.size() + 1;
            allProductArrayList.addAll(productArrayList);
//            productAdapter.notifyDataSetChanged();
            posAdapter.notifyItemRangeInserted(positionStart, allProductArrayList.size());
            loading = false;
            binding.bottomProgressBar.setVisibility(View.GONE);
        }

        binding.rvCart.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!loading) {//cek dulu datanya lagi di load aatau gak
                    if (searchWithCat.equals("")) {
                        if (manager != null && manager.findLastCompletelyVisibleItemPosition() == allProductArrayList.size() - 1) {
                            binding.bottomProgressBar.setVisibility(View.VISIBLE);
                            index++;
                            loading = true;
                            new RequestUrl().execute();
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setBroadcastReceiver();
    }

    private void setBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("CART_POS");
//        if(broadcastReceiver == null){
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getExtras().getString("message", null);
                if(message != null){
                    textCartItemCount.setText(message);
                }
            }
        };
//        }
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(broadcastReceiver!=null){
            unregisterReceiver(broadcastReceiver);
        }
    }
}