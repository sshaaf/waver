---
title: "Chapter 9: Discourse Search Integration"
order: 9
---
## Integrating with Discourse Search

This chapter will guide you through integrating Discourse search functionality into your Keycloak Management Console Provider (MCP) server. This allows users to search the Keycloak community discourse forum directly from the administration console, which can be extremely helpful for troubleshooting and finding solutions to common problems.  This ties into the broader concepts of **User Management** and **Client Management**, as it empowers administrators with tools to better manage their Keycloak instances.

We'll be using the Discourse API to fetch search results. The integration involves three main components:

1. **`DiscourseService`**: This interface defines the REST client used to communicate with the Discourse API.
2. **`SearchResource`**: This resource class exposes an endpoint that our MCP server can use to initiate searches.
3. **`DiscourseTool`**: This class leverages the `SearchResource` and exposes the search functionality as an MCP tool.

Let's break down each part:

**1. `DiscourseService`**

This interface uses MicroProfile Rest Client to simplify interaction with the Discourse API.  The `@RegisterRestClient` annotation registers the interface as a REST client, specifying the base URI of the Discourse forum.

```java
// /Users/sshaaf/git/java/waver/.waver-git-clone/sshaaf/keycloak-mcp-server/src/main/java/dev/shaaf/keycloak/mcp/server/discourse/DiscourseService.java
@Path("/search.json")
@RegisterRestClient(baseUri = "https://keycloak.discourse.group")
public interface DiscourseService {

    @GET
    SearchResult search(@QueryParam("q") String query);
}
```

The `search` method defines a GET request to the `/search.json` endpoint, accepting a search query as a query parameter `q`.

**2. `SearchResource`**

This class acts as a bridge between the `DiscourseService` and the MCP tool.

```java
// /Users/sshaaf/git/java/waver/.waver-git-clone/sshaaf/keycloak-mcp-server/src/main/java/dev/shaaf/keycloak/mcp/server/discourse/SearchResource.java
@Path("/search")
public class SearchResource {

    @RestClient
    DiscourseService discourseService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public SearchResult performSearch(@QueryParam("term") String term) {
        return discourseService.search(term);
    }
}
```

The `@RestClient` annotation injects an instance of `DiscourseService`. The `performSearch` method takes a search term as input, calls the `search` method of the `DiscourseService`, and returns the `SearchResult`.

**3. `DiscourseTool`**

This class makes the search functionality available as an MCP tool.

```java
// /Users/sshaaf/git/java/waver/.waver-git-clone/sshaaf/keycloak-mcp-server/src/main/java/dev/shaaf/keycloak/mcp/server/discourse/DiscourseTool.java
@ApplicationScoped
public class DiscourseTool {

    @Inject
    SearchResource searchResource;

    @Tool(description = "Search keycloak community discourse for similar issues...")
    String search(@ToolArg(description = "Search discource for similar discussions") String query) {
        return searchResource.performSearch(query).toString();
    }
}
```

The `@Tool` annotation exposes the `search` method as an MCP tool.  The `@ToolArg` annotation provides a description for the search query argument.

By combining these components, you can seamlessly integrate Discourse search into your Keycloak MCP server, providing a valuable resource for users seeking help and information within the Keycloak community. Remember, understanding these concepts contributes to a smoother experience with **Realm Management** and other related administrative tasks within Keycloak.  In the next chapter, we will cover [next chapter topic].
