<template>
  <div class="flex justify-center items-center min-h-screen bg-white">
    <div class="w-full max-w-md px-4">
      <div class="bg-gray-50 p-6 rounded-lg border border-gray-200">
        <h1 class="text-2xl font-bold text-center text-gray-900 mb-6">Create Account</h1>

        <form @submit.prevent="handleSubmit" class="space-y-4">
          <div>
            <label class="block font-semibold text-gray-700 text-sm mb-2">Display Name</label>
            <input 
              v-model="displayName" 
              type="text" 
              required 
              class="w-full p-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm transition" 
              placeholder="John Doe"
            />
          </div>

          <div>
            <label class="block font-semibold text-gray-700 text-sm mb-2">Email</label>
            <input 
              v-model="email" 
              type="email" 
              required 
              class="w-full p-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm transition" 
              placeholder="name@example.com"
            />
          </div>

          <div>
            <label class="block font-semibold text-gray-700 text-sm mb-2">Password</label>
            <input 
              v-model="password" 
              type="password" 
              required 
              minlength="6" 
              class="w-full p-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm transition" 
              placeholder="Min 6 characters"
            />
          </div>

          <p v-show="errorMessage" class="text-red-600 text-xs text-center bg-red-50 p-2 rounded border border-red-200">
            {{ errorMessage }}
          </p>

          <button 
            type="submit" 
            :disabled="loading" 
            class="w-full bg-blue-600 text-white font-bold py-2 rounded-lg hover:bg-blue-700 transition disabled:opacity-50 text-sm"
          >
            {{ loading ? 'Creating account...' : 'Sign Up' }}
          </button>

          <div class="relative my-4">
            <div class="absolute inset-0 flex items-center"><div class="w-full border-t border-gray-300"></div></div>
            <div class="relative flex justify-center text-xs"><span class="px-2 bg-gray-50 text-gray-500">or</span></div>
          </div>

          <button 
            type="button" 
            @click="handleGoogleLogin" 
            class="w-full flex justify-center items-center gap-2 border border-gray-300 bg-white text-gray-700 py-2 rounded-lg hover:bg-gray-100 transition font-semibold text-sm"
          >
            <img src="https://www.svgrepo.com/show/475656/google-color.svg" class="w-4 h-4" alt="Google" />
            Continue with Google
          </button>

          <p class="text-center text-xs text-gray-600 pt-2">
            Already have an account?
            <router-link to="/login" class="text-blue-600 hover:text-blue-700 font-semibold transition">
              Sign In
            </router-link>
          </p>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, inject } from 'vue'
import { createUserWithEmailAndPassword, updateProfile, GoogleAuthProvider, signInWithRedirect, getRedirectResult } from 'firebase/auth'
import { auth } from '@/firebase'
import { useRouter } from 'vue-router'

const router = useRouter()
const alertToast = inject('alertToast')
const displayName = ref('')
const email = ref('')
const password = ref('')
const loading = ref(false)
const errorMessage = ref('')

const handleSubmit = async () => {
  loading.value = true
  errorMessage.value = ''

  try {
    const userCredential = await createUserWithEmailAndPassword(auth, email.value, password.value)
    if (displayName.value) {
      await updateProfile(userCredential.user, { displayName: displayName.value })
    }
    alertToast.value?.success('Account created!', 'Welcome to the news feed')
    router.push('/')
  } catch (error) {
    switch (error.code) {
      case 'auth/email-already-in-use': 
        errorMessage.value = 'Email already in use'
        alertToast.value?.error('Sign up failed', 'This email is already registered')
        break
      case 'auth/invalid-email': 
        errorMessage.value = 'Invalid email format'
        alertToast.value?.error('Invalid email', 'Please check the email format')
        break
      case 'auth/weak-password': 
        errorMessage.value = 'Password must be 6+ characters'
        alertToast.value?.error('Weak password', 'Password must be 6+ characters')
        break
      default: 
        errorMessage.value = error.message
        alertToast.value?.error('Error', error.message)
    }
  } finally {
    loading.value = false
  }
}

const handleGoogleLogin = async () => {
  try {
    const provider = new GoogleAuthProvider()
    await signInWithRedirect(auth, provider)
  } catch (error) {
    errorMessage.value = 'Could not initiate Google signup'
    alertToast.value?.error('Google signup failed', error.message)
  }
}

getRedirectResult(auth)
  .then((result) => {
    if (result) {

      alertToast.value?.success('Google signup successful!', 'Welcome')
      router.push('/')
    }
  })
  .catch((error) => {
    errorMessage.value = 'Google signup failed'
    alertToast.value?.error('Google signup failed', error.message)
  })
</script>