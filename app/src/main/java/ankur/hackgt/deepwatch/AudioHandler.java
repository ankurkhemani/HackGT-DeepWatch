package ankur.hackgt.deepwatch;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * Created by kumaas on 9/25/16.
 */
public class AudioHandler {

    private int _timeInterval;
    private HashMap<String, Vector<EntityStructure>> _files;

    static class EntityStructure {
        public int start;
        public int end;
        public String text;

        public EntityStructure(int start, int end, String text) {
            this.start = start;
            this.end = end;
            this.text = text;
        }
    }

    public AudioHandler(Resources res) {
        this(10, res);
    }

    public AudioHandler(int timeInterval, Resources res) {
        this._timeInterval = timeInterval;
        this._files = new HashMap<String, Vector<EntityStructure>>();
        String [] myfiles = new String [] { "0", "1"};
        for (String filename: myfiles) {
            if (filename == "0") {
                InputStream is = res.openRawResource(R.drawable.audio0);
            }
            else if (filename == "1") {
                InputStream is = res.openRawResource(R.drawable.audio1);
            }
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = br.readLine()) != null) {
                    List<String> timeStamps = Arrays.asList(line.split(","));
                    EntityStructure es = new EntityStructure(
                            Integer.parseInt(timeStamps.get(0)),
                            Integer.parseInt(timeStamps.get(1)), br.readLine());

                    if(this._files.containsKey(filename)){
                        this._files.get(filename).add(es);
                    }
                    else {
                        Vector<EntityStructure> ess = new Vector<EntityStructure>();
                        ess.add(es);
                        this._files.put(filename, ess);
                    }
                }
                br.close();
            }
            catch (IOException e) {
                Log.d("ERROR:", e.getMessage());
            }
        }
    }

    public List<String> getEntities(String fileId, int timeStamp) {
        List<String> result = new ArrayList<String>();
        if (_files.containsKey(fileId)) {
            for (EntityStructure es: _files.get(fileId)) {
                if ((es.start < timeStamp) && (es.start > (timeStamp - this._timeInterval))) {
                    result.add(es.text);
                }
                else if ((es.end < timeStamp) && (es.end > (timeStamp - this._timeInterval))) {
                    result.add(es.text);
                }
            }
        }
        return result;
    }
}
