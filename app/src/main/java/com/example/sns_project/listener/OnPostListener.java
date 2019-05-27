package com.example.sns_project.listener;
//게시물 관리 리스너
public interface OnPostListener {
    void onDelete(int position); //삭제 기능
    void onModify(int position); //수정 기능
}
