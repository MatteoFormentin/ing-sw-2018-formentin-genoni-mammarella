package it.polimi.se2018.model.card.objective_private_card;

import it.polimi.se2018.model.card.windowPatternCard.WindowPatternCard;
import it.polimi.se2018.model.dice.DiceColor;

/**
 * Pivate objective card Sfumature Gialle.
 * <p>
 * Description
 * Somma dei valori su tutti i dadi gialli
 *
 * @author Matteo Formentin
 */
public class YellowObjectivePrivateCard extends ObjectivePrivateCard {
    public YellowObjectivePrivateCard() {
        super();
        super.setID(4);
        super.setDiceColor(DiceColor.Yellow);
        super.setName("Sfumature Gialle");
        super.setDescription("Somma dei valori su tutti i dadi gialli");
    }

    @Override
    public int calculatePoint(WindowPatternCard windowPatternCard) {
        return 0;
    }
}