/*
 * Copyright 2011 Sina.
 *
 * Licensed under the Apache License and Weibo License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.open.weibo.com
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gn.util.findfastfood.net;

import java.io.IOException;
import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Context;

public class AsyncNetApiRunner {
	
	private NetApi mAppStarApi;
	
	public AsyncNetApiRunner(NetApi api){
		this.mAppStarApi = api;
	}
	
	public void request(final Context context, final String url, final List<NameValuePair> params, 
			final String httpMethod, final RequestListener listener){
		new Thread(){
			@Override 
			public void run() {
                try {
					String resp = mAppStarApi.syncUpdate(context, url, params);
                    listener.onComplete(resp);
                } catch (NetException e) {
                    listener.onError(e);
                }
            }
		}.start();
	}
	
	
    public static interface RequestListener {

        public void onComplete(String response);

        public void onIOException(IOException e);

        public void onError(NetException e);

    }
	
}
