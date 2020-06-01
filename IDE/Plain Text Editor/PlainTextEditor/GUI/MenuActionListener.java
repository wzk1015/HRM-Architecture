package PlainTextEditor.gui;

import PlainTextEditor.util.ClipboardManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

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
            case "Copy":
                ClipboardManager.setSystemClipboardText(textArea.getSelectedText());
                break;
            case "Cut":
                ClipboardManager.setSystemClipboardText(textArea.getSelectedText());
            case "Delete":
                int start = textArea.getSelectionStart();
                int end = textArea.getSelectionEnd();
                String text = textArea.getText();
                textArea.setText(text.substring(0, start) + text.substring(end));
                break;
            case "Exit":
                mainFrame.onWindowClosing();
                break;
            case "Find":
                mainFrame.find();
                break;
            case "Find Last":
                mainFrame.findLast();
                break;
            case "Find Next":
                mainFrame.findNext();
                break;
            case "Font":
                new FontSettingsDialog(textArea.getFont(), textArea.getForeground(), mainFrame);
                break;
            case "New":
                mainFrame.newFile();
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
            case "Paste":
                textArea.append(ClipboardManager.getSystemClipboardText());
                break;
            case "Replace":
                mainFrame.replace();
                break;
            case "Save":
                mainFrame.saveFile();
                break;
            case "Save as":
                mainFrame.saveFileAs();
                break;
            case "Select All":
                textArea.selectAll();
                break;
            case "Show Status Bar":
                JCheckBoxMenuItem statusMenuItem = (JCheckBoxMenuItem) e.getSource();
                boolean checkedFlag = statusMenuItem.getState();
                mainFrame.setStatusBarVisible(checkedFlag);
                break;
            case "Time and Date":
                SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss yyyy/MM/dd");
                textArea.append(format.format(new Date()));
                break;
            case "Undo":
                break;
            case "Zoom in":
                mainFrame.zoomIn();
                break;
            case "Zoom out":
                mainFrame.zoomOut();
                break;
            case "Reset":
                mainFrame.zoomReset();
                break;
            default:
                break;
        }
    }

    public void register(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }
}
