package Model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.Random;

public class Board {
    private int height;
    private int width;
    private int minecount; //number of total mines on the board
    private SimpleIntegerProperty minesLeftModel;; //number of unflagged mines left
    private int cellsleft; //number of unopened cells left
    private Cell[][] board; //used to display # of mines left


    public Board(int height, int width, int minecount){
        this.height = height;
        this.width = width;
        this.minecount = minecount;
        this.minesLeftModel = new SimpleIntegerProperty(minecount);
        this.cellsleft = height * width;

        board = new Cell[height][width];
        initialize_board();
    }

    private void initialize_board() {
        for(int r = 0; r < height; r++) {
            for(int c = 0; c < width; c++) {
                board[r][c] = new Cell();
            }
        }

        Random rnd = new Random();
        //set mines
        int i = 0;
        while(i < minecount) {
            //TODO: Optimize this function, there are more efficient ways
            //seed mine
            int seed = rnd.nextInt(width * height);
            int r = seed / width;
            int c = seed % width;
            //place mine if valid seed
            if(!board[r][c].getMine()) {
                board[r][c].setMine();
                i++;
            }
        }

        for(int r= 0; r < height; r++) {
            for(int c= 0; c < width; c++) {
                if(board[r][c].getMine()) { //for all neighbors of a mine cell
                    if(r-1 >= 0 && c-1 >= 0)
                        board[r-1][c-1].incrementValue();
                    if(r-1 >= 0)
                        board[r-1][c].incrementValue();
                    if(r-1 >= 0 && c+1 < width)
                        board[r-1][c+1].incrementValue();
                    if(c-1 >= 0)
                        board[r][c-1].incrementValue();
                    if(c+1 < width)
                        board[r][c+1].incrementValue();
                    if(r+1 < height && c-1 >= 0)
                        board[r+1][c-1].incrementValue();
                    if(r+1 < height)
                        board[r+1][c].incrementValue();
                    if(r+1 < height && c+1 < width)
                        board[r+1][c+1].incrementValue();
                }
            }
        }
    }

    // ------------------------------------ Execute Moves ----------------------------------------
    public void putFlag(int r, int c) {
        if(board[r][c].getFlipped()) {
            return;
        } else if(!board[r][c].getFlagged()) {
            board[r][c].setFlagged(true);
            minesLeftModel.set(minesLeftModel.get() - 1);
        } else {
            board[r][c].setFlagged(false);
            minesLeftModel.set(minesLeftModel.get() + 1);
        }
    }

    private void openFlood(int r, int c) {
        if(r-1 >= 0 && c-1 >= 0 && !board[r-1][c-1].getFlipped() && !board[r-1][c-1].getMine())
            openSingle(r-1, c-1);
        if(r-1 >= 0 && !board[r-1][c].getFlipped() && !board[r-1][c].getMine())
            openSingle(r-1, c);
        if(r-1 >= 0 && c+1 < width && !board[r-1][c+1].getFlipped() && !board[r-1][c+1].getMine())
            openSingle(r-1, c+1);
        if(c-1 >= 0 && !board[r][c-1].getFlipped() && !board[r][c-1].getMine())
            openSingle(r, c-1);
        if(c+1 < width && !board[r][c+1].getFlipped() && !board[r][c+1].getMine())
            openSingle(r, c+1);
        if(r+1 < height && c-1 >= 0 && !board[r+1][c-1].getFlipped() && !board[r+1][c-1].getMine())
            openSingle(r+1, c-1);
        if(r+1 < height && !board[r+1][c].getFlipped() && !board[r+1][c].getMine())
            openSingle(r+1, c);
        if(r+1 < height && c+1 < width && !board[r+1][c+1].getFlipped() && !board[r+1][c+1].getMine())
            openSingle(r+1, c+1);
    }

    public boolean openSingle(int r, int c) {
        if(board[r][c].getFlipped()) { //flipped = true -> do nothing.
            return true;
        } else { //cell isOpened=false
            if (board[r][c].getFlagged())
                return true;
            else if(board[r][c].getMine()) { // mine = true -> oops, lost.
                System.out.println("You opened a MINE!");
                return false;
            } else { //If cell hasMine=false
                board[r][c].setFlipped();
                cellsleft--;
                if(board[r][c].getValue() == 0) //value > 0 -> flood
                    openFlood(r, c);
                return true;
            }
        }
    }


