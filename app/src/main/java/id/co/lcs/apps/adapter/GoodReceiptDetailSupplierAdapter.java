package id.co.lcs.apps.adapter;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import id.co.lcs.apps.activity.GoodReceiptDetailActivity;
import id.co.lcs.apps.activity.GoodReceiptSupplierActivity;
import id.co.lcs.apps.activity.GoodReceiptSupplierDetailActivity;
import id.co.lcs.apps.databinding.RowViewGrDetailBinding;
import id.co.lcs.apps.databinding.RowViewGrSupplierBinding;
import id.co.lcs.apps.databinding.RowViewGrSupplierDetailBinding;
import id.co.lcs.apps.model.GoodReceiptDetail;
import id.co.lcs.apps.model.GoodReceiptSupplier;
import id.co.lcs.apps.model.GoodReceiptSupplierDetail;

public class GoodReceiptDetailSupplierAdapter extends RecyclerView.Adapter<GoodReceiptDetailSupplierAdapter.DataObjectHolder> {

    private List<GoodReceiptSupplierDetail> dataList;
    private GoodReceiptSupplierDetailActivity mContext;

    public GoodReceiptDetailSupplierAdapter(GoodReceiptSupplierDetailActivity context, List<GoodReceiptSupplierDetail> grDetailList) {
        this.dataList = grDetailList;
        mContext = context;
    }

    class DataObjectHolder extends RecyclerView.ViewHolder {
        private RowViewGrSupplierDetailBinding binding;

        DataObjectHolder(RowViewGrSupplierDetailBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.chBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    dataList.get((Integer) binding.chBox.getTag()).setChecked(isChecked);
                }
            });
            binding.txtQty.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(!s.toString().isEmpty() && !s.toString().equals(dataList.get((Integer) binding.txtQty.getTag()).getGrQty())){
                        int actualQty = (int) Double.parseDouble(dataList.get((Integer) binding.txtQty.getTag()).getGrQty());
                        int grQty = (int) Double.parseDouble(s.toString());
                        if (grQty <= actualQty) {
                            mContext.changeQTY(s.toString(), dataList.get(Integer.parseInt(binding.txtQty.getTag().toString())));
                        }else{
                            Toast.makeText(mContext, "GR Supplier Quantity cannot bigger then Actual Quantity("
                                    + dataList.get((Integer) binding.txtQty.getTag()).getGrQty() + ")", Toast.LENGTH_SHORT).show();
                            binding.txtQty.setText(dataList.get((Integer) binding.txtQty.getTag()).getGrQty());
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            binding.edtBatch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!binding.txtQty.getText().toString().trim().isEmpty() && TextUtils.isDigitsOnly(binding.txtQty.getText())){
                        mContext.changeBatch(dataList.get((Integer) binding.edtBatch.getTag()), s.toString());
                    }else{
                        Toast.makeText(mContext, "Quantity cannot empty or must be number", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            binding.btnSN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!binding.txtQty.getText().toString().trim().isEmpty()){
                        mContext.changeSN((Integer) binding.btnSN.getTag());
                    }else{
                        Toast.makeText(mContext, "Quantity cannot empty or must be number", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public GoodReceiptDetailSupplierAdapter.DataObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GoodReceiptDetailSupplierAdapter.DataObjectHolder(RowViewGrSupplierDetailBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final GoodReceiptDetailSupplierAdapter.DataObjectHolder holder, final int position) {
        final GoodReceiptSupplierDetail data = dataList.get(position);
        holder.binding.txtItemNumber.setText(data.getItemNumber());
        holder.binding.txtItem.setText(data.getItem());
        holder.binding.txtQty.setTag(position);
        holder.binding.edtBatch.setTag(position);
        holder.binding.btnSN.setTag(position);
        holder.binding.txtQty.setText(data.getQty());
        holder.binding.chBox.setTag(position);
        holder.binding.chBox.setChecked(data.isChecked());
        if(data.getItemType().equals("Batch")){
            holder.binding.itemTypeSN.setVisibility(View.GONE);
            holder.binding.itemTypeBatch.setVisibility(View.VISIBLE);
        }else if(data.getItemType().equals("Serial")){
            holder.binding.itemTypeBatch.setVisibility(View.GONE);
            holder.binding.itemTypeSN.setVisibility(View.VISIBLE);
        }else{
            holder.binding.itemTypeBatch.setVisibility(View.GONE);
            holder.binding.itemTypeSN.setVisibility(View.GONE);
        }
        holder.binding.edtRejectedQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().equals("")){
                    mContext.changeRejectedQTY(position, Double.parseDouble(s.toString()));
                }else{
                    mContext.changeRejectedQTY(position, 0);
                }
            }
        });
        holder.binding.edtShortageQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().equals("")) {
                    mContext.changeShortageQTY(position, Double.parseDouble(s.toString()));
                }else{
                    mContext.changeShortageQTY(position, 0);
                }
            }
        });
        holder.binding.edtRejectedQty.setText(String.valueOf(data.getRejectedQty()));
        holder.binding.edtShortageQty.setText(String.valueOf(data.getShortageQty()));

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

    public void filterList(ArrayList<GoodReceiptSupplierDetail> filteredList) {
        dataList = filteredList;
        notifyDataSetChanged();
    }


}
