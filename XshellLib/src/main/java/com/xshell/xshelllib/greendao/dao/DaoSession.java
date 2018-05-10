package com.xshell.xshelllib.greendao.dao;

import android.database.sqlite.SQLiteDatabase;

import com.xshell.xshelllib.greendao.bean.AppUpdate;
import com.xshell.xshelllib.greendao.bean.FileCache;
import com.xshell.xshelllib.greendao.bean.Hint;
import com.xshell.xshelllib.greendao.bean.XLog;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig hintDaoConfig;
    private final DaoConfig appUpdateDaoConfig;
    private final DaoConfig fileCacheDaoConfig;
    private final DaoConfig xLogDaoConfig;

    private final HintDao hintDao;
    private final AppUpdateDao appUpdateDao;
    private final FileCacheDao fileCacheDao;
    private final XLogDao xLogDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        hintDaoConfig = daoConfigMap.get(HintDao.class).clone();
        hintDaoConfig.initIdentityScope(type);

        appUpdateDaoConfig = daoConfigMap.get(AppUpdateDao.class).clone();
        appUpdateDaoConfig.initIdentityScope(type);

        fileCacheDaoConfig = daoConfigMap.get(FileCacheDao.class).clone();
        fileCacheDaoConfig.initIdentityScope(type);

        xLogDaoConfig = daoConfigMap.get(XLogDao.class).clone();
        xLogDaoConfig.initIdentityScope(type);

        hintDao = new HintDao(hintDaoConfig, this);
        appUpdateDao = new AppUpdateDao(appUpdateDaoConfig, this);
        fileCacheDao = new FileCacheDao(fileCacheDaoConfig, this);
        xLogDao = new XLogDao(xLogDaoConfig, this);

        registerDao(Hint.class, hintDao);
        registerDao(AppUpdate.class, appUpdateDao);
        registerDao(FileCache.class, fileCacheDao);
        registerDao(XLog.class, xLogDao);
    }
    
    public void clear() {
        hintDaoConfig.getIdentityScope().clear();
        appUpdateDaoConfig.getIdentityScope().clear();
        fileCacheDaoConfig.getIdentityScope().clear();
        xLogDaoConfig.getIdentityScope().clear();
    }

    public HintDao getHintDao() {
        return hintDao;
    }

    public AppUpdateDao getAppUpdateDao() {
        return appUpdateDao;
    }

    public FileCacheDao getFileCacheDao() {
        return fileCacheDao;
    }

    public XLogDao getXLogDao() {
        return xLogDao;
    }

}
