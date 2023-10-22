import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DrawingState implements Serializable {
    private List<Shape> shapes;

    public DrawingState(List<Shape> shapes) {
        this.shapes = shapes;
    }


    public List<Shape> getShapes() {
        return shapes;
    }
}
