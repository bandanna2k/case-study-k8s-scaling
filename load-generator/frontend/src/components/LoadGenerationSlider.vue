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
                    {{ displayValue }} seconds
                </div>
            </div>

            <button class="submit-btn" @click="handleSubmit">
                Start Load Generation
            </button>
        </div>
    </div>
</template>

<script>
import { ref, computed } from 'vue'

export default {
  name: 'HelloWorld',
  setup() {
    const sliderValue = ref(1.0)
    const minValue = ref(0.1)
    const maxValue = ref(10.0)
    const step = ref(0.1)

    const displayValue = computed(() => {
      return parseFloat(sliderValue.value).toFixed(1)
    })

    const handleSubmit = () => {
        const response = await fetch('/load', {
              method: 'POST',
              headers: {
                'Content-Type': 'application/json',
              },
              body: JSON.stringify({
                timeValue: parseFloat(this.displayValue),
                timestamp: new Date().toISOString()
              })
            });
    }

    return {
      sliderValue,
      minValue,
      maxValue,
      step,
      displayValue,
      handleSubmit
    }
  }
}

</script>

<style scoped>
</style>