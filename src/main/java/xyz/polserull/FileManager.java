package xyz.polserull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileManager {
    private static final Logger logger = LogManager.getLogger(FileManager.class);
    public final static String dataPath = System.getProperty("user.home") + "/OutlookCreator";

    public static void checkHomePathExists() {
        Path path = Path.of(dataPath+"/configs");
        if (!Files.exists(path)) {
            logger.info("Data folder not located creating...");
            try {
                Files.createDirectory(Path.of(dataPath + "/outlooks"));
                Files.createDirectory(path);
                logger.info("Data folder creation success...");
            } catch (IOException e) {
                logger.fatal("Failed to create data path folder: "+e);
            }
        }
    }
}
