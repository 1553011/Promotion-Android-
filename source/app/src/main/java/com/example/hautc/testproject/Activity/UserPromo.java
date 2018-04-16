package com.example.hautc.testproject.Activity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gakon on 12/8/2017.
 */

public class UserPromo implements Serializable {
    String address;
    String code;
    String date;
    String district;
    String idPromo;
    String brand;
    String img;

    public  UserPromo(){
    }
    public UserPromo(String address, String code, String date, String district, String idPromo,String brand,String img)
    {

        this.address=address;
        this.code=code;
        this.date=date;
        this.district=district;
        this.idPromo=idPromo;
        this.img=img;
        this.brand=brand;
    }

    public String getAddress() {
        return address;
    }

    public String getCode() {
        return code;
    }

    public String getDate() {
        return date;
    }

    public String getDistrict() {
        return district;
    }

    public String getIdPromo() {
        return idPromo;
    }

    public String getBrand() {
        return brand;
    }

    public String getImg() {
        return img;
    }

    public Map<String, Object> toMap(String mail, String id) {
        mail=mail.replace('.',',');
        HashMap<String, Object> result = new HashMap<>();
        result.put("/user/" + mail+"/"+id+ "/address", this.address);
        result.put("/user/" + mail +"/"+id +  "/code", this.code);
        result.put("/user/" + mail +"/"+id +  "/date", this.date);
        result.put("/user/" + mail +"/"+id +  "/district", this.district);
        result.put("/user/" + mail +"/"+id +  "/idPromo", this.idPromo);
        result.put("/user/" + mail +"/"+id +  "/brand", this.brand);
        result.put("/user/" + mail +"/"+id +  "/img", this.img);
        return result;
    }


}
