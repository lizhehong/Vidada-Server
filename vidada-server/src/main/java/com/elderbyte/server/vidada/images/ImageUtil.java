package com.elderbyte.server.vidada.images;


import com.elderbyte.common.locations.ResourceLocation;
import com.elderbyte.server.vidada.media.Resolution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class ImageUtil {

    private static final Logger logger = LoggerFactory.getLogger(ImageUtil.class);

    public static Resolution getImageResolution(final ResourceLocation resource) throws IOException {
        Resolution result = null;
        String suffix = cleanExtension(resource.getExtension());
        Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(suffix);
        if (iter.hasNext()) {
            ImageReader reader = iter.next();
            InputStream stream = resource.openInputStream();
            ImageInputStream iis = ImageIO.createImageInputStream(stream);
            reader.setInput(iis);
            int width = reader.getWidth(reader.getMinIndex());
            int height = reader.getHeight(reader.getMinIndex());
            result = new Resolution(width, height);
            reader.dispose();
            stream.close();
        } else {
            logger.error("No reader found for given format: " + suffix);
        }
        return result;
    }


    public static Resolution getImageResolution(final String path) throws IOException {
        Resolution result = null;
        String suffix = getFileSuffix(path);
        Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(suffix);
        if (iter.hasNext()) {
            ImageReader reader = iter.next();
            ImageInputStream stream = new FileImageInputStream(new File(path));
            reader.setInput(stream);
            int width = reader.getWidth(reader.getMinIndex());
            int height = reader.getHeight(reader.getMinIndex());
            result = new Resolution(width, height);
            reader.dispose();
        } else {
            logger.error("No reader found for given format: " + suffix);
        }
        return result;
    }

    private static String getFileSuffix(final String path) {
        String result = null;
        if (path != null) {
            result = "";
            if (path.lastIndexOf('.') != -1) {
                result = path.substring(path.lastIndexOf('.'));
                result = cleanExtension(result);
            }
        }
        return result;
    }

    private static String cleanExtension(String extension){
        if (extension.startsWith(".")) {
            extension = extension.substring(1);
        }
        return extension;
    }



}
