package PlainTextEditor.gui;

import javax.swing.*;

public class FindAction {

    public static void findNext(MainFrame frame, String findWhat,
                                boolean caseSensitive, boolean loop) {
        JTextArea textArea = frame.getTextArea();
        String text = textArea.getText();
        int caretPos = textArea.getCaretPosition();
        if (!caseSensitive) {
            text = text.toLowerCase();
            findWhat = findWhat.toLowerCase();
        }
        int index = text.indexOf(findWhat, caretPos);
        if (index == -1) {
            if (loop) {
                index = text.indexOf(findWhat);
                if (index == -1) {
                    new PopupAlertDialog("Nothing matches!");
                } else {
                    textArea.setSelectionStart(index);
                    textArea.setSelectionEnd(index + findWhat.length());
                }
            } else {
                new PopupAlertDialog("Search reaches bottom and nothing matches!");
            }
        } else {
            textArea.setSelectionStart(index);
            textArea.setSelectionEnd(index + findWhat.length());
        }
    }

    public static void findLast(MainFrame frame, String findWhat,
                                boolean caseSensitive, boolean loop) {
        JTextArea textArea = frame.getTextArea();
        String text = textArea.getText();
        int caretPos = textArea.getCaretPosition();
        if (!caseSensitive) {
            text = text.toLowerCase();
            findWhat = findWhat.toLowerCase();
        }
        int index = text.substring(0, caretPos).lastIndexOf(findWhat);
        if (index == -1) {
            if (loop) {
                index = text.lastIndexOf(findWhat);
                if (index == -1) {
                    new PopupAlertDialog("Nothing matches!");
                } else {
                    textArea.setSelectionStart(index);
                    textArea.setSelectionEnd(index + findWhat.length());
                }
            } else {
                new PopupAlertDialog("Search reaches top and nothing matches!");
            }
        } else {
            textArea.setSelectionStart(index);
            textArea.setSelectionEnd(index + findWhat.length());
        }
    }
}
