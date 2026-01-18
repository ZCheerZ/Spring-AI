package com.zbj.ai.controller;


import com.zbj.ai.repository.ChatHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.content.Media;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Objects;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID; //比原来的AbstractchatMemoryAdvisor空出来一层


@RequiredArgsConstructor //这里使用 Lombok 的 @RequiredArgsConstructor 注解来自动生成构造函数 private final需要构造函数才能注入
@RestController
@RequestMapping("/ai")
public class TestController {

    private final ChatClient chatClient;

    private final ChatHistoryRepository chatHistoryRepository;

    private Flux<String> multiModalChat(String prompt, String chatId, List<MultipartFile> files) {
        // 1.解析多媒体
        List<Media> medias = files.stream()
                .map(file -> new Media(
                                MimeType.valueOf(Objects.requireNonNull(file.getContentType())),
                                file.getResource()
                        )
                )
                .toList();
        // 2.请求模型
        return chatClient.prompt()
                .user(p -> p.text(prompt).media(medias.toArray(Media[]::new)))
                .advisors(a -> a.param(CONVERSATION_ID, chatId))
                .stream()
                .content();
    }

    private Flux<String> textChat(String prompt, String chatId) {
        return chatClient.prompt()
                .user(prompt)
                .advisors(a -> a.param(CONVERSATION_ID, chatId))
                .stream()
                .content();
    }

    @RequestMapping(value = "/chat", produces = "text/html;charset=utf-8")
    public Flux<String> chat(
            @RequestParam("prompt") String prompt,
            @RequestParam("chatId") String chatId,
            @RequestParam(value = "files", required = false) List<MultipartFile> files) {
        // 使用 chatClient 进行聊天
        // 保存会话id 到内存 之后去数据库
        chatHistoryRepository.save("chat", chatId);
        // 请求模型  使用 chatClient 进行聊天
        if (files == null || files.isEmpty()) {
            // 没有附件，纯文本聊天
            return textChat(prompt, chatId);
        } else {
            // 有附件，多模态聊天
            return multiModalChat(prompt, chatId, files);
        }

    }

    @RequestMapping(value = "/call")
    public String hello(String prompt) {
        // 去config内修改bean使用chatClient是ollma模型进行聊天还是openai调用的api模型进行
        String content = chatClient.prompt().user(prompt).call().content();
        return content;
    }
}
