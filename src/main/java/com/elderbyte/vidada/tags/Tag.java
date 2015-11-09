package com.elderbyte.vidada.tags;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


/**
 * Represents a simple tag, where the the tag name is the id.
 *
 * @author IsNull
 *
 */
@Entity
@Access(AccessType.FIELD)
@XmlAccessorType(XmlAccessType.FIELD)
public class Tag  implements Comparable<Tag> {

    /***************************************************************************
     *                                                                         *
     * Static builder                                                          *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a new Tag from the given string.
     * @param tagName
     * @return
     */
    public static Optional<Tag> buildTag(String tagName){
        Tag tag = null;
        tagName = toTagIdName(tagName);
        tag = new Tag(tagName);

        if(tag.isValid()) {
            return Optional.ofNullable(tag);
        }else{
            return Optional.empty();
        }
    }

    /**
     * Creates a new Tag from the given string.
     * @param tags
     * @return
     */
    public static Set<Tag> buildTags(String... tags){
        Set<Tag> createdTags = new HashSet<>();
        for(String tagStr : tags){
            Optional<Tag> t = Tag.buildTag(tagStr);
            if(t.isPresent()){
                createdTags.add(t.get());
            }
        }
        return createdTags;
    }

    private static String toTagIdName(String text){
        String tagIdName = text.trim().toLowerCase();
        tagIdName = tagIdName.replaceAll("\\s+", ".");  // Replace whitespaces by .
        return tagIdName;
    }


	/***************************************************************************
	 *                                                                         *
	 * Private fields                                                          *
	 *                                                                         *
	 **************************************************************************/

	transient int hashcode_cache = -1;

	@Id
	@Column(nullable=false)
	private String name;


	/***************************************************************************
	 *                                                                         *
	 * Constructors                                                            *
	 *                                                                         *
	 **************************************************************************/

    /**
     * Empty ORM Constructor
     */
	protected Tag() { }

    /**
     * Creates a new Tag with the given name
     * @param name
     */
	Tag(String name){
		this.setName(name);
	}

	/***************************************************************************
	 *                                                                         *
	 * Properties                                                              *
	 *                                                                         *
	 **************************************************************************/

    /**
     * Gets the tag name
     * @return
     */
	public String getName() {
		return name;
	}

	protected void setName(String name) {
		this.name = name;
	}

    /**
     * Is this tag valid
     * @return
     */
    public boolean isValid() {
        return(this.name != null && !this.name.isEmpty());
    }

	/***************************************************************************
	 *                                                                         *
	 * Overridden Public API                                                   *
	 *                                                                         *
	 **************************************************************************/

	@Override
	public String toString(){
		return name;
	}

	@Override
	public int hashCode() {
		if(hashcode_cache == -1){
			final int prime = 31;
			hashcode_cache = prime * 1 + ((name == null) ? 0 : name.hashCode());
		}
		return hashcode_cache;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tag other = (Tag) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public int compareTo(Tag o) {
		if(o == null) return -1;
		return this.getName().compareTo(o.getName());
	}


}
