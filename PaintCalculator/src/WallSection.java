public class WallSection {
    private Shape wall_shape;
    private boolean is_positive;

    public WallSection(boolean positive, Shape shape){
        wall_shape = shape;
        is_positive = positive;
    }

    public float signed_area(){
        return wall_shape.area() * (is_positive ? 1.0f : -1.0f);
    }

    public String shape_details(){
        String prefix = is_positive ? "added " : "subtracted ";
        return prefix + wall_shape.as_string();
    }
}
