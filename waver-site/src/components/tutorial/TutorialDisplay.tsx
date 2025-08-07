'use client';

import { Tutorial, FileNode } from '@/types';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Separator } from '@/components/ui/separator';
import { 
  Star, 
  GitFork, 
  Clock, 
  ExternalLink, 
  User, 
  Calendar,
  Book,
  Target,
  FileText,
  Folder,
  File
} from 'lucide-react';
import Link from 'next/link';
import { useState } from 'react';
import { MermaidRenderer } from '@/components/MermaidRenderer';

interface TutorialDisplayProps {
  tutorial: Tutorial;
  processedContent?: string;
}

export function TutorialDisplay({ tutorial, processedContent }: TutorialDisplayProps) {
  const [showFileTree, setShowFileTree] = useState(false);

  const { repository, metadata, content, fileStructure } = tutorial;

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      {/* Header */}
      <Card>
        <CardHeader>
          <div className="flex items-start justify-between">
            <div className="flex-1">
              <CardTitle className="text-2xl font-bold text-gray-900 mb-2">
                {metadata.title}
              </CardTitle>
              <CardDescription className="text-lg text-gray-600">
                {metadata.description}
              </CardDescription>
            </div>
          </div>

          {/* Repository info */}
          <div className="flex flex-wrap items-center gap-4 text-sm text-gray-500 mt-4">
            <div className="flex items-center gap-1">
              <User className="w-4 h-4" />
              <span>{repository.owner.login}</span>
            </div>
            <div className="flex items-center gap-1">
              <Star className="w-4 h-4" />
              <span>{repository.stargazers_count.toLocaleString()}</span>
            </div>
            <div className="flex items-center gap-1">
              <GitFork className="w-4 h-4" />
              <span>{repository.forks_count.toLocaleString()}</span>
            </div>
            {repository.language && (
              <Badge variant="outline">
                {repository.language}
              </Badge>
            )}
          </div>

          {/* Metadata */}
          <div className="flex flex-wrap gap-4 mt-4">
            {metadata.difficulty && (
              <div className="flex items-center gap-1 text-sm">
                <Target className="w-4 h-4 text-gray-500" />
                <span className="capitalize">{metadata.difficulty}</span>
              </div>
            )}
            {metadata.estimatedTime && (
              <div className="flex items-center gap-1 text-sm">
                <Clock className="w-4 h-4 text-gray-500" />
                <span>{metadata.estimatedTime}</span>
              </div>
            )}
            {metadata.author && (
              <div className="flex items-center gap-1 text-sm">
                <Book className="w-4 h-4 text-gray-500" />
                <span>By {metadata.author}</span>
              </div>
            )}
            {metadata.formattedLastUpdated && (
              <div className="flex items-center gap-1 text-sm">
                <Calendar className="w-4 h-4 text-gray-500" />
                <span>Updated {/* */}{metadata.formattedLastUpdated}</span>
              </div>
            )}
          </div>

          {/* Tags */}
          {metadata.tags && metadata.tags.length > 0 && (
            <div className="flex flex-wrap gap-2 mt-4">
              {metadata.tags.map((tag, index) => (
                <Badge key={index} variant="secondary">
                  {tag}
                </Badge>
              ))}
            </div>
          )}

          {/* Actions */}
          <div className="flex gap-2 mt-4">
            <Button asChild>
              <Link href={repository.html_url} target="_blank" rel="noopener noreferrer">
                <ExternalLink className="w-4 h-4 mr-2" />
                View on GitHub
              </Link>
            </Button>
            {fileStructure && fileStructure.length > 0 && (
              <Button 
                variant="outline" 
                onClick={() => setShowFileTree(!showFileTree)}
              >
                <Folder className="w-4 h-4 mr-2" />
                {showFileTree ? 'Hide' : 'Show'} File Structure
              </Button>
            )}
          </div>
        </CardHeader>
      </Card>

      {/* Prerequisites */}
      {metadata.prerequisites && metadata.prerequisites.length > 0 && (
        <Card>
          <CardHeader>
            <CardTitle className="text-lg">Prerequisites</CardTitle>
          </CardHeader>
          <CardContent>
            <ul className="list-disc list-inside space-y-1">
              {metadata.prerequisites.map((prerequisite, index) => (
                <li key={index} className="text-gray-700">{prerequisite}</li>
              ))}
            </ul>
          </CardContent>
        </Card>
      )}

      {/* File Structure */}
      {showFileTree && fileStructure && fileStructure.length > 0 && (
        <Card>
          <CardHeader>
            <CardTitle className="text-lg flex items-center gap-2">
              <Folder className="w-5 h-5" />
              Project Structure
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="bg-gray-50 p-4 rounded-lg">
              <FileTree nodes={fileStructure} />
            </div>
          </CardContent>
        </Card>
      )}

      {/* Tutorial Content */}
      <Card>
        <CardHeader>
          <CardTitle className="text-lg flex items-center gap-2">
            <FileText className="w-5 h-5" />
            Tutorial Content
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="prose prose-lg max-w-none">
            {processedContent && processedContent.trim() ? (
              <div dangerouslySetInnerHTML={{ __html: processedContent }} />
            ) : (
              <MermaidRenderer content={content} />
            )}
          </div>
        </CardContent>
      </Card>
    </div>
  );
}

// File tree component
interface FileTreeProps {
  nodes: FileNode[];
  depth?: number;
}

function FileTree({ nodes, depth = 0 }: FileTreeProps) {
  return (
    <div className={`${depth > 0 ? 'ml-4' : ''} space-y-1`}>
      {nodes.map((node, index) => (
        <div key={index}>
          <div className="flex items-center gap-2 text-sm">
            {node.type === 'directory' ? (
              <Folder className="w-4 h-4 text-blue-500" />
            ) : (
              <File className="w-4 h-4 text-gray-500" />
            )}
            <span className={node.type === 'directory' ? 'font-medium' : ''}>
              {node.name}
            </span>
            {node.language && (
              <Badge variant="outline" className="text-xs">
                {node.language}
              </Badge>
            )}
          </div>
          {node.children && node.children.length > 0 && (
            <FileTree nodes={node.children} depth={depth + 1} />
          )}
        </div>
      ))}
    </div>
  );
}