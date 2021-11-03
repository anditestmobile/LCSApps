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
import id.co.lcs.apps.adapter.ProductDetailAdapter;
import id.co.lcs.apps.adapter.ProductSearchListAdapter;
import id.co.lcs.apps.constants.Constants;
import id.co.lcs.apps.databinding.ActivityProductBinding;
import id.co.lcs.apps.helper.Helper;
import id.co.lcs.apps.model.Category;
import id.co.lcs.apps.model.Product;
import id.co.lcs.apps.model.ProductDetail;
import id.co.lcs.apps.model.ProductResponse;
import id.co.lcs.apps.model.Search;
import id.co.lcs.apps.model.StockDetailsReponse;
import id.co.lcs.apps.model.StockDetailsRequest;
import id.co.lcs.apps.model.WMDetailStock;
import id.co.lcs.apps.model.WMStock;
import id.co.lcs.apps.utils.Utils;

/**
 * Created by TED on 17-Jul-20
 */
public class ProductSearchListActivity extends BaseActivity {
    private ActivityProductBinding binding;
    private ArrayList<Category> categoryArrayList = new ArrayList<>();
    private ArrayList<Product> productArrayList = new ArrayList<>();
    private ArrayList<ProductDetail> productDetailArrayList = new ArrayList<>();
    private ArrayList<Product> allProductArrayList = new ArrayList<>();
    private ProductSearchListAdapter productAdapter;
    private ProductDetailAdapter productDetailAdapter;
    private Category category = new Category();
    private String errorMessage;
    private List<HashMap<String, Object>> data = new ArrayList<>();
    GridLayoutManager manager;
    private boolean loading = false;
    private int index = 1;
    Object[] categoryID;
    public int PARAM = 0;
    public String txtSearch = "";
    private String itemCode;
    private TextView showMore;
    private StockDetailsReponse sdResponse = new StockDetailsReponse();
    private boolean[] expanded = {false};
    private Product dataInventory;
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        init();

        categoryArrayList = (ArrayList<Category>) Helper.getItemParam(Constants.LIST_PRODUCT);

        manager = new GridLayoutManager(this, 2);
        binding.rvCart.setLayoutManager(manager);
        binding.rvCart.setHasFixedSize(true);
        binding.rvCart.setNestedScrollingEnabled(false);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle(null);

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
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
        PARAM = 1;
        new RequestUrl().execute();
        getProgressDialog().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem mSearch = menu.findItem(R.id.appSearchBar);

        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setMaxWidth(Integer.MAX_VALUE);

        mSearchView.setQueryHint("Search");
        mSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.equals(txtSearch)) {
                    index = 1;
                }
                txtSearch = query;
                getData();
//                Toast.makeText(getApplicationContext(), query, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                adapter.getFilter().filter(newText);
                return true;
            }
        });
        mSearch.expandActionView();
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
//                            txtPrice.setText("S$" + String.format("%.2f", amount[0] * price));
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
//                            txtPrice.setText("S$" + String.format("%.2f", amount[0] * price));
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
                        if (!editable.toString().isEmpty()) {
                            txtPrice.setText("$" + Helper.doubleToStringNoDecimal(Integer.parseInt(editable.toString()) * data.getPrice()));
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

                    } else {
                        int i[] = {2};
                        if (productDetailArrayList.size() - 1 == i[i.length - 1]) {
                            for (int j = i.length - 1; j >= 0; j--) {
                                productDetailArrayList.remove(i[j]);
                                productDetailAdapter.notifyItemRemoved(i[j]);
                            }
                            labelBarcode.setVisibility(View.GONE);
                            imgBarcode.setVisibility(View.GONE);
                            showMore.setText(Html.fromHtml("<u>" + "Show more" + "</u>"));
                            expanded[0] = false;
                        }
                    }
                });

            });
        }
    }

    public void setSizeCart() {
//        ArrayList<Product> cartList = new ArrayList<>();
//        if (Helper.getItemParam(Constants.CART_LIST) != null) {
//            cartList = (ArrayList<Product>) Helper.getItemParam(Constants.CART_LIST);
//        } else {
//            cartList = new ArrayList<>();
//        }
//        if (cartList.size() == 0) {
//            binding.sizeLayout.setVisibility(View.GONE);
//        } else if (cartList.size() > 99) {
//            binding.txtCartSize.setText("99+");
//            binding.sizeLayout.setVisibility(View.VISIBLE);
//        } else {
//            binding.sizeLayout.setVisibility(View.VISIBLE);
//            binding.txtCartSize.setText(String.valueOf(cartList.size()));
//        }
    }

    private class RequestUrl extends AsyncTask<Void, Void, ProductResponse> {

        @Override
        protected ProductResponse doInBackground(Void... voids) {
            try {
                if (PARAM == 0) {
                    String URL_SEARCH_PRODUCT = Constants.API_PREFIX + Constants.API_PRODUCT_DETAILS_SEARCH;
                    final String url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_SEARCH_PRODUCT);
                    Search s = new Search();
                    s.setSearch(txtSearch);
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
                    } else if (index == 1 && product.getStatusCode() == 3) {
                        binding.emptyLayout.setVisibility(View.VISIBLE);
                        binding.rvCart.setVisibility(View.GONE);
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
                productAdapter = new ProductSearchListAdapter(this, allProductArrayList);
                binding.rvCart.setAdapter(productAdapter);
                binding.emptyLayout.setVisibility(View.GONE);
                binding.rvCart.setVisibility(View.VISIBLE);
            } else {
                binding.emptyLayout.setVisibility(View.VISIBLE);
                binding.rvCart.setVisibility(View.GONE);
//                Toast.makeText(getApplicationContext(), "Ops something went wrong..", Toast.LENGTH_SHORT).show();
            }
            loading = false;
        } else {
            allProductArrayList.addAll(productArrayList);
            productAdapter.notifyDataSetChanged();
//            productAdapter.notifyItemRangeInserted(index, allProductArrayList.size());
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
                    if (manager != null && manager.findLastCompletelyVisibleItemPosition() == allProductArrayList.size() - 1) {
                        binding.bottomProgressBar.setVisibility(View.VISIBLE);
                        //bottom of list!
                        index++;
                        loading = true;
//                        Toast.makeText(getApplicationContext(),String.valueOf(index),Toast.LENGTH_SHORT).show();
//                        new ConnectionOdoo().execute();//item count
                        new RequestUrl().execute();
//                        loading = true;
                    }
                }
            }
        });
    }
}