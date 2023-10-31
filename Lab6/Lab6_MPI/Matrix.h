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
static int process_rank = 0;
static MPI_Comm col_Comm;
static MPI_Comm row_Comm;
static int uppper_bound = 3;
static int offset = 0;
static int precision = 1;


namespace Matrix{
	//General
	namespace General {
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
					for (int k = 0; k < size; k++) {
						C[i * size + j] += A[i * size + k] * B[k * size + j];
					}
				}
			}
		}

		void print(double* A, unsigned int size) {
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					std::cout << A[i * size + j] << " ";
				}
				std::cout << std::endl;
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
	}

	//Line Scheme Multiplication
	namespace LineScheme{
		int coords;

		void shift(double* B_line, int line_len, int size) {
			MPI_Status Status;
			int next_p = coords + 1;
			if (next_p == process_num) next_p = 0;
			int prev_p = coords - 1;
			if (prev_p == -1) prev_p = process_num - 1;
			MPI_Sendrecv_replace(B_line, line_len * size, MPI_DOUBLE, next_p, 0, prev_p, 0, col_Comm, &Status);
		}

		void lineSchemeMultiplication(double* A_line, double* B_line, double* res, int size, int line_len, int iter) {
			int ind = line_len * iter;
			for (int i = 0; i < line_len; i++) {
				for (int j = 0; j < line_len; j++) {
					for (int k = 0; k < size; k++) {
						res[ind] += A_line[i * size + k] * B_line[j * size + k];
					}
					ind++;
				}
				ind += size - line_len;
			}
		}


		void computeLine(double* A_line, double* B_line, double* C_line, int line_len, int size) {
			int iter = coords;
			for (int i = 0; i < process_num; i++) {
				lineSchemeMultiplication(A_line, B_line, C_line, size, line_len, iter);
				iter++;
				if (iter == process_num) {
					iter = 0;
				}
				shift(B_line, line_len, size);
			}
		}

		void scatter(double* matrix, double* matrix_lined, int line_len, int size) {
			MPI_Scatter(&(matrix[size * line_len * coords]), line_len * size, MPI_DOUBLE, matrix_lined, line_len * size, MPI_DOUBLE, 0, row_Comm);
		}

		void gather(double* C, double* C_lined, int line_len, int size) {
			MPI_Gather(C_lined, line_len * size, MPI_DOUBLE, C, line_len * size, MPI_DOUBLE, 0, row_Comm);
		}
		void initComms(int line_len) {
			MPI_Comm_split(MPI_COMM_WORLD, process_rank / line_len, process_rank, &row_Comm);
			MPI_Comm_split(MPI_COMM_WORLD, process_rank / line_len, process_rank, &col_Comm);
		}

		void initProcess(double*& A, double*& B, double*& C, double*& A_lined, double*& B_lined, double*& C_lined, int& size, int& line_len) {
			line_len = size / process_num;

			A_lined = new double[line_len * size];
			B_lined = new double[line_len * size];
			C_lined = new double[line_len * size];
			for (int i = 0; i < line_len * size; i++) {
				C_lined[i] = 0;
			}
			if (process_rank == 0) {
				A = new double[size * size];
				B = new double[size * size];
				C = new double[size * size];
				A = General::generateRandom(uppper_bound, offset, precision, size);
				B = General::generateRandom(uppper_bound, offset, precision, size);
			}

		}

		void scatterMatrices(double* A, double* B, double* A_lined, double* B_lined, int size, int line_len) {
			if (process_rank == 0) {
				General::transpose(B, size);
			}
			scatter(A, A_lined, line_len, size);
			scatter(B, B_lined, line_len, size);
		}

		void collectResultLineScheme(double* C, double* C_lined, int line_len, int size) {
			gather(C, C_lined, line_len, size);
		}


		void destruct(double* A, double* B, double* C, double* A_lined, double* B_lined, double* C_lined) {
			delete[] A_lined;
			delete[] B_lined;
			delete[] C_lined;
			if (process_rank == 0) {
				delete[] A;
				delete[] B;
				delete[] C;
			}
		}

		void runLineSchemeMultiplicationTest(int argc, char* argv[], int dim) {
			double* A, * B, * C, * A_lined, * B_lined, * C_lined;
			int size;
			int line_len;
			double start_count, end_count, delta;
			coords = process_rank;
			size = dim;
			if (dim % process_num != 0) {
				if (process_rank == 0) {
					std::cout << "\n Invalid number of proesses for multiplication of matrices of these sizes";
				}
				return;
			}
			initProcess(A, B, C, A_lined, B_lined, C_lined, size, line_len);
			initComms(line_len);

			start_count = MPI_Wtime();
			scatterMatrices(A, B, A_lined, B_lined, size, line_len);
			computeLine(A_lined, B_lined, C_lined, line_len, size);
			end_count = MPI_Wtime();
			collectResultLineScheme(C, C_lined, line_len, size);
			destruct(A, B, C, A_lined, B_lined, C_lined);
			delta = end_count - start_count;
			if (process_rank == 0) {
				std::cout << "Line Scheme Test results (size " << size << "x" << size << " ): " << delta << std::endl;
			}
		}
	}

	//Cannon Multiplication
	namespace Cannon{
		int grid[2];
		int grid_size;
		MPI_Comm grid_Comm;
		void shiftLeft(double* A, int size, int block_size) {
			int next_p = grid[1] + 1;
			if (grid[1] == grid_size - 1) next_p = 0;
			int prev_p = grid[1] - 1;
			if (grid[1] == 0) prev_p = grid_size - 1;
			MPI_Status status;
			MPI_Sendrecv_replace(A, block_size * block_size, MPI_DOUBLE, next_p, 0, prev_p, 0, row_Comm, &status);
		}
		void shiftRight(double* B, int size, int block_size) {
			MPI_Status Status;
			int NextProc = grid[0] + 1;
			if (grid[0] == grid_size - 1) NextProc = 0;
			int PrevProc = grid[0] - 1;
			if (grid[0] == 0) PrevProc = grid_size - 1;
			MPI_Sendrecv_replace(B, block_size * block_size, MPI_DOUBLE, NextProc, 0, PrevProc, 0, col_Comm, &Status);
		}

		void collectResultCannon(double* C, double* C_block, int size, int block_size) {
			double* res_row = new double[size * block_size];
			for (int i = 0; i < block_size; i++) {
				MPI_Gather(&C_block[i * block_size], block_size, MPI_DOUBLE, &res_row[i * size], block_size, MPI_DOUBLE, 0, row_Comm);
			}
			if (grid[1] == 0) {
				MPI_Gather(res_row, block_size * size, MPI_DOUBLE, C, block_size * size, MPI_DOUBLE, 0, col_Comm);
			}
			delete[] res_row;
		}
		void initComputation(double* A, double* B, double* C, int size, int block_size) {
			for (int i = 0; i < grid_size; ++i) {
				General::simpleMultiplication(A, B, C, block_size);
				shiftLeft(A, size, block_size);
				shiftRight(B, size, block_size);
			}
		}

		void scatterBlock(double* matr, double* block, int row, int col, int size, int block_size) {
			int start_pos = col * block_size * size + row * block_size;
			int cur_pos = start_pos;
			for (int i = 0; i < block_size; ++i, cur_pos += size) {
				MPI_Scatter(&matr[cur_pos], block_size, MPI_DOUBLE, &(block[i * block_size]), block_size, MPI_DOUBLE, 0, grid_Comm);
			}
		}

		void scatter(double* A, double* A_block, double* B, double* B_block, int size, int block_size) {
			int N = grid[0];
			int M = grid[1];
			scatterBlock(A, A_block, N, (N + M) % grid_size, size, block_size);
			scatterBlock(B, B_block, (N + M) % grid_size, M, size, block_size);
		}

		void initGridCommsCannon() {
			int d_size[2];
			int period[2];
			int sub_dim[2];
			d_size[0] = grid_size;
			d_size[1] = grid_size;
			period[0] = 0;
			period[1] = 0;
			MPI_Cart_create(MPI_COMM_WORLD, 2, d_size, period, 1, &grid_Comm);
			MPI_Cart_coords(grid_Comm, process_rank, 2, grid);
			sub_dim[0] = 0;
			sub_dim[1] = 1;
			MPI_Cart_sub(grid_Comm, sub_dim, &row_Comm);
			sub_dim[0] = 1;
			sub_dim[1] = 0;
			MPI_Cart_sub(grid_Comm, sub_dim, &col_Comm);
		}

		void deconstruct(double* A, double* B, double* C, double* A_block, double* B_block, double* C_block) {
			if (process_rank == 0) {
				delete[] A;
				delete[] B;
				delete[] C;
			}
			delete[] A_block;
			delete[] B_block;
			delete[] C_block;
		}
		void initCannon(double*& A, double*& B, double*& C, double*& A_block, double*& B_block, double*& C_block, int& size, int& block_size) {
			block_size = size / grid_size;
			A_block = new double[block_size * block_size];
			B_block = new double[block_size * block_size];
			C_block = new double[block_size * block_size];
			C_block = General::generateFilled(0, block_size);
			if (process_rank == 0) {
				A = new double[size * size];
				B = new double[size * size];
				C = new double[size * size];
				A = General::generateRandom(uppper_bound, offset, precision, size);
				B = General::generateRandom(uppper_bound, offset, precision, size);
			}
		}


		void runCannonMultiplicationTest(int argc, char* argv[], int dim) {
			double* A, * B, * C, * A_block, * B_block, * C_block;
			int size;
			int block_size;
			double start_count, end_count, delta;
			size = dim;
			grid_size = sqrt((double)process_num);
			if (process_num != grid_size * grid_size) {
				if (process_rank == 0) {
					std::cout << "\n Invalid number of processes for algoritm execution";
				}
				return;
			}
			initGridCommsCannon();
			initCannon(A, B, C, A_block, B_block, C_block, size, block_size);
			scatter(A, A_block, B, B_block, size, block_size);
			start_count = MPI_Wtime();
			initComputation(A_block, B_block, C_block, size, block_size);
			end_count = MPI_Wtime();
			collectResultCannon(C, C_block, size, block_size);
			deconstruct(A, B, C, A_block, B_block, C_block);
			delta = end_count - start_count;
			if (process_rank == 0)
				std::cout << "Cannon Test results (size " << size << "x" << size << " ): " << delta << std::endl;
		}
	}

	//Fox Multiplication
	namespace Fox{
		int grid[2];
		int grid_size;
		MPI_Comm grid_Comm;
		void initGridCommsFox() {
			int dim_size[2];
			int period[2];
			int sub_dimension[2];
			dim_size[0] = grid_size;
			dim_size[1] = grid_size;
			period[0] = 0;
			period[1] = 0;
			MPI_Cart_create(MPI_COMM_WORLD, 2, dim_size, period, 1, &grid_Comm);
			MPI_Cart_coords(grid_Comm, process_rank, 2, grid);
			sub_dimension[0] = 0;
			sub_dimension[1] = 1;
			MPI_Cart_sub(grid_Comm, sub_dimension, &row_Comm);
			sub_dimension[0] = 1;
			sub_dimension[1] = 0;
			MPI_Cart_sub(grid_Comm, sub_dimension, &col_Comm);
		}

		void initFox(double*& A, double*& B, double*& C, double*& A_block, double*& B_block, double*& C_block, double*& A_sup_block, int& size, int& block_size) {
			block_size = size / grid_size;
			A_block = new double[block_size * block_size];
			B_block = new double[block_size * block_size];
			C_block = new double[block_size * block_size];
			A_sup_block = new double[block_size * block_size];
			C_block = General::generateFilled(0, block_size);
			if (process_rank == 0) {
				A = new double[size * size];
				B = new double[size * size];
				C = new double[size * size];
				A = General::generateRandom(uppper_bound, offset, precision, size);
				B = General::generateRandom(uppper_bound, offset, precision, size);
			}
		}

		void scatterMatricesFox(double* matrix, double* matrx_block, int size, int block_size) {
			double* row = new double[block_size * size];
			if (grid[1] == 0) {
				MPI_Scatter(matrix, block_size * size, MPI_DOUBLE, row, block_size * size, MPI_DOUBLE, 0, col_Comm);
			}
			for (int i = 0; i < block_size; i++) {
				MPI_Scatter(&row[i * size], block_size, MPI_DOUBLE, &(matrx_block[i * block_size]), block_size, MPI_DOUBLE, 0, row_Comm);
			}
			delete[] row;
		}

		void scatterFox(double* A, double* B, double* A_block, double* B_block, int size, int block_size) {
			scatterMatricesFox(A, A_block, size, block_size);
			scatterMatricesFox(B, B_block, size, block_size);
		}

		void collectResultFox(double* C, double* C_block, int size, int block_size) {
			double* res_row = new double[size * block_size];
			for (int i = 0; i < block_size; i++) {
				MPI_Gather(&C_block[i * block_size], block_size, MPI_DOUBLE, &res_row[i * size], block_size, MPI_DOUBLE, 0, row_Comm);
			}
			if (grid[1] == 0) {
				MPI_Gather(res_row, block_size * size, MPI_DOUBLE, C, block_size * size, MPI_DOUBLE, 0, col_Comm);
			}
			delete[] res_row;
		}
		void sendA(int i, double* A, double* A_sup_block, int block_size) {
			int Pivot = (grid[0] + i) % grid_size;
			if (grid[1] == Pivot) {
				for (int i = 0; i < block_size * block_size; i++)
					A[i] = A_sup_block[i];
			}
			MPI_Bcast(A, block_size * block_size, MPI_DOUBLE, Pivot, row_Comm);
		}

		void sendB(double* B, int block_size) {
			int next_process = grid[0] + 1;
			if (grid[0] == grid_size - 1) next_process = 0;
			int prev_process = grid[0] - 1;
			if (grid[0] == 0) prev_process = grid_size - 1;
			MPI_Status mpi_status;
			MPI_Sendrecv_replace(B, block_size * block_size, MPI_DOUBLE, next_process, 0, prev_process, 0, col_Comm, &mpi_status);
		}

		void initComputation(double* A, double* A_sup_block, double* B, double* C, int block_size) {
			for (int i = 0; i < grid_size; i++) {
				sendA(i, A, A_sup_block, block_size);
				General::simpleMultiplication(A, B, C, block_size);
				sendB(B, block_size);
			}
		}

		void deconstruct(double* A, double* B, double* C, double* A_block, double* B_block, double* C_block, double* A_sup_block = NULL) {
			if (process_rank == 0) {
				delete[] A;
				delete[] B;
				delete[] C;
			}
			delete[] A_block;
			delete[] B_block;
			delete[] C_block;
			if (!A_sup_block) {
				delete[] A_sup_block;
			}
		}

		void runFoxMultiplicationTest(int argc, char* argv[], int dim) {
			double* A, * B, * C, * A_block, * B_block, * C_block, * A_sup_block;
			int size;
			int block_size;
			double start_count, end_count, delta;
			grid_size = sqrt((double)process_num);
			if (process_num != grid_size * grid_size) {
				if (process_rank == 0) {
					std::cout << "\nNumber of processes must be a perfect square \n";
				}
				return;
			}
			size = dim;
			initGridCommsFox();
			initFox(A, B, C, A_block, B_block, C_block, A_sup_block, size, block_size);
			scatterFox(A, B, A_sup_block, B_block, size, block_size);
			start_count = MPI_Wtime();
			initComputation(A_block, A_sup_block, B_block, C_block, block_size);
			end_count = MPI_Wtime();
			collectResultFox(C, C_block, size, block_size);
			deconstruct(A, B, C, A_block, B_block, C_block, A_sup_block);

			delta = end_count - start_count;
			if (process_rank == 0)
				std::cout << "Fox Test results (size " << size << "x" << size << " ): " << delta << std::endl;
		}
	}
	void runAlgorithmTest(int argc, char* argv[], int dim) {
		LineScheme::runLineSchemeMultiplicationTest(argc, argv, dim);
		Cannon::runCannonMultiplicationTest(argc, argv, dim);
		Fox::runFoxMultiplicationTest(argc, argv, dim);
	}
};

