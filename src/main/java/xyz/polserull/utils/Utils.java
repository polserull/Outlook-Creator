package xyz.polserull.utils;

import imgui.ImGui;
import imgui.type.ImString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.polserull.Version;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.net.URISyntaxException;
import java.net.URL;

public class Utils {
    private static final Logger logger = LogManager.getLogger(Utils.class);

    public static void openURL(String url) {
        try { Desktop.getDesktop().browse(new URL(url).toURI()); } catch (IOException | URISyntaxException e) {
            logger.error("Unable to open URL to -> {}", url);
        }
    }

    public static void setOutlookTextPreset(ImString outlookTextString) {
        outlookTextString.set("""
                ! DO NOT EDIT ABOVE && !\s

                {**}\s
                VALID: {***}\s
                ISSUED: {****}\s

                &&\s
                ====== OUTLOOK INFO ======\s



                ====== OUTLOOK END  ======\s
                &&"""
        );
    }

    public static void setSystemInfoText(ImString outlookTextString) {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        outlookTextString.set("====== SYSTEM INFO ======\n\n"+
                "Program Version: "+Version.version+"\n"+
                "LWJGL Version: "+ org.lwjgl.Version.getVersion()+"\n"+
                "IMGUI Version: "+ ImGui.getVersion()+"\n\n"+
                "Operating System: "+ osBean.getName()+"\n"+
                "Arch: "+ osBean.getArch()+"\n"+
                "Processors: "+ osBean.getAvailableProcessors()+"\n\n"+
                "Heap Mem Usage: "+ memoryBean.getHeapMemoryUsage()+"\n"+
                "Non-Heap Mem Usage: "+ memoryBean.getNonHeapMemoryUsage()+"\n\n"+
                "Java Runtime Version: "+ System.getProperty("java.version")+"\n"+
                "Java Runtime Name: "+ runtimeBean.getVmName()+"\n"+
                "Java Runtime Vendor: "+ runtimeBean.getVmVendor()+"\n"
        );
    }

    public static BufferedImage loadImage(String filePath) {
        try { return ImageIO.read(Utils.class.getResourceAsStream(filePath)); } catch (IOException e) {
            logger.fatal("Failed to load image: "+e);
            return null;
        }
    }
}
