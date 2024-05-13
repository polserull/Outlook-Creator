package xyz.polserull;

import imgui.*;
import imgui.app.Application;
import imgui.app.Configuration;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiMouseButton;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import imgui.type.ImString;
import xyz.polserull.Render.DrawRisks;
import xyz.polserull.utils.ImageLoader;
import xyz.polserull.utils.OutlookFileGen;
import xyz.polserull.utils.Utils;
import xyz.polserull.utils.ZuluTime;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static xyz.polserull.utils.ZuluTime.getCurrentZuluHour;
import static xyz.polserull.utils.ZuluTime.zTime;

public class Main extends Application {
    private boolean startup = false;
    private boolean polygonSelectActive = false;
    private boolean polygonsPresent = false;

    private int plserulImgID;
    private int outlookImgID;
    private int pointLayerID;
    private int risksLayerID;
    private BufferedImage outlookImg;
    private BufferedImage pointLayer;
    private BufferedImage risksLayer;

    private Graphics2D g2d;
    private Graphics2D g2dL2;
    private Graphics2D g2dR3;

    String[] riskNames = {"?", "LOW", "SLGT", "MRGL", "MDT"};
    int[] riskCurrent = {1};

    private final ImString systemInfoTextBox = new ImString(1256);
    private final ImString outlookInfoText = new ImString(1256);

    private final ArrayList<polygonPoint> outlookCurrentPoints = new ArrayList<>();

    private final ImBoolean aboutPopupState = new ImBoolean(false);

    private String[] outlookValidDates;
    private final ImInt outlookSelectedDate = new ImInt();
    private final ImInt currentOutlookID = new ImInt();
    private final ImInt outlookValidStart = new ImInt();
    private final ImInt outlookValidEnd = new ImInt();

    private final String[] currentDay = new String[1];
    private final ImInt outlookIssuedHour = new ImInt();

    private final ImBoolean presentRiskTOR = new ImBoolean();
    private final ImBoolean presentRiskHail = new ImBoolean();
    private final ImBoolean presentRiskDMGHail = new ImBoolean();
    private final ImBoolean presentRiskFLOOD = new ImBoolean();
    private final ImBoolean presentRiskThuSt = new ImBoolean();
    private final ImBoolean presentRiskDMGLT = new ImBoolean();
    private final ImBoolean presentRiskWGUST = new ImBoolean();

    @Override
    public void configure(Configuration config) {
        config.setTitle("Outlook Creator | P\u00F8lserull");
        config.setWidth(1279);
        config.setHeight(615);
    }

    @Override
    protected void initImGui(Configuration config) {
        super.initImGui(config);

        final ImGuiIO io = ImGui.getIO();
        final ImGuiStyle st = ImGui.getStyle();
        io.setIniFilename(FileManager.dataPath+"/configs/"+"imgui.ini");
        st.setFrameBorderSize(0.2f);
        st.setFrameRounding(2);
    }

