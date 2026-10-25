package com.library.repository;

import com.library.model.Loan;

@org.springframework.stereotype.Repository
public class LoanRepository extends InMemoryRepository<Loan, Long> {
}
