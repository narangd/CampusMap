package com.example.campusmap.xmlparser;

import android.content.Context;
import android.content.res.XmlResourceParser;

import com.example.campusmap.R;
import com.example.campusmap.tree.branch.Building;
import com.example.campusmap.tree.branch.Floor;
import com.example.campusmap.tree.branch.Parent;
import com.example.campusmap.tree.branch.Room;
import com.example.campusmap.tree.branch.University;
import com.example.campusmap.xmlparser.search.Pair;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

public class BuildingInfoParser {
    private static BuildingInfoParser instance;
    private University university;
    private static final String ns = null;

    private BuildingInfoParser(XmlResourceParser parser) {
        university = new University();
        loadFromXml(parser);
    }

    private void loadFromXml(XmlResourceParser parser) {
        try {
            while (parser.next() != XmlResourceParser.END_DOCUMENT) {
                switch (parser.getEventType()) {
                    case XmlResourceParser.START_TAG:
                        manageStartTag(parser);
                        break;
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void manageStartTag(XmlResourceParser parser) {
        switch (parser.getName()) {
            case "building": // <building num="1" name="100주년 기념관">
                int building_num = Integer.parseInt(parser.getAttributeValue(ns, "num"));
                String building_name = parser.getAttributeValue(ns, "name");
                university.addBuilding(new Building(building_num, building_name));
                break;
            case "floor": // <floor num="1">
                int floor_num = Integer.parseInt( parser.getAttributeValue(ns, "num") );
                university.concatFloor(new Floor(floor_num, university.getLast()));
                break;
            case "room": // <room name="방재센터">
                String room_name = parser.getAttributeValue(ns, "name");
                String room_text = getRoomText(parser);
                university.concatRoom(new Room(room_name, room_text, university.getLast().getLast()));
                break;
        }
    }

    private String getRoomText(XmlResourceParser parser) {
        try {
            parser.require(XmlResourceParser.START_TAG, ns, "room");
            parser.next();
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        return parser.getText();
    }

    public University toBuildingList() {
        return university;
    }

    public Building toFloorList(int buildingIndex) {
        return toBuildingList().get(buildingIndex);
    }

    public Floor toRoomList(int buildingIndex, int floorIndex) {
        return toBuildingList().get(buildingIndex).get(floorIndex);
    }

    public static BuildingInfoParser getInstance(XmlResourceParser parser) {
        if (instance == null)
            instance = new BuildingInfoParser(parser);
        return instance;
    }


    public static ArrayList<Parent> search(Context context, final String query) {
        BuildingInfoParser parser = getInstance(context.getResources().getXml(R.xml.building_info));
        ArrayList<Parent> result = new ArrayList<>();
        University buildingList = parser.toBuildingList();

        for (Building floorList : buildingList) {
            String building = floorList.toString();
            if (building.matches(".*" + query + ".*")) {
                result.add(floorList);
            }
            for (Floor roomList : floorList) {
                String floor = roomList.toString();
                if (floor.matches(".*" + query + ".*")) {
                    result.add(roomList);
                }
                for (Room roomItem : roomList) {
                    String room = roomItem.toString();
                    if (room.matches(".*" + query + ".*")) {
                        result.add(roomItem);
                    }
                }
            }
        }
        return result;
    }

    // TODO: 2015-11-23 DataBase로 대체하면 사용..
//    public static Cursor searchSuggestions(Context context, String query) {
//        BuildingInfoParser parser = new BuildingInfoParser(context.getResources().getXml(R.xml.building_info));
//        TreeItem buildingList = parser.toBuildingList();
//        MatrixCursor result = new MatrixCursor(new String[]{BaseColumns._ID, "str"});
//        int id = 0;
//
//        for (TreeItem floorList : buildingList) {
//            String building = floorList.toString();
//            if (building.matches(".*" + query + ".*")) {
//                result.addRow(new Object[] {id++, building});
//            }
//            for (TreeItem roomList : floorList) {
//                String floor = roomList.toString();
//                if (floor.matches(".*" + query + ".*")) {
//                    result.addRow(new Object[]{id++, floor});
//                }
//                for (TreeItem roomItem : roomList) {
//                    String room = roomItem.toString();
//                    if (room.matches(".*" + query + ".*")) {
//                        result.addRow(new Object[]{id++, room});
//                    }
//                }
//            }
//        }
//        return result;
//    }
}