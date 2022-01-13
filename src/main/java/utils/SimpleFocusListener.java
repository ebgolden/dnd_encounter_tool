package utils;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public abstract class SimpleFocusListener implements FocusListener {
    @Override
    public void focusGained(FocusEvent e) {}

    @Override
    public abstract void focusLost(FocusEvent e);
}