package com.desafiosupera.sistemadegerenciamentodetarefas.repository;

import com.desafiosupera.sistemadegerenciamentodetarefas.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    UserDetails findByLogin(String login);
}
