'use client';

import { useState, useCallback, useMemo } from 'react';
import { TutorialCard } from '@/components/tutorial/TutorialCard';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { Separator } from '@/components/ui/separator';
import { Tutorial } from '@/types';
import { Search, BookOpen, Sparkles, Github, Loader2 } from 'lucide-react';

interface HomePageClientProps {
  featuredTutorials: Tutorial[];
  allTutorials: Tutorial[];
  categories: string[];
}

export function HomePageClient({ featuredTutorials, allTutorials, categories }: HomePageClientProps) {
  const [selectedCategory, setSelectedCategory] = useState<string>('all');
  const [searchTerm, setSearchTerm] = useState('');
  const [githubUrl, setGithubUrl] = useState('');
  const [isGenerating, setIsGenerating] = useState(false);
  const [generateMessage, setGenerateMessage] = useState('');

  // Filter tutorials based on category and search term
  const filteredTutorials = useMemo(() => {
    let tutorials = allTutorials;
    
    // Apply search filter
    if (searchTerm) {
      const lowercaseQuery = searchTerm.toLowerCase();
      tutorials = tutorials.filter(tutorial => {
        return (
          tutorial.metadata.title.toLowerCase().includes(lowercaseQuery) ||
          tutorial.metadata.description.toLowerCase().includes(lowercaseQuery) ||
          tutorial.metadata.tags?.some(tag => tag.toLowerCase().includes(lowercaseQuery)) ||
          tutorial.repository.language?.toLowerCase().includes(lowercaseQuery) ||
          tutorial.repository.topics?.some(topic => topic.toLowerCase().includes(lowercaseQuery))
        );
      });
    }
    
    // Apply category filter
    if (selectedCategory !== 'all') {
      tutorials = tutorials.filter(tutorial => 
        tutorial.metadata.tags?.some(tag => tag.toLowerCase() === selectedCategory.toLowerCase())
      );
    }
    
    return tutorials;
  }, [selectedCategory, searchTerm, allTutorials]);

  const handleGenerateTutorial = async () => {
    if (!githubUrl.trim()) {
      setGenerateMessage('Please enter a GitHub repository URL');
      return;
    }

    setIsGenerating(true);
    setGenerateMessage('');

    try {
      const response = await fetch('/api/generate-tutorial', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ repositoryUrl: githubUrl }),
      });

      if (response.ok) {
        setGenerateMessage('Your site has been scheduled for indexing, return back in 10 mins');
        setGithubUrl('');
      } else {
        const error = await response.json();
        setGenerateMessage(error.message || 'Failed to schedule tutorial generation');
      }
    } catch (error) {
      setGenerateMessage('Failed to connect to the server. Please try again.');
    } finally {
      setIsGenerating(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-white border-b">
        <div className="container mx-auto px-4 py-8">
          <div className="text-center">
            <h1 className="text-4xl font-bold text-gray-900 mb-4 flex items-center justify-center gap-3">
              <BookOpen className="w-10 h-10 text-blue-600" />
              Waver Tutorial Platform
            </h1>
            <p className="text-xl text-gray-600 max-w-2xl mx-auto">
              Transform any GitHub repository into an interactive tutorial. 
              Explore curated tutorials or generate your own from any public repository.
            </p>
          </div>
        </div>
      </div>

      <div className="container mx-auto px-4 py-8 space-y-8">
        {/* Generate Tutorial Section */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Github className="w-5 h-5" />
              Generate Your Own Tutorial
            </CardTitle>
            <CardDescription>
              Enter a GitHub repository URL to generate a tutorial automatically
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className="flex gap-4">
              <div className="flex-1">
                <Input
                  placeholder="https://github.com/username/repository"
                  value={githubUrl}
                  onChange={(e) => setGithubUrl(e.target.value)}
                  className="w-full"
                />
              </div>
              <Button 
                onClick={handleGenerateTutorial}
                disabled={isGenerating}
                className="flex items-center gap-2"
              >
                {isGenerating ? (
                  <>
                    <Loader2 className="w-4 h-4 animate-spin" />
                    Generating...
                  </>
                ) : (
                  <>
                    <Sparkles className="w-4 h-4" />
                    Generate
                  </>
                )}
              </Button>
            </div>
            {generateMessage && (
              <div className={`mt-3 p-3 rounded-md text-sm ${
                generateMessage.includes('scheduled') 
                  ? 'bg-green-50 text-green-700 border border-green-200' 
                  : 'bg-red-50 text-red-700 border border-red-200'
              }`}>
                {generateMessage}
              </div>
            )}
          </CardContent>
        </Card>

        <Separator />

        {/* Recent Tutorials */}
        {featuredTutorials.length > 0 && (
          <div>
            <div className="flex items-center gap-2 mb-6">
              <Sparkles className="w-6 h-6 text-yellow-500" />
              <h2 className="text-2xl font-bold text-gray-900">Recent Tutorials</h2>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {featuredTutorials.slice(0, 6).map((tutorial) => (
                <TutorialCard
                  key={tutorial.id}
                  tutorial={tutorial}
                />
              ))}
            </div>
          </div>
        )}

        <Separator />

        {/* All Tutorials */}
        <div>
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-2xl font-bold text-gray-900">All Tutorials</h2>
            <div className="flex items-center gap-4">
              {/* Search */}
              <div className="relative">
                <Search className="w-4 h-4 absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                <Input
                  placeholder="Search tutorials..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="pl-10 w-64"
                />
              </div>
            </div>
          </div>

          {/* Category Filters */}
          <div className="flex flex-wrap gap-2 mb-6">
            <button
              onClick={() => setSelectedCategory('all')}
              className={`px-3 py-1 rounded-full text-sm font-medium transition-colors ${
                selectedCategory === 'all' 
                  ? 'bg-blue-600 text-white' 
                  : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
              }`}
            >
              All Categories
            </button>
            {categories.map((category) => (
              <button
                key={category}
                onClick={() => setSelectedCategory(category)}
                className={`px-3 py-1 rounded-full text-sm font-medium transition-colors ${
                  selectedCategory === category 
                    ? 'bg-blue-600 text-white' 
                    : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
                }`}
              >
                {category}
              </button>
            ))}
          </div>

          {/* Tutorial Grid */}
          {filteredTutorials.length > 0 ? (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {filteredTutorials.map((tutorial) => (
                <TutorialCard
                  key={tutorial.id}
                  tutorial={tutorial}
                />
              ))}
            </div>
          ) : (
            <Card className="text-center py-8">
              <CardContent>
                <p className="text-gray-500">
                  No tutorials found matching your criteria.
                </p>
              </CardContent>
            </Card>
          )}
        </div>
      </div>
    </div>
  );
} 