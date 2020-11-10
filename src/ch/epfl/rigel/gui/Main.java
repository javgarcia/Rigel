package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.AsterismLoader;
import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.sound.SkySoundManager;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.converter.LocalTimeStringConverter;
import javafx.util.converter.NumberStringConverter;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;


/**
 * Main class that manages and runs the application.
 *
 * @author Victor Nazianzeno -- Le Jamtel(312075)
 * @author Javier García Arredondo(311166)
 */
public class Main extends Application {

    // Constants for the different file paths.
    private final static String HYG_CATALOGUE_NAME = "/hygdata_v3.csv", AST_CATALOGUE_NAME = "/asterisms.txt", AWESOME_FONT_NAME = "/Font Awesome 5 Free-Solid-900.otf";

    // Constants for the different unicodes.
    private final static String
            RESET_UNICODE = "\uf0e2", PLAY_UNICODE = "\uf04b", PAUSE_UNICODE = "\uf04c", GEAR_UNICODE = "\u2699", RIGHT_UNICODE = "\u2192", LEFT_UNICODE = "\u2190", UP_UNICODE = "\u2191", DOWN_UNICODE = "\u2193", KEYBOARD_UNICODE = "\u2328", MOUSE_TOGGLE_UNICODE = "\u21D6", PLUS_UNICODE = "\u002B", MINUS_UNICODE = "\u2212";

    // Constant for launch welcome screen method.
    private final static double BOTTOM_ANCHOR = 25d;

    // Numerical constants.
    private final static int TIMELINE_PERIOD = 100;
    private final static double CREDIT_HEIGHT = 330d, CREDIT_WIDTH = 480d, OPTION_HEIGHT = 189d, OPTION_WIDTH = 245d, PAD_SIZE = 100d;

    // Coordinate constants
    private final static GeographicCoordinates INIT_GEO_COORD = GeographicCoordinates.ofDeg(6.57, 46.52);
    private final static HorizontalCoordinates INIT_HORIZON_COORD = HorizontalCoordinates.ofDeg(180.000000000001, 15);
    // Text for the credit, initialized as a constant.
    private final static String CREDIT_TEXT_TITLE = "\nProjet Rigel - EPFL\n\n", CREDIT_TEXT =
            "Projet réalisé par Victor Nazianzeno et Javier García Arredondo \n"
                    + "dans le cadre du cours de programmation BA2 2020 de l'EPFL\n"
                    + "enseigné par le professeur Michel Schinz.\n\n"
                    + "Crédits :\n\n"
                    + "Musique : \n"
                    + "\"Rigel\", musique du canvas, composée par V.Nazianzeno.\n"
                    + "\"Cosmos\", musique de l'écran d'accueil, composée par V.Nazianzeno.\n\n"
                    + "Design de l'écran d'accueil effectué par J.García.\n\n"
                    + "Données numériques sur les principaux objets célestes :\n"
                    + "https://solarsystem.nasa.gov/\n"
                    + "https://www.wikipedia.org/\n\n"
                    + "Police et image de l'écran d'accueil :\n"
                    + "Gentium Basic, Open Font License.\n"
                    + "Image : https://pixabay.com/photos/galaxy-infinity-milky-way-orbit-1837306/\n";

    private Font fontAwesome;


