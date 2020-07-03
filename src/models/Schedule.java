package models;

public class Schedule {
	private double time;
	private String location;
	private String item;
	public Restaurant restaurant;

	public Schedule(double time, String location, String item) {
		this.time = time;
		this.location = location;
		this.item = item;
	}

	public double getTime() {
		return time;
	}


	public void setTime(double time) {
		this.time = time;
	}


	public String getLocation() {
		return location;
	}


	public void setLocation(String location) {
		this.location = location;
	}


	public String getItem() {
		return item;
	}


	public void setItem(String item) {
		this.item = item;
	}

}
