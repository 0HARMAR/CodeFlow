package com.example.codeflow.agent.tools;

import com.example.codeflow.agent.Tool;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class GetUserProfileTool implements Tool {

    private final RestClient userClient;

    public GetUserProfileTool(@Qualifier("userClient") RestClient userClient) {
        this.userClient = userClient;
    }

    @Override
    public String getName() { return "get_user_profile"; }

    @Override
    public String getDescription() { return "获取用户信息"; }

    @Override
    public Map<String, Object> getParametersSchema() {
        return Map.of(
                "type", "object",
                "properties", Map.of(
                        "userId", Map.of("type", "integer", "description", "用户ID")
                ),
                "required", List.of("userId")
        );
    }

    @Override
    public String execute(Map<String, Object> arguments) {
        Long userId = ToolUtils.toLong(arguments.get("userId"));
        try {
            Map<String, Object> user = userClient.get()
                    .uri("/api/users/{id}", userId)
                    .retrieve()
                    .body(Map.class);
            if (user == null || user.isEmpty()) {
                return ToolUtils.toJson(Map.of("error", "用户不存在", "userId", userId));
            }
            Map<String, Object> out = new LinkedHashMap<>();
            out.put("id", user.get("id"));
            out.put("username", user.get("username"));
            out.put("email", user.get("email"));
            out.put("avatar", user.get("avatar"));
            return ToolUtils.toJson(out);
        } catch (Exception e) {
            return ToolUtils.toJson(Map.of("error", "获取用户失败: " + e.getMessage()));
        }
    }
}
