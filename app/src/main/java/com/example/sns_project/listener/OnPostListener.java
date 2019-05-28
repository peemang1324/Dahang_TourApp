package com.example.sns_project.listener;
//게시물 관리 리스너
public interface OnPostListener {
    void onDelete(int position);
    void onModify(int position);
}

