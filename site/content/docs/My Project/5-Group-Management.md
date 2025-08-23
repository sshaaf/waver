---
title: "Chapter 5: Group Management"
order: 5
---
## Managing Groups in Keycloak

Groups in Keycloak provide a way to organize users and manage their access to resources.  This chapter will guide you through the basics of group management using the provided tools and services.  Understanding group management is crucial for controlling access and simplifying user management within your Keycloak realms.  This ties into concepts like `Role Management`, `User Management`, and `Realm Management` because groups can be assigned roles, contain users, and exist within a realm.

### Working with Groups

The `GroupService` class provides the core functionality for interacting with Keycloak groups.  Let's explore some of the key methods:

**1. Retrieving Groups:**

You can retrieve all groups within a realm using the `getGroups` method:

```java
List<GroupRepresentation> groups = groupService.getGroups("your_realm_name");
```

This method returns a list of `GroupRepresentation` objects, each representing a group in the specified realm.  Remember to replace `"your_realm_name"` with the actual name of your realm.

**2. Getting a Specific Group:**

To retrieve a specific group, use the `getGroup` method, providing the realm and the group's ID:

```java
GroupRepresentation group = groupService.getGroup("your_realm_name", "group_id");
```

This returns a `GroupRepresentation` object for the specified group or `null` if the group isn't found.

**3. Creating a Group:**

Creating a new group is straightforward with the `createGroup` method:

```java
String result = groupService.createGroup("your_realm_name", "new_group_name");
```

This method takes the realm and the desired group name as arguments. It returns a success or error message.

**4. Updating a Group:**

The `updateGroup` method allows you to modify an existing group:

```java
GroupRepresentation updatedGroup = new GroupRepresentation();
updatedGroup.setName("updated_group_name");
String result = groupService.updateGroup("your_realm_name", "group_id", updatedGroup);
```

You'll need to create a `GroupRepresentation` object with the desired changes and provide it along with the realm and group ID.

**5. Deleting a Group:**

Deleting a group is done using the `deleteGroup` method:

```java
String result = groupService.deleteGroup("your_realm_name", "group_id");
```

This method takes the realm and group ID as arguments and returns a success or error message.

### Using the GroupTool

The `GroupTool` class provides a convenient way to access the `GroupService` functionality from the command line. Here are a few examples:

**1. Listing all groups:**

```bash
./kc-mcp-server group get-groups --realm your_realm_name
```

**2. Creating a new group:**

```bash
./kc-mcp-server group create-group --realm your_realm_name --group-name my_new_group
```

These command-line tools simplify group management tasks, making them easily scriptable and integrable into other workflows.  This interacts with the broader concept of `Client Management` by providing tools a client can use to manage groups.


This chapter has provided a foundational understanding of group management in Keycloak.  Experiment with these methods and tools to become comfortable with managing groups and organizing your users effectively. In the next chapters, we'll delve deeper into other essential aspects of Keycloak administration, such as role management and user management, building upon the knowledge you've gained here.  Don't be afraid to try things out and explore the provided code examples – practice is the key to mastering Keycloak!
