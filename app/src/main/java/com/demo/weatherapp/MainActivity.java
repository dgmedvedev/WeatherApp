package com.demo.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

    String city = "Minsk";
    String name;
    String temp;
    String description;
    String json;

    String urlAddressPart1;
    String urlAddressPart2;
    String fullUrlAddress;
    String regex;

    Button button_weather;
    EditText editTextCity;
    TextView textViewCity;
    TextView textViewTemp;
    TextView textViewDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        urlAddressPart1 = getString(R.string.url_address_part1);
        urlAddressPart2 = getString(R.string.url_address_part2);
        fullUrlAddress = urlAddressPart1 + city + urlAddressPart2;
        regex = getString(R.string.regex);

        button_weather = findViewById(R.id.buttonShowWeather);
        editTextCity = findViewById(R.id.editTextCity);
        textViewCity = findViewById(R.id.textViewCity);
        textViewTemp = findViewById(R.id.textViewTemp);
        textViewDescription = findViewById(R.id.textViewDescription);

        getContent();

        button_weather.setOnClickListener(view -> {
            city = editTextCity.getText().toString();
            fullUrlAddress = urlAddressPart1 + city + urlAddressPart2;
            getContent();
        });
    }

    private void getContent() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            getJson();
            getData();
            handler.post(() -> {
                textViewCity.setText(name);
                textViewTemp.setText(temp);
                textViewDescription.setText(description);
            });
        });
    }

    private void getJson() {
        URL url;
        HttpURLConnection urlConnection = null;
        StringBuilder result = new StringBuilder();
        try {
            url = new URL(fullUrlAddress);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = urlConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                result.append(line);
                line = reader.readLine();
            }
            json = result.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    private void getData() {
        try {
            JSONObject jsonObject = new JSONObject(json);
            name = jsonObject.getString("name");

            JSONObject main = jsonObject.getJSONObject("main");
            temp = main.getString("temp");
            double t = Double.parseDouble(temp);
            t = t - 273.15;
            temp = String.format(regex, t);
            temp = "Температура: " + temp;

            JSONArray weather = jsonObject.getJSONArray("weather");
            JSONObject index = weather.getJSONObject(0);
            description = index.getString("description");
            description = "На улице: " + description;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}