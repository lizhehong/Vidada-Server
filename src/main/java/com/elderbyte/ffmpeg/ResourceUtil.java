package com.elderbyte.ffmpeg;

import archimedes.core.util.PackageUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URI;


public final class ResourceUtil {


    private static final Logger logger = LogManager.getLogger(ResourceUtil.class.getName());

    /**
     * Tries to extract a resource packed withhin this jar
     * @param resourcePath The relative path of the resource inside this jar.
     * @return Returns the path to the extracted resource.
     * @throws IOException Thrown when extracting the resource has failed
     */
    public static File extractResource(String resourcePath) throws IOException {

        File extractedFile = null;

        try {
            URI jar = PackageUtil.getJarURI(FFmpegInterop.class);
            URI extractedUri = PackageUtil.extractFile(jar, resourcePath);

            extractedFile = new File(extractedUri);
            logger.debug("Extracted resource to " + extractedFile);
        } catch (Exception e) {
            throw new IOException("Failed to extract resource " + resourcePath, e);
        }

        return extractedFile;
    }
}
