package it.polimi.se2018.model.card.objective_public_card;

import it.polimi.se2018.model.card.window_pattern_card.Cell;
import it.polimi.se2018.model.card.window_pattern_card.WindowPatternCard;
import it.polimi.se2018.model.dice.Dice;

/**
 * Public objective card per Sfumature Scure.
 * <p>
 * Description
 * Set di 5 & 6 ovunque
 *
 * @author Matteo Formentin
 */
public class DarkNumber extends ObjectivePublicCard {
    public DarkNumber() {
        super();
        super.setId(6);
        super.setName("Sfumature Scure");
        super.setDescription("Set di 5 & 6 ovunque");
        super.setPoint(2);
    }

    @Override
    public int calculatePoint(WindowPatternCard windowPatternCard) {
        Cell[][] matrix = windowPatternCard.getMatrix();
        int points;
        int five = 0;
        int six = 0;
        Dice currentCellDice;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                currentCellDice = matrix[i][j].getDice();
                if (currentCellDice == null) continue;
                if (currentCellDice.getValue() == 5) {
                    five++;
                }
                if (currentCellDice.getValue() == 6) {
                    six++;
                }
            }
        }
        points = super.getPoint() * Math.min(five, six);
        return points;
    }
}
