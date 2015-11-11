package com.fishtosky.coolweather.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.fishtosky.coolweather.model.City;
import com.fishtosky.coolweather.model.County;
import com.fishtosky.coolweather.model.Province;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

/*解析从服务器返回数据的工具类*/
public class Utility {
	/*
	 * 解析和处理从服务器返回的省级数据 数据格式为01|北京，02|上海，......
	 */
	public static boolean handleProvinceResponse(CoolWeatherDB coolWeatherDB,
			String response) {
		if (!TextUtils.isEmpty(response)) {
			String[] allProvinces = response.split(",");
			if (allProvinces != null && allProvinces.length > 0) {
				for (String p : allProvinces) {
					// "|"表示按位或，具有特殊意义，要使用这个特殊字符必须进行转义，需使用转义
					// 字符“\”，而“\”本身就是一个特殊字符，所以还需要对其进行转义“\\”相当于
					// "\","\\|"就相当于"\|"
					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					// 将解析出来的数据存储到数据库
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}

	/*
	 * 解析和处理从服务器返回的市级数据 数据格式为01|石家庄，02|沧州，......
	 */
	public static boolean handleCityResponse(CoolWeatherDB coolWeatherDB,
			String response, int provinceId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allcities = response.split(",");
			if (allcities != null && allcities.length > 0) {
				for (String c : allcities) {
					String[] array = c.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					coolWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}

	/* 解析和处理从服务器返回的县级数据 */
	public static boolean handleCountyResponse(CoolWeatherDB coolWeatherDB,
			String response, int cityId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCounties = response.split(",");
			if (allCounties != null && allCounties.length > 0) {
				for (String c : allCounties) {
					String[] array = c.split("\\|");
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					coolWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}

	/* 解析服务器返回的JSON数据，并将解析的结果存储到本地的sharepreference文件中 */
	public static void handleWeatherResponse(Context context, String response) {
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
			String cityName = weatherInfo.getString("city");
			String weatherCode = weatherInfo.getString("cityid");
			String temp1 = weatherInfo.getString("temp1");
			String temp2 = weatherInfo.getString("temp2");
			String weatherDesp = weatherInfo.getString("weather");
			String publishTime = weatherInfo.getString("ptime");
			saveWeatherInfo(context, cityName, weatherCode, temp1, temp2,
					weatherDesp, publishTime);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private static void saveWeatherInfo(Context context, String cityName,
			String weatherCode, String temp1, String temp2, String weatherDesp,
			String publishTime) {
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
		//放置一个标志位
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_desc", weatherDesp);
		editor.putString("publish_time", publishTime);
		editor.putString("current_date", sdf.format(new Date()));
		editor.commit();
	}
}
