package me.odj.cymorth;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.app.ActionBar.NAVIGATION_MODE_TABS;
import static android.app.ActionBar.Tab;
import static android.app.ActionBar.TabListener;

public class TimetableView extends FragmentActivity implements TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    final TabListener self = this;

    /*@Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equalsIgnoreCase(AlarmService.ACTION_RELOAD)) {
            Log.d("abersistant", "Caught reload broadcast");
            finish();
            startActivity(getIntent());
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setAppLocale(this);

        setTitle(R.string.your_timetable);
        setContentView(R.layout.timetable);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        final TimetableView mTabListener = this;

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager
                .SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
                if(position >= mSectionsPagerAdapter.getCount()) {
                    for(int i=position; i < position + 5; i++) {
                        actionBar.addTab(
                            actionBar.newTab()
                                .setText(mSectionsPagerAdapter.getPageTitle(i))
                                .setTabListener(mTabListener));
                    }
                }
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        mViewPager.setCurrentItem((c.get(Calendar.DAY_OF_WEEK) - Calendar
                .MONDAY));
    }

    @Override
    public void onResume() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences
                (this);
        if(sp.getBoolean("new_data", false) == true) {
            sp.edit().putBoolean("new_data", false).commit();
            finish();
            startActivity(getIntent());
        }
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.timetable, menu);

        MenuItem item1 = menu.findItem(R.id.action_import);
        Intent intent1 = new Intent(this.getApplicationContext(),
                ImportActivity.class);
        item1.setIntent(intent1);

        MenuItem item2 = menu.findItem(R.id.action_settings);
        Intent intent2 = new Intent(this.getApplicationContext(),
                SettingsActivity.class);
        item2.setIntent(intent2);

        MenuItem item3 = menu.findItem(R.id.action_about);
        Intent intent3 = new Intent(this.getApplicationContext(), About.class);
        item3.setIntent(intent3);

        return true;
    }

    public void switch_language(MenuItem item) {
        if(Utils.getCurrentLanguage(getApplicationContext()) == "cy") {
            Utils.setCurrentLanguage(getApplicationContext(), "gb");
        } else {
            Utils.setCurrentLanguage(getApplicationContext(), "cy");
        }
        Log.d("cymorth", "Language set to " + Utils.getCurrentLanguage(getApplicationContext()));
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private int pageCount = 5;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a DummySectionFragment (defined as a static inner class
            // below) with the page number as its lone argument.
            Date day;
            Calendar c = UniCalendar.getInstance();
                c.set(Calendar.DAY_OF_WEEK, (position) + Calendar.MONDAY);
                day = c.getTime();
            TimetablePage fragment = new TimetablePage();
            Bundle args = new Bundle();
            args.putString("title" + (String)this.getPageTitle(position), "");
            fragment.tab_day = day;
            fragment.setArguments(args);
            return (Fragment)fragment;
        }

        @Override
        public int getCount() {
            return this.pageCount;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            //Locale l = Locale.getDefault();
            Calendar c = UniCalendar.getInstance();
            Date day;
            String prefix = "";
            String suffix = "";
            c.set(Calendar.DAY_OF_WEEK, (position) + Calendar.MONDAY);
            day = c.getTime();
            //return prefix + String.format("%1$ta %1$td",
            //        day).toUpperCase() + suffix;
            return Utils.getLocalDateShort(getApplicationContext(), c).toUpperCase();
        }
    }

}
