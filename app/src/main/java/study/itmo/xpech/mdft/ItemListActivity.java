package study.itmo.xpech.mdft;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;


public class ItemListActivity extends AppCompatActivity {

    private boolean mTwoPane;
    RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private DescriptionLoader.MBinder binder;



    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ItemListActivity.this.binder = (DescriptionLoader.MBinder) service;
            binder.setCallback(p -> adapter.setElement(p));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mTwoPane = true;
        }


        recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView(recyclerView);
        DescriptionLoader.load(this, getString(R.string.query_url));
        bindService(new Intent(this, DescriptionLoader.class), serviceConnection, 0);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        adapter = new RecyclerViewAdapter(this, mTwoPane);
        recyclerView.setAdapter(adapter);
    }

    public void onDestroy() {
        unbindService(serviceConnection);
        super.onDestroy();
    }

}
