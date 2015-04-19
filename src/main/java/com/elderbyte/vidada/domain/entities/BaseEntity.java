package com.elderbyte.vidada.domain.entities;


import archimedes.core.data.observable.ObservableBean;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Base class for all DB Entities.
 * Provides basic functionality, and  proper ID handling in equals.
 * @author IsNull
 */
@Access(AccessType.FIELD)
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class BaseEntity extends ObservableBean {

}
