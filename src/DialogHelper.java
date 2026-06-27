import javax.swing.*;
import java.awt.*;

/**
 * Helper untuk membuka dialog/window tambahan:
 * - openAppWindow   : jendela dummy aplikasi (Notepad, Calculator, dll.)
 * - openFolderWindow: jendela file manager untuk folder
 * - openRunDialog   : dialog "Run..."
 * - openShutdownDialog: dialog pilihan Power/Shutdown
 */
public class DialogHelper {

    /**
     * Membuka jendela dummy aplikasi berdasarkan nama app.
     * Mengembalikan pesan status yang bisa ditampilkan di taskbar.
     */
    public static String openAppWindow(Component parent, String appName) {
        JDialog dlg = new JDialog(
                (JFrame) SwingUtilities.getWindowAncestor(parent),
                appName, false);
        dlg.setSize(500, 380);
        dlg.setLocationRelativeTo(parent);
        dlg.setResizable(true);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(new Color(30, 30, 40));

        // Title bar
        JPanel titleBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        titleBar.setBackground(new Color(20, 60, 140));
        JLabel title = new JLabel(appName);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleBar.add(title);
        content.add(titleBar, BorderLayout.NORTH);

        // Konten app dari AppFactory
        content.add(AppFactory.getAppContent(appName), BorderLayout.CENTER);

        // Status bar bawah
        JLabel status = new JLabel("  " + appName + " - Ready");
        status.setForeground(new Color(150, 150, 180));
        status.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        status.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(50, 55, 80)));
        status.setOpaque(true);
        status.setBackground(new Color(22, 24, 38));
        status.setPreferredSize(new Dimension(0, 22));
        content.add(status, BorderLayout.SOUTH);

        dlg.setContentPane(content);
        dlg.setVisible(true);

        return "Membuka: " + appName;
    }

    /**
     * Membuka jendela File Manager untuk folder tertentu.
     */
    public static String openFolderWindow(Component parent, String folderName) {
        JDialog dlg = new JDialog(
                (JFrame) SwingUtilities.getWindowAncestor(parent),
                "📁 " + folderName, false);
        dlg.setSize(460, 340);
        dlg.setLocationRelativeTo(parent);
        dlg.setContentPane(AppFactory.makeFileManager());
        dlg.setVisible(true);

        return "Membuka: " + folderName;
    }

    /**
     * Membuka dialog Run.
     * Mengembalikan pesan status hasil perintah yang dijalankan.
     */
    public static String openRunDialog(Component parent) {
        final String[] result = {""}; // untuk menangkap status

        JDialog dlg = new JDialog(
                (JFrame) SwingUtilities.getWindowAncestor(parent),
                "Run", true);
        dlg.setSize(380, 160);
        dlg.setLocationRelativeTo(parent);
        dlg.setResizable(false);

        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        p.add(new JLabel("Buka program, folder, dokumen, atau situs web:"), BorderLayout.NORTH);

        JTextField tf = new JTextField("notepad");
        p.add(tf, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton ok = new JButton("OK");
        ok.addActionListener(e -> {
            result[0] = "Menjalankan: " + tf.getText();
            dlg.dispose();
        });
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> dlg.dispose());
        btns.add(ok);
        btns.add(cancel);
        p.add(btns, BorderLayout.SOUTH);

        dlg.setContentPane(p);
        dlg.setVisible(true);

        return result[0];
    }

    /**
     * Membuka dialog pilihan Power/Shutdown.
     * Mengembalikan pesan status hasil pilihan pengguna.
     */
    public static String openShutdownDialog(Component parent) {
        String[] options = {"Shutdown", "Restart", "Sleep", "Batal"};
        int choice = JOptionPane.showOptionDialog(
                parent,
                "Pilih tindakan daya:",
                "Power Options",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]
        );

        switch (choice) {
            case 0: return "Sedang mematikan komputer... (simulasi)";
            case 1: return "Sedang me-restart... (simulasi)";
            case 2: return "Komputer dalam mode Sleep (simulasi)";
            default: return "";
        }
    }
}