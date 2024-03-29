package com.elderbyte.vidada.entities;

import javax.persistence.*;

/**
 * Represents an entity with an auto-generated integer id.
 *
 * @author IsNull
 *
 */
@MappedSuperclass
@Access(AccessType.FIELD)
public abstract class IdEntity extends BaseEntity {

	@Id
	@GeneratedValue
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id){
		this.id = id;
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
		IdEntity other = (IdEntity) obj;
		if (id != other.id)
			return false;
		return true;
	}


}
