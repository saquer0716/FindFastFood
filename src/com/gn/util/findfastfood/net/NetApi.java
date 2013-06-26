package com.gn.util.findfastfood.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.gn.util.findfastfood.net.AsyncNetApiRunner.RequestListener;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

public class NetApi {
	private final static String SDK_VERSION = "1";
	private final static String SDK_LEVEL = "01";
	
	public static final String SERVER = "http://www.androidaplication.com/ias/put?sectionid=83"; 
	
	private static NetApi mAppStarApiInstance = null;
	
	private NetApi(){
		
	}
	
	public static synchronized NetApi getInstance(){
		if(mAppStarApiInstance == null){
			mAppStarApiInstance = new NetApi();
		}
		return mAppStarApiInstance;
	}
	
	/**
     * Requst AppStar api by get or post
     * 
     * @param url
     *            Openapi request URL.
     * @param params
     *            http get or post parameters . e.g.
     *            gettimeling?max=max_id&min=min_id max and max_id is a pair of
     *            key and value for params, also the min and min_id
     * @param httpMethod
     *            http verb: e.g. "GET", "POST", "DELETE"
     * @throws IOException
     * @throws MalformedURLException
     * @throws AppStarApiException
     */
	private String request(Context context, String url, List<NameValuePair> appStarParams, String httpMethod) throws NetException{
		
		String rlt = Utility.openUrl(context, url, httpMethod);
        return rlt;
    }
    
    //we have two kinds of AppStarApi request, one is sync and the other one is async.
    //app usage status should be updated in async and log in, revise app, etc. should be 
    //sync.
    public String syncUpdate(Context context, String url, List<NameValuePair> params) throws NetException{
    	String result = "";
    	
    	result = request(context, url, params, Utility.HTTPMETHOD_GET);
    	return result;
    } 
    
    public String syncUpload(Context context, String url, List<NameValuePair> params) throws NetException{
    	String result = "";
    	
    	result = request(context, url, params, Utility.HTTPMETHOD_POST);
    	return result;
    } 
    
    public void asyncUpdate(Context context, String url, List<NameValuePair> params, RequestListener listener){
    	AsyncNetApiRunner runner = new AsyncNetApiRunner(mAppStarApiInstance);
    	runner.request(context, url, params, Utility.HTTPMETHOD_GET, listener);
    } 
    
    public void asyncUpload(Context context, String url, List<NameValuePair> params, RequestListener listener){
    	AsyncNetApiRunner runner = new AsyncNetApiRunner(mAppStarApiInstance);
    	runner.request(context, url, params, Utility.HTTPMETHOD_POST, listener);
    } 
}
