package com.elderbyte.server.vidada.tasks;

/**
 * A job id.
 * A Job represents an asynchronous running task.
 * @author IsNull
 *
 */
public class JobId {

	private final int id;


	public JobId(int id){
		this.id = id;
	}


	public int getId(){
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		JobId other = (JobId) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
