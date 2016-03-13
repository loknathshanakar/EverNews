package com.evernews.evernews;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

public class AddTab extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
Context context;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tab);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.go_backpng);

        context=this;
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        for (int i = 0; i < Initilization.addOnList.size(); i++) {
            int position = i;
            if (i == 0) {
                View v = View.inflate(context, R.layout.layout_tab_2, null);
                TextView tvt = (TextView) v.findViewById(R.id.tab_tv_2);
                tvt.setText("Featured");
                tabLayout.getTabAt(i).setCustomView(v);
                tabLayout.getTabAt(i).getCustomView().setBackgroundResource(R.drawable.tab_color2);
                tabLayout.setSelectedTabIndicatorColor(Color.rgb(0, 0, 100));
                tabLayout.setSelectedTabIndicatorHeight(5);
            }
            if (i == 1) {
                View v = View.inflate(context, R.layout.layout_tab_3, null);
                TextView tvt = (TextView) v.findViewById(R.id.tab_tv_3);
                tvt.setText("Popular");
                tabLayout.getTabAt(i).setCustomView(v);
                tabLayout.getTabAt(i).getCustomView().setBackgroundResource(R.drawable.tab_color3);
                tabLayout.setSelectedTabIndicatorColor(Color.rgb(0, 0, 100));
                tabLayout.setSelectedTabIndicatorHeight(5);
            }
            if (i == 2) {
                View v = View.inflate(context, R.layout.layout_tab_4, null);
                TextView tvt = (TextView) v.findViewById(R.id.tab_tv_4);
                tvt.setText("Category");
                tabLayout.getTabAt(i).setCustomView(v);
                tabLayout.getTabAt(i).getCustomView().setBackgroundResource(R.drawable.tab_color4);
                tabLayout.setSelectedTabIndicatorColor(Color.rgb(0, 0, 100));
                tabLayout.setSelectedTabIndicatorHeight(5);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_tab, menu);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent=new Intent(AddTab.this,Main.class);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id==android.R.id.home){
            Intent intent=new Intent(AddTab.this,Main.class);
            startActivity(intent);
            finish();
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_add_tab, container, false);
            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position==0){
                return NewsAddListFragment.newInstanceREE(position, "Featured");
            }
            else if(position==1){
                return NewsAddListFragment.newInstanceREE(position, "Popular");
            }else{
                return NewsAddListFragment.newInstanceREE(position, "Categories");
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Featured";
                case 1:
                    return "Popular";
                case 2:
                    return "Categories";
            }
            return null;
        }
    }
}
