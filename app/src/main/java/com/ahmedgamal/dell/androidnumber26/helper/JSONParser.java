package com.ahmedgamal.dell.androidnumber26.helper;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by ahmed gamal on 09/2/2015.
 */
public class JSONParser {

    public JSONParser() {

    }

    public String getJSONFromUrl(String path) {
        // Making HTTP request
        String json = null;
        try {
            URL u = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);

            InputStream in = new BufferedInputStream(conn.getInputStream());
            json = org.apache.commons.io.IOUtils.toString(in, "UTF-8");

        } catch (IOException e) {
            //e.printStackTrace();
        }
        return json;
    }

/*    public String getResponseStr(String url) {
        try {
            URL mUrl = new URL(url);
            InputStream input = mUrl.openConnection().getInputStream();
            StringBuffer sb = new StringBuffer();
            BufferedReader localBuff = new BufferedReader(new InputStreamReader(input, "UTF-8"));
            String temp = null;
            while ((temp = localBuff.readLine()) != null) {
                sb.append(temp).append("\n");
            }

            return sb.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
*/
    public List<LatLng> getStoresLocationFromJSON(String jsonResponse) {
        List<LatLng> storesLoc = new ArrayList<LatLng>();
        try {
            if (!jsonResponse.isEmpty()) {
                JSONArray allStores = new JSONArray(jsonResponse);
                JSONObject locObj = null;
                for (int i = 0; i < allStores.length(); i++) {
                    locObj = allStores.getJSONObject(i);
                    LatLng oneLoc = new LatLng(Double.parseDouble(locObj.getString("lat")), Double.parseDouble(locObj.getString("lng")));
                    storesLoc.add(oneLoc);
                }

            }
        } catch (Exception e) {
            return null;
        }
        //
        return storesLoc;
    }

    public List<LatLng> getPathLatLngFromJSON(String result) {
        List<LatLng> list = null;
        try {
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes
                    .getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            list = decodePoly(encodedString);

        } catch (Exception e) {
            return null;
        }
        return list;
    }

    // decodes encoded String into PolyLine object
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

}
