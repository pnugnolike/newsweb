<template>
  <div class="space-y-4">

    <h2 class="text-lg font-bold text-gray-900">
      Comments <span class="text-gray-500 font-normal">({{ comments.length }})</span>
    </h2>

    <div v-if="comments.length > 0" class="relative">
      <input 
        v-model="commentSearchQuery" 
        type="text" 
        placeholder="Search comments..."
        class="w-full p-2 pl-9 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm transition"
      />
      <svg class="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
      </svg>
      <button 
        v-if="commentSearchQuery" 
        @click="commentSearchQuery = ''"
        class="absolute right-2 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 transition"
      >
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
        </svg>
      </button>
      <p v-if="commentSearchQuery && filteredComments.length !== comments.length" class="text-xs text-gray-500 mt-1">
        Found {{ filteredComments.length }} of {{ comments.length }} comments
      </p>
    </div>

    <div v-if="user" class="p-4 bg-gray-50 rounded-lg border border-gray-200">
      <textarea 
        v-model="newCommentText" 
        placeholder="Share your thoughts..."
        rows="3"
        class="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm transition resize-none"
      ></textarea>

      <div class="flex flex-col sm:flex-row sm:items-end gap-3 mt-3">
        <div class="flex-grow">
          <label class="text-xs text-gray-600 block mb-1">Attach image (optional)</label>
          <input 
            type="file" 
            @change="handleFileUpload" 
            accept="image/*"
            class="text-xs w-full file:mr-3 file:py-2 file:px-3 file:rounded file:border-0 file:text-xs file:font-semibold file:bg-gray-200 file:text-gray-700 hover:file:bg-gray-300"
          />
          <p v-if="uploadStatus" :class="uploadStatus.includes('fail') ? 'text-red-500' : 'text-green-600'" class="text-xs mt-1">
            {{ uploadStatus }}
          </p>
        </div>
        <button 
          @click="addComment" 
          :disabled="isSubmitting || (!newCommentText.trim() && !fileToUpload)"
          class="bg-blue-600 text-white font-semibold px-4 py-2 rounded-lg hover:bg-blue-700 disabled:opacity-50 transition text-sm whitespace-nowrap"
        >
          {{ isSubmitting ? 'Sending...' : 'Post' }}
        </button>
      </div>
    </div>

    <div v-else class="p-3 bg-gray-100 rounded-lg text-gray-700 text-sm">
      Sign in to comment
    </div>

    <div v-if="filteredAndDisplayedComments.length > 0" class="space-y-3">
      <div v-for="comment in filteredAndDisplayedComments" :key="comment.id" class="p-3 border border-gray-200 rounded-lg">
        
        <div class="flex items-start gap-2 mb-2">
          <img 
            :src="'https://ui-avatars.com/api/?name=' + encodeURIComponent(comment.userName || 'User') + '&background=2563EB&color=fff&size=32'" 
            class="w-8 h-8 rounded-full flex-shrink-0 object-cover"
          />
          <div class="flex-grow min-w-0">
            <p class="font-semibold text-gray-900 text-sm">{{ comment.userName || 'Anonymous' }}</p>
            <p class="text-xs text-gray-500">{{ formatTime(comment.timestamp.seconds * 1000) }}</p>
          </div>
        </div>

        <div v-if="editingId === comment.id" class="p-2 bg-blue-50 rounded border border-blue-200">
          <textarea v-model="editingText" class="w-full p-2 border border-gray-300 rounded text-xs" rows="2" />
          <div class="flex gap-2 mt-2">
            <button @click="saveEdit(comment.id)" class="bg-green-600 text-white px-3 py-1 rounded text-xs hover:bg-green-700 transition">
              Save
            </button>
            <button @click="cancelEdit" class="bg-gray-400 text-white px-3 py-1 rounded text-xs hover:bg-gray-500 transition">
              Cancel
            </button>
          </div>
        </div>

        <div v-else>
          <p class="text-sm text-gray-800 mb-2 break-words leading-relaxed">{{ comment.text }}</p>
          <img 
            v-if="comment.fileUrl" 
            :src="comment.fileUrl" 
            :alt="'Comment attachment'"
            class="max-w-full h-auto rounded border border-gray-200 max-h-48 cursor-pointer mb-2" 
            @click="window.open(comment.fileUrl)"
          />

          <div v-if="user && user.uid === comment.userId" class="flex gap-3 text-xs">
            <button @click="startEdit(comment)" class="text-blue-600 hover:text-blue-800 font-semibold transition">
              Edit
            </button>
            <button @click="deleteComment(comment.id)" class="text-red-600 hover:text-red-800 font-semibold transition">
              Delete
            </button>
          </div>
        </div>
      </div>
    </div>


    <div v-if="filteredComments.length > MAX_DISPLAY && !commentSearchQuery" class="text-center">
      <button 
        @click="showAll = !showAll" 
        class="text-blue-600 hover:text-blue-700 font-semibold text-sm transition"
      >
        {{ showAll ? 'Show less' : `Show all (${filteredComments.length})` }}
      </button>
    </div>


    <div v-if="comments.length === 0" class="py-4 text-center text-gray-500 text-sm">
      No comments yet
    </div>
    <div v-else-if="filteredComments.length === 0 && commentSearchQuery" class="py-4 text-center text-gray-500 text-sm">
      No comments match your search
    </div>
  </div>
