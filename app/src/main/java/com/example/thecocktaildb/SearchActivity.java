package com.example.thecocktaildb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.example.thecocktaildb.adapter.CocktailAdapter;
import com.example.thecocktaildb.model.Cocktail;
import com.example.thecocktaildb.model.CocktailsResponse;
import com.example.thecocktaildb.rest.ApiClient;
import com.example.thecocktaildb.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private FloatingSearchView floatingSearchView;
    private CocktailAdapter cocktailAdapter;
    private RecyclerView recyclerView;
    private TextView resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        recyclerView = (RecyclerView) findViewById(R.id.searched_list_recyclerview);
        resultText = (TextView) findViewById(R.id.result_text);

        floatingSearchView = (FloatingSearchView) findViewById(R.id.search_cocktail_text);
        floatingSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                if(newQuery.length() >= 1){
                    Call<CocktailsResponse> call = apiService.getDrinkByTitle(newQuery.trim().toString());
                    call.enqueue(getCocktails());
                }else {
                    resultText.setText("Enter text to start search");
                    recyclerView.setVisibility(View.INVISIBLE);
                    resultText.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private Callback<CocktailsResponse> getCocktails() {
        return new Callback<CocktailsResponse>() {
            @Override
            public void onResponse(Call<CocktailsResponse> call, Response<CocktailsResponse> response) {
                int statusCode = response.code();
                //200(OK), 400(BAD_REQUEST), 404(NOT_FOUND)
                switch (statusCode) {
                    case 200:
                        CocktailsResponse drinks = response.body();
                        List<Cocktail> cocktails = drinks.getDrinks();
                        if (cocktails != null){
                            recyclerView.setVisibility(View.VISIBLE);
                            resultText.setVisibility(View.INVISIBLE);
                            ArrayList<Cocktail> list = (ArrayList<Cocktail>) drinks.getDrinks();
                            GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
                            recyclerView.setLayoutManager(gridLayoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            cocktailAdapter = new CocktailAdapter(getApplicationContext(), list, "fromTheCocktailDb");
                            recyclerView.setAdapter(cocktailAdapter);
                        }else {
                            recyclerView.setVisibility(View.INVISIBLE);
                            resultText.setText("No cocktails found");
                            resultText.setVisibility(View.VISIBLE);
                        }
                        break;
                    default:
                        responseError("Http error!");
                        Log.e(TAG, "HTTP CODE " + statusCode);
                        break;
                }
            }

            @Override
            public void onFailure(Call<CocktailsResponse> call, Throwable t) {
                Log.e(TAG, t.toString());
                responseError("Http error!");
            }
        };
    }

    private void responseError(String message) {
        Toast.makeText(getApplicationContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
    }

}
