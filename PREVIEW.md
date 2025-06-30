# 📖 Local Documentation Preview

This directory contains scripts to preview your FarLanders documentation locally before deploying to GitHub Pages.

## 🚀 Quick Start

### Option 1: Full Preview (Recommended)

Builds everything including tests, coverage, and documentation:

```bash
./preview-docs.sh
```

This will:

- ✅ Run all tests
- 📊 Generate coverage reports
- 📚 Generate Javadoc
- 🏗️ Build the JAR file
- 🌐 Start local server at http://localhost:8080

### Option 2: Quick Preview (Faster)

Just builds documentation without running tests:

```bash
./quick-preview.sh
```

This will:

- 📚 Generate Javadoc only
- 🌐 Start local server at http://localhost:8080
- ⚡ Much faster for quick previews

## 🔧 Manual Preview

If you prefer to use your own server or build process:

1. **Build the documentation:**

   ```bash
   ./gradlew javadoc test jacocoTestReport build
   ```

2. **Create the public directory structure:**

   ```bash
   mkdir -p public/{api,coverage,tests,download}
   ```

3. **Copy files:**

   ```bash
   cp -r build/docs/javadoc/* public/api/
   cp -r build/reports/jacoco/test/html/* public/coverage/
   cp -r build/reports/tests/test/* public/tests/
   cp build/libs/*.jar public/download/
   cp CHANGELOG.md public/
   ```

4. **Serve with your preferred method:**

   ```bash
   # Using Python 3
   cd public && python3 -m http.server 8080

   # Using Node.js (if you have serve installed)
   cd public && npx serve -p 8080

   # Using PHP
   cd public && php -S localhost:8080
   ```

## 🛠️ Alternative Servers

If Python isn't available, you can use:

- **Node.js:** `npm install -g serve && serve public -p 8080`
- **PHP:** `php -S localhost:8080 -t public`
- **Live Server (VS Code):** Install Live Server extension and right-click on `public/index.html`

## 📂 Generated Structure

The preview scripts create this structure:

```
public/
├── index.html          # Main documentation page
├── api/                # Javadoc API documentation
├── coverage/           # Test coverage reports
├── tests/              # Test execution results
├── download/           # Built JAR files
├── CHANGELOG.md        # Version history
├── README.md           # Project README
└── CNAME               # Custom domain configuration
```

## 🌐 Live Preview vs Production

- **Local Preview:** Shows exactly what will be deployed
- **Production:** Available at https://www.farlanders.cc after GitHub Actions deployment
- **Custom Domain:** Configured via CNAME file and Cloudflare DNS

## 🔄 Auto-refresh

For automatic browser refresh during development, consider using:

- **Browser-sync:** `npm install -g browser-sync && browser-sync start --server public --port 8080 --files "public/**/*"`
- **Live Server extension** in VS Code
- **File watchers** in your IDE

## 🐛 Troubleshooting

### Port 8080 is busy

```bash
# Try a different port
cd public && python3 -m http.server 8081
```

### Permission denied

```bash
chmod +x preview-docs.sh quick-preview.sh
```

### Python not found

Install Python 3 or use an alternative server method listed above.

### Tests failing

Use `quick-preview.sh` to preview just the documentation without running tests.

## 📝 Notes

- The `public/` directory is ignored by Git (see `.gitignore`)
- Preview scripts mimic the exact GitHub Actions workflow
- Local preview includes a "LOCAL PREVIEW" badge to distinguish from production
- CNAME file is included for completeness but not needed for local preview
