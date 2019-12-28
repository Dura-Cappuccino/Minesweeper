package Model;

import javafx.beans.property.SimpleStringProperty;

public class Cell {
    /**
     * Var fields:
     * value (ie. a mine or a number (0-8))
     * status (solved or hidden) (flagged squares count as solved)
     */
    private boolean mine;
    private int value;
    private boolean flipped;
    private boolean flagged; //Note: only used for mine countdown, doesn't affect ending

    private SimpleStringProperty valueModel;

    public Cell() {
        this.mine = false;
        this.value = 0;
        this.flipped = false;
        this.flagged = false;
        valueModel = new SimpleStringProperty(" ");
    }

    protected void setMine() {
        mine = true;
    }

    protected void incrementValue() {
        value++;
    }

    protected void setFlipped() {
        flipped = true;
        //GUI bind-property requires this
        if (mine)
            valueModel.set("*");
        else if (value == 0)
            valueModel.set("+");
        else
            valueModel.set(Integer.toString(value));
    }

    protected void setFlagged(boolean toggle) {
        flagged = toggle;
        if(flagged)
            valueModel.set("F");
        else
            valueModel.set(" ");
    }

    protected boolean getMine() {
        return mine;
    }

    protected int getValue() {
        return value;
    }

    protected boolean getFlipped() {
        return flipped;
    }

    protected boolean getFlagged() {
        return flagged;
    }

    protected SimpleStringProperty valueProperty() {
        return valueModel;
    }
}
