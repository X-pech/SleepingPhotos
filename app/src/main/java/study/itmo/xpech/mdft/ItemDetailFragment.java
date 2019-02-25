package study.itmo.xpech.mdft;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import study.itmo.xpech.mdft.model.App;
import study.itmo.xpech.mdft.model.DBHelper;
import study.itmo.xpech.mdft.model.Pic;
import study.itmo.xpech.mdft.util.ExtraValues;

public class ItemDetailFragment extends Fragment {

    Pic pic;
    View rootView;
    ImageView imageView;
    TextView textView;
    Context context;
    Boolean favourite;
    DBHelper helper = null;
    Button favButtonDetail;
    CompositeDisposable compositeDisposable;
    String add, rem;


    public ItemDetailFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        compositeDisposable = new CompositeDisposable();
        if (getArguments().containsKey(ExtraValues.DATA_KEY_DETAIL.toString())) {
            pic = getArguments().getParcelable(ExtraValues.DATA_KEY_DETAIL.toString());
        }
        add = getString(R.string.FavAdd);
        rem = getString(R.string.FavRemoved);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.item_detail, container, false);

        helper = App.getDB();
        favButtonDetail = rootView.findViewById(R.id.fav_button_detail);
        favButtonDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favourite) {
                    Log.d("MEME", "REMOVED");
                    favButtonDetail.setText(add);
                    compositeDisposable.add(helper.delete(pic.description, pic.srcUrl).subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            // Nothing)
                        }
                    }));
                    favourite = false;
                } else {
                    Log.d("MEME", "added");
                    favButtonDetail.setText(rem);
                    compositeDisposable.add(helper.add(pic.description, pic.srcUrl).subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            //Nothing)
                        }
                    }));
                    favourite = true;
                }
            }
        });

        compositeDisposable.add(helper.check(pic.description, pic.srcUrl).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                favourite = aBoolean;
                if (!favourite) {
                    favButtonDetail.setText(add);
                } else {
                    favButtonDetail.setText(rem);
                }
            }
        }));
        imageView = rootView.findViewById(R.id.image_view);
        textView = rootView.findViewById(R.id.image_description);
        Picasso.get().load(pic.srcUrl).into(imageView);
        imageView.setContentDescription(pic.description);
        textView.setText(pic.description);
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
