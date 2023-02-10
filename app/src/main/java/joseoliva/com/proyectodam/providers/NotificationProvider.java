package joseoliva.com.proyectodam.providers;

import joseoliva.com.proyectodam.models.FCMBody;
import joseoliva.com.proyectodam.models.FCMResponse;
import joseoliva.com.proyectodam.retrofit.IFCMApi;
import joseoliva.com.proyectodam.retrofit.RetrofitClient;
import retrofit2.Call;

public class NotificationProvider {

    private String url = "https://fcm.googleapis.com";

    public NotificationProvider(){}

    public Call<FCMResponse> sendNotification(FCMBody body){
        return RetrofitClient.getClient(url).create(IFCMApi.class).send(body);
    }
}
