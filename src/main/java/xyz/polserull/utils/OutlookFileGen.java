package xyz.polserull.utils;

import imgui.type.ImString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class OutlookFileGen {
    private static final Logger logger = LogManager.getLogger(OutlookFileGen.class);

    public static void saveOutlook(int outlookID, BufferedImage outlookImage, BufferedImage risksLayer, ImString outlookInfo, String valid, String issue) {
        // Prompt user for location of outlook files
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            logger.error("Unable to set style!");
        }

        File selectedLocation = null;
        JFileChooser locationExplorer = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        locationExplorer.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnValue = locationExplorer.showOpenDialog(null);

        if(returnValue == JFileChooser.APPROVE_OPTION) {
            selectedLocation = locationExplorer.getSelectedFile();
        } else {
            logger.warn("No directory was selected!");
        }
        // Create Outlook Image
        BufferedImage mergedImage = new BufferedImage(815, 555, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = mergedImage.createGraphics();

        g2d.drawImage(outlookImage, 0, 0, null);
        g2d.drawImage(risksLayer, 0, 0, null);
        g2d.dispose();

        // Create Outlook Document
        File outputFile = new File(selectedLocation.getPath()+"\\outlook-"+outlookID+".png");
        File outlookRaw = new File(selectedLocation.getPath()+"\\outlook-"+outlookID+"-data.wxod");

        try { ImageIO.write(mergedImage, "png", outputFile); } catch (IOException e) { throw new RuntimeException(e); }
        try {
            FileWriter fw = new FileWriter(outlookRaw, false);
            BufferedWriter bw = new BufferedWriter(fw);
            String outlookData = outlookInfo.get().replace("{**}", String.valueOf(outlookID)).replace("{***}", valid).replace("{****}", issue).replace("! DO NOT EDIT ABOVE && ! ", "WXOD");;

            bw.write(outlookData);

            bw.close();
        } catch (IOException e) {
            logger.fatal("Unable to create outlook data file: "+e);
        }
        logger.info("Issued Outlook!");
    }

    public void loadOutlook(String outlookPath) { // TODO
    }
}
