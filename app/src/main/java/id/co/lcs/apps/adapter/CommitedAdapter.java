package id.co.lcs.apps.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import id.co.lcs.apps.activity.WMStockActivity;
import id.co.lcs.apps.databinding.RowViewCommitedBinding;
import id.co.lcs.apps.databinding.RowViewWmstockBinding;
import id.co.lcs.apps.model.Commited;
import id.co.lcs.apps.model.WMDetailStock;


/**
 * Created by Samuel Gunawan on 3/14/2018.
 */

public class CommitedAdapter extends RecyclerView.Adapter<CommitedAdapter.DataObjectHolder> {

    private List<Commited> dataList;
    private WMStockActivity mContext;

    public CommitedAdapter(WMStockActivity context, List<Commited> commitedList) {
        this.dataList = commitedList;
        mContext = context;
    }

    class DataObjectHolder extends RecyclerView.ViewHolder {
        private RowViewCommitedBinding binding;

        DataObjectHolder(RowViewCommitedBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

    @NonNull
    @Override
    public CommitedAdapter.DataObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommitedAdapter.DataObjectHolder(RowViewCommitedBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final CommitedAdapter.DataObjectHolder holder, final int position) {
        final Commited data = dataList.get(position);
        holder.binding.txtDocType.setText(data.getDocType());
        holder.binding.txtDocNum.setText(data.getDocNum());
        holder.binding.txtQty.setText(data.getCommitedQty());
    }

    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }

    public void filterList(ArrayList<Commited> filteredList) {
        dataList = filteredList;
        notifyDataSetChanged();
    }
}