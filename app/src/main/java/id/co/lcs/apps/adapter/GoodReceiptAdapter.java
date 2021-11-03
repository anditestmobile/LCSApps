package id.co.lcs.apps.adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import id.co.lcs.apps.activity.GoodReceiptActivity;
import id.co.lcs.apps.databinding.RowViewGrBinding;
import id.co.lcs.apps.model.GoodReceipt;

public class GoodReceiptAdapter extends RecyclerView.Adapter<GoodReceiptAdapter.DataObjectHolder> {

    private List<GoodReceipt> dataList;
    private GoodReceiptActivity mContext;

    public GoodReceiptAdapter(GoodReceiptActivity context, List<GoodReceipt> grList) {
        this.dataList = grList;
        mContext = context;
    }

    class DataObjectHolder extends RecyclerView.ViewHolder {
        private RowViewGrBinding binding;

        DataObjectHolder(RowViewGrBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

    @NonNull
    @Override
    public GoodReceiptAdapter.DataObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GoodReceiptAdapter.DataObjectHolder(RowViewGrBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final GoodReceiptAdapter.DataObjectHolder holder, final int position) {
        final GoodReceipt data = dataList.get(position);
        holder.binding.txtTRNo.setText(data.getTrNo());
        holder.binding.txtFromWH.setText(data.getFromWh());
        holder.binding.txtToWH.setText(data.getToWh());
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

    public void filterList(ArrayList<GoodReceipt> filteredList) {
        dataList = filteredList;
        notifyDataSetChanged();
    }


}
