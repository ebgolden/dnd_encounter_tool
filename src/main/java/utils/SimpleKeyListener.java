package utils;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public abstract class SimpleKeyListener implements KeyListener {
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public abstract void keyReleased(KeyEvent e);
}