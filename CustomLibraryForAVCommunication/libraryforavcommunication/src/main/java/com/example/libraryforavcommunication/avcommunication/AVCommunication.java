package com.example.libraryforavcommunication.avcommunication;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.libraryforavcommunication.avcommunication.rangeseekbar.RangeSeekBar;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AVCommunication {

    private SimpleExoPlayer simpleExoPlayer = null;
    private PlayerView exoPlayerView, mPlayerView;
    private Context mContext;
    private Handler mHandler = new Handler();
    private TextView mCurrentTimeTextView, mPlayTimeDurationTextView;
    private long pauseTime = 0;
    private SeekBar mSeekbar;
    private Runnable mRunnable;

    private int preMin = -1;
    private int preMax = -1;
    private RangeSeekBar mRangeSeekbar;

    // TODO : just checking this , if we can use value from this library and fetch it in another library.
    private int videoID;

    public AVCommunication(Context context) {
        mContext = context;
    }

    public static long convertToMills(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    public PlayerView setVideoPlayer() {
        exoPlayerView = new PlayerView(mContext);
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, new DefaultTrackSelector());
        exoPlayerView.setPlayer(simpleExoPlayer);
        exoPlayerView.setVisibility(View.VISIBLE);
        //exoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE);
        exoPlayerView.setMinimumWidth(10);
        exoPlayerView.setMinimumHeight(1000);
        exoPlayerView.measure(200, 200);
        exoPlayerView.setLayoutParams(new PlayerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 600));
        exoPlayerView.setBackgroundColor(Color.BLACK);
        exoPlayerView.setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS);
        exoPlayerView.setRewindIncrementMs(1000);
        return exoPlayerView;
    }

    public SimpleExoPlayer makeStream(PlayerView playerView) {
        makeHandler();
        mHandler.postDelayed(mRunnable, 100);
        this.mPlayerView = playerView;
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, new DefaultTrackSelector());
        mPlayerView.setPlayer(simpleExoPlayer);

        DefaultDataSourceFactory defaultExtractorsFactory =
                new DefaultDataSourceFactory(mContext, Util.getUserAgent(mContext, "exo-demo"));
//        Uri uri = Uri.parse("https://bitmovin-a.akamaihd.net/content/playhouse-vr/mpds/105560.mpd");
//        Uri uriMp4 =
//                Uri.parse(
//                        "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4");
//        Uri uriHls =
//                Uri.parse(
//                        "https://devstreaming-cdn.apple.com/videos/streaming/examples/bipbop_4x3/bipbop_4x3_variant.m3u8");
//        Uri uriSs =
//                Uri.parse(
//                        "https://playready.directtaps.net/smoothstreaming/SSWSS720H264/SuperSpeedway_720.ism");

        String filename = "https://bitmovin-a.akamaihd.net/content/playhouse-vr/mpds/105560.mpd";
        String filenameArray[] = filename.split("\\.");
        String extension = filenameArray[filenameArray.length - 1];
        Uri videoUri = Uri.parse(filename);

        DashMediaSource dashMediaSource;
        HlsMediaSource hlsMediaSource;
        ExtractorMediaSource extractorMediaSource;
        SsMediaSource ssMediaSource = null;

        if (extension.equals("mp4")) {
            extractorMediaSource = new ExtractorMediaSource.Factory(defaultExtractorsFactory)
                    .createMediaSource(videoUri);
            simpleExoPlayer.prepare(extractorMediaSource);
        } else if (extension.equals("mpd")) {
            dashMediaSource = new
                    DashMediaSource.Factory(defaultExtractorsFactory).createMediaSource(videoUri);
            simpleExoPlayer.prepare(dashMediaSource);

        } else if (extension.equals("m3u8")) {
            hlsMediaSource = new
                    HlsMediaSource.Factory(defaultExtractorsFactory).createMediaSource(videoUri);
            simpleExoPlayer.prepare(hlsMediaSource);
        } else if (extension.equals("ism")) {
            ssMediaSource = new SsMediaSource.Factory(defaultExtractorsFactory).createMediaSource(videoUri);
            simpleExoPlayer.prepare(ssMediaSource);

        }

        simpleExoPlayer.setPlayWhenReady(true);


        return simpleExoPlayer;
    }

    public void play() {
        simpleExoPlayer.setPlayWhenReady(true);
    }

    public void pause() {
        simpleExoPlayer.setPlayWhenReady(false);
    }

    public void seekTo(long position) {
        simpleExoPlayer.seekTo(position);
    }

    public void replay() {
        simpleExoPlayer.seekTo(0);
    }



    public void playSegment(Long startTime, Long endTime) {

        pauseTime = endTime;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Long start = Long.valueOf(startTime);
                Long end = Long.valueOf(endTime);

                //  mRangeSeekbar.setRangeValues(0, (end - start));
                mRangeSeekbar.setSelectedMinValue(start);
                mRangeSeekbar.setSelectedMaxValue(end);


                simpleExoPlayer.seekTo((int) start.intValue() * 1000);
                pauseTime = end.intValue() * 1000;

            }
        }, 2000);


    }

    public void playSegmentSimple(SeekBar seekBar, int startTime, int endTime) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSeekbar = seekBar;


                seekBar.setProgress(startTime);
                simpleExoPlayer.seekTo(startTime * 1000);
                pauseTime = endTime;

                simpleExoPlayer.addListener(new Player.EventListener() {
                    @Override
                    public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

                    }

                    @Override
                    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

                    }

                    @Override
                    public void onLoadingChanged(boolean isLoading) {
                        mHandler.postDelayed(mRunnable, 100);
                        seekBar.setMax((int) (simpleExoPlayer.getDuration() / 1000));
                        seekBar.setProgress((int) (simpleExoPlayer.getCurrentPosition() / 1000));
                        Log.d("FUCK:", "" + simpleExoPlayer.getDuration() / 1000);

                    }

                    @Override
                    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                    }

                    @Override
                    public void onRepeatModeChanged(int repeatMode) {

                    }

                    @Override
                    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

                    }

                    @Override
                    public void onPlayerError(ExoPlaybackException error) {

                    }

                    @Override
                    public void onPositionDiscontinuity(int reason) {

                    }

                    @Override
                    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

                    }

                    @Override
                    public void onSeekProcessed() {

                    }
                });


                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        simpleExoPlayer.seekTo(seekBar.getProgress() * 1000);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        mHandler.postDelayed(mRunnable, 100);
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        simpleExoPlayer.seekTo(seekBar.getProgress() * 1000);

                    }
                });


