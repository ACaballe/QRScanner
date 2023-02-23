package caballe.arnau.qrscanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class QRGenerator extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private EditText input;
    private Button generate, share;
    private ImageView imageView;

    private QRCodeWriter writer;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrgenerator);

        input = findViewById(R.id.txt_inp);
        generate = findViewById(R.id.btn_generate);
        share = findViewById(R.id.btn_share);
        imageView = findViewById(R.id.imageView);

        generate.setOnClickListener(this);
        share.setOnClickListener(this);

        setNavigationViewListener();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_generate:
                qrGenerator();
                break;
            case R.id.btn_share:
                compartirQR();
                break;
        }
    }

    /* QR GENERATOR */
    private void qrGenerator(){
        // Crea un objeto QRCodeWriter
        writer = new QRCodeWriter();

        if(!input.getText().toString().isEmpty()){
            try {
                // Genera un código QR a partir del texto deseado
                BitMatrix bitMatrix = writer.encode(input.getText().toString(), BarcodeFormat.QR_CODE, 512, 512);

                // Crea un bitmap del código QR generado
                int width = bitMatrix.getWidth();
                int height = bitMatrix.getHeight();
                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                    }
                }

                // Muestra el código QR en un ImageView
                imageView.setImageBitmap(bitmap);

                share.setVisibility(View.VISIBLE);

            } catch (WriterException e) {
                //e.printStackTrace();
                Toast.makeText(this, getString(R.string.errorGeneratingQR), Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, getString(R.string.noValues), Toast.LENGTH_SHORT).show();
        }
    }

    /* SHARE QR */
    private void compartirQR() {
        // Obtener la imagen del código QR como un bitmap
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        // Guardar la imagen en la carpeta de imágenes de la aplicación
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "qr_code.png");
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Crear un Intent para compartir la imagen
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/png");
        Uri imageUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Mostrar el selector de aplicaciones para compartir
        startActivity(Intent.createChooser(shareIntent, getString(R.string.shareQR)));
    }

    /* SIDE MENU */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.menu_home:
                Intent actHome = new Intent(this,MainActivity.class);
                startActivity(actHome);
                break;
            case R.id.menu_record:
                Intent scanHistory = new Intent(this, ScanHistory.class);
                startActivity(scanHistory);
                break;
            case R.id.menu_qrGenerator:
                break;
            case R.id.menu_settings:
                Toast.makeText(this,getString(R.string.settings),Toast.LENGTH_SHORT).show();
                break;

        }
        //close navigation drawer
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setNavigationViewListener() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}