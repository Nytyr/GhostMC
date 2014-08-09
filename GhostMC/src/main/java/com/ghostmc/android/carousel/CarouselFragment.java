package com.ghostmc.android.carousel;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ghostmc.android.MainActivity;
import com.ghostmc.android.R;
import com.ghostmc.android.ui.TagLogos;

public class CarouselFragment extends Fragment {

    public static Fragment newInstance(MainActivity context,
                                       long id,
                                       String name,
                                       String icon,
                                       float scale
    ) {
        Bundle b = new Bundle();
        b.putLong("id", id);
        b.putString("name", name);
        b.putString("icon", icon);
        b.putFloat("scale", scale);
        return Fragment.instantiate(context, CarouselFragment.class.getName(), b);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        LinearLayout l = (LinearLayout)
                inflater.inflate(R.layout.mf, container, false);

        long pos = this.getArguments().getLong("id");
        String name = this.getArguments().getString("name");
        TextView tv = (TextView) l.findViewById(R.id.text);
        tv.setText(name);

        final ImageView content = (ImageView) l.findViewById(R.id.content);
        content.setImageResource(TagLogos.getDrawable(this.getArguments().getString("logo")));

        CarouselLayout root = (CarouselLayout) l.findViewById(R.id.root);
        float scale = this.getArguments().getFloat("scale");
        root.setScaleBoth(scale);

        return l;
    }
}