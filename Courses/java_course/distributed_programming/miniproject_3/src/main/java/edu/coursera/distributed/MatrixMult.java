package edu.coursera.distributed;

import edu.coursera.distributed.util.MPI;
import edu.coursera.distributed.util.MPI.MPIException;

/**
 * A wrapper class for a parallel, MPI-based matrix multiply implementation.
 */
public class MatrixMult {
    /**
     *
     * @param a Input matrix
     * @param b Input matrix
     * @param c Output matrix
     * @param mpi MPI object supporting MPI APIs
     * @throws MPIException On MPI error. It is not expected that your
     *                      implementation should throw any MPI errors during
     *                      normal operation.
     */
    public static void parallelMatrixMultiply(Matrix a, Matrix b, Matrix c,
                                              final MPI mpi) throws MPIException {


        /**
         *  SPMD -> Same program running on all processes / ranks
         *  MPI_COMM_WORLD is the communicator
         *  Matrix is implemented as 1D array as MPI uses 1D buffer to send / receive
         */

        // Each process has a Rank to Identify it

        final int rank = mpi.MPI_Comm_rank(mpi.MPI_COMM_WORLD);

        // Number of Processes
        final int size = mpi.MPI_Comm_size(mpi.MPI_COMM_WORLD);

        final int rows = c.getNRows();
        final int chunkRow = (rows + size - 1) / size;
        // Divide into Chunks for each process. Each Process runs only the row chunk

        // Get Start and End Index of each chunk
        final int start = rank * chunkRow;
        int end = (rank + 1) * chunkRow;
        if (end > rows) end = rows;
        //  Edge Case check -> endIndex is bound by actual Size of Resultant matrix

        //BroadCast Matrix a and b to other ranks
        //Broadcast sends data in array => need to convert 2D Matrix to Array
        mpi.MPI_Bcast(a.getValues(), 0, a.getNRows() * a.getNCols(), 0, mpi.MPI_COMM_WORLD);
        //BroadCast sends array of info, takes offset, num of elements, Root Rank, Communicator as args
        mpi.MPI_Bcast(b.getValues(), 0, b.getNRows() * b.getNCols(), 0 , mpi.MPI_COMM_WORLD);



        for (int i = start; i < end; i++) {
            for (int j = 0; j < c.getNCols(); j++) {
                c.set(i, j, 0.0);
                for (int k = 0; k < b.getNRows(); k++) {
                    c.incr(i, j, a.get(i, k) * b.get(k, j));
                }
            }
        }

        // Gather Result if Rank 0, Send Results if Rank != 0
        if (rank == 0) {

            // Buffer for the Other Process's Results that Rank 0 will receive
            MPI.MPI_Request [] requests = new MPI.MPI_Request[size - 1];

            // Iterate through Ranks and Receive their Results in non Blocking way
            for (int i = 1; i < size; i++) {
                // get Row Start and Row End for ith Chunk
                final int rankStartRow = i * chunkRow;
                int rankEndRow = (i+1) * chunkRow;

                // EdgeCase if Rank End row -> (i + 1) * chunkSize is greater than number of Actual Rows
                if (rankEndRow > rows) rankEndRow = rows;

                final int rowOffset = rankStartRow * c.getNCols();

                //Number of Elements in the chunk
                final int nElements = ( rankEndRow - rankStartRow ) * c.getNCols();

                // Non Blocking Receive from other ranks
                requests[i-1] = mpi.MPI_Irecv(c.getValues(), rowOffset, nElements, i, i, mpi.MPI_COMM_WORLD);
                // Buffer to Receive, RowOffset, num of Elements, Rank of Sender, Tag for Message, Comm
            }
            mpi.MPI_Waitall(requests);
            // like async-await, wait for all other ranks to return result (Async -> reason for Speedup)

        } else {
            // Other Ranks send their Results to rook rank
            mpi.MPI_Send(c.getValues(), start * c.getNCols(), (end - start) * c.getNCols(), 0, rank, mpi.MPI_COMM_WORLD);
            // Buffer to send , offset, num elements, destination rank, myrank, comm
        }
    }
}