package saikarthik.locateme;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import java.io.IOException;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class GPSActivity extends AppCompatActivity {

    //private static final String BASE_URL = "http://192.168.43.176/a/p1.php";
    //private OkHttpClient client = new OkHttpClient();
    Button getloc;
    TextView timepass;
    TextView result;
    Geocoder geocoder;
    List<Address> addressList,addressList2;
    GPSTracker gps;
    TextView nearLoc;
    double latitude=0.0, longitude=0.0;
    Button be,bn,bs,bw,maps;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        result = (TextView)findViewById(R.id.result);
        geocoder = new Geocoder(this, Locale.getDefault());
        getloc = (Button)findViewById(R.id.getLocation);
        timepass = (TextView)findViewById(R.id.timepass);
        be = (Button)findViewById(R.id.eas);
        bn = (Button)findViewById(R.id.nor);
        bs = (Button)findViewById(R.id.sou);
        bw = (Button)findViewById(R.id.wes);
        be.setVisibility(View.GONE);
        bw.setVisibility(View.GONE);
        bn.setVisibility(View.GONE);
        bs.setVisibility(View.GONE);
        maps = (Button)findViewById(R.id.maps);
        maps.setVisibility(View.GONE);

        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);
        if (isFirstRun) {
            //show start activity
            startActivity(new Intent(GPSActivity.this, Init.class));
        }

        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("isFirstRun", false).apply();

        getloc.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AksHelper aksHelper = new AksHelper(GPSActivity.this);
            ArrayList ar = aksHelper.getData();
            name = ar.get(0).toString();
            aksHelper.close();
            be.setVisibility(View.VISIBLE);
            bw.setVisibility(View.VISIBLE);
            bn.setVisibility(View.VISIBLE);
            bs.setVisibility(View.VISIBLE);
            maps.setVisibility(View.VISIBLE);
            if (ContextCompat.checkSelfPermission(GPSActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(GPSActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(GPSActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            else {
                boolean connected = false;
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    connected = true;
                    gps = new GPSTracker(GPSActivity.this, GPSActivity.this);
                    if (gps.canGetLocation()) {
                        latitude = gps.getLatitude();
                        longitude = gps.getLongitude();
                        try {
                            TimeUnit.SECONDS.sleep(5);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        try {
                            addressList = geocoder.getFromLocation(latitude, longitude, 1);
                            StringBuilder address = new StringBuilder();
                            address.append(addressList.get(0).getAddressLine(0)).append(", ").append(addressList.get(0).getAddressLine(1)).append(", ");
                            address.append(addressList.get(0).getLocality()).append(", ").append(addressList.get(0).getSubAdminArea()).append(", ");
                            address.append(addressList.get(0).getAdminArea()).append(", ").append(addressList.get(0).getCountryName()).append(", ");
                            address.append(addressList.get(0).getPostalCode()).append(", ");
                            result.setText(address.toString());
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }

                        /*
                        ................................... Web Activity here ...........................
                        */

                        timepass.setText("The Nearest Locations for: \n");
                    }
                    else {
                        gps.showSettingsAlert();
                    }
                }
                else {
                    connected = false;
                }
                if (!connected) {
                    Toast.makeText(GPSActivity.this, "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
                }

            }
        }
        });


        maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GPSActivity.this,GoogleMaps.class);
                Bundle bun = new Bundle();
                bun.putDouble("lat",latitude);
                bun.putDouble("lon",longitude);
                intent.putExtras(bun);
                startActivity(intent);
            }
        });


       // ---------------------------------------------------------------------

        bn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nearLoc.setText("");
                List<String> nloc1 = new ArrayList<>();
                double j1, j2;
                String direction = "North\n";
                for (int i = 1; i < 5; i++) {
                    j1 = i * 0.001 * 5;
                    try {
                        addressList2 = geocoder.getFromLocation(latitude + j1, longitude, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String addressStr2_ = addressList2.get(0).getAddressLine(1); //  area
                    String areaStr_ = addressList2.get(0).getLocality(); // main place - Ambattur
                    String subAdminArea_ = addressList2.get(0).getSubAdminArea(); // District
                    String cityStr_ = addressList2.get(0).getAdminArea(); // city
                    String distance = Integer.toString((int)(500 * i));
                    String fullAddress_ = "Distance: " + distance + "m, Direction: " + direction + "Address: " + addressStr2_ + ", " + subAdminArea_ + ", " + areaStr_ + ", " + cityStr_;
                    nloc1.add(i - 1, fullAddress_);
                }
                for (int j = 0; j < 2; j++) {
                    for (int i = 1; i < 5; i++) {
                        j1 = i * 0.001 * 5;
                        j2 = j1 * (Math.pow(-1,j));
                        try {
                            addressList2 = geocoder.getFromLocation(latitude + j1, longitude + j2, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String addressStr2_ = addressList2.get(0).getAddressLine(1); //  area
                        String areaStr_ = addressList2.get(0).getLocality(); // main place - Ambattur
                        String subAdminArea_ = addressList2.get(0).getSubAdminArea(); // District
                        String cityStr_ = addressList2.get(0).getAdminArea(); // city
                        String distance = Integer.toString((int) (500 * i));
                        String fullAddress_ = "Distance: " + distance + "m, Direction: " + direction + "Address: " + addressStr2_ + ", " + subAdminArea_ + ", " + areaStr_ + ", " + cityStr_;
                        nloc1.add(i - 1+4, fullAddress_);
                    }
                }
                for (int i = 0; i < nloc1.size(); i++) {
                    nearLoc.append("\n" + nloc1.get(i) + "\n");
                }
            }
        });

        be.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nearLoc.setText("");
                List<String> nloc2 = new ArrayList<>();
                double j1, j2;
                String direction = "East\n";
                for (int i = 1; i < 5; i++) {
                    j1 = i * 0.001 * 5;
                    j2 = j1;
                    try {
                        addressList2 = geocoder.getFromLocation(latitude, longitude + j2, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String addressStr2_ = addressList2.get(0).getAddressLine(1); //  area
                    String areaStr_ = addressList2.get(0).getLocality(); // main place - Ambattur
                    String subAdminArea_ = addressList2.get(0).getSubAdminArea(); // District
                    String cityStr_ = addressList2.get(0).getAdminArea(); // city
                    String distance = Integer.toString((int)(500 * i));
                    String fullAddress_ = "Distance: " + distance + "m, Direction: " + direction + "Address: " + addressStr2_ + ", " + subAdminArea_ + ", " + areaStr_ + ", " + cityStr_;
                    nloc2.add(i - 1, fullAddress_);
                }
                for (int j = 0; j < 2; j++) {
                    for (int i = 1; i < 5; i++) {
                        j1 = i * 0.001 * 5;
                        j2 = j1 * (Math.pow(-1,j));
                        try {
                            addressList2 = geocoder.getFromLocation(latitude + j2, longitude + j1, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String addressStr2_ = addressList2.get(0).getAddressLine(1); //  area
                        String areaStr_ = addressList2.get(0).getLocality(); // main place - Ambattur
                        String subAdminArea_ = addressList2.get(0).getSubAdminArea(); // District
                        String cityStr_ = addressList2.get(0).getAdminArea(); // city
                        String distance = Integer.toString((int) (500 * i));
                        String fullAddress_ = "Distance: " + distance + "m, Direction: " + direction + "Address: " + addressStr2_ + ", " + subAdminArea_ + ", " + areaStr_ + ", " + cityStr_;
                        nloc2.add(i - 1+4, fullAddress_);
                    }
                }

                for (int i = 0; i < nloc2.size(); i++) {
                    nearLoc.append("\n" + nloc2.get(i) + "\n");
                }
            }
        });

        bs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nearLoc.setText("");
                List<String> nloc3 = new ArrayList<>();
                double j1, j2;
                String direction = "South\n";
                for (int i = 1; i < 5; i++) {
                    j1 = i * 0.001 * 5;
                    try {
                        addressList2 = geocoder.getFromLocation(latitude - j1, longitude, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String addressStr2_ = addressList2.get(0).getAddressLine(1); //  area
                    String areaStr_ = addressList2.get(0).getLocality(); // main place - Ambattur
                    String subAdminArea_ = addressList2.get(0).getSubAdminArea(); // District
                    String cityStr_ = addressList2.get(0).getAdminArea(); // city
                    String distance = Integer.toString((int)(500 * i));
                    String fullAddress_ = "Distance: " + distance + "m, Direction: " + direction + "Address: " + addressStr2_ + ", " + subAdminArea_ + ", " + areaStr_ + ", " + cityStr_;
                    nloc3.add(i - 1, fullAddress_);
                }
                for (int j = 0; j < 2; j++) {
                    for (int i = 1; i < 5; i++) {
                        j1 = i * 0.001 * 5;
                        j2 = j1 * (Math.pow(-1,j));
                        try {
                            addressList2 = geocoder.getFromLocation(latitude - j1, longitude + j2, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String addressStr2_ = addressList2.get(0).getAddressLine(1); //  area
                        String areaStr_ = addressList2.get(0).getLocality(); // main place - Ambattur
                        String subAdminArea_ = addressList2.get(0).getSubAdminArea(); // District
                        String cityStr_ = addressList2.get(0).getAdminArea(); // city
                        String distance = Integer.toString((int) (500 * i));
                        String fullAddress_ = "Distance: " + distance + "m, Direction: " + direction + "Address: " + addressStr2_ + ", " + subAdminArea_ + ", " + areaStr_ + ", " + cityStr_;
                        nloc3.add(i - 1+4, fullAddress_);
                    }
                }
                for (int i = 0; i < nloc3.size(); i++) {
                    nearLoc.append("\n" + nloc3.get(i) + "\n");
                }
            }
        });

        bw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nearLoc.setText("");
                List<String> nloc4 = new ArrayList<>();
                double j1, j2;
                String direction = "West\n";
                for (int i = 1; i < 5; i++) {
                    j1 = i * 0.001 * 5;
                    j2 = j1;
                    try {
                        addressList2 = geocoder.getFromLocation(latitude, longitude - j2, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String addressStr2_ = addressList2.get(0).getAddressLine(1); //  area
                    String areaStr_ = addressList2.get(0).getLocality(); // main place - Ambattur
                    String subAdminArea_ = addressList2.get(0).getSubAdminArea(); // District
                    String cityStr_ = addressList2.get(0).getAdminArea(); // city
                    String distance = Integer.toString((int)(500 * i));
                    String fullAddress_ = "Distance: " + distance + "m, Direction: " + direction + "Address: " + addressStr2_ + ", " + subAdminArea_ + ", " + areaStr_ + ", " + cityStr_;
                    nloc4.add(i - 1, fullAddress_);
                }
                for (int j = 0; j < 2; j++) {
                    for (int i = 1; i < 5; i++) {
                        j1 = i * 0.001 * 5;
                        j2 = j1 * (Math.pow(-1,j));
                        try {
                            addressList2 = geocoder.getFromLocation(latitude - j2, longitude - j1, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String addressStr2_ = addressList2.get(0).getAddressLine(1); //  area
                        String areaStr_ = addressList2.get(0).getLocality(); // main place - Ambattur
                        String subAdminArea_ = addressList2.get(0).getSubAdminArea(); // District
                        String cityStr_ = addressList2.get(0).getAdminArea(); // city
                        String distance = Integer.toString((int) (500 * i));
                        String fullAddress_ = "Distance: " + distance + "m, Direction: " + direction + "Address: " + addressStr2_ + ", " + subAdminArea_ + ", " + areaStr_ + ", " + cityStr_;
                        nloc4.add(i - 1+4, fullAddress_);
                    }
                }
                for (int i = 0; i < nloc4.size(); i++) {
                    nearLoc.append("\n" + nloc4.get(i) + "\n");
                }
            }
        });


    //     -----------------------------------------------------------------------------


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    gps = new GPSTracker(GPSActivity.this, GPSActivity.this);
                    if (gps.canGetLocation()) {
                        double latitude = gps.getLatitude();
                        double longitude = gps.getLongitude();
                    }
                    else {
                        gps.showSettingsAlert();
                    }
                }
                else {
                    Toast.makeText(GPSActivity.this, "You need to grant permission",Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}
