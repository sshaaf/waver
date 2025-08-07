import { NextRequest, NextResponse } from 'next/server';

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

    // TODO: Here you would integrate with your backend service
    // For now, we'll simulate the API call
    console.log('Scheduling tutorial generation for:', repositoryUrl);

    // Simulate API call to backend
    // const response = await fetch('YOUR_BACKEND_URL/api/generate-tutorial', {
    //   method: 'POST',
    //   headers: {
    //     'Content-Type': 'application/json',
    //   },
    //   body: JSON.stringify({ repositoryUrl }),
    // });

    // if (!response.ok) {
    //   throw new Error('Backend service unavailable');
    // }

    // Return success response
    return NextResponse.json(
      { 
        message: 'Tutorial generation scheduled successfully',
        repositoryUrl 
      },
      { status: 200 }
    );

  } catch (error) {
    console.error('Error generating tutorial:', error);
    return NextResponse.json(
      { message: 'Internal server error' },
      { status: 500 }
    );
  }
} 