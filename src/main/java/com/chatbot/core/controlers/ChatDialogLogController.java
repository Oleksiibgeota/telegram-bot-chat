package com.chatbot.core.controlers;


import com.chatbot.core.dto.AuthorizedUserDTO;
import com.chatbot.core.dto.ChatDialogDTO;
import com.chatbot.core.dto.wrapper.FieldResponse;
import com.chatbot.core.dto.wrapper.ListResponse;
import com.chatbot.core.dto.wrapper.UIResponse;
import com.chatbot.core.services.ChatDialogLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dialogs")
@Slf4j
@RequiredArgsConstructor
public class ChatDialogLogController {

    private final ChatDialogLog chatDialogLog;

    @GetMapping("/{telegramUserId}")
    public ResponseEntity<UIResponse<ListResponse<ChatDialogDTO>>> getLogs(
            final AuthorizedUserDTO au,
            @PathVariable(value = "telegramUserId") Long telegramUserId) {
        return ResponseEntity.ok(new UIResponse<>(
                new ListResponse<>(
                        this.chatDialogLog.getLogsByTelegramUserId(telegramUserId)))
        );
    }

    @GetMapping()
    public ResponseEntity<UIResponse<FieldResponse>> test() {
        return ResponseEntity.ok(new UIResponse<>(new FieldResponse("Hello")));
    }
}
