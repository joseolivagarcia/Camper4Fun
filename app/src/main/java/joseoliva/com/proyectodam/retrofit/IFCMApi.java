package joseoliva.com.proyectodam.retrofit;

import joseoliva.com.proyectodam.models.FCMBody;
import joseoliva.com.proyectodam.models.FCMResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAJt-InD4:APA91bHxgfuc2yYY6dpGELqc1sqbclOnnMrWEaUgSJ8eHm7UXTNVVKTWn4zPiKtfg4eRetpwdyjNqvgfT_-aBgi_nrexAEedhtnUEJcz7u57MJ_Sg2IfYSTCTRySUQhuCJk4AM0IXNwk"

    })
    @POST("fcm/send")
    Call<FCMResponse> send(@Body FCMBody body);
}
