import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class PlayerDialogBox extends JDialog {
    private GUIWindow guiWindow;
    private ArrayList<String> songPaths; // store the path of all imported songs
    public PlayerDialogBox(GUIWindow guiWindow){
        this.guiWindow = guiWindow;
        songPaths = new ArrayList<>();
        setTitle("Create Playlist");
        setSize(400,400);
        setResizable(false);
        getContentPane().setBackground(GUIWindow.FRAME_COLOR);
        setLayout(null);
        setModal(true); // The dialog is given priority in focus.
        setLocationRelativeTo(guiWindow);


        addDialogComponents();

    }

    private void addDialogComponents(){

        JPanel songContainer = new JPanel();
        songContainer.setLayout(new BoxLayout(songContainer,BoxLayout.Y_AXIS));
        songContainer.setBounds((int)(getWidth() * 0.025),10,(int)(getWidth()*0.90), (int)(getHeight() * 0.75));
        add(songContainer);

        // Button to add a song
        JButton addSongButton = new JButton("Add");
        addSongButton.setBounds(60,(int)(getHeight() * 0.80),100,25);
        addSongButton.setFont(new Font("Rockwell", Font.BOLD,14));
        add(addSongButton);
        addSongButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open Explorer
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setFileFilter(new FileNameExtensionFilter("MP3","mp3"));
                jFileChooser.setCurrentDirectory(new File("src/assets/Music"));
                int result = jFileChooser.showOpenDialog(PlayerDialogBox.this);

                File selectedFile = jFileChooser.getSelectedFile();
                if(result == JFileChooser.APPROVE_OPTION && selectedFile != null){
                    JLabel pathLabel = new JLabel(selectedFile.getPath());
                    pathLabel.setFont(new Font("Rockwell", Font.BOLD,12));
                    pathLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                    // add to the list
                    songPaths.add(pathLabel.getText()); // add song path to an ArrayList

                    songContainer.add(pathLabel); // add to Dialog Box

                    songContainer.revalidate(); // refresh
                }

            }
        });

        // Save Playlist

        JButton savePlaylist = new JButton("Save");
        savePlaylist.setBounds(215,(int)(getHeight() * 0.80),100,25);
        savePlaylist.setFont(new Font("Rockwell", Font.BOLD,14));
        savePlaylist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    JFileChooser jFileChooser = new JFileChooser();
                    jFileChooser.setCurrentDirectory(new File("src/assets/PlaylistFiles"));

                    int result = jFileChooser.showSaveDialog(PlayerDialogBox.this);
                    if(result == JFileChooser.APPROVE_OPTION){
                        File selectedFile = jFileChooser.getSelectedFile(); // grab Reference to the file that will be saved
                        // Check if file is in .txt form if not then convert it
                        if (!selectedFile.getName().substring(selectedFile.getName().length()-4).equalsIgnoreCase(".txt")){
                            selectedFile = new File(selectedFile.getAbsoluteFile() + ".txt");

                        }


                        selectedFile.createNewFile();
                        FileWriter fileWriter = new FileWriter(selectedFile);
                        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                        for(String songPath: songPaths){
                            bufferedWriter.write(songPath + "\n");
                        }
                        bufferedWriter.close();

                        JOptionPane.showMessageDialog(PlayerDialogBox.this,"Succesfully Created a Playlist!"); // Confirms playlist is saved

                        PlayerDialogBox.this.dispose(); // Closes dialog box after playlist is saved
                    }


                }catch (Exception excep){
                    excep.printStackTrace();
                }




            }
        });
        add(savePlaylist);
    }
}
