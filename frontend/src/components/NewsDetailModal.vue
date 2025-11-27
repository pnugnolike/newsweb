<template>
  <div v-if="isOpen" class="fixed inset-0 z-50 bg-black/40 flex items-center justify-center p-4">
    <div class="bg-white rounded-lg max-w-2xl w-full max-h-[90vh] overflow-y-auto">
      <!-- Close Button -->
      <div class="sticky top-0 bg-white border-b border-gray-200 p-4 flex justify-end">
        <button @click="$emit('close')" class="text-gray-400 hover:text-gray-600 transition p-1">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
          </svg>
        </button>
      </div>

      <!-- Content -->
      <div class="p-6 sm:p-8">
        <!-- Image -->
        <div v-if="news?.urlToImage" class="w-full h-64 bg-gray-100 rounded-lg overflow-hidden mb-6">
          <img :src="news.urlToImage" :alt="news.title" class="w-full h-full object-cover">
        </div>

        <!-- Meta Info -->
        <div class="flex items-center gap-3 mb-4 text-sm text-gray-600">
          <span class="font-semibold text-blue-600">{{ news?.source?.name }}</span>
          <span>{{ formatDate(news?.publishedAt) }}</span>
        </div>

        <!-- Title -->
        <h1 class="text-2xl sm:text-3xl font-bold text-gray-900 mb-4">{{ news?.title }}</h1>

        <!-- Description -->
        <p class="text-base text-gray-700 mb-6 leading-relaxed">{{ news?.description }}</p>

        <!-- Read Full Article Link -->
        <a :href="news?.url" target="_blank" rel="noopener noreferrer"
           class="inline-flex items-center gap-2 text-blue-600 hover:text-blue-700 font-semibold mb-8">
          Read full article
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
          </svg>
        </a>

        <!-- Divider -->
        <div class="border-t border-gray-200 pt-8">
          <!-- Comments Section -->
          <CommentSection v-if="news?.title" :newsId="news.title" :user="user" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'
import CommentSection from './CommentSection.vue'

defineProps({
  isOpen: Boolean,
  news: Object,
  user: Object
})

defineEmits(['close'])

const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}
</script>
