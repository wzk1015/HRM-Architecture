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
            case "Exit":
                mainFrame.dispose();
                break;
            case "Font":
                new FontSettingsDialog(textArea.getFont(), textArea.getForeground(), mainFrame);
                break;
            case "New":
                break;
            case "New Window":
                new Thread(MainFrame::new).start();
                break;
            case "Open":
                if (mainFrame.getUnsavedStatus()) {
                    int result = FileSaveDialog.querySave(mainFrame, null);
                    switch (result) {
                        default:
                        case FileSaveDialog.FSD_CANCEL:
                            break;
                        case FileSaveDialog.FSD_NOT:
                        case FileSaveDialog.FSD_SAVE:
                            mainFrame.openFile();
                    }
                } else {
                    mainFrame.openFile();
                }
                break;
            case "Save":
                break;
            case "Save as":
                break;
            default:
                break;
        }
    }

    public void register(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }
}
