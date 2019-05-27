package com.example.sns_project.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.sns_project.info.PostInfo;
import com.example.sns_project.R;
import com.example.sns_project.activity.PostActivity;
import com.example.sns_project.listener.OnPostListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import static com.example.sns_project.Util.isStorageUrl;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private ArrayList<PostInfo> mDataset;
    private Activity activity;

    private OnPostListener onPostListener;

    static class PostViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;

        PostViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public PostAdapter(Activity activity, ArrayList<PostInfo> myDataset) {
        this.mDataset = myDataset;
        this.activity = activity;
    }

    //Listener setter
    public void setOnPostListener(OnPostListener onPostListener) {
        this.onPostListener = onPostListener;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public PostAdapter.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { //viewType 으로 position 값 확인
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        final PostViewHolder postViewHolder = new PostViewHolder(cardView);
        cardView.setOnClickListener(v -> {
            Intent intent = new Intent(activity, PostActivity.class);
            intent.putExtra("postInfo", mDataset.get(postViewHolder.getAdapterPosition()));
            activity.startActivity(intent);
        });

        cardView.findViewById(R.id.cv_post_menu).setOnClickListener(v -> showPopup(v, postViewHolder.getAdapterPosition()));

        return postViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final PostViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        //제목 가져오기
        TextView titleTextView = cardView.findViewById(R.id.tv_title);
        titleTextView.setText(mDataset.get(position).getTitle());
        //게시글 생성날짜 가져오기
        TextView createdAtTextView = cardView.findViewById(R.id.tv_createdAt);
        createdAtTextView.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(mDataset.get(position).getCreatedAt()));
        //이미지 텍스트 동적 배치
        LinearLayout contentsLayout = cardView.findViewById(R.id.ll_contents);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ArrayList<String> contentsList = mDataset.get(position).getContents();

        if(contentsLayout.getTag() == null || !contentsLayout.getTag().equals(contentsList)){ //태그가 contensList와 다르고 null 일 경우
            contentsLayout.setTag(contentsList);
            contentsLayout.removeAllViews();

            //TODO 더보기 오류 수정(다른것이 뜸)
            final int MORE_CONTENTS = 5; //더보기 개수 제한 설정
            //동적 화면 구성
            for (int i = 0; i < contentsList.size(); i++){

                if(i == MORE_CONTENTS){ //게시글 내용이 3개이상일 경우 더보기 기능 추가
                    TextView textView = new TextView(activity);
                    textView.setLayoutParams(layoutParams);
                    textView.setText("더보기...");
                    contentsLayout.addView(textView);
                    break;
                }

                String contents = contentsList.get(i);
                if(isStorageUrl(contents)){ //URL이고 storage 경로일 경우
                    ImageView imageView = new ImageView(activity);
                    imageView.setLayoutParams(layoutParams);
                    //이미지 화면 맞춤
                    imageView.setAdjustViewBounds(true);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    contentsLayout.addView(imageView);
                    Glide.with(activity).load(contents).override(1000).thumbnail(0.1f).into(imageView);
                }else {
                    TextView textView = new TextView(activity);
                    textView.setLayoutParams(layoutParams);
                    textView.setText(contents);
                    textView.setTextColor(Color.rgb(0, 0, 0));
                    contentsLayout.addView(textView);
                }
            }
        }

        for (int i = 0; i < contentsLayout.getChildCount(); i++) { //컨텐츠 개수만큼 반복
            String contents = contentsList.get(i);
            View view = contentsLayout.getChildAt(i);
            if(view instanceof ImageView){ //ImageView일 경우
                Glide.with(activity).load(contents).override(1000).thumbnail(0.1f).into((ImageView) view);
            }else if(view instanceof TextView){ //TextView일 경우
                ((TextView) contentsLayout.getChildAt(i)).setText(contents);
            }
        }

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void showPopup(View v, int position) { //게시물 우상단 팝업창 메소드(팝업 뷰, 게시물 id값)
        PopupMenu popup = new PopupMenu(activity, v);
        popup.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.post_modify: //게시글 수정
                    onPostListener.onModify(position); //수정 리스너
                    return true;
                case R.id.post_delete: //게시글 삭제
                    onPostListener.onDelete(position); //삭제 리스너
                    return true;
                default:
                    return false;
            }
        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.post, popup.getMenu());
        popup.show();
    }
}