package study.itmo.xpech.mdft;

import android.support.test.runner.AndroidJUnit4;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.picasso.Picasso;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import study.itmo.xpech.mdft.model.FlickrInterface;
import study.itmo.xpech.mdft.model.Pic;

@RunWith(AndroidJUnit4.class)
public class RetrofitTest {

    private static final String QUERY = "sleep";
    private static final String BASE_URL = "https://api.flickr.com";
    private CompositeDisposable compositeDisposable;

    @Before
    public void setUp() {
        compositeDisposable = new CompositeDisposable();
    }

    @Test
    public void testRetrofit() {
        FlickrInterface api = (new Retrofit.Builder().baseUrl("https://api.flickr.com")
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory
                        .createWithScheduler(Schedulers.io())).build()).create(FlickrInterface.class);

        compositeDisposable.add(api.getData(QUERY, "json", "1").subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<JsonNode>() {
                    @Override
                    public void accept(JsonNode jsonNode) throws Exception {
                        assertNotNull(jsonNode);
                        JsonNode pics = jsonNode.path("items");
                        assertNotNull(pics);
                        for (int i = 0; i < 20; i++) {
                            assertNotNull(pics.get(i));
                            assertNotNull(pics.get(i).get("title"));
                            assertNotNull(pics.get(i).get("media").get("m"));
                        }
                    }
                }));
    }

    @After
    public void shutDown() {
        compositeDisposable.clear();
    }

}
