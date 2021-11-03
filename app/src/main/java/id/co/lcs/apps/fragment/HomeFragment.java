package id.co.lcs.apps.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.chip.Chip;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

import id.co.lcs.apps.activity.PreviewImageActivity;
import id.co.lcs.apps.activity.ProductSearchListActivity;
import id.co.lcs.apps.adapter.CategoryAdapter;
import id.co.lcs.apps.constants.Constants;
import id.co.lcs.apps.databinding.FragmentHomeBinding;
import id.co.lcs.apps.helper.Helper;
import id.co.lcs.apps.model.Category;
import id.co.lcs.apps.model.CategoryResponse;
import id.co.lcs.apps.model.Product;
import id.co.lcs.apps.model.ProductDetail;
import id.co.lcs.apps.adapter.ProductDetailAdapter;
import id.co.lcs.apps.adapter.SliderAdapter;
import id.co.lcs.apps.model.ProductResponse;
import id.co.lcs.apps.model.SliderItem;
import id.co.lcs.apps.model.StockDetailsReponse;
import id.co.lcs.apps.model.StockDetailsRequest;
import id.co.lcs.apps.model.WMDetailStock;
import id.co.lcs.apps.model.WMStock;
import id.co.lcs.apps.utils.Utils;
import id.co.lcs.apps.R;

/**
 * Created by TED on 15-Jul-20
 */
public class HomeFragment extends BaseFragment {
    protected View rootView;
    private FragmentHomeBinding binding;
    private CategoryAdapter categoryAdapter;
    private ProductDetailAdapter productDetailAdapter;

