package com.example.sns_project.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.sns_project.R;
import com.example.sns_project.adapter.PostAdapter;
import com.example.sns_project.info.PostInfo;
import com.example.sns_project.listener.OnPostListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;

import static com.example.sns_project.Util.SYSTMEM_LOG;
import static com.example.sns_project.Util.isStorageUrl;
import static com.example.sns_project.Util.showToast;
import static com.example.sns_project.Util.storageUrlToName;

public class GuideBoardActivity extends BasicActivity {
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private PostAdapter postAdapter; //게시물 Adapter
    private ArrayList<PostInfo> postList; //게시물 ArrayList
    StorageReference storageRef;
    private int storageDeleteCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_board);

        postList = new ArrayList<>(); //게시물 리스트 초기화
        postAdapter = new PostAdapter(GuideBoardActivity.this, postList); //게시물 Adapter 초기화
        postAdapter.setOnPostListener(onPostListener); //Listener 전달

        //게시글 추가 버튼
        RecyclerView recyclerView = findViewById(R.id.rv_post);
        findViewById(R.id.floatingActionButton).setOnClickListener(onClickListener);

        //recyclerView 갱신
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(GuideBoardActivity.this));
        recyclerView.setAdapter(postAdapter);
    }

    @Override
    protected void onResume() { //새로고침
        super.onResume();
        postUpdate();
    }

    OnPostListener onPostListener = new OnPostListener() { //메인 화면에서 게시물 수정 삭제시 발생되는 리스너
        @Override
        public void onDelete(int position) {
            String postId = postList.get(position).getPostId(); //postID 값 저장
            firebaseFirestore.collection("posts").document(postId) //posts 컬렉션 안에있는 게시물 위치값 가져옴
                    .delete()
                    .addOnSuccessListener(aVoid -> {  //게시글 삭제 성공
                        showToast(GuideBoardActivity.this, "삭제 완료");
                        postUpdate();
                    })
                    .addOnFailureListener(e -> showToast(GuideBoardActivity.this, "삭제 오류")); //게시글 삭제 실패

            ArrayList<String> contentsList = postList.get(position).getContents();

            for (int i = 0; i < contentsList.size(); i++) {
                String contents = contentsList.get(i);
                if (isStorageUrl(contents)) { //URL이고 storage 경로일 경우
                    storageDeleteCount++; //삭제 개수 파악

                    StorageReference desertRef = storageRef.child("posts/" + postId + "/" + storageUrlToName(contents)); //Firebase Storage 경로 설정
                    desertRef.delete().addOnSuccessListener(aVoid -> {
                        storageDeleteCount--;
                        storeUploader(postId);
                    }).addOnFailureListener(exception -> showToast(GuideBoardActivity.this, "ERROR"));

                }
            }
            storeUploader(postId);
        }

        @Override
        public void onModify(int position) {
            myStartActivity(WritePostActivity.class, postList.get(position));
        }
    };

    View.OnClickListener onClickListener = v -> {
        switch (v.getId()) {
            case R.id.floatingActionButton:
                //myStartActivity(WritePostActivity.class); //게시글 작성 화면으로 이동
                myStartActivity(ChattingActivity.class);
                break;
        }
    };

    private void postUpdate() { //게시물 갱신 메소드
        if (firebaseUser != null) { //사용자가 로그인 되었다면
            CollectionReference collectionReference = firebaseFirestore.collection("posts");
            collectionReference
                    .orderBy("createdAt", Query.Direction.DESCENDING).get() //데이터 내림차순 정렬
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            postList.clear(); //ArrayList 초기화
                            for (QueryDocumentSnapshot document : task.getResult()) { //게시글 데이터 설정
                                Log.d(SYSTMEM_LOG, document.getId() + " => " + document.getData());
                                postList.add(new PostInfo(
                                        document.getData().get("title").toString(), //게시글 제목
                                        (ArrayList<String>) document.getData().get("contents"), //게시글 내용
                                        document.getData().get("publisher").toString(), //게시글을 생성한 유저
                                        new Date(document.getDate("createdAt").getTime()), //게시글 생성일자
                                        document.getId())); //게시글 ID
                            }
                            postAdapter.notifyDataSetChanged(); //어뎁터를 통해서 데이터 갱신
                        } else {
                            Log.d(SYSTMEM_LOG, "Error getting documents: ", task.getException());
                        }
                    });
        }
    }

    private void storeUploader(String postId) { //Store 수정
        if (storageDeleteCount == 0) {
            firebaseFirestore.collection("posts").document(postId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        showToast(GuideBoardActivity.this, "게시글을 삭제하였습니다.");
                        postUpdate();
                    })
                    .addOnFailureListener(e -> showToast(GuideBoardActivity.this, "게시글을 삭제하지 못하였습니다."));
        }
    }

    private void myStartActivity(Class c) { //원하는 Activity로 이동시켜주는 메소드
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

    private void myStartActivity(Class c, PostInfo postInfo) { //원하는 Activity로 이동시켜주는 메소드
        Intent intent = new Intent(this, c);
        intent.putExtra("postInfo", postInfo);
        startActivity(intent);
    }
}
