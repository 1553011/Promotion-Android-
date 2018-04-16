package com.example.hautc.testproject.Fragment.ControlFragment;


import java.io.Serializable;

/**
 * Created by hautc on 11/14/2017.
 */

public class PromoInfo implements Serializable{
    String brand;
    String date;
    String descrip;
    String promoimage;
    String briefdescrip;
    float rating;
    double lat;
    double lng;
    String address;
    int numberofview;
    String district;
    String type;
    String code;
    String promoId;

    public PromoInfo(String brand, String date, String descrip, String promoimage, String briefdescrip, float rating, double lat, double lng, String address, int numberofview, String district, String type, String code,String promoid) {
        this.brand = brand;
        this.date = date;
        this.descrip = descrip;
        this.promoimage = promoimage;
        this.briefdescrip = briefdescrip;
        this.rating = rating;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.numberofview = numberofview;
        this.district = district;
        this.type = type;
        this.promoId=promoid;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public PromoInfo(String brand, String date, String descrip, String promoimage, String briefdescrip, float rating, double lat, double lng, String address, int numberofview, String district, String type) {
        this.brand = brand;
        this.date = date;
        this.descrip = descrip;
        this.promoimage = promoimage;
        this.briefdescrip = briefdescrip;
        this.rating = rating;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.numberofview = numberofview;
        this.district = district;
        this.type = type;
    }

    public String getPromoId() {
        return promoId;
    }

    public String getDistrict() {

        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public PromoInfo(String brand, String date, String descrip, String promoimage, String briefdescrip, float rating, double lat, double lng, String address, int numberofview) {
        this.brand = brand;
        this.date = date;
        this.descrip = descrip;
        this.promoimage = promoimage;
        this.briefdescrip = briefdescrip;
        this.rating = rating;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.numberofview = numberofview;
    }

    public PromoInfo(String brand, String date, String descrip, String promoimage, String briefdescrip) {
        this.brand = brand;
        this.date = date;
        this.descrip = descrip;
        this.promoimage = promoimage;
        this.briefdescrip = briefdescrip;
    }

    public PromoInfo() {
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescrip() {
        return descrip;
    }

    public void setDescrip(String descrip) {
        this.descrip = descrip;
    }

    public String getPromoimage() {
        return promoimage;
    }

    public void setPromoimage(String promoimage) {
        this.promoimage = promoimage;
    }

    public String getBriefdescrip() {
        return briefdescrip;
    }

    public void setBriefdescrip(String briefdescrip) {
        this.briefdescrip = briefdescrip;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getNumberofview() {
        return numberofview;
    }

    public void setNumberofview(int numberofview) {
        this.numberofview = numberofview;
    }
}
