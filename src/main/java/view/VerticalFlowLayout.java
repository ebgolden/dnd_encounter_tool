package view;

import java.awt.LayoutManager;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Container;
import java.awt.Insets;

public class VerticalFlowLayout implements LayoutManager, java.io.Serializable {
    int halign;
    int valign;
    int hgap;
    int vgap;
    public final static int TOP         = 0;
    public final static int CENTER      = 1;
    public final static int BOTTOM      = 2;
    public final static int LEFT        = 3;
    public final static int RIGHT       = 4;

    public VerticalFlowLayout() {
        this(LEFT, TOP, 5, 5);
    }

    public VerticalFlowLayout(int halign, int valign, int hgap, int vgap) {
        this.hgap = hgap;
        this.vgap = vgap;
        setAlignment(halign, valign);
    }

    public void setAlignment(int halign, int valign) {
        this.halign = halign;
        this.valign = valign;
    }

    public void addLayoutComponent(String name, Component comp) {}

    public void removeLayoutComponent(Component comp) {}

    public Dimension preferredLayoutSize(Container target) {
        synchronized (target.getTreeLock()) {
            Dimension dim = new Dimension(0, 0);
            int nmembers = target.getComponentCount();
            boolean firstVisibleComponent = true;
            for (int ii = 0; ii < nmembers; ++ii) {
                Component m = target.getComponent(ii);
                if (m.isVisible()) {
                    Dimension d = m.getPreferredSize();
                    dim.width = Math.max(dim.width, d.width);
                    if (firstVisibleComponent)
                        firstVisibleComponent = false;
                    else dim.height += this.vgap;
                    dim.height += d.height;
                }
            }
            Insets insets = target.getInsets();
            dim.width += insets.left + insets.right + this.hgap * 2;
            dim.height += insets.top + insets.bottom + this.vgap * 2;
            return dim;
        }
    }

    public Dimension minimumLayoutSize(Container target) {
        synchronized (target.getTreeLock()) {
            Dimension dim = new Dimension(0, 0);
            int nmembers = target.getComponentCount();
            boolean firstVisibleComponent = true;
            for (int ii = 0; ii < nmembers; ii++) {
                Component m = target.getComponent(ii);
                if (m.isVisible()) {
                    Dimension d = m.getPreferredSize();
                    dim.width = Math.max(dim.width, d.width);
                    if (firstVisibleComponent)
                        firstVisibleComponent = false;
                    else dim.height += this.vgap;
                    dim.height += d.height;
                }
            }
            Insets insets = target.getInsets();
            dim.width += insets.left + insets.right + this.hgap * 2;
            dim.height += insets.top + insets.bottom + this.vgap * 2;
            return dim;
        }
    }

    public void layoutContainer(Container target) {
        synchronized (target.getTreeLock()) {
            Insets insets = target.getInsets();
            int maxheight = target.getHeight() - (insets.top + insets.bottom + this.vgap * 2);
            int nmembers = target.getComponentCount();
            int y = 0;
            Dimension preferredSize = preferredLayoutSize(target);
            Dimension targetSize = target.getSize();
            switch (this.valign) {
                case TOP:
                    y = insets.top;
                    break;
                case CENTER:
                    y = (targetSize.height - preferredSize.height) / 2;
                    break;
                case BOTTOM:
                    y = targetSize.height - preferredSize.height - insets.bottom;
                    break;
            }
            for (int i = 0; i < nmembers; i++) {
                Component m = target.getComponent(i);
                if (m.isVisible()) {
                    Dimension d = m.getPreferredSize();
                    m.setSize(d.width, d.height);
                    if ((y + d.height) <= maxheight) {
                        if (y > 0)
                            y += this.vgap;
                        int x = 0;
                        switch (this.halign) {
                            case LEFT:
                                x = insets.left;
                                break;
                            case CENTER:
                                x = (targetSize.width - d.width) / 2;
                                break;
                            case RIGHT:
                                x = targetSize.width - d.width - insets.right;
                                break;
                        }
                        m.setLocation(x, y);
                        y += d.getHeight();
                    }
                    else break;
                }
            }
        }
    }

    public String toString() {
        String halign = "";
        switch (this.halign) {
            case LEFT:          halign = "left"; break;
            case CENTER:        halign = "center"; break;
            case RIGHT:         halign = "right"; break;
        }
        String valign = "";
        switch (this.valign) {
            case TOP:           valign = "top"; break;
            case CENTER:        valign = "center"; break;
            case BOTTOM:        valign = "bottom"; break;
        }
        return getClass().getName() + "[hgap=" + hgap + ",vgap=" + vgap + ",halign=" + halign + ",valign=" + valign + "]";
    }
}