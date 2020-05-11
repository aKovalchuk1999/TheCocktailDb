package com.example.thecocktaildb.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CocktailsResponse {
    @SerializedName("drinks")
    private List<Cocktail> drinks;

    public List<Cocktail> getDrinks() {
        return drinks;
    }
}
