package com.example.amolgursali.googlemap;

import android.graphics.Color;
import android.location.Location;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static com.example.amolgursali.googlemap.GoogleMapsBottomSheetBehavior.PEEK_HEIGHT_AUTO;

public class DirectionActivity extends FragmentActivity  {

    SupportMapFragment supportMapFragment;
    GoogleMap googleMap;
    List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
    LatLng start=new LatLng(19.147067,72.835767);
    LatLng end=new LatLng(19.118980,72.848172);
    String url;
    View view1;
    TextView date,time;
    BottomSheetDialog bottomSheetDialog;
    GoogleMapsBottomSheetBehavior bottomSheetBehavior;
    List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();
    Random rnd = new Random();
    PolylineOptions polyLineOptions = null;
    LinearLayout detail;
    TextView detailaddress;
    List<String> detailaddressList;
    NestedScrollView nested;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction_acctivity);
        supportMapFragment=(SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        googleMap=supportMapFragment.getMap();
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        // if we want direction from source to destination then we need to use google map json api.
        // if we want to point out only one location then just use MarkerOption to set the marker for particular location.

       /* MarkerOptions markerOptions=new MarkerOptions();

        markerOptions.position(start);

        markerOptions.position(end);

        googleMap.addMarker(markerOptions);*/

        bottomSheetDialog = new BottomSheetDialog(DirectionActivity.this);

        view1 = findViewById(R.id.bottomsheet);

        nested=(NestedScrollView)findViewById(R.id.bottomsheet);

        detail=(LinearLayout)findViewById(R.id.detail);


//        view2=findViewById(R.id.content);

        bottomSheetBehavior = GoogleMapsBottomSheetBehavior.from(view1);

        bottomSheetBehavior.setPeekHeight(200); // in pixels
// or use the default peek height, which is different from the support library
        bottomSheetBehavior.setPeekHeight(PEEK_HEIGHT_AUTO);
        bottomSheetBehavior.setState(GoogleMapsBottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setHideable(false);

        date=(TextView)findViewById(R.id.date);

        time=(TextView)findViewById(R.id.time);

        webService();

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start,16));

        addMarker();

    }

    private void addMarker()
    {
        if (googleMap != null) {
            googleMap.addMarker(new MarkerOptions().position(start)
                    .title("Office"));
            googleMap.addMarker(new MarkerOptions().position(end)
                    .title("Home"));

        }
    }

    private void webService()
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        double latFrom=19.147067;
        double lngFrom=72.835767;
        double latTo=19.118980;
        double lngTo=72.848172;
        final List<PolylineOptions> polylines=new ArrayList<>();
        url="http://maps.googleapis.com/maps/api/directions/json?origin=" + latFrom + "," + lngFrom + "&destination=" + latTo + "," + lngTo + "&mode=driving&alternatives=false&sensor=false&units=metric";
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                Log.d("URL",url);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("OK")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("routes");
                        for (int i = 0; i < jsonArray.length(); i++)
                        {
                            JSONObject c = jsonArray.getJSONObject(i);
                            JSONArray legsarray = c.getJSONArray("legs");

                            for (int j = 0; j < legsarray.length(); j++) {
                                JSONObject ca = legsarray.getJSONObject(j);

                                JSONObject km=ca.getJSONObject("distance");

                                date.setText("Distance="+km.getString("text"));

                                JSONObject t=ca.getJSONObject("duration");

                                time.setText("Duration="+t.getString("text"));

                                JSONObject endlocation = ca.getJSONObject("end_location");
                                String endlat = endlocation.getString("lat");
                                String endlongt = endlocation.getString("lng");

                                JSONObject startlocation = ca.getJSONObject("start_location");
                                String startlat = startlocation.getString("lat");
                                String startlongt = startlocation.getString("lng");

                                JSONArray steps = ca.getJSONArray("steps");
                                for (int step = 0; step < steps.length(); step++)
                                {
                                    String polyline = "";
                                    JSONObject p = steps.getJSONObject(step);

                                    detailaddress=new TextView(DirectionActivity.this);
                                    detailaddress.setText(Html.fromHtml(String.valueOf(Html.fromHtml(p.getString("html_instructions").toString()))));
                                    detail.addView(detailaddress);


                                    JSONObject jp = p.getJSONObject("polyline");
                                    polyline = jp.getString("points");
                                    List<LatLng> list = decodePoly(polyline);


                                    for (int l = 1; l < list.size(); l++) {
                                        HashMap<String, String> hm = new HashMap<String, String>();
                                        hm.put("lat",
                                                Double.toString(((LatLng) list.get(l)).latitude));
                                        hm.put("lng",
                                                Double.toString(((LatLng) list.get(l)).longitude));
                                        path.add(hm);
                                    }

                                }
                            }

                        }
                        routes.add(path);
                        ArrayList<LatLng> points = null;

                        for (int i1 = 0; i1 < routes.size(); i1++) {
                            points = new ArrayList<LatLng>();
                            polyLineOptions = new PolylineOptions();
                            List<HashMap<String, String>> path1 = routes.get(i1);

                            for (int j = 0; j < path1.size(); j++) {
                                HashMap<String, String> point = path1.get(j);

                                double lat = Double.parseDouble(point.get("lat"));
                                double lng = Double.parseDouble(point.get("lng"));
                                LatLng position = new LatLng(lat, lng);

                                points.add(position);
                            }

                            polyLineOptions.addAll(points);
                            polyLineOptions.width(10);
                            polyLineOptions.geodesic(true);
                            polyLineOptions.color(getResources().getColor(R.color.colorPrimaryDark));
                            polylines.add(polyLineOptions);
                        }

                        googleMap.addPolyline(polyLineOptions);


                        bottomSheetDialog.show();

                        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                            @Override
                            public void onMapClick(LatLng clickCoords) {
                                for (PolylineOptions polyline : polylines) {


                                    // do whatever you want here on click of map

                                   /* for (LatLng polyCoords : polyline.getPoints()) {
                                        float[] results = new float[1];
                                        Location.distanceBetween(clickCoords.latitude, clickCoords.longitude,
                                                polyCoords.latitude, polyCoords.longitude, results);

                                        if (results[0] < 100) {
                                            // If distance is less than 100 meters, this is your polyline
                                            Log.e("InfoLatLang", "Found @ "+clickCoords.latitude+" "+clickCoords.longitude);
                                        }
                                    }*/


                                }
                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(DirectionActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                error.printStackTrace();
            }
        });
        queue.add(stringRequest);
    }

    private List<LatLng> decodePoly(String encoded)
    {
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

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
