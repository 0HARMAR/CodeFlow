$root = Split-Path -Parent $MyInvocation.MyCommand.Path

# 微服务架构：gateway(8080) + user-service(8081) + content-service(8082) + agent-service(8083) + 前端(80)

$gateway  = Start-Process -NoNewWindow -PassThru -WorkingDirectory "$root/gateway"              -FilePath "cmd" -ArgumentList "/c", "mvn compile spring-boot:run"
$user     = Start-Process -NoNewWindow -PassThru -WorkingDirectory "$root/backend/user-service" -FilePath "cmd" -ArgumentList "/c", "mvn compile spring-boot:run"
$content  = Start-Process -NoNewWindow -PassThru -WorkingDirectory "$root/backend/CodeFlow"     -FilePath "cmd" -ArgumentList "/c", "mvnw.cmd compile spring-boot:run"
$agent    = Start-Process -NoNewWindow -PassThru -WorkingDirectory "$root/backend/agent-service" -FilePath "cmd" -ArgumentList "/c", "mvn compile spring-boot:run"
$frontend = Start-Process -NoNewWindow -PassThru -WorkingDirectory "$root/frontend/code-flow"   -FilePath "cmd" -ArgumentList "/c", "npm run serve"

try {
    Wait-Process -InputObject $gateway, $user, $content, $agent, $frontend
} finally {
    foreach ($p in @($gateway, $user, $content, $agent, $frontend)) {
        if (!$p.HasExited) { Stop-Process -Id $p.Id -Force }
    }
}
