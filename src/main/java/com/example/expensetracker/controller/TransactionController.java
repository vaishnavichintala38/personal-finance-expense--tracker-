package com.example.expensetracker.controller;

import com.example.expensetracker.model.Transaction;
import com.example.expensetracker.repository.TransactionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TransactionController {

    private final TransactionRepository repository;

    public TransactionController(TransactionRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/transactions")
    public List<Transaction> getAll() {
        return repository.findAllByOrderByIdDesc();
    }

    @PostMapping("/transactions")
    public Transaction create(@RequestBody Transaction transaction) {
        return repository.save(transaction);
    }

    @DeleteMapping("/transactions/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/summary")
    public Map<String, Double> getSummary() {
        List<Transaction> all = repository.findAll();
        double income = all.stream()
                .filter(t -> "INCOME".equalsIgnoreCase(t.getType()) && t.getAmount() != null)
                .mapToDouble(Transaction::getAmount)
                .sum();
        double expense = all.stream()
                .filter(t -> "EXPENSE".equalsIgnoreCase(t.getType()) && t.getAmount() != null)
                .mapToDouble(Transaction::getAmount)
                .sum();

        Map<String, Double> summary = new HashMap<>();
        summary.put("income", income);
        summary.put("expense", expense);
        summary.put("balance", income - expense);
        return summary;
    }
}
