package com.demo.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    String cityName;
    String temp;
    String description;

    String urlAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        urlAddress = getString(R.string.url_address);

        getContent();
    }

    private void getContent() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        //Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(this::getJson);
    }

    private void getJson() {
        URL url;
        HttpURLConnection urlConnection = null;
        StringBuilder result = new StringBuilder();
        try {
            url = new URL(urlAddress);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = urlConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                result.append(line);
                line = reader.readLine();
            }

            String json = result.toString();
            JSONObject jsonObject = new JSONObject(json);
            cityName = jsonObject.getString("name");

            JSONObject main = jsonObject.getJSONObject("main");
            temp = main.getString("temp");

            JSONArray weather = jsonObject.getJSONArray("weather");
            JSONObject index = weather.getJSONObject(0);
            description = index.getString("description");

            Log.i("CONTENT_WEATHER", "city = " + cityName +
                    ", temp = " + temp + ", " + description);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }
}