package com.example.sns_project.info;


public class MemberInfo {
    private String name; //이름
    private String age; //나이
    private  String gender; //성별
    private String Birthday; //생년월일
    private String phone_number; //전화번호
    private String address; //주소
    private String profile_photoUrl; //프로필 사진 url
    private String tour; //사용자 유형

    /* Constructor */
    public MemberInfo(String name, String age, String gender,  String birthday, String phone_number, String address, String tour, String profile_photoUrl) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.Birthday = birthday;
        this.phone_number = phone_number;
        this.address = address;
        this.tour = tour;
        this.profile_photoUrl = profile_photoUrl;
    }
    public MemberInfo(String name, String age, String gender, String birthday, String phone_number, String address, String tour) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.Birthday = birthday;
        this.phone_number = phone_number;
        this.address = address;
        this.tour = tour;
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

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public String getProfile_photoUrl() {
        return profile_photoUrl;
    }

    public void setProfile_photoUrl(String profile_photoUrl) {
        this.profile_photoUrl = profile_photoUrl;
    }

    public String getTour() {
        return tour;
    }

    public void setTour(String tour) {
        this.tour = tour;
    }
}
