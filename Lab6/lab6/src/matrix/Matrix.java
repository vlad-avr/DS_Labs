package matrix;

import java.security.SecureRandom;
import java.text.DecimalFormat;

public class Matrix {

    private final int rows;
    private final int cols;
    private double[][] matrix;

    public Matrix(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        matrix = new double[rows][cols];
    }

    public Matrix(Matrix matrix){
        this.cols = matrix.cols();
        this.rows = matrix.rows();
        this.matrix = new double[rows][cols];
        copyMatrix(matrix);
    }

    public void set(double val, int i, int j){
        try{
            matrix[i][j] = val;
        }catch(IndexOutOfBoundsException exception){
            System.out.println(exception.getMessage());
        }
    }

    public double get(int i, int j){
        try{
            return matrix[i][j];
        }catch(IndexOutOfBoundsException exception){
            System.out.println(exception.getMessage());
            return 0;
        }       
    }

    public int rows(){
        return this.rows;
    }

    public int cols(){
        return this.cols;
    }

    public void generateMatrix(double upper, double offset){
        SecureRandom rnd = new SecureRandom();
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                matrix[i][j] = rnd.nextDouble(upper) + offset;
            }
        }
    }

    public void copyMatrix(Matrix matrix){
        copyMatrix(matrix, 0, matrix.rows(), 0, matrix.cols());
    }

    public void copyMatrix(Matrix matrix, int startRow, int endRow, int startCol, int endCol){
        try{
            for(int i = startRow; i < endRow; i++){
                for(int j = startCol; j < endCol; j++){
                    this.matrix[i][j] = matrix.get(i, j);
                }
            }
        }catch(IndexOutOfBoundsException e){
            System.out.println(e.getMessage());
        }
    }

    public void print(){
        System.out.println("\n Matrix : \n");
        DecimalFormat df = new DecimalFormat("0.00");

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print(df.format(matrix[i][j]) + "\t");
            }
            System.out.println();
        }
    }

    public static class MatrixMultiplier {
        public static Matrix simpleMultiplicaion(Matrix A, Matrix B) {
            if(A.rows() != B.cols() || A.cols() != B.rows()){
                System.out.println("\nInvalid Matrices sizes : unable to multiply\n");
                return null;
            }
            Matrix res = new Matrix(A.rows(), B.cols());
            for(int i = 0; i < A.rows(); i++){
                for(int j = 0; j < B.cols(); j++){
                    res.set(0, i, j);
                    for(int k = 0; k < A.cols(); k++){
                        res.set(res.get(i, j) + A.get(i, k)*B.get(k, j), i, j);
                    }
                }
            }
            return res;
        }
    }

}
