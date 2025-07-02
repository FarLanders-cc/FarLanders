#!/bin/bash

# FarLanders Documentation Quick Preview Script
# This script builds documentation quickly without running tests

set -e

echo "⚡ FarLanders Documentation Quick Preview"
echo "======================================="

# Clean previous build
echo "🧹 Cleaning previous builds..."
rm -rf public

# Build just the documentation (skip tests for speed)
echo "🔨 Building documentation (skipping tests for speed)..."
./gradlew javadoc

# Create documentation structure
echo "📚 Creating documentation structure..."
mkdir -p public
mkdir -p public/api
mkdir -p public/coverage  
mkdir -p public/tests
mkdir -p public/download

# Copy available documentation
echo "📋 Copying available files..."
cp -r build/docs/javadoc/* public/api/ 2>/dev/null || echo "⚠️  Javadoc not found"

# Copy existing reports if available
if [ -d "build/reports/jacoco/test/html" ]; then
    cp -r build/reports/jacoco/test/html/* public/coverage/
else
    echo "<h1>Coverage Report</h1><p>Run full preview to generate coverage report</p>" > public/coverage/index.html
fi

if [ -d "build/reports/tests/test" ]; then
    cp -r build/reports/tests/test/* public/tests/
else
    echo "<h1>Test Results</h1><p>Run full preview to generate test results</p>" > public/tests/index.html
fi

# Copy other files
cp CHANGELOG.md public/ 2>/dev/null || echo "⚠️  CHANGELOG.md not found"
cp README.md public/ 2>/dev/null || echo "📝 README.md not found"

# Copy favicon
echo "🎨 Adding favicon..."
cp logo.png public/favicon.png 2>/dev/null || echo "⚠️  logo.png not found, skipping favicon..."

# Create index.html
echo "🏠 Creating index page..."
cat > public/index.html << 'EOF'
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>FarLanders Documentation - Quick Preview</title>
    <link rel="icon" type="image/png" href="./favicon.png">
    <link rel="shortcut icon" type="image/png" href="./favicon.png">
    <style>
        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif; margin: 40px; line-height: 1.6; }
        .container { max-width: 800px; margin: 0 auto; }
        .nav-card { background: #f8f9fa; padding: 20px; margin: 15px 0; border-radius: 8px; border: 1px solid #e9ecef; }
        .nav-card h3 { margin-top: 0; color: #495057; }
        .nav-card a { color: #007bff; text-decoration: none; font-weight: 500; }
        .nav-card a:hover { text-decoration: underline; }
        .badge { background: #28a745; color: white; padding: 4px 8px; border-radius: 4px; font-size: 12px; }
        .preview-badge { background: #17a2b8; color: white; padding: 4px 8px; border-radius: 4px; font-size: 12px; }
        .header { text-align: center; margin-bottom: 40px; }
        .version { color: #6c757d; }
        .alert { background: #d1ecf1; border: 1px solid #bee5eb; color: #0c5460; padding: 12px; border-radius: 6px; margin-bottom: 20px; }
    </style>
</head>
<body>
    <div class="container">
        
        <div class="header">
            <h1>🏔️ FarLanders Documentation</h1>
            <p class="version">Version <span class="badge">1.0.1</span>
            <p>Minecraft plugin for enhanced world generation</p>
        </div>
        
        <div class="nav-card">
            <h3>📚 API Documentation</h3>
            <p>Complete Javadoc API reference for developers</p>
            <a href="./api/">Browse API Documentation →</a>
        </div>
        
        <div class="nav-card">
            <h3>🧪 Test Results</h3>
            <p>Test execution results (run full preview for latest)</p>
            <a href="./tests/">View Test Results →</a>
        </div>
        
        <div class="nav-card">
            <h3>📊 Code Coverage</h3>
            <p>Test coverage analysis (run full preview for latest)</p>
            <a href="./coverage/">View Coverage Report →</a>
        </div>
        
        <div class="nav-card">
            <h3>📝 Changelog</h3>
            <p>Version history and release notes</p>
            <a href="./changelog.html">View Changelog →</a>
        </div>
        
        <div class="nav-card">
            <h3>🔗 Links</h3>
            <p>
                <a href="https://github.com/YourOrganization/FarLanders">GitHub Repository</a> •
                <a href="https://farlanders.cc">Main Website</a>
            </p>
        </div>
    </div>
</body>
</html>
EOF

echo "✅ Quick documentation preview built!"
echo ""
echo "🚀 Starting local server..."
echo "📍 Open your browser to: http://localhost:8080"
echo "🛑 Press Ctrl+C to stop the server"
echo ""

# Start server
if command -v python3 &> /dev/null; then
    cd public && python3 -m http.server 8080
elif command -v python &> /dev/null; then
    cd public && python -m SimpleHTTPServer 8080
else
    echo "❌ Python not found! Please install Python to run the local server."
    echo "📁 Documentation files are ready in the 'public' directory"
fi
