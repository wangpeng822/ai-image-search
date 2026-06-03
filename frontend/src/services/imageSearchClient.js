const mockResults = [
  {
    imageId: 'img_10001',
    title: '红色连衣裙商品图',
    score: 0.9135,
    url: 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=900&q=80',
    vectorStatus: 'synced',
    contentType: 'image/jpeg',
    createdAt: '2026-06-02 09:10:21'
  },
  {
    imageId: 'img_10002',
    title: '复古红色外套',
    score: 0.8712,
    url: 'https://images.unsplash.com/photo-1529139574466-a303027c1d8b?auto=format&fit=crop&w=900&q=80',
    vectorStatus: 'synced',
    contentType: 'image/jpeg',
    createdAt: '2026-06-02 09:13:44'
  },
  {
    imageId: 'img_10003',
    title: '街拍相似风格',
    score: 0.823,
    url: 'https://images.unsplash.com/photo-1483985988355-763728e1935b?auto=format&fit=crop&w=900&q=80',
    vectorStatus: 'synced',
    contentType: 'image/jpeg',
    createdAt: '2026-06-02 09:17:05'
  },
  {
    imageId: 'img_10004',
    title: '待同步图片样本',
    score: 0.7421,
    url: 'https://images.unsplash.com/photo-1496747611176-843222e1e57c?auto=format&fit=crop&w=900&q=80',
    vectorStatus: 'pending',
    contentType: 'image/jpeg',
    createdAt: '2026-06-02 09:21:30'
  }
]

const mockTasks = [
  { taskId: 'task_90001', imageId: 'img_10001', taskType: 'index', status: 'success', retryCount: 0, updatedAt: '2026-06-02 09:11:02' },
  { taskId: 'task_90002', imageId: 'img_10004', taskType: 'index', status: 'pending', retryCount: 0, updatedAt: '2026-06-02 09:22:00' },
  { taskId: 'task_90003', imageId: 'img_09998', taskType: 'reindex', status: 'failed', retryCount: 2, updatedAt: '2026-06-02 08:57:18' }
]

export function normalizeSearchResults(items = []) {
  return items.map((item) => ({
    id: item.imageId,
    title: item.title || item.imageId,
    scoreText: typeof item.score === 'number' ? `${(item.score * 100).toFixed(2)}%` : '-',
    imageUrl: item.url,
    vectorStatus: item.vectorStatus || 'unknown',
    contentType: item.contentType || '-',
    createdAt: item.createdAt || '-'
  }))
}

async function requestJson(fetchImpl, url, options) {
  const response = await fetchImpl(url, options)
  if (!response.ok) {
    throw new Error(`Request failed: ${response.status}`)
  }
  return response.json()
}

export function createImageSearchClient({ fetchImpl = globalThis.fetch } = {}) {
  return {
    async uploadImage(file) {
      if (!file) {
        return { imageId: 'img_preview', status: 'pending' }
      }
      const formData = new FormData()
      formData.append('file', file)
      return requestJson(fetchImpl, '/api/images', { method: 'POST', body: formData })
    },

    async textSearch({ keyword, page = 1, pageSize = 20 }) {
      return requestJson(fetchImpl, '/api/search/text', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ keyword, page, pageSize })
      })
    },

    async imageSearch({ file, page = 1, pageSize = 20 }) {
      const formData = new FormData()
      if (file) formData.append('file', file)
      formData.append('page', String(page))
      formData.append('pageSize', String(pageSize))
      return requestJson(fetchImpl, '/api/search/image', { method: 'POST', body: formData })
    },

    async listImages() {
      return requestJson(fetchImpl, '/api/images', { method: 'GET' })
    },

    async listVectorTasks() {
      return requestJson(fetchImpl, '/api/vector-tasks', { method: 'GET' })
    }
  }
}

export function createFallbackClient() {
  return {
    async uploadImage() {
      return { imageId: `img_${Date.now()}`, status: 'pending' }
    },
    async textSearch() {
      return { items: mockResults, total: mockResults.length }
    },
    async imageSearch() {
      return { items: mockResults.slice().reverse(), total: mockResults.length }
    },
    async listImages() {
      return { items: mockResults, total: mockResults.length }
    },
    async listVectorTasks() {
      return { items: mockTasks, total: mockTasks.length }
    }
  }
}

export async function withFallback(primaryCall, fallbackCall) {
  try {
    return await primaryCall()
  } catch {
    return fallbackCall()
  }
}
