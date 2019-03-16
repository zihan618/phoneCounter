package com.szh.a12260.phone_counter.entity;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table "MONTH_RECORD".
 */
public class MonthRecordDao extends AbstractDao<MonthRecord, Long> {

    public static final String TABLENAME = "MONTH_RECORD";

    /**
     * Properties of entity MonthRecord.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Timestamp = new Property(1, Long.class, "timestamp", false, "TIMESTAMP");
        public final static Property PackageName = new Property(2, String.class, "packageName", false, "PACKAGE_NAME");
        public final static Property TimeSpent = new Property(3, Long.class, "timeSpent", false, "TIME_SPENT");
    }


    public MonthRecordDao(DaoConfig config) {
        super(config);
    }

    public MonthRecordDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /**
     * Creates the underlying database table.
     */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "\"MONTH_RECORD\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"TIMESTAMP\" INTEGER," + // 1: timestamp
                "\"PACKAGE_NAME\" TEXT," + // 2: packageName
                "\"TIME_SPENT\" INTEGER);"); // 3: timeSpent
    }

    /**
     * Drops the underlying database table.
     */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"MONTH_RECORD\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, MonthRecord entity) {
        stmt.clearBindings();

        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }

        Long timestamp = entity.getTimestamp();
        if (timestamp != null) {
            stmt.bindLong(2, timestamp);
        }

        String packageName = entity.getPackageName();
        if (packageName != null) {
            stmt.bindString(3, packageName);
        }

        Long timeSpent = entity.getTimeSpent();
        if (timeSpent != null) {
            stmt.bindLong(4, timeSpent);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, MonthRecord entity) {
        stmt.clearBindings();

        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }

        Long timestamp = entity.getTimestamp();
        if (timestamp != null) {
            stmt.bindLong(2, timestamp);
        }

        String packageName = entity.getPackageName();
        if (packageName != null) {
            stmt.bindString(3, packageName);
        }

        Long timeSpent = entity.getTimeSpent();
        if (timeSpent != null) {
            stmt.bindLong(4, timeSpent);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }

    @Override
    public MonthRecord readEntity(Cursor cursor, int offset) {
        MonthRecord entity = new MonthRecord( //
                cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
                cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // timestamp
                cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // packageName
                cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3) // timeSpent
        );
        return entity;
    }

    @Override
    public void readEntity(Cursor cursor, MonthRecord entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setTimestamp(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setPackageName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setTimeSpent(cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3));
    }

    @Override
    protected final Long updateKeyAfterInsert(MonthRecord entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }

    @Override
    public Long getKey(MonthRecord entity) {
        if (entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(MonthRecord entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }

}
