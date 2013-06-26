package com.gn.util.findfastfood.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.content.Context;
import android.os.Bundle;

public class Utility {
	private static final int SET_CONNECTION_TIMEOUT = 20000;
    private static final int SET_SOCKET_TIMEOUT = 20000;
    
    private static final String APN_TABLE_URI = "content://telephony/carriers";
    private static final String PREFERRED_APN_URI = "content://telephony/carriers/preferapn";
    
    public static final String HTTPMETHOD_POST = "POST";
    public static final String HTTPMETHOD_GET = "GET";
    public static final String HTTPMETHOD_DELETE = "DELETE";
    
	public static Bundle constructAppListBundle(){
		Bundle bundle = null;
		return bundle;
	}
	
	public static Bundle constructFriendListListBundle(){
		Bundle bundle = null;
		return bundle;
	}
	
	public static Bundle constructUrlBundle(){
		Bundle bundle = null;
		return bundle;
	}
	
	public static String encodeUrl(List<NameValuePair> paras){
		if((paras == null) || (paras.size() == 0)){
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
		
		for (int loc = 0; loc < paras.size(); loc++) {
//            if (loc != 0){
//            	sb.append("&");
//            }
            
            sb.append("&");
            
            sb.append(URLEncoder.encode(paras.get(loc).getName()) + "="
                    + URLEncoder.encode(paras.get(loc).getValue()));
        }
        return sb.toString();
	}
	
	public static String encodeUrlUtf8(NetParameters paras){
		if((paras == null) || (paras.size() == 0)){
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
		for (int loc = 0; loc < paras.size(); loc++) {
//            if (loc != 0){
//            	sb.append("&");
//            }
            sb.append("&");
            
            try {
				sb.append(URLEncoder.encode(paras.getKey(loc), "UTF-8") + "="
				        + URLEncoder.encode(paras.getValue(loc), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return sb.toString();
	}
	
	/**
     * Construct a url encoded entity by parameters .
     * 
     * @param bundle
     *            :parameters key pairs
     * @return UrlEncodedFormEntity: encoed entity
     */
    public static List<NameValuePair> getPostEntityNamepairs(NetParameters bundle){
        if (bundle == null || bundle.getBundle().isEmpty()) {
            return null;
        }
        List<NameValuePair> form = new ArrayList<NameValuePair>();
        
        for(int loc = 0; loc < bundle.size(); loc++){
        	form.add(new BasicNameValuePair(bundle.getKey(loc), bundle.getValue(loc)));
        }
		return form;
    }
    
    public static JSONObject getPostJSON(List<NameValuePair> appFireParams, List<NameValuePair> appStarParams){
    	if (appFireParams == null || appFireParams.isEmpty()) {
            return null;
        }
    	
    	if (appStarParams == null || appStarParams.isEmpty()) {
            return null;
        }
    	
        JSONObject jsonObject = new JSONObject();
        
        try {
        	//device information
        	JSONObject jsonObjectDeviceinfo = new JSONObject();
        	for(int i = 0; i < appFireParams.size(); i++){
        		jsonObjectDeviceinfo.put(appFireParams.get(i).getName(), appFireParams.get(i).getValue());
        	}
        	jsonObject.put("di", jsonObjectDeviceinfo);
        	
        	//user information
        	JSONObject jsonObjectUserinfo = new JSONObject();
        	jsonObjectUserinfo.put(appStarParams.get(0).getName(), appStarParams.get(0).getValue());
        	//jsonObjectUserinfo.put(appStarParams.get(1).getName(), appStarParams.get(1).getValue());
        	jsonObject.put("ui", jsonObjectUserinfo);
        	
        	//app list
        	JSONObject jsonObjectAppList = new JSONObject();
        	for(int loc = 1; loc < appStarParams.size(); loc++){
        		JSONObject jsonObjectAppInfo = new JSONObject();
        		JSONArray  jsonArrayAds = new JSONArray();
        		jsonArrayAds.put("AdMob");
        		jsonArrayAds.put("Leadbolt");
        		
        		//ad network
        		jsonObjectAppInfo.put("an", jsonArrayAds);
        		
        		jsonObjectAppList.put(appStarParams.get(loc).getValue(), jsonObjectAppInfo);
        	}
        	jsonObject.put("al", jsonObjectAppList);
		} catch (JSONException e) {
			e.printStackTrace();
		}
        
        return jsonObject;
    }
	
	public static String openUrl(Context context, String url, String httpMethod) throws NetException{
		String result = "";
		
		try {
			HttpClient client = getHttpClient(context);
			HttpUriRequest request = null;
			
			if(httpMethod.equals("GET")){
                HttpGet get = new HttpGet(url);
                request = get;
			}else if(httpMethod.equals("POST")){
				HttpPost post = new HttpPost(url);				
				request = post;
			}else if (httpMethod.equals("DELETE")) {
				HttpDelete delete = new HttpDelete();
                request = delete;
            }
			
			HttpResponse response = client.execute(request);
			result = read(response);
			if(response.getStatusLine().getStatusCode() != 200){
                String err = null;
                int errCode = 0;
				try {
					JSONObject json = new JSONObject(result);
					err = json.getString("error");
					errCode = json.getInt("error_code");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				throw new NetException(String.format(err), errCode);
			}
			
			return result;
		} catch (Exception e) {
			throw new NetException(e);
		}
	}
	
	private static HttpClient getHttpClient(Context ctx){
		BasicHttpParams httpParameters = new BasicHttpParams();
        // Set the default socket timeout (SO_TIMEOUT) // in
        // milliseconds which is the timeout for waiting for data.
        HttpConnectionParams.setConnectionTimeout(httpParameters, Utility.SET_CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParameters, Utility.SET_SOCKET_TIMEOUT);
        HttpProtocolParams.setVersion(httpParameters, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(httpParameters, HTTP.UTF_8);
        
        HttpClient client = new DefaultHttpClient(httpParameters);
        
        return client;
	}
	
	private static String read(HttpResponse response) throws NetException{
		String result = "";
		HttpEntity entity = response.getEntity();
		InputStream instream;
		
		try {
			instream = entity.getContent();
			
			ByteArrayOutputStream content = new ByteArrayOutputStream();
			int readBytes = 0;
            byte[] sBuffer = new byte[512];
            while ((readBytes = instream.read(sBuffer)) != -1) {
                content.write(sBuffer, 0, readBytes);
            }
            result = new String(content.toByteArray());
			return result;
		} catch (IllegalStateException e) {
			throw new NetException(e);
		} catch (IOException e) {
			throw new NetException(e);
		}
	}
}
