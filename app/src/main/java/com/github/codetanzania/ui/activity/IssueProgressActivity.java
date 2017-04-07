package com.github.codetanzania.ui.activity;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.github.codetanzania.ui.fragment.GoogleMapFragment;
import com.github.codetanzania.ui.fragment.InternalNoteFragment;
import com.github.codetanzania.ui.fragment.IssueDetailsFragment;
import com.github.codetanzania.model.Comment;
import com.github.codetanzania.model.ServiceRequest;
import com.github.codetanzania.util.AppConfig;

import java.util.ArrayList;

import tz.co.codetanzania.R;

public class IssueProgressActivity extends AppCompatActivity /*implements OnMapReadyCallback*/ {

    // private GoogleMap mGoogleMap;
    private DetailsPagerAdapter mDetailsPagerAdapter;
    private ViewPager mViewPager;
    private BottomNavigationView mBottomNav;
    private ServiceRequest mServiceRequest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_progress);
        mServiceRequest = getIntent().getExtras().getParcelable(AppConfig.Const.TICKET);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        // TODO: add ticket id as the title of this actionbar

        mDetailsPagerAdapter = new DetailsPagerAdapter(getSupportFragmentManager(), 3);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(mDetailsPagerAdapter);

        mBottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.action_view_details:
                        mViewPager.setCurrentItem(DetailsPagerAdapter.DETAILS_FRAG_POS);
                        return true;
                    case R.id.action_view_progress:
                        mViewPager.setCurrentItem(DetailsPagerAdapter.INTERNAL_NOTES_FRAG_POS);
                        return true;
                    case R.id.action_view_on_map:
                        mViewPager.setCurrentItem(DetailsPagerAdapter.GOOGLE_MAP_FRAG_POS);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class DetailsPagerAdapter extends FragmentPagerAdapter {

        public static final int DETAILS_FRAG_POS = 0;
        public static final int INTERNAL_NOTES_FRAG_POS = 1;
        public static final int GOOGLE_MAP_FRAG_POS = 2;
        private final int FRAGS_COUNT;

        private String[] titles;

        public DetailsPagerAdapter(FragmentManager fm, int frags_count) {
            super(fm);
            FRAGS_COUNT = frags_count;
            titles = getResources().getStringArray(R.array.details_viewpager_titles);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == DETAILS_FRAG_POS) {
                return IssueDetailsFragment.getInstance(getIntent().getExtras());
            } else if (position == INTERNAL_NOTES_FRAG_POS) {

                // no surprises
                mServiceRequest.comments = mServiceRequest.comments == null ?
                        new ArrayList<Comment>() : mServiceRequest.comments;
                // lets fix this baby
                Comment selfComment = new Comment();
                selfComment.timestamp = mServiceRequest.createdAt;
                selfComment.commentor = mServiceRequest.reporter.name;
                selfComment.content = mServiceRequest.description;
                mServiceRequest.comments.add(selfComment);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(AppConfig.Const.ISSUE_COMMENTS, (ArrayList<? extends Parcelable>) mServiceRequest.comments);
                return InternalNoteFragment.getInstance(bundle);
            } else if (position == GOOGLE_MAP_FRAG_POS) {
                return new GoogleMapFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return FRAGS_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
