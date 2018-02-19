package com.ola.jeet.olamusicplay.activity;

/**
 * Created by jeet on 12/18/2017.
 */

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ola.jeet.olamusicplay.R;
import com.ola.jeet.olamusicplay.models.SongItem;
import com.ola.jeet.olamusicplay.util.PrefManager;
import com.ola.jeet.olamusicplay.util.RuntimePermission;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


public class PlayActivity extends AppCompatActivity implements VideoRendererEventListener {


    private static final String TAG = "PlayActivity";
    private SimpleExoPlayerView simpleExoPlayerView;
    private SimpleExoPlayer player;
    private TextView resolutionTextView,name,download,playlist;
    private String location;
    private ArrayList<SongItem> songList;
    private SongItem songItem;
    private PrefManager sharedPreference;
    private static final int REQUEST_APP_SETTINGS=101;
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // toolbar fancy stuff
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.toolbar_player);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        resolutionTextView = new TextView(this);
        resolutionTextView = (TextView) findViewById(R.id.resolution_textView);
        name=(TextView)findViewById(R.id.sample_app_title);
        download=(TextView) findViewById(R.id.download);
        playlist=(TextView) findViewById(R.id.playlist);
        sharedPreference=new PrefManager(this);
        Typeface typeface= Typeface.createFromAsset(getAssets(),"fontawesome-webfont.ttf");
        download.setTypeface(typeface);


        songItem=getIntent().getParcelableExtra("current_song");
        name.setText(getIntent().getStringExtra("name"));
        resolutionTextView.setText(getIntent().getStringExtra("artist"));
        location=getIntent().getStringExtra("url");
        songList=getIntent().getParcelableArrayListExtra("list");


        /*
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(location).openConnection();
            connection.setInstanceFollowRedirects(false);

            while (connection.getResponseCode() / 100 == 3) {
                location = connection.getHeaderField("location");
                connection = (HttpURLConnection) new URL(location).openConnection();
            }
        }catch (Exception e){

        }
        */



// 1. Create a default TrackSelector
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

// 2. Create a default LoadControl
        LoadControl loadControl = new DefaultLoadControl();

// 3. Create the player
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
        simpleExoPlayerView = new SimpleExoPlayerView(this);
        simpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.player_view);

//Set media controller
        simpleExoPlayerView.setUseController(true);
        simpleExoPlayerView.requestFocus();

// Bind the player to the view.
        simpleExoPlayerView.setPlayer(player);




//Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter bandwidthMeterA = new DefaultBandwidthMeter();
//Produces DataSource instances through which media data is loaded.

        String userAgent = Util.getUserAgent(this, "OlaMusicPlay");

// Default parameters, except allowCrossProtocolRedirects is true
        DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(
                userAgent,
                null /* listener */,
                DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                true /* allowCrossProtocolRedirects */
        );

        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(
                this,
                null /* listener */,
                httpDataSourceFactory
        );

        //DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "exoplayer2example"), bandwidthMeterA);
//Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        ArrayList<MediaSource> mediaSources=new ArrayList<>();

        int length=songList.size();
        int length1=length;
        int current=songItem.getIndex();
        for(SongItem songItem1:songList)
            mediaSources.add(new ExtractorMediaSource(Uri.parse(songItem1.getUrl()), dataSourceFactory,extractorsFactory, null, null));
        /*
        while(length1>0){
            SongItem songItem1=songList.get(current);
            mediaSources.add(new ExtractorMediaSource(Uri.parse(songItem1.getUrl()), dataSourceFactory,extractorsFactory, null, null));
            if(current==length-1)
                current=0;
            else
                current++;
            length1--;
        }
        */
        //MediaSource videoSource = new ExtractorMediaSource(mp4Uri, dataSourceFactory,extractorsFactory, null, null);
        //final LoopingMediaSource loopingSource = new LoopingMediaSource(videoSource);
        final ConcatenatingMediaSource source = new ConcatenatingMediaSource(mediaSources.toArray(new MediaSource[mediaSources.size()]));
        //LoopingMediaSource compositeSource = new LoopingMediaSource(source);
