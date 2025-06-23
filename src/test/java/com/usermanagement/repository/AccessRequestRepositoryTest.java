package com.usermanagement.repository;

import com.usermanagement.model.entity.AccessRequest;
import com.usermanagement.model.entity.AccessRequest.Status;
import com.usermanagement.model.entity.Role;
import com.usermanagement.model.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Saravanamuthukumar S
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class AccessRequestRepositoryTest {

    @Autowired
    private AccessRequestRepository accessRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User requestingUser;
    private User reviewingUser;
    private Role requestedRole;

    @BeforeEach
    void setUp() {
        requestingUser = new User();
        requestingUser.setName("Requesting User");
        requestingUser.setEmail("requester@example.com");
        requestingUser.setPassword("password");
        userRepository.save(requestingUser);

        reviewingUser = new User();
        reviewingUser.setName("Reviewing User");
        reviewingUser.setEmail("reviewer@example.com");
        reviewingUser.setPassword("password");
        userRepository.save(reviewingUser);

        requestedRole = new Role();
        requestedRole.setName("ROLE_ADMIN");
        requestedRole.setDescription("Administrator role");
        roleRepository.save(requestedRole);
    }

    @Test
    void shouldSaveAccessRequest() {
        // Given
        AccessRequest request = new AccessRequest();
        request.setUser(requestingUser);
        request.setRole(requestedRole);
        request.setStatus(Status.PENDING);

        // When
        AccessRequest savedRequest = accessRequestRepository.save(request);

        // Then
        assertThat(savedRequest.getId()).isNotNull();
        assertThat(savedRequest.getUser()).isEqualTo(requestingUser);
        assertThat(savedRequest.getRole()).isEqualTo(requestedRole);
        assertThat(savedRequest.getStatus()).isEqualTo(Status.PENDING);
    }

    @Test
    void shouldFindByUserId() {
        // Given
        AccessRequest request1 = new AccessRequest();
        request1.setUser(requestingUser);
        request1.setRole(requestedRole);
        request1.setStatus(Status.PENDING);
        accessRequestRepository.save(request1);

        AccessRequest request2 = new AccessRequest();
        request2.setUser(requestingUser);
        request2.setRole(requestedRole);
        request2.setStatus(Status.PENDING);
        accessRequestRepository.save(request2);

        // When
        Page<AccessRequest> requests = accessRequestRepository.findByUserId(
            requestingUser.getId(),
            PageRequest.of(0, 10)
        );

        // Then
        assertThat(requests.getContent()).hasSize(2);
        assertThat(requests.getContent()).extracting("user")
            .containsOnly(requestingUser);
    }

    @Test
    void shouldFindByStatus() {
        // Given
        AccessRequest pendingRequest = new AccessRequest();
        pendingRequest.setUser(requestingUser);
        pendingRequest.setRole(requestedRole);
        pendingRequest.setStatus(Status.PENDING);
        accessRequestRepository.save(pendingRequest);

        AccessRequest approvedRequest = new AccessRequest();
        approvedRequest.setUser(requestingUser);
        approvedRequest.setRole(requestedRole);
        approvedRequest.setStatus(Status.APPROVED);
        accessRequestRepository.save(approvedRequest);

        // When
        Page<AccessRequest> pendingRequests = accessRequestRepository.findByStatus(
            Status.PENDING,
            PageRequest.of(0, 10)
        );
        Page<AccessRequest> approvedRequests = accessRequestRepository.findByStatus(
            Status.APPROVED,
            PageRequest.of(0, 10)
        );

        // Then
        assertThat(pendingRequests.getContent()).hasSize(1);
        assertThat(approvedRequests.getContent()).hasSize(1);
        assertThat(pendingRequests.getContent().get(0).getStatus())
            .isEqualTo(Status.PENDING);
        assertThat(approvedRequests.getContent().get(0).getStatus())
            .isEqualTo(Status.APPROVED);
    }

    @Test
    void shouldFindBySearchCriteria() {
        // Given
        AccessRequest request1 = new AccessRequest();
        request1.setUser(requestingUser);
        request1.setRole(requestedRole);
        request1.setStatus(Status.PENDING);
        accessRequestRepository.save(request1);

        AccessRequest request2 = new AccessRequest();
        request2.setUser(requestingUser);
        request2.setRole(requestedRole);
        request2.setStatus(Status.APPROVED);
        accessRequestRepository.save(request2);

        // When
        Page<AccessRequest> filteredRequests = accessRequestRepository.findBySearchCriteria(
            requestingUser.getId(),
            Status.PENDING,
            PageRequest.of(0, 10)
        );

        // Then
        assertThat(filteredRequests.getContent()).hasSize(1);
        assertThat(filteredRequests.getContent().get(0).getStatus())
            .isEqualTo(Status.PENDING);
    }

    @Test
    void shouldCheckExistingRequest() {
        // Given
        AccessRequest request = new AccessRequest();
        request.setUser(requestingUser);
        request.setRole(requestedRole);
        request.setStatus(Status.PENDING);
        accessRequestRepository.save(request);

        // When & Then
        assertThat(accessRequestRepository.existsByUserIdAndRoleIdAndStatus(
            requestingUser.getId(),
            requestedRole.getId(),
            Status.PENDING
        )).isTrue();

        assertThat(accessRequestRepository.existsByUserIdAndRoleIdAndStatus(
            requestingUser.getId(),
            requestedRole.getId(),
            Status.APPROVED
        )).isFalse();
    }
} 