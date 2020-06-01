package PlainTextEditor.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Objects;

public class FindDialog extends JDialog {

    private String findWhat;
    private boolean caseSensitive;
    private boolean loop;

    private enum SEARCH_DIRECTION {
        UPWARDS,
        DOWNWARDS
    }

    private SEARCH_DIRECTION direction;

    private JTextField input;
    private JCheckBox caseSensitiveBox;
    private JCheckBox loopBox;
    private JRadioButton upwards;
    private JRadioButton downwards;
    private final MainFrame frame;
    private final JTextArea textArea;

    public FindDialog(MainFrame mainFrame) {
        this.caseSensitive = true;
        this.loop = false;
        this.direction = SEARCH_DIRECTION.DOWNWARDS;
        this.frame = mainFrame;
        this.textArea = mainFrame.getTextArea();
        String selected = textArea.getSelectedText();
        this.findWhat = Objects.requireNonNullElse(selected, " ");
        setupUI();
    }

    private void setupUI() {
        this.setTitle("Find");
        this.setResizable(false);
        this.setSize(450, 180);
        this.setLayout(new BorderLayout());
        this.setLocationRelativeTo(null);
        JPanel displayPanel = new JPanel();
        displayPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        input = new JTextField(30);
        JLabel label = new JLabel("Find what: ");
        JPanel inputTextPanel = new JPanel();
        JPanel labelPanel = new JPanel();
        inputTextPanel.add(input);
        labelPanel.add(label);
        JPanel inputPanel = new JPanel();
        inputPanel.add(labelPanel);
        inputPanel.add(inputTextPanel);

        JPanel optionPanel = new JPanel();
        JPanel leftPanel = new JPanel();
        caseSensitiveBox = new JCheckBox("Case Sensitive");
        loopBox = new JCheckBox("Loop Search");
        leftPanel.add(caseSensitiveBox);
        leftPanel.add(loopBox);
        leftPanel.setBorder(BorderFactory.createTitledBorder("Options"));
        JPanel rightPanel = new JPanel();
        upwards = new JRadioButton("Upwards");
        downwards = new JRadioButton("Downwards");
        upwards.addActionListener(e -> downwards.setSelected(!upwards.isSelected()));
        downwards.addActionListener(e -> upwards.setSelected(!downwards.isSelected()));
        rightPanel.add(upwards);
        rightPanel.add(downwards);
        rightPanel.setBorder(BorderFactory.createTitledBorder("Direction"));
        optionPanel.add(leftPanel);
        optionPanel.add(rightPanel);

        JPanel controlPanel = new JPanel();
        JButton findNext = new JButton("Find Next");
        JButton cancel = new JButton("Cancel");
        findNext.addActionListener(e -> find());
        cancel.addActionListener(e -> dispose());
        controlPanel.add(findNext);
        controlPanel.add(cancel);

        this.update();
        this.add(inputPanel, BorderLayout.NORTH);
        this.add(optionPanel);
        this.add(controlPanel, BorderLayout.SOUTH);
        this.setModal(false);
    }

    private void update() {
        this.input.setText(this.findWhat);
        this.caseSensitiveBox.setSelected(caseSensitive);
        this.loopBox.setSelected(loop);
        this.upwards.setSelected(direction == SEARCH_DIRECTION.UPWARDS);
        this.downwards.setSelected(direction == SEARCH_DIRECTION.DOWNWARDS);
    }

    private void find() {
        this.findWhat = input.getText();
        this.caseSensitive = caseSensitiveBox.isSelected();
        this.loop = loopBox.isSelected();
        this.direction = upwards.isSelected() ? SEARCH_DIRECTION.UPWARDS : SEARCH_DIRECTION.DOWNWARDS;
        if (direction == SEARCH_DIRECTION.DOWNWARDS) {
            FindAction.findNext(frame, findWhat, caseSensitive, loop);
        } else {
            FindAction.findLast(frame, findWhat, caseSensitive, loop);
        }
    }

    public void findNext() {
        FindAction.findNext(frame, findWhat, caseSensitive, loop);
    }

    public void findLast() {
        FindAction.findLast(frame, findWhat, caseSensitive, loop);
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
