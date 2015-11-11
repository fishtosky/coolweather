package com.fishtosky.coolweather.activity;

import com.fishtosky.coolweather.R;
import com.fishtosky.coolweather.service.AutoUpdateService;
import com.fishtosky.coolweather.util.HttpCallbackListener;
import com.fishtosky.coolweather.util.HttpUtils;
import com.fishtosky.coolweather.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity implements OnClickListener {
	private LinearLayout weatherInfoLayout;
	private TextView cityName;
	private TextView publishTime;
	private TextView weatherDesc;
	private TextView temp1;
	private TextView temp2;
	private TextView currentDate;
	private Button switchCity;
	private Button refreshWeather;

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
		switchCity = (Button) findViewById(R.id.switch_city);
		refreshWeather = (Button) findViewById(R.id.refresh_weather);
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);

		String countyCode = getIntent().getStringExtra("county_code");
		if (!TextUtils.isEmpty(countyCode)) {
			// �����������activity��intent�з������ؼ����룬������ؼ������ѯ����
			publishTime.setText("ͬ����...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityName.setVisibility(View.INVISIBLE);
			// �����ؼ������ѯ��������
			queryWeatherCode(countyCode);
		} else {
			// ���û�з����ؼ����룬��ֱ����ʾ�Ѿ��趨�õı�������
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
						// ���ص����ݸ�ʽΪ�ؼ�����|��������
						String[] array = response.split("\\|");
						String weatherCode = array[1];
						queryWeatherInfo(weatherCode);
					}
				} else if ("query_weather_info".equals(type)) {
					if (!TextUtils.isEmpty(response)) {
						// ���ص����ݸ�ʽΪJson����
						Utility.handleWeatherResponse(WeatherActivity.this,
								response);
						// ���½���
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
						publishTime.setText("ͬ��ʧ�ܣ�");
					}
				});
			}
		});
	}

	/* ��sharepreference�ж�ȡ���ݣ�����ʾ�ڽ����� */
	private void showWeather() {
		SharedPreferences preference = PreferenceManager
				.getDefaultSharedPreferences(this);
		cityName.setText(preference.getString("city_name", ""));
		publishTime.setText("����" + preference.getString("publish_time", "")
				+ "����");
		currentDate.setText(preference.getString("current_date", ""));
		weatherDesc.setText(preference.getString("weather_desc", ""));
		temp1.setText(preference.getString("temp1", ""));
		temp2.setText(preference.getString("temp2", ""));
		cityName.setVisibility(View.VISIBLE);
		weatherInfoLayout.setVisibility(View.VISIBLE);
		
		//��ʾ������Ϣ��ͬʱ�������
		Intent intent=new Intent(this, AutoUpdateService.class);
		startService(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.switch_city:
			Intent intent = new Intent(this, ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
			publishTime.setText("ͬ����...");
			SharedPreferences preference = PreferenceManager
					.getDefaultSharedPreferences(this);
			//��ȡ����������
			String weatherCode=preference.getString("weather_code", "");
			if(!TextUtils.isEmpty(weatherCode)){
				queryWeatherInfo(weatherCode);
			}
			break;
		}
	}
}
