package study.itmo.xpech.mdft;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemDetailFragment extends Fragment {

    private String srcUrl;
    private String description;
    private PictureLoader.MBinder binder;
    View rootView;
    ImageView imageView;
    TextView textView;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ItemDetailFragment.this.binder = (PictureLoader.MBinder) service;
            binder.setCallback(p -> setImageBitmap(p));
        }

        public void setImageBitmap(Bitmap pic) {
            imageView.setImageBitmap(pic);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ExtraValues.EXTRA_URL.toString())) {
            srcUrl = getArguments().getString(ExtraValues.EXTRA_URL.toString());
        }
        if (getArguments().containsKey(ExtraValues.EXTRA_DESC.toString())) {
            description = getArguments().getString(ExtraValues.EXTRA_DESC.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String cachePath = getContext().getCacheDir().getAbsolutePath().concat("/").concat(srcUrl);
        rootView = inflater.inflate(R.layout.item_detail, container, false);
        imageView = (ImageView) rootView.findViewById(R.id.image_view);
        textView = (TextView) rootView.findViewById(R.id.image_description);
        PictureLoader.load(rootView.getContext(), srcUrl, cachePath);
        imageView.setContentDescription(description);
        textView.setText(description);
        getContext().bindService(new Intent(getContext(), PictureLoader.class), serviceConnection, 0);
        return rootView;
    }

    @Override
    public void onDestroy() {
        getContext().unbindService(serviceConnection);
        super.onDestroy();
    }
}
