package com.master.machines.loginandregister.ui.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.master.machines.loginandregister.R;
import com.master.machines.loginandregister.db.DAO;
import com.master.machines.loginandregister.db.DataBaseUser;
import com.master.machines.loginandregister.model.User;
import com.master.machines.loginandregister.ui.adapter.ListUserAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListUserActivity extends AppCompatActivity implements View.OnClickListener {

    public static String TAG = ListUserActivity.class.getSimpleName();

    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout mSwipeRefresh;
    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    @BindView(R.id.fab_add)
    FloatingActionButton mFabAdd;

    @BindView(android.R.id.progress)
    ViewGroup mProgress;
    @BindView(R.id.text_empty)
    TextView mTextEmpty;
    @BindView(R.id.container_try_again)
    ViewGroup mContainerTryAgain;

    private ArrayList<User> mDataList;
    private ListUserAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private RecyclerView.OnScrollListener recyclerViewOnScrollListener =
            new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
                    if (firstVisibleItemPosition > 0)
                        mFabAdd.setVisibility(View.GONE);
                    else
                        mFabAdd.setVisibility(View.VISIBLE);
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user);
        ButterKnife.bind(this);

        init();
        initEvents();
        setupSwipeRefresh();
        setupRecyclerView();

        loadData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add:
                Intent intent = new Intent(this, AddUserActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    private void init() {
        mDataList = new ArrayList<>();
    }

    private void initEvents() {
        mFabAdd.setOnClickListener(this);
    }

    private void setupSwipeRefresh() {
        mSwipeRefresh.setProgressBackgroundColorSchemeColor(
                getResources().getColor(R.color.light));
        mSwipeRefresh.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mSwipeRefresh.setRefreshing(false);
                        loadData();
                    }
                });
    }

    private void setupRecyclerView() {
        mAdapter = new ListUserAdapter(this);
        mAdapter.setOnListener(new ListUserAdapter.OnItemClickListener() {
            @Override
            public boolean onItemClick(View view, User user, int position, boolean longPress) {
                Intent intent = new Intent(ListUserActivity.this, UpdateUserActivity.class);
                intent.putExtra("User", user);
                startActivity(intent);
                finish();
                return false;
            }
        });

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecycler.setHasFixedSize(true);
        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.setAdapter(mAdapter);
        mRecycler.addOnScrollListener(recyclerViewOnScrollListener);
    }

    private void setListShown(boolean shown) {
        final View v = getWindow().getDecorView().getRootView();
        if (v != null) {
            mTextEmpty.setText("No tiene contactos registrados");
            mTextEmpty.setTextColor(getResources().getColor(R.color.colorAccent));
            mRecycler.setVisibility(shown ? View.VISIBLE : View.GONE);
            mProgress.setVisibility(shown ? View.GONE : View.VISIBLE);
            mTextEmpty.setVisibility(shown && mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }
    }

    private void loadData() {
        setListShown(false);
        mDataList.clear();
        DataBaseUser DBU = new DataBaseUser(this, "user", null, 1);
        Cursor c = DAO.getUser(DBU);
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    User user = new User();
                    user.setId(c.getInt(0));
                    user.setPhoto(c.getString(1));
                    user.setName(c.getString(2));
                    user.setLastName(c.getString(3));
                    user.setAddress(c.getString(4));
                    Log.e(TAG, "User: " + user);
                    mDataList.add(user);
                } while (c.moveToNext());
            }
        }
        Log.e(TAG, "ListUser: " + mDataList);
        if (mDataList != null && mDataList.size() > 0)
            mAdapter.setData(mDataList);
        setListShown(true);

    }
}
