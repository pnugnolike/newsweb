<template>
  <div id="app" class="min-h-screen bg-white flex flex-col">
    
    <AlertToast ref="alertToast" />

    <header class="border-b border-gray-200 sticky top-0 z-20 bg-white" :class="{ 'hidden': isAuthPage }">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-3 flex justify-between items-center">
        <router-link to="/" class="text-lg font-semibold text-gray-900 hover:text-gray-700 transition">
          News Feed
        </router-link>
        <nav>
          <router-link to="/" class="text-sm text-gray-600 hover:text-gray-900 transition">
            Home
          </router-link>
        </nav>
      </div>
    </header>


    <main class="flex-grow">

      <div v-if="isAuthPage" class="w-full">
        <router-view :alertToast="alertToast" />
      </div>

      <div v-else class="flex justify-center w-full">
        <div class="w-full max-w-5xl px-4 sm:px-6 lg:px-8 py-8">
          <Auth />
          
          <router-view :alertToast="alertToast" />
        </div>
      </div>
    </main>

    <footer class="border-t border-gray-200 text-center py-2 text-xs text-gray-500" :class="{ 'hidden': isAuthPage }">
      &copy; 2025 News Feed | Vue 3
    </footer>

  </div>
</template>

<script setup>

import { RouterView, RouterLink, useRoute } from 'vue-router'
import { ref, provide, computed, onMounted } from 'vue'
import Auth from './components/Auth.vue'
import { onAuthStateChanged } from 'firebase/auth'
import AlertToast from './components/AlertToast.vue'

const route = useRoute()
const alertToast = ref(null)
provide('alertToast', alertToast)

const isAuthPage = computed(() => {
  return route.path === '/login' || route.path === '/signup'
})
</script>

<style scoped>
.router-link-exact-active {
  color: #1f2937;
  font-weight: 600;
}
</style>
