package id.co.lcs.apps.activity;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import id.co.lcs.apps.databinding.ActivitySignupBinding;

/**
 * Created by TED on 17-Jul-20
 */
public class SignupActivity extends AppCompatActivity {
    ActivitySignupBinding binding;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SignupActivity.this, "Please check your email for verification", Toast.LENGTH_SHORT).show();
            }
        });

        String signInString = "Already have an account? " + "<u><b>Sign In</b></u>";
        binding.txtSignIn.setText(Html.fromHtml(signInString));

        binding.txtSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }
}
