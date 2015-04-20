package com.elderbyte.vidada.service.images;

import archimedes.core.images.IMemoryImage;
import archimedes.core.images.IRawImageFactory;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

@Service
public class RawImageFactoryFx implements IRawImageFactory {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     *
     */
    public RawImageFactoryFx(){

	}


	@Override
	public IMemoryImage createImage(InputStream inputStream) {
		Image imageFx = new Image(inputStream);
		return new MemoryImageFx(imageFx);
	}

	@Override
	public boolean writeImage(IMemoryImage image, OutputStream outputStream) {
		if(!(image.getOriginal() instanceof Image))
			throw new IllegalArgumentException("image must be of type javafx.*.Image");

		Image imageFX = (Image)image.getOriginal();

		try {
			return ImageIO.write(SwingFXUtils.fromFXImage(imageFX, null), "png", outputStream);
		} catch (Exception e) {
            logger.error("Failed to write image to output stream.",e);
		}

		return false;
	}

	@Override
	public IMemoryImage createImage(File file) {
		if(file.exists()){
			return createImage(file.toURI());
		}else{
            logger.error("Can not create image from file: " + file + "(missing)");
		}
		return null;
	}

	@Override
	public IMemoryImage createImage(URI file) {
		return new MemoryImageFx(new Image(file.toString()));
	}

}
