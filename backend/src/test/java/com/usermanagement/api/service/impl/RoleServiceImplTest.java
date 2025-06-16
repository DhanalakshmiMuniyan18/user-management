package com.usermanagement.api.service.impl;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import com.usermanagement.api.repository.RoleRepository;
import com.usermanagement.api.repository.PermissionRepository;
import static org.junit.jupiter.api.Assertions.*;

public class RoleServiceImplTest {
    @Test
    void contextLoads() {
        RoleRepository roleRepository = Mockito.mock(RoleRepository.class);
        PermissionRepository permissionRepository = Mockito.mock(PermissionRepository.class);
        RoleServiceImpl service = new RoleServiceImpl(roleRepository, permissionRepository);
        assertNotNull(service);
    }
}
