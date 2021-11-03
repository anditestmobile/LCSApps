package id.co.lcs.apps.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

import id.co.lcs.apps.R;
import id.co.lcs.apps.constants.Constants;
import id.co.lcs.apps.helper.Helper;
import id.co.lcs.apps.model.Product;
import id.co.lcs.apps.model.User;
import id.co.lcs.apps.utils.Utils;

public class BaseActivity extends AppCompatActivity {
    protected BottomSheetDialog bottomSheetDialog;
    protected ProgressDialog progress;
    public User user;

    protected void init() {
        user = (User) Helper.getItemParam(Constants.USER_DETAILS);
        initBase();
    }



    protected void setToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
    public void setButton(Button btn, String text, int id) {
        btn.setBackgroundDrawable(ContextCompat
                .getDrawable(getApplicationContext(),
                        id));
        btn.setText(text);

    }

    protected void initBase() {
        progress = Utils.createProgressDialog(this);
    }

    public ProgressDialog getProgressDialog(){
        return progress;
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    public void getImageGlide(Context context, String imageUrl, ImageView image) {
        if (!imageUrl.equals("false")) {
            Glide.with(context)
                    .applyDefaultRequestOptions(new RequestOptions()
                            .placeholder(R.drawable.ic_no_image)
                            .error(R.drawable.ic_no_image))
                    .asBitmap()
                    .load(Base64.decode(imageUrl, Base64.DEFAULT))
                    .into(image);
        } else {
            Glide.with(context)
                    .applyDefaultRequestOptions(new RequestOptions()
                            .placeholder(R.drawable.ic_no_image)
                            .error(R.drawable.ic_no_image))
                    .asBitmap()
                    .load(imageUrl)
                    .into(image);
        }
    }

    public void sentCart(String qty){
        Intent broadcast = new Intent();
        broadcast.setAction("CART");
        broadcast.putExtra("message", qty);
        getApplicationContext().sendBroadcast(broadcast);
    }



    public void showDialogConfirmation(Product data) {
        if (bottomSheetDialog == null || !bottomSheetDialog.isShowing()) {
            bottomSheetDialog = Utils.showBottomSheetDialog(this, R.layout.bsheet_confirmation, (itemView, bottomSheet) -> {

                ImageView imgProduct = itemView.findViewById(R.id.imgProduct);
                TextView txtTitle = itemView.findViewById(R.id.labelTitle);
                TextView txtProductName = itemView.findViewById(R.id.txtProductName);
                Button btnNo = itemView.findViewById(R.id.btnSkip);
                Button btnYes = itemView.findViewById(R.id.btnEmail);

                Glide.with(getApplicationContext())
                        .load(data.getImageUrl())
                        .into(imgProduct);
                txtTitle.setText("Do you want to add this product to cart?");
                txtProductName.setText(data.getProductName());
                btnYes.setText("Add to cart");

                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplicationContext(), "Added to cart!", Toast.LENGTH_SHORT).show();
                        ArrayList<Product> listProduct = (ArrayList<Product>) Helper.getItemParam(Constants.LIST_CART);
                        if(listProduct == null){
                            listProduct = new ArrayList<>();
//                            data.setQty(1);
                            data.setStatusCheckout(true);
                            listProduct.add(data);
                            Helper.setItemParam(Constants.LIST_CART, listProduct);
                            sentCart(String.valueOf(listProduct.size()));
                        }else{
                            boolean flag = true;
                            for(Product product : listProduct){
                                if(product.getProductName().equals(data.getProductName())){
//                                    data.setQty(data.getQty() + 1);
                                    flag = false;
                                    break;
                                }
                            }
                            if(flag){
//                                data.setQty(1);
                                data.setStatusCheckout(true);
                                listProduct.add(data);
                                sentCart(String.valueOf(listProduct.size()));
                            }
                            Helper.setItemParam(Constants.LIST_CART, listProduct);

                        }

                        bottomSheetDialog.dismiss();
                    }
                });

                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                    }
                });
            });
        }
    }

    public int getImage(String imageName) {

        int drawableResourceId = this.getResources().getIdentifier(imageName, "drawable", this.getPackageName());

        return drawableResourceId;
    }

}
