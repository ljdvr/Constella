/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graphtheory;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

/**
 *
 * @author mk
 */
public class GraphProperties {

    public int[][] adjacencyMatrix;
    public int[][] distanceMatrix;
    public Vector<VertexPair> vpList;

    public int[][] generateAdjacencyMatrix(Vector<Vertex> vList, Vector<Edge> eList) {
        adjacencyMatrix = new int[vList.size()][vList.size()];

        for (int a = 0; a < vList.size(); a++) {
            for (int b = 0; b < vList.size(); b++) {
                adjacencyMatrix[a][b] = 0;
            }
        }

        for (int i = 0; i < eList.size(); i++) {
            Edge edge = eList.get(i);
            int index1 = vList.indexOf(edge.vertex1);
            int index2 = vList.indexOf(edge.vertex2);

            if (edge.isDirected()) {
                adjacencyMatrix[index1][index2] = 1;
            } else {
                adjacencyMatrix[index1][index2] = 1;
                adjacencyMatrix[index2][index1] = 1;
            }
        }
        return adjacencyMatrix;
    }

    public int[][] generateDistanceMatrix(Vector<Vertex> vList) {
        distanceMatrix = new int[vList.size()][vList.size()];

        for (int a = 0; a < vList.size(); a++) // initialize
        {
            for (int b = 0; b < vList.size(); b++) {
                distanceMatrix[a][b] = 0;
            }
        }

        VertexPair vp;
        int shortestDistance;
        for (int i = 0; i < vList.size(); i++) {
            for (int j = i + 1; j < vList.size(); j++) {
                vp = new VertexPair(vList.get(i), vList.get(j));
                shortestDistance = vp.getShortestDistance();
                distanceMatrix[vList.indexOf(vp.vertex1)][vList.indexOf(vp.vertex2)] = shortestDistance;
                distanceMatrix[vList.indexOf(vp.vertex2)][vList.indexOf(vp.vertex1)] = shortestDistance;
            }
        }
        return distanceMatrix;
    }

