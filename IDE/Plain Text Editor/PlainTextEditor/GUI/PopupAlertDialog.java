package PlainTextEditor.gui;

import javax.swing.*;
import java.awt.*;

public class PopupAlertDialog extends JDialog {

    public PopupAlertDialog(String message) {
        JLabel container = new JLabel(message);
        container.setHorizontalAlignment(SwingConstants.CENTER);
        container.setVerticalAlignment(SwingConstants.CENTER);
        this.add(container);
        this.setTitle("Alert");
        this.setLocationRelativeTo(null);
        this.setLayout(new GridLayout(1, 1));
        this.setSize(400, 150);
        this.setModal(true);
        this.setVisible(true);
    }
}
