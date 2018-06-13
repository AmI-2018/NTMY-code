package io.ami2018.ntmy.recyclerviews;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import io.ami2018.ntmy.R;
import io.ami2018.ntmy.model.User;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private ArrayList<User> list;
    private UserClickListener listener;

    public UserAdapter(UserClickListener listener) {
        this.list = new ArrayList<>();
        this.listener = listener;
    }

    public void addElement(User user) {
        if (!this.contains(user.getUserId())) {
            this.list.add(user);
            this.notifyDataSetChanged();
        }
    }

    public void clear() {
        this.list.clear();
        this.notifyDataSetChanged();
    }

    private boolean contains(Integer userId) {
        for (User u : this.list) {
            if (u.getUserId().intValue() == userId.intValue()) return true;
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_user_photo, parent, false);
        return new UserViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int position) {
        User user = list.get(position);
        holder.setUser(user);
        holder.mImage.setImageBitmap(user.getPhoto());
    }

    protected static class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CircleImageView mImage;

        private User mUser;

        private UserClickListener mListener;

        private UserViewHolder(View itemView, UserClickListener listener) {
            super(itemView);
            this.mImage = itemView.findViewById(R.id.item_user_photo_civ);
            this.mListener = listener;
            itemView.setOnClickListener(this);
        }

        private void setUser(User user) {
            this.mUser = user;
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view, mUser);
        }
    }
}
