package view;

import controller.PDFController;
import javax.swing.*;
import java.awt.*;

public class PDFPanel extends JPanel {
    private final PDFController PDF_CONTROLLER;

    public PDFPanel() {
        super(new BorderLayout());
        PDF_CONTROLLER = new PDFController();
        JPanel viewerComponentPanel = PDF_CONTROLLER.getViewerComponentPanel();
        viewerComponentPanel.setPreferredSize(new Dimension(400, 243));
        viewerComponentPanel.setMaximumSize(new Dimension(400, 243));
        super.add(viewerComponentPanel, BorderLayout.CENTER);
        super.invalidate();
    }

    public PDFController getPDFController() {
        return PDF_CONTROLLER;
    }
}