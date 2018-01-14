package com.ahtaya.chidozie.areaguide;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Class to adapt fragments to viewPager
 */
public class TypeFragmentAdapter extends FragmentPagerAdapter {

    //ArrayList for search types
    private ArrayList<String> mtypes;

    //Constructor
    TypeFragmentAdapter(FragmentManager fm, ArrayList<String> types) {
        super(fm);
        mtypes = types;
        //sorting array alphabetically
        Collections.sort(mtypes);
    }

    /**
     * Gets the item positions
     */
    @Override
    public Fragment getItem(int position) {
        //iterates over item positions to create the fragments.
        for (int pos = 0; pos < mtypes.size(); pos++) {
            if (position == pos){
                TypeFragment typeFragment = new TypeFragment();
                Bundle args = new Bundle();
                args.putString("type", mtypes.get(pos));
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
        for (int pos = 0; pos < mtypes.size(); pos++) {
            if (position == pos){
                return mtypes.get(pos).replace("_", " ").toUpperCase() + "s";
            }
        }
        return null;
    }

    @Override
    public int getCount() {
        return mtypes.size();
    }
}
