package app.resta.com.restaurantapp.model;

/**
 * Created by Sriram on 24/06/2017.
 */
public enum ReviewEnum {
    VERYGOOD(4), GOOD(3), BAD(2), WORSE(1);
    private int value;

    ReviewEnum(int value) {
        this.value = value;
    }
}
