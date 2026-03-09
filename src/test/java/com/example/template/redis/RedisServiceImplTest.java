package com.example.template.redis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.time.Duration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
@DisplayName("RedisServiceImpl 단위 테스트")
class RedisServiceImplTest {

    @Mock private RedisTemplate<String, String> redisTemplate;
    @Mock private ValueOperations<String, String> valueOps;
    @InjectMocks private RedisServiceImpl redisService;

    @Nested
    @DisplayName("saveAccessToken")
    class SaveAccessToken {

        @Test
        @DisplayName("성공_Redis에_저장")
        void 성공() {
            given(redisTemplate.opsForValue()).willReturn(valueOps);

            redisService.saveAccessToken("user-uuid", "access-token", 600L);

            then(valueOps).should().set("user-uuid", "access-token", Duration.ofSeconds(600L));
        }
    }

    @Nested
    @DisplayName("getAccessToken")
    class GetAccessToken {

        @Test
        @DisplayName("성공_토큰반환")
        void 성공() {
            given(redisTemplate.opsForValue()).willReturn(valueOps);
            given(valueOps.get("user-uuid")).willReturn("stored-token");

            String result = redisService.getAccessToken("user-uuid");

            assertThat(result).isEqualTo("stored-token");
        }

        @Test
        @DisplayName("없으면_null_반환")
        void null반환() {
            given(redisTemplate.opsForValue()).willReturn(valueOps);
            given(valueOps.get("unknown")).willReturn(null);

            String result = redisService.getAccessToken("unknown");

            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("deleteAccessToken")
    class DeleteAccessToken {

        @Test
        @DisplayName("성공_Redis에서_삭제")
        void 성공() {
            redisService.deleteAccessToken("user-uuid");

            then(redisTemplate).should().delete("user-uuid");
        }
    }

    @Nested
    @DisplayName("hasAccessToken")
    class HasAccessToken {

        @Test
        @DisplayName("키_존재_true")
        void 존재_true() {
            given(redisTemplate.hasKey("user-uuid")).willReturn(Boolean.TRUE);

            assertThat(redisService.hasAccessToken("user-uuid")).isTrue();
        }

        @Test
        @DisplayName("키_없음_false")
        void 없음_false() {
            given(redisTemplate.hasKey("user-uuid")).willReturn(Boolean.FALSE);

            assertThat(redisService.hasAccessToken("user-uuid")).isFalse();
        }

        @Test
        @DisplayName("null_반환시_false")
        void null_false() {
            given(redisTemplate.hasKey("user-uuid")).willReturn(null);

            assertThat(redisService.hasAccessToken("user-uuid")).isFalse();
        }
    }
}
