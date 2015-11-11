package com.fishtosky.coolweather.service;

import com.fishtosky.coolweather.receiver.AutoUpdateReceiver;
import com.fishtosky.coolweather.util.HttpCallbackListener;
import com.fishtosky.coolweather.util.HttpUtils;
import com.fishtosky.coolweather.util.Utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.TextUtils;

/*�ں�̨һֱ���еķ����Զ���������*/
public class AutoUpdateService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				updateWeather();
			}
		}).start();
		// ��ȡ��ʱ��������
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		int interval = 8 * 60 * 60 * 1000;// 8Сʱ����һ��
		// ����ʱ��
		long triggerAtTime = SystemClock.elapsedRealtime() + interval;
		Intent i = new Intent(this, AutoUpdateReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
		// ���ö�ʱ��
		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime,
				pi);
		return super.onStartCommand(intent, flags, startId);
	}

	protected void updateWeather() {
		SharedPreferences preference = PreferenceManager
				.getDefaultSharedPreferences(this);
		String weatherCode = preference.getString("weather_code", "");
		if (!TextUtils.isEmpty(weatherCode)) {
			String address = "http://www.weather.com.cn/data/cityinfo/"
					+ weatherCode + ".html";
			HttpUtils.sendHttpRequest(address, new HttpCallbackListener() {
				@Override
				public void onFinish(String response) {
					//�����µ�������Ϣ���浽SharePreference���� 
					Utility.handleWeatherResponse(AutoUpdateService.this, response);
				}
				
				@Override
				public void onError(Exception e) {
					
				}
			});
		}
	}

}
