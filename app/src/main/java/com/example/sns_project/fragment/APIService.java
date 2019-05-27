package com.example.sns_project.fragment;

import com.example.sns_project.Notifications.MyResponse;
import com.example.sns_project.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAsQRqIME:APA91bHhGXnmQD7w627bGw20c8ETAdgywJ7Pm2ezh3NgMwznXvODCUM9At3_dF5z6fVX1mOvdjniVYiV-3G6wMyIyuzWloiwvSmTjSC84xJHNv6q9zL3uEwh1tsNbjzU5S1mx3kelzQ6"

            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification (@Body Sender body);
}
