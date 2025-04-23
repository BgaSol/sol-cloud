package com.bgasol.web.system.user.service;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bgasol.common.core.base.exception.BaseException;
import com.bgasol.model.system.user.dto.UserLoginDto;
import com.bgasol.model.system.user.entity.UserEntity;
import com.bgasol.model.system.user.vo.VerificationVo;
import com.bgasol.web.system.user.mapper.UserMapper;
import com.pig4cloud.captcha.ArithmeticCaptcha;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class LoginService {
    private final UserMapper userMapper;

    private final RedisTemplate<String, String> redisTemplate;

    private final static String UserLoginCodeKey = "system:login:captcha:";

    private final UserService userService;

    @Value("${system.captcha.length}")
    private Integer captchaLength;

    @Value("${system.captcha.max}")
    private Integer captchaMaxNumber;

    public VerificationVo getVerificationCode() {
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(100, 35); // 算术类型
        captcha.setLen(captchaLength);  // 几位数运算，默认是两位
        captcha.supportAlgorithmSign(4); // 可设置支持的算法：2 表示只生成带加减法的公式
        captcha.setDifficulty(captchaMaxNumber); // 设置计算难度，参与计算的每一个整数的最大值
        String text = captcha.text(); // 获取运算结果

        String key = UserLoginCodeKey + UUID.randomUUID(); // 生成验证码的key
        redisTemplate.opsForValue().set(key, text, 1, TimeUnit.MINUTES); // 保存到redis
        VerificationVo verificationVo = new VerificationVo();
        verificationVo.setVerificationCode(captcha.toBase64());
        verificationVo.setVerificationId(key);
        return verificationVo;
    }

    public void logout() {
        StpUtil.logout();
    }

    @Transactional(readOnly = true)
    public SaTokenInfo login(UserLoginDto userLoginDto) {
        String captchaKey = UserLoginCodeKey + userLoginDto.getVerificationCodeKey();
        // 获取验证码
        String verificationCode = redisTemplate.opsForValue().get(captchaKey);
        if (verificationCode == null) {
            log.error("验证码已过期");
            throw new BaseException("验证码已过期");
        }
        // 删除验证码
        redisTemplate.delete(captchaKey);
        if (!verificationCode.equals(userLoginDto.getVerificationCode())) {
            log.error("验证码错误");
            throw new BaseException("验证码错误");
        }
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getUsername, userLoginDto.getUsername());
        UserEntity userEntity = this.userMapper.selectOne(queryWrapper);
        if (userEntity == null) {
            log.error("用户不存在");
            throw new BaseException("用户名或密码错误");
        }
        if (!userEntity.getPassword().equals(userService.encodePassword(userLoginDto.getPassword()))) {
            log.error("密码错误");
            throw new BaseException("用户名或密码错误");
        }
        if (userEntity.getLocked()) {
            log.error("用户已锁定");
            throw new BaseException("用户已锁定");
        }
        StpUtil.login(userEntity.getId());
        return StpUtil.getTokenInfo();
    }

    public List<UserEntity> findOnlineUser() {
        List<String> sessionIds = StpUtil.searchSessionId("", 0, -1, false);
        return sessionIds.stream().map(sessionId -> {
            SaSession saSession = StpUtil.getSessionBySessionId(sessionId);
            return userService.getUserInfo((String) saSession.getLoginId());
        }).toList();
    }
}
