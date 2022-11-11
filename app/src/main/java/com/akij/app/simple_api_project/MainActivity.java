package com.akij.app.simple_api_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.akij.app.simple_api_project.adapter.HomeForecastTemperatureAdapter;
import com.akij.app.simple_api_project.adapter.HomeHourlyAdapter;
import com.akij.app.simple_api_project.model.HomeHourlyModel;
import com.akij.app.simple_api_project.model.HomeTemperatureModel;
import com.akij.app.simple_api_project.model.WeatherModel;
import com.akij.app.simple_api_project.network.APIClient;
import com.akij.app.simple_api_project.network.APIInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    TextView txtTemperature, txtCity, txtCloudCover, txtHumidity, txtPrecipitation, txtWind, txtSoilTemperature;
    APIInterface apiInterface;
    RecyclerView recyclerView, hourlyRecyclerView;
    Double precipitation, surfacePressure, evaporation, soilTemperature, temperature, wind, visibility, soilMoisture, raining, snowfall;
    int cloudCover, humidity, windDirection;
    String cityName, sunRise, sunSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Weather App");
        findAllView();
        apiInterface = APIClient.getClient().create(APIInterface.class);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        layoutManager.scrollToPosition(0);
        layoutManager1.scrollToPosition(0);
        recyclerView.setLayoutManager(layoutManager);
        hourlyRecyclerView.setLayoutManager(layoutManager1);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        snapHelper.attachToRecyclerView(hourlyRecyclerView);
        List<HomeTemperatureModel> list= new ArrayList<>();
        List<HomeHourlyModel> homeHourlyModelList= new ArrayList<>();

        Call<WeatherModel> call= apiInterface.getAllWeatherData();
        call.enqueue(new Callback<WeatherModel>() {
            @Override
            public void onResponse(Call<WeatherModel> call, Response<WeatherModel> response) {
                if(!response.isSuccessful()){
                    Log.d("status_code: ", String.valueOf(response.code()));
                    Toast.makeText(MainActivity.this, "Called filed", Toast.LENGTH_SHORT).show();
                    return;
                }
                WeatherModel weatherModel= response.body();
                cityName= weatherModel.getTimezone();
                temperature= weatherModel.getHourly().getSoilTemperature0cm().get(0);
                cloudCover= weatherModel.getHourly().getCloudcoverLow().get(0);
                humidity= weatherModel.getHourly().getRelativehumidity2m().get(0);
                precipitation= weatherModel.getHourly().getPrecipitation().get(0);
                wind= weatherModel.getHourly().getWindspeed10m().get(0);
                soilTemperature= weatherModel.getHourly().getSoilTemperature0cm().get(0);

                sunRise= weatherModel.getDaily().getSunrise().get(0);
                sunSet= weatherModel.getDaily().getSunset().get(0);
                windDirection= weatherModel.getHourly().getWinddirection10m().get(0);
                visibility= weatherModel.getHourly().getVisibility().get(0);
                soilMoisture= weatherModel.getHourly().getSoilMoisture01cm().get(0);
                raining= weatherModel.getHourly().getRain().get(0);
                snowfall= weatherModel.getHourly().getSnowfall().get(0);
                surfacePressure= weatherModel.getHourly().getSurfacePressure().get(0);
                evaporation= weatherModel.getHourly().getEvapotranspiration().get(0);
                /// ui update
                txtCity.setText(cityName);
                txtTemperature.setText(String.valueOf(temperature));
                txtCloudCover.setText(String.valueOf(cloudCover+" %"));
                txtHumidity.setText(String.valueOf(humidity+" %"));
                txtPrecipitation.setText(String.valueOf(precipitation+" %"));
                txtWind.setText(String.valueOf(wind+" km/h"));
                txtSoilTemperature.setText(String.valueOf(soilTemperature+" C"));
                // list view update
                double v= visibility / 1000.0;
                String subSun= sunRise.substring(sunRise.length() - 5);
                String subSet= sunSet.substring(sunSet.length() - 5);
                homeHourlyModelList.add(new HomeHourlyModel(R.drawable.sunrise, "Sunrise", subSun+" AM"));
                homeHourlyModelList.add(new HomeHourlyModel(R.drawable.sunset_60, "Sunset", subSet+" PM"));
                homeHourlyModelList.add(new HomeHourlyModel(R.drawable.wind_direction, "Wind direction", windDirection+" km/h"));
                homeHourlyModelList.add(new HomeHourlyModel(R.drawable.visibility, "Visibility", v+" km"));
                homeHourlyModelList.add(new HomeHourlyModel(R.drawable.soil_moisture, "Soil Moisturise", soilMoisture+" m³/m³"));
                homeHourlyModelList.add(new HomeHourlyModel(R.drawable.raining, "Rain", raining+" mm"));
                homeHourlyModelList.add(new HomeHourlyModel(R.drawable.snowfall, "Snowfall", snowfall+" cm"));
                homeHourlyModelList.add(new HomeHourlyModel(R.drawable.surface_pressure, "Surface pressure", surfacePressure+" hPa"));
                homeHourlyModelList.add(new HomeHourlyModel(R.drawable.evaporation, "Evaporation", evaporation+ " mm"));
                HomeHourlyAdapter hourlyAdapter= new HomeHourlyAdapter(MainActivity.this, homeHourlyModelList);
                hourlyRecyclerView.setAdapter(hourlyAdapter);
            }

            @Override
            public void onFailure(Call<WeatherModel> call, Throwable t) {
                Log.d("called_failed: ", t.toString());
            }
        });


        list.add(new HomeTemperatureModel("01-10-2022", "30"));
        list.add(new HomeTemperatureModel("01-11-2022", "20"));
        list.add(new HomeTemperatureModel("01-12-2022", "35"));
        list.add(new HomeTemperatureModel("01-13-2022", "25"));
        list.add(new HomeTemperatureModel("01-14-2022", "15"));




        HomeForecastTemperatureAdapter adapter= new HomeForecastTemperatureAdapter(this, list);

        recyclerView.setAdapter(adapter);


    }

    void findAllView(){
        txtTemperature= findViewById(R.id.txt_temperature);
        txtCity= findViewById(R.id.txt_city);
        txtCloudCover= findViewById(R.id.txt_cloud_cover);
        txtHumidity= findViewById(R.id.txt_humidity);
        txtPrecipitation= findViewById(R.id.txt_precipitation);
        txtWind= findViewById(R.id.txt_wind);
        txtSoilTemperature= findViewById(R.id.txt_soil_temperature);
        recyclerView= findViewById(R.id.forecast_temperature_recycler_view);
        hourlyRecyclerView= findViewById(R.id.home_hourly_recycler_view);
    }

}














