package id.co.lcs.apps.adapter;
import android.content.Context;
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
import id.co.lcs.apps.model.GoodReceiptSupplierDetail;
import id.co.lcs.apps.model.SerialNumberInternal;

public class GoodReceiptDetailSerialAdapter extends RecyclerView.Adapter<GoodReceiptDetailSerialAdapter.DataObjectHolder> {

    private List<SerialNumberInternal> serialNumberInternals;
    private GoodReceiptDetail gr;
    private GoodReceiptDetailActivity mContext;
    private String totalGRQty;
    private boolean clicked = false;

    public GoodReceiptDetailSerialAdapter(GoodReceiptDetailActivity context, GoodReceiptDetail data) {
        this.serialNumberInternals = data.getSerialNumberInternals();
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
                    mContext.deleteSerialQTY(Integer.parseInt(binding.edtQty.getTag().toString()), gr);
                }
            });
        }
    }

    @NonNull
    @Override
    public GoodReceiptDetailSerialAdapter.DataObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GoodReceiptDetailSerialAdapter.DataObjectHolder(RowViewListSnBatchBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final GoodReceiptDetailSerialAdapter.DataObjectHolder holder, final int position) {
        final SerialNumberInternal data = serialNumberInternals.get(position);
        holder.binding.txtSNBatchNo.setText(data.getSerialNo() + " - " + Helper.changeDateFomat("dd-MM-yyyy", "dd/MM/yyyy", data.getAdmDate()));

        holder.binding.edtQty.setTag(position);
        holder.binding.imgDelete.setTag(position);
        holder.binding.edtQty.setEnabled(false);
    }

    @Override
    public int getItemCount() {
        return serialNumberInternals != null ? serialNumberInternals.size() : 0;
    }

    public void filterList(ArrayList<SerialNumberInternal> filteredList) {
        serialNumberInternals = filteredList;
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
