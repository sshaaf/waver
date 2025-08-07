import { Suspense } from 'react';
import { getFeaturedTutorials, getAllCategories, getAllTutorials } from '@/data/curated-tutorials';
import { HomePageClient } from '@/components/HomePageClient';

export default async function Home() {
  // Load data server-side
  const [featuredTutorials, allTutorials, categories] = await Promise.all([
    getFeaturedTutorials(),
    getAllTutorials(),
    getAllCategories(),
  ]);

  return (
    <Suspense fallback={<div>Loading...</div>}>
      <HomePageClient 
        featuredTutorials={featuredTutorials}
        allTutorials={allTutorials}
        categories={categories}
      />
    </Suspense>
  );
}