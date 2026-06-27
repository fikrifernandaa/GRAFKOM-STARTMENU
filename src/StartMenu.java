import javax.swing.*;

/**
 * ============================================================
 *  CLASSIC START MENU - Final Project Grafika Komputer
 *  Java Swing (library dasar) - Tanpa JavaFX / Qt / ImGui
 *
 *  Cara compile & run:
 *    javac *.java
 *    java StartMenu
 *
 *  Atau buka di IDE (IntelliJ / Eclipse / NetBeans) lalu Run.
 * ============================================================
 */
public class StartMenu extends JFrame {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StartMenu app = new StartMenu();
            app.setVisible(true);
        });
    }

    public StartMenu() {
        setTitle("Classic Start Menu - Final Project Grafika Komputer");

        DesktopPanel desktop = new DesktopPanel();
        setContentPane(desktop);

        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
    }
}