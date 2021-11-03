package id.co.lcs.apps.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import id.co.lcs.apps.R;
import id.co.lcs.apps.activity.TransferRequestActivity;
import id.co.lcs.apps.activity.WMStockActivity;
import id.co.lcs.apps.databinding.RowViewTrBinding;
import id.co.lcs.apps.databinding.RowViewWmstockBinding;
import id.co.lcs.apps.model.TRStock;
import id.co.lcs.apps.model.WMDetailStock;
import id.co.lcs.apps.model.WMStock;


/**
 * Created by Samuel Gunawan on 3/14/2018.
 */

public class WMStockRecyclerAdapter extends RecyclerView.Adapter<WMStockRecyclerAdapter.DataObjectHolder> {

    private List<WMDetailStock> dataList;
    private WMStockActivity mContext;

    public WMStockRecyclerAdapter(WMStockActivity context, List<WMDetailStock> stockList) {
        this.dataList = stockList;
        mContext = context;
    }

    class DataObjectHolder extends RecyclerView.ViewHolder {
        private RowViewWmstockBinding binding;

        DataObjectHolder(RowViewWmstockBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

    @NonNull
    @Override
    public WMStockRecyclerAdapter.DataObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WMStockRecyclerAdapter.DataObjectHolder(RowViewWmstockBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final WMStockRecyclerAdapter.DataObjectHolder holder, final int position) {
        final WMDetailStock data = dataList.get(position);
        holder.binding.txtQty.setText(data.getAvailableQty());
        holder.binding.txtUom.setText(data.getUom());
        holder.binding.txtWhs.setText(data.getWhs());
        holder.binding.txtCmQty.setText(data.getCommitedQty());
        holder.binding.rootCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.showDetail(data);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }

    public void filterList(ArrayList<WMDetailStock> filteredList) {
        dataList = filteredList;
        notifyDataSetChanged();
    }
}