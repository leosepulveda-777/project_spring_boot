package com.cesde.project_spring_boot.controller;

import com.cesde.project_spring_boot.dto.UserDTO;
import com.cesde.project_spring_boot.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UserController - Controlador REST para manejar peticiones HTTP
 *
 * @RestController: Indica que esta clase maneja peticiones REST
 * @RequestMapping: Define el path base para todos los endpoints (/api/users)
 * @Tag: Para documentación de Swagger
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "👥 Gestión de Usuarios", description = "API para crear, leer, actualizar y eliminar usuarios")
public class UserController {

    // Inyectar el servicio
    @Autowired
    private UserService userService;

    // ========================================
    // 📋 OBTENER USUARIOS - DIFERENTES VERSIONES
    // ========================================

    /**
     * 🌟 VERSIÓN 1: GET /api/users - Obtener TODOS los usuarios (sin paginación)
     *
     * ✅ Usar para: Pocas cantidades de datos
     * ❌ NO usar para: Miles de usuarios (problemas de memoria)
     */
    @GetMapping
    @Operation(summary = "Obtener todos los usuarios",
            description = "Retorna la lista completa de usuarios sin paginación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        try {
            List<UserDTO> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 🌟 VERSIÓN 2: GET /api/users/paged - Obtener usuarios CON PAGINACIÓN
     *
     * Query Parameters:
     * - page=0 (número de página, empieza en 0)
     * - size=10 (cantidad de elementos por página)
     * - sort=firstName (campo para ordenar)
     * - direction=asc (dirección: asc o desc)
     *
     * Ejemplos:
     * - /api/users/paged (página 0, 10 elementos)
     * - /api/users/paged?page=1&size=5
     * - /api/users/paged?page=0&size=20&sort=email&direction=desc
     */
    @GetMapping("/paged")
    @Operation(summary = "Obtener usuarios con paginación",
            description = "Retorna usuarios paginados con opciones de ordenamiento")
    public ResponseEntity<Page<UserDTO>> getUsersPaged(
            @Parameter(description = "Número de página (empieza en 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Cantidad de elementos por página", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Campo para ordenar", example = "firstName")
            @RequestParam(defaultValue = "firstName") String sort,

            @Parameter(description = "Dirección de orden (asc o desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String direction) {

        try {
            // Crear objeto Sort para ordenamiento
            Sort.Direction sortDirection = Sort.Direction.fromString(direction);
            Sort sortBy = Sort.by(sortDirection, sort);

            // Crear Pageable (objeto que maneja la paginación)
            Pageable pageable = PageRequest.of(page, size, sortBy);

            Page<UserDTO> users = userService.getAllUsersWithPaging(pageable);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ========================================
    // 🔍 OBTENER UN USUARIO - DIFERENTES VERSIONES
    // ========================================

    /**
     * 🌟 VERSIÓN 1: GET /api/users/1 - Obtener usuario por ID (Path Variable)
     *
     * @PathVariable: Toma el {id} de la URL y lo pasa como parámetro
     * Ejemplos: /api/users/1, /api/users/25, /api/users/999
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID",
            description = "Retorna un usuario específico usando su ID único")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<UserDTO> getUserById(
            @Parameter(description = "ID único del usuario", required = true, example = "1")
            @PathVariable Long id) {

        try {
            UserDTO user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 🌟 VERSIÓN 2: GET /api/users/by-id?id=1 - Obtener usuario por ID (Query Param)
     *
     * @RequestParam: Toma el parámetro ?id=1 de la URL
     * Ejemplos: /api/users/by-id?id=1, /api/users/by-id?id=25
     */
    @GetMapping("/by-id")
    @Operation(summary = "Obtener usuario por ID (query param)",
            description = "Alternativa usando query parameter en lugar de path variable")
    public ResponseEntity<UserDTO> getUserByIdParam(
            @Parameter(description = "ID del usuario", required = true, example = "1")
            @RequestParam Long id) {

        try {
            UserDTO user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 🌟 VERSIÓN 3: GET /api/users/email/juan@ejemplo.com - Por email (Path Variable)
     *
     * Ejemplos: /api/users/email/juan@ejemplo.com
     */
    @GetMapping("/email/{email}")
    @Operation(summary = "Obtener usuario por email (path)",
            description = "Busca usuario usando email como parte de la URL")
    public ResponseEntity<UserDTO> getUserByEmailPath(
            @Parameter(description = "Email del usuario", required = true, example = "juan@ejemplo.com")
            @PathVariable String email) {

        try {
            UserDTO user = userService.getUserByEmail(email);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 🌟 VERSIÓN 4: GET /api/users/by-email?email=juan@ejemplo.com - Por email (Query Param)
     *
     * Ejemplos: /api/users/by-email?email=juan@ejemplo.com
     */
    @GetMapping("/by-email")
    @Operation(summary = "Obtener usuario por email (query)",
            description = "Busca usuario usando email como query parameter")
    public ResponseEntity<UserDTO> getUserByEmailQuery(
            @Parameter(description = "Email del usuario", required = true, example = "juan@ejemplo.com")
            @RequestParam String email) {

        try {
            UserDTO user = userService.getUserByEmail(email);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ========================================
    // 🔎 BÚSQUEDAS - MÚLTIPLES VERSIONES
    // ========================================

    /**
     * 🌟 VERSIÓN 1: GET /api/users/search?name=Juan - Buscar sin paginación
     *
     * @RequestParam: Toma el parámetro ?name=Juan de la URL
     * Ejemplos:
     * - /api/users/search?name=Juan
     * - /api/users/search?name=ana
     */
    @GetMapping("/search")
    @Operation(summary = "Buscar usuarios por nombre",
            description = "Busca usuarios que contengan el nombre especificado")
    public ResponseEntity<List<UserDTO>> searchUsers(
            @Parameter(description = "Nombre a buscar", required = true, example = "Juan")
            @RequestParam String name) {

        try {
            List<UserDTO> users = userService.searchUsersByName(name);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 🌟 VERSIÓN 2: GET /api/users/search/paged?name=Juan&page=0&size=5 - Con paginación
     *
     * Ejemplos:
     * - /api/users/search/paged?name=Juan
     * - /api/users/search/paged?name=ana&page=1&size=3
     */
    @GetMapping("/search/paged")
    @Operation(summary = "Buscar usuarios con paginación",
            description = "Busca usuarios con soporte de paginación")
    public ResponseEntity<Page<UserDTO>> searchUsersPaged(
            @Parameter(description = "Nombre a buscar", required = true, example = "Juan")
            @RequestParam String name,

            @Parameter(description = "Página", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Tamaño de página", example = "10")
            @RequestParam(defaultValue = "10") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("firstName"));
            Page<UserDTO> users = userService.searchUsersByNameWithPaging(name, pageable);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 🌟 VERSIÓN 3: GET /api/users/search/by-firstname?name=Juan - Buscar por nombre específico
     */
    @GetMapping("/search/by-firstname")
    @Operation(summary = "Buscar por nombre específico",
            description = "Busca usuarios por nombre (firstName)")
    public ResponseEntity<List<UserDTO>> searchByFirstName(
            @RequestParam String name) {

        try {
            List<UserDTO> users = userService.getUsersByFirstNameStarting(name);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 🌟 VERSIÓN 4: GET /api/users/domain/gmail.com - Por dominio de email
     */
    @GetMapping("/domain/{domain}")
    @Operation(summary = "Buscar por dominio de email",
            description = "Encuentra usuarios con un dominio de email específico")
    public ResponseEntity<List<UserDTO>> getUsersByEmailDomain(
            @Parameter(description = "Dominio de email", example = "gmail.com")
            @PathVariable String domain) {

        try {
            List<UserDTO> users = userService.getUsersByEmailDomain(domain);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ========================================
    // 📝 CREAR USUARIO
    // ========================================

    /**
     * POST /api/users - Crear un nuevo usuario
     *
     * @PostMapping: Maneja peticiones HTTP POST
     * @RequestBody: Convierte el JSON del body en un objeto UserDTO
     * @Valid: Válida los datos usando las anotaciones del DTO
     *
     * Ejemplo de JSON:
     * {
     *   "firstName": "Juan",
     *   "lastName": "Pérez",
     *   "email": "juan@ejemplo.com",
     *   "phone": "+56912345678"
     * }
     */
    @PostMapping
    @Operation(summary = "Crear nuevo usuario",
            description = "Crea un usuario con la información proporcionada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o email ya existe")
    })
    public ResponseEntity<UserDTO> createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Información del nuevo usuario", required = true)
            @Valid @RequestBody UserDTO userDTO) {

        try {
            UserDTO createdUser = userService.createUser(userDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ========================================
    // ✏️ ACTUALIZAR USUARIO - DIFERENTES VERSIONES
    // ========================================

    /**
     * 🌟 VERSIÓN 1: PUT /api/users/1 - Actualizar completo (Path Variable)
     *
     * PUT significa reemplazar TODO el recurso
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario completo",
            description = "Actualiza toda la información del usuario")
    public ResponseEntity<UserDTO> updateUser(
            @Parameter(description = "ID del usuario", required = true, example = "1")
            @PathVariable Long id,

            @Valid @RequestBody UserDTO userDTO) {

        try {
            UserDTO updatedUser = userService.updateUser(id, userDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 🌟 VERSIÓN 2: PUT /api/users/update?id=1 - Actualizar (Query Param)
     */
    @PutMapping("/update")
    @Operation(summary = "Actualizar usuario (query param)",
            description = "Actualiza usuario usando ID como query parameter")
    public ResponseEntity<UserDTO> updateUserByParam(
            @RequestParam Long id,
            @Valid @RequestBody UserDTO userDTO) {

        try {
            UserDTO updatedUser = userService.updateUser(id, userDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ========================================
    // 🗑️ ELIMINAR USUARIO - DIFERENTES VERSIONES
    // ========================================

    /**
     * 🌟 VERSIÓN 1: DELETE /api/users/1 - Eliminar (Path Variable)
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario",
            description = "Elimina un usuario del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID del usuario", required = true, example = "1")
            @PathVariable Long id) {

        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 🌟 VERSIÓN 2: DELETE /api/users/remove?id=1 - Eliminar (Query Param)
     */
    @DeleteMapping("/remove")
    @Operation(summary = "Eliminar usuario (query param)",
            description = "Elimina usuario usando ID como query parameter")
    public ResponseEntity<Void> deleteUserByParam(@RequestParam Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ========================================
    // 📊 ESTADÍSTICAS Y UTILIDADES
    // ========================================

    /**
     * GET /api/users/count - Obtener cantidad total de usuarios
     */
    @GetMapping("/count")
    @Operation(summary = "Contar usuarios",
            description = "Retorna el número total de usuarios en el sistema")
    public ResponseEntity<Map<String, Object>> getUserCount() {
        try {
            long count = userService.getTotalUsersCount();

            Map<String, Object> response = new HashMap<>();
            response.put("totalUsers", count);
            response.put("message", "Total de usuarios en el sistema");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/users/exists/email?email=test@ejemplo.com - Verificar si existe email
     */
    @GetMapping("/exists/email")
    @Operation(summary = "Verificar si existe email",
            description = "Verifica si ya existe un usuario con ese email")
    public ResponseEntity<Map<String, Object>> checkEmailExists(@RequestParam String email) {
        try {
            boolean exists = userService.existsByEmail(email);

            Map<String, Object> response = new HashMap<>();
            response.put("email", email);
            response.put("exists", exists);
            response.put("message", exists ? "El email ya está registrado" : "El email está disponible");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/users/recent?hours=24 - Usuarios creados recientemente
     */
    @GetMapping("/recent")
    @Operation(summary = "Usuarios recientes",
            description = "Obtiene usuarios creados en las últimas X horas")
    public ResponseEntity<List<UserDTO>> getRecentUsers(
            @Parameter(description = "Horas hacia atrás", example = "24")
            @RequestParam(defaultValue = "24") int hours) {

        try {
            List<UserDTO> recentUsers = userService.getRecentUsers(hours);
            return ResponseEntity.ok(recentUsers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/users/stats/domain?domain=gmail.com - Estadísticas por dominio
     */
    @GetMapping("/stats/domain")
    @Operation(summary = "Estadísticas por dominio",
            description = "Cuenta usuarios por dominio de email")
    public ResponseEntity<Map<String, Object>> getDomainStats(@RequestParam String domain) {
        try {
            long count = userService.countUsersByEmailDomain(domain);
            List<UserDTO> users = userService.getUsersByEmailDomain(domain);

            Map<String, Object> response = new HashMap<>();
            response.put("domain", domain);
            response.put("count", count);
            response.put("users", users);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/search/lastname")
@Operation(summary = "Search users by last name", description = "Retrieve users whose last name contains the given string (case insensitive)")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Successfully retrieved users"),
    @ApiResponse(responseCode = "500", description = "Internal server error")
})
public ResponseEntity<List<UserDTO>> searchUsersByLastName(
        @Parameter(description = "Last name or part of it", example = "Pérez")
        @RequestParam("name") String lastName) {
    try {
        List<UserDTO> users = userService.searchByLastName(lastName);
        return ResponseEntity.ok(users); // 200 con lista vacía si no encuentra nada
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
}