package net.deadlights.project1;

import android.app.Fragment;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.media.AudioManager;

import com.squareup.picasso.Picasso;


public class PlayTrackFragment extends Fragment implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener
{
    public final static String TRACK = "TRACK";
    public final static String ARTIST = "ARTIST";
    private ArtistTrack _track;
    private SearchResult _artist;
    private TextView _tvArtistName;
    private TextView _tvAlbumTitle;
    private TextView _tvTrackTitle;
    private TextView _tvDuration;
    private TextView _tvPosition;
    private ImageView _ivAlbumArt;
    private Button _btnPlay;
    private Button _btnNext;
    private Button _btnBack;
    private SeekBar _seekBar;
    android.os.Handler _handler = new android.os.Handler();
    MediaPlayer _mediaPlayer;
    private Boolean _isPlaying = false;
    private Boolean _isPrepared = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.frag_play_track, container, false);
        Bundle extras = getArguments();
        if (extras != null)
        {
            _track = ArtistTrack.fromJsonString(extras.getString(TRACK));
            _artist = SearchResult.fromJsonString(extras.getString(ARTIST));
        }
        else if (savedInstanceState != null)
        {
            _track = ArtistTrack.fromJsonString(savedInstanceState.getString(TRACK));
            _artist = SearchResult.fromJsonString(savedInstanceState.getString(ARTIST));
        }

        if (!(_track == null || _artist == null)) {
            _tvAlbumTitle = (TextView) v.findViewById(R.id.tvAlbumTitle);
            _tvAlbumTitle.setText(_track.getAlbumName());

            _tvArtistName = (TextView) v.findViewById(R.id.tvArtistName);
            _tvArtistName.setText(_artist.getArtistName());

            _tvTrackTitle = (TextView)v.findViewById(R.id.tvTrackTitle);
            _tvTrackTitle.setText(_track.getTrackName());

            _tvDuration = (TextView)v.findViewById(R.id.tvDuration);

            _tvPosition = (TextView)v.findViewById(R.id.tvCurrentPosition);

            _ivAlbumArt = (ImageView)v.findViewById(R.id.ivAlbumArt);
            if (_track.getLargest() != null && _track.getLargest().length() > 0) {
                Picasso.with(getActivity()).load(_track.getLargest()).into(_ivAlbumArt);
            }

            _btnPlay = (Button)v.findViewById(R.id.btnPlay);
            _btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _handler.removeCallbacksAndMessages(null);
                    playPauseTrack();
                }
            });

            _btnNext = (Button)v.findViewById(R.id.btnForward);
            _btnBack = (Button)v.findViewById(R.id.btnBack);


            _seekBar = (SeekBar)v.findViewById(R.id.sbSeek);
            _seekBar.setEnabled(false);
            _seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        int milliseconds = progress * 1000;
                        _mediaPlayer.seekTo(milliseconds);
                        updateCurrentPosition(milliseconds);
                        Log.d(this.getClass().getSimpleName(), "Current Progress: " + progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            _handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    initTrack();
                }
            }, 1000);
        }


        return v;
    }

    private void initTrack() {
        _mediaPlayer = new MediaPlayer();
        _mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            _mediaPlayer.setDataSource(_track.getUrl());
            _mediaPlayer.setOnPreparedListener(this);
            _mediaPlayer.setOnCompletionListener(this);
            _mediaPlayer.prepareAsync();

        } catch (java.io.IOException e) {
            Log.e(this.getClass().getSimpleName(), "Could not play track.");
        }
    }

    private void playPauseTrack()
    {
        if (!_isPrepared)
        {
            initTrack();
        }
        _handler.removeCallbacksAndMessages(null);
        if (_mediaPlayer.isPlaying()) {
            _mediaPlayer.pause();
            setPlayButtonState(false);
        }
        else
        {
            _mediaPlayer.start();
            setPlayButtonState(true);
            updateCurrentPosition(0);
        }
    }

    private void updateCurrentPosition(long milliseconds) {
        if (_mediaPlayer.isPlaying()) {
            milliseconds = _mediaPlayer.getCurrentPosition();
        }

        _tvPosition.setText(Utility.getFormattedTime(milliseconds));
        _seekBar.setProgress((int)milliseconds / 1000);

        _handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateCurrentPosition(0);
            }
        }, 1000);
    }

    private void setPlayButtonState(Boolean isPlaying)
    {
        _btnPlay.setText(isPlaying ? "||" : ">");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        _isPrepared = true;
        _seekBar.setEnabled(true);
        _seekBar.setMax(_mediaPlayer.getDuration() / 1000);
        _tvDuration.setText(Utility.getFormattedTime(_mediaPlayer.getDuration()));
        playPauseTrack();
    }

    @Override
    public void onPause() {
        super.onPause();
        _handler.removeCallbacksAndMessages(null);
        _mediaPlayer.stop();
        _mediaPlayer.release();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        _handler.removeCallbacksAndMessages(null);
        _handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getFragmentManager().popBackStack();
            }
        }, 750);
    }
}
