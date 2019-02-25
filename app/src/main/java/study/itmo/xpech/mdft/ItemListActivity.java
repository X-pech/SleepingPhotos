package study.itmo.xpech.mdft;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.fasterxml.jackson.databind.JsonNode;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import study.itmo.xpech.mdft.model.App;
import study.itmo.xpech.mdft.model.DBHelper;
import study.itmo.xpech.mdft.model.Pic;
import study.itmo.xpech.mdft.util.ExtraValues;


public class ItemListActivity extends AppCompatActivity {

    private boolean mTwoPane;
    RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    EditText search;
    Pic[] savedList;
    Button searchButton;
    Button favButton;
    DBHelper helper;
    CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        compositeDisposable = new CompositeDisposable();
        helper = App.getDB();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mTwoPane = true;
        }

        search = findViewById(R.id.search_text);
        searchButton = findViewById(R.id.search_button);
        favButton = findViewById(R.id.fav_button);

        recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView(recyclerView);
        Log.d("MEME", "CREATING");

        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupRecyclerView(recyclerView);
                Log.d("MEME", "FAV OPRESSED");
                compositeDisposable.add(helper.getData().subscribe(new Consumer<Pic[]>() {
                    @Override
                    public void accept(Pic[] pics) throws Exception {
                        adapter.setElement(pics);
                    }
                }));
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupRecyclerView(recyclerView);
                compositeDisposable.add(App.getApi().getData(search.getText().toString(), "json", "1")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<JsonNode>() {
                            @Override
                            public void accept(JsonNode res) throws Exception {
                                int constant_size = 20;
                                Log.d("MEME", "STARTED ON RESPONSE");
                                savedList = new Pic[constant_size];
                                JsonNode pics = (res.path("items"));
                                if (pics == null)
                                    return;
                                try {
                                    for (int i = 0; i < pics.size(); i++) {
                                        savedList[i] = new Pic(i + 1, pics.get(i));
                                    }
                                    Log.d("MEME", "LOADED LIST");
                                } catch (Exception e) {
                                    Log.d("MEME", "SHIT HAPPENDED");
                                    e.printStackTrace();
                                }
                                setupRecyclerView(recyclerView);
                                adapter.setElement(savedList);
                            }
                        }));
            }
        });

        //DescriptionLoader.load(this, getString(R.string.query_url));
        //bindService(new Intent(this, DescriptionLoader.class), serviceConnection, 0);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (savedList != null) {
            Log.d("MEME", "Saved state");
            outState.putParcelableArray(ExtraValues.DATA_KEY_ARRAY.toString(), savedList);
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d("MEME", "RESTORING STATE");
        if (savedInstanceState != null) {
            Log.d("MEME", "SAVED STATE IS NOT NOOL");
            savedList = (Pic[]) savedInstanceState.getParcelableArray(ExtraValues.DATA_KEY_ARRAY.toString());
            if (savedList != null) {
                Log.d("MEME", "YES ITS NOT EMPTY");
                adapter.setElement(savedList);
            }
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        adapter = new RecyclerViewAdapter(this, mTwoPane);
        recyclerView.setAdapter(adapter);
    }

    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

}
