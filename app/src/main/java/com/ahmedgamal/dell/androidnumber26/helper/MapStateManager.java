package com.ahmedgamal.dell.androidnumber26.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by ahmed gamal on 09/2/2015.
 */
public class MapStateManager {
    private final String LATITUDE = "latitude";
    private final String LONGITUDE = "longitude";
    private final String TILT = "tilt";
    private final String ZOOM = "zoom";
    private final String BEARING = "bearing";
    private final String MAPTYPE = "maptype";
    private final String PREFS_NAME = "mapCameraState";

    private SharedPreferences mapStatePrefs;

    public MapStateManager(Context context) {
        mapStatePrefs = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
    }

    public void saveMapState(GoogleMap map) {
        SharedPreferences.Editor editor = mapStatePrefs.edit();
        CameraPosition position = map.getCameraPosition();

        editor.putFloat(LATITUDE, (float) position.target.latitude);
        editor.putFloat(LONGITUDE, (float) position.target.longitude);
        editor.putFloat(ZOOM, (float) position.zoom);
        editor.putFloat(TILT, (float) position.tilt);
        editor.putFloat(BEARING, (float) position.bearing);
        editor.putInt(MAPTYPE, map.getMapType());

        editor.commit();
    }

    public CameraPosition getSavedCameraPosition() {
        double latitude = mapStatePrefs.getFloat(LATITUDE, 0);
        if (latitude == 0)
            return null;

        double longitude = mapStatePrefs.getFloat(LONGITUDE, 0);
        LatLng latLng = new LatLng(latitude, longitude);

        float zoom = mapStatePrefs.getFloat(ZOOM, 0);
        float bearing = mapStatePrefs.getFloat(BEARING, 0);
        float tilt = mapStatePrefs.getFloat(TILT, 0);

        CameraPosition position = new CameraPosition(latLng, zoom, tilt, bearing);
        return position;
    }

    public int getSavedMapType() {
        int mapType = mapStatePrefs.getInt(MAPTYPE, 0);
        if (mapType != 0)
            return mapType;

        return GoogleMap.MAP_TYPE_NORMAL;
    }
}
