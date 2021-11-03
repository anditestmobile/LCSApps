package id.co.lcs.apps.adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import id.co.lcs.apps.activity.GoodReceiptActivity;
import id.co.lcs.apps.activity.GoodReceiptSupplierActivity;
import id.co.lcs.apps.databinding.RowViewGrBinding;
import id.co.lcs.apps.databinding.RowViewGrSupplierBinding;
import id.co.lcs.apps.model.GoodReceipt;
import id.co.lcs.apps.model.GoodReceiptSupplier;

public class GoodReceiptSupplierAdapter extends RecyclerView.Adapter<GoodReceiptSupplierAdapter.DataObjectHolder> {

    private List<GoodReceiptSupplier> dataList;
    private GoodReceiptSupplierActivity mContext;

    public GoodReceiptSupplierAdapter(GoodReceiptSupplierActivity context, List<GoodReceiptSupplier> grList) {
        this.dataList = grList;
        mContext = context;
    }

    class DataObjectHolder extends RecyclerView.ViewHolder {
        private RowViewGrSupplierBinding binding;

        DataObjectHolder(RowViewGrSupplierBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

    @NonNull
    @Override
    public GoodReceiptSupplierAdapter.DataObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GoodReceiptSupplierAdapter.DataObjectHolder(RowViewGrSupplierBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final GoodReceiptSupplierAdapter.DataObjectHolder holder, final int position) {
        final GoodReceiptSupplier data = dataList.get(position);
        holder.binding.txtCardCode.setText(data.getCardCode());
        holder.binding.txtPONumber.setText(data.getPoNumber());
        holder.binding.txtDocDate.setText(data.getDocDate());
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

    public void filterList(ArrayList<GoodReceiptSupplier> filteredList) {
        dataList = filteredList;
        notifyDataSetChanged();
    }


}
