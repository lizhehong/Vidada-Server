package com.elderbyte.common;

import java.util.StringTokenizer;

/**
 * Represents an immutable Version
 *
 * Sample:  2.5.6
 *
 *
 */
public final class Version implements Comparable {

    private static final String	SEPARATOR = ".";

    public static Version EMPTY = new Version(0, 0, 0);

    /**
     * Parses the given version string into a version.
     * @param versionString The version string like 2.5.3
     * @exception VersionFormatException Thrown when the string could not be parsed.
     * @return Returns the version object.
     */
    public static Version ofString(String versionString) throws VersionFormatException{

        try {
            int major = 0;
            int minor = 0;
            int micro = 0;
            String qualifier = ""; //$NON-NLS-1$

            StringTokenizer st = new StringTokenizer(versionString, SEPARATOR, true);
            major = Integer.parseInt(st.nextToken());

            if (st.hasMoreTokens()) {
                st.nextToken(); // consume delimiter
                minor = Integer.parseInt(st.nextToken());

                if (st.hasMoreTokens()) {
                    st.nextToken(); // consume delimiter
                    micro = Integer.parseInt(st.nextToken());

                    if (st.hasMoreTokens()) {
                        st.nextToken(); // consume delimiter
                        qualifier = st.nextToken();

                        if (st.hasMoreTokens()) {
                            throw new IllegalArgumentException("invalid format"); //$NON-NLS-1$
                        }
                    }
                }
            }
            return new Version(major, minor, micro, qualifier);
        }
        catch (Exception e) {
            throw new VersionFormatException("Could not parse version string '" + versionString + "'", e); //$NON-NLS-1$
        }
    }


    private final int major;
    private final int minor;
    private final int micro;
    private final String qualifier;


    /**
     * Creates a new immutable version
     * @param major
     * @param minor
     * @param micro
     */
    public Version(int major, int minor, int micro){
        this(major, minor, micro, null );
    }

    /**
     * Creates a new immutable version with a qualifier
     * @param major
     * @param minor
     * @param micro
     * @param qualifier
     */
    public Version(int major, int minor, int micro, String qualifier){
        this.major = major;
        this.minor = minor;
        this.micro = micro;
        this.qualifier = qualifier != null ? qualifier : "";
    }

    @Override
    public int compareTo(Object object) {
        if (object == this) return 0;

        if(object instanceof Version){
            Version other = (Version) object;

            int result = major - other.major;
            if (result != 0) {
                return result;
            }

            result = minor - other.minor;
            if (result != 0) {
                return result;
            }

            result = micro - other.micro;
            if (result != 0) {
                return result;
            }

            return qualifier.compareTo(other.qualifier);
        }
        return -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Version version = (Version) o;

        if (major != version.major) return false;
        if (minor != version.minor) return false;
        if (micro != version.micro) return false;
        return !(qualifier != null ? !qualifier.equals(version.qualifier) : version.qualifier != null);
    }

    @Override
    public int hashCode() {
        int result = major;
        result = 31 * result + minor;
        result = 31 * result + micro;
        result = 31 * result + (qualifier != null ? qualifier.hashCode() : 0);
        return result;
    }

    public String toString() {
        String base = major + SEPARATOR + minor + SEPARATOR + micro;
        if (qualifier.length() == 0) { //$NON-NLS-1$
            return base;
        }
        else {
            return base + SEPARATOR + qualifier;
        }
    }

    /**
     * Thrown when the version format was not valid.
     */
    public static class VersionFormatException extends Exception{
        public VersionFormatException(String message, Throwable cause){
            super(message, cause);
        }
    }
}
