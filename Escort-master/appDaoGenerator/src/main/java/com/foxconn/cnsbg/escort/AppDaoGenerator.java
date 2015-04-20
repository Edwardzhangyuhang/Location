package com.foxconn.cnsbg.escort;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

public class AppDaoGenerator {
    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "com.foxconn.cnsbg.escort.subsys.dao");

        Entity location = schema.addEntity("LocationEntity");
        location.addIdProperty();
        Property locationTime = location.addLongProperty("datetimestamp").getProperty();
        location.addFloatProperty("distance");
        location.addBooleanProperty("firstPoint");
        location.addDoubleProperty("latitude");
        location.addDoubleProperty("longitude");

        Entity user = schema.addEntity("UserEntity");
        user.addIdProperty();
        user.addFloatProperty("hourDistance");
        user.addFloatProperty("totalDistance");//also used to mark hidden status
        Property username = user.addStringProperty("username").getProperty();

        Entity mainuser = schema.addEntity("MainUserEntity");
        //MainUser is inherited from User but which may cause confusion
        //mainuser.setSuperclass("UserEntity");
        mainuser.addIdProperty();
        mainuser.addStringProperty("mainusername");

        Property userIdProperty = location.addLongProperty("userId").getProperty();
        location.addToOne(user, userIdProperty).setName("user");
        ToMany userToLocation = user.addToMany(location, userIdProperty);
        userToLocation.orderAsc(locationTime);
        userToLocation.setName("locations");

        Property locationIdProperty = user.addLongProperty("locationId").getProperty();
        user.addToOne(location, locationIdProperty).setName("lastLocation");

        //"friendId = null" means "not MainUser's friend"
        Property friendIdProperty = user.addLongProperty("friendId").getProperty();
        user.addToOne(mainuser, friendIdProperty).setName("friend");
        ToMany mainuserToUser = mainuser.addToMany(user, friendIdProperty);
        mainuserToUser.orderAsc(username);
        mainuserToUser.setName("friends");

        //"inviteId = null" means "not invite request from other user"
        Property inviteIdProperty = user.addLongProperty("inviteId").getProperty();
        user.addToOne(mainuser, inviteIdProperty).setName("invite");
        mainuserToUser = mainuser.addToMany(user, inviteIdProperty);
        mainuserToUser.orderAsc(username);
        mainuserToUser.setName("invites");

        Entity cachedLocation = schema.addEntity("CachedLocationEntity");
        cachedLocation.addIdProperty();
        cachedLocation.addStringProperty("UDID");
        cachedLocation.addLongProperty("datetimestamp");
        cachedLocation.addIntProperty("latitudeE6");
        cachedLocation.addIntProperty("longitudeE6");

        Entity cachedAcceleration = schema.addEntity("CachedAccelerationEntity");
        cachedAcceleration.addIdProperty();
        cachedAcceleration.addStringProperty("UDID");
        cachedAcceleration.addLongProperty("datetimestamp");
        cachedAcceleration.addFloatProperty("accelX_avg");
        cachedAcceleration.addFloatProperty("accelY_avg");
        cachedAcceleration.addFloatProperty("accelZ_avg");
        cachedAcceleration.addDoubleProperty("accelX_stddev");
        cachedAcceleration.addDoubleProperty("accelY_stddev");
        cachedAcceleration.addDoubleProperty("accelZ_stddev");

        new DaoGenerator().generateAll(schema, "app/src/main/java");
    }
}
