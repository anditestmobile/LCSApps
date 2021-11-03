package id.co.lcs.apps.adapter;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import id.co.lcs.apps.R;
import id.co.lcs.apps.activity.GoodReceiptActivity;
import id.co.lcs.apps.activity.StockTakeActivity;
import id.co.lcs.apps.databinding.RowViewGrBinding;
import id.co.lcs.apps.databinding.RowViewStockTakeBinding;
import id.co.lcs.apps.model.GoodReceipt;
import id.co.lcs.apps.model.StockTake;

public class StockTakeAdapter extends RecyclerView.Adapter<StockTakeAdapter.DataObjectHolder> {

    private List<StockTake> dataList;
    private StockTakeActivity mContext;

    public StockTakeAdapter(StockTakeActivity context, List<StockTake> stockTakeList) {
        this.dataList = stockTakeList;
        mContext = context;
    }

    class DataObjectHolder extends RecyclerView.ViewHolder {
        private RowViewStockTakeBinding binding;

        DataObjectHolder(RowViewStockTakeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

    @NonNull
    @Override
    public StockTakeAdapter.DataObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StockTakeAdapter.DataObjectHolder(RowViewStockTakeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final StockTakeAdapter.DataObjectHolder holder, final int position) {
        final StockTake data = dataList.get(position);
        holder.binding.txtItemNr.setText(data.getItemCode());
        holder.binding.txtItem.setText(data.getItemName());
        holder.binding.edtAmount.setText(String.valueOf(data.getQty()));

        final int[] amount = {data.getQty()};
        holder.binding.btnAdd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (amount[0] >= 1) {
                    amount[0]++;
                    holder.binding.edtAmount.setText(String.valueOf(amount[0]));
                    data.setQty(amount[0]);
//                    mContext.updateTotalChart(data);
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
                    data.setQty(amount[0]);
//                    mContext.updateTotalChart(data);
                }
                return false;
            }
        });
        holder.binding.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.showDialogConfirmationRemove(data, position, 0);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }

    public void filterList(ArrayList<StockTake> filteredList) {
        dataList = filteredList;
        notifyDataSetChanged();
    }
}
