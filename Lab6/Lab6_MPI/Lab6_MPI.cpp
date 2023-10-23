#include <iostream>
#include <mpi.h>
#include <random>
#include <time.h>
#include "Matrix.h"

int main(int argc, char* argv[])
{
	srand(time(0));
	/*Matrix* matrix = new Matrix(2, 2);
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
	delete matrix;*/
    MPI_Init(&argc, &argv);

    int rank, size;
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    MPI_Comm_size(MPI_COMM_WORLD, &size);
	std::cout << "\nPrinting from task " << rank << " \\ " << size;
	MPI_Finalize();
	return 0;
}

