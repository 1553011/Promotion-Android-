package com.example.hautc.testproject.Fragment.User;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gakon on 12/23/2017.
 */

public class Comment implements Serializable {
    String avatar;
    String content;
    int mark;
    String name;

    Comment(){}
    public Comment(String a,String c,int m,String n){
        avatar=a;
        content=c;
        mark=m;
        name=n;
    }
    public String getAvatar() {
        return avatar;
    }

    public String getContent() {
        return content;
    }

    public int getMark() {
        return mark;
    }

    public String getName() {
        return name;
    }
    public Map<String, Object> toMap(String mail,String idPromo) {
        mail=mail.replace('.',',');
        HashMap<String, Object> result = new HashMap<>();
        result.put("/comment/"+ idPromo+"/"+mail+ "/avatar", this.avatar);
        result.put("/comment/"+ idPromo+"/"+mail+ "/content", this.content);
        result.put("/comment/"+ idPromo+"/"+mail+ "/mark", this.mark);
        result.put("/comment/"+ idPromo+"/"+mail+ "/name", this.name);
        return result;
    }
}
