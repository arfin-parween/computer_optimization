import java.util.Arrays;

public class simplexMethod {

    private final double[][] tableau; // The tableau
    private final int numberOfConstraints; // Number of constraints
    private final int numberOfOriginalVariables; // Number of original variables

    public simplexMethod(double[][] A, double[] b, double[] c, boolean isMaximization) {
        // Flag to indicate if the problem is a maximization problem
        numberOfConstraints = b.length;
        numberOfOriginalVariables = c.length;
        tableau = new double[numberOfConstraints + 1][numberOfOriginalVariables + numberOfConstraints + 1];

        // Initialize tableau
        for (int i = 0; i < numberOfConstraints; i++) {
            for (int j = 0; j < numberOfOriginalVariables; j++) {
                tableau[i][j] = A[i][j];
            }
        }

        for (int i = 0; i < numberOfConstraints; i++) {
            tableau[i][numberOfOriginalVariables + i] = 1.0;
        }

        for (int j = 0; j < numberOfOriginalVariables; j++) {
            tableau[numberOfConstraints][j] = isMaximization ? c[j] : -c[j];
        }

        for (int i = 0; i < numberOfConstraints; i++) {
            tableau[i][numberOfOriginalVariables + numberOfConstraints] = b[i];
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
            if (tableau[numberOfConstraints][j] > max) {
                max = tableau[numberOfConstraints][j];
                pivotColumn = j;
            }
        }
        return pivotColumn;
    }

    private int getPivotRow(int pivotColumn) {
        int pivotRow = -1;
        double minRatio = Double.POSITIVE_INFINITY;
        for (int i = 0; i < numberOfConstraints; i++) {
            if (tableau[i][pivotColumn] > 0) {
                double ratio = tableau[i][numberOfOriginalVariables + numberOfConstraints] / tableau[i][pivotColumn];
                if (ratio < minRatio) {
                    minRatio = ratio;
                    pivotRow = i;
                }
            }
        }
        return pivotRow;
    }

    private void pivot(int pivotRow, int pivotColumn) {
        double pivotElement = tableau[pivotRow][pivotColumn];

        for (int j = 0; j < numberOfOriginalVariables + numberOfConstraints + 1; j++) {
            tableau[pivotRow][j] /= pivotElement;
        }

        for (int i = 0; i < numberOfConstraints + 1; i++) {
            if (i != pivotRow) {
                double ratio = tableau[i][pivotColumn];
                for (int j = 0; j < numberOfOriginalVariables + numberOfConstraints + 1; j++) {
                    tableau[i][j] -= ratio * tableau[pivotRow][j];
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
                if (tableau[j][i] == 1) value = tableau[j][numberOfOriginalVariables + numberOfConstraints];
                else if (tableau[j][i] != 0) isBasic = false;
            }
            if (isBasic) solution[i] = value;
            else solution[i] = 0;
        }
        return solution;
    }

    public static void main(String[] args) {
        double[][] A = {
                {2, 1, 1},
                {1, 2, 3},
                {2, 2, 1}
        };
        double[] b = {2, 5, 6};
        double[] c = {1, 2, 3};

        // true for maximization, false for minimization
        boolean isMaximization = false;

        simplexMethod simplexMethod = new simplexMethod(A, b, c, isMaximization);
        simplexMethod.solve();

        System.out.println("Solution: " + Arrays.toString(simplexMethod.getSolution()));
    }
}
