package it.polimi.se2018.model.card.objective_public_card;

import it.polimi.se2018.model.card.window_pattern_card.Cell;
import it.polimi.se2018.model.card.window_pattern_card.WindowPatternCard;
import it.polimi.se2018.model.dice.Dice;

/**
 * Public objective card Sfumature Medie.
 * <p>
 * Description
 * Set di 3 & 4 ovunque
 *
 * @author Matteo Formentin
 */
public class MidNumber extends ObjectivePublicCard {
    public MidNumber() {
        super();
        super.setId(5);
        super.setName("Sfumature Medie");
        super.setDescription("Set di 3 & 4 ovunque");
        super.setPoint(2);
    }

    @Override
    public int calculatePoint(WindowPatternCard windowPatternCard) {
        Cell[][] matrix = windowPatternCard.getMatrix();
        int points;
        int three = 0;
        int four = 0;
        Dice currentCellDice;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                currentCellDice = matrix[i][j].getDice();
                if (currentCellDice == null) continue;
                if (currentCellDice.getValue() == 3) {
                    three++;
                }
                if (currentCellDice.getValue() == 4) {
                    four++;
                }
            }
        }
        points = super.getPoint() * Math.min(three, four);
        return points;
    }
}
