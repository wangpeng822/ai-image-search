import test from 'node:test'
import assert from 'node:assert/strict'
import {
  createImageSearchClient,
  normalizeSearchResults
} from './imageSearchClient.js'

test('normalizeSearchResults maps backend items into UI cards', () => {
  const cards = normalizeSearchResults([
    { imageId: 'img_1', title: '红色连衣裙', score: 0.8732, url: '/a.jpg', vectorStatus: 'synced' }
  ])

  assert.deepEqual(cards, [
    {
      id: 'img_1',
      title: '红色连衣裙',
      scoreText: '87.32%',
      imageUrl: '/a.jpg',
      vectorStatus: 'synced'
    }
  ])
})

test('textSearch posts keyword and pagination to Java API', async () => {
  const calls = []
  const client = createImageSearchClient({
    fetchImpl: async (url, options) => {
      calls.push({ url, options })
      return {
        ok: true,
        json: async () => ({ items: [], total: 0 })
      }
    }
  })

  await client.textSearch({ keyword: '红色连衣裙', page: 2, pageSize: 12 })

  assert.equal(calls[0].url, '/api/search/text')
  assert.equal(calls[0].options.method, 'POST')
  assert.equal(calls[0].options.headers['Content-Type'], 'application/json')
  assert.equal(calls[0].options.body, JSON.stringify({ keyword: '红色连衣裙', page: 2, pageSize: 12 }))
})
