package it.polimi.se2018.view.gui.stage;

import it.polimi.se2018.network.client.ClientController;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Login {
    private boolean answer;
    private Stage stage;

    public Login(Stage owner) {
        stage = new Stage(StageStyle.UTILITY);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(owner);
        stage.setResizable(false);
        answer=false;
    }
    public boolean display(ClientController client){
        GridPane form =new GridPane();
        Scene scene =new Scene(form,250,150);
        stage.setScene(scene);

        //stage design

        //gridPane design
        form.setAlignment(Pos.CENTER);
        form.setHgap(5);
        form.setVgap(10);


        //GridPane children
        TextField nameInput = new TextField();
        Label ip =new Label("Nickname:");
        ip.setLabelFor(nameInput);
        form.addRow(0,ip,nameInput);

        //escape
        Button connect = new Button("Login");
        Button back = new Button ("Back");
        form.addRow(2,back,connect);
        //components action
        connect.setOnAction(e-> {
                if(client.login(nameInput.getText())){
                    answer=true;
                    stage.close();
                }else{
                    answer=false;
                    new AlertMessage(stage).displayMessage("Non puoi eseguire il login");
                }
        });
        back.setOnAction(e->{
            stage.close();
        } );

        stage.showAndWait();
        return answer;
    }
}
