package com.example.codeflow.agent.tools;

import com.example.codeflow.agent.Tool;
import com.example.codeflow.model.User;
import com.example.codeflow.service.UserService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class GetUserProfileTool implements Tool {

    private final UserService userService;

    public GetUserProfileTool(UserService userService) {
        this.userService = userService;
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
        User user = userService.findById(userId);
        if (user == null) {
            return ToolUtils.toJson(Map.of("error", "用户不存在", "userId", userId));
        }
        return ToolUtils.toJson(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "bio", user.getBio() != null ? user.getBio() : "",
                "createdAt", user.getCreatedAt() != null ? user.getCreatedAt().toString() : ""
        ));
    }
}