    @Override
    public void process() {
        if(!startup) {
            ClassLoader classLoader = Main.class.getClassLoader();
            outlookValidDates = ZuluTime.nextThreeDays();
            currentDay[0] = outlookValidDates[0];

            outlookImg = Utils.loadImage("/convective-outlook-map-template.png");
            BufferedImage polserullImg = Utils.loadImage("/pfp.png");
            outlookImgID = ImageLoader.loadTexture(outlookImg);
            plserulImgID = ImageLoader.loadTexture(polserullImg);
            g2d = outlookImg.createGraphics();

            pointLayer = new BufferedImage(815, 555, BufferedImage.TYPE_INT_ARGB);
            pointLayerID = ImageLoader.loadTexture(pointLayer);
            g2dL2 = pointLayer.createGraphics();

            risksLayer = new BufferedImage(815, 555, BufferedImage.TYPE_INT_ARGB);
            risksLayerID = ImageLoader.loadTexture(risksLayer);
            g2dR3 = risksLayer.createGraphics();

            setRisksPresent();

            Utils.setOutlookTextPreset(outlookInfoText);
            Utils.setSystemInfoText(systemInfoTextBox);
            startup=true;
        }

        if(ImGui.beginMainMenuBar()) {
            if(ImGui.beginMenu("File")) {
                if(ImGui.menuItem("Exit")) { System.exit(0); }
                ImGui.endMenu();
            }
            if(ImGui.beginMenu("Sources")) {
                if(ImGui.menuItem("MET OFFICE")) { Utils.openURL("https://www.metoffice.gov.uk/weather/warnings-and-advice/uk-warnings"); }
                if(ImGui.menuItem("NETWEATHER")) { Utils.openURL("https://www.netweather.tv/charts-and-data"); }
                if(ImGui.menuItem("ESTOFEX")) { Utils.openURL("https://www.estofex.org/"); }
                if(ImGui.menuItem("TORRO")) { Utils.openURL("https://www.torro.org.uk/home"); }
                if(ImGui.menuItem("SP Charts")) { Utils.openURL("https://www.metoffice.gov.uk/weather/maps-and-charts/surface-pressure"); }
                if(ImGui.menuItem("Soundings")) { Utils.openURL("https://weather.uwyo.edu/upperair/sounding.html"); }
                if(ImGui.menuItem("Ventusky")) { Utils.openURL("https://www.ventusky.com/?p=55.0;-2.2;5&l=rain-3h&m=ukmo_uk"); }
                ImGui.endMenu();
            }
            if(ImGui.menuItem("About")) { aboutPopupState.set(true); }
            ImGui.endMainMenuBar();
        }

        if(aboutPopupState.get()) {
            polygonSelectActive=false;
            ImGui.openPopup("About");
            ImGui.setNextWindowSize(460, 300);
            if(ImGui.beginPopupModal("About", aboutPopupState, 32 | 2)) {
                ImGui.image(plserulImgID, 130, 125); ImGui.sameLine();
                ImGui.text("Outlook Creator\n" +
                        "Created By: p\u00F8lserull\n"+
                        "Version: "+Version.version+"\n\n"+
                        "A outlook creator for educational or\n" +
                        "research purposes based in the UK & Ireland\n");
                if(ImGui.button("KO-FI")) { Utils.openURL("https://ko-fi.com/polserull"); } ImGui.sameLine();
                if(ImGui.button("GITHUB")) { Utils.openURL("https://github.com/polserull"); } ImGui.sameLine();
                if(ImGui.button("TWITTER")) { Utils.openURL("https://twitter.com/_polserull"); } ImGui.sameLine();
                if(ImGui.button("WEBSITE")) { Utils.openURL("https://polserull.xyz/");}

                ImGui.pushItemWidth(400);
                ImGui.inputTextMultiline("##SystemInfo", systemInfoTextBox, ImGuiInputTextFlags.ReadOnly);
                ImGui.endPopup();
            }
        }
        outlookIssuedHour.set(getCurrentZuluHour());

        ImGui.setNextWindowSize(375, 595);
        ImGui.setNextWindowPos(0, 20);
        ImGui.begin("Outlook Info", 2 | 4 | 32);
        ImGui.text("Outlook ID: ");
        ImGui.inputInt("##idIntInput", currentOutlookID, 0);
        ImGui.text("Valid: ");
        ImGui.combo("##outlookDate", outlookSelectedDate, outlookValidDates); ImGui.sameLine(); ImGui.pushItemWidth(50);
        ImGui.combo("##timeValidStart", outlookValidStart, zTime); ImGui.sameLine();
        ImGui.combo("##timeValidEnd", outlookValidEnd, zTime);
        ImGui.text("Issued: "); ImGui.beginDisabled(); ImGui.pushItemWidth(0);
        ImGui.combo("##outlookIssueDate", new ImInt(), currentDay); ImGui.sameLine(); ImGui.pushItemWidth(108);
        ImGui.combo("##timeIssueValidStart", outlookIssuedHour, zTime);
        ImGui.endDisabled(); ImGui.pushItemWidth(0); ImGui.separator();
        if(ImGui.collapsingHeader("Risks")) {
            ImGui.beginTabBar("##outlookRiskLayers");
            if(ImGui.beginTabItem("Risk Layer")) {
                ImGui.pushItemWidth(358);
                ImGui.sliderInt("##riskLevelSlider", riskCurrent, 1, 4, riskNames[riskCurrent[0]]);
                if(ImGui.button("Create Polygon", 175, 19)) { if(polygonSelectActive) {drawOutlookRiskPolygon(riskCurrent[0]);} else { polygonSelectActive = true; } } ImGui.sameLine();
                ImGui.beginDisabled(!polygonsPresent);
                if(ImGui.button("Clear All Polygons", 175, 19)) { clearRisksLayer(); }
                ImGui.endDisabled();
                ImGui.endTabItem();
            }
            if(ImGui.beginTabItem("Present Risks")) {
                if(ImGui.checkbox("TOR", presentRiskTOR)) { setRisksPresent(); } ImGui.sameLine(130);
                if(ImGui.checkbox("HAIL", presentRiskHail)) { setRisksPresent(); }
                if(ImGui.checkbox("DMG-HAIL", presentRiskDMGHail)) { setRisksPresent(); } ImGui.sameLine(130);
                if(ImGui.checkbox("FLOODING", presentRiskFLOOD)) { setRisksPresent(); }
                if(ImGui.checkbox("THUNDERSTORMS", presentRiskThuSt)) { setRisksPresent(); } ImGui.sameLine(130);
                if(ImGui.checkbox("DMG-LIGHTNING", presentRiskDMGLT)) { setRisksPresent(); }
                if(ImGui.checkbox(">50MPH GUSTS", presentRiskWGUST)) { setRisksPresent(); }
                ImGui.endTabItem();
            }
            ImGui.endTabBar();
        }
        ImGui.separator();
        if(ImGui.collapsingHeader("Outlook Text")) {
            ImGui.pushItemWidth(360);
            ImGui.inputTextMultiline("##outlookTextInput", outlookInfoText);
        }
        ImGui.separator();
        ImGui.beginDisabled();
        ImGui.button("Issue to Discord", 175, 19); ImGui.sameLine();
        ImGui.button("Issue to Twitter", 175, 19);
        ImGui.endDisabled();
        if(ImGui.button("Issue / Save", 358, 19)) {
            g2d.setColor(Color.BLACK);
            String outlookValidDate = outlookValidDates[outlookSelectedDate.get()]+" "+ZuluTime.zTime[outlookValidStart.get()]+"-"+ZuluTime.zTime[outlookValidEnd.get()];
            String outlookIssueDate = outlookValidDates[0]+" "+ zTime[outlookIssuedHour.get()];
            g2d.drawString(outlookValidDate, 74, 512);
            g2d.drawString(outlookIssueDate, 78, 524);
            OutlookFileGen.saveOutlook(currentOutlookID.get(), outlookImg, risksLayer, outlookInfoText, outlookValidDate, outlookIssueDate);
        }
        ImGui.end();

        ImGui.setNextWindowSize(904, 595);
        ImGui.setNextWindowPos(375, 20);
        ImGui.begin("Outlook Image", 2 | 4 | 32);
        ImGui.image(outlookImgID, 815, 555);
        ImGui.setCursorPos(8, 27);
        ImGui.image(risksLayerID, 815, 555);
        ImGui.setCursorPos(8, 27);
        ImGui.image(pointLayerID, 815, 555);

        if(ImGui.isMouseClicked(ImGuiMouseButton.Left)) {
            float mouseX = ImGui.getMousePosX() - 383f;
            float mouseY = ImGui.getMousePosY() - 46f;

            if(mouseX > 0 && mouseX < 808 && mouseY > 0 && mouseY < 459 && polygonSelectActive) {
                outlookCurrentPoints.add(new polygonPoint((int) mouseX, (int) mouseY));
                drawOutlookPoint(mouseX, mouseY);

                if(outlookCurrentPoints.size() >= 2) {
                    int x1Center = outlookCurrentPoints.get(outlookCurrentPoints.size()-2).getXCorrd()+5/2;
                    int y1Center = outlookCurrentPoints.get(outlookCurrentPoints.size()-2).getYCorrd()+5/2;
                    int x2Center = outlookCurrentPoints.get(outlookCurrentPoints.size()-1).getXCorrd()+5/2;
                    int y2Center = outlookCurrentPoints.get(outlookCurrentPoints.size()-1).getYCorrd()+5/2;
                    drawOutlookLine(x1Center, y1Center, x2Center, y2Center);
                }
            }
        }
        ImGui.end();
    }

