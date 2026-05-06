import { ref, onUnmounted } from 'vue'

export function useDraggable() {
  const panelStyle = ref({})
  let dragging = false
  let resizing = false
  let resizeDir = ''
  let startX = 0
  let startY = 0
  let startLeft = 0
  let startTop = 0
  let startWidth = 0
  let startHeight = 0

  const MIN_W = 320
  const MAX_W = 700
  const MIN_H = 360
  const MAX_H = 700

  function onMouseMove(e) {
    if (dragging) {
      panelStyle.value = {
        ...panelStyle.value,
        left: `${startLeft + e.clientX - startX}px`,
        top: `${startTop + e.clientY - startY}px`,
        bottom: 'auto',
        right: 'auto',
      }
      return
    }
    if (resizing) {
      const dx = e.clientX - startX
      const dy = e.clientY - startY
      const next = { ...panelStyle.value }

      if (resizeDir.includes('e')) {
        next.width = `${clamp(startWidth + dx, MIN_W, MAX_W)}px`
      }
      if (resizeDir.includes('s')) {
        next.height = `${clamp(startHeight + dy, MIN_H, MAX_H)}px`
      }
      if (resizeDir.includes('w')) {
        const w = clamp(startWidth - dx, MIN_W, MAX_W)
        next.width = `${w}px`
        next.left = `${startLeft + startWidth - w}px`
        next.right = 'auto'
      }
      if (resizeDir.includes('n')) {
        const h = clamp(startHeight - dy, MIN_H, MAX_H)
        next.height = `${h}px`
        next.top = `${startTop + startHeight - h}px`
        next.bottom = 'auto'
      }

      panelStyle.value = next
    }
  }

  function onMouseUp() {
    dragging = false
    resizing = false
    document.removeEventListener('mousemove', onMouseMove)
    document.removeEventListener('mouseup', onMouseUp)
  }

  function onHeaderMouseDown(e) {
    const panel = e.currentTarget.parentElement
    if (!panel) return
    dragging = true
    startX = e.clientX
    startY = e.clientY
    const rect = panel.getBoundingClientRect()
    const parentRect = panel.offsetParent
      ? panel.offsetParent.getBoundingClientRect()
      : { left: 0, top: 0 }
    startLeft = rect.left - parentRect.left
    startTop = rect.top - parentRect.top
    document.addEventListener('mousemove', onMouseMove)
    document.addEventListener('mouseup', onMouseUp)
  }

  function onResizeMouseDown(e, dir) {
    e.preventDefault()
    e.stopPropagation()
    const panel = e.currentTarget.parentElement
    if (!panel) return
    resizing = true
    resizeDir = dir
    startX = e.clientX
    startY = e.clientY
    const rect = panel.getBoundingClientRect()
    const parentRect = panel.offsetParent
      ? panel.offsetParent.getBoundingClientRect()
      : { left: 0, top: 0 }
    startLeft = rect.left - parentRect.left
    startTop = rect.top - parentRect.top
    startWidth = rect.width
    startHeight = rect.height
    document.addEventListener('mousemove', onMouseMove)
    document.addEventListener('mouseup', onMouseUp)
  }

  function resetPosition() {
    dragging = false
    resizing = false
    document.removeEventListener('mousemove', onMouseMove)
    document.removeEventListener('mouseup', onMouseUp)
    panelStyle.value = {}
  }

  onUnmounted(() => {
    document.removeEventListener('mousemove', onMouseMove)
    document.removeEventListener('mouseup', onMouseUp)
  })

  function clamp(v, min, max) {
    return Math.min(Math.max(v, min), max)
  }

  return { panelStyle, onHeaderMouseDown, onResizeMouseDown, resetPosition }
}
