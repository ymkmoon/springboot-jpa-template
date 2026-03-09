package com.example.template.common.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthorityGroupDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CreateRequest {
        private String levelCode;
        private String name;
        private String description;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class UpdateRequest {
        private String groupId;
        private String levelCode;
        private String name;
        private String description;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class DeleteRequest {
        private String groupId;
    }

    @Getter
    public static class AuthorityGroupResponse {
        private String id;
        private String levelCode;
        private String name;
        private String description;
        private boolean isActive;

        @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
        @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
        private LocalDateTime createdAt;

        @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
        @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
        private LocalDateTime updatedAt;

        @Builder
        public AuthorityGroupResponse(String id, String levelCode, String name, String description,
                boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt) {
            this.id = id;
            this.levelCode = levelCode;
            this.name = name;
            this.description = description;
            this.isActive = isActive;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }
    }
}
