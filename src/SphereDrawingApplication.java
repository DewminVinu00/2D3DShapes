import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;

import javax.swing.*;

public class SphereDrawingApplication extends JFrame {
    private GLCanvas canvas;

    public SphereDrawingApplication() {
        setTitle("3D Sphere Drawing Application");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        canvas = new GLCanvas();
        canvas.addGLEventListener(new SphereRenderer());
        getContentPane().add(canvas);

        // Create an animator to continuously redraw the canvas
        FPSAnimator animator = new FPSAnimator(canvas, 60);
        animator.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SphereDrawingApplication app = new SphereDrawingApplication();
            app.setVisible(true);
        });
    }
}

class SphereRenderer implements GLEventListener {
    private float rotateAngle = 0.0f;
    private GLUT glut;

    public SphereRenderer() {
        glut = new GLUT();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        // Initialization code (if any) goes here
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        gl.glLoadIdentity();
        gl.glRotatef(rotateAngle, 1.0f, 1.0f, 1.0f);

        // Draw a 3D sphere using GLUT
        gl.glColor3f(1.0f, 0.0f, 0.0f);
        glut.glutSolidSphere(0.5, 50, 50);

        gl.glFlush();
        rotateAngle += 1.0f;
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        // Reshaping code (if any) goes here
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        // Cleanup code (if any) goes here
    }
}
