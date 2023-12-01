package edu.coursera.concurrent;

import static edu.rice.pcdp.PCDP.isolated;

/**
 * A thread-safe transaction implementation using object-based isolation.
 */
public final class BankTransactionsUsingObjectIsolation
        extends ThreadSafeBankTransaction {
    /**
     * {@inheritDoc}
     */
    @Override
    public void issueTransfer(final int amount, final Account src,
            final Account dst) {
        /*
         * global isolation, based on the reference code provided in
         * BankTransactionsUsingGlobalIsolation. Keep in mind that isolation
         * must be applied to both src and dst.
         */
        try {
            isolated(src, dst, () -> {
                if (src.withdraw(amount)) {
                    dst.deposit(amount);
                }
            });
        } catch (UnsupportedOperationException e){
            e.printStackTrace();
        }
    }
}
