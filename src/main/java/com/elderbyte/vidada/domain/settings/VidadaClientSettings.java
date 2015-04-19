package com.elderbyte.vidada.domain.settings;

import archimedes.core.util.OSValidator;
import com.elderbyte.vidada.domain.util.ConfigurationUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;


/**
 * Holds the applications global settings
 * @author IsNull
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VidadaClientSettings extends JsonSettings {

	/***************************************************************************
	 *                                                                         *
	 * Transient Fields                                                        *
	 *                                                                         *
	 **************************************************************************/

    transient private static final Logger logger = LogManager.getLogger(VidadaClientSettings.class.getName());


    transient public static final String ProductName = "vidada-client";

	transient public static File Path;
	transient public static String defaultCache;
	transient public static File infoPropertiesFile;

	static {
		// Default paths
		Path = new File(ConfigurationUtil.vidadaAppDataDirectory(), ProductName + ".json");
		defaultCache = ConfigurationUtil.vidadaAppDataDirectory() + "/cache";
		infoPropertiesFile = new File(".", "info.properties");

	}

	transient private String versionInfo = null;
	transient private VidadaInstanceConfig currentInstance;

	/***************************************************************************
	 *                                                                         *
	 * Persisted Fields                                                        *
	 *                                                                         *
	 **************************************************************************/

	public Set<VidadaInstanceConfig> vidadaInstances = new HashSet<VidadaInstanceConfig>();
	private String localCachePath = defaultCache;
	private boolean enableDirectPlaySound = false;
	private boolean ignoreImages = false;
	private boolean ignoreMovies = false;
	private boolean forceHDPIRender = false;
	//private boolean usingMetaData = true;
	private boolean isDebug = false;
	private List<MediaPlayerCommand> externalMediaPlayers = new ArrayList<MediaPlayerCommand>();

	/***************************************************************************
	 *                                                                         *
	 * Singleton                                                               *
	 *                                                                         *
	 **************************************************************************/

	transient private static VidadaClientSettings instance;
	public static VidadaClientSettings instance(){

		if(instance == null){
			if(Path.exists())
			{
                logger.info("Loading settings from " + Path);
                instance = JsonSettings.loadSettings(Path, VidadaClientSettings.class);
			}
		}

		if(instance == null){
            logger.info("Could not load existing settings, creating defaults...");
			instance = new VidadaClientSettings();
            instance.loadDefaults();
		}

		instance.setPath(Path);
		instance.persist();

		return instance;
	}

    private VidadaClientSettings(){
        // Empty serialisation constructor
    }

    private void loadDefaults(){
        // Set defaults
        vidadaInstances.add(VidadaInstanceConfig.LOCAL);
        vidadaInstances.add(new VidadaInstanceConfig("Localhost (REST-API)", "http://localhost:5555/api"));

        if(OSValidator.isOSX()){
            // Add known media players if installed:
            if((new File("/Applications/VLC.app")).exists()) {
                externalMediaPlayers.add(new MediaPlayerCommand("VLC", "open -n /Applications/VLC.app --args $media"));
            }
            if((new File("/Applications/mpv.app")).exists()) {
                externalMediaPlayers.add(new MediaPlayerCommand("MPV", "open -n /Applications/mpv.app --args $media"));
            }
            if((new File("/Applications/mplayer2.app")).exists()) {
                externalMediaPlayers.add(new MediaPlayerCommand("MPlayer 2", "open -n /Applications/mplayer2.app --args $media"));
            }
            if((new File("/Applications/DivX Player.app")).exists()) {
                externalMediaPlayers.add(new MediaPlayerCommand("DivX-Player", "open -n \\\"/Applications/DivX Player.app\\\" --args $media"));
            }
            if((new File("/Applications/MPlayerX.app")).exists()) {
                externalMediaPlayers.add(new MediaPlayerCommand("MPlayerX", "open -n /Applications/MPlayerX.app --args -url $media"));
            }
            if((new File("/Applications/mpv.app")).exists()) {
                externalMediaPlayers.add(new MediaPlayerCommand("MPlayer Extended", "open -n /Applications/MPlayerExtended.app --args $media"));
            }
        }else if(OSValidator.isWindows()){
            // TODO Config known Windows Media Players
        }else if(OSValidator.isLinux()){
            // TODO Config known Linux Media Players
        }
    }


	/***************************************************************************
	 *                                                                         *
	 * Properties                                                              *
	 *                                                                         *
	 **************************************************************************/


	/**
	 * Gets the path to the local cache
	 * @return
	 */
	public File getAbsoluteCachePath() {
		return toAbsolutePath(localCachePath);
	}



	public String getVersionInfo() {
		if(versionInfo == null)
		{
			Properties properties = new Properties();
			if(infoPropertiesFile.exists())
			{
				FileInputStream is = null;
				try {
					is = new FileInputStream(infoPropertiesFile);
					properties.load(is);
				} catch (FileNotFoundException e) {
                    logger.error(e);
				} catch (IOException e) {
                    logger.error(e);
				}finally{
					if(is != null)
						try {
							is.close();
						} catch (IOException e) {
                            logger.error(e);
						}
				}
			}else{
				logger.error("Can not find " + infoPropertiesFile.getAbsolutePath());
			}
			versionInfo = properties.getProperty("vidada.version", "unknown");
		}
		return versionInfo;
	}

	public boolean isForceHDPIRender() {
		return forceHDPIRender;
	}


	public void setForceHDPIRender(boolean forceHDPIRender) {
		this.forceHDPIRender = forceHDPIRender;
	}



	/**
	 * Is this app currently running in Debug mode?
	 * @return
	 */
	public boolean isDebug() {
		return isDebug;
	}

	/**
	 * Set the debug mode of this App
	 * @param isDebug
	 */
	public void setDebug(boolean isDebug) {
		this.isDebug = isDebug;
	}

	/**
	 * Should sound be played while DirectPlay preview?
	 * @return
	 */
	public boolean isEnableDirectPlaySound() {
		return enableDirectPlaySound;
	}

	/**
	 * Set if sound be played while DirectPlay preview.
	 * @param enableDirectPlaySound
	 */
	public void setEnableDirectPlaySound(boolean enableDirectPlaySound) {
		this.enableDirectPlaySound = enableDirectPlaySound;
	}


	public boolean isIgnoreImages() {
		return ignoreImages;
	}

	public void setIgnoreImages(boolean ignoreImages) {
		this.ignoreImages = ignoreImages;
	}

	public boolean isIgnoreMovies() {
		return ignoreMovies;
	}

	public void setIgnoreMovies(boolean ignoreMovies) {
		this.ignoreMovies = ignoreMovies;
	}

	public Collection<VidadaInstanceConfig> getVidadaInstances(){
		return vidadaInstances;
	}

	public Collection<MediaPlayerCommand> getExternalMediaPlayers() {
		return externalMediaPlayers;
	}


	/***************************************************************************
	 *                                                                         *
	 * Transient Properties                                                    *
	 *                                                                         *
	 **************************************************************************/

	public void setCurrentInstnace(VidadaInstanceConfig instance) {
		currentInstance = instance;
	}

	public VidadaInstanceConfig getCurrentInstance(){
		return currentInstance;
	}

	/***************************************************************************
	 *                                                                         *
	 * Private static methods                                                  *
	 *                                                                         *
	 **************************************************************************/


	private static File toAbsolutePath(String path){
		return toAbsolutePath(new File(path));
	}

	private static File toAbsolutePath(File path){
		if(path == null) return null;

		if(path.isAbsolute())
		{
			return path;
		}else {
			return new File(".", path.getPath());
		}
	}

}


