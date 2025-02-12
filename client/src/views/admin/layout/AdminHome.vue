<template>
  <div class="welcome-container">
    <h1>Welcome to Admin Console!</h1>
    <el-button type="primary" @click="goToDashboard">控制台</el-button>
  </div>
</template>

<script lang="ts" setup>
import {useRouter} from 'vue-router';

const router = useRouter();

const goToDashboard = () => {
  router.push({name: 'AdminHome'});
};
</script>

<style lang="scss" scoped>
.welcome-container {
  text-align: center;
  padding: 100px;
  background: linear-gradient(135deg, #0f2027, #2c5364); /* 渐变背景 */
  color: #ffffff; /* 白色文字 */
  min-height: 100vh; /* 使容器至少占满整个视口高度 */
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;

  h1 {
    font-size: 36px;
    color: #ffffff;
    margin-bottom: 20px;
    text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5); /* 文字阴影 */
  }

  p {
    font-size: 20px;
    color: #bdc3c7;
    margin-bottom: 40px;
    text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3); /* 文字阴影 */
  }

  .el-button {
    font-size: 18px;
    padding: 15px 30px;
    background-color: #3498db; /* 蓝色按钮 */
    border: none;
    transition: background-color 0.3s ease; /* 过渡效果 */

    &:hover {
      background-color: #2980b9; /* 鼠标悬停时颜色加深 */
    }
  }
}
</style>
