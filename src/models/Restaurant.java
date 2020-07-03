package models;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Restaurant {
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private transient double distance;
    private int drivers;
    private ArrayList<String> menu;
    public transient Semaphore semaphore;

    public Restaurant(String name, String address, double latitude, double longitude, double distance, ArrayList<String> menu) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.menu = menu;
        this.distance = distance;
        //semaphore = new Semaphore(drivers);
    }

    public String getName() {
        return name;
    }
    public void setSem(int permits){
    	this.semaphore = new Semaphore(permits);
    }

    public String getAddress() {
        return address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
    
    public int getDrivers() {
		return drivers;
	}
	
	public void setDrivers(int drivers) {
		this.drivers = drivers;
	}

    public ArrayList<String> getMenu() {
        return menu;
    }
    
	public double getDistance() {
		return distance;
	}
	
	public void setDistance(double distance) {
		this.distance = distance;
	}

    @SuppressWarnings("unchecked")
    public JSONObject toJson() {
        // Initialize new JSONObject
        JSONObject jsonObject = new JSONObject();

        // Add String array to JSONArray
        JSONArray jsonArray = new JSONArray();
        jsonArray.addAll(this.menu);

        // Insert components into jsonObject
        jsonObject.put("name", this.name);
        jsonObject.put("address", this.address);
        jsonObject.put("latitude", this.latitude);
        jsonObject.put("longitude", this.longitude);
        jsonObject.put("menu", jsonArray);

        return jsonObject;
    }

}
