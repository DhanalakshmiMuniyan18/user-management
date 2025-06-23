# RBACSecurityAspect Edge Case Documentation

This document lists all identified and tested edge cases for the `RBACSecurityAspect` class, including the expected behavior and error handling for each scenario.

---

## 1. Null Authentication
- **Description:** No authentication object in the security context.
- **Test:** `checkPermission_NullAuthentication_ThrowsException`
- **Expected:** Throws `SecurityException` with message `User not authenticated`.

## 2. Unauthenticated User
- **Description:** Authentication object exists but is not authenticated.
- **Test:** `checkPermission_NotAuthenticated_ThrowsException`
- **Expected:** Throws `SecurityException` with message `User not authenticated`.

## 3. Anonymous User
- **Description:** Authenticated as `anonymousUser`.
- **Test:** `checkPermission_AnonymousAuthentication_ThrowsException`
- **Expected:** Throws `SecurityException` with message `Anonymous users are not allowed to access secured methods`.

## 4. No Authorities
- **Description:** User has no authorities/roles.
- **Test:** (Covered by invalid/empty authorities tests)
- **Expected:** Throws `SecurityException` with message `No valid roles found`.

## 5. Authority with Invalid Prefix
- **Description:** Authority does not start with `ROLE_`.
- **Test:** `checkPermission_AuthorityWithInvalidPrefix_ThrowsException`
- **Expected:** Throws `SecurityException` with message `No valid roles found`.

## 6. Authority with Non-numeric Role ID
- **Description:** Authority is `ROLE_abc` (non-numeric).
- **Test:** `checkPermission_AuthorityWithNonNumericRoleId_ThrowsException`
- **Expected:** Throws `SecurityException` with message `No valid roles found`.

## 7. No Valid Roles After Filtering
- **Description:** All authorities are invalid, so no valid roles remain.
- **Test:** `checkPermission_NoValidRolesAfterFiltering_ThrowsException`
- **Expected:** Throws `SecurityException` with message `No valid roles found`.

## 8. Too Many Roles
- **Description:** User has more than `MAX_ROLES_PER_USER` roles.
- **Test:** `checkPermission_TooManyRoles_ThrowsException`
- **Expected:** Throws `SecurityException` with message containing `exceeds maximum allowed roles`.

## 9. No Roles Found in DB
- **Description:** No roles found for the user in the database.
- **Test:** `checkPermission_NonExistentRoleId_ThrowsException`
- **Expected:** Throws `SecurityException` with message `No roles found for user ...`.

## 10. Role with Null or Empty Permissions
- **Description:** Role exists but has `null` or empty permissions.
- **Test:** `checkPermission_RoleWithNullPermissions_ThrowsException`, `checkPermission_RoleWithNoPermissions_ThrowsException`
- **Expected:** Throws `SecurityException` with message `User ... does not have the required permissions: ...`.

## 11. Permission Name is Null or Blank
- **Description:** Permission in a role has a `null` or blank name.
- **Test:** `checkPermission_PermissionWithNullOrBlankName_ThrowsException`
- **Expected:** Throws `SecurityException` with message `User ... does not have the required permissions: ...`.

## 12. RequirePermission Annotation is Null
- **Description:** The annotation is missing or not present.
- **Test:** `checkPermission_RequirePermissionAnnotationIsNull_ThrowsException`
- **Expected:** Throws `IllegalArgumentException` with message `RequirePermission annotation cannot be null and must have value`.

## 13. RequirePermission Value is Null
- **Description:** The annotation's `value()` returns `null`.
- **Test:** `checkPermission_RequirePermissionValueIsNull_ThrowsException`
- **Expected:** Throws `IllegalArgumentException` with message `RequirePermission annotation cannot be null and must have value`.

## 14. RequirePermission Value is Empty
- **Description:** The annotation's `value()` is an empty array.
- **Test:** `checkPermission_EmptyPermissionsArray_Success`
- **Expected:** Access is granted (no permissions required).

## 15. RequirePermission Value Contains Null or Blank
- **Description:** The annotation's `value()` contains a `null` or blank string.
- **Test:** `checkPermission_RequirePermissionValueContainsNullOrBlank_ThrowsException`
- **Expected:** Throws `IllegalArgumentException` with message `Required permission cannot be null or empty`.

## 16. User Has None of the Required Permissions
- **Description:** User has roles/permissions, but not the required ones.
- **Test:** `checkPermission_WithoutRequiredPermission_ThrowsException`
- **Expected:** Throws `SecurityException` with message `User ... does not have the required permissions: ...`.

## 17. User Has Some, But Not All, Required Permissions (allRequired=true)
- **Description:** User is missing at least one required permission.
- **Test:** `checkPermission_WithMultiplePermissions_AllRequired_Success` (success if all present, fail if not)
- **Expected:** Throws `SecurityException` if not all present.

## 18. User Has At Least One Required Permission (allRequired=false)
- **Description:** User has only one of several required permissions.
- **Test:** `checkPermission_WithMultiplePermissions_AnyRequired_Success`
- **Expected:** Access is granted if any required permission is present.

## 19. Case Sensitivity
- **Description:** Permission names differ only in case.
- **Test:** `checkPermission_CaseSensitivePermissions_ThrowsException`
- **Expected:** Throws `SecurityException` if case does not match.

## 20. Special Characters in Permission Names
- **Description:** Permission names contain special characters.
- **Test:** `checkPermission_SpecialCharactersInPermissionName_Success`
- **Expected:** Access is granted if permission matches exactly.

---

**All edge cases are now tested and robustly handled in the codebase.** 