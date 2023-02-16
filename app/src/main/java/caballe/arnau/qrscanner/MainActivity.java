package caballe.arnau.qrscanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.zxing.integration.android.IntentIntegrator;

import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private TextView scanResult;
    private Button access, scan;
    private String url;
    private boolean cancelado = false;
    private DrawerLayout mDrawerLayout;

    IntentIntegrator integrator = new IntentIntegrator(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setNavigationViewListener();

        scanResult = findViewById(R.id.txt_scannResult);
        access = findViewById(R.id.btn_access);
        scan = findViewById(R.id.btn_scan);

        scanResult.setOnClickListener(this);
        access.setOnClickListener(this);
        scan.setOnClickListener(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        startQRScanner();

    }

    /* START QR SCANNER */
    private void startQRScanner(){
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt(getString(R.string.scanYourCode));
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }

    /* AFTER QR SCANNER */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                cancelado = true;
                Toast.makeText(MainActivity.this, getString(R.string.scanCanceled), Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(MainActivity.this, "Contenido del código escaneado: " + result.getContents(), Toast.LENGTH_SHORT).show();
                cancelado = false;
                url = result.getContents();
                scanResult.setText(result.getContents());
                saveResultToHistory(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txt_scannResult:
                copyToClipboard(scanResult);
                break;
            case R.id.btn_access:
                if(!cancelado){
                    try {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(browserIntent);
                    }catch (Exception e){
                        Toast.makeText(MainActivity.this, getString(R.string.errorOnAccess), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(MainActivity.this, getString(R.string.qrNotScanned), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_scan:
                startQRScanner();
                break;
        }

    }

    /* COPY TO CLIPBOARD */
    public void copyToClipboard(TextView textView) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("text", textView.getText().toString());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, getString(R.string.textCopiedToClipboard), Toast.LENGTH_SHORT).show();
    }

    /* SIDE MENU */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.menu_home:
                break;
            case R.id.menu_settings:
                Toast.makeText(this,getString(R.string.settings),Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_record:
                Intent scanHistory = new Intent(this, ScanHistory.class);
                startActivity(scanHistory);
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

    /* SAVE HISTORY */
    public void saveResultToHistory(String result) {
        try {
            File file = new File(getFilesDir(), "history.txt");
            FileWriter writer = new FileWriter(file, true); // true para añadir al final del archivo existente
            writer.write(result + "\n");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Menu funcional no lateral
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_settings:
                Toast.makeText(this,"Ajustaments", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_record:
                Toast.makeText(this,"Historial", Toast.LENGTH_SHORT).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */

}