</template>

<script setup>
import { defineProps, ref, onMounted, computed, inject } from 'vue'
import { db, collection, onSnapshot, addDoc, updateDoc, deleteDoc, query, where, orderBy, doc } from '@/firebase'
import axios from 'axios'

const alertToast = inject('alertToast')

const props = defineProps({
  newsId: String,
  user: Object
})

const MAX_DISPLAY = 3
const showAll = ref(false)
const comments = ref([])
const newCommentText = ref('')
const fileToUpload = ref(null)
const isSubmitting = ref(false)
const uploadStatus = ref('')
const editingId = ref(null)
const editingText = ref('')
const commentSearchQuery = ref('')

const filteredComments = computed(() => {
  if (!commentSearchQuery.value.trim()) {
    return comments.value
  }
  
  const query = commentSearchQuery.value.toLowerCase()
  return comments.value.filter(comment => {
    return (
      comment.text?.toLowerCase().includes(query) ||
      comment.userName?.toLowerCase().includes(query)
    )
  })
})

const filteredAndDisplayedComments = computed(() => {
  if (commentSearchQuery.value.trim()) {
    return filteredComments.value
  }
  return showAll.value ? filteredComments.value : filteredComments.value.slice(0, MAX_DISPLAY)
})

const fetchComments = () => {
  const q = query(
    collection(db, "comments"),
    where("newsId", "==", props.newsId),
    orderBy("timestamp", "desc")
  )

  onSnapshot(q, (snapshot) => {
    comments.value = snapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    }))
  }, (error) => {
    console.error("Error fetching comments: ", error)
  })
}

onMounted(fetchComments)

const handleFileUpload = (event) => {
  fileToUpload.value = event.target.files[0]
  uploadStatus.value = fileToUpload.value ? `Ready: ${fileToUpload.value.name}` : ''
}

const uploadFile = async (file) => {
  uploadStatus.value = 'Uploading...'
  const formData = new FormData()
  formData.append('file', file)

  try {
    const response = await axios.post('https://newsweb-udjj.onrender.com/api/files/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    uploadStatus.value = 'Upload successful'
    return response.data.fileUrl
  } catch (error) {
    console.error('File upload failed:', error)
    uploadStatus.value = 'Upload failed'
    return null
  }
}

const addComment = async () => {
  if (!newCommentText.value.trim() && !fileToUpload.value) return
  if (!props.user) return

  isSubmitting.value = true
  let fileUrl = null

  if (fileToUpload.value) {
    fileUrl = await uploadFile(fileToUpload.value)
    if (!fileUrl) {
      isSubmitting.value = false
      alertToast.value?.error('Upload failed', 'Could not upload the file')
      return
    }
  }
  
  const commentData = {
    newsId: props.newsId,
    userId: props.user.uid,
    userName: props.user.displayName || props.user.email,
    text: newCommentText.value.trim(),
    fileUrl: fileUrl,
    timestamp: new Date()
  }

  try {
    await addDoc(collection(db, "comments"), commentData)
    newCommentText.value = ''
    fileToUpload.value = null
    uploadStatus.value = ''
    const fileInput = document.querySelector('input[type="file"]')
    if (fileInput) fileInput.value = ''
    alertToast.value?.success('Comment posted!', 'Your comment has been added')
  } catch (e) {
    console.error("Error adding comment: ", e)
    alertToast.value?.error('Failed to post comment', 'Please try again')
  } finally {
    isSubmitting.value = false
  }
}

const startEdit = (comment) => {
  editingId.value = comment.id
  editingText.value = comment.text
}

const cancelEdit = () => {
  editingId.value = null
  editingText.value = ''
}

const saveEdit = async (id) => {
  if (!editingText.value.trim()) return
  try {
    await updateDoc(doc(db, "comments", id), {
      text: editingText.value.trim(),
      updatedAt: new Date()
    })
    cancelEdit()
    alertToast.value?.success('Comment updated', 'Your changes have been saved')
  } catch (e) {
    console.error("Error updating comment: ", e)
    alertToast.value?.error('Failed to update comment', 'Please try again')
  }
}

const deleteComment = async (id) => {
  alertToast.value?.warning('Delete comment', 'Are you sure you want to delete this comment?', {
    action: async () => {
      try {
        await deleteDoc(doc(db, "comments", id))
        alertToast.value?.success('Comment deleted', 'Your comment has been removed')
      } catch (e) {
        console.error("Error deleting comment: ", e)
        alertToast.value?.error('Failed to delete comment', 'Please try again')
      }
    }
  })
}

const formatTime = (timestamp) => {
  return new Date(timestamp).toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>
