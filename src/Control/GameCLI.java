package Control;

import java.util.Scanner;
import Config.Config;
import Model.Board;

public class GameCLI {

    Board playboard;

    public GameCLI(int height, int width, int mines) {
        playboard = new Board(height, width, mines);
    }

    public void play() {
        boolean gameover = false;
        boolean won = false;
        Scanner scan = new Scanner(System.in);

         while(!gameover) {
             //Print Board
             playboard.printPlay();

             //Read Input: Format = <row> <col> <0/1/2>
             // where 0 = left-click, 1 = both-click, and 2 = right-click, flag
             System.out.println("Make a move: <row> <col> <0/1/2> ");
             String temp = scan.nextLine();
             String[] input = temp.split(" ");
             int row = Integer.parseInt(input[0]);
             int col = Integer.parseInt(input[1]);
             int click = Integer.parseInt(input[2]);

             //validate move
             if(!makeMove(row, col, click))
                 //if mine, game over flag, and exit out of loop
                 gameover = true;
             else if(playboard.boardComplete()) {
                 gameover = true;
                 won = true;
             }
         }

         playboard.printSolution();
         if(won)
             System.out.println("Congratutions! You have won!");
         else
             System.out.println("Oops! Better luck next time...");
    }

    //internal helper function that calls board & tells what kind of move
    //returns false if mine is flipped & true otherwise
    private boolean makeMove(int row, int col, int click) {
        switch(click) {
            case 0:
                return playboard.openSingle(row, col);
            case 1:
                playboard.putFlag(row, col);
                return true;
            case 2:
                return playboard.openCascade(row, col);
            default:
                return true;
        }
    }
    public static void main(String[] args) {
        Config.readConfig();
        int height = Config.getHeight();
        int width = Config.getWidth();
        int mines = Config.getMines();

        GameCLI newgame = new GameCLI(height, width, mines);
        newgame.play();
    }
}
