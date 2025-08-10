import { Tutorial } from '@/types';
import { loadTutorialContent, getAllAvailableTutorials } from '@/lib/tutorial-loader';

// Load tutorials from the generated directory
export async function loadTutorialsFromGenerated(): Promise<Tutorial[]> {
  const availableTutorials = await getAllAvailableTutorials();
  const tutorials: Tutorial[] = [];

  for (const tutorialId of availableTutorials) {
    const tutorial = await loadTutorialContent(tutorialId);
    if (tutorial) {
      tutorials.push(tutorial);
    }
  }

  return tutorials;
}

// For now, we'll use a placeholder until we have more tutorials
export const tutorials: Tutorial[] = [];

/**
 * Get Recent tutorials
 */
export async function getFeaturedTutorials(): Promise<Tutorial[]> {
  const allTutorials = await loadTutorialsFromGenerated();
  return allTutorials.filter(tutorial => 
    tutorial.metadata.tags?.includes('Featured') || tutorial.metadata.tags?.includes('featured')
  );
}

/**
 * Get tutorials by category
 */
export async function getTutorialsByCategory(category: string): Promise<Tutorial[]> {
  const allTutorials = await loadTutorialsFromGenerated();
  return allTutorials.filter(tutorial => 
    tutorial.metadata.tags?.some(tag => tag.toLowerCase() === category.toLowerCase())
  );
}

/**
 * Get all categories
 */
export async function getAllCategories(): Promise<string[]> {
  const allTutorials = await loadTutorialsFromGenerated();
  const categories = new Set<string>();
  
  allTutorials.forEach(tutorial => {
    tutorial.metadata.tags?.forEach(tag => {
      if (tag && typeof tag === 'string') {
        categories.add(tag);
      }
    });
  });
  
  return Array.from(categories).sort();
}

/**
 * Get tutorial by slug
 */
export async function getTutorialBySlug(slug: string): Promise<Tutorial | undefined> {
  const allTutorials = await loadTutorialsFromGenerated();
  return allTutorials.find(tutorial => tutorial.slug === slug);
}

/**
 * Get tutorial by ID
 */
export async function getTutorialById(id: string): Promise<Tutorial | undefined> {
  const allTutorials = await loadTutorialsFromGenerated();
  return allTutorials.find(tutorial => tutorial.id === id);
}

/**
 * Search tutorials
 */
export async function searchTutorials(query: string): Promise<Tutorial[]> {
  const allTutorials = await loadTutorialsFromGenerated();
  const lowercaseQuery = query.toLowerCase();
  
  return allTutorials.filter(tutorial => {
    return (
      tutorial.metadata.title.toLowerCase().includes(lowercaseQuery) ||
      tutorial.metadata.description.toLowerCase().includes(lowercaseQuery) ||
      tutorial.metadata.tags?.some((tag: string) => tag.toLowerCase().includes(lowercaseQuery)) ||
      tutorial.repository.language?.toLowerCase().includes(lowercaseQuery) ||
      tutorial.repository.topics?.some((topic: string) => topic.toLowerCase().includes(lowercaseQuery))
    );
  });
}

/**
 * Get all tutorials
 */
export async function getAllTutorials(): Promise<Tutorial[]> {
  return await loadTutorialsFromGenerated();
}