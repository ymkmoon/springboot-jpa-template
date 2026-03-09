package com.example.template.common.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MenuDto {

    @Getter
    public static class MenuResponse {
        private String id;
        private String menuName;
        private String path;
        private Integer sortOrder;

        @Builder
        public MenuResponse(String id, String menuName, String path, Integer sortOrder) {
            this.id = id;
            this.menuName = menuName;
            this.path = path;
            this.sortOrder = sortOrder;
        }
    }

    @Getter
    public static class MenuAccessCheckResponse {
        private String menuId;
        private boolean accessible;

        @Builder
        public MenuAccessCheckResponse(String menuId, boolean accessible) {
            this.menuId = menuId;
            this.accessible = accessible;
        }
    }

}
