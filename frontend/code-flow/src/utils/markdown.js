/**
 * Parse text with markdown links [text](url) into segments for Vue rendering.
 * Returns an array of { type: 'text' | 'link', content, url? }.
 */
export function parseMarkdownLinks(text) {
  if (!text) return []
  const segments = []
  const regex = /\[([^\]]+)\]\((\/[^)]+)\)/g
  let lastIndex = 0
  let match
  while ((match = regex.exec(text)) !== null) {
    if (match.index > lastIndex) {
      segments.push({ type: 'text', content: text.slice(lastIndex, match.index) })
    }
    segments.push({ type: 'link', content: match[1], url: match[2] })
    lastIndex = match.index + match[0].length
  }
  if (lastIndex < text.length) {
    segments.push({ type: 'text', content: text.slice(lastIndex) })
  }
  return segments
}
