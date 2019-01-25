package study.itmo.xpech.mdft;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class PictureLoader extends IntentService {

    private Bitmap returnCache;
    private final Handler main = new Handler(Looper.getMainLooper());
    private OnLoad callback;

    public PictureLoader() {
        super("DescriptionLoader");
    }

    public void onCreate() {
        super.onCreate();
    }

    public static void load(Context context, String srcUrl, String cachePath) {
        Intent intent = new Intent(context, PictureLoader.class);
        intent.putExtra(ExtraValues.EXTRA_URL.toString(), srcUrl);
        intent.putExtra(ExtraValues.EXTRA_CACHEPATH.toString(), cachePath);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String srcUrl = intent.getStringExtra(ExtraValues.EXTRA_URL.toString());
        String cachePath = intent.getStringExtra(ExtraValues.EXTRA_CACHEPATH.toString());
        getImage(srcUrl, cachePath);
    }

    public void getImage(String srcUrl, String cachePath) {
        try {
            File file = new File(cachePath);

            if (!file.exists()) {

                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }

                URL url = new URL(srcUrl);
                InputStream is = url.openStream();
                OutputStream cachedFile = new BufferedOutputStream(new FileOutputStream(cachePath));
                for (int i = is.read(); i != -1; i = is.read()) {
                    cachedFile.write(i);
                }
                cachedFile.close();
                is.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bitmap res = BitmapFactory.decodeFile(cachePath);
        main.post(() -> deliver(res));
    }

    public static class MBinder extends Binder {
        private final PictureLoader pictureLoader;

        public MBinder(PictureLoader pictureLoader) {
            this.pictureLoader = pictureLoader;
        }

        public void setCallback(final OnLoad callback) {
            new Handler(Looper.getMainLooper()).post(() -> {
                pictureLoader.callback = callback;
                pictureLoader.callback.onLoad(pictureLoader.returnCache);
            });
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MBinder(this);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        callback = null;
        return super.onUnbind(intent);
    }

    public void deliver(Bitmap data) {
        if (callback != null) callback.onLoad(data);
        else
            returnCache = data;
    }

    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    public interface OnLoad {
        void onLoad(Bitmap data);
    }

}
