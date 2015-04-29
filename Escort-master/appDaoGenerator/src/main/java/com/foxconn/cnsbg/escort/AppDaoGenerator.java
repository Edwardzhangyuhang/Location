package com.foxconn.cnsbg.escort;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class AppDaoGenerator {
    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "com.foxconn.cnsbg.escort.subsys.dao");

        Entity loc = schema.addEntity("CachedLocData");
        loc.addIdProperty();
        loc.addStringProperty("deviceID");
        loc.addStringProperty("carID");
        loc.addDateProperty("time");
        loc.addDoubleProperty("latitude");
        loc.addDoubleProperty("longitude");
        loc.addStringProperty("provider");
        loc.addFloatProperty("accuracy");
        loc.addDoubleProperty("altitude");
        loc.addFloatProperty("bearing");
        loc.addFloatProperty("speed");
        loc.addBooleanProperty("mock");

        loc.addIntProperty("batteryLevel");
        loc.addIntProperty("signalStrength");
        loc.addStringProperty("lockStatus");
        loc.addStringProperty("doorStatus");

        Entity ble = schema.addEntity("CachedBleData");
        ble.addIdProperty();
        ble.addStringProperty("deviceID");
        ble.addStringProperty("carID");
        ble.addDateProperty("time");
        ble.addStringProperty("mac");
        ble.addIntProperty("rssi");

        ble.addIntProperty("batteryLevel");
        ble.addIntProperty("signalStrength");
        ble.addStringProperty("lockStatus");
        ble.addStringProperty("doorStatus");

        new DaoGenerator().generateAll(schema, "app/src/main/java");
    }
}
