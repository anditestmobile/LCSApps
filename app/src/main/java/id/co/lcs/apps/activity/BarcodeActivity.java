package id.co.lcs.apps.activity;

/**
 * Created by TED on 18-Jul-20
 */


import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Map;

import id.co.lcs.apps.BuildConfig;
import id.co.lcs.apps.constants.Constants;
import id.co.lcs.apps.databinding.ActivityBarcodeBinding;
import id.co.lcs.apps.databinding.ActivitySplashBinding;
import id.co.lcs.apps.helper.Helper;
import id.co.lcs.apps.helper.QRCodeHelper;
import id.co.lcs.apps.model.User;
import id.co.lcs.apps.service.SessionManager;


public class BarcodeActivity extends BaseActivity {
    ActivityBarcodeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBarcodeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        init();
        initialize();
    }

    private void initialize() {
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BarcodeActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Helper.removeItemParam(Constants.BAR_CODE);
                Helper.removeItemParam(Constants.QUOTATION_HISTORY);
            }
        });
        String barcode = Helper.getItemParam(Constants.BAR_CODE).toString();
        String customerName = Helper.getItemParam(Constants.CUSTOMERNAME).toString();
        Bitmap bitmap = null;
        try {
            bitmap = QRCodeHelper.encodeAsBitmap(barcode, BarcodeFormat.CODE_128, 800, 500);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        binding.imgBarcode.setImageBitmap(bitmap);
        binding.txtDate.setText(Helper.todayDate1("MMMM dd, yyyy"));
        binding.txtOrderID.setText("Quotation : " + barcode + "\n" + customerName);

        binding.cLayout.setDrawingCacheEnabled(true);
        // this is the important code :)
        // Without it the view will have a dimension of 0,0 and the bitmap will be null

        binding.cLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        binding.cLayout.layout(0, 0, binding.cLayout.getMeasuredWidth(), binding.cLayout.getMeasuredHeight());

        binding.cLayout.buildDrawingCache(true);
        Bitmap b = Bitmap.createBitmap(binding.cLayout.getDrawingCache());
        binding.cLayout.setDrawingCacheEnabled(false); // clear drawing cache
        Bitmap dstBmp = null;
        if (b.getWidth() >= b.getHeight()){

            dstBmp = Bitmap.createBitmap(
                    b,
                    b.getWidth()/2 - b.getHeight()/2,
                    0,
                    b.getHeight(),
                    b.getHeight()
            );

        }else{

            dstBmp = Bitmap.createBitmap(
                    b,
                    0,
                    b.getHeight()/2 - b.getWidth()/2,
                    b.getWidth(),
                    b.getWidth()
            );
        }

        saveImg(dstBmp, barcode);

//        Bitmap bitmapLayout = Bitmap.createBitmap(binding.cLayout.getWidth(), binding.cLayout.getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmapLayout);
//        view.draw(canvas);
//        saveImg(bitmapLayout, barcode);
//        saveImg(bitmap);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(BarcodeActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        Helper.removeItemParam(Constants.BAR_CODE);
        Helper.removeItemParam(Constants.QUOTATION_HISTORY);
    }

    private void saveLayout(Bitmap bitmapLayout, String barcode) {
        try {
            FileOutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory() + "/path/to/" + barcode +".png");
            bitmapLayout.compress(Bitmap.CompressFormat.PNG, 100, output);
            output.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveImg(Bitmap bitmap, String barcode) {
        boolean saved;
        OutputStream fos;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentResolver resolver = getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, barcode);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/" + "LCS");
                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                fos = resolver.openOutputStream(imageUri);
            } else {
                String imagesDir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM).toString() + File.separator + "LCS";

                File file = new File(imagesDir);

                if (!file.exists()) {
                    file.mkdir();
                }

                File image = new File(imagesDir, barcode + ".png");

                fos = new FileOutputStream(image);


            }

            saved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
