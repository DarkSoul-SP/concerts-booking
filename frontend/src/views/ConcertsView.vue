<template>
  <div class="page-shell">
    <div>
      <h1 class="page-title">Future concerts</h1>
      <p class="page-subtitle">Choose a quantity and book available seats for upcoming shows.</p>
    </div>

    <v-alert v-if="message.text" :type="message.type" variant="tonal" class="mt-5">
      {{ message.text }}
    </v-alert>

    <v-progress-linear v-if="loading" indeterminate color="primary" class="mt-6" />

    <div v-else class="concert-grid">
      <v-card v-for="concert in concerts" :key="concert.id">
        <v-card-title class="text-h6 font-weight-bold">
          {{ concert.artist }}
        </v-card-title>
        <v-card-subtitle>{{ concert.title }}</v-card-subtitle>

        <v-card-text>
          <div class="d-flex align-center mb-2">
            <v-icon icon="mdi-map-marker" size="18" class="mr-2" />
            <span>{{ concert.venue }}</span>
          </div>
          <div class="d-flex align-center mb-2">
            <v-icon icon="mdi-calendar-clock" size="18" class="mr-2" />
            <span>{{ formatDate(concert.concertDateTime) }}</span>
          </div>
          <div class="d-flex align-center mb-2">
            <v-icon icon="mdi-currency-usd" size="18" class="mr-2" />
            <span>{{ formatMoney(concert.ticketPrice) }} per ticket</span>
          </div>
          <div class="d-flex align-center">
            <v-icon icon="mdi-seat" size="18" class="mr-2" />
            <span>{{ concert.availableSeats }} of {{ concert.totalSeats }} seats available</span>
          </div>

          <v-divider class="my-4" />

          <v-text-field
            v-model.number="quantities[concert.id]"
            label="Tickets"
            type="number"
            min="1"
            :max="Math.min(20, concert.availableSeats)"
            :disabled="!user || concert.availableSeats === 0 || bookingId === concert.id"
            prepend-inner-icon="mdi-ticket"
          />
        </v-card-text>

        <v-card-actions>
          <v-btn
            color="primary"
            variant="flat"
            block
            :loading="bookingId === concert.id"
            :disabled="!canBook(concert)"
            prepend-icon="mdi-ticket-confirmation"
            @click="book(concert)"
          >
            {{ user ? 'Book tickets' : 'Login to book' }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { api } from '@/api/client'
import { authState } from '@/router'

const user = computed(() => authState.user)
const concerts = ref([])
const loading = ref(true)
const bookingId = ref(null)
const quantities = reactive({})
const message = reactive({ type: 'success', text: '' })

onMounted(loadConcerts)

async function loadConcerts() {
  loading.value = true
  try {
    concerts.value = await api.concerts()
    concerts.value.forEach((concert) => {
      quantities[concert.id] = quantities[concert.id] || 1
    })
  } catch (error) {
    showMessage('error', error.message)
  } finally {
    loading.value = false
  }
}

function canBook(concert) {
  const quantity = Number(quantities[concert.id])
  return user.value && concert.availableSeats > 0 && quantity >= 1 && quantity <= concert.availableSeats && quantity <= 20
}

async function book(concert) {
  if (!canBook(concert)) {
    return
  }

  bookingId.value = concert.id
  try {
    const quantity = Number(quantities[concert.id])
    await api.book(concert.id, quantity)
    showMessage('success', `${quantity} ticket${quantity === 1 ? '' : 's'} booked for ${concert.artist}.`)
    await loadConcerts()
  } catch (error) {
    showMessage('error', error.message)
  } finally {
    bookingId.value = null
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
