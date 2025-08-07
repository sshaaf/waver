import { unified } from 'unified';
import remarkParse from 'remark-parse';
import remarkGfm from 'remark-gfm';
import remarkRehype from 'remark-rehype';
import rehypeHighlight from 'rehype-highlight';
import rehypeStringify from 'rehype-stringify';
import matter from 'gray-matter';
import { TutorialMetadata } from '@/types';
import { visit } from 'unist-util-visit';

/**
 * Custom remark plugin to handle Mermaid code blocks
 */
function remarkMermaid() {
  return function (tree: any) {
    visit(tree, 'code', (node: any) => {
      if (node.lang === 'mermaid') {
        // Convert Mermaid code blocks to a special HTML element
        node.type = 'html';
        node.value = `<div class="mermaid-diagram" data-mermaid="${encodeURIComponent(node.value)}" id="mermaid-${Date.now()}-${Math.random().toString(36).substr(2, 9)}">
          <div class="mermaid-loading">Loading diagram...</div>
        </div>`;
        delete node.lang;
        delete node.meta;
      }
    });
  };
}

/**
 * Process markdown content and extract frontmatter
 */
export async function processMarkdown(content: string, tutorialSlug?: string): Promise<{
  content: string;
  metadata: Partial<TutorialMetadata>;
}> {
  try {
    // Parse frontmatter
    const { data, content: markdownContent } = matter(content);
    
    // Process markdown to HTML
    let processedContent = await unified()
      .use(remarkParse)
      .use(remarkGfm) // GitHub Flavored Markdown
      .use(remarkMermaid) // Custom Mermaid plugin
      .use(remarkRehype, { allowDangerousHtml: true })
      .use(rehypeHighlight) // Syntax highlighting
      .use(rehypeStringify, { allowDangerousHtml: true })
      .process(markdownContent);

    let htmlContent = String(processedContent);

    // Mermaid diagrams are now handled by the remark plugin
    // No additional processing needed
    
    // Process relative links to chapter pages
    if (tutorialSlug) {
      htmlContent = processChapterLinks(htmlContent, tutorialSlug);
    }

    return {
      content: htmlContent,
      metadata: {
        title: data.title,
        description: data.description,
        author: data.author,
        tags: data.tags || [],
        difficulty: data.difficulty,
        estimatedTime: data.estimatedTime,
        prerequisites: data.prerequisites || [],
        lastUpdated: data.lastUpdated,
      },
    };
  } catch (error) {
    console.error('Error processing markdown:', error);
    throw new Error('Failed to process markdown content');
  }
}

/**
 * Extract metadata from README content
 */
export function extractMetadataFromReadme(content: string, repositoryName: string): TutorialMetadata {
  const { data, content: markdownContent } = matter(content);
  
  // Extract title from frontmatter or first heading
  let title = data.title || repositoryName;
  if (!data.title) {
    const titleMatch = markdownContent.match(/^#\s+(.+)$/m);
    if (titleMatch) {
      title = titleMatch[1].trim();
    }
  }

  // Extract description from frontmatter or first paragraph
  let description = data.description || '';
  if (!data.description) {
    const lines = markdownContent.split('\n');
    for (const line of lines) {
      const trimmed = line.trim();
      if (trimmed && !trimmed.startsWith('#') && !trimmed.startsWith('```')) {
        description = trimmed;
        break;
      }
    }
  }

  return {
    title,
    description,
    author: data.author,
    tags: data.tags || [],
    difficulty: data.difficulty || 'intermediate',
    estimatedTime: data.estimatedTime,
    prerequisites: data.prerequisites || [],
    lastUpdated: data.lastUpdated,
  };
}



/**
 * Process relative links to chapter pages
 */
function processChapterLinks(htmlContent: string, tutorialSlug: string): string {
  // Find all relative links to .md files and convert them to proper chapter routes
  const linkRegex = /<a href="\.\/([^"]+\.md)">([^<]+)<\/a>/g;
  
  return htmlContent.replace(linkRegex, (match, filename, linkText) => {
    // Create the proper chapter route
    const chapterRoute = `/tutorial/${tutorialSlug}/chapter/${encodeURIComponent(filename)}`;
    
    return `<a href="${chapterRoute}">${linkText}</a>`;
  });
}



/**
 * Generate a slug from a string
 */
export function generateSlug(text: string): string {
  return text
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, '-')
    .replace(/^-+|-+$/g, '');
}

/**
 * Determine programming language from file extension
 */
export function getLanguageFromExtension(filename: string): string | undefined {
  const ext = filename.split('.').pop()?.toLowerCase();
  
  const languageMap: Record<string, string> = {
    js: 'javascript',
    jsx: 'javascript',
    ts: 'typescript',
    tsx: 'typescript',
    py: 'python',
    java: 'java',
    cpp: 'cpp',
    c: 'c',
    cs: 'csharp',
    php: 'php',
    rb: 'ruby',
    go: 'go',
    rs: 'rust',
    swift: 'swift',
    kt: 'kotlin',
    scala: 'scala',
    sh: 'bash',
    bash: 'bash',
    zsh: 'bash',
    fish: 'bash',
    ps1: 'powershell',
    sql: 'sql',
    html: 'html',
    css: 'css',
    scss: 'scss',
    sass: 'sass',
    json: 'json',
    xml: 'xml',
    yaml: 'yaml',
    yml: 'yaml',
    toml: 'toml',
    ini: 'ini',
    conf: 'ini',
    md: 'markdown',
    dockerfile: 'dockerfile',
    tf: 'terraform',
  };

  return languageMap[ext || ''];
}

/**
 * Process code content for syntax highlighting
 */
export async function processCodeContent(content: string, language?: string): Promise<string> {
  const codeBlock = language ? `\`\`\`${language}\n${content}\n\`\`\`` : `\`\`\`\n${content}\n\`\`\``;
  
  const processed = await unified()
    .use(remarkParse)
    .use(remarkRehype)
    .use(rehypeHighlight)
    .use(rehypeStringify)
    .process(codeBlock);

  return String(processed);
}

/**
 * Clean and format markdown content
 */
export function cleanMarkdownContent(content: string): string {
  return content
    .replace(/^\s+|\s+$/g, '') // Trim whitespace
    .replace(/\n{3,}/g, '\n\n'); // Normalize multiple newlines
}