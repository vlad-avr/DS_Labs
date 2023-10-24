#pragma once
#include <iostream>
#include <random>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <math.h>
#include <mpi.h>
#include <iostream>
static int slice_size;
static int process_num = 0;
static int proces_rank = 0;
static MPI_Comm col_Comm;
static MPI_Comm row_Comm;
static int uppper_bound = 100;
static int offset = 1;
static int precision = 100;


namespace Matrix{
	double* generateRandom(unsigned int upper_bound, int offset, unsigned int precision, unsigned int size) {
		unsigned int actual_size = size * size;
		double* matr = new double[actual_size];
		for (int i = 0; i < actual_size; i++) {
			matr[i] = (rand() % (upper_bound * precision) + offset * precision) / (double)precision;
		}
		return matr;
	}


	double* generateFilled(double filler, unsigned int size) {
		unsigned int actual_size = size * size;
		double* matr = new double[actual_size];
		for (int i = 0; i < actual_size; i++) {
			matr[i] = filler;
		}
		return matr;
	}

	void simpleMultiplication(double* A, double* B, double* C, unsigned int size) {
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				C[i * size + j] = 0.0;
				for (int k = 0; k < size; k++) {
					C[i * size + j] += A[i * size + k] * B[k * size + j];
				}
			}
		}
	}

	void print(double* A, unsigned int size){
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				std::cout << A[i * size + j] << " ";
			}
			std::cout << std::endl;
		}
	}


	//Line Scheme
	int coords;

	void shift(double* matr_lined, int line_len, int size) {
		MPI_Status mpi_status;
		int next_process = coords + 1;
		if (next_process == process_num) {
			next_process = 0;
		}
		int prev_process = coords - 1;
		if (prev_process == -1) {
			prev_process = process_num - 1;
		}
		MPI_Sendrecv_replace(matr_lined, line_len * size, MPI_DOUBLE, next_process, 0, prev_process, 0, col_Comm, &mpi_status);
	}

	void lineScemeMultiplication(double* A, double* B, double* C, int size, int line_len, int iteration) {
		int index = line_len * iteration;

		for (int i = 0; i < line_len; i++) {
			for (int j = 0; j < line_len; j++) {

				for (int k = 0; k < size; k++) {
					C[index] += A[i * size + k] * B[j * size + k];
				}
				index++;
			}
			index += size - line_len;
		}
	}

	void computeLine(double* A, double* B, double* C, int line_len, int size) {
		int iter = coords;
		for (int i = 0; i < process_num; i++) {
			lineScemeMultiplication(A, B, C, size, line_len, iter);
			iter++;
			if (iter == process_num) {
				iter = 0;
			}
			shift(B, line_len, size);
		}
	}

	void scatter(double* matrix, double* matrix_lined, int line_len, int size) {
		MPI_Scatter(&(matrix[size * line_len * coords]), line_len * size, MPI_DOUBLE, matrix_lined, line_len * size, MPI_DOUBLE, 0, row_Comm);
	}

	void gather(double* cMatrix, double* cMatrixTape, int tapeLen, int size) {
		MPI_Gather(cMatrixTape, tapeLen * size, MPI_DOUBLE, cMatrix, tapeLen * size, MPI_DOUBLE, 0, row_Comm);
	}
	void initComms(int line_len) {
		MPI_Comm_split(MPI_COMM_WORLD, proces_rank / line_len, proces_rank, &row_Comm);
		MPI_Comm_split(MPI_COMM_WORLD, proces_rank / line_len, proces_rank, &col_Comm);
	}

	void initProcess(double*& A, double*& B, double*& C, double*& A_lined, double*& B_lined, double*& C_lined, int& size, int& line_len) {
		line_len = size / process_num;

		A_lined = new double[line_len * size];
		B_lined = new double[line_len * size];
		C_lined = new double[line_len * size];
		for (int i = 0; i < line_len * size; i++) {
			C_lined[i] = 0;
		}
		if (proces_rank == 0) {
			A = new double[size * size];
			B = new double[size * size];
			C = new double[size * size];
			A = generateRandom(uppper_bound, offset, precision, size);
			B = generateRandom(uppper_bound, offset, precision, size);
			printf("\n A: \n");
			print(A, size);
			printf("\n B: \n");
			print(B, size);
			C = generateFilled(0, size);
		}

	}

	void transpose(double* matrix, int size) {
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				double t = matrix[i * size + j];
				matrix[i * size + j] = matrix[j * size + i];
				matrix[j * size + i] = t;
			}
		}
	}

	void scatterMatrices(double* A, double* B, double* A_lined, double* B_lined, int size, int line_len) {
		if (proces_rank == 0) {
			transpose(B, size);
		}
		scatter(A, A_lined, line_len, size);
		scatter(B, B_lined, line_len, size);
	}

	void collectResult(double* C, double* C_lined, int line_len, int size) {
		gather(C, C_lined, line_len, size);
		if (proces_rank == 0) {
			printf("\n C: \n");
			print(C, size);
		}
	}


	void destruct(double* A, double* B, double* C, double* A_lined, double* B_lined, double* C_lined) {
		delete[] A_lined;
		delete[] B_lined;
		delete[] C_lined;
		if (proces_rank == 0) {
			delete[] A;
			delete[] B;
			delete[] C;
		}
	}

	double runLineSchemeMultiplicationTest(int argc, char* argv[], int dim) {
		double* A;
		double* B;
		double* C;
		int size;
		int line_len;
		double* A_lined;
		double* B_lined;
		double* C_lined;
		double start_count, end_count, delta;
		coords = proces_rank;
		size = dim;
		if (dim % process_num != 0) {
			if (proces_rank == 0) {
				printf("Invalid dimensions input -> must be dividable by " + process_num);
			}
			return 1;
		}
		initProcess(A, B, C, A_lined, B_lined, C_lined, size, line_len);
		initComms(line_len);

		start_count = MPI_Wtime();
		scatterMatrices(A, B, A_lined, B_lined, size, line_len);
		computeLine(A_lined, B_lined, C_lined, line_len, size);
		end_count = MPI_Wtime();
		collectResult(C, C_lined, line_len, size);
		destruct(A, B, C, A_lined, B_lined, C_lined);
		delta = end_count - start_count;
		if (proces_rank == 0) {
			std::cout << "Line Scheme Test results (size " << size << "x" << size << " ): " << delta << std::endl;
		}
		return delta;
	}
};

