package ankur.hackgt.deepwatch.fragments;

import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ankur.hackgt.deepwatch.R;
import ankur.hackgt.deepwatch.model.UpdateData;

/**
 * Created by Ankur on 25/09/16.
 */
public class CelebrityFragment extends Fragment implements UpdateableFragment {

    TextView mTitle;
    TextView mBody;
    ImageView imageView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.celebrity, container, false);
        mTitle = (TextView) rootView.findViewById(R.id.title);
        mBody = (TextView) rootView.findViewById(R.id.body);
        imageView = (ImageView) rootView.findViewById(R.id.image);
        return rootView;
    }

    @Override
    public void update(UpdateData data) {
        // this method will be called for every fragment in viewpager
        // so check if update is for this fragment
        if(data.position==0) {
            // do whatever you want to update your UI
            mTitle.setText(Html.fromHtml(data.celebName));
            mBody.setText(Html.fromHtml(data.celebDetails));
            imageView.setImageBitmap(data.celebImage);

        }
    }
}