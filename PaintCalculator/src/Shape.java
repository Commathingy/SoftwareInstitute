import java.util.Scanner;

public abstract class Shape {
    public abstract float area();

    public abstract boolean ask_measurements(Scanner reader);

    public abstract String as_string();
}
