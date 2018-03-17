package script.view;

import org.osbot.rs07.api.model.Item;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ErrorMessageView {

    private static JFrame _instance;

    public static void display(Item currentItem, Exception error)
    {
        if(_instance != null)
            return;
        JFrame frame = new JFrame();

        frame.setTitle("Error Message");
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                frame.dispose();
                _instance = null;
            }
        });

        frame.setLocationRelativeTo(frame.getOwner());
        JTextArea textField = new JTextArea();
        textField.append("Current Item: ");
        textField.append(currentItem == null ? "No More Items" : currentItem.getName());
        textField.append("\nError Message \n");
        textField.append(error.getMessage() + "\n");
        final StackTraceElement[] elements = error.getStackTrace();
        for (int i = 0; i < 5 && i < elements.length; i++) {
            textField.append(elements[i].toString() + "\n");
        }
        frame.add(textField);
        frame.pack();
        frame.setVisible(true);
        _instance = frame;
    }
}
