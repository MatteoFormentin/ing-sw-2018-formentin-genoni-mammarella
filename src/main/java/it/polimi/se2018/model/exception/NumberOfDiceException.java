package it.polimi.se2018.model.exception;

public class NumberOfDiceException extends Exception {
    public  NumberOfDiceException(){
        super("Non ci sono abbastanza dadi nel sacchetto");
    }
}