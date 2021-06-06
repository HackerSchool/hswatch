package com.hswatch.refactor;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hswatch.Utils;
import com.hswatch.bluetooth.GPSListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

//TODO(documentar)
public class Watch {

    private String name;
    private String address;
    private final HashMap<String, String> weeksMap;
    private int timeInterval;


    /**
     * Ignored columns
     */
    private double[] gpsCoordinates = {0, 0};
    private String location;
    private final Context context;
    private final RequestQueue requestQueue;

    public Watch(Context context, @NonNull BluetoothDevice bluetoothDevice) {
        this.name = bluetoothDevice.getName();
        this.address = bluetoothDevice.getAddress();
        this.context = context;
        this.weeksMap = Utils.getWeekArray(context);

        this.requestQueue = Volley.newRequestQueue(context);
    }

    public void getWeatherStatus(VolleyCallBack callBack) {

        String API_URL = getAPIURL();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, API_URL, null,
                response -> {
                    try {
                        callBack.requestReturn(parseResponse(response));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace);
        this.requestQueue.add(request);

    }

    public interface VolleyCallBack {
        void requestReturn(ArrayList<String> requestList);
    }

    @NonNull
    private ArrayList<String> parseResponse(@NonNull JSONObject response) throws JSONException {
        ArrayList<String> weatherResponse = new ArrayList<>();
        weatherResponse.add(this.location);
        JSONArray jsonArray = response.getJSONArray(Utils.WEATHER_DATA);
        for (int j = 0; j < jsonArray.length(); j++) {
            JSONObject indexStatus = jsonArray.getJSONObject(j);

            // Add the string indicating the icon
            weatherResponse.add(indexStatus.getJSONObject(Utils.WEATHER_ICON)
                    .getString(Utils.WEATHER_ICON_CODE));

            // Add the string indicating Maximum and Minimum Temperature
            weatherResponse.add(String.valueOf(indexStatus.getDouble(Utils.WEATHER_MAX_TEMP)));
            weatherResponse.add(String.valueOf(indexStatus.getDouble(Utils.WEATHER_MIN_TEMP)));

            // Add the string indicating the Probability of Precipitation
            weatherResponse.add(String.valueOf(indexStatus.getInt(Utils.WEATHER_POP)));
        }
        return weatherResponse;
    }

    @NonNull
    private String getAPIURL() {
        // URL Link to request the weather
        String url = "https://api.weatherbit.io/v2.0/forecast/daily?";

        // Get the user or default' preferences and add them on the URL Link
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this.context);

        url += getLocationString(sharedPreferences) +
                getUnitSystem(sharedPreferences) + Utils.getKey();

        return url;
    }

    @NonNull
    private String getUnitSystem(@NonNull SharedPreferences sharedPreferences) {
        // Tells which system that the API uses to show on the request.
        // Set metric system as default
        //TODO(criar chave e default na Utils)
        String unitSystem = sharedPreferences.getString("unidades", "M");
        if (unitSystem != null) {
            return "&units=" + unitSystem;
        } else {
            return "&units=M";
        }
    }

    @NonNull
    private String getLocationString(@NonNull SharedPreferences sharedPreferences) {
        // Tells if the user choose to use the GPS location - gpsOn - and define that on the URL Link
        // to the API
        //TODO(criar chave e default na Utils)
        boolean gpsOn = sharedPreferences.getBoolean("gps_switch", false);

        if (!gpsOn) {
            String cityChoosen = sharedPreferences.getString("cidades", "Lisbon");
            if (cityChoosen != null) {
                location = (Objects.equals("Lisbon", cityChoosen) ? "Lisboa" : cityChoosen);
            } else {
                location = "Lisboa";
            }
            return "city=" + location;
        } else {
            double[] currentCoordinates = GPSListener.getInstance(this.context).getGpsCoordinates();
            float roundedLat = (float) Math.round(currentCoordinates[0] * 10) / 10;
            float roundedLon = (float) Math.round(currentCoordinates[1] * 10) / 10;
            location = "GPS: (" + roundedLat + ", " + roundedLon + ")";
            return "lat= " + currentCoordinates[0] + "&lon=" + currentCoordinates[1];
        }
    }

    public int getTimeInterval() {
        // Get
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this.context);
        //TODO(criar chave e default na Utils)
        String period = sharedPreferences.getString("horas", "15");

        if (period != null) {
            timeInterval = Math.max(Integer.parseInt(period), 15);
        }

        return timeInterval;
    }

    public String[] getCurrentTime() {
        return Utils.getCurrentTime(this.weeksMap);
    }

    public String getName() {
        return name;
    }
}
