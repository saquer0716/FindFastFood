package com.gn.util.findfastfood.model;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.util.Log;

import com.gn.util.findfastfood.net.AsyncNetApiRunner;
import com.gn.util.findfastfood.net.NetApi;
import com.gn.util.findfastfood.net.NetException;
import com.gn.util.findfastfood.util.AppLog;


public class RestaurantDataManager {
	private static final String GOOGLE_PLACES_SEARCH_KEY = "AIzaSyD-htI5nCdJss2wynjT3hA0jAkmf9iM-oo";
	private static final String GOOGLE_PLACES_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
	private static final String GOOGLE_DISTANCE_CAL_URL = "https://maps.googleapis.com/maps/api/distancematrix/json?";
	private static final String GOOGLE_DIRECTION_URL = "https://maps.googleapis.com/maps/api/directions/json?";
	
	public enum STATUS{NOT_CARE(-1), OK(0), ZERO_RESULT(1), EXCEED_LIMIT(2), NOT_KNOWN(3);
		private int value;    		
		private STATUS(int value) {this.value = value;}
		public int getValue() {return value;}};
	
	private enum SEARCH_TYPE{SEARCH_NONE(0), SEARCH_PLACES(1), SEARCH_DISTANCES(2), SEARCH_DIRECTIONS(3);
		private int value;    
		private SEARCH_TYPE(int value) {this.value = value;}
		public int getValue() {return value;}};
	
	private enum TRAVEL_TYPE{TRAVEL_NONE(-1), TRAVEL_DRIVING(0), TRAVEL_WALKING(1);
		private int value;    
		private TRAVEL_TYPE(int value) {this.value = value;}
		public int getValue() {return value;}};

	private Context context;
	private Coordinate currentCoordinate;
	private static RestaurantDataManager mRestaurantDataManager = null;
	
	private ArrayList<Restaurant> restaurantList= new ArrayList<Restaurant>();
	
	private SEARCH_TYPE searchType = SEARCH_TYPE.SEARCH_NONE;
	private TRAVEL_TYPE travelType = TRAVEL_TYPE.TRAVEL_NONE;
	
	private NetApi netApi = NetApi.getInstance();
	private SearchListener searchlistener;
	
	private AsyncNetApiRunner.RequestListener listener = new AsyncNetApiRunner.RequestListener() {	
		@Override
		public void onIOException(IOException e) {
			searchlistener.onComplete(e, STATUS.NOT_CARE, null);
		}
		
		@Override
		public void onError(NetException e) {
			searchlistener.onComplete(e, STATUS.NOT_CARE, null);
		}
		
		@Override
		public void onComplete(String response) {
			AppLog.d("FindFastFood", "search finished, search type: " + searchType);
			try {
				JSONObject json = new JSONObject(response);
				String status = json.getString("status");
				
				if (status.equals("OK")) {
					if (searchType == SEARCH_TYPE.SEARCH_PLACES) {
						parsePlacesSearchResult(json);
						searchDistance();	
					}else if (searchType == SEARCH_TYPE.SEARCH_DISTANCES) {
						parseDistanceSearchResult(json);
						
						if (travelType == TRAVEL_TYPE.TRAVEL_DRIVING) {
							travelType = TRAVEL_TYPE.TRAVEL_WALKING;
							searchDistance();
						} else {
							searchlistener.onComplete(null, STATUS.OK, restaurantList);
						}
					}					
				} else {
					AppLog.d("FindFastFood", "search result not OK, search type: " + searchType);
					STATUS statusCode;
					if (status.equals("ZERO_RESULTS")) {
						statusCode = STATUS.ZERO_RESULT;
					}else if (status.equals("OVER_QUERY_LIMIT")) {
						statusCode = STATUS.EXCEED_LIMIT;
					}else {
						statusCode = STATUS.NOT_KNOWN;
					}
					
					searchlistener.onComplete(null, statusCode, null);
				}			
			} catch (Exception e) {
				searchlistener.onComplete(e, STATUS.NOT_CARE, null);
			}
		}
	};
	
	private RestaurantDataManager(){
		
	}
	
	public static synchronized RestaurantDataManager getInstance(){
		if(mRestaurantDataManager == null){
			mRestaurantDataManager = new RestaurantDataManager();
		}
		return mRestaurantDataManager;
	}
	
	
	public void setListenter(Context ctx, SearchListener l){
		context = ctx;		
		searchlistener = l;
	}	
	
	
	public void searchRestaurant(String place, Coordinate coordinate){	
		currentCoordinate = coordinate;
		
		searchType = SEARCH_TYPE.SEARCH_PLACES;
		
		String searchUrl = setUpPlaceSearchUrl(place);
		netApi.asyncUpdate(context, searchUrl, null, listener);
	}
	
