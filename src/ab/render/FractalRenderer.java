package ab.render;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.concurrent.Task;
import javafx.scene.image.WritableImage;

import ab.fractal.Fractal;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class FractalRenderer
{
    public static final int NUMTHREADS = 1;

    class ResultWriter extends AnimationTimer
    {
        private BlockingQueue<int[]> results;
        private WritableImage img;
        private int numTotal;

        private int numRendered;

        ResultWriter(BlockingQueue<int[]> messages, WritableImage img, int numTotal)
        {
            this.results = messages;
            this.img = img;
            this.numTotal = numTotal;

            numRendered = 0;
        }

        @Override
        public void handle(long now)
        {
            List<int[]> pointsToDraw = new ArrayList<>();
            results.drainTo(pointsToDraw, 2000);

            for(int[] pixel : pointsToDraw)
            {
                img.getPixelWriter().setArgb(pixel[0], pixel[1], getColor(pixel[2]));
                progress.setValue(progress.doubleValue() + 1.0 / numTotal);

                numRendered++;
            }

            if(numRendered >= numTotal)
            {
                progress.setValue(0);
                stop();
            }
        }
    }

    class RenderTask extends Task<Void>
    {
        private Fractal f;
        private boolean[] pointsToRender;
        private BlockingQueue<int[]> results;

        private Random random;

        RenderTask(Fractal f, boolean[] pointsToRender, BlockingQueue<int[]> results)
        {
            this.f = f;
            this.pointsToRender = pointsToRender;
            this.results = results;

            random = new Random();
        }

        private int getNextPoint()
        {
            int initI = random.nextInt(pointsToRender.length);
            int i = initI;

            while(i != initI - 1)
            {
                if(!pointsToRender[i])
                {
                    pointsToRender[i] = true;
                    return i;
                }

                i++;

                if(i == pointsToRender.length)
                {
                    i = 0;
                }
            }

            return -1;
        }

        @Override
        protected Void call() throws Exception
        {
            int i = getNextPoint();

            while(i != -1)
            {
                int x = i % f.getxSize();
                int y = (int) ((double) i / f.getxSize());

                int iterations = f.iteratePoint(f.getXFromPixel(x), f.getYFromPixel(y));
                results.put(new int[]{x, y, iterations});

                i = getNextPoint();
            }

            return null;
        }
    }

    private List<Integer> colors;
    private double m;
    private double b;

    private DoubleProperty progress;

    public FractalRenderer(DoubleProperty progress, List<Integer> colors)
    {
        this.colors = colors;
        this.progress = progress;
    }

    public FractalRenderer(DoubleProperty progress, double m, double b)
    {
        this.m = m;
        this.b = b;
        this.progress = progress;
    }

    public WritableImage renderFractal(Fractal f)
    {
        WritableImage result = new WritableImage(f.getxSize(), f.getySize());
        boolean[] pointsToRender = new boolean[f.getxSize() * f.getySize()];
        BlockingQueue<int[]> results = new LinkedBlockingQueue<>();

        new ResultWriter(results, result, f.getxSize() * f.getySize()).start();

        Platform.runLater(() ->
        {
            for(int i = 0; i < NUMTHREADS; i++)
            {
                new Thread(new RenderTask(f, pointsToRender, results)).start();
            }
        });

        return result;
    }

    private int getColor(int iterations)
    {
        if(iterations == -1)
        {
            return 0xFF000000;
        }
        if(colors == null)
        {
            return (int) (m * iterations+ b);
        }
        else
        {
            return colors.get(iterations % colors.size());
        }
    }
}
