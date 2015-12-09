# HeaderFooterGridView
像ListView一样给GridView添加Header和Footer，目前只添加了Header，后续完善中....

# Installation

    compile 'com.hhl.headerfootergridview:headerfootergridview:0.1'

# Usage
    
    <com.hhl.headerfootergridview.HeaderGridView
            android:id="@+id/grid_view"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:numColumns="2"
            android:stretchMode="columnWidth"
            tools:listitem="@layout/header_view" />
            
            
     mGridView = (HeaderGridView) findViewById(R.id.grid_view);
            View headerView1 = View.inflate(this, R.layout.header_view, null);
            View headerView2 = View.inflate(this, R.layout.header_view, null);
            mGridView.addHeaderView(headerView1);
            mGridView.addHeaderView(headerView2);
    
            //setAdapter之前调用addHeaderView
            mGridView.setAdapter(mAdapter);
    
# TODO
    目前只有header，下一步添加footer...
