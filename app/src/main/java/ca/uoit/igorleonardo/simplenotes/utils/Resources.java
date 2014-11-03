package ca.uoit.igorleonardo.simplenotes.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ca.uoit.igorleonardo.simplenotes.R;

public class Resources {
    public static final int TITLE   = 1;
    public static final int BODY    = 2;
    public static final int YELLOW  = 0;
    public static final int BLUE    = 1;
    public static final int WHITE   = 2;
    public static final int GREEN   = 3;
    public static final int RED     = 4;

    public static final Map<Integer, Integer> sBgSelectorBtnsMap = new HashMap<Integer, Integer>();
    static {
        sBgSelectorBtnsMap.put(R.id.iv_bg_yellow, YELLOW);
        sBgSelectorBtnsMap.put(R.id.iv_bg_red, RED);
        sBgSelectorBtnsMap.put(R.id.iv_bg_blue, BLUE);
        sBgSelectorBtnsMap.put(R.id.iv_bg_green, GREEN);
        sBgSelectorBtnsMap.put(R.id.iv_bg_white, WHITE);
    }

    public static final Map<Integer, Integer> sBgSelectorSelectionMap = new HashMap<Integer, Integer>();
    static {
        sBgSelectorSelectionMap.put(YELLOW, R.id.iv_bg_yellow_select);
        sBgSelectorSelectionMap.put(RED, R.id.iv_bg_red_select);
        sBgSelectorSelectionMap.put(BLUE, R.id.iv_bg_blue_select);
        sBgSelectorSelectionMap.put(GREEN, R.id.iv_bg_green_select);
        sBgSelectorSelectionMap.put(WHITE, R.id.iv_bg_white_select);
    }

    public final static int [] NOTE_BG_RESOURCES = new int [] {
            R.color.default_background_addnote,
            R.color.blue_background_addnote,
            R.color.white_background_addnote,
            R.color.green_background_addnote,
            R.color.red_background_addnote
    };

    private final static int [] NOTE_BG_TITLE_RESOURCES = new int [] {
            R.color.default_background_title_addnote,
            R.color.blue_background_title_addnote,
            R.color.white_background_title_addnote,
            R.color.green_background_title_addnote,
            R.color.red_background_title_addnote
    };

    public Resources() {
    }

    public int getBgColor(int id, int type){
        return type == TITLE ? NOTE_BG_TITLE_RESOURCES[id] : NOTE_BG_RESOURCES[id];
    }

    public String formatDate(long timestamp){
        Date noteDate = new Date(timestamp);
        SimpleDateFormat formatter = new SimpleDateFormat("E, LLL d'***', y - HH:mm");
        SimpleDateFormat day = new SimpleDateFormat("d");
        return formatter.format(noteDate).replace("***", ordinal(day.format(noteDate)));
    }

    public String ordinal(String num){
        String[] suffix = {"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};
        int m = Integer.parseInt(num) % 100;
        return suffix[(m > 10 && m < 20) ? 0 : (m % 10)];
    }
}
