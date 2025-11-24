package com.bgasol.web.system.user.service;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bgasol.common.core.base.exception.BaseException;
import com.bgasol.model.system.user.dto.UserLoginDto;
import com.bgasol.model.system.user.entity.UserEntity;
import com.bgasol.model.system.user.vo.VerificationVo;
import com.bgasol.web.system.user.cache.CaptchaCache;
import com.bgasol.web.system.user.mapper.UserMapper;
import com.pig4cloud.captcha.ArithmeticCaptcha;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.bgasol.common.constant.value.SystemConfigValues.ADMIN_USER_ID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {
    private final UserMapper userMapper;

    private final CaptchaCache captchaCache;

    private final UserService userService;

    @Value("${system.captcha.is-open}")
    private Boolean captchaIsOpen;

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
        String key = UUID.randomUUID().toString(); // 生成验证码的key

        captchaCache.save(key, text); // 保存到缓存

        return VerificationVo.builder()
                .verificationCode(captcha.toBase64())
                .verificationId(key)
                .captcha(captchaIsOpen ? null : text)
                .build();
    }

    public void logout() {
        StpUtil.logout();
    }

    @Transactional(readOnly = true)
    public SaTokenInfo login(UserLoginDto userLoginDto) {
        if (captchaIsOpen){
            String verificationCodeKey = userLoginDto.getVerificationCodeKey();
            // 获取验证码并且删除
            String verificationCode = captchaCache.getAndDelete(verificationCodeKey);
            if (StringUtils.isEmpty(verificationCode)) {
                log.error("验证码已过期");
                throw new BaseException("验证码已过期");
            }
            if (!verificationCode.equals(userLoginDto.getVerificationCode())) {
                log.error("验证码错误");
                throw new BaseException("验证码错误");
            }
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

        if (!ADMIN_USER_ID.equals(userEntity.getId())) {
            userEntity = this.userService.findById(userEntity.getId());
            if (userEntity.getLocked()) {
                log.error("用户已锁定");
                throw new BaseException("用户已锁定");
            }
            if (ObjectUtils.isEmpty(userEntity.getRoles())) {
                throw new BaseException("用户未绑定角色，无法登录");
            }
            if (ObjectUtils.isEmpty(userEntity.getDepartment())) {
                throw new BaseException("用户未绑定部门，无法登录");
            }
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
