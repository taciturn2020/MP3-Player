import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

public class MusicPlayer extends PlaybackListener {
    // Store song's details through song class
    private Song currentSong;
    public Song getCurrentSong(){
        return currentSong;
    }

    private static final Object playSignal = new Object(); // used to sync the update slider and playmusic threads

    // reference to update playback slider
    private GUIWindow guiWindow;

    // Using Jlayer to create an object to handle the music playback
    private AdvancedPlayer advancedPlayer;

    // boolean to check paused
    private boolean isPaused;

    // Stores the frame for playback pausing / resuming
    private int currentFrame;

    public void setCurrentFrame(int frame){
        currentFrame = frame;
    }

    // Storing milliseconds passed since playback started to update slider

    private int currentTimeInMS;
    public void setCurrentTimeInMS(int timeinMS){
        currentTimeInMS = timeinMS;
    }
    public MusicPlayer(GUIWindow guiWindow){
        this.guiWindow = guiWindow;


    }
    public void loadSong(Song song){
        currentSong = song;

        if(currentSong != null){
            playCurrentSong();
        }


    }
    public void stopSong(){
        if(advancedPlayer != null){
            advancedPlayer.stop();
            advancedPlayer.close();
            advancedPlayer = null;
        }

    }

    public void pauseSong(){
        if(advancedPlayer != null){
            isPaused = true;
            stopSong();
        }

    }

    public void playCurrentSong(){
        if (currentSong == null)
            return;
        try{
            // Load MP3 audio data
            FileInputStream fileInputStream = new FileInputStream(currentSong.getFilePath());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

            // create advanced player object
            advancedPlayer = new AdvancedPlayer(bufferedInputStream);
            advancedPlayer.setPlayBackListener(this);
            startMusic();
            startPlayBackSlider();
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private void startMusic(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    if (isPaused){
                        synchronized (playSignal){
                            isPaused = false;

                            playSignal.notify();
                        }
                        // Resume at last paused location
                        advancedPlayer.play(currentFrame, Integer.MAX_VALUE);

                    }
                    else {
                        // start playing music
                        advancedPlayer.play();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void startPlayBackSlider(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(isPaused){
                    try{
                        // wait until other thread signals to continue
                        synchronized (playSignal){
                            playSignal.wait();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                while(!isPaused){
                    try {
                        currentTimeInMS++;
                        // convert time for Milliseconds to frames
                        int calculcatedFrame = (int) ((double) currentTimeInMS * 2.08 * currentSong.getFrameRatePerMS());

                        //update slider
                        guiWindow.setPlaybackSliderValue(calculcatedFrame);

                        //Make sure each iteration is 1 millsecond
                        Thread.sleep(1);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void playbackStarted(PlaybackEvent evt) {
        // starting of song
        super.playbackStarted(evt);
        System.out.println("playBack Started");
    }

    @Override
    public void playbackFinished(PlaybackEvent evt) {
        // Song finished or paused or player closed
        super.playbackFinished(evt);
        System.out.println("playback finished");
        System.out.println("Stopped @" + evt.getFrame());
        if (isPaused == true)
        // converting frame values to millisecond value
            currentFrame += (int) ((double)evt.getFrame() * currentSong.getFrameRatePerMS());
    }
}
