package view;

import org.icepdf.ri.common.ComponentKeyBinding;
import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;

import javax.swing.*;
import java.awt.*;

public class PDFPanel extends JPanel {
    private final SwingController CONTROLLER;

    public PDFPanel() {
        super(new BorderLayout());
        CONTROLLER = new SwingController();
        SwingViewBuilder factory = new SwingViewBuilder(CONTROLLER);
        JPanel viewerComponentPanel = factory.buildViewerPanel();
        viewerComponentPanel.setPreferredSize(new Dimension(400, 243));
        viewerComponentPanel.setMaximumSize(new Dimension(400, 243));
        ComponentKeyBinding.install(CONTROLLER, viewerComponentPanel);
        CONTROLLER.getDocumentViewController().setAnnotationCallback(
                new org.icepdf.ri.common.MyAnnotationCallback(
                        CONTROLLER.getDocumentViewController()));
        super.add(viewerComponentPanel, BorderLayout.CENTER);
        super.invalidate();
        System.setProperty("org.icepdf.core.nfont.truetype.hinting", "true");
        System.setProperty("org.icepdf.core.awtFontLoading", "true");
    }

    public SwingController getController() {
        return CONTROLLER;
    }
}
