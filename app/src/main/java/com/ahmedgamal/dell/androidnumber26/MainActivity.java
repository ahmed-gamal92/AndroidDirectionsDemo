package com.ahmedgamal.dell.androidnumber26;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;

import com.ahmedgamal.dell.androidnumber26.fragment.MapFragment;
import com.ahmedgamal.dell.androidnumber26.util.AppUtils;

/**
 * Created by ahmed gamal on 09/2/2015.
 */
public class MainActivity extends AppCompatActivity {

    //toolbar buttons
    private ImageButton btnStores;
    private ImageButton btnClear;

    private MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        initToolbarControllers(toolbar);
        setToolbarControllersListeners();
        AppUtils.checkFirstLaunchNetwork(this);
        displayMap();
    }


    private void setToolbarControllersListeners() {
        btnStores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapFragment.getStores();
            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapFragment.clear();
            }
        });
    }

    private void initToolbarControllers(Toolbar toolbar) {
        btnStores = (ImageButton) toolbar.findViewById(R.id.ibtn_stores);
        btnClear = (ImageButton) toolbar.findViewById(R.id.ibtn_clear);
    }

    private void displayMap() {
        mapFragment = new MapFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, mapFragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
