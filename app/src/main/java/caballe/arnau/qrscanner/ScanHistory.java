package caballe.arnau.qrscanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ScanHistory extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private List<String> elements = new ArrayList<>();
    private RecyclerView viewLlista;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_history);
        //SIDE MENU
        setNavigationViewListener();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(elements);

        viewLlista = findViewById(R.id.viewLlista);
        viewLlista.setAdapter(adapter);

        loadHistory();
    }

    /* LOAD HISTORY IN RECYCLERVIEW */
    private void loadHistory(){
        List<String> scanHistory = getScanHistory(ScanHistory.this);
        for (int i = scanHistory.size() - 1; i >= 0; i--){
            elements.add(scanHistory.get(i));
        }
        viewLlista.getAdapter().notifyDataSetChanged();
    }

    /* READ FILE AND RETURN HISTORY */
    private List<String> getScanHistory(Context context) {
        List<String> scanHistory = new ArrayList<>();

        try {
            FileInputStream fis = context.openFileInput("history.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                scanHistory.add(line);
            }
            fis.close();
        } catch (IOException e) {
            Toast.makeText(this,"Error could not read file", Toast.LENGTH_SHORT).show();
        }

        return scanHistory;
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
            case R.id.menu_settings:
                Toast.makeText(this,getString(R.string.settings),Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_record:
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