    // Risk Level -> 1 - LOW / 2 - SLGT / 3 - MRGL / 4 - MDT
    private void drawOutlookRiskPolygon(int riskLevel) {
        int nPoints = outlookCurrentPoints.size();
        int[] xPoints = new int[nPoints];
        int[] yPoints = new int[nPoints];
        for(int i = 0; i < outlookCurrentPoints.size(); i++) {
            xPoints[i] = outlookCurrentPoints.get(i).getXCorrd();
            yPoints[i] = outlookCurrentPoints.get(i).getYCorrd();
        }

        switch(riskLevel) {
            case 1:
                g2dR3.setColor(new Color(0x5983B700, true));
                break;
            case 2:
                g2dR3.setColor(new Color(0x59B2FF00, true));
                break;
            case 3:
                g2dR3.setColor(new Color(0x59FF9142, true));
                break;
            case 4:
                g2dR3.setColor(new Color(0x59FF3A3A, true));
                break;
        }

        g2dR3.fillPolygon(xPoints, yPoints, nPoints);

        outlookCurrentPoints.clear();
        pointLayer = new BufferedImage(815, 555, BufferedImage.TYPE_INT_ARGB);
        g2dL2 = pointLayer.createGraphics();
        polygonsPresent = true;
        polygonSelectActive = false;

        refreshOutlookImage();
    }