    /**
     * Main method of Rigel.
     *
     * @param args the arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }


    /**
     * Starts the application.
     * Manages the JavaFX representation elements.
     *
     * @param primaryStage the stage within tha application is run.
     * @throws IOException if there is an input error with the setup of the observation instant panes.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {

        // Building of the Star Catalogue.
        StarCatalogue.Builder builder;
        StarCatalogue catalogue;

        // Loading the stars.
        try (InputStream hygStream = getClass().getResourceAsStream(HYG_CATALOGUE_NAME)) {
            builder = new StarCatalogue.Builder()
                    .loadFrom(hygStream, HygDatabaseLoader.INSTANCE);
        }

        // Loading the asterisms.
        try (InputStream astStream = getClass().getResourceAsStream(AST_CATALOGUE_NAME)) {
            catalogue = builder
                    .loadFrom(astStream, AsterismLoader.INSTANCE)
                    .build();
        }

        // Loading the font.
        try (InputStream fontStream = getClass()
                .getResourceAsStream(AWESOME_FONT_NAME)) {
            fontAwesome = Font.loadFont(fontStream, 15);
        }

        // Initialization of the Celestial data box loader.
        final CelestialDataManager celestialDataManager = new CelestialDataManager(primaryStage);

        SkySoundManager skySoundManager = new SkySoundManager();

        // Zoned date time setup.
        ZonedDateTime when = ZonedDateTime.now();
        DateTimeBean dateTimeBean = new DateTimeBean();
        dateTimeBean.setZonedDateTime(when);

        // Observer location setup.
        ObserverLocationBean observerLocationBean = new ObserverLocationBean();
        observerLocationBean.setCoordinates(
               INIT_GEO_COORD);

        // Viewing parameters setup.
        ViewingParametersBean viewingParametersBean = new ViewingParametersBean();
        viewingParametersBean.setCenter(
               INIT_HORIZON_COORD);
        viewingParametersBean.setFieldOfViewDeg(100);

        // Time animator setup.
        TimeAnimator timeAnimator = new TimeAnimator(dateTimeBean);

        // Construction of the Sky canvas manager.
        SkyCanvasManager canvasManager = new SkyCanvasManager(
                                                    catalogue,
                                                    dateTimeBean,
                                                    observerLocationBean,
                                                    viewingParametersBean,
                                                    celestialDataManager,
                                                    skySoundManager);

        // Stage setup.
        primaryStage.setTitle("Rigel");
        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(600);
        primaryStage.setX(200);
        primaryStage.setY(200);

        // Pane including the sky.
        Canvas sky = canvasManager.canvas();
        StackPane stackPane = new StackPane(sky);
        Pane skyPane = new Pane(stackPane);

        sky.widthProperty().bind(skyPane.widthProperty());
        sky.heightProperty().bind(skyPane.heightProperty());

        // Construction of the principal border pane and its components.
        BorderPane bp = new BorderPane();

        HBox controlBar = new HBox();
        bp.setTop(controlBar);

        BorderPane infoBar = new BorderPane();
        bp.setBottom(infoBar);

        bp.setCenter(skyPane);

        // Control and info bars setup.
        setupControlBar(controlBar, observerLocationBean, dateTimeBean, timeAnimator, stackPane, sky, canvasManager, skySoundManager);
        setupInfoBar(infoBar, canvasManager, viewingParametersBean);

        launchWelcomeScreen(primaryStage, bp, sky, skySoundManager);
        primaryStage.show();
        sky.requestFocus();
    }


    /**
     * Creates and launches the welcome screen of Rigel.
     *
     * @param primaryStage    the stage where the welcome screen is included.
     * @param bp              the border pane.
     * @param sky             the canvas.
     * @param skySoundManager the sky sound manager.
     * @throws IOException if there is an import error related to the pictures.
     */
    private void launchWelcomeScreen(Stage primaryStage, BorderPane bp, Canvas sky, SkySoundManager skySoundManager) throws IOException {
        AnchorPane startAP = new AnchorPane();

        Image hollowHomeImage;
        Image homeImage;
        Image speaker;

        // Loading the images.
        try (InputStream hollowHomeImageStream = getClass().getResourceAsStream("/welcome1.png")) {
            hollowHomeImage = new Image(hollowHomeImageStream);
        }
        try (InputStream homeImageStream = getClass().getResourceAsStream("/welcome2.png")) {
            homeImage = new Image(homeImageStream);
        }
        try (InputStream speakerImageStream = getClass().getResourceAsStream("/speaker.png")) {
            speaker = new Image(speakerImageStream, 15, 15, true, false);
        }

        // Creating the ImageViews.
        ImageView mainImageView = new ImageView(hollowHomeImage);
        ImageView speakerView = new ImageView(speaker);

        // Creation of the start Button.
        Button start = new Button("Start");
        start.setFont(fontAwesome);
        start.setPrefSize(100, 40);

        // Initialization of the volume slider.
        Slider volumeSlider = new Slider();
        createVolumeSlider(volumeSlider, skySoundManager.getHomeScreemVolumeProperty());

        startAP.getChildren().add(mainImageView);

        Scene scene = new Scene(startAP);

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);

