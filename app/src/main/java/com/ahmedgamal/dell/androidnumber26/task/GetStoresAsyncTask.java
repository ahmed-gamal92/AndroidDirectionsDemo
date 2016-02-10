package com.ahmedgamal.dell.androidnumber26.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.ahmedgamal.dell.androidnumber26.fragment.MapFragment;
import com.ahmedgamal.dell.androidnumber26.helper.JSONParser;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by ahmed gamal on 09/2/2015.
 */
public class GetStoresAsyncTask extends AsyncTask<Void, Void, List<LatLng>>

{

    private ProgressDialog progressDialog;
    String url;
    Context context;
    MapFragment mapFragment;

    public GetStoresAsyncTask(String urlPass, Context context,MapFragment mapFragment) {
        url = urlPass;
        this.context = context;
        this.mapFragment = mapFragment;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Getting Stores...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    @Override
    protected List<LatLng> doInBackground(Void... params) {
        try {
            JSONParser jsonParser = new JSONParser();
            String jsonResponse = jsonParser.getJSONFromUrl(url);
            //
            List<LatLng> storesLoc = jsonParser.getStoresLocationFromJSON(jsonResponse);
            //
            return storesLoc;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<LatLng> storesLocations) {
        super.onPostExecute(storesLocations);
        progressDialog.hide();

        if (storesLocations != null) {
            mapFragment.addStoresMarkers(storesLocations);
        } else {
            Toast.makeText(context, "No route found", Toast.LENGTH_SHORT).show();
        }
    }



}