	private void searchDistance(){
		searchType = SEARCH_TYPE.SEARCH_DISTANCES;
		
		if (travelType == TRAVEL_TYPE.TRAVEL_NONE) {
			travelType = TRAVEL_TYPE.TRAVEL_DRIVING;
		} else {
			travelType = TRAVEL_TYPE.TRAVEL_WALKING;
		}
		
		String searchUrl = setUpDistanceSearchUrl();
		netApi.asyncUpdate(context, searchUrl, null, listener);
	}
	
	public void searchDirection(){
		searchType = SEARCH_TYPE.SEARCH_DIRECTIONS;
		String searchUrl = setUpDirectionSearchUrl();
		netApi.asyncUpdate(context, searchUrl, null, listener);
	}
	
	private String setUpPlaceSearchUrl(String place){
		String url = GOOGLE_PLACES_SEARCH_URL;
	    
	    url += "key=" + GOOGLE_PLACES_SEARCH_KEY + "&";
	    url += "name=" + place + "&";
	    url += "location=" + currentCoordinate.latitude + "," + currentCoordinate.longitude + "&";
	    url += "radius=5000" + "&";
	    url += "type=restauran%7Cfood" + "&";
	    url += "sensor=false";
		
		return url;
	}
	
	private String setUpDistanceSearchUrl(){
		String url = GOOGLE_DISTANCE_CAL_URL;
		
		url += "origins=" + currentCoordinate.latitude + "," + currentCoordinate.longitude + "&";
		url += "destinations=";
		
		if (restaurantList.size() > 0) {
			url += restaurantList.get(0).coordinate.latitude + "," + restaurantList.get(0).coordinate.longitude;
			
			for (int i = 1; i < restaurantList.size(); i++) {
				Restaurant restaurant = restaurantList.get(i);
				
				url += "%7C" + restaurant.coordinate.latitude + "," + restaurant.coordinate.longitude;
			}
			
			url += "&";
		}
		
		if (travelType == TRAVEL_TYPE.TRAVEL_DRIVING) {
			url += "mode=driving" + "&";
		} else {
			url += "mode=walking" + "&";
		}
		
		url += "sensor=false";
		
		return url;
	}
	
	private String setUpDirectionSearchUrl(){
		return "";
	}
	
	private void parsePlacesSearchResult(JSONObject json) throws JSONException{
		JSONArray foundRestaurants= json.getJSONArray("results");
		restaurantList.clear();
		
		for (int i = 0; i < foundRestaurants.length(); i++) {
			JSONObject restaurant = foundRestaurants.getJSONObject(i);
			Restaurant foundRestaurant = new Restaurant();
			
			foundRestaurant.vicinity = restaurant.getString("vicinity");
			foundRestaurant.name = restaurant.getString("name");
			JSONObject coordinate = restaurant.getJSONObject("geometry").getJSONObject("location");
			foundRestaurant.coordinate = new Coordinate(coordinate.getDouble("lat"),coordinate.getDouble("lng"));
			
			restaurantList.add(foundRestaurant);
		}	
	}
	
	private void parseDistanceSearchResult(JSONObject json) throws JSONException{
		JSONArray rows = json.getJSONArray("rows");
		
		if (rows.length() > 0) {
			JSONObject row = rows.getJSONObject(0);
			
			JSONArray elements = row.getJSONArray("elements");
			
			for (int i = 0; i < elements.length(); i++) {
				JSONObject element = elements.getJSONObject(i);
				
				String status = element.getString("status");
				if (status.equals("OK")) {
					JSONObject distance = element.getJSONObject("distance");
					
					String text = distance.getString("text");
					double value = distance.getDouble("value");
					
					Restaurant restaurant = restaurantList.get(i);
					if (travelType == TRAVEL_TYPE.TRAVEL_DRIVING) {
						restaurant.distanceDrivingText = text;
						restaurant.distanceDrivingValue = value;
					} else {
						restaurant.distanceWalkingText = text;
						restaurant.distanceWalkingValue = value;
					}
				}
			}
		}

	}
	
	
	public Restaurant getRestaurant(int index){
		return restaurantList.get(index);
	}
	
	public ArrayList<Restaurant> getRestaurantList(){
		return restaurantList;
	}
	
	public static interface SearchListener {

        public void onComplete(Exception e, STATUS status, ArrayList<Restaurant> restaurants);
    }
}
