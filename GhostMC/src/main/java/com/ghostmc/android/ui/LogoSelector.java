package com.ghostmc.android.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ghostmc.android.R;

import java.util.Map;

public class LogoSelector {

    public static void launch(final Activity activity, final Handler handler) {
        final Dialog dialog = new Dialog(activity, R.style.LogoDialog);
        dialog.setContentView(R.layout.dialog_logos);

        final Display display = activity.getWindowManager().getDefaultDisplay();
        final int logoWidth = display.getWidth()/5;

        final LinearLayout logosContainer = (LinearLayout) dialog.findViewById(R.id.content);
        for (final Map.Entry<String, Integer> logo : TagLogos.logos.entrySet()) {
            final ImageView imageView = new ImageView(activity);
            imageView.setPadding(20, 20, 20, 20);
            ViewGroup.LayoutParams layoutParams = new ActionBar.LayoutParams(logoWidth, logoWidth);
            imageView.setLayoutParams(layoutParams);
            imageView.setImageResource(logo.getValue());
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handler.onLogoSelected(logo.getKey());
                    dialog.dismiss();
                }
            });
            logosContainer.addView(imageView);
        }
        dialog.show();
    }

    public interface Handler {
        void onLogoSelected(String name);
    }
}
