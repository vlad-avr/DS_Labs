#include <iostream>
#include <mpi.h>
#include <random>
#include <time.h>
#include "Matrix.h"

int main(int argc, char* argv[])
{
	srand(time(0));
	double* A = Matrix::generateRandom(20, 5, 10, 4);
	double* B = Matrix::generateFilled(2, 4);
	std::cout << "\n A:\n";
	Matrix::print(A, 4);
	std::cout << "\n B:\n";
	Matrix::print(B, 4);
	double* C = Matrix::simpleMultiplication(A, B, 4);
	std::cout << "\n C:\n";
	Matrix::print(C, 4);
    /*MPI_Init(&argc, &argv);

    int rank, size;
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    MPI_Comm_size(MPI_COMM_WORLD, &size);
	std::cout << "\nPrinting from task " << rank << " \\ " << size;
	MPI_Finalize();*/
	return 0;
}

