package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.CelestialObject;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;
import ch.epfl.rigel.sound.SkySoundManager;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.transform.Transform;

import java.util.List;
import java.util.Optional;

/**
 * Canvas manager, creates the canvas, handles the user interactions and paints the sky in consequence.
 *
 * @author Victor Nazianzeno -- Le Jamtel(312075)
 * @author Javier García Arredondo(311166)
 */
public final class SkyCanvasManager {

    private final Canvas canvas;
    private final DoubleBinding mouseAzDeg, mouseAltDeg, dilatationFactor;
    private final ObjectBinding<CelestialObject> objectUnderMouse;

    // The current altitude of the viewing parameters, in degrees, and rounded by steps of 5 integers.
    private final IntegerBinding roundedViewAltDeg;

    private final SkyCanvasPainter painter;

    private final ObjectBinding<StereographicProjection> projection;
    private final ObjectBinding<ObservedSky> observedSky;
    private final ObjectBinding<Transform> planeToCanvas;

    // Maximum distance for the closest object method, expressed in the stereographic plane.
    private final static int MAX_DISTANCE = 10, CANVAS_INIT_WIDTH = 1100, CANVAS_INIT_HEIGHT = 600, AZIMUTH_SHIFT = 10, ALTITUDE_SHIFT = 5;

    // Field of view interval.
    private final static ClosedInterval ZOOM_INTERVAL = ClosedInterval.of(30, 150);
    // Interval for the Horizontal coordinates azimuth.
    private final static RightOpenInterval AZ_INTERVAL_DEG = RightOpenInterval.of(0, 360);
    // Interval for the Horizontal coordinates altitude.
    private final static ClosedInterval ALT_INTERVAL_DEG = ClosedInterval.of(-30, 90);

    private final List<String> names;
    private final CelestialDataManager celestialDataManager;
    private final SkySoundManager skySoundManager;

    private final BooleanProperty drawConstellation;
    private final ViewingParametersBean viewingParametersBean;


    /**
     * Class constructor, initializes the bindings between all the properties and the listeners.
     *
     * @param catalogue             the stars and asterisms catalogue.
     * @param dateTimeBean          the bean representing the zoned date time.
     * @param observerLocationBean  bean describing the observer's position.
     * @param viewingParametersBean bean determining the visible portion of the sky, i.e the field of view.
     * @param celestialDataLoader the celestial data loader used for launching the information windows.
     * @param skySoundManager the sky sound manager.
     */
    public SkyCanvasManager(StarCatalogue catalogue, DateTimeBean dateTimeBean, ObserverLocationBean observerLocationBean, ViewingParametersBean viewingParametersBean, CelestialDataManager celestialDataLoader, SkySoundManager skySoundManager) {

        this.viewingParametersBean = viewingParametersBean;
        this.celestialDataManager = celestialDataLoader;
        names = celestialDataLoader.getCelestialNames();
        // Initialization of the canvas.
        canvas = new Canvas(CANVAS_INIT_WIDTH, CANVAS_INIT_HEIGHT);
        // Initialization of the sky painter given the created canvas.
        painter = new SkyCanvasPainter(canvas);

        this.skySoundManager = skySoundManager;

        // Mouse position, described as a point on a plane.
        ObjectProperty<Point2D> mousePosition = new SimpleObjectProperty<>();

        drawConstellation = new SimpleBooleanProperty(true);


        /* CREATION OF THE DIFFERENT BINDINGS */

        // Projection binding, depends on the coordinates of the projection center.
        projection = Bindings.createObjectBinding(() ->
                        new StereographicProjection(viewingParametersBean.getCenter()), viewingParametersBean.centerProperty());

        // The observed sky, depends on the date time bean, the coordinates of the observer, the stereographic projection, and the stars and asterisms catalogue.
        observedSky = Bindings.createObjectBinding(() ->
                        new ObservedSky(dateTimeBean.getZonedDateTime(), observerLocationBean.getCoordinates(), projection.get(), catalogue),
                        dateTimeBean.dateProperty(), dateTimeBean.timeProperty(), dateTimeBean.zoneIdProperty(), observerLocationBean.coordinatesProperty(), projection);

        // Scaling factor used by the Transform object.
        dilatationFactor = Bindings.createDoubleBinding(() ->
                            canvas.getWidth() / projection.get().applyToAngle(Angle.ofDeg(viewingParametersBean.getFieldOfViewDeg())),
                            projection, viewingParametersBean.fieldOfViewDegProperty());

        // Transform object, link between the projection coordinates and the canvas coordinates.
        // Represents a scaling and a translation.
        planeToCanvas = Bindings.createObjectBinding(() ->
                          Transform.affine(dilatationFactor.get(), 0, 0, -dilatationFactor.get(),
                                     canvas.getWidth() / 2, canvas.getHeight() / 2),
                          dilatationFactor, canvas.widthProperty(), canvas.heightProperty());


        // Positions of the mouse in the horizontal coordinates system, non null.
        ObjectBinding<HorizontalCoordinates> mouseHorizontalPosition =
                Bindings.createObjectBinding(() -> {
                    //Avoiding NullPointerException when using inverseTransform.
                    if (mousePosition.get() != null) {
                        CartesianCoordinates inverseCoordinates =
                                CartesianCoordinates.of(planeToCanvas.get().inverseTransform(mousePosition.get()));

                        return projection.get().inverseApply(inverseCoordinates);
                    }
                    // Initial coordinates are set to (0,0) while the mouse position on the canvas hasn't been updated.
                    return HorizontalCoordinates.of(0, 0);
                }, planeToCanvas, mousePosition, projection);


        // Properties describing the azimuth and altitude of the previous coordinates.
        mouseAzDeg = Bindings.createDoubleBinding(() -> mouseHorizontalPosition.get().azDeg(), mouseHorizontalPosition);
        mouseAltDeg = Bindings.createDoubleBinding(() -> mouseHorizontalPosition.get().altDeg(), mouseHorizontalPosition);


        // Describe the nearest celestial object to non-null mouse coordinates.
        objectUnderMouse = Bindings.createObjectBinding(() -> {
            //Avoiding NullPointerException when using inverseTransform
            if (mousePosition.get() != null) {
                // Applying the inverse transformation from the canvas coordinates to the stereographic ones.
                double maxDistance = Math.abs(planeToCanvas.get().inverseDeltaTransform(MAX_DISTANCE, 0).getX());

                Optional<CelestialObject> closestObject =
                        observedSky.get().objectClosestTo(CartesianCoordinates.of(
                                                                planeToCanvas.get().inverseTransform(mousePosition.get())), maxDistance);

                return closestObject.orElse(null);
            } else {
                return null;
            }
        }, observedSky, mousePosition, planeToCanvas);


        // The current altitude of the viewing parameters, in degrees, and rounded by steps of 5 integers.
        roundedViewAltDeg = Bindings.createIntegerBinding(() -> {
                                double currentViewAltDeg = Angle.toDeg(viewingParametersBean.getCenter().alt());
                                return (currentViewAltDeg >= 0) ? (int)(currentViewAltDeg + 0.5) : (int) (currentViewAltDeg - 0.5);
                            }, viewingParametersBean.centerProperty(), viewingParametersBean.fieldOfViewDegProperty());


        // Initialization of the different keyboard and mouse listeners.
        initializeEventListeners(canvas, mousePosition);


        // Creates the different listeners in order to be able to update the sky painter.
        createListeners(observedSky, dilatationFactor, planeToCanvas);
    }


