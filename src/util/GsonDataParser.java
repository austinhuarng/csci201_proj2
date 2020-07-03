package util;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
import models.Restaurant;
import models.RestaurantList;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class GsonDataParser {
    private static Gson gson = new Gson();

    public static ArrayList<Restaurant> getRestaurantsFromJson(String fileName) throws IOException,
            FieldNotFoundException, ClassCastException, JsonSyntaxException {

        // This is a sort of roundabout way of doing things since this GSON usage was added
        // after the Simple.JSON implementation
        RestaurantList rl = gson.fromJson(new FileReader(fileName), RestaurantList.class);
        validateFields(rl.getRestaurants());
        return rl.getRestaurants();
    }

    /**
     * @param restaurants Arraylist of Restaurant objects
     * @throws FieldNotFoundException Indicates that a field is missing, probably since it was parsed wrong
     */
    private static void validateFields(ArrayList<Restaurant> restaurants) throws FieldNotFoundException {
        for (Restaurant restaurant: restaurants) {
            if(restaurant.getName() == null || restaurant.getMenu() == null || restaurant.getAddress() == null ||
            restaurant.getLatitude() == null || restaurant.getLongitude() == null) {
                throw new FieldNotFoundException("Missing data parameters");
            }
        }
    }
}
