package com.library.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class LibraryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void fullLoanFlow() throws Exception {
        postBook("Dune").andExpect(status().isCreated()).andExpect(jsonPath("$.available").value(true));
        postUser("ana@mail.com", 1).andExpect(status().isCreated());

        mockMvc.perform(post("/library/loans").param("userEmail", "ana@mail.com").param("bookTitle", "Dune"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        mockMvc.perform(post("/library/loans").param("userEmail", "ana@mail.com").param("bookTitle", "Dune"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("El libro 'Dune' no está disponible"));

        mockMvc.perform(patch("/library/loans/1/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RETURNED"));

        mockMvc.perform(patch("/library/loans/1/return")).andExpect(status().isConflict());
    }

    @Test
    void mapsErrorsToHttpStatus() throws Exception {
        postUser("no-es-email", 1).andExpect(status().isBadRequest());
        postBook("Dune").andExpect(status().isCreated());
        postBook("Dune").andExpect(status().isConflict());

        mockMvc.perform(get("/library/books/Nada")).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.path").value("/library/books/Nada"));
        mockMvc.perform(get("/library/loans/abc")).andExpect(status().isBadRequest());
        mockMvc.perform(post("/library/loans").param("userEmail", "ana@mail.com"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/library/books").contentType(MediaType.APPLICATION_JSON).content("{malo"))
                .andExpect(status().isBadRequest());
    }

    private org.springframework.test.web.servlet.ResultActions postBook(String title) throws Exception {
        return mockMvc.perform(post("/library/books").contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"" + title + "\",\"author\":\"Frank Herbert\",\"genre\":\"Ciencia ficción\"}"));
    }

    private org.springframework.test.web.servlet.ResultActions postUser(String email, int limit) throws Exception {
        return mockMvc.perform(post("/library/users").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Ana\",\"email\":\"" + email + "\",\"loanLimit\":" + limit + "}"));
    }
}
