import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class ShadedDrawing extends JFrame {

    public ShadedDrawing() {
        setTitle("Shaded Drawing");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(new DrawingPanel());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ShadedDrawing app = new ShadedDrawing();
            app.setVisible(true);
        });
    }

    class DrawingPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            int width = 100;
            int height = 100;
            int x = 150;
            int y = 150;

            Graphics2D g2d = (Graphics2D) g;

            // Create a rectangle
            Rectangle2D rect = new Rectangle2D.Double(x, y, width, height);

            // Define the shading colors
            Color startColor = Color.RED;
            Color endColor = Color.BLUE;

            // Define the gradient paint
            GradientPaint gradient = new GradientPaint(x, y, startColor, x + width, y + height, endColor);

            // Apply the gradient to the rectangle
            g2d.setPaint(gradient);
            g2d.fill(rect);
        }
    }
}
