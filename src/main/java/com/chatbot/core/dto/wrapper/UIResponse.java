package com.chatbot.core.dto.wrapper;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UIResponse<T extends UIResponseBody> {
    private boolean success = true;
    private T body;
    private List<ErrorDTO> errors;

    public UIResponse(List<ErrorDTO> errors) {
        this.success = false;
        this.errors = errors;
    }

    public UIResponse(T body) {
        this.body = body;
    }
}
