package com.example.template.common.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthorityGroupMenuDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CreateRequest {
        private String groupId;
        private List<String> menuIds;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class UpdateRequest {
        private String groupId;
        private List<String> menuIds;
    }


    @Getter
    public static class AuthorityGroupMenuResponse {
        private String id;
        private String groupId;
        private String menuId;
        private String menuName;
        private String menuPath;
        private boolean isActive;

        @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
        @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
        private LocalDateTime createdAt;

        @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
        @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
        private LocalDateTime updatedAt;

        @Builder
        public AuthorityGroupMenuResponse(String id, String groupId, String menuId, String menuName,
                String menuPath, boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt) {
            this.id = id;
            this.groupId = groupId;
            this.menuId = menuId;
            this.menuName = menuName;
            this.menuPath = menuPath;
            this.isActive = isActive;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }
    }
}
