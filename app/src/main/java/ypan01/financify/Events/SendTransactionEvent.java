package ypan01.financify.Events;


import ypan01.financify.Transaction;

public class SendTransactionEvent {
    private Transaction transaction;

    public SendTransactionEvent(Transaction trans) {
        transaction = trans;
    }

    public Transaction getTransaction() {
        return transaction;
    }
}
