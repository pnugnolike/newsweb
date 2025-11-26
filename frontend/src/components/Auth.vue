<template>
  <div v-if="user" class="flex justify-center w-full mb-8">
    <div class="bg-gray-50 p-4 rounded-lg border border-gray-200 flex justify-between items-center gap-4 max-w-md w-full">
      <div class="flex items-center gap-3">
        <img 
          :src="user.photoURL || 'https://ui-avatars.com/api/?name=' + (user.displayName || user.email) + '&background=2563EB&color=fff&size=40'" 
          class="w-10 h-10 rounded-full object-cover"
        />
        <div class="min-w-0">
          <p class="font-semibold text-gray-900 text-sm truncate">{{ user.displayName || 'User' }}</p>
          <p class="text-xs text-gray-500 truncate">{{ user.email }}</p>
        </div>
      </div>
      <button 
        @click="handleLogout" 
        class="bg-red-600 hover:bg-red-700 text-white font-semibold px-3 py-2 rounded-lg text-sm transition whitespace-nowrap"
      >
        Sign out
      </button>
    </div>
  </div>

  <div v-else class="flex justify-center w-full mb-8">
    <div class="bg-gray-50 p-4 rounded-lg border border-gray-200 flex justify-between items-center gap-4 max-w-md w-full">
      <div class="flex-grow">
        <p class="font-semibold text-gray-900 text-sm">Welcome to News Feed</p>
        <p class="text-xs text-gray-500">Sign in to comment and interact</p>
      </div>
      <div class="flex gap-2">
        <router-link 
          to="/login" 
          class="bg-blue-600 hover:bg-blue-700 text-white font-semibold px-4 py-2 rounded-lg text-sm transition whitespace-nowrap"
        >
          Login
        </router-link>
        <router-link 
          to="/signup" 
          class="bg-gray-200 hover:bg-gray-300 text-gray-800 font-semibold px-4 py-2 rounded-lg text-sm transition whitespace-nowrap"
        >
          Sign Up
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue'
import { auth } from '@/firebase'
import { signOut, onAuthStateChanged } from 'firebase/auth'

const alertToast = inject('alertToast')
const user = ref(null)

onMounted(() => {
  onAuthStateChanged(auth, (currentUser) => {
    user.value = currentUser
  })
})

const handleLogout = async () => {
  alertToast.value?.warning('Sign out', 'Are you sure you want to sign out?', {
    action: async () => {
      try {
        await signOut(auth)
        alertToast.value?.info('Signed out', 'You have been successfully logged out')
      } catch (error) {
        alertToast.value?.error('Error', 'Failed to sign out')
      }
    }
  })
}
</script>
