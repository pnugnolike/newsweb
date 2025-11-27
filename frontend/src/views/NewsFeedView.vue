<template>
  <div class="bg-white">
    <div class="max-w-6xl mx-auto px-4 sm:px-6 py-6 sm:py-8">
      <h1 class="text-3xl sm:text-4xl font-bold text-gray-900">News</h1>
      <p class="text-gray-600 text-sm mt-1">Stay informed with the latest stories</p>
    </div>

    <div class="max-w-6xl mx-auto px-4 sm:px-6">
      <!-- Search Bar -->
      <div class="mb-8 flex gap-2">
        <input v-model="searchQuery" @keyup.enter="searchNews" type="text" placeholder="Search news..."
          class="flex-grow px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition" />
        <button @click="searchNews"
          class="px-6 py-2 bg-blue-600 text-white font-semibold rounded-lg hover:bg-blue-700 transition duration-200 flex items-center gap-2">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
              d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
          </svg>
          Search
        </button>
        <button v-if="searchQuery" @click="clearSearch"
          class="px-4 py-2 text-gray-600 hover:text-gray-800 font-medium transition">
          Clear
        </button>
      </div>

      <div v-if="!user" class="mb-6 p-3 bg-gray-50 border border-gray-200 rounded text-gray-700 text-sm">
        Sign in to comment on stories
      </div>

      <div v-if="loading" class="flex justify-center py-12">
        <div class="text-gray-400">
          <svg class="w-8 h-8 animate-spin" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"></path>
          </svg>
        </div>
      </div>

      <div v-else-if="news.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 pb-12">
        <article v-for="item in news" :key="item.title" @click="openDetail(item)"
          class="group cursor-pointer flex flex-col">
          <div class="relative w-full h-48 bg-gray-100 rounded overflow-hidden mb-3">
            <img v-if="item.urlToImage" :src="item.urlToImage" :alt="item.title"
              class="w-full h-full object-cover group-hover:scale-105 transition duration-300" />
            <div v-else class="w-full h-full flex items-center justify-center text-gray-300 text-xs">
              No image
            </div>
          </div>

          <div class="flex-grow flex flex-col">
            <div class="flex items-center gap-2 mb-2">
              <span class="text-xs font-semibold text-blue-600">{{ item.source.name }}</span>
              <span class="text-xs text-gray-500">{{ formatDate(item.publishedAt) }}</span>
            </div>

            <h2 class="text-base font-bold text-gray-900 mb-2 line-clamp-2 group-hover:text-blue-600 transition">
              {{ item.title }}
            </h2>

            <p class="text-sm text-gray-600 mb-3 flex-grow line-clamp-2">
              {{ item.description }}
            </p>

            <button
              class="text-blue-600 hover:text-blue-700 text-sm font-semibold inline-flex items-center gap-1 transition">
              Read more
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
              </svg>
            </button>
          </div>
        </article>
      </div>

      <div v-else class="py-16 text-center">
        <p class="text-lg font-semibold text-gray-900 mb-1">No news found</p>
        <p class="text-sm text-gray-600">Try adjusting your search terms</p>
      </div>
    </div>

    <NewsDetailModal :isOpen="showDetailModal" :news="selectedNews" :user="user" @close="showDetailModal = false" />
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue'
import axios from 'axios'
import { auth } from '@/firebase'
import { onAuthStateChanged } from 'firebase/auth'
import NewsDetailModal from '@/components/NewsDetailModal.vue'

const alertToast = inject('alertToast')

const news = ref([])
const loading = ref(true)
const user = ref(null)
const showDetailModal = ref(false)
const selectedNews = ref(null)
const searchQuery = ref('')

onMounted(() => {
  onAuthStateChanged(auth, (currentUser) => {
    user.value = currentUser
  })
  fetchNews()
})

const fetchNews = async () => {
  loading.value = true
  try {
    const response = await axios.get('https://newsweb-udjj.onrender.com/api/external-news')
    if (response.data?.articles) {
      news.value = response.data.articles.filter(
        item => item.urlToImage && item.description && item.title
      )
      alertToast.value?.info('News loaded', `Found ${news.value.length} stories`)
    }
  } catch (error) {
    console.error('Error fetching news:', error)
    alertToast.value?.error('Failed to load news', 'Check your connection and try again')
  } finally {
    loading.value = false
  }
}

const searchNews = async () => {
  if (!searchQuery.value.trim()) return

  loading.value = true
  try {
    const response = await axios.get('https://newsweb-udjj.onrender.com/api/search-news', {
      params: { query: searchQuery.value }
    })

    if (response.data?.articles) {
      news.value = response.data.articles.filter(
        item => item.urlToImage && item.description && item.title
      )
      alertToast.value?.info('Search complete', `Found ${news.value.length} results for "${searchQuery.value}"`)
    } else {
      news.value = []
    }
  } catch (error) {
    console.error('Error searching news:', error)
    alertToast.value?.error('Search failed', 'Please try again later')
  } finally {
    loading.value = false
  }
}

const clearSearch = () => {
  searchQuery.value = ''
  fetchNews()
}

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

const openDetail = (item) => {
  selectedNews.value = item
  showDetailModal.value = true
}
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  overflow: hidden;
}
</style>
