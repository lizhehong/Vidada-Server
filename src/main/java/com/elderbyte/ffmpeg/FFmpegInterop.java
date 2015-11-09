package com.elderbyte.ffmpeg;


import archimedes.core.shell.ShellExec;
import archimedes.core.util.Lists;
import archimedes.core.util.OSValidator;
import com.elderbyte.common.ArgumentNullException;
import com.elderbyte.common.Version;
import com.elderbyte.vidada.media.Resolution;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.File;
import java.net.URI;
import java.time.Duration;
import java.util.*;
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

    private final static Logger logger = LoggerFactory.getLogger(FFmpegInterop.class);

    private static final Version ffmpegRequiredVersion = new Version(2, 0, 0); // Minimal required verison of ffmpeg

    private static FFmpegInterop instance = null;
    private static int DEFAULT_TIMEOUT = 1000 * 5; // ms

    // Patterns to extract ffmpeg information from console output

    private static final Pattern regex_Duration = Pattern.compile("Duration: (\\d\\d):(\\d\\d):(\\d\\d)\\.(\\d\\d)");
    private static final Pattern regex_BitRate = Pattern.compile("bitrate: (\\d*) kb/s");
    private static final Pattern regex_Resolution = Pattern.compile("Video: .* (\\d{2,})x(\\d{2,})");
    private static final Pattern regex_Version = Pattern.compile("ffmpeg version ([\\d|\\.]+)");
    private static final Pattern regex_VersionLazy = Pattern.compile("ffmpeg version (.+)\\s");


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
	public final void extractFrame(URI pathToVideo, File pathToImage, int second, Resolution size) throws FFmpegException {

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
		argumentBuilder.add("scale=max("+size.getWidth()+"\\,a*"+size.getHeight()+"):max("+size.getHeight()+"\\,"+size.getWidth()+"/a),crop="+size.getWidth()+":"+size.getHeight());

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

        /*
		argumentBuilder.add("-ss"); 		// seek -
		argumentBuilder.add("2");
        */
		argumentBuilder.add("-i");
		argumentBuilder.add(shieldPathArgument(pathToVideo));


		argumentBuilder.add("-an");			// no audio

        /*
		argumentBuilder.add("-frames:v");	// frames to extract
		argumentBuilder.add("1");
		*/

		String output = ffmpegExec(argumentBuilder, DEFAULT_TIMEOUT);

		int videoDuration = 0;
		int videoBitrate = 0;
        Resolution resolution = Resolution.Empty;


		//
		// parse duration
		//
		Matcher m = regex_Duration.matcher(output);
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
		m = regex_BitRate.matcher(output);
		if(m.find()){
			// bitrate: 10731 kb/s
			videoBitrate = Integer.parseInt(m.group(1));

		}else {
            logger.warn("Bitrate info not found!");
		}


		//
		// parse native resolution
		//
		m = regex_Resolution.matcher(output);
		if(m.find()){
			resolution = new Resolution(
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

        String output = ffmpegExec("-version");

        if (output != null && !output.isEmpty()) {

            // extract the version string from the ffmpeg output
            // ffmpeg version 2.8.1 .... or git hash
            Matcher matcher = regex_VersionLazy.matcher(output);
            if (matcher.find()) {
                String versionStr = matcher.group(1);
                return new Version(0,0,0, versionStr);
            } else {
                throw new FFmpegException("Could not parse ffmpeg version, regex does not match output!");
            }
        }
        throw new FFmpegException("Failed to retrieve ffmpeg version!");
    }

    /**
     * Returns the version of ffmpeg currently used.
     *
     *  @exception FFmpegException Thrown when there was a problem executing the command.
    */
    @Deprecated
    public final Version ffmpegVersionExact() throws FFmpegException {

        String output = ffmpegExec("-version");

        if(output != null && !output.isEmpty()){

            // extract the version string from the ffmpeg output
            // ffmpeg version 2.8.1 ....

            Matcher matcher = regex_Version.matcher(output);
            if(matcher.find()){
                String versionStr = matcher.group(1);
                try {
                    return Version.ofString(versionStr);
                } catch (Version.VersionFormatException e) {
                    throw new FFmpegException("Could not parse ffmpeg version!", e);
                }
            }else{
                throw new FFmpegException("Could not parse ffmpeg version, regex does not match output!");
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
                }else{
                    logger.warn("ffmpeg version was null, assuming not available!");
                    isAvailableCache = false;
                }
            }catch (Exception e){
                logger.error("ffmpeg available failed!", e);
                isAvailableCache = false;
            }
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

        if(args.length == 0){
            throw new FFmpegException("At least one argument is required for ffmpeg!");
        }

        return ffmpegExec(Arrays.asList(args), DEFAULT_TIMEOUT);
    }


    /**
     * Execute the given ffmpeg command and returns the console output.
     * @param args The arguments to pass to ffmpeg
     * @param timeout How long should we wait for a response from ffmpeg?
     * @return Returns the console output of the successful command.
     * @exception FFmpegException Thrown when there was a problem executing the command.
     */
    private String ffmpegExec(Collection<String> args, long timeout) throws FFmpegException {

        if(args == null) throw new ArgumentNullException("args");

        StringBuilder output = new StringBuilder();

        try {
            List<String> fullArgs = new ArrayList<>();
            String ffmpegCommand = ffmpegCmd();

            logger.info("ffmpegCommand: '" + ffmpegCommand + "'");

            if(ffmpegCommand != null && !ffmpegCommand.isEmpty()){
                fullArgs.add(ffmpegCommand);
            }else {
                throw new FFmpegException(String.format("ffmpeg-command '%s' is not properly configured!", ffmpegCommand));
            }
            fullArgs.addAll(args);

            String[] command = Lists.toArray(fullArgs, String.class);
            int exitVal = ShellExec.executeAndWait(command, output, timeout);

            /* TODO Since we use ffmpeg -i for extracting information, and this actually returns -1 we cant handle it this way...

            if(exitVal != 0) {
                // No success!
                throw new FFmpegException(
                    String.format("Failed with exit-code %s to execute command '%s'! Output: %s", exitVal, toFlatString(args), output));
            }*/

        } catch(Exception e){
            throw new FFmpegException(String.format("Failed to execute command '%s'!", toFlatString(args)), e);
        }

        return output.toString();



    }

    private static String toFlatString(Iterable<String> args){
        String flat = "";
        for (String arg : args){
            flat += arg + " ";
        }
        return flat.trim();
    }

}
