'use client';

import { Tutorial } from '@/types';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Star, GitFork, Clock, ExternalLink, User } from 'lucide-react';
import Link from 'next/link';
import { formatDate } from '@/lib/utils/ssr-safe';

interface TutorialCardProps {
  tutorial: Tutorial;
}

export function TutorialCard({ tutorial }: TutorialCardProps) {
  const { repository, metadata } = tutorial;

  const title = metadata.title || repository.name;
  const description = metadata.description || repository.description || 'No description available';

  return (
    <Card className="w-full transition-all duration-200 hover:shadow-lg">
      <CardHeader>
        <div className="flex items-start justify-between">
          <div className="flex-1">
            <div className="flex items-center gap-2 mb-2">
              <CardTitle className="text-lg font-semibold text-gray-900">
                {title}
              </CardTitle>
              {metadata.tags?.includes('Featured') && (
                <Badge variant="secondary" className="bg-blue-100 text-blue-700">
                  Featured
                </Badge>
              )}
            </div>
            <CardDescription className="text-gray-600">
              {description}
            </CardDescription>
          </div>
        </div>

        {/* Repository metadata */}
        <div className="flex flex-wrap items-center gap-4 text-sm text-gray-500 mt-3">
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
            <Badge variant="outline" className="text-xs">
              {repository.language}
            </Badge>
          )}
        </div>
      </CardHeader>

      <CardContent className="pt-0">
        <div className="flex flex-wrap gap-2 mb-4">
          {metadata.tags?.slice(0, 3).map((tag, index) => (
            <Badge key={index} variant="outline" className="text-xs">
              {tag}
            </Badge>
          ))}
          {metadata.tags && metadata.tags.length > 3 && (
            <Badge variant="outline" className="text-xs">
              +{metadata.tags.length - 3} more
            </Badge>
          )}
        </div>

        <div className="flex gap-2">
          <Button
            asChild
            className="flex-1"
            size="sm"
          >
            <Link href={`/tutorial/${tutorial.slug}`}>
              View Tutorial
            </Link>
          </Button>
          <Button
            variant="outline"
            size="sm"
            asChild
          >
            <Link href={repository.html_url} target="_blank" rel="noopener noreferrer">
              <ExternalLink className="w-4 h-4" />
            </Link>
          </Button>
        </div>

        {/* Last updated */}
        <div className="flex items-center gap-1 text-xs text-gray-400 mt-3">
          <Clock className="w-3 h-3" />
          <span>Updated {formatDate(repository.updated_at)}</span>
        </div>
      </CardContent>
    </Card>
  );
}