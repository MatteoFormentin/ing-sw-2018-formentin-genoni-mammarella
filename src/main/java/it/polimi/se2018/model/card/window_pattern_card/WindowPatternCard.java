package it.polimi.se2018.model.card.window_pattern_card;

import it.polimi.se2018.model.dice.Dice;
import it.polimi.se2018.model.dice.DiceColor;

/**
 * <strong>Class WindowPatternCard</strong>
 * <em>Description</em>
 * Class that define the Window Pattern Card.
 *
 * @author Davide Mammarella *
 * @version 1.0
 * @since 1.0
 * @author Luca Genoni only for the version 1.1
 * @version 1.1
 * @since 1.1
 */

public class WindowPatternCard {
    private String name;
    private int difficulty;
    private Cell[][] matrix;
    private boolean firstDice;
    /* set/get matrix name level
        insertDice controlla restrizioni adiacenza (al suo interno controlla restrizione cella
        insertDice controlla 3 boolean per 3 restrizioni
     */

    /**
     * Method <strong>Matrix</strong>
     * <em>Description</em>
     * create a WindowPatternCard given the name, difficulty and the matrix
     *
     * @param name of the WindowPatternCard
     * @param difficulty of the WindowPatternCard
     * @param matrix of Cell of the WindowPatternCard
     */
    WindowPatternCard(String name,int difficulty,Cell[][] matrix){
        this.name=name;
        this.difficulty=difficulty;
        this.matrix=matrix;
        this.firstDice=true;
    }
    WindowPatternCard(){
        this.name=null;
        this.difficulty=0;
        this.matrix=null;
        this.firstDice=true;
    }


    /**
     * Method <strong>getName</strong>
     * <em>Description</em>
     * Get the name of the Window Pattern Card.
     */
    public String getName() {
        return name;
    }

    /**
     * Method <strong>setName</strong>
     * <em>Description</em>
     * Set a name for the Window Pattern Card.
     *
     * @param name assigned to the Window Pattern Card
     */
    void setName(String name) {
        this.name = name;
    }

    /**
     * Method <strong>getDifficulty</strong>
     * <em>Description</em>
     * Get the getDifficulty of the Window Pattern Card.
     */
    public int getDifficulty() {
        return difficulty;
    }
    /**
     * Method <strong>setDifficulty</strong>
     * <em>Description</em>
     * Set a difficulty for the Window Pattern Card.
     *
     * @param difficulty assigned to the Window Pattern Card
     */
    void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
    /**
     * Method <strong>getDifficulty</strong>
     * <em>Description</em>
     * Get the matrix of the Window Pattern Card.
     */
    public Cell[][] getMatrix() {
        return matrix;
    }
    /**
     * Method <strong>setDifficulty</strong>
     * <em>Description</em>
     * Set a matrix for the Window Pattern Card.
     *
     * @param matrix [4][5] to assigned to the Window Pattern Card
     */
    void setMatrix(Cell[][] matrix) {
        this.matrix = matrix;
    }
    /**
     * Method <strong>getDifficulty</strong>
     * <em>Description</em>
     * Get the matrix of the Window Pattern Card.
     */
    public Dice getDice(int line,int colum) {
        return matrix[line][colum].getDice();
    }
    public void removeDice(int line,int colum){
        matrix[line][colum].setDice(null);
    }
    /**
     * Method <strong>checkMatrixAdjacentRestriction</strong>
     * <em>Description</em>
     * Check if the dice is allowed based on the restriction imposed from the adjacent cell.
     *
     * @return true if the Adjacent is ok
     */


    private boolean checkMatrixAdjacentRestriction(int line, int column) {
        if (firstDice){
            return(column==0||column==4||line==0||line==1);
        }else{
            if (column!=0) if(matrix[line][column - 1].getDice()!=null) return true;
            if (column!=4) if(matrix[line][column + 1].getDice()!=null) return true;
            if (line!=0) if(matrix[line - 1][column].getDice()!=null) return true;
            if (line!=3) if(matrix[line + 1][column].getDice()!=null) return true;
            return false;
        }
    }

