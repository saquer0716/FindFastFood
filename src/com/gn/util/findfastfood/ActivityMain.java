package com.gn.util.findfastfood;

import com.actionbarsherlock.app.SherlockActivity;
import com.gn.util.findfastfood.model.RestaurantDataManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.drm.DrmStore.Action;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class ActivityMain extends SherlockActivity {
	private static final int RESTAURANT_NUM = 5;
	private static final int RESTAURANT_KFC = 0;
	private static final int RESTAURANT_MCDONALD = 1;
	private static final int RESTAURANT_HUNGRYJACK = 2;
	private static final int RESTAURANT_SUBWAY = 3;
	
	private static final String[] RESTAURANT_SEARCH_NAME = new String[]{"kfc", "mcdonald", "\"hungry+jack\"", "\"subway\""}; 
	
	private GridView		restaurantGridView;
	private GridViewAdapter restaurantGridViewAdapter;
	
	private Dialog gpsDialog; //google play service install dialog
	
	private RestaurantDataManager restaurantDataManager = RestaurantDataManager.getInstance();
	
	private class GridViewAdapter extends BaseAdapter{
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return RESTAURANT_NUM;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			View myView = arg1;
			if (myView == null) {
				LayoutInflater inflater = getLayoutInflater();
				myView = inflater.inflate(R.layout.grid_item_restaurant, null);
				
				ImageView image = (ImageView)myView.findViewById(R.id.grid_item_restaurant_image);
				TextView title  = (TextView)myView.findViewById(R.id.grid_item_restaurant_title);
				
				switch (arg0) {
				case RESTAURANT_KFC:
					image.setImageResource(R.drawable.selector_grid_item_kfc);
					title.setText(getString(R.string.grid_item_title_kfc));
					break;
				case RESTAURANT_MCDONALD:
					image.setImageResource(R.drawable.grid_item_mcdonalds);
					title.setText(getString(R.string.grid_item_title_mcdonald));
					break;
				case RESTAURANT_HUNGRYJACK:
					image.setImageResource(R.drawable.grid_item_hungryjacks);
					title.setText(R.string.grid_item_title_hungryjack);
					break;
				case RESTAURANT_SUBWAY:
					image.setImageResource(R.drawable.grid_item_subway);
					title.setText(R.string.grid_item_title_subway);
					break;
				default:
					break;
				}
			}
			return myView;
		}		
		
	}
	
	private BroadcastReceiver installReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(ActivityMain.this) == ConnectionResult.SUCCESS){
				if ((gpsDialog != null) && (gpsDialog.isShowing())) {
					try {
			    		unregisterReceiver(installReceiver); 
					} catch (Exception e) {
						// TODO: handle exception
					}
					
					gpsDialog.dismiss();
				}
			}
		}	
	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        restaurantGridView = (GridView)findViewById(R.id.main_gridview_restaurant);
        restaurantGridViewAdapter = new GridViewAdapter();
        restaurantGridView.setAdapter(restaurantGridViewAdapter);
        restaurantGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ActivityMain.this, ActivitySearchResult.class);
				intent.putExtra("search_key_word", RESTAURANT_SEARCH_NAME[arg2]);
				startActivity(intent);
			}
		});
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	
    	int errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
    	if (errorCode != ConnectionResult.SUCCESS) {
    		gpsDialog = GooglePlayServicesUtil.getErrorDialog(errorCode, this, 716, new DialogInterface.OnCancelListener() {			
				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					onBackPressed(); 
				}
			});
    		
    		IntentFilter intentFilter = new IntentFilter();
    		intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
    		intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
    		intentFilter.addDataScheme("package");
    		registerReceiver(installReceiver, intentFilter);
    		
    		gpsDialog.show();
		}
    }
    
    @Override
    protected void onPause(){
    	super.onPause();
    }
    
    @Override
    protected void onStop(){
    	super.onStop();
    	try {
    		unregisterReceiver(installReceiver); 
		} catch (Exception e) {
			// TODO: handle exception
		}	
    }   
}
