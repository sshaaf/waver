import { notFound } from 'next/navigation';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { loadTutorialContent, loadTutorialChapter } from '@/lib/tutorial-loader';
import { ArrowLeft, BookOpen } from 'lucide-react';
import Link from 'next/link';
import { processMarkdown } from '@/lib/markdown/processor';
import { MermaidRenderer } from '@/components/MermaidRenderer';

interface ChapterPageProps {
  params: {
    slug: string;
    chapter: string;
  };
}

export default async function ChapterPage({ params }: ChapterPageProps) {
  const { slug, chapter } = await params;
  
  // Load tutorial metadata
  const tutorial = await loadTutorialContent(slug);
  
  if (!tutorial) {
    notFound();
  }

  // Load chapter content
  const chapterContent = await loadTutorialChapter(slug, decodeURIComponent(chapter));
  
  if (!chapterContent) {
    notFound();
  }

  // Process the markdown content
  const { content: processedContent } = await processMarkdown(chapterContent, slug);

  const chapterTitle = decodeURIComponent(chapter).replace('.md', '').replace(/-/g, ' ');

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="container mx-auto px-4 py-8">
        {/* Navigation */}
        <div className="mb-6 flex items-center gap-4">
          <Button variant="outline" asChild>
            <Link href="/">
              <ArrowLeft className="w-4 h-4 mr-2" />
              Home
            </Link>
          </Button>
          <Button variant="outline" asChild>
            <Link href={`/tutorial/${slug}`}>
              <BookOpen className="w-4 h-4 mr-2" />
              {tutorial.metadata.title}
            </Link>
          </Button>
        </div>

        {/* Chapter content */}
        <div className="max-w-4xl mx-auto">
          <Card>
            <CardHeader>
              <CardTitle className="text-2xl font-bold text-gray-900">
                {chapterTitle}
              </CardTitle>
              <p className="text-gray-600">
                Chapter from: {tutorial.metadata.title}
              </p>
            </CardHeader>
            <CardContent>
              <div className="prose prose-lg max-w-none">
                <MermaidRenderer content={processedContent} />
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
} 