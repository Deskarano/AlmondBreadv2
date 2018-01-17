package ab.fractal;

public abstract class Fractal
{
    public static final int NUMITERATIONS = 16384;

    private int xSize;
    private int ySize;

    private double xScale;
    private double yScale;

    private double topLeftX;
    private double topLeftY;

    public Fractal(double x1, double y1, double x2, double y2, int xSize, int ySize)
    {
        this.xSize = xSize;
        this.ySize = ySize;

        this.xScale = Math.abs(x1 - x2) / xSize;
        this.yScale = Math.abs(y1 - y2) / ySize;

        this.topLeftX = Math.min(x1, x2);
        this.topLeftY = Math.max(y1, y2);
    }

    public double getXFromPixel(int pixel)
    {
        return topLeftX + xScale * pixel;
    }

    public double getYFromPixel(int pixel)
    {
        return topLeftY - yScale * pixel;
    }

    public int getPixelFromX(double x)
    {
        return (int) (Math.abs(x - topLeftX) / xScale);
    }

    public int getPixelFromY(double y)
    {
        return (int) (Math.abs(y - topLeftY) / yScale);
    }

    public int getxSize()
    {
        return xSize;
    }

    public int getySize()
    {
        return ySize;
    }

    public abstract int iteratePoint(double x, double y);
}
