package com.example.template.common.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ListResponseDto<T> {

	private final long totalCount;
    private final List<T> list;

    public static <T> ListResponseDto<T> of(long totalCount, List<T> list) {
        return ListResponseDto.<T>builder()
            .totalCount(totalCount)
            .list(list)
            .build();
    }
}
