package com.chatbot.core.dto.wrapper;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ListResponse<T> implements UIResponseBody {
    private List<T> elements;

    public ListResponse(List<T> list) {
        this.elements = list;
    }
}
