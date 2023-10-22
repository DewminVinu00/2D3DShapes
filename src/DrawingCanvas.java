import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class DrawingCanvas extends JPanel {
    private ArrayList<Shape> shapes;
        private Shape currentShape;


   

    DrawingCanvas() {
 this.shapes = new ArrayList<>();
        this.shapes = shapes;    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Shape shape : shapes) {
            shape.draw(g);
            if (shape.isSelected) {
                shape.drawResizeHandles(g);
            }
        }
    }
}
