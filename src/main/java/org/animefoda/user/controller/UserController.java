package org.animefoda.user.controller;

import entities.role.Role;
import entities.role.RoleName;
import entities.user.User;
import entities.user.UserDTO;
import exception.AlreadyExistsException;
import exception.BadCredentialsException;
import org.animefoda.user.annotation.DecryptedBody;
import org.animefoda.user.request.RegisterRequest;
import org.animefoda.user.request.UpdateProfileRequest;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import response.ApiResponse;
import services.RoleService;
import services.UserService;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RestController
public class UserController {

    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public UserController(
            UserService userService,
            RoleService roleService,
            PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping({ "/register", "/p/register" })
    public ApiResponse<UserDTO> register(@DecryptedBody @RequestBody RegisterRequest req)
            throws AlreadyExistsException {
        // Validações básicas
        if (req.email() == null || req.password() == null || req.username() == null) {
            throw new IllegalArgumentException("Email, password and username are required");
        }

        // Gera hash da senha
        String salt = BCrypt.gensalt();
        String rawHash = BCrypt.hashpw(req.password(), salt);
        String prefixedPassword = "{bcrypt}" + rawHash;

        // Role padrão
        Set<Role> roles = new HashSet<>();
        roleService.findByName(RoleName.ROLE_USER).ifPresent(roles::add);

        // Cria usuário
        User user = User.builder()
                .name(req.name())
                .surname(req.surname())
                .username(req.username())
                .email(req.email())
                .password(prefixedPassword)
                .salt(salt)
                .superUser(false)
                .roles(roles)
                .birthDate(req.birthDate())
                .build();

        User savedUser = userService.save(user);

        return ApiResponse.setSuccess(savedUser.toUserDTO());
    }

    @GetMapping("/me")
    public ApiResponse<UserDTO> me(@AuthenticationPrincipal Jwt jwt) throws BadCredentialsException {
        String userId = jwt.getSubject();
        User user = userService.findById(UUID.fromString(userId))
                .orElseThrow(() -> new BadCredentialsException("User not found"));
        return ApiResponse.setSuccess(user.toUserDTO());
    }

    @PutMapping("/me")
    public ApiResponse<UserDTO> updateProfile(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody UpdateProfileRequest req) throws BadCredentialsException {
        String userId = jwt.getSubject();
        User user = userService.findById(UUID.fromString(userId))
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        // TODO: Implementar atualização de perfil
        // Precisa adicionar método update no UserService do baseproject

        return ApiResponse.setSuccess(user.toUserDTO());
    }
}
