<template>
  <div class="ai-chat-layout">
    <el-card class="chat-card">
      <template #header>
        <div class="chat-header">
          <div>
            <div class="chat-title">AI 智能问诊</div>
            <div class="chat-subtitle">
              说明：本功能仅提供健康科普与就诊建议，不能替代医生面对面诊疗。
            </div>
          </div>
          <el-button size="small" @click="clearChat">清空对话</el-button>
        </div>
      </template>

      <div class="chat-body" ref="chatBodyRef">
        <div v-if="messages.length === 0" class="chat-empty">
          <p>请描述您的症状或问题，例如：</p>
          <ul>
            <li>这两天一直咳嗽，有点发烧，需要去医院吗？</li>
            <li>最近总是头痛，可能是什么原因？</li>
          </ul>
        </div>

        <div
          v-for="(msg, index) in messages"
          :key="index"
          class="chat-message"
          :class="msg.role === 'user' ? 'from-user' : 'from-ai'"
        >
          <div class="avatar">
            <el-icon v-if="msg.role === 'user'"><User /></el-icon>
            <el-icon v-else><HelpFilled /></el-icon>
          </div>
          <div class="bubble">
            <div class="meta">
              <span class="name">{{ msg.role === 'user' ? '我' : 'AI 医疗助手' }}</span>
              <span class="time">{{ formatTime(msg.time) }}</span>
            </div>
            <div class="content" v-html="msg.content"></div>
          </div>
        </div>

        <div v-if="loading" class="chat-loading">
          <el-icon class="loading-icon"><Loading /></el-icon>
          正在为您思考，请稍候...
        </div>
      </div>

      <div class="chat-input">
        <el-input
          v-model="inputMessage"
          type="textarea"
          :rows="3"
          resize="none"
          maxlength="500"
          show-word-limit
          placeholder="请尽可能详细描述您的症状、持续时间、伴随症状、既往病史、正在使用的药物等信息..."
          @keyup.enter.exact.prevent="handleEnter"
        />
        <div class="chat-input-footer">
          <span class="tip">提示：紧急情况请立即拨打急救电话或前往医院就诊。</span>
          <el-button type="primary" :loading="loading" @click="sendMessage">
            发送
          </el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { User, HelpFilled, Loading } from '@element-plus/icons-vue'

const messages = ref([])
const inputMessage = ref('')
const loading = ref(false)
const chatBodyRef = ref(null)

// 可选：从本地用户信息中尝试获取 patientId（如果不存在就传 null）
const patientId = ref(null)

const initPatientId = () => {
  try {
    const userDataStr = localStorage.getItem('userData')
    if (!userDataStr) return
    const data = JSON.parse(userDataStr)
    // 如果后端未来在 userData 里直接放 patientId，可以在这里取
    if (data.patientId) {
      patientId.value = data.patientId
    }
  } catch (e) {
    // 忽略解析错误
  }
}

const scrollToBottom = () => {
  nextTick(() => {
    if (chatBodyRef.value) {
      chatBodyRef.value.scrollTop = chatBodyRef.value.scrollHeight
    }
  })
}

const formatTime = (date) => {
  const d = typeof date === 'string' ? new Date(date) : date
  return d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

const sendMessage = async () => {
  const content = inputMessage.value.trim()
  if (!content) {
    ElMessage.warning('请先输入要咨询的问题')
    return
  }
  if (loading.value) return

  // 本地先追加一条用户消息
  const userMsg = {
    role: 'user',
    content,
    time: new Date()
  }
  messages.value.push(userMsg)
  inputMessage.value = ''
  scrollToBottom()

  loading.value = true
  try {
    const payload = {
      message: content,
      patientId: patientId.value
    }
    const res = await axios.post('/chatWithAIJson', payload, {
      headers: { 'Content-Type': 'application/json' }
    })
    if (res.data.code === 0 && res.data.data) {
      const aiContent = res.data.data.replace(/\n/g, '<br/>')
      messages.value.push({
        role: 'assistant',
        content: aiContent,
        time: new Date()
      })
      scrollToBottom()
    } else {
      ElMessage.error(res.data.msg || 'AI 暂时无法回复，请稍后再试')
    }
  } catch (e) {
    console.error('AI 对话失败：', e)
    ElMessage.error('AI 对话失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const handleEnter = (e) => {
  // 在多行输入框中按一次 Enter 直接发送，按 Shift+Enter 换行
  if (e.shiftKey) {
    inputMessage.value += '\n'
  } else {
    sendMessage()
  }
}

const clearChat = () => {
  messages.value = []
}

onMounted(() => {
  initPatientId()
})
</script>

<style scoped>
.ai-chat-layout {
  width: 100%;
  height: 100%;
  box-sizing: border-box;
}

.chat-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-title {
  font-size: 18px;
  font-weight: 600;
}

.chat-subtitle {
  font-size: 12px;
  color: #6b7f95;
  margin-top: 4px;
}

.chat-body {
  flex: 1;
  padding: 12px;
  margin-bottom: 12px;
  border-radius: 8px;
  background: #f5faff;
  overflow-y: auto;
}

.chat-empty {
  text-align: left;
  color: #6b7f95;
  font-size: 13px;
}

.chat-empty ul {
  margin: 8px 0 0 18px;
  padding: 0;
}

.chat-message {
  display: flex;
  margin-bottom: 12px;
}

.chat-message.from-user {
  flex-direction: row-reverse;
}

.chat-message .avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #2f80ed;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  margin: 0 8px;
}

.chat-message.from-ai .avatar {
  background: #eaf3ff;
  color: #2f80ed;
}

.chat-message .bubble {
  max-width: 70%;
  padding: 8px 10px;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.chat-message.from-user .bubble {
  background: #2f80ed;
  color: #fff;
}

.bubble .meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 4px;
  font-size: 11px;
  color: #6b7f95;
}

.from-user .bubble .meta {
  color: rgba(255, 255, 255, 0.8);
}

.bubble .content {
  font-size: 14px;
  line-height: 1.6;
  word-break: break-word;
}

.chat-loading {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #6b7f95;
}

.loading-icon {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.chat-input {
  border-top: 1px solid #e6f0fa;
  padding-top: 8px;
}

.chat-input-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 6px;
}

.chat-input-footer .tip {
  font-size: 12px;
  color: #a0a3a9;
}
</style>


