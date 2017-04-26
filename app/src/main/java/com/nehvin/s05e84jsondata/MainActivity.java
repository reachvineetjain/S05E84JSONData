package com.nehvin.s05e84jsondata;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DownloadWeatherContent weatherContent = new DownloadWeatherContent();
        String result = null;
        try {
            //19.2089781,72.8357042   lat=19.2089781&lon=72.8357042
            result = weatherContent.execute("http://api.openweathermap.org/data/2.5/weather?lat=19.2089781&lon=72.8357042&appid=72f99145220bac2613bffc41ebee0df1").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        TextView txtView = (TextView) findViewById(R.id.textView);
        txtView.setText(result);
    }

    class DownloadWeatherContent extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... params) {
            URL url = null;
            HttpURLConnection connection = null;
            InputStream in = null;
            InputStreamReader reader = null;
            String result = "";
            try {
                url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                in = connection.getInputStream();
                reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1)
                {
                    char current = (char)data;
                    result += current;
                    data=reader.read();
                }
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "Unable to get Content";
            } catch (IOException e) {
                e.printStackTrace();
                return "Unable to get Content";
            }
            finally {
                try {
                    if(in != null)
                        in.close();
                    if(reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");
                Log.i(" weather ", weatherInfo);
                JSONArray jsonArray = new JSONArray(weatherInfo);
                for (int i=0; i < jsonArray.length(); i++)
                {
                    JSONObject jsonPart = jsonArray.getJSONObject(i);
                    Log.i("main", jsonPart.getString("main"));
                    Log.i("description", jsonPart.getString("description"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}