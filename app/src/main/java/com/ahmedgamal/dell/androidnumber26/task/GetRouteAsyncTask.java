package com.ahmedgamal.dell.androidnumber26.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.ahmedgamal.dell.androidnumber26.helper.JSONParser;
import com.ahmedgamal.dell.androidnumber26.fragment.MapFragment;


/**
 * Created by ahmed gamal on 09/2/2015.
 * Copy right http://stackoverflow.com/questions/24992851/getting-direction-between-two-points-in-google-maps-crossing-specific-points
 */
public class GetRouteAsyncTask extends AsyncTask<Void, Void, String> {

    private ProgressDialog progressDialog;
    String url;
    Context context;
    MapFragment mapFragment;

    public GetRouteAsyncTask(String urlPass, Context context, MapFragment mapFragment) {
        url = urlPass;
        this.context = context;
        this.mapFragment = mapFragment;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Getting Path...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            JSONParser jParser = new JSONParser();
            String json = jParser.getJSONFromUrl(url);
            return json;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        progressDialog.hide();
        if (result != null) {
            mapFragment.drawPath(result);
        } else {
            Toast.makeText(context, "No route found", Toast.LENGTH_SHORT).show();
        }
    }
}
