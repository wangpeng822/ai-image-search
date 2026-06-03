<script setup>
import { computed, onMounted, ref } from 'vue'
import {
  createFallbackClient,
  createImageSearchClient,
  normalizeSearchResults,
  withFallback
} from './services/imageSearchClient.js'

const api = createImageSearchClient()
const fallback = createFallbackClient()

const keyword = ref('红色连衣裙')
const activeMode = ref('text')
const statusFilter = ref('all')
const uploadFileName = ref('')
const queryFileName = ref('')
const results = ref([])
const tasks = ref([])
const total = ref(0)
const loading = ref(false)
const notice = ref('Java API 未启动时页面会自动使用本地 mock 数据。')

const filteredResults = computed(() => {
  if (statusFilter.value === 'all') return results.value
  return results.value.filter((item) => item.vectorStatus === statusFilter.value)
})

function statusLabel(status) {
  const labels = {
    synced: '已同步',
    pending: '待入库',
    failed: '失败',
    success: '成功',
    running: '运行中'
  }
  return labels[status] || status
}

async function loadInitialData() {
  const [imageResponse, taskResponse] = await Promise.all([
    withFallback(() => api.listImages(), () => fallback.listImages()),
    withFallback(() => api.listVectorTasks(), () => fallback.listVectorTasks())
  ])
  results.value = normalizeSearchResults(imageResponse.items)
  total.value = imageResponse.total
  tasks.value = taskResponse.items
}

async function submitTextSearch() {
  activeMode.value = 'text'
  loading.value = true
  const response = await withFallback(
    () => api.textSearch({ keyword: keyword.value, page: 1, pageSize: 20 }),
    () => fallback.textSearch()
  )
  results.value = normalizeSearchResults(response.items)
  total.value = response.total
  notice.value = `已执行文搜图：${keyword.value}`
  loading.value = false
}

async function submitImageSearch(event) {
  const file = event.target.files?.[0]
  queryFileName.value = file?.name || ''
  activeMode.value = 'image'
  loading.value = true
  const response = await withFallback(
    () => api.imageSearch({ file, page: 1, pageSize: 20 }),
    () => fallback.imageSearch()
  )
  results.value = normalizeSearchResults(response.items)
  total.value = response.total
  notice.value = file ? `已执行图搜图：${file.name}` : '已执行图搜图'
  loading.value = false
}

async function uploadImage(event) {
  const file = event.target.files?.[0]
  uploadFileName.value = file?.name || ''
  const response = await withFallback(() => api.uploadImage(file), () => fallback.uploadImage())
  notice.value = `图片已提交入库：${response.imageId}，状态 ${response.status}`
  await loadInitialData()
}

onMounted(loadInitialData)
</script>

<template>
  <main class="app-shell">
    <aside class="sidebar">
      <div class="brand">
        <span class="brand-mark">AI</span>
        <div>
          <h1>图片搜索工作台</h1>
          <p>Java 控制面 + Python 向量面预留</p>
        </div>
      </div>

      <section class="panel">
        <div class="panel-title">图片入库</div>
        <label class="upload-zone">
          <input type="file" accept="image/*" @change="uploadImage" />
          <span>选择图片上传</span>
          <small>{{ uploadFileName || '生成 image_id 并创建向量任务' }}</small>
        </label>
      </section>

      <section class="panel">
        <div class="panel-title">文搜图</div>
        <div class="search-row">
          <input v-model="keyword" type="text" placeholder="输入中文描述" @keydown.enter="submitTextSearch" />
          <button @click="submitTextSearch">搜索</button>
        </div>
      </section>

      <section class="panel">
        <div class="panel-title">图搜图</div>
        <label class="upload-zone compact">
          <input type="file" accept="image/*" @change="submitImageSearch" />
          <span>上传查询图</span>
          <small>{{ queryFileName || '临时图后续由 Java 写入 OSS' }}</small>
        </label>
      </section>

      <section class="panel">
        <div class="panel-title">向量任务</div>
        <div class="task-list">
          <div v-for="task in tasks" :key="task.taskId" class="task-item">
            <div>
              <strong>{{ task.taskId }}</strong>
              <span>{{ task.imageId }} / {{ task.taskType }}</span>
            </div>
            <em :class="`task-${task.status}`">{{ statusLabel(task.status) }}</em>
          </div>
        </div>
      </section>
    </aside>

    <section class="content">
      <header class="toolbar">
        <div>
          <p class="eyebrow">Search Results</p>
          <h2>{{ activeMode === 'text' ? '文搜图结果' : '图搜图结果' }}</h2>
          <span>{{ notice }}</span>
        </div>
        <div class="toolbar-actions">
          <select v-model="statusFilter">
            <option value="all">全部状态</option>
            <option value="synced">已同步</option>
            <option value="pending">待入库</option>
            <option value="failed">失败</option>
          </select>
          <button :disabled="loading" @click="loadInitialData">刷新</button>
        </div>
      </header>

      <div class="stats">
        <div>
          <span>总结果</span>
          <strong>{{ total }}</strong>
        </div>
        <div>
          <span>当前显示</span>
          <strong>{{ filteredResults.length }}</strong>
        </div>
        <div>
          <span>向量任务</span>
          <strong>{{ tasks.length }}</strong>
        </div>
      </div>

      <div class="result-grid">
        <article v-for="item in filteredResults" :key="item.id" class="image-card">
          <img :src="item.imageUrl" :alt="item.title" />
          <div class="image-card-body">
            <div class="score">{{ item.scoreText }}</div>
            <h3>{{ item.title }}</h3>
            <p>{{ item.id }} / {{ item.contentType }}</p>
            <div class="meta">
              <span :class="`status status-${item.vectorStatus}`">{{ statusLabel(item.vectorStatus) }}</span>
              <span>{{ item.createdAt }}</span>
            </div>
          </div>
        </article>
      </div>
    </section>
  </main>
</template>