    private void drawOutlookPoint(float x, float y) {
        g2dL2.setColor(Color.BLACK);
        g2dL2.setStroke(new BasicStroke(2));
        g2dL2.drawRect((int) x, (int) y, 5, 5);
        refreshOutlookImage();
    }

    private void drawOutlookLine(int x, int y, int x1, int y1) {
        g2dL2.setColor(Color.BLACK);
        g2dL2.drawLine(x, y, x1, y1);
        refreshOutlookImage();
    }

    private void clearRisksLayer() {
        risksLayer = new BufferedImage(815, 555, BufferedImage.TYPE_INT_ARGB);
        g2dR3 = risksLayer.createGraphics();
        polygonsPresent = false;
        refreshOutlookImage();
    }

    private void refreshOutlookImage() {
        outlookImgID = ImageLoader.loadTexture(outlookImg);
        pointLayerID = ImageLoader.loadTexture(pointLayer);
        risksLayerID = ImageLoader.loadTexture(risksLayer);
    }

    public void setRisksPresent() {
        DrawRisks.renderRiskLayer(presentRiskTOR.get(), presentRiskHail.get(), presentRiskDMGHail.get(), presentRiskFLOOD.get(), presentRiskThuSt.get(), presentRiskDMGLT.get(), presentRiskWGUST.get(), g2d);
        refreshOutlookImage();
    }

    public static void main(String[] args) { FileManager.checkHomePathExists(); launch(new Main()); }
}