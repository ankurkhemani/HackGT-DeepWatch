package ankur.hackgt.deepwatch.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ankur.hackgt.deepwatch.R;

/**
 * Created by Ankur on 25/09/16.
 */
public class TopicFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.celebrity, container, false);

        return rootView;
    }
}