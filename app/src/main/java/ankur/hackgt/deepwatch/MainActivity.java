package ankur.hackgt.deepwatch;


import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.FaceRectangle;
import com.pixelcan.inkpageindicator.InkPageIndicator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ankur.hackgt.deepwatch.model.UpdateData;


public class MainActivity extends FragmentActivity implements TextureView.SurfaceTextureListener, MediaController.MediaPlayerControl {



    private static final String TAG = MainActivity.class.getName();
    private MediaPlayer mMediaPlayer;
    private TextureView mPreview;
    ImageView im;
    Button deepWatch;
    private FaceServiceClient faceServiceClient;
    private MediaController mediaController;
    InkPageIndicator mIndicator;
    int[] mVideos = {R.raw.v1, R.raw.v2};
    static int mVideoNumber = -1;
    private CustomPagerAdapter mCustomPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);



        mPreview = (TextureView) findViewById(R.id.textureView);
        mPreview.setSurfaceTextureListener(this);


        faceServiceClient = new FaceServiceRestClient(Config.MICROSOFT_DEVELOPER_KEY);


        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        mCustomPagerAdapter = new CustomPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mCustomPagerAdapter);
        mIndicator = (InkPageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(viewPager);

        deepWatch = (Button) findViewById(R.id.deepwatch);
        deepWatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("bitmap", "" + getBitmap());
//                im = (ImageView) findViewById(R.id.image);
//                im.setImageBitmap(getBitmap());
                mMediaPlayer.pause();
                mediaController.show();
                Log.d("DEBUG:", "Calling detectAndFrame");
                detectAndFrame(getBitmap());
                UpdateData data = new UpdateData();
                data.position = 0;
                data.celebName = "Kaley Cuoco";
                data.celebDetails = "I love acting :P";
                data.celebImage = getBitmap();
                mCustomPagerAdapter.update(data);


            }
        });


    }

    public Bitmap getBitmap(){
        return  mPreview.getBitmap();
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {

        Surface surface = new Surface(surfaceTexture);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setSurface(surface);
        mMediaPlayer.setLooping(true);
        mediaController = new MediaController(MainActivity.this);
        mediaController.setMediaPlayer(this);//your activity which implemented MediaPlayerControl
        mediaController.setAnchorView(mPreview);
        mediaController.setEnabled(true);
        mediaController.setPadding(0, 0, 0, 0);
        mediaController.show();
        playNextVideo();
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                playNextVideo();

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            // Make sure we stop video and release resources when activity is destroyed.
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }

    private void detectAndFrame(final Bitmap imageBitmap){
            Log.d("DEBUG:", "Inside detectAndFrame");
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            ByteArrayInputStream inputStream =
                    new ByteArrayInputStream(outputStream.toByteArray());
        AsyncTask<InputStream, String, Face[]> detectTask =
                new AsyncTask<InputStream, String, Face[]>() {
                    @Override
                    protected Face[] doInBackground(InputStream... params) {
                        Log.d("DEBUG:", "Inside AsyncTask");
                        try {
                            publishProgress("Detecting...");
                            Face[] result = faceServiceClient.detect(
                                    params[0],
                                    true,         // returnFaceId
                                    false,        // returnFaceLandmarks
                                    null           // returnFaceAttributes: a string like "age, gender"
                            );
                            if (result == null) {
                                publishProgress("Detection Finished. Nothing detected");
                                return null;
                            }
                            publishProgress(
                                    String.format("Detection Finished. %d face(s) detected",
                                            result.length));
                            return result;
                        } catch (Exception e) {
                            publishProgress("Detection failed");
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(Face[] result) {
                        Log.d("DEBUG:", "Inside Post Execution");

                        if (result == null) return;
                        //im.setImageBitmap(getFaceRectanglesOnBitmap(imageBitmap, result).get(0));
                        //imageBitmap.recycle();
                    }
                };
        detectTask.execute(inputStream);
    }

    private static List<Bitmap> getFaceRectanglesOnBitmap(Bitmap originalBitmap, Face[] faces) {
        Log.d("DEBUG:", "Inside getFaceRectangles");
        List<Bitmap> result = new ArrayList<Bitmap>();

        if (faces != null) {
            for (Face face : faces) {
                FaceRectangle faceRectangle = face.faceRectangle;
                Log.d("DEBUG:", faceRectangle.left + " " + faceRectangle.top + " " + faceRectangle.width + " " + faceRectangle.height);
                Bitmap bitmap = Bitmap.createBitmap(originalBitmap, faceRectangle.left,
                        faceRectangle.top, faceRectangle.width, faceRectangle.height);

                result.add(bitmap);
            }
        }
        if (result == null || result.size()==0)
            Log.d("DEBUG:", "Exiting getFaceRectangles with null");
        Log.d("DEBUG:", "Exiting getFaceRectangles");
        return result;
    }

    //--MediaPlayerControl methods----------------------------------------------------
    @Override
    public void start() {
        mMediaPlayer.start();
    }
    @Override
    public void pause() {
        mMediaPlayer.pause();
    }
    @Override
    public int getDuration() {
        return mMediaPlayer.getDuration();
    }
    @Override
    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }
    @Override
    public void seekTo(int i) {
        mMediaPlayer.seekTo(i);
    }
    @Override
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }
    @Override
    public int getBufferPercentage() {
        return 0;
    }
    @Override
    public boolean canPause() {
        return true;
    }
    @Override
    public boolean canSeekBackward() {
        return true;
    }
    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //the MediaController will hide after 3 seconds - tap the screen to make it appear again
        mediaController.show();
        return false;
    }
    //--------------------------------------------------------------------------------

    private void playNextVideo(){
        try {
            if (mMediaPlayer != null) {
                mMediaPlayer.reset();
                mVideoNumber = ++mVideoNumber == mVideos.length ? 0 : mVideoNumber;
                Log.d("videoNumber: ", "" + mVideoNumber);
                String FILE_NAME = "android.resource://" + getPackageName() + "/" + mVideos[mVideoNumber];
                mMediaPlayer.setDataSource(MainActivity.this, Uri.parse(FILE_NAME));
                mMediaPlayer.prepareAsync();
                // Play video when the media source is ready for playback.
                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        // set up media controller
                        mediaPlayer.start();
                    }
                });
            }
        }catch(IllegalArgumentException e){
            Log.d(TAG, e.getMessage());
        }catch(SecurityException e){
            Log.d(TAG, e.getMessage());
        }catch(IllegalStateException e){
            Log.d(TAG, e.getMessage());
        }catch(IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }
}
