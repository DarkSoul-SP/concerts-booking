<template>
  <v-app>
    <v-app-bar color="surface" elevation="1">
      <v-app-bar-title class="font-weight-bold">Concerts Booking</v-app-bar-title>

      <v-spacer />

      <v-btn :to="{ name: 'concerts' }" variant="text" prepend-icon="mdi-ticket-confirmation">
        Concerts
      </v-btn>
      <v-btn
        v-if="user"
        :to="{ name: 'my-bookings' }"
        variant="text"
        prepend-icon="mdi-calendar-check"
      >
        My bookings
      </v-btn>

      <v-divider vertical class="mx-2" />

      <template v-if="user">
        <span class="text-body-2 mr-3 d-none d-sm-inline">{{ user.username }}</span>
        <v-btn variant="tonal" color="primary" prepend-icon="mdi-logout" @click="logout">
          Logout
        </v-btn>
      </template>
      <template v-else>
        <v-btn :to="{ name: 'login' }" variant="text" prepend-icon="mdi-login">
          Login
        </v-btn>
        <v-btn :to="{ name: 'register' }" color="primary" variant="flat" prepend-icon="mdi-account-plus">
          Register
        </v-btn>
      </template>
    </v-app-bar>

    <v-main>
      <router-view @authenticated="onAuthenticated" />
    </v-main>

    <v-snackbar v-model="snackbar.visible" color="error" timeout="4000">
      {{ snackbar.message }}
    </v-snackbar>
  </v-app>
</template>

<script setup>
import { computed, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { api } from '@/api/client'
import { authState } from '@/router'

const router = useRouter()
const user = computed(() => authState.user)
const snackbar = reactive({ visible: false, message: '' })

function onAuthenticated(user) {
  authState.user = user
  authState.checked = true
}

async function logout() {
  try {
    await api.logout()
  } catch (error) {
    snackbar.message = error.message
    snackbar.visible = true
  } finally {
    authState.user = null
    authState.checked = true
    router.push({ name: 'concerts' })
  }
}
</script>
