package it.polimi.se2018.view.gui.stage;


import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


import static it.polimi.se2018.view.gui.GuiInstance.getGuiInstance;


/**
 * Class for handle the setup of the connection
 *
 * @author Luca Genoni
 */
public class SetUpConnection {
    private Stage stage;

    /**
     * Constructor
     *
     * @param owner of the stage for this class
     */
    public SetUpConnection(Stage owner) {
        stage = new Stage(StageStyle.UTILITY);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(owner);
        stage.setResizable(false);
    }

    /**
     * Method for display the stage
     */
    public void display() {
        GridPane form = new GridPane();
        Scene scene = new Scene(form, 250, 150);
        stage.setScene(scene);

        //gridPane design
        form.setAlignment(Pos.CENTER);
        form.setHgap(5);
        form.setVgap(10);


        //GridPane children
        TextField ipInput = new TextField();
        Label ip = new Label("Server IP :");
        ip.setLabelFor(ipInput);
        form.addRow(0, ip, ipInput);
        //port
        final ToggleGroup group = new ToggleGroup();

        RadioButton rb1 = new RadioButton("RMI");
        rb1.setUserData(0);
        rb1.setToggleGroup(group);
        rb1.setSelected(true);


        RadioButton rb2 = new RadioButton("SOCKET");
        rb2.setUserData(1);
        rb2.setToggleGroup(group);

        form.addRow(1, rb1, rb2);
        //escape
        Button connect = new Button("Connect");
        Button back = new Button("Back");
        form.addRow(2, back, connect);
        //components action
        connect.setOnAction(e -> {
            int i = Integer.parseInt(group.getSelectedToggle().getUserData().toString());
            try {
                getGuiInstance().getClient().startClient(ipInput.getText(), i);
                stage.close();
            } catch (Exception ex) {
                new AlertMessage(stage).displayMessage(ex.getMessage());
            }
        });
        back.setOnAction(e -> stage.close());
        stage.showAndWait();
    }
}
