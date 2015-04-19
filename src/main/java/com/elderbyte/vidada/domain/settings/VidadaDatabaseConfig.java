package com.elderbyte.vidada.domain.settings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents a Vidada database config
 * @author IsNull
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VidadaDatabaseConfig {

	private String databaseDirectory;
    private boolean useLocalCache;

	public VidadaDatabaseConfig(){}

	public VidadaDatabaseConfig(String databaseDirectory, boolean useLocalCache){
		this.databaseDirectory = databaseDirectory;
        this.useLocalCache = useLocalCache;
	}

	public String getFileCachePath() {
		return databaseDirectory + "/cache" ;
	}

	public String getDataBasePath() {
		return databaseDirectory + "/vidada";
	}

	public void setDatabaseDirectory(String databaseDirectory) {
		this.databaseDirectory = databaseDirectory;
	}

    /**
     * Determines if a local thumb cache is used additionally to a media library based cache.
     * @return
     */
    public boolean isUseLocalCache() {
        return useLocalCache;
    }

    public void setUseLocalCache(boolean useLocalCache) {
        this.useLocalCache = useLocalCache;
    }

	@Override
	public String toString(){
		return getDataBasePath();
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((databaseDirectory == null) ? 0 : databaseDirectory.hashCode());
		result = prime * result
				+ (useLocalCache ? 13 : 27);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VidadaDatabaseConfig other = (VidadaDatabaseConfig) obj;
		if (databaseDirectory == null) {
			if (other.databaseDirectory != null)
				return false;
		} else if (!databaseDirectory.equals(other.databaseDirectory))
			return false;
		return useLocalCache == useLocalCache;
	}



}
