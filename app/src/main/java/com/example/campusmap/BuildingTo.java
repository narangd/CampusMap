package com.example.campusmap;


public class BuildingTo {

    static public final String MAILTO_SCHEME = "buildingto:";

    private int mBuildingID;

//    MailTo

    private BuildingTo() {
    }

    public static boolean isBuildingTo(String url) {
        if (url != null && url.startsWith(MAILTO_SCHEME)) {
            return true;
        }
        return false;
    }

    public static BuildingTo parse(String url) {
        String noScheme = url.substring(MAILTO_SCHEME.length());
        BuildingTo buildingTo = new BuildingTo();
        buildingTo.mBuildingID = Integer.parseInt(noScheme);
        return buildingTo;
    }

    public int getID() {
        return mBuildingID;
    }
}
