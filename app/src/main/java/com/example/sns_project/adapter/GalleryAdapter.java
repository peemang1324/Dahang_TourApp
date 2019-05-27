package com.example.sns_project.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.sns_project.R;

import java.util.ArrayList;

import static com.example.sns_project.Util.INTENT_PATH;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {
    private ArrayList<String> mDataset;
    private Activity activity;

    static class GalleryViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        GalleryViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public GalleryAdapter(Activity activity, ArrayList<String> myDataset){
        this.activity = activity;
        mDataset = myDataset;
    }

    @Override
    public GalleryAdapter.GalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery, parent, false);

        final GalleryViewHolder galleryViewHolder = new GalleryViewHolder(cardView);
        //갤러리에서 사진을 선택했을 경우
        cardView.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(INTENT_PATH, mDataset.get(galleryViewHolder.getAdapterPosition()));
            activity.setResult(Activity.RESULT_OK, resultIntent);
            activity.finish();
        });

        return galleryViewHolder;
    }

    @Override
    public void onBindViewHolder(final GalleryViewHolder holder, int position) { //이미지를 출력해주는 메소드
        CardView cardView = holder.cardView;
        ImageView imageView = cardView.findViewById(R.id.iv_gallery);
        //image Resizing
        Glide.with(activity).load(mDataset.get(position)).centerCrop().override(600).into(imageView);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}