    private ArrayList<Category> categoryArrayList = new ArrayList<>();
    private ArrayList<Product> productArrayList = new ArrayList<>();
    private ArrayList<ProductDetail> productDetailArrayList = new ArrayList<>();
    private ArrayList<Product> prodList = new ArrayList<>();
    private CategoryResponse categoryResponse = new CategoryResponse();
    public int PARAM = 0;
    public int index = 1;
    private boolean loading = false;
    private int indexScroll = 0;
    private LinearLayoutManager manager;
    private ArrayList<Category> resultsCategory = new ArrayList<>();
    private ArrayList<Category> singleResultCategory = new ArrayList<>();
    private String cName, filterText = "";
    private String itemCode;
    private TextView showMore;
    private StockDetailsReponse sdResponse = new StockDetailsReponse();
    private boolean[] expanded = {false};
    private Button btnAddToCart;
    private Product dataInventory;
    private Category singleCategory;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        rootView = binding.getRoot();
        init();
        initialize();
        getData();
        return rootView;
    }

    private void getData() {
        PARAM = 1;
        new RequestUrl().execute();
        getProgressDialog().show();
    }

    public void getInventoryStock(Product data) {
        itemCode = data.getProductCode();
        dataInventory = data;
        PARAM = 2;
        new RequestUrl().execute();
        getProgressDialog().show();
    }

    private class RequestUrl extends AsyncTask<Void, Void, ProductResponse> {

        @Override
        protected ProductResponse doInBackground(Void... voids) {
            try {
                if (PARAM == 0) {
                    String URL_PRODUCT = Constants.API_PREFIX + Constants.API_PRODUCT_DETAILS_INDEX;
                    final String url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_PRODUCT);
                    for (int i = indexScroll; i < indexScroll + 3; i++) {
                        Category c = categoryResponse.getResponseData().get(i);
                        Category cat = new Category();
                        cat.setCategory(c.getCategory());
                        cat.setIndex(1);
                        ProductResponse p = (ProductResponse) Helper.postWebservice(url, cat, ProductResponse.class);
                        if (p != null && p.getStatusCode() == 1) {
                            cat.setProductArrayList(p.getResponseData());
                            resultsCategory.add(cat);
                        }
                    }
//                    }
                    return null;
                } else if (PARAM == 1) {
                    String URL_CATEGORY = Constants.API_PREFIX + Constants.API_CATEGORY;
                    final String url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_CATEGORY);
                    categoryResponse = (CategoryResponse) Helper.getWebserviceWithoutHeaders(url, CategoryResponse.class);
                    return null;
                } else if (PARAM == 2) {
                    String URL_STOCK_DETAILS = Constants.API_PREFIX + Constants.API_GET_STOCK;
                    final String url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_STOCK_DETAILS);
                    StockDetailsRequest sd = new StockDetailsRequest();
                    sd.setBarcode("");
                    sd.setItemCode(itemCode);
                    sdResponse = (StockDetailsReponse) Helper.postWebservice(url, sd, StockDetailsReponse.class);
                    return null;
                } else {
                    String URL_PRODUCT = Constants.API_PREFIX + Constants.API_PRODUCT_DETAILS_INDEX;
                    final String url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_PRODUCT);
                    singleCategory = new Category();
                    singleCategory.setCategory(cName);
                    singleCategory.setIndex(1);
                    return (ProductResponse) Helper.postWebservice(url, singleCategory, ProductResponse.class);
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
                if (categoryResponse != null && categoryResponse.getStatusCode() == 1) {
                    binding.emptyLayout.setVisibility(View.GONE);
                    binding.scrollView.setVisibility(View.VISIBLE);
                    initData(resultsCategory);
                } else {
                    binding.emptyLayout.setVisibility(View.VISIBLE);
                    binding.scrollView.setVisibility(View.GONE);
                    if (Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR) != null) {
                        Toast.makeText(getActivity(), Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR).toString(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), product.getStatusMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (PARAM == 1) {
                if (categoryResponse != null && categoryResponse.getStatusCode() == 1) {
                    setCategoryChips(categoryResponse.getResponseData());
                    PARAM = 0;
                    new RequestUrl().execute();
                } else {
                    getProgressDialog().dismiss();
                    binding.emptyLayout.setVisibility(View.VISIBLE);
                    binding.scrollView.setVisibility(View.GONE);
                    if (Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR) != null) {
                        Toast.makeText(getActivity(), Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR).toString(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), categoryResponse.getStatusMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (PARAM == 2) {
                getProgressDialog().dismiss();
                if (sdResponse != null) {
                    if (sdResponse.getStatusCode() == 1) {
                        showDialogAddToCart(dataInventory);
                    } else {
                        Toast.makeText(getActivity(), sdResponse.getStatusMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR) != null) {
                        Toast.makeText(getActivity(), Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR).toString(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), sdResponse.getStatusMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                if (product != null) {
                    if (product.getStatusCode() == 1) {
                        singleCategory.setProductArrayList(product.getResponseData());
                        singleResultCategory.add(singleCategory);
                        categoryAdapter.filterList(singleResultCategory);
                    } else {
                        Toast.makeText(getActivity(), product.getStatusMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR) != null) {
                        Toast.makeText(getActivity(), Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR).toString(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), sdResponse.getStatusMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        }

    }

    private void initialize() {
        setImageSlider();

        binding.edtSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Intent intent = new Intent(getActivity(), ProductSearchListActivity.class);
                    getActivity().startActivity(intent);
                    return true;
                }
                return true;
            }
        });

//         Scheme colors for animation
        binding.pullToRefresh.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light)
        );

        binding.btnReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });

        binding.pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                filterText = "";
                indexScroll = 0;
                resultsCategory = new ArrayList<>();
                categoryAdapter = null;
                binding.rvCategory.removeAllViews();
