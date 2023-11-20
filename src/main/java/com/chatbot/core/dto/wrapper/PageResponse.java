package com.chatbot.core.dto.wrapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> implements UIResponseBody {
    private List<T> elements;
    private int totalPages;
    private long totalElements;
    private int page;
    private int size;

    public PageResponse(Page<T> page) {
        this.page = page.getNumber();
        this.size = page.getSize();
        this.totalPages = page.getTotalPages();
        this.elements = page.getContent();
        this.totalElements = page.getTotalElements();
    }
}
