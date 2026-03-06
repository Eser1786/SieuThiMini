package GUI;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.basic.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Utility methods for styling components used across multiple panels.
 */
public class UIUtils {
    /**
     * Returns a rounded text field used by most forms.
     */
    public static JTextField makeField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Arial", Font.PLAIN, 20));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xAAAAAA), 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        return tf;
    }

    /**
     * Simple helper that creates a small round button used in table action columns.
     */
    public static JButton makeActionButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }
        };
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Money formatter — attaches to ANY JTextField that holds a VNĐ amount.
    // User types digits; listener strips non-digits, re-formats with comma
    // separators (e.g. 1000000 → "1,000,000"), then restores caret.
    // Pattern: call UIUtils.attachMoneyFormatter(tf) immediately after creating tf.
    // To read the raw long value: Long.parseLong(tf.getText().replace(",","").replace(".",""))
    // ─────────────────────────────────────────────────────────────────────────
    public static void attachMoneyFormatter(JTextField tf) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US); // comma separators
        tf.getDocument().addDocumentListener(new DocumentListener() {
            private boolean updating = false;
            private void reformat() {
                if (updating) return;
                SwingUtilities.invokeLater(() -> {
                    if (updating) return;
                    updating = true;
                    try {
                        String raw = tf.getText().replaceAll("[^0-9]", "");
                        if (raw.isEmpty()) { tf.setText(""); return; }
                        long val = Long.parseLong(raw);
                        String formatted = nf.format(val);
                        tf.setText(formatted);
                        tf.setCaretPosition(formatted.length());
                    } catch (NumberFormatException ignored) {
                    } finally {
                        updating = false;
                    }
                });
            }
            public void insertUpdate(DocumentEvent e)  { reformat(); }
            public void removeUpdate(DocumentEvent e)  { reformat(); }
            public void changedUpdate(DocumentEvent e) { reformat(); }
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Style a JComboBox to match the app's purple/lavender theme.
    // Replaces the default OS-dependent look with a clean flat style:
    // white background, Arial 13, thin border, lavender arrow button.
    // Pattern: call UIUtils.styleComboBox(cb) right after creating the combo.
    // ─────────────────────────────────────────────────────────────────────────
    public static void styleComboBox(JComboBox<?> cb) {
        cb.setFont(new Font("Arial", Font.PLAIN, 13));
        cb.setBackground(Color.WHITE);
        cb.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xBBBBBB), 1),
                BorderFactory.createEmptyBorder(0, 2, 0, 2)));
        cb.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton() {
                    @Override public Dimension getPreferredSize() { return new Dimension(26, 0); }
                    @Override protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(0xD1C4E9));
                        g2.fillRect(0, 0, getWidth(), getHeight());
                        // Draw chevron down – scaled from 24x24 viewBox
                        int sz = 12;
                        int ox = (getWidth() - sz) / 2;
                        int oy = (getHeight() - sz) / 2;
                        g2.translate(ox, oy);
                        g2.scale(sz / 24.0, sz / 24.0);
                        g2.setColor(new Color(0x5C4A7F));
                        Path2D chev = new Path2D.Float();
                        chev.moveTo(4.293f, 8.293f);
                        chev.curveTo(4.683f, 7.902f, 5.317f, 7.902f, 5.707f, 8.293f);
                        chev.lineTo(12f, 14.586f);
                        chev.lineTo(18.293f, 8.293f);
                        chev.curveTo(18.683f, 7.902f, 19.317f, 7.902f, 19.707f, 8.293f);
                        chev.curveTo(20.098f, 8.683f, 20.098f, 9.317f, 19.707f, 9.707f);
                        chev.lineTo(12.707f, 16.707f);
                        chev.curveTo(12.317f, 17.098f, 11.683f, 17.098f, 11.293f, 16.707f);
                        chev.lineTo(4.293f, 9.707f);
                        chev.curveTo(3.902f, 9.317f, 3.902f, 8.683f, 4.293f, 8.293f);
                        chev.closePath();
                        g2.fill(chev);
                        g2.dispose();
                    }
                };
                btn.setFocusPainted(false);
                btn.setBorderPainted(false);
                btn.setContentAreaFilled(false);
                return btn;
            }
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Icon factories — return ImageIcon painted with Java2D, no SVG library needed.
    // Each icon is rendered into a sz×sz BufferedImage.
    // ─────────────────────────────────────────────────────────────────────────

    /** Eye-open icon (show password). Color: ACCENT purple by default. */
    public static ImageIcon iconEyeOpen(int sz, Color color) {
        return paintIcon(sz, color, (g2, s) -> {
            g2.setStroke(new BasicStroke(2f * s, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            Path2D eye = new Path2D.Float();
            eye.moveTo(2, 12); eye.curveTo(2, 12, 6, 6, 12, 6);
            eye.curveTo(18, 6, 22, 12, 22, 12);
            eye.curveTo(22, 12, 18, 18, 12, 18);
            eye.curveTo(6, 18, 2, 12, 2, 12);
            eye.closePath();
            g2.draw(eye);
            g2.setStroke(new BasicStroke(1f));
            g2.fill(new Ellipse2D.Double(9, 9, 6, 6));
        });
    }

    /** Eye-slashed icon (hide password). */
    public static ImageIcon iconEyeOff(int sz, Color color) {
        return paintIcon(sz, color, (g2, s) -> {
            g2.setStroke(new BasicStroke(2f * s, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            Path2D eye = new Path2D.Float();
            eye.moveTo(2, 12); eye.curveTo(2, 12, 6, 6, 12, 6);
            eye.curveTo(18, 6, 22, 12, 22, 12);
            eye.curveTo(22, 12, 18, 18, 12, 18);
            eye.curveTo(6, 18, 2, 12, 2, 12);
            eye.closePath();
            g2.draw(eye);
            g2.setStroke(new BasicStroke(1f));
            g2.fill(new Ellipse2D.Double(9, 9, 6, 6));
            g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 220));
            g2.setStroke(new BasicStroke(2.2f * s, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(4, 20, 20, 4);
        });
    }

    /** Pencil/edit icon. */
    public static ImageIcon iconEdit(int sz, Color color) {
        return paintIcon(sz, color, (g2, s) -> {
            g2.setStroke(new BasicStroke(1f));
            // main pencil body
            Path2D body = new Path2D.Float();
            body.moveTo(3, 17.25f); body.lineTo(3, 21); body.lineTo(6.75f, 21);
            body.lineTo(19.81f, 7.94f); body.lineTo(16.06f, 4.19f); body.closePath();
            g2.fill(body);
            // tip detail
            Path2D tip = new Path2D.Float();
            tip.moveTo(20.71f, 6.04f);
            tip.curveTo(21.1f, 5.65f, 21.1f, 5.01f, 20.71f, 4.63f);
            tip.lineTo(19.37f, 3.29f);
            tip.curveTo(18.98f, 2.9f, 18.34f, 2.9f, 17.96f, 3.29f);
            tip.lineTo(16.83f, 4.42f); tip.lineTo(20.58f, 8.17f); tip.closePath();
            g2.fill(tip);
        });
    }

    /** Trash-can icon. */
    public static ImageIcon iconTrash(int sz, Color color) {
        return paintIcon(sz, color, (g2, s) -> {
            g2.setStroke(new BasicStroke(1f));
            // lid
            Path2D lid = new Path2D.Float();
            lid.moveTo(9, 3); lid.lineTo(15, 3); lid.lineTo(16, 5);
            lid.lineTo(21, 5); lid.lineTo(21, 7); lid.lineTo(3, 7);
            lid.lineTo(3, 5); lid.lineTo(8, 5); lid.closePath();
            g2.fill(lid);
            // bin
            Path2D bin = new Path2D.Float();
            bin.moveTo(6, 9); bin.lineTo(18, 9); bin.lineTo(17, 20);
            bin.curveTo(17, 21, 16, 22, 15, 22);
            bin.lineTo(9, 22);
            bin.curveTo(8, 22, 7, 21, 7, 20); bin.closePath();
            g2.fill(bin);
            // inner lines
            g2.setColor(new Color(255, 255, 255, 180));
            g2.setStroke(new BasicStroke(1.5f * s));
            g2.drawLine(10, 11, 10, 19);
            g2.drawLine(14, 11, 14, 19);
        });
    }

    /** Refresh / reload icon. */
    public static ImageIcon iconRefresh(int sz, Color color) {
        return paintIcon(sz, color, (g2, s) -> {
            g2.setStroke(new BasicStroke(1f));
            // Scale the 65x65 viewBox SVG paths into 24x24 space
            g2.scale(24.0 / 65.0, 24.0 / 65.0);
            Path2D p1 = new Path2D.Float();
            p1.moveTo(32.5f, 5f);
            p1.curveTo(27.1f, 5f, 22.06f, 6.58f, 17.8f, 9.28f);
            p1.lineTo(12.05f, 3.53f); p1.lineTo(12.05f, 19.64f); p1.lineTo(28.16f, 19.64f);
            p1.lineTo(21.77f, 13.25f);
            p1.curveTo(24.95f, 11.46f, 28.6f, 10.43f, 32.5f, 10.43f);
            p1.curveTo(44.67f, 10.43f, 54.57f, 20.33f, 54.57f, 32.5f);
            p1.curveTo(54.57f, 35.4f, 54f, 38.17f, 52.97f, 40.7f);
            p1.lineTo(57.71f, 43.46f);
            p1.curveTo(59.18f, 40.1f, 60f, 36.39f, 60f, 32.5f);
            p1.curveTo(60f, 17.34f, 47.66f, 5f, 32.5f, 5f); p1.closePath();
            g2.fill(p1);
            Path2D p2 = new Path2D.Float();
            p2.moveTo(43.23f, 51.75f);
            p2.curveTo(40.05f, 53.53f, 36.4f, 54.57f, 32.5f, 54.57f);
            p2.curveTo(20.33f, 54.57f, 10.43f, 44.67f, 10.43f, 32.5f);
            p2.curveTo(10.43f, 29.76f, 10.95f, 27.15f, 11.87f, 24.73f);
            p2.lineTo(7.14f, 21.88f);
            p2.curveTo(5.77f, 25.15f, 5f, 28.74f, 5f, 32.5f);
            p2.curveTo(5f, 47.66f, 17.34f, 60f, 32.5f, 60f);
            p2.curveTo(37.91f, 60f, 42.94f, 58.42f, 47.19f, 55.71f);
            p2.lineTo(52.95f, 61.47f); p2.lineTo(52.95f, 45.36f); p2.lineTo(36.84f, 45.36f);
            p2.lineTo(43.23f, 51.75f); p2.closePath();
            g2.fill(p2);
        });
    }

    @FunctionalInterface
    private interface IconPainter { void paint(Graphics2D g2, float scale); }

    private static ImageIcon paintIcon(int sz, Color color, IconPainter painter) {
        BufferedImage img = new BufferedImage(sz, sz, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        // scale from 24-unit viewBox to sz pixels
        float scale = sz / 24f;
        g2.scale(scale, scale);
        g2.setColor(color);
        painter.paint(g2, scale);
        g2.dispose();
        return new ImageIcon(img);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Style all scroll bars of a JScrollPane with rounded lavender thumb.
    // Hides the up/down arrows; uses a pill-shaped thumb (0xC5B3E6).
    // Pattern: call UIUtils.styleScrollPane(scroll) after new JScrollPane(table).
    // ─────────────────────────────────────────────────────────────────────────
    public static void styleScrollPane(JScrollPane sp) {
        sp.setBorder(BorderFactory.createLineBorder(new Color(0xCCCCCC)));
        sp.getVerticalScrollBar().setUI(new FlatScrollBarUI());
        sp.getHorizontalScrollBar().setUI(new FlatScrollBarUI());
        sp.getVerticalScrollBar().setUnitIncrement(16);
    }

    private static class FlatScrollBarUI extends BasicScrollBarUI {
        private static final Color THUMB = new Color(0xC5B3E6);
        private static final Color TRACK = new Color(0xF3F0FA);
        @Override protected void configureScrollBarColors() {
            thumbColor = THUMB; trackColor = TRACK;
        }
        @Override protected JButton createDecreaseButton(int o) { return zeroBtn(); }
        @Override protected JButton createIncreaseButton(int o) { return zeroBtn(); }
        private JButton zeroBtn() {
            JButton b = new JButton();
            b.setPreferredSize(new Dimension(0, 0));
            b.setMinimumSize(new Dimension(0, 0));
            b.setMaximumSize(new Dimension(0, 0));
            return b;
        }
        @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            if (r.isEmpty()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(THUMB);
            g2.fillRoundRect(r.x + 2, r.y + 2, r.width - 4, r.height - 4, 8, 8);
            g2.dispose();
        }
        @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
            g.setColor(TRACK);
            g.fillRect(r.x, r.y, r.width, r.height);
        }
    }

    /**
     * Creates a "card" panel used by the dashboard (home) cards. Provides a light drop shadow.
     */
    public static JPanel createCard() {
        JPanel card = new JPanel(new BorderLayout(0, 8)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // light shadow
                g2.setColor(new Color(0, 0, 0, 40));
                g2.fillRoundRect(6, 8, getWidth() - 4, getHeight() - 4, 16, 16);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        return card;
    }
}