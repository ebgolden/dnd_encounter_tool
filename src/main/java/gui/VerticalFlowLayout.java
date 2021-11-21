package gui;

import java.awt.LayoutManager;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Container;
import java.awt.Insets;

public class VerticalFlowLayout implements LayoutManager, java.io.Serializable {
    /**
     * <code>align</code> is the property that determines
     * how each row distributes empty space.
     * It can be one of the following values:
     * <ul>
     * <li><code>LEFT</code>
     * <li><code>RIGHT</code>
     * <li><code>CENTER</code>
     * </ul>
     *
     * @serial
     * @see #getHalignment
     * @see #setAlignment
     */
    int halign;

    /**
     * <code>align</code> is the property that determines
     * how each col distributes empty space.
     * It can be one of the following values:
     * <ul>
     * <li><code>TOP</code>
     * <li><code>BOTTOM</code>
     * <li><code>CENTER</code>
     * </ul>
     *
     * @serial
     * @see #getValignment
     * @see #setAlignment
     */
    int valign;

    /**
     * The flow layout manager allows a seperation of
     * components with gaps.  The horizontal gap will
     * specify the space between components and between
     * the components and the borders of the
     * <code>Container</code>.
     *
     * @serial
     * @see #getHgap()
     * @see #setHgap(int)
     */
    int hgap;

    /**
     * The flow layout manager allows a seperation of
     * components with gaps.  The vertical gap will
     * specify the space between rows and between the
     * the rows and the borders of the <code>Container</code>.
     *
     * @serial
     * @see #getHgap()
     * @see #setHgap(int)
     */
    int vgap;

    /**
     * This value indicates that each row of components
     * should be top-justified.
     */
    public final static int TOP         = 0;

    /**
     * This value indicates that each row of components
     * should be centered.
     */
    public final static int CENTER      = 1;

    /**
     * This value indicates that each row of components
     * should be bottom-justified.
     */
    public final static int BOTTOM      = 2;

    /**
     * This value indicates that each row of components
     * should be left-justified.
     */
    public final static int LEFT        = 3;

    /**
     * This value indicates that each row of components
     * should be right-justified.
     */
    public final static int RIGHT       = 4;

    /**
     *  Constructor for the VerticalFlowLayout object
     */
    public VerticalFlowLayout() {
        this(LEFT, TOP, 5, 5);
    }

    /**
     *  Constructor for the VerticalFlowLayout object
     *
     *@param  halign  Description of Parameter
     *@param  valign  Description of Parameter
     */
    public VerticalFlowLayout(int halign, int valign) {
        this(halign, valign, 5, 5);
    }

    /**
     *  Constructor for the VerticalFlowLayout object
     *
     *@param  halign  Description of Parameter
     *@param  valign  Description of Parameter
     *@param  hgap    Description of Parameter
     *@param  vgap    Description of Parameter
     */
    public VerticalFlowLayout(int halign, int valign, int hgap, int vgap) {
        this.hgap = hgap;
        this.vgap = vgap;
        setAlignment(halign, valign);
    }

    /**
     *  Sets the Alignment attribute of the VerticalFlowLayout object
     *
     *@param  halign  The new Alignment value
     *@param  valign  The new Alignment value
     */
    public void setAlignment(int halign, int valign) {
        this.halign = halign;
        this.valign = valign;
    }

    /**
     * Gets the horizontal gap between components
     * and between the components and the borders
     * of the <code>Container</code>
     *
     * @return     the horizontal gap between components
     *             and between the components and the borders
     *             of the <code>Container</code>
     * @see        java.awt.FlowLayout#setHgap
     * @since      JDK1.1
     */
    public int getHgap() {
        return hgap;
    }

    /**
     * Sets the horizontal gap between components and
     * between the components and the borders of the
     * <code>Container</code>.
     *
     * @param hgap the horizontal gap between components
     *             and between the components and the borders
     *             of the <code>Container</code>
     * @see        java.awt.FlowLayout#getHgap
     * @since      JDK1.1
     */
    public void setHgap(int hgap) {
        this.hgap = hgap;
    }

