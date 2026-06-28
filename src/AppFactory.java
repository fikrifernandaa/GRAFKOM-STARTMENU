import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Factory class untuk membuat konten panel setiap aplikasi dummy.
 * Setiap metode mengembalikan JPanel yang siap ditampilkan di dalam JDialog.
 */
public class AppFactory {

    /**
     * Memilih dan mengembalikan panel konten berdasarkan nama aplikasi.
     */
    public static JPanel getAppContent(String appName) {
        switch (appName) {
            case "Notepad":        return makeNotepad();
            case "Calculator":     return makeCalculator();
            case "Paint":          return makePaint();
            case "Browser":        return makeBrowser();
            case "Music Player":   return makeMusicPlayer();
            case "Video Player":   return makeVideoPlayer();
            case "File Manager":   return makeFileManager();
            case "Command Prompt": return makeCommandPrompt();
            default:               return makeDefaultPanel(appName);
        }
    }

    // ----------------------------------------------------------------
    //  Panel default (app tidak dikenali)
    // ----------------------------------------------------------------
    private static JPanel makeDefaultPanel(String appName) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(new Color(245, 245, 248));
        JLabel lbl = new JLabel("[ " + appName + " ]");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbl.setForeground(new Color(100, 150, 220));
        p.add(lbl);
        return p;
    }

    // ----------------------------------------------------------------
    //  Notepad
    // ----------------------------------------------------------------
    public static JPanel makeNotepad() {
        JPanel p = new JPanel(new BorderLayout());

        JMenuBar mb = new JMenuBar();
        mb.setBackground(new Color(240, 240, 245));
        for (String m : new String[]{"File", "Edit", "Format", "View", "Help"}) {
            JMenu menu = new JMenu(m);
            menu.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            mb.add(menu);
        }
        p.add(mb, BorderLayout.NORTH);

        JTextArea ta = new JTextArea("Selamat datang di Notepad!\n\nKetik teks di sini...");
        ta.setFont(new Font("Consolas", Font.PLAIN, 13));
        ta.setBackground(Color.WHITE);
        ta.setForeground(new Color(30, 30, 30));
        ta.setCaretColor(new Color(30, 30, 30));
        p.add(new JScrollPane(ta), BorderLayout.CENTER);

        return p;
    }

    // ----------------------------------------------------------------
    //  Calculator
    // ----------------------------------------------------------------
    public static JPanel makeCalculator() {
        JPanel p = new JPanel(new BorderLayout(4, 4));
        p.setBackground(new Color(32, 32, 48));
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        final String[] disp   = {"0"};
        final double[] mem    = {0, 0};
        final boolean[] newNum = {true};

        JLabel display = new JLabel("0", SwingConstants.RIGHT);
        display.setFont(new Font("Segoe UI Light", Font.PLAIN, 36));
        display.setForeground(Color.WHITE);
        display.setOpaque(true);
        display.setBackground(new Color(22, 24, 38));
        display.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        p.add(display, BorderLayout.NORTH);

        String[] btns = {
                "%", "CE", "C", "⌫",
                "1/x","x²","√x","÷",
                "7","8","9","×",
                "4","5","6","−",
                "1","2","3","+",
                "±","0",".","="
        };

        JPanel grid = new JPanel(new GridLayout(6, 4, 4, 4));
        grid.setBackground(new Color(32, 32, 48));
        final String[] op = {""};

        for (String btn : btns) {
            JButton b = new JButton(btn);
            b.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            b.setFocusPainted(false);
            b.setBorderPainted(false);

            boolean isOp   = btn.matches("[÷×−+=]") || btn.equals("=");
            boolean isSpec = btn.matches("%|CE|C|⌫|1/x|x²|√x|±");
            b.setBackground(btn.equals("=")  ? new Color(0, 100, 200)
                    : isOp                   ? new Color(50, 55, 80)
                    : isSpec                 ? new Color(45, 48, 70)
                    : new Color(60, 64, 90));
            b.setForeground(Color.WHITE);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            b.addActionListener(ev -> {
                try {
                    if (btn.matches("[0-9]|\\.")) {
                        if (newNum[0]) {
                            disp[0] = btn.equals(".") ? "0." : btn;
                            newNum[0] = false;
                        } else {
                            disp[0] = disp[0].equals("0") && !btn.equals(".") ? btn : disp[0] + btn;
                        }
                    } else if (btn.equals("C") || btn.equals("CE")) {
                        disp[0] = "0"; newNum[0] = true;
                    } else if (btn.equals("⌫")) {
                        disp[0] = disp[0].length() > 1
                                ? disp[0].substring(0, disp[0].length() - 1) : "0";
                    } else if (btn.matches("[÷×−+]")) {
                        mem[0] = Double.parseDouble(disp[0]);
                        op[0]  = btn;
                        newNum[0] = true;
                    } else if (btn.equals("=")) {
                        double a = mem[0], b2 = Double.parseDouble(disp[0]), r = 0;
                        switch (op[0]) {
                            case "+": r = a + b2; break;
                            case "−": r = a - b2; break;
                            case "×": r = a * b2; break;
                            case "÷": r = b2 != 0 ? a / b2 : 0; break;
                            default:  r = b2;
                        }
                        disp[0]  = r == (long) r ? String.valueOf((long) r) : String.format("%.6f", r);
                        newNum[0] = true;
                    } else if (btn.equals("%")) {
                        disp[0] = String.valueOf(Double.parseDouble(disp[0]) / 100);
                    } else if (btn.equals("±")) {
                        double v = Double.parseDouble(disp[0]);
                        disp[0] = v == (long) (-v) ? String.valueOf((long) (-v)) : String.valueOf(-v);
                    } else if (btn.equals("√x")) {
                        double v = Math.sqrt(Double.parseDouble(disp[0]));
                        disp[0] = v == (long) v ? String.valueOf((long) v) : String.format("%.4f", v);
                    } else if (btn.equals("x²")) {
                        double v = Math.pow(Double.parseDouble(disp[0]), 2);
                        disp[0] = v == (long) v ? String.valueOf((long) v) : String.format("%.4f", v);
                    } else if (btn.equals("1/x")) {
                        double v = Double.parseDouble(disp[0]);
                        disp[0] = v != 0 ? String.format("%.6f", 1.0 / v) : "Error";
                    }
                } catch (Exception ex) {
                    disp[0] = "Error";
                }
                display.setText(disp[0]);
            });
            grid.add(b);
        }
        p.add(grid, BorderLayout.CENTER);
        return p;
    }

    // ----------------------------------------------------------------
    //  Paint
    // ----------------------------------------------------------------
    public static JPanel makePaint() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(240, 240, 245));

        // Toolbar warna
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        toolbar.setBackground(new Color(230, 232, 240));
        toolbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 215)));

        final Color[] selColor = {Color.BLACK};
        final int[]   brushSize = {4};

        Color[] palette = {
                Color.BLACK, Color.WHITE, Color.RED, new Color(0, 120, 215),
                Color.GREEN, Color.YELLOW, Color.ORANGE, Color.MAGENTA,
                new Color(139, 69, 19), new Color(0, 200, 180)
        };

        for (Color c : palette) {
            JButton cb = new JButton();
            cb.setBackground(c);
            cb.setPreferredSize(new Dimension(22, 22));
            cb.setBorderPainted(true);
            cb.setFocusPainted(false);
            cb.addActionListener(e -> selColor[0] = c);
            toolbar.add(cb);
        }

        toolbar.add(new JLabel("  Ukuran: "));
        JComboBox<String> sizeBox = new JComboBox<>(new String[]{"2", "4", "8", "14"});
        sizeBox.setSelectedIndex(1);
        sizeBox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sizeBox.addActionListener(e ->
                brushSize[0] = Integer.parseInt((String) sizeBox.getSelectedItem()));
        toolbar.add(sizeBox);
        p.add(toolbar, BorderLayout.NORTH);

        // Canvas
        BufferedImage canvas = new BufferedImage(494, 290, BufferedImage.TYPE_INT_RGB);
        Graphics2D cg = canvas.createGraphics();
        cg.setColor(Color.WHITE);
        cg.fillRect(0, 0, 494, 290);
        cg.dispose();

        JPanel paintArea = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(canvas, 0, 0, null);
            }
        };
        paintArea.setBackground(Color.WHITE);

        final int[] lastPt = {-1, -1};
        MouseAdapter ma = new MouseAdapter() {
            void draw(MouseEvent e) {
                Graphics2D cg2 = canvas.createGraphics();
                cg2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                cg2.setColor(selColor[0]);
                int bs = brushSize[0];
                if (lastPt[0] >= 0) {
                    cg2.setStroke(new BasicStroke(bs, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    cg2.drawLine(lastPt[0], lastPt[1], e.getX(), e.getY());
                }
                cg2.fillOval(e.getX() - bs / 2, e.getY() - bs / 2, bs, bs);
                lastPt[0] = e.getX();
                lastPt[1] = e.getY();
                cg2.dispose();
                paintArea.repaint();
            }
            public void mousePressed(MouseEvent e)  { draw(e); }
            public void mouseDragged(MouseEvent e)  { draw(e); }
            public void mouseReleased(MouseEvent e) { lastPt[0] = -1; lastPt[1] = -1; }
        };
        paintArea.addMouseListener(ma);
        paintArea.addMouseMotionListener(ma);

        p.add(new JScrollPane(paintArea), BorderLayout.CENTER);
        return p;
    }

    // ----------------------------------------------------------------
    //  Browser
    // ----------------------------------------------------------------
    public static JPanel makeBrowser() {
        JPanel p = new JPanel(new BorderLayout(0, 0));
        p.setBackground(Color.WHITE);

        JPanel nav = new JPanel(new BorderLayout(4, 0));
        nav.setBackground(new Color(240, 242, 248));
        nav.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));

        JTextField urlBar = new JTextField("https://www.google.com");
        urlBar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        urlBar.setBackground(Color.WHITE);
        urlBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 190, 215), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));

        JButton goBtn = new JButton("Go");
        goBtn.setBackground(new Color(0, 100, 200));
        goBtn.setForeground(Color.WHITE);
        goBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        goBtn.setBorderPainted(false);
        goBtn.setFocusPainted(false);

        nav.add(new JLabel("🔒 "), BorderLayout.WEST);
        nav.add(urlBar, BorderLayout.CENTER);
        nav.add(goBtn, BorderLayout.EAST);
        p.add(nav, BorderLayout.NORTH);

        // Halaman Google dummy
        JPanel page = new JPanel();
        page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS));
        page.setBackground(Color.WHITE);
        page.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel logo = new JLabel("Google");
        logo.setFont(new Font("Product Sans", Font.BOLD, 52));
        logo.setForeground(new Color(66, 133, 244));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField search = new JTextField(28);
        search.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        search.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 220), 1),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        search.setMaximumSize(new Dimension(380, 42));
        search.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        btnRow.setBackground(Color.WHITE);
        btnRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        for (String b : new String[]{"Google Search", "I'm Feeling Lucky"}) {
            JButton bb = new JButton(b);
            bb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            bb.setBackground(new Color(248, 249, 250));
            bb.setBorderPainted(true);
            bb.setFocusPainted(false);
            btnRow.add(bb);
        }

        page.add(Box.createVerticalStrut(40));
        page.add(logo);
        page.add(Box.createVerticalStrut(20));
        page.add(search);
        page.add(Box.createVerticalStrut(12));
        page.add(btnRow);
        p.add(new JScrollPane(page), BorderLayout.CENTER);
        return p;
    }

    // ----------------------------------------------------------------
    //  Music Player
    // ----------------------------------------------------------------
    public static JPanel makeMusicPlayer() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(18, 18, 28));

        // Album art
        JPanel art = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(80, 40, 120),
                        getWidth(), getHeight(), new Color(20, 80, 160));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255, 255, 255, 60));
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
                FontMetrics fm = g2.getFontMetrics();
                String note = "🎵";
                g2.drawString(note, (getWidth() - fm.stringWidth(note)) / 2, getHeight() / 2 + 20);
            }
        };
        art.setPreferredSize(new Dimension(160, 160));

        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBackground(new Color(18, 18, 28));
        right.setBorder(BorderFactory.createEmptyBorder(20, 16, 20, 16));

        String[] songs = {
                "01  Bohemian Rhapsody - Queen",
                "02  Shape of You - Ed Sheeran",
                "03  Blinding Lights - The Weeknd",
                "04  Levitating - Dua Lipa",
                "05  Stay - Kid LAROI"
        };

        JLabel nowPlaying = new JLabel("♪  " + songs[0].substring(4));
        nowPlaying.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nowPlaying.setForeground(Color.WHITE);
        nowPlaying.setAlignmentX(Component.LEFT_ALIGNMENT);
        right.add(nowPlaying);
        right.add(Box.createVerticalStrut(4));

        JLabel artist = new JLabel("   Queen  •  Greatest Hits");
        artist.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        artist.setForeground(new Color(150, 155, 180));
        artist.setAlignmentX(Component.LEFT_ALIGNMENT);
        right.add(artist);
        right.add(Box.createVerticalStrut(12));

        JProgressBar prog = new JProgressBar(0, 100);
        prog.setValue(35);
        prog.setBackground(new Color(50, 52, 75));
        prog.setForeground(new Color(0, 120, 215));
        prog.setMaximumSize(new Dimension(300, 4));
        prog.setBorderPainted(false);
        prog.setAlignmentX(Component.LEFT_ALIGNMENT);
        right.add(prog);
        right.add(Box.createVerticalStrut(4));

        JLabel time = new JLabel("   1:28 / 5:55");
        time.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        time.setForeground(new Color(120, 125, 155));
        time.setAlignmentX(Component.LEFT_ALIGNMENT);
        right.add(time);
        right.add(Box.createVerticalStrut(14));

        JPanel ctrl = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        ctrl.setBackground(new Color(18, 18, 28));
        ctrl.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (String btn : new String[]{"⏮", "⏪", "⏸", "⏩", "⏭"}) {
            JButton b = new JButton(btn);
            b.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            b.setBackground(new Color(40, 42, 62));
            b.setForeground(Color.WHITE);
            b.setFocusPainted(false);
            b.setBorderPainted(false);
            b.setPreferredSize(new Dimension(36, 36));
            ctrl.add(b);
        }
        right.add(ctrl);
        right.add(Box.createVerticalStrut(14));

        DefaultListModel<String> model = new DefaultListModel<>();
        for (String s : songs) model.addElement(s);
        JList<String> list = new JList<>(model);
        list.setBackground(new Color(25, 26, 40));
        list.setForeground(new Color(170, 175, 200));
        list.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        list.setSelectedIndex(0);
        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && list.getSelectedValue() != null) {
                nowPlaying.setText("♪  " + list.getSelectedValue().substring(4));
            }
        });
        JScrollPane sp = new JScrollPane(list);
        sp.setBorder(null);
        sp.setPreferredSize(new Dimension(0, 100));
        sp.setAlignmentX(Component.LEFT_ALIGNMENT);
        right.add(sp);

        p.add(art, BorderLayout.WEST);
        p.add(right, BorderLayout.CENTER);
        return p;
    }

    // ----------------------------------------------------------------
    //  Video Player
    // ----------------------------------------------------------------
    public static JPanel makeVideoPlayer() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.BLACK);

        JPanel screen = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(30, 30, 30));
                g2.fillRect(20, 15, getWidth() - 40, getHeight() - 40);
                g2.setColor(new Color(255, 255, 255, 80));
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
                g2.drawString("▶", getWidth() / 2 - 18, getHeight() / 2 + 16);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                g2.setColor(new Color(120, 125, 155));
                g2.drawString("Video Player - BGI Final Project.mp4", 30, getHeight() - 20);
            }
        };

        JPanel controls = new JPanel(new BorderLayout(4, 4));
        controls.setBackground(new Color(20, 20, 30));
        controls.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

        JProgressBar vp = new JProgressBar(0, 100);
        vp.setValue(20);
        vp.setBackground(new Color(50, 52, 70));
        vp.setForeground(new Color(220, 60, 60));
        vp.setBorderPainted(false);
        controls.add(vp, BorderLayout.NORTH);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 2));
        btnRow.setBackground(new Color(20, 20, 30));
        for (String b : new String[]{"⏮", "⏪", "⏸", "⏩", "⏭", "🔊", "⛶"}) {
            JButton btn = new JButton(b);
            btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            btn.setBackground(new Color(40, 42, 60));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setPreferredSize(new Dimension(34, 28));
            btnRow.add(btn);
        }
        JLabel vtime = new JLabel("  0:45 / 3:22");
        vtime.setForeground(new Color(150, 155, 180));
        vtime.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnRow.add(vtime);
        controls.add(btnRow, BorderLayout.CENTER);

        p.add(screen, BorderLayout.CENTER);
        p.add(controls, BorderLayout.SOUTH);
        return p;
    }

    // ----------------------------------------------------------------
    //  File Manager (tanpa parameter - default Documents)
    // ----------------------------------------------------------------
    public static JPanel makeFileManager() {
        return makeFileManager("File Manager");
    }

    // ----------------------------------------------------------------
    //  File Manager (dengan nama folder spesifik)
    // ----------------------------------------------------------------
    public static JPanel makeFileManager(String folderName) {
        JPanel p = new JPanel(new BorderLayout());

        // Tentukan path dan isi berdasarkan nama folder
        String path;
        String[][] files;
        String itemCount;

        switch (folderName) {
            case "Documents":
                path = "C:\\Users\\Fikri\\Documents";
                files = new String[][]{
                        {"📁", "Tugas Kuliah",      "Folder",   "-"},
                        {"📁", "Laporan PKL",       "Folder",   "-"},
                        {"📄", "Skripsi_Draft.docx","2.4 MB",   "Word Document"},
                        {"📄", "readme.txt",        "1 KB",     "Text File"},
                        {"📊", "data_mahasiswa.xlsx","56 KB",   "Excel File"},
                        {"📄", "proposal_TA.pdf",   "1.2 MB",  "PDF Document"},
                        {"📄", "catatan_kuliah.txt","18 KB",    "Text File"},
                        {"📊", "nilai_semester.xlsx","34 KB",   "Excel File"},
                };
                itemCount = "8 item  |  Ruang kosong: 48.2 GB";
                break;

            case "Pictures":
                path = "C:\\Users\\Fikri\\Pictures";
                files = new String[][]{
                        {"📁", "Liburan 2024",      "Folder",   "-"},
                        {"📁", "Screenshot",        "Folder",   "-"},
                        {"📁", "Wallpaper",         "Folder",   "-"},
                        {"🖼️", "foto_wisuda.jpg",   "4.7 MB",  "JPEG Image"},
                        {"🖼️", "profil_linkedin.png","890 KB", "PNG Image"},
                        {"🖼️", "kampus_umm.jpg",    "3.1 MB",  "JPEG Image"},
                        {"🖼️", "selfie_2024.jpg",   "2.8 MB",  "JPEG Image"},
                        {"🖼️", "logo_hmif.png",     "220 KB",  "PNG Image"},
                };
                itemCount = "8 item  |  Ruang kosong: 48.2 GB";
                break;

            case "Music":
                path = "C:\\Users\\Fikri\\Music";
                files = new String[][]{
                        {"📁", "Playlist Belajar",  "Folder",   "-"},
                        {"📁", "Favorites",         "Folder",   "-"},
                        {"🎵", "Bohemian Rhapsody.mp3",  "8.4 MB", "MP3 Audio"},
                        {"🎵", "Shape of You.mp3",       "6.1 MB", "MP3 Audio"},
                        {"🎵", "Blinding Lights.mp3",    "7.2 MB", "MP3 Audio"},
                        {"🎵", "Levitating.mp3",         "5.9 MB", "MP3 Audio"},
                        {"🎵", "Stay - Kid LAROI.mp3",   "4.8 MB", "MP3 Audio"},
                        {"🎵", "As It Was.mp3",          "6.7 MB", "MP3 Audio"},
                };
                itemCount = "8 item  |  Ruang kosong: 48.2 GB";
                break;

            case "Downloads":
                path = "C:\\Users\\Fikri\\Downloads";
                files = new String[][]{
                        {"📦", "JDK-21_installer.exe",   "159 MB",  "Application"},
                        {"📦", "IntelliJ_IDEA.exe",      "687 MB",  "Application"},
                        {"📦", "VirtualBox-7.0.exe",     "105 MB",  "Application"},
                        {"📄", "Modul_PraktikumOS.pdf",  "3.4 MB",  "PDF Document"},
                        {"📄", "Soal_UAS_2024.pdf",      "1.1 MB",  "PDF Document"},
                        {"📊", "Template_Laporan.xlsx",  "48 KB",   "Excel File"},
                        {"🖼️", "wallpaper_pack.zip",     "24.6 MB", "ZIP Archive"},
                        {"📦", "OracleDB_11g.zip",       "1.8 GB",  "ZIP Archive"},
                };
                itemCount = "8 item  |  Ruang kosong: 48.2 GB";
                break;

            case "Computer":
                path = "Computer";
                files = new String[][]{
                        {"💾", "Local Disk (C:)",   "238 GB / 476 GB",  "Local Disk"},
                        {"💾", "Data (D:)",         "182 GB / 476 GB",  "Local Disk"},
                        {"📀", "DVD Drive (E:)",    "Kosong",           "Optical Drive"},
                        {"🔌", "USB Drive (F:)",    "12 GB / 32 GB",    "Removable Disk"},
                };
                itemCount = "4 perangkat  |  2 drive lokal, 1 optik, 1 removable";
                break;

            case "Control Panel":
                path = "Control Panel";
                files = new String[][]{
                        {"⚙️", "System",            "-",    "Pengaturan Sistem"},
                        {"🔊", "Sound",             "-",    "Pengaturan Suara"},
                        {"🖥️", "Display",           "-",    "Pengaturan Layar"},
                        {"🌐", "Network",           "-",    "Pengaturan Jaringan"},
                        {"🔒", "Security Center",  "-",    "Keamanan Windows"},
                        {"🖨️", "Printers",         "-",    "Perangkat Cetak"},
                        {"🔋", "Power Options",    "-",    "Pengaturan Daya"},
                        {"📅", "Date and Time",    "-",    "Waktu & Tanggal"},
                };
                itemCount = "8 item";
                break;

            case "Settings":
                path = "Settings";
                files = new String[][]{
                        {"🎨", "Personalization",  "-",    "Tema & Tampilan"},
                        {"👤", "Accounts",         "-",    "Akun Pengguna"},
                        {"🔒", "Privacy",          "-",    "Privasi"},
                        {"🌐", "Network & Internet","-",   "Koneksi Internet"},
                        {"📱", "Bluetooth",        "-",    "Perangkat Bluetooth"},
                        {"🔔", "Notifications",   "-",    "Notifikasi"},
                        {"♿", "Accessibility",   "-",    "Aksesibilitas"},
                        {"🔄", "Windows Update",  "-",    "Pembaruan Sistem"},
                };
                itemCount = "8 item";
                break;

            default: // File Manager default / folder tidak dikenal
                path = "C:\\Users\\Fikri";
                files = new String[][]{
                        {"📁", "Documents",        "Folder",   "-"},
                        {"📁", "Pictures",         "Folder",   "-"},
                        {"📁", "Music",            "Folder",   "-"},
                        {"📁", "Downloads",        "Folder",   "-"},
                        {"📄", "readme.txt",       "1 KB",     "Text File"},
                        {"📊", "data.xlsx",        "24 KB",    "Excel File"},
                        {"🖼️", "photo.jpg",        "3.2 MB",   "JPEG Image"},
                        {"📽️", "video.mp4",        "128 MB",   "Video File"},
                };
                itemCount = "8 item  |  Ruang kosong: 48.2 GB";
                break;
        }

        JPanel nav = new JPanel(new BorderLayout(4, 0));
        nav.setBackground(new Color(240, 242, 248));
        nav.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        JTextField addr = new JTextField(path);
        addr.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        nav.add(new JLabel("  📁 "), BorderLayout.WEST);
        nav.add(addr, BorderLayout.CENTER);

        String[] cols = {"", "Nama", "Ukuran", "Tipe"};
        Object[][] rows = new Object[files.length][4];
        for (int i = 0; i < files.length; i++) rows[i] = files[i];

        JTable table = new JTable(rows, cols);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(26);
        table.getColumnModel().getColumn(0).setMaxWidth(30);
        table.setBackground(Color.WHITE);
        table.setGridColor(new Color(230, 232, 240));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(240, 242, 248));

        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBackground(new Color(240, 242, 248));
        statusBar.add(new JLabel(itemCount));

        p.add(nav, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(statusBar, BorderLayout.SOUTH);
        return p;
    }

    // ----------------------------------------------------------------
    //  Command Prompt
    // ----------------------------------------------------------------
    public static JPanel makeCommandPrompt() {
        JPanel p = new JPanel(new BorderLayout());

        JTextArea output = new JTextArea();
        output.setBackground(new Color(12, 12, 12));
        output.setForeground(new Color(204, 204, 204));
        output.setFont(new Font("Consolas", Font.PLAIN, 13));
        output.setCaretColor(new Color(204, 204, 204));
        output.setEditable(false);
        output.setText(
                "Microsoft Windows [Version 10.0.19045]\r\n" +
                        "(c) Microsoft Corporation. All rights reserved.\r\n\r\n" +
                        "C:\\Users\\Fikri> "
        );

        JTextField input = new JTextField();
        input.setBackground(new Color(12, 12, 12));
        input.setForeground(new Color(204, 204, 204));
        input.setFont(new Font("Consolas", Font.PLAIN, 13));
        input.setCaretColor(new Color(204, 204, 204));
        input.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        input.addActionListener(e -> {
            String cmd  = input.getText().trim();
            String resp;
            switch (cmd.toLowerCase()) {
                case "dir":
                    resp = "\r\n Directory of C:\\Users\\Fikri\r\n\r\n" +
                            " <DIR>  Documents\r\n <DIR>  Pictures\r\n" +
                            " <DIR>  Downloads\r\n readme.txt  1,024 bytes\r\n";
                    break;
                case "cls":
                    output.setText("C:\\Users\\Fikri> ");
                    input.setText("");
                    return;
                case "help":
                    resp = "\r\nPerintah tersedia: dir, cls, help, ver, date\r\n";
                    break;
                case "ver":
                    resp = "\r\nMicrosoft Windows [Version 10.0.19045]\r\n";
                    break;
                case "date":
                    resp = "\r\nTanggal sekarang: "
                            + new SimpleDateFormat("dd/MM/yyyy").format(new Date()) + "\r\n";
                    break;
                case "exit":
                    p.getRootPane().getParent().setVisible(false);
                    return;
                default:
                    resp = "\r\n'" + cmd + "' tidak dikenali sebagai perintah internal.\r\n";
            }
            output.append(cmd + resp + "\r\nC:\\Users\\Fikri> ");
            input.setText("");
            output.setCaretPosition(output.getDocument().getLength());
        });

        p.add(new JScrollPane(output), BorderLayout.CENTER);
        p.add(input, BorderLayout.SOUTH);
        return p;
    }
}