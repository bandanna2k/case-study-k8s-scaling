<template>
  <div id="app">
    <div class="container">
      <h1>Time Selector</h1>

      <div class="slider-container">
        <label class="slider-label">Select time duration:</label>
        <input
            type="range"
            class="slider"
            v-model="sliderValue"
            :min="minValue"
            :max="maxValue"
            :step="step"
        />
        <div class="value-display">
          {{ displayValue }} milliseconds
        </div>
      </div>

      <button class="submit-btn" @click="handleSubmit">
        Start Load Generation
      </button>

      <p>{{ message }}</p>
    </div>
  </div>
</template>

<script>
import {ref, computed} from 'vue'

export default {
  name: 'HelloWorld',
  setup() {
    const sliderValue = ref(1000)
    const minValue = ref(100)
    const maxValue = ref(10 * 1000)
    const step = ref(100)
    const message = ref("")

    const displayValue = computed(() => {
      return sliderValue.value
    })

    const handleSubmit = async () => {
      console.log('displayValue.value:', displayValue.value);
      await fetch('/v1/load', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({
              delay: parseFloat(displayValue.value),
            })
          })
          .then(response => {
            if(response.status === 200) {
              message.value = "Load generated at " + response.json();
            }
            else {
              message.value = "Failed to generate load.";
            }
          })
          .catch(reason => {
            message.value = "Exception: " + reason;
          });
    }

    return {
      sliderValue,
      minValue,
      maxValue,
      step,
      displayValue,
      handleSubmit,
      message
    }
  }
}

</script>

<style scoped>
</style>