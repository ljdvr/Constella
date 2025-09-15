package graphtheory;

/**
 *
 * @author mk
 */
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.Vector;
import javax.swing.*;
import java.awt.BasicStroke;

public class Canvas {

    public JFrame frame;
    private JMenuBar menuBar;
    private CanvasPane canvas;
    private Graphics2D graphic;
    private Color backgroundColour;
    private Image canvasImage, canvasImage2;
    private int selectedTool;
    private int selectedWindow;
    private Dimension screenSize;
    public int width, height;
    private int clickedVertexIndex;
    private int clickedEdgeIndex;
    private FileManager fileManager = new FileManager();

    /////////////
    private Vector<Vertex> vertexList;
    private Vector<Edge> edgeList;
    private GraphProperties gP = new GraphProperties();
    private Vertex selectedVertexForProperties = null;
    private String degreeInfo = "";
    private String componentInfo = "";
    /////////////

    // Space/constellation theme elements
    private BufferedImage starfield;
    private Random random = new Random();

    public Canvas(String title, int width, int height, Color bgColour) {
        frame = new JFrame();
        frame.setTitle(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        canvas = new CanvasPane();
        InputListener inputListener = new InputListener();
        canvas.addMouseListener(inputListener);
        canvas.addMouseMotionListener(inputListener);

        this.width = width;
        this.height = height;
        canvas.setPreferredSize(new Dimension(width, height));

        // Create starfield background
        createStarfield();

        // --- Menu Bar (only File, Extras, Window; Tools removed) ---
        menuBar = new JMenuBar();
        menuBar.setBackground(new Color(10, 10, 40));

        JMenu menuOptions1 = new JMenu("File");
        JMenu menuOptions2 = new JMenu("Extras");
        JMenu menuOptions3 = new JMenu("Window");

        styleMenu(menuOptions1);
        styleMenu(menuOptions2);
        styleMenu(menuOptions3);

        JMenuItem item = createMenuItem("Open Constellation", KeyEvent.VK_O);
        item.addActionListener(new MenuListener());
        menuOptions1.add(item);

        item = createMenuItem("Save Constellation", KeyEvent.VK_S);
        item.addActionListener(new MenuListener());
        menuOptions1.add(item);

        item = createMenuItem("Auto Arrange Stars", 0);
        item.addActionListener(new MenuListener());
        menuOptions2.add(item);

        item = createMenuItem("Clear Sky", 0);
        item.addActionListener(new MenuListener());
        menuOptions2.add(item);

        item = createMenuItem("Constellation View", 0);
        item.addActionListener(new MenuListener());
        menuOptions3.add(item);

        item = createMenuItem("Stellar Properties", 0);
        item.addActionListener(new MenuListener());
        menuOptions3.add(item);

        menuBar.add(menuOptions1);
        menuBar.add(menuOptions2);
        menuBar.add(menuOptions3);

        frame.setJMenuBar(menuBar);

        // --- Bottom Panel with Tool Buttons ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        bottomPanel.setOpaque(false);

        JButton addStarBtn = new JButton("Add Star");
        JButton connectStarsBtn = new JButton("Connect Stars");
        JButton moveToolBtn = new JButton("Move Tool");
        JButton removeToolBtn = new JButton("Remove Tool");
        JButton viewPropertiesBtn = new JButton("View Properties");


        for (JButton btn : new JButton[]{addStarBtn, connectStarsBtn, moveToolBtn, removeToolBtn, viewPropertiesBtn}) {
            btn.setFocusPainted(false);
            btn.setBackground(new Color(30, 30, 80, 180));
            btn.setForeground(Color.BLACK);
            bottomPanel.add(btn);
        }

        // Wrap canvas in a layered pane so buttons overlay on top
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(width, height));

        canvas.setBounds(0, 0, width, height);

        // Place buttons just below the status text
        int statusY = height / 2 + (height * 2) / 5; // same Y as status text
        bottomPanel.setBounds(30, statusY + 10, width, 30); // centered across width

        layeredPane.add(canvas, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(bottomPanel, JLayeredPane.PALETTE_LAYER);

        frame.setContentPane(layeredPane);

        addStarBtn.addActionListener(e -> {
            selectedTool = 1;
            selectedVertexForProperties = null;
            degreeInfo = "";
            componentInfo = "";
            refresh();
        });

        connectStarsBtn.addActionListener(e -> {
            selectedTool = 2;
            selectedVertexForProperties = null;
            degreeInfo = "";
            componentInfo = "";
            refresh();
        });

        moveToolBtn.addActionListener(e -> {
            selectedTool = 3;
            selectedVertexForProperties = null;
            degreeInfo = "";
            componentInfo = "";
            refresh();
        });

        removeToolBtn.addActionListener(e -> {
            selectedTool = 4;
            selectedVertexForProperties = null;
            degreeInfo = "";
            componentInfo = "";
            refresh();
        });

        viewPropertiesBtn.addActionListener(e -> {
            selectedTool = 5;
            refresh();
        });

        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setBounds(screenSize.width / 2 - width / 2, screenSize.height / 2 - height / 2, width, height);
        frame.pack();
        setVisible(true);

        vertexList = new Vector<Vertex>();
        edgeList = new Vector<Edge>();
    }

    private void styleMenu(JMenu menu) {
        menu.setForeground(Color.WHITE);
        menu.setBackground(new Color(10, 10, 40));
        menu.setOpaque(true);
    }

    private JMenuItem createMenuItem(String text, int acceleratorKey) {
        JMenuItem item = new JMenuItem(text);
        if (acceleratorKey != 0) {
            item.setAccelerator(KeyStroke.getKeyStroke(acceleratorKey, KeyEvent.CTRL_DOWN_MASK));
        }
        item.setForeground(Color.WHITE);
        item.setBackground(new Color(20, 20, 60));
        item.setOpaque(true);
        return item;
    }

    private void createStarfield() {
        starfield = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = starfield.createGraphics();

        g.setColor(new Color(5, 5, 30));
        g.fillRect(0, 0, width, height);

        g.setColor(Color.WHITE);
        for (int i = 0; i < 200; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int size = random.nextInt(3) + 1;
            g.fillOval(x, y, size, size);
        }

        g.setColor(new Color(200, 200, 255));
        for (int i = 0; i < 20; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int size = random.nextInt(4) + 2;
            g.fillOval(x, y, size, size);
        }

        g.dispose();
    }

    class InputListener implements MouseListener, MouseMotionListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (selectedWindow == 0) {
                switch (selectedTool) {
                    case 1: { // Add Star
                        Vertex v = new Vertex("" + vertexList.size(), e.getX(), e.getY());
                        vertexList.add(v);
                        v.draw(graphic);
                        break;
                    }
                    case 4: { // Remove Tool
                        boolean vertexRemoved = false;
                        for (int i = vertexList.size() - 1; i >= 0; i--) {
                            Vertex vertex = vertexList.get(i);
                            if (vertex.hasIntersection(e.getX(), e.getY())) {
                                for (int j = edgeList.size() - 1; j >= 0; j--) {
                                    Edge edge = edgeList.get(j);
                                    if (edge.vertex1 == vertex || edge.vertex2 == vertex) {
                                        if (edge.vertex1 == vertex) {
                                            edge.vertex2.connectedVertices.remove(vertex);
                                        } else {
                                            edge.vertex1.connectedVertices.remove(vertex);
                                        }
                                        edgeList.remove(j);
                                    }
                                }
                                vertexList.remove(i);
                                vertexRemoved = true;
                                break;
                            }
                        }
                        

                        if (!vertexRemoved) {
                            for (int i = edgeList.size() - 1; i >= 0; i--) {
                                Edge edge = edgeList.get(i);
                                if (edge.hasIntersection(e.getX(), e.getY())) {
                                    edge.vertex1.connectedVertices.remove(edge.vertex2);
                                    edge.vertex2.connectedVertices.remove(edge.vertex1);
                                    edgeList.remove(i);
                                    break;
                                }
                            }
                        }
                        break;
                    }
                    case 5: { 
                        selectedVertexForProperties = null;
                        degreeInfo = "";
                        componentInfo = "";
                        
                        for (Edge d : edgeList) {
                            d.wasFocused = false;
                        }
                        
                        for (int i = vertexList.size() - 1; i >= 0; i--) {
                            Vertex vertex = vertexList.get(i);
                            if (vertex.hasIntersection(e.getX(), e.getY())) {
                                selectedVertexForProperties = vertex;
                                int degree = vertex.getDegree();
                                degreeInfo = "Degree: " + degree;
                                
                                char componentLabel = gP.getComponentLabel(vertex, vertexList);
                                componentInfo = "Component: " + componentLabel;
                                break;
                            }
                        }
                        break;
                    }
                }
                refresh();
            }
        }
        @Override public void mouseEntered(MouseEvent e) {}
        @Override public void mouseExited(MouseEvent e) {}
        @Override
        public void mousePressed(MouseEvent e) {
            if (selectedWindow == 0 && vertexList.size() > 0) {
                switch (selectedTool) {
                    case 2: { // Connect Stars
                        for (Vertex v : vertexList) {
                            if (v.hasIntersection(e.getX(), e.getY())) {
                                v.wasClicked = true;
                                clickedVertexIndex = vertexList.indexOf(v);
                            } else v.wasClicked = false;
                        }
                        break;
                    }
                    case 3: { // Move Tool
                        for (Edge d : edgeList) {
                            if (d.hasIntersection(e.getX(), e.getY())) {
                                d.wasClicked = true;
                                clickedEdgeIndex = edgeList.indexOf(d);
                            } else d.wasClicked = false;
                        }
                        for (Vertex v : vertexList) {
                            if (v.hasIntersection(e.getX(), e.getY())) {
                                v.wasClicked = true;
                                clickedVertexIndex = vertexList.indexOf(v);
                            } else v.wasClicked = false;
                        }
                        break;
                    }
                    case 4: { // Remove Tool - Visual feedback when hovering over items to remove
                        for (Edge d : edgeList) {
                            d.wasClicked = d.hasIntersection(e.getX(), e.getY());
                        }
                        for (Vertex v : vertexList) {
                            v.wasClicked = v.hasIntersection(e.getX(), e.getY());
                        }
                        break;
                    }
                }
                refresh();
            }
        }
        @Override
        public void mouseReleased(MouseEvent e) {
            if (selectedWindow == 0 && vertexList.size() > 0) {
                switch (selectedTool) {
                    case 2: {
                        Vertex parentV = vertexList.get(clickedVertexIndex);
                        for (Vertex v : vertexList) {
                            if (v.hasIntersection(e.getX(), e.getY()) && v != parentV && !v.connectedToVertex(parentV)) {
                                Edge edge = new Edge(v, parentV);
                                v.addVertex(parentV);
                                parentV.addVertex(v);
                                v.wasClicked = false;
                                parentV.wasClicked = false;
                                edgeList.add(edge);
                            } else v.wasClicked = false;
                        }
                        break;
                    }
                    case 3: {
                        vertexList.get(clickedVertexIndex).wasClicked = false;
                        break;
                    }
                }
            }
            erase();
            refresh();
        }
        @Override
        public void mouseDragged(MouseEvent e) {
            if (selectedWindow == 0 && vertexList.size() > 0) {
                erase();
                switch (selectedTool) {
                    case 2: {
                        graphic.setColor(new Color(100, 200, 255, 150));
                        drawLine(vertexList.get(clickedVertexIndex).location.x,
                                vertexList.get(clickedVertexIndex).location.y,
                                e.getX(), e.getY());
                        break;
                    }
                    case 3: {
                        if (vertexList.get(clickedVertexIndex).wasClicked) {
                            vertexList.get(clickedVertexIndex).location.x = e.getX();
                            vertexList.get(clickedVertexIndex).location.y = e.getY();
                        }
                        break;
                    }
                }
                refresh();
            }
        }
        @Override
        public void mouseMoved(MouseEvent e) {
            if (selectedWindow == 0) {
                if (selectedTool != 5) {
                    for (Edge d : edgeList) {
                        d.wasFocused = d.hasIntersection(e.getX(), e.getY());
                    }
                }
                for (Vertex v : vertexList) {
                    v.wasFocused = v.hasIntersection(e.getX(), e.getY());
                }
                refresh();
            }
        }
    }

    class MenuListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equals("Auto Arrange Stars")) {
                arrangeVertices();
                erase();
            } else if (command.equals("Clear Sky")) {
                edgeList.removeAllElements();
                vertexList.removeAllElements();
                clickedVertexIndex = 0;
                erase();
            } else if (command.equals("Open Constellation")) {
                int returnValue = fileManager.jF.showOpenDialog(frame);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    loadFile(fileManager.loadFile(fileManager.jF.getSelectedFile()));
                    selectedWindow = 0;
                }
            } else if (command.equals("Save Constellation")) {
                int returnValue = fileManager.jF.showSaveDialog(frame);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    fileManager.saveFile(vertexList, fileManager.jF.getSelectedFile());
                }
            } else if (command.equals("Constellation View")) {
                selectedWindow = 0;
                erase();
            } else if (command.equals("Stellar Properties")) {
                selectedWindow = 1;
                if (vertexList.size() > 0) {
                    int[][] matrix = gP.generateAdjacencyMatrix(vertexList, edgeList);
                    Vector<Vertex> tempList = gP.vertexConnectivity(vertexList);
                    for (Vertex v : tempList) {
                        vertexList.get(vertexList.indexOf(v)).wasClicked = true;
                    }
                    reloadVertexConnections(matrix, vertexList);
                    gP.generateDistanceMatrix(vertexList);
                    
                    int componentCount = gP.countComponents(vertexList);
                    System.out.println("Graph has " + componentCount + " component(s)");
                    System.out.println("Vertex degrees:");
                    for (Vertex v : vertexList) {
                        System.out.println("  " + v.name + ": " + v.getDegree());
                    }
                    
                    gP.displayContainers(vertexList);
                }
                erase();
            }
            refresh();
        }
    }

    private void arrangeVertices() {
        double deg2rad = Math.PI / 180;
        double radius = height / 5;
        double centerX = width / 2;
        double centerY = height / 2;
        int interval = 360 / vertexList.size();

        for (int i = 0; i < vertexList.size(); i++) {
            double degInRad = i * deg2rad * interval;
            double x = centerX + (Math.cos(degInRad) * radius);
            double y = centerY + (Math.sin(degInRad) * radius);
            vertexList.get(i).location.x = (int) x;
            vertexList.get(i).location.y = (int) y;
        }
    }

    private void reloadVertexConnections(int[][] aMatrix, Vector<Vertex> vList) {
        for (Vertex v : vList) {
            v.connectedVertices.clear();
        }
        for (int i = 0; i < aMatrix.length; i++) {
            for (int j = 0; j < aMatrix.length; j++) {
                if (aMatrix[i][j] == 1) {
                    vList.get(i).addVertex(vList.get(j));
                }
            }
        }
    }

    private void loadFile(Vector<Vector> File) {
        vertexList = File.firstElement();
        edgeList = File.lastElement();
        erase();
    }

    public void refresh() {
    graphic.drawImage(starfield, 0, 0, null);
    
    for (Edge e : edgeList) {
        e.draw(graphic);
    }
    for (Vertex v : vertexList) {
        v.draw(graphic);
    }
    
    // Draw degree information if a vertex is selected
    if (selectedTool == 5 && selectedVertexForProperties != null) {
        drawDegreeInfo(graphic);
    }
    
    canvas.repaint();
}
    private void drawDegreeInfo(Graphics g) {
        if (selectedVertexForProperties != null) {
            Graphics2D g2d = (Graphics2D) g;
            
            g2d.setColor(new Color(100, 100, 255, 100)); // Blue glow
            g2d.fillOval(selectedVertexForProperties.location.x - 25, 
                        selectedVertexForProperties.location.y - 25, 50, 50);
            
            g2d.setColor(new Color(100, 100, 255)); // Blue color for edges
            g2d.setStroke(new BasicStroke(4f));
            
            for (Vertex connected : selectedVertexForProperties.connectedVertices) {
                g.drawLine(selectedVertexForProperties.location.x, 
                        selectedVertexForProperties.location.y,
                        connected.location.x, connected.location.y);
            }
            g2d.setStroke(new BasicStroke(1f));
            
            int popupX = selectedVertexForProperties.location.x + 40;
            int popupY = selectedVertexForProperties.location.y - 30;
            
            int popupWidth = 120;
            int popupHeight = 50;
            
            g2d.setColor(new Color(0, 0, 0, 220)); // Semi-transparent black
            g2d.fillRoundRect(popupX - 15, popupY - 25, popupWidth, popupHeight, 15, 15);
            
            g2d.setColor(Color.YELLOW); // Yellow border
            g2d.setStroke(new BasicStroke(2f));
            g2d.drawRoundRect(popupX - 15, popupY - 25, popupWidth, popupHeight, 15, 15);
            g2d.setStroke(new BasicStroke(1f));
            
            g2d.setColor(Color.YELLOW); // Yellow text
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.drawString(degreeInfo, popupX - 10, popupY - 5);
            g2d.drawString(componentInfo, popupX - 10, popupY + 15);
            
            g2d.setColor(Color.YELLOW); // Yellow line
            g2d.setStroke(new BasicStroke(2f));
            g2d.drawLine(popupX, popupY - 10, 
                        selectedVertexForProperties.location.x + 15, 
                        selectedVertexForProperties.location.y);
            g2d.setStroke(new BasicStroke(1f));
        }
    }

    public void setVisible(boolean visible) {
        if (graphic == null) {
            Dimension size = canvas.getSize();
            canvasImage = canvas.createImage(size.width, size.height);
            canvasImage2 = canvas.createImage(size.width, size.height);
            graphic = (Graphics2D) canvasImage.getGraphics();
            graphic.setColor(backgroundColour);
            graphic.fillRect(0, 0, size.width, size.height);
            graphic.setColor(new Color(200, 200, 255));
        }
        frame.setVisible(visible);
    }

    public boolean isVisible() {
        return frame.isVisible();
    }

    public void erase() {
        graphic.setColor(new Color(0, 0, 20, 200));
        graphic.fillRect(0, 0, width, height);
    }

    public void erase(int x, int y, int x1, int y2) {
        graphic.clearRect(x, y, x1, y2);
    }

    public void drawString(String text, int x, int y, float size) {
        Font orig = graphic.getFont();
        graphic.setFont(graphic.getFont().deriveFont(1, size));
        graphic.setColor(new Color(200, 200, 255));
        graphic.drawString(text, x, y);
        graphic.setFont(orig);
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        graphic.drawLine(x1, y1, x2, y2);
    }

    private class CanvasPane extends JPanel {
        public void paint(Graphics g) {
            switch (selectedWindow) {
                case 0: {
                    g.drawImage(canvasImage, 0, 0, null);
                    g.setColor(Color.WHITE);
                    
                    // Get component count
                    int componentCount = gP.countComponents(vertexList);
                    
                    g.drawString("Stars: " + vertexList.size() +
                            "  Connections: " + edgeList.size() +
                            "  Components: " + componentCount +
                            "  Tool: " + getToolName(selectedTool),
                            50, height / 2 + (height * 2) / 5);

                    break;
                }
                case 1: {
                    canvasImage2.getGraphics().clearRect(0, 0, width, height);
                    gP.drawAdjacencyMatrix(canvasImage2.getGraphics(), vertexList, width / 2 + 50, 50);
                    gP.drawDistanceMatrix(canvasImage2.getGraphics(), vertexList, width / 2 + 50, height / 2 + 50);
                    g.drawImage(canvasImage2, 0, 0, null);
                    drawString("Constellation becomes disconnected when red stars are removed.", 100, height - 30, 20);
                    g.drawString("See console for constellation diameter information", 100, height / 2 + 50);
                    g.drawImage(canvasImage.getScaledInstance(width / 2, height / 2, Image.SCALE_SMOOTH), 0, 0, null);
                    g.draw3DRect(0, 0, width / 2, height / 2, true);
                    g.setColor(new Color(200, 200, 255));
                    break;
                }
            }
        }
    }

    private String getToolName(int toolId) {
        switch (toolId) {
            case 1: return "Add Star";
            case 2: return "Connect Stars";
            case 3: return "Move Tool";
            case 4: return "Remove Tool";
            case 5: return "View Properties";
            default: return "Unknown";
        }
    }
}