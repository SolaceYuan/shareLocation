package cn.bean;



public class UserBean {
    private String uuid;
    private double lat;
    private double lng;
    public boolean isLogged;

    public void setLogged(boolean isLogged){
        this.isLogged=isLogged;
    }
    public  boolean isLogged(){
        return isLogged;
    }

    public String getUuid() {
        return uuid;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "uuid='" + uuid + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}
