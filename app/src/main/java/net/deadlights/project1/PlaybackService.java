package net.deadlights.project1;

import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;



public class PlaybackService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private MediaPlayer _mediaPlayer;
    private ArtistTrack _track;
    private android.os.Handler _handler = new android.os.Handler();

    public static final String ACTION_START = "net.deadlights.project1.action.START";
    public static final String ACTION_PLAY = "net.deadlights.project1.action.PLAY";
    public static final String ACTION_PAUSE = "net.deadlights.project1.action.PAUSE";
    public static final String ACTION_STOP = "net.deadlights.project1.action.STOP";

    @Override
    public void onCreate() {
        super.onCreate();
        _mediaPlayer = new MediaPlayer();
        _mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    public static final String ACTION_SEEK = "net.deadlights.project1.action.SEEK";
    public static final String ACTION_FINISHED = "net.deadlights.project1.action.FINISHED";
    public static final String ACTION_PREPARED = "net.deadlights.project1.action.PREPARED";

    public static final String PARAM_TRACK = "net.deadlights.project1.extra.TRACK";
    public static final String PARAM_POSITION = "net.deadlights.project1.extra.POSITION";
    public static final String PARAM_DURATION = "net.deadlights.project1.extra.DURATION";

    public static void startActionSTART(Context context, ArtistTrack artistTrack) {
        Intent intent = new Intent(context, PlaybackService.class);
        intent.setAction(ACTION_START);
        intent.putExtra(PARAM_TRACK, artistTrack.toJsonString());
        context.startService(intent);
        Log.d("startAction", "Action start");
    }

    public static void startActionPAUSE(Context context) {
        Intent intent = new Intent(context, PlaybackService.class);
        intent.setAction(ACTION_PAUSE);
        context.startService(intent);
        Log.d("startAction", "Action pause");
    }

    public static void startActionPLAY(Context context) {
        Intent intent = new Intent(context, PlaybackService.class);
        intent.setAction(ACTION_PLAY);
        context.startService(intent);
        Log.d("startAction", "Action play");
    }

    public static void startActionSTOP(Context context) {
        Intent intent = new Intent(context, PlaybackService.class);
        intent.setAction(ACTION_STOP);
        context.startService(intent);
        Log.d("startAction", "Action stop");
    }

    public static void startActionSEEK(Context context, int position) {
        Intent intent = new Intent(context, PlaybackService.class);
        intent.putExtra(PARAM_POSITION, position);
        intent.setAction(ACTION_SEEK);
        context.startService(intent);
        Log.d("startAction", "Action seek");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        if (intent != null) {
            final String action = intent.getAction();
            Log.d(this.getClass().getSimpleName(), "Action: " + action);
            if (ACTION_START.equals(action)) {
                final ArtistTrack track = ArtistTrack.fromJsonString(intent.getStringExtra(PARAM_TRACK));
                handleActionStart(track);
            } else if (ACTION_PAUSE.equals(action)) {
                handleActionPause();
            } else if (ACTION_PLAY.equals(action)) {
                handleActionPlay();
            } else if (ACTION_STOP.equals(action)) {
                handleActionStop();
            } else if (ACTION_SEEK.equals(action)) {
                int seekTo = intent.getIntExtra(PARAM_POSITION, 0);
                handleActionSeek(seekTo);
            }
        }
    }

    private void handleActionStart(ArtistTrack track) {
        _track = track;
        if (_mediaPlayer != null)
        {
            _mediaPlayer.stop();
            _mediaPlayer.reset();
        }
        try {
            _mediaPlayer.setDataSource(_track.getUrl());
            _mediaPlayer.setOnPreparedListener(this);
            _mediaPlayer.setOnCompletionListener(this);
            _mediaPlayer.setOnErrorListener(this);
            _mediaPlayer.prepareAsync();
        }
        catch(java.io.IOException e)
        {
            Log.e(this.getClass().getSimpleName(), "Could not initialize track.");
            _track = null;
            _mediaPlayer.stop();
            _mediaPlayer.reset();
            _mediaPlayer = null;
        }
        Intent i = new Intent();
        i.setAction(ACTION_START);
        sendBroadcast(i);
        Log.d("handleAction", "Action start");
    }

    private void handleActionPlay()
    {
        if (_mediaPlayer != null && !_mediaPlayer.isPlaying())
        {
            _mediaPlayer.start();
            updateCurrentPosition(0);
        }
        Log.d("handleAction", "Action play");
    }

    private void handleActionPause()
    {
        if (_mediaPlayer != null && _mediaPlayer.isPlaying())
        {
            _mediaPlayer.pause();
        }
        Log.d("handleAction", "Action pause");
    }

    private void handleActionStop()
    {
        if (_mediaPlayer != null)
        {
            onCompletion(_mediaPlayer);
        }
        Log.d("handleAction", "Action stop");
    }

    private void handleActionSeek(int seekTo)
    {
        if (_mediaPlayer != null)
        {
            _mediaPlayer.seekTo(seekTo);
            updateCurrentPosition(seekTo);
        }
        Log.d("handleAction", "Action seek");
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Intent i = new Intent(ACTION_FINISHED);
        sendBroadcast(i);
        this.stopSelf();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Intent i = new Intent(ACTION_PREPARED);
        i.putExtra(PARAM_DURATION, _mediaPlayer.getDuration());
        sendBroadcast(i);
        _mediaPlayer.start();
    }

    private void updateCurrentPosition(long milliseconds) {
        if (_mediaPlayer.isPlaying()) {
            milliseconds = _mediaPlayer.getCurrentPosition();
        }
        Intent i = new Intent(ACTION_SEEK);
        i.putExtra(PARAM_POSITION, milliseconds);
        sendBroadcast(i);
        _handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateCurrentPosition(0);
            }
        }, 1000);
        Log.d("handleAction", "Update Position");
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra)
    {
        return false;
    }
}
