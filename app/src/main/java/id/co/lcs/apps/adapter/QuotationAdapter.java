package id.co.lcs.apps.adapter;

/**
 * Created by TED on 16-Jul-20
 */

import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import id.co.lcs.apps.R;
import id.co.lcs.apps.activity.CartActivity;
import id.co.lcs.apps.activity.QuotationActivity;
import id.co.lcs.apps.databinding.RowViewCart1Binding;
import id.co.lcs.apps.databinding.RowViewQuotationBinding;
import id.co.lcs.apps.model.Product;

/**
 * Created by TED on 15-Jul-20
 */

public class QuotationAdapter extends RecyclerView.Adapter<QuotationAdapter.DataObjectHolder> {
    private ArrayList<Product> mDataSet;
    private QuotationActivity mContext;
    private ProductDetailAdapter productDetailAdapter;


    public QuotationAdapter(QuotationActivity context, ArrayList<Product> data) {
        mDataSet = data;
        mContext = context;
    }

    class DataObjectHolder extends RecyclerView.ViewHolder {
        private RowViewQuotationBinding binding;

        DataObjectHolder(RowViewQuotationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

    @NonNull
    @Override
    public DataObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new DataObjectHolder(RowViewQuotationBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final DataObjectHolder holder, final int position) {
        final Product data = mDataSet.get(position);

        holder.binding.txtIndex.setText(String.valueOf(position + 1));
        Glide.with(mContext)
                .applyDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.drawable.ic_no_image)
                        .error(R.drawable.ic_no_image))
                .asBitmap()
                .load(data.getImageUrl())
                .into(holder.binding.imgProduct);

//        holder.binding.txtPrice.setText("$10");
        holder.binding.txtShowMore.setText(Html.fromHtml("<u>" + "Show more" + "</u>"));

        /*product detail*/
        holder.binding.rvProductDetail.setLayoutManager(new LinearLayoutManager(mContext));
        holder.binding.rvProductDetail.setHasFixedSize(true);
        holder.binding.rvProductDetail2.setLayoutManager(new LinearLayoutManager(mContext));
        holder.binding.rvProductDetail2.setHasFixedSize(true);

        productDetailAdapter = new ProductDetailAdapter(mContext, data.getProductDetailArrayList());
        holder.binding.rvProductDetail.setAdapter(productDetailAdapter);
        holder.binding.rvProductDetail.setItemAnimator(new DefaultItemAnimator());
        holder.binding.rvProductDetail.setHasFixedSize(false);
        holder.binding.rvProductDetail.setNestedScrollingEnabled(false);

        productDetailAdapter = new ProductDetailAdapter(mContext, data.getProductDetailArrayListMore());
        holder.binding.rvProductDetail2.setAdapter(productDetailAdapter);
        holder.binding.rvProductDetail2.setItemAnimator(new DefaultItemAnimator());
        holder.binding.rvProductDetail2.setHasFixedSize(false);
        holder.binding.rvProductDetail2.setNestedScrollingEnabled(false);

//        double price = 88;
//        holder.binding.txtPrice.setText("$" + price);
        holder.binding.edtAmount.setText(String.valueOf(data.getQty()));
        holder.binding.txtPrice.setText("S$"+ String.format("%.2f",data.getQty() * data.getPrice()));
        holder.binding.checkBoxCart.setChecked(data.isStatusCheckout());

        final int[] amount = {data.getQty()};
        holder.binding.btnAdd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (amount[0] >= 1) {
                    amount[0]++;
                    holder.binding.edtAmount.setText(String.valueOf(amount[0]));
                    holder.binding.txtPrice.setText("S$" + String.format("%.2f",amount[0] * data.getPrice()));
                    data.setQty(amount[0]);
//                    data.setPrice(Double.parseDouble(String.valueOf(amount[0] * price)));
                    mContext.updateTotalChart(data);
                }
                return false;
            }
        });

        holder.binding.btnRemove.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (amount[0] != 1) {
                    amount[0]--;
                    holder.binding.edtAmount.setText(String.valueOf(amount[0]));
                    holder.binding.txtPrice.setText("S$" + String.format("%.2f",amount[0] * data.getPrice()));
                    data.setQty(amount[0]);
//                    data.setPrice(Double.parseDouble(String.valueOf(amount[0] * price)));
                    mContext.updateTotalChart(data);
                }
                return false;
            }
        });

        holder.binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.showDialogConfirmationRemove("Do you want to remove this product from cart?", data, position);
            }
        });
        holder.binding.checkBoxCart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                data.setStatusCheckout(b);
                mContext.updateTotalChart(data);
            }
        });

        final boolean[] expanded = {false};
        holder.binding.txtShowMore.setOnClickListener(view -> {
            mContext.getInventoryStock(data);
        });

        holder.binding.imgProduct.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mContext.showPreview(data);
                return false;
            }
        });

        holder.binding.edtAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().isEmpty()) {
                    data.setQty(Integer.parseInt(editable.toString().trim()));
                }else{
                    data.setQty(0);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mDataSet != null ? mDataSet.size() : 0;
    }

    public void filterList(ArrayList<Product> filteredList) {
        mDataSet = filteredList;
        notifyDataSetChanged();
    }
}

