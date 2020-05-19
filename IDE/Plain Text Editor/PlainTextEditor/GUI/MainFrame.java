package PlainTextEditor.GUI;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class MainFrame extends JFrame {

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
    private final JMenuItem zoomMenuItem = new JMenuItem("Zoom");
    private final JMenuItem statusMenuItem = new JMenuItem("Show Status Bar");

    // help menu items
    private final JMenuItem aboutMenuItem = new JMenuItem("About");

    private JTextArea textArea;

    private final MenuActionListener menuActionListener = new MenuActionListener();

    private boolean edited;

    public MainFrame() {
        initMenuBar();
        this.menuActionListener.register(this);
        this.setSize(800, 600);
        this.setTitle("Untitled - Text Editor");
        this.setLocationRelativeTo(null);
        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
                if (edited) {
                    //todo
                }
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
        this.initTextArea();
        this.initMenuActionListener();
        this.edited = false;
        this.setVisible(true);
    }

    public JTextArea getTextArea() {
        return this.textArea;
    }

    private void initMenuBar() {
        fileMenu.add(newMenuItem);
        fileMenu.add(windowMenuItem);
        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(saveAsMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);
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
        editMenu.setMnemonic('E');

        autoWrapMenuItem.setSelected(true);

        formatMenu.add(autoWrapMenuItem);
        formatMenu.add(fontMenuItem);
        formatMenu.setMnemonic('O');

        viewMenu.add(zoomMenuItem);
        viewMenu.add(statusMenuItem);
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

        zoomMenuItem.addActionListener(menuActionListener);
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
                if (!edited) {
                    edited = true;
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (!edited) {
                    edited = true;
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (!edited) {
                    edited = true;
                }
            }
        });
    }
}
