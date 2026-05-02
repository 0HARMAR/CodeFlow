import { ref, onUnmounted } from 'vue'

export function useDraggable() {
  const panelStyle = ref({})
  let dragging = false
  let startX = 0
  let startY = 0
  let startLeft = 0
  let startTop = 0

  function onMouseMove(e) {
    if (!dragging) return
    panelStyle.value = {
      left: `${startLeft + e.clientX - startX}px`,
      top: `${startTop + e.clientY - startY}px`,
      bottom: 'auto',
      right: 'auto',
    }
  }

  function onMouseUp() {
    dragging = false
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

  function resetPosition() {
    dragging = false
    document.removeEventListener('mousemove', onMouseMove)
    document.removeEventListener('mouseup', onMouseUp)
    panelStyle.value = {}
  }

  onUnmounted(() => {
    document.removeEventListener('mousemove', onMouseMove)
    document.removeEventListener('mouseup', onMouseUp)
  })

  return { panelStyle, onHeaderMouseDown, resetPosition }
}
