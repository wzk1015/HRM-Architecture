package PlainTextEditor.gui;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

public class FileSaveDialog extends JDialog {

    public static final int FSD_SAVE = 0;
    public static final int FSD_NOT = 1;
    public static final int FSD_CANCEL = 2;

    public FileSaveDialog() {
        setupUI();
    }

    private void setupUI() {
        this.setTitle("Plain Text Editor");
        this.setResizable(false);
        this.setSize(300, 100);
        this.setLocationRelativeTo(null);
        this.setLayout(new FlowLayout());
        JLabel messageLabel = new JLabel("Changes will lose unless you save, save or not?");
        this.add(messageLabel);
        JButton saveButton = new JButton("Save(S)");
        JButton notButton = new JButton("Not(N)");
        JButton cancelButton = new JButton("Cancel");
        this.add(saveButton);
        this.add(notButton);
        this.add(cancelButton);
        this.setModal(true);
        this.setVisible(true);
    }

    public static int querySave(MainFrame frame, String path) {
        AtomicInteger ret = new AtomicInteger(-1);
        JDialog dialog = new JDialog();
        dialog.setTitle("Plain Text Editor");
        dialog.setResizable(false);
        dialog.setSize(300, 100);
        dialog.setLocationRelativeTo(null);
        dialog.setLayout(new GridLayout(2, 1));
        JLabel messageLabel = new JLabel("Changes will lose unless you save, save or not?");
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        messageLabel.setVerticalAlignment(JLabel.CENTER);
        dialog.add(messageLabel);
        JPanel lowerPanel = new JPanel();
        JButton saveButton = new JButton("Save(S)");
        JButton notButton = new JButton("Not(N)");
        JButton cancelButton = new JButton("Cancel");
        saveButton.addActionListener(e -> {
            ret.set(FSD_SAVE);
            FileSaveAction.fileSave(dialog, frame, path);
        });
        notButton.addActionListener(e -> {
            ret.set(FSD_NOT);
            dialog.dispose();
        });
        cancelButton.addActionListener(e -> {
            ret.set(FSD_CANCEL);
            dialog.dispose();
        });
        lowerPanel.add(saveButton);
        lowerPanel.add(notButton);
        lowerPanel.add(cancelButton);
        dialog.add(lowerPanel);
        dialog.setModal(true);
        dialog.setVisible(true);
        return ret.get();
    }
}
