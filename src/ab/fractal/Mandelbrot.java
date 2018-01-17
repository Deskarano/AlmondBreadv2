package ab.fractal;

public class Mandelbrot extends Fractal
{
    public Mandelbrot(double x1, double y1, double x2, double y2, int xSize, int ySize)
    {
        super(x1, y1, x2, y2, xSize, ySize);
    }

    @Override
    public int iteratePoint(double pointX, double pointY)
    {
        int i = 0;
        boolean escaped = false;

        double x = 0;
        double y = 0;

        while(i < Fractal.NUMITERATIONS && !escaped)
        {
            double newX = x * x - y * y;
            double newY = 2 * x * y;

            newX += pointX;
            newY += pointY;

            x = newX;
            y = newY;

            escaped = x > 2 || y > 2;

            i++;
        }

        if(escaped)
        {
            return i;
        }
        else
        {
            return -1;
        }
    }
}
