package com.ahmedgamal.dell.androidnumber26.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.widget.Toast;

import com.ahmedgamal.dell.androidnumber26.fragment.MapFragment;
import com.ahmedgamal.dell.androidnumber26.helper.GoogleApiHelper;

/**
 * Created by ahmed gamal on 09/2/2015.
 */
public class GetLocationAsyncTask extends AsyncTask<Void, Void, Location> {

    private ProgressDialog progressDialog;
    private Context context;
    private MapFragment mapFragment;
    private GoogleApiHelper googleApiHelper;

    public GetLocationAsyncTask(Context context, MapFragment mapFragment,GoogleApiHelper googleApiHelper) {
        this.context = context;
        this.mapFragment = mapFragment;
        this.googleApiHelper = googleApiHelper;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Getting Current Location...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    @Override
    protected Location doInBackground(Void... params) {
        try {
            Location currenLocation = googleApiHelper.getCurrentLocation();

            return currenLocation;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(Location result) {
        super.onPostExecute(result);
        progressDialog.hide();
        if (result != null) {
            mapFragment.currentLocationFound(result);
        } else {
            Toast.makeText(context, "Current Location not found, Please enable GPS", Toast.LENGTH_LONG).show();
        }
    }
}
