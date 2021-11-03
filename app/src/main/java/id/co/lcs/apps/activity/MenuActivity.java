package id.co.lcs.apps.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import id.co.lcs.apps.BuildConfig;
import id.co.lcs.apps.constants.Constants;
import id.co.lcs.apps.databinding.ActivityLogin1Binding;
import id.co.lcs.apps.databinding.ActivityMenuBinding;
import id.co.lcs.apps.fragment.HomeFragment;
import id.co.lcs.apps.helper.Helper;
import id.co.lcs.apps.model.LoginResponse;
import id.co.lcs.apps.model.UserLogin;
import id.co.lcs.apps.model.WHResponse;
import id.co.lcs.apps.model.Warehouse;
import id.co.lcs.apps.service.SessionManager;

/**
 * Created by TED on 17-Jul-20
 */
public class MenuActivity extends BaseActivity {
    ActivityMenuBinding binding;
    String[] singleChoiceItems;
    String[] bin = {"Bin 001", "Bin 002", "Bin 003", "Bin 004"};
    int itemSelected = 0;
    public int PARAM = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMenuBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        String versionName = BuildConfig.VERSION_NAME;
        binding.txtVersion.setText("v" + versionName);
        init();
        binding.btnSalesman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        binding.btnPoS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, PointOfSalesActivity.class);
                startActivity(intent);
            }
        });

        binding.btnInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PARAM = 0;
                new RequestUrl().execute();
                getProgressDialog().show();
            }
        });

        binding.imgLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MenuActivity.this)
                        .setMessage("Are you sure to logout?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new SessionManager(getApplicationContext()).clearData();
                                Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
            }
        });
    }

    public void showDialogWH(List<Warehouse> listWH){
        singleChoiceItems = new String[listWH.size()];
        for(int i=0;i<listWH.size();i++){
            singleChoiceItems[i] = listWH.get(i).getWhID();
        }
        new androidx.appcompat.app.AlertDialog.Builder(MenuActivity.this)
                .setTitle("Select Warehouse ")
                .setSingleChoiceItems(singleChoiceItems, itemSelected, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int selectedIndex) {
                        itemSelected = selectedIndex;
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //set what would happen when positive button is clicked
                        Helper.setItemParam(Constants.WAREHOUSE, singleChoiceItems[itemSelected]);
                        List<String> whTemp = new ArrayList<>();
                        for(String wh : singleChoiceItems){
                            if(!wh.equals(singleChoiceItems[itemSelected])) {
                                whTemp.add(wh);
                            }
                        }
                        Helper.setItemParam(Constants.LIST_WAREHOUSE, whTemp);
                        Helper.setItemParam(Constants.LIST_BIN, bin);
                        Intent intent = new Intent(MenuActivity.this, InventoryMenuActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onBackPressed() {
//        finish();
        moveTaskToBack(true);
    }

    private class RequestUrl extends AsyncTask<Void, Void, WHResponse> {

        @Override
        protected WHResponse doInBackground(Void... voids) {
            try {
                if (PARAM == 0) {
                    String URL_WAREHOUSE = Constants.API_PREFIX + Constants.API_WAREHOUSE;
                    final String url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_WAREHOUSE);
                    return (WHResponse) Helper.getWebserviceWithoutHeaders(url, WHResponse.class);
                } else {
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
        protected void onPostExecute(WHResponse wh) {
            if (PARAM == 0) {
                getProgressDialog().dismiss();
                if (wh != null && wh.getStatusCode() == 1) {
                    showDialogWH(wh.getResponseData());
                } else {
                    if (Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR) != null) {
                        Toast.makeText(getApplicationContext(), Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR).toString(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), wh.getStatusMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
}
