package PlainTextEditor.GUI;

import javax.swing.*;
import java.awt.*;

public class FontSettingsDialog extends JDialog {

    private static final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    private static final String[] fontFamilies = ge.getAvailableFontFamilyNames();
    private static final String[] fontStyles = new String[]{"Regular", "Bold", "Italic", "Bold Italic"};
    private static final Integer[] fontSizes =
            new Integer[]{8, 9, 10, 11, 12, 14, 16, 18, 20, 22, 24, 26, 28, 36, 48, 72};

    private final MainFrame mainFrame;

    private JComboBox<String> fontFamilyBox;
    private JComboBox<Integer> fontSizeBox;
    private JComboBox<String> fontStyleBox;

    private JLabel fontDisplayLabel;

    private Font font;
    private String fontFamily;
    private int fontSize;
    private int fontStyle;
    private Color fontColor;

    public FontSettingsDialog(Font font, Color color, MainFrame frame) {
        this.font = font;
        this.fontFamily = font.getFontName();
        this.fontSize = font.getSize();
        this.fontStyle = font.getStyle();
        this.fontColor = color;
        this.mainFrame = frame;
        setUpUI();
    }

    private void setUpUI() {
        this.setSize(500, 400);
        this.setTitle("Font Settings");
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setLayout(new GridLayout(2, 1));
        JPanel panelUpper = new JPanel();
        panelUpper.setLayout(new GridLayout(4, 2));
        panelUpper.setBorder(BorderFactory.createTitledBorder("Editor Font"));

        JLabel familyLabel = new JLabel("Font Family");
        familyLabel.setHorizontalAlignment(JLabel.CENTER);
        panelUpper.add(familyLabel);
        this.fontFamilyBox = new JComboBox<>(fontFamilies);
        fontFamilyBox.setEditable(false);
        fontFamilyBox.setSelectedItem(this.fontFamily);
        fontFamilyBox.addActionListener(e -> {
            preview();
        });
        panelUpper.add(fontFamilyBox);

        JLabel styleLabel = new JLabel("Font Style");
        styleLabel.setHorizontalAlignment(JLabel.CENTER);
        panelUpper.add(styleLabel);
        this.fontStyleBox = new JComboBox<>(fontStyles);
        fontStyleBox.setEditable(false);
        switch (fontStyle) {
            case Font.PLAIN:
                fontStyleBox.setSelectedItem("Regular");
                break;
            case Font.BOLD:
                fontStyleBox.setSelectedItem("Bold");
                break;
            case Font.ITALIC:
                fontStyleBox.setSelectedItem("Italic");
                break;
            case Font.BOLD | Font.ITALIC:
                fontStyleBox.setSelectedItem("Bold Italic");
                break;
            default:
                break;
        }
        fontStyleBox.setSelectedItem(this.fontStyle);
        fontStyleBox.addActionListener(e -> {
            preview();
        });
        panelUpper.add(fontStyleBox);

        JLabel sizeLabel = new JLabel("Font Size");
        sizeLabel.setHorizontalAlignment(JLabel.CENTER);
        panelUpper.add(sizeLabel);
        this.fontSizeBox = new JComboBox<>(fontSizes);
        fontSizeBox.setEditable(false);
        fontSizeBox.setSelectedItem(fontSize);
        fontSizeBox.addActionListener(e -> {
            preview();
        });
        panelUpper.add(fontSizeBox);

        JDialog colorChooseDialog = new JDialog();

        JLabel colorLabel = new JLabel("Font Color");
        colorLabel.setHorizontalAlignment(JLabel.CENTER);
        panelUpper.add(colorLabel);
        JPanel colorChoosePanel = new JPanel();
        colorChoosePanel.setLayout(new GridLayout(1, 2));
        JLabel colorDisplayLabel = new JLabel();
        colorDisplayLabel.setBackground(fontColor);
        colorDisplayLabel.setOpaque(true);
        colorDisplayLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        colorChoosePanel.add(colorDisplayLabel);
        JButton colorChooseButton = new JButton("Chose Color");
        colorChooseButton.addActionListener(e -> {
            Color color = JColorChooser.showDialog(colorChooseDialog, "Chose Color", fontColor);
            if (color == null) {
                return;
            }
            colorDisplayLabel.setBackground(color);
            preview(color);
        });
        colorChoosePanel.add(colorChooseButton);
        panelUpper.add(colorChoosePanel);

        JPanel panelLower = new JPanel();
        panelLower.setLayout(new GridLayout(1, 2));

        JPanel fontDisplayPanel = new JPanel();
        fontDisplayPanel.setLayout(new GridLayout(1, 1));
        fontDisplayPanel.setBorder(BorderFactory.createTitledBorder("Preview"));
        fontDisplayLabel = new JLabel("AaBbYyZz");
        fontDisplayLabel.setHorizontalAlignment(JLabel.CENTER);
        fontDisplayLabel.setVerticalAlignment(JLabel.CENTER);
        fontDisplayLabel.setFont(font);
        fontDisplayLabel.setForeground(fontColor);
        fontDisplayPanel.add(fontDisplayLabel);
        panelLower.add(fontDisplayPanel);

        JPanel functionalPanel = new JPanel();
        functionalPanel.setLayout(new BorderLayout());
        Box functionalInner = Box.createHorizontalBox();
        functionalPanel.add(functionalInner, BorderLayout.SOUTH);
        JButton applyButton = new JButton("Apply");
        JButton cancelButton = new JButton("Cancel");
        applyButton.addActionListener(e -> {
            applySettings();
            destroyDialog();
        });
        cancelButton.addActionListener(e -> {
            destroyDialog();
        });
        functionalInner.add(Box.createHorizontalGlue());
        functionalInner.add(applyButton);
        functionalInner.add(Box.createHorizontalGlue());
        functionalInner.add(cancelButton);
        functionalInner.add(Box.createHorizontalGlue());
        panelLower.add(functionalPanel);

        this.add(panelUpper);
        this.add(panelLower);
        this.setModal(true);
        this.setVisible(true);
    }

    private void preview() {
        this.fontFamily = (String) this.fontFamilyBox.getSelectedItem();
        String fontStyleString = (String) this.fontStyleBox.getSelectedItem();
        try {
            assert fontStyleString != null;
            switch (fontStyleString) {
                case "Regular":
                    this.fontStyle = Font.PLAIN;
                    break;
                case "Bold":
                    this.fontStyle = Font.BOLD;
                    break;
                case "Italic":
                    this.fontStyle = Font.ITALIC;
                    break;
                case "Bold Italic":
                    this.fontStyle = Font.BOLD | Font.ITALIC;
                    break;
                default:
                    break;
            }
        } catch (Exception ignored) {
        }
        Integer fontSizeValue = (Integer) this.fontSizeBox.getSelectedItem();
        try {
            assert fontSizeValue != null;
            this.fontSize = fontSizeValue;
        } catch (Exception ignored) {
        }
        this.font = new Font(fontFamily, fontStyle, fontSize);
        this.fontDisplayLabel.setFont(font);
    }

    private void preview(Color color) {
        preview();
        this.fontColor = color;
        this.fontDisplayLabel.setForeground(color);
    }

    private void applySettings() {
        JTextArea textArea = mainFrame.getTextArea();
        textArea.setFont(font);
        textArea.setForeground(fontColor);
    }

    private void destroyDialog() {
        this.setVisible(false);
        this.setModal(false);
        this.dispose();
    }
}
