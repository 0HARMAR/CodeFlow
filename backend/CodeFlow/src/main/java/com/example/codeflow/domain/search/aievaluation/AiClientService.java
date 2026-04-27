package com.example.codeflow.domain.search.aievaluation;

import com.example.codeflow.model.Article;
import com.example.codeflow.repository.ArticleRepository;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class AiClientService {

    private RestClient restClient;

    private ArticleRepository articleRepository;

    public AiClientService(ArticleRepository articleRepository) {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:3000")
                .build();
        this.articleRepository = articleRepository;
    }

    private AiEvaluation aiEvaluation = new AiEvaluation();

    public String chat(List<Message> messages) {

        ChatRequest request = new ChatRequest(messages);

        ChatResponse response = restClient.post()
                .uri("/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(ChatResponse.class);

        return response != null ? response.getReply() : null;
    }

    public void keywordContentMatch(String keyword) {
        List<Article> articles = articleRepository.findAll();
        for (Article article : articles) {
            if (aiEvaluation.getKeywordContentMatch().containsKey(article.getId().intValue())) continue;
            String content = article.getContent();
            List<Message> messages = List.of(
                    new Message("system", "你是一个用于搜索系统的评测程序，根据用户给的关键词，给出相关性评分。"),
                    new Message("user", "请你只输出一个 JSON 对象，且必须是合法 JSON。\n" +
                            "不要输出任何解释、注释或多余文本。\n" +
                            "\n" +
                            "JSON 格式如下：\n" +
                            "{\n" +
                            "  \"score\": 0 | 1 | 2 | 3 | 4\n" +
                            "}\n" +
                            "\n" +
                            "评分含义：\n" +
                            "0 = 完全不相关\n" +
                            "1 = 弱相关\n" +
                            "2 = 一般\n" +
                            "3 = 强相关\n" +
                            "4 = 极强相关\n" +
                            "\n" +
                            "我的关键词是:\n" +
                            "java\n\n" +
                            "现在请对下面内容进行评分：\n" +
                            content)
            );
            String reply = chat(messages);
            aiEvaluation.getKeywordContentMatch().put(Math.toIntExact(article.getId()), parseAiJson(reply));
        }
    }

    public void titleContentMatch(String keyword) {
        List<Article> articles = articleRepository.findAll();
        for (Article article : articles) {
            if (aiEvaluation.getTitleContentMatch().containsKey(article.getId().intValue())) continue;
            String title = article.getTitle();
            String content = article.getContent();
            List<Message> messages = List.of(
                    new Message("system", "你是一个用于搜索系统的评测程序，根据用户给的文章标题与内容，给出相关性评分。"),
                    new Message("user", "请你只输出一个 JSON 对象，且必须是合法 JSON。\n" +
                            "不要输出任何解释、注释或多余文本。\n" +
                            "\n" +
                            "JSON 格式如下：\n" +
                            "{\n" +
                            "  \"score\": 0 | 1 | 2 | 3 | 4\n" +
                            "}\n" +
                            "\n" +
                            "评分含义：\n" +
                            "0 = 完全不相关\n" +
                            "1 = 弱相关\n" +
                            "2 = 一般\n" +
                            "3 = 强相关\n" +
                            "4 = 极强相关\n" +
                            "\n" +
                            "文章标题是: \n" +
                            title +"\n\n" +
                            "现在请对下面内容进行评分：\n" +
                            content)
            );

            String reply = chat(messages);
            aiEvaluation.getTitleContentMatch().put(Math.toIntExact(article.getId()), parseAiJson(reply));
        }
    }

    public void accuracy(String keyword) {
        List<Article> articles = articleRepository.findAll();
        for (Article article : articles) {
            if (aiEvaluation.getAccuracy().containsKey(article.getId().intValue())) continue;
            String content = article.getContent();
            List<Message> messages = List.of(
                    new Message("system", "你是一个用于搜索系统的评测程序，根据用户给的文章内容，检索相关文档后再给出正确性评分和简短理由，理由中包含文档的依据。"),
                    new Message("user", "请你输出一个 JSON 对象(必须是合法 JSON),然后空一行，再讲出评分理由，请严格按照协议规范检查每一个字段的位长度和格式，任何细微错误也要扣分\n" +
                            "\n" +
                            "JSON 格式如下：\n" +
                            "{\n" +
                            "  \"score\": 0 | 1 | 2 | 3 | 4\n" +
                            "}\n" +
                            "\n" +
                            "评分含义：\n" +
                            "0 = 完全错误\n" +
                            "1 = 多数错误\n" +
                            "2 = 部分正确\n" +
                            "3 = 基本正确\n" +
                            "4 = 完全正确且专业\n" +
                            "\n" +
                            "现在请对下面内容进行评分：\n" +
                            content)
            );
            String reply = chat(messages);
            aiEvaluation.getAccuracy().put(Math.toIntExact(article.getId()), parseAiJson(reply));
        }
    }

    private AiEvaluation loadLocalEvaluation() {
        try {
            return new AievalParser("C:\\Users\\hemin\\CodeFlow\\backend\\CodeFlow\\src\\main\\resources\\result.aieval").load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public AiEvaluation aiEvaluate(String keyword) {
        aiEvaluation = loadLocalEvaluation();

        keywordContentMatch(keyword);
        titleContentMatch(keyword);
        accuracy(keyword);

        return aiEvaluation;
    }
    private AiScorer parseAiJson(String reply) {
        // 查找 JSON 开始和结束位置
        int startIndex = reply.indexOf('{');
        int endIndex = reply.lastIndexOf('}');

        if (startIndex == -1 || endIndex == -1 || startIndex > endIndex) {
            throw new IllegalArgumentException("Invalid JSON format in reply: " + reply);
        }

        // 截取纯 JSON 部分
        String json = reply.substring(startIndex, endIndex + 1);

        // 使用正则表达式提取 score 值
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\"?score\"?\\s*:\\s*(\\d+)");
        java.util.regex.Matcher matcher = pattern.matcher(json);

        if (matcher.find()) {
            Integer score = Integer.parseInt(matcher.group(1));
            return AiScorer.fromInt(score);
        } else {
            throw new IllegalArgumentException("Score not found in JSON: " + json);
        }
    }

}
