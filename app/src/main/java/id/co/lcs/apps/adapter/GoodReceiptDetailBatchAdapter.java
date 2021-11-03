package id.co.lcs.apps.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import id.co.lcs.apps.activity.GoodReceiptDetailActivity;
import id.co.lcs.apps.databinding.RowViewListSnBatchBinding;
import id.co.lcs.apps.helper.Helper;
import id.co.lcs.apps.model.BatchInternal;
import id.co.lcs.apps.model.GoodReceiptDetail;

public class GoodReceiptDetailBatchAdapter extends RecyclerView.Adapter<GoodReceiptDetailBatchAdapter.DataObjectHolder> {

    private List<BatchInternal> batchInternals;
    private GoodReceiptDetailActivity mContext;
    private GoodReceiptDetail gr;
    private String totalGRQty;
    private boolean clicked = false;

    public GoodReceiptDetailBatchAdapter(GoodReceiptDetailActivity context, GoodReceiptDetail data) {
        this.batchInternals = data.getBatchInternals();
        this.gr = data;
        this.totalGRQty = data.getGrQty();
        mContext = context;
    }

    class DataObjectHolder extends RecyclerView.ViewHolder {
        private RowViewListSnBatchBinding binding;

        DataObjectHolder(RowViewListSnBatchBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.deleteBatchQTY(Integer.parseInt(binding.edtQty.getTag().toString()), gr);
                }
            });

            binding.edtQty.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (binding.edtQty.hasFocus()) {
                        int total = Integer.parseInt(gr.getQty());
                        int totalTemp = 0;
                        for (BatchInternal bi : batchInternals) {
                            if (!bi.getBatch().equals(batchInternals.get((Integer) binding.edtQty.getTag()).getBatch()) && !bi.getAdmDate()
                                    .equals(batchInternals.get((Integer) binding.edtQty.getTag()).getAdmDate())) {
                                totalTemp += Integer.parseInt(bi.getBatchQty());
                            } else {
                                if (!s.toString().equals(""))
                                    totalTemp += Integer.parseInt(s.toString());
                            }
                        }
                        if (totalTemp > total) {
                            binding.edtQty.setText("0");
                            Toast.makeText(mContext, "Total batch quantity cannot bigger then GR quantity", Toast.LENGTH_SHORT).show();
                        } else {
                            if (!s.toString().equals("") && s.toString().length() != 1) {
                                if (Character.toString(s.toString().charAt(0)).equals("0"))
                                    binding.edtQty.setText(s.toString().replaceFirst("^0+(?!$)", ""));
                            }
                            mContext.changeBatchQTY(s.toString().replaceFirst("^0+(?!$)", ""), Integer.parseInt(binding.edtQty.getTag().toString()), gr);
                        }
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public GoodReceiptDetailBatchAdapter.DataObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GoodReceiptDetailBatchAdapter.DataObjectHolder(RowViewListSnBatchBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final GoodReceiptDetailBatchAdapter.DataObjectHolder holder, final int position) {
        final BatchInternal data = batchInternals.get(position);
        holder.binding.txtSNBatchNo.setText(data.getBatch() + " - " + Helper.changeDateFomat("dd-MM-yyyy", "dd/MM/yyyy", data.getAdmDate()));

        holder.binding.edtQty.setTag(position);
        holder.binding.imgDelete.setTag(position);
        if (data.getBatchQty() == null) {
            holder.binding.edtQty.setText("0");
            data.setBatchQty("0");
        } else {
            holder.binding.edtQty.setText(data.getBatchQty());
        }
    }

    @Override
    public int getItemCount() {
        return batchInternals != null ? batchInternals.size() : 0;
    }

    public void filterList(ArrayList<BatchInternal> filteredList) {
        batchInternals = filteredList;
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
