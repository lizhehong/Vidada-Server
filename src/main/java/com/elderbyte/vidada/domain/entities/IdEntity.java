package com.elderbyte.vidada.domain.entities;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Represents an entity with an auto-generated integer id.
 *
 * @author IsNull
 *
 */
@MappedSuperclass
@Access(AccessType.FIELD)
@XmlAccessorType(XmlAccessType.FIELD)
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
