package graphtheory;

import java.awt.*;
import java.awt.geom.Line2D;

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
        } else {
            g2d.drawLine(x1, y1, x2, y2);
        }


        // --- Draw edge weight if set ---
        if (weight >= 0) {
            int midX = (x1 + x2) / 2;
            int midY = (y1 + y2) / 2;
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.drawString(String.valueOf(weight), midX + 8, midY - 8);
        }
    }

    private void drawArrowHead(Graphics2D g2d, int x1, int y1, int x2, int y2) {
        double phi = Math.toRadians(30); 
        int barb = 15; 

        double dy = y2 - y1;
        double dx = x2 - x1;
        double theta = Math.atan2(dy, dx);

        int vertexRadius = 20; 
        double lineLength = Math.sqrt(dx * dx + dy * dy);
        double ratio = (lineLength - vertexRadius) / lineLength;
        double endX = x1 + dx * ratio;
        double endY = y1 + dy * ratio;

        int[] xPoints = new int[3];
        int[] yPoints = new int[3];

        xPoints[0] = (int) endX;
        yPoints[0] = (int) endY;

        xPoints[1] = (int) (endX - barb * Math.cos(theta + phi));
        yPoints[1] = (int) (endY - barb * Math.sin(theta + phi));

        xPoints[2] = (int) (endX - barb * Math.cos(theta - phi));
        yPoints[2] = (int) (endY - barb * Math.sin(theta - phi));

        g2d.fillPolygon(xPoints, yPoints, 3);
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
