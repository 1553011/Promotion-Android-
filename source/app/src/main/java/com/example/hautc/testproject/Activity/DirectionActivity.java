package com.example.hautc.testproject.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.example.hautc.testproject.Fragment.ControlFragment.PromoInfo;
import com.example.hautc.testproject.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class DirectionActivity extends AppCompatActivity implements OnMapReadyCallback {

    // Biến các vị trí hiện tại
    LatLng currentPos;
    LatLng destionalPos;

    // Dữ liệu và bine61 google map
    PromoInfo info;
    GoogleMap mMap;

    String key = "AIzaSyDd_h_-wgTVp7nYib87JdS56pDkmuO_6q8";

    FloatingActionButton update;

    private static final int[] COLORS = new int[]{R.color.colorPrimaryDark,R.color.colorPrimary,R.color.colorPrimaryLight,R.color.colorAccent,R.color.primary_dark_material_light};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);

        //Khởi tạo map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Lấy dữ liệu dầu vào và map các view
        Intent intent = getIntent();
        info = (PromoInfo) intent.getSerializableExtra("data");
        double lat = intent.getDoubleExtra("lat", 0);
        double lng = intent.getDoubleExtra("lng", 0);
        currentPos = new LatLng(lat, lng);
        destionalPos = new LatLng(info.getLat(), info.getLng());

        update = findViewById(R.id.update_location);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Move camera về vị trí hiện tại
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPos,16));
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.addMarker(new MarkerOptions().position(currentPos).title("Current position"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPos,16));
        mMap.addMarker(new MarkerOptions().position(destionalPos).title(info.getAddress()));
        findPath(destionalPos);
    }

    /***
     * Tìm đường đi ngắn nhất tới destination
     * @param destination điểm đến
     */
    void findPath(LatLng destination){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("FInd your path! Please wait");
        progressDialog.show();
        // Khởi tạo biến tìm đường
        Routing routing = new Routing.Builder()
                .travelMode(Routing.TravelMode.DRIVING)
                .withListener(new RoutingListener() {
                    @Override
                    public void onRoutingFailure(RouteException e) {

                    }

                    @Override
                    public void onRoutingStart() {

                    }

                    @Override
                    public void onRoutingSuccess(ArrayList<Route> arrayList, int i) {
                        ArrayList polylines = new ArrayList<>();
                        //add route(s) to the map.
                        for (int f = 0; f <arrayList.size(); f++) {
                            //In case of more than 5 alternative routes
                            int colorIndex = i % COLORS.length;

                            PolylineOptions polyOptions = new PolylineOptions();
                            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
                            polyOptions.width(10 + i * 3);
                            polyOptions.addAll(arrayList.get(i).getPoints());
                            Polyline polyline = mMap.addPolyline(polyOptions);
                            polylines.add(polyline);
                        }
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onRoutingCancelled() {

                    }
                })
                .waypoints(currentPos, destination)
                .key(key)
                .build();
        routing.execute();
    }
}
