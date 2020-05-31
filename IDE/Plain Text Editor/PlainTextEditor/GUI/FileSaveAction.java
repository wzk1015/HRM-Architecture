package PlainTextEditor.gui;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

public class FileSaveAction {

    private FileSaveAction() {
    }

    public static boolean fileSave(Component component, MainFrame frame, String path) {
        if (path == null) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (fileChooser.showSaveDialog(component) == JFileChooser.APPROVE_OPTION) {
                File saveFile = fileChooser.getSelectedFile();
                if (saveFile.exists()) {
                    JDialog fileExistsDialog = new JDialog();
                    fileExistsDialog.setTitle("Confirm save as");
                    fileExistsDialog.setResizable(false);
                    fileExistsDialog.setSize(300, 100);
                    fileExistsDialog.setLocationRelativeTo(null);
                    fileExistsDialog.setLayout(new GridLayout(2, 1));
                    JLabel fileExistsMessage = new JLabel("File exists, overwrite it?");
                    fileExistsMessage.setHorizontalAlignment(JLabel.CENTER);
                    fileExistsMessage.setVerticalAlignment(JLabel.CENTER);
                    fileExistsDialog.add(fileExistsMessage);
                    JButton saveBtn = new JButton("Sure(S)");
                    JButton notBtn = new JButton("Not(N)");
                    AtomicBoolean ret = new AtomicBoolean(false);
                    saveBtn.addActionListener(ee -> {
                        try {
                            OutputStreamWriter writer = new OutputStreamWriter(
                                    new FileOutputStream(saveFile),
                                    StandardCharsets.UTF_8
                            );
                            writer.append(frame.getTextArea().getText());
                            writer.close();
                        } catch (IOException fileNotFoundException) {
                            fileNotFoundException.printStackTrace();
                        }
                        ret.set(true);
                        fileExistsDialog.dispose();
                    });
                    notBtn.addActionListener(ee -> {
                        ret.set(false);
                        fileExistsDialog.dispose();
                    });
                    JPanel lowerPnl = new JPanel();
                    lowerPnl.add(saveBtn);
                    lowerPnl.add(notBtn);
                    fileExistsDialog.add(lowerPnl);
                    fileExistsDialog.setModal(true);
                    fileExistsDialog.setVisible(true);
                    return ret.get();
                } else {
                    try {
                        OutputStreamWriter writer = new OutputStreamWriter(
                                new FileOutputStream(saveFile),
                                StandardCharsets.UTF_8
                        );
                        writer.append(frame.getTextArea().getText());
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            } else {
                return false;
            }
        } else {
            try {
                OutputStreamWriter writer = new OutputStreamWriter(
                        new FileOutputStream(path),
                        StandardCharsets.UTF_8
                );
                writer.append(frame.getTextArea().getText());
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
    }
}
