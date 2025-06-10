package com.bgasol.common.core.base.aspect;

import com.bgasol.common.core.base.cache.CacheEvictById;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RedissonClient;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class CacheEvictAspect {
    private final RedissonClient redissonClient;

    private SpelExpressionParser spelExpressionParser = new SpelExpressionParser();

    @Around("@annotation(cacheEvictById)")
    public void cacheEvictById(JoinPoint joinPoint, CacheEvictById cacheEvictById) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        // 获取参数名与值
        EvaluationContext context = new StandardEvaluationContext();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }

        String rawKey = cacheEvictById.key();
        Expression expression = spelExpressionParser.parseExpression(rawKey);
        String keyValue = (String) expression.getValue(context);
        String cacheKey = cacheEvictById.cacheName() + ":" + keyValue;
        // 删除缓存
        boolean delete = redissonClient.getBucket(cacheKey).delete();
        if (delete) {
            log.info("删除缓存：{}", cacheKey);
        }
    }
}
