package org.javaup;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/ask")
    public String ask(@RequestParam(value = "question", defaultValue = "请介绍一下 Spring AI 的核心能力") String question) {
        return this.chatClient.prompt()
                .user(question)
                .call()
                .content();
    }

    //接口的produces要设置成text/html;charset=utf-8，不然前端无法接收流式输出
    @GetMapping(value = "/stream", produces = "text/html;charset=utf-8")
    public Flux<String> stream(
            @RequestParam(value = "question", defaultValue = "请用三点介绍 Spring AI 为什么适合 Java 项目") String question) {
        return this.chatClient.prompt()
                .user(question)
                .stream()
                .content();
    }

    @GetMapping("/ask-with-system")
    public String askWithSystem(
            @RequestParam(value = "system", required = false) String system,
            @RequestParam(value = "question", defaultValue = "请介绍一下 Spring AI") String question) {
        ChatClient.ChatClientRequestSpec requestSpec = this.chatClient.prompt();
        if (StringUtils.hasText(system)) {
            requestSpec = requestSpec.system(system);
        }
        return requestSpec.user(question)
                .call()
                .content();
    }
}
