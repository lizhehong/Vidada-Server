package com.elderbyte.vidada.domain.util;

import archimedes.core.util.OSValidator;

/**
 *
 */
public class ConfigurationUtil {

    /**
     *
     * @return
     */
    public static String vidadaAppDataDirectory(){
        return OSValidator.defaultAppData() + "/Vidada";
    }


}
