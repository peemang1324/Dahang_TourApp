package com.example.sns_project.fragment;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sns_project.activity.GalleryActivity;
import com.example.sns_project.activity.PasswordReset;
import com.example.sns_project.activity.WritePostActivity;
import com.example.sns_project.info.MemberInfo;
import com.example.sns_project.info.User;
import com.example.sns_project.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ObjectStreamException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.example.sns_project.Util.showToast;
import static com.example.sns_project.Util.storageUrlToName;

public class ProfileFragment extends Fragment {

    private CircleImageView image_profile;
    private TextView username;

    private DatabaseReference reference;
    private FirebaseUser fuser;

    private StorageReference storageReference;

    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        image_profile = view.findViewById(R.id.profile_image);
        username = view.findViewById(R.id.username);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                if (user.getImageURL().equals("defalut")){
                    image_profile.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(getContext()).load(user.getImageURL()).centerCrop().into(image_profile);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        image_profile.setOnClickListener(v -> openImage());

        return view;
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private void uploadImage(){
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Uploading");
        pd.show();

        if (imageUri != null){




            /*Storage 프로필 이미지 삭제*/
            StorageReference desertRef = storageReference.child("users/" + fuser.getUid() + "/profileImage.jpg"); //Firebase Storage 삭제 경로 설정
            desertRef.delete().addOnSuccessListener(aVoid -> {
            }).addOnFailureListener(exception -> Toast.makeText(getContext(), "스토리지 삭제 실패.", Toast.LENGTH_SHORT).show());

            /*Storage 프로필 이미지 추가*/
            final StorageReference fileReference = storageReference.child("users/" + fuser.getUid() + "/profileImage.jpg");

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask((Continuation<UploadTask.TaskSnapshot, Task<Uri>>) task -> {
                if (!task.isSuccessful()){
                    throw task.getException();
                }
                return fileReference.getDownloadUrl();
            }) .addOnCompleteListener((OnCompleteListener<Uri>) task -> {
                if (task.isSuccessful()){
                    Uri downloadUri = task.getResult();
                    String mUri = downloadUri.toString();

                    /*realtime DB update*/
                    reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("imageURL", mUri);
                    reference.updateChildren(map);


                    /*firestore DB update*/
                    MemberInfo memberInfo = new MemberInfo(downloadUri.toString());
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("users").document(fuser.getUid()).set(memberInfo)
                            .addOnSuccessListener(aVoid ->{
                                //Toast.makeText(getContext(), "프로필 사진을 수정했습니다.", Toast.LENGTH_SHORT).show();
                                    })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "프로필 사진을 수정 실패.", Toast.LENGTH_SHORT).show();
                                    });

                    pd.dismiss();
                } else {
                    Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            });
        } else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(getContext(), "Upload in Progress", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        }
    }

}

