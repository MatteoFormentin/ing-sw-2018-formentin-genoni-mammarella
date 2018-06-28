package it.polimi.se2018.view.gui;

import it.polimi.se2018.view.gui.gamestage.GuiGame;
import it.polimi.se2018.view.gui.gamestage.ShowValue;
import it.polimi.se2018.view.gui.stage.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import static it.polimi.se2018.view.gui.gamestage.GuiGame.createGuiGame;

/**
 * Main class for the Gui, where the JavaFx Application start.
 * Setup of the connection and start of the game.
 * Can't handle the EventView (that are related to the actual game)
 *
 * @author Luca Genoni
 */
public class GuiMain extends Application {
    private static Stage primaryStage;
    private GuiGame game;

    static void launchGui() {
        launch();
    }

    private static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    /**
     * The start of the JavaFx thread
     *
     * @param primaryStage received by the Application.launch() method
     * @throws Exception if something goes wrong during the construction of the application
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        setPrimaryStage(primaryStage);

        //creating a Group object
        VBox menu = new VBox();
        Scene startMenu = new Scene(menu, 779, 261);
        primaryStage.setScene(startMenu);
        //design menu stage
        primaryStage.initStyle(StageStyle.UNIFIED);
        primaryStage.setTitle("Launcher Sagrada");
        primaryStage.setResizable(false);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.centerOnScreen();
        primaryStage.setOnCloseRequest(e -> {
            e.consume();
            closeProgram();
        });
        BackgroundImage backgroundImage = new BackgroundImage(new Image("file:src/main/java/it/polimi/se2018/resources/Immagine.jpg", 779, 261, true, true),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        menu.setBackground(new Background(backgroundImage));
        menu.setAlignment(Pos.CENTER);
        menu.setSpacing(20);

        // add button to the menu
        Button playButton = new Button("Fai il Login e inizia una partita");
        playButton.setOnAction(e -> {

            boolean login = new Login(primaryStage).display();
            if (login) {
                game = createGuiGame();
                game.setGameWait(primaryStage);
            }
        });
        Button connectionButton = new Button("Impostazioni di rete");
        connectionButton.setOnAction(e -> new SetUpConnection(primaryStage).display());
        Button close = new Button();
        close.setText("Esci Dal Gioco");
        close.setOnAction(e -> closeProgram());

        menu.getChildren().addAll(playButton, connectionButton, close);

        //show the stage after the setup
        primaryStage.show();
        primaryStage.setAlwaysOnTop(false);
    }

    /**
     * Method for close the application
     */
    public static void closeProgram() {
        Boolean result = new ConfirmBox(primaryStage).displayMessage("Sei sicuro di voler uscire dal gioco?");
        if (result) {
            primaryStage.close();
            Platform.exit();
            System.err.println("Se non termina controllare i thread ancora attivi sul client divesi da quello della gui");
        }
    }
}
