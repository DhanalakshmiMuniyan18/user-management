package com.userservice.management.rbac.security;

import com.userservice.management.rbac.model.Permission;
import com.userservice.management.rbac.model.Role;
import com.userservice.management.rbac.repository.RoleRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Saravanamuthukumar S
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class RBACSecurityAspectTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private RBACSecurityAspect rbacSecurityAspect;

    private RequirePermission requirePermission;
    private final String testUsername = "testUser";
    private final Long roleId = 1L;

    @BeforeEach
    public void setUp() {
        // Setup security context
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        
        // Default authentication setup with lenient settings
        lenient().when(authentication.isAuthenticated()).thenReturn(true);
        lenient().when(authentication.getName()).thenReturn(testUsername);
        
        // Create a mock RequirePermission annotation with lenient settings
        requirePermission = mock(RequirePermission.class, withSettings().lenient());
        lenient().when(requirePermission.value()).thenReturn(new String[]{"TEST_PERMISSION"});
        lenient().when(requirePermission.allRequired()).thenReturn(true);
    }

    @SuppressWarnings("unchecked")
    @Test
    void checkPermission_WithRequiredPermission_Success() throws Throwable {
        // Setup role with required permission
        Role role = new Role();
        role.setId(roleId);
        role.setName("TEST_ROLE");
        role.setPermissions(new HashSet<>());
        
        Permission permission = new Permission();
        permission.setId(1L);
        permission.setName("TEST_PERMISSION");
        permission.setRoles(new HashSet<>());
        
        role.getPermissions().add(permission);
        permission.getRoles().add(role);

        // Setup authentication with role
        Collection<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_" + roleId));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(roleRepository.findByIdInWithPermissions(Collections.singleton(roleId)))
                .thenReturn(Collections.singleton(role));

        // Setup joinPoint to return some value
        Object expectedResult = "test result";
        when(joinPoint.proceed()).thenReturn(expectedResult);

        // Execute and verify
        Object result = rbacSecurityAspect.checkPermission(joinPoint, requirePermission);
        
        assertEquals(expectedResult, result);
        verify(roleRepository).findByIdInWithPermissions(Collections.singleton(roleId));
        verify(joinPoint).proceed();
    }

    @SuppressWarnings("unchecked")
    @Test
    void checkPermission_WithoutRequiredPermission_ThrowsException() throws Throwable {
        // Setup role without required permission
        Role role = new Role();
        role.setId(roleId);
        role.setName("TEST_ROLE");
        role.setPermissions(new HashSet<>());
        
        Permission permission = new Permission();
        permission.setId(1L);
        permission.setName("OTHER_PERMISSION");
        permission.setRoles(new HashSet<>());
        
        role.getPermissions().add(permission);
        permission.getRoles().add(role);

        // Setup authentication with role
        Collection<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_" + roleId));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(roleRepository.findByIdInWithPermissions(Collections.singleton(roleId)))
                .thenReturn(Collections.singleton(role));

        // Execute and verify
        SecurityException exception = assertThrows(SecurityException.class,
                () -> rbacSecurityAspect.checkPermission(joinPoint, requirePermission));
        
        assertEquals("User " + testUsername + " does not have the required permissions: TEST_PERMISSION", exception.getMessage());
        verify(joinPoint, never()).proceed();
    }

    @SuppressWarnings("unchecked")
    @Test
    void checkPermission_WithMultiplePermissions_AllRequired_Success() throws Throwable {
        // Create a mock RequirePermission annotation with multiple permissions
        RequirePermission multiPermission = mock(RequirePermission.class, withSettings().lenient());
        when(multiPermission.value()).thenReturn(new String[]{"PERMISSION_1", "PERMISSION_2"});
        when(multiPermission.allRequired()).thenReturn(true);

        // Setup role with all required permissions
        Role role = new Role();
        role.setId(roleId);
        role.setName("TEST_ROLE");
        role.setPermissions(new HashSet<>());
        
        Permission permission1 = new Permission();
        permission1.setId(1L);
        permission1.setName("PERMISSION_1");
        permission1.setRoles(new HashSet<>());
        
        Permission permission2 = new Permission();
        permission2.setId(2L);
        permission2.setName("PERMISSION_2");
        permission2.setRoles(new HashSet<>());
        
        role.getPermissions().add(permission1);
        role.getPermissions().add(permission2);
        permission1.getRoles().add(role);
        permission2.getRoles().add(role);

        // Setup authentication with role
        Collection<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_" + roleId));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(roleRepository.findByIdInWithPermissions(Collections.singleton(roleId)))
                .thenReturn(Collections.singleton(role));

        Object expectedResult = "test result";
        when(joinPoint.proceed()).thenReturn(expectedResult);

        // Execute and verify
        Object result = rbacSecurityAspect.checkPermission(joinPoint, multiPermission);
        
        assertEquals(expectedResult, result);
        verify(joinPoint).proceed();
    }

    @SuppressWarnings("unchecked")
    @Test
    void checkPermission_WithMultiplePermissions_AnyRequired_Success() throws Throwable {
        // Create a mock RequirePermission annotation with multiple permissions (any required)
        RequirePermission multiPermission = mock(RequirePermission.class, withSettings().lenient());
        when(multiPermission.value()).thenReturn(new String[]{"PERMISSION_1", "PERMISSION_2"});
        when(multiPermission.allRequired()).thenReturn(false);

        // Setup role with one of the required permissions
        Role role = new Role();
        role.setId(roleId);
        role.setName("TEST_ROLE");
        role.setPermissions(new HashSet<>());
        
        Permission permission = new Permission();
        permission.setId(1L);
        permission.setName("PERMISSION_1"); // This matches one required permission
        permission.setRoles(new HashSet<>());
        
        role.getPermissions().add(permission);
        permission.getRoles().add(role);

        // Setup authentication with role
        Collection<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_" + roleId));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(roleRepository.findByIdInWithPermissions(Collections.singleton(roleId)))
                .thenReturn(Collections.singleton(role));

        Object expectedResult = "test result";
        when(joinPoint.proceed()).thenReturn(expectedResult);

        // Execute and verify
        Object result = rbacSecurityAspect.checkPermission(joinPoint, multiPermission);
        assertEquals(expectedResult, result);
        verify(joinPoint).proceed();
    }

    @Test
    void checkPermission_NotAuthenticated_ThrowsException() throws Throwable {
        // Override default authentication setup
        when(authentication.isAuthenticated()).thenReturn(false);

        // Execute and verify
        SecurityException exception = assertThrows(SecurityException.class,
                () -> rbacSecurityAspect.checkPermission(joinPoint, requirePermission));
        
        assertEquals("User not authenticated", exception.getMessage());
        verify(joinPoint, never()).proceed();
    }

    @Test
    void checkPermission_InvalidRoleIdFormat_ThrowsException() throws Throwable {
        // Setup authentication with invalid role ID format
        Collection<SimpleGrantedAuthority> authorities = Collections.singleton(
            new SimpleGrantedAuthority("ROLE_INVALID"));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        // Execute and verify
        SecurityException exception = assertThrows(SecurityException.class,
                () -> rbacSecurityAspect.checkPermission(joinPoint, requirePermission));
        
        assertEquals("No valid roles found", exception.getMessage());
        verify(joinPoint, never()).proceed();
    }

    @Test
    void checkPermission_EmptyPermissionsArray_Success() throws Throwable {
        // Create a mock RequirePermission annotation with empty permissions array
        RequirePermission emptyPermission = mock(RequirePermission.class, withSettings().lenient());
        when(emptyPermission.value()).thenReturn(new String[]{});
        when(emptyPermission.allRequired()).thenReturn(true);

        // Setup role with any permission
        Role role = new Role();
        role.setId(roleId);
        role.setName("TEST_ROLE");
        role.setPermissions(new HashSet<>());
        
        Permission permission = new Permission();
        permission.setId(1L);
        permission.setName("ANY_PERMISSION");
        permission.setRoles(new HashSet<>());
        
        role.getPermissions().add(permission);
        permission.getRoles().add(role);

        // Setup authentication
        Collection<SimpleGrantedAuthority> authorities = Collections.singleton(
            new SimpleGrantedAuthority("ROLE_" + roleId));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(roleRepository.findByIdInWithPermissions(Collections.singleton(roleId)))
                .thenReturn(Collections.singleton(role));

        Object expectedResult = "test result";
        when(joinPoint.proceed()).thenReturn(expectedResult);

        // Execute and verify
        Object result = rbacSecurityAspect.checkPermission(joinPoint, emptyPermission);
        assertEquals(expectedResult, result);
        verify(joinPoint).proceed();
    }

    @Test
    void checkPermission_RoleWithNoPermissions_ThrowsException() throws Throwable {
        // Setup role without any permissions
        Role role = new Role();
        role.setId(roleId);
        role.setName("TEST_ROLE");
        role.setPermissions(new HashSet<>());

        // Setup authentication
        Collection<SimpleGrantedAuthority> authorities = Collections.singleton(
            new SimpleGrantedAuthority("ROLE_" + roleId));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(roleRepository.findByIdInWithPermissions(Collections.singleton(roleId)))
                .thenReturn(Collections.singleton(role));

        // Execute and verify
        SecurityException exception = assertThrows(SecurityException.class,
                () -> rbacSecurityAspect.checkPermission(joinPoint, requirePermission));
        
        assertEquals("User " + testUsername + " does not have the required permissions: TEST_PERMISSION",
                exception.getMessage());
        verify(joinPoint, never()).proceed();
    }

    @Test
    void checkPermission_NonExistentRoleId_ThrowsException() throws Throwable {
        // Setup authentication with non-existent role ID
        Collection<SimpleGrantedAuthority> authorities = Collections.singleton(
            new SimpleGrantedAuthority("ROLE_999"));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(roleRepository.findByIdInWithPermissions(Collections.singleton(999L)))
                .thenReturn(Collections.emptySet());

        // Execute and verify
        SecurityException exception = assertThrows(SecurityException.class,
                () -> rbacSecurityAspect.checkPermission(joinPoint, requirePermission));
        
        assertEquals("No roles found for user " + testUsername, exception.getMessage());
        verify(joinPoint, never()).proceed();
    }

    @Test
    void checkPermission_MultipleRolesWithConflictingPermissions_Success() throws Throwable {
        // Setup roles with conflicting permissions
        Role role1 = new Role();
        role1.setId(1L);
        role1.setName("ROLE_1");
        role1.setPermissions(new HashSet<>());
        
        Permission permission1 = new Permission();
        permission1.setId(1L);
        permission1.setName("TEST_PERMISSION");
        permission1.setRoles(new HashSet<>());
        role1.getPermissions().add(permission1);
        permission1.getRoles().add(role1);

        Role role2 = new Role();
        role2.setId(2L);
        role2.setName("ROLE_2");
        role2.setPermissions(new HashSet<>());
        
        Permission permission2 = new Permission();
        permission2.setId(2L);
        permission2.setName("OTHER_PERMISSION");
        permission2.setRoles(new HashSet<>());
        role2.getPermissions().add(permission2);
        permission2.getRoles().add(role2);

        // Setup authentication with multiple roles
        Collection<SimpleGrantedAuthority> authorities = Arrays.asList(
            new SimpleGrantedAuthority("ROLE_1"),
            new SimpleGrantedAuthority("ROLE_2")
        );
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(roleRepository.findByIdInWithPermissions(Set.of(1L, 2L)))
                .thenReturn(Set.of(role1, role2));

        Object expectedResult = "test result";
        when(joinPoint.proceed()).thenReturn(expectedResult);

        // Execute and verify
        Object result = rbacSecurityAspect.checkPermission(joinPoint, requirePermission);
        assertEquals(expectedResult, result);
        verify(joinPoint).proceed();
    }

    @Test
    void checkPermission_CaseSensitivePermissions_ThrowsException() throws Throwable {
        // Setup role with permission in different case
        Role role = new Role();
        role.setId(roleId);
        role.setName("TEST_ROLE");
        role.setPermissions(new HashSet<>());
        
        Permission permission = new Permission();
        permission.setId(1L);
        permission.setName("test_permission"); // lowercase version
        permission.setRoles(new HashSet<>());
        
        role.getPermissions().add(permission);
        permission.getRoles().add(role);

        // Setup authentication
        Collection<SimpleGrantedAuthority> authorities = Collections.singleton(
            new SimpleGrantedAuthority("ROLE_" + roleId));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(roleRepository.findByIdInWithPermissions(Collections.singleton(roleId)))
                .thenReturn(Collections.singleton(role));

        // Execute and verify
        SecurityException exception = assertThrows(SecurityException.class,
                () -> rbacSecurityAspect.checkPermission(joinPoint, requirePermission));
        
        assertEquals("User " + testUsername + " does not have the required permissions: TEST_PERMISSION",
                exception.getMessage());
        verify(joinPoint, never()).proceed();
    }

    @Test
    void checkPermission_SpecialCharactersInPermissionName_Success() throws Throwable {
        // Setup RequirePermission with special characters
        RequirePermission specialPermission = mock(RequirePermission.class, withSettings().lenient());
        when(specialPermission.value()).thenReturn(new String[]{"TEST@PERMISSION#123"});
        when(specialPermission.allRequired()).thenReturn(true);

        // Setup role with matching special character permission
        Role role = new Role();
        role.setId(roleId);
        role.setName("TEST_ROLE");
        role.setPermissions(new HashSet<>());
        
        Permission permission = new Permission();
        permission.setId(1L);
        permission.setName("TEST@PERMISSION#123");
        permission.setRoles(new HashSet<>());
        
        role.getPermissions().add(permission);
        permission.getRoles().add(role);

        // Setup authentication
        Collection<SimpleGrantedAuthority> authorities = Collections.singleton(
            new SimpleGrantedAuthority("ROLE_" + roleId));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(roleRepository.findByIdInWithPermissions(Collections.singleton(roleId)))
                .thenReturn(Collections.singleton(role));

        Object expectedResult = "test result";
        when(joinPoint.proceed()).thenReturn(expectedResult);

        // Execute and verify
        Object result = rbacSecurityAspect.checkPermission(joinPoint, specialPermission);
        assertEquals(expectedResult, result);
        verify(joinPoint).proceed();
    }

    @Test
    void checkPermission_ExceptionDuringMethodExecution_PropagatesException() throws Throwable {
        // Setup role with required permission
        Role role = new Role();
        role.setId(roleId);
        role.setName("TEST_ROLE");
        role.setPermissions(new HashSet<>());
        
        Permission permission = new Permission();
        permission.setId(1L);
        permission.setName("TEST_PERMISSION");
        permission.setRoles(new HashSet<>());
        
        role.getPermissions().add(permission);
        permission.getRoles().add(role);

        // Setup authentication
        Collection<SimpleGrantedAuthority> authorities = Collections.singleton(
            new SimpleGrantedAuthority("ROLE_" + roleId));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(roleRepository.findByIdInWithPermissions(Collections.singleton(roleId)))
                .thenReturn(Collections.singleton(role));

        // Setup joinPoint to throw exception
        RuntimeException expectedError = new RuntimeException("Method execution failed");
        when(joinPoint.proceed()).thenThrow(expectedError);

        // Execute and verify
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> rbacSecurityAspect.checkPermission(joinPoint, requirePermission));
        
        assertEquals(expectedError, exception);
        verify(joinPoint).proceed();
    }

    @Test
    void checkPermission_NullAuthentication_ThrowsException() throws Throwable {
        // Override security context to return null authentication
        when(securityContext.getAuthentication()).thenReturn(null);

        // Execute and verify
        SecurityException exception = assertThrows(SecurityException.class,
                () -> rbacSecurityAspect.checkPermission(joinPoint, requirePermission));
        
        assertEquals("User not authenticated", exception.getMessage());
        verify(joinPoint, never()).proceed();
    }

    @Test
    void checkPermission_AnonymousAuthentication_ThrowsException() throws Throwable {
        // Setup anonymous authentication
        when(authentication.getName()).thenReturn("anonymousUser");
        Collection<SimpleGrantedAuthority> authorities = Collections.singleton(
            new SimpleGrantedAuthority("ROLE_ANONYMOUS"));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        // Use a local mock for RequirePermission with lenient stubbing
        RequirePermission localRequirePermission = mock(RequirePermission.class, withSettings().lenient());
        lenient().when(localRequirePermission.value()).thenReturn(new String[]{"ANY_PERMISSION"});
        lenient().when(localRequirePermission.allRequired()).thenReturn(true);

        // Execute and verify
        SecurityException exception = assertThrows(SecurityException.class,
                () -> rbacSecurityAspect.checkPermission(joinPoint, localRequirePermission));
        
        assertEquals("Anonymous users are not allowed to access secured methods", exception.getMessage());
        verify(joinPoint, never()).proceed();
    }

    @Test
    void checkPermission_AuthorityWithInvalidPrefix_ThrowsException() throws Throwable {
        // Authority does not start with ROLE_
        Collection<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("USER_1"));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        SecurityException exception = assertThrows(SecurityException.class,
                () -> rbacSecurityAspect.checkPermission(joinPoint, requirePermission));
        assertEquals("No valid roles found", exception.getMessage());
        verify(joinPoint, never()).proceed();
    }

    @Test
    void checkPermission_AuthorityWithNonNumericRoleId_ThrowsException() throws Throwable {
        // Authority is ROLE_abc
        Collection<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_abc"));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        SecurityException exception = assertThrows(SecurityException.class,
                () -> rbacSecurityAspect.checkPermission(joinPoint, requirePermission));
        assertEquals("No valid roles found", exception.getMessage());
        verify(joinPoint, never()).proceed();
    }

    @Test
    void checkPermission_NoValidRolesAfterFiltering_ThrowsException() throws Throwable {
        // All authorities are invalid
        Collection<SimpleGrantedAuthority> authorities = Arrays.asList(
            new SimpleGrantedAuthority("USER_1"),
            new SimpleGrantedAuthority("ROLE_abc")
        );
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        SecurityException exception = assertThrows(SecurityException.class,
                () -> rbacSecurityAspect.checkPermission(joinPoint, requirePermission));
        assertEquals("No valid roles found", exception.getMessage());
        verify(joinPoint, never()).proceed();
    }

    @Test
    void checkPermission_TooManyRoles_ThrowsException() throws Throwable {
        // User has more than MAX_ROLES_PER_USER authorities
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (int i = 0; i < 101; i++) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + i));
        }
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        Set<Long> roleIds = new HashSet<>();
        for (int i = 0; i < 101; i++) {
            roleIds.add((long) i);
        }
        when(roleRepository.findByIdInWithPermissions(roleIds)).thenReturn(Collections.emptySet());
        SecurityException exception = assertThrows(SecurityException.class,
                () -> rbacSecurityAspect.checkPermission(joinPoint, requirePermission));
        assertTrue(exception.getMessage().contains("exceeds maximum allowed roles"));
        verify(joinPoint, never()).proceed();
    }

    @Test
    void checkPermission_RoleWithNullPermissions_ThrowsException() throws Throwable {
        // Role with null permissions
        Role role = new Role();
        role.setId(roleId);
        role.setName("TEST_ROLE");
        role.setPermissions(null);
        Collection<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_" + roleId));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(roleRepository.findByIdInWithPermissions(Collections.singleton(roleId)))
                .thenReturn(Collections.singleton(role));
        SecurityException exception = assertThrows(SecurityException.class,
                () -> rbacSecurityAspect.checkPermission(joinPoint, requirePermission));
        assertEquals("User " + testUsername + " does not have the required permissions: TEST_PERMISSION", exception.getMessage());
        verify(joinPoint, never()).proceed();
    }

    @Test
    void checkPermission_PermissionWithNullOrBlankName_ThrowsException() throws Throwable {
        // Permission with null name
        Role role = new Role();
        role.setId(roleId);
        role.setName("TEST_ROLE");
        role.setPermissions(new HashSet<>());
        Permission permission = new Permission();
        permission.setId(1L);
        permission.setName(null);
        permission.setRoles(new HashSet<>());
        role.getPermissions().add(permission);
        permission.getRoles().add(role);
        Collection<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_" + roleId));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(roleRepository.findByIdInWithPermissions(Collections.singleton(roleId)))
                .thenReturn(Collections.singleton(role));
        SecurityException exception = assertThrows(SecurityException.class,
                () -> rbacSecurityAspect.checkPermission(joinPoint, requirePermission));
        assertEquals("User " + testUsername + " does not have the required permissions: TEST_PERMISSION", exception.getMessage());
        verify(joinPoint, never()).proceed();

        // Permission with blank name
        permission.setName("");
        SecurityException exception2 = assertThrows(SecurityException.class,
                () -> rbacSecurityAspect.checkPermission(joinPoint, requirePermission));
        assertEquals("User " + testUsername + " does not have the required permissions: TEST_PERMISSION", exception2.getMessage());
        verify(joinPoint, never()).proceed();
    }

    @Test
    void checkPermission_RequirePermissionAnnotationIsNull_ThrowsException() throws Throwable {
        // Pass null annotation
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> rbacSecurityAspect.checkPermission(joinPoint, null));
        assertEquals("RequirePermission annotation cannot be null and must have value", exception.getMessage());
        verify(joinPoint, never()).proceed();
    }

    @Test
    void checkPermission_RequirePermissionValueIsNull_ThrowsException() throws Throwable {
        // Annotation value() returns null
        RequirePermission nullValuePermission = mock(RequirePermission.class, withSettings().lenient());
        when(nullValuePermission.value()).thenReturn(null);
        when(nullValuePermission.allRequired()).thenReturn(true);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> rbacSecurityAspect.checkPermission(joinPoint, nullValuePermission));
        assertEquals("RequirePermission annotation cannot be null and must have value", exception.getMessage());
        verify(joinPoint, never()).proceed();
    }

    @Test
    void checkPermission_RequirePermissionValueContainsNullOrBlank_ThrowsException() throws Throwable {
        // Annotation value() contains null or blank
        RequirePermission badValuePermission = mock(RequirePermission.class, withSettings().lenient());
        when(badValuePermission.value()).thenReturn(new String[]{null, " ", "PERMISSION_1"});
        when(badValuePermission.allRequired()).thenReturn(true);
        // Setup role with permission
        Role role = new Role();
        role.setId(roleId);
        role.setName("TEST_ROLE");
        role.setPermissions(new HashSet<>());
        Permission permission = new Permission();
        permission.setId(1L);
        permission.setName("PERMISSION_1");
        permission.setRoles(new HashSet<>());
        role.getPermissions().add(permission);
        permission.getRoles().add(role);
        Collection<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_" + roleId));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(roleRepository.findByIdInWithPermissions(Collections.singleton(roleId)))
                .thenReturn(Collections.singleton(role));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> rbacSecurityAspect.checkPermission(joinPoint, badValuePermission));
        assertEquals("Required permission cannot be null or empty", exception.getMessage());
        verify(joinPoint, never()).proceed();
    }
}