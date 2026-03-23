import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const username = ref(localStorage.getItem('username') || '')

  const setUserInfo = (userToken, userName) => {
    token.value = userToken
    username.value = userName
    localStorage.setItem('token', userToken)
    localStorage.setItem('username', userName)
  }

  const clearUserInfo = () => {
    token.value = ''
    username.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('username')
  }

  return {
    token,
    username,
    setUserInfo,
    clearUserInfo
  }
})
