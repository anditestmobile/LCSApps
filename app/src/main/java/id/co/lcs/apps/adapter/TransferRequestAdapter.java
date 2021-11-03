package id.co.lcs.apps.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import id.co.lcs.apps.R;
import id.co.lcs.apps.activity.StockTakeActivity;
import id.co.lcs.apps.activity.TransferRequestActivity;
import id.co.lcs.apps.activity.WMStockActivity;
import id.co.lcs.apps.databinding.RowViewProductBinding;
import id.co.lcs.apps.databinding.RowViewStockTakeBinding;
import id.co.lcs.apps.databinding.RowViewTrBinding;
import id.co.lcs.apps.model.Product;
import id.co.lcs.apps.model.StockTake;
import id.co.lcs.apps.model.TRStock;
import id.co.lcs.apps.model.WMStock;


/**
 * Created by Samuel Gunawan on 3/14/2018.
 */

public class TransferRequestAdapter extends RecyclerView.Adapter<TransferRequestAdapter.DataObjectHolder> {

    private List<TRStock> dataList;
    private TransferRequestActivity mContext;

    public TransferRequestAdapter(TransferRequestActivity context, List<TRStock> stockList) {
        this.dataList = stockList;
        mContext = context;
    }

    class DataObjectHolder extends RecyclerView.ViewHolder {
        private RowViewTrBinding binding;

        DataObjectHolder(RowViewTrBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

    @NonNull
    @Override
    public TransferRequestAdapter.DataObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TransferRequestAdapter.DataObjectHolder(RowViewTrBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final TransferRequestAdapter.DataObjectHolder holder, final int position) {
        final TRStock data = dataList.get(position);
        holder.binding.txtItemNumber.setText(data.getIdMaterial());
        holder.binding.txtMatDesc.setText(data.getDesc());
        holder.binding.edtQTY.setText(data.getAvailableQty());
        holder.binding.edtQTY.setTag(position);
        holder.binding.edtQTY.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(holder.binding.edtQTY.hasFocus()) {
                    mContext.changeQTY(s.toString(), Integer.parseInt(holder.binding.edtQTY.getTag().toString()));
                }
            }
        });
        holder.binding.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.deleteItem(data, position);
            }
        });
        if(data.getUom() != null && !data.getUom().trim().isEmpty()){
            holder.binding.txtUom.setVisibility(View.VISIBLE);
            holder.binding.txtUom.setText(data.getUom());
        }else{
            holder.binding.txtUom.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }

    public void filterList(ArrayList<TRStock> filteredList) {
        dataList = filteredList;
        notifyDataSetChanged();
    }
}


