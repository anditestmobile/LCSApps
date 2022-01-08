package id.co.lcs.apps.activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import id.co.lcs.apps.R;
import id.co.lcs.apps.databinding.ActivityMainBinding;
import id.co.lcs.apps.fragment.HomeFragment;
import id.co.lcs.apps.fragment.ScannerFragment;
import id.co.lcs.apps.fragment.BestBuyFragment;


public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    ActivityMainBinding binding;
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        init();
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle(null);

        binding.bottomNavigation.setOnNavigationItemSelectedListener(this);
//        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) binding.bottomNavigation.getLayoutParams();
//        layoutParams.setBehavior(new BottomNavigationViewBehavior());

        loadFragment(new HomeFragment());

        binding.btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        Glide.with(this)
                .load(getImage("no_profile"))
                .circleCrop()
                .into(binding.imgProfile);
        binding.txtUsername.setText(user.getFirstName());

        binding.layoutProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        binding.btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, QuotationHistoryActivity.class);
                startActivity(intent);
            }
        });


    }



    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
//                    .addToBackStack(null)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()){
            case R.id.home:
                binding.txtTitle.setText("HOME");
                fragment = new HomeFragment();
                break;
            case R.id.favorite:
                binding.txtTitle.setText("BEST BUY");
                fragment = new BestBuyFragment();
                break;
            case R.id.barcodeScanner:
                binding.txtTitle.setText("SCANNER");
                fragment = new ScannerFragment();
                break;
        }
        return loadFragment(fragment);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setBroadcastReceiver();
    }

    private void setBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("CART");
        intentFilter.addAction("CHECKOUT");
//        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String message = intent.getExtras().getString("message", null);
                    if(message != null){
                        binding.cartNotif.setVisibility(View.VISIBLE);
                        binding.cartNotif.setText(message);
                    }
                }
            };
//        }
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(broadcastReceiver!=null){
            unregisterReceiver(broadcastReceiver);
        }
    }
}