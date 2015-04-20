package com.elderbyte.vidada.domain.settings;

import archimedes.core.exceptions.NotSupportedException;
import archimedes.core.expressions.Predicate;
import archimedes.core.geometry.Size;
import archimedes.core.util.Lists;
import archimedes.core.util.OSValidator;
import com.elderbyte.vidada.domain.util.ConfigurationUtil;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VidadaServerSettings extends JsonSettings {

	/***************************************************************************
     *                                                                         *
	 * Transient fields                                                        *
	 *                                                                         *
	 **************************************************************************/

    transient private final Logger logger = LoggerFactory.getLogger(this.getClass());
	transient public static final String ProductName = "vidada-server";

	transient public static File Path;
	transient public static String defaultDB;

	static {
		// Default paths
		Path = new File(ConfigurationUtil.vidadaAppDataDirectory(), ProductName + ".json");
		defaultDB  = ConfigurationUtil.vidadaAppDataDirectory() + "/data";
	}


	transient private VidadaDatabaseConfig currentDBConfig = null;

	// thumb size boundaries
	transient public static final int THUMBNAIL_SIZE_MAX = 500;
	transient public static final double THUMBNAIL_SIDE_RATIO = 0.70;

	/***************************************************************************
	 *                                                                         *
	 * Persistent fields                                                       *
	 *                                                                         *
	 **************************************************************************/

	private Set<VidadaDatabaseConfig> databases = new HashSet<VidadaDatabaseConfig>();
	private boolean usingMetaData = true;
	private boolean isDebug = false;
	private boolean enableNetworkSharing = true;


	/***************************************************************************
	 *                                                                         *
	 * Singleton                                                               *
	 *                                                                         *
	 **************************************************************************/

	transient private static VidadaServerSettings instance;
	public static VidadaServerSettings instance(){

		if(instance == null){

			if(Path.exists())
			{
				instance = JsonSettings.loadSettings(Path, VidadaServerSettings.class);
			}
		}

		if(instance == null){
			instance = new VidadaServerSettings();
		}

		instance.setPath(Path);
		instance.persist();

		return instance;
	}

	private VidadaServerSettings(){
		databases.add(getDefaultConfig());
	}

	/***************************************************************************
	 *                                                                         *
	 * Properties                                                              *
	 *                                                                         *
	 **************************************************************************/


	/**
	 * Returns the max thumb resolution used in the whole application
	 * (HDPI aware)
	 * @return
	 */
	public Size getMaxThumbResolution(){
		int maxWidth = THUMBNAIL_SIZE_MAX * (OSValidator.isHDPI() ? 2 : 1);
		return new Size(
				maxWidth,
				(int)((double)maxWidth*THUMBNAIL_SIDE_RATIO));
	}


	/**
	 *
	 * @return
	 */
	public List<VidadaDatabaseConfig> getAvaiableDatabases(){
		return Lists.where(databases, new Predicate<VidadaDatabaseConfig>() {
            @Override
            public boolean where(VidadaDatabaseConfig t) {
                File path = toAbsolutePath(t.getDataBasePath()).getParentFile().getAbsoluteFile();

                logger.debug("Available database " + path.getAbsolutePath() + " exists? " + path.exists());
                return path.exists();
            }
        });
	}



	/**
	 * Automatically configure the database
	 * @return Returns true if the db was configured successfully.
	 */
	public boolean autoConfigDatabase() {

		if(OSValidator.isAndroid())
		{
            logger.info("config db for Android, using default location:");
			setCurrentDBConfig(getDefaultConfig());
			return true;
		}else{
            logger.info("found " + databases.size() + " configured DBs.");

			List<VidadaDatabaseConfig> availableDbs = getAvaiableDatabases();

			if(availableDbs.isEmpty()){
                logger.info("No database config points to an existing location. Restored defaults.");
				databases.add(getDefaultConfig());
				availableDbs.add(getDefaultConfig());
				persist();
			}

			if(availableDbs.size() == 1){
				VidadaDatabaseConfig mydb = availableDbs.get(0);
				if(mydb != null)
				{
					setCurrentDBConfig(mydb);
                    logger.info("Auto-config the only available database as primary.");
					return true;
				}
			}
		}

		return false;
	}


	/**
	 * Available
	 * @return
	 */
	public VidadaDatabaseConfig getCurrentDBConfig() {
		return currentDBConfig;
	}

	private void checkDatabaseConfig() throws NotSupportedException{
		if(getCurrentDBConfig() == null)
		{
			throw new NotSupportedException("Database is not configured!");
		}
	}

	public void setCurrentDBConfig(VidadaDatabaseConfig db){
		currentDBConfig = db;
		logger.debug("current db: " + currentDBConfig != null ? currentDBConfig.getDataBasePath() : "null");
	}

	public File getAbsoluteDBPath(){
		checkDatabaseConfig();
		return toAbsolutePath(getCurrentDBConfig().getDataBasePath());
	}

	/**
	 * Gets the path to the local cache
	 * @return
	 */
	public File getAbsoluteCachePath() {
		checkDatabaseConfig();
		return toAbsolutePath(getCurrentDBConfig().getFileCachePath());
	}


	/**
	 * Determites if meta data is used to help identify files
	 * and store additional informations
	 * @return
	 */
	public boolean isUsingMetaData() {
		return usingMetaData;
	}


	public void setUsingMetaData(boolean usingMetaData) {
		this.usingMetaData = usingMetaData;
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

	public boolean isEnableNetworkSharing() {
		return enableNetworkSharing;
	}

	public void setEnableNetworkSharing(boolean enableNetworkSharing) {
		this.enableNetworkSharing = enableNetworkSharing;
	}

	/***************************************************************************
	 *                                                                         *
	 * Private static methods                                                  *
	 *                                                                         *
	 **************************************************************************/

	private static VidadaDatabaseConfig getDefaultConfig(){
		VidadaDatabaseConfig defaultConfig = new VidadaDatabaseConfig(defaultDB, false);
		return defaultConfig;
	}


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
