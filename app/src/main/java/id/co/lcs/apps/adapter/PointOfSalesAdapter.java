package id.co.lcs.apps.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

import id.co.lcs.apps.R;
import id.co.lcs.apps.activity.PointOfSalesActivity;
import id.co.lcs.apps.activity.ProductListActivity;
import id.co.lcs.apps.databinding.RowViewListProductBinding;
import id.co.lcs.apps.databinding.RowViewPosBinding;
import id.co.lcs.apps.model.Product;

/**
 * Created by TED on 15-Jul-20
 */

public class PointOfSalesAdapter extends RecyclerView.Adapter<PointOfSalesAdapter.DataObjectHolder> {
    private ArrayList<Product> mDataSet;
    private PointOfSalesActivity mContext;
    private DecimalFormatSymbols otherSymbols;
    private DecimalFormat format;

    public PointOfSalesAdapter(PointOfSalesActivity context, ArrayList<Product> data) {
        mDataSet = data;
        mContext = context;
    }

    class DataObjectHolder extends RecyclerView.ViewHolder {
        private RowViewPosBinding binding;

        DataObjectHolder(@NonNull RowViewPosBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public DataObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DataObjectHolder(RowViewPosBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final DataObjectHolder holder, final int position) {
        final Product data = mDataSet.get(position);
        if(!data.getImageUrl().equals("false")) {
            Glide.with(mContext)
                    .applyDefaultRequestOptions(new RequestOptions()
                            .placeholder(R.drawable.ic_no_image)
                            .error(R.drawable.ic_no_image))
                    .asBitmap()
                    .load(data.getImageUrl())
                    .into(holder.binding.imgProduct);
        }else{
            Glide.with(mContext)
                    .applyDefaultRequestOptions(new RequestOptions()
                            .placeholder(R.drawable.ic_no_image)
                            .error(R.drawable.ic_no_image))
                    .asBitmap()
                    .load(data.getImageUrl())
                    .into(holder.binding.imgProduct);
        }
        holder.binding.txtProductName.setText(data.getProductName());
        holder.binding.txtProductCategory.setText(data.getProductCategory());
        holder.binding.txtProductCode.setText(data.getProductCode());

        holder.binding.cvProduct.setOnClickListener(view -> mContext.getInventoryStock(data));
    }


    @Override
    public int getItemCount() {
        return mDataSet != null ? mDataSet.size() : 0;
    }

    public void filterList(ArrayList<Product> filteredList) {
        mDataSet = filteredList;
        notifyDataSetChanged();
    }

    protected void setFormatSeparator() {
        otherSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');
        format = new DecimalFormat("#,###,###,###.##", otherSymbols);
        format.setDecimalSeparatorAlwaysShown(false);
    }
}

