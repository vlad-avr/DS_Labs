#pragma once
#include <iostream>
#include <random>

static class Matrix
{
public:
	static double* generateRandom(unsigned int upper_bound, int offset, unsigned int precision, unsigned int size) {
		unsigned int actual_size = size * size;
		double* matr = new double[actual_size];
		for (int i = 0; i < actual_size; i++) {
			matr[i] = (rand() % (upper_bound * precision) + offset * precision) / (double)precision;
		}
		return matr;
	}


	static double* generateFilled(double filler, unsigned int size) {
		unsigned int actual_size = size * size;
		double* matr = new double[actual_size];
		for (int i = 0; i < actual_size; i++) {
			matr[i] = filler;
		}
		return matr;
	}

	static double* simpleMultiplication(double* A, double* B, unsigned int size) {
		unsigned int actual_size = size * size;
		double* C = new double[actual_size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				C[i * size + j] = 0.0;
				for (int k = 0; k < size; k++) {
					C[i * size + j] += A[i * size + k] * B[k * size + j];
				}
			}
		}
		return C;
	}

	static void print(double* A, unsigned int size){
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				std::cout << A[i * size + j] << " ";
			}
			std::cout << std::endl;
		}
	}
};

