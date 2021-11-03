package id.co.lcs.apps.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


import id.co.lcs.apps.activity.ProductListActivity;
import id.co.lcs.apps.constants.Constants;
import id.co.lcs.apps.databinding.RowViewCategoryBinding;
import id.co.lcs.apps.fragment.HomeFragment;
import id.co.lcs.apps.helper.Helper;
import id.co.lcs.apps.model.Category;

/**
 * Created by TED on 15-Jul-20
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.DataObjectHolder> {
    private ArrayList<Category> mDataSet;
    private HomeFragment mContext;
    private ProductAdapter productAdapter;


    public CategoryAdapter(HomeFragment context, ArrayList<Category> data) {
        mDataSet = data;
        mContext = context;
    }
    class DataObjectHolder extends RecyclerView.ViewHolder {
        private RowViewCategoryBinding binding;

        DataObjectHolder(RowViewCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

    @NonNull
    @Override
    public DataObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new DataObjectHolder(RowViewCategoryBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final DataObjectHolder holder, final int position) {
        final Category data = mDataSet.get(position);
        holder.binding.txtCategory.setText(data.getCategory());

        holder.binding.rvProduct.setLayoutManager(new LinearLayoutManager(mContext.getContext(), LinearLayoutManager.HORIZONTAL, false));
        holder.binding.rvProduct.setHasFixedSize(true);

        productAdapter = new ProductAdapter(mContext, data.getProductArrayList());
        holder.binding.rvProduct.setAdapter(productAdapter);
        holder.binding.txtShowMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helper.setItemParam(Constants.CATEGORY, data);
                Intent intent = new Intent(mContext.getContext(), ProductListActivity.class);
                mContext.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void filterList(ArrayList<Category> filteredList) {
        mDataSet = filteredList;
        notifyDataSetChanged();
    }
}

