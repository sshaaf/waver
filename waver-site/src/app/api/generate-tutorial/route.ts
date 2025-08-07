import { NextRequest, NextResponse } from 'next/server';
import { v4 as uuidv4 } from 'uuid';

export async function POST(request: NextRequest) {
  try {
    const { repositoryUrl } = await request.json();

    if (!repositoryUrl) {
      return NextResponse.json(
        { message: 'Repository URL is required' },
        { status: 400 }
      );
    }

    // Validate GitHub URL format
    const githubUrlPattern = /^https:\/\/github\.com\/[^\/]+\/[^\/]+$/;
    if (!githubUrlPattern.test(repositoryUrl)) {
      return NextResponse.json(
        { message: 'Please provide a valid GitHub repository URL' },
        { status: 400 }
      );
    }

    // Get cloud event service URL from environment
    const cloudEventServiceUrl = process.env.CLOUD_EVENT_SERVICE_URL || 'http://localhost:8080';
    
    // Generate a unique event ID
    const eventId = uuidv4();
    
    // Prepare cloud event payload
    const cloudEventPayload = {
      sourceUrl: repositoryUrl
    };

    // Make cloud event call to the processing service
    console.log('Sending cloud event to:', `${cloudEventServiceUrl}/requests`);
    console.log('Event ID:', eventId);
    console.log('Repository URL:', repositoryUrl);

    const response = await fetch(`${cloudEventServiceUrl}/requests`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'ce-specversion': '1.0',
        'ce-type': 'dev.shaaf.waver.processing.request',
        'ce-source': '/waver-site-frontend',
        'ce-id': eventId,
      },
      body: JSON.stringify(cloudEventPayload),
    });

    if (!response.ok) {
      console.error('Cloud event service error:', response.status, response.statusText);
      throw new Error(`Cloud event service responded with status: ${response.status}`);
    }

    console.log('Cloud event sent successfully');

    // Return success response
    return NextResponse.json(
      { 
        message: 'Tutorial generation scheduled successfully',
        repositoryUrl,
        eventId
      },
      { status: 200 }
    );

  } catch (error) {
    console.error('Error generating tutorial:', error);
    return NextResponse.json(
      { message: 'Failed to schedule tutorial generation. Please try again.' },
      { status: 500 }
    );
  }
} 