package com.example.account.service;

import com.example.account.Exception.AccountException;
import com.example.account.type.ErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class LockServiceTest {
    @Mock
    RedissonClient redissonClient;

    @Mock
    RLock lock;

    @InjectMocks
    LockService lockService;

    @Test
    void successGetLock() throws InterruptedException {
        //given
        given(redissonClient.getLock(anyString())).
                willReturn(lock);
        given(lock.tryLock(anyLong(), anyLong(), any()))
                .willReturn(true);

        //when
        //then
        assertDoesNotThrow(()
                -> lockService.lock("1234"));
        // 따로 리턴 값이 없기 때문에 위와 같이 작성
    }

    @Test
    void failedGetLock() throws InterruptedException {
        //given
        given(redissonClient.getLock(anyString())).
                willReturn(lock);
        given(lock.tryLock(anyLong(), anyLong(), any()))
                .willReturn(false);
        //when

        AccountException accountException = assertThrows(
                AccountException.class, () ->
                lockService.lock("1234"));
        //then
        assertEquals(ErrorCode.ACCOUNT_TRANSACTION_LOCK, accountException.getErrorCode());

    }

}