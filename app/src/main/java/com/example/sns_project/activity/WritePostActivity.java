package com.example.sns_project.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.example.sns_project.R;
import com.example.sns_project.info.PostInfo;
import com.example.sns_project.view.ContentsItemView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import static com.example.sns_project.Util.GALLERY_IMAGE;
import static com.example.sns_project.Util.GALLERY_VIDEO;
import static com.example.sns_project.Util.INTENT_MEDIA;
import static com.example.sns_project.Util.INTENT_PATH;
import static com.example.sns_project.Util.isStorageUrl;
import static com.example.sns_project.Util.showToast;
import static com.example.sns_project.Util.storageUrlToName;

public class WritePostActivity extends BasicActivity {
    private final static String TAG ="WritePostActivity";
    private FirebaseUser user;
    private StorageReference storageRef;
    private ArrayList<String> pathList = new ArrayList<>();
    private LinearLayout parent;
    private ImageView selectImageView;
    private EditText selectEditText;
    private EditText et_post_title, et_post_content;
    private RelativeLayout rl_post_background, loaderLayout;
    private int pathCount, successCount;
    PostInfo postInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);



        loaderLayout = findViewById(R.id.loaderLayout);
        parent = findViewById(R.id.contents_layout); //레이아웃 추가
        rl_post_background = findViewById(R.id.rl_post_background);
        et_post_title = findViewById(R.id.et_post_title);
        et_post_content = findViewById(R.id.et_post_content);

        //버튼을 눌렀을 경우 처리
        findViewById(R.id.rl_post_background).setOnClickListener(onClickListener);
        findViewById(R.id.bt_post).setOnClickListener(onClickListener);
        findViewById(R.id.bt_post_image).setOnClickListener(onClickListener);
        findViewById(R.id.bt_post_video).setOnClickListener(onClickListener);
        findViewById(R.id.bt_edit_postimage).setOnClickListener(onClickListener);
        findViewById(R.id.bt_edit_postvideo).setOnClickListener(onClickListener);
        findViewById(R.id.bt_delete_contents).setOnClickListener(onClickListener);

        et_post_content.setOnFocusChangeListener(onFocusChangeListener);
        et_post_title.setOnFocusChangeListener((v, hasFocus) -> { //제목에는 추가하지 못하도록 설정
            if(hasFocus){
                selectEditText = null;
            }
        });

        FirebaseStorage storage = FirebaseStorage.getInstance(); //Firebase Storage 초기화
        storageRef = storage.getReference();

        postInfo = (PostInfo) getIntent().getSerializableExtra("postInfo"); //게시물 id값
        postKeep();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 0: //이미지, 동영상 추가
                if(resultCode == Activity.RESULT_OK){
                    String path = data.getStringExtra(INTENT_PATH);
                    pathList.add(path); //경로를 생성 할때마다 추가

                    ContentsItemView contentsItemView = new ContentsItemView(this);

                    if(selectEditText == null){ //edit텍스트 선택 수정이 없을 경우
                        parent.addView(contentsItemView);
                    }else{ //있을 경우 해당 위치에 추가
                        for (int i = 0 ; i < parent.getChildCount(); i++){
                            if(parent.getChildAt(i) == selectEditText.getParent()){
                                parent.addView(contentsItemView, i+1);
                                break;
                            }
                        }
                    }


                    contentsItemView.setImage(path);
                    contentsItemView.setOnClickListener(v -> {
                        rl_post_background.setVisibility(View.VISIBLE); //이미지 뷰를 눌렀을 경우 수정 버튼 생성
                        selectImageView = (ImageView) v;
                    });
                    contentsItemView.setOnFocusChangeListener(onFocusChangeListener);


                }
                break;
            case 1: //이미지 동영상 수정
                if(resultCode == Activity.RESULT_OK){
                    String path = data.getStringExtra(INTENT_PATH);
                    pathList.set(parent.indexOfChild( (View)selectImageView.getParent()) - 1, path); //parent가 몇번째 자식인지를 알아옴
                    Glide.with(this).load(path).centerCrop().override(1000).into(selectImageView);

                }
                break;
        }
    }

    View.OnClickListener onClickListener = v -> {
        switch (v.getId()){
            case R.id.bt_post: //게시글 작성 버튼
                storageUpload();
                break;
            case R.id.bt_post_image: //이미지 업로드 버튼
                myStartActivity(GalleryActivity.class, GALLERY_IMAGE, 0);
                break;
            case R.id.bt_post_video: //동영상 업로드 버튼
                myStartActivity(GalleryActivity.class, GALLERY_VIDEO, 0);
                break;
            case R.id.rl_post_background: //이미지, 동영상 수정 버튼
                if(rl_post_background.getVisibility() == View.VISIBLE){ //수정 버튼이 보일 경우
                    rl_post_background.setVisibility(View.GONE);
                }
                break;
            case R.id.bt_edit_postimage: //이미지 수정 버튼
                myStartActivity(GalleryActivity.class, GALLERY_IMAGE, 1);
                rl_post_background.setVisibility(View.GONE);
                break;
            case R.id.bt_edit_postvideo: //동영상 수정 버튼
                myStartActivity(GalleryActivity.class, GALLERY_VIDEO, 1);
                rl_post_background.setVisibility(View.GONE);
                break;
            case R.id.bt_delete_contents: //삭제 버튼
                View selectedView = (View)selectImageView.getParent();

                StorageReference desertRef = storageRef.child("posts/" + postInfo.getPostId() + "/" + storageUrlToName(pathList.get(parent.indexOfChild(selectedView) - 1))); //Firebase Storage 경로 설정
                desertRef.delete().addOnSuccessListener(aVoid -> {
                    Log.e(TAG, "Firebase Storage Data Deleted");
                }).addOnFailureListener(exception ->showToast(WritePostActivity.this, "Storage File Delete Error"));

                pathList.remove(parent.indexOfChild(selectedView) - 1); //parent가 몇번째 자식인지를 알아옴
                parent.removeView(selectedView);
                rl_post_background.setVisibility(View.GONE);

                break;
        }
    };

    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() { //게시글 위치 확인
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus){
                selectEditText = (EditText) v;
            }
        }
    };

    private void storageUpload() {
        EditText titleFocus = findViewById(R.id.et_post_title);
        final String title = titleFocus.getText().toString();

        if (title.length() > 0) {
            loaderLayout.setVisibility(View.VISIBLE);
            final ArrayList<String> contentsList = new ArrayList<>();
            user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

            final DocumentReference documentReference = postInfo == null ? firebaseFirestore.collection("posts").document() : firebaseFirestore.collection("posts").document(postInfo.getPostId());
            final Date date = postInfo == null ? new Date() : postInfo.getCreatedAt(); //수정했을 경우 또는 게시물을 생성했을 경우

            for(int i = 0; i < parent.getChildCount(); i++){
                LinearLayout linearLayout = (LinearLayout)parent.getChildAt(i);
                for(int ii = 0; ii < linearLayout.getChildCount(); ii++){
                    View view = linearLayout.getChildAt(ii);
                    if(view instanceof EditText){
                        String text = ((EditText)view).getText().toString();
                        if(text.length() > 0){
                            contentsList.add(text);
                        }
                    } else if (!isStorageUrl(pathList.get(pathCount))) {
                        String path = pathList.get(pathCount);
                        successCount++;
                        contentsList.add(path);
                        String[] pathArray = path.split("\\.");
                        final StorageReference mountainImagesRef = storageRef.child("posts/" + documentReference.getId() + "/" + pathCount + "." + pathArray[pathArray.length - 1]);
                        try {
                            InputStream stream = new FileInputStream(new File(pathList.get(pathCount)));
                            StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("index", "" + (contentsList.size() - 1)).build();
                            UploadTask uploadTask = mountainImagesRef.putStream(stream, metadata);
                            uploadTask.addOnFailureListener(exception -> {
                            }).addOnSuccessListener(taskSnapshot -> {
                                final int index = Integer.parseInt(taskSnapshot.getMetadata().getCustomMetadata("index"));
                                mountainImagesRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                    successCount--;
                                    contentsList.set(index, uri.toString());
                                    if (successCount == 0) {
                                        PostInfo postInfo = new PostInfo(title, contentsList, user.getUid(), date);
                                        storeUpload(documentReference, postInfo);
                                    }
                                });
                            });
                        } catch (FileNotFoundException e) {
                            Log.e("로그", "에러: " + e.toString());
                        }
                        pathCount++;
                    }
                }
            }
            if(pathList.size() == 0) { //이미지가 없는 경우(글씨만 있는 경우)
                storeUpload(documentReference, new PostInfo(title, contentsList, user.getUid(), new Date()));
            }
        } else {
            showToast(WritePostActivity.this,"제목을 입력해주세요.");
            titleFocus.requestFocus();
        }
    }


    private void storeUpload(DocumentReference documentReference, PostInfo postInfo){
        documentReference.set(postInfo.getPostInfo())  //postId는 DB에 저장하지 않음
                .addOnSuccessListener(aVoid -> {
                    loaderLayout.setVisibility(View.GONE);
                    Log.d(TAG, "DocumentSnapshot successfully written!");
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error writing document", e);
                    loaderLayout.setVisibility(View.GONE);});
    }

    private void postKeep(){ //수정 시 게시물 유지
        if(postInfo != null){
            et_post_title.setText(postInfo.getTitle()); //제목 받아옴
            ArrayList<String> contentsList = postInfo.getContents(); //내용 목록 arrayList 저장

            for(int i = 0 ; i < contentsList.size(); i++){
                String contents = contentsList.get(i); //1개의 항목씩 가져옴
                if(isStorageUrl(contents)) { //URL이고 storage 경로일 경우
                    pathList.add(contents);
                    ContentsItemView contentsItemView = new ContentsItemView(this);
                    parent.addView(contentsItemView);

                    contentsItemView.setImage(contents);
                    contentsItemView.setOnClickListener(v -> {
                        rl_post_background.setVisibility(View.VISIBLE);
                        selectImageView = (ImageView) v;
                    });

                    contentsItemView.setOnFocusChangeListener(onFocusChangeListener);
                    if (i < contentsList.size() - 1) {
                        String nextContents = contentsList.get(i + 1);
                        if (!isStorageUrl(nextContents)) {
                            contentsItemView.setText(nextContents);
                        }
                    }
                }else if(i == 0){ //첫번째 EditText가 존재한다면
                    et_post_content.setText(contents);
                }
            }

        }
    }

    private void myStartActivity(Class c, int media, int requestCode){ //원하는 Activity로 이동시켜주는 메소드
        Intent intent = new Intent(this, c);
        intent.putExtra(INTENT_MEDIA, media);
        startActivityForResult(intent, requestCode);
    }
}


