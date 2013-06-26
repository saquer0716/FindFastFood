package com.gn.util.findfastfood;

import java.util.ArrayList;

import com.actionbarsherlock.app.SherlockActivity;
import com.gn.util.findfastfood.model.Coordinate;
import com.gn.util.findfastfood.model.Restaurant;
import com.gn.util.findfastfood.model.RestaurantDataManager;
import com.gn.util.findfastfood.model.RestaurantDataManager.STATUS;
import com.gn.util.findfastfood.model.RestaurantDataManager.SearchListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import android.location.Location;
import android.os.Bundle;

public class ActivitySearchResult extends SherlockActivity implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener{
	
	private String searchRestaurantName;
	
	private LocationClient mLocationClient;
	private Location currentLocation;
	
	
	
	private RestaurantDataManager restaurantDataManager = RestaurantDataManager.getInstance();
	
	private static final LocationRequest REQUEST = LocationRequest.create()
//		      .setInterval(5000)         // 5 seconds
//		      .setFastestInterval(16)    // 16ms = 60fps
			  .setNumUpdates(1)
		      .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        
        searchRestaurantName = getIntent().getStringExtra("search_key_word");
        restaurantDataManager.setListenter(this, new SearchListener() {
			
			@Override
			public void onComplete(Exception e, STATUS status,
					ArrayList<Restaurant> restaurants) {
				
			}
		});
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	if (mLocationClient == null) {
    		mLocationClient = new LocationClient(
    				getApplicationContext(),
    				this,  // ConnectionCallbacks
    				this); // OnConnectionFailedListener
        }
    	
    	if (currentLocation == null) {
    		mLocationClient.connect();
		}   	
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	if (mLocationClient != null) {
    		mLocationClient.disconnect();
    	}
    }

	@Override
	public void onConnected(Bundle arg0) {
		mLocationClient.requestLocationUpdates(
		        REQUEST,
		        this);  // LocationListener
	}

	@Override
	public void onDisconnected() {
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
	}    
	
	@Override
	public void onLocationChanged(Location arg0) {
		currentLocation = arg0;

		restaurantDataManager.searchRestaurant(searchRestaurantName, new Coordinate(currentLocation.getLatitude(), currentLocation.getLongitude()));
	}
}
