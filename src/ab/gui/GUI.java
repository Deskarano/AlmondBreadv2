package ab.gui;

import ab.fractal.Fractal;
import ab.fractal.Mandelbrot;
import ab.render.FractalRenderer;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.Toolkit;
import java.awt.Dimension;

public class GUI extends Application
{
    private int width;
    private int height;

    private Text coordinates;
    private ImageView underlay;
    private ImageView fractal;
    private ImageView overlay;

    private ProgressBar progress;

    private Fractal f;
    private FractalRenderer r;

    private int x1;
    private int y1;
    private int x2;
    private int y2;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        width = (int) screenSize.getWidth() - 75;
        height = (int) screenSize.getHeight() - 75;

        //width = 500;
        //height = 500;

        BorderPane GUI = constructGUI();
        Scene mainScene = new Scene(GUI);

        primaryStage.setScene(mainScene);
        primaryStage.setResizable(true);
        primaryStage.setTitle("AlmondBread v2");
        primaryStage.show();

        double x1 = -2 * (double) width / height;
        double y1 = 1.25;
        double x2 = .5 * (double) width / height;
        double y2 = -1.25;

        f = new Mandelbrot(x1, y1, x2, y2, width, height - 40);
        r = new FractalRenderer(progress.progressProperty(), 0xFFFFFF / Fractal.NUMITERATIONS, 0xFF000000);

        fractal.imageProperty().setValue(r.renderFractal(f));
    }

    private BorderPane constructGUI()
    {
        BorderPane GUI = new BorderPane();
        GUI.setPrefWidth(width);
        GUI.setPrefHeight(height);

        MenuBar menu = constructMenu();
        StackPane display = constructDisplay();
        AnchorPane footer = constructFooter();

        GUI.setTop(menu);
        GUI.setCenter(display);
        GUI.setBottom(footer);

        return GUI;
    }

    private MenuBar constructMenu()
    {
        MenuBar main = new MenuBar();
        main.setPrefHeight(20);

        Menu file = new Menu("File");
        MenuItem close = new MenuItem("Close");

        close.setOnAction(event ->
        {
            System.exit(0);
        });

        file.getItems().addAll(close);
        main.getMenus().addAll(file);
        return main;
    }

    private StackPane constructDisplay()
    {
        StackPane display = new StackPane();

        underlay = new ImageView();
        fractal = new ImageView();
        overlay = new ImageView();

        underlay.setFitWidth(width);
        underlay.setFitHeight(height - 40);
        underlay.setPreserveRatio(false);

        fractal.setFitWidth(width);
        fractal.setFitHeight(height - 40);
        fractal.setPreserveRatio(false);

        overlay.setFitWidth(width);
        overlay.setFitHeight(height - 40);
        overlay.setPreserveRatio(false);
        overlay.setImage(new OverlayImage(width, height - 40));

        overlay.setOnMouseMoved(event ->
        {
            x1 = (int) event.getX();
            y1 = (int) event.getY();

            coordinates.setText("(" + f.getXFromPixel(x1) + ", " + f.getYFromPixel(y1) + ")");
        });

        overlay.setOnMouseDragged(event ->
        {
            x2 = (int) event.getX();
            y2 = (int) event.getY();

            //adjust the x2 coordinate to fit the aspect ratio
            if(x2 > x1)
            {
                x2 = (int) ((double) width / (height - 40) * Math.abs(y1 - y2) + x1);
            }
            else
            {
                x2 = (int) (x1 - (double) width / (height - 40) * Math.abs(y1 - y2));
            }

            coordinates.setText("(" + f.getXFromPixel(x1) + ", " + f.getYFromPixel(y1) + ") to (" + f.getXFromPixel(x2) + ", " + f.getYFromPixel(y2) + ")");

            ((OverlayImage) overlay.getImage()).reset();
            ((OverlayImage) overlay.getImage()).rectangle(x1, y1, x2, y2, 0xFFFFFFFF);
        });

        overlay.setOnMouseReleased(event ->
        {
            try
            {
                ((OverlayImage) overlay.getImage()).reset();

                underlay.imageProperty().setValue(fractal.getImage());

                Rectangle2D rect = new Rectangle2D(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2));
                underlay.setViewport(rect);

                Fractal newF = new Mandelbrot(f.getXFromPixel(x1), f.getYFromPixel(y1), f.getXFromPixel(x2), f.getYFromPixel(y2), width, height - 40);
                fractal.imageProperty().setValue(r.renderFractal(newF));

                f = newF;
            }
            catch(Exception e)
            {
                System.out.println("!");
            }
        });

        display.getChildren().addAll(underlay, fractal, overlay);
        return display;
    }

    private AnchorPane constructFooter()
    {
        AnchorPane footer = new AnchorPane();
        footer.setPrefHeight(20);

        coordinates = new Text("Default");

        StackPane right = new StackPane();

        progress = new ProgressBar(0);
        progress.setPrefWidth(250);

        Text percent = new Text("Default");
        percent.textProperty().bind(Bindings.multiply(progress.progressProperty(), 100).asString("%.2f").concat("%"));

        right.getChildren().addAll(progress, percent);

        footer.getChildren().addAll(right);
        AnchorPane.setLeftAnchor(coordinates, 2.5);
        AnchorPane.setBottomAnchor(coordinates, 2.5);
        AnchorPane.setRightAnchor(right, 2.5);

        return footer;
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
