import { Tutorial, TutorialGenerationResult, GitHubRepository, TutorialMetadata, FileNode } from '@/types';
import { fetchRepositoryFromUrl, fetchReadme, parseGitHubUrl, fetchRepositoryTree } from '@/lib/github/client';
import { processMarkdown, extractMetadataFromReadme, generateSlug, getLanguageFromExtension } from '@/lib/markdown/processor';

/**
 * Generate a tutorial from a GitHub repository URL
 */
export async function generateTutorialFromUrl(repositoryUrl: string): Promise<TutorialGenerationResult> {
  try {
    // Validate URL
    const parsed = parseGitHubUrl(repositoryUrl);
    if (!parsed) {
      return {
        success: false,
        error: 'Invalid GitHub repository URL',
      };
    }

    // Fetch repository data
    const repository = await fetchRepositoryFromUrl(repositoryUrl);
    
    // Fetch README content
    const readmeContent = await fetchReadme(parsed.owner, parsed.repo);
    
    if (!readmeContent) {
      return {
        success: false,
        error: 'Repository does not have a README file',
      };
    }

    // Process README and extract metadata
    const { content, metadata: frontmatterMetadata } = await processMarkdown(readmeContent);
    const readmeMetadata = extractMetadataFromReadme(readmeContent, repository.name);
    
    // Merge metadata from README and frontmatter
    const metadata: TutorialMetadata = {
      ...readmeMetadata,
      ...frontmatterMetadata,
      lastUpdated: repository.updated_at,
    };

    // Generate file structure
    const fileStructure = await generateFileStructure(parsed.owner, parsed.repo);

    // Create tutorial object
    const tutorial: Tutorial = {
      id: `${repository.owner.login}-${repository.name}`,
      repository,
      metadata,
      content,
      readmeContent,
      fileStructure,
      slug: generateSlug(`${repository.owner.login}-${repository.name}`),
      generatedAt: new Date().toISOString(),
    };

    return {
      success: true,
      tutorial,
      message: 'Tutorial generated successfully',
    };
  } catch (error: any) {
    console.error('Error generating tutorial:', error);
    return {
      success: false,
      error: error.message || 'Failed to generate tutorial',
    };
  }
}

/**
 * Generate file structure for a repository
 */
async function generateFileStructure(owner: string, repo: string): Promise<FileNode[]> {
  try {
    const tree = await fetchRepositoryTree(owner, repo);
    
    // Filter and organize files
    const relevantFiles = tree.filter((item: any) => {
      const path = item.path as string;
      
      // Skip certain directories and files
      const skipPatterns = [
        'node_modules/',
        '.git/',
        'dist/',
        'build/',
        '.next/',
        'coverage/',
        '.nyc_output/',
        'vendor/',
        '__pycache__/',
        '.venv/',
        'venv/',
        '.env',
        '.DS_Store',
        'Thumbs.db',
      ];
      
      return !skipPatterns.some(pattern => path.includes(pattern));
    });

    // Create a tree structure
    const root: { [key: string]: FileNode } = {};
    
    relevantFiles.forEach((item: any) => {
      const pathParts = item.path.split('/');
      let currentLevel = root;
      
      pathParts.forEach((part: string, index: number) => {
        if (!currentLevel[part]) {
          const isLastPart = index === pathParts.length - 1;
          currentLevel[part] = {
            name: part,
            path: item.path,
            type: isLastPart && item.type === 'blob' ? 'file' : 'directory',
            children: isLastPart && item.type === 'blob' ? undefined : {},
            language: isLastPart && item.type === 'blob' ? getLanguageFromExtension(part) : undefined,
          };
        }
        
        if (currentLevel[part].children) {
          currentLevel[part] = currentLevel[part];
        }
        
        if (index < pathParts.length - 1) {
          currentLevel = currentLevel[part].children as { [key: string]: FileNode };
        }
      });
    });

    // Convert object structure to array
    function convertToArray(obj: { [key: string]: FileNode }): FileNode[] {
      return Object.values(obj).map(node => ({
        ...node,
        children: node.children ? convertToArray(node.children as { [key: string]: FileNode }) : undefined,
      }));
    }

    return convertToArray(root);
  } catch (error) {
    console.error('Error generating file structure:', error);
    return [];
  }
}

/**
 * Get tutorial by slug (for pre-generated tutorials)
 */
export async function getTutorialBySlug(slug: string): Promise<Tutorial | null> {
  // This would typically fetch from a database or cache
  // For now, we'll return null since we're generating on-demand
  return null;
}

/**
 * Validate repository URL format
 */
export function validateRepositoryUrl(url: string): { valid: boolean; error?: string } {
  try {
    const urlObj = new URL(url);
    
    if (urlObj.hostname !== 'github.com') {
      return { valid: false, error: 'URL must be a GitHub repository' };
    }

    const pathParts = urlObj.pathname.split('/').filter(Boolean);
    
    if (pathParts.length < 2) {
      return { valid: false, error: 'Invalid repository URL format' };
    }

    return { valid: true };
  } catch {
    return { valid: false, error: 'Invalid URL format' };
  }
}

/**
 * Get repository info without generating full tutorial
 */
export async function getRepositoryInfo(repositoryUrl: string): Promise<GitHubRepository | null> {
  try {
    return await fetchRepositoryFromUrl(repositoryUrl);
  } catch (error) {
    console.error('Error fetching repository info:', error);
    return null;
  }
}