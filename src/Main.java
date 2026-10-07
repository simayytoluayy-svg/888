import java.util.Scanner;

public class Main {
    private static final int SIZE = 9;
    private static final int EMPTY = 0;
    private static final int EASY = 1;
    private static final int MEDIUM = 2;
    private static final int HARD = 3;
    private static final int MISTAKES = 0;
    private static final int HINTS_USED = 1;

    private static final int[][] EASY_PUZZLE = {
            {5, 3, 0, 0, 7, 0, 0, 0, 0},
            {6, 0, 0, 1, 9, 5, 0, 0, 0},
            {0, 9, 8, 0, 0, 0, 0, 6, 0},
            {8, 0, 0, 0, 6, 0, 0, 0, 3},
            {4, 0, 0, 8, 0, 3, 0, 0, 1},
            {7, 0, 0, 0, 2, 0, 0, 0, 6},
            {0, 6, 0, 0, 0, 0, 2, 8, 0},
            {0, 0, 0, 4, 1, 9, 0, 0, 5},
            {0, 0, 0, 0, 8, 0, 0, 7, 9}
    };

    private static final int[][] SOLUTION = {
            {5, 3, 4, 6, 7, 8, 9, 1, 2},
            {6, 7, 2, 1, 9, 5, 3, 4, 8},
            {1, 9, 8, 3, 4, 2, 5, 6, 7},
            {8, 5, 9, 7, 6, 1, 4, 2, 3},
            {4, 2, 6, 8, 5, 3, 7, 9, 1},
            {7, 1, 3, 9, 2, 4, 8, 5, 6},
            {9, 6, 1, 5, 3, 7, 2, 8, 4},
            {2, 8, 7, 4, 1, 9, 6, 3, 5},
            {3, 4, 5, 2, 8, 6, 1, 7, 9}
    };

    public static void main(String[] args) {
        int difficulty = EASY;
        int[][] puzzle = createPuzzle(difficulty);
        int[][] board = copyBoard(puzzle);
        int[] statistics = new int[2];
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("Welcome to the Sudoku Quiz!");
        while (running) {
            showMenu(difficulty);
            int choice = readNumber(scanner, "Choose an option: ");

            if (choice == 1) {
                printBoard(board);
            } else if (choice == 2) {
                enterNumber(scanner, board, puzzle, statistics);
            } else if (choice == 3) {
                clearCell(scanner, board, puzzle);
            } else if (choice == 4) {
                checkBoard(board);
            } else if (choice == 5) {
                giveHint(board, statistics);
            } else if (choice == 6) {
                showStatistics(board, puzzle, statistics, difficulty);
            } else if (choice == 7) {
                difficulty = chooseDifficulty(scanner);
                puzzle = createPuzzle(difficulty);
                board = copyBoard(puzzle);
                statistics[MISTAKES] = 0;
                statistics[HINTS_USED] = 0;
                System.out.println("The puzzle has been reset.");
            } else if (choice == 8) {
                running = false;
                System.out.println("Thanks for playing!");
            } else {
                System.out.println("Please choose a number from 1 to 8.");
            }
        }

        scanner.close();
    }

    private static void showMenu(int difficulty) {
        System.out.println();
        System.out.println("Current difficulty: " + difficultyName(difficulty));
        System.out.println("1. Show the Sudoku board");
        System.out.println("2. Enter a number");
        System.out.println("3. Clear an entered number");
        System.out.println("4. Check the solution");
        System.out.println("5. Get a hint");
        System.out.println("6. Show statistics");
        System.out.println("7. Choose difficulty and restart");
        System.out.println("8. Quit");
    }

    private static void printBoard(int[][] board) {
        System.out.println();
        for (int row = 0; row < SIZE; row++) {
            if (row % 3 == 0) {
                System.out.println("+-------+-------+-------+");
            }
            for (int column = 0; column < SIZE; column++) {
                if (column % 3 == 0) {
                    System.out.print("| ");
                }
                if (board[row][column] == EMPTY) {
                    System.out.print(". ");
                } else {
                    System.out.print(board[row][column] + " ");
                }
            }
            System.out.println("|");
        }
        System.out.println("+-------+-------+-------+");
    }

    private static void enterNumber(Scanner scanner, int[][] board, int[][] puzzle, int[] statistics) {
        int row = readCellCoordinate(scanner, "Enter row (1-9): ");
        int column = readCellCoordinate(scanner, "Enter column (1-9): ");

        if (puzzle[row][column] != EMPTY) {
            System.out.println("That cell is a clue and cannot be changed.");
            return;
        }

        int value = readNumber(scanner, "Enter a number (1-9): ");
        if (value < 1 || value > 9) {
            System.out.println("The number must be between 1 and 9.");
            statistics[MISTAKES]++;
            return;
        }

        if (!isAllowed(board, row, column, value)) {
            System.out.println("That number conflicts with its row, column, or 3x3 box.");
            statistics[MISTAKES]++;
            return;
        }

        board[row][column] = value;
        System.out.println("Number entered.");
    }