        // Implementation of the Fade-in.
        FadeTransition fade = new FadeTransition(Duration.seconds(6.3), startAP);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setCycleCount(1);
        fade.play();
        skySoundManager.launchHomeScreenMusic();

        // Finish-event handling.
        fade.setOnFinished((e) -> {
            mainImageView.setImage(homeImage);
            startAP.getChildren().addAll(start, volumeSlider, speakerView);
            AnchorPane.setBottomAnchor(start, BOTTOM_ANCHOR);

            // Setting anchors of the children nodes.
            AnchorPane.setLeftAnchor(start, startAP.getWidth() / 2 - start.getPrefWidth() / 2);
            AnchorPane.setBottomAnchor(volumeSlider, BOTTOM_ANCHOR);
            AnchorPane.setLeftAnchor(volumeSlider, startAP.getWidth() - 150d);
            AnchorPane.setBottomAnchor(speakerView, BOTTOM_ANCHOR);
            AnchorPane.setLeftAnchor(speakerView, startAP.getWidth() - 170d);
        });

        // Button click listener, implementation of the Fade-out.
        start.setOnMouseClicked((e) -> {
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(4), startAP);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0.5);
            fadeOut.setCycleCount(1);
            fadeOut.play();

            // Disabling the button and removing the slider when the fade-out begins.
            start.setDisable(true);
            startAP.getChildren().removeAll(speakerView, volumeSlider);
            skySoundManager.endHomeMusic();

