package com.elderbyte.vidada.vidada.media;

import org.apache.commons.io.FilenameUtils;

public class NameUtil {

	/**
	 * Makes the given file-name string better readable by removing special chars, extensions etc.
	 * @param rawName
	 * @return
	 */
	public static String fromFileNameToTitle(String rawName) {

        rawName = rawName.trim();

        rawName = FilenameUtils.removeExtension(rawName);

		rawName = rawName.replaceAll("\\[.*?\\]", "");
		rawName = rawName.replaceAll("\\(.*?\\)", "");

		rawName = rawName.replaceAll("[\\.|_|-]", " ");

		return rawName.trim();
	}
}
