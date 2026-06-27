import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Panel utama yang menangani:
 * - Rendering wallpaper, taskbar, tombol Start, panel menu (kiri/kanan/submenu)
 * - Input mouse dan keyboard
 * - State menu (buka/tutup, hover, search)
 */
class DesktopPanel extends JPanel {

    // ---- State ----
    boolean menuOpen     = false;
    boolean showSub      = false;
    int     hoverLeft    = -1;
    int     hoverRight   = -1;
    int     hoverSub     = -1;
    boolean hoverStart   = false;
    boolean hoverPower   = false;
    String  searchText   = "";
    boolean searchActive = false;
    String  statusMsg    = "Klik START atau tekan S untuk membuka menu";

    // ---- Timer untuk jam & repaint ----
    Timer clockTimer;

    // ---- Shortcut konstanta dari MenuData ----
    private static final int WIN_W   = MenuData.WIN_W;
    private static final int WIN_H   = MenuData.WIN_H;
    private static final int TB_H    = MenuData.TB_H;
    private static final int MENU_Y  = MenuData.MENU_Y;
    private static final int LEFT_W  = MenuData.LEFT_W;
    private static final int RIGHT_W = MenuData.RIGHT_W;
    private static final int MENU_W  = MenuData.MENU_W;
    private static final int MENU_H  = MenuData.MENU_H;
    private static final int ITEM_H  = MenuData.ITEM_H;
    private static final int BTN_W   = MenuData.BTN_W;
    private static final int SUB_W   = MenuData.SUB_W;

    public DesktopPanel() {
        setPreferredSize(new Dimension(WIN_W, WIN_H));
        setFocusable(true);
        requestFocusInWindow();

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
                handleClick(e.getX(), e.getY());
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
            menuOpen     = !menuOpen;
            showSub      = false;
            searchActive = false;
            searchText   = "";
            statusMsg    = menuOpen
                    ? "Start Menu terbuka  |  ESC untuk tutup"
                    : "Klik START atau tekan S untuk membuka menu";
            repaint();
            return;
        }

        if (!menuOpen) return;

        int li = getLeftIdx(cx, cy);
        int ri = getRightIdx(cx, cy);
        int si = showSub ? getSubIdx(cx, cy) : -1;

