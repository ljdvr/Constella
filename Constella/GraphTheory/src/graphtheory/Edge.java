package graphtheory;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

public class Edge {

    public Vertex vertex1;
    public Vertex vertex2;
    public boolean wasFocused;
    public boolean wasClicked;

    private int weight = -1; // default: no weight

    public Edge(Vertex v1, Vertex v2) {
        vertex1 = v1;
        vertex2 = v2;
    }

    // --- weight methods ---
    public void setWeight(int w) {
        this.weight = w;
    }

    public int getWeight() {
        return weight;
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // --- Draw edge line with style ---
        if (wasClicked) {
            g2d.setColor(new Color(255, 100, 100));
            g2d.setStroke(new BasicStroke(5f)); // clicked edge
        } else if (wasFocused) {
            g2d.setColor(new Color(100, 200, 255));
            g2d.setStroke(new BasicStroke(4f)); // focused edge
        } else {
            g2d.setColor(new Color(200, 200, 255, 180)); // default
            g2d.setStroke(new BasicStroke(3f));
        }

        g2d.drawLine(vertex1.location.x, vertex1.location.y,
                     vertex2.location.x, vertex2.location.y);

        g2d.setStroke(new BasicStroke(1f)); // reset stroke

        // --- Draw edge weight if set ---
        if (weight >= 0) {
            int midX = (vertex1.location.x + vertex2.location.x) / 2;
            int midY = (vertex1.location.y + vertex2.location.y) / 2;

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.drawString(String.valueOf(weight), midX + 8, midY - 8);
        }
    }

    public boolean hasIntersection(int x, int y) {
        return pointToLineDistance(x, y,
                vertex1.location.x, vertex1.location.y,
                vertex2.location.x, vertex2.location.y) <= 8;
    }

    private double pointToLineDistance(int px, int py, int x1, int y1, int x2, int y2) {
        Line2D line = new Line2D.Double(x1, y1, x2, y2);
        return line.ptSegDist(px, py);
    }
}
