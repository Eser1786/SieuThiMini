package GUI;

import java.awt.*;
import javax.swing.SwingUtilities;

/**
 * A FlowLayout subtype that wraps components to a new row when there is not
 * enough horizontal space, and reports the correct preferred height so that
 * containers placed in BorderLayout.NORTH expand vertically as needed.
 *
 * Based on Rob Camick's WrapLayout (public domain).
 */
public class WrapLayout extends FlowLayout {

    public WrapLayout() { super(); }
    public WrapLayout(int align) { super(align); }
    public WrapLayout(int align, int hgap, int vgap) { super(align, hgap, vgap); }

    @Override
    public Dimension preferredLayoutSize(Container target) {
        return layoutSize(target, true);
    }

    @Override
    public Dimension minimumLayoutSize(Container target) {
        Dimension min = layoutSize(target, false);
        min.width -= (getHgap() + 1);
        return min;
    }

    /**
     * Override layoutContainer so that whenever the required height changes
     * (items wrap/unwrap), we notify the parent to re-layout — this ensures
     * BorderLayout.NORTH grows/shrinks correctly on every resize including
     * minimize→maximize cycles.
     */
    @Override
    public void layoutContainer(Container target) {
        Dimension pref = preferredLayoutSize(target);
        super.layoutContainer(target);
        // Compare needed height against the container's ACTUAL current height,
        // not the stale stored preferred size, so maximize/restore is handled.
        if (pref.height != target.getHeight()) {
            target.setPreferredSize(pref);
            Container parent = target.getParent();
            if (parent != null) {
                SwingUtilities.invokeLater(() -> {
                    parent.revalidate();
                    parent.repaint();
                });
            }
        }
    }

    private Dimension layoutSize(Container target, boolean preferred) {
        synchronized (target.getTreeLock()) {
            int targetWidth = target.getSize().width;
            Container container = target;
            while (targetWidth == 0 && container.getParent() != null) {
                container = container.getParent();
                targetWidth = container.getSize().width;
            }
            if (targetWidth == 0) targetWidth = Integer.MAX_VALUE;

            int hgap = getHgap();
            int vgap = getVgap();
            Insets ins = target.getInsets();
            int maxWidth = targetWidth - ins.left - ins.right - hgap * 2;

            Dimension dim = new Dimension(0, 0);
            int rowW = 0, rowH = 0;

            for (int i = 0; i < target.getComponentCount(); i++) {
                Component m = target.getComponent(i);
                if (!m.isVisible()) continue;
                Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();
                if (rowW != 0 && rowW + hgap + d.width > maxWidth) {
                    addRow(dim, rowW, rowH, vgap);
                    rowW = 0; rowH = 0;
                }
                if (rowW != 0) rowW += hgap;
                rowW += d.width;
                rowH = Math.max(rowH, d.height);
            }
            addRow(dim, rowW, rowH, vgap);

            dim.width  += ins.left + ins.right + hgap * 2;
            dim.height += ins.top  + ins.bottom + vgap * 2;
            return dim;
        }
    }

    private void addRow(Dimension dim, int rowW, int rowH, int vgap) {
        dim.width = Math.max(dim.width, rowW);
        if (dim.height > 0) dim.height += vgap;
        dim.height += rowH;
    }
}
