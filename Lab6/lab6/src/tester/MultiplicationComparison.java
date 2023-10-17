package tester;

import matrix.Matrix;

public class MultiplicationComparison {
    

    public void start(){
        Matrix A = new Matrix(3, 2);
        A.generateMatrix(10, 1);
        Matrix B = new Matrix(2, 3);
        B.generateMatrix(2, 3);
        A.print();
        B.print();

        Matrix C = Matrix.MatrixMultiplier.simpleMultiplicaion(A, B);
        C.print();
    }
}
