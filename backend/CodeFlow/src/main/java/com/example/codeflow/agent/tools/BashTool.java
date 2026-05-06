package com.example.codeflow.agent.tools;

import com.example.codeflow.agent.Tool;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

@Component
public class BashTool implements Tool {

    private static final long TIMEOUT_SECONDS = 30;

    @Override
    public String getName() { return "run_bash"; }

    @Override
    public String getDescription() {
        return "在服务器上执行一个shell命令并返回输出。用于精确的文本处理、统计、计算等任务。可选的 stdin 参数会将文本通过标准输入传给命令，适合配合 wc、grep 等命令使用。";
    }

    @Override
    public Map<String, Object> getParametersSchema() {
        return Map.of(
                "type", "object",
                "properties", Map.of(
                        "command", Map.of("type", "string", "description", "要执行的shell命令，例如 'wc -m' 统计字符数"),
                        "stdin", Map.of("type", "string", "description", "可选，通过标准输入传递给命令的文本内容")
                ),
                "required", List.of("command")
        );
    }

    @Override
    public String execute(Map<String, Object> arguments) {
        String command = (String) arguments.get("command");
        if (command == null || command.isBlank()) {
            return ToolUtils.toJson(Map.of("error", "command 参数不能为空"));
        }

        String stdin = (String) arguments.get("stdin");

        // handle wc natively — wc doesn't exist on Windows
        String wcResult = tryWc(command, stdin);
        if (wcResult != null) {
            return wcResult;
        }

        try {
            ProcessBuilder pb;
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                pb = new ProcessBuilder("cmd.exe", "/c", command);
            } else {
                pb = new ProcessBuilder("bash", "-c", command);
            }
            pb.redirectErrorStream(true);

            Process process = pb.start();

            if (stdin != null && !stdin.isEmpty()) {
                try (OutputStream os2 = process.getOutputStream()) {
                    os2.write(stdin.getBytes(StandardCharsets.UTF_8));
                    os2.flush();
                }
            }

            boolean finished = process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);

            if (!finished) {
                process.destroyForcibly();
                return ToolUtils.toJson(Map.of(
                        "error", "命令执行超时（" + TIMEOUT_SECONDS + "秒）",
                        "command", command
                ));
            }

            String output = readStream(process.getInputStream());
            int exitCode = process.exitValue();

            return ToolUtils.toJson(Map.of(
                    "exitCode", exitCode,
                    "output", output,
                    "command", command
            ));
        } catch (Exception e) {
            return ToolUtils.toJson(Map.of(
                    "error", "命令执行失败: " + e.getMessage(),
                    "command", command
            ));
        }
    }

    // pure-Java wc for cross-platform reliability (Windows cmd.exe lacks wc)
    private String tryWc(String command, String stdin) {
        String trimmed = command.trim();
        boolean chars = trimmed.matches("^wc\\s+-m(\\s|$).*");
        boolean words = trimmed.matches("^wc\\s+-w(\\s|$).*");
        boolean lines = trimmed.matches("^wc\\s+-l(\\s|$).*");
        boolean bytes = trimmed.matches("^wc\\s+-c(\\s|$).*");
        boolean all   = trimmed.matches("^wc(\\s|$).*");

        if (!chars && !words && !lines && !bytes && !all) return null;
        if (stdin == null) return null;

        int charCount = stdin.length();
        int wordCount = stdin.trim().isEmpty() ? 0 : stdin.trim().split("\\s+").length;
        int lineCount = stdin.isEmpty() ? 0 : stdin.split("\\r?\\n", -1).length;
        int byteCount = stdin.getBytes(StandardCharsets.UTF_8).length;

        StringBuilder sb = new StringBuilder();
        if (all) {
            sb.append(String.format("%7d %7d %7d %7d", lineCount, wordCount, byteCount, charCount));
        } else {
            if (lines) sb.append(lineCount);
            else if (words) sb.append(wordCount);
            else if (chars) sb.append(charCount);
            else sb.append(byteCount);
        }

        return ToolUtils.toJson(Map.of(
                "exitCode", 0,
                "output", sb.toString(),
                "command", command
        ));
    }

    private String readStream(InputStream is) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int n;
        while ((n = is.read(data)) != -1) {
            buffer.write(data, 0, n);
        }
        String result = buffer.toString(StandardCharsets.UTF_8).trim();
        if (result.length() > 10000) {
            result = result.substring(0, 10000) + "\n... (输出已截断)";
        }
        return result;
    }
}
