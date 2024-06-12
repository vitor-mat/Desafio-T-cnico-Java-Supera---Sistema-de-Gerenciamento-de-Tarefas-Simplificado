package com.desafiosupera.sistemadegerenciamentodetarefas.util;

import com.desafiosupera.sistemadegerenciamentodetarefas.model.User;
import com.desafiosupera.sistemadegerenciamentodetarefas.enums.UserRole;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Constants {
    public static final User USER_1 = new User("userName1", new BCryptPasswordEncoder().encode("123"), UserRole.ADMIN);
    public static final User USER_2 = new User("userName2", new BCryptPasswordEncoder().encode("456"), UserRole.USER);
}
