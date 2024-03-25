import javax.swing.*;

public class GUIWindow extends JFrame {
    public GUIWindow(){
        super("MP3 Player");
        setSize(400,600); // sets resolution of the GUI window
        setDefaultCloseOperation(EXIT_ON_CLOSE); // stops program on window close
        setLocationRelativeTo(null); // opens window at the center of the screen
        setResizable(false);
        setLayout(null);
    }

}
