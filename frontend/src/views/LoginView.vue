<template>
  <div class="page-shell">
    <v-card class="auth-panel">
      <v-card-title class="text-h5 font-weight-bold">Login</v-card-title>
      <v-card-text>
        <v-alert v-if="error" type="error" variant="tonal" class="mb-4">
          {{ error }}
        </v-alert>

        <v-form @submit.prevent="submit">
          <v-text-field
            v-model="form.username"
            label="Username"
            autocomplete="username"
            prepend-inner-icon="mdi-account"
            :disabled="loading"
          />
          <v-text-field
            v-model="form.password"
            label="Password"
            type="password"
            autocomplete="current-password"
            prepend-inner-icon="mdi-lock"
            :disabled="loading"
          />
          <v-btn
            type="submit"
            color="primary"
            block
            size="large"
            :loading="loading"
            prepend-icon="mdi-login"
          >
            Login
          </v-btn>
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-btn :to="{ name: 'register' }" variant="text">
          Create account
        </v-btn>
      </v-card-actions>
    </v-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { api } from '@/api/client'

const emit = defineEmits(['authenticated'])
const route = useRoute()
const router = useRouter()
const loading = ref(false)
const error = ref('')
const form = reactive({
  username: '',
  password: ''
})

async function submit() {
  error.value = ''
  loading.value = true

  try {
    const user = await api.login(form)
    emit('authenticated', user)
    router.push(route.query.redirect || { name: 'concerts' })
  } catch (err) {
    error.value = err.message
  } finally {
    loading.value = false
  }
}
</script>
