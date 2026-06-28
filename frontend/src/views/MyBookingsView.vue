<template>
  <div class="page-shell">
    <div>
      <h1 class="page-title">My bookings</h1>
      <p class="page-subtitle">Review active and cancelled ticket bookings.</p>
    </div>

    <v-alert v-if="message.text" :type="message.type" variant="tonal" class="mt-5">
      {{ message.text }}
    </v-alert>

    <v-progress-linear v-if="loading" indeterminate color="primary" class="mt-6" />

    <v-card v-else class="mt-6">
      <v-list v-if="bookings.length" lines="three">
        <v-list-item v-for="booking in bookings" :key="booking.id" class="booking-row py-4">
          <template #prepend>
            <v-avatar :color="booking.status === 'ACTIVE' ? 'success' : 'secondary'" variant="tonal">
              <v-icon :icon="booking.status === 'ACTIVE' ? 'mdi-ticket-confirmation' : 'mdi-ticket-off'" />
            </v-avatar>
          </template>

          <v-list-item-title class="font-weight-bold">
            {{ booking.artist }} - {{ booking.concertTitle }}
          </v-list-item-title>
          <v-list-item-subtitle>
            {{ booking.venue }} · {{ formatDate(booking.concertDateTime) }}
          </v-list-item-subtitle>
          <v-list-item-subtitle>
            {{ booking.quantity }} ticket{{ booking.quantity === 1 ? '' : 's' }} · {{ formatMoney(booking.totalPrice) }}
          </v-list-item-subtitle>

          <template #append>
            <div class="d-flex align-center ga-3">
              <v-chip
                :color="booking.status === 'ACTIVE' ? 'success' : 'secondary'"
                size="small"
                variant="tonal"
              >
                {{ booking.status }}
              </v-chip>
              <v-btn
                v-if="booking.status === 'ACTIVE'"
                color="error"
                variant="tonal"
                size="small"
                :loading="cancelingId === booking.id"
                prepend-icon="mdi-cancel"
                @click="cancelBooking(booking)"
              >
                Cancel
              </v-btn>
            </div>
          </template>
        </v-list-item>
      </v-list>

      <v-empty-state
        v-else
        icon="mdi-ticket-outline"
        title="No bookings yet"
        text="Bookings appear here after you reserve tickets."
      >
        <template #actions>
          <v-btn :to="{ name: 'concerts' }" color="primary" prepend-icon="mdi-ticket">
            Browse concerts
          </v-btn>
        </template>
      </v-empty-state>
    </v-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { api } from '@/api/client'

const bookings = ref([])
const loading = ref(true)
const cancelingId = ref(null)
const message = reactive({ type: 'success', text: '' })

onMounted(loadBookings)

async function loadBookings() {
  loading.value = true
  try {
    bookings.value = await api.myBookings()
  } catch (error) {
    showMessage('error', error.message)
  } finally {
    loading.value = false
  }
}

async function cancelBooking(booking) {
  cancelingId.value = booking.id
  try {
    await api.cancelBooking(booking.id)
    showMessage('success', 'Booking cancelled.')
    await loadBookings()
  } catch (error) {
    showMessage('error', error.message)
  } finally {
    cancelingId.value = null
  }
}

function showMessage(type, text) {
  message.type = type
  message.text = text
}

function formatMoney(value) {
  return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(value)
}

function formatDate(value) {
  return new Intl.DateTimeFormat('en-US', {
    dateStyle: 'medium',
    timeStyle: 'short'
  }).format(new Date(value))
}
</script>
