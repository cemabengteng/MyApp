package com.example.view.StripPagerTabLayout;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by liuj on 2016/2/22.
 * tab构造类
 */
public class TabBuilder {

    /**
     * 创建Tab
     *
     * @param pos
     * @param adapter
     * @param tabContianer
     * @return
     */
    public static Tab createTab(int pos, PagerAdapter adapter, ViewGroup tabContianer) {
        if (adapter instanceof CustomTabPagerAdapter) {
            View customView = ((CustomTabPagerAdapter) adapter).getCustomTab(pos, tabContianer);
            if (customView != null) {
                return new Tab().customView(((CustomTabPagerAdapter) adapter).getCustomTab(pos, tabContianer));
            } else {
                return createSimpleTab(pos, adapter, tabContianer);
            }
        } else {
            return createSimpleTab(pos, adapter, tabContianer);
        }
    }


    /**
     * 创建普通的TAB
     *
     * @param pos
     * @param adapter
     * @param tabContainer
     * @return
     */
    private static Tab createSimpleTab(int pos, PagerAdapter adapter, ViewGroup tabContainer) {
        if (adapter instanceof IconTitlePagerAdapter) {
            return new Tab().title((String) adapter.getPageTitle(pos)).icon(((IconTitlePagerAdapter) adapter).getIcon(pos));
        } else if (adapter instanceof IconPagerAdapter) {
            return new Tab().icon(((IconPagerAdapter) adapter).getIcon(pos));
        }
        return new Tab().title((String) adapter.getPageTitle(pos));
    }

    public static Tab createTabByDataList(int i, List<String> lists) {
        if (lists != null && lists.size() > 0) {
            return new Tab().title(lists.get(i));
        }
        return new Tab().title("");
    }
}
