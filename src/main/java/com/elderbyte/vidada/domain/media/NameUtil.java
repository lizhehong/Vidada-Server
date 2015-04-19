package com.elderbyte.vidada.domain.media;

public class NameUtil {

	/**
	 * Makes the given string better readable by removing special chars
	 * TODO Move to MediaUtils
	 *
	 * @param rawName
	 * @return
	 */
	public static String prettifyName(String rawName) {

		rawName = rawName.replaceAll("\\[.*?\\]", "");
		rawName = rawName.replaceAll("\\(.*?\\)", "");

		rawName = rawName.replaceAll("[\\.|_|-]", " ");

		return rawName.trim();
	}
}