    /**
     * Gets the vertical gap between components and
     * between the components and the borders of the
     * <code>Container</code>.
     *
     * @return     the vertical gap between components
     *             and between the components and the borders
     *             of the <code>Container</code>
     * @see        java.awt.FlowLayout#setVgap
     * @since      JDK1.1
     */
    public int getVgap() {
        return vgap;
    }

    /**
     * Sets the vertical gap between components and between
     * the components and the borders of the <code>Container</code>.
     *
     * @param vgap the vertical gap between components
     *             and between the components and the borders
     *             of the <code>Container</code>
     * @see        java.awt.FlowLayout#getVgap
     * @since      JDK1.1
     */
    public void setVgap(int vgap) {
        this.vgap = vgap;
    }

    /**
     *  Gets the Halignment attribute of the VerticalFlowLayout object
     *
     *@return    The Halignment value
     */
    public int getHalignment() {
        return this.halign;
    }

    /**
     *  Gets the Valignment attribute of the VerticalFlowLayout object
     *
     *@return    The Valignment value
     */
    public int getValignment() {
        return this.valign;
    }

    /**
     * Adds the specified component to the layout.
     * Not used by this class.
     * @param name the name of the component
     * @param comp the component to be added
     */
    public void addLayoutComponent(String name, Component comp) {
    }

    /**
     * Removes the specified component from the layout.
     * Not used by this class.
     * @param comp the component to remove
     * @see       java.awt.Container#removeAll
     */
    public void removeLayoutComponent(Component comp) {
    }

    /**
     * Returns the preferred dimensions for this layout given the
     * <i>visible</i> components in the specified target container.
     *
     * @param target the container that needs to be laid out
     * @return    the preferred dimensions to lay out the
     *            subcomponents of the specified container
     * @see Container
     * @see #minimumLayoutSize
     * @see       java.awt.Container#getPreferredSize
     */
    public Dimension preferredLayoutSize(Container target) {
        synchronized (target.getTreeLock()) {
            Dimension dim = new Dimension(0, 0);
            int nmembers = target.getComponentCount();
            boolean firstVisibleComponent = true;

            for (int ii = 0; ii < nmembers; ii++) {
                Component m = target.getComponent(ii);
                if (m.isVisible()) {
                    Dimension d = m.getPreferredSize();
                    dim.width = Math.max(dim.width, d.width);
                    if (firstVisibleComponent) {
                        firstVisibleComponent = false;
                    }
                    else {
                        dim.height += this.vgap;
                    }
                    dim.height += d.height;
                }
            }
            Insets insets = target.getInsets();
            dim.width += insets.left + insets.right + this.hgap * 2;
            dim.height += insets.top + insets.bottom + this.vgap * 2;
            return dim;
        }
    }

    /**
     * Returns the minimum dimensions needed to layout the <i>visible</i>
     * components contained in the specified target container.
     * @param target the container that needs to be laid out
     * @return    the minimum dimensions to lay out the
     *            subcomponents of the specified container
     * @see #preferredLayoutSize
     * @see       java.awt.Container
     * @see       java.awt.Container#doLayout
     */
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
                    if (firstVisibleComponent) {
                        firstVisibleComponent = false;
                    }
                    else {
                        dim.height += this.vgap;
                    }
                    dim.height += d.height;
                }
            }
            Insets insets = target.getInsets();
            dim.width += insets.left + insets.right + this.hgap * 2;
            dim.height += insets.top + insets.bottom + this.vgap * 2;
            return dim;
        }
    }

    /**
     * Lays out the container. This method lets each
     * <i>visible</i> component take
     * its preferred size by reshaping the components in the
     * target container in order to satisfy the alignment of
     * this <code>VerticalFlowLayout</code> object.
     *
     * @param target the specified component being laid out
     * @see Container
     * @see       java.awt.Container#doLayout
     */
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
                        if (y > 0) {
                            y += this.vgap;
                        }

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
                    else {
                        break;
                    }
                }
            }
        }
    }

    /**
     * Returns a string representation of this <code>VerticalFlowLayout</code>
     * object and its values.
     * @return     a string representation of this layout
     */
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