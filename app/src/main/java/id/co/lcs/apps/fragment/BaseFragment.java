package id.co.lcs.apps.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

import id.co.lcs.apps.R;
import id.co.lcs.apps.constants.Constants;
import id.co.lcs.apps.helper.Helper;
import id.co.lcs.apps.model.Product;
import id.co.lcs.apps.model.User;
import id.co.lcs.apps.utils.Utils;


/**
 * Created by TED on 17-Jul-20
 */
public class BaseFragment extends Fragment {
    protected BottomSheetDialog bottomSheetDialog;
    protected ProgressDialog progress;
    public User user;

    protected void init() {
        user = (User) Helper.getItemParam(Constants.USER_DETAILS);
        initBase();
    }

    protected void setToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    protected void initBase() {
        progress = Utils.createProgressDialog(getActivity());
    }

    public ProgressDialog getProgressDialog(){
        return progress;
    }

    public void showDialogConfirmation(Product data) {
        if (bottomSheetDialog == null || !bottomSheetDialog.isShowing()) {
            bottomSheetDialog = Utils.showBottomSheetDialog(getActivity(), R.layout.bsheet_confirmation, (itemView, bottomSheet) -> {

                ImageView imgProduct = itemView.findViewById(R.id.imgProduct);
                TextView txtTitle = itemView.findViewById(R.id.labelTitle);
                TextView txtProductName = itemView.findViewById(R.id.txtProductName);
                Button btnNo = itemView.findViewById(R.id.btnSkip);
                Button btnYes = itemView.findViewById(R.id.btnEmail);

                Glide.with(getContext())
                        .load(data.getImageUrl())
                        .into(imgProduct);
                txtTitle.setText("Do you want to add this product to cart?");
                txtProductName.setText(data.getProductName());
                btnYes.setText("Add to cart");

                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getContext(), "Added to cart!", Toast.LENGTH_SHORT).show();
                        ArrayList<Product> listProduct = (ArrayList<Product>) Helper.getItemParam(Constants.LIST_CART);
                        if(listProduct == null){
                            listProduct = new ArrayList<>();
//                            data.setQty(1);
                            data.setStatusCheckout(true);
                            Product p = new Product(data.getProductCode(),data.getProductName(),data.getProductCategory(),data.getImageUrl()
                                    ,data.getInStock(),data.getPrice(),data.getTotaIndex(),data.getRowNumber(),data.getSalesAmt(),data.getQty()
                                    ,data.isWishList(),data.isStatusCheckout(),data.getProductDetailArrayList(),data.getProductDetailArrayListMore());
                            listProduct.add(p);
                            Helper.setItemParam(Constants.LIST_CART, listProduct);
                            sentCart(String.valueOf(listProduct.size()));
                        }else{
                            boolean flag = true;
                            for(Product product : listProduct){
                                if(product.getProductName().equals(data.getProductName())){
                                    product.setQty(product.getQty() + data.getQty());
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
    public void sentCart(String qty){
        Intent broadcast = new Intent();
        broadcast.setAction("CART");
        broadcast.putExtra("message", qty);
        getActivity().sendBroadcast(broadcast);
    }

    public void showDialogConfirmationRemove(String message) {
        if (bottomSheetDialog == null || !bottomSheetDialog.isShowing()) {
            bottomSheetDialog = Utils.showBottomSheetDialog(getActivity(), R.layout.bsheet_confirmation, (itemView, bottomSheet) -> {

                ImageView imgProduct = itemView.findViewById(R.id.imgProduct);
                TextView txtTitle = itemView.findViewById(R.id.labelTitle);
                TextView txtProductName = itemView.findViewById(R.id.txtProductName);
                TextView txtAmount = itemView.findViewById(R.id.edtAmount);
                TextView txtPrice = itemView.findViewById(R.id.txtPrice);
                Button btnNo = itemView.findViewById(R.id.btnSkip);
                Button btnYes = itemView.findViewById(R.id.btnEmail);

//                imgProduct.setVisibility(View.GONE);
//                txtProductName.setVisibility(View.GONE);
                txtAmount.setVisibility(View.GONE);
//                txtPrice.setVisibility(View.GONE);


                Glide.with(getContext())
                        .load("https://cf.shopee.sg/file/f3444526f1f4966cca13ff7370a79623")
                        .into(imgProduct);
                txtTitle.setText(message);
                btnYes.setText("Remove");

                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getContext(), "Removed!", Toast.LENGTH_SHORT).show();
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
}
