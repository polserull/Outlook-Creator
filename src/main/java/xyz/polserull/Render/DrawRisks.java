package xyz.polserull.Render;

import java.awt.*;

public class DrawRisks {
    public static void renderRiskLayer(boolean torRisk, boolean hailRisk, boolean dhailRisk, boolean floodRisk, boolean thRisk, boolean dlRisk, boolean wgRisk, Graphics2D g2d) {
        boolean[] riskStates = {thRisk, floodRisk, dlRisk, hailRisk, dhailRisk, wgRisk, torRisk};
        String[] riskText = {"\u2608 TSTM", "\uFE4F FLOOD", "\u2607 DMG-LGHT", "* HAIL", "*\"2.50 DMG-HAIL", "\u21B3 >55MPH GUST", "\u25BC FC/TOR"};

        Font font = new Font("Serif", Font.PLAIN, 16);
        g2d.setFont(font);

        int y = 220;
        for (int i = 0; i < 7; i++) {
            if(riskStates[i]) {
                g2d.setColor(Color.black);
                g2d.drawString(riskText[i], 12, y);
            } else {
                g2d.setColor(Color.gray);
                g2d.drawString(riskText[i], 12, y);
            }
            y += 18;
        }
    }
}
