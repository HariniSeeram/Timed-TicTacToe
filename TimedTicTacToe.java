import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.*;

public class TimedTicTacToe4x4 {

    static final int SIZE = 4;
    static final int TIME_LIMIT = 15;

    static char[][] board = new char[SIZE][SIZE];

    static void initBoard() {
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                board[i][j] = ' ';
    }

    static void printBoard() {
        System.out.println("\n      0   1   2   3");
        for (int i = 0; i < SIZE; i++) {
            System.out.print(" " + i + "    ");
            for (int j = 0; j < SIZE; j++) {
                System.out.print(board[i][j]);
                if (j < SIZE - 1) System.out.print(" | ");
            }
            System.out.println();
            if (i < SIZE - 1) System.out.println("     ---+---+---+---");
        }
        System.out.println();
    }
    
    static boolean hasWinner(char p) {
        for (int i = 0; i < SIZE; i++) {
            boolean ok = true;
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] != p) { ok = false; break; }
            }
            if (ok) return true;
        }

        for (int j = 0; j < SIZE; j++) {
            boolean ok = true;
            for (int i = 0; i < SIZE; i++) {
                if (board[i][j] != p) { ok = false; break; }
            }
            if (ok) return true;
        }

        boolean okDiag1 = true;
        for (int i = 0; i < SIZE; i++) {
            if (board[i][i] != p) { okDiag1 = false; break; }
        }
        if (okDiag1) return true;

        boolean okDiag2 = true;
        for (int i = 0; i < SIZE; i++) {
            if (board[i][SIZE - 1 - i] != p) { okDiag2 = false; break; }
        }
        return okDiag2;
    }

    static boolean isDraw() {
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                if (board[i][j] == ' ') return false;
        return true;
    }

    static int[] timedMove(BufferedReader br, String playerName, char symbol) {
        ExecutorService ex = Executors.newSingleThreadExecutor();

        Future<String> future = ex.submit(() -> {
            System.out.println("⏳ " + playerName + " (" + symbol + ") - You have " + TIME_LIMIT + " seconds!");
            System.out.print(playerName + " (" + symbol + "), enter row and col (0-3): ");
            return br.readLine();
        });

        try {
            String line = future.get(TIME_LIMIT, TimeUnit.SECONDS);
            if (line == null || line.trim().isEmpty()) return null;

            String[] parts = line.trim().split("\\s+");
            if (parts.length != 2) return null;

            int row = Integer.parseInt(parts[0]);
            int col = Integer.parseInt(parts[1]);
            return new int[]{row, col};

        } catch (TimeoutException e) {
            System.out.println("\n Time over! " + playerName + " missed ONLY this move.");
            return null;
        } catch (Exception e) {
            System.out.println("Invalid input! Move missed.");
            return null;
        } finally {
            future.cancel(true);
            ex.shutdownNow();
        }
    }

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("=======================================");
        System.out.println("      4x4 TIMED TIC TAC TOE (15s)");
        System.out.println("=======================================");
        System.out.println("Rules:");
        System.out.println("- Board size: 4x4");
        System.out.println("- Enter row col (example: 2 3)");
        System.out.println("- 15 seconds per move else move skipped");
        System.out.println("- Winner = 4 in a row (row/col/diagonal)\n");

        System.out.print("Enter Player 1 name (X): ");
        String p1 = br.readLine();
        System.out.print("Enter Player 2 name (O): ");
        String p2 = br.readLine();

        boolean playAgain = true;

        while (playAgain) {
            initBoard();
            char current = 'X';

            while (true) {
                printBoard();

                String currentName = (current == 'X') ? p1 : p2;

                int[] move = timedMove(br, currentName, current);

                if (move != null) {
                    int row = move[0], col = move[1];

                    if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) {
                        System.out.println("Out of bounds! Move skipped.");
                    } else if (board[row][col] != ' ') {
                        System.out.println("Cell already filled! Move skipped.");
                    } else {
                        board[row][col] = current;

                        if (hasWinner(current)) {
                            printBoard();
                            System.out.println("🎉 " + currentName + " (" + current + ") wins!");
                            break;
                        }

                        if (isDraw()) {
                            printBoard();
                            System.out.println("Draw match!");
                            break;
                        }
                    }
                }

                current = (current == 'X') ? 'O' : 'X';
            }

            System.out.print("\nPlay again? (y/n): ");
            String ans = br.readLine();
            playAgain = ans != null && ans.trim().equalsIgnoreCase("y");
        }

        System.out.println("\nThanks for playing! ");
    }
}