package id.co.lcs.apps.adapter;

/**
 * Created by TED on 16-Jul-20
 */

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import id.co.lcs.apps.activity.DeliveryStatusActivity;
import id.co.lcs.apps.databinding.RowViewDeliveryStatusBinding;
import id.co.lcs.apps.model.DeliveryStatus;


public class DeliveryStatusAdapter extends RecyclerView.Adapter<DeliveryStatusAdapter.DataObjectHolder> {
    private ArrayList<DeliveryStatus> mDataSet;
    private DeliveryStatusActivity mContext;


    public DeliveryStatusAdapter(DeliveryStatusActivity context, ArrayList<DeliveryStatus> data) {
        mDataSet = data;
        mContext = context;
    }

    class DataObjectHolder extends RecyclerView.ViewHolder {
        private RowViewDeliveryStatusBinding binding;

        DataObjectHolder(RowViewDeliveryStatusBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

    @NonNull
    @Override
    public DataObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new DataObjectHolder(RowViewDeliveryStatusBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final DataObjectHolder holder, final int position) {
        final DeliveryStatus data = mDataSet.get(position);

        RowViewDeliveryStatusBinding binding = holder.binding;
        binding.txtDate.setText(data.getDate());
        binding.txtDoNumber.setText(data.getDoNumber());
        binding.txtStatus.setText(data.getStatus());
    }


    @Override
    public int getItemCount() {
        return mDataSet != null ? mDataSet.size() : 0;
    }


}

