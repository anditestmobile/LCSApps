package id.co.lcs.apps.adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import id.co.lcs.apps.activity.GoodReceiptActivity;
import id.co.lcs.apps.activity.QuotationHistoryActivity;
import id.co.lcs.apps.databinding.RowViewGrBinding;
import id.co.lcs.apps.databinding.RowViewQuotationHistoryBinding;
import id.co.lcs.apps.helper.Helper;
import id.co.lcs.apps.model.GoodReceipt;
import id.co.lcs.apps.model.SalesOrderRequest;

public class QuotationHistoryAdapter extends RecyclerView.Adapter<QuotationHistoryAdapter.DataObjectHolder> {

    private List<SalesOrderRequest> dataList;
    private QuotationHistoryActivity mContext;

    public QuotationHistoryAdapter(QuotationHistoryActivity context, List<SalesOrderRequest> quoList) {
        this.dataList = quoList;
        mContext = context;
    }

    class DataObjectHolder extends RecyclerView.ViewHolder {
        private RowViewQuotationHistoryBinding binding;

        DataObjectHolder(RowViewQuotationHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

    @NonNull
    @Override
    public QuotationHistoryAdapter.DataObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new QuotationHistoryAdapter.DataObjectHolder(RowViewQuotationHistoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final QuotationHistoryAdapter.DataObjectHolder holder, final int position) {
        final SalesOrderRequest data = dataList.get(position);
        holder.binding.txtQuoNo.setText(data.getQuotation());
        holder.binding.txtDate.setText(Helper.changeFormatDate("yyyyMMdd", "dd-MM-yyyy", data.getDate()));
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

    public void filterList(ArrayList<SalesOrderRequest> filteredList) {
        dataList = filteredList;
        notifyDataSetChanged();
    }


}
