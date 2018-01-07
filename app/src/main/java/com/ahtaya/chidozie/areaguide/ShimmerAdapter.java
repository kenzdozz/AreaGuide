package com.ahtaya.chidozie.areaguide;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.shimmer.ShimmerFrameLayout;

/**
 * Created by CHIDOZIE on 12/31/2017.
 * ShimmerAdapter is similar to to SpotAdapter only that it doesn't adapt the RecyclerView with any data
 * It inflates the RecyclerView with a layout file shimmer_card that has the shimmer effect like facebook
 */

public class ShimmerAdapter extends RecyclerView.Adapter<ShimmerAdapter.ShimmerViewHolder> {

    class ShimmerViewHolder extends RecyclerView.ViewHolder {

        ShimmerFrameLayout shimmerFrameLayout;

        ShimmerViewHolder(View itemView) {
            super(itemView);
            shimmerFrameLayout = itemView.findViewById(R.id.shimmer_view);
        }
    }

    @Override
    public ShimmerAdapter.ShimmerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shimmer_card, parent, false);
        return new ShimmerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ShimmerAdapter.ShimmerViewHolder holder, int position) {
        holder.shimmerFrameLayout.setDuration(2000);
        holder.shimmerFrameLayout.startShimmerAnimation();
    }

    /**
     * return 7 shimmer child views
     */
    @Override
    public int getItemCount() { return 7; }
}
