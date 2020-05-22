package PlainTextEditor.GUI;

import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Scanner;

public class LayoutFactory {

    public static JPanel createHTMLJPanel(String filename) {
        JPanel panel = new JPanel();
        JScrollPane container;
        JEditorPane pane;
        try {
            InputStream input = new FileInputStream(filename);
            Scanner scanner = new Scanner(input);
            StringBuilder sb = new StringBuilder();
            while (scanner.hasNextLine()) {
                sb.append(scanner.nextLine()).append("\n");
            }
            input.close();
            scanner.close();
            pane = new JEditorPane("text/html", sb.toString());
            pane.setEditable(false);
            pane.setCaretPosition(0);
            container = new JScrollPane(pane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        } catch (Exception e) {
            container = new JScrollPane(
                    new JLabel("Error (" + e + "): " + filename + " contents couldn't be loaded.")
            );
        }
        panel.setLayout(new GridLayout(1, 1));
        panel.add(container);
        return panel;
    }
}
