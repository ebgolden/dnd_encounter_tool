package controller;

import org.icepdf.ri.common.ComponentKeyBinding;
import org.icepdf.ri.common.MyAnnotationCallback;
import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;
import javax.swing.*;
import java.io.File;
import java.net.MalformedURLException;

public class PDFController extends SwingController {
    private final JPanel VIEWER_COMPONENT_PANEL;

    public PDFController() {
        super();
        SwingViewBuilder factory = new SwingViewBuilder(this);
        VIEWER_COMPONENT_PANEL = factory.buildViewerPanel();
        ComponentKeyBinding.install(this, VIEWER_COMPONENT_PANEL);
        getDocumentViewController().setAnnotationCallback(new MyAnnotationCallback(getDocumentViewController()));
        System.setProperty("org.icepdf.core.nfont.truetype.hinting", "true");
        System.setProperty("org.icepdf.core.awtFontLoading", "true");
    }

    public JPanel getViewerComponentPanel() {
        return VIEWER_COMPONENT_PANEL;
    }

    public void viewCharacterSheet(File file) throws MalformedURLException {
        openDocument(file.toURI().toURL());
    }
}