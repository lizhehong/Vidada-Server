package com.elderbyte.common.locations;


/**
 * Represents authentication information.
 *
 * @author IsNull
 *
 */
public class Credentials {

	/**
     * Represents no credital information
	 */
	public static final Credentials ANONYMOUS = new Credentials(null,null,null);

    /***************************************************************************
     *                                                                         *
     * Private fields                                                          *
     *                                                                         *
     **************************************************************************/

	transient private boolean remember = false;;

	private String domain;
	private String username;
	private String password;

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

	/** Serialisation Constructor */
	protected Credentials(){ }

	public Credentials(String domain, String username, String password) {
		this(username, password);
		this.domain = domain;
	}

	public Credentials(String username, String password) {
		this.username = username;
		this.password = password;
	}

    /***************************************************************************
     *                                                                         *
     * Properties                                                              *
     *                                                                         *
     **************************************************************************/

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Shall this credentials be remembered by the key service?
	 * @param state
	 */
	public void setRemember(boolean state){
		this.remember = state;
	}

	/**
	 * Shall this credentials be remembered by the key service?
	 */
	public boolean isRemember() {
		return this.remember;
	}


    /***************************************************************************
     *                                                                         *
     * Public overridden API                                                   *
     *                                                                         *
     **************************************************************************/

	@Override
	public String toString(){
		return "domain: " + getDomain() + " user: " + getUsername() + " pass:{"+getPassword() + "}";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((domain == null) ? 0 : domain.hashCode());
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
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
		Credentials other = (Credentials) obj;
		if (domain == null) {
			if (other.domain != null)
				return false;
		} else if (!domain.equals(other.domain))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}



}
