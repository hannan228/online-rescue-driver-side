package com.example.driverside;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;

import com.example.driverside.Model.ActiveUser;
import com.example.driverside.Model.DriverType;
import com.example.driverside.Model.LocationInfo;
import com.example.driverside.Model.UserRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, RoutingListener {
    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private DatabaseReference myRef;
    private DatabaseReference myRef1,myRef2,myRef3,activeCase,activeCaseDriver;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String subEmail;
    private String userEmail;
    private static final String TAG = "MapsActivity";
    private static String driverType;
    private TextView mapTitle;
    private double lat,log,latitude,longitude;
    private TextView estimatedDistanceMap,estimatedTimeMap;
    private String availableStatus="available";
    private int i,j = 0;
    private LocationInfo locationInfo;
    private ActiveUser activeUser;
    private DriverType driverType1;
    private LatLng callerLatLong;
    private double km = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        driverType = intent.getStringExtra("rescuer");
        availableStatus = intent.getStringExtra("availableStatus");
        //Log.d(TAG, "check point intent 2 "+driverType1);
        database = FirebaseDatabase.getInstance();

        mapTitle = findViewById(R.id.mapTitleID);
        estimatedDistanceMap = findViewById(R.id.distanceLocation);
        estimatedTimeMap = findViewById(R.id.estimatedTimeLocation);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        subEmail = mUser.getEmail();
        subEmail = subEmail.substring(0, subEmail.indexOf("."));
        myRef3 = database.getReference("ActiveDriver").child(subEmail).child("user request");
        activeCase = database.getReference("Active Case").child(subEmail);

        mapTitle.setText("Available: "+driverType);
        Log.d(TAG, "onCreate: .com"+subEmail);
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                lat = location.getLatitude();
                log = location.getLongitude();

                if (driverType!= null) {
                    if (lat > 20.0 && log > 20.0) {
                        LatLng driverLocation = new LatLng(lat, log);

                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(driverLocation).title("driver is here").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulance_small)));
                        if (availableStatus.equals("available")){
                        myRef3.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                                        ActiveUser activeUser = child.getValue(ActiveUser.class);

                                        callerLatLong = new LatLng(Double.parseDouble(activeUser.getLat()), Double.parseDouble(activeUser.getLog()));
                                        mMap.addMarker(new MarkerOptions().position(callerLatLong).title("caller is here"));
                                        availableStatus = "notAvailable";
                                        userEmail = child.getKey();
                                        UserRequest userRequest = new UserRequest("" + lat, "" + log, "" + (child.getKey()));
                                        activeCase.setValue(userRequest);

                                    }
                                } else {
                                    Log.d(TAG, "onDataChange: here" + dataSnapshot);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            } });
                    }else if (availableStatus.equals("notAvailable")){  // is ko 1 dafa e chla den sai trh attw kafi h
                            activeCase.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()) {
                                        availableStatus = "notAvailable";
                                        UserRequest userRequest = dataSnapshot.getValue(UserRequest.class);
                                        userEmail = userRequest.getEmail();
                                        activeCaseDriver = database.getReference("Active Case").child(""+userEmail);
                                        activeCaseDriver.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.exists()) {
                                                    UserRequest userRequest = dataSnapshot.getValue(UserRequest.class);
                                                    latitude = Double.parseDouble(userRequest.getLat());
                                                    longitude = Double.parseDouble(userRequest.getLog());

                                                    LatLng callerLtLng = new LatLng(latitude,longitude);
                                                    mMap.addMarker(new MarkerOptions().position(callerLtLng).title("caller is here"));
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                            }
                                        }); } }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });
                        }else {

                        }
                        i= i+1;

                        if (userEmail!=null && lat!=0.0 && log != 0.0 && latitude!=0.0 && longitude != 0.0){
                            UserRequest userRequest = new UserRequest(""+lat,""+log,""+userEmail);
                            activeCase.setValue(userRequest);

                            double dis = distance(lat, log, latitude, longitude);
                            km = dis / 0.62137;
                            estimatedDistanceMap.setText(new DecimalFormat("##.####").format(km) + " km");
                            estTime();

                            getRouteToMarker(lat,log,latitude,longitude);

                        }


                        callerLatLong = new LatLng(latitude,longitude);
                        mMap.addMarker(new MarkerOptions().position(callerLatLong).title("caller is here"));

                        locationInfo = new LocationInfo(""+lat,""+log);

                        driverType1 = new DriverType(""+lat,""+log,""+driverType);
                        activeUser = new ActiveUser(""+lat,""+log,""+driverType);


                        if (i==1) {
                            myRef1.setValue(activeUser);
                            myRef.setValue(locationInfo);
                        }
                        myRef2.setValue(driverType1);

                    } else {
                        Log.d(TAG, "acci else ");
                    }
                } else if (lat > 20 && log > 20) {
                    LatLng driverLocation = new LatLng(lat, log);
                    mapTitle.setText("Not available for any service..");
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(driverLocation).title("driver is here").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulance_small)));

                    // Log.d(TAG, "empty ");
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            mMap.setMyLocationEnabled(true);
            return;
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public void estTime(){
        if (km <= 1) {
            double time1 = km + 1;
            estimatedTimeMap.setText(new DecimalFormat("##.##").format(time1) + " min");
        } else if (km > 1 || km <= 2) {
            double time1 = km + 2;
            estimatedTimeMap.setText(new DecimalFormat("##.##").format(time1) + " min");
        } else if (km > 2 || km <= 4) {
            double time1 = km + 3;
            estimatedTimeMap.setText(new DecimalFormat("##.##").format(time1) + " min");
        } else if (km > 4 || km <= 10) {
            double time1 = km + 5;
            estimatedTimeMap.setText(new DecimalFormat("##.##").format(time1) + " min");
        } else if (km > 10) {
            double time1 = km + 6;
            estimatedTimeMap.setText(new DecimalFormat("##.##").format(time1) + " min");
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng newLocation = new LatLng(31.177167,74.105169);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(newLocation));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 15));
        mMap.addMarker(new MarkerOptions().position(newLocation).title("you are here").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulance_small)));
        //mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                try {
                    locationManager.wait(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        myRef1 = database.getReference("ActiveDriver").child(subEmail);
        myRef = database.getReference(""+driverType).child(subEmail);
        myRef2 = database.getReference(driverType+"fuck").child(subEmail);

    }


    // for draw route between two points from SIM coder

    private void getRouteToMarker(double lat2,double log2, double latitude2, double longitude2){
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(new LatLng(lat2, log2),new LatLng(latitude2,longitude2))
                .key("AIzaSyDpxqq5fXUcZSaH5SS_Luj2_uRpPxnNDP0")
                .build();
        routing.execute();
    }

    private List<Polyline> polylines;
    //private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};

    //when alternative paths needed then following code
    private static final int[] COLORS = new int[]{R.color.design_default_color_primary_dark,R.color.colorYellow,R.color.design_default_color_primary_dark,R.color.design_default_color_primary_dark,R.color.primary_dark_material_light};
    // when alternaive paths


    @Override
    public void onRoutingFailure(RouteException e) {
        if(e != null) {
            Toast.makeText(this, "Error:onRouting " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
//        if(polylines.size()>0) {
//            for (Polyline poly : polylines) {
//                poly.remove();
//            }
//        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);
//            Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingCancelled() {

    }
}
