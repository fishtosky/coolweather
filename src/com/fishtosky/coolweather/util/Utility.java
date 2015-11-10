package com.fishtosky.coolweather.util;

import com.fishtosky.coolweather.model.City;
import com.fishtosky.coolweather.model.County;
import com.fishtosky.coolweather.model.Province;

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
					String[] array=c.split("\\|");
					County county=new County();
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
}
