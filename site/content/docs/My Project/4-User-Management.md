---
title: "Chapter 4: User Management"
order: 4
---
# Chapter 3: Managing Users

User Management is a crucial aspect of any identity and access management system.  This chapter will guide you through how to manage users within a Keycloak realm using the provided tools.  This involves creating, retrieving, updating, and deleting users, as well as managing their group memberships and roles.  Understanding these operations is essential for controlling access to your applications and resources. This chapter builds upon the concepts introduced in the *Realm Management* chapter, as all user operations take place within a specific realm.

## Key Concepts

* **User Representation:**  A JSON object representing a user in Keycloak. It contains information like username, first name, last name, email, enabled status, and credentials.
* **Credentials:**  Information used to authenticate a user, such as a password.
* **Groups:**  Collections of users, often used for assigning roles and permissions collectively. See the *Group Management* chapter for more detail.
* **Roles:** Define permissions within a realm or client. See the *Role Management* chapter for more detail.

## Working with Users

The `UserService` class provides the core functionality for interacting with users in a Keycloak realm.  The `UserTool` class then exposes this functionality as command-line tools. Let's explore some common operations:

### Getting All Users

You can retrieve all users within a realm using the `getUsers` method:

```java
// From UserService.java
public List<UserRepresentation> getUsers(String realm) {
    return keycloak.realm(realm).users().list();
}
```

This method, exposed via the `UserTool`, returns a list of `UserRepresentation` objects.  Each `UserRepresentation` contains details about a single user.

```bash
# Using the mcp tool
./mcp user getUsers <realm-name>
```

### Creating a New User

Adding a new user involves creating a `UserRepresentation` object and populating it with the user's details.  The `addUser` method handles this:

```java
// From UserService.java
public String addUser(String realm, String username, String firstName, String lastName, String email, String password) {
    // ... (Code to create and populate UserRepresentation and CredentialRepresentation objects)
    Response response = keycloak.realm(realm).users().create(user);
    // ... (Code to handle the response)
}
```

This is also exposed via the `UserTool`:

```bash
./mcp user addUser <realm-name> <username> <first-name> <last-name> <email> <password>
```

Remember to replace placeholders like `<realm-name>` with actual values.

### Getting a User

You can retrieve a specific user either by their username or ID:

```java
// From UserService.java
public UserRepresentation getUserByUsername(String realm, String username) { /* ... */ }
public UserRepresentation getUserById(String realm, String userId) { /* ... */ }
```

And through the `UserTool`:

```bash
./mcp user getUserByUsername <realm-name> <username>
```


### Updating a User

Modifying an existing user involves retrieving the `UserRepresentation`, making the necessary changes, and then updating it in Keycloak:

```java
// From UserService.java
public String updateUser(String realm, String userId, UserRepresentation userRepresentation) { /* ... */ }
```

While the provided code doesn't expose `updateUser` directly through `UserTool`, it's a crucial function for modifying user details.  Future tutorial chapters might cover creating such a tool.

### Deleting a User

Removing a user is straightforward:

```java
// From UserService.java
public String deleteUser(String realm, String username) { /* ... */ }
```

Though not directly exposed by `UserTool`, you could extend the `UserTool` class with a new `deleteUser` method to achieve this through the command-line.

### Managing Groups and Roles

`UserService` also provides methods for managing user's group memberships and assigned roles, connecting the concepts of *User Management* with *Group Management* and *Role Management*. These functionalities are available through the `UserTool` examples like `addUserToGroup`, `getUserRoles`, and `addRoleToUser`. Refer to their respective chapters for more in-depth information.


This chapter covered essential user management operations.  By combining these techniques, you can effectively manage users within your Keycloak realms and control access to your protected resources.  The next chapters will delve deeper into more advanced Keycloak features, building upon the foundation established here.  Keep experimenting and exploring!
