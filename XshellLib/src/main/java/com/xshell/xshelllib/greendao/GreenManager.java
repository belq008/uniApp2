package com.xshell.xshelllib.greendao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.xshell.xshelllib.greendao.dao.AppUpdateDao;
import com.xshell.xshelllib.greendao.dao.DaoMaster;
import com.xshell.xshelllib.greendao.dao.DaoSession;
import com.xshell.xshelllib.greendao.dao.FileCacheDao;
import com.xshell.xshelllib.greendao.dao.HintDao;
import com.xshell.xshelllib.greendao.dao.XLogDao;


/**
 * Created by zzy on 2016/8/3.
 * greenDao管理器
 */
public class GreenManager {

    private static GreenManager instance;
    private static Object object = new Object();
    private SQLiteDatabase db;
    private DaoSession daoSession;


    public static GreenManager getInstance(Context context) {
        if (instance == null) {
            synchronized (object) {
                if (instance == null) {
                    instance = new GreenManager(context);
                }
            }
        }
        return instance;
    }

    private GreenManager(Context context) {
        // 官方推荐将获取 DaoMaster 对象的方法放到 Application 层，这样将避免多次创建生成 Session 对象
        setupDatabase(context);
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    private void setupDatabase(Context context) {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "app-db", null);
        db = helper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();

    }

    public AppUpdateDao getAppUpdateDao() {
        return daoSession.getAppUpdateDao();
    }

    public HintDao getHintDao() {
        return daoSession.getHintDao();
    }

    public FileCacheDao getFileCacheDao() {
        return daoSession.getFileCacheDao();
    }
    public XLogDao getXLogDao() {
        return daoSession.getXLogDao();
    }


    /*↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑greenDAO↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑*/
}
