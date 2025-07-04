#!/bin/bash

# FarLanders Documentation Local Preview Script
# This script builds the documentation locally and serves it on http://localhost:8080

set -e

echo "🏔️ FarLanders Documentation Preview"
echo "=================================="

# Clean previous build
echo "🧹 Cleaning previous builds..."
rm -rf public build/docs build/reports

# Build the project and generate documentation
echo "🔨 Building project and generating documentation..."
./gradlew clean build javadoc

# Try to run tests and generate coverage report
echo "🧪 Running tests..."
if ./gradlew test; then
    echo "✅ Tests completed successfully"
    # Try to generate coverage report if JaCoCo is available
    if ./gradlew jacocoTestReport 2>/dev/null; then
        echo "📊 Coverage report generated"
    else
        echo "⚠️  JaCoCo not available, skipping coverage report"
    fi
else
    echo "⚠️  Some tests failed, but continuing with documentation build..."
fi

# Create documentation structure (same as GitHub Actions)
echo "📚 Creating documentation structure..."
mkdir -p public
mkdir -p public/api
mkdir -p public/coverage  
mkdir -p public/tests
mkdir -p public/download

# Copy generated documentation
echo "📋 Copying generated files..."
cp -r build/docs/javadoc/* public/api/ 2>/dev/null || echo "⚠️  Javadoc not found, skipping..."
cp -r build/reports/jacoco/test/html/* public/coverage/ 2>/dev/null || echo "⚠️  Coverage report not found, skipping..."
cp -r build/reports/tests/test/* public/tests/ 2>/dev/null || echo "⚠️  Test reports not found, skipping..."
cp build/libs/*.jar public/download/ 2>/dev/null || echo "⚠️  JAR files not found, skipping..."
cp CHANGELOG.md public/ 2>/dev/null || echo "⚠️  CHANGELOG.md not found, skipping..."
cp README.md public/ 2>/dev/null || echo "📝 README.md not found, that's okay"

# Copy HTML documentation from docs/ folder
echo "📖 Copying HTML documentation..."
if [ -d "docs" ]; then
    # Copy all HTML files and assets from docs/ to public/
    cp docs/*.html public/ 2>/dev/null || echo "⚠️  HTML docs not found, skipping..."
    cp docs/*.css public/ 2>/dev/null || echo "⚠️  CSS files not found, skipping..."
    cp docs/*.png public/ 2>/dev/null || echo "⚠️  PNG files not found, skipping..."
    cp docs/*.jpg public/ 2>/dev/null || echo "⚠️  JPG files not found, skipping..."
    cp docs/*.gif public/ 2>/dev/null || echo "⚠️  GIF files not found, skipping..."
    echo "✅ HTML documentation copied successfully"
else
    echo "⚠️  docs/ folder not found, skipping HTML documentation..."
fi

# Copy favicon
echo "🎨 Adding favicon..."
# Try to copy from docs first (our new logo), then fallback to root
if [ -f "docs/logo.png" ]; then
    cp docs/logo.png public/favicon.png
    echo "✅ Favicon copied from docs/logo.png"
elif [ -f "logo.png" ]; then
    cp logo.png public/favicon.png
    echo "✅ Favicon copied from logo.png"
else
    echo "⚠️  logo.png not found, skipping favicon..."
fi

# Create index.html if it doesn't exist from docs/ (same as GitHub Actions)
echo "🏠 Creating index page..."
if [ ! -f "public/index.html" ]; then
    echo "📝 Creating fallback index.html..."
cat > public/index.html << 'EOF'
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>FarLanders Documentation</title>
    <link rel="icon" type="image/png" href="./favicon.png">
    <link rel="shortcut icon" type="image/png" href="./favicon.png">
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Orbitron:wght@400;700;900&family=Jersey+20:wght@400&display=swap');
        
        :root {
            --heading-font: 'Orbitron', 'Courier New', monospace;
            --body-font: 'Jersey 20', 'Monaco', 'Consolas', monospace;
        }
        
        body { 
            font-family: var(--body-font); 
            margin: 40px; 
            line-height: 1.6; 
            letter-spacing: 0.5px;
        }
        .container { max-width: 800px; margin: 0 auto; }
        .nav-card { background: #f8f9fa; padding: 20px; margin: 15px 0; border-radius: 8px; border: 1px solid #e9ecef; }
        .nav-card h3 { 
            margin-top: 0; 
            color: #495057; 
            font-family: var(--heading-font);
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 1px;
        }
        .nav-card a { color: #52357B; text-decoration: none; font-weight: 700; }
        .nav-card a:hover { text-decoration: underline; }
        .badge { background: #648DB3; color: white; padding: 4px 8px; border-radius: 4px; font-size: 12px; font-family: var(--heading-font); font-weight: 700; }
        .preview-badge { background: #5459AC; color: white; padding: 4px 8px; border-radius: 4px; font-size: 12px; font-family: var(--heading-font); font-weight: 700; }
        .header { text-align: center; margin-bottom: 40px; }
        .header h1 {
            font-family: var(--heading-font);
            font-weight: 900;
            text-transform: uppercase;
            letter-spacing: 2px;
            color: #52357B;
        }
        .version { color: #5459AC; }
        .alert { background: #B2D8CE; border: 1px solid #648DB3; color: #52357B; padding: 12px; border-radius: 6px; margin-bottom: 20px; }
        .main-docs { background: linear-gradient(135deg, #52357B 0%, #5459AC 100%); color: white; border: none; }
        .main-docs a { color: white; font-weight: 700; font-family: var(--heading-font); }
        .main-docs h3 { color: white; font-family: var(--heading-font); font-weight: 700; text-transform: uppercase; letter-spacing: 1px; }
        .main-docs p { color: rgba(255, 255, 255, 0.9); }
        .section-divider { border-top: 2px solid #B2D8CE; margin: 30px 0; padding-top: 20px; }
        h2 { 
            font-family: var(--heading-font);
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 1px;
            color: #52357B;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>FarLanders Documentation</h1>
            <p class="version">Version <span class="badge">1.1.1</span>
            <p>Enhanced Minecraft World Generation & Adventure Plugin</p>
        </div>
        
        <div class="nav-card main-docs">
            <h3>📖 Main Documentation</h3>
            <p>Complete user guide, features, and plugin documentation</p>
            <a href="./documentation-hub.html">Browse Documentation Hub →</a>
        </div>
        
        <div class="nav-card">
            <h3>🏠 Plugin Overview</h3>
            <p>Feature overview, commands, and getting started guide</p>
            <a href="./plugin-overview.html">Plugin Documentation →</a>
        </div>
        
        <div class="nav-card">
            <h3>📜 Story Design</h3>
            <p>Epic adventure storyline and progression system</p>
            <a href="./story-design.html">Story Documentation →</a>
        </div>
        
        <div class="nav-card">
            <h3>🚀 Implementation Roadmap</h3>
            <p>Development timeline and planned features</p>
            <a href="./roadmap.html">View Roadmap →</a>
        </div>

        <div class="section-divider">
            <h2>Developer Resources</h2>
        </div>
        
        <div class="nav-card">
            <h3>📚 API Documentation</h3>
            <p>Complete Javadoc API reference for developers</p>
            <a href="./api/">Browse API Documentation →</a>
        </div>
        
        <div class="nav-card">
            <h3>🧪 Test Results</h3>
            <p>Latest test execution results and reports</p>
            <a href="./tests/">View Test Results →</a>
        </div>
        
        <div class="nav-card">
            <h3>📊 Code Coverage</h3>
            <p>Test coverage analysis and metrics</p>
            <a href="./coverage/">View Coverage Report →</a>
        </div>
        
        <div class="nav-card">
            <h3>📥 Downloads</h3>
            <p>Latest plugin JAR file for server installation</p>
            <a href="./download/">Download Plugin →</a>
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
else
    echo "✅ Using existing index.html from docs/"
fi

# Copy CNAME for completeness
cp CNAME public/ 2>/dev/null || echo "📝 CNAME not found, that's okay for local preview"

echo "✅ Documentation built successfully!"
echo ""
echo "🚀 Starting local server..."
echo "📍 Open your browser to: http://localhost:8080"
echo "🛑 Press Ctrl+C to stop the server"
echo ""

# Check if Python 3 is available, otherwise use Python 2
if command -v python3 &> /dev/null; then
    cd public && python3 -m http.server 8080
elif command -v python &> /dev/null; then
    cd public && python -m SimpleHTTPServer 8080
else
    echo "❌ Python not found! Please install Python to run the local server."
    echo "📁 Documentation files are ready in the 'public' directory"
    echo "💡 You can also use any other static file server to serve the 'public' directory"
fi
