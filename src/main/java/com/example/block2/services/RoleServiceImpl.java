package com.example.block2.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.block2.dto.RoleDto;
import com.example.block2.entity.Role;
import com.example.block2.exceptions.EntityNotFoundException;
import com.example.block2.mapper.RoleMapper;
import com.example.block2.repositories.RoleRepository;
import com.example.block2.services.interfaces.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service class for managing roles.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    /**
     * Creates a new role.
     *
     * @param roleDto the role to create
     * @return the created role
     */
    @Override
    @Transactional
    public Role createRole(RoleDto roleDto) {
        log.info("Attempting to create role: {}", roleDto);
        Role role = roleMapper.toEntity(roleDto);
        Role savedRole = roleRepository.save(role);

        log.debug("Role created successfully: {}", savedRole);

        return savedRole;
    }

    /**
     * Retrieves all roles.
     *
     * @return a list of all roles
     */
    @Override
    public List<Role> getAllRoles() {
        log.info("Getting all roles");

        return roleRepository.findAll();
    }

    /**
     * Retrieves a role by its id.
     *
     * @param id the id of the role to retrieve
     * @return the retrieved role
     * @throws EntityNotFoundException if no role is found with the given id
     */
    @Override
    public Role getRole(Long id) {
        log.info("Get role with id: {}", id);

        return findRoleById(id);
    }

    /**
     * Updates a role.
     *
     * @param id      the id of the role to update
     * @param roleDto the new role data
     * @return the updated role
     * @throws RuntimeException if an error occurs while updating the role
     */
    @Override
    @Transactional
    public Role updateRole(Long id, RoleDto roleDto) {
        log.info("Update role with id: {}", id);
        Role role = findRoleById(id);
        role.setName(roleDto.getRole());

        Role updatedRole = roleRepository.save(role);
        log.debug("Updated role: {}", updatedRole);

        return updatedRole;
    }

    /**
     * Deletes a role by its id.
     *
     * @param id the id of the role to delete
     * @throws RuntimeException if an error occurs while deleting the role
     */
    @Override
    public void deleteRole(Long id) {
        log.info("Delete role with id: {}", id);
        Role role = findRoleById(id);

        roleRepository.delete(role);
        log.debug("Role with id: {} successfully deleted", id);
    }

    private Role findRoleById(Long id) {
        log.debug("Find role with id: {}", id);
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Role not found with id: {}", id);
                    return new EntityNotFoundException("Role", id);
                });
        log.debug("Role found: {}", role);

        return role;
    }
}