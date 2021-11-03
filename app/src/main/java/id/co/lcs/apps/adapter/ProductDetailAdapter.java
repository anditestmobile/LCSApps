package id.co.lcs.apps.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import id.co.lcs.apps.databinding.RowViewProduct1ContentBinding;
import id.co.lcs.apps.model.ProductDetail;

/**
 * Created by TED on 16-Jul-20
 */

public class ProductDetailAdapter extends RecyclerView.Adapter<ProductDetailAdapter.DataObjectHolder> {
    private ArrayList<ProductDetail> mDataSet;
    private Context mContext;


    public ProductDetailAdapter(Context context, ArrayList<ProductDetail> data) {
        mDataSet = data;
        mContext = context;
    }
    class DataObjectHolder extends RecyclerView.ViewHolder {
        private RowViewProduct1ContentBinding binding;

        DataObjectHolder(RowViewProduct1ContentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

    @NonNull
    @Override
    public DataObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new DataObjectHolder(RowViewProduct1ContentBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final DataObjectHolder holder, final int position) {
        final ProductDetail data = mDataSet.get(position);

        holder.binding.labelBrand.setText(data.getLabel());
        holder.binding.txtBrand.setText(data.getValue());
    }


    @Override
    public int getItemCount() {
        return mDataSet != null? mDataSet.size() : 0;
    }

    public void filterList(ArrayList<ProductDetail> filteredList) {
        mDataSet = filteredList;
        notifyDataSetChanged();
    }
}

