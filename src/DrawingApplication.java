import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.*;
import javax.swing.JFileChooser;
import java.io.Serializable;

public class DrawingApplication extends JFrame {
  private ArrayList < Shape > shapes = new ArrayList < > ();
  private Shape currentShape;
  private Color currentColor = Color.BLACK;
  private JComboBox < String > shapeSelector;
  private Map < String, Color > shapeColors = new HashMap < > ();
  private String selectedShape = "Circle"; // Default shape
  private Point initialMousePress;
  private Shape selectedShapeToMove;
  private boolean isResizing = false;
  private boolean useGradient = false;
  private transient JFileChooser fileChooser;
  private transient DrawingState drawingState;

  private int resizeHandleIndex = -1;
  private static final int HIDE_HANDLES_DELAY = 3000; // 3000 milliseconds (3 seconds)
  private Timer hideHandlesTimer;
  private JPanel controlPanel;

  private void initFileChooser() {
    if (fileChooser == null) {
      fileChooser = new JFileChooser();
    }
  }

  private void changeShapeColor(Color newColor) {
    if (selectedShapeToMove != null) {
      selectedShapeToMove.color = newColor;
      DrawingApplication.this.repaint();
    }
  }

  private void saveDrawings() {
    JFileChooser fileChooser = new JFileChooser();
    int returnValue = fileChooser.showSaveDialog(this);

    if (returnValue == JFileChooser.APPROVE_OPTION) {
      File selectedFile = fileChooser.getSelectedFile();
      try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(selectedFile))) {
        outputStream.writeObject(shapes);
      } catch (IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error saving the drawings.", "Error", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  public DrawingApplication() {
    setTitle("Drawing Application");
    setSize(800, 600);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    controlPanel = new JPanel(); // Initialize controlPanel here
    // Add a button to change the color of the selected shape
    JButton changeColorButton = new JButton("Change Color");
    controlPanel.add(changeColorButton);

    changeColorButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Color newColor = JColorChooser.showDialog(DrawingApplication.this, "Select a New Color", currentColor);
        changeShapeColor(newColor);
      }
    });

    JButton saveButton = new JButton("Save");

    saveButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        saveDrawings();
      }
    });

    JButton useGradientButton = new JButton("Use Gradient");

   useGradientButton.addActionListener(new ActionListener() {
  @Override
  public void actionPerformed(ActionEvent e) {
    useGradient = !useGradient;
    DrawingApplication.this.repaint();
  }
});


    JPanel controlPanel = new JPanel();
    JButton selectColorButton = new JButton("Select Color");
    JButton clearButton = new JButton("Clear");
    shapeSelector = new JComboBox < > (new String[] {
      "Circle",
      "Rectangle",
      "Triangle"
    });

    controlPanel.add(selectColorButton);
    controlPanel.add(clearButton);
    controlPanel.add(shapeSelector);
    controlPanel.add(useGradientButton);

    controlPanel.add(saveButton);

    DrawingCanvas canvas = new DrawingCanvas();
    canvas.addMouseListener(new MyMouseListener());
    canvas.addMouseMotionListener(new MyMouseMotionListener());

    selectColorButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        currentColor = JColorChooser.showDialog(DrawingApplication.this, "Select a Color", currentColor);

        if (selectedShapeToMove != null) {
          selectedShapeToMove.color = currentColor;
          DrawingApplication.this.repaint();
        }
      }
    });

    hideHandlesTimer = new Timer(HIDE_HANDLES_DELAY, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        hideShapeHandles();
        hideHandlesTimer.stop();
      }

    });
    hideHandlesTimer.setRepeats(false);

    clearButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        shapes.clear();
        canvas.repaint();
      }
    });

    shapeSelector.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        selectedShape = (String) shapeSelector.getSelectedItem();
      }
    });

    add(controlPanel, BorderLayout.NORTH);
    add(canvas, BorderLayout.CENTER);

  }

  private class DrawingCanvas extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      for (Shape shape: shapes) {
        shape.draw(g);
      }
    }
  }

  private class Shape implements Serializable {
    private double rotationAngle = 0.0;

    String shapeType;
    int x, y, width, height;
    Color color;
    Rectangle[] resizeHandles = new Rectangle[8]; // Eight resize handles for all corners and sides
    private boolean isHovered = false; // Add a flag to track hover state

    private boolean showHandles = true;

    public Shape(String shapeType, int x, int y, int width, int height) {
      this.shapeType = shapeType;
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      this.color = currentColor;
      this.rotationAngle = 0.0;

      updateResizeHandles();
    }

    public void draw(Graphics g) {
      g.setColor(color);
      if (shapeType.equals("Circle")) {
        g.fillOval(x, y, width, height);
      } else if (shapeType.equals("Rectangle")) {
        g.fillRect(x, y, width, height);
      } else if (shapeType.equals("Triangle")) {
        int[] xPoints = {
          x,
          x + width / 2,
          x + width
        };
        int[] yPoints = {
          y + height,
          y,
          y + height
        };
        g.fillPolygon(xPoints, yPoints, 3);
      }

      if (isResizing) {
        g.setColor(Color.RED);
        int arrowSize = 10; // Adjust the size of the arrows

        // Draw resize arrows
        g.fillPolygon(new int[] {
          x + width / 2, x + width / 2, x + width / 2 + arrowSize
        }, new int[] {
          y,
          y - arrowSize,
          y
        }, 3); // Up arrow
        g.fillPolygon(new int[] {
          x + width, x + width + arrowSize, x + width
        }, new int[] {
          y + height / 2, y + height / 2, y + height / 2 + arrowSize
        }, 3); // Right arrow
        g.fillPolygon(new int[] {
          x + width / 2, x + width / 2, x + width / 2 + arrowSize
        }, new int[] {
          y + height, y + height + arrowSize, y + height
        }, 3); // Down arrow
        g.fillPolygon(new int[] {
          x,
          x - arrowSize,
          x
        }, new int[] {
          y + height / 2, y + height / 2, y + height / 2 + arrowSize
        }, 3); // Left arrow
      }

      if (useGradient) {
    if (isHovered) {
      GradientPaint gradient = new GradientPaint(
        x, y, color, x + width, y + height, Color.WHITE); // You can adjust the colors
      ((Graphics2D) g).setPaint(gradient);
    } else {
      GradientPaint gradient = new GradientPaint(
        x, y, Color.WHITE, x + width, y + height, color); // You can adjust the colors
      ((Graphics2D) g).setPaint(gradient);
    }
  } else {
    g.setColor(color);
  }
    }

    public void setHovered(boolean hovered) {
      isHovered = hovered;
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

    public void updateResizeHandles() {
      int handleSize = 1;
      // Corners
      resizeHandles[0] = new Rectangle(x - handleSize, y - handleSize, 2 * handleSize, 2 * handleSize); // Top-left
      resizeHandles[1] = new Rectangle(x + width - handleSize, y - handleSize, 2 * handleSize, 2 * handleSize); // Top-right
      resizeHandles[2] = new Rectangle(x + width - handleSize, y + height - handleSize, 2 * handleSize, 2 * handleSize); // Bottom-right
      resizeHandles[3] = new Rectangle(x - handleSize, y + height - handleSize, 2 * handleSize, 2 * handleSize); // Bottom-left
      // Sides
      resizeHandles[4] = new Rectangle(x + width / 2 - handleSize, y - handleSize, 2 * handleSize, 2 * handleSize); // Top
      resizeHandles[5] = new Rectangle(x + width - handleSize, y + height / 2 - handleSize, 2 * handleSize, 2 * handleSize); // Right
      resizeHandles[6] = new Rectangle(x + width / 2 - handleSize, y + height - handleSize, 2 * handleSize, 2 * handleSize); // Bottom
      resizeHandles[7] = new Rectangle(x - handleSize, y + height / 2 - handleSize, 2 * handleSize, 2 * handleSize); // Left
    }

    public void move(int deltaX, int deltaY) {
      x += deltaX;
      y += deltaY;
      updateResizeHandles();
    }

    public void resize(int handleIndex, int deltaX, int deltaY) {
      int oldX = x;
      int oldY = y;
      int oldWidth = width;
      int oldHeight = height;

      if (handleIndex == 0) { // Top-left
        x += deltaX;
        y += deltaY;
        width -= deltaX;
        height -= deltaY;
      } else if (handleIndex == 1) { // Top-right
        y += deltaY;
        width += deltaX;
        height -= deltaY;
      } else if (handleIndex == 2) { // Bottom-right
        width += deltaX;
        height += deltaY;
      } else if (handleIndex == 3) { // Bottom-left
        x += deltaX;
        width -= deltaX;
        height += deltaY;
      } else if (handleIndex == 4) { // Top
        y += deltaY;
        height -= deltaY;
      } else if (handleIndex == 5) { // Right
        width += deltaX;
      } else if (handleIndex == 6) { // Bottom
        height += deltaY;
      } else if (handleIndex == 7) { // Left
        x += deltaX;
        width -= deltaX;
      }

      // Prevent negative width or height
      if (width <= 0) {
        x = oldX;
        width = oldWidth;
      }
      if (height <= 0) {
        y = oldY;
        height = oldHeight;
      }

      updateResizeHandles();
    }

    public void toggleHandlesVisibility() {
      showHandles = !showHandles;
      if (showHandles) {
        hideHandlesTimer.restart();
      }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
      out.defaultWriteObject(); // Serialize the default fields
      // Handle custom serialization for non-serializable fields
      out.writeObject(shapeType); // Serialize the shape type
      out.writeInt(x);
      out.writeInt(y);
      out.writeInt(width);
      out.writeInt(height);
      out.writeObject(color);
    }

    private void readObject(ObjectInputStream in ) throws IOException, ClassNotFoundException {
      in.defaultReadObject(); // Read the default fields
      // Handle custom deserialization for non-serializable fields
      shapeType = (String) in.readObject(); // Read the shape type
      x = in.readInt();
      y = in.readInt();
      width = in.readInt();
      height = in.readInt();
      color = (Color) in.readObject();
      updateResizeHandles();
    }

  }

  private class MyMouseListener extends MouseAdapter {
    @Override
    public void mousePressed(MouseEvent e) {
      initialMousePress = e.getPoint();
      selectedShapeToMove = null;
      resizeHandleIndex = -1;
      if (SwingUtilities.isRightMouseButton(e)) {
        for (int i = shapes.size() - 1; i >= 0; i--) {
          Shape shape = shapes.get(i);
          if (shape.contains(e.getPoint())) {
            shapes.remove(shape);
            DrawingApplication.this.repaint();
            return; // Clear the shape and exit
          }
        }
      }

      for (int i = shapes.size() - 1; i >= 0; i--) {
        Shape shape = shapes.get(i);
        if (shape.contains(e.getPoint())) {
          selectedShapeToMove = shape;
          shape.setHovered(true); // Set the hover flag to true
          break;
        }
      }

      for (int i = shapes.size() - 1; i >= 0; i--) {
        Shape shape = shapes.get(i);
        for (int j = 0; j < 8; j++) {
          if (shape.resizeHandles[j].contains(e.getPoint())) {
            isResizing = true;
            resizeHandleIndex = j;
            break;
          }
        }
        if (isResizing) {
          selectedShapeToMove = shape;
          break;
        } else if (shape.contains(e.getPoint())) {
          selectedShapeToMove = shape;
          break;
        }
      }

      if (selectedShapeToMove == null) {
        currentShape = new Shape(selectedShape, e.getX(), e.getY(), 50, 50);
        shapes.add(currentShape);
      }

      DrawingApplication.this.repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
      for (Shape shape: shapes) {
        if (shape.contains(e.getPoint())) {
          shape.setHovered(true);
          hideHandlesTimer.restart(); // Restart the timer on mouse movement
        } else {
          shape.setHovered(false);
        }
        setCursor(Cursor.getDefaultCursor());
        DrawingApplication.this.repaint();
      }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      selectedShapeToMove = null;
      isResizing = false;
    }

  }

  private class MyMouseMotionListener implements MouseMotionListener {
    @Override
    public void mouseDragged(MouseEvent e) {
      if (selectedShapeToMove != null) {
        if (isResizing) {
          selectedShapeToMove.resize(resizeHandleIndex, e.getX() - initialMousePress.x, e.getY() - initialMousePress.y);
        } else {
          int deltaX = e.getX() - initialMousePress.x;
          int deltaY = e.getY() - initialMousePress.y;
          selectedShapeToMove.move(deltaX, deltaY);
        }
        initialMousePress = e.getPoint();
        DrawingApplication.this.repaint();
      }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
      for (Shape shape: shapes) {
        for (int i = 0; i < 8; i++) {
          if (shape.resizeHandles[i].contains(e.getPoint())) {
            setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
            return;
          }
        }
      }
      setCursor(Cursor.getDefaultCursor());
    }
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      DrawingApplication app = new DrawingApplication();
      app.setVisible(true);
    });
  }

  private void hideShapeHandles() {
    for (Shape shape: shapes) {
      shape.showHandles = false;
    }
    DrawingApplication.this.repaint();
  }

}