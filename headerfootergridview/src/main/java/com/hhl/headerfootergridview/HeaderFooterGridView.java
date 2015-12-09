package com.hhl.headerfootergridview;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;

import java.util.ArrayList;

/**
 * 自定义带Header和Footer的GridView
 * {@link HeaderGridView}
 * Created by HanHailong on 15/11/29.
 */
public class HeaderFooterGridView extends GridView {

    private static final String TAG = "HeaderFooterGridView";

    /**
     * A class that represents a fixed view in a list, for example a header at the top
     * or a footer at the bottom.
     */
    private static class FixedViewInfo {
        /**
         * The view to add to the grid
         */
        public View view;
        public ViewGroup viewContainer;
        /**
         * The data backing the view. This is returned from {@link ListAdapter#getItem(int)}.
         */
        public Object data;
        /**
         * <code>true</code> if the fixed view should be selectable in the grid
         */
        public boolean isSelectable;
    }

    private ArrayList<FixedViewInfo> mHeaderViewInfos = new ArrayList<FixedViewInfo>();
    private ArrayList<FixedViewInfo> mFooterViewInfos = new ArrayList<FixedViewInfo>();

    public HeaderFooterGridView(Context context) {
        this(context, null);
    }

    public HeaderFooterGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeaderFooterGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initHeaderFooterInfos();
    }

    private void initHeaderFooterInfos() {
        setClipChildren(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ListAdapter adapter = getAdapter();
        if (adapter != null && adapter instanceof HeaderFooterGridViewAdapter) {
            ((HeaderFooterGridViewAdapter) adapter).setNumColumns(getNumColumns());
        }
    }

    /**
     * Add a fixed view to appear at the top of the grid. If addHeaderView is
     * called more than once, the views will appear in the order they were
     * added. Views added using this call can take focus if they want.
     * NOTE: Call this before calling setAdapter. This is so HeaderFooterGridView can wrap
     * the supplied cursor with one that will also account for header views.
     *
     * @param v
     * @param data
     * @param isSelectable
     */
    public void addHeaderView(View v, Object data, boolean isSelectable) {
        ListAdapter listAdapter = getAdapter();
        if (listAdapter != null && !(listAdapter instanceof HeaderFooterGridViewAdapter)) {
            throw new IllegalStateException("Cannot add header view to grid -- setAdapter has already been called");
        }

        FixedViewInfo info = new FixedViewInfo();
        FrameLayout containerFl = new FrameLayout(getContext());
        containerFl.addView(v);
        info.viewContainer = containerFl;
        info.view = v;
        info.data = data;
        info.isSelectable = isSelectable;
        mHeaderViewInfos.add(info);

        if (listAdapter != null) {
            ((HeaderFooterGridViewAdapter) listAdapter).notifyDataSetChanged();
        }
    }

    /**
     * Add a fixed view to appear at the bottom of the grid. If addFooterView is
     * called more than once, the views will appear in the order they were
     * added. Views added using this call can take focus if they want.
     * NOTE: Call this before calling setAdapter. This is so HeaderFooterGridView can wrap
     * the supplied cursor with one that will also account for header views.
     *
     * @param v
     * @param data
     * @param isSelectable
     */
    public void addFooterView(View v, Object data, boolean isSelectable) {
        ListAdapter listAdapter = getAdapter();
        if (listAdapter != null && !(listAdapter instanceof HeaderFooterGridViewAdapter)) {
            throw new IllegalStateException("Cannot add footer view to grid -- setAdapter has already been called");
        }
    }

    /**
     * add header view
     *
     * @param v
     */
    public void addHeaderView(View v) {
        addHeaderView(v, null, true);
    }

    public int getHeaderViewCount() {
        return mHeaderViewInfos.size();
    }

    public boolean removeHeaderView(View v) {
        if (mHeaderViewInfos.size() > 0) {
            //first delete header view
            boolean result = false;
            ListAdapter adapter = getAdapter();
            if (adapter != null && ((HeaderFooterGridViewAdapter) adapter).removeHeader(v)) {
                result = true;
            }
            removeFixedViewInfo(v, mHeaderViewInfos);
            return result;
        }
        return false;
    }

    private void removeFixedViewInfo(View v, ArrayList<FixedViewInfo> where) {
        int len = where.size();
        for (int i = 0; i < len; ++i) {
            FixedViewInfo info = where.get(i);
            if (info.view == v) {
                where.remove(i);
                break;
            }
        }
    }

    public void addFooterView(View v) {

    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        if (mHeaderViewInfos.size() > 0) {
            HeaderFooterGridViewAdapter hfAdapter = new HeaderFooterGridViewAdapter(mHeaderViewInfos, adapter);
            int numColumns = getNumColumns();
            if (numColumns > 1) {
                hfAdapter.setNumColumns(numColumns);
            }
            super.setAdapter(hfAdapter);
        } else {
            super.setAdapter(adapter);
        }
    }

    private class FullWidthFixedViewLayout extends FrameLayout {

        public FullWidthFixedViewLayout(Context context) {
            super(context);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int targetWidth = HeaderFooterGridView.this.getMeasuredWidth() -
                    HeaderFooterGridView.this.getPaddingLeft() - HeaderFooterGridView.this.getPaddingRight();
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(targetWidth
                    , MeasureSpec.getMode(widthMeasureSpec));
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    /**
     * ListAdapter used when a HeaderFooterGridView has header views. This ListAdapter
     * wraps another one and also keeps track of the header views and their
     * associated data objects.
     * This is intended as a base class; you will probably not need to
     * use this class directly in your own code.
     */
    private static class HeaderFooterGridViewAdapter implements WrapperListAdapter, Filterable {

        public HeaderFooterGridViewAdapter(ArrayList<FixedViewInfo> headerViewInfos, ListAdapter adapter) {

        }

        @Override
        public ListAdapter getWrappedAdapter() {
            return null;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public Filter getFilter() {
            return null;
        }

        public void setNumColumns(int numColumns) {

        }

        public void notifyDataSetChanged() {
            //TODO

        }

        public boolean removeHeader(View v) {
            return false;
        }
    }
}
