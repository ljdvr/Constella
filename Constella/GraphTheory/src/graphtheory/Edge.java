package graphtheory;

import java.awt.geom.Line2D;
import java.awt.*;

public class Edge {

    public Vertex vertex1;
    public Vertex vertex2;
    public boolean wasFocused;
    public boolean wasClicked;

    private int weight = -1; // default: no weight
    private boolean isDirected;

    public Edge(Vertex v1, Vertex v2, boolean directed) {
        vertex1 = v1;
        vertex2 = v2;
        this.isDirected = directed;
    }

    // --- weight methods ---
    public void setWeight(int w) {
        this.weight = w;
    }

    public int getWeight() {
        return weight;
    }

    public boolean isDirected() { 
        return isDirected; 
    }

    public void setDirected(boolean directed) { 
        this.isDirected = directed; 
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

        int x1 = vertex1.location.x;
        int y1 = vertex1.location.y;
        int x2 = vertex2.location.x;
        int y2 = vertex2.location.y;
        g2d.drawLine(x1, y1, x2, y2);

        g2d.setStroke(new BasicStroke(1f)); // reset stroke

        if (isDirected) {
            drawArrowHead(g2d, x1, y1, x2, y2);
        }

        // --- Draw edge weight if set ---
        if (weight >= 0) {
            int midX = (x1 + x2) / 2;
            int midY = (y1 + y2) / 2;
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.drawString(String.valueOf(weight), midX + 8, midY - 8);
        }
    }

    private void drawArrowHead(Graphics2D g2d, int x1, int y1, int x2, int y2) {
        double phi = Math.toRadians(25);
        int barb = 15;

        double dy = y2 - y1;
        double dx = x2 - x1;
        double theta = Math.atan2(dy, dx);

        double rho1 = theta + phi;
        double rho2 = theta - phi;

        int x3 = (int) (x2 - barb * Math.cos(rho1));
        int y3 = (int) (y2 - barb * Math.sin(rho1));
        int x4 = (int) (x2 - barb * Math.cos(rho2));
        int y4 = (int) (y2 - barb * Math.sin(rho2));

        Polygon arrowHead = new Polygon();
        arrowHead.addPoint(x2, y2);
        arrowHead.addPoint(x3, y3);
        arrowHead.addPoint(x4, y4);

        g2d.setColor(new Color(200, 200, 255, 180));
        g2d.fillPolygon(arrowHead);
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
