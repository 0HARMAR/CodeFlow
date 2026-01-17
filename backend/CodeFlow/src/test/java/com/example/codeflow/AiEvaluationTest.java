package com.example.codeflow;

import com.example.codeflow.domain.search.aievaluation.AiClientService;
import com.example.codeflow.domain.search.aievaluation.Message;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class AiEvaluationTest {
    @Test
    void test() {
        AiClientService service = new AiClientService();
        List<Message> messages = List.of(
                new Message("system", "You are a helpful assistant."),
                new Message("user", "帮我写一个加法函数的 Node.js 例子。")
        );
        String reply = service.chat(messages);
        System.out.println(reply);
    }

    // keyword-content match test
    @Test
    void test2() {
        AiClientService service = new AiClientService();
        String content = "JVM 和 Python 的主要区别有哪些";
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
        String reply = service.chat(messages);
        System.out.println(reply);
    }

    // title-content match test
    @Test
    void test3() {
        AiClientService service = new AiClientService();
        String title = "java简介";
        String content = "java是一个面向对象的编程语言";
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

        String reply = service.chat(messages);
        System.out.println(reply);
    }

    // Accuracy test
    @Test
    void test4() {
        AiClientService service = new AiClientService();
        String content = "Literal Header Field without Indexing\n" +
                "\n" +
                "   A literal header field without indexing representation results in\n" +
                "   appending a header field to the decoded header list without altering\n" +
                "   the dynamic table.\n" +
                "\n" +
                "     0   1   2   3   4   5   6   7\n" +
                "   +---+---+---+---+---+---+---+---+\n" +
                "   | 0 | 0 | 0 | 0 |  Index (5+)   |\n" +
                "   +---+---+-----------------------+\n" +
                "   | HF |     Value Length (71+)     |\n" +
                "   +---+---------------------------+\n" +
                "   | Value String (Length octets)  |\n" +
                "   +-------------------------------+";
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
        String reply = service.chat(messages);
        System.out.println(reply);
    }

}
