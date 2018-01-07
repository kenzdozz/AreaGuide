package com.ahtaya.chidozie.areaguide;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Class to adapt fragments to viewPager
 */
public class TypeFragmentAdapter extends FragmentPagerAdapter {

    //String array for search types
    private String[] mtypes;

    //Constructor
    TypeFragmentAdapter(FragmentManager fm, String[] types){
        super(fm);
        mtypes = types;
    }

    /**
     * Gets the item positions
     */
    @Override
    public Fragment getItem(int position) {
        //iterates over item positions to create the fragments.
        for (int pos = 0; pos < mtypes.length; pos++ ){
            if (position == pos){
                TypeFragment typeFragment = new TypeFragment();
                Bundle args = new Bundle();
                args.putString("type", mtypes[pos]);
                typeFragment.setArguments(args);
                return typeFragment;
            }
        }
        return null;
    }

    /**
     * Sets Fragment titles in TabLayout
     */
    @Override
    public CharSequence getPageTitle(int position) {
        //iterates over positions to generate titles
        for (int pos = 0; pos < mtypes.length; pos++ ){
            if (position == pos){
                return mtypes[pos].toUpperCase() + "s";
            }
        }
        return null;
    }

    @Override
    public int getCount() { return mtypes.length; }
}
