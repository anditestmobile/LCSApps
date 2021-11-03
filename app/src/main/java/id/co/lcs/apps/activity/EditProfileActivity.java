package id.co.lcs.apps.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import id.co.lcs.apps.databinding.ActivityEditProfileBinding;


/**
 * Created by TED on 17-Jul-20
 */
public class EditProfileActivity extends AppCompatActivity {
    private ActivityEditProfileBinding binding;
    private View rootView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle(null);

        Glide.with(this)
                .load("https://scontent.fcgk26-1.fna.fbcdn.net/v/t1.0-9/230179_6410053047_9879_n.jpg?_nc_cat=107&_nc_sid=de6eea&_nc_eui2=AeEr8oEhDbc3F-Cp8awUjSVwdN2FztLTmBR03YXO0tOYFG4hzEZc0dSkaZdDEnYDEvo&_nc_ohc=0OWyqEs5yp4AX9udGal&_nc_ht=scontent.fcgk26-1.fna&oh=5ba6cf0fc90463e9e0db3ff24922cae2&oe=5F64B7BD")
                .circleCrop()
                .into(binding.imgProfile);

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }
}