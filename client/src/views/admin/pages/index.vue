<script lang='ts' setup>
</script>
<template>
  <div class='admin-app'>
    <div class='header'>
      <router-view name='header'></router-view>
    </div>
    <div class='lower'>
      <div class='menu-view'>
        <router-view name='menu'></router-view>
      </div>
      <div class='main-view'>
        <router-view v-slot="{ Component }">
          <transition enter-active-class="animate__animated animate__bounceInLeft"
                      leave-active-class="animate__animated animate__bounceOutRight"
                      mode="out-in">
            <component :is="Component"/>
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>
<style lang='scss' scoped>
.admin-app {
  overflow: hidden;
  position: absolute;
  width: 100vw;
  height: 100vh;
  display: flex;
  flex-direction: column;

  .header {
    position: relative;
    z-index: 100;
  }

  .lower {
    position: relative;
    flex: 1;
    display: flex;
    flex-direction: row;
    min-height: 0;

    .menu-view {
      z-index: 50;
      height: 100%;
      position: relative;
      min-height: 0;
      overflow-y: auto;
      //width: 300px;

      //.menu-container {
      //  height: 100%;
      //  width: 100%;
      //  position: absolute;
      //}
    }

    .main-view {
      flex: 1;
      position: relative;
    }
  }
}
</style>