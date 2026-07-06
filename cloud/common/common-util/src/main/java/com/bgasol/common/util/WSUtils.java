package com.bgasol.common.util;

public class WSUtils {
    public static String GetWSTopic(String serviceName) {
        return "system.ws." + serviceName;
    }
}
