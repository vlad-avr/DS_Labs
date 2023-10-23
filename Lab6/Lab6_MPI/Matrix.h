#pragma once
#include <random>
#include <iostream>
#include <mpi.h>

class Matrix
{
private:
	double** matrix;
	int rows;
	int cols;

public:
	Matrix(int rows, int cols) {
		this->rows = rows;
		this->cols = cols;
		matrix = new double*[rows];
		for (int i = 0; i < rows; i++) {
			matrix[i] = new double[cols];
		}
	}

	~Matrix() {
		for (int i = 0; i < rows; i++) {
			delete matrix[i];
		}
		delete matrix;
	}

	double get(int i, int j) {
		if (i < 0 || i >= rows || j < 0 || j >= cols) {
			std::cout << "\nIndex out of bounds!\n";
			return NULL;
		}
		return matrix[i][j];
	}

	double set(double val, int i, int j) {
		if (i < 0 || i >= rows || j < 0 || j >= cols) {
			std::cout << "\nIndex out of bounds!\n";
			return NULL;
		}
		matrix[i][j] = val;
	}

	void generate_matrix(int max_val, int offset, int precision) {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				matrix[i][j] = (rand() % max_val + offset) / (double)(pow(10, precision));
			}
		}
	}

	void print() {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				std::cout << matrix[i][j] << ' ';
			}
			std::cout << std::endl;
		}
	}

	int get_rows() {
		return this->rows;
	}

	int get_cols() {
		return this->cols;
	}

	static Matrix* simple_multiplication(Matrix* A, Matrix* B) {
		if (A->cols != B->rows) {
			std::cout << "\nInvalid matrices sizes (number of rows of A != number of columns of B)!\n";
			return NULL;
		}
		Matrix* C = new Matrix(A->rows, B->cols);
		for (int i = 0; i < A->rows; i++) {
			for (int j = 0; j < B->cols; j++) {
				C->matrix[i][j] = 0;
				for (int k = 0; k < A->cols; k++) {
					C->matrix[i][j] += A->matrix[i][k] * B->matrix[k][j];
				}
			}
		}
		return C;
	}

};

