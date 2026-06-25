import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.Timer;

/**
 * ============================================================
 *  CLASSIC START MENU - Final Project Grafika Komputer
 *  Java Swing (library dasar) - Tanpa JavaFX / Qt / ImGui
 *
 *  Cara compile & run:
 *    javac StartMenu.java
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

        pack(); // gunakan ukuran panel
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
    }
}

/* ============================================================
   PANEL UTAMA — menggambar semua komponen
   ============================================================ */
class DesktopPanel extends JPanel {

    // ---- Warna tema ----
    static final Color COL_DESK1     = new Color(15, 80, 140);
    static final Color COL_DESK2     = new Color(5,  30,  70);
    static final Color COL_TASKBAR   = new Color(18, 20, 35);
    static final Color COL_START     = new Color(0,  100, 200);
    static final Color COL_START_H   = new Color(30, 140, 255);
    static final Color COL_MENU_L    = new Color(32, 36, 56);
    static final Color COL_MENU_R    = new Color(20, 22, 40);
    static final Color COL_HOVER     = new Color(0,  80, 160);
    static final Color COL_BORDER    = new Color(60, 100, 180);
    static final Color COL_SEP       = new Color(60, 65, 100);
    static final Color COL_SRCH      = new Color(45, 48, 72);
    static final Color COL_SRCH_ACT  = new Color(60, 64, 96);
    static final Color COL_PWR       = new Color(160, 25, 25);
    static final Color COL_PWR_H     = new Color(210, 45, 45);
    static final Color COL_WHITE     = Color.WHITE;
    static final Color COL_GRAY      = new Color(170, 175, 200);
    static final Color COL_BLUE      = new Color(100, 178, 255);
    static final Color COL_YELLOW    = new Color(255, 220, 80);
    static final Color COL_SUBMENU   = new Color(40, 44, 68);

    // ---- Ukuran layout ----
    static final int WIN_W   = 1024;
    static final int WIN_H   = 768;
    static final int TB_H    = 44;
    static final int MENU_Y  = 180;
    static final int LEFT_W  = 300;
    static final int RIGHT_W = 190;
    static final int MENU_W  = LEFT_W + RIGHT_W;
    static final int MENU_H  = WIN_H - TB_H - MENU_Y;
    static final int ITEM_H  = 36;
    static final int BTN_W   = 100;
    static final int SUB_W   = 200;

    // ---- State ----
    boolean menuOpen    = false;
    boolean showSub     = false;
    int hoverLeft       = -1;
    int hoverRight      = -1;
    int hoverSub        = -1;
    boolean hoverStart  = false;
    boolean hoverPower  = false;
    String searchText   = "";
    boolean searchActive= false;
    String statusMsg    = "Klik START atau tekan S untuk membuka menu";

    // ---- Aplikasi yang terbuka (dummy windows) ----
    JDialog openApp     = null;

    // ---- Data menu ----
    String[][] leftItems = {
            {"Notepad",        "📄"},
            {"Calculator",     "🔢"},
            {"Paint",          "🎨"},
            {"Browser",        "🌐"},
            {"Music Player",   "🎵"},
            {"Video Player",   "🎬"},
            {"File Manager",   "📁"},
            {"Command Prompt", "⬛"},
            {"---", ""},               // separator
            {"All Programs",   "📋"},
    };

    String[][] rightItems = {
            {"Fikri",          "👤"},
            {"---", ""},
            {"Documents",      "📄"},
            {"Pictures",       "🖼️"},
            {"Music",          "🎵"},
            {"Downloads",      "⬇️"},
            {"---", ""},
            {"Computer",       "💻"},
            {"Control Panel",  "⚙️"},
            {"Settings",       "🔧"},
            {"---", ""},
            {"Help",           "❓"},
            {"Run...",         "▶️"},
    };

    String[] subItems = {
            "Accessories", "System Tools", "Startup",
            "Maintenance", "Microsoft Office", "Games"
    };

    // ---- Timer untuk jam ----
    Timer clockTimer;

    public DesktopPanel() {
        setPreferredSize(new Dimension(WIN_W, WIN_H));
        setFocusable(true);
        requestFocusInWindow();

        // Timer refresh UI (60fps) + jam
        clockTimer = new Timer(16, e -> repaint());
        clockTimer.start();

        setupMouseListener();
        setupKeyListener();
    }

