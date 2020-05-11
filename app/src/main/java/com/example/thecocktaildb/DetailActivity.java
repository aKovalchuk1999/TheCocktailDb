package com.example.thecocktaildb;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.thecocktaildb.localDb.DbQuery;
import com.example.thecocktaildb.model.Cocktail;
import com.example.thecocktaildb.model.CocktailsResponse;
import com.example.thecocktaildb.rest.ApiClient;
import com.example.thecocktaildb.rest.ApiInterface;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private static final String TAG = DetailActivity.class.getSimpleName();
    private ImageView cocktailImage;
    private TextView cocktailTitle, cocktailAlcoholic, cocktailGlass, cocktailInstructions;
    private LinearLayout cocktailIngredients;

    private DbQuery dbQuery = new DbQuery(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        cocktailImage = (ImageView) findViewById(R.id.detail_cocktail_image);
        cocktailTitle = (TextView) findViewById(R.id.detail_cocktail_title);
        cocktailAlcoholic = (TextView) findViewById(R.id.detail_alcoholic_text);
        cocktailGlass = (TextView) findViewById(R.id.detail_glass_text);
        cocktailInstructions = (TextView) findViewById(R.id.detail_instructions_text);
        cocktailIngredients = (LinearLayout) findViewById(R.id.detail_ingredients_list);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String cocktailId = extras.getString("cocktailId");
        String loadType = extras.getString("loadType");



        if (loadType.equals("fromTheCocktailDb")){

            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<CocktailsResponse> call = apiService.getDrinkById(cocktailId);
            progressDialogShow();
            call.enqueue(callCocktailsById(dbQuery));
            Log.e(TAG, "Loaded from TheCocktailDb");

        }else if (loadType.equals("fromLocalDb")){
            Cocktail cocktail = dbQuery.getCocktailById(cocktailId);
            showData(cocktail, "fromLocalDb");
            writeToDb(cocktail);
            Log.e(TAG, "Loaded from local Db");
        }

    }

    private Callback<CocktailsResponse> callCocktailsById(final DbQuery dbQuery) {
        return new Callback<CocktailsResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<CocktailsResponse> call, Response<CocktailsResponse> response) {
                int statusCode = response.code();
                switch (statusCode) {
                    case 200:
                        CocktailsResponse drinks = response.body();
                        if (!drinks.getDrinks().isEmpty()){
                            Cocktail cocktail = drinks.getDrinks().get(0);
                            showData(cocktail, "fromTheCocktailDb");
                            writeToDb(cocktail);
                        }
                        break;
                    default:
                        responseError("HTTP problems!!!");
                        Log.e(TAG, "HTTP CODE " + statusCode);
                        break;
                }
                progressDialogHide();
            }
            @Override
            public void onFailure(Call<CocktailsResponse> call, Throwable t) {
                Log.e(TAG, t.toString());
                responseError("HTTP Problems!!!");
                progressDialogHide();
            }
        };
    }


    private void progressDialogShow() {
        progressDialog = new ProgressDialog(DetailActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    private void progressDialogHide() {
        if(progressDialog != null && progressDialog.isShowing()) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }
    private void responseError(String message) {
        Toast.makeText(getApplicationContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
    }

    private void showData(Cocktail cocktail, String type){
        try {
            Glide.with(getApplicationContext()).load(Uri.parse(cocktail.getThumbnail())).into(cocktailImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cocktailTitle.setText(cocktail.getName().toString());
        cocktailAlcoholic.setText(cocktail.getAlcoholic());
            if (cocktail.getAlcoholic().equals("Non alcoholic")){
                cocktailAlcoholic.setTextColor(getResources().getColor(R.color.green));
            }else {
                cocktailAlcoholic.setTextColor(Color.RED);
            }
        cocktailGlass.setText(cocktail.getGlass());
        cocktailIngredients.removeAllViews();
        List<String> cocktailIndredient = null;
        if (type.equals("fromLocalDb")){
            String ingredients = cocktail.getIngredient1();
            String replace = ingredients.replaceAll("^\\[|]$", "");
            cocktailIndredient = new ArrayList<String>(Arrays.asList(replace.split(",")));
        }else if (type.equals("fromTheCocktailDb")){
            cocktailIndredient = cocktail.getIngredientMeasureList();
        }
        for (String ingredient : cocktailIndredient){
            TextView ingredientTextView = new TextView(getApplicationContext());
            ingredientTextView.setText(ingredient.replace("\n", "").replace("\r", ""));
            ingredientTextView.setTextColor(Color.BLACK);
            ingredientTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            cocktailIngredients.addView(ingredientTextView);
        }
        cocktailInstructions.setText(cocktail.getInstructions());
    }
    private void writeToDb(Cocktail cocktail){
        String idDrink = cocktail.getIdDrink();
        String name = cocktail.getName();
        String category = cocktail.getCategory();
        String alcoholic = cocktail.getAlcoholic();
        String glass = cocktail.getGlass();
        String instructions = cocktail.getInstructions();
        String thumbnail = cocktail.getThumbnail();
        String ingredients = cocktail.getIngredientMeasureList().toString();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String dateModified = formatter.format(date);
        Cocktail insertCocktail = new Cocktail(idDrink, name, category, alcoholic, glass,
                instructions, thumbnail, ingredients, dateModified);
        if (dbQuery.getCocktailById(idDrink) == null){
            dbQuery.insertCocktail(insertCocktail);
        }else {
            dbQuery.updateCocktailInfo(insertCocktail);
        }
    }

}
