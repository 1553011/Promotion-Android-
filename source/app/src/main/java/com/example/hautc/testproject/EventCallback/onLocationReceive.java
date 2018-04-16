package com.example.hautc.testproject.EventCallback;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by hautc on 11/27/2017.
 */

public interface onLocationReceive {
    /***
     * Duoc975 gọi khi nhận vị trí vừa mới cập nhật từ Activity cha
     * @param currentLocation vị trí hiện tại
     * @param mode chế độ load promo có 3 chế độ NEARBY, TYPE, DISTRICT
     * @param data dữ liệu nếu có, dữ liệu này chỉ áp dụng cho TYPE, DISTRICT
     */
    void onReceiveLocationFromActivity(LatLng currentLocation, int mode, String data);
}
