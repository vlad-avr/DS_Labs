#include <iostream>
#include <mpi.h>
#include <random>
#include <time.h>
#include "Matrix.h"

/*
			TESTING REULTS
			1 Process (close to simple multiplication)
	sizes	time LS		time C		time F
	75		0.0014342	0.0014355	0.0014487
	150		0.0097122	0.0114491	0.0112273
	300		0.0737388	0.104838	0.108422
	600		0.585983	0.938643	0.935126
	1200	4.7901		10.2771		10.1112
	2400	38.1361		126.917		120.128
			4 Processes
	sizes	time LS		time C		time F
	72		0.0015		0.0003661	0.0003461
	144		0.0023623	0.0049278	0.0025901
	288		0.0283144	0.0234162	0.0292338
	576		0.171016	0.313588	0.324538
	1152	1.42826		3.2519		3.21194
	2304	12.3019		34.529		39.2149
			9 Processes
	sizes	time LS		time C		time F
	81		0.0007338	0.0086338	0.0030108
	162		0.0066713	0.0143497	0.0133035
	324		0.0350313	0.0455015	0.0371725
	648		0.217905	0.366119	0.295722
	1296	1.97816		3.60472		3.86905
	2592	17.4416		40.7901		41.4177
*/

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

