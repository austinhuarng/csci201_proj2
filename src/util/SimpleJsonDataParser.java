package util;

import models.Restaurant;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * DataParser class to parse the JSON file using Simple.JSON or GSON
 *
 * Although there are way more steps with Simple.JSON, it gives us a more fine-tuned control
 * over our parsing so we can do things we can't do in GSON
 **/
public class SimpleJsonDataParser {
    // Initialize our JSON parser (using JSON.simple)
    private static JSONParser parser = new JSONParser();

    public static ArrayList<Restaurant> getRestaurantsFromJson(String fileName) throws IOException, ParseException,
            FieldNotFoundException, ClassCastException {
        // Initialize our output array
        ArrayList<Restaurant> restaurants = new ArrayList<>();

        // Read in file (Can throw IOException)
        FileReader fileReader = new FileReader(fileName);

        // Attempt to convert file to JSON object (Can throw ParseException)
        JSONObject data = (JSONObject) parser.parse(fileReader);

        // Throw exception if the "data" key doesn't exist
        if (!data.containsKey("data")) throw new FieldNotFoundException("Missing data parameters");

        // Parse all the restaurants in the JSON Array
        JSONArray restaurantsJson = (JSONArray) data.get("data");
        for (Object restaurantJson: restaurantsJson) {
            Restaurant restaurant = parseRestaurant((JSONObject) restaurantJson);
            restaurants.add(restaurant);
        }

        return restaurants;
    }

    private static Restaurant parseRestaurant(JSONObject json) throws FieldNotFoundException, ClassCastException {
        // Check that all the fields exist (Can throw a FieldNotFoundException)
        validateFields(json);

        // Get field value (Each cast can throw a ClassCastException which tells us that the data was malformed)
        String name = (String) json.get("name");
        String address = (String) json.get("address");
        double latitude = (Double) json.get("latitude");
        double longitude = (Double) json.get("longitude");
        int drivers = (Integer)json.get("drivers");

        // Parse JSONArray of menu items and create a String Array of menu items
        ArrayList<String> menu = new ArrayList<>();
        JSONArray menuJson = (JSONArray) json.get("menu");
        for (Object object : menuJson) {
            menu.add((String) object);
        }


        return new Restaurant(name, address, latitude, longitude, 0.0, menu);
    }

    private static void validateFields (JSONObject json) throws FieldNotFoundException {
        if(!json.containsKey("name") || !json.containsKey("address") || !json.containsKey("latitude")
                || !json.containsKey("longitude") || !json.containsKey("menu")) {
            throw new FieldNotFoundException("Missing data parameters");
        }
    }
}
