package utils;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.util.concurrent.Callable;

public class TextFieldFactory {
    public JFormattedTextField getTextField(String value) {
        if (value == null)
            value = "";
        return new JFormattedTextField(value);
    }

    public JFormattedTextField getTextField(double value) {
        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Double.class);
        formatter.setMinimum(-100.5);
        formatter.setMaximum(100.5);
        formatter.setAllowsInvalid(true);
        formatter.setCommitsOnValidEdit(false);
        JFormattedTextField textField = new JFormattedTextField(formatter);
        textField.setText(String.valueOf(value));
        return textField;
    }

    public JFormattedTextField getTextField(int value) {
        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(1000);
        formatter.setAllowsInvalid(true);
        formatter.setCommitsOnValidEdit(false);
        JFormattedTextField textField = new JFormattedTextField(formatter);
        textField.setText(String.valueOf(value));
        return textField;
    }

    public void addListeners(JTextField textField, Callable<Void> function) {
        textField.addKeyListener(new SimpleKeyListener() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    try {
                        function.call();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        });
        textField.addFocusListener(new SimpleFocusListener() {
            @Override
            public void focusLost(FocusEvent e) {
                try {
                    function.call();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });
    }
}