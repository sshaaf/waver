import matter from 'gray-matter';
import { Tutorial, TutorialMetadata, FileNode, WaverConfig } from '@/types';
import { getObject, objectExists, getTutorialDirectories, getTutorialFiles } from './minio-client';
import { formatDate } from './utils';

/**
 * Load tutorial content from MinIO S3
 */
export async function loadTutorialContent(tutorialId: string): Promise<Tutorial | null> {
  try {
    // Check if tutorial directory exists in MinIO
    const configKey = `${tutorialId}/waver-config.json`;
    const indexKey = `${tutorialId}/index.md`;
    
    if (!(await objectExists(configKey))) {
      console.error(`Config file not found in MinIO: ${configKey}`);
      return null;
    }

    if (!(await objectExists(indexKey))) {
      console.error(`Index file not found in MinIO: ${indexKey}`);
      return null;
    }

    // Load waver-config.json file from MinIO
    const configContent = await getObject(configKey);
    if (!configContent) {
      console.error(`Failed to read config file from MinIO: ${configKey}`);
      return null;
    }

    const config: WaverConfig = JSON.parse(configContent);

    // Load index.md file from MinIO
    const indexContent = await getObject(indexKey);
    if (!indexContent) {
      console.error(`Failed to read index file from MinIO: ${indexKey}`);
      return null;
    }

    let { content } = matter(indexContent); // Only get content, no frontmatter
    
    // Fix: Remove code block wrapper if the content is wrapped in a markdown code block
    if (content.startsWith('```markdown\n')) {
      content = content.replace(/^```markdown\n/, '').replace(/\n```$/, '');
    }

    // Parse metadata from config
    const metadata: TutorialMetadata = {
      title: config.title || tutorialId,
      description: config.description || '',
      author: config.author,
      tags: config.tags || [],
      difficulty: config.difficulty || 'intermediate',
      estimatedTime: config.estimatedTime,
      prerequisites: config.prerequisites || [],
      lastUpdated: config.lastUpdated || new Date().toISOString(),
      formattedLastUpdated: config.lastUpdated ? formatDate(config.lastUpdated) : formatDate(new Date().toISOString()),
    };

    // Load all markdown files in the tutorial directory from MinIO
    const files = await getTutorialFiles(tutorialId);
    const markdownFiles = files.filter(file => file.endsWith('.md') && file !== 'index.md');
    
    // Create file structure
    const fileStructure: FileNode[] = [
      {
        name: 'index.md',
        path: 'index.md',
        type: 'file',
        language: 'markdown',
      },
      {
        name: 'waver-config.json',
        path: 'waver-config.json',
        type: 'file',
        language: 'json',
      },
      ...markdownFiles.map(file => ({
        name: file,
        path: file,
        type: 'file' as const,
        language: 'markdown',
      }))
    ];

    // Create a mock repository object (since we're not using GitHub API)
    const repository = {
      id: 1,
      name: tutorialId,
      full_name: `${tutorialId}`,
      description: metadata.description,
      html_url: config.repo || `https://github.com/example/${tutorialId}`,
      clone_url: config.repo || `https://github.com/example/${tutorialId}.git`,
      stargazers_count: 0,
      forks_count: 0,
      language: config.language || 'Markdown',
      default_branch: 'main',
      updated_at: metadata.lastUpdated,
      created_at: new Date().toISOString(),
      owner: {
        login: 'tutorial-author',
        avatar_url: 'https://avatars.githubusercontent.com/u/1234567?v=4',
        html_url: 'https://github.com/tutorial-author',
      },
      topics: config.tags || [],
    };

    return {
      id: tutorialId,
      repository,
      metadata,
      content,
      slug: tutorialId,
      generatedAt: new Date().toISOString(),
      fileStructure,
    };
  } catch (error) {
    console.error(`Error loading tutorial ${tutorialId} from MinIO:`, error);
    return null;
  }
}

/**
 * Load a specific chapter from a tutorial in MinIO
 */
export async function loadTutorialChapter(tutorialId: string, chapterName: string): Promise<string | null> {
  try {
    const chapterKey = `${tutorialId}/${chapterName}`;
    
    if (!(await objectExists(chapterKey))) {
      console.error(`Chapter file not found in MinIO: ${chapterKey}`);
      return null;
    }

    const content = await getObject(chapterKey);
    if (!content) {
      console.error(`Failed to read chapter file from MinIO: ${chapterKey}`);
      return null;
    }

    const { content: markdownContent } = matter(content);
    
    return markdownContent;
  } catch (error) {
    console.error(`Error loading chapter ${chapterName} from tutorial ${tutorialId} from MinIO:`, error);
    return null;
  }
}

/**
 * Get all available tutorials from MinIO
 */
export async function getAllAvailableTutorials(): Promise<string[]> {
  try {
    const directories = await getTutorialDirectories();
    return directories;
  } catch (error) {
    console.error('Error getting available tutorials from MinIO:', error);
    return [];
  }
}

/**
 * Check if a tutorial exists in MinIO
 */
export async function tutorialExists(tutorialId: string): Promise<boolean> {
  try {
    const configKey = `${tutorialId}/waver-config.json`;
    return await objectExists(configKey);
  } catch (error) {
    console.error(`Error checking if tutorial ${tutorialId} exists in MinIO:`, error);
    return false;
  }
}

/**
 * Get tutorial chapters (markdown files) from MinIO
 */
export async function getTutorialChapters(tutorialId: string): Promise<string[]> {
  try {
    const files = await getTutorialFiles(tutorialId);
    return files.filter(file => file.endsWith('.md') && file !== 'index.md');
  } catch (error) {
    console.error(`Error getting chapters for tutorial ${tutorialId} from MinIO:`, error);
    return [];
  }
} 