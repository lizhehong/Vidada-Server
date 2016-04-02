package com.elderbyte.vidada.vidada.entities;


import com.fasterxml.jackson.annotation.JsonAutoDetect;

import javax.persistence.Access;
import javax.persistence.AccessType;

/**
 * Base class for all DB Entities.
 * Provides basic functionality, and  proper ID handling in equals.
 * @author IsNull
 */
@Access(AccessType.FIELD)   // Hibernate uses fields for ORM mapping
@JsonAutoDetect(            // We use fields for JSON (de)serialisation
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    getterVisibility = JsonAutoDetect.Visibility.NONE)
public abstract class BaseEntity {

}
