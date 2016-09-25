package ankur.hackgt.deepwatch.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ankur.hackgt.deepwatch.R;
import ankur.hackgt.deepwatch.model.UpdateData;

/**
 * Created by Ankur on 25/09/16.
 */
public class TopicFragment extends Fragment implements UpdateableFragment{
    TextView mEntity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.topic, container, false);
        mEntity = (TextView)rootView.findViewById(R.id.entity);
        return rootView;
    }

    @Override
    public void update(UpdateData data) {
        // this method will be called for every fragment in viewpager
        // so check if update is for this fragment
        if(data.position==1) {
            // do whatever you want to update your UI
            mEntity.setText(data.entities);


        }
    }
}