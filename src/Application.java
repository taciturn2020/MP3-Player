import javax.swing.*;

public class Application {
    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GUIWindow().setVisible(true);

              // Song song = new Song("src/assets/Wind Riders - Asher Fulero.mp3");
              // System.out.println(song.getSongTitle());
              // System.out.println(song.getSongArtist());

            }
        });



    }
}
