import { notFound } from 'next/navigation';
import { TutorialDisplay } from '@/components/tutorial/TutorialDisplay';
import { Button } from '@/components/ui/button';
import { loadTutorialContent, getTutorialChapters } from '@/lib/tutorial-loader';
import { ArrowLeft, BookOpen, FileText } from 'lucide-react';
import Link from 'next/link';
import { processMarkdown } from '@/lib/markdown/processor';

interface TutorialPageProps {
  params: {
    slug: string;
  };
}

export default async function TutorialPage({ params }: TutorialPageProps) {
  const { slug } = await params;
  
  // Load tutorial content
  const tutorial = await loadTutorialContent(slug);
  
  if (!tutorial) {
    notFound();
  }

  // Process the markdown content
  const { content: processedContent } = await processMarkdown(tutorial.content, slug);
  
  // Get chapters
  const chapters = await getTutorialChapters(slug);

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="container mx-auto px-4 py-8">
        {/* Back button */}
        <div className="mb-6">
          <Button variant="outline" asChild>
            <Link href="/">
              <ArrowLeft className="w-4 h-4 mr-2" />
              Back to Home
            </Link>
          </Button>
        </div>

        {/* Tutorial content */}
        <div className="max-w-4xl mx-auto">
          <TutorialDisplay tutorial={tutorial} processedContent={processedContent} />
        </div>

        {/* Chapters navigation */}
        {chapters.length > 0 && (
          <div className="mt-12 max-w-4xl mx-auto">
            <div className="bg-white rounded-lg shadow-sm border p-6">
              <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center gap-2">
                <FileText className="w-5 h-5" />
                Tutorial Chapters
              </h2>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {chapters.map((chapter) => (
                  <Link
                    key={chapter}
                    href={`/tutorial/${slug}/chapter/${encodeURIComponent(chapter)}`}
                    className="block p-4 border rounded-lg hover:bg-gray-50 transition-colors"
                  >
                    <h3 className="font-medium text-gray-900 mb-1">
                      {chapter.replace('.md', '').replace(/-/g, ' ')}
                    </h3>
                    <p className="text-sm text-gray-600">
                      Click to read this chapter
                    </p>
                  </Link>
                ))}
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
} 