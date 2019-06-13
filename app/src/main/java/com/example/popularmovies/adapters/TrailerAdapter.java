package com.example.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.popularmovies.R;
import com.example.popularmovies.data.Trailer;

import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    /*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    final private TrailerAdapterOnClickHandler clickHandler;
    private Context context;
    private List<Trailer> trailers;

    public TrailerAdapter(Context context, TrailerAdapterOnClickHandler clickHandler) {
        this.context = context;
        this.clickHandler = clickHandler;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.trailer_list_item, viewGroup, false);
        TrailerViewHolder viewHolder = new TrailerViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int i) {
        String trailer = context.getString(R.string.trailer) + (i + 1);
        holder.trailerButton.setText(trailer);
    }

    @Override
    public int getItemCount() {
        if (trailers == null) return 0;
        return trailers.size();
    }

    public void setTrailersData(List<Trailer> trailers) {
        this.trailers = trailers;
        notifyDataSetChanged();
    }

    public void clear() {
        final int size = getItemCount();
        this.trailers.clear();
        notifyItemRangeRemoved(0, size);
    }

    /**
     * The interface that receives onClick messages.
     */
    public interface TrailerAdapterOnClickHandler {
        void onClick(Trailer trailer);
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Button trailerButton;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            trailerButton = itemView.findViewById(R.id.trailer_button);
            trailerButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Trailer clickedTrailer = trailers.get(position);
            clickHandler.onClick(clickedTrailer);
        }
    }
}
