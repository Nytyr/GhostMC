package com.ghostmc.android;


import android.content.Intent;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ghostmc.android.app.NfcActivity;
import com.ghostmc.android.carousel.CarouselPagerAdapter;
import com.ghostmc.android.ui.TagSelector;

import java.util.List;

public class MainActivity extends NfcActivity implements View.OnClickListener {

    private final static String TAG = "MainActivity";

    public CarouselPagerAdapter adapter;
    public ViewPager pager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button reader = (Button) findViewById(R.id.reader);
        reader.setOnClickListener(this);
        final Button writter = (Button) findViewById(R.id.writter);
        writter.setOnClickListener(this);
        final Button delete = (Button) findViewById(R.id.delete);
        delete.setOnClickListener(this);
        final Button keys = (Button) findViewById(R.id.keys);
        keys.setOnClickListener(this);

        // Carousel
        pager = (ViewPager) findViewById(R.id.carousel);

        setCarousel();

    }

    private void setCarousel() {
        List<TagSelector.Item> tags = TagSelector.get(getApplicationContext());

        if (tags.isEmpty()) {
            final TagSelector.Item tag = new TagSelector.Item();
            tag.id = -1;
            tag.name = getString(R.string.empty);
            tags.add(tag);
        }

        adapter = new CarouselPagerAdapter(this, getSupportFragmentManager(), tags);
        pager.setAdapter(adapter);

        pager.setOnPageChangeListener(adapter);

        pager.setCurrentItem(adapter.getFirstPage());

        // Necessary or the pager will only have one extra page to show
        // make this at least however many pages you can see
        pager.setOffscreenPageLimit(3);

        // Set margin for pages as a negative number, so a part of next and
        // previous pages will be showed
        Display display = getWindowManager().getDefaultDisplay();
        int margin = Math.round((float) (display.getWidth() / 1.8));
        Log.d(TAG, "Margin > "+ margin);
        pager.setPageMargin(-margin);
    }

    @Override
    public void onResume() {
        super.onResume();
        setCarousel();
    }

    @Override
    public void onReceiveTag(final Tag tag) {
        final MifareClassic mifareClassic = MifareClassic.get(tag);
        if (mifareClassic == null) {
            Toast.makeText(getApplicationContext(), getString(R.string.no_mifare), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.reader:
                Intent readerIntent = new Intent(MainActivity.this, ReaderActivity.class);
                startActivity(readerIntent);
                break;
            case R.id.writter:
                Intent writterIntent = new Intent(MainActivity.this, WritterActivity.class);
                writterIntent.putExtra("selectedTag", adapter.getCurrentPosition());
                startActivity(writterIntent);
                break;
            case R.id.delete:
                Intent deleteIntent = new Intent(MainActivity.this, DeleteActivity.class);
                deleteIntent.putExtra("selectedTag", adapter.getCurrentPosition());
                startActivity(deleteIntent);
                break;
            case R.id.keys:
                Intent keyIntent = new Intent(MainActivity.this, KeyActivity.class);
                startActivity(keyIntent);
                break;
        }
    }
}
