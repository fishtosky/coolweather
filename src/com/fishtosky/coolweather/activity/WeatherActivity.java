package com.fishtosky.coolweather.activity;

import com.fishtosky.coolweather.R;
import com.fishtosky.coolweather.util.HttpCallbackListener;
import com.fishtosky.coolweather.util.HttpUtils;
import com.fishtosky.coolweather.util.Utility;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity {
	private LinearLayout weatherInfoLayout;
	private TextView cityName;
	private TextView publishTime;
	private TextView weatherDesc;
	private TextView temp1;
	private TextView temp2;
	private TextView currentDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityName = (TextView) findViewById(R.id.city_name);
		publishTime = (TextView) findViewById(R.id.publish_time);
		weatherDesc = (TextView) findViewById(R.id.weather_descri);
		temp1 = (TextView) findViewById(R.id.temp1);
		temp2 = (TextView) findViewById(R.id.temp2);
		currentDate = (TextView) findViewById(R.id.current_date);
		String countyCode = getIntent().getStringExtra("county_code");
		if (!TextUtils.isEmpty(countyCode)) {
			// 如果从启动该activity的intent中返回了县级代码，则根据县级代码查询天气
			publishTime.setText("同步中...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityName.setVisibility(View.INVISIBLE);
			// 根据县级代码查询天气代码
			queryWeatherCode(countyCode);
		} else {
			// 如果没有返回县级代码，则直接显示已经设定好的本地天气
			showWeather();
		}
	}

	private void queryWeatherCode(String countyCode) {
		String address = "http://www.weather.com.cn/data/list3/city"
				+ countyCode + ".xml";
		queryFromService(address, "query_weather_code");
	}

	private void queryWeatherInfo(String weatherCode) {
		String address = "http://www.weather.com.cn/data/cityinfo/"
				+ weatherCode + ".html";
		queryFromService(address, "query_weather_info");
	};

	private void queryFromService(String address, final String type) {
		HttpUtils.sendHttpRequest(address, new HttpCallbackListener() {
			@Override
			public void onFinish(String response) {
				if ("query_weather_code".equals(type)) {
					if (!TextUtils.isEmpty(response)) {
						// 返回的数据格式为县级代码|天气代码
						String[] array = response.split("\\|");
						String weatherCode = array[1];
						queryWeatherInfo(weatherCode);
					}
				} else if ("query_weather_info".equals(type)) {
					if (!TextUtils.isEmpty(response)) {
						// 返回的数据格式为Json数据
						Utility.handleWeatherResponse(WeatherActivity.this,
								response);
						// 更新界面
						runOnUiThread(new Runnable() {
							public void run() {
								showWeather();
							}
						});
					}
				}
			}

			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					public void run() {
						publishTime.setText("同步失败！");
					}
				});
			}
		});
	}

	/* 从sharepreference中读取数据，并显示在界面上 */
	private void showWeather() {
		SharedPreferences preference = PreferenceManager
				.getDefaultSharedPreferences(this);
		cityName.setText(preference.getString("city_name", ""));
		publishTime.setText("今天"+preference.getString("publish_time", "")+"发布");
		currentDate.setText(preference.getString("current_date", ""));
		weatherDesc.setText(preference.getString("weather_desc", ""));
		temp1.setText(preference.getString("temp1", ""));
		temp2.setText(preference.getString("temp2", ""));
		cityName.setVisibility(View.VISIBLE);
		weatherInfoLayout.setVisibility(View.VISIBLE);
	}
}
