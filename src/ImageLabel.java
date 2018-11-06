import javax.swing.text.*;
import javax.swing.text.StyleContext.NamedStyle;
import javax.swing.*;
import java.awt.*;
import java.util.*;
class ImageLabel extends JLabel{
    public ImageLabel(ImageIcon icon){
        super.setIcon(icon);
    }
    private AlphaComposite cmp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1);
    private float alpha;

    public void setAlpha(float alpha) {
        this.alpha = alpha;
        if (isVisible())   paintImmediately(getBounds());
    }

    @Override
    protected void paintComponent(Graphics g) {
        // TODO Auto-generated method stub
        Graphics2D g2d = (Graphics2D)g;
        g2d.setComposite(cmp.derive(alpha));
        super.paintComponent(g2d);

    }
}