/**
 * Core types for the Waver Tutorial Platform
 */

export interface GitHubRepository {
  id?: number;
  name: string;
  full_name: string;
  description: string | null;
  html_url: string;
  clone_url?: string;
  stargazers_count: number;
  forks_count: number;
  language: string | null;
  default_branch?: string;
  updated_at: string;
  created_at: string;
  owner: {
    login: string;
    avatar_url: string;
    html_url: string;
  };
  topics: string[];
}

export interface TutorialMetadata {
  title: string;
  description: string;
  author?: string;
  tags?: string[];
  difficulty?: 'beginner' | 'intermediate' | 'advanced';
  estimatedTime?: string;
  prerequisites?: string[];
  lastUpdated?: string;
  formattedLastUpdated?: string;
}

export interface Tutorial {
  id: string;
  repository: GitHubRepository;
  metadata: TutorialMetadata;
  content: string;
  readmeContent?: string;
  fileStructure?: FileNode[];
  slug: string;
  generatedAt: string;
}

export interface FileNode {
  name: string;
  path: string;
  type: 'file' | 'directory';
  children?: FileNode[];
  content?: string;
  language?: string;
}

export interface CuratedTutorial {
  repositoryUrl: string;
  featured?: boolean;
  category?: string;
  customTitle?: string;
  customDescription?: string;
}

export interface TutorialGenerationResult {
  success: boolean;
  tutorial?: Tutorial;
  error?: string;
  message?: string;
}

export interface GitHubAPIError {
  message: string;
  status: number;
  documentation_url?: string;
}

export interface WaverConfig {
  title: string;
  description?: string;
  repo?: string;
  author?: string;
  language?: string;
  tags: string[];
  difficulty?: 'beginner' | 'intermediate' | 'advanced';
  estimatedTime?: string;
  prerequisites?: string[];
  lastUpdated?: string;
}