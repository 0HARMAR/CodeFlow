import { onMounted, onUnmounted, ref,} from 'vue'
import axios from "@/utils/axios";
export function useArticleReadTracker(articleIdRef, contentRef, onSessionEnd) {
    const HEARTBEAT_INTERVAL = 5
    const INACTIVE_TIMEOUT = 10

    const isVisible = ref(true)
    const hasInteracted = ref(false)
    const lastActiveAt = ref(Date.now())
    const timer = ref(null)
    const pendingSeconds = ref(0)

    const isInReadingSession = ref(false)
    const sessionStartAt = ref(0)
    const sessionSeconds = ref(0)
    const lastSessionSeconds = ref(0)


    const handleVisibility = () => {
        isVisible.value = document.visibilityState === 'visible'
    }

    const markActive = () => {
        hasInteracted.value = true
        lastActiveAt.value = Date.now()
    }

    const isInContent = () => {
        if (!contentRef.value) return false
        const rect = contentRef.value.getBoundingClientRect()
        return rect.top < window.innerHeight * 0.6 && rect.bottom > 0
    }

    const isReading = () => {
        if (!articleIdRef()) return false

        const inactive =
            Date.now() - lastActiveAt.value > INACTIVE_TIMEOUT * 1000

        return (
            isVisible.value &&
            hasInteracted.value &&
            !inactive &&
            isInContent()
        )
    }

    const report = () => {
        if (pendingSeconds.value === 0) return
        const articleId = articleIdRef()
        if (!articleId) return

        axios.post('/api/read/heartbeat', {
            articleId,
            delta: pendingSeconds.value
        }).catch(err => {
            console.error('Heartbeat failed', err)
        })

        pendingSeconds.value = 0
    }

    const start = () => {
        if (timer.value) return
        timer.value = setInterval(() => {
            const reading = isReading()

            // 1️⃣ 进入一次阅读
            if (reading && !isInReadingSession.value) {
                isInReadingSession.value = true
                sessionStartAt.value = Date.now()
                sessionSeconds.value = 0
            }

            // 2️⃣ 正在阅读
            if (reading) {
                pendingSeconds.value += HEARTBEAT_INTERVAL
                sessionSeconds.value += HEARTBEAT_INTERVAL
            }

            // 3️⃣ 退出一次阅读
            if (!reading && isInReadingSession.value) {
                endSession()
            }

            // 4️⃣ 心跳上报（累计）
            if (pendingSeconds.value > 0) {
                report()
            }
        }, HEARTBEAT_INTERVAL * 1000)
    }

    const endSession = () => {
        if (!isInReadingSession.value) return

        // 保存当前阅读时长
        const session = sessionSeconds.value
        lastSessionSeconds.value = session
        console.log('一次阅读时长：', session)

        // 通知外部
        if (onSessionEnd && typeof onSessionEnd === 'function') {
            onSessionEnd(session)
        }

        isInReadingSession.value = false
        sessionStartAt.value = 0
        sessionSeconds.value = 0
    }

    const stop = () => {
        clearInterval(timer.value)
        timer.value = null
    }

    onMounted(() => {
        document.addEventListener('visibilitychange', handleVisibility)
        window.addEventListener('scroll', markActive, { passive: true })
        window.addEventListener('mousemove', markActive)
        window.addEventListener('keydown', markActive)
        start()
    })

    onUnmounted(() => {
        endSession()
        report()
        stop()
        document.removeEventListener('visibilitychange', handleVisibility)
        window.removeEventListener('scroll', markActive)
        window.removeEventListener('mousemove', markActive)
        window.removeEventListener('keydown', markActive)
    })

    return {
        lastSessionSeconds
    }
}
