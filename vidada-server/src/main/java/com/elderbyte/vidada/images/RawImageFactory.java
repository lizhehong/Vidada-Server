package com.elderbyte.vidada.images;

import com.elderbyte.common.ArgumentNullException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

@Service
public class RawImageFactory implements IRawImageFactory {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Override
    public IMemoryImage createImage(File file) {

        if(file.exists()){
            try {
                return new MemoryImage(ImageIO.read(file));
            } catch (IOException e) {
                logger.error("Failed to read image " + file, e);
            }
        }else{
            logger.error("Can not create image from file: " + file + "(missing)");
        }
        return null;
    }

    @Override
    public IMemoryImage createImage(URI uri) {

        if(uri == null) throw new ArgumentNullException("uri");

        try {
            ImageIO.read(uri.toURL());
        } catch (IOException e) {
            logger.error("Failed to read image from url", e);
        }
        return null;
    }

    @Override
    public IMemoryImage createImage(InputStream inputStream) {

        if(inputStream == null) throw new ArgumentNullException("inputStream");

        try {
            return new MemoryImage(ImageIO.read(inputStream));
        } catch (IOException e) {
            logger.error("Failed to read image from stream", e);
        }
        return null;
    }

    @Override
    public boolean writeImage(IMemoryImage iMemoryImage, OutputStream outputStream) {

        if(iMemoryImage == null) throw new ArgumentNullException("iMemoryImage");
        if(outputStream == null) throw new ArgumentNullException("outputStream");

        RenderedImage img = (RenderedImage)iMemoryImage.getOriginal();

        try {
            return ImageIO.write(img, "png", outputStream);
        } catch (Exception e) {
            logger.error("Failed to write image to output stream.",e);
        }
        return false;
    }
}
