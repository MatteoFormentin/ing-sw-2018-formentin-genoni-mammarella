package it.polimi.se2018.model;


import it.polimi.se2018.exception.GameException;
import it.polimi.se2018.exception.gameboard_exception.NoDiceException;
import it.polimi.se2018.exception.gameboard_exception.player_state_exception.*;
import it.polimi.se2018.exception.gameboard_exception.tool_exception.NoEnoughTokenException;
import it.polimi.se2018.exception.gameboard_exception.tool_exception.ValueDiceWrongException;
import it.polimi.se2018.exception.gameboard_exception.window_exception.WindowRestriction;
import it.polimi.se2018.model.card.objective_private_card.ObjectivePrivateCard;
import it.polimi.se2018.model.card.window_pattern_card.WindowPatternCard;
import it.polimi.se2018.model.dice.Dice;
import it.polimi.se2018.model.dice.DiceStack;

/**
 * Player state and data. his active dice in hand is in position 0, convention
 *
 * @author Matteo Formentin
 * @author Luca Genoni
 */
public class Player {
    private String nickname;
    private int indexInGame;
    private int favorToken;
    private int points;
    private ObjectivePrivateCard privateObject;
    private WindowPatternCard playerWindowPattern;
    private WindowPatternCard[] the4WindowPattern;
    private DiceStack handDice;
    private boolean firstTurn;
    private boolean hasDrawNewDice;
    private boolean hasPlaceANewDice;
    private boolean hasUsedToolCard;

    /**
     * Constructor for a new player.
     *
     * @param indexInGame ID of the player.
     * @param nickname    nickname of the player.
     */
    public Player(int indexInGame, String nickname) {
        this.indexInGame = indexInGame;
        this.nickname = nickname;
        favorToken = 0;
        points = 0;
        privateObject = null;
        playerWindowPattern = null;
        handDice = new DiceStack();
        firstTurn = true;
        hasDrawNewDice = false;
        hasPlaceANewDice = false;
        hasUsedToolCard = false;
    }
    //************************************getter**********************************************
    //************************************getter**********************************************
    //************************************getter**********************************************

    public int getIndexInGame() {
        return indexInGame;
    }

    public void setIndexInGame(int indexInGame) {
        this.indexInGame = indexInGame;
    }

    public String getNickname() {
        return nickname;
    }

    public int getFavorToken() {
        return favorToken;
    }

