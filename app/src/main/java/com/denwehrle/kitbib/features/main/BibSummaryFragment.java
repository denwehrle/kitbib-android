package com.denwehrle.kitbib.features.main;

import android.database.Cursor;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.denwehrle.kitbib.BR;
import com.denwehrle.kitbib.R;
import com.denwehrle.kitbib.data.provider.Contract.BibSummaryEntry;
import com.denwehrle.kitbib.data.provider.Provider;
import com.denwehrle.kitbib.data.remote.NetworkTasks;
import com.denwehrle.kitbib.data.remote.interfaces.AsyncStatus;
import com.denwehrle.kitbib.data.sync.SyncUtils;
import com.denwehrle.kitbib.features.common.CursorRecyclerAdapter;
import com.denwehrle.kitbib.features.common.DataBindingRecyclerAdapter;

/**
 * @author Dennis Wehrle
 */
public class BibSummaryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener {

    private CursorRecyclerAdapter recyclerAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View mCoordinatorLayoutView;

    public static BibSummaryFragment newInstance() {
        return new BibSummaryFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bib_summary, container, false);

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

    private void initRecyclerView(final RecyclerView recyclerView) {
        recyclerAdapter = new CursorRecyclerAdapter(
                R.layout.item_bib_summary,
                BR.cursor, R.string.no_summary_data
        );

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerAdapter);

        // add listener
        recyclerAdapter.setOnItemClickListener(new DataBindingRecyclerAdapter.OnItemClickListener<Cursor>() {
            @Override
            public void onItemClick(int position, Cursor data, View view) {
                view.showContextMenu();
            }
        });
        recyclerAdapter.setOnContextMenuItemClickListener(
                new DataBindingRecyclerAdapter.OnContextMenuItemClickListener() {
                    @Override
                    public void onContextMenuItemClick(int position, View view, MenuItem item) {
                        String index = "";
                        if (item.getTitle().equals("Verlängern")) {

                            Cursor cursor = recyclerAdapter.getData(position);

                            if (cursor != null && cursor.getCount() > position) {
                                cursor.moveToPosition(position);
                                index = cursor.getString(cursor.getColumnIndex(BibSummaryEntry.COLUMN_INDEX));
                                //cursor.close();
                            }

                            if (swipeRefreshLayout != null)
                                swipeRefreshLayout.setRefreshing(true);

                            new NetworkTasks.SingleExtension(new AsyncStatus() {
                                @Override
                                public void result(boolean result) {
                                    if (!result) {
                                        Snackbar.make(mCoordinatorLayoutView, getActivity().getString(R.string.extention_failed), Snackbar.LENGTH_LONG)                                                .show();
                                        if (swipeRefreshLayout != null)
                                            swipeRefreshLayout.setRefreshing(false);
                                    } else {
                                        Snackbar.make(mCoordinatorLayoutView, getActivity().getString(R.string.extention_success), Snackbar.LENGTH_LONG)                                                .show();
                                        if (swipeRefreshLayout != null)
                                            swipeRefreshLayout.setRefreshing(false);
                                        onRefresh();
                                    }

                                }
                            }, getContext(), MainActivity.mAccount).execute(index);
                        } else
                            Toast.makeText(getActivity(), "Coming soon ;)", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String sortOrder = BibSummaryEntry.COLUMN_DUE_DATE + " ASC";

        return new CursorLoader(getActivity(),
                Provider.BIB_SUMMARY_URI,
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
            SyncUtils.triggerRefresh(MainActivity.mAccount, 1);
            if (swipeRefreshLayout != null)
                swipeRefreshLayout.setRefreshing(true);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            Snackbar.make(mCoordinatorLayoutView, getActivity().getString(R.string.connection_failed), Snackbar.LENGTH_LONG)
                    .setAction(getActivity().getString(R.string.connection_retry), new View.OnClickListener() {
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