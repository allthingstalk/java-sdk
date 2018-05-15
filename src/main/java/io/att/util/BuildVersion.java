package io.att.util;

public class BuildVersion {
    public static String getBuildVersion() {
        String version = BuildVersion.class.getPackage().getImplementationVendor();
        return version != null ? version : "0.0.0";
    }
}
