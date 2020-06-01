package PlainTextEditor.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Objects;

public class ReplaceDialog extends JDialog {

    private String findWhat;
    private String replaceWith;
    private boolean caseSensitive;
    private boolean loop;

    private JTextField input;
    private JTextField replaceInput;
    private JCheckBox caseSensitiveBox;
    private JCheckBox loopBox;
    private final MainFrame frame;
    private final JTextArea textArea;

    public ReplaceDialog(MainFrame mainFrame) {
        this.caseSensitive = true;
        this.loop = false;
        this.frame = mainFrame;
        this.textArea = mainFrame.getTextArea();
        String selected = textArea.getSelectedText();
        this.findWhat = Objects.requireNonNullElse(selected, " ");
        this.replaceWith = "";
        setupUI();
    }

    private void setupUI() {
        this.setTitle("Replace");
        this.setResizable(false);
        this.setSize(450, 190);
        this.setLayout(new BorderLayout());
        this.setLocationRelativeTo(null);
        JPanel displayPanel = new JPanel();
        displayPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel();
        JLabel inputLabel = new JLabel("Find What: ");
        input = new JTextField(30);
        JLabel replaceLabel = new JLabel("Replace With: ");
        replaceInput = new JTextField(30);
        JPanel labelsPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        JPanel fieldsPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        labelsPanel.add(inputLabel);
        labelsPanel.add(replaceLabel);
        fieldsPanel.add(input);
        fieldsPanel.add(replaceInput);
        inputPanel.add(labelsPanel);
        inputPanel.add(fieldsPanel);

        JPanel optionPanel = new JPanel();
        caseSensitiveBox = new JCheckBox("Case Sensitive");
        loopBox = new JCheckBox("Loop");
        optionPanel.add(caseSensitiveBox);
        optionPanel.add(loopBox);

        JPanel controlPanel = new JPanel();
        JButton findNext = new JButton("Find Next");
        JButton replace = new JButton("Replace");
        JButton replaceAll = new JButton("Replace All");
        JButton cancel = new JButton("Cancel");
        findNext.addActionListener(e -> findNext());
        replace.addActionListener(e -> replace());
        replaceAll.addActionListener(e -> replaceAll());
        cancel.addActionListener(e -> dispose());
        controlPanel.add(findNext);
        controlPanel.add(replace);
        controlPanel.add(replaceAll);
        controlPanel.add(cancel);

        displayPanel.add(inputPanel, BorderLayout.NORTH);
        displayPanel.add(optionPanel);
        displayPanel.add(controlPanel, BorderLayout.SOUTH);

        update();
        this.add(displayPanel);
        this.setModal(false);
    }

    private void update() {
        this.input.setText(findWhat);
        this.replaceInput.setText(replaceWith);
        this.caseSensitiveBox.setSelected(caseSensitive);
        this.loopBox.setSelected(loop);
    }

    private void findNext() {
        findWhat = input.getText();
        replaceWith = replaceInput.getText();
        caseSensitive = caseSensitiveBox.isSelected();
        loop = loopBox.isSelected();
        FindAction.findNext(frame, findWhat, caseSensitive, loop);
    }

    private void replace() {
        findWhat = input.getText();
        replaceWith = replaceInput.getText();
        caseSensitive = caseSensitiveBox.isSelected();
        loop = loopBox.isSelected();
        //todo
        new PopupAlertDialog("not implemented!");
    }

    private void replaceAll() {
        findWhat = input.getText();
        replaceWith = replaceInput.getText();
        caseSensitive = caseSensitiveBox.isSelected();
        loop = loopBox.isSelected();
        //todo
        new PopupAlertDialog("not implemented!");
    }

    public void callUp() {
        if (this.isVisible()) {
            this.setVisible(false);
        }
        String selected = textArea.getSelectedText();
        if (selected != null) {
            this.findWhat = selected;
        }
        this.update();
        this.input.selectAll();
        this.setVisible(true);
    }
}
