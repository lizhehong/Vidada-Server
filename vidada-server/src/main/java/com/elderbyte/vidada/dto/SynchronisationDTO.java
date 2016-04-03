package com.elderbyte.vidada.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 *
 */
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE)
public class SynchronisationDTO {

    private boolean isSynchronizing;

    public SynchronisationDTO(boolean isSynchronizing){
        this.isSynchronizing = isSynchronizing;
    }

    public boolean isSynchronizing() {
        return isSynchronizing;
    }

    public void setIsSynchronizing(boolean isSynchronizing) {
        this.isSynchronizing = isSynchronizing;
    }
}
