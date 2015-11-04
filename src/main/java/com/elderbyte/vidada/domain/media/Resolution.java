package com.elderbyte.vidada.domain.media;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;

/**
 * Represents the resolution of a visual media.
 */
@Embeddable
@Access(AccessType.FIELD)   //Use fields for ORM mapping
public class Resolution {

    public static final Resolution Empty = new Resolution(0,0);

    private int width;
    private int height;

    /**
     * Empty ORM / JSON Constructor
     */
    protected Resolution(){ }

    /**
     * Creates a new Resolution
     * @param width
     * @param height
     */
    public Resolution(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Parses a resolution String like '1024x720' into a Resolution object
     * @param resolution
     * @return
     */
    public static Resolution ofString(String resolution) {
        if(resolution != null){
            String[] parts = resolution.split("x");
            if(parts.length > 1){
                try {
                    return new Resolution(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
                }catch (NumberFormatException e){
                    return Resolution.Empty;
                }
            }
        }
        return Resolution.Empty;
    }


    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isEmpty() {
        return width == 0 && height == 0;
    }

    @Override
    public String toString() {
        return width + "x" + height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Resolution that = (Resolution) o;

        if (width != that.width) return false;
        return height == that.height;

    }

    @Override
    public int hashCode() {
        int result = width;
        result = 31 * result + height;
        return result;
    }



}
