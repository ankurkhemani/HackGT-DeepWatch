package ankur.hackgt.deepwatch;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
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

import com.microsoft.projectoxford.face.*;
import com.microsoft.projectoxford.face.contract.*;

import java.io.IOException;


public class MainActivity extends Activity implements TextureView.SurfaceTextureListener, MediaController.MediaPlayerControl {


    private static final String TAG = MainActivity.class.getName();
    private MediaPlayer mMediaPlayer;
    private TextureView mPreview;

    Button deepWatch;
    private FaceServiceClient faceServiceClient;
    private MediaController mediaController;

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

        deepWatch = (Button) findViewById(R.id.deepwatch);
        deepWatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("bitmap", "" + getBitmap());
                ImageView im = (ImageView) findViewById(R.id.image);
                im.setImageBitmap(getBitmap());
                mMediaPlayer.pause();
                mediaController.show();
            }
        });

    }

    public Bitmap getBitmap(){
        return  mPreview.getBitmap();
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        Surface surface = new Surface(surfaceTexture);

        try {
            mMediaPlayer = new MediaPlayer();
            String FILE_NAME = "android.resource://" + getPackageName() + "/" + R.raw.video_1;
            mMediaPlayer
                    .setDataSource(this, Uri.parse(FILE_NAME));
            mMediaPlayer.setSurface(surface);
            mMediaPlayer.setLooping(true);

            // don't forget to call MediaPlayer.prepareAsync() method when you use constructor for
            // creating MediaPlayer
            mMediaPlayer.prepareAsync();
            mediaController = new MediaController(MainActivity.this);
            mediaController.setMediaPlayer(this);//your activity which implemented MediaPlayerControl
            mediaController.setAnchorView(mPreview);
            mediaController.setEnabled(true);
            mediaController.setPadding(0, 0, 0, 0);
            mediaController.show();

            // Play video when the media source is ready for playback.
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    // set up media controller
                    mediaPlayer.start();
                }
            });

        } catch (IllegalArgumentException e) {
            Log.d(TAG, e.getMessage());
        } catch (SecurityException e) {
            Log.d(TAG, e.getMessage());
        } catch (IllegalStateException e) {
            Log.d(TAG, e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
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
}
