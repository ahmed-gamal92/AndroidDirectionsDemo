package com.ahmedgamal.dell.androidnumber26.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ahmedgamal.dell.androidnumber26.helper.JSONParser;
import com.ahmedgamal.dell.androidnumber26.task.GetLocationAsyncTask;
import com.ahmedgamal.dell.androidnumber26.util.AppUtils;
import com.ahmedgamal.dell.androidnumber26.task.GetRouteAsyncTask;
import com.ahmedgamal.dell.androidnumber26.task.GetStoresAsyncTask;
import com.ahmedgamal.dell.androidnumber26.helper.GoogleApiHelper;
import com.ahmedgamal.dell.androidnumber26.helper.MapStateManager;
import com.ahmedgamal.dell.androidnumber26.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.VisibleRegion;

import java.util.List;
/**
 * Created by ahmed gamal on 09/2/2015.
 */
public class MapFragment extends Fragment {

    private final int GPS_ERRORDIALOG_REQUEST = 500;
    private final float DEFAULT_ZOOM = 7;


    private Context context;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private GoogleApiHelper googleApiHelper;

    private boolean googlePlayServicesOk;
    //
    public final static String MODE_WALKING = "walking";

    public MapFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);


        if (servicesOK()) {

            if (initMap()) {

                googlePlayServicesOk = true;
                resetMapControllers(rootView);

                //Get google api client
                googleApiHelper = new GoogleApiHelper(context);
            }

        }

        return rootView;
    }

    public void getStores() {
        VisibleRegion visibleRegion = mMap.getProjection().getVisibleRegion();
        LatLng nearLeft = visibleRegion.nearLeft;
        //LatLng nearRight = visibleRegion.nearRight;
        //LatLng farLeft = visibleRegion.farLeft;
        LatLng farRight = visibleRegion.farRight;
        //
        //String storesURL = makeStoresURL(52.48675300749431,13.35877465576175,52.54420821064123,13.444605344238312);
        String storesURL = makeStoresURL(nearLeft.latitude, nearLeft.longitude, farRight.latitude, farRight.longitude);
        //Log.d("JSON", "url " + storesURL);
        if(AppUtils.isConnectingToInternet(context)) {
            try {
                new GetStoresAsyncTask(storesURL, context, this).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
            }catch (Exception e) {
                Toast.makeText(context, "An Error Ocuured, Please try again", Toast.LENGTH_LONG).show();
            }
        }
    }


    /**
     * Move  position button to map center right and hides compass and zoom
     */
    private void resetMapControllers(View rootView) {
        // hide zoom and compass buttons
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setCompassEnabled(false);
        uiSettings.setZoomControlsEnabled(false);
        ImageButton locBtn = (ImageButton) rootView.findViewById(R.id.btn_loc);
        locBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCurrentLocation();
            }
        });
    }

    private void goToCurrentLocation() {
        try {
            new GetLocationAsyncTask(context, this, googleApiHelper).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        }catch (Exception e) {
            Toast.makeText(context, R.string.str_err_get_curr_loc, Toast.LENGTH_LONG).show();
        }
    }

    public void currentLocationFound (Location loc) {
        LatLng currLoc =  new LatLng(loc.getLatitude(),loc.getLongitude());
        goToLocation(currLoc, DEFAULT_ZOOM, false);
        addMarker(currLoc, true);
    }

    /***
     * moves camera to specific location
     *
     * @param loc
     * @param zoom
     * @param moveCamera
     */
    private void goToLocation(LatLng loc, float zoom, boolean moveCamera) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(loc, zoom);
        if (moveCamera)
            mMap.moveCamera(cameraUpdate);
        else
            mMap.animateCamera(cameraUpdate);

    }

    /**
     * pins a marker at specified location, removes it if already exists
     *
     * @param loc
     */
    public void addMarker(LatLng loc, boolean isSource) {
        // marker
        BitmapDescriptor markerFromBitmapDescriptor;

        if (isSource) {
            markerFromBitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);

        }else{
            markerFromBitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
        }

        MarkerOptions markerOptions = new MarkerOptions().title("").position(loc).
                icon(markerFromBitmapDescriptor);
        mMap.addMarker(markerOptions);
    }

    public void addStoresMarkers(List<LatLng> storesLocations) {
        for (LatLng loc : storesLocations) {
            addMarker(loc, false);
        }
    }

    private boolean initMap() {
        if (mMap == null) {
            mapFragment = ((SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.map));
            mMap = mapFragment.getMap();

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {

                    Location currLoc = googleApiHelper.getCurrentLocation();
                    if (currLoc == null) {
                        Toast.makeText(context, R.string.str_err_loc_not_available, Toast.LENGTH_LONG).show();
                    } else {
                        LatLng currLatLng = new LatLng(currLoc.getLatitude(), currLoc.getLongitude());
                        addMarker(currLatLng, true);
                        if (!currLatLng.equals(marker.getPosition()))
                            drawPath(currLatLng, marker.getPosition());
                    }
                    return false;
                }
            });

        }
        return (mMap != null);
    }

    public void drawPath(LatLng src, LatLng dest) {
        String url = makeRouteURL(src.latitude, src.longitude, dest.latitude,
                dest.longitude);
        if (AppUtils.isConnectingToInternet(context)) {
            try {
                new GetRouteAsyncTask(url, context, this).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
            }catch (Exception e) {
                Toast.makeText(context, "An Error Ocuured, Please try again", Toast.LENGTH_LONG).show();
            }
        }
    }

    /***
     * check google play services installed on host device
     *
     * @return
     */
    private boolean servicesOK() {
        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable, (Activity) context, GPS_ERRORDIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(context, "Can't connect to google play services", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (googlePlayServicesOk) {
            MapStateManager mapStateManager = new MapStateManager(context);
            CameraPosition cameraPosition = mapStateManager.getSavedCameraPosition();
            if (cameraPosition != null) {
                CameraUpdate update = CameraUpdateFactory.newCameraPosition(cameraPosition);
                mMap.moveCamera(update);
                mMap.setMapType(mapStateManager.getSavedMapType());
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (googlePlayServicesOk) {
            MapStateManager mapStateManager = new MapStateManager(context);
            mapStateManager.saveMapState(mMap);
        }
    }

    /**
     * removes all map drawings
     */
    public void clear() {
        mMap.clear();
    }

    /**
     * generates the url to get location
     * @param sourcelat
     * @param sourcelog
     * @param destlat
     * @param destlog
     * @return
     */
    private String makeRouteURL(double sourcelat, double sourcelog, double destlat,
                                double destlog) {
        StringBuilder urlString = new StringBuilder();
        urlString.append("http://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString.append(Double.toString(sourcelog));
        urlString.append("&destination=");// to
        urlString.append(Double.toString(destlat));
        urlString.append(",");
        urlString.append(Double.toString(destlog));
        urlString.append("&sensor=false&mode=" + MODE_WALKING + "&alternatives=true");
        return urlString.toString();
    }

    /**
     * generates url to get stores locations
     * @param sourcelat
     * @param sourcelog
     * @param destlat
     * @param destlog
     * @return
     */
    private String makeStoresURL(double sourcelat, double sourcelog, double destlat,
                                 double destlog) {
        StringBuilder urlString = new StringBuilder();

        urlString.append("https://www.barzahlen.de/filialfinder/get_stores?map_bounds=((");
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString.append(Double.toString(sourcelog));
        urlString.append("),(");// to
        urlString.append(Double.toString(destlat));
        urlString.append(",");
        urlString.append(Double.toString(destlog));
        urlString.append("))");
        return urlString.toString();
    }

    // draws path from result String
    public void drawPath(String result) {
        try {
            // Tranform the string into a json object
            JSONParser jsonParser = new JSONParser();
            List<LatLng> list = (jsonParser.getPathLatLngFromJSON(result));

            for (int z = 0; z < list.size() - 1; z++) {
                LatLng src = list.get(z);
                LatLng dest = list.get(z + 1);
                mMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(src.latitude, src.longitude),
                                new LatLng(dest.latitude, dest.longitude))
                        .width(10).color(Color.BLUE).geodesic(true));
            }

        } catch (Exception e) {
            Toast.makeText(context, "No route found", Toast.LENGTH_SHORT).show();
        }
    }


}