package com.cesde.project_spring_boot.service;

import com.cesde.project_spring_boot.dto.UserDTO;
import com.cesde.project_spring_boot.model.User;
import com.cesde.project_spring_boot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * UserService - Contiene la lógica de negocio para usuarios
 *
 * @Service: Le dice a Spring que esta es una clase de servicio
 * Aquí van las reglas de negocio, validaciones, etc.
 */
@Service
public class UserService {

    // Inyección de dependencia - Spring nos da una instancia del repository
    @Autowired
    private UserRepository userRepository;

    // ========================================
    // MÉTODOS BÁSICOS CRUD
    // ========================================

    /**
     * Obtener todos los usuarios (sin paginación)
     */
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();

        // Convertir de User a UserDTO usando Stream API
        return users.stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Obtener todos los usuarios CON PAGINACIÓN
     * Gracias a JpaRepository, tenemos paginación automática
     */
    public Page<UserDTO> getAllUsersWithPaging(Pageable pageable) {
        Page<User> usersPage = userRepository.findAll(pageable);

        // Convertir Page<User> a Page<UserDTO>
        List<UserDTO> userDTOs = usersPage.getContent().stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());

        return new PageImpl<>(userDTOs, pageable, usersPage.getTotalElements());
    }

    /**
     * Obtener un usuario por ID
     */
    public UserDTO getUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            return UserDTO.fromEntity(userOptional.get());
        } else {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
    }

    /**
     * Crear un nuevo usuario
     */
    public UserDTO createUser(UserDTO userDTO) {
        // Validar que el email no exista
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Ya existe un usuario con el email: " + userDTO.getEmail());
        }

        // Convertir DTO a entidad y guardar
        User user = userDTO.toEntity();
        User savedUser = userRepository.save(user);

        return UserDTO.fromEntity(savedUser);
    }

    /**
     * Actualizar un usuario existente
     */
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Verificar si el email cambió y si ya existe
            if (!user.getEmail().equals(userDTO.getEmail()) &&
                    userRepository.existsByEmail(userDTO.getEmail())) {
                throw new RuntimeException("Ya existe un usuario con el email: " + userDTO.getEmail());
            }

            // Actualizar los campos
            user.setFirstName(userDTO.getFirstName());
            user.setLastName(userDTO.getLastName());
            user.setEmail(userDTO.getEmail());
            user.setPhone(userDTO.getPhone());

            User updatedUser = userRepository.save(user);
            return UserDTO.fromEntity(updatedUser);
        } else {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
    }

    /**
     * Eliminar un usuario
     */
    public void deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
    }

    // ========================================
    // MÉTODOS DE BÚSQUEDA
    // ========================================

    /**
     * Obtener usuario por email
     */
    public UserDTO getUserByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            return UserDTO.fromEntity(userOptional.get());
        } else {
            throw new RuntimeException("Usuario no encontrado con email: " + email);
        }
    }

    /**
     * Buscar usuarios por nombre (sin paginación)
     */
    public List<UserDTO> searchUsersByName(String name) {
        List<User> users = userRepository.findByNameContaining(name);

        return users.stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Buscar usuarios por nombre CON PAGINACIÓN
     */
    public Page<UserDTO> searchUsersByNameWithPaging(String name, Pageable pageable) {
        Page<User> usersPage = userRepository.findByNameContainingWithPaging(name, pageable);

        List<UserDTO> userDTOs = usersPage.getContent().stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());

        return new PageImpl<>(userDTOs, pageable, usersPage.getTotalElements());
    }

    /**
     * Buscar usuarios por nombre que empiece con...
     */
    public List<UserDTO> getUsersByFirstNameStarting(String firstName) {
        List<User> users = userRepository.findByFirstNameContainingIgnoreCase(firstName);

        return users.stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Buscar usuarios por dominio de email
     */
    public List<UserDTO> getUsersByEmailDomain(String domain) {
        List<User> users = userRepository.findByEmailDomainNative(domain);

        return users.stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Obtener usuarios creados recientemente
     */
    public List<UserDTO> getRecentUsers(int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        List<User> users = userRepository.findByCreatedAtAfter(since);

        return users.stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // ========================================
    // MÉTODOS DE ESTADÍSTICAS
    // ========================================

    /**
     * Contar total de usuarios
     */
    public long getTotalUsersCount() {
        return userRepository.count(); // Método de JpaRepository
    }

    /**
     * Contar usuarios por dominio de email
     */
    public long countUsersByEmailDomain(String domain) {
        return userRepository.countByEmailDomain(domain);
    }

    /**
     * Verificar si existe un usuario con ese email
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    public List<UserDTO> searchByLastName(String lastName) {
    List<User> users = userRepository.findByLastNameContainingIgnoreCase(lastName);
    return users.stream().map(UserDTO::fromEntity).collect(Collectors.toList());
}
}