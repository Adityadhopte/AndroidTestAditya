package com.example.testappunsplash.serviceapi;

import com.example.testappunsplash.model.Photo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


//  pagination parameters
public interface MovieApiService {
    @GET("photos/")
    Call<List<Photo>> getPhotos(@Query("client_id") String apiKey, @Query("page") int page, @Query("per_page") int perPage);
}
