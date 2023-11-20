package com.chatbot.core.controlers;

import com.chatbot.core.dto.AuthorizedUserDTO;
import com.chatbot.core.dto.LoginRequestDTO;
import com.chatbot.core.dto.wrapper.FieldResponse;
import com.chatbot.core.dto.wrapper.UIResponse;
import com.chatbot.core.services.LoginService;
import com.chatbot.core.utils.ClientInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/login")
@Slf4j
@RequiredArgsConstructor
public class LoginController {


    private final LoginService loginService;

    @PostMapping()
    public ResponseEntity<UIResponse<AuthorizedUserDTO>> login(
            @Valid @RequestBody final LoginRequestDTO loginRequest,
            HttpServletRequest request) {
        return ResponseEntity.ok(new UIResponse<>(
                loginService.login(loginRequest, ClientInfo.getIPv6Address(request))));
    }

    @GetMapping()
    public ResponseEntity<UIResponse<FieldResponse>> test() {
        return ResponseEntity.ok(new UIResponse<>(new FieldResponse("Hello")));
    }

}
