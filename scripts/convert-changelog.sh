#!/usr/bin/env bash
set -euo pipefail

# convert-changelog.sh
# Usage: ./scripts/convert-changelog.sh [INPUT.md] [OUTPUT.html]
# Default: INPUT=CHANGELOG.md, OUTPUT=docs/changelog.html

IN="${1:-CHANGELOG.md}"
OUT="${2:-docs/changelog.html}"

mkdir -p "$(dirname "$OUT")"

echo "Converting '$IN' -> '$OUT'"

if [ ! -f "$IN" ]; then
  echo "Error: input file '$IN' not found." >&2
  exit 2
fi

# 1) Prefer pandoc if available
if command -v pandoc >/dev/null 2>&1; then
  echo "Using pandoc to generate HTML..."
  pandoc --from=markdown --to=html5 --standalone --css=docs/style.css -o "$OUT" "$IN"
  echo "Written: $OUT"
  exit 0
fi

# 2) Try python3 + markdown module if available
if command -v python3 >/dev/null 2>&1; then
  if python3 - <<'PY' 2>/dev/null
try:
    import markdown
except Exception:
    raise SystemExit(2)
PY
  then
    echo "Using python-markdown module to generate HTML..."
    python3 - "$IN" <<'PY' > "$OUT"
import sys,markdown
md=open(sys.argv[1],encoding='utf-8').read()
html_body=markdown.markdown(md,extensions=['fenced_code','tables','toc'])
template='''<!doctype html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width,initial-scale=1">
<title>Changelog</title>
<link rel="stylesheet" href="/style.css">
</head>
<body>
<div class="container changelog">
<h1>Changelog</h1>
%s
</div>
</body>
</html>'''
print(template % html_body)
PY
    echo "Written: $OUT"
    exit 0
  fi
fi

# 3) Fallback: pure-Python minimal converter (no external deps)
echo "Falling back to built-in markdown parser (limited but robust)."
python3 - "$IN" <<'PY' > "$OUT"
import re,sys,html
src=open(sys.argv[1],encoding='utf-8').read().splitlines()
out=[]
in_code=False
in_list=False
for line in src:
    if line.startswith('```'):
        if in_code:
            out.append('</code></pre>')
            in_code=False
        else:
            out.append('<pre><code>')
            in_code=True
        continue
    if in_code:
        out.append(html.escape(line))
        continue
    m=re.match(r'^(#{1,6})\s+(.*)', line)
    if m:
        level=len(m.group(1))
        raw = m.group(2)
        # create a slug that matches markdown-style anchors used in the changelog links
        slug = raw.replace('[','').replace(']','').replace('.','')
        slug = re.sub(r"\s+", '-', slug)
        out.append(f"<h{level} id=\"{slug}\">{html.escape(raw)}</h{level}>")
        continue
    m=re.match(r'^\s*[-*+]\s+(.*)', line)
    if m:
        if not in_list:
            in_list=True
            out.append('<ul>')
        # render inline markdown inside list items (links, code, bold, italics)
        item = m.group(1)
        item = re.sub(r'`([^`]+)`', lambda mm: f"<code>{html.escape(mm.group(1))}</code>", item)
        item = re.sub(r'\*\*(.+?)\*\*', lambda mm: f"<strong>{html.escape(mm.group(1))}</strong>", item)
        item = re.sub(r'\*(.+?)\*', lambda mm: f"<em>{html.escape(mm.group(1))}</em>", item)
        item = re.sub(r'\[([^\]]+)\]\(([^)]+)\)', lambda mm: f"<a href=\"{html.escape(mm.group(2),quote=True)}\">{html.escape(mm.group(1))}</a>", item)
        out.append(f"<li>{item}</li>")
        continue
    else:
        if in_list:
            out.append('</ul>')
            in_list=False
    if re.match(r'^\s*[-*_]{3,}\s*$', line):
        out.append('<hr/>')
        continue
    if line.strip()=='':
        out.append('')
        continue
    # inline code
    line=re.sub(r'`([^`]+)`', lambda m: f"<code>{html.escape(m.group(1))}</code>", line)
    # bold **text**
    line=re.sub(r'\*\*(.+?)\*\*', r'<strong>\1</strong>', line)
    # italics *text*
    line=re.sub(r'\*(.+?)\*', r'<em>\1</em>', line)
    # links [text](url)
    line=re.sub(r'\[([^\]]+)\]\(([^)]+)\)', r'<a href="\2">\1</a>', line)
    out.append(f"<p>{line}</p>")
if in_list:
    out.append('</ul>')
html_doc='''<!doctype html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width,initial-scale=1">
<title>Changelog</title>
<link rel="stylesheet" href="/style.css">
</head>
<body>
<div class="container changelog">
<h1>Changelog</h1>
{body}
</div>
</body>
</html>'''.format(body="\n".join(out))
print(html_doc)
PY
echo "Written: $OUT (fallback)"

exit 0
