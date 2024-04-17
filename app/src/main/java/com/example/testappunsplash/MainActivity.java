package com.example.testappunsplash;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.example.testappunsplash.model.Photo;
import com.example.testappunsplash.serviceapi.MovieApiService;
import com.example.testappunsplash.serviceapi.RetrofitInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PhotoAdapter adapter;
    private List<Photo> photos;
    private int currentPage = 1;
    private int perPage = 20;
    private boolean isLoading = false;
    private ProgressDialog progressDialog;
    private ImageView noInternetImageView;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        noInternetImageView = findViewById(R.id.noInternetImageView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        photos = new ArrayList<>();
        adapter = new PhotoAdapter(photos, this);

//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        if (isNetworkAvailable()) {
            loadPhotos(currentPage, perPage);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            noInternetImageView.setVisibility(View.VISIBLE);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        // Pagination - Load more items when reaching the end of the list
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!isLoading && !recyclerView.canScrollVertically(1)) {
                    currentPage++;
                    loadPhotos(currentPage, perPage);
                }
            }
        });
    }

    private void loadPhotos(int page, int perPage) {
        progressDialog.show();
        isLoading = true;

        MovieApiService service = RetrofitInstance.getService();
        Call<List<Photo>> call = service.getPhotos("ytCqnDHUqxYAUUjGUKTmE207cHfMVaAswjXWc5Cw-hQ", page, perPage);
        call.enqueue(new Callback<List<Photo>>() {
            @Override
            public void onResponse(Call<List<Photo>> call, Response<List<Photo>> response) {
                progressDialog.dismiss();
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful()) {
                    List<Photo> newPhotos = response.body();
                    if (newPhotos != null) {
                        photos.addAll(newPhotos);
                        adapter.notifyDataSetChanged();
                        if (isLastPage()) {
                            // Handle end of data
                        }
                    } else {
                        Log.d("Success", "Response body is null");
                    }
                } else {
                    Log.d("Success", "Response not successful: " + response.message());
                    Toast.makeText(MainActivity.this, "Failed to load photos", Toast.LENGTH_SHORT).show();
                }
                isLoading = false;
            }

            @Override
            public void onFailure(Call<List<Photo>> call, Throwable t) {
                progressDialog.dismiss();
                swipeRefreshLayout.setRefreshing(false);

                Log.d("Failed", "Failed to load photos: " + t.getMessage());
                isLoading = false;
                Toast.makeText(MainActivity.this, "Failed to load photos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshData() {
        photos.clear();
        currentPage = 1;
        loadPhotos(currentPage, perPage);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }

    private boolean isLastPage() {
        return photos.size() < perPage;
    }
}
