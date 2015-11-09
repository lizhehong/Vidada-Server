package com.elderbyte.vidada.agents;


import com.elderbyte.vidada.media.Resolution;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Holds media metadata information
 */
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE)
public class MediaMetadataDto {

    private Set<String> productionCompanies = new HashSet<>();
    private Set<String> genres = new HashSet<>();
    private Set<String> cast = new HashSet<>();

    private ZonedDateTime releaseDate;
    private String description;
    private Float rating;
    private String posterUrl;
    private Resolution resolution;
    private Integer bitRate;
    private Integer duration;

    public MediaMetadataDto(){ }

    public Integer getBitRate() {
        return bitRate;
    }

    public void setBitRate(int bitRate) {
        this.bitRate = bitRate;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public ZonedDateTime getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(ZonedDateTime releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<String> getProductionCompanies() {
        return productionCompanies;
    }


    public Set<String> getGenres() {
        return genres;
    }

    public Set<String> getCast() {
        return cast;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public Resolution getResolution() {
        return resolution;
    }

    public void setResolution(Resolution resolution) {
        this.resolution = resolution;
    }
}
