package id.co.lcs.apps.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import id.co.lcs.apps.adapter.DeliveryStatusAdapter;
import id.co.lcs.apps.databinding.ActivityDeliveryStatus1Binding;
import id.co.lcs.apps.model.DeliveryStatus;

/**
 * Created by TED on 19-Jul-20
 */

public class DeliveryStatusActivity extends AppCompatActivity {
    private ActivityDeliveryStatus1Binding binding;
    private DeliveryStatusAdapter mAdapter;
    private ArrayList<DeliveryStatus> deliveryStatusArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDeliveryStatus1Binding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle(null);


        binding.btnBack.setOnClickListener(view1 -> onBackPressed());

        binding.rvDeliveryStatus.setLayoutManager(new LinearLayoutManager(this));
        binding.rvDeliveryStatus.setHasFixedSize(true);

        mAdapter = new DeliveryStatusAdapter(this, getDeliveryStatusList());
        binding.rvDeliveryStatus.setAdapter(mAdapter);


    }

    private ArrayList<DeliveryStatus> getDeliveryStatusList() {
        deliveryStatusArrayList = new ArrayList<>();

        DeliveryStatus deliveryStatus = new DeliveryStatus("10-07-2020", "DO00001", "ON THE WAY");
        deliveryStatusArrayList.add(deliveryStatus);

        deliveryStatus = new DeliveryStatus("12-07-2020", "DO00002", "DELIVERED");
        deliveryStatusArrayList.add(deliveryStatus);

        deliveryStatus = new DeliveryStatus("17-07-2020", "DO00003", "ON THE WAY");
        deliveryStatusArrayList.add(deliveryStatus);


        return deliveryStatusArrayList;
    }


}