package com.fishtosky.coolweather.activity;

import java.util.ArrayList;
import java.util.List;

import com.fishtosky.coolweather.R;
import com.fishtosky.coolweather.model.City;
import com.fishtosky.coolweather.model.County;
import com.fishtosky.coolweather.model.Province;
import com.fishtosky.coolweather.util.CoolWeatherDB;
import com.fishtosky.coolweather.util.HttpCallbackListener;
import com.fishtosky.coolweather.util.HttpUtils;
import com.fishtosky.coolweather.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {
	private static final int LEVIE_PROVINCE = 0;
	private static final int LEVIE_CITY = 1;
	private static final int LEVIE_COUNTY = 2;

	private ProgressDialog progressDialog;
	private TextView titleView;
	private ListView list;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolWeatherDB;
	private List<String> dataList = new ArrayList<String>();

	private List<Province> provinceList;
	private List<City> cityList;
	private List<County> countyList;

	private Province selectedProvince;
	private City selectedCity;

	private int currentLevel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.chose_area);
		titleView = (TextView) findViewById(R.id.title_text);
		list = (ListView) findViewById(R.id.list_view);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, dataList);
		list.setAdapter(adapter);
		coolWeatherDB = CoolWeatherDB.getInstance(this);

		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (currentLevel == LEVIE_PROVINCE) {
					selectedProvince = provinceList.get(position);
					queryCities();
				} else if (currentLevel == LEVIE_CITY) {
					selectedCity = cityList.get(position);
					queryCounties();
				}
			}
		});
		queryProvinces();
	}

	/* 查询全国所有的省份，优先从数据库中查询，如果找不到则再从服务器中查询 */
	private void queryProvinces() {
		provinceList = coolWeatherDB.loadProvinces();
		// 将省份的数据添加至dataList并更新listView
		if (provinceList.size() > 0) {
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			list.setSelection(0);
			titleView.setText("中国");
			currentLevel = LEVIE_PROVINCE;
		} else {
			queryFromService(null, "province");
		}
	}

	/* 查询省内所有的市，优先从数据库中查找，找不到则去服务器查找 */
	protected void queryCities() {
		cityList = coolWeatherDB.loadCities(selectedProvince.getId());
		if (cityList.size() > 0) {
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			list.setSelection(0);
			titleView.setText(selectedProvince.getProvinceName());
			currentLevel = LEVIE_CITY;
		} else {
			queryFromService(selectedProvince.getProvinceCode(), "city");
		}
	}

	protected void queryCounties() {
		countyList = coolWeatherDB.loadCounties(selectedCity.getId());
		if (countyList.size() > 0) {
			dataList.clear();
			for (County county : countyList) {
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			list.setSelection(0);
			titleView.setText(selectedCity.getCityName());
			currentLevel = LEVIE_COUNTY;
		} else {
			queryFromService(selectedCity.getCityCode(), "county");
		}
	}

	private void queryFromService(String code, final String type) {
		String address;
		if (!TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city" + code
					+ ".xml";
		} else {
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtils.sendHttpRequest(address, new HttpCallbackListener() {
			@Override
			public void onFinish(String response) {
				boolean result = false;
				if ("province".equals(type)) {
					result = Utility.handleProvinceResponse(coolWeatherDB,
							response);
				} else if ("city".equals(type)) {
					result = Utility.handleCityResponse(coolWeatherDB,
							response, selectedProvince.getId());
				} else if ("county".equals(type)) {
					result = Utility.handleCountyResponse(coolWeatherDB,
							response, selectedCity.getId());
				}
				System.out.println(result);
				
				//如果查询到数据则到主线程更新UI
				if(result){
					runOnUiThread(new Runnable() {
						public void run() {
							closeProgressDialog();
							if("province".equals(type)){
								queryProvinces();
							}else if("city".equals(type)){
								queryCities();
							}else if("county".equals(type)){
								queryCounties();
							}
						}
					});
				}
			}

			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败！", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	private void showProgressDialog() {
		if(progressDialog==null){
			progressDialog=new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	private void closeProgressDialog(){
		if(progressDialog!=null){
			progressDialog.dismiss();
		}
	}
	
	//重写Back键
	@Override
	public void onBackPressed() {
		if(currentLevel==LEVIE_COUNTY){
			queryCities();
		}else if(currentLevel==LEVIE_CITY){
			queryProvinces();
		}else{
			finish();
		}
	}

}