//                binding.imageSlider.removeAllViews();
                setImageSlider();
                PARAM = 1;
                new RequestUrl().execute();
                // To keep animation for 1 seconds
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Stop animation (This will be after 1 seconds)
                        binding.pullToRefresh.setRefreshing(false);
                    }
                }, 4000); // Delay in millis
            }
        });
    }

    private void setImageSlider() {
        SliderAdapter adapter = new SliderAdapter(getContext());

        List<SliderItem> sliderItemList = new ArrayList<>();
        SliderItem item = new SliderItem();
        item.setImageUrl(Constants.URL_IMAGE_1);
        sliderItemList.add(item);

        item = new SliderItem();
        item.setImageUrl(Constants.URL_IMAGE_2);
        sliderItemList.add(item);

        item = new SliderItem();
        item.setImageUrl(Constants.URL_IMAGE_3);
        sliderItemList.add(item);

        item = new SliderItem();
        item.setImageUrl(Constants.URL_IMAGE_4);
        sliderItemList.add(item);

        adapter.renewItems(sliderItemList);

        binding.imageSlider.setSliderAdapter(adapter);

        binding.imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        binding.imageSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        binding.imageSlider.setIndicatorSelectedColor(Color.WHITE);
        binding.imageSlider.setIndicatorUnselectedColor(Color.GRAY);
        binding.imageSlider.setScrollTimeInSec(3); //set scroll delay in seconds :
        binding.imageSlider.startAutoCycle();
    }

    private void filter(String text) {
        filterText = text;
        ArrayList<Category> filteredListCategory = new ArrayList<>();
        for (Category item : resultsCategory) {
            ArrayList<Product> filteredListProduct = new ArrayList<>();
            prodList = item.getProductArrayList();
            for (Product itemProduct : prodList) {
                if (itemProduct.getProductCategory().toLowerCase().contains(text.toLowerCase()) ||
                        itemProduct.getProductName().toLowerCase().contains(text.toLowerCase()) ||
                        itemProduct.getProductCategory().toLowerCase().contains(text.toLowerCase())) {
                    filteredListProduct.add(itemProduct);
                }
            }
            if (filteredListProduct.size() != 0) {
                filteredListCategory.add(new Category(item.getCategory(), filteredListProduct));
            }
        }
        categoryAdapter.filterList(filteredListCategory);
    }

    private void filterCategory(String text) {
        filterText = text;
        ArrayList<Category> filteredListCategory = new ArrayList<>();
        for (Category item : resultsCategory) {
            ArrayList<Product> filteredListProduct = new ArrayList<>();
            prodList = item.getProductArrayList();
            for (Product itemProduct : prodList) {
                if (itemProduct.getProductCategory().toLowerCase().contains(text.toLowerCase())) {
                    filteredListProduct.add(itemProduct);
                }
            }
            if (filteredListProduct.size() != 0) {
                filteredListCategory.add(new Category(item.getCategory(), filteredListProduct));
            }
        }
        if (filteredListCategory != null && filteredListCategory.size() != 0) {
            categoryAdapter.filterList(filteredListCategory);
        } else {
            singleResultCategory = new ArrayList<>();
            for (Category c : categoryResponse.getResponseData()) {
                if (c.getCategory().equals(text)) {
                    cName = c.getCategory();
                }
            }
            PARAM = 3;
            new RequestUrl().execute();
        }
    }

    private void initData(List<Category> responseData) {
        if (categoryAdapter != null && categoryAdapter.getItemCount() != 0) {
            categoryAdapter.filterList(resultsCategory);
            loading = false;
            binding.bottomProgressBar.setVisibility(View.GONE);
        } else {
            Helper.setItemParam(Constants.LIST_PRODUCT, resultsCategory);
            manager = new LinearLayoutManager(getContext());
            binding.rvCategory.setLayoutManager(manager);
            binding.rvCategory.setHasFixedSize(true);
            binding.rvCategory.setNestedScrollingEnabled(false);
            categoryAdapter = new CategoryAdapter(this, resultsCategory);
            binding.rvCategory.setAdapter(categoryAdapter);
            binding.scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    if (binding != null) {
                        View view = (View) binding.scrollView.getChildAt(binding.scrollView.getChildCount() - 1);

                        int diff = (view.getBottom() - (binding.scrollView.getHeight() + binding.scrollView
                                .getScrollY()));

                        if (diff == 0) {
                            if (!loading) {
                                if (filterText.equals("")) {
                                    binding.bottomProgressBar.setVisibility(View.VISIBLE);
                                    //bottom of list!
                                    indexScroll = indexScroll + 3;
                                    loading = true;
                                    PARAM = 0;
                                    new RequestUrl().execute();
                                } else {
                                    binding.bottomProgressBar.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                }
            });
            loading = false;
        }
    }

    public void setCategoryChips(List<Category> categories) {
        binding.chipGroup.removeAllViews();
        Chip allChip = (Chip) this.getLayoutInflater().inflate(R.layout.item_chip_brand, null, false);
        allChip.setText("All");
        allChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filter("");
            }
        });
        binding.chipGroup.addView(allChip);
        for (Category category :
                categories) {
            Chip mChip = (Chip) this.getLayoutInflater().inflate(R.layout.item_chip_brand, null, false);
            mChip.setText(category.getCategory());
            int paddingDp = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 10,
                    getResources().getDisplayMetrics()
            );
            mChip.setPadding(paddingDp, 0, paddingDp, 0);
            mChip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CompoundButton b = (CompoundButton) v;
                    String buttonText = b.getText().toString();
                    filterCategory(buttonText);
                }
            });
            binding.chipGroup.addView(mChip);
        }
    }

    public void checkWishList(Product data, boolean wishList) {
        for (Category item : resultsCategory) {
            for (Product productItem : item.getProductArrayList()) {
                if (productItem.getProductName().equals(data.getProductName())) {
                    productItem.setWishList(wishList);
                }
            }
        }
        Helper.setItemParam(Constants.LIST_PRODUCT, resultsCategory);
    }

    public void showPreview(Product data) {
        Helper.setItemParam(Constants.IMAGE_URL, data.getImageUrl());
        Intent intent = new Intent(getActivity(), PreviewImageActivity.class);
        startActivity(intent);
    }

    public void showDialogAddToCart(Product data) {
        if (bottomSheetDialog == null || !bottomSheetDialog.isShowing()) {
            bottomSheetDialog = Utils.showBottomSheetDialog(getActivity(), R.layout.bsheet_add_to_cart, (itemView, bottomSheet) -> {
                ImageView imgProduct = itemView.findViewById(R.id.imgProduct);
                ImageView imgBarcode = itemView.findViewById(R.id.imgBarcode);
                btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
                TextView txtPrice = itemView.findViewById(R.id.txtPrice);
                TextView labelTitle = itemView.findViewById(R.id.labelTitle);
                RecyclerView rvProductDetail = itemView.findViewById(R.id.rvProductDetail);
                showMore = itemView.findViewById(R.id.txtShowMore);
                TextView labelBarcode = itemView.findViewById(R.id.labelBarcode);
                EditText edtAmount = itemView.findViewById(R.id.edtAmount);
                ImageView btnAdd = itemView.findViewById(R.id.btnAdd);
                ImageView btnRemove = itemView.findViewById(R.id.btnRemove);
//                int temp = 0;
//                for (WMDetailStock stock : sdResponse.getResponseData().get(0).getDetailStocks()) {
//                    if (stock.getAvailableQty().equals("0")) {
//                        continue;
//                    } else {
//                        temp++;
//                    }
//                }
//                if (temp == 0) {
//                    btnAddToCart.setEnabled(false);
//                } else {
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
//                txtPrice.setText("$" + String.format("%.2f", price));
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
//                            txtPrice.setText("$" + String.format("%.2f", amount[0] * price));
                            txtPrice.setText("$" + Helper.doubleToStringNoDecimal(amount[0] * price));
                            data.setQty(amount[0]);
//                            data.setPrice(Double.parseDouble(String.format("%.2f", amount[0] * price)));
                        }

                    }
                });

                btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (amount[0] != 1) {
                            amount[0]--;
                            edtAmount.setText(String.valueOf(amount[0]));
//                            txtPrice.setText("$" + String.format("%.2f", amount[0] * price));
                            txtPrice.setText("$" + Helper.doubleToStringNoDecimal( amount[0] * price));
                            data.setQty(amount[0]);
//                            data.setPrice(Double.parseDouble(String.format("%.2f", amount[0] * price)));
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
//                            txtPrice.setText("$" + String.format("%.2f", Integer.parseInt(editable.toString()) * data.getPrice()));
                            txtPrice.setText("$" + Helper.doubleToStringNoDecimal(Integer.parseInt(editable.toString()) * data.getPrice()));
                            data.setQty(Integer.parseInt(editable.toString().trim()));
                        }
                    }
                });

                Glide.with(getContext())
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

                rvProductDetail.setLayoutManager(new LinearLayoutManager(getContext()));
                rvProductDetail.setHasFixedSize(true);

                productDetailAdapter = new ProductDetailAdapter(this.getContext(), productDetailArrayList);
                rvProductDetail.setAdapter(productDetailAdapter);
                rvProductDetail.setItemAnimator(new DefaultItemAnimator());
                rvProductDetail.setHasFixedSize(false);
                rvProductDetail.setNestedScrollingEnabled(false);

                btnAddToCart.setOnClickListener(v -> {
                    if (edtAmount.getText().toString().trim().isEmpty()) {
                        Toast.makeText(getActivity(), "Quantity cannot empty", Toast.LENGTH_SHORT).show();
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
                            showMore.setText(Html.fromHtml("<u>" + "Show more" + "</u>"));
                            expanded[0] = false;
                        }


                    }

                });

            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Helper.getItemParam(Constants.LIST_CART) != null) {
            ArrayList<Product> listProduct = (ArrayList<Product>) Helper.getItemParam(Constants.LIST_CART);
            sentCart(String.valueOf(listProduct.size()));
        }
    }

    private void showMoreDetail(List<WMDetailStock> data) {
        ProductDetail detail = new ProductDetail();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}