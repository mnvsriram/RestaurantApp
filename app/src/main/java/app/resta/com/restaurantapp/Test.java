package app.resta.com.restaurantapp;

/**
 * Created by Sriram on 19/01/2017.
 */
public class Test {

    public static void main(String[] args) {
        int a = 0xFF000000;

        try {
            System.out.println(Integer.parseInt("FFFFFF", 16));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
