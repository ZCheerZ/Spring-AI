package com.zbj.ai.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@RestController
@RequestMapping("/ai")
public class TestController {

    private final ChatClient chatClient;

    @RequestMapping(value = "/chat", produces = "text/html;charset=UTF-8")
    public Flux<String> chat(String prompt) {
        // 使用 chatClient 进行聊天
        Flux<String> content = chatClient.prompt().user(prompt).stream().content();
        return content;
    }

    @RequestMapping(value = "/call")
    public String hello(String prompt) {
        // 使用 chatClient 进行聊天
        String content = chatClient.prompt().user(prompt).call().content();
        return content;
    }
}
