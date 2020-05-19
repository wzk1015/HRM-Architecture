package PlainTextEditor.GUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuActionListener implements ActionListener {

    private MainFrame mainFrame;

    @Override
    public void actionPerformed(ActionEvent e) {
        if (mainFrame == null) {
            return;
        }
        JTextArea textArea = mainFrame.getTextArea();
        switch (e.getActionCommand()) {
            case "About":
                new AboutDialog();
                break;
            case "Auto Wrap":
                JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
                boolean checked = item.getState();
                textArea.setLineWrap(checked);
                textArea.setWrapStyleWord(checked);
                break;
            case "New Window":
                new Thread(MainFrame::new).start();
                break;
            case "Exit":
                mainFrame.dispose();
                break;
            case "Font":
                new FontSettingsDialog(textArea.getFont(), textArea.getForeground(), mainFrame);
                break;
            default:
                break;
        }
    }

    public void register(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }
}
