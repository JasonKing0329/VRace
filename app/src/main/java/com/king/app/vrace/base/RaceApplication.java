package com.king.app.vrace.base;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.king.app.vrace.conf.AppConfig;
import com.king.app.vrace.conf.AppConstants;
import com.king.app.vrace.model.entity.DaoMaster;
import com.king.app.vrace.model.entity.DaoSession;
import com.king.app.vrace.model.entity.EliminationReasonDao;
import com.king.app.vrace.model.entity.MapBeanDao;
import com.king.app.vrace.model.entity.MapCountryDao;
import com.king.app.vrace.model.entity.MapPathDao;
import com.king.app.vrace.model.entity.TeamEliminationDao;
import com.king.app.vrace.model.setting.SettingProperty;
import com.king.app.vrace.utils.DebugLog;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.QueryBuilder;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/20 19:23
 */
public class RaceApplication extends Application {

    private static RaceApplication instance;

    private DaoSession daoSession;
    private RHelper helper;
    private Database database;

    public static RaceApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    /**
     * 程序初始化使用外置数据库
     * 需要由外部调用，如果在onCreate里直接初始化会创建新的数据库
     */
    public void createGreenDao() {
        if (SettingProperty.getDatabaseType() == AppConstants.DATABASE_REAL) {
            helper = new RHelper(getApplicationContext(), AppConfig.DB_CBS_NAME);
        }
        else {
            helper = new RHelper(getApplicationContext(), AppConfig.DB_NAME);
        }
        database = helper.getWritableDb();
        daoSession = new DaoMaster(database).newSession();

        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
    }

    public void reCreateGreenDao() {
        daoSession.clear();
        database.close();
        helper.close();
        createGreenDao();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public Database getDatabase() {
        return database;
    }

    public static class RHelper extends DaoMaster.OpenHelper {

        public RHelper(Context context, String name) {
            super(context, name);
        }

        public RHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(Database db, int oldVersion, int newVersion) {
            DebugLog.e(" oldVersion=" + oldVersion + ", newVersion=" + newVersion);
            switch (oldVersion) {
                case 1:
                case 2:
                    EliminationReasonDao.createTable(db, true);
                    TeamEliminationDao.createTable(db, true);
                case 3:
                    MapBeanDao.createTable(db, true);
                    MapPathDao.createTable(db, true);
                    MapCountryDao.createTable(db, true);
                    break;
            }
        }
    }
}
