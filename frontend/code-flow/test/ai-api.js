import express from "express";
import OpenAI from "openai";
import cors from "cors"; // <- 导入 cors


const app = express();
app.use(express.json());

// CORS 配置
app.use(cors({
    origin: "*", // 允许所有域名访问，可改为前端地址如 "http://localhost:5173"
    methods: ["GET", "POST", "PUT", "DELETE", "OPTIONS"],
    allowedHeaders: ["Content-Type", "Authorization"],
}));

// 初始化 OpenAI 客户端
const openai = new OpenAI({
    baseURL: "https://api.deepseek.com",
    apiKey: "sk-0eb2697d6b2346da8dd54b62bb58659c",
});

// 简单接口 /chat
app.post("/chat", async (req, res) => {
    try {
        const { messages } = req.body;

        if (!messages) {
            return res.status(400).json({ error: "messages is required" });
        }

        const completion = await openai.chat.completions.create({
            model: "deepseek-chat",
            messages,
        });

        res.json({
            reply: completion.choices[0].message.content,
        });
    } catch (err) {
        console.error(err);
        res.status(500).json({ error: err.message });
    }
});

// 启动服务
const PORT = 3000;
app.listen(PORT, () => {
    console.log(`Server running on http://localhost:${PORT}`);
});
