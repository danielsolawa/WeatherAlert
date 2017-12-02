package com.danielsolawa.locationapp.client;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

/**
 * Created by NeverForgive on 2017-09-01.
 */

public class OpenWeatherRestClient {
    public enum QueryType{
        weather, forecast
    }

    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
    /*
     * To obtain api key, register at http://openweathermap.org 
     */
    private static final String API_KEY = "";

    private static AsyncHttpClient client = new AsyncHttpClient();
    private static AsyncHttpClient syncClient = new SyncHttpClient();

    public static void get(String url, RequestParams params,
                           AsyncHttpResponseHandler responseHandler){
        client.get(url, params, responseHandler);
    }

    public static void getSynchronous(String url, RequestParams params,
                                        AsyncHttpResponseHandler responseHandler){
        syncClient.get(url, params, responseHandler);
    }

    public static String generateUrl(double latitude, double longitude, QueryType type){
        StringBuilder builder = new StringBuilder();
        builder.append(BASE_URL);
        builder.append(type.toString());
        builder.append("?lat=");
        builder.append(latitude);
        builder.append("&lon=");
        builder.append(longitude);
        builder.append("&units=metric");
        builder.append("&APPID=");
        builder.append(API_KEY);


        return builder.toString();
    }


}
