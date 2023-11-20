package com.chatbot.core.dto.wrapper;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class PageSegmentResponse<S, T> implements UIResponseBody {
    private Map<S, List<T>> elements;
    private int totalPages;
    private int page;
    private int size;

    public PageSegmentResponse(Map<S, List<T>> segmentMap, Pageable pageable, int count) {
        this.page = pageable.getPageNumber();
        this.size = count;
        this.elements = segmentMap;
        this.totalPages = pageable.getPageSize() == 0 ? 1 : (int) Math.ceil((double) count / (double) pageable.getPageSize());
    }

}
