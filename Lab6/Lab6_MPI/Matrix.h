#pragma once
#include <random>
#include <iostream>

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

	void generate_matrix(int max_val, int offset, int precision) {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				matrix[i][j] = (rand() % max_val + offset) / (double)(10^precision);
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


};

