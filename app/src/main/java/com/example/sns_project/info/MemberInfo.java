package com.example.sns_project.info;


public class MemberInfo {
    private String name; //이름
    private String Birthday; //생년월일
    private String phone_number; //전화번호
    private String address; //주소
    private String profile_photoUrl; //프로필 사진 url

    /* Constructor */
    public MemberInfo(String name, String birthday, String phone_number, String address, String profile_photoUrl) {
        this.name = name;
        this.Birthday = birthday;
        this.phone_number = phone_number;
        this.address = address;
        this.profile_photoUrl = profile_photoUrl;
    }
    public MemberInfo(String name, String birthday, String phone_number, String address) {
        this.name = name;
        this.Birthday = birthday;
        this.phone_number = phone_number;
        this.address = address;
    }


    public MemberInfo(String profile_photoUrl) {
        this.profile_photoUrl = profile_photoUrl;
    }

    public MemberInfo() {

    }

    /* Getter & Setter */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthday() {
        return Birthday;
    }

    public void setBirthday(String birthday) {
        Birthday = birthday;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getprofile_photoUrl() {
        return profile_photoUrl;
    }

    public void setprofile_photoUrl(String photoUrl) {
        this.profile_photoUrl = profile_photoUrl;
    }
}
