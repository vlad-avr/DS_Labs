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
		
	}

private:

};

