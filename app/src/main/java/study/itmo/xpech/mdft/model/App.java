package study.itmo.xpech.mdft.model;

import android.app.Application;
import android.util.Log;

import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class App extends Application {
    // DBHELP

    private static FlickrInterface api;
    public static DBHelper helper;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("SOS", "onCreate App");
        helper = DBHelper.getInstance(this);
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.flickr.com")
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory
                        .createWithScheduler(Schedulers.io())).build();
        api = retrofit.create(FlickrInterface.class);
    }

    @Override
    public void onTerminate() {
        helper.close();
        super.onTerminate();
    }

    public static FlickrInterface getApi() {
        return api;
    }

    public static DBHelper getDB() {return helper;}

}
