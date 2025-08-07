'use client';

import { useEffect, useState } from 'react';
import { MermaidDiagram } from './MermaidDiagram';

interface MermaidRendererProps {
  content: string;
}

export function MermaidRenderer({ content }: MermaidRendererProps) {
  const [processedContent, setProcessedContent] = useState(content);
  const [mermaidDiagrams, setMermaidDiagrams] = useState<Array<{ id: string; code: string }>>([]);

  useEffect(() => {
    console.log('MermaidRenderer: Processing content...');
    console.log('Content length:', content.length);
    console.log('Content sample:', content.substring(0, 500));
    console.log('MermaidRenderer: Component re-render triggered');
    
    // Find all Mermaid diagram placeholders - more robust pattern
    const mermaidRegex = /<div class="mermaid-diagram"[^>]*data-mermaid="([^"]*)"[^>]*id="([^"]*)"[^>]*>[\s\S]*?<\/div>/g;
    console.log('MermaidRenderer: Looking for diagrams with regex:', mermaidRegex);
    console.log('MermaidRenderer: Content to search:', content.substring(0, 1000));
    const diagrams: Array<{ id: string; code: string }> = [];
    let match;
    let processedContent = content;

    while ((match = mermaidRegex.exec(content)) !== null) {
      const encodedCode = match[1];
      const id = match[2];
      const code = decodeURIComponent(encodedCode);
      
      console.log('Found Mermaid diagram:', { id, code: code.substring(0, 200) + '...' });
      console.log('Full Mermaid code length:', code.length);
      diagrams.push({ id, code });
      
      // Replace the placeholder with a React component placeholder
      processedContent = processedContent.replace(
        match[0],
        `<div data-mermaid-component="${id}"></div>`
      );
    }

    // Debug: Let's also try a simpler search to see what's actually in the content
    const simpleSearch = content.match(/<div class="mermaid-diagram[^>]*>/g);
    console.log('MermaidRenderer: Simple search found:', simpleSearch);
    
    // Debug: Let's also check for any div with mermaid in the class
    const anyMermaidDiv = content.match(/<div[^>]*mermaid[^>]*>/g);
    console.log('MermaidRenderer: Any mermaid div found:', anyMermaidDiv);
    
    // Fallback: If no diagrams found with regex, try manual parsing
    if (diagrams.length === 0 && anyMermaidDiv) {
      console.log('MermaidRenderer: Trying fallback parsing...');
      anyMermaidDiv.forEach((div, index) => {
        const dataMermaidMatch = div.match(/data-mermaid="([^"]*)"/);
        const idMatch = div.match(/id="([^"]*)"/);
        
        if (dataMermaidMatch && idMatch) {
          const encodedCode = dataMermaidMatch[1];
          const id = idMatch[1];
          const code = decodeURIComponent(encodedCode);
          
          console.log('Fallback found Mermaid diagram:', { id, code: code.substring(0, 200) + '...' });
          diagrams.push({ id, code });
        }
      });
    }

    console.log('Total diagrams found:', diagrams.length);
    setMermaidDiagrams(diagrams);
    setProcessedContent(processedContent);
  }, [content]);

  // Function to render Mermaid components in the processed content
  const renderContent = () => {
    console.log('MermaidRenderer: Rendering content...');
    console.log('Mermaid diagrams:', mermaidDiagrams);
    
    if (mermaidDiagrams.length === 0) {
      console.log('MermaidRenderer: No diagrams found, rendering raw content');
      return <div dangerouslySetInnerHTML={{ __html: processedContent }} />;
    }

    // Split the content by Mermaid component placeholders
    const parts = processedContent.split(/<div data-mermaid-component="([^"]*)"><\/div>/);
    console.log('MermaidRenderer: Content parts:', parts.length);
    console.log('MermaidRenderer: Processed content sample:', processedContent.substring(0, 500));
    console.log('MermaidRenderer: Parts:', parts.map((part, i) => `Part ${i}: ${part.substring(0, 100)}...`));
    const elements: JSX.Element[] = [];

    for (let i = 0; i < parts.length; i++) {
      if (i % 2 === 0) {
        // Regular HTML content
        if (parts[i]) {
          elements.push(
            <div key={`content-${i}`} dangerouslySetInnerHTML={{ __html: parts[i] }} />
          );
        }
      } else {
        // Mermaid component placeholder
        const diagramId = parts[i];
        const diagram = mermaidDiagrams.find(d => d.id === diagramId);
        
        console.log('MermaidRenderer: Processing diagram placeholder:', { diagramId, diagram });
        
        if (diagram) {
          elements.push(
            <MermaidDiagram
              key={`mermaid-${diagramId}-${diagram.code.length}`}
              id={diagram.id}
              mermaidCode={diagram.code}
            />
          );
        }
      }
    }

    console.log('MermaidRenderer: Total elements to render:', elements.length);
    return <>{elements}</>;
  };

  return renderContent();
} 