package com.example.thecocktaildb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thecocktaildb.adapter.CocktailAdapter;
import com.example.thecocktaildb.localDb.DbQuery;
import com.example.thecocktaildb.model.Cocktail;
import com.example.thecocktaildb.model.CocktailsResponse;
import com.example.thecocktaildb.rest.ApiClient;
import com.example.thecocktaildb.rest.ApiInterface;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton openSearchActivity;
    private CocktailAdapter cocktailAdapter;
    private RecyclerView recyclerView;
    private TextView resultText;

    private DbQuery dbQuery;
    private ArrayList<Cocktail> cocktailsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.main_cocktails_list_recyclerview);
        resultText = (TextView) findViewById(R.id.main_empty_list_text);
        dbQuery = new DbQuery(getApplicationContext());


        if(dbQuery.getNumberOfCocktails() > 0){
            resultText.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            updateDate();
        }else {
            resultText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }

        openSearchActivity = (FloatingActionButton) findViewById(R.id.main_search_btn);
        openSearchActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateDate();
    }

    private void updateDate(){
        cocktailsList = (ArrayList<Cocktail>) dbQuery.getAllCocktails();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        cocktailAdapter = new CocktailAdapter(getApplicationContext(), cocktailsList, "fromLocalDb");
        recyclerView.setAdapter(cocktailAdapter);
    }

}
