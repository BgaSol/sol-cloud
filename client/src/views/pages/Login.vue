<script lang='ts' setup>
import {onMounted, ref} from 'vue';
import {Service, UserLoginDto, VerificationResult} from "~/generated/system";
import {useRoute, useRouter} from "vue-router";
import {useUser} from "~/pinia/modules/user";
import {ElButton, ElCol, ElForm, ElFormItem, ElImage, ElInput, ElMessage, ElRow, FormInstance} from "element-plus";
import {useFormValidation} from "~/composables/FormValidationHook";
import AppHeader from "~/views/app/layout/AppHeader.vue";
import {set} from "@vueuse/core";

const loginDto = ref<UserLoginDto>({
  username: '',
  password: '',
  verificationCode: '',
  verificationCodeKey: ''
});
const needCaptcha = ref<boolean>(true);
const imageBase64 = ref<string>();
const getCaptcha = () => {
  // 获取验证码
  Service.getVerificationCode().then((res) => {
    if (res.data?.captcha){
      loginDto.value.verificationCode = res.data?.captcha
      needCaptcha.value = false;
    }else{
      needCaptcha.value = true;
    }
    loginDto.value.verificationCodeKey = res.data?.verificationId as string
    imageBase64.value = res.data?.verificationCode as string;
    setTimeout(getCaptcha,30 * 1000)
  });
};
onMounted(() => {
  getCaptcha();
});
const router = useRouter();
const route = useRoute();
onMounted(() => {
  if (route.query.error === '401') {
    ElMessage.warning('请先登录');
  }
})
const user = useUser();

const formRef = ref<FormInstance>();

const {errData: loginDtoErr, validate, resetValidate} = useFormValidation();
const loading = ref(false);
// 登录
const login = () => {
  resetValidate();
  loading.value = true;
  Service.login(loginDto.value).then((res) => {
    if (res.code === 400) {
      validate(res.data as VerificationResult[]);
    } else if (res.code === 200) {
      user.setToken(<string>res.data?.tokenValue)
      if (route.query.redirect) {
        // 跳转到上次访问的页面
        window.location.href = route.query.redirect as string;
      } else {
        router.push({name: "admin_home"});
      }
    }
  }).catch(() => {
    getCaptcha();
    loading.value = false;
  });
};
const pageLoading = ref(true);
onMounted(() => {
  pageLoading.value = false;
});
</script>

<template>
  <div class='login'>
    <app-header></app-header>
    <el-row class="mt-10vw">
      <el-col v-motion-roll-bottom :duration="600" v-show="!pageLoading"
              :lg="{span:5,offset:13}"
              :md="{span:6,offset:12}"
              :sm="{span:10,offset:7}"
              :xl="{span:4,offset:15}"
              :xs="{span:12,offset:5}">
        <el-form ref="formRef" :model='loginDto' class='login-form'>
          <el-form-item>
            <div class='title'>用户登录</div>
          </el-form-item>
          <el-form-item :error="loginDtoErr.username">
            <el-input v-model='loginDto.username' placeholder='请输入用户名'
                      @keyup.enter.native='login'>
            </el-input>
          </el-form-item>
          <el-form-item :error="loginDtoErr.password">
            <el-input v-model='loginDto.password' placeholder='请输入密码' show-password
                      type='password'
                      @keyup.enter.native='login'>
            </el-input>
          </el-form-item>
          <el-form-item :error="loginDtoErr.verificationCode">
            <el-input v-model="loginDto.verificationCode" placeholder="请输入验证码" @keyup.enter.native='login'>
              <template #append>
                <el-image v-show="needCaptcha" fit="fill" :src="imageBase64" class="el-image-block cursor-pointer"
                          @click="getCaptcha"></el-image>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button :loading='loading' type='primary' @click='login'>
              登录
            </el-button>
          </el-form-item>
        </el-form>
      </el-col>
    </el-row>
  </div>
</template>

<style lang='scss' scoped>
.login {
  overflow-x: hidden;
  width: 100%;
  min-height: 100vh;
  background: linear-gradient(to right, var(--el-bg-color), var(--el-color-primary));
  position: absolute;

  .login-form {
    width: 100%;
    border: var(--el-border);
    border-radius: var(--el-border-radius-round);
    padding-left: 18px;
    padding-right: 18px;
    padding-top: 18px;
    background-color: var(--el-bg-color-overlay);

    :deep(.el-input-group__append) {
      padding: 0;
    }

    .title {
      position: relative;
      font-size: 18px;
      font-weight: bold;
    }
  }

  .login-images {
    text-align: left;
    position: absolute;
    left: 100px;
    width: 400px;
    height: 400px;
    top: 50%;
    transform: translateY(-50%);

    .app-login-logo {
      position: absolute;
      width: 50px;
      left: 173px;
      top: 200px;
      opacity: 0.8;
    }

    .login-message {
      position: absolute;
      width: 100%;
      top: 140px;
      text-align: center;

    }
  }

  .logo {
    position: absolute;
    top: 20px;
    left: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #ffffffaa;
    border: var(--el-border);
    border-radius: var(--el-border-radius-round);

    .app-menu-logo-login {
      :deep(img) {
        height: 100%;
        width: auto;
      }
    }
  }
}
</style>
