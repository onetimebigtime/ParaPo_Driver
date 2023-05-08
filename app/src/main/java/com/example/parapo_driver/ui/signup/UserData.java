package com.example.parapo_driver.ui.signup;

public class UserData {
    public int seat_1, seat_2, seat_3, seat_4, seat_5, seat_6;
    public double latitude, longitude;
    public String user_id ,full_name, plate_number, route;

    public boolean is_online;

    public UserData(){

    }

    public UserData(String user_id, String full_name, String plate_number,String route, int seat_1, int seat_2, int seat_3, int seat_4, int seat_5, int seat_6, double latitude, double longitude, boolean is_online) {
        this.user_id = user_id;
        this.full_name = full_name;
        this.plate_number = plate_number;
        this.route = route;
        this.seat_1 = seat_1;
        this.seat_2 = seat_2;
        this.seat_3 = seat_3;
        this.seat_4 = seat_4;
        this.seat_5 = seat_5;
        this.seat_6 = seat_6;
        this.latitude = latitude;
        this.longitude = longitude;
        this.is_online = is_online;
    }
}
