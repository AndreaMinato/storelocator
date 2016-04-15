package it.kdevgroup.storelocator;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mattia on 07/04/16.
 */

public class EventsCardsAdapter extends RecyclerView.Adapter<EventsCardsAdapter.CardViewHolder> {

    private ArrayList<Store> stores;  //lista di eventi
    private Context ctx;
    private int filter;

    public EventsCardsAdapter(ArrayList<Store> stores, Context ctx) {
        this.stores = stores;
        this.ctx = ctx;
    }

    /**
     * Chiamato quando il recycler view ha bisogno di una card per mostrare un evento
     *
     * @param viewGroup view padre di ogni carta (recyclerview in teoria)
     * @param viewType  tipo della view che sarà popolata (CardView)
     * @return oggetto CardViewHolder definito alla fine che setterà i vari TextView presenti nella CardView
     */
    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card, viewGroup, false);
        return new CardViewHolder(v);
    }

    /**
     * Crea una card, chiamato ogni volta che deve essere mostrata una CardView
     *
     * @param cardHolder CardViewHolder restituito dal metodo precedente
     * @param position   posizione di un evento nella lista
     */
    @Override
    public void onBindViewHolder(CardViewHolder cardHolder, final int position) {
        //cardHolder.blogName.setText(stores.get(position).getBlogName());
        // carico l'immagine con picasso

        //Picasso.with(ctx).setIndicatorsEnabled(true);

        //load thumbnail sulla card?
        Picasso.with(ctx)
                .load(stores.get(position).getThumbnail())
                .fit()
        //      .networkPolicy(NetworkPolicy.OFFLINE, NetworkPolicy.NO_CACHE)
                .into(cardHolder.thumb);

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return stores.size();
    }

    public void addEvents(List<Store> eventsToAdd) {
        for (Store newEvent : eventsToAdd) {
            stores.add(newEvent);
            notifyItemInserted(stores.size() - 1);
        }
    }

    /**
     * "Contenitore" di ogni card
     */
    public static class CardViewHolder extends RecyclerView.ViewHolder {
        CardView card;
        TextView blogName;
        ImageView thumb;

        CardViewHolder(View itemView) {
            super(itemView);
            card = (CardView) itemView.findViewById(R.id.cardView);
            blogName = (TextView) itemView.findViewById(R.id.textView);
            thumb = (ImageView) itemView.findViewById(R.id.imgThumb);
        }
    }

}