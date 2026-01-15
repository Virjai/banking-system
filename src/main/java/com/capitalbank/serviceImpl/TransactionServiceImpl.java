package com.capitalbank.serviceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.capitalbank.dao.TransactionDao;
import com.capitalbank.model.Transaction;
import com.capitalbank.service.TransactionService;

public class TransactionServiceImpl implements TransactionService {

    private TransactionDao transactionDao;

    public void setTransactionDao(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }

    // ================= CREATE =================
    @Override
    public Transaction createTransaction(Transaction transaction) {

        // Business rule
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setStatus("PENDING");

        return transactionDao.save(transaction);
    }

    // ================= READ =================
    @Override
    public Optional<List<Transaction>> getAllTransactions() {
        return transactionDao.findAllTransaction();
    }

    @Override
    public Optional<Transaction> getTransactionById(long transactionId) {
        return transactionDao.findByTransactionId(transactionId);
    }

    @Override
    public Optional<Transaction> getTransactionByDate(LocalDate start, LocalDate end) {
        return transactionDao.findTransactionByDate(start, end);
    }

    // ================= UPDATE =================
    @Override
    public boolean completeTransaction(long transactionId) {

        // Business rule
        String newStatus = "COMPLETED";

        return transactionDao.updateByTransactionId(transactionId);
    }

    // ================= DELETE =================
    @Override
    public boolean removeTransaction(long transactionId) {
        return transactionDao.deleteByTransactionId(transactionId);
    }
}
