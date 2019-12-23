package Model;

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

    public Cell() {
        this.mine = false;
        this.value = 0;
        this.flipped = false;
        this.flagged = false;
    }

    protected void setMine() {
        mine = true;
    }

    protected void incrementValue() {
        value++;
    }

    protected void setFlipped() { //FIXME: How to resolve flag status?
        flipped = true;
    }

    protected void setFlagged(boolean toggle) {
        flagged = toggle;
    }

    protected boolean getMine() { //TODO: revert back to if-then
        return mine;
    }

    protected int getValue() {
        return value;
    }

    protected boolean getFlipped() { //TODO: revert back to if-then
        return flipped;
    }

    protected boolean getFlagged() {
        return flagged;
    }
}