    /**
     * Initializes the different listeners, in order to call the sky painter after any change.
     *
     * @param observedSky      the property containing the sky that can be observed by the user.
     * @param dilatationFactor the scale factor property.
     * @param planeToCanvas    the transform property.
     */
    private void createListeners(ObjectBinding<ObservedSky> observedSky, DoubleBinding dilatationFactor, ObjectBinding<Transform> planeToCanvas) {
        observedSky.addListener((p, o, n) -> updatePainter());
        dilatationFactor.addListener((p, o, n) -> updatePainter());
        planeToCanvas.addListener((p, o, n) -> updatePainter());
        drawConstellation.addListener((p,o,n) -> updatePainter());
    }


    /**
     * Calls to all the sky canvas painter methods, update of the drawn sky.
     */
    public void updatePainter() {
        // Reset of the canvas.
        painter.clear();

        //Drawing of the sky on the canvas.
        painter.drawParallels(projection.get(), planeToCanvas.get(), canvas.getWidth());
        painter.drawMeridians(projection.get(), planeToCanvas.get(), roundedViewAltDeg.get(), canvas.getWidth(), canvas.getHeight());
        painter.drawStars(observedSky.get(), planeToCanvas.get(),drawConstellation.get());
        painter.drawPlanets(observedSky.get(), planeToCanvas.get());
        painter.drawMoon(observedSky.get(), projection.get(), planeToCanvas.get());
        painter.drawSun(observedSky.get(), projection.get(), planeToCanvas.get());
        painter.drawHorizon(projection.get(), planeToCanvas.get(), roundedViewAltDeg.get(), canvas.getWidth());
    }


    /**
     * Calls to the different event listeners methods.
     *
     * @param canvas                the canvas.
     * @param mousePosition the mouse position property.
     */
    private void initializeEventListeners(Canvas canvas, ObjectProperty<Point2D> mousePosition) {
        // Mouse left click listener, allow keyboard event.
        setMouseClickEvent(canvas);

        // Keys event, sky rotation implementation.
        setKeyEvent(canvas);

        // Mouse scroll event, zoom implementation.
        setScrollEvent(canvas);

        // Mouse position event, retrieving its coordinates on the canvas.
        setMousePositionEvents(canvas, mousePosition);
    }


