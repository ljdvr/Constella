package graphtheory;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class MenuPage extends JFrame {

    private BufferedImage starfield;
    private Image backgroundImage;
    private int width = 600, height = 400;
    private Random random = new Random();

    public MenuPage() {
        setTitle("Constella - Menu");
        setSize(width, height);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create starfield background
        createStarfield();

        // Load PNG (transparent background)
        backgroundImage = new ImageIcon(getClass().getResource("/graphtheory/Constella.png")).getImage();

        JPanel panel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Draw starfield stretched to window size
                g.drawImage(starfield, 0, 0, getWidth(), getHeight(), null);

                // Draw PNG (centered, scaled dynamically)
                int maxWidth = (int) (getWidth() * 0.9);
                int maxHeight = (int) (getHeight() * 0.9);
                double aspectRatio = (double) backgroundImage.getWidth(null) / backgroundImage.getHeight(null);

                int newWidth = maxWidth;
                int newHeight = (int) (newWidth / aspectRatio);

                if (newHeight > maxHeight) {
                    newHeight = maxHeight;
                    newWidth = (int) (newHeight * aspectRatio);
                }

                int x = (getWidth() - newWidth) / 2;
                int y = (getHeight() - newHeight) / 2;

                g.drawImage(backgroundImage, x, y, newWidth, newHeight, null);
            }
        };

        // Start button
        JButton startButton = new JButton("START");
        startButton.setFont(new Font("Arial", Font.BOLD, 15));
        startButton.setFocusPainted(false);

        // Position the button (absolute layout, center)
        int btnWidth = 100, btnHeight = 50;
        startButton.setBounds((width - btnWidth) / 2, (height - btnHeight) / 2 + 80, btnWidth, btnHeight);

        startButton.addActionListener(e -> {
            dispose();
            new Canvas("Constella - Graph Theory", 800, 600, Color.BLACK);
        });

        panel.add(startButton);
        add(panel);

        // Resize listener → keeps button centered when window resizes
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int btnX = (panel.getWidth() - btnWidth) / 2;
                int btnY = (panel.getHeight() - btnHeight) / 2 + 80;
                startButton.setBounds(btnX, btnY, btnWidth, btnHeight);
                panel.repaint();
            }
        });
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MenuPage().setVisible(true);
        });
    }
}
