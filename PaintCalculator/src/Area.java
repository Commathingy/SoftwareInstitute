import java.util.Scanner;

public class Area extends Shape {
    private float area;
    public float area() {
        return area;
    }

    public boolean ask_measurements(Scanner reader) {
        boolean confirmed = false;
        boolean successful = false;
        System.out.print("Please insert the known area: ");
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
                System.out.println("Negative area not allowed, please insert another value or go back");
            } else{
                area = value;
                successful = true;
                confirmed = true;
            }
        }
        return successful;
    }


    public String as_string() {
        return "Area: " + area + "cm^2";
    }

    public static String shape_help() {
        return "A known area. Allows inputting of any areas that are already known, but may not be able to be obtained from one of the other shape options";
    }
}