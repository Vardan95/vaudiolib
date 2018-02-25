package com.vpetrosyan.audio.vwaveview;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by varan on 2/25/18.
 */

public class WaveImageViewAdapter extends RecyclerView.Adapter<WaveImageViewAdapter.WaveImageHolder> {

    private Bitmap waveImage;

    public void setImage(Bitmap bitmap) {
        waveImage = bitmap;
        notifyDataSetChanged();
    }

    @Override
    public WaveImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_wave, parent, false);
        return new WaveImageHolder(view);
    }

    @Override
    public void onBindViewHolder(WaveImageHolder holder, int position) {
        if(waveImage != null && !waveImage.isRecycled()) {
            holder.imageView.setImageBitmap(waveImage);
        }
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    class WaveImageHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        WaveImageHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_wave);
        }
    }
}
