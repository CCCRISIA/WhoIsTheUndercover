package com.lorytech.WhoIsTheUndercover;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by HCF on 2017/8/23.
 */

public class ListAdapterUtil {

    /**
     * 设置ListView适配高度
     *
     * @param listView
     */
    public static void MeasureHeight(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int totalHeight = 0;
            for (int k = 0; k < listAdapter.getCount(); k++) {
                View listItem = listAdapter.getView(k, null, listView);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight + (listView.getDividerHeight() * (listView.getCount() - 1));
            listView.setLayoutParams(params);
        }
    }

}
