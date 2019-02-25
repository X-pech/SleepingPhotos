package study.itmo.xpech.mdft.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DBHelper extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "XpechFavsTable";
    private static final String DESCRIPTION = "description";
    private static final String SRC_URL = "srcUrl";
    private static final int DESCRIPTION_ID = 1;
    private static final int SRC_URL_ID = 2;
    private static SQLiteDatabase db;
    private static DBHelper helper;

    private DBHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    static public DBHelper getInstance(Context context) {
        if (helper == null) {
            helper = new DBHelper(context);
            db = helper.getWritableDatabase();
        }
        return helper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (id integer primary key autoincrement, " +
                DESCRIPTION + " text, " +
                SRC_URL + " text);");
    }

    private String getDescription(Cursor cursor) {
        return cursor.getString(DESCRIPTION_ID);
    }

    private String getSrcUrl(Cursor cursor) {
        return cursor.getString(SRC_URL_ID);
    }

    private Cursor getAllData() {
        return db.rawQuery("select * from " + TABLE_NAME, null);
    }

    private int getId(String descr, String url) {
        Cursor cr = getAllData();
        int ans = -1;
        while (cr.moveToNext()) {
            if (getDescription(cr).equals(descr) && getSrcUrl(cr).equals(url)) {
                ans = cr.getShort(0);
                break;
            }
        }
        cr.close();
        return ans;
    }

    private boolean checkCommon(final String descr, final String url) {
        return getId(descr, url) != -1;
    }

    public Observable<Boolean> check(final String descr, final String url) {
        Callable<Boolean> f = () -> {
            return checkCommon(descr, url);
        };
        return Observable.fromCallable(f).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Pic[]> getData() {
        Callable<Pic[]> f = () -> {
            Cursor cr = getAllData();
            Pic[] ans = new Pic[cr.getCount()];
            int ind = 0;
            while (cr.moveToNext()) {
                ans[ind] = new Pic(ind, getDescription(cr), getSrcUrl(cr));
                ind++;
            }
            cr.close();
            return ans;
        };
        return Observable.fromCallable(f).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Boolean> delete(final String descr, final String url) {
        Callable<Boolean> f = () -> {
            if (!checkCommon(descr, url)) {
                return false;
            }

            int ind = getId(descr, url);
            if (ind != -1) {
                db.execSQL("delete from " + TABLE_NAME + " where id = " + Integer.toString(ind) + ";");
            }
            return true;
        };
        return Observable.fromCallable(f).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Boolean> add(final String descr, final String url) {
        Callable<Boolean> f = () -> {
            if (!checkCommon(descr, url)) {
                db.close();
                db = this.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put(DESCRIPTION, descr);
                cv.put(SRC_URL, url);
                long res = db.insert(TABLE_NAME, null, cv);
                return true;
            }
            return false;
        };
        return Observable.fromCallable(f).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
