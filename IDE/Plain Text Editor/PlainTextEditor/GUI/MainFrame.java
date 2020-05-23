package PlainTextEditor.GUI;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class MainFrame extends JFrame {

    private static final AtomicInteger surviving = new AtomicInteger(0);

    private final JMenuBar menuBar = new JMenuBar();

    private final JMenu fileMenu = new JMenu("File(F)");
    private final JMenu editMenu = new JMenu("Edit(E)");
    private final JMenu formatMenu = new JMenu("Format(O)");
    private final JMenu viewMenu = new JMenu("View(V)");
    private final JMenu helpMenu = new JMenu("Help(H)");

    // file menu items
    private final JMenuItem newMenuItem = new JMenuItem("New");
    private final JMenuItem windowMenuItem = new JMenuItem("New Window");
    private final JMenuItem openMenuItem = new JMenuItem("Open");
    private final JMenuItem saveMenuItem = new JMenuItem("Save");
    private final JMenuItem saveAsMenuItem = new JMenuItem("Save as");
    private final JMenuItem exitMenuItem = new JMenuItem("Exit");

    // edit menu items
    private final JMenuItem undoMenuItem = new JMenuItem("Undo");
    private final JMenuItem cutMenuItem = new JMenuItem("Cut");
    private final JMenuItem copyMenuItem = new JMenuItem("Copy");
    private final JMenuItem pasteMenuItem = new JMenuItem("Paste");
    private final JMenuItem delMenuItem = new JMenuItem("Delete");
    private final JMenuItem findMenuItem = new JMenuItem("Find");
    private final JMenuItem findNextMenuItem = new JMenuItem("Find Next");
    private final JMenuItem findLastMenuItem = new JMenuItem("Find Last");
    private final JMenuItem replaceMenuItem = new JMenuItem("Replace");
    private final JMenuItem selectAllMenuItem = new JMenuItem("Select All");
    private final JMenuItem timeMenuItem = new JMenuItem("Time and Date");

    // format menu items
    private final JCheckBoxMenuItem autoWrapMenuItem = new JCheckBoxMenuItem("Auto Wrap");
    private final JMenuItem fontMenuItem = new JMenuItem("Font");

    // view menu items
    private final JMenu zoomSubmenu = new JMenu("Zoom");
    private final JMenuItem zoomInMenuItem = new JMenuItem("Zoom in");
    private final JMenuItem zoomOutMenuItem = new JMenuItem("Zoom out");
    private final JMenuItem zoomResetMenuItem = new JMenuItem("Reset");
    private final JMenuItem statusMenuItem = new JMenuItem("Show Status Bar");

    // help menu items
    private final JMenuItem aboutMenuItem = new JMenuItem("About");

    private JTextArea textArea;
    private JLabel zoomRateLabel;
    private JLabel caretPositionLabel;

    private final MenuActionListener menuActionListener = new MenuActionListener();

    private AtomicBoolean unsaved;

    private String title;
    private String filepath;
    private String encoding;
    private int zoomRate;

    public MainFrame() {
        this.initStatus();
        this.initUI();
        this.initMenuBar();
        this.initMenuActionListener();
        this.initTextArea();
        this.initStatusBar();
        this.setVisible(true);
    }

    public JTextArea getTextArea() {
        return this.textArea;
    }

    public boolean getUnsavedStatus() {
        return unsaved.get();
    }

    private void initStatus() {
        surviving.addAndGet(1);
        this.unsaved = new AtomicBoolean(false);
        this.title = "Untitled";
        this.filepath = null;
        this.encoding = "UTF-8";
        this.zoomRate = 100;
    }

    private void initUI() {
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.setSize(800, 600);
        this.setTitle(title + " - Text Editor");
        this.setLocationRelativeTo(null);
        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
                onWindowClosing();
            }

            @Override
            public void windowClosed(WindowEvent e) {
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });
    }

    private void initMenuBar() {
        fileMenu.add(newMenuItem);
        fileMenu.add(windowMenuItem);
        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(saveAsMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);

        newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        windowMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
                InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        saveAsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK));
        fileMenu.setMnemonic('F');

        editMenu.add(undoMenuItem);
        editMenu.addSeparator();
        editMenu.add(cutMenuItem);
        editMenu.add(copyMenuItem);
        editMenu.add(pasteMenuItem);
        editMenu.add(delMenuItem);
        editMenu.add(findMenuItem);
        editMenu.add(findNextMenuItem);
        editMenu.add(findLastMenuItem);
        editMenu.add(replaceMenuItem);
        editMenu.addSeparator();
        editMenu.add(selectAllMenuItem);
        editMenu.add(timeMenuItem);

        undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
        cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
        copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
        delMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        findMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
        findNextMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
        findLastMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, InputEvent.SHIFT_DOWN_MASK));
        replaceMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_DOWN_MASK));
        selectAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
        timeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        editMenu.setMnemonic('E');

        autoWrapMenuItem.setSelected(true);

        formatMenu.add(autoWrapMenuItem);
        formatMenu.add(fontMenuItem);
        formatMenu.setMnemonic('O');

        viewMenu.add(zoomSubmenu);
        viewMenu.add(statusMenuItem);
        zoomSubmenu.add(zoomInMenuItem);
        zoomSubmenu.add(zoomOutMenuItem);
        zoomSubmenu.add(zoomResetMenuItem);
        viewMenu.setMnemonic('V');

        helpMenu.add(aboutMenuItem);
        helpMenu.setMnemonic('H');

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(formatMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);
        this.setJMenuBar(menuBar);
    }

    private void initMenuActionListener() {
        this.menuActionListener.register(this);
        newMenuItem.addActionListener(menuActionListener);
        windowMenuItem.addActionListener(menuActionListener);
        openMenuItem.addActionListener(menuActionListener);
        saveMenuItem.addActionListener(menuActionListener);
        saveAsMenuItem.addActionListener(menuActionListener);
        exitMenuItem.addActionListener(menuActionListener);

        undoMenuItem.addActionListener(menuActionListener);
        cutMenuItem.addActionListener(menuActionListener);
        copyMenuItem.addActionListener(menuActionListener);
        pasteMenuItem.addActionListener(menuActionListener);
        delMenuItem.addActionListener(menuActionListener);
        findMenuItem.addActionListener(menuActionListener);
        findNextMenuItem.addActionListener(menuActionListener);
        findLastMenuItem.addActionListener(menuActionListener);
        replaceMenuItem.addActionListener(menuActionListener);
        selectAllMenuItem.addActionListener(menuActionListener);
        timeMenuItem.addActionListener(menuActionListener);

        autoWrapMenuItem.addActionListener(menuActionListener);
        fontMenuItem.addActionListener(menuActionListener);

        zoomSubmenu.addActionListener(menuActionListener);
        statusMenuItem.addActionListener(menuActionListener);

        aboutMenuItem.addActionListener(menuActionListener);
    }

    private void initTextArea() {
        this.textArea = new JTextArea();
        JScrollPane textAreaContainer = new JScrollPane(textArea);
        textAreaContainer.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.add(textAreaContainer);
        try {
            this.textArea.setFont(new Font("微软雅黑", Font.PLAIN, 18));
            this.textArea.setForeground(Color.BLACK);
        } catch (Exception ignored) {
        }
        boolean checked = autoWrapMenuItem.getState();
        textArea.setLineWrap(checked);
        textArea.setWrapStyleWord(checked);
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (!unsaved.get()) {
                    MainFrame.this.setTitle("*" + title + " - Text Editor");
                    unsaved.set(true);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (!unsaved.get()) {
                    MainFrame.this.setTitle("*" + title + " - Text Editor");
                    unsaved.set(true);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (!unsaved.get()) {
                    MainFrame.this.setTitle("*" + title + " - Text Editor");
                    unsaved.set(true);
                }
            }
        });
        textArea.getCaret().addChangeListener(e -> caretPositionLabel.setText(getCaretPositionString()));
    }

    private void initStatusBar() {
        JPanel statusBar = new JPanel();
        statusBar.setPreferredSize(new Dimension(0, 20));
        statusBar.setLayout(new FlowLayout(FlowLayout.RIGHT));
        JLabel encodingLabel = new JLabel(this.encoding);
        zoomRateLabel = new JLabel(zoomRate + "%");
        caretPositionLabel = new JLabel(getCaretPositionString());
        statusBar.add(caretPositionLabel);
        statusBar.add(new JLabel("  "));
        statusBar.add(zoomRateLabel);
        statusBar.add(new JLabel("  "));
        statusBar.add(encodingLabel);
        this.add(statusBar, BorderLayout.SOUTH);
    }

    private String getCaretPositionString() {
        int caretPosition = textArea.getCaretPosition();
        String text = textArea.getText();
        String substring = text.substring(0, caretPosition);
        String[] lines = substring.split("\n");
        int row = lines.length;
        int col = lines[lines.length - 1].length() + 1;
        return "Ln:" + row + "  " + "Col:" + col;
    }

    public void onWindowClosing() {
        if (unsaved.get()) {
            int result = FileSaveDialog.querySave(MainFrame.this, filepath);
            switch (result) {
                default:
                case FileSaveDialog.FSD_CANCEL:
                    break;
                case FileSaveDialog.FSD_NOT:
                case FileSaveDialog.FSD_SAVE:
                    surviving.addAndGet(-1);
                    if (surviving.get() <= 0) {
                        System.exit(0);
                    }
                    dispose();
            }
        } else {
            surviving.addAndGet(-1);
            if (surviving.get() <= 0) {
                System.exit(0);
            }
            dispose();
        }
    }

    public void newFile() {
        if (unsaved.get()) {
            int result = FileSaveDialog.querySave(MainFrame.this, filepath);
            switch (result) {
                default:
                case FileSaveDialog.FSD_CANCEL:
                    break;
                case FileSaveDialog.FSD_NOT:
                case FileSaveDialog.FSD_SAVE:
                    this.unsaved.set(false);
                    this.textArea.setText("");
                    this.title = "Untitled";
                    this.setTitle(title + " - Text Editor");
                    // out of unknown reason, after this the title of the windows
                    // fails to react according to unsaved status
                    // fix it later
            }
        } else {
            this.textArea.setText("");
            this.title = "Untitled";
            this.setTitle(title + " - Text Editor");
        }
    }

    public void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File readFile = fileChooser.getSelectedFile();
            if (readFile.exists()) {
                try {
                    InputStream input = new FileInputStream(readFile);
                    Scanner scanner = new Scanner(input);
                    StringBuilder sb = new StringBuilder();
                    if (scanner.hasNextLine()) {
                        sb.append(scanner.nextLine());
                    }
                    while (scanner.hasNextLine()) {
                        sb.append("\n").append(scanner.nextLine());
                    }
                    input.close();
                    scanner.close();
                    textArea.setText(sb.toString());
                    this.filepath = readFile.getAbsolutePath();
                    this.title = readFile.getName();
                    this.setTitle(title + " - Text Editor");
                    this.unsaved.set(false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void saveFile() {
        if (FileSaveAction.fileSave(this, this, this.filepath)) {
            this.unsaved.set(false);
            this.setTitle(title + " - Text Editor");
        }
    }

    public void saveFileAs() {
        if (FileSaveAction.fileSave(this, this, null)) {
            this.unsaved.set(false);
            this.setTitle(title + " - Text Editor");
        }
    }
}
