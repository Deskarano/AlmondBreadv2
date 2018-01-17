package ab.gui;

import javafx.scene.image.WritableImage;

public class OverlayImage extends WritableImage
{
    private static final boolean LINE = false;
    private static final boolean RECTANGLE = true;

    private boolean lastOp;
    private int lastX1;
    private int lastY1;
    private int lastX2;
    private int lastY2;

    public OverlayImage(int width, int height)
    {
        super(width, height);

        for(int i = 0; i < width; i++)
        {
            for(int j = 0; j < height; j++)
            {
                getPixelWriter().setArgb(i, j, 0x01000000);
            }
        }
    }

    public void line(int x1, int y1, int x2, int y2, int argb)
    {
        if(Math.abs(x2 - x1) > Math.abs(y2 - y1))
        {
            double slope = (double) (y2 - y1) / Math.abs(x2 - x1);

            for(int i = 0; i < Math.abs(x2 - x1); i++)
            {
                try
                {
                    getPixelWriter().setArgb(x1 + i * sign(x2 - x1), (int) (y1 + (double) i * slope), argb);
                }
                catch(Exception e)
                {
                }
            }
        }
        else
        {
            double slope = (double) (x2 - x1) / Math.abs(y2 - y1);

            for(int i = 0; i < Math.abs(y2 - y1); i++)
            {
                try
                {
                    getPixelWriter().setArgb((int) (x1 + (double) i * slope), y1 + i * sign(y2 - y1), argb);
                }
                catch(Exception e)
                {

                }
            }
        }

        lastOp = LINE;
        lastX1 = x1;
        lastY1 = y1;
        lastX2 = x2;
        lastY2 = y2;
    }

    public void rectangle(int x1, int y1, int x2, int y2, int argb)
    {
        line(x1, y1, x2, y1, argb);
        line(x2, y1, x2, y2, argb);
        line(x2, y2, x1, y2, argb);
        line(x1, y2, x1, y1, argb);

        lastOp = RECTANGLE;
        lastX1 = x1;
        lastY1 = y1;
        lastX2 = x2;
        lastY2 = y2;
    }

    public void reset()
    {
        if(lastOp == LINE)
        {
            line(lastX1, lastY1, lastX2, lastY2, 0x01000000);
        }
        else
        {
            rectangle(lastX1, lastY1, lastX2, lastY2, 0x01000000);
        }
    }

    private int sign(double num)
    {
        if(num < 0) return -1;
        if(num > 0) return 1;
        return 0;
    }
}
