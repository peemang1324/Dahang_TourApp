package com.example.sns_project;

import android.app.Activity;
import android.util.Patterns;
import android.widget.Toast;

public class Util {
    public Util(){
        /* */
    }

    public static final String INTENT_PATH = "path";
    public static final String INTENT_MEDIA = "path";
    public static final String SYSTMEM_LOG = "system_lohg";
    public static final int GALLERY_IMAGE = 0;
    public static final int GALLERY_VIDEO = 1;


    public static void showToast(Activity activity, String msg){ //토스트창 출력
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }


    public static boolean isStorageUrl(String url){ //Firebase StorageURL
        return Patterns.WEB_URL.matcher(url).matches() || url.contains("https://firebasestorage.googleapis.com/v0/b/sns-project-1de99.appspot.com/o/posts");
    }

    public static String storageUrlToName(String url){
        // ? 기준으로 한번 나누고 저장한 뒤 @2F 뒤에있는 ? 부터 제목 끝에있는 ?까지 저장 (파일 이름 저장)
        return url.split("\\?")[0].split("%2F")[url.split("\\?")[0].split("%2F").length - 1];
    }
}
