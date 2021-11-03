package id.co.lcs.apps.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import id.co.lcs.apps.databinding.ActivitySoBinding;

public class SoActivity extends AppCompatActivity {
    ActivitySoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        String text="1234567890"; // Whatever you need to encode in the QR code
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,1000,1000);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            binding.imgQRcode.setImageBitmap(bitmap);
            binding.txtQRCode.setText(text);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}