//                mSeekbar.setProgress(Integer.parseInt(startTime));
//                simpleExoPlayer.seekTo((int) start.intValue() * 1000);
//                pauseTime = end.intValue() * 1000;

            }
        }, 2000);


    }

    public void destroyStream() {
        //simpleExoPlayer = null;
    }

    public void makeHandler() {
        mRunnable =
                new Runnable() {
                    @Override
                    public void run() {
                        displayCurrentTime();
                        displayVideoDuration();
                        mHandler.postDelayed(this, 100);

                        if (mSeekbar != null) {
                            mSeekbar.setProgress((int) (simpleExoPlayer.getCurrentPosition() / 1000));
                        }
                        //  Log.e("PauseTime", pauseTime + "");
                        Log.e("currentmills", simpleExoPlayer.getCurrentPosition() + "");
                        Log.e("currentmills1000", (simpleExoPlayer.getCurrentPosition() * 1000) + "");

                        if (pauseTime != 0) {

                            if (simpleExoPlayer.getCurrentPosition() > pauseTime * 1000) {
//                                simpleExoPlayer.setPlayWhenReady(false);
                                simpleExoPlayer.setPlayWhenReady(false);
                                pauseTime = 0;
                            }
                        }
                    }
                };
    }

    public void setTimeLabel(TextView currentTimeTextView, TextView playTimeDurationTextView) {
        mCurrentTimeTextView = currentTimeTextView;
        mPlayTimeDurationTextView = playTimeDurationTextView;
        makeHandler();
    }

    public void setSeekBar(SeekBar seekBar) {

        mSeekbar = seekBar;
        simpleExoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
                mHandler.postDelayed(mRunnable, 100);
                seekBar.setMax((int) (simpleExoPlayer.getDuration() / 1000));
                seekBar.setProgress((int) (simpleExoPlayer.getCurrentPosition() / 1000));
                Log.d("FUCK:", "" + simpleExoPlayer.getDuration() / 1000);

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                simpleExoPlayer.seekTo(seekBar.getProgress() * 1000);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.postDelayed(mRunnable, 100);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                simpleExoPlayer.seekTo(seekBar.getProgress() * 1000);

            }
        });
    }

    public void setRangeSeekbar(RangeSeekBar rangeSeekbar) {

        this.mRangeSeekbar = rangeSeekbar;
        mRangeSeekbar.setNotifyWhileDragging(true);

        //  mRangeSeekbar.setRangeValues(0, simpleExoPlayer.getDuration() /1000);

        mRangeSeekbar.setOnRangeSeekBarChangeListener(
                (bar, minValue, maxValue) ->
                {
                    simpleExoPlayer.seekTo((int) minValue * 1000);

                    int min = ((int) minValue);
                    int max = ((int) maxValue);

                    if (min != preMin) {
                        mRangeSeekbar.setSelectedMinValue(min);
                    } else if (max != preMax) {
                        mRangeSeekbar.setSelectedMaxValue(max);
                    } else {
                        preMin = min;
                        preMax = max;
                    }

                    Log.e("asdsadsad", "Min :" + minValue + " Max :" + maxValue);

                });

    }
    // TODO : just checking this , if we can use value from this library and fetch it in another library.
    public void setVideoID(int videoID)
    {
        this.videoID=videoID;
    }

    public int getVideoID()
    {
        return  videoID;
    }
    private void displayCurrentTime() {
        String formattedTime = null;
        formattedTime = formatTime((int) simpleExoPlayer.getCurrentPosition());
        if (mCurrentTimeTextView != null) {
            mCurrentTimeTextView.setText(formattedTime);
        }
    }

    private String formatTime(int millis) {
        int seconds = millis / 1000;
        int minutes = seconds / 60;
        int hours = minutes / 60;
        return (hours == 0 ? "" : hours + ":") + String.format("%02d:%02d", minutes % 60, seconds % 60);
    }

    private void displayVideoDuration() {
        String formattedTime = null;
        if (mRangeSeekbar != null) {
            mRangeSeekbar.setRangeValues(0, simpleExoPlayer.getDuration() / 1000);
        }
        formattedTime = formatTime((int) simpleExoPlayer.getDuration());
        if (mPlayTimeDurationTextView != null) {
            mPlayTimeDurationTextView.setText(formattedTime);
        }
    }


}
