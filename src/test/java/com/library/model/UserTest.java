package com.library.model;

import com.library.exception.InvalidDataException;
import com.library.exception.LoanLimitExceededException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void rejectsInvalidData() {
        assertThrows(InvalidDataException.class, () -> new User(" ", "ana@mail.com", 2));
        assertThrows(InvalidDataException.class, () -> new User("Ana", "no-es-email", 2));
        assertThrows(InvalidDataException.class, () -> new User("Ana", "ana@mail.com", 0));
    }

    @Test
    void cannotExceedLoanLimit() {
        User user = new User("Ana", "ana@mail.com", 1);
        user.registerLoan();

        assertFalse(user.canBorrow());
        assertThrows(LoanLimitExceededException.class, user::registerLoan);

        user.registerReturn();
        assertTrue(user.canBorrow());
    }
}
