import './assets/main.css'

import { createApp } from 'vue'
import Toast from "vue-toastification"
import App from './App.vue'
import "vue-toastification/dist/index.css"

const app = createApp(App);

const option = {
    position: "top-right",
    timeout: 5000,
    pauseOnHover: true,
    closeButton: "button",
    icon: true
}
app.use(Toast, option)
app.mount('#app');




