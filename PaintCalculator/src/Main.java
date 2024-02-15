import java.util.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private final static String default_help = """
            help - lists the commands and a basic description of each, type help command-name for more detail on that command. help help gives more information about this calculator.
            undo - deletes the last added/subtracted section of wall
            redo - undo the action performed by an undo action
            add - allows adding a section of wall to be painted
            subtract - allows removing a section of wall to be painted
            current - gives information on the calculation so far
            finish - finish adding/subtracting wall sections and get the final result
            """;

    private static String get_help_on(String item){
        return switch (item) {
            case "triangle" -> Triangle.shape_help();
            case "area" -> Area.shape_help();
            case "rectangle" -> Rectangle.shape_help();
            case "sector" -> Sector.shape_help();
            case "help" -> """
                    This is a calculator to calculate the amount of paint needed for painting a room.
                    It allows adding sections of wall of varying shapes, as well as removing sections that correspond to doors, windows etc.
                    This can be done using the add or subtract commands. When you are done, use the finish command to get the final amount of paint needed.""";
            case "undo" -> """
                    Removes the most recently added/subtracted section of wall. If there is none to remove, does nothing.
                    This can be reversed by redo, unless a wall section is added/subtracted after the undo, but before the redo.
                    Can be done repeatedly to remove multiple sections, which can also then be redone multiple times""";
            case "redo" -> """
                    Re-adds the last section of wall removed by undo.
                    Will do nothing if there has been any section added/subtracted since the last undo""";
            case "current" -> """
                    Will display the current number of sections involved in the calculation and the total area so far.""";
            case "add" -> """
                    Used to add a single section of wall as an area to be painted.
                    The possible choices of shape of wall section are Rectangle, Segment, Triangle and a known area.
                    Can either be used on its own to be given more prompts, or directly as "add shape-name" to go directly to filling in info for the shape""";
            case "subtract" -> """
                    Similar to add, but used to remove a section of wall from the area calculation, for example for a door.
                    eg. If a section of wall with size 150cm x 220cm contains a door of size 100cm * 200cm,
                    then you can first "add" that wall as a single rectangle, then "subtract" the door with a single rectangle.
                    Can also be used directly with the name of the shape as with add""";
            case "finish" -> """
                    Finishes the area calculations and return the final result on the amount of paint required and cost.
                    This can also be used to reset the calculation.""";
            default -> "Could not find any command or shape with the provided name";
        };
    }


    private static Optional<Shape> try_create_shape(Scanner reader, String shape_name){

        Shape wall_shape = null;

        switch (shape_name){
            case "1", "rectangle": {
                wall_shape = new Rectangle();
                break;
            }
            case "2", "triangle": {
                wall_shape = new Triangle();
                break;
            }
            case "3", "sector": {
                wall_shape = new Sector();
                break;
            }
            case "4", "area": {
                wall_shape = new Area();
                break;
            }
            default: {
                System.out.println("Not a valid shape name, please try again");
                return Optional.empty();
            }
        }

        if (wall_shape.ask_measurements(reader)){
            return Optional.of(wall_shape);
        } else {
            System.out.println("Not all information about shape added TODO");
            return Optional.empty();
        }
    }


    private static void add_section(boolean is_positive, Stack<WallSection> sections, Stack<WallSection> redo_queue, Scanner reader){
        System.out.print("You have chosen to " + ((is_positive) ? "add" : "remove") + " a section of wall. ");
        System.out.println("If this is incorrect, or you want to go back, type back and press the enter key");

        System.out.println("Please choose the shape of the wall section by writing its name or number and pressing the enter key.");
        System.out.println("For more information about a particular shape, type \"help name-of-shape\" without the quotation marks and press the enter key");
        System.out.println("1. Rectangle");
        System.out.println("2. Triangle");
        System.out.println("3. Sector");
        System.out.println("4. Area\n");

        boolean valid_input = false;

        while (!valid_input){

            System.out.print("Enter shape: ");

            String input = reader.nextLine().toLowerCase();
            String[] parts = input.split(" ");
            if (input.equals("back")) {
                    return;
            }
            else if (parts[0].equals("help")) {
                if (parts.length > 1) {
                    System.out.println(get_help_on(parts[1]));
                } else {
                    System.out.println("To get help about a particular shape, type help followed by the name of the shape and press the enter key");
                }
            }
            else {
                Optional<Shape> result = try_create_shape(reader, input);
                if (result.isPresent()){
                    valid_input = true;
                    System.out.println("Section of wall added successfully! Add/subtract more sections of wall or finish.");
                    redo_queue.clear();
                    sections.push(new WallSection(is_positive, result.get()));
                }
            }
        }
    }


    private static boolean run(){
        Scanner reader = new Scanner(System.in);
        boolean finished = false;
        boolean should_repeat = false;
        Stack<WallSection> sections = new Stack<>();
        Stack<WallSection> redo_queue = new Stack<>();

        System.out.println("Welcome to the Paint-Area Calculator \nTo begin, please type one of the following commands and press the enter key:\n");
        System.out.println(default_help);

        while (!finished) {
            System.out.print("Enter command: ");
            //get the input and lower case-ify it
            String input = reader.nextLine().trim().toLowerCase();

            //split into command and (optional) command modifier
            String[] parts = input.split(" ");
            String command = parts[0];
            Optional<String> modifier = Optional.empty();
            if (parts.length > 1) {
                modifier = Optional.of(parts[1]);
            }

            switch (command) {

                case "current": {
                    System.out.println("Number of wall sections: " + sections.size());
                    float total_area = 0.0f;
                    for (WallSection section : sections){
                        total_area += section.signed_area();
                    }
                    System.out.printf("Total area so far:  %.2f cm^2\n", total_area);
                }

                case "help": {
                    if (modifier.isPresent()){
                        System.out.println(get_help_on(modifier.get()));
                    } else {
                        System.out.println(default_help);
                    }
                    break;
                }

                case "undo": {
                    try {
                        WallSection removed = sections.pop();
                        redo_queue.push(removed);
                        System.out.println("Undid creation of wall section :: " +removed.shape_details());
                    } catch (EmptyStackException e){
                        System.out.println("There was nothing to undo");
                    }
                    break;
                }

                case "redo": {
                    try {
                        WallSection add_back = redo_queue.pop();
                        sections.push(add_back);
                        System.out.println("Redid creation of wall section :: " + add_back.shape_details());
                    } catch (EmptyStackException e){
                        System.out.println("There was nothing to redo");
                    }
                    break;
                }

                case "finish": {
                    boolean confirmed = false;
                    System.out.println("Are you sure? (Yes/No)");
                    while (!confirmed){
                        switch (reader.nextLine().toLowerCase()){
                            case "yes": {
                                confirmed = true;
                                break;
                            }
                            case "no","back": {
                                break;
                            }
                        }
                    }

                    float total_area = 0.0f;
                    for (WallSection section : sections){
                        total_area += section.signed_area();
                    }
                    System.out.printf("Total amount of paint needed (cm^2): %.2f \n", total_area);
                    float total_area_m2 = total_area / 10000.0f;
                    System.out.printf("Total amount of paint needed (m^2): %.2f \n", total_area_m2);

                    float m2percan = 50.0f; //1 litre = 10m^2
                    int number_cans = (int) Math.ceil(total_area_m2 / m2percan);
                    System.out.println("Number of cans needed: " + number_cans);
                    float cost_per_can = 3.5f;
                    System.out.printf("Total cost at £3.50 per can: £%.2f \n", (cost_per_can*number_cans));


                    System.out.println("Would you like to do another calculation? (Yes/No)");
                    confirmed = false;
                    finished = true;
                    while (!confirmed){
                        switch (reader.nextLine().toLowerCase()){
                            case "yes": {
                                should_repeat = true;
                                confirmed = true;
                                break;
                            }
                            case "no": {
                                confirmed = true;
                                break;
                            }
                            case "back": {
                                finished = false;
                                confirmed = true;
                                break;
                            }
                        }
                    }
                    break;
                }

                case "add": {
                    if (modifier.isPresent()){
                        Optional<Shape> result = try_create_shape(reader, modifier.get());
                        if (result.isPresent()){
                            System.out.println("Section of wall added successfully! Add/subtract more sections of wall or finish.");
                            redo_queue.clear();
                            sections.push(new WallSection(true, result.get()));
                        }
                    } else{
                        add_section(true, sections, redo_queue, reader);
                    }
                    break;
                }

                case "subtract": {
                    if (modifier.isPresent()){
                        Optional<Shape> result = try_create_shape(reader, modifier.get());
                        if (result.isPresent()){
                            System.out.println("Section of wall added successfully! Add/subtract more sections of wall or finish.");
                            redo_queue.clear();
                            sections.push(new WallSection(false, result.get()));
                        }
                    } else{
                        add_section(false, sections, redo_queue, reader);
                    }
                    float total_area = 0.0f;
                    for (WallSection section : sections){
                        total_area += section.signed_area();
                    }
                    if (total_area < 0.0f){
                        System.out.println("The total area is currently negative, if this was unexpected, consider using undo.");
                    }
                    break;
                }

                default: {
                    System.out.println("Could not understand command, please check spelling and try again");
                }
            }
        }

        return should_repeat;
    }


    public static void main(String[] args) {
        boolean should_run;
        do {
            should_run = run();
        } while (should_run);
        System.out.println("Thank you for using the Paint-Area Calculator, shutting down now");
    }
}