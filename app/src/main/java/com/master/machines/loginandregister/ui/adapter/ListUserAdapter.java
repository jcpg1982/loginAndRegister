package com.master.machines.loginandregister.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.master.machines.loginandregister.R;
import com.master.machines.loginandregister.model.User;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListUserAdapter extends RecyclerView.Adapter<ListUserAdapter.loadViewHolder> {

    private static final String TAG = ListUserAdapter.class.getSimpleName();

    private ArrayList<User> mDataList;
    private Context mContext;

    private OnItemClickListener mListener;

    public ListUserAdapter(Context context) {
        this.mContext = context;
        this.mDataList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ListUserAdapter.loadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_user, parent, false);
        return new loadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull loadViewHolder holder, int position) {
        final User item = mDataList.get(position);
        holder.bind(item);
    }

    @Override
    public long getItemId(int position) {
        return mDataList.get(position).getId();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (mDataList != null && mDataList.size() > 0) {
            return mDataList.size();
        } else {
            return 0;
        }
    }

    public void setData(@NonNull ArrayList<User> list) {
        mDataList.clear();
        for (User item : list) {
            mDataList.add(item);
            notifyDataSetChanged();
        }
    }

    public void setOnListener(OnItemClickListener listener) {
        mListener = listener;
        notifyDataSetChanged();
    }

    public class loadViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.container_item)
        View mContainerItem;
        @BindView(R.id.image)
        ImageView mImage;
        @BindView(R.id.text_name)
        TextView mTextName;
        @BindView(R.id.text_last_name)
        TextView mTextLastName;
        @BindView(R.id.text_address)
        TextView mTextAddress;

        private User user;

        public loadViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContainerItem.setOnClickListener(this);
        }

        public void bind(User item) {
            user = item;
            if (user.getPhoto() != null) {
                File file = new File(user.getPhoto());
                Glide.with(mContext)
                        .load(file)
                        .placeholder(mContext.getResources().getDrawable(R.mipmap.ic_launcher))
                        .error(mContext.getResources().getDrawable(R.mipmap.ic_launcher))
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .into(mImage);
            }

            if (!TextUtils.isEmpty(user.getName())) {
                mTextName.setText(user.getName());
            } else {
                mTextName.setText("");
            }

            if (!TextUtils.isEmpty(user.getLastName())) {
                mTextLastName.setText(user.getLastName());
            } else {
                mTextLastName.setText("");
            }

            if (!TextUtils.isEmpty(user.getAddress())) {
                mTextAddress.setText(user.getAddress());
            } else {
                mTextAddress.setText("");
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.container_item:
                    if (mListener != null) {
                        mListener.onItemClick(v, user, getAdapterPosition(), false);
                    }
                    break;
            }
        }
    }

    public interface OnItemClickListener {

        boolean onItemClick(View view, final User user, int position, boolean longPress);
    }
}