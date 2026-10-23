<template>
  <div class="auth-page">
    <span class="bg-orb orb-1"></span>
    <span class="bg-orb orb-2"></span>

    <div class="auth-shell" :class="{ 'is-wide': wide }">
      <div class="auth-brand">
        <div class="auth-logo">
          <img src="/hospital-logo.svg" alt="仁心医院" />
        </div>
        <div>
          <h1>仁心医院</h1>
          <p>门诊信息系统</p>
        </div>
      </div>

      <div class="auth-card">
        <slot />
      </div>

      <div class="auth-footer">
        <span>JWT 安全认证</span>
        <span>数据加密</span>
        <span>7×24 小时服务</span>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  wide: {
    type: Boolean,
    default: false
  }
})
</script>

<style scoped>
.auth-page {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  padding: 32px 18px;
  background:
    radial-gradient(circle at 12% 12%, rgba(181, 217, 255, 0.55), transparent 34%),
    radial-gradient(circle at 88% 8%, rgba(214, 236, 255, 0.72), transparent 30%),
    linear-gradient(150deg, #eef6ff 0%, #f9fcff 50%, #e8f3ff 100%);
}

.bg-orb {
  position: absolute;
  border-radius: 50%;
  background: radial-gradient(circle at 30% 30%, rgba(255, 255, 255, 0.92), rgba(255, 255, 255, 0.08));
  pointer-events: none;
}

.orb-1 {
  width: 360px;
  height: 360px;
  left: -130px;
  bottom: -130px;
  opacity: 0.55;
}

.orb-2 {
  width: 260px;
  height: 260px;
  right: -90px;
  top: -90px;
  opacity: 0.45;
}

.auth-shell {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 460px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 18px;
}

.auth-shell.is-wide {
  max-width: 580px;
}

.auth-brand {
  display: flex;
  align-items: center;
  gap: 12px;
}

.auth-logo {
  width: 46px;
  height: 46px;
  border-radius: 13px;
  padding: 9px;
  background: linear-gradient(135deg, #2f80ed 0%, #56ccf2 100%);
  box-shadow: 0 10px 24px rgba(47, 128, 237, 0.24);
  flex-shrink: 0;
}

.auth-logo img {
  width: 100%;
  height: 100%;
  filter: brightness(0) invert(1);
}

.auth-brand h1 {
  margin: 0;
  font-size: 20px;
  font-weight: 800;
  color: #12395f;
  letter-spacing: 0.04em;
}

.auth-brand p {
  margin: 2px 0 0;
  color: #4d7ba8;
  font-size: 12px;
  letter-spacing: 0.08em;
}

.auth-card {
  position: relative;
  width: 100%;
  overflow: hidden;
  padding: 32px 34px 30px;
  background: #ffffff;
  border: 1px solid var(--his-border);
  border-radius: 20px;
  box-shadow: 0 24px 60px rgba(27, 73, 122, 0.12);
}

.auth-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #2f80ed 0%, #56ccf2 100%);
}

.auth-footer {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 18px;
  color: #5b7c9c;
  font-size: 12px;
}

.auth-footer span {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.auth-footer span::before {
  content: '';
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: #2f80ed;
  opacity: 0.7;
}

@media (max-width: 640px) {
  .auth-page {
    padding: 24px 14px;
  }

  .auth-card {
    padding: 26px 22px 24px;
    border-radius: 16px;
  }

  .auth-brand h1 {
    font-size: 18px;
  }

  .auth-logo {
    width: 42px;
    height: 42px;
  }
}
</style>
