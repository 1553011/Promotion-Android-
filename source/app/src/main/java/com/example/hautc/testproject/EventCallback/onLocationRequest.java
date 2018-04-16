package com.example.hautc.testproject.EventCallback;

/**
 * Created by hautc on 11/27/2017.
 */

public interface onLocationRequest {
    /***
     * Gửi thông báo xin cấp vị trí cho Activity
     * @param sender người gửi
     */
    void onReceiveLocationRequestFromFragment(String sender);
}