    // In GraphProperties.java, modify the findComponent method to accept vertexList as a parameter:
    private void findComponent(Vertex v, Vector<Vertex> visited, Vector<Vertex> vertexList) {
        visited.add(v);

        // Use BFS to traverse both outgoing and incoming connections
        Vector<Vertex> queue = new Vector<Vertex>();
        queue.add(v);

        while (!queue.isEmpty()) {
            Vertex current = queue.remove(0);

            // Check outgoing connections
            for (Vertex neighbor : current.connectedVertices) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }

            // Check incoming connections (vertices that point to current)
            for (Vertex potentialSource : vertexList) {
                if (potentialSource.connectedVertices.contains(current) && !visited.contains(potentialSource)) {
                    visited.add(potentialSource);
                    queue.add(potentialSource);
                }
            }
        }
    }

    // Update the countComponents method to pass the vertexList:
    public int countComponents(Vector<Vertex> vList) {
        Vector<Vertex> visited = new Vector<Vertex>();
        int componentCount = 0;

        for (Vertex v : vList) {
            if (!visited.contains(v)) {
                componentCount++;
                findComponent(v, visited, vList); // Pass vList as parameter
            }
        }

        return componentCount;
    }

    // Update the findComponentForCollection method to accept vertexList:
    private void findComponentForCollection(Vertex v, Vector<Vertex> visited, Vector<Vertex> component, Vector<Vertex> vertexList) {
        visited.add(v);
        component.add(v);

        Vector<Vertex> queue = new Vector<Vertex>();
        queue.add(v);

        while (!queue.isEmpty()) {
            Vertex current = queue.remove(0);

            // Outgoing connections
            for (Vertex neighbor : current.connectedVertices) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    component.add(neighbor);
                    queue.add(neighbor);
                }
            }

            // Incoming connections
            for (Vertex potentialSource : vertexList) {
                if (potentialSource.connectedVertices.contains(current) && !visited.contains(potentialSource)) {
                    visited.add(potentialSource);
                    component.add(potentialSource);
                    queue.add(potentialSource);
                }
            }
        }
    }

    // Update the findComponents method to pass vertexList:
    public Vector<Vector<Vertex>> findComponents(Vector<Vertex> vList) {
        Vector<Vector<Vertex>> components = new Vector<Vector<Vertex>>();
        Vector<Vertex> visited = new Vector<Vertex>();

        for (Vertex v : vList) {
            if (!visited.contains(v)) {
                Vector<Vertex> component = new Vector<Vertex>();
                findComponentForCollection(v, visited, component, vList); // Pass vList as parameter
                components.add(component);
            }
        }

        return components;
    }

    public char getComponentLabel(Vertex vertex, Vector<Vertex> vList) {
        Vector<Vector<Vertex>> components = findComponents(vList);

        for (int i = 0; i < components.size(); i++) {
            if (components.get(i).contains(vertex)) {
                return (char) ('A' + i);
            }
        }

        return '?';
    }

    public void displayContainers(Vector<Vertex> vList) {
        vpList = new Vector<VertexPair>();
        int[] kWideGraph = new int[10];
        for (int i = 0; i < kWideGraph.length; i++) {
            kWideGraph[i] = -1;
        }

        VertexPair vp;

        for (int a = 0; a < vList.size(); a++) {    // assign vertex pairs
            for (int b = a + 1; b < vList.size(); b++) {
                vp = new VertexPair(vList.get(a), vList.get(b));
                vpList.add(vp);
                int longestWidth = 0;
                System.out.println(">Vertex Pair " + vList.get(a).name + "-" + vList.get(b).name + "\n All Paths:");
                vp.generateVertexDisjointPaths();
                for (int i = 0; i < vp.VertexDisjointContainer.size(); i++) {//for every container of the vertex pair
                    int width = vp.VertexDisjointContainer.get(i).size();
                    Collections.sort(vp.VertexDisjointContainer.get(i), new descendingWidthComparator());
                    int longestLength = vp.VertexDisjointContainer.get(i).firstElement().size();
                    longestWidth = Math.max(longestWidth, width);
                    System.out.println("\tContainer " + i + " - " + "Width=" + width + " - Length=" + longestLength);

                    for (int j = 0; j < vp.VertexDisjointContainer.get(i).size(); j++) //for every path in the container
                    {
                        System.out.print("\t\tPath " + j + "\n\t\t\t");
                        for (int k = 0; k < vp.VertexDisjointContainer.get(i).get(j).size(); k++) {
                            System.out.print("-" + vp.VertexDisjointContainer.get(i).get(j).get(k).name);
                        }
                        System.out.println();
                    }

                }
                //d-wide for vertexPair
                for (int k = 1; k <= longestWidth; k++) { // 1-wide, 2-wide, 3-wide...
                    int minLength = 999;
                    for (int m = 0; m < vp.VertexDisjointContainer.size(); m++) // for each container with k-wide select shortest length
                    {
                        minLength = Math.min(minLength, vp.VertexDisjointContainer.get(m).size());
                    }
                    if (minLength != 999) {
                        System.out.println(k + "-wide for vertexpair(" + vp.vertex1.name + "-" + vp.vertex2.name + ")=" + minLength);
                        kWideGraph[k] = Math.max(kWideGraph[k], minLength);
                    }
                }
            }
        }

        for (int i = 0; i < kWideGraph.length; i++) {
            if (kWideGraph[i] != -1) {
                System.out.println("D" + i + "(G)=" + kWideGraph[i]);
            }
        }

    }

    public void drawAdjacencyMatrix(Graphics g, Vector<Vertex> vList, int x, int y) {
        int cSize = 20;
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(x, y - 30, vList.size() * cSize + cSize, vList.size() * cSize + cSize);
        g.setColor(Color.black);
        g.drawString("AdjacencyMatrix", x, y - cSize);
        for (int i = 0; i < vList.size(); i++) {
            g.setColor(Color.RED);
            g.drawString(vList.get(i).name, x + cSize + i * cSize, y);
            g.drawString(vList.get(i).name, x, cSize + i * cSize + y);
            g.setColor(Color.black);
            for (int j = 0; j < vList.size(); j++) {
                g.drawString("" + adjacencyMatrix[i][j], x + cSize * (j + 1), y + cSize * (i + 1));
            }
        }
    }

    public void drawDistanceMatrix(Graphics g, Vector<Vertex> vList, int x, int y) {
        int cSize = 20;
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(x, y - 30, vList.size() * cSize + cSize, vList.size() * cSize + cSize);
        g.setColor(Color.black);
        g.drawString("ShortestPathMatrix", x, y - cSize);
        for (int i = 0; i < vList.size(); i++) {
            g.setColor(Color.RED);
            g.drawString(vList.get(i).name, x + cSize + i * cSize, y);
            g.drawString(vList.get(i).name, x, cSize + i * cSize + y);
            g.setColor(Color.black);
            for (int j = 0; j < vList.size(); j++) {
                g.drawString("" + distanceMatrix[i][j], x + cSize * (j + 1), y + cSize * (i + 1));
            }
        }
    }

    public Vector<Vertex> vertexConnectivity(Vector<Vertex> vList) {
        Vector<Vertex> origList = new Vector<Vertex>();
        Vector<Vertex> tempList = new Vector<Vertex>();
        Vector<Vertex> toBeRemoved = new Vector<Vertex>();
        Vertex victim;

        origList.setSize(vList.size());
        Collections.copy(origList, vList);

        int maxPossibleRemove = 0;
        while (graphConnectivity(origList)) {
            Collections.sort(origList, new ascendingDegreeComparator());
            maxPossibleRemove = origList.firstElement().getDegree();

            for (Vertex v : origList) {
                if (v.getDegree() == maxPossibleRemove) {
                    for (Vertex z : v.connectedVertices) {
                        if (!tempList.contains(z)) {
                            tempList.add(z);
                        }
                    }
                }
            }

            while (graphConnectivity(origList) && tempList.size() > 0) {
                Collections.sort(tempList, new descendingDegreeComparator());
                victim = tempList.firstElement();
                tempList.removeElementAt(0);
                origList.remove(victim);
                for (Vertex x : origList) {
                    x.connectedVertices.remove(victim);
                }
                toBeRemoved.add(victim);
            }
            tempList.removeAllElements();
        }

        return toBeRemoved;
    }

    private boolean graphConnectivity(Vector<Vertex> vList) {
        if (vList == null || vList.size() == 0) {
            return true;
        }

        Vector<Vertex> visitedList = new Vector<Vertex>();
        Vertex start = vList.firstElement();
        visitedList.add(start);
        recurseGraphConnectivity(start, visitedList, vList); // recursive function

        return visitedList.size() == vList.size();
    }

    private void recurseGraphConnectivity(Vertex current, Vector<Vertex> visitedList, Vector<Vertex> allVertices) {
        for (Vertex v : current.connectedVertices) {
            if (!visitedList.contains(v) && allVertices.contains(v)) {
                visitedList.add(v);
                recurseGraphConnectivity(v, visitedList, allVertices);
            }
        }
        // Note: this follows outgoing edges only (like original).
        // If you want to consider incoming edges as well, you would
        // need to iterate through allVertices and check who points to 'current'.
    }

    private class ascendingDegreeComparator implements Comparator<Vertex> {

        @Override
        public int compare(Vertex v1, Vertex v2) {
            if (v1.getDegree() > v2.getDegree()) {
                return 1;
            } else if (v1.getDegree() < v2.getDegree()) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    private class descendingDegreeComparator implements Comparator<Vertex> {

        @Override
        public int compare(Vertex v1, Vertex v2) {
            if (v1.getDegree() > v2.getDegree()) {
                return -1;
            } else if (v1.getDegree() < v2.getDegree()) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    private class descendingWidthComparator implements Comparator<Vector<Vertex>> {

        @Override
        public int compare(Vector<Vertex> v1, Vector<Vertex> v2) {

            if (v1.size() > v2.size()) {
                return -1;
            } else if (v1.size() < v2.size()) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
