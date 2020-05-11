package com.example.thecocktaildb.rest;

import com.example.thecocktaildb.model.CocktailsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("filter.php?a=Non_Alcoholic")
    Call<CocktailsResponse> getNonAlcoholicDrinks();

    @GET("lookup.php")
    Call<CocktailsResponse> getDrinkById(@Query("i") String idDrink);

    @GET("search.php")
    Call<CocktailsResponse> getDrinkByTitle(@Query("s") String strDrink);

}
