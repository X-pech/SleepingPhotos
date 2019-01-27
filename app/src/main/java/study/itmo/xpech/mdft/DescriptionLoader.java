package study.itmo.xpech.mdft;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


public class DescriptionLoader extends IntentService {

    private JSONObject returnCache;
    private final Handler main = new Handler(Looper.getMainLooper());
    private OnLoad callback;

    public DescriptionLoader() {
        super("DescriptionLoader");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static void load(Context context, String srcUrl) {
        Intent intent = new Intent(context, DescriptionLoader.class);
        intent.putExtra(ExtraValues.EXTRA_URL.toString(), srcUrl);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String srcUrl = intent.getStringExtra(ExtraValues.EXTRA_URL.toString());
        getDescriptions(srcUrl);
    }

    public void getDescriptions(String srcUrl) {

        JSONObject tempJSO = new JSONObject();
        try {
            URL url = new URL(srcUrl);
            URLConnection connection = url.openConnection();
            connection.connect();
            InputStream is = connection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is), 8);
            StringBuilder sb = new StringBuilder();
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                sb.append(line).append('\n');
            }
            String result = sb.substring(15, sb.length() - 2);
            tempJSO = new JSONObject(result);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final JSONObject finalJSO = tempJSO;
        main.post(() -> deliver(finalJSO));

    }

    public static class MBinder extends Binder {
        private final DescriptionLoader descriptionLoader;

        public MBinder(DescriptionLoader descriptionLoader) {
            this.descriptionLoader = descriptionLoader;
        }

        public void setCallback(final OnLoad callback) {
            new Handler(Looper.getMainLooper()).post(() -> {
                descriptionLoader.callback = callback;
                descriptionLoader.callback.onLoad(descriptionLoader.returnCache);
            });
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MBinder(this);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        callback  = null;
        return super.onUnbind(intent);
    }

    public void deliver(JSONObject data) {
        if (callback != null) callback.onLoad(data);
        else
            returnCache = data;
    }

    public interface OnLoad {
        void onLoad(JSONObject data);
    }

}