    public boolean openCascade(int r, int c) {
        if(!board[r][c].getFlipped()) { //flipped = false -> do nothing.
            return true;
        } else { //cell flipped=true
            if(board[r][c].getValue() == 0) { //value == 0 -> do nothing.
                return true;
            } else { //cell neighbourMineCount > 0
                int neighborFlags = countNeighborFlags(r, c);
                if(neighborFlags != board[r][c].getValue())
                    return true; //value != neighbour flagged = true -> do nothing.
                else if(!validateFlaggedMines(r, c))
                    return false; // neighbor mines != neighbor flagged -> oops, lost.
                else {
                    openFlood(r, c);
                    return true;
                }
            }
        }
    }

    private int countNeighborFlags(int r, int c) {
        int sum = 0;
        if(r-1 >= 0 && c-1 >= 0 && board[r-1][c-1].getFlagged())
            sum++;
        if(r-1 >= 0 && board[r-1][c].getFlagged())
            sum++;
        if(r-1 >= 0 && c+1 < width && board[r-1][c+1].getFlagged())
            sum++;
        if(c-1 >= 0 && board[r][c-1].getFlagged())
            sum++;
        if(c+1 < width && board[r][c+1].getFlagged())
            sum++;
        if(r+1 < height && c-1 >= 0 && board[r+1][c-1].getFlagged())
            sum++;
        if(r+1 < height && board[r+1][c].getFlagged())
            sum++;
        if(r+1 < height && c+1 < width && board[r+1][c+1].getFlagged())
            sum++;
        return sum;
    }

    private boolean validateFlaggedMines(int r, int c) {
        if(r-1 >= 0 && c-1 >= 0)
            if((board[r-1][c-1].getFlagged() != board[r-1][c-1].getMine()))
                return false;
        if(r-1 >= 0)
            if((board[r-1][c].getFlagged() != board[r-1][c].getMine()))
                return false;
        if(r-1 >= 0 && c+1 < width)
            if(board[r-1][c+1].getFlagged() != board[r-1][c+1].getMine())
                return false;
        if(c-1 >= 0)
            if(board[r][c-1].getFlagged() != board[r][c-1].getMine())
                return false;
        if(c+1 < width)
            if(board[r][c+1].getFlagged() != board[r][c+1].getMine())
                return false;
        if(r+1 < height && c-1 >= 0)
            if(board[r+1][c-1].getFlagged() != board[r+1][c-1].getMine())
                return false;
        if(r+1 < height)
            if(board[r+1][c].getFlagged() != board[r+1][c].getMine())
                return false;
        if(r+1 < height && c+1 < width)
            if(board[r+1][c+1].getFlagged() != board[r+1][c+1].getMine())
                return false;
        return true;
    }

    public boolean boardComplete() {
        if(cellsleft != minecount)
            return false;
        else
            return true;
    }

    // ------------------------------------ Print Board ----------------------------------------
    public void printSolution() {
        System.out.print("  ");
        for(int c= 0; c < width; c++) {
            System.out.print(c);
        }
        System.out.println();
        System.out.print("--");
        for(int c= 0; c < width; c++) {
            System.out.print("-");
        }
        System.out.println();
        for(int r= 0; r < height; r++) {
            System.out.print(r + "|");
            for(int c= 0; c < width; c++) {
                if(board[r][c].getMine())
                    System.out.print("*");
                else
                    System.out.print(board[r][c].getValue());
            }
            System.out.println();
        }
        System.out.println();
    }

    public void printPlay() {
        System.out.println("Mines-left: " + minesLeftModel.get() + ", Cells-left: " + cellsleft);
        for(int r= 0; r < height; r++) {
            for(int c= 0; c < width; c++) {
                if(board[r][c].getFlagged())
                    System.out.print(" F");
                else if(!board[r][c].getFlipped())
                    System.out.print(" -");
                else { //cell was flipped
                    if(board[r][c].getMine())
                        System.out.print(" *");
                    else if (board[r][c].getValue() == 0)
                        System.out.print(" +");
                    else
                        System.out.print(" " + board[r][c].getValue());
                }
            }
            System.out.println();
        }
    }

    // --------------------------------- GUI Ref Properties -------------------------------------

    public SimpleIntegerProperty minesLeftProperty() {
        return minesLeftModel;
    }

    public SimpleStringProperty valueProperty(int r, int c) {
        return board[r][c].valueProperty();
    }
}