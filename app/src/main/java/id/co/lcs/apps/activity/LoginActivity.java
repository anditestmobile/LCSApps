package id.co.lcs.apps.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Toast;

import id.co.lcs.apps.BuildConfig;
import id.co.lcs.apps.R;
import id.co.lcs.apps.constants.Constants;
import id.co.lcs.apps.databinding.ActivityLogin1Binding;
import id.co.lcs.apps.helper.Helper;
import id.co.lcs.apps.model.LoginResponse;
import id.co.lcs.apps.model.UserLogin;
import id.co.lcs.apps.service.SessionManager;

/**
 * Created by TED on 17-Jul-20
 */
public class LoginActivity extends BaseActivity {
    ActivityLogin1Binding binding;
    public int PARAM = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        binding = ActivityLogin1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String versionName = BuildConfig.VERSION_NAME;
        binding.txtVersion.setText("v" + versionName);
        String signUpString = "Don't have an account? " + "<u><b>Sign Up</b></u>";
        binding.txtSignUp.setText(Html.fromHtml(signUpString));

        binding.btnLogin.setOnClickListener(view -> {
            if (binding.edtUsername.getText() != null && binding.edtUsername.getText().toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.login_msg_empty_username), Toast.LENGTH_SHORT).show();
            } else if (binding.edtPassword.getText() != null && binding.edtPassword.getText().toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.login_msg_empty_password), Toast.LENGTH_SHORT).show();
            } else {
                PARAM = 0;
                new RequestUrl().execute();
                getProgressDialog().show();
            }
        });

        binding.txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

    }

    private class RequestUrl extends AsyncTask<Void, Void, LoginResponse> {

        @Override
        protected LoginResponse doInBackground(Void... voids) {
            try {
                if (PARAM == 0) {
                    String URL_LOGIN = Constants.API_PREFIX + Constants.API_LOGIN;

                    final String url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_LOGIN);
                    UserLogin userLogin = new UserLogin();
                    userLogin.setPwd(binding.edtPassword.getText().toString());
                    userLogin.setUserID(binding.edtUsername.getText().toString());
                    return (LoginResponse) Helper.postWebservice(url, userLogin, LoginResponse.class);
                }else{
                    return null;
                }
            } catch (Exception ex) {
                if (ex.getMessage() != null) {
                    Helper.setItemParam(Constants.INTERNAL_SERVER_ERROR, ex.getMessage());
                }
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(LoginResponse logins) {
            if (PARAM == 0) {
                getProgressDialog().dismiss();
                if(logins != null && logins.getStatusCode() == 1){
                    logins.getResponseData().get(0).setUsername(binding.edtUsername.getText().toString());
                    Helper.setItemParam(Constants.USER_DETAILS, logins.getResponseData().get(0));
                    new SessionManager(getApplicationContext()).createDataSession(Helper.objectToString(logins.getResponseData().get(0)));
                    Intent i = new Intent(LoginActivity.this, MenuActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                } else {
                    if (Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR) != null) {
                        Toast.makeText(getApplicationContext(), Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR).toString(), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), logins.getStatusMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

    }

    @Override
    public void onBackPressed() {
//        finish();
        moveTaskToBack(true);
    }
}