    // ============================================================
    //  MOUSE LISTENER
    // ============================================================
    void setupMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int cx = e.getX(), cy = e.getY();
                handleClick(cx, cy);
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                handleHover(e.getX(), e.getY());
            }
        });
    }

    void handleHover(int mx, int my) {
        hoverStart = inRect(mx, my, 0, WIN_H - TB_H, BTN_W, TB_H);

        if (menuOpen) {
            hoverLeft  = getLeftIdx(mx, my);
            hoverRight = getRightIdx(mx, my);
            hoverPower = inRect(mx, my, 8, MENU_Y + MENU_H - 36, LEFT_W - 16, 28);
            hoverSub   = showSub ? getSubIdx(mx, my) : -1;
        } else {
            hoverLeft = hoverRight = hoverSub = -1;
            hoverPower = false;
        }
        repaint();
    }

    void handleClick(int cx, int cy) {
        // Klik tombol START
        if (inRect(cx, cy, 0, WIN_H - TB_H, BTN_W, TB_H)) {
            menuOpen    = !menuOpen;
            showSub     = false;
            searchActive= false;
            searchText  = "";
            statusMsg   = menuOpen
                    ? "Start Menu terbuka  |  ESC untuk tutup"
                    : "Klik START atau tekan S untuk membuka menu";
            repaint(); return;
        }

        if (!menuOpen) return;

        int li = getLeftIdx(cx, cy);
        int ri = getRightIdx(cx, cy);
        int si = showSub ? getSubIdx(cx, cy) : -1;

        // Klik search
        if (inRect(cx, cy, 8, MENU_Y + 8, LEFT_W - 16, 26)) {
            searchActive = true;
            statusMsg = "Ketik nama program...";
        }
        // Klik item kiri
        else if (li >= 0 && !leftItems[li][0].equals("---")) {
            if (leftItems[li][0].equals("All Programs")) {
                showSub = !showSub;
                statusMsg = showSub ? "All Programs terbuka" : "All Programs tertutup";
            } else {
                openAppWindow(leftItems[li][0]);
                menuOpen = false; showSub = false;
            }
        }
        // Klik submenu
        else if (si >= 0) {
            statusMsg = "Membuka folder: " + subItems[si];
            openFolderWindow(subItems[si]);
            showSub = false;
        }
        // Klik item kanan
        else if (ri >= 0 && !rightItems[ri][0].equals("---")) {
            if (rightItems[ri][0].equals("Run...")) {
                openRunDialog();
            } else {
                openFolderWindow(rightItems[ri][0]);
            }
            menuOpen = false; showSub = false;
        }
        // Klik power
        else if (inRect(cx, cy, 8, MENU_Y + MENU_H - 36, LEFT_W - 16, 28)) {
            openShutdownDialog();
        }
        // Klik di luar menu
        else if (!inRect(cx, cy, 0, MENU_Y, MENU_W, MENU_H)) {
            menuOpen = false; showSub = false; searchActive = false;
            statusMsg = "Klik START atau tekan S untuk membuka menu";
        } else {
            searchActive = false;
        }
        repaint();
    }

    // ============================================================
    //  KEY LISTENER
    // ============================================================
    void setupKeyListener() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int k = e.getKeyCode();

                if (k == KeyEvent.VK_ESCAPE) {
                    if (menuOpen) {
                        menuOpen = false; showSub = false;
                        searchActive = false; searchText = "";
                        statusMsg = "Klik START atau tekan S untuk membuka menu";
                    }
                    repaint();
                }
                else if (k == KeyEvent.VK_S && !searchActive) {
                    menuOpen = !menuOpen;
                    showSub = false; searchActive = false; searchText = "";
                    statusMsg = menuOpen
                            ? "Start Menu terbuka  |  ESC untuk tutup"
                            : "Klik START atau tekan S untuk membuka menu";
                    repaint();
                }
                else if (k == KeyEvent.VK_BACK_SPACE && searchActive) {
                    if (!searchText.isEmpty()) {
                        searchText = searchText.substring(0, searchText.length() - 1);
                        statusMsg = searchText.isEmpty()
                                ? "Ketik nama program..."
                                : "Mencari: " + searchText;
                        repaint();
                    }
                }
                else if (k == KeyEvent.VK_ENTER && searchActive) {
                    if (!searchText.isEmpty())
                        statusMsg = "Hasil pencarian: " + searchText;
                    searchActive = false;
                    repaint();
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
                char ch = e.getKeyChar();
                if (menuOpen && searchActive && ch >= 32 && ch < 127 && searchText.length() < 40) {
                    searchText += ch;
                    statusMsg = "Mencari: " + searchText;
                    repaint();
                }
            }
        });
    }

    // ============================================================
    //  PAINT — RENDER SEMUA KOMPONEN
    // ============================================================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,    RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        drawWallpaper(g2);
        drawTaskbar(g2);
        drawStartBtn(g2);

        if (menuOpen) {
            drawLeftPanel(g2);
            drawRightPanel(g2);
            if (showSub) drawSubMenu(g2);
        }
    }

    // ---- Wallpaper ----
    void drawWallpaper(Graphics2D g) {
        // Gradient background langit
        GradientPaint gp = new GradientPaint(0, 0, COL_DESK1, 0, WIN_H - TB_H, COL_DESK2);
        g.setPaint(gp);
        g.fillRect(0, 0, WIN_W, WIN_H - TB_H);

        // Cahaya matahari di kanan atas
        RadialGradientPaint sun = new RadialGradientPaint(
                new Point2D.Float(WIN_W - 150, 120), 200,
                new float[]{0f, 1f},
                new Color[]{new Color(255, 200, 80, 60), new Color(0, 0, 0, 0)}
        );
        g.setPaint(sun);
        g.fillRect(0, 0, WIN_W, WIN_H - TB_H);

        // Bukit/pegunungan silhouette
        g.setPaint(new GradientPaint(0, WIN_H - TB_H - 180, new Color(10, 50, 100),
                0, WIN_H - TB_H,       new Color(5, 25, 55)));
        int[] hx = {0, 120, 220, 320, 450, 560, 650, 750, 850, 950, WIN_W, WIN_W, 0};
        int[] hy = {WIN_H-TB_H-80, WIN_H-TB_H-160, WIN_H-TB_H-130, WIN_H-TB_H-200,
                WIN_H-TB_H-150, WIN_H-TB_H-180, WIN_H-TB_H-120, WIN_H-TB_H-160,
                WIN_H-TB_H-110, WIN_H-TB_H-140, WIN_H-TB_H-90,  WIN_H-TB_H, WIN_H-TB_H};
        g.fillPolygon(hx, hy, hx.length);

        // Bukit kedua (depan, lebih gelap)
        g.setPaint(new Color(5, 20, 45));
        int[] hx2 = {0, 150, 300, 500, 700, 900, WIN_W, WIN_W, 0};
        int[] hy2 = {WIN_H-TB_H-50, WIN_H-TB_H-90, WIN_H-TB_H-60, WIN_H-TB_H-80,
                WIN_H-TB_H-55, WIN_H-TB_H-70, WIN_H-TB_H-40, WIN_H-TB_H, WIN_H-TB_H};
        g.fillPolygon(hx2, hy2, hx2.length);

        // Bintang-bintang
        g.setColor(new Color(255, 255, 255, 120));
        int[][] stars = {{80,60},{200,40},{350,90},{500,30},{650,70},{800,45},
                {150,120},{420,100},{720,110},{920,80},{50,150},{600,140}};
        for (int[] s : stars) {
            g.fillOval(s[0], s[1], 2, 2);
        }

        // Teks desktop
        g.setFont(new Font("Segoe UI", Font.BOLD, 26));
        drawTextCenter(g, "Classic Start Menu", WIN_H/2 - 100, new Color(200, 225, 255, 200));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        drawTextCenter(g, "Final Project Grafika Komputer  |  Java Swing", WIN_H/2 - 65, new Color(140, 175, 220, 180));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        drawTextCenter(g, "[ S ] Buka Menu    [ Klik item ] Buka Aplikasi    [ ESC ] Tutup",
                WIN_H/2 - 42, new Color(100, 140, 190, 160));
    }

    // ---- Taskbar ----
    void drawTaskbar(Graphics2D g) {
        // Background blur-like taskbar
        g.setColor(COL_TASKBAR);
        g.fillRect(0, WIN_H - TB_H, WIN_W, TB_H);
        g.setColor(new Color(60, 100, 180, 150));
        g.drawLine(0, WIN_H - TB_H, WIN_W, WIN_H - TB_H);

        // Jam
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        g.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g.setColor(COL_WHITE);
        g.drawString(time, WIN_W - 65, WIN_H - TB_H + 18);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        g.setColor(COL_GRAY);
        g.drawString(date, WIN_W - 70, WIN_H - TB_H + 34);
        g.setColor(COL_SEP);
        g.drawLine(WIN_W - 80, WIN_H - TB_H + 6, WIN_W - 80, WIN_H - 6);

        // Status
        g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g.setColor(COL_GRAY);
        FontMetrics fm = g.getFontMetrics();
        int sw = fm.stringWidth(statusMsg);
        g.drawString(statusMsg, (WIN_W - sw) / 2, WIN_H - TB_H + 26);
    }

    // ---- Tombol START ----
    void drawStartBtn(Graphics2D g) {
        Color col = (hoverStart || menuOpen) ? COL_START_H : COL_START;
        drawRoundRect(g, 0, WIN_H - TB_H, BTN_W, TB_H, col, COL_BORDER, 0);

        // Windows logo sederhana
        g.setColor(new Color(255, 255, 255, 200));
        int lx = 12, ly = WIN_H - TB_H + 14;
        g.fillRect(lx,      ly,     8, 8);   // kiri atas
        g.fillRect(lx + 10, ly,     8, 8);   // kanan atas
        g.fillRect(lx,      ly + 10, 8, 8);  // kiri bawah
        g.fillRect(lx + 10, ly + 10, 8, 8);  // kanan bawah

        g.setFont(new Font("Segoe UI", Font.BOLD, 13));
        g.setColor(COL_WHITE);
        g.drawString("Start", 34, WIN_H - TB_H + 27);
    }

    // ---- Panel Kiri ----
    void drawLeftPanel(Graphics2D g) {
        int px = 0, py = MENU_Y, pw = LEFT_W, ph = MENU_H;

        // Background dengan efek glassmorphism sederhana
        drawRoundRect(g, px, py, pw, ph, COL_MENU_L, COL_BORDER, 0);

        // Header strip user
        GradientPaint hdr = new GradientPaint(px, py, new Color(50, 100, 200, 180),
                px + pw, py, new Color(30, 60, 140, 180));
        g.setPaint(hdr);
        g.fillRect(px, py, pw, 50);
        g.setColor(COL_WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 15));
        g.drawString("👤  Fikri", px + 12, py + 32);
        g.setColor(COL_BORDER);
        g.drawLine(px, py + 50, px + pw, py + 50);

        // Search bar
        int sy = py + 58, sh = 30;
        Color sbg = searchActive ? COL_SRCH_ACT : COL_SRCH;
        drawRoundRect(g, px + 8, sy, pw - 16, sh, sbg, COL_BORDER, 8);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        if (searchText.isEmpty() && !searchActive) {
            g.setColor(COL_GRAY);
            g.drawString("🔍  Cari program dan file...", px + 16, sy + 20);
        } else {
            g.setColor(COL_WHITE);
            g.drawString("🔍  " + searchText + (searchActive ? "|" : ""), px + 16, sy + 20);
        }

        // Daftar program
        int yy = py + 98;
        int progAreaH = ph - 98 - 40;

        for (int i = 0; i < leftItems.length; i++) {
            String name = leftItems[i][0];
            String icon = leftItems[i][1];

            // Separator
            if (name.equals("---")) {
                g.setColor(COL_SEP);
                g.drawLine(px + 8, yy + 4, px + pw - 8, yy + 4);
                yy += 12;
                continue;
            }

            // Filter search
            if (!searchText.isEmpty() &&
                    !name.toLowerCase().contains(searchText.toLowerCase())) {
                yy += ITEM_H;
                continue;
            }

            // Batas area (jangan tumpuk tombol power)
            if (yy + ITEM_H > py + ph - 44) break;

            // Hover
            if (i == hoverLeft) {
                g.setColor(COL_HOVER);
                g.fillRoundRect(px + 2, yy, pw - 4, ITEM_H, 6, 6);
            }

            // Ikon & teks
            g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            g.setColor(COL_BLUE);
            g.drawString(icon, px + 12, yy + ITEM_H - 11);

            g.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            g.setColor(i == hoverLeft ? COL_WHITE : COL_GRAY);
            g.drawString(name, px + 40, yy + ITEM_H - 11);

            if (name.equals("All Programs")) {
                g.setColor(COL_BLUE);
                g.drawString("▶", px + pw - 20, yy + ITEM_H - 11);
            }

            yy += ITEM_H;
        }

        // Tombol Power
        int pwy = py + ph - 36;
        Color pc = hoverPower ? COL_PWR_H : COL_PWR;
        drawRoundRect(g, px + 8, pwy, pw - 16, 28, pc, new Color(220, 80, 80), 6);
        g.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g.setColor(COL_WHITE);
        drawTextCenterRect(g, "⏻  Power / Shutdown", px + 8, pwy, pw - 16, 28);
    }

    // ---- Panel Kanan ----
    void drawRightPanel(Graphics2D g) {
        int px = LEFT_W, py = MENU_Y, pw = RIGHT_W, ph = MENU_H;

        drawRoundRect(g, px, py, pw, ph, COL_MENU_R, COL_BORDER, 0);

        int yy = py + 12;
        for (int i = 0; i < rightItems.length; i++) {
            String name = rightItems[i][0];
            String icon = rightItems[i][1];

            if (name.equals("---")) {
                g.setColor(COL_SEP);
                g.drawLine(px + 6, yy + 5, px + pw - 6, yy + 5);
                yy += 14;
                continue;
            }

            if (yy + ITEM_H > py + ph - 8) break;

            if (i == hoverRight) {
                g.setColor(COL_HOVER);
                g.fillRoundRect(px + 2, yy, pw - 4, ITEM_H, 6, 6);
            }

            g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
            g.setColor(COL_BLUE);
            g.drawString(icon, px + 8, yy + ITEM_H - 10);

            g.setFont(new Font("Segoe UI", Font.PLAIN, i == 0 ? 14 : 12));
            g.setColor(i == 0 ? COL_WHITE : (i == hoverRight ? COL_WHITE : COL_GRAY));
            if (i == 0) g.setFont(new Font("Segoe UI", Font.BOLD, 14));
            g.drawString(name, px + 32, yy + ITEM_H - 10);

            yy += ITEM_H;
        }
    }

    // ---- Submenu All Programs ----
    void drawSubMenu(Graphics2D g) {
        int sx = LEFT_W + RIGHT_W + 4;
        int sy = MENU_Y + 20;
        int sh = subItems.length * ITEM_H + 32;

        drawRoundRect(g, sx, sy, SUB_W, sh, COL_SUBMENU, COL_BORDER, 8);

        g.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g.setColor(COL_BLUE);
        g.drawString("All Programs", sx + 10, sy + 18);
        g.setColor(COL_SEP);
        g.drawLine(sx + 6, sy + 24, sx + SUB_W - 6, sy + 24);

        for (int i = 0; i < subItems.length; i++) {
            int iy = sy + 26 + i * ITEM_H;
            if (i == hoverSub) {
                g.setColor(COL_HOVER);
                g.fillRoundRect(sx + 2, iy, SUB_W - 4, ITEM_H, 6, 6);
            }
            g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
            g.setColor(COL_BLUE);
            g.drawString("📂", sx + 8, iy + ITEM_H - 10);
            g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g.setColor(i == hoverSub ? COL_WHITE : COL_GRAY);
            g.drawString(subItems[i], sx + 32, iy + ITEM_H - 10);
        }
    }

    // ============================================================
    //  HELPER GAMBAR
    // ============================================================
    void drawRoundRect(Graphics2D g, int x, int y, int w, int h,
                       Color fill, Color border, int arc) {
        g.setColor(fill);
        if (arc > 0) g.fillRoundRect(x, y, w, h, arc, arc);
        else         g.fillRect(x, y, w, h);
        g.setColor(border);
        if (arc > 0) g.drawRoundRect(x, y, w, h, arc, arc);
        else         g.drawRect(x, y, w, h);
    }

    void drawTextCenter(Graphics2D g, String txt, int y, Color col) {
        g.setColor(col);
        FontMetrics fm = g.getFontMetrics();
        int x = (WIN_W - fm.stringWidth(txt)) / 2;
        g.drawString(txt, x, y);
    }

    void drawTextCenterRect(Graphics2D g, String txt, int rx, int ry, int rw, int rh) {
        FontMetrics fm = g.getFontMetrics();
        int x = rx + (rw - fm.stringWidth(txt)) / 2;
        int y = ry + (rh + fm.getAscent() - fm.getDescent()) / 2;
        g.drawString(txt, x, y);
    }

    boolean inRect(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    // ============================================================
    //  HIT TEST
    // ============================================================
    int getLeftIdx(int mx, int my) {
        if (!inRect(mx, my, 0, MENU_Y + 98, LEFT_W, MENU_H - 98 - 44)) return -1;
        int yy = MENU_Y + 98;
        for (int i = 0; i < leftItems.length; i++) {
            if (leftItems[i][0].equals("---")) { yy += 12; continue; }
            if (yy + ITEM_H > MENU_Y + MENU_H - 44) break;
            if (my >= yy && my < yy + ITEM_H) return i;
            yy += ITEM_H;
        }
        return -1;
    }

    int getRightIdx(int mx, int my) {
        if (!inRect(mx, my, LEFT_W, MENU_Y, RIGHT_W, MENU_H)) return -1;
        int yy = MENU_Y + 12;
        for (int i = 0; i < rightItems.length; i++) {
            if (rightItems[i][0].equals("---")) { yy += 14; continue; }
            if (yy + ITEM_H > MENU_Y + MENU_H - 8) break;
            if (my >= yy && my < yy + ITEM_H) return i;
            yy += ITEM_H;
        }
        return -1;
    }

    int getSubIdx(int mx, int my) {
        int sx = LEFT_W + RIGHT_W + 4;
        int sy = MENU_Y + 26;
        for (int i = 0; i < subItems.length; i++) {
            int iy = sy + i * ITEM_H;
            if (inRect(mx, my, sx, iy, SUB_W, ITEM_H)) return i;
        }
        return -1;
    }

    // ============================================================
    //  DUMMY APP WINDOWS
    // ============================================================
    void openAppWindow(String appName) {
        statusMsg = "Membuka: " + appName;
        JDialog dlg = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this),
                appName, false);
        dlg.setSize(500, 380);
        dlg.setLocationRelativeTo(this);
        dlg.setResizable(true);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(new Color(30, 30, 40));

        // Title bar panel
        JPanel titleBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        titleBar.setBackground(new Color(20, 60, 140));
        JLabel title = new JLabel(appName);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleBar.add(title);
        content.add(titleBar, BorderLayout.NORTH);

        // App content
        JPanel appContent = getAppContent(appName, dlg);
        content.add(appContent, BorderLayout.CENTER);

        // Status bar
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
    }

    JPanel getAppContent(String appName, JDialog dlg) {
        JPanel p = new JPanel();
        p.setBackground(new Color(245, 245, 248));

        switch (appName) {
            case "Notepad":      return makeNotepad();
            case "Calculator":   return makeCalculator();
            case "Paint":        return makePaint();
            case "Browser":      return makeBrowser();
            case "Music Player": return makeMusicPlayer();
            case "Video Player": return makeVideoPlayer();
            case "File Manager": return makeFileManager();
            case "Command Prompt": return makeCommandPrompt();
            default:
                p.setLayout(new GridBagLayout());
                JLabel lbl = new JLabel("[ " + appName + " ]");
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
                lbl.setForeground(new Color(100, 150, 220));
                p.add(lbl);
                return p;
        }
    }

    // -- Notepad --
    JPanel makeNotepad() {
        JPanel p = new JPanel(new BorderLayout());
        // Menu bar
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

    // -- Calculator --
    JPanel makeCalculator() {
        JPanel p = new JPanel(new BorderLayout(4, 4));
        p.setBackground(new Color(32, 32, 48));
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        final String[] disp = {"0"};
        final double[] mem  = {0, 0};
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

            boolean isOp = btn.matches("[÷×−+=]") || btn.equals("=");
            boolean isSpec = btn.matches("%|CE|C|⌫|1/x|x²|√x|±");
            b.setBackground(btn.equals("=") ? new Color(0, 100, 200)
                    : isOp             ? new Color(50, 55, 80)
                    : isSpec           ? new Color(45, 48, 70)
                    : new Color(60, 64, 90));
            b.setForeground(Color.WHITE);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            b.addActionListener(ev -> {
                String cmd = btn;
                try {
                    if (cmd.matches("[0-9]|\\.")) {
                        if (newNum[0]) { disp[0] = cmd.equals(".") ? "0." : cmd; newNum[0] = false; }
                        else disp[0] = disp[0].equals("0") && !cmd.equals(".") ? cmd : disp[0] + cmd;
                    } else if (cmd.equals("C") || cmd.equals("CE")) {
                        disp[0] = "0"; newNum[0] = true;
                    } else if (cmd.equals("⌫")) {
                        disp[0] = disp[0].length() > 1 ? disp[0].substring(0, disp[0].length()-1) : "0";
                    } else if (cmd.matches("[÷×−+]")) {
                        mem[0] = Double.parseDouble(disp[0]); op[0] = cmd; newNum[0] = true;
                    } else if (cmd.equals("=")) {
                        double a = mem[0], b2 = Double.parseDouble(disp[0]), r = 0;
                        switch (op[0]) {
                            case "+": r = a + b2; break; case "−": r = a - b2; break;
                            case "×": r = a * b2; break; case "÷": r = b2!=0 ? a/b2 : 0; break;
                            default:  r = b2;
                        }
                        disp[0] = r == (long)r ? String.valueOf((long)r) : String.format("%.6f", r);
                        newNum[0] = true;
                    } else if (cmd.equals("%")) {
                        disp[0] = String.valueOf(Double.parseDouble(disp[0]) / 100);
                    } else if (cmd.equals("±")) {
                        double v = Double.parseDouble(disp[0]);
                        disp[0] = v == (long)(-v) ? String.valueOf((long)(-v)) : String.valueOf(-v);
                    } else if (cmd.equals("√x")) {
                        double v = Math.sqrt(Double.parseDouble(disp[0]));
                        disp[0] = v == (long)v ? String.valueOf((long)v) : String.format("%.4f", v);
                    } else if (cmd.equals("x²")) {
                        double v = Math.pow(Double.parseDouble(disp[0]), 2);
                        disp[0] = v == (long)v ? String.valueOf((long)v) : String.format("%.4f", v);
                    } else if (cmd.equals("1/x")) {
                        double v = Double.parseDouble(disp[0]);
                        disp[0] = v != 0 ? String.format("%.6f", 1.0/v) : "Error";
                    }
                } catch (Exception ex) { disp[0] = "Error"; }
                display.setText(disp[0]);
            });
            grid.add(b);
        }
        p.add(grid, BorderLayout.CENTER);
        return p;
    }

    // -- Paint --
    JPanel makePaint() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(240, 240, 245));

        // Toolbar warna
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        toolbar.setBackground(new Color(230, 232, 240));
        toolbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 215)));

        final Color[] selColor = {Color.BLACK};
        final int[] brushSize  = {4};

        Color[] palette = {Color.BLACK, Color.WHITE, Color.RED, new Color(0,120,215),
                Color.GREEN, Color.YELLOW, Color.ORANGE, Color.MAGENTA,
                new Color(139,69,19), new Color(0,200,180)};

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
        String[] sizes = {"2","4","8","14"};
        JComboBox<String> sizeBox = new JComboBox<>(sizes);
        sizeBox.setSelectedIndex(1);
        sizeBox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sizeBox.addActionListener(e ->
                brushSize[0] = Integer.parseInt((String)sizeBox.getSelectedItem()));
        toolbar.add(sizeBox);

        p.add(toolbar, BorderLayout.NORTH);

        // Canvas
        BufferedImage canvas = new BufferedImage(494, 290, BufferedImage.TYPE_INT_RGB);
        Graphics2D cg = canvas.createGraphics();
        cg.setColor(Color.WHITE);
        cg.fillRect(0, 0, 494, 290);
        cg.dispose();

        JPanel paintArea = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
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
                cg2.fillOval(e.getX() - bs/2, e.getY() - bs/2, bs, bs);
                lastPt[0] = e.getX(); lastPt[1] = e.getY();
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

    // -- Browser --
    JPanel makeBrowser() {
        JPanel p = new JPanel(new BorderLayout(0, 0));
        p.setBackground(Color.WHITE);

        JPanel nav = new JPanel(new BorderLayout(4, 0));
        nav.setBackground(new Color(240, 242, 248));
        nav.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));

        JTextField urlBar = new JTextField("https://www.google.com");
        urlBar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        urlBar.setBackground(Color.WHITE);
        urlBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180,190,215), 1),
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

        // Dummy halaman
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

    // -- Music Player --
    JPanel makeMusicPlayer() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(18, 18, 28));

        // Album art
        JPanel art = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0,0,new Color(80,40,120),
                        getWidth(),getHeight(),new Color(20,80,160));
                g2.setPaint(gp); g2.fillRect(0,0,getWidth(),getHeight());
                g2.setColor(new Color(255,255,255,60));
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
                FontMetrics fm = g2.getFontMetrics();
                String note = "🎵";
                g2.drawString(note, (getWidth()-fm.stringWidth(note))/2, getHeight()/2+20);
            }
        };
        art.setPreferredSize(new Dimension(160, 160));

        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBackground(new Color(18, 18, 28));
        right.setBorder(BorderFactory.createEmptyBorder(20, 16, 20, 16));

        String[] songs = {"01  Bohemian Rhapsody - Queen",
                "02  Shape of You - Ed Sheeran",
                "03  Blinding Lights - The Weeknd",
                "04  Levitating - Dua Lipa",
                "05  Stay - Kid LAROI"};

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
        for (String btn : new String[]{"⏮","⏪","⏸","⏩","⏭"}) {
            JButton b = new JButton(btn);
            b.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            b.setBackground(new Color(40, 42, 62));
            b.setForeground(Color.WHITE);
            b.setFocusPainted(false); b.setBorderPainted(false);
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

    // -- Video Player --
    JPanel makeVideoPlayer() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.BLACK);

        JPanel screen = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D)g;
                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Layar dummy
                g2.setColor(new Color(30, 30, 30));
                g2.fillRect(20, 15, getWidth()-40, getHeight()-40);
                g2.setColor(new Color(255,255,255,80));
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
                g2.drawString("▶", getWidth()/2 - 18, getHeight()/2 + 16);
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
        for (String b : new String[]{"⏮","⏪","⏸","⏩","⏭","🔊","⛶"}) {
            JButton btn = new JButton(b);
            btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            btn.setBackground(new Color(40, 42, 60));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false); btn.setBorderPainted(false);
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

    // -- File Manager --
    JPanel makeFileManager() {
        JPanel p = new JPanel(new BorderLayout());

        // Address bar
        JPanel nav = new JPanel(new BorderLayout(4, 0));
        nav.setBackground(new Color(240, 242, 248));
        nav.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        JTextField addr = new JTextField("C:\\Users\\Fikri\\Documents");
        addr.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        nav.add(new JLabel("  📁 "), BorderLayout.WEST);
        nav.add(addr, BorderLayout.CENTER);

        // File list
        String[][] files = {
                {"📁", "Documents", "Folder", "-"},
                {"📁", "Pictures",  "Folder", "-"},
                {"📁", "Downloads", "Folder", "-"},
                {"📄", "readme.txt","1 KB",   "Text File"},
                {"📊", "data.xlsx", "24 KB",  "Excel File"},
                {"🖼️", "photo.jpg", "3.2 MB", "JPEG Image"},
                {"📽️", "video.mp4", "128 MB", "Video File"},
                {"📦", "setup.exe", "45 MB",  "Application"},
        };
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
        statusBar.add(new JLabel("8 item  |  Ruang kosong: 48.2 GB"));

        p.add(nav, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(statusBar, BorderLayout.SOUTH);
        return p;
    }

    // -- Command Prompt --
    JPanel makeCommandPrompt() {
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
            String cmd = input.getText().trim();
            String resp;
            switch (cmd.toLowerCase()) {
                case "dir":    resp = "\r\n Directory of C:\\Users\\Fikri\r\n\r\n" +
                        " <DIR>  Documents\r\n <DIR>  Pictures\r\n" +
                        " <DIR>  Downloads\r\n readme.txt  1,024 bytes\r\n"; break;
                case "cls":    output.setText("C:\\Users\\Fikri> "); input.setText(""); return;
                case "help":   resp = "\r\nPerintah tersedia: dir, cls, help, ver, date\r\n"; break;
                case "ver":    resp = "\r\nMicrosoft Windows [Version 10.0.19045]\r\n"; break;
                case "date":   resp = "\r\nTanggal sekarang: " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()) + "\r\n"; break;
                case "exit":   p.getRootPane().getParent().setVisible(false); return;
                default:       resp = "\r\n'" + cmd + "' tidak dikenali sebagai perintah internal.\r\n";
            }
            output.append(cmd + resp + "\r\nC:\\Users\\Fikri> ");
            input.setText("");
            output.setCaretPosition(output.getDocument().getLength());
        });

        p.add(new JScrollPane(output), BorderLayout.CENTER);
        p.add(input, BorderLayout.SOUTH);
        return p;
    }

    // -- Folder window --
    void openFolderWindow(String name) {
        statusMsg = "Membuka: " + name;
        JDialog dlg = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this),
                "📁 " + name, false);
        dlg.setSize(460, 340);
        dlg.setLocationRelativeTo(this);
        dlg.setContentPane(makeFileManager());
        dlg.setVisible(true);
    }

    // -- Run dialog --
    void openRunDialog() {
        JDialog dlg = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this),
                "Run", true);
        dlg.setSize(380, 160);
        dlg.setLocationRelativeTo(this);
        dlg.setResizable(false);

        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        p.add(new JLabel("Buka program, folder, dokumen, atau situs web:"), BorderLayout.NORTH);

        JTextField tf = new JTextField("notepad");
        p.add(tf, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton ok = new JButton("OK");
        ok.addActionListener(e -> {
            statusMsg = "Menjalankan: " + tf.getText();
            dlg.dispose();
        });
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> dlg.dispose());
        btns.add(ok); btns.add(cancel);
        p.add(btns, BorderLayout.SOUTH);

        dlg.setContentPane(p);
        dlg.setVisible(true);
    }

    // -- Shutdown dialog --
    void openShutdownDialog() {
        String[] options = {"Shutdown", "Restart", "Sleep", "Batal"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Pilih tindakan daya:",
                "Power Options",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]
        );
        if (choice == 0) {
            statusMsg = "Sedang mematikan komputer... (simulasi)";
        } else if (choice == 1) {
            statusMsg = "Sedang me-restart... (simulasi)";
        } else if (choice == 2) {
            statusMsg = "Komputer dalam mode Sleep (simulasi)";
        }
        menuOpen = false;
        repaint();
    }
}