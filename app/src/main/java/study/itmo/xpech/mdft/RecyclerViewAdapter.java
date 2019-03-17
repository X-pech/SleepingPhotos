package study.itmo.xpech.mdft;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import study.itmo.xpech.mdft.pic.PicturesContent;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {


    private final ItemListActivity mParentActivity;
    private final ArrayList<PicturesContent.Pic> mValues = new ArrayList<>();
    private final boolean mTwoPane;
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            PicturesContent.Pic item = (PicturesContent.Pic) view.getTag();
            if (mTwoPane) {
                Bundle arguments = new Bundle();
                arguments.putString(ExtraValues.EXTRA_URL.toString(), item.srcUrl);
                arguments.putString(ExtraValues.EXTRA_DESC.toString(), item.description);
                ItemDetailFragment fragment = new ItemDetailFragment();
                fragment.setArguments(arguments);
                mParentActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.item_detail_container, fragment)
                        .commit();
            } else {
                Context context = view.getContext();
                Intent intent = new Intent(context, ItemDetailActivity.class);
                intent.putExtra(ExtraValues.EXTRA_URL.toString(), item.srcUrl);
                intent.putExtra(ExtraValues.EXTRA_DESC.toString(), item.description);

                context.startActivity(intent);
            }
        }
    };

    RecyclerViewAdapter(ItemListActivity parent, boolean twoPane) {
        mParentActivity = parent;
        mTwoPane = twoPane;
    }

    private static String getFullPls(String pureUrl) {
        String res = pureUrl.substring(0, pureUrl.length() - 5);
        return res + "c.jpg";
    }

    public void setElement(JSONObject data) {
        if (data == null)
            return;
        try {
            JSONArray pics = data.getJSONArray("items");
            JSONObject pic;
            String description, srcUrl;
            for (int i = 0; i < pics.length(); i++) {
                pic = pics.getJSONObject(i);
                description = pic.getString("title");
                srcUrl = getFullPls(pic.getJSONObject("media").getString("m"));
                mValues.add(PicturesContent.createPicItem(i + 1, description, srcUrl));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).description);

        holder.itemView.setTag(mValues.get(position));
        holder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mIdView;
        final TextView mContentView;

        ViewHolder(View view) {
            super(view);
            mIdView = view.findViewById(R.id.id_text);
            mContentView = view.findViewById(R.id.content);
        }
    }
}