---
title: "Chapter 1: Realm Management"
order: 1
---
## Managing Realms in Keycloak

Realms are fundamental to Keycloak. They act as containers for users, clients, and other resources.  Think of them as separate security domains within your application.  Each realm is isolated from others, allowing you to manage different sets of users and access policies independently.  Understanding Realm Management is crucial for setting up and configuring your Keycloak server. This chapter will guide you through the basics of creating, retrieving, updating, and deleting realms.

This tutorial uses a `RealmService` class and a `RealmTool` class to interact with Keycloak's realm API.  `RealmService` encapsulates the core logic for managing realms, while `RealmTool` exposes these functionalities as command-line tools.

### Retrieving Realms

The `RealmService` class provides two methods for retrieving realm information: `getRealms()` and `getRealm(String realmName)`.

*   **`getRealms()`**: This method retrieves all available realms within your Keycloak server.  It returns a list of `RealmRepresentation` objects, each containing details about a specific realm.

```java
// From RealmService.java
public List<RealmRepresentation> getRealms() {
    return keycloak.realms().findAll();
}
```

*   **`getRealm(String realmName)`**:  This method retrieves a specific realm based on its name.  It returns a single `RealmRepresentation` object if the realm exists or `null` if not found.

```java
// From RealmService.java
public RealmRepresentation getRealm(String realmName) {
    try {
        return keycloak.realm(realmName).toRepresentation();
    } catch (NotFoundException e) {
        Log.error("Realm not found: " + realmName, e);
        return null;
    }
}
```

These methods are then used by the `RealmTool` to provide command-line access to realm information. For example, the `getRealms` tool utilizes the `getRealms()` method:

```java
// From RealmTool.java
@Tool(description = "Get all realms from keycloak")
String getRealms() {
    try {
        return mapper.writeValueAsString(realmsService.getRealms());
    } catch (Exception e) {
        throw new ToolCallException("Failed to get realms from keycloak");
    }
}
```


### Creating a Realm

The `createRealm` method allows you to create a new realm.  It takes the realm name, display name, and enabled status as parameters.

```java
// From RealmService.java
public String createRealm(String realmName, String displayName, boolean enabled) {
    RealmRepresentation realm = new RealmRepresentation();
    realm.setRealm(realmName);
    realm.setDisplayName(displayName);
    realm.setEnabled(enabled);

    try {
        keycloak.realms().create(realm);
        return "Successfully created realm: " + realmName;
    } catch (Exception e) {
        Log.error("Exception creating realm: " + realmName, e);
        return "Error creating realm: " + realmName + " - " + e.getMessage();
    }
}
```

This method is exposed through the `createRealm` tool in `RealmTool`:

```java
// From RealmTool.java
@Tool(description = "Create a new realm")
String createRealm(@ToolArg(description = "A String denoting the name of the realm to create") String realmName,
                   @ToolArg(description = "A String denoting the display name for the realm") String displayName,
                   @ToolArg(description = "A boolean indicating whether the realm should be enabled") boolean enabled) {
    return realmsService.createRealm(realmName, displayName, enabled);
}
```

By using these methods, you can easily manage the lifecycle of your realms.  Later chapters will explore how these realms are used to manage other aspects of your Keycloak setup, like [User Management](./user-management.md), [Client Management](./client-management.md), and [Role Management](./role-management.md).  This modular design helps in keeping your security configuration organized and maintainable. Remember to check out the other chapters to learn more about these interconnected concepts and build a comprehensive understanding of Keycloak.
