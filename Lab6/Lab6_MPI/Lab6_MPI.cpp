#include <iostream>
#include <mpi.h>
#include <random>
#include <time.h>
#include "Matrix.h"

int main(int argc, char* argv[])
{
	srand(time(0));
	setvbuf(stdout, 0, _IONBF, 0);
    MPI_Init(&argc, &argv);
	int dimensions = 64;

    MPI_Comm_rank(MPI_COMM_WORLD, &proces_rank);
    MPI_Comm_size(MPI_COMM_WORLD, &process_num);
	
	MPI_Bcast(&dimensions, 1, MPI_INT, 0, MPI_COMM_WORLD);
	/*Matrix::runLineSchemeMultiplicationTest(argc, argv, dimensions);
	Matrix::runCannonMultiplicationTest(argc, argv, dimensions);
	Matrix::runFoxMultiplicationTest(argc, argv, dimensions);*/
	Matrix::runAlgorithmTest(argc, argv, dimensions);
	MPI_Finalize();
	return 0;
}

