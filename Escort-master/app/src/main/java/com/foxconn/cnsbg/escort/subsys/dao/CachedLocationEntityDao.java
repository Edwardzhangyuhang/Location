package com.foxconn.cnsbg.escort.subsys.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table CACHED_LOCATION_ENTITY.
*/
public class CachedLocationEntityDao extends AbstractDao<CachedLocationEntity, Long> {

    public static final String TABLENAME = "CACHED_LOCATION_ENTITY";

    /**
     * Properties of entity CachedLocationEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property UDID = new Property(1, String.class, "UDID", false, "UDID");
        public final static Property Datetimestamp = new Property(2, Long.class, "datetimestamp", false, "DATETIMESTAMP");
        public final static Property LatitudeE6 = new Property(3, Integer.class, "latitudeE6", false, "LATITUDE_E6");
        public final static Property LongitudeE6 = new Property(4, Integer.class, "longitudeE6", false, "LONGITUDE_E6");
    };


    public CachedLocationEntityDao(DaoConfig config) {
        super(config);
    }
    
    public CachedLocationEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'CACHED_LOCATION_ENTITY' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'UDID' TEXT," + // 1: UDID
                "'DATETIMESTAMP' INTEGER," + // 2: datetimestamp
                "'LATITUDE_E6' INTEGER," + // 3: latitudeE6
                "'LONGITUDE_E6' INTEGER);"); // 4: longitudeE6
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'CACHED_LOCATION_ENTITY'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, CachedLocationEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String UDID = entity.getUDID();
        if (UDID != null) {
            stmt.bindString(2, UDID);
        }
 
        Long datetimestamp = entity.getDatetimestamp();
        if (datetimestamp != null) {
            stmt.bindLong(3, datetimestamp);
        }
 
        Integer latitudeE6 = entity.getLatitudeE6();
        if (latitudeE6 != null) {
            stmt.bindLong(4, latitudeE6);
        }
 
        Integer longitudeE6 = entity.getLongitudeE6();
        if (longitudeE6 != null) {
            stmt.bindLong(5, longitudeE6);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public CachedLocationEntity readEntity(Cursor cursor, int offset) {
        CachedLocationEntity entity = new CachedLocationEntity( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // UDID
            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2), // datetimestamp
            cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // latitudeE6
            cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4) // longitudeE6
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, CachedLocationEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUDID(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setDatetimestamp(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
        entity.setLatitudeE6(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setLongitudeE6(cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(CachedLocationEntity entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(CachedLocationEntity entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
