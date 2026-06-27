import java.awt.*;

/**
 * Konstanta warna tema, ukuran layout, dan data item menu.
 * Digunakan bersama oleh DesktopPanel dan class render lainnya.
 */
public class MenuData {

    // ---- Warna tema ----
    public static final Color COL_DESK1     = new Color(15, 80, 140);
    public static final Color COL_DESK2     = new Color(5,  30,  70);
    public static final Color COL_TASKBAR   = new Color(18, 20, 35);
    public static final Color COL_START     = new Color(0,  100, 200);
    public static final Color COL_START_H   = new Color(30, 140, 255);
    public static final Color COL_MENU_L    = new Color(32, 36, 56);
    public static final Color COL_MENU_R    = new Color(20, 22, 40);
    public static final Color COL_HOVER     = new Color(0,  80, 160);
    public static final Color COL_BORDER    = new Color(60, 100, 180);
    public static final Color COL_SEP       = new Color(60, 65, 100);
    public static final Color COL_SRCH      = new Color(45, 48, 72);
    public static final Color COL_SRCH_ACT  = new Color(60, 64, 96);
    public static final Color COL_PWR       = new Color(160, 25, 25);
    public static final Color COL_PWR_H     = new Color(210, 45, 45);
    public static final Color COL_WHITE     = Color.WHITE;
    public static final Color COL_GRAY      = new Color(170, 175, 200);
    public static final Color COL_BLUE      = new Color(100, 178, 255);
    public static final Color COL_YELLOW    = new Color(255, 220, 80);
    public static final Color COL_SUBMENU   = new Color(40, 44, 68);

    // ---- Ukuran layout ----
    public static final int WIN_W   = 1024;
    public static final int WIN_H   = 768;
    public static final int TB_H    = 44;
    public static final int MENU_Y  = 180;
    public static final int LEFT_W  = 300;
    public static final int RIGHT_W = 190;
    public static final int MENU_W  = LEFT_W + RIGHT_W;
    public static final int MENU_H  = WIN_H - TB_H - MENU_Y;
    public static final int ITEM_H  = 36;
    public static final int BTN_W   = 100;
    public static final int SUB_W   = 200;

    // ---- Data item menu kiri ----
    public static final String[][] LEFT_ITEMS = {
            {"Notepad",        "📄"},
            {"Calculator",     "🔢"},
            {"Paint",          "🎨"},
            {"Browser",        "🌐"},
            {"Music Player",   "🎵"},
            {"Video Player",   "🎬"},
            {"File Manager",   "📁"},
            {"Command Prompt", "⬛"},
            {"---", ""},
            {"All Programs",   "📋"},
    };

    // ---- Data item menu kanan ----
    public static final String[][] RIGHT_ITEMS = {
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

    // ---- Data submenu All Programs ----
    public static final String[] SUB_ITEMS = {
            "Accessories", "System Tools", "Startup",
            "Maintenance", "Microsoft Office", "Games"
    };
}