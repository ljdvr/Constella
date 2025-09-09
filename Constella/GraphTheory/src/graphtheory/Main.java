package graphtheory;

import java.util.Date;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) throws Exception {

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        Date date = new Date();
        new MenuPage().setVisible(true);
    }
}