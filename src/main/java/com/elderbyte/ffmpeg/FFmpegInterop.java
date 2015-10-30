package com.elderbyte.ffmpeg;


import archimedes.core.geometry.Size;
import archimedes.core.shell.ShellExec;
import archimedes.core.util.OSValidator;
import archimedes.core.util.PackageUtil;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipException;

/**
 * Platform independent ffmpeg wrapper to extract thumbnails and basic media info
 *
 * @author IsNull
 *
 */
public abstract class FFmpegInterop {

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    protected static final Logger logger = LogManager.getLogger(FFmpegInterop.class.getName());


    protected static File encoder;

	private static FFmpegInterop instance = null;
	private static int DEFAULT_TIMEOUT = 1000 * 5; // ms

    /**
     * Gets the FFmpegInterop instance
     * @return
     */
	public synchronized static FFmpegInterop instance(){

		if(instance == null){
			if(OSValidator.isWindows()){
				instance = new FFmpegInteropWindows();
			}else {
				instance = new FFmpegInteropUnix();
			}

            logger.info("Loaded ffmpeg interop: " + instance.getClass().getName());
		}
		return instance;
	}



	/**
	 *  Extracts the frame at the given time as an image.
	 *
	 * @param pathToVideo
	 * @param pathToImage
	 * @param second
	 * @param size
	 * @throws FFmpegException
	 */
	public void createImage(URI pathToVideo, File pathToImage, int second, Size size) throws FFmpegException {

		FileUtils.deleteQuietly(pathToImage);

		List<String> argumentBuilder = new ArrayList<String>();

		argumentBuilder.add("-ss"); 		// seek - better before setting the -i input!
		argumentBuilder.add(second +"");

		argumentBuilder.add("-i");			// input video
		argumentBuilder.add(shieldPathArgument(pathToVideo));

		argumentBuilder.add("-an");			// no audio

		argumentBuilder.add("-frames:v");	// frames to extract
		argumentBuilder.add("1");

		argumentBuilder.add("-y");			// overwrite existing thumb

		// thumb size
		argumentBuilder.add("-vf");
		// the following will scale the thumb to the desired size but not stretch the image
		argumentBuilder.add("scale=max("+size.width+"\\,a*"+size.height+"):max("+size.height+"\\,"+size.width+"/a),crop="+size.width+":"+size.height);

		argumentBuilder.add(shieldPathArgument(pathToImage));

		String log = ffmpegExec(argumentBuilder, DEFAULT_TIMEOUT);

		if(!pathToImage.exists())
		{
			throw new FFmpegException("Image could not been created.(file missing)", log);
		}
	}


	//
	// define the patterns to extract the information
	//
	private static final Pattern regex_Duration = Pattern.compile("Duration: (\\d\\d):(\\d\\d):(\\d\\d)\\.(\\d\\d)");
	private static final Pattern regex_BitRate = Pattern.compile("bitrate: (\\d*) kb/s");
	private static final Pattern regex_Resolution = Pattern.compile("Video: .* (\\d{2,})x(\\d{2,})");

	public VideoInfo getVideoInfo(URI pathToVideo) {

		//if(!pathToVideo.exists())
		//	throw new IllegalArgumentException("file must exist and being readable! @ " + pathToVideo.toString());

		List<String> argumentBuilder = new ArrayList<String>();

		argumentBuilder.add("-ss"); 		// seek -
		argumentBuilder.add("2");

		argumentBuilder.add("-i");
		argumentBuilder.add(shieldPathArgument(pathToVideo));

		argumentBuilder.add("-an");			// no audio

		argumentBuilder.add("-frames:v");	// frames to extract
		argumentBuilder.add("1");

		String log = ffmpegExec(argumentBuilder, DEFAULT_TIMEOUT);

		int videoDuration = 0;
		int videoBitrate = 0;
		Size resolution = null;


		//
		// parse duration
		//
		Matcher m = regex_Duration.matcher(log);
		if(m.find()){
			// Duration: 02:41:41.68,

			Duration hours = Duration.ofHours(Integer.parseInt(m.group(1)));
			Duration minutes = Duration.ofMinutes(Integer.parseInt(m.group(2)));
			Duration seconds = Duration.ofSeconds(Integer.parseInt(m.group(3)));

			Duration completeDuration  = hours.plus(minutes).plus(seconds);
			videoDuration = (int)completeDuration.getSeconds();
		}else {
            logger.warn("Duration info not found!");
		}

		//
		// parse bitrate
		//
		m = regex_BitRate.matcher(log);
		if(m.find()){
			// bitrate: 10731 kb/s
			videoBitrate = Integer.parseInt(m.group(1));

		}else {
            logger.warn("Bitrate info not found!");
		}


		//
		// parse native resolution
		//
		m = regex_Resolution.matcher(log);
		if(m.find()){
			resolution = new Size(
					Integer.parseInt(m.group(1)),
					Integer.parseInt(m.group(2)));
		}else {
            logger.warn("Resolution info not found!");
		}

		return new VideoInfo(videoDuration, videoBitrate, resolution);
	}


	/**
	 * Execute the given ffmpeg command
	 * @param args
	 * @param timeout
	 * @return
	 */
	private String ffmpegExec(List<String> args, long timeout){

		StringBuilder output = new StringBuilder();

		try {
			args.add(0, getFFmpegCMD());
			String[] command = args.toArray(new String[0]);

			int exitVal = ShellExec.executeAndWait(command, output, timeout);

		} catch( TimeoutException e){
            logger.warn("ffmpeg process timed out: " + e.getMessage());
        } catch(Exception e) {
            logger.error(e);
		}
		return output.toString();
	}

	protected String dimensionToString(Size size){
		return  size.width + "x" + size.height;
	}

	protected String shieldPathArgument(File pathArg){
		return "\"" + pathArg.getAbsolutePath() + "\"";
	}

	protected String shieldPathArgument(URI pathArg){
		return "\"" + pathArg.getPath() + "\"";
	}

	/**
	 * Is ffmpeg avaiable
	 * @return
	 */
	public abstract boolean isAvaiable();

	protected abstract String getFFmpegCMD();

}
