import java.util.Scanner;

public class Sector extends Shape {

    private float radius;
    private float angle;

    public float area() {
        return (float) Math.PI * radius * radius * (angle / 360.0f);
    }

    public boolean ask_measurements(Scanner reader) {
        boolean confirmed = false;
        System.out.print("Please insert the radius in cm: ");
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
                System.out.println("Negative radius not allowed, please insert another value or go back");
            } else {
                radius = value;
                confirmed = true;
            }
        }

        confirmed = false;
        System.out.print("Please insert the angle in degrees: ");
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
            if ((value < 0.0f) || (value > 360.0f)) {
                System.out.println("Angle must be between 0 and 360 degrees, please insert another value or go back");
            } else{
                angle = value;
                confirmed = true;
            }
        }
        return true;
    }

    public String as_string() {
        return "Sector: radius - " + radius + "cm, angle - " + angle + " degrees";
    }

    public static String shape_help() {
        return "A sector of a circle (ice-cream cone shape). This can be used to create a circle by choosing an angle of 360.";
    }
}