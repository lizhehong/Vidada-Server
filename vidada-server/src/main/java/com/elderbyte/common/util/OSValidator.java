package com.elderbyte.common.util;

import java.io.File;


/**
 * Provides methods to identify the current executing OS / Platform
 * @author IsNull
 *
 */
public class OSValidator {

	/**
     * Get the name of the current running platform
	 * @return
	 */
	public static String getPlatformName() {
		Platform platform = getPlatform();
		return platform.toString().toLowerCase();
	}

	/**
	 * Get the current running platform
	 * @return
	 */
	public static Platform getPlatform() {
		if (isWindows()) {
			return Platform.Windows;
		} else if (isOSX()) {
			return Platform.OSX;
		} else if (isUnix()) {
			return Platform.Unix;
		} else if (isLinux()) {
			return Platform.Linux;
		} else if (isSolaris()) {
			return Platform.Solaris;
		} else if (isAndroid()) {
			return Platform.Android;
		}
		return Platform.None;
	}

	/**
	 * Is the current OS a windows?
	 * @return
	 */
	public static boolean isWindows() {

		String os = System.getProperty("os.name").toLowerCase();
		// windows
		return (os.contains("win"));

	}

	/**
	 * Is the current operating system OS X ?
	 * @return
	 */
	public static boolean isOSX() {
		String os = System.getProperty("os.name").toLowerCase();
		// Mac
		return (os.contains("mac"));
	}

	/**
	 * Is the current OS a Unix?
	 * @return
	 */
	public static boolean isUnix() {
		String os = System.getProperty("os.name").toLowerCase();
		return os.contains("nix");
	}

	/**
	 * Is the current OS a Linux/Unix?
	 * @return
	 */
	public static boolean isLinux() {
		String os = System.getProperty("os.name").toLowerCase();
		return os.contains("nux");
	}

	/**
	 * Is the current OS a Linux Solaris?
	 * @return
	 */
	public static boolean isSolaris() {
		String os = System.getProperty("os.name").toLowerCase();
		return (os.contains("sunos"));
	}

	/**
	 * IS the current OS a Android OS?
	 * @return
	 */
	public static boolean isAndroid(){
		String os = System.getProperty("java.runtime.name").toLowerCase();
		return os.contains("android");
	}

	/**
	 * Gets the default App data folder for the current running platform
	 * @return
	 */
	public static File defaultAppData()
	{
		String appDataPath = null;

		String OS = System.getProperty("os.name").toUpperCase();
		if (OS.contains("WIN"))
			appDataPath = System.getenv("APPDATA");
		else if (OS.contains("MAC"))
			appDataPath = System.getProperty("user.home") + "/Library/Application Support";
		else if (OS.contains("NUX"))
			appDataPath = System.getProperty("user.home");
		else
			appDataPath = System.getProperty("user.dir");


		return appDataPath!= null ? new File(appDataPath) : null;
	}

	private static boolean ForceHDPI = false;

	/**
	 * Force HDPI rendering
	 * This will cause {@code isHDPI()} to always return true.
	 * @param forceHDPI
	 */
	public static void setForceHDPI(boolean forceHDPI){
		ForceHDPI = forceHDPI;
	}

	/**
	 * Determines if the current display has a HDPI (retina) resolution
	 * @return
	 */
	public static boolean isHDPI() {

		// TODO also support android

		if(ForceHDPI)
			return true;

		if (isOSX()) {
			// This should probably be reset each time there's a display change.
			// A 5-minute search didn't turn up any such event in the Java API.
			// Also, should we use the Toolkit associated with the editor window?

			// NOTE: apple.awt.contentScaleFactor is only avaiable in Java 1.6 from Apple
			// Java 8 does also support HDPI rendering, but currently there is no easy way
			// to detect it automatically.
			boolean retinaProp=false;
			Float prop = (Float)
					java.awt.Toolkit.getDefaultToolkit().getDesktopProperty("apple.awt.contentScaleFactor");
			if (prop != null) {
				retinaProp = prop == 2;
			}

			return retinaProp;
		}
		return false;
	}


    /**
     * Represents an operating system family / platform
     * @author IsNull
     *
     */
    public enum Platform {
        None,

        Windows,
        OSX,
        Unix,
        Linux,
        Android,
        Solaris
    }






}
