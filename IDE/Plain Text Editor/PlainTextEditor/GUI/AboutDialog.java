package PlainTextEditor.gui;

import PlainTextEditor.Globals;

import javax.swing.JDialog;
import java.awt.GridLayout;

public class AboutDialog extends JDialog {

    public AboutDialog() {
        this.setLayout(new GridLayout(1, 1));
        this.setSize(500, 300);
        this.setTitle("About\"Plain Text Editor\"");
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.add(LayoutFactory.createHTMLJPanel("src/" + Globals.helpPath + "About.html"));
        this.setModal(true);
        this.setVisible(true);
    }
}
