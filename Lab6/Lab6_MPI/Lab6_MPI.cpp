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
    MPI_Comm_rank(MPI_COMM_WORLD, &process_rank);
    MPI_Comm_size(MPI_COMM_WORLD, &process_num);
	if (process_num == 1) {
		Matrix::runAlgorithmTest(argc, argv, 75);
		Matrix::runAlgorithmTest(argc, argv, 150);
		Matrix::runAlgorithmTest(argc, argv, 300);
		Matrix::runAlgorithmTest(argc, argv, 600);
		Matrix::runAlgorithmTest(argc, argv, 1200);
		Matrix::runAlgorithmTest(argc, argv, 2400);
	}
	else if (process_num == 4) {
		Matrix::runAlgorithmTest(argc, argv, 72);
		Matrix::runAlgorithmTest(argc, argv, 144);
		Matrix::runAlgorithmTest(argc, argv, 288);
		Matrix::runAlgorithmTest(argc, argv, 576);
		Matrix::runAlgorithmTest(argc, argv, 1152);
		Matrix::runAlgorithmTest(argc, argv, 2304);
	}
	else if (process_num == 9) {
		Matrix::runAlgorithmTest(argc, argv, 81);
		Matrix::runAlgorithmTest(argc, argv, 162);
		Matrix::runAlgorithmTest(argc, argv, 324);
		Matrix::runAlgorithmTest(argc, argv, 648);
		Matrix::runAlgorithmTest(argc, argv, 1296);
		Matrix::runAlgorithmTest(argc, argv, 2592);
	}
	else {
		std::cout << "\nERROR : Invalid number of processes!";
	}
	
	MPI_Finalize();
	return 0;
}

