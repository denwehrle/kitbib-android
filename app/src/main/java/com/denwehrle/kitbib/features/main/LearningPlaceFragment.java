package com.denwehrle.kitbib.features.main;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.denwehrle.kitbib.BR;
import com.denwehrle.kitbib.R;
import com.denwehrle.kitbib.data.provider.Contract;
import com.denwehrle.kitbib.data.provider.Contract.LearningPlaceEntry;
import com.denwehrle.kitbib.data.provider.Contract.LearningPlaceOccupationEntry;
import com.denwehrle.kitbib.data.remote.NetworkTasks;
import com.denwehrle.kitbib.data.sync.SyncUtils;
import com.denwehrle.kitbib.features.common.CursorRecyclerAdapter;

/**
 * @author Dennis Wehrle
 */
public class LearningPlaceFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener {

    private CursorRecyclerAdapter recyclerAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View mCoordinatorLayoutView;

    public static LearningPlaceFragment newInstance() {
        return new LearningPlaceFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_learning_place, container, false);

        mCoordinatorLayoutView = getActivity().findViewById(R.id.snackbar);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        initRecyclerView(recyclerView);

        setupSwipeRefresh(rootView);

        return rootView;
    }

    private void setupSwipeRefresh(View rootView) {

        // ref to swiperefreshlayout
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);

        int color;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            color = getActivity().getColor(R.color.colorPrimary);
        } else {
            color = getActivity().getResources().getColor(R.color.colorPrimary);
        }
        swipeRefreshLayout.setColorSchemeColors(color);

        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void initRecyclerView(RecyclerView recyclerView) {
        recyclerAdapter = new CursorRecyclerAdapter(
                R.layout.item_bib_learning_place,
                BR.cursor, R.string.no_data
        );

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerAdapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String sortOrder = LearningPlaceEntry.COLUMN_INDEX + " ASC";

        return new CursorLoader(getActivity(),
                Uri.withAppendedPath(LearningPlaceOccupationEntry.CONTENT_URI, Contract.PATH_LEARNING_PLACE),
                null,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        recyclerAdapter.swapCursor(data);
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        recyclerAdapter.swapCursor(null);
    }

    @Override
    public void onRefresh() {
        if (NetworkTasks.isNetworkAvailable(getContext())) {
            SyncUtils.triggerRefresh(MainActivity.mAccount, 3);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            Snackbar.make(mCoordinatorLayoutView, "Keine Verbindung", Snackbar.LENGTH_LONG)
                    .setAction("Wiederholen", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onRefresh();
                        }
                    })
                    .setActionTextColor(getResources().getColor(R.color.colorPrimary))
                    .show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}