package com.fishtosky.coolweather.util;

import com.fishtosky.coolweather.model.City;
import com.fishtosky.coolweather.model.County;
import com.fishtosky.coolweather.model.Province;

import android.text.TextUtils;

/*�����ӷ������������ݵĹ�����*/
public class Utility {
	/*
	 * �����ʹ���ӷ��������ص�ʡ������ ���ݸ�ʽΪ01|������02|�Ϻ���......
	 */
	public static boolean handleProvinceResponse(CoolWeatherDB coolWeatherDB,
			String response) {
		if (!TextUtils.isEmpty(response)) {
			String[] allProvinces = response.split(",");
			if (allProvinces != null && allProvinces.length > 0) {
				for (String p : allProvinces) {
					// "|"��ʾ��λ�򣬾����������壬Ҫʹ����������ַ��������ת�壬��ʹ��ת��
					// �ַ���\��������\���������һ�������ַ������Ի���Ҫ�������ת�塰\\���൱��
					// "\","\\|"���൱��"\|"
					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					// ���������������ݴ洢�����ݿ�
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}

	/*
	 * �����ʹ���ӷ��������ص��м����� ���ݸ�ʽΪ01|ʯ��ׯ��02|���ݣ�......
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

	/* �����ʹ���ӷ��������ص��ؼ����� */
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
