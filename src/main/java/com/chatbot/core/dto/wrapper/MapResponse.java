package com.chatbot.core.dto.wrapper;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class MapResponse<T1, T2> implements UIResponseBody {
    private Map<T1, T2> elements;

    public MapResponse(Map<T1, T2> map) {
        this.elements = map;
    }
}
