package com.elderbyte.vidada.service.images;

import archimedes.core.images.IMemoryImage;
import archimedes.core.swing.images.ScalrEx;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

class MemoryImageFx implements IMemoryImage {

	private final Image original;

	/**
     *
	 * @param original The original image. Must not be null.
	 */
	public MemoryImageFx(Image original){
		if(original == null)
			throw new IllegalArgumentException("original");

		this.original = original;
	}

	@Override
	public Image getOriginal() {
		return this.original;
	}

	@Override
	public int getWidth() {
		return (int)this.original.getWidth();
	}

	@Override
	public int getHeight() {
		return (int)this.original.getHeight();
	}

	@Override
	public IMemoryImage rescale(int width, int heigth) {

		BufferedImage img = new BufferedImage(
				(int)original.getWidth(),
				(int)original.getHeight(),
				BufferedImage.TYPE_INT_ARGB);

		SwingFXUtils.fromFXImage(original, img);
		BufferedImage rescaled = ScalrEx.rescaleImage(img, width, heigth);
		WritableImage rescaledFX = new WritableImage(width, heigth);

		return new MemoryImageFx(SwingFXUtils.toFXImage(rescaled, rescaledFX));
	}

	@Override
	public String toString(){
		return "MemoryImageFx: org: " + getOriginal() + " (" + getWidth() + "x" + getHeight() +")";
	}

	@Override
	public void writePNG(OutputStream outputstream) throws IOException {
		ImageIO.write(SwingFXUtils.fromFXImage((Image) getOriginal(), null), "png", outputstream);
	}

}
