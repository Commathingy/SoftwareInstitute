import java.util.Scanner;

public class Triangle extends Shape{
    float height;
    float width;

    public float area() {
        return height * width / 2;
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
        return "Triangle: width - " + width + "cm, height - " + height + "cm";
    }

    public static String shape_help() {
        return "A triangle defined by it's height and width. " +
                "The width should be the length of one of the edges, and the height the distance from that edge to the other corner";
    }
}