    private static void clearCell(Scanner scanner, int[][] board, int[][] puzzle) {
        int row = readCellCoordinate(scanner, "Enter row (1-9): ");
        int column = readCellCoordinate(scanner, "Enter column (1-9): ");

        if (puzzle[row][column] != EMPTY) {
            System.out.println("Clue cells cannot be cleared.");
        } else if (board[row][column] == EMPTY) {
            System.out.println("That cell is already empty.");
        } else {
            board[row][column] = EMPTY;
            System.out.println("Cell cleared.");
        }
    }

    private static void checkBoard(int[][] board) {
        boolean hasEmptyCell = false;
        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                if (board[row][column] == EMPTY) {
                    hasEmptyCell = true;
                }
            }
        }
        if (hasEmptyCell) {
            System.out.println("The puzzle is not complete yet.");
        } else if (matchesSolution(board)) {
            System.out.println("Congratulations! You solved the Sudoku!");
        } else {
            System.out.println("The board is full, but some numbers are incorrect.");
        }
    }

    private static void giveHint(int[][] board, int[] statistics) {
        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                if (board[row][column] == EMPTY) {
                    board[row][column] = SOLUTION[row][column];
                    statistics[HINTS_USED]++;
                    System.out.println("Hint: row " + (row + 1) + ", column " + (column + 1)
                            + " is " + SOLUTION[row][column] + ".");
                    return;
                }
            }
        }
        System.out.println("There are no empty cells. Check your solution!");
    }

    private static void showStatistics(int[][] board, int[][] puzzle, int[] statistics, int difficulty) {
        int enteredNumbers = 0;
        int emptyCells = 0;
        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                if (puzzle[row][column] == EMPTY && board[row][column] != EMPTY) {
                    enteredNumbers++;
                }
                if (board[row][column] == EMPTY) {
                    emptyCells++;
                }
            }
        }
        System.out.println("Difficulty: " + difficultyName(difficulty));
        System.out.println("Numbers entered (including hints): " + enteredNumbers);
        System.out.println("Empty cells remaining: " + emptyCells);
        System.out.println("Invalid moves: " + statistics[MISTAKES]);
        System.out.println("Hints used: " + statistics[HINTS_USED]);
    }

    private static boolean matchesSolution(int[][] board) {
        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                if (board[row][column] != SOLUTION[row][column]) {
                    return false;
                }
            }
        }
        return true;
    }

    private static int chooseDifficulty(Scanner scanner) {
        System.out.println("Choose difficulty:");
        System.out.println("1. Easy");
        System.out.println("2. Medium");
        System.out.println("3. Hard");
        int difficulty;
        do {
            difficulty = readNumber(scanner, "Choose 1, 2, or 3: ");
            if (difficulty < EASY || difficulty > HARD) {
                System.out.println("Please choose 1, 2, or 3.");
            }
        } while (difficulty < EASY || difficulty > HARD);
        return difficulty;
    }

    private static String difficultyName(int difficulty) {
        if (difficulty == EASY) {
            return "Easy";
        } else if (difficulty == MEDIUM) {
            return "Medium";
        }
        return "Hard";
    }

    private static int[][] createPuzzle(int difficulty) {
        if (difficulty == EASY) {
            return copyBoard(EASY_PUZZLE);
        }

        int emptyCellsPerRow;
        if (difficulty == MEDIUM) {
            emptyCellsPerRow = 5;
        } else {
            emptyCellsPerRow = 6;
        }

        int[][] puzzle = new int[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                int pattern = (row * 3 + column * 5) % SIZE;
                if (pattern < emptyCellsPerRow) {
                    puzzle[row][column] = EMPTY;
                } else {
                    puzzle[row][column] = SOLUTION[row][column];
                }
            }
        }
        return puzzle;
    }

    private static boolean isAllowed(int[][] board, int row, int column, int value) {
        for (int index = 0; index < SIZE; index++) {
            if ((index != column && board[row][index] == value)
                    || (index != row && board[index][column] == value)) {
                return false;
            }
        }

        int firstRow = row / 3 * 3;
        int firstColumn = column / 3 * 3;
        for (int boxRow = firstRow; boxRow < firstRow + 3; boxRow++) {
            for (int boxColumn = firstColumn; boxColumn < firstColumn + 3; boxColumn++) {
                if ((boxRow != row || boxColumn != column) && board[boxRow][boxColumn] == value) {
                    return false;
                }
            }
        }
        return true;
    }

    private static int readCellCoordinate(Scanner scanner, String prompt) {
        int coordinate;
        do {
            coordinate = readNumber(scanner, prompt);
            if (coordinate < 1 || coordinate > 9) {
                System.out.println("Enter a number from 1 to 9.");
            }
        } while (coordinate < 1 || coordinate > 9);
        return coordinate - 1;
    }

    private static int readNumber(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    private static int[][] copyBoard(int[][] source) {
        int[][] copy = new int[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                copy[row][column] = source[row][column];
            }
        }
        return copy;
    }
}
