package ch.epfl.rigel.gui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Class managing the celestial information loading and the launching of the information windows.
 *
 * @author Victor Nazianzeno -- Le Jamtel(312075)
 * @author Javier García Arredondo(311166)
 */
public final class CelestialDataManager {

    private final static Charset C = StandardCharsets.UTF_8;

    // File name.
    private final static String CELESTIAL_FILE_NAME = "/celestial.txt";

    private final List<String[]> celestObjectData = new ArrayList<>();
    private static final List<String> INFO_LABELS = new ArrayList<>();

    // Information windows stage.
    private final Stage infoStage = new Stage();
    // Information windows scene.
    private final Scene scene;


    /**
     * Constructor for the celestial data manager.
     *
     * @param mainStage the main stage of the program.
     */
    public CelestialDataManager(Stage mainStage) throws IOException {
        // Loads the information from the celestial object file.
        buildCelestialObjectsList();
        scene = new Scene(new GridPane());

        // Bind the close request of the main stage with the information windows close request.
        mainStage.setOnCloseRequest(event ->
                infoStage.close());
        infoStage.setAlwaysOnTop(true);
        infoStage.setResizable(false);
        infoStage.setTitle("Information");
    }


    /**
     * Loads the celestial information from the celestial file.
     *
     * @throws IOException if there is an input error.
     */
    private void buildCelestialObjectsList() throws IOException {
        String line;
        try (BufferedReader r = new BufferedReader(new InputStreamReader(CelestialDataManager.class.getResourceAsStream(CELESTIAL_FILE_NAME), C))) {

            while ((line = r.readLine()) != null) {
                if (line.charAt(0) != '#') {
                    if (!line.startsWith("Nom"))
                        celestObjectData.add(line.split(","));
                    else {
                        // Caption for each information is added to the infoLabels list.
                        INFO_LABELS.addAll(List.of(line.split(",")));
                    }
                }
            }
        }
    }


    /**
     * Getter for all the main celestial names.
     *
     * @return a list of string containing the celestial object names.
     */
    public List<String> getCelestialNames() {
        List<String> names = new ArrayList<>();
        for (String[] objData : celestObjectData) {
            names.add(objData[0]);
        }
        return names;
    }


    /**
     * Main method launching the information windows containing the given celestial object information.
     *
     * @param celestialObject the given celestial object.
     */
    public void launchInfoWindow(String celestialObject) {
        scene.setRoot(getObjectInfoBox(celestialObject));
        infoStage.setScene(scene);
        infoStage.show();
    }


    /**
     * Checks if the windows has been launched, i.e if it is visible.
     *
     * @return true if the stage is visible.
     */
    public boolean hasLaunched() {
        return infoStage.isShowing();
    }


    /**
     * Creates the information box given a celestial object.
     *
     * @param celestialObject the given celestial object.
     * @return the built gridPane.
     */
    private GridPane getObjectInfoBox(String celestialObject) {
        List<String> objectInfo;
        for (String[] objData : celestObjectData) {
            if (celestialObject.equalsIgnoreCase(objData[0])) {
                objectInfo = List.of(objData);
                return buildInfoBox(objectInfo);
            }
        }
        // Considering the case when the given string is invalid, or the information about the given celestial object is not available.
        throw new IllegalArgumentException("invalid celestial object");
    }


    /**
     * Adds the information in an empty information box.
     *
     * @param celestialInfos the given celestial objects information.
     * @return the build gridPane.
     */
    private GridPane buildInfoBox(List<String> celestialInfos) {
        GridPane infoBox = emptyInfoBox();
        List<Label> labels = new ArrayList<>();
        celestialInfos.forEach(c -> {
                    Label l = new Label(c);
                    l.setStyle("-fx-text-fill: azure; -fx-font-size: 14;");
                    labels.add(l);
                }
        );
        for (int i = 0; i < labels.size(); i++) {
            infoBox.add(labels.get(i), 2, i + 2);
        }

        return infoBox;
    }


    /**
     * Constructs an empty information box.
     *
     * @return an empty information containing the caption labels.
     */
    private GridPane emptyInfoBox() {

        // Initialization of the box.
        GridPane emptyBox = new GridPane();
        emptyBox.setStyle("-fx-background-color: #202020; -fx-hgap: 20; -fx-vgap: 20; -fx-min-height: 160; -fx-min-width: 300;");
        emptyBox.setPadding(new Insets(5, 5, 5, 5));
        emptyBox.setBorder(new Border(new BorderStroke(Color.valueOf("#171717"), BorderStrokeStyle.SOLID, null, new BorderWidths(10))));

        Label title = new Label("Celestial Object Information");
        title.setStyle("-fx-text-fill: azure; -fx-font-size: 16;");
        emptyBox.add(title, 0, 0, 3, 1);
        for (int i = 0; i < INFO_LABELS.size(); i++) {
            Label label = new Label(INFO_LABELS.get(i));
            label.setStyle("-fx-text-fill: azure; -fx-font-size: 14;");
            emptyBox.add(label, 0, i + 2);
        }
        return emptyBox;
    }
}

