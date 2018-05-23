package banyan.com.anilcrm.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import banyan.com.anilcrm.R;


/**
 * Created by Jo 05-03-2018.
 */
public class Fragment_Enquiry extends Fragment {



    public Fragment_Enquiry() {
        // Required empty public constructor
    }

    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 3 ;
    String value = "nothing";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         *Inflate tab_layout and setup Views.
         */
        View x =  inflater.inflate(R.layout.fragment_enquiry,null);
        tabLayout = (TabLayout) x.findViewById(R.id.tabs);
        viewPager = (ViewPager) x.findViewById(R.id.viewpager);

        /**
         *Set an Apater for the View Pager
         */
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

        return x;

    }

    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Return fragment with respect to Position .
         */

        @Override
        public Fragment getItem(int position)
        {
            switch (position){
                case 0 : return new Tab_Enquiry_Shop_Fragment();
                case 1 : return new Tab_Edit_Shop_Fragment();
                case 2 : return new Tab_Enquiry_Agency_Fragment();
           }
            return null;
        }

        @Override
        public int getCount() {

            return int_items;

        }
        /**
         * This method returns the title of the tab according to the position.
         */
        @Override
        public CharSequence getPageTitle(int position) {

            switch (position){
                case 0 :
                    return "Shops";
                case 1 :
                    return "Edit Shop";
                case 2 :
                    return "Agencies";
              /*  case 2 :
                    return "Locations";*/
            }
            return null;
        }
    }

}
