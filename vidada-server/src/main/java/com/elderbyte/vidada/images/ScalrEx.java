package com.elderbyte.vidada.images;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.VolatileImage;


public class ScalrEx {

	/**
     * Rescale the given image to exactly fith the given size
	 * We do not want any stretching, so if proportions do not fit,
	 * we scale it to fit the bigger side and cut off the rest.
	 *
	 * @param original
	 * @param newWidth
	 * @param newHeight
	 * @return
	 */
	public static BufferedImage rescaleImage(Image original, int newWidth, int newHeight){

		if(original == null)
			throw new IllegalArgumentException("original must not be null");


		BufferedImage resized = null;

		BufferedImage source = toBufferedImage(original, BufferedImage.TYPE_INT_ARGB);

		//calculate proportion of thumbnail
		float thumbProportion = (float)newHeight / (float)newWidth;

		//calculate proportion of image
		float imageProportion = (float)source.getHeight() / (float)source.getWidth();

		float difference = thumbProportion - imageProportion;

		if(difference > 0){

			//Image is proportional wider than thumb (example: panorama)
			//in this case we want to fit the full height of the image into the thumb and cut the left and the right side off

			BufferedImage image;
			image = Scalr.resize(
					source,
					Scalr.Method.QUALITY,
					Scalr.Mode.FIT_TO_HEIGHT,
					newWidth, newHeight);

			resized = Scalr.crop(image, image.getWidth()/2-newWidth/2, 0, newWidth, newHeight, (BufferedImageOp)null);

		}else if (difference == 0) {

			//proportion of thumb and image are exactly the same
			//in this case we want to fit the image exactly into the thumbnail

			resized = Scalr.resize(
					source,
					Scalr.Method.QUALITY,
					Scalr.Mode.FIT_EXACT,
					newWidth, newHeight);


		} else {
			BufferedImage image;
			image = Scalr.resize(
					source,
					Scalr.Method.QUALITY,
					Scalr.Mode.FIT_TO_WIDTH,
					newWidth, newHeight);

			resized = Scalr.crop(image, 0, image.getHeight()/2-newHeight/2, newWidth, newHeight, (BufferedImageOp)null);
		}


		/*
		else if ( 0 > difference && difference > -0.5) {
			//Image is proportional thinner but not that much
			//in this case we want to fit the full widht of the image into the thumb and cut the up and down side off
			BufferedImage image;
			image = Scalr.resize(
					source,
					Mode.FIT_TO_WIDTH,
					newWidth, newHeight);
			resized = Scalr.crop(image, 0, image.getHeight()/2-newHeight/2, newWidth, newHeight, (BufferedImageOp)null);
		}else {
			//Image is proportianl thinner
			//in this case we want to fit the full height of the image into the thumb and fill the left and the right side with black
			BufferedImage image;
			image = Scalr.resize(
					source,
					Mode.FIT_TO_HEIGHT,
					newWidth, newHeight);
			BufferedImage realImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = realImage.createGraphics();
			g.setColor(Color.black);
			g.fillRect(0, 0, newWidth, newHeight);
			g.drawImage(image, (newWidth/2)-(image.getWidth()/2), 0, null);
			resized = realImage;
		}
		 */



		assert resized.getWidth() == newWidth && resized.getHeight() == newHeight : "The rescaled image dimensions must exactly fit the requested ones";

		return resized;
	}

	public static BufferedImage rescaleResponsive(Image original, int newWidth, int newHeight, Scalr.Method method){

		if(original == null)
			throw new IllegalArgumentException("original must not be null");


		BufferedImage resized = null;

		BufferedImage source = toBufferedImage(original, BufferedImage.TYPE_INT_ARGB);

		//calculate proportion of frame
		float thumbProportion = (float)newHeight / (float)newWidth;

		//calculate proportion of image
		float imageProportion = (float)source.getHeight() / (float)source.getWidth();

		float difference = thumbProportion - imageProportion;

		if(difference > 0){

			resized = Scalr.resize(
					source,
					method,
					Scalr.Mode.FIT_TO_WIDTH,
					newWidth, newHeight);


		}else{
			resized = Scalr.resize(
					source,
					method,
					Scalr.Mode.FIT_TO_HEIGHT,
					newWidth, newHeight);
		}


		return resized;
	}


	/**
	 * Converts the given image into a buffered image.
	 * @param image
	 * @param type
	 * @return
	 */
	public static BufferedImage toBufferedImage(final Image image, final int type) {

		if(image == null)
			throw new IllegalArgumentException("image can not be null");

		if (image instanceof BufferedImage)
			return (BufferedImage) image;
		if (image instanceof VolatileImage)
			return ((VolatileImage) image).getSnapshot();
		loadImage(image);
		final BufferedImage buffImg = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
		final Graphics2D g2 = buffImg.createGraphics();
		g2.drawImage(image, null, null);
		g2.dispose();
		return buffImg;
	}

	private static void loadImage(final Image image) {
		class StatusObserver implements ImageObserver {
			boolean imageLoaded = false;
			@Override
			public boolean imageUpdate(final Image img, final int infoflags,
									   final int x, final int y, final int width, final int height) {
				if (infoflags == ALLBITS) {
					synchronized (this) {
						imageLoaded = true;
						notify();
					}
					return true;
				}
				return false;
			}
		}
		final StatusObserver imageStatus = new StatusObserver();
		synchronized (imageStatus) {
			if (image.getWidth(imageStatus) == -1 || image.getHeight(imageStatus) == -1) {
				while (!imageStatus.imageLoaded) {
					try {
						imageStatus.wait();
					} catch (InterruptedException ex) {}
				}
			}
		}
	}



}
