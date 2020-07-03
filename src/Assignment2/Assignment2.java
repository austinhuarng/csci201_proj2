package Assignment2;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import comparators.AlphabeticalComparator;
import comparators.DistanceComparator;
import models.Orders;
import models.Restaurant;
import models.RestaurantList;
import models.Schedule;
import models.ScheduleList;
import util.FieldNotFoundException;

public class Assignment1 {
    private static Scanner scan;
    private static String fileName;
    public static RestaurantList restaurants;
    private static ScheduleList orders;
    public static Vector<Schedule> ordersPending;	
    private static Double userLatitude;
    private static Double userLongitude;
    private static boolean hasEdits = false;
	

    public static void main(String[] args) {
        scan = new Scanner(System.in);
        loadRestaurantFile();
        while(true){
        	try {
                loadScheduleFile();
                for(Schedule sch : ScheduleList.getOrders()){
                	for(Restaurant res : restaurants.getRestaurants()){
                		if(res.getName().equalsIgnoreCase(sch.getLocation())){
                			sch.restaurant = res;
                		}
                	}
                }

                for(Schedule schedule : ScheduleList.getOrders()){
                	if(!restaurants.getRestaurants().contains(schedule.restaurant)){
                		throw new NumberFormatException();
                	}
                }
                break;
			} catch (ArrayIndexOutOfBoundsException e) {
        		System.out.println("Missing data parameters.");
			} catch(NumberFormatException e){
				System.out.println("File has improper data format");
			}
        }
        getUserLocation();
        loadDistances();


        for(int i=0; i<restaurants.getRestaurants().size(); i++){
        	restaurants.getRestaurants().get(i).setSem(restaurants.getRestaurants().get(i).getDrivers());

        }
        System.out.println("Starting execution of program...");
        ExecutorService executors = Executors.newCachedThreadPool();
        for(int i=0; i<ScheduleList.getOrders().size(); i++){
        	Orders neworder = new Orders(ScheduleList.getOrders().get(i));
        	executors.execute(neworder);
        }
        executors.shutdown();
		while(!executors.isTerminated()) {
			Thread.yield();
		}
		System.out.println("All orders complete!");
        
//        while (true) {
//            mainMenu();
//        }
    }

    private static void loadRestaurantFile() {
        boolean validFile = false;
        do {
            System.out.println("What is the name of the restaurant file?");
            fileName = scan.nextLine();

            try {
                restaurants = new RestaurantList(fileName);
                System.out.println("The file has been properly read");
                validFile = true;
            } catch (IOException e) {
                System.out.println("The file " + fileName + " could not be found.");
            } catch (ParseException e) {
                System.out.println("Cannot parse file.");
            } catch (FieldNotFoundException e) {
                System.out.println("Missing data parameters.");
            } catch (ClassCastException | JsonSyntaxException e){
                System.out.println("Data is malformed.");
            }
        }
        while (!validFile);
    }
    
    private static void loadScheduleFile() {
        boolean validFile = false;
        do {
            System.out.println("What is the name of the schedule file?");
            fileName = scan.nextLine();
            try {
                orders = new ScheduleList(fileName);
                System.out.println("The file has been properly read");
                validFile = true;
            } catch (IOException e) {
                System.out.println("The file " + fileName + " could not be found.");
            } catch (FieldNotFoundException e) {
                System.out.println("Missing data parameters.");
            }
        }
        while (!validFile);
    }
    
    private static void getUserLocation() {
        userLatitude = getLatitude("What is your latitude?");
        userLongitude = getLongitude("What is your longitude?");
    }
    
    private static void loadDistances() {
    	for (Restaurant r : restaurants.getRestaurants()) {
    		r.setDistance(calculateDistance(r.getLatitude(), r.getLongitude()));
    	}
    }

    private static double getLatitude(String query) {
        double latitude = 0.0;
        while (true) {
            System.out.println(query);
            String latitudeString = scan.nextLine();
            try {
                latitude = Double.parseDouble(latitudeString);
                if (latitude < -90.0 || latitude > 90.0) {
                	throw new NumberFormatException();
                }
                return latitude;
            } catch (NumberFormatException ignore) { }
        }
    }

    private static double getLongitude(String query) {
        double longitude = 0.0;
        while (true) {
            System.out.println(query);
            String longitudeString = scan.nextLine();
            try {
                longitude = Double.parseDouble(longitudeString);
                if (longitude < -180.0 || longitude > 180.0) {
                	throw new NumberFormatException();
                }
                return longitude;
            } catch (NumberFormatException ignore) { }
        }
    }

