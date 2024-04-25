import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.*;
import java.util.ArrayList;

public class MusicPlayer extends PlaybackListener {
    // Store song's details through song class
    private Song currentSong;
    public Song getCurrentSong(){
        return currentSong;
    }

    private ArrayList<Song> playlist;

    private int currentPlaylistIndex;

    private static final Object playSignal = new Object(); // used to sync the update slider and playmusic threads

    // reference to update playback slider
    private GUIWindow guiWindow;

    // Using Jlayer to create an object to handle the music playback
    private AdvancedPlayer advancedPlayer;

    // boolean to check paused
    private boolean isPaused;

    private boolean songFinished;
    private boolean pressedNext, pressedPrev;

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

        playlist = null;

        if (!songFinished)
            stopSong();

        if(currentSong != null){
            playCurrentSong();
            currentFrame = 0;
            currentTimeInMS = 0;
            guiWindow.setPlaybackSliderValue(0);
        }


    }

    public void loadPlaylist(File playlistFile){
        playlist = new ArrayList<>();

        try{
            FileReader fileReader = new FileReader(playlistFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String songPath = new String(); // Read and store each line from the text file into this variable
            while((songPath = bufferedReader.readLine()) != null){
                // Create a song object based on the read file path
                Song song = new Song(songPath);

                playlist.add(song);

            }
        }catch (Exception e){
            e.printStackTrace();
        }

        if (playlist.size() > 0){
            guiWindow.setPlaybackSliderValue(0); // reset slider
            currentTimeInMS = 0;

            currentSong = playlist.get(0); // update song to the start of the playlist

            currentFrame = 0; // Start from the beginning

            guiWindow.togglePauseButton();
            guiWindow.updateSongInfo(currentSong);
            guiWindow.updatePlayBackSlider(currentSong);


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
    public void nextSong(){
        if(playlist == null)
            return; // return null if no song left in playlist

        // make sure there is a song in playlist to go next to
        if (currentPlaylistIndex + 1 > playlist.size() - 1)
            return;

        pressedNext = true;


        if (!songFinished)
            stopSong();

        currentPlaylistIndex++;

        currentSong = playlist.get(currentPlaylistIndex);

        // set frame and ms count to 0
        currentFrame = 0;
        currentTimeInMS = 0;

        //update song info
        guiWindow.togglePauseButton();
        guiWindow.updateSongInfo(currentSong);
        guiWindow.updatePlayBackSlider(currentSong);

        playCurrentSong();

    }

    public void prevSong(){
        if(playlist == null) return; // return null if no song left in playlist

        // make sure there is a song in playlist to go back to
        if (currentPlaylistIndex - 1 < 0)
            return;

        pressedPrev = true;


        if (!songFinished)
            stopSong();

        currentPlaylistIndex--;

        currentSong = playlist.get(currentPlaylistIndex);

        // set frame and ms count to 0
        currentFrame = 0;
        currentTimeInMS = 0;

        //update song info
        guiWindow.togglePauseButton();
        guiWindow.updateSongInfo(currentSong);
        guiWindow.updatePlayBackSlider(currentSong);

        playCurrentSong();
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
                while(!isPaused && !songFinished && !pressedNext && !pressedPrev){
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
        songFinished = false;
        pressedNext = false;
        pressedPrev = false;
    }

    @Override
    public void playbackFinished(PlaybackEvent evt) {
        // Song finished or paused or player closed
        super.playbackFinished(evt);
        System.out.println("playback finished");
        System.out.println("Stopped @" + evt.getFrame());
        if (isPaused == true) {
            // converting frame values to millisecond value
            currentFrame += (int) ((double) evt.getFrame() * currentSong.getFrameRatePerMS());
        }
        else{
            // if user pressed next or prev button, return null to avoid double calling nextSong function
            if (pressedNext || pressedPrev)
                return;

            // Song end
            songFinished = true;


            if (playlist == null)
                guiWindow.togglePlayButton();
            else{
                if (currentPlaylistIndex == playlist.size() - 1){
                    // stop playback
                    guiWindow.togglePlayButton();
                }
                else{
                    // go to next song
                    nextSong();
                }
            }

        }
    }
}
