package graphtheory;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

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
            g2d.setStroke(new BasicStroke(3f));
        } else if (wasFocused) {
            g2d.setColor(new Color(100, 200, 255)); 
            g2d.setStroke(new BasicStroke(3f));
        } else {
            g2d.setColor(new Color(200, 200, 255, 180)); // Light blue with transparency
            g2d.setStroke(new BasicStroke(2f));
        }
        g2d.drawLine(vertex1.location.x, vertex1.location.y, vertex2.location.x, vertex2.location.y);
        g2d.setStroke(new BasicStroke(1f)); // Reset stroke
    }

    public boolean hasIntersection(int x, int y) {
        int x1, x2, y1, y2;
        x1 = vertex1.location.x;
        x2 = vertex2.location.x;
        y1 = vertex1.location.y;
        y2 = vertex2.location.y;
        float slope = 0;
        if (x2 != x1) {
            slope = (y2 - y1) / (x2 - x1);
        }

        float b = Math.abs(x1 * slope - y1);

        if (y + b <= Math.round(slope * x) + 10 && y + b >= Math.round(slope * x) - 10) {
            if (x1 > x2 && y1 > y2) {
                if (x <= x1 && x >= x2 && y <= y1 && y >= y2) {
                    return true;
                }
            } else if (x1 < x2 && y1 > y2) {
                if (x <= x2 && x >= x1 && y <= y1 && y >= y2) {
                    return true;
                }
            } else if (x1 < x2 && y1 < y2) {
                if (x <= x2 && x >= x1 && y <= y2 && y >= y1) {
                    return true;
                }
            } else if (x <= x1 && x >= x2 && y <= y2 && y >= y1) {
                return true;
            }
        }
        return false;
    }
}