import java.util.Scanner;

public class Rectangle extends Shape {

    private float height;
    private float width;
    public float area() {
        return width*height;
    }

    public boolean ask_measurements(Scanner reader) {
        boolean confirmed = false;
        System.out.print("Please insert the height in cm: ");
        while (!confirmed){
            String input = reader.nextLine();
            float value;
            if (input.equals("back")) {
                return false;
            } else {
                try {
                    value = Float.parseFloat(input);
                } catch (NumberFormatException e) {
                    System.out.println("Could not understand input, please try again or go back");
                    continue;
                }
            }
            if (value < 0.0f) {
                System.out.println("Negative height not allowed, please insert another value or go back");
            } else {
                height = value;
                confirmed = true;
            }
        }

        confirmed = false;
        System.out.print("Please insert the width in cm: ");
        while (!confirmed){
            String input = reader.nextLine();
            float value;
            if (input.equals("back")){
                return false;
            } else{
                try {
                    value = Float.parseFloat(input);
                } catch (NumberFormatException e) {
                    System.out.println("Could not understand input, please try again or go back");
                    continue;
                }
            }
            if (value < 0.0f) {
                System.out.println("Negative width not allowed, please insert another value or go back");
            } else{
                width = value;
                confirmed = true;
            }
        }
        return true;
    }

    public String as_string() {
        return "Rectangle: width - " + width + "cm, height - " + height + "cm";
    }

    public static String shape_help() {
        return "A simple rectangle, the width is the the length of one of edges and the height is the length of a neighbouring edge";
    }
}