            // Finish-event handling.
            fadeOut.setOnFinished((e2) -> {
                scene.setRoot(bp);
                primaryStage.setResizable(true);
                sky.requestFocus();
            });
        });
    }


    /**
     * Sets up the control bar.
     *
     * @param hbox                 which includes all the components of the control bar.
     * @param observerLocationBean the observer location bean.
     * @param dateTimeBean         the date time bean.
     * @param timeAnimator         the time animator.
     * @param skyPane              the StackPane allowing layering of nodes on the canvas.
     * @param sky                  the sky canvas.
     * @param skyManager           the sky canvas manager.
     * @param skySoundManager      the sky sound manager.
     */
    private void setupControlBar(HBox hbox, ObserverLocationBean observerLocationBean, DateTimeBean dateTimeBean, TimeAnimator timeAnimator, StackPane skyPane, Canvas sky, SkyCanvasManager skyManager, SkySoundManager skySoundManager) {
        hbox.setStyle("-fx-spacing: 4; -fx-padding: 4;");

        // Setting up the observation position box
        HBox observationPos = new HBox();
        setupObserverCoordinates(observationPos, observerLocationBean);

        // Setting up the observation instant box
        HBox observationInstant = new HBox();
        setupObservationInstantPanes(observationInstant, dateTimeBean, timeAnimator);

        // Setting up the timeFlow box
        HBox timeFlow = new HBox();
        setupTimeFlowPanes(timeFlow, timeAnimator, dateTimeBean);

        // Setting up the option box
        HBox optionBox = new HBox();
        setupOptionBox(optionBox, skyPane, sky, skyManager, skySoundManager);

        // Setting up the credits box
        HBox creditBox = new HBox();
        setupCreditBox(creditBox, skyPane, sky);

        // Setting up the toggling direction box
        HBox directionBox = new HBox();
        setupDirectionTogglingBox(directionBox, skyPane, skyManager);

        // Creation of the box separators
        Separator separator = new Separator();
        separator.setOrientation(Orientation.VERTICAL);
        Separator separator1 = new Separator();
        separator1.setOrientation(Orientation.VERTICAL);
        Separator separator2 = new Separator();
        separator2.setOrientation(Orientation.VERTICAL);
        Separator separator3 = new Separator();
        separator3.setOrientation(Orientation.VERTICAL);
        Separator separator4 = new Separator();
        separator4.setOrientation(Orientation.VERTICAL);

        // Adding all the children nodes to the control bar
        hbox.getChildren().addAll(observationPos, separator, observationInstant, separator1, timeFlow, separator2, directionBox, separator3, optionBox, separator4, creditBox);
    }


    /**
     * Secondary method which sets up the observer coordinates component of the control bar.
     *
     * @param observationPos       the HBox including the observer location elements.
     * @param observerLocationBean the observer location bean.
     */
    private void setupObserverCoordinates(HBox observationPos, ObserverLocationBean observerLocationBean) {
        observationPos.setStyle("-fx-spacing: inherit;\n" +
                                "-fx-alignment: baseline-left;");

        Label longitudeLabel = new Label("Longitude (°) :");
        Label latitudeLabel = new Label("Latitude (°) :");

        TextField longitude = auxSetupObserverCoordinates(GeographicCoordinates::isValidLonDeg, observerLocationBean.lonDegProperty());
        TextField latitude = auxSetupObserverCoordinates(GeographicCoordinates::isValidLatDeg, observerLocationBean.latDegProperty());
        observationPos.getChildren().addAll(longitudeLabel, longitude, latitudeLabel, latitude);
    }


    /**
     * Auxiliary (assistant) method which helps on the formatting of the observer coordinates setup.
     *
     * @param predicate predicate testing the validity of the longitude / latitude given in argument.
     * @param sdp       the double property (containing the latitude or the longitude).
     * @return the text field including the formatting of the observer coordinates elements in the HBox.
     */
    private TextField auxSetupObserverCoordinates(Predicate<Double> predicate, SimpleDoubleProperty sdp) {
        NumberStringConverter stringConverter = new NumberStringConverter("#0.00");

        UnaryOperator<TextFormatter.Change> coordsFilter = (change -> {
            try {
                String newText = change.getControlNewText();
                double newCoordsDeg = stringConverter.fromString(newText).doubleValue();
                return (predicate.test(newCoordsDeg) ? change : null);

            } catch (Exception e) {
                return null;
            }
        });
        TextFormatter<Number> coordsTextFormatter = new TextFormatter<>(stringConverter, 0, coordsFilter);

        TextField coordsTextField = new TextField();
        coordsTextField.setStyle("-fx-pref-width: 60;\n" +
                                 "-fx-alignment: baseline-right;");

        coordsTextField.setTextFormatter(coordsTextFormatter);
        coordsTextFormatter.valueProperty().bindBidirectional(sdp);
        return coordsTextField;
    }


    /**
     * Secondary method which sets up the observation instant component of the control bar.
     *
     * @param observationInstant the HBox including the observation instant elements.
     * @param dateTimeBean       the date time bean.
     * @param timeAnimator       the time animator.
     */
    private void setupObservationInstantPanes(HBox observationInstant, DateTimeBean dateTimeBean, TimeAnimator timeAnimator) {
        observationInstant.setStyle("-fx-spacing: inherit;\n" +
                                    "-fx-alignment: baseline-left;");
        Label dateLabel = new Label("Date :");
        Label hourLabel = new Label("Heure :");

        DatePicker datePicker = new DatePicker();
        datePicker.valueProperty().bindBidirectional(dateTimeBean.dateProperty());
        datePicker.setStyle("-fx-pref-width: 120;");

        TextField hour = auxSetupHourField(dateTimeBean);

        ComboBox<ZoneId> zoneId = auxSetupZoneIdBox(dateTimeBean);
        observationInstant.getChildren().addAll(dateLabel, datePicker, hourLabel, hour, zoneId);

        bindInstantDisableProperty(dateLabel, datePicker, hourLabel, hour, zoneId, timeAnimator);
    }


    /**
     * Auxiliary (assistant) method which helps on the formatting of the hour field setup.
     *
     * @param dtb the date time bean.
     * @return the text field including the formatting of the hour field.
     */
    private TextField auxSetupHourField(DateTimeBean dtb) {
        DateTimeFormatter hmsFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        LocalTimeStringConverter stringConverter = new LocalTimeStringConverter(hmsFormatter, hmsFormatter);

        TextFormatter<LocalTime> timeFormatter = new TextFormatter<>(stringConverter);

        timeFormatter.valueProperty().bindBidirectional(dtb.timeProperty());

        TextField hourTextField = new TextField();
        hourTextField.setStyle("-fx-pref-width: 75;\n" +
                "-fx-alignment: baseline-right;");

        hourTextField.setTextFormatter(timeFormatter);
        return hourTextField;
    }


    /**
     * Auxiliary (assistant) method which sets up the zone id box of the observation instant.
     *
     * @param dtb the date time bean.
     * @return the combo box including the options for the zone id.
     */
    private ComboBox<ZoneId> auxSetupZoneIdBox(DateTimeBean dtb) {
        ComboBox<ZoneId> zoneIdComboBox = new ComboBox<>();
        zoneIdComboBox.valueProperty().bindBidirectional(dtb.zoneIdProperty());
        Set<String> zoneIdSet = ZoneId.getAvailableZoneIds();

        List<ZoneId> sortedZoneIdList = new ArrayList<>();
        zoneIdSet.stream().sorted().forEach(s -> sortedZoneIdList.add(ZoneId.of(s)));

        zoneIdComboBox.setItems(FXCollections.observableList(sortedZoneIdList));
        zoneIdComboBox.setStyle("-fx-pref-width: 180;");
        return zoneIdComboBox;
    }


    /**
     * Secondary method which sets up the time flow panes of the control bar.
     *
     * @param timeFlow     the HBox including the time flow elements.
     * @param timeAnimator the time animator.
     * @param dateTimeBean the date time bean.
     */
    private void setupTimeFlowPanes(HBox timeFlow, TimeAnimator timeAnimator, DateTimeBean dateTimeBean) {
        timeFlow.setStyle("-fx-spacing: inherit;");

        // Time accelerator box setup.
        ChoiceBox<NamedTimeAccelerator> timeAccBox = new ChoiceBox<>();
        timeAccBox.setItems(FXCollections.observableList(List.of(NamedTimeAccelerator.values())));
        timeAccBox.setValue(NamedTimeAccelerator.TIMES_300);

        timeAnimator.acceleratorProperty().bind(Bindings.select(timeAccBox.valueProperty(), "accelerator"));

        // Buttons controlling the time flow.
        Button resetButton = new Button(RESET_UNICODE);
        resetButton.setFont(fontAwesome);

        Button playPauseButton = new Button(PLAY_UNICODE);
        playPauseButton.setFont(fontAwesome);

        timeFlow.getChildren().addAll(timeAccBox, resetButton, playPauseButton);
        bindTimeFlowDisableProperty(resetButton, timeAccBox, timeAnimator);

        // Behaviour of the play and pause button.
        playPauseButton.setOnAction(event -> {
            if (timeAnimator.runningProperty().get()) {
                timeAnimator.stop();
                playPauseButton.setText(PLAY_UNICODE);
            } else {
                timeAnimator.start();
                playPauseButton.setText(PAUSE_UNICODE);
            }
        });

        // Behaviour of the reset button.
        resetButton.setOnAction(event -> dateTimeBean.setZonedDateTime(ZonedDateTime.now()));
    }


    /**
     * When the time animator is running, disables the user option of changing the date, hour and zone id.
     *
     * @param dateLabel     the label of the date.
     * @param datePicker    the picker of the date.
     * @param hourLabel     the label of the hour.
     * @param hourFieldText the field text for the hour.
     * @param zoneId        the combo box for the zone id.
     * @param timeAnimator  the time animator.
     */
    private void bindInstantDisableProperty(Label dateLabel, DatePicker datePicker, Label hourLabel, TextField hourFieldText, ComboBox<ZoneId> zoneId, TimeAnimator timeAnimator) {
        hourLabel.disableProperty().bind(timeAnimator.runningProperty());
        dateLabel.disableProperty().bind(timeAnimator.runningProperty());
        datePicker.disableProperty().bind(timeAnimator.runningProperty());
        hourFieldText.disableProperty().bind(timeAnimator.runningProperty());
        zoneId.disableProperty().bind(timeAnimator.runningProperty());
    }


    /**
     * When the time animator is running, disables the user option of changing the accelerator and clicking the reset button.
     *
     * @param reset        the reset button.
     * @param accBox       the choice box of the accelerator.
     * @param timeAnimator the time animator.
     */
    private void bindTimeFlowDisableProperty(Button reset, ChoiceBox<NamedTimeAccelerator> accBox, TimeAnimator timeAnimator) {
        reset.disableProperty().bind(timeAnimator.runningProperty());
        accBox.disableProperty().bind(timeAnimator.runningProperty());
    }


    /**
     * Setup method for the information bar.
     *
     * @param infoBar               the border pane containing the elements of the information bar.
     * @param canvasManager         the sky canvas manager.
     * @param viewingParametersBean the viewing parameters bean.
     */
    private void setupInfoBar(BorderPane infoBar, SkyCanvasManager canvasManager, ViewingParametersBean viewingParametersBean) {
        infoBar.setStyle("-fx-padding: 4;\n" +
                         "-fx-background-color: white; ");

        // FOV field.
        Text fieldOfViewText = new Text();
        fieldOfViewText.textProperty().bind(Bindings.format("Champ de vue : %.1fº", viewingParametersBean.fieldOfViewDegProperty()));

        // Closest object field.
        Text closestObjectText = new Text();
        canvasManager.objectUnderMouseProperty().addListener(
                (p, o, n) -> {
                    if (n != null)
                        closestObjectText.setText(canvasManager.getObjectUnderMouse().info());
                    else if (canvasManager.getObjectUnderMouse() == null) {
                        closestObjectText.setText(" ");
                    }
                });

        // Mouse coordinates field.
        Text mousePosText = new Text();
        mousePosText.textProperty().bind(Bindings.format("Azimut : %.2f°, hauteur : %.2f°",
                canvasManager.mouseAzDegProperty(), canvasManager.mouseAltDegProperty()));

        // Spatial arrangement of the elements of the information bar.
        infoBar.setLeft(fieldOfViewText);
        infoBar.setCenter(closestObjectText);
        infoBar.setRight(mousePosText);
    }


    /**
     * Setup method for the option button and the option pane.
     *
     * @param option          the box containing the option button
     * @param skyPane         the StackPane containing the canvas and the option pane.
     * @param sky             sky canvas
     * @param skyManager      the sky canvas manager
     * @param skySoundManager the sky sound manager
     */
    private void setupOptionBox(HBox option, StackPane skyPane, Canvas sky, SkyCanvasManager skyManager, SkySoundManager skySoundManager) {
        // Setting up the option button.
        Button optionButton = new Button(GEAR_UNICODE);
        option.getChildren().add(optionButton);

        //Setting up the option pane vertical box.
        VBox optionPane = new VBox();
        auxSetupOptionPane(optionPane, skyManager, sky, skySoundManager);


        // Setting up the toggling of the option pane.
        optionButton.setOnAction(event -> {
            if (skyPane.getChildren().contains(optionPane)) {
                skyPane.getChildren().remove(optionPane);
            } else {
                skyPane.getChildren().add(optionPane);
            }
        });
    }


    /**
     * Auxiliary (assistant) setup method initializing the option pane toggle by the option button
     *
     * @param optionPane      the option pane, vertical box.
     * @param skyManager      the ssky canvas manager
     * @param sky             the sky canvas
     * @param skySoundManager the sky sound manager.
     */
    private void auxSetupOptionPane(VBox optionPane, SkyCanvasManager skyManager, Canvas sky, SkySoundManager skySoundManager) {
        // Style configuration.
        optionPane.setStyle("-fx-background-color: rgba(100, 100, 100, 0.3); -fx-background-radius: 10;");
        optionPane.setSpacing(10);
        optionPane.setPadding(new Insets(10, 5, 0, 5));

        //Binding the sky dimensions to the option vertical box dimensions.
        optionPane.setMaxWidth(OPTION_WIDTH);
        optionPane.setMaxHeight(OPTION_HEIGHT);
        sky.widthProperty().addListener((p, o, n) -> StackPane.setAlignment(optionPane, Pos.TOP_RIGHT));
        sky.heightProperty().addListener((p, o, n) -> StackPane.setAlignment(optionPane, Pos.TOP_RIGHT));


        // Setting up the notification volume slider.
        Text notifText = new Text();
        notifText.textProperty().bind(Bindings.format("FX volume : %.2f%s", skySoundManager.getNotifVolumeProperty().multiply(100), "%"));
        notifText.setStyle("-fx-fill: azure");

        Slider notifSlider = new Slider();
        createVolumeSlider(notifSlider, skySoundManager.getNotifVolumeProperty());


        // Setting up the ambient music volume slider.
        Text ambientText = new Text();
        ambientText.textProperty().bind(Bindings.format("Music volume : %.2f%s", skySoundManager.getAmbientVolumeProperty().multiply(100), "%"));
        ambientText.setStyle("-fx-fill: azure");

        Slider ambientSlider = new Slider();
        createVolumeSlider(ambientSlider, skySoundManager.getAmbientVolumeProperty());


        // Setting up the drawing choice radio buttons.
        Text drawingText = new Text("Drawing choice : ");
        drawingText.setStyle("-fx-fill: azure");

        RadioButton drawingAsterisms = new RadioButton("Asterisms");
        drawingAsterisms.setStyle("-fx-text-fill: azure");

        RadioButton drawingConstellations = new RadioButton("Constellations");
        drawingConstellations.setStyle("-fx-text-fill: azure");

        ToggleGroup drawingChoice = new ToggleGroup();
        drawingAsterisms.setToggleGroup(drawingChoice);
        drawingConstellations.setToggleGroup(drawingChoice);

        // Binding the radio buttons with the drawing constellation boolean.
        drawingAsterisms.setOnAction(event -> skyManager.setDrawConstellation(false));
        drawingConstellations.setOnAction(event -> skyManager.setDrawConstellation(true));

        // Creation of the separators.
        Separator separator1 = new Separator();
        separator1.setOrientation(Orientation.HORIZONTAL);
        Separator separator2 = new Separator();
        separator2.setOrientation(Orientation.HORIZONTAL);

        //  Setting up the buttons inside the option pane.
        HBox drawingBox = new HBox(drawingAsterisms, drawingConstellations);
        drawingBox.setSpacing(5);

        // Adding all the children nodes inside the option vertical box.
        optionPane.getChildren().addAll(notifText, notifSlider, separator1, ambientText, ambientSlider, separator2, drawingText, drawingBox);
    }


    /**
     * Auxiliary (assistant) method creating a volume slider and binding its value to a volume property.
     *
     * @param slider         the given slider.
     * @param volumeProperty the volume property binded to the slider's value.
     */
    private void createVolumeSlider(Slider slider, DoubleProperty volumeProperty) {
        slider.setMin(0);
        slider.setMax(1);
        slider.valueProperty().bindBidirectional(volumeProperty);
    }


    /**
     * Setup method for the credit button and the option pane.
     *
     * @param creditBox the credit horizontal box containing the credit button
     * @param skyPane   the StackPane containing the credit vertical pane.
     * @param sky       the sky canvas
     */
    private void setupCreditBox(HBox creditBox, StackPane skyPane, Canvas sky) {

        // Setting up the credit button.
        Button creditButton = new Button(PLUS_UNICODE);
        creditBox.getChildren().addAll(creditButton);

        // Setting up the credit box.
        VBox creditPane = new VBox();
        auxSetupCreditPane(creditPane, sky);

        // Setting up the  toggling of the credit pane.
        creditButton.setOnAction(event -> {

            if (skyPane.getChildren().contains(creditPane)) {
                creditButton.setText(PLUS_UNICODE);
                skyPane.getChildren().remove(creditPane);
            } else {
                creditButton.setText(MINUS_UNICODE);
                skyPane.getChildren().add(creditPane);
            }
        });
    }


    /**
     * Auxiliary (assistant) method initializing the credit pane toggle by the credit button.
     *
     * @param creditPane the credit pane, vertical box.
     * @param sky        the sky canvas.
     */
    private void auxSetupCreditPane(VBox creditPane, Canvas sky) {

        // Setting up the credit pane design and dimension.
        creditPane.setStyle("-fx-background-color: rgba(90, 90, 90, 0.5); -fx-background-radius: 10;");
        sky.widthProperty().addListener((p, o, n) -> StackPane.setAlignment(creditPane, Pos.TOP_LEFT));
        sky.heightProperty().addListener((p, o, n) -> StackPane.setAlignment(creditPane, Pos.TOP_LEFT));
        creditPane.setMaxHeight(CREDIT_HEIGHT);
        creditPane.setMaxWidth(CREDIT_WIDTH);
        creditPane.setPadding(new Insets(0, 0, 0, 10));

        // Construction of the credit text.
        Text creditTitle = new Text(CREDIT_TEXT_TITLE);
        Text creditBody = new Text(CREDIT_TEXT);
        creditTitle.setStyle("-fx-font-weight:normal; -fx-fill: azure; -fx-font-size: 16");
        creditBody.setStyle("-fx-font-weight:normal; -fx-fill: azure; -fx-font-size: 13");
        creditTitle.setFont(fontAwesome);
        creditBody.setFont(fontAwesome);
        creditPane.getChildren().addAll(creditTitle, creditBody);
    }


    /**
     * Setup method for the mouse directional pad and the mouse toggle button.
     *
     * @param directionBox     the horizontal box containing the toggle button
     * @param skyPane          the stack pane containing the mouse pad
     * @param skyCanvasManager the sky canvas manager.
     */
    private void setupDirectionTogglingBox(HBox directionBox, StackPane skyPane, SkyCanvasManager skyCanvasManager) {

        // Setting up the credit button.
        Button directionButton = new Button(KEYBOARD_UNICODE);
        directionBox.getChildren().addAll(directionButton);

        // Setting up the 4 directional buttons.
        Button right = new Button(RIGHT_UNICODE);
        setButtonListener(right, "RIGHT", skyCanvasManager);

        Button left = new Button(LEFT_UNICODE);
        setButtonListener(left, "LEFT", skyCanvasManager);

        Button up = new Button(UP_UNICODE);
        setButtonListener(up, "UP", skyCanvasManager);

        Button down = new Button(DOWN_UNICODE);
        setButtonListener(down, "DOWN", skyCanvasManager);

        // Setting up the grid pane representing the directional pad.
        GridPane directionalPad = new GridPane();

        directionalPad.add(up, 1, 0);
        directionalPad.add(left, 0, 1);
        directionalPad.add(right, 2, 1);
        directionalPad.add(down, 1, 2);
        directionalPad.setMaxHeight(PAD_SIZE);
        directionalPad.setMaxWidth(PAD_SIZE);
        directionalPad.setPadding(new Insets(0, 15, 15, 0));

        skyPane.getChildren().add(directionalPad);
        StackPane.setAlignment(directionalPad, Pos.BOTTOM_RIGHT);

        // Setting up the toggling of the directional mouse pad.
        directionButton.setOnAction(event -> {
            if (skyPane.getChildren().contains(directionalPad)) {
                directionButton.textProperty().setValue(MOUSE_TOGGLE_UNICODE);
                skyPane.getChildren().remove(directionalPad);
            } else {
                directionButton.textProperty().setValue(KEYBOARD_UNICODE);
                skyPane.getChildren().add(directionalPad);
            }
        });
    }


    /**
     * Auxiliary (assistant) method initializing the directional buttons and their listeners
     *
     * @param b                the given button.
     * @param dir              the direction it represents
     * @param skyCanvasManager the sky canvas manager.
     */
    private void setButtonListener(Button b, String dir, SkyCanvasManager skyCanvasManager) {

        b.setMinSize(38, 38);
        b.setOpacity(0.5);

        // Setting up a timeline allowing the repetition of the direction movement while the button is pressed.
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(TIMELINE_PERIOD), actionEvent -> skyCanvasManager.updateDirection(dir)));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.stop();

        // Different event handler for the button.
        b.setOnMousePressed(event -> timeline.play());
        b.setOnMouseReleased(event -> timeline.stop());
        b.setOnMouseClicked(event -> skyCanvasManager.updateDirection(dir));

        // Button opacity event handler.
        b.hoverProperty().addListener((p, o, n) -> {
            if (n) {
                b.setOpacity(1);
            } else {
                b.setOpacity(0.5);
                timeline.stop();
            }
        });

        // The direction is set to IDLE as soon as the timeline is stopped.
        timeline.statusProperty().addListener((p, o, n) -> {
            if (timeline.getStatus() == Animation.Status.STOPPED) {
                skyCanvasManager.updateDirection("IDLE");
            }
        });
    }
}