<template>
  <div class="layout-container">
    <div class="layout-header">
      <div class="logo">商品管理系统</div>
      <div class="user-info">
        <span class="username">{{ username }}</span>
        <el-button type="text" @click="handleLogout">退出登录</el-button>
      </div>
    </div>
    <div class="layout-main">
      <router-view />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { logout } from '../api/auth'

const router = useRouter()
const username = ref('')

onMounted(() => {
  username.value = localStorage.getItem('username') || ''
})

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await logout()
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    ElMessage.success('已退出登录')
    router.push('/login')
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.layout-header {
  height: 60px;
  background: linear-gradient(135deg, #ff5000 0%, #ff7800 100%);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.logo {
  font-size: 20px;
  font-weight: 600;
  color: #fff;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.username {
  color: #fff;
  font-size: 14px;
}

.user-info .el-button {
  color: #fff;
}

.user-info .el-button:hover {
  color: rgba(255, 255, 255, 0.8);
}

.layout-main {
  flex: 1;
  padding: 20px;
  background: #f5f5f5;
}
</style>
