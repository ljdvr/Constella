package graphtheory;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

/**
 *
 * @author mk
 */
public class Edge {

    public Vertex vertex1;
    public Vertex vertex2;
    public boolean wasFocused;
    public boolean wasClicked;

    public Edge(Vertex v1, Vertex v2) {
        vertex1 = v1;
        vertex2 = v2;
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        if (wasClicked) {
            g2d.setColor(new Color(255, 100, 100)); 
            g2d.setStroke(new BasicStroke(5f)); // Make clicked edges thicker for better visibility
        } else if (wasFocused) {
            g2d.setColor(new Color(100, 200, 255)); 
            g2d.setStroke(new BasicStroke(4f)); // Make focused edges thicker
        } else {
            g2d.setColor(new Color(200, 200, 255, 180)); // Light blue with transparency
            g2d.setStroke(new BasicStroke(3f)); // Make normal edges thicker for easier clicking
        }
        g2d.drawLine(vertex1.location.x, vertex1.location.y, vertex2.location.x, vertex2.location.y);
        g2d.setStroke(new BasicStroke(1f)); // Reset stroke
    }

    public boolean hasIntersection(int x, int y) {
        // Use a proper line-point distance calculation
        return pointToLineDistance(x, y, 
                                 vertex1.location.x, vertex1.location.y,
                                 vertex2.location.x, vertex2.location.y) <= 8; // Increased tolerance
    }

    /**
     * Calculate the distance from a point to a line segment
     */
    private double pointToLineDistance(int px, int py, int x1, int y1, int x2, int y2) {
        // Convert to Line2D for better precision
        Line2D line = new Line2D.Double(x1, y1, x2, y2);
        
        // Calculate the distance from point to line
        double dist = line.ptSegDist(px, py);
        
        return dist;
    }

    // Alternative simpler implementation if you prefer:
    private double pointToLineDistanceSimple(int px, int py, int x1, int y1, int x2, int y2) {
        // Vector from line start to end
        double lineVecX = x2 - x1;
        double lineVecY = y2 - y1;
        
        // Vector from line start to point
        double pointVecX = px - x1;
        double pointVecY = py - y1;
        
        // Calculate dot product
        double dotProduct = pointVecX * lineVecX + pointVecY * lineVecY;
        
        // Calculate squared length of line
        double lineLengthSquared = lineVecX * lineVecX + lineVecY * lineVecY;
        
        // Calculate projection parameter
        double t = Math.max(0, Math.min(1, dotProduct / lineLengthSquared));
        
        // Calculate closest point on line
        double closestX = x1 + t * lineVecX;
        double closestY = y1 + t * lineVecY;
        
        // Calculate distance
        double dx = px - closestX;
        double dy = py - closestY;
        
        return Math.sqrt(dx * dx + dy * dy);
    }
}