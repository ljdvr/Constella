package graphtheory;

import java.awt.Color;
import java.awt.Point;
import java.util.Vector;
import java.awt.Graphics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;

/**
 *
 * @author mk
 */
public class Vertex implements Comparable {

    public String name;
    public Point location;
    public boolean wasFocused;
    public boolean wasClicked;
    public boolean isGlowing = false;
    private int size1 = 30;
    private int size2 = 40;
    public Vector<Vertex> connectedVertices;

    public Vertex(String name, int x, int y) {
        this.name = name;
        location = new Point(x, y);
        connectedVertices = new Vector<Vertex>();
    }

    public void addVertex(Vertex v) {
        connectedVertices.add(v);
    }

    public boolean hasIntersection(int x, int y) {
        double distance = Math.sqrt(Math.pow((x - location.x), 2) + Math.pow((y - location.y), 2));

        if (distance > size2 / 2) {
            return false;
        } else {
            return true;
        }
    }

    public boolean connectedToVertex(Vertex v) {
        if (connectedVertices.contains(v)) {
            return true;
        }
        return false;
    }

    public int getDegree() {
        return connectedVertices.size();
    }

    public int compareTo(Object v) {
        if (((Vertex) v).getDegree() > getDegree()) {
            return 1;
        } else if (((Vertex) v).getDegree() < getDegree()) {
            return -1;
        } else {
            return 0;
        }
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // 🔵 Glow effect first (drawn behind the node)
        if (isGlowing) {
            g2d.setColor(new Color(50, 150, 255));
            g2d.fillOval(location.x - size2 / 2 - 10, location.y - size2 / 2 - 10, size2 + 20, size2 + 20);
        }

        if (wasClicked) {
            // Red star (critical node)
            g2d.setColor(Color.RED);
            g2d.fillOval(location.x - size2 / 2, location.y - size2 / 2, size2, size2);
            g2d.setColor(Color.WHITE);
            g2d.fillOval(location.x - size1 / 2, location.y - size1 / 2, size1, size1);
        } else if (wasFocused) {
            // Blue highlighted star
            GradientPaint gradient = new GradientPaint(
                location.x - size2 / 2, location.y - size2 / 2, new Color(100, 100, 255),
                location.x + size2 / 2, location.y + size2 / 2, new Color(0, 0, 200)
            );
            g2d.setPaint(gradient);
            g2d.fillOval(location.x - size2 / 2, location.y - size2 / 2, size2, size2);
            g2d.setColor(Color.WHITE);
            g2d.fillOval(location.x - size1 / 2, location.y - size1 / 2, size1, size1);
        } else {
            // Normal star with gradient
            GradientPaint gradient = new GradientPaint(
                location.x - size2 / 2, location.y - size2 / 2, new Color(200, 200, 255),
                location.x + size2 / 2, location.y + size2 / 2, new Color(200, 200, 255)
            );
            g2d.setPaint(gradient);
            g2d.fillOval(location.x - size2 / 2, location.y - size2 / 2, size2, size2);
            g2d.setColor(Color.WHITE);
            g2d.fillOval(location.x - size1 / 2, location.y - size1 / 2, size1, size1);
        }

        g2d.setColor(Color.BLACK);
        g2d.drawString(name, location.x - 5, location.y + 5);
    }
}