import java.awt.*;
import java.awt.Cursor;
import java.awt.image.BufferedImage;

public class Shape {
    String shapeType;
    int x, y, width, height;
    Color color;
    boolean isSelected = false;
    private Cursor nwResizeCursor, neResizeCursor, seResizeCursor, swResizeCursor;

    public Shape(String shapeType, int x, int y, int width, int height) {
        this.shapeType = shapeType;
        this.x = x;
        this.y = y;
        this.width = width;
        Shape aThis = this;
height = height;
        Color currentColor = null;
        this.color = currentColor; // Make sure 'currentColor' is defined or replace it with a specific color

        // Create custom resize cursors
        createResizeCursors();
    }

    private void createResizeCursors() {
        BufferedImage nwCursorImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        createResizeCursorImage(nwCursorImage, Color.RED);
        nwResizeCursor = Toolkit.getDefaultToolkit().createCustomCursor(nwCursorImage, new Point(0, 0), "NW Resize");

        BufferedImage neCursorImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        createResizeCursorImage(neCursorImage, Color.RED);
        neResizeCursor = Toolkit.getDefaultToolkit().createCustomCursor(neCursorImage, new Point(0, 0), "NE Resize");

        BufferedImage seCursorImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        createResizeCursorImage(seCursorImage, Color.RED);
        seResizeCursor = Toolkit.getDefaultToolkit().createCustomCursor(seCursorImage, new Point(0, 0), "SE Resize");

        BufferedImage swCursorImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        createResizeCursorImage(swCursorImage, Color.RED);
        swResizeCursor = Toolkit.getDefaultToolkit().createCustomCursor(swCursorImage, new Point(0, 0), "SW Resize");
    }

    private void createResizeCursorImage(BufferedImage image, Color color) {
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(5, 5, 27, 27);
        g2d.drawLine(27, 5, 5, 27);
        g2d.dispose();
    }

    public void draw(Graphics g) {
        g.setColor(color);
        if (shapeType.equals("Circle")) {
            g.fillOval(x, y, width, height);
        } else if (shapeType.equals("Rectangle")) {
            g.fillRect(x, y, width, height);
        } else if (shapeType.equals("Triangle")) {
            int[] xPoints = {x, x + width / 2, x + width};
            int[] yPoints = {y + height, y, y + height};
            g.fillPolygon(xPoints, yPoints, 3);
        }
    }

    public void drawResizeHandles(Graphics g) {
        int handleSize = 10;
        g.setColor(Color.RED);
        g.fillRect(x - handleSize, y - handleSize, 2 * handleSize, 2 * handleSize); // Top-left
        g.fillRect(x + width - handleSize, y - handleSize, 2 * handleSize, 2 * handleSize); // Top-right
        g.fillRect(x + width - handleSize, y + height - handleSize, 2 * handleSize, 2 * handleSize); // Bottom-right
        g.fillRect(x - handleSize, y + height - handleSize, 2 * handleSize, 2 * handleSize); // Bottom-left
    }

    public boolean contains(Point point) {
        if (shapeType.equals("Circle")) {
            int centerX = x + width / 2;
            int centerY = y + height / 2;
            double distance = Math.sqrt(Math.pow(centerX - point.x, 2) + Math.pow(centerY - point.y, 2));
            return distance <= width / 2;
        } else {
            return new Rectangle(x, y, width, height).contains(point);
        }
    }

    public void move(int deltaX, int deltaY) {
        x += deltaX;
        y += deltaY;
    }

    public void resize(int handleIndex, Point dragPoint) {
        if (handleIndex == 0) { // Top-left
            int newWidth = x + width - dragPoint.x;
            int newHeight = y + height - dragPoint.y;
            if (newWidth > 0 && newHeight > 0) {
                x = dragPoint.x;
                y = dragPoint.y;
                width = newWidth;
                height = newHeight;
            }
        } else if (handleIndex == 1) { // Top-right
            int newWidth = dragPoint.x - x;
            int newHeight = y + height - dragPoint.y;
            if (newWidth > 0 && newHeight > 0) {
                y = dragPoint.y;
                width = newWidth;
                height = newHeight;
            }
        } else if (handleIndex == 2) { // Bottom-right
            int newWidth = dragPoint.x - x;
            int newHeight = dragPoint.y - y;
            if (newWidth > 0 && newHeight > 0) {
                width = newWidth;
                height = newHeight;
            }
        } else if (handleIndex == 3) { // Bottom-left
            int newWidth = x + width - dragPoint.x;
            int newHeight = dragPoint.y - y;
            if (newWidth > 0 && newHeight > 0) {
                x = dragPoint.x;
                width = newWidth;
                height = newHeight;
            }
        }
    }

    public boolean isResizingHandle(Point point) {
        // Check if the point is inside one of the resize handles
        int handleSize = 10;
        return (point.getX() >= x - handleSize && point.getX() <= x + handleSize && point.getY() >= y - handleSize && point.getY() <= y + handleSize) // Top-left
                || (point.getX() >= x + width - handleSize && point.getX() <= x + width + handleSize && point.getY() >= y - handleSize && point.getY() <= y + handleSize) // Top-right
                || (point.getX() >= x + width - handleSize && point.getX() <= x + width + handleSize && point.getY() >= y + height - handleSize && point.getY() <= y + height + handleSize) // Bottom-right
                || (point.getX() >= x - handleSize && point.getX() <= x + handleSize && point.getY() >= y + height - handleSize && point.getY() <= y + height + handleSize); // Bottom-left
    }

    public Cursor getResizeCursor(Point point) {
        if (isResizingHandle(point)) {
            if (point.equals(new Point(x, y))) {
                return nwResizeCursor; // Top-left
            } else if (point.equals(new Point(x + width, y))) {
                return neResizeCursor; // Top-right
            } else if (point.equals(new Point(x + width, y + height))) {
                return seResizeCursor; // Bottom-right
            } else if (point.equals(new Point(x, y + height))) {
                return swResizeCursor; // Bottom-left
            }
        }
        return null;
    }
}
