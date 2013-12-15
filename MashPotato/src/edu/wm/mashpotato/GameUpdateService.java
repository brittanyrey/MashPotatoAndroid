package edu.wm.mashpotato;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import edu.wm.mashpotato.web.Constants;
import edu.wm.mashpotato.web.WebTask;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class GameUpdateService extends Service implements LocationListener{
        String username;
        String password;
        LocationManager mlocManager;
        String provider;
        Location l;
        double lat;
        double lng;
        private boolean canGetLocation;
        final static String TAG = "GameUpdateService";
        
        // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
 
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
        
        private class DownloadWebPageTask extends WebTask {

         public DownloadWebPageTask(boolean hasPairs, String username,
                                String password, List<NameValuePair> pairs, boolean isPost) {
                        super(hasPairs, username, password, pairs, isPost, false);
                }

                @Override
         protected void onPostExecute(String result) {
                 // Log.e(TAG, result);
         }
         }

    @Override
   public IBinder onBind(Intent intent) {
          // TODO: Return the communication channel to the service.
          throw new UnsupportedOperationException("Not yet implemented");
   }

   @Override
   public void onCreate() {
          // TODO Auto-generated method stub
//                  Log.i(TAG, "Service created! Hurray!");
// Toast.makeText(getApplicationContext(), "Service Created", 1).show();
                  mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

         LocationListener mlocListener = this;
         mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, 0, mlocListener);
                
        super.onCreate();
   }

   @Override
   public void onDestroy() {
          // TODO Auto-generated method stub
                  Log.i(TAG, "Service murdered! Hurray!");
// Toast.makeText(getApplicationContext(), "Service Destroy", 1).show();
          super.onDestroy();
   }

   @Override
   public int onStartCommand(Intent intent, int flags, int startId) {
          // TODO Auto-generated method stub
//         Log.i(TAG, "Run Service run! Lat " + lat + " Lng: " + lng);
        
         username = (String)intent.getExtras().get("username");
         password = (String)intent.getExtras().get("password");
// Toast.makeText(getApplicationContext(), "Service Running ", 1).show();
// Criteria c = new Criteria();
// mCurrentLocation = mLocationClient.getLastLocation();
// provider = locationManager.getBestProvider(c, false);
// l = locationManager.getLastKnownLocation(provider);
// double lng=l.getLongitude();
// double lat=l.getLatitude();
// System.out.println("Longitude: " + lng);
// System.out.println("Latitude: " + lat);
          List<NameValuePair> pairs = new ArrayList<NameValuePair>();
          this.getLocation();
          pairs.add(new BasicNameValuePair(Constants.lng, lng+""));
          pairs.add(new BasicNameValuePair(Constants.lat, lat+""));
          DownloadWebPageTask task = new DownloadWebPageTask(true, username, password, pairs, true);
          task.execute(new String[] { Constants.updatePlayerInfo });
          // Log.e(TAG, "Started");
          return super.onStartCommand(intent, flags, startId);
   }
   
   @Override
   public void onLocationChanged(Location loc)
   {
         // Log.e(TAG, "Loc changed");
       lat = loc.getLatitude();
       lng = loc.getLongitude();
       // Log.e(TAG, "lat " + lat + " lng " + lng);
// List<NameValuePair> pairs = new ArrayList<NameValuePair>();
// pairs.add(new BasicNameValuePair("lng", lng+""));
// pairs.add(new BasicNameValuePair("lat", lat+""));
// DownloadWebPageTask task = new DownloadWebPageTask(true, username, password, pairs, true);
// task.execute(new String[] { c.updateLocation() });

// String Text = "My current location is: " +
// "Latitud = " + loc.getLatitude() +
// "Longitud = " + loc.getLongitude();
//
// Toast.makeText( getApplicationContext(), Text, Toast.LENGTH_SHORT).show();
   }

   @Override
   public void onProviderDisabled(String provider)
   {
     Toast.makeText( getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT ).show();
   }

   @Override
   public void onProviderEnabled(String provider)
   {
     Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
   }

   @Override
   public void onStatusChanged(String provider, int status, Bundle extras)
   {

   }
   
   public Location getLocation() {
       Location location = null;
       // Log.e(TAG, "Get Location");
        try {

           // getting GPS status
           boolean isGPSEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

           // getting network status
           boolean isNetworkEnabled = mlocManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

           if (!isGPSEnabled && !isNetworkEnabled) {
               // no network provider is enabled
           } else {
               this.canGetLocation = true;
               // First get location from Network Provider
               if (isNetworkEnabled) {
                   mlocManager.requestLocationUpdates(
                           LocationManager.NETWORK_PROVIDER,
                           MIN_TIME_BW_UPDATES,
                           MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
// Log.d("Network", "Network");
                   if (mlocManager != null) {
                       location = mlocManager
                               .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                       if (location != null) {
                           lat = location.getLatitude();
                           lng = location.getLongitude();
                       }
                   }
               }
               // if GPS Enabled get lat/long using GPS Services
               if (isGPSEnabled) {
                   if (location == null) {
                         mlocManager.requestLocationUpdates(
                               LocationManager.GPS_PROVIDER,
                               MIN_TIME_BW_UPDATES,
                               MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
// Log.d("GPS Enabled", "GPS Enabled");
                       if (mlocManager != null) {
                           location = mlocManager
                                   .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                           if (location != null) {
                               lat = location.getLatitude();
                               lng = location.getLongitude();
                           }
                       }
                   }
               }
           }

       } catch (Exception e) {
           e.printStackTrace();
       }

       return location;
   }
}