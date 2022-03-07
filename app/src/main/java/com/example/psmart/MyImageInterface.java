package com.example.psmart;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface MyImageInterface {

    String IMAGEURL = "https://alliancelanguages.com/psmart/faces/";

    @FormUrlEncoded
    @POST("uploadimage.php")
    Call<String> getImageData(
            @Field("name") String name,
            @Field("image") String image,
             @Field("id") String id,
            @Field("role") String role

    );

}