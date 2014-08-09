package com.ghostmc.android.carousel;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.ghostmc.android.MainActivity;
import com.ghostmc.android.R;
import com.ghostmc.android.ui.TagSelector;

import java.util.List;

public class CarouselPagerAdapter extends FragmentPagerAdapter implements
        ViewPager.OnPageChangeListener {

    private CarouselLayout cur = null;
    private CarouselLayout next = null;
    private MainActivity context;
    private FragmentManager fm;
    private float scale;
    private int currentPosition = -1;

    public final static int LOOPS = 1000;
    public final static float BIG_SCALE = 1.0f;
    public final static float SMALL_SCALE = 0.7f;
    public final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;

    private List<TagSelector.Item> tags;

    public CarouselPagerAdapter(MainActivity context, FragmentManager fm, List<TagSelector.Item> tags) {
        super(fm);
        this.fm = fm;
        this.context = context;
        this.tags = tags;
    }

    public int getFirstPage() {
        return getPages() * LOOPS / 2;
    }

    public int getPages() {
        return tags.size();
    }

    public TagSelector.Item getCurrentTag() {
        return tags.get(getCurrentPosition());
    }

    public int getCurrentPosition() {
        return currentPosition % getPages();
    }

    @Override
    public Fragment getItem(int position)
    {
        if (position == getFirstPage()) {
            scale = BIG_SCALE;
        } else {
            scale = SMALL_SCALE;
        }

        position = position % getPages();
        final TagSelector.Item tag = tags.get(position);
        return CarouselFragment.newInstance(context, tag.id, tag.name, tag.logo, scale);
    }

    @Override
    public int getCount()
    {
        return getPages() * LOOPS;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels)
    {
        if (positionOffset >= 0f && positionOffset <= 1f)
        {
            cur = getRootView(position);
            next = getRootView(position +1);

            cur.setScaleBoth(BIG_SCALE
                    - DIFF_SCALE * positionOffset);
            next.setScaleBoth(SMALL_SCALE
                    + DIFF_SCALE * positionOffset);
        }
    }

    @Override
    public void onPageSelected(int position) {
        this.currentPosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    private CarouselLayout getRootView(int position)
    {
        return (CarouselLayout)
                fm.findFragmentByTag(this.getFragmentTag(position))
                        .getView().findViewById(R.id.root);
    }

    private String getFragmentTag(int position)
    {
        return "android:switcher:" + context.pager.getId() + ":" + position;
    }
}