    public void setFavorToken(int favorToken) {
        this.favorToken = favorToken;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public ObjectivePrivateCard getPrivateObject() {
        return privateObject;
    }

    void setPrivateObject(ObjectivePrivateCard privateObject) {
        this.privateObject = privateObject;
    }

    WindowPatternCard[] getThe4WindowPattern() {
        return the4WindowPattern;
    }

    void setThe4WindowPattern(WindowPatternCard[] the4WindowPattern) {
        this.the4WindowPattern = the4WindowPattern;
    }

    public WindowPatternCard getPlayerWindowPattern() {
        return playerWindowPattern;
    }

    //************************************setter**********************************************
    //************************************setter**********************************************
    //************************************setter**********************************************

    public void setPlayerWindowPattern(WindowPatternCard playerWindowPattern) {
        this.playerWindowPattern = playerWindowPattern;
    }

    public DiceStack getHandDice() {
        return handDice;
    }

    public boolean isFirstTurn() {
        return firstTurn;
    }

    public void setFirstTurn(boolean firstTurn) {
        this.firstTurn = firstTurn;
    }

    public boolean isHasDrawNewDice() {
        return hasDrawNewDice;
    }

    public void setHasDrawNewDice(boolean hasDrawNewDice) {
        this.hasDrawNewDice = hasDrawNewDice;
    }

    public boolean isHasPlaceANewDice() {
        return hasPlaceANewDice;
    }

    public void setHasPlaceANewDice(boolean hasPlaceANewDice) {
        this.hasPlaceANewDice = hasPlaceANewDice;
    }

    public boolean isHasUsedToolCard() {
        return hasUsedToolCard;
    }

    public void setHasUsedToolCard(boolean hasUsedToolCard) {
        this.hasUsedToolCard = hasUsedToolCard;
    }

    /**
     * special setter for the windowPattern
     *
     * @param index of the chosen window
     */
    void choosePlayerWindowPattern(int index) {
        playerWindowPattern = the4WindowPattern[index];
        favorToken = the4WindowPattern[index].getDifficulty();
    }


    //************************************window's method**********************************************
    //************************************window's method**********************************************
    //************************************window's method**********************************************
    //************************************window's method**********************************************
    //************************************window's method**********************************************
    //************************************window's method**********************************************

    /**
     * Method to add a dice to hand.
     *
     * @param dice         the dice to add.
     * @param fromDicePool true if the dice come from dice pool.
     */
    void addDiceToHand(Dice dice, boolean fromDicePool) throws NoDiceException, AlreadyDrawANewDiceException {
        if (dice == null) throw new NoDiceException();
        if (fromDicePool && hasDrawNewDice) throw new AlreadyDrawANewDiceException();
        handDice.addFirst(dice);
        if (fromDicePool) hasDrawNewDice = true;
    }

    /**
     * Method used to insert the dice on the window pattern.
     *
     * @param line        index of the window's line.
     * @param column      index of the window's column.
     * @param adjacentR   true if need to be near a dice, false otherwise.
     * @param colorR      true if need to check this restriction.
     * @param valueR      true if need to check this restriction.
     * @param firstInsert true if it insert a new dice for the turn.
     * @throws WindowRestriction the specific exception of the insert.
     * @throws PlayerException   the exception regarding the state of the player.
     */
    public void insertDice(int line, int column, boolean adjacentR, boolean colorR, boolean valueR, boolean firstInsert)
            throws WindowRestriction, PlayerException {
        if (handDice.isEmpty()) throw new NoDiceInHandException();
        if (firstInsert && hasPlaceANewDice) throw new AlreadyPlaceANewDiceException();
        playerWindowPattern.insertDice(line, column, handDice.get(0), adjacentR, colorR, valueR);
        handDice.remove(0);
        if (firstInsert) hasPlaceANewDice = true;
    }

    /**
     * A method for activated the use of the toolCard methods
     * Check the state of the player and his money.
     *
     * @param cost of the tool card.
     */
    void useToolCard(int cost) throws AlreadyUseToolCardException, NoEnoughTokenException {
        if (cost > favorToken) throw new NoEnoughTokenException();
        hasUsedToolCard = true;
        favorToken -= cost;
    }

    void endTurn(boolean nextTurnIsATypeFirstTurn) {
        hasUsedToolCard = false;
        hasDrawNewDice = false;
        hasPlaceANewDice = false;
        firstTurn = nextTurnIsATypeFirstTurn;
    }


    //*********************************************Tool's method*************************************************
    //*********************************************Tool's method*************************************************
    //*********************************************Tool's method*************************************************
    //*********************************************Tool's method*************************************************
    //*********************************************Tool's method*************************************************
    //*********************************************Tool's method*************************************************


    /**
     * move the dice from the indicated coordinate by hand. Available when using a tool card
     *
     * @param line   index of the wind's line
     * @param column index of the window's column
     * @throws WindowRestriction if something isn't right
     */
    void removeDiceFromWindowAndAddToHand(int line, int column) throws WindowRestriction {
        Dice dice = playerWindowPattern.removeDice(line, column);
        handDice.addFirst(dice);
    }

    public Dice removeDiceFromHand() throws NoDiceInHandException {
        if (handDice.isEmpty()) throw new NoDiceInHandException();
        return handDice.remove(0);
    }

    /**
     * the player roll the active dice (index=0) in hand. Available when using a tool card
     */
    void rollDiceInHand() throws NoDiceInHandException {
        if (handDice.isEmpty()) throw new NoDiceInHandException();
        handDice.reRollAllDiceInStack();
    }

    /**
     * the player change the face of the dice. Available when using a tool card
     */
    void oppositeFaceDice() throws NoDiceInHandException, NoDiceException {
        if (handDice.isEmpty()) throw new NoDiceInHandException();
        handDice.getDice(0).oppositeValue();
    }

    /**
     * the player roll the active dice (index=0) in hand. Available when using a tool card
     *
     * @param increase true if the player want to increase the value, false for decrease
     */
    void increaseOrDecrease(boolean increase) throws NoDiceException, ValueDiceWrongException, NoDiceInHandException {
        if (handDice.isEmpty()) throw new NoDiceInHandException();
        handDice.getDice(0).increaseOrDecrease(increase);
    }


    void setValueDiceHand(int value) throws NoDiceException, ValueDiceWrongException, NoDiceInHandException {
        if (handDice.isEmpty()) throw new NoDiceInHandException();
        handDice.getDice(0).setValue(value);
    }


    /**
     * the player can now place a new dice but the second turn will be skipped. Available when using a tool card
     */
    public void endSpecialFirstTurn() throws GameException {
        if (!firstTurn) throw new GameException("non puoi usare questo effetto adesso");
        hasUsedToolCard = true;
        hasDrawNewDice = false;
        hasPlaceANewDice = false;
        firstTurn = false;
    }
}