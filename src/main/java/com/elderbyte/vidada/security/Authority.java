package com.elderbyte.vidada.security;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Collection;

/**
 * An authority (a security role) used by Spring Security.
 */
@Entity
@Table(name = "T_AUTHORITY")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Authority implements Serializable {

    @NotNull
    @Size(min = 0, max = 50)
    @Id
    @Column(length = 50)
    private String name;

    /** Empty Constructor for ORM */
    protected Authority(){}

    public Authority(String name){
        setName(name);
    }

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Authority authority = (Authority) o;

        if (name != null ? !name.equals(authority.name) : authority.name != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Authority{" +
                "name='" + name + '\'' +
                "}";
    }

    /**
     * Returns a comma delimited string of authorities
     * @param authorities
     * @return
     */
    public static String toFlatString(Collection<Authority> authorities){
        String roleString = "";
        if(!authorities.isEmpty()){
            for (Authority role : authorities){
                roleString += role.getName()+",";
            }
            return roleString.substring(0, roleString.length()-1);
        }
        return roleString;
    }
}
