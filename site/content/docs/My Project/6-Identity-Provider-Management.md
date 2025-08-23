---
title: "Chapter 6: Identity Provider Management"
order: 6
---
## Managing Identity Providers

Identity Provider (IdP) management is crucial for controlling how users access your application.  It allows you to integrate with various authentication services like Google, Facebook, or GitHub, simplifying the login process for your users.  This chapter will guide you through managing IdPs within a specific realm using the provided tools and services.

This ties into other key concepts like **Realm Management**, **User Management**, and **Authentication Flow Management**.  Understanding how IdPs fit into the larger picture of authentication and authorization is essential for building secure and user-friendly applications.

The `IdentityProviderService` class provides core functionality for interacting with IdPs:

```java
// /Users/sshaaf/git/java/waver/.waver-git-clone/sshaaf/keycloak-mcp-server/src/main/java/dev/shaaf/keycloak/mcp/server/idp/IdentityProviderService.java
public IdentityProviderRepresentation getIdentityProvider(String realm, String alias) {
    try {
        return keycloak.realm(realm).identityProviders().get(alias).toRepresentation();
    } catch (NotFoundException e) {
        Log.error("Identity provider not found: " + alias, e);
        return null;
    } // ... further error handling
}

public String createIdentityProvider(String realm, IdentityProviderRepresentation identityProvider) {
    try {
        Response response = keycloak.realm(realm).identityProviders().create(identityProvider);
        // ... check response status and handle accordingly
    } // ... further error handling
}
```

These snippets demonstrate how to retrieve and create an IdP. `getIdentityProvider` retrieves an IdP representation by its `alias` within a given `realm`.  `createIdentityProvider` creates a new IdP based on the provided `IdentityProviderRepresentation` object.  The `IdentityProviderService` also contains methods for updating and deleting IdPs, providing comprehensive management capabilities.

The `IdentityProviderTool` class simplifies access to these functions through command-line tools:

```java
// /Users/sshaaf/git/java/waver/.waver-git-clone/sshaaf/keycloak-mcp-server/src/main/java/dev/shaaf/keycloak/mcp/server/idp/IdentityProviderTool.java
@Tool(description = "Get all identity providers in a realm")
String getIdentityProviders(@ToolArg(description = "A String denoting the name of the realm") String realm) {
    try {
        return mapper.writeValueAsString(identityProviderService.getIdentityProviders(realm));
    } // ... error handling
}

@Tool(description = "Get a specific identity provider")
String getIdentityProvider(@ToolArg(description = "A String denoting the name of the realm") String realm,
                           @ToolArg(description = "A String denoting the alias of the identity provider") String alias) {
    try {
        return mapper.writeValueAsString(identityProviderService.getIdentityProvider(realm, alias));
    } // ... error handling
}
```

These tools, annotated with `@Tool`, provide a convenient way to interact with IdPs. `getIdentityProviders` retrieves all IdPs within a specified `realm`. `getIdentityProvider` retrieves a specific IdP using its `alias` and `realm`.  The `IdentityProviderTool` also includes a tool for getting IdP mappers, which control how user attributes are mapped from the IdP to Keycloak.

By combining the `IdentityProviderService` and `IdentityProviderTool`, you gain a powerful and flexible system for managing your application's identity providers.  Remember to explore the other methods available in the `IdentityProviderService` for a complete understanding of its capabilities.  With these tools, you can effectively manage user access and streamline the authentication process within your application.
