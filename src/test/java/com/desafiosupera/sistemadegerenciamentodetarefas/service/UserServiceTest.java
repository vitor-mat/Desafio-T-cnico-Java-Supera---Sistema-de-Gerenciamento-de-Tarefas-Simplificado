package com.desafiosupera.sistemadegerenciamentodetarefas.service;

import com.desafiosupera.sistemadegerenciamentodetarefas.enums.UserRole;
import com.desafiosupera.sistemadegerenciamentodetarefas.model.User;
import com.desafiosupera.sistemadegerenciamentodetarefas.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Should be able to find users")
    void findAll() {
        List<User> users = Arrays.asList(
                new User("user1", "password1", UserRole.USER),
                new User("user2", "password2", UserRole.ADMIN)
        );
        when(userRepository.findAll()).thenReturn(users);

        // When
        List<User> foundUsers = userService.findAll();

        // Then
        assertEquals(users.size(), foundUsers.size());
        assertEquals(users.get(0).getLogin(), foundUsers.get(0).getLogin());
        assertEquals(users.get(1).getLogin(), foundUsers.get(1).getLogin());
    }

    @Test
    @DisplayName("Should return an empty list when no users are found")
    void findAllWhenNoUsersFound() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<User> foundUsers = userService.findAll();

        assertTrue(foundUsers.isEmpty());
    }
}