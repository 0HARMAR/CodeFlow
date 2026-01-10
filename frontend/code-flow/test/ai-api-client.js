// test-client.js
import fetch from "node-fetch"; // Node 18+ 可以直接用 fetch，无需安装 node-fetch

async function main() {
    try {
        const response = await fetch("http://localhost:3000/chat", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                messages: [
                    { role: "system", content: "You are a helpful assistant." },
                    { role: "user", content: "帮我写一个加法函数的 Node.js 例子。" }
                ]
            }),
        });

        const data = await response.json();
        console.log("AI 回复:", data.reply);
    } catch (err) {
        console.error("请求失败:", err);
    }
}

main();
