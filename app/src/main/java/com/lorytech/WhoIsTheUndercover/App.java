package com.lorytech.WhoIsTheUndercover;

import android.app.Application;

import com.lorytech.WhoIsTheUndercover.DaoMaster.DevOpenHelper;

import org.greenrobot.greendao.database.Database;


/**
 * Created by ZhangChen on 2017/12/5 15:33
 */

public class App extends Application {
    /** A flag to show how easily you can switch from standard SQLite to the encrypted SQLCipher. */
    public static final boolean ENCRYPTED = true;

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        DevOpenHelper helper = new DevOpenHelper(this, ENCRYPTED ? "words-db-encrypted" : "words-db");
        Database db = ENCRYPTED ? helper.getEncryptedWritableDb("super-secret") : helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}






