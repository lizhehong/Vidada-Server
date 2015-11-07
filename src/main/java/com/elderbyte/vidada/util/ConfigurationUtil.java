package com.elderbyte.vidada.util;

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
