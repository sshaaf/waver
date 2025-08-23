---
title: "Chapter 3: Role Management"
order: 3
---
## Managing Roles in Keycloak

Roles are fundamental to access control in Keycloak. They allow you to group permissions and assign them to users, groups, or even service accounts.  This chapter will guide you through managing roles using the provided `RoleService` and `RoleTool` classes.  This ties into other key concepts like User Management, Group Management, and Client Management, as roles are often assigned to these entities.

### Understanding the `RoleService`

The `RoleService` class provides the core functionality for interacting with Keycloak's role management API.  It uses the `Keycloak` admin client for all operations. Let's explore some of the key methods:

* **`getRealmRoles(String realm)`**: Retrieves all roles within a specified realm.  This is useful for getting a comprehensive view of all available roles.

```java
List<RoleRepresentation> roles = roleService.getRealmRoles("myrealm");
```

* **`getRealmRole(String realm, String roleName)`**: Retrieves a specific role by its name within a given realm.

```java
RoleRepresentation role = roleService.getRealmRole("myrealm", "admin");
```

* **`createRealmRole(String realm, String roleName, String description)`**: Creates a new realm role.  You provide the realm, the desired role name, and an optional description.

```java
String result = roleService.createRealmRole("myrealm", "user", "Standard user role");
```

* **`updateRealmRole(String realm, String roleName, RoleRepresentation roleRepresentation)`**: Updates an existing realm role.  The `RoleRepresentation` object allows you to modify various attributes of the role.

```java
RoleRepresentation role = roleService.getRealmRole("myrealm", "user");
role.setDescription("Updated user role description");
String result = roleService.updateRealmRole("myrealm", "user", role);
```

* **`deleteRealmRole(String realm, String roleName)`**: Deletes a realm role.

```java
String result = roleService.deleteRealmRole("myrealm", "user");
```

The `RoleService` also provides methods for managing **composite roles**.  A composite role combines multiple roles into a single unit.  This simplifies permission management by allowing you to assign a set of roles at once.

* **`getRoleComposites(String realm, String roleName)`**: Retrieves a list of roles that make up a composite role.

```java
List<RoleRepresentation> composites = roleService.getRoleComposites("myrealm", "admin");
```

* **`addCompositeToRole(String realm, String roleName, String compositeRoleName)`**: Adds a role to the specified composite role.

```java
String result = roleService.addCompositeToRole("myrealm", "admin", "view-reports");
```

* **`removeCompositeFromRole(String realm, String roleName, String compositeRoleName)`**: Removes a role from the specified composite role.

```java
String result = roleService.removeCompositeFromRole("myrealm", "admin", "edit-reports");
```

### Using the `RoleTool`

The `RoleTool` class exposes the `RoleService` functionality through a command-line interface.  This is particularly helpful for scripting and automation.  Currently, the `RoleTool` offers the following commands:

* **`getRealmRoles`**: Retrieves all realm roles.

```bash
./mcp roles getRealmRoles myrealm
```

* **`getRealmRole`**: Retrieves a specific realm role.

```bash
./mcp roles getRealmRole myrealm admin
```


This chapter provides a foundational understanding of Role Management within your Keycloak environment. By leveraging the `RoleService` and `RoleTool`, you can effectively manage roles and permissions, contributing to a secure and well-organized system.  Remember to explore the provided code examples and adapt them to your specific needs.  Next, you might want to delve deeper into User Management or Group Management to see how roles are applied in practice.
