import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

public class MusicPlayer extends PlaybackListener {
    private Song currentSong;

    // Using Jlayer to create an object to handle the music playback
    private AdvancedPlayer advancedPlayer;

    private boolean isPaused;

    private int currentFrame;
    public MusicPlayer(){



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
