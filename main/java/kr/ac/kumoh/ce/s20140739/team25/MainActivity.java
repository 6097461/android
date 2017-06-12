package kr.ac.kumoh.ce.s20140739.team25;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity  {


    private SectionsPagerAdapter mSectionsPagerAdapter;

    private static Typeface mTypeface=null;
    private ViewPager mViewPager;
    static String SERVER_IP_PORT = "http://192.168.0.48:3003";
    private final long FINISH_INTERVAL_TIME=1500;
    private long backPressedTime=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT>=21){
            getWindow().setStatusBarColor(Color.rgb(13,72,135));//상태바색깔
        }

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);



    }

    @Override
    public void onBackPressed() {
        long tempTime=System.currentTimeMillis();
        long intervalTime=tempTime-backPressedTime;
        if(0<=intervalTime&&FINISH_INTERVAL_TIME>=intervalTime){
            //super.onBackPressed();
            Intent t=new Intent(this,MainActivity.class);
            this.startActivity(t);
            this.moveTaskToBack(true);
            this.finish();
            android.os.Process.killProcess(android.os.Process.myPid());



        }
        else{
            backPressedTime=tempTime;
            Toast.makeText(getApplicationContext(),"한번 더 누르면 앱이 종료됩니다.",Toast.LENGTH_SHORT).show();
        }

    }

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
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
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
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            switch (position) {
                case 0:
                    return new myfrag1();
                case 1:
                    return new myfrag2();
                case 2:
                    return new myfrag3();
                case 3:
                    return new myfrag4();
            }
            return null;
        }

        @Override
        public int getCount() {

            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "HOME";
                case 1:
                    return "KEY";
                case 2:
                    return "MyPage";
                case 3:
                    return "center";
            }
            return null;
        }
//        @Override
//        public int getItemPosition(Object object) {
//
//            return POSITION_NONE;
//        }


    }
}
