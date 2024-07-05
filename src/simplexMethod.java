import java.util.Arrays;

public class simplexMethod {

    private final double[][] table; // The table
    private final int numberOfConstraints; // Number of constraints
    private final int numberOfOriginalVariables; // Number of original variables

    public simplexMethod(double[][] A, double[] b, double[] c, boolean isMaximization) {
        // Flag to indicate if the problem is a maximization problem
        numberOfConstraints = b.length;
        numberOfOriginalVariables = c.length;
        table = new double[numberOfConstraints + 1][numberOfOriginalVariables + numberOfConstraints + 1];

        // Initialize table
        for (int i = 0; i < numberOfConstraints; i++) {
            for (int j = 0; j < numberOfOriginalVariables; j++) {
                table[i][j] = A[i][j];
            }
        }

        for (int i = 0; i < numberOfConstraints; i++) {
            table[i][numberOfOriginalVariables + i] = 1.0;
        }

        for (int j = 0; j < numberOfOriginalVariables; j++) {
            table[numberOfConstraints][j] = isMaximization ? c[j] : -c[j];
        }

        for (int i = 0; i < numberOfConstraints; i++) {
            table[i][numberOfOriginalVariables + numberOfConstraints] = b[i];
        }
    }

    // Run simplexMethod algorithm
    public void solve() {
        while (true) {
            int pivotColumn = getPivotColumn();
            if (pivotColumn == -1) break; // Optimal solution found

            int pivotRow = getPivotRow(pivotColumn);
            if (pivotRow == -1) throw new ArithmeticException("Unbounded solution");

            pivot(pivotRow, pivotColumn);
        }
    }

    private int getPivotColumn() {
        int pivotColumn = -1;
        double max = 0;
        for (int j = 0; j < numberOfOriginalVariables + numberOfConstraints; j++) {
            if (table[numberOfConstraints][j] > max) {
                max = table[numberOfConstraints][j];
                pivotColumn = j;
            }
        }
        return pivotColumn;
    }

    private int getPivotRow(int pivotColumn) {
        int pivotRow = -1;
        double minRatio = Double.POSITIVE_INFINITY;
        for (int i = 0; i < numberOfConstraints; i++) {
            if (table[i][pivotColumn] > 0) {
                double ratio = table[i][numberOfOriginalVariables + numberOfConstraints] / table[i][pivotColumn];
                if (ratio < minRatio) {
                    minRatio = ratio;
                    pivotRow = i;
                }
            }
        }
        return pivotRow;
    }

    private void pivot(int pivotRow, int pivotColumn) {
        double pivotElement = table[pivotRow][pivotColumn];

        for (int j = 0; j < numberOfOriginalVariables + numberOfConstraints + 1; j++) {
            table[pivotRow][j] /= pivotElement;
        }

        for (int i = 0; i < numberOfConstraints + 1; i++) {
            if (i != pivotRow) {
                double ratio = table[i][pivotColumn];
                for (int j = 0; j < numberOfOriginalVariables + numberOfConstraints + 1; j++) {
                    table[i][j] -= ratio * table[pivotRow][j];
                }
            }
        }
    }

    public double[] getSolution() {
        double[] solution = new double[numberOfOriginalVariables];
        for (int i = 0; i < numberOfOriginalVariables; i++) {
            boolean isBasic = true;
            double value = 0;
            for (int j = 0; j < numberOfConstraints; j++) {
                if (table[j][i] == 1) value = table[j][numberOfOriginalVariables + numberOfConstraints];
                else if (table[j][i] != 0) isBasic = false;
            }
            if (isBasic) solution[i] = value;
            else solution[i] = 0;
        }
        return solution;
    }

    public static void main(String[] args) {
        // A matrix is used to pass constraints
        double[][] A = {
                {1, 2, 1},
                {3, 0, 2},
                {1, 4, 0}
        };
        // b is used to pass right hand side value of the constraints
        // c is to pass objective function
        double[] b = {430, 460, 420};
        double[] c = {3, 2, 5};

        // true for maximization, false for minimization
        boolean isMaximization = true;

        simplexMethod simplexMethod = new simplexMethod(A, b, c, isMaximization);
        simplexMethod.solve();

        System.out.println("Solution: " + Arrays.toString(simplexMethod.getSolution()));
    }
}
