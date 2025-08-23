---
title: "Chapter 2: Client Management"
order: 2
---
## Managing Clients

Clients are essentially applications that are registered with your Keycloak realm.  They are configured to request authentication and authorization on behalf of their users. Think of them as the "audience" for whom your users are authenticating. Client Management encompasses creating, updating, deleting, and configuring these client applications. This involves managing client secrets, roles associated with the client, and protocol mappers.  Understanding Client Management is crucial for controlling access to your resources and services.  It plays a pivotal role in the overall Authentication Flow Management and integrates tightly with concepts like User Management, Group Management, and Role Management, as client roles can be assigned to users and groups within your realm.

This chapter will guide you through the essential operations for managing clients using the provided Java code examples.

### Retrieving Clients

You can retrieve all clients within a specific realm or a specific client by its ID.  This is particularly useful when you need to inspect existing clients or integrate with other management tools.

```java
// Get all clients within a realm
String getClients(@ToolArg(description = "Realm name") String realm) {
    try {
        return mapper.writeValueAsString(clientService.getClients(realm));
    } catch (Exception e) {
        // Handle exceptions appropriately
    }
}

// Get a specific client by its client ID
String getClient(@ToolArg(description = "Realm name") String realm,
                 @ToolArg(description = "Client ID") String clientId) {
    try {
        Optional<ClientRepresentation> client = clientService.findClientByClientId(realm, clientId);
        return mapper.writeValueAsString(client.orElse(null));
    } catch (Exception e) {
        // Handle exceptions appropriately
    }
}
```

These code snippets utilize the `ClientService` class to interact with the Keycloak API. The `getClients` method retrieves all clients within a realm, while `getClient` retrieves a specific client using its unique `clientId`. The results are returned as JSON strings for easy processing.

### Creating a New Client

Creating a new client involves defining its core attributes, such as the client ID, name, and protocol.  The code example below demonstrates how to create a new client:

```java
String addClient(String realm, String clientId, String redirectUris) {
    return clientService.createClient(realm, clientId, redirectUris);
}

//Within ClientService.java
public String createClient(String realm, String clientName, String redirectUri) {
    // ... (Client creation logic) ...
        clientRepresentation.setClientId(clientName);
        clientRepresentation.setName(clientName);
        clientRepresentation.setProtocol("openid-connect");
        //Other important configurations
        clientRepresentation.setRedirectUris(Collections.singletonList(redirectUri+"/*"));
    // ... (Client creation logic) ...
}
```

This `createClient` method within the `ClientService` class sets essential properties of the `ClientRepresentation` object. The `redirectUris` parameter is especially important for handling the flow of users after authentication.  This parameter should match with the redirect URIs configured in your client application.

### Deleting a Client

Deleting a client removes it from the realm.  This action should be performed with caution as it revokes access for all users associated with the client.

```java
String deleteClient(String realm, String clientId) {
    return clientService.deleteClient(realm, clientId);
}
```

This `deleteClient` method permanently removes the specified client from the given realm.

### Managing Client Secrets

Client secrets are used for confidential clients to authenticate with Keycloak.  You can generate new secrets as needed.

```java
// Generate a new client secret
String generateNewClientSecret(String realm, String clientId) {
    return clientService.generateNewClientSecret(realm, clientId);
}
```

The `generateNewClientSecret` method demonstrates how to create a new secret for a client.  This is a crucial operation for maintaining security.

### Client Roles

Clients can have their own roles, distinct from realm roles.  These client roles allow fine-grained control over permissions within the context of a specific client application. The following snippets demonstrate managing client roles.

```java
// Get client roles
String getClientRoles(String realm, String clientId) {
    // ... (Code to get client roles) ...
}

// Create a client role
String createClientRole(String realm, String clientId, String roleName, String description) {
    // ... (Code to create client role) ...
}

// Delete a client role
String deleteClientRole(String realm, String clientId, String roleName) {
    // ... (Code to delete client role) ...
}
```

These methods showcase how to retrieve, create, and delete client roles.  This granular control is essential for managing permissions within your applications.


This chapter provides a foundational understanding of Client Management in Keycloak.  By mastering these concepts, you'll be well-equipped to control access to your applications and resources effectively. Remember that Client Management is closely linked with other key aspects like Role Management and Authentication Flow Management. In the next chapters, we will delve into these related concepts to provide a comprehensive understanding of Keycloak's capabilities.
