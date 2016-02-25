package com.elderbyte.common.util;

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
