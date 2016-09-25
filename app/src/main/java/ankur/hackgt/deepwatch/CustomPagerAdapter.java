package ankur.hackgt.deepwatch;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ankur.hackgt.deepwatch.fragments.CelebrityFragment;
import ankur.hackgt.deepwatch.fragments.TopicFragment;
import ankur.hackgt.deepwatch.fragments.UpdateableFragment;
import ankur.hackgt.deepwatch.model.UpdateData;

/**
 * Created by Ankur on 25/09/16.
 */
class CustomPagerAdapter extends FragmentPagerAdapter {
    private UpdateData updateData;

    public CustomPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Top Rated fragment activity
                return new CelebrityFragment();
            case 1:
                // Games fragment activity
                return new TopicFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    //call this method to update fragments in ViewPager dynamically
    public void update(UpdateData xyzData) {
        this.updateData = xyzData;
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        if (object instanceof UpdateableFragment) {
            ((UpdateableFragment) object).update(updateData);
        }
        //don't return POSITION_NONE, avoid fragment recreation.
        return super.getItemPosition(object);
    }
}