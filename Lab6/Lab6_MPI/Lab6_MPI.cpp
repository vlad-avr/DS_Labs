#include <iostream>
#include <mpi.h>
#include <random>
#include <time.h>
#include "Matrix.h"

int main(int argc, char* argv[])
{
	srand(time(0));
	Matrix* matrix = new Matrix(2, 2);
	Matrix* matrix1 = new Matrix(2, 2);
	matrix1->generate_matrix(5, -2, 0);
	matrix->generate_matrix(5, -2, 0);
	std::cout << "Matrix A : \n";
	matrix->print();
	std::cout << "\nMatrix B : \n";
	matrix1->print();
	Matrix* C = Matrix::simple_multiplication(matrix, matrix1);
	std::cout << "\n Matrix A*B : \n";
	C->print();
	delete matrix1;
	delete matrix;
 //   std::cout << "Hello World!\n";
	//int myid, numprocs, namelen;
	//char processor_name[MPI_MAX_PROCESSOR_NAME];

	//MPI_Init(&argc, &argv);        // starts MPI
	//MPI_Comm_rank(MPI_COMM_WORLD, &myid);  // get current process id
	//MPI_Comm_size(MPI_COMM_WORLD, &numprocs);      // get number of processeser
	//MPI_Get_processor_name(processor_name, &namelen);

	//if (myid == 0) printf("number of processes: %d\n...", numprocs);
	//printf("%s: Hello world from process %d \n", processor_name, myid);

	//MPI_Finalize();

	return 0;
}

