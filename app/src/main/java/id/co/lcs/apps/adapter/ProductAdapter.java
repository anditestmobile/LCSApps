package id.co.lcs.apps.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import id.co.lcs.apps.R;
import id.co.lcs.apps.constants.Constants;
import id.co.lcs.apps.databinding.RowViewProductBinding;
import id.co.lcs.apps.fragment.HomeFragment;
import id.co.lcs.apps.helper.Helper;
import id.co.lcs.apps.model.Product;

/**
 * Created by TED on 15-Jul-20
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.DataObjectHolder> {
    private ArrayList<Product> mDataSet;
    private HomeFragment mContext;


    public ProductAdapter(HomeFragment context, ArrayList<Product> data) {
        mDataSet = data;
        mContext = context;
    }
    class DataObjectHolder extends RecyclerView.ViewHolder {
        private RowViewProductBinding binding;

        DataObjectHolder(RowViewProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

    @NonNull
    @Override
    public DataObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new DataObjectHolder(RowViewProductBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final DataObjectHolder holder, final int position) {
        final Product data = mDataSet.get(position);

        Glide.with(mContext)
                .applyDefaultRequestOptions(new RequestOptions()
                .placeholder(R.drawable.ic_no_image)
                .error(R.drawable.ic_no_image))
                .load(data.getImageUrl())
                .into(holder.binding.imgProduct);
        holder.binding.txtProductName.setText(data.getProductName());
        holder.binding.txtProductCategory.setText(data.getProductCategory());
        holder.binding.wishList.setChecked(data.isWishList());
        holder.binding.txtProductCode.setText(data.getProductCode());
        holder.binding.txtPrice.setText("$" + Helper.doubleToStringNoDecimal(data.getPrice()));

        holder.binding.wishList.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                data.setWishList(b);
                mContext.checkWishList(data, b);
            }
        });

        holder.binding.rootProduct.setOnClickListener(view -> mContext.getInventoryStock(data));
    }


    @Override
    public int getItemCount() {
        return mDataSet != null? mDataSet.size() : 0;
    }

    public void filterList(ArrayList<Product> filteredList) {
        mDataSet = filteredList;
        notifyDataSetChanged();
    }
}

