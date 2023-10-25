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
	//Basic functions for operations with Matrices
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
			/*printf("\n A: \n");
			print(A, size);
			printf("\n B: \n");
			print(B, size);*/
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

	void collectResultLineScheme(double* C, double* C_lined, int line_len, int size) {
		gather(C, C_lined, line_len, size);
		/*if (proces_rank == 0) {
			printf("\n C: \n");
			print(C, size);
		}*/
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
		double* A, *B, *C, *A_lined, *B_lined, *C_lined;
		int size;
		int line_len;
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
		collectResultLineScheme(C, C_lined, line_len, size);
		destruct(A, B, C, A_lined, B_lined, C_lined);
		delta = end_count - start_count;
		if (proces_rank == 0) {
			std::cout << "Line Scheme Test results (size " << size << "x" << size << " ): " << delta << std::endl;
		}
		return delta;
	}

	//Cannon

	int grid[2];
	int grid_size;
	MPI_Comm grid_Comm;
	void multiplyBlocks(double* A, double* B, double* C, int size) {
		int i, j, k;
		for (i = 0; i < size; i++) {
			for (j = 0; j < size; j++)
				for (k = 0; k < size; k++)
					C[i * size + j] += A[i * size + k] * B[k * size + j];
		}
	}
	void shiftRight(double* left_block, int Size, int size) {
		int next_process = grid[1] + 1;
		MPI_Status mpi_status;
		if (grid[1] == grid_size - 1) { 
			next_process = 0; 
		}
		int prev_process = grid[1] - 1;
		if (grid[1] == 0) {
			prev_process = grid_size - 1;
		}
		MPI_Sendrecv_replace(left_block, size * size, MPI_DOUBLE, next_process, 0, prev_process, 0, row_Comm, &mpi_status);
	}
	void shiftLeft(double* right_block, int Size, int BlockSize) {
		int next_process = grid[0] + 1;
		MPI_Status mpi_status;
		if (grid[0] == grid_size - 1) {
			next_process = 0;
		}
		int prev_process = grid[1] - 1;
		if (grid[0] == 0) {
			prev_process = grid_size - 1;
		}
		MPI_Sendrecv_replace(right_block, BlockSize * BlockSize, MPI_DOUBLE, next_process, 0, prev_process, 0, col_Comm, &mpi_status);
	}

	void collectResultCannon(double* C, double* C_block, int size, int block_size) {
		double* result = new double[size * block_size];
		for (int i = 0; i < block_size; i++) {
			MPI_Gather(&C_block[i * block_size], block_size, MPI_DOUBLE, &result[i * size], block_size, MPI_DOUBLE, 0, row_Comm);
		}
		if (grid[1] == 0) {
			MPI_Gather(result, block_size * size, MPI_DOUBLE, C, block_size * size, MPI_DOUBLE, 0, col_Comm);
		}
		delete[] result;
	}
	void calculate(double* A_block, double* B_block, double* C_block, int size, int block_size) {
		for (int i = 0; i < grid_size; ++i) {
			multiplyBlocks(A_block, B_block, C_block, block_size);
			shiftRight(A_block, size, block_size);
			shiftLeft(B_block, size, block_size);
		}
	}

	void scatterBlock(double* matrix, double* block, int x, int y, int size, int block_size) {
		int start_pos = y * block_size * size + x * block_size;
		int cur_pos = start_pos;
		for (int i = 0; i < block_size; ++i, cur_pos += size) {
			MPI_Scatter(&matrix[cur_pos], block_size, MPI_DOUBLE, &(block[i * block_size]), block_size, MPI_DOUBLE, 0, grid_Comm);
		}
	}

	void scatterBlocks(double* A, double* A_block, double* B, double* B_block, int size, int block_size) {
		//double* block_row = new double[block_size * size];
		int N = grid[0];
		int M = grid[1];
		scatterBlock(A, A_block, N, (N + M) % grid_size, size, block_size);
		scatterBlock(B, B_block, (N + M) % grid_size, M, size, block_size);
	}

	void initGridComms() {
		int dims_sizes[2];
		int periods[2];
		int dims_divs[2];
		dims_sizes[0] = grid_size;
		dims_sizes[1] = grid_size;
		periods[0] = 0;
		periods[1] = 0;
		MPI_Cart_create(MPI_COMM_WORLD, 2, dims_sizes, periods, 1, &grid_Comm);
		MPI_Cart_coords(grid_Comm, proces_rank, 2, grid);
		dims_divs[0] = 0;
		dims_divs[1] = 1;
		MPI_Cart_sub(grid_Comm, dims_divs, &row_Comm);
		dims_divs[0] = 1;
		dims_divs[1] = 0;
		MPI_Cart_sub(grid_Comm, dims_divs, &col_Comm);
	}

	void deconstruct(double* A, double* B, double* C, double* A_block, double* B_block, double* C_block) {
		if (proces_rank == 0) {
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

		for (int i = 0; i < block_size * block_size; i++) {
			C_block[i] = 0;
		}
		if (proces_rank == 0) {
			A = new double[size * size];
			B = new double[size * size];
			C = new double[size * size];
			A = generateRandom(uppper_bound, offset, precision, size);
			B = generateRandom(uppper_bound, offset, precision, size);
		}
	}


	double runCannonMultiplicationTest(int argc, char* argv[], int dim) {
		double* A, *B, *C, *A_block, *B_block, *C_block;
		int size;
		int block_size;
		double start_count, end_count, delta;
		size = dim;
		grid_size = sqrt((double)process_num);
		if (process_num != grid_size * grid_size) {
			if (proces_rank == 0) {
				printf("Number of processes must be a perfect square \n");
			}
			return 1;
		}
		initGridComms();
		initCannon(A, B, C, A_block, B_block, C_block, size, block_size);
		scatterBlocks(A, A_block, B, B_block, size, block_size);

		start_count = MPI_Wtime();
		calculate(A_block, B_block, C_block, size, block_size);
		end_count = MPI_Wtime();

		collectResultCannon(C, C_block, size, block_size);
		deconstruct(A, B, C, A_block, B_block,
			C_block);

		delta = end_count - start_count;
		if (proces_rank == 0)
			std::cout << "Cannon Test results (size " << size << "x" << size << " ): " << delta << std::endl;
		return delta;
	}

	void CreateGridCommunicators() {
		int DimSize[2]; // Number of processes in each dimension of the grid
		int Periodic[2]; // =1, if the grid dimension should be periodic
		int Subdims[2]; // =1, if the grid dimension should be fixed
		DimSize[0] = grid_size;
		DimSize[1] = grid_size;
		Periodic[0] = 0;
		Periodic[1] = 0;
		// Creation of the Cartesian communicator
		MPI_Cart_create(MPI_COMM_WORLD, 2, DimSize, Periodic, 1, &grid_Comm);
		// Determination of the cartesian coordinates for every process
		MPI_Cart_coords(grid_Comm, proces_rank, 2, grid);
		// Creating communicators for rows
		Subdims[0] = 0; // Dimensionality fixing
		Subdims[1] = 1; // The presence of the given dimension in the subgrid
		MPI_Cart_sub(grid_Comm, Subdims, &row_Comm);
		// Creating communicators for columns
		Subdims[0] = 1;
		Subdims[1] = 0;
		MPI_Cart_sub(grid_Comm, Subdims, &col_Comm);
	}
	// Function for memory allocation and data initialization
	void ProcessInitialization(double*& pAMatrix, double*& pBMatrix,
		double*& pCMatrix, double*& pAblock, double*& pBblock, double*& pCblock,
		double*& pTemporaryAblock, int& Size, int& BlockSize) {

		BlockSize = Size / grid_size;
		pAblock = new double[BlockSize * BlockSize];
		pBblock = new double[BlockSize * BlockSize];
		pCblock = new double[BlockSize * BlockSize];
		pTemporaryAblock = new double[BlockSize * BlockSize];
		for (int i = 0; i < BlockSize * BlockSize; i++) {
			pCblock[i] = 0;
		}
		if (proces_rank == 0) {
			pAMatrix = new double[Size * Size];
			pBMatrix = new double[Size * Size];
			pCMatrix = new double[Size * Size];
			//DummyDataInitialization(pAMatrix, pBMatrix, Size);
			pAMatrix = generateRandom(uppper_bound, offset, precision, Size);
			/*RandomDataInitialization(pAMatrix, pBMatrix, Size);*/
		}
	}
	// Function for checkerboard matrix decomposition
	void CheckerboardMatrixScatter(double* pMatrix, double* pMatrixBlock,
		int Size, int BlockSize) {
		double* MatrixRow = new double[BlockSize * Size];
		if (grid[1] == 0) {
			MPI_Scatter(pMatrix, BlockSize * Size, MPI_DOUBLE, MatrixRow,
				BlockSize * Size, MPI_DOUBLE, 0, col_Comm);
		}
		for (int i = 0; i < BlockSize; i++) {
			MPI_Scatter(&MatrixRow[i * Size], BlockSize, MPI_DOUBLE,
				&(pMatrixBlock[i * BlockSize]), BlockSize, MPI_DOUBLE, 0, row_Comm);
		}
		delete[] MatrixRow;
	}
	// Data distribution among the processes
	void DataDistribution(double* pAMatrix, double* pBMatrix, double* pMatrixAblock, double* pBblock, int Size, int BlockSize) {
		// Scatter the matrix among the processes of the first grid column
		CheckerboardMatrixScatter(pAMatrix, pMatrixAblock, Size, BlockSize);
		CheckerboardMatrixScatter(pBMatrix, pBblock, Size, BlockSize);
	}
	// Function for gathering the result matrix
	void ResultCollection(double* pCMatrix, double* pCblock, int Size,
		int BlockSize) {
		double* pResultRow = new double[Size * BlockSize];
		for (int i = 0; i < BlockSize; i++) {
			MPI_Gather(&pCblock[i * BlockSize], BlockSize, MPI_DOUBLE,
				&pResultRow[i * Size], BlockSize, MPI_DOUBLE, 0, row_Comm);
		}
		if (grid[1] == 0) {
			MPI_Gather(pResultRow, BlockSize * Size, MPI_DOUBLE, pCMatrix,
				BlockSize * Size, MPI_DOUBLE, 0, col_Comm);
		}
		delete[] pResultRow;
	}
	// Broadcasting blocks of the matrix A to process grid rows
	void ABlockCommunication(int iter, double* pAblock, double* pMatrixAblock,
		int BlockSize) {
		// Defining the leading process of the process grid row
		int Pivot = (grid[0] + iter) % grid_size;
		// Copying the transmitted block in a separate memory buffer
		if (grid[1] == Pivot) {
			for (int i = 0; i < BlockSize * BlockSize; i++)
				pAblock[i] = pMatrixAblock[i];
		}
		// Block broadcasting
		MPI_Bcast(pAblock, BlockSize * BlockSize, MPI_DOUBLE, Pivot, row_Comm);
	}
	// Function for cyclic shifting the blocks of the matrix B
	void BblockCommunication(double* pBblock, int BlockSize) {
		MPI_Status Status;
		int NextProc = grid[0] + 1;
		if (grid[0] == grid_size - 1) NextProc = 0;
		int PrevProc = grid[0] - 1;
		if (grid[0] == 0) PrevProc = grid_size - 1;
		MPI_Sendrecv_replace(pBblock, BlockSize * BlockSize, MPI_DOUBLE,
			NextProc, 0, PrevProc, 0, col_Comm, &Status);
	}
	// Function for parallel execution of the Fox method
	void ParallelResultCalculation(double* pAblock, double* pMatrixAblock,
		double* pBblock, double* pCblock, int BlockSize) {
		for (int iter = 0; iter < grid_size; iter++) {
			// Sending blocks of matrix A to the process grid rows
			ABlockCommunication(iter, pAblock, pMatrixAblock, BlockSize);
			// Block multiplication
			multiplyBlocks(pAblock, pBblock, pCblock, BlockSize);
			// Cyclic shift of blocks of matrix B in process grid columns
			BblockCommunication(pBblock, BlockSize);
		}
	}
	// Test printing of the matrix block
	/*void TestBlocks(double* pBlock, int BlockSize, char str[]) {
		MPI_Barrier(MPI_COMM_WORLD);
		if (ProcRank == 0) {
			printf("%s \n", str);
		}
		for (int i = 0; i < ProcNum; i++) {
			if (ProcRank == i) {
				printf("ProcRank = %d \n", ProcRank);
				PrintMatrix(pBlock, BlockSize, BlockSize);
			}
			MPI_Barrier(MPI_COMM_WORLD);
		}
	}*/
	// Function for testing the matrix multiplication result
	void TestResult(double* pAMatrix, double* pBMatrix, double* pCMatrix,
		int Size) {
		double* pSerialResult; // Result matrix of serial multiplication
		double Accuracy = 1.e-6; // Comparison accuracy
		int equal = 0; // =1, if the matrices are not equal
		int i; // Loop variable
		if (proces_rank == 0) {
			pSerialResult = new double[Size * Size];
			for (i = 0; i < Size * Size; i++) {
				pSerialResult[i] = 0;
			}
			multiplyBlocks(pAMatrix, pBMatrix, pSerialResult, Size);
			for (i = 0; i < Size * Size; i++) {
				if (fabs(pSerialResult[i] - pCMatrix[i]) >= Accuracy)
					equal = 1;
			}
			if (equal == 1)
				printf("The results of serial and parallel algorithms are NOT"
					"identical. Check your code.");
			else
				printf("The results of serial and parallel algorithms are "
					"identical. ");
		}
	}
	// Function for computational process termination
	void ProcessTermination(double* pAMatrix, double* pBMatrix,
		double* pCMatrix, double* pAblock, double* pBblock, double* pCblock,
		double* pMatrixAblock) {
		if (proces_rank == 0) {
			delete[] pAMatrix;
			delete[] pBMatrix;
			delete[] pCMatrix;
		}
		delete[] pAblock;
		delete[] pBblock;
		delete[] pCblock;
		delete[] pMatrixAblock;
	}

	double runFoxMultiplicationTest(int argc, char* argv[], int dim) {
		double* pAMatrix; // First argument of matrix multiplication
		double* pBMatrix; // Second argument of matrix multiplication
		double* pCMatrix; // Result matrix
		int size; // Size of matrices
		int BlockSize; // Sizes of matrix blocks
		double* pAblock; // Initial block of matrix A
		double* pBblock; // Initial block of matrix B
		double* pCblock; // Block of result matrix C
		double* pMatrixAblock;
		double start_count, end_count, delta;
		grid_size = sqrt((double)process_num);
		if (process_num != grid_size * grid_size) {
			if (proces_rank == 0) {
				printf("Number of processes must be a perfect square \n");
			}
			return 1;
		}
		size = dim;
		// Creating the cartesian grid, row and column communcators
		CreateGridCommunicators();
		// Memory allocation and initialization of matrix elements
		ProcessInitialization(pAMatrix, pBMatrix, pCMatrix, pAblock, pBblock,
			pCblock, pMatrixAblock, size, BlockSize);
		DataDistribution(pAMatrix, pBMatrix, pMatrixAblock, pBblock, size,
			BlockSize);
		start_count = MPI_Wtime();
		// Execution of the Fox method
		ParallelResultCalculation(pAblock, pMatrixAblock, pBblock,
			pCblock, BlockSize);
		end_count = MPI_Wtime();
		// Gathering the result matrix
		ResultCollection(pCMatrix, pCblock, size, BlockSize);
		// TestResult(pAMatrix, pBMatrix, pCMatrix, Size); //
		// Process Termination
		ProcessTermination(pAMatrix, pBMatrix, pCMatrix, pAblock, pBblock,
			pCblock, pMatrixAblock);

		delta = end_count - start_count;
		if (proces_rank == 0)
			std::cout << "Fox Test results (size " << size << "x" << size << " ): " << delta << std::endl;
		return delta;
	}
};