        // Klik search bar
        if (inRect(cx, cy, 8, MENU_Y + 58, LEFT_W - 16, 30)) {
            searchActive = true;
            statusMsg    = "Ketik nama program...";
        }
        // Klik item kiri
        else if (li >= 0 && !MenuData.LEFT_ITEMS[li][0].equals("---")) {
            if (MenuData.LEFT_ITEMS[li][0].equals("All Programs")) {
                showSub   = !showSub;
                statusMsg = showSub ? "All Programs terbuka" : "All Programs tertutup";
            } else {
                statusMsg = DialogHelper.openAppWindow(this, MenuData.LEFT_ITEMS[li][0]);
                menuOpen  = false;
                showSub   = false;
            }
        }
        // Klik submenu
        else if (si >= 0) {
            statusMsg = DialogHelper.openFolderWindow(this, MenuData.SUB_ITEMS[si]);
            showSub   = false;
        }
        // Klik item kanan
        else if (ri >= 0 && !MenuData.RIGHT_ITEMS[ri][0].equals("---")) {
            if (MenuData.RIGHT_ITEMS[ri][0].equals("Run...")) {
                String msg = DialogHelper.openRunDialog(this);
                if (!msg.isEmpty()) statusMsg = msg;
            } else {
                statusMsg = DialogHelper.openFolderWindow(this, MenuData.RIGHT_ITEMS[ri][0]);
            }
            menuOpen = false;
            showSub  = false;
        }
        // Klik tombol Power
        else if (inRect(cx, cy, 8, MENU_Y + MENU_H - 36, LEFT_W - 16, 28)) {
            String msg = DialogHelper.openShutdownDialog(this);
            if (!msg.isEmpty()) statusMsg = msg;
            menuOpen = false;
        }
        // Klik di luar area menu
        else if (!inRect(cx, cy, 0, MENU_Y, MENU_W, MENU_H)) {
            menuOpen     = false;
            showSub      = false;
            searchActive = false;
            statusMsg    = "Klik START atau tekan S untuk membuka menu";
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
                        menuOpen     = false;
                        showSub      = false;
                        searchActive = false;
                        searchText   = "";
                        statusMsg    = "Klik START atau tekan S untuk membuka menu";
                    }
                    repaint();
                } else if (k == KeyEvent.VK_S && !searchActive) {
                    menuOpen     = !menuOpen;
                    showSub      = false;
                    searchActive = false;
                    searchText   = "";
                    statusMsg    = menuOpen
                            ? "Start Menu terbuka  |  ESC untuk tutup"
                            : "Klik START atau tekan S untuk membuka menu";
                    repaint();
                } else if (k == KeyEvent.VK_BACK_SPACE && searchActive) {
                    if (!searchText.isEmpty()) {
                        searchText = searchText.substring(0, searchText.length() - 1);
                        statusMsg  = searchText.isEmpty()
                                ? "Ketik nama program..."
                                : "Mencari: " + searchText;
                        repaint();
                    }
                } else if (k == KeyEvent.VK_ENTER && searchActive) {
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
                    statusMsg   = "Mencari: " + searchText;
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
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
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
        GradientPaint gp = new GradientPaint(0, 0, MenuData.COL_DESK1, 0, WIN_H - TB_H, MenuData.COL_DESK2);
        g.setPaint(gp);
        g.fillRect(0, 0, WIN_W, WIN_H - TB_H);

        // Cahaya matahari
        RadialGradientPaint sun = new RadialGradientPaint(
                new Point2D.Float(WIN_W - 150, 120), 200,
                new float[]{0f, 1f},
                new Color[]{new Color(255, 200, 80, 60), new Color(0, 0, 0, 0)}
        );
        g.setPaint(sun);
        g.fillRect(0, 0, WIN_W, WIN_H - TB_H);

        // Pegunungan belakang
        g.setPaint(new GradientPaint(0, WIN_H - TB_H - 180, new Color(10, 50, 100),
                0, WIN_H - TB_H, new Color(5, 25, 55)));
        int[] hx  = {0,120,220,320,450,560,650,750,850,950,WIN_W,WIN_W,0};
        int[] hy  = {
                WIN_H-TB_H-80,WIN_H-TB_H-160,WIN_H-TB_H-130,WIN_H-TB_H-200,
                WIN_H-TB_H-150,WIN_H-TB_H-180,WIN_H-TB_H-120,WIN_H-TB_H-160,
                WIN_H-TB_H-110,WIN_H-TB_H-140,WIN_H-TB_H-90,WIN_H-TB_H,WIN_H-TB_H
        };
        g.fillPolygon(hx, hy, hx.length);

        // Pegunungan depan
        g.setPaint(new Color(5, 20, 45));
        int[] hx2 = {0,150,300,500,700,900,WIN_W,WIN_W,0};
        int[] hy2 = {
                WIN_H-TB_H-50,WIN_H-TB_H-90,WIN_H-TB_H-60,WIN_H-TB_H-80,
                WIN_H-TB_H-55,WIN_H-TB_H-70,WIN_H-TB_H-40,WIN_H-TB_H,WIN_H-TB_H
        };
        g.fillPolygon(hx2, hy2, hx2.length);

        // Bintang
        g.setColor(new Color(255, 255, 255, 120));
        int[][] stars = {
                {80,60},{200,40},{350,90},{500,30},{650,70},{800,45},
                {150,120},{420,100},{720,110},{920,80},{50,150},{600,140}
        };
        for (int[] s : stars) g.fillOval(s[0], s[1], 2, 2);

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
        g.setColor(MenuData.COL_TASKBAR);
        g.fillRect(0, WIN_H - TB_H, WIN_W, TB_H);
        g.setColor(new Color(60, 100, 180, 150));
        g.drawLine(0, WIN_H - TB_H, WIN_W, WIN_H - TB_H);

        // Jam & tanggal
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        g.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g.setColor(MenuData.COL_WHITE);
        g.drawString(time, WIN_W - 65, WIN_H - TB_H + 18);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        g.setColor(MenuData.COL_GRAY);
        g.drawString(date, WIN_W - 70, WIN_H - TB_H + 34);
        g.setColor(MenuData.COL_SEP);
        g.drawLine(WIN_W - 80, WIN_H - TB_H + 6, WIN_W - 80, WIN_H - 6);

        // Status teks
        g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g.setColor(MenuData.COL_GRAY);
        FontMetrics fm = g.getFontMetrics();
        int sw = fm.stringWidth(statusMsg);
        g.drawString(statusMsg, (WIN_W - sw) / 2, WIN_H - TB_H + 26);
    }

    // ---- Tombol START ----
    void drawStartBtn(Graphics2D g) {
        Color col = (hoverStart || menuOpen) ? MenuData.COL_START_H : MenuData.COL_START;
        drawRoundRect(g, 0, WIN_H - TB_H, BTN_W, TB_H, col, MenuData.COL_BORDER, 0);

        // Logo Windows
        g.setColor(new Color(255, 255, 255, 200));
        int lx = 12, ly = WIN_H - TB_H + 14;
        g.fillRect(lx,      ly,      8, 8);
        g.fillRect(lx + 10, ly,      8, 8);
        g.fillRect(lx,      ly + 10, 8, 8);
        g.fillRect(lx + 10, ly + 10, 8, 8);

        g.setFont(new Font("Segoe UI", Font.BOLD, 13));
        g.setColor(MenuData.COL_WHITE);
        g.drawString("Start", 34, WIN_H - TB_H + 27);
    }

    // ---- Panel Kiri ----
    void drawLeftPanel(Graphics2D g) {
        int px = 0, py = MENU_Y, pw = LEFT_W, ph = MENU_H;
        drawRoundRect(g, px, py, pw, ph, MenuData.COL_MENU_L, MenuData.COL_BORDER, 0);

        // Header user
        GradientPaint hdr = new GradientPaint(px, py, new Color(50, 100, 200, 180),
                px + pw, py, new Color(30, 60, 140, 180));
        g.setPaint(hdr);
        g.fillRect(px, py, pw, 50);
        g.setColor(MenuData.COL_WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 15));
        g.drawString("👤  Fikri", px + 12, py + 32);
        g.setColor(MenuData.COL_BORDER);
        g.drawLine(px, py + 50, px + pw, py + 50);

        // Search bar
        int sy = py + 58, sh = 30;
        Color sbg = searchActive ? MenuData.COL_SRCH_ACT : MenuData.COL_SRCH;
        drawRoundRect(g, px + 8, sy, pw - 16, sh, sbg, MenuData.COL_BORDER, 8);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        if (searchText.isEmpty() && !searchActive) {
            g.setColor(MenuData.COL_GRAY);
            g.drawString("🔍  Cari program dan file...", px + 16, sy + 20);
        } else {
            g.setColor(MenuData.COL_WHITE);
            g.drawString("🔍  " + searchText + (searchActive ? "|" : ""), px + 16, sy + 20);
        }

        // Daftar program
        int yy = py + 98;
        for (int i = 0; i < MenuData.LEFT_ITEMS.length; i++) {
            String name = MenuData.LEFT_ITEMS[i][0];
            String icon = MenuData.LEFT_ITEMS[i][1];

            if (name.equals("---")) {
                g.setColor(MenuData.COL_SEP);
                g.drawLine(px + 8, yy + 4, px + pw - 8, yy + 4);
                yy += 12;
                continue;
            }
            if (!searchText.isEmpty() && !name.toLowerCase().contains(searchText.toLowerCase())) {
                yy += ITEM_H;
                continue;
            }
            if (yy + ITEM_H > py + ph - 44) break;

            if (i == hoverLeft) {
                g.setColor(MenuData.COL_HOVER);
                g.fillRoundRect(px + 2, yy, pw - 4, ITEM_H, 6, 6);
            }
            g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            g.setColor(MenuData.COL_BLUE);
            g.drawString(icon, px + 12, yy + ITEM_H - 11);

            g.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            g.setColor(i == hoverLeft ? MenuData.COL_WHITE : MenuData.COL_GRAY);
            g.drawString(name, px + 40, yy + ITEM_H - 11);

            if (name.equals("All Programs")) {
                g.setColor(MenuData.COL_BLUE);
                g.drawString("▶", px + pw - 20, yy + ITEM_H - 11);
            }
            yy += ITEM_H;
        }

        // Tombol Power
        int pwy = py + ph - 36;
        Color pc = hoverPower ? MenuData.COL_PWR_H : MenuData.COL_PWR;
        drawRoundRect(g, px + 8, pwy, pw - 16, 28, pc, new Color(220, 80, 80), 6);
        g.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g.setColor(MenuData.COL_WHITE);
        drawTextCenterRect(g, "⏻  Power / Shutdown", px + 8, pwy, pw - 16, 28);
    }

    // ---- Panel Kanan ----
    void drawRightPanel(Graphics2D g) {
        int px = LEFT_W, py = MENU_Y, pw = RIGHT_W, ph = MENU_H;
        drawRoundRect(g, px, py, pw, ph, MenuData.COL_MENU_R, MenuData.COL_BORDER, 0);

        int yy = py + 12;
        for (int i = 0; i < MenuData.RIGHT_ITEMS.length; i++) {
            String name = MenuData.RIGHT_ITEMS[i][0];
            String icon = MenuData.RIGHT_ITEMS[i][1];

            if (name.equals("---")) {
                g.setColor(MenuData.COL_SEP);
                g.drawLine(px + 6, yy + 5, px + pw - 6, yy + 5);
                yy += 14;
                continue;
            }
            if (yy + ITEM_H > py + ph - 8) break;

            if (i == hoverRight) {
                g.setColor(MenuData.COL_HOVER);
                g.fillRoundRect(px + 2, yy, pw - 4, ITEM_H, 6, 6);
            }
            g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
            g.setColor(MenuData.COL_BLUE);
            g.drawString(icon, px + 8, yy + ITEM_H - 10);

            boolean isFirst = (i == 0);
            g.setFont(new Font("Segoe UI", isFirst ? Font.BOLD : Font.PLAIN, isFirst ? 14 : 12));
            g.setColor(isFirst ? MenuData.COL_WHITE
                    : (i == hoverRight ? MenuData.COL_WHITE : MenuData.COL_GRAY));
            g.drawString(name, px + 32, yy + ITEM_H - 10);
            yy += ITEM_H;
        }
    }

    // ---- Submenu All Programs ----
    void drawSubMenu(Graphics2D g) {
        int sx = LEFT_W + RIGHT_W + 4;
        int sy = MENU_Y + 20;
        int sh = MenuData.SUB_ITEMS.length * ITEM_H + 32;

        drawRoundRect(g, sx, sy, SUB_W, sh, MenuData.COL_SUBMENU, MenuData.COL_BORDER, 8);

        g.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g.setColor(MenuData.COL_BLUE);
        g.drawString("All Programs", sx + 10, sy + 18);
        g.setColor(MenuData.COL_SEP);
        g.drawLine(sx + 6, sy + 24, sx + SUB_W - 6, sy + 24);

        for (int i = 0; i < MenuData.SUB_ITEMS.length; i++) {
            int iy = sy + 26 + i * ITEM_H;
            if (i == hoverSub) {
                g.setColor(MenuData.COL_HOVER);
                g.fillRoundRect(sx + 2, iy, SUB_W - 4, ITEM_H, 6, 6);
            }
            g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
            g.setColor(MenuData.COL_BLUE);
            g.drawString("📂", sx + 8, iy + ITEM_H - 10);
            g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g.setColor(i == hoverSub ? MenuData.COL_WHITE : MenuData.COL_GRAY);
            g.drawString(MenuData.SUB_ITEMS[i], sx + 32, iy + ITEM_H - 10);
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
        for (int i = 0; i < MenuData.LEFT_ITEMS.length; i++) {
            if (MenuData.LEFT_ITEMS[i][0].equals("---")) { yy += 12; continue; }
            if (yy + ITEM_H > MENU_Y + MENU_H - 44) break;
            if (my >= yy && my < yy + ITEM_H) return i;
            yy += ITEM_H;
        }
        return -1;
    }

    int getRightIdx(int mx, int my) {
        if (!inRect(mx, my, LEFT_W, MENU_Y, RIGHT_W, MENU_H)) return -1;
        int yy = MENU_Y + 12;
        for (int i = 0; i < MenuData.RIGHT_ITEMS.length; i++) {
            if (MenuData.RIGHT_ITEMS[i][0].equals("---")) { yy += 14; continue; }
            if (yy + ITEM_H > MENU_Y + MENU_H - 8) break;
            if (my >= yy && my < yy + ITEM_H) return i;
            yy += ITEM_H;
        }
        return -1;
    }

    int getSubIdx(int mx, int my) {
        int sx = LEFT_W + RIGHT_W + 4;
        int sy = MENU_Y + 26;
        for (int i = 0; i < MenuData.SUB_ITEMS.length; i++) {
            int iy = sy + i * ITEM_H;
            if (inRect(mx, my, sx, iy, SUB_W, ITEM_H)) return i;
        }
        return -1;
    }
}