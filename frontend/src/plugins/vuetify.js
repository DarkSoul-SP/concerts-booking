import { createVuetify } from 'vuetify'

export default createVuetify({
  theme: {
    defaultTheme: 'concertTheme',
    themes: {
      concertTheme: {
        dark: false,
        colors: {
          primary: '#1f6f8b',
          secondary: '#8f5f2a',
          accent: '#2f8f5b',
          error: '#b3261e',
          warning: '#a66300',
          success: '#287d3c',
          background: '#f7f8fa',
          surface: '#ffffff'
        }
      }
    }
  },
  defaults: {
    VBtn: {
      rounded: 'sm'
    },
    VCard: {
      rounded: 'sm',
      elevation: 1
    },
    VTextField: {
      variant: 'outlined',
      density: 'comfortable'
    },
    VNumberInput: {
      variant: 'outlined',
      density: 'comfortable'
    }
  }
})
