'use client';

import { useEffect, useRef, useState } from 'react';

// Helper function to load Mermaid script
const loadMermaidScript = (callback: () => void) => {
  console.log('MermaidDiagram: Loading Mermaid script...');
  const existingScript = document.getElementById('mermaid-script');
  if (!existingScript) {
    console.log('MermaidDiagram: Creating new script element...');
    const script = document.createElement('script');
    script.src = 'https://cdn.jsdelivr.net/npm/mermaid@10/dist/mermaid.min.js';
    script.id = 'mermaid-script';
    document.body.appendChild(script);
    script.onload = () => {
      console.log('MermaidDiagram: Script loaded, initializing Mermaid...');
      // Initialize Mermaid after the script is loaded
      (window as any).mermaid.initialize({
        startOnLoad: false,
        theme: 'default',
        securityLevel: 'loose',
        fontFamily: 'Inter, sans-serif',
      });
      console.log('MermaidDiagram: Mermaid initialized');
      // Add a small delay to ensure Mermaid is fully ready
      setTimeout(() => {
        console.log('MermaidDiagram: Mermaid should be ready now');
        console.log('MermaidDiagram: Checking if window.mermaid is available:', typeof (window as any).mermaid !== 'undefined');
        if (callback) callback();
      }, 200);
    };
    script.onerror = (error) => {
      console.error('MermaidDiagram: Script loading error:', error);
    };
  } else {
    console.log('MermaidDiagram: Script already exists');
    // If script already exists, just run the callback
    if (callback) callback();
  }
};

interface MermaidDiagramProps {
  id: string;
  mermaidCode: string;
}

export function MermaidDiagram({ id, mermaidCode }: MermaidDiagramProps) {
  console.log('MermaidDiagram: Component rendered with props:', { id, mermaidCode: mermaidCode?.substring(0, 100) + '...' });
  
  const containerRef = useRef<HTMLDivElement>(null);
  const [isMermaidReady, setIsMermaidReady] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let isMounted = true;
    
    loadMermaidScript(() => {
      if (isMounted) {
        console.log('MermaidDiagram: Setting isMermaidReady to true');
        setIsMermaidReady(true);
      }
    });

    return () => {
      isMounted = false;
    };
  }, []);

  useEffect(() => {
    let isMounted = true;
    
    console.log('MermaidDiagram: Rendering effect triggered', { isMermaidReady, mermaidCode: mermaidCode?.substring(0, 50) + '...' });
    
    const renderDiagram = async () => {
      // Ensure the container and mermaid library are ready
      if (!containerRef.current || typeof (window as any).mermaid === 'undefined') {
        console.log('MermaidDiagram: Container or mermaid not ready', { 
          hasContainer: !!containerRef.current, 
          hasMermaid: typeof (window as any).mermaid !== 'undefined' 
        });
        return;
      }

      console.log('MermaidDiagram: Rendering diagram...', { id });
      console.log('MermaidDiagram: Full mermaidCode:', mermaidCode);
      console.log('MermaidDiagram: Code length:', mermaidCode.length);

      // Clear any previous error state
      if (isMounted) {
        setError(null);
      }

      containerRef.current.innerHTML = '<div class="mermaid-loading">Rendering diagram...</div>'; // Clear and show loading

      try {
        console.log('MermaidDiagram: About to render with code:', mermaidCode);
        const { svg } = await (window as any).mermaid.render(id, mermaidCode);
        console.log('MermaidDiagram: Render successful, SVG length:', svg.length);
        console.log('MermaidDiagram: SVG preview:', svg.substring(0, 500) + '...');
        
        // Check if component is still mounted and container still exists
        if (isMounted && containerRef.current) {
          containerRef.current.innerHTML = svg;
          console.log('MermaidDiagram: SVG inserted into DOM');
        }
      } catch (error) {
        console.error('MermaidDiagram: Error rendering diagram:', error);
        if (isMounted) {
          setError(error instanceof Error ? error.message : 'Unknown error');
          if (containerRef.current) {
            containerRef.current.innerHTML = `<div class="mermaid-error">Error rendering diagram: ${error}</div>`;
          }
        }
      }
    };

    if (isMermaidReady && mermaidCode) {
      console.log('MermaidDiagram: Conditions met, checking if Mermaid is available...');
      // Check if Mermaid is actually available
      if (typeof (window as any).mermaid !== 'undefined') {
        console.log('MermaidDiagram: Mermaid is available, calling renderDiagram');
        renderDiagram();
      } else {
        console.log('MermaidDiagram: Mermaid not available yet, will retry...');
        // Retry after a short delay
        setTimeout(() => {
          if (isMounted && typeof (window as any).mermaid !== 'undefined') {
            console.log('MermaidDiagram: Retrying renderDiagram');
            renderDiagram();
          }
        }, 100);
      }
    } else {
      console.log('MermaidDiagram: Conditions not met', { isMermaidReady, hasMermaidCode: !!mermaidCode });
    }

    return () => {
      isMounted = false;
    };
  }, [id, mermaidCode, isMermaidReady]);

  if (!isMermaidReady) {
    return (
      <div className="mermaid-diagram w-full flex justify-center items-center">
        <div className="mermaid-loading">Loading diagram engine...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="mermaid-diagram w-full flex justify-center items-center">
        <div className="mermaid-error">Error: {error}</div>
      </div>
    );
  }

  return <div ref={containerRef} className="mermaid-diagram w-full flex justify-center items-center" />;
} 