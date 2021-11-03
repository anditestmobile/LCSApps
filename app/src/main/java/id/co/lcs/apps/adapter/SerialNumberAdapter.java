package id.co.lcs.apps.adapter;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import id.co.lcs.apps.activity.SerialNumberActivity;
import id.co.lcs.apps.activity.StockTakeActivity;
import id.co.lcs.apps.databinding.RowViewSerialNumberBinding;
import id.co.lcs.apps.databinding.RowViewStockTakeBinding;
import id.co.lcs.apps.model.Batch;
import id.co.lcs.apps.model.SerialNumber;
import id.co.lcs.apps.model.StockTake;

public class SerialNumberAdapter extends RecyclerView.Adapter<SerialNumberAdapter.DataObjectHolder> {

    private List<SerialNumber> dataList;
    private SerialNumberActivity mContext;

    public SerialNumberAdapter(SerialNumberActivity context, List<SerialNumber> serialNumbers) {
        this.dataList = serialNumbers;
        mContext = context;
    }

    class DataObjectHolder extends RecyclerView.ViewHolder {
        private RowViewSerialNumberBinding binding;

        DataObjectHolder(RowViewSerialNumberBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.edtSN.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    mContext.changeSN(s.toString(), Integer.parseInt(binding.edtSN.getTag().toString()));
                }
            });
        }

    }

    @NonNull
    @Override
    public SerialNumberAdapter.DataObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SerialNumberAdapter.DataObjectHolder(RowViewSerialNumberBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final SerialNumberAdapter.DataObjectHolder holder, final int position) {
        final SerialNumber data = dataList.get(position);
        holder.binding.edtSN.setTag(position);
        holder.binding.edtSN.setText(data.getSerialNo());
        holder.binding.txtNo.setText(String.valueOf(position+1) + ".");
        holder.binding.edtSN.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mContext.changeSNPosition(position);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }

    public void filterList(ArrayList<SerialNumber> filteredList) {
        dataList = filteredList;
        notifyDataSetChanged();
    }
}
