import PlainTextEditor.gui.MainFrame;

public class Launcher {

    public static void main(String[] args) {
        new Thread(MainFrame::new).start();
    }
}
