package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.User;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by veviego on 7/5/17.
 */

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.ViewHolder> {

    TwitterClient client;
    private List<User> mUsers;
    Context context;

    // pass in the users array in the constructor
    public PeopleAdapter(List<User> users) {
        mUsers = users;
    }

    // for each row, inflate the layout and cache references into ViewHolder
    @Override
    public PeopleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        client = TwitterApplication.getRestClient();
        LayoutInflater inflater = LayoutInflater.from(context);
        View userView = inflater.inflate(R.layout.item_user, parent, false);
        ViewHolder viewHolder = new ViewHolder(userView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get data according to position
        final User user = mUsers.get(position);

        // populate the views according to this data
        final String userName = "@" + user.screenName;

        holder.tvUserName.setText(userName);
        holder.tvName.setText(user.name);
        holder.tvTagline.setText(user.tagline);

        // Load profile image
        // load profile image with glide
        Glide.with(context)
                .load(user.profileImageUrl)
                .bitmapTransform(new RoundedCornersTransformation(context, 15, 0))
                .into(holder.ivProfileImage);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    // create ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivProfileImage;
        public TextView tvUserName;
        public TextView tvName;
        public TextView tvTagline;

        public ViewHolder(final View itemView) {
            super(itemView);

            // perform findViewByID lookups
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivUserProfileImage);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserScreenName);
            tvName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvTagline = (TextView) itemView.findViewById(R.id.tvUserTagline);

        }

    }

    // Clean all elements of the recycler
    public void clear() {
        mUsers.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<User> list) {
        mUsers.addAll(list);
        notifyDataSetChanged();
    }
}
