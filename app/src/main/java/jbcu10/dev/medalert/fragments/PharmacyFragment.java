package jbcu10.dev.medalert.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
 import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

import com.etsy.android.grid.StaggeredGridView;

import java.util.List;

 import butterknife.ButterKnife;
import jbcu10.dev.medalert.R;
import jbcu10.dev.medalert.activity.HomeActivity;
import jbcu10.dev.medalert.adapter.PharmacyAdapter;
 import jbcu10.dev.medalert.db.StoreRepository;
 import jbcu10.dev.medalert.model.Store;

/**
 * Created by dev on 10/12/17.
 */

public class PharmacyFragment extends ListFragment implements AbsListView.OnScrollListener,
        AbsListView.OnItemClickListener, OnItemLongClickListener {
    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final String LOADING_PLOTS = "Loading First Aid...";
    private static final String ERROR = "Error:";
    public StoreRepository pharmacyRepository;
    ProgressDialog pDialog;

    View rootView;
    private StaggeredGridView mGridView;
    private boolean mHasRequestedMore;
    private PharmacyAdapter mAdapter;

    public PharmacyFragment() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_firstaid, null, false);
        ButterKnife.bind(this, rootView);
        pharmacyRepository = new StoreRepository(getActivity());
        pDialog = new ProgressDialog(getActivity());

        getActivity().setTitle("Drug Stores");
        List<Store> stores = pharmacyRepository.getAll();
        initializeGridView();
        if (stores != null) {
            onLoadMoreItems(stores);
        }
        return rootView;

    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        return false;
    }


    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public void initializeGridView() {
        mAdapter = new PharmacyAdapter(getActivity(), R.id.txt_name, R.id.imageView);
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                setListAdapter(mAdapter);
            }

        });
    }

    private void onLoadMoreItems(List<Store> stores) {
        for (Store data : stores) {
            mAdapter.add(data);
        }
        mAdapter.notifyDataSetChanged();
        mHasRequestedMore = false;
        hideDialog();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getListView().setOnItemClickListener(this);
    }

}