// Prepare the player with the source.
        player.prepare(source);
        player.seekTo(current,0);


        player.addListener(new ExoPlayer.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {
                Log.v(TAG, "Listener-onTimelineChanged...");
            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                Log.v(TAG, "Listener-onTracksChanged...");
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
                Log.v(TAG, "Listener-onLoadingChanged...isLoading:"+isLoading);
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Log.v(TAG, "Listener-onPlayerStateChanged..." + playbackState);
                if (playbackState == PlaybackStateCompat.STATE_SKIPPING_TO_NEXT) {
                    //do something
                }
                if (playbackState == PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS) {
                    //do something else
                }
            }


            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Log.v(TAG, "Listener-onPlayerError...");
                player.stop();
                player.prepare(source);
                player.setPlayWhenReady(true);
            }

            @Override
            public void onPositionDiscontinuity() {
                Log.v(TAG, "Listener-onPositionDiscontinuity...");
                int sourceIndex = player.getCurrentWindowIndex();
                Log.d(TAG,String.valueOf(sourceIndex));
                if(sourceIndex<songList.size()) {
                    name.setText(songList.get(sourceIndex).getSongName());
                    resolutionTextView.setText(songList.get(sourceIndex).getSongArtist());
                }

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                Log.v(TAG, "Listener-onPlaybackParametersChanged...");
            }
        });

        player.setPlayWhenReady(true); //run file/link when ready to play.
        player.setVideoDebugListener(this); //for listening to resolution change and  outputing the resolution
    }//End of onCreate

    @Override
    public void onVideoEnabled(DecoderCounters counters) {

    }

    @Override
    public void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {

    }

    @Override
    public void onVideoInputFormatChanged(Format format) {

    }

    @Override
    public void onDroppedFrames(int count, long elapsedMs) {

    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        Log.v(TAG, "onVideoSizeChanged ["  + " width: " + width + " height: " + height + "]");
        //resolutionTextView.setText("RES:(WxH):"+width+"X"+height +"\n           "+height+"p");
    }

    @Override
    public void onRenderedFirstFrame(Surface surface) {

    }

    @Override
    public void onVideoDisabled(DecoderCounters counters) {

    }

    public void downloadFile(View view)
    {
        RuntimePermission runtimePermission;
        if(Build.VERSION.SDK_INT>=23) {
            if(ActivityCompat.checkSelfPermission(PlayActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==
                            PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(PlayActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
                downloadStart();
            }
            else{
                runtimePermission = new RuntimePermission(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, PlayActivity.this, RuntimePermission.MY_PERMISSIONS_REQUEST_STORAGE,
                        getString(R.string.check_storage_permission_text), true);
                runtimePermission.checkPermission();
            }
        }
        else
            downloadStart();
    }

    public void downloadStart(){
            try {
                int sourceIndex = player.getCurrentWindowIndex();
                //URL url = new URL(songList.get(sourceIndex).getUrl());

                String url = songList.get(sourceIndex).getUrl();
                Log.d(TAG,url);
/*
                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.setInstanceFollowRedirects(false);

                    while (connection.getResponseCode() / 100 == 3) {
                        url = connection.getHeaderField("location");
                        connection = (HttpURLConnection) new URL(location).openConnection();
                    }
*/
                //System.out.println("Short URL: "+ shortURL);

                new DownloadFileAsync().execute("https://s3-ap-southeast-1.amazonaws.com/he-public-data/Aik%20-%20Alif-(Mr-Jatt.com)8ae5316.mp3");
            }catch (Exception e){

            }

    }
/*
    URLConnection connectURL(String strURL) {
        URLConnection conn =null;
        try {
            URL inputURL = new URL(strURL);
            conn = inputURL.openConnection();
            int test = 0;

        }catch(MalformedURLException e) {
            System.out.println("Please input a valid URL");
        }catch(IOException ioe) {
            System.out.println("Can not connect to the URL");
        }
        return conn;
    }
*/
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_DOWNLOAD_PROGRESS:
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage("Downloading file..");
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
                return mProgressDialog;
            default:
                return null;
        }
    }
    public void openList(View view)
    {

        String jsonadd=sharedPreference.getPlaylist();
        Gson gsonadd = new Gson();
        Type typeadd = new TypeToken<ArrayList<SongItem>>() {}.getType();
        ArrayList<SongItem> songItemArrayList = gsonadd.fromJson(jsonadd, typeadd);
        if(songItemArrayList==null){
            Toast.makeText(this,"List empty",Toast.LENGTH_SHORT).show();
        }else {
            Intent intent=new Intent(this,MainActivity.class);
            intent.putParcelableArrayListExtra("list", songItemArrayList);
            intent.putExtra("toolbar", "My Playlist");
            startActivity(intent);
        }
    }

    class DownloadFileAsync extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(DIALOG_DOWNLOAD_PROGRESS);
        }

        @Override
        protected String doInBackground(String... aurl) {
            int count;
            try {
                URL url = new URL(aurl[0]);
                URLConnection conexion = url.openConnection();
                conexion.connect();
                int lenghtOfFile = conexion.getContentLength();
                Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);
                File cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"Ola Play");
                if(!cacheDir.exists())
                    cacheDir.mkdirs();

                int sourceIndex = player.getCurrentWindowIndex();
                File f=new File(cacheDir,songList.get(sourceIndex).getSongName()+".mp3");
                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(f);
                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress(""+(int)((total*100)/lenghtOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {}
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            Log.d("ANDRO_ASYNC",progress[0]);
            mProgressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String unused) {
            dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==RuntimePermission.MY_PERMISSIONS_REQUEST_STORAGE )
        {
            int temp=0;
            int temp1=0;
            for(int result=0;result<grantResults.length;result++)
            {
                if(grantResults[result]==-1 && ActivityCompat.checkSelfPermission(this,permissions[result])!=PackageManager.PERMISSION_GRANTED)
                {
                    temp++;
                }
                if(!ActivityCompat.shouldShowRequestPermissionRationale(this,permissions[result]))
                {
                    temp1++;
                }
            }
            if(temp==temp1)
            {
                if(sharedPreference==null)
                    sharedPreference=new PrefManager(this);
                sharedPreference.setNeverAskBeforeStorageState(true);
            }
            if(ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
            {
                downloadStart();
            }else{
                if(sharedPreference.getNeverAskBeforeStorageState())
                {
                    goToSettings();
                }

            }

        }
    }

    private void goToSettings() {
        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(myAppSettings, REQUEST_APP_SETTINGS);
    }

    public void addPlaylist(View view)
    {
        String jsonadd=sharedPreference.getPlaylist();
        Gson gsonadd = new Gson();
        Type typeadd = new TypeToken<ArrayList<SongItem>>() {}.getType();
        ArrayList<SongItem> songItemArrayList = gsonadd.fromJson(jsonadd, typeadd);
        if(songItemArrayList==null){
            songItemArrayList=new ArrayList<>();
        }
        if(!songItemArrayList.contains(songItem))
        {
            songItemArrayList.add(songItem);
        }

        Gson gson = new Gson();
        String json = gson.toJson(songItemArrayList);
        sharedPreference.setPlaylist(json);

        Toast.makeText(this,"Song added to playlist",Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_APP_SETTINGS) {
            if(ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
            {
                downloadStart();
            } else {
                Toast.makeText(this, "Go to settings and enable storage", Toast.LENGTH_LONG).show();
            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.getItem(0).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id=item.getItemId();
        switch(id) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

            return super.onOptionsItemSelected(item);
    }




//-------------------------------------------------------ANDROID LIFECYCLE---------------------------------------------------------------------------------------------

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop()...");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart()...");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume()...");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause()...");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy()...");
        player.release();
    }
}