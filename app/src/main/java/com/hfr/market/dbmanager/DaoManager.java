package com.hfr.market.dbmanager;

import android.content.Context;

import com.hfr.market.model.DaoMaster;
import com.hfr.market.model.DaoSession;

import org.greenrobot.greendao.query.QueryBuilder;

/**
 * 1、创建数据库
 * 2、创建数据库的表
 * 3、包含对数据库的CRUD
 * 4、对数据库的升级
 */
public class DaoManager {
    private static final String TAG = DaoManager.class.getSimpleName();
    //数据库名称
    private static final String DB_NAME = "mydb.sqlite";
    //多线程访问，声明单例
    private volatile static DaoManager manager;
    private static DaoMaster.DevOpenHelper helper;
    private static DaoMaster daoMaster;
    private static DaoSession daoSession;
    private Context context;

    /**
     * 使用单例模式获得操作数据库的对象：要保证数据的安全有效
     *
     * @return
     */
    public static DaoManager getInstance() {
        DaoManager instance = null;
        if (instance == null) {
            synchronized (DaoManager.class) {
                if (instance == null) {
                    instance = new DaoManager();
                    manager = instance;
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context;
    }

    /**
     * 写入操作，系统会判断是否存在数据库，如果没有则创建数据库
     */
    public DaoMaster getDaoMaster() {
        if (daoMaster == null) {
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
            daoMaster = new DaoMaster(helper.getWritableDb());
        }
        return daoMaster;
    }

    /**
     * 完成对数据库的添加、删除、修改、查询的操作，仅仅是一个接口，供我们调用。
     *
     * @return
     */
    public DaoSession getDaoSession() {
        if (daoSession == null) {
            if (daoMaster == null) {
                daoMaster = getDaoMaster();
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }

    /**
     * 打开输出日志的操作，默认是关闭的
     */
    public void setDebug() {
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
    }

    /**
     * 关闭所有的操作，数据库开启的时候，使用完毕了，必须要关闭，否则会造成资源浪费。
     */
    public void closeConnection() {
        closeHelper();
        closeDaoSession();
    }

    private void closeDaoSession() {
        if (daoSession != null) {
            daoSession.clear();
            daoSession = null;
        }
    }

    private void closeHelper() {
        if (helper != null) {
            helper.close();
            helper = null;
        }
    }

}
