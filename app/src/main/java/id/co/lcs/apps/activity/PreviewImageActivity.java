package id.co.lcs.apps.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;

import id.co.lcs.apps.R;
import id.co.lcs.apps.constants.Constants;
import id.co.lcs.apps.databinding.ActivityMainBinding;
import id.co.lcs.apps.databinding.ActivityPreviewBinding;
import id.co.lcs.apps.databinding.FragmentHomeBinding;
import id.co.lcs.apps.helper.Helper;

public class PreviewImageActivity extends AppCompatActivity {

    private ActivityPreviewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        binding = ActivityPreviewBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        Glide.with(this)
                .load(Helper.getItemParam(Constants.IMAGE_URL))
                .into(binding.imgPreview);
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}