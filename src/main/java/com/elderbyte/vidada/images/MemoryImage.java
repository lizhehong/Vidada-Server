package com.elderbyte.vidada.images;

import archimedes.core.images.IMemoryImage;
import archimedes.core.images.ScalrEx;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

class MemoryImage implements IMemoryImage {

	private final BufferedImage original;

	/**
     *
	 * @param original The original image. Must not be null.
	 */
	public MemoryImage(BufferedImage original){
		if(original == null)
			throw new IllegalArgumentException("original");

		this.original = original;
	}

	@Override
	public BufferedImage getOriginal() {
		return this.original;
	}

	@Override
	public int getWidth() {
		return this.original.getWidth();
	}

	@Override
	public int getHeight() {
		return this.original.getHeight();
	}

	@Override
	public IMemoryImage rescale(int width, int heigth) {
		BufferedImage rescaled = ScalrEx.rescaleImage(original, width, heigth);
		return new MemoryImage(rescaled);
	}

	@Override
	public String toString(){
		return "MemoryImage: org: " + getOriginal() + " (" + getWidth() + "x" + getHeight() +")";
	}

	@Override
	public void writePNG(OutputStream outputstream) throws IOException {
		ImageIO.write(getOriginal(), "png", outputstream);
	}

}
