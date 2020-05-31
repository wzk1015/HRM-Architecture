package PlainTextEditor.util;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;

public class ClipboardManager {

    private static final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

    public static boolean isEmpty() {
        return getSystemClipboardText().isEmpty();
    }

    public static String getSystemClipboardText() {
        Transferable clipboardContent = clipboard.getContents(null);
        if (clipboardContent != null &&
                clipboardContent.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                return (String) clipboardContent.getTransferData(DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException | IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static void setSystemClipboardText(String text) {
        if (text != null && !text.equals("")) {
            Transferable tfText = new StringSelection(text);
            clipboard.setContents(tfText, null);
        }
    }
}
