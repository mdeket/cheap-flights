package com.cheapflights.tickets.service;

import lombok.extern.java.Log;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

@Service
@Log
public class FileService {

    private final String DATA_FOLDER = "uploads";

    public ParsingStatus getParsingStatus(String fileName) {
        Path path = Paths.get(String.format("%s%s%s", DATA_FOLDER, File.separator, fileName));
        if (Files.exists(path)) {
            return ParsingStatus.PARSING;
        } else {
            return ParsingStatus.DONE;
        }
    }

    /**
     * Saves file to uploads folder in project root.
     *
     * @param file
     * @return Saved file.
     */
    public File saveFile(MultipartFile file) {
        log.info("Saving file to UPLOADS folder.");
        Path path = Paths.get(DATA_FOLDER);
        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                log.log(Level.SEVERE, String.format("Failed when trying to create %s folder", DATA_FOLDER), e);
                e.printStackTrace();
            }
        }

        try (InputStream inputStream = file.getInputStream()) {
            File newFile = new File(String.format("%s%s%s", DATA_FOLDER, File.separator, file.getOriginalFilename()));
            FileUtils.copyInputStreamToFile(inputStream, newFile);
            return newFile;
        } catch (IOException e) {
            log.log(Level.SEVERE, String.format("Saving file to %s folder.", DATA_FOLDER), e);
        }
        throw new RuntimeException("Input file was not successfully saved.");
    }

    /**
     * Deletes a file from uploads folder.
     */
    public void deleteFile(String fileName) {
        log.info(String.format("Deleting file %s from UPLOADS folder.", fileName));
        Path path = Paths.get(String.format("%s/%s", DATA_FOLDER, fileName));
        if (Files.exists(path)) {
            try {
                Files.deleteIfExists(path);
                log.info(String.format("File %s is successfully deleted.", fileName));
            } catch (IOException e) {
                log.log(Level.SEVERE, String.format("An error occurred when tried to delete a file %s", fileName), e);
            }
        } else {
            log.warning(String.format("File does not exist! %s", path.toString()));
        }
    }
}