    /**
     * Method <strong>checkMatrixColorRestriction</strong>
     * <em>Description</em>
     * Check if the dice is allowed based on the color restriction of the matrix.
     *
     * @return true if the color is ok
     */
    private boolean checkMatrixAdjacentColorRestriction(int line, int column, DiceColor diceColor) {

        if (column!=0) if(matrix[line][column - 1].getDice().getColor()==diceColor) return false;
        if (column!=4) if(matrix[line][column + 1].getDice().getColor()==diceColor) return false;
        if (line!=0) if(matrix[line - 1][column].getDice().getColor()==diceColor) return false;
        if (line!=3) if(matrix[line + 1][column].getDice().getColor()==diceColor) return false;
        return true;
    }

    /**
     * Method <strong>checkMatrixValueRestriction</strong>
     * <em>Description</em>
     * Check if the dice is allowed based on the value restriction of the matrix.
     *
     * @return true if the value is ok
     */
    private boolean checkMatrixAdjacentValueRestriction(int line, int column, int diceValue) {
        if (column!=0) if(matrix[line][column - 1].getDice().getValue()==diceValue) return false;
        if (column!=4) if(matrix[line][column + 1].getDice().getValue()==diceValue) return false;
        if (line!=0) if(matrix[line - 1][column].getDice().getValue()==diceValue) return false;
        if (line!=3) if(matrix[line + 1][column].getDice().getValue()==diceValue) return false;
        return true;
    }

    /**
     * /**
     * Method <strong>insertDice</strong>
     * <em>Description</em>
     * insertDice for the player or all the restriction on
     *
     * @param line index [0,3] of the WindowsPattern
     * @param column index [0,4] of the WindowsPattern
     * @param dice to insert in the WindowsPattern
     * @return true if the insertDice is ok, false if can't insert the dice
     */
    public boolean insertDice(int line, int column, Dice dice) {
        try{
            if(checkMatrixAdjacentRestriction(line,column) &&
                    checkMatrixAdjacentValueRestriction(line,column,dice.getValue()) &&
                    checkMatrixAdjacentColorRestriction(line,column,dice.getColor()))
                matrix[line][column].insertDice(dice);
            //else there is an exception
        }catch(Exception Exception) {
            // can't place the dice
            return false;
        }
        firstDice=false;
        return true;
    }

    /**
     * /**
     * Method <strong>insertDice</strong>
     * <em>Description</em>
     * insertDice for the tool card or to negate some restriction
     *
     * @param line index [0,3] of the WindowsPattern
     * @param column index [0,4] of the WindowsPattern
     * @param dice to insert in the WindowsPattern
     * @param colorRestriction true if the Restriction need to be respected, false if it is ignored:
     *                         colorRestriction->the dice respect the restriction, == !valueRestriction || respect the restriction
     *                         if this logic produce true can insert the dice
     * @param valueRestriction true if the Restriction need to be respected, false if it is ignored:
     *                         valueRestriction->the dice respect the restriction, == !valueRestriction || respect the restriction
     *                         if this logic produce true can insert the dice
     * @param adjacentRestriction true if the Restriction need to be respected, false if the Restriction need to be violated
     *                            adjacentRestriction==the dice respect the restriction
     *                            if this logic produce true can insert the dice
     * @return true if the insertDice is ok, false if can't insert the dice
     */
    public boolean insertDice(int line, int column, Dice dice, boolean adjacentRestriction,
                              boolean colorRestriction, boolean valueRestriction) {
        try{
            if (adjacentRestriction != checkMatrixAdjacentRestriction(line,column)) throw new Exception();
            if (colorRestriction && !checkMatrixAdjacentColorRestriction(line,column,dice.getColor())) throw new Exception();
            if (valueRestriction && !checkMatrixAdjacentValueRestriction(line,column,dice.getValue())) throw new Exception();
            //if All the restriction of the WindowPatter are ok then
            matrix[line][column].insertDice(dice);
            //else there is an exception insertDice can throw the exception of the cell
        }catch(Exception Exception) {
            return false;// can't place the dice
        }
        firstDice=false;
        return true;
    }


}