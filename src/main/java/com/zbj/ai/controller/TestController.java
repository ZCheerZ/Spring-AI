package com.zbj.ai.controller;


import com.zbj.ai.repository.ChatHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID; //比原来的AbstractchatMemoryAdvisor空出来一层


@RequiredArgsConstructor //这里使用 Lombok 的 @RequiredArgsConstructor 注解来自动生成构造函数 private final需要构造函数才能注入
@RestController
@RequestMapping("/ai")
public class TestController {

    private final ChatClient chatClient;

    private final ChatHistoryRepository chatHistoryRepository;


    @RequestMapping(value = "/chat", produces = "text/html;charset=UTF-8")
    public Flux<String> chat(String prompt, String chatId) {
        // 使用 chatClient 进行聊天
        // 保存会话id 到内存 之后去数据库
        chatHistoryRepository.save("chat", chatId);
        // 使用 chatClient 进行聊天
        Flux<String> content = chatClient.prompt().user(prompt)
                .advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, chatId))
                .stream().content();
        return content;
    }

    @RequestMapping(value = "/call")
    public String hello(String prompt) {
        // 使用 chatClient 进行聊天
        String content = chatClient.prompt().user(prompt).call().content();
        return content;
    }
}
