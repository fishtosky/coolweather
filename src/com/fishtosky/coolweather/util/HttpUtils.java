package com.fishtosky.coolweather.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpConnection;

/*�����罻���Ĺ�����*/
public class HttpUtils {
	/* ��������������� */
	public static void sendHttpRequest(final String address,
			final HttpCallbackListener listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				HttpURLConnection connection = null;
				try {
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(5000);
					connection.setReadTimeout(5000);
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(in));
					String line;
					StringBuilder response=new StringBuilder();
					while((line=reader.readLine())!=null){
						response.append(line);
					}
					if(listener!=null){
						listener.onFinish(response.toString());
					}
				} catch (Exception e) {
					if(listener!=null){
						listener.onError(e);
					}
				}finally{
					if(connection!=null){
						connection.disconnect();
						connection=null;
					}
				}
			}
		}).start();
	}
}
