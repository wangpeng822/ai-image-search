# AI Image Search Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a single repository containing a Vue 3 AI image search console, a Java Spring Boot backend skeleton, database schema, and reserved integration points for vector search and aiagent.

**Architecture:** The frontend calls Java-facing `/api` endpoints only. The backend returns mock data now and isolates future Python/Qdrant integration behind `VectorAgentClient`. Database SQL records image assets, vector tasks, and search logs while Qdrant remains an external index.

**Tech Stack:** Vue 3, Vite, Node test runner, Spring Boot, Maven, MySQL SQL docs.

---

### Task 1: Frontend API Contract

**Files:**
- Create: `frontend/package.json`
- Create: `frontend/src/services/imageSearchClient.test.js`
- Create: `frontend/src/services/imageSearchClient.js`

- [ ] **Step 1: Write failing tests**

```js
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
```

- [ ] **Step 2: Run test to verify it fails**

Run: `npm test` from `frontend`.
Expected: FAIL because `imageSearchClient.js` does not export the required functions yet.

- [ ] **Step 3: Implement client**

Create `createImageSearchClient`, `normalizeSearchResults`, upload, text search, image search, list, and task methods.

- [ ] **Step 4: Run test to verify it passes**

Run: `npm test` from `frontend`.
Expected: PASS.

### Task 2: Vue Console

**Files:**
- Create: `frontend/index.html`
- Create: `frontend/src/main.js`
- Create: `frontend/src/App.vue`
- Create: `frontend/src/styles.css`

- [ ] **Step 1: Implement the Vue app**

The app renders upload, text search, image search, status filters, mock result cards, and vector task status.

- [ ] **Step 2: Run build**

Run: `npm run build` from `frontend`.
Expected: Vite builds `dist`.

### Task 3: Java Backend Skeleton

**Files:**
- Create: `backend/pom.xml`
- Create: `backend/src/main/java/com/example/aiimagesearch/AiImageSearchApplication.java`
- Create: `backend/src/main/java/com/example/aiimagesearch/controller/ImageController.java`
- Create: `backend/src/main/java/com/example/aiimagesearch/controller/SearchController.java`
- Create: `backend/src/main/java/com/example/aiimagesearch/controller/VectorTaskController.java`
- Create: `backend/src/main/java/com/example/aiimagesearch/service/VectorAgentClient.java`
- Create: `backend/src/main/java/com/example/aiimagesearch/service/MockVectorAgentClient.java`
- Create: `backend/src/main/resources/application.yml`

- [ ] **Step 1: Implement mock backend endpoints**

Return stable mock data and keep vector integration behind `VectorAgentClient`.

- [ ] **Step 2: Compile backend**

Run with Maven when available: `mvn test` from `backend`.
Expected: Spring context compiles.

### Task 4: Database And Agent Contracts

**Files:**
- Create: `docs/sql/mysql-schema.sql`
- Create: `ai-agent/README.md`
- Create: `README.md`

- [ ] **Step 1: Add schema**

Define `image_asset`, `image_vector_task`, and `image_search_log`.

- [ ] **Step 2: Add agent contract**

Document reserved Python endpoints and Qdrant payload expectations.
