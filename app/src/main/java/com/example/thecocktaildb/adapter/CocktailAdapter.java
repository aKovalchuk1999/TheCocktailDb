package com.example.thecocktaildb.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.thecocktaildb.DetailActivity;
import com.example.thecocktaildb.R;
import com.example.thecocktaildb.localDb.DbQuery;
import com.example.thecocktaildb.model.Cocktail;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CocktailAdapter extends RecyclerView.Adapter<CocktailAdapter.MyViewHolder>{

    Context context;
    ArrayList<Cocktail> list;
    String loadType;

    public CocktailAdapter(Context context, ArrayList<Cocktail> list, String loadType){
        this.context = context;
        this.list = list;
        this.loadType = loadType;
    }

    @NonNull
    @Override
    public CocktailAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cocktail_card_item, parent, false);
        return new CocktailAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CocktailAdapter.MyViewHolder holder, final int position) {
        try {
            Glide.with(context.getApplicationContext()).load(Uri.parse(list.get(position).getThumbnail()))
                    .into(holder.cocktailImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.cocktailTitle.setText(list.get(position).getName().toString());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DetailActivity.class);
                Bundle extras = new Bundle();
                extras.putString("cocktailId", list.get(position).getIdDrink().toString());
                extras.putString("loadType", loadType.toString());
                intent.putExtras(extras);
                v.getContext().startActivity(intent);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView cocktailImage;
        TextView cocktailTitle;
        public MyViewHolder (View view) {
            super(view);
            cocktailTitle = (TextView) view.findViewById(R.id.drink);
            cocktailImage = (ImageView) view.findViewById(R.id.thumbnail);
        }
    }

}
