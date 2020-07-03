package models;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import util.FieldNotFoundException;

public class ScheduleList {
	public static Vector<Schedule> orders;
	
	public ScheduleList(String fileName) throws FieldNotFoundException, IOException{
		ScheduleList.orders = parseSchedule(fileName);		
	}
	
	public Vector<Schedule> parseSchedule(String fileName) throws IOException, FieldNotFoundException{
		Vector<Schedule> allorders = new Vector<Schedule>(); 
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
		while ((line = br.readLine()) != null){
        	String[] arrOfStr = line.split(", ", 0);
        	String timestring = arrOfStr[0];
        	Double time = Double.parseDouble(timestring);
        	String loc = arrOfStr[1];
        	String item = arrOfStr[2];
        	allorders.add(new Schedule(time, loc, item));
        }
		return allorders;
	}
	
	public static Vector<Schedule> getOrders(){
		return orders;
	}
}
