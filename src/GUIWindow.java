import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GUIWindow extends JFrame {

    private MusicPlayer musicPlayer;

    private JFileChooser jFileChooser;

    private JLabel songTitle, songArtist;

    private JPanel playbackButtons;

    public GUIWindow(){
        super("MP3 Player"); // initialization and title of the window

        setSize(400,600); // sets resolution of the GUI window

        setDefaultCloseOperation(EXIT_ON_CLOSE); // stops program on window close

        setLocationRelativeTo(null); // opens window at the center of the screen

        setResizable(false); // prevent resizability

        setLayout(null); // null layout which allows more customization later
        getContentPane().setBackground(FRAME_COLOR);

        // Add an explorer menu to choose files
        jFileChooser = new JFileChooser();
        jFileChooser.setCurrentDirectory((new File("src/assets/Music")));
        jFileChooser.setFileFilter(new FileNameExtensionFilter("MP3","mp3"));
        musicPlayer = new MusicPlayer();
        setComponents();
    }
    public static final Color FRAME_COLOR = Color.BLACK;
    public static final Color TEXT_COLOR = Color.WHITE;
    private void setComponents(){
        setToolbar(); // load toolbar


        // Song Title

        songTitle = new JLabel("Song Title");
        songTitle.setBounds(0,285,getWidth() - 10, 30);
        songTitle.setFont(new Font("Rockwell",Font.BOLD,24));
        songTitle.setForeground(TEXT_COLOR);
        songTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(songTitle);

        //Song Artist

        songArtist = new JLabel("Artist");
        songArtist.setBounds(0,315,getWidth()-10,30);
        songArtist.setFont(new Font("Rockwell",Font.PLAIN,24));
        songArtist.setForeground(TEXT_COLOR);
        songArtist.setHorizontalAlignment(SwingConstants.CENTER);
        add(songArtist);

        //Playback Slider

        Icon icon = new ImageIcon("src/assets/record (2).png");
        UIManager.put("Slider.horizontalThumbIcon", icon);
        UIManager.put("Slider.tickColor", Color.ORANGE);
        JSlider playbackSlider = new JSlider(JSlider.HORIZONTAL,0,100,0);
        playbackSlider.setBounds(getWidth()/2 - 300/2,365,300,40);
        playbackSlider.setBackground(null); // no background
        playbackSlider.setOpaque(false); // make transparent
        add(playbackSlider);

        //Playback Buttons
        setPlaybackButtons();
        // Load image

        JLabel songImage = new JLabel(setImage("src/assets/Untitled design.png"));
        songImage.setBounds(0,0,getWidth(),getHeight());
        add(songImage);

    }

    private void setToolbar(){
        JToolBar toolbar = new JToolBar();
        JMenuBar menu = new JMenuBar();
        toolbar.add(menu);

        JMenu songMenu = new JMenu("Song"); // drop down menu for songs
        menu.add(songMenu);
        JMenuItem songLoad = new JMenuItem("Load Song");  // Load song option
        songLoad.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // integer value that indicates what action user performed
                int result = jFileChooser.showOpenDialog(GUIWindow.this);
                File selectedFile = jFileChooser.getSelectedFile();

                // check if user pressed open button on file explorer window
                if(result == JFileChooser.APPROVE_OPTION && selectedFile != null){
                    //create a song object for selected file
                    Song song = new Song (selectedFile.getPath());

                    musicPlayer.loadSong(song);
                    // update song title and artist
                    updateSongInfo(song);

                    //Toggle pause and play button
                    togglePauseButton();
                }
            }
        });
        songMenu.add(songLoad);

        JMenu playlistMenu = new JMenu("Playlist"); //drop down menu for playlists
        menu.add(playlistMenu);
        JMenuItem createPlaylist = new JMenuItem("Create a Playlist"); // create playlist option
        playlistMenu.add(createPlaylist);
        JMenuItem loadPlaylist = new JMenuItem("Load a Playlist"); // load playlist option
        playlistMenu.add(loadPlaylist);


        toolbar.setBounds(0,0,getWidth(),20); // configure toolbar
        toolbar.setFloatable(false); // prevent toolbar from being popped out

        toolbar.add(menu);

        add(toolbar);

    }

    private void setPlaybackButtons(){
    playbackButtons = new JPanel();
    playbackButtons.setBounds(0,435,getWidth()-10,80);
    playbackButtons.setBackground(null);

    //Previous Song
        JButton prevButton = new JButton(setImage("src/assets/rewind (1) (1).png"));
        prevButton.setBorderPainted(false);
        prevButton.setBackground(null);
        prevButton.setOpaque(false);
        playbackButtons.add(prevButton);

    // Play Button
        JButton playButton = new JButton(setImage("src/assets/play-button (1).png"));
        playButton.setBorderPainted(false);
        playButton.setBackground(null);
        playButton.setOpaque(false);
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //toggle play button and pause button
                togglePauseButton();

                musicPlayer.playCurrentSong();
            }
        });
        playbackButtons.add(playButton);

    // Pause Button
        JButton pauseButton = new JButton(setImage("src/assets/pause-button (2).png"));
        pauseButton.setBorderPainted(false);
        pauseButton.setBackground(null);
        pauseButton.setOpaque(false);
        pauseButton.setVisible(false);
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Toggle pause button and play button
                togglePlayButton();

                //Pause Song
                musicPlayer.pauseSong();
            }
        });
        playbackButtons.add(pauseButton);

    // Next Button
        JButton nextButton = new JButton(setImage("src/assets/fast-forward (1).png"));
        nextButton.setBorderPainted(false);
        nextButton.setBackground(null);
        nextButton.setOpaque(false);
        playbackButtons.add(nextButton);
    playbackButtons.setOpaque(false);
    add(playbackButtons);
    }

    private void updateSongInfo(Song song){
        songTitle.setText(song.getSongTitle());
        songArtist.setText(song.getSongArtist());
    }

    private void togglePauseButton(){
        JButton playButton = (JButton) playbackButtons.getComponent(1);
        JButton pauseButton = (JButton) playbackButtons.getComponent(2);

        playButton.setVisible(false);
        playButton.setEnabled(false);

        pauseButton.setVisible(true);
        pauseButton.setEnabled(true);
    }
    private void togglePlayButton(){
        JButton playButton = (JButton) playbackButtons.getComponent(1);
        JButton pauseButton = (JButton) playbackButtons.getComponent(2);

        playButton.setVisible(true);
        playButton.setEnabled(true);

        pauseButton.setVisible(false);
        pauseButton.setEnabled(false);
    }
    private ImageIcon setImage(String imagePath){
        try{
            BufferedImage image = ImageIO.read(new File(imagePath));
            return new ImageIcon(image);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return null;


    }




}
