package study.itmo.xpech.mdft;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import study.itmo.xpech.mdft.model.DBHelper;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class DBTest {

    private DBHelper helper;
    private CompositeDisposable compositeDisposable;
    private Consumer<Boolean> asserterTrue, asserterFalse, asserterEq;
    private final int magicNumber = 7;

    @Before
    public void setUp() {
        getTargetContext().deleteDatabase(DBHelper.TABLE_NAME);
        helper = DBHelper.getInstance(getTargetContext());
        compositeDisposable = new CompositeDisposable();
        asserterFalse = new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean)
                    Log.d("MEME", "SLISHKOM TRU");
                assertFalse(aBoolean);
            }
        };

        asserterTrue = new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                assertTrue(aBoolean);
            }
        };

    }

    @After
    public void shutDown() {
        compositeDisposable.clear();
        Log.d("MEME", "OP PODREZAL");
        helper.close();
    }

    private void check(String description, String srcUrl, boolean result) throws InterruptedException {
        compositeDisposable.add(helper.check(description, srcUrl).subscribe(result ? asserterTrue : asserterFalse));
    }

    private void add(String description, String srcUrl, boolean result) throws InterruptedException {
        compositeDisposable.add(helper.add(description, srcUrl).subscribe(result ? asserterTrue : asserterFalse));
    }

    private void del(String description, String srcUrl, boolean result) throws InterruptedException {
        compositeDisposable.add(helper.delete(description, srcUrl).subscribe(result ? asserterTrue : asserterFalse));
    }

    public void simpleTest(String description, String srcUrl) throws InterruptedException {
        check(srcUrl, description, false);
        add(description, srcUrl, true);
        add(description, srcUrl, false);
        del(description, srcUrl, true);
        del(description, srcUrl, false);

    }

    private String ts(final int i) {
        return String.valueOf(i);
    }

    @Test
    public void checkOnce() throws InterruptedException {
        simpleTest("TEST", "ONCE");
    }

}
