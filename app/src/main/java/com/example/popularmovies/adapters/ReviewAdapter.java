package com.example.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.popularmovies.R;
import com.example.popularmovies.data.Review;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private Context context;
    private List<Review> reviews;

    public ReviewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.review_list_item, viewGroup, false);
        ReviewViewHolder viewHolder = new ReviewViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int i) {
        Review review = reviews.get(i);
        holder.authorTextView.setText(review.getAuthor());
        holder.contentTextView.setText(review.getContent());


    }

    @Override
    public int getItemCount() {
        if (reviews == null) return 0;
        return reviews.size();
    }

    public void setReviewsData(List<Review> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    public void clear() {
        final int size = getItemCount();
        this.reviews.clear();
        notifyItemRangeRemoved(0, size);
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView authorTextView;
        TextView contentTextView;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            authorTextView = itemView.findViewById(R.id.author_textView);
            contentTextView = itemView.findViewById(R.id.content_textView);
        }
    }
}
