import { Octokit } from '@octokit/rest';
import { GitHubRepository, GitHubAPIError } from '@/types';

// Initialize Octokit client
// Note: For production, you might want to use a GitHub token for higher rate limits
const octokit = new Octokit({
  // auth: process.env.GITHUB_TOKEN, // Uncomment and set in .env.local for higher rate limits
});

/**
 * Parse GitHub repository URL to extract owner and repo name
 */
export function parseGitHubUrl(url: string): { owner: string; repo: string } | null {
  try {
    const urlObj = new URL(url);
    
    if (urlObj.hostname !== 'github.com') {
      return null;
    }

    const pathParts = urlObj.pathname.split('/').filter(Boolean);
    
    if (pathParts.length < 2) {
      return null;
    }

    return {
      owner: pathParts[0],
      repo: pathParts[1].replace('.git', ''), // Remove .git suffix if present
    };
  } catch {
    return null;
  }
}

/**
 * Fetch repository information from GitHub API
 */
export async function fetchRepository(owner: string, repo: string): Promise<GitHubRepository> {
  try {
    const { data } = await octokit.rest.repos.get({
      owner,
      repo,
    });

    return {
      id: data.id,
      name: data.name,
      full_name: data.full_name,
      description: data.description,
      html_url: data.html_url,
      clone_url: data.clone_url,
      stargazers_count: data.stargazers_count,
      forks_count: data.forks_count,
      language: data.language,
      default_branch: data.default_branch,
      updated_at: data.updated_at,
      created_at: data.created_at,
      owner: {
        login: data.owner.login,
        avatar_url: data.owner.avatar_url,
        html_url: data.owner.html_url,
      },
      topics: data.topics || [],
    };
  } catch (error: any) {
    const githubError: GitHubAPIError = {
      message: error.message || 'Failed to fetch repository',
      status: error.status || 500,
      documentation_url: error.response?.data?.documentation_url,
    };
    throw githubError;
  }
}

/**
 * Fetch repository from GitHub URL
 */
export async function fetchRepositoryFromUrl(url: string): Promise<GitHubRepository> {
  const parsed = parseGitHubUrl(url);
  
  if (!parsed) {
    throw new Error('Invalid GitHub repository URL');
  }

  return fetchRepository(parsed.owner, parsed.repo);
}

/**
 * Fetch file content from repository
 */
export async function fetchFileContent(
  owner: string,
  repo: string,
  path: string,
  ref?: string
): Promise<string> {
  try {
    const { data } = await octokit.rest.repos.getContent({
      owner,
      repo,
      path,
      ref: ref || 'HEAD',
    });

    if (Array.isArray(data) || data.type !== 'file') {
      throw new Error(`Path ${path} is not a file`);
    }

    if (data.content) {
      return Buffer.from(data.content, 'base64').toString('utf-8');
    }

    throw new Error('File content not available');
  } catch (error: any) {
    throw new Error(`Failed to fetch file ${path}: ${error.message}`);
  }
}

/**
 * Fetch README content from repository
 */
export async function fetchReadme(owner: string, repo: string): Promise<string | null> {
  const readmeFiles = ['README.md', 'readme.md', 'README.rst', 'README.txt', 'README'];
  
  for (const filename of readmeFiles) {
    try {
      return await fetchFileContent(owner, repo, filename);
    } catch {
      // Continue to next filename
    }
  }
  
  return null; // No README found
}

/**
 * Get repository file tree
 */
export async function fetchRepositoryTree(
  owner: string,
  repo: string,
  ref?: string
): Promise<any[]> {
  try {
    const { data } = await octokit.rest.git.getTree({
      owner,
      repo,
      tree_sha: ref || 'HEAD',
      recursive: 'true',
    });

    return data.tree;
  } catch (error: any) {
    throw new Error(`Failed to fetch repository tree: ${error.message}`);
  }
}