    private static void mainMenu() {
        String menu = "1) Display all restaurants" + "\n" +
                "2) Search for a restaurant" + "\n" +
                "3) Search for a menu item " + "\n" +
                "4) Add a new restaurant" + "\n" +
                "5) Remove a restaurant" + "\n" +
                "6) Sort restaurants" + "\n" +
                "7) Exit" + "\n" +
                "What would you like to do?";

        while (true) {
            int selection = getSelection(menu, 1, 7);
            switch (selection) {
                case 1:
                    displayRestaurants(restaurants.getRestaurants());
                    break;
                case 2:
                    searchRestaurant();
                    break;
                case 3:
                    searchMenuItem();
                    break;
                case 4:
                    addRestaurant();
                    break;
                case 5:
                    removeRestaurant();
                    break;
                case 6:
                    sortRestaurants();
                    break;
                case 7:
                    exit();
                    break;
            }
        }
    }

    private static void exit() {
        if (hasEdits) {
            String menu = "1) Yes\n" + "2) No\n" + "Would you like to save your edits?";
            int selection = getSelection(menu, 1, 2);
            if (selection == 1) {
                // Save edits
            	Gson gson = new GsonBuilder().setPrettyPrinting().create();
            	String fileOutput = gson.toJson(restaurants);
            	FileWriter fw = null;
            	PrintWriter pw = null;
            	
            	try {
            		fw = new FileWriter(fileName);
                    pw = new PrintWriter(fw);
                    pw.print(fileOutput);
            	} catch (IOException ioe) {
            		ioe.printStackTrace();
            	} finally {
            		try {
						fw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
            		pw.close();
            	}

                System.out.println("Your edits have been saved to " + fileName);

            	// Note: To save a file using Simple.JSON use the following snippet where applicable
                //
                // try (FileWriter file = new FileWriter(fileName)) {
                //      file.write(restaurants.toJson().toJSONString());
                //  } catch (IOException e) {
                //      e.printStackTrace();
                //  }
            }
        }
        // Exit
        System.out.println("Thank you for using my program!");
        System.exit(0);
    }

    private static void addRestaurant() {
    	
    	boolean invalidName = true;
    	String name = null;
    	
    	while(invalidName) {
    		System.out.println("What is the name of the restaurant you would like to add?");
            name = scan.nextLine();
            if (restaurants.getRestaurant(name) != null) {
            	System.out.println("There is already an entry for " + name + ".");
            } else {
            	invalidName = false;
            }
    	}
        
    	System.out.println("What is the address for " + name + "?");
        String address = scan.nextLine();
        
        double latitude = getLatitude("What is the latitude for " + name + "?");
        double longitude = getLongitude("What is the longitude for " + name + "?");
        
        ArrayList<String> menu = getMenu(name);
        
        double dist = calculateDistance(latitude, longitude);
        
        // ADD THE NEW RESTAURANT AND PRINT OUT DETAILS
        restaurants.getRestaurants().add(new Restaurant(name, address, latitude, longitude, dist, menu));
        
        String displayDist = String.format("%.1f", dist);
        
        hasEdits = true;
        
        System.out.println("There is now a new entry for:");
        System.out.println(name + ", located " + displayDist + " miles away at " + address);
        
        // Menu Output Formatting as follows
        // 1 Item : RestaurantName serves itemname.
        // 2 Items : RestaurantName serves itemname1 and itemname2.
        // 3 Items : RestaurantName serves itemname1, itemname2, and itemname3.
        // 4+ Items : RestaurantName serves itemname1, itemname2, ... , itemnameN-1, and itemnameN.
        
        if (menu.size() > 1) {
        	String menuString = name + " serves";
        	
        	for (int i = 0; i < menu.size() - 1; i++) {
        		menuString += " " + menu.get(i) + ",";
        	}
        	
        	if (menu.size() == 2) {
        		menuString = menuString.substring(0, menuString.length() - 1);
        	}
        	
        	menuString += " and " + menu.get(menu.size() - 1) + ".";
        	
            System.out.println(menuString);
        } else {
        	System.out.println(name + " serves " + menu.get(0) + ".");
        }
        
    }
    
    
    
    private static ArrayList<String> getMenu(String name) {
    	
    	ArrayList<String> menu = new ArrayList<String>();
    	boolean moreMenuItems = true;
    	
    	while(moreMenuItems) {
    		System.out.print("What does " + name + " serve? ");
        	String item = scan.nextLine();
        	
        	menu.add(item);
        	
        	System.out.println("\n\t1) Yes\n\t2) No");
        	System.out.print("Does " + name + " serve anything else? ");
        	
        	String response = scan.nextLine();
        	
        	int selection = Integer.parseInt(response);
        	
        	if (selection == 2) {
        		moreMenuItems = false;
        	}
    	}
    	
    	return menu;
    }
    
    private static void removeRestaurant() {

    	System.out.println();
		
		int i = 1;
		for (Restaurant r : restaurants.getRestaurants()) {
			System.out.println("\t" + i + ") " + r.getName());
			i++;
		}
		
		System.out.print("Which restaurant would you like to remove?");
        String selection = scan.nextLine();
        // ERROR CHECK THIS INPUT
        int toRemove = Integer.parseInt(selection);

        String name = restaurants.getRestaurants().get(toRemove-1).getName();
        
        restaurants.getRestaurants().remove(toRemove-1);
        
        System.out.println("\n" + name + " is now removed.\n");
        
        hasEdits = true;
    	
    }

    private static void searchMenuItem() {
 
        System.out.println("What menu item would you like to search for?");
        String query = scan.nextLine();
        
        ArrayList<String> results = restaurants.getRestaurantsWithItem(query);

        if (results.size() > 0) {
            for (String r : results) {
            	System.out.println(r);
            }
        }
        else{
            System.out.println("No restaurant nearby serves " + query + ".");
        }
        
        
    }

    private static void searchRestaurant() {
    	
        System.out.println("What is the name of the restaurant you'd like to search for?");
        String query = scan.nextLine();
        ArrayList<Restaurant> results = restaurants.searchRestaurants(query);

        if (results.size() > 0) {
            displayRestaurants(results);
        }
        else{
            System.out.println(query + " could not be found.");
        }
    }

    /**
     * Prints out restaurants from input to user in a readable format
     * Eg: Restaurant x, located y miles away at z.
     *
     * @param restaurants ArrayList of Restaurants
     */
    private static void displayRestaurants(ArrayList<Restaurant> restaurants) {
        for (Restaurant r: restaurants) {
        	//format the miles
        	double dist = calculateDistance(r.getLatitude(), r.getLongitude());
        	String displayDist = String.format("%.1f", dist);
        	System.out.println(r.getName() + ", located " + displayDist + " mile(s) away at " + r.getAddress());
        }
    }

    /**
     * Prints menu and grabs a valid selection given the menu text and the selection range
     * @param menu Menu string
     * @param min  Minimum selection value
     * @param max  Maximum selection value
     * @return The users selection as an integer
     */
    private static int getSelection(String menu, int min, int max) {
        // Initialize our return value
        int selection = -1;
        boolean validSelection = false;

        // Display the menu to the user
        System.out.println(menu);

        do {
            // Get user selection and check if it's valid
            try {
                String selectionString = scan.nextLine(); // Get selection as a string
                selection = Integer.parseInt(selectionString); // Attempt to parse this to an int
                validSelection = selection >= min && selection <= max; // Validate the selection
            }
            // If parsing the selection to an int failed, we do nothing so the loop repeats
            catch (NumberFormatException ignore) { }
            // Before we potentially loop, we want to alter the user if their option was invalid
            finally {
                if (!validSelection) {
                    System.out.println("That is not a valid option.");
                }
            }
        } while(!validSelection);

        return selection;
    }
    
    private static void sortRestaurants() {
    		
    	System.out.println("\n\t1) A to Z\n\t2) Z to A\n\t3) Closest to farthest\n\t4) Farthest to closest");
    	System.out.print("How would you like to sort by? ");
    	
    	String selection = scan.nextLine();
    	int choice = Integer.parseInt(selection);
    	
    	switch(choice) {
    	case 1:
    		restaurants.getRestaurants().sort(new AlphabeticalComparator());
    		System.out.println("Your restaurants are now sorted from A to Z.");
    		break;
    	case 2:
    		restaurants.getRestaurants().sort(Collections.reverseOrder(new AlphabeticalComparator()));
    		System.out.println("Your restaurants are now sorted from Z to A.");
    		break;
    	case 3:
    		restaurants.getRestaurants().sort(new DistanceComparator());
    		System.out.println("Your restaurants are now sorted from closest to farthest.");
    		break;
    	case 4:
    		restaurants.getRestaurants().sort(Collections.reverseOrder(new DistanceComparator()));
    		System.out.println("Your restaurants are now sorted from farthest to closest.");
    		break;
    	default:
    		break;
    	}
    	
    	hasEdits = true;
    }


    /**
     * Calculates distance from user to the specified coordinates
     *
     * @param latitude Latitude of restaurant
     * @param longitude Longitude of restaurant
     * @return Distance from user to restaurant
     */
    private static double calculateDistance(double latitude, double longitude) {
    	return 3963.0 * Math.acos((Math.sin(Math.toRadians(userLatitude)) * Math.sin(Math.toRadians(latitude))) + Math.cos(Math.toRadians(userLatitude))
        * Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(userLongitude) - Math.toRadians(longitude)));
    }

}
