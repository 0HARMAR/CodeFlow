$root = Split-Path -Parent $MyInvocation.MyCommand.Path

$backend = Start-Process -NoNewWindow -PassThru -WorkingDirectory "$root/backend/CodeFlow" -FilePath "cmd" -ArgumentList "/c", "mvnw.cmd compile spring-boot:run"
$frontend = Start-Process -NoNewWindow -PassThru -WorkingDirectory "$root/frontend/code-flow" -FilePath "cmd" -ArgumentList "/c", "npm run serve"

try {
    Wait-Process -InputObject $backend, $frontend
} finally {
    if (!$backend.HasExited) { Stop-Process -Id $backend.Id -Force }
    if (!$frontend.HasExited) { Stop-Process -Id $frontend.Id -Force }
}