    /**
     * Initializes the mouse position listener in order to get its coordinates on the canvas.
     *
     * @param canvas        the canvas.
     * @param mousePosition the mouse position, described as a point on the plane.
     */
    private void setMousePositionEvents(Canvas canvas, ObjectProperty<Point2D> mousePosition) {
        canvas.setOnMouseMoved(event -> mousePosition.set(new Point2D(event.getX(), event.getY())));

    }


    /**
     * Initializes the mouse click listener in order to allow future keyboard events.
     *
     * @param canvas the canvas.
     */
    private void setMouseClickEvent(Canvas canvas) {
        canvas.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown()) {
                canvas.requestFocus();
                if (getObjectUnderMouse() != null) {
                    if (names.contains(getObjectUnderMouse().name())) {
                        skySoundManager.launchNotifSound(celestialDataManager.hasLaunched());
                        celestialDataManager.launchInfoWindow(getObjectUnderMouse().name());

                    }
                }
            }
        });
    }


    /**
     * Initializes the keys listener in order to implement the sky rotation functionality.
     *
     * @param canvas                the canvas.
     */
    private void setKeyEvent(Canvas canvas) {

        canvas.setOnKeyPressed(event -> {

            // 4 cases depending on the pressed key.
            event.consume();
            switch (event.getCode()) {

                case LEFT:
                    updateDirection("LEFT");
                    break;

                case RIGHT:
                    updateDirection("RIGHT");
                    break;

                case UP:
                    updateDirection("UP");
                    break;

                case DOWN:
                    updateDirection("DOWN");
                    break;

            }
        });
    }


    /**
     * Initializes the mouse scroll listener in order to implement the zoom functionality.
     *
     * @param canvas                the canvas.
     */
    private void setScrollEvent(Canvas canvas) {
        canvas.setOnScroll(event -> {
            double deltaX = event.getDeltaX();
            double deltaY = event.getDeltaY();
            double maxAbs = Math.max(Math.abs(deltaX), Math.abs(deltaY));

            if (maxAbs == Math.abs(deltaX)) {
                viewingParametersBean.setFieldOfViewDeg(ZOOM_INTERVAL.clip(viewingParametersBean.getFieldOfViewDeg() - deltaX));
            } else {
                viewingParametersBean.setFieldOfViewDeg(ZOOM_INTERVAL.clip(viewingParametersBean.getFieldOfViewDeg() - deltaY));
            }
        });
    }


    /**
     * Getter for the canvas.
     *
     * @return the canvas.
     */
    public Canvas canvas() {
        return canvas;
    }


    /**
     * Getter for the nearest celestial object property to the mouse coordinates.
     *
     * @return the property containing the celestial object.
     */
    public ObjectBinding<CelestialObject> objectUnderMouseProperty() {
        return objectUnderMouse;
    }

    /**
     * Getter for the nearest celestial object to the mouse coordinates.
     *
     * @return the celestial object.
     */
    public CelestialObject getObjectUnderMouse() {
        return objectUnderMouse.get();
    }

    /**
     * Getter for the mouse azimuth coordinate property.
     *
     * @return the azimuth property.
     */
    public DoubleBinding mouseAzDegProperty() {
        return mouseAzDeg;
    }

    /**
     * Getter for the mouse altitude coordinate property.
     *
     * @return the altitude property.
     */
    public DoubleBinding mouseAltDegProperty() {
        return mouseAltDeg;
    }


    /**
     * Setter for the constellation drawing boolean.
     * @param value new boolean value.
     */
    public void setDrawConstellation(boolean value){
        drawConstellation.setValue(value);
    }


    /**
     * Updates the direction shift depending on the given direction string.
     * @param newDirection direction string
     */
    public void updateDirection(String newDirection) {

        switch (newDirection) {

            case "LEFT":
                viewingParametersBean.setCenter(HorizontalCoordinates.ofDeg(
                        AZ_INTERVAL_DEG.reduce(viewingParametersBean.getCenter().azDeg() - AZIMUTH_SHIFT),
                        viewingParametersBean.getCenter().altDeg()));
                break;

            case "RIGHT":
                viewingParametersBean.setCenter(HorizontalCoordinates.ofDeg(
                        AZ_INTERVAL_DEG.reduce(viewingParametersBean.getCenter().azDeg() + AZIMUTH_SHIFT),
                        viewingParametersBean.getCenter().altDeg()));
                break;

            case "UP":
                viewingParametersBean.setCenter(HorizontalCoordinates.ofDeg(
                        viewingParametersBean.getCenter().azDeg(),
                        ALT_INTERVAL_DEG.clip(viewingParametersBean.getCenter().altDeg() + ALTITUDE_SHIFT)));
                break;

            case "DOWN":
                viewingParametersBean.setCenter(HorizontalCoordinates.ofDeg(
                        viewingParametersBean.getCenter().azDeg(),
                        ALT_INTERVAL_DEG.clip(viewingParametersBean.getCenter().altDeg() - ALTITUDE_SHIFT)));
                break;

                // Idle direction by default;
            default:
                break;
        }
    }
}
