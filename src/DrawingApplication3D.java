import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class DrawingApplication3D extends JFrame {
    private JButton drawCubeButton;
    private JButton drawSphereButton;
    private JButton drawCylinderButton;
    private JButton addButton;
    private JButton resizeButton;
    private JButton colorButton; // Added color selection button
    private DrawingPanel drawingArea;

    private boolean drawCube = true;
    private boolean drawCylinder = false;
    private float rotateAngle = 0.0f;

    private List<Shape> shapes = new ArrayList<>();
    private Shape selectedShape = null;
    private Point dragStart = null;
    private boolean isResizing = false;
    private int resizingDirection = 0;
    private static final int RESIZE_NONE = 0;
    private static final int RESIZE_NW = 1;
    private static final int RESIZE_NE = 2;
    private static final int RESIZE_SW = 3;
    private static final int RESIZE_SE = 4;

    public DrawingApplication3D() {
        setTitle("Rotating, Moving, Resizing, and Adding 3D Shapes");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        drawCubeButton = new JButton("Draw Cube");
        drawSphereButton = new JButton("Draw Sphere");
        drawCylinderButton = new JButton("Draw Cylinder");
        addButton = new JButton("Add Shape");
        resizeButton = new JButton("Resize Shape");
        colorButton = new JButton("Change Color"); // Added color selection button
        drawingArea = new DrawingPanel();

        drawCubeButton.addActionListener(e -> setDrawMode(true, false));
        drawSphereButton.addActionListener(e -> setDrawMode(false, false));
        drawCylinderButton.addActionListener(e -> setDrawMode(false, true));
        addButton.addActionListener(e -> addShape());
        resizeButton.addActionListener(e -> startResizing());
        colorButton.addActionListener(e -> changeShapeColor()); // Added color button action

       drawingArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    removeShapeAtPoint(e.getPoint());
                } else if (isResizing) {
                    resizingDirection = getResizingDirection(e.getPoint());
                    if (resizingDirection != RESIZE_NONE) {
                        dragStart = e.getPoint();
                        selectedShape = getSelectedShape(dragStart);
                    }
                } else {
                    dragStart = e.getPoint();
                    selectedShape = getSelectedShape(dragStart);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                dragStart = null;
                if (isResizing) {
                    resizingDirection = RESIZE_NONE;
                }
            }
        });

        drawingArea.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedShape != null) {
                    if (isResizing) {
                        resizeSelectedShape(e.getPoint());
                    } else {
                        int dx = e.getX() - dragStart.x;
                        int dy = e.getY() - dragStart.y;
                        selectedShape.move(dx, dy);
                    }
                    repaint();
                    dragStart = e.getPoint();
                }
            }
        });

        setLayout(new BorderLayout());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(drawCubeButton);
        buttonPanel.add(drawSphereButton);
        buttonPanel.add(drawCylinderButton);
        buttonPanel.add(addButton);
        buttonPanel.add(resizeButton);
        buttonPanel.add(colorButton); // Added color button to the panel
        add(buttonPanel, BorderLayout.NORTH);
        add(drawingArea, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DrawingApplication3D app = new DrawingApplication3D();
            app.setVisible(true);
        });
    }

    private void setDrawMode(boolean drawCube, boolean drawCylinder) {
        this.drawCube = drawCube;
        this.drawCylinder = drawCylinder;
        isResizing = false;
        selectedShape = null;
        repaint();
    }

    private void addShape() {
        if (drawCube) {
            shapes.add(new Cube());
        } else if (drawCylinder) {
            shapes.add(new Cylinder());
        } else {
            shapes.add(new Sphere());
        }
        isResizing = false;
        selectedShape = null;
        repaint();
    }

    private void removeShapeAtPoint(Point point) {
        for (Shape shape : shapes) {
            if (shape.contains(point)) {
                shapes.remove(shape);
                selectedShape = null;
                repaint();
                return;
            }
        }
    }

    private Shape getSelectedShape(Point point) {
        for (Shape shape : shapes) {
            if (shape.contains(point)) {
                return shape;
            }
        }
        return null;
    }

    private void startResizing() {
        if (selectedShape != null) {
            isResizing = true;
            resizingDirection = RESIZE_SE; // Default to resizing bottom-right
        }
    }

    private int getResizingDirection(Point point) {
        if (selectedShape == null) return RESIZE_NONE;
        Rectangle bounds = selectedShape.getBounds();
        int x = point.x;
        int y = point.y;
        int resizeHandleSize = 10; // Adjust the handle size as needed

        if (y >= bounds.y && y < bounds.y + resizeHandleSize) {
            if (x >= bounds.x && x < bounds.x + resizeHandleSize) {
                return RESIZE_NW; // Top-left
            } else if (x >= bounds.x + bounds.width - resizeHandleSize && x < bounds.x + bounds.width) {
                return RESIZE_NE; // Top-right
            }
        } else if (y >= bounds.y + bounds.height - resizeHandleSize && y < bounds.y + bounds.height) {
            if (x >= bounds.x && x < bounds.x + resizeHandleSize) {
                return RESIZE_SW; // Bottom-left
            } else if (x >= bounds.x + bounds.width - resizeHandleSize && x < bounds.x + bounds.width) {
                return RESIZE_SE; // Bottom-right
            }
        }

        return RESIZE_NONE;
    }

    private void resizeSelectedShape(Point newPoint) {
        if (selectedShape != null && dragStart != null) {
            Rectangle bounds = selectedShape.getBounds();
            int dx = newPoint.x - dragStart.x;
            int dy = newPoint.y - dragStart.y;

            switch (resizingDirection) {
                case RESIZE_NW:
                    bounds.x += dx;
                    bounds.y += dy;
                    bounds.width -= dx;
                    bounds.height -= dy;
                    break;
                case RESIZE_NE:
                    bounds.y += dy;
                    bounds.width += dx;
                    bounds.height -= dy;
                    break;
                case RESIZE_SW:
                    bounds.x += dx;
                    bounds.width -= dx;
                    bounds.height += dy;
                    break;
                case RESIZE_SE:
                    bounds.width += dx;
                    bounds.height += dy;
                    break;
            }

            selectedShape.setBounds(bounds);
            dragStart = newPoint;
            repaint();
        }
    }

    // Added method to change the selected shape's color
    private void changeShapeColor() {
        if (selectedShape != null) {
            Color newColor = JColorChooser.showDialog(this, "Select Color", selectedShape.color);
            if (newColor != null) {
                selectedShape.setColor(newColor);
                repaint();
            }
        }
    }

    class DrawingPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            for (Shape shape : shapes) {
                shape.draw(g);
            }
        }
    }

    abstract class Shape {
        int x, y, size;
        Color color;
        Color defaultColor; // Added a default color for one side

        public Shape(int x, int y) {
            this.x = x;
            this.y = y;
            size = 100;
            color = Color.RED; // Default color
            defaultColor = Color.GREEN; // Default color for one side
        }

        public void draw(Graphics g) {
            // Implement shape drawing in subclasses
        }

        public boolean contains(Point p) {
            return getBounds().contains(p);
        }

        public void move(int dx, int dy) {
            x += dx;
            y += dy;
        }

        public void setBounds(Rectangle bounds) {
            x = bounds.x;
            y = bounds.y;
            size = bounds.width; // Assumes it's a square
        }

        public Rectangle getBounds() {
            return new Rectangle(x, y, size, size);
        }

        // Added method to set the shape's color
        public void setColor(Color color) {
            this.color = color;
        }
    }

    class Cube extends Shape {
        public Cube() {
            super(200, 200);
        }

        @Override
        public void draw(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            AffineTransform transform = new AffineTransform();
            transform.rotate(Math.toRadians(rotateAngle), x + size / 2, y + size / 2);
            g2d.transform(transform);

            // Draw the front face
            g2d.setColor(color); // Use the selected color
            g2d.fillRect(x, y, size, size);

            // Draw the left face
            int[] xLeft = {x, x, x - 20, x - 20};
            int[] yLeft = {y, y + size, y + size - 20, y - 20};
            g2d.setColor(defaultColor); // Use the default color for one side
            g2d.fillPolygon(xLeft, yLeft, 4);

            // Draw the top face
            int[] xTop = {x, x + size, x + size - 20, x - 20};
            int[] yTop = {y, y, y - 20, y - 20};
            g2d.setColor(color); // Use the selected color
            g2d.fillPolygon(xTop, yTop, 4);

            g2d.dispose();
        }
    }

    class Sphere extends Shape {
        public Sphere() {
            super(200, 200);
        }

        @Override
        public void draw(Graphics g) {
            int centerX = x + size / 2;
            int centerY = y + size / 2;
            int radius = size / 2;

            // Create a new Graphics2D object
            Graphics2D g2d = (Graphics2D) g.create();

            // Draw the sphere
            g2d.setColor(color); // Use the selected color
            g2d.fillOval(centerX - radius, centerY - radius, size, size);

            // Create a gradient from the top-left to the bottom-right
            GradientPaint gradient = new GradientPaint(
                    centerX - radius, centerY - radius, color,
                    centerX + radius, centerY + radius, Color.WHITE);

            g2d.setPaint(gradient);
            g2d.fill(new Ellipse2D.Double(centerX - radius, centerY - radius, size, size));

            g2d.dispose();
        }

        @Override
        public void setBounds(Rectangle bounds) {
            x = bounds.x;
            y = bounds.y;
            size = bounds.width; // Assumes it's a square
        }
    }

    class Cylinder extends Shape {
        public Cylinder() {
            super(200, 200);
        }

        @Override
        public void draw(Graphics g) {
            int centerX = x + size / 2;
            int centerY = y + size / 2;
            int radius = size / 4;
            int height = size / 2;

            // Draw the top circle
            g.setColor(color); // Use the selected color
            g.fillOval(centerX - radius, centerY - height / 2, radius * 2, height / 2);

            // Draw the bottom circle
            g.setColor(color); // Use the selected color
            g.fillOval(centerX - radius, centerY, radius * 2, height / 2);

            // Draw the curved surface
            GradientPaint gradientPaint = new GradientPaint(centerX, centerY - height / 2, color,
                    centerX, centerY + height / 2, color);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setPaint(gradientPaint);
            g2d.fill(new Ellipse2D.Double(centerX - radius, centerY - height / 2, radius * 2, height));
            g2d.dispose();
        }
    }
}
