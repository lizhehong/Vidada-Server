package com.elderbyte.ffmpeg;


import archimedes.core.geometry.Size;
import archimedes.core.shell.ShellExec;
import archimedes.core.util.OSValidator;
import com.elderbyte.common.Version;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static final Version ffmpegRequiredVersion = new Version(2, 0, 0); // Minimal required verison of ffmpeg

    private static FFmpegInterop instance = null;
    private static int DEFAULT_TIMEOUT = 1000 * 5; // ms

    // Patterns to extract ffmpeg information from console output

    private static final Pattern regex_Duration = Pattern.compile("Duration: (\\d\\d):(\\d\\d):(\\d\\d)\\.(\\d\\d)");
    private static final Pattern regex_BitRate = Pattern.compile("bitrate: (\\d*) kb/s");
    private static final Pattern regex_Resolution = Pattern.compile("Video: .* (\\d{2,})x(\\d{2,})");


    /***************************************************************************
     *                                                                         *
     * Singleton                                                               *
     *                                                                         *
     **************************************************************************/

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

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

	/**
	 *  Extracts the frame at the given time as an image.
	 *
	 * @param pathToVideo
	 * @param pathToImage
	 * @param second
	 * @param size
	 * @throws FFmpegException
	 */
	public final void extractFrame(URI pathToVideo, File pathToImage, int second, Size size) throws FFmpegException {

		FileUtils.deleteQuietly(pathToImage);

		List<String> argumentBuilder = new ArrayList<>();

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
			throw new FFmpegException("Frame could not be extracted! (image file missing) Console: " + log);
		}
	}


    /**
     * Returns basic information about the given media item
     * @param pathToVideo Path to the video file.
     * @return
     * @exception FFmpegException Thrown when there was a problem executing the command.
     */
	public final VideoInfo getVideoInfo(URI pathToVideo) throws FFmpegException {

		List<String> argumentBuilder = new ArrayList<>();

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
     * Returns the version of ffmpeg currently used.
     *
     *  @exception FFmpegException Thrown when there was a problem executing the command.
     */
    public final Version ffmpegVersion() throws FFmpegException {

        String version = ffmpegExec("--version");

        if(version != null && !version.isEmpty()){
            try {
                return Version.ofString(version.trim());
            } catch (Version.VersionFormatException e) {
                throw new FFmpegException("Could not parse ffmpeg version!", e);
            }
        }
        throw new FFmpegException("Failed to retrieve ffmpeg version!");
    }

    private Boolean isAvailableCache = null;

    /**
     * Returns true if ffmpeg is available?
     */
    public final boolean isAvailable(){

        if(isAvailableCache == null){
            try {
                Version version = ffmpegVersion();
                if(version != null){
                    isAvailableCache = true;
                }
            }catch (Exception e){
                logger.error("ffmpeg available failed!", e);
            }
            isAvailableCache = false;
        }

        return isAvailableCache;
    }


    /***************************************************************************
     *                                                                         *
     * Protected Methods                                                       *
     *                                                                         *
     **************************************************************************/

	protected String shieldPathArgument(File pathArg){
		return "\"" + pathArg.getAbsolutePath() + "\"";
	}

	protected String shieldPathArgument(URI pathArg){
		return "\"" + pathArg.getPath() + "\"";
	}


    /**
     * Subclasses have to implement this method and specify the exact command/path which
     * has to be executed as ffmpeg.
     *
     * If ffmpeg is extracted or downloaded on demand, subclasses also have to handle this here.
     *
     * @return
     */
	protected abstract String ffmpegCmd();

    /***************************************************************************
     *                                                                         *
     * Private Methods                                                         *
     *                                                                         *
     **************************************************************************/

    /**
     * Execute the given ffmpeg command and returns the console output.
     * @param args The arguments to pass to ffmpeg
     * @return Returns the console output of the successful command.
     * @exception FFmpegException Thrown when there was a problem executing the command.
     */
    private String ffmpegExec(String... args) throws FFmpegException {
        return ffmpegExec(Arrays.asList(args), DEFAULT_TIMEOUT);
    }


    /**
     * Execute the given ffmpeg command and returns the console output.
     * @param args The arguments to pass to ffmpeg
     * @param timeout How long should we wait for a response from ffmpeg?
     * @return Returns the console output of the successful command.
     * @exception FFmpegException Thrown when there was a problem executing the command.
     */
    private String ffmpegExec(List<String> args, long timeout) throws FFmpegException {

        StringBuilder output = new StringBuilder();

        String[] command = null;
        try {
            args.add(0, ffmpegCmd());
            command = args.toArray(new String[args.size()]);
            int exitVal = ShellExec.executeAndWait(command, output, timeout);

            if(exitVal != 0) {
                // No success!
                throw new FFmpegException(
                    String.format("Failed with exit-code %s to execute command '%s'!", exitVal, toFlatString(command)));
            }

        } catch(Exception e){
            throw new FFmpegException(String.format("Failed to execute command '%s'!", toFlatString(command)), e);
        }
        return output.toString();
    }

    private static String toFlatString(String[] args){
        String flat = "";
        for (String arg : args){
            flat += arg + " ";
        }
        return flat.trim();
    }

}
