package id.co.lcs.apps.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import id.co.lcs.apps.activity.GoodReceiptDetailActivity;
import id.co.lcs.apps.databinding.RowViewGrDetailBinding;
import id.co.lcs.apps.model.GoodReceiptDetail;
import id.co.lcs.apps.utils.CustomEditText;

public class GoodReceiptDetailAdapter extends RecyclerView.Adapter<GoodReceiptDetailAdapter.DataObjectHolder> {

    private List<GoodReceiptDetail> dataList;
    private GoodReceiptDetailActivity mContext;
    private boolean clicked = false;
    private boolean clickCh = false;
    private List<RecyclerView.Adapter> listAdapter;

    public GoodReceiptDetailAdapter(GoodReceiptDetailActivity context, List<GoodReceiptDetail> grDetailList) {
        this.dataList = grDetailList;
        this.listAdapter = new ArrayList<>();
        mContext = context;
    }

    class DataObjectHolder extends RecyclerView.ViewHolder {
        private RowViewGrDetailBinding binding;

        DataObjectHolder(RowViewGrDetailBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.chBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    dataList.get((Integer) binding.chBox.getTag()).setChecked(isChecked);
                }
            });

            binding.txtBatchNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(dataList.get((Integer) binding.txtBatchNo.getTag()).isExpand()) {
                        dataList.get((Integer) binding.txtBatchNo.getTag()).setExpand(false);
                        binding.divider.setVisibility(View.VISIBLE);
                        binding.layoutListBatch.setVisibility(View.GONE);
                    }else {
                        dataList.get((Integer) binding.txtBatchNo.getTag()).setExpand(true);
                        binding.divider.setVisibility(View.GONE);
                        binding.layoutListBatch.setVisibility(View.VISIBLE);
                    }
                }
            });
            binding.btnAddBatch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.getDataBatchOrSerial(dataList.get((Integer)binding.btnAddBatch.getTag()));
                }
            });
        }
    }

    @NonNull
    @Override
    public GoodReceiptDetailAdapter.DataObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GoodReceiptDetailAdapter.DataObjectHolder(RowViewGrDetailBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final GoodReceiptDetailAdapter.DataObjectHolder holder, final int position) {
        final GoodReceiptDetail data = dataList.get(position);
        holder.binding.txtItemNumber.setText(data.getItemNumber());
        holder.binding.txtItem.setText(data.getItemName());
        holder.binding.txtQty.setText(data.getQty());
        holder.binding.txtQty.setTag(position);
        holder.binding.chBox.setTag(position);
        holder.binding.txtBatchNo.setTag(position);
        holder.binding.chBox.setChecked(data.isChecked());
        holder.binding.btnAddBatch.setTag(position);
        holder.binding.txtBarcode.setText(data.getBarcode());
        holder.binding.txtQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (holder.binding.txtQty.hasFocus()) {
                    mContext.changeQTY(s.toString(), dataList.get(Integer.parseInt(holder.binding.txtQty.getTag().toString())));
                }
            }
        });
        if (!data.getItemType().equals("None")) {
            holder.binding.lBatchSerial.setVisibility(View.VISIBLE);
            if (data.getItemType().equals("Batch")) {
                GoodReceiptDetailBatchAdapter mAdapter = null;
                if(listAdapter.size() - 1 >= Integer.parseInt(holder.binding.btnAddBatch.getTag().toString())) {
                    if (listAdapter.get(Integer.parseInt(holder.binding.btnAddBatch.getTag().toString())) != null) {
                        mAdapter = (GoodReceiptDetailBatchAdapter) listAdapter.get(Integer.parseInt(holder.binding.btnAddBatch.getTag().toString()));
                    }
                }
                holder.binding.txtHeaderTitle.setText("Batch No.");
                holder.binding.txtBatchNo.setText("Batch No.");
                holder.binding.btnAddBatch.setText("+ Batch No.");
                if(mAdapter != null) {
                    mAdapter = new GoodReceiptDetailBatchAdapter(mContext, data);
                    holder.binding.recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                    holder.binding.recyclerView.setHasFixedSize(true);
                    holder.binding.recyclerView.setAdapter(mAdapter);
                    listAdapter.set(Integer.parseInt(holder.binding.btnAddBatch.getTag().toString()), mAdapter);
                }else{
                    mAdapter = new GoodReceiptDetailBatchAdapter(mContext, data);
                    holder.binding.recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                    holder.binding.recyclerView.setHasFixedSize(true);
                    holder.binding.recyclerView.setAdapter(mAdapter);
                    listAdapter.add(mAdapter);
                }
            } else {
                GoodReceiptDetailSerialAdapter mAdapter = null;
                if(listAdapter.size() - 1 >= Integer.parseInt(holder.binding.btnAddBatch.getTag().toString())) {
                    if (listAdapter.get(Integer.parseInt(holder.binding.btnAddBatch.getTag().toString())) != null) {
                        mAdapter = (GoodReceiptDetailSerialAdapter) listAdapter.get(Integer.parseInt(holder.binding.btnAddBatch.getTag().toString()));
                    }
                }
                if (data.getSerialNumberInternals() != null && data.getSerialNumberInternals().size() != 0) {
                    holder.binding.btnAddBatch.setText("Edit Serial No.");
                }else {
                    holder.binding.btnAddBatch.setText("+ Serial No.");
                }
                holder.binding.txtHeaderTitle.setText("Serial No.");
                holder.binding.txtBatchNo.setText("Serial No.");
                if(mAdapter != null) {
                    mAdapter = new GoodReceiptDetailSerialAdapter(mContext, data);
                    holder.binding.recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                    holder.binding.recyclerView.setHasFixedSize(true);
                    holder.binding.recyclerView.setAdapter(mAdapter);
                    listAdapter.set(Integer.parseInt(holder.binding.btnAddBatch.getTag().toString()), mAdapter);
                }else{
                    mAdapter = new GoodReceiptDetailSerialAdapter(mContext, data);
                    holder.binding.recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                    holder.binding.recyclerView.setHasFixedSize(true);
                    holder.binding.recyclerView.setAdapter(mAdapter);
                    listAdapter.add(mAdapter);
                }
            }
        } else {
            listAdapter.add(null);
            holder.binding.itemTypeSNBatch.setVisibility(View.GONE);
            holder.binding.lBatchSerial.setVisibility(View.GONE);
        }
        holder.binding.btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!holder.binding.txtQty.getText().toString().trim().isEmpty()) {
                    mContext.getDataBatchOrSerial(data);
                } else {
                    Toast.makeText(mContext, "Quantity cannot empty or must be number", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (data.getUom() != null && !data.getUom().trim().isEmpty()) {
            holder.binding.txtUom.setVisibility(View.VISIBLE);
            holder.binding.txtUom.setText(data.getUom());
        } else {
            holder.binding.txtUom.setVisibility(View.GONE);
        }


        if (!data.isExpand()) {
            holder.binding.divider.setVisibility(View.VISIBLE);
            holder.binding.layoutListBatch.setVisibility(View.GONE);
        } else {
            holder.binding.divider.setVisibility(View.GONE);
            holder.binding.layoutListBatch.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }

    public void filterList(ArrayList<GoodReceiptDetail> filteredList) {
        dataList = filteredList;
        notifyDataSetChanged();
    }

    public static void hideKeyboard(Context context, View view) {
        if ((context == null) || (view == null)) {
            return;
        }
        InputMethodManager mgr =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}

