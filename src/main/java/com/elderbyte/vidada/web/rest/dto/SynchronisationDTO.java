package com.elderbyte.vidada.web.rest.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 *
 */
// We use fields for JSON (de)serialisation
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
