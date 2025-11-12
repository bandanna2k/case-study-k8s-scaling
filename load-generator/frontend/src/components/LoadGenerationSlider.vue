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
        <div>
          {{ displayResults }}
        </div>
      </div>

      <p>{{ message }}</p>
    </div>
  </div>
</template>

<script>
import {ref, computed} from 'vue'
import PollingTimer from 'polling-timer';

export default {
  name: 'Load Generator',
  setup() {
    const sliderValue = ref(1000)
    const minValue = ref(100)
    const maxValue = ref(10 * 1000)
    const step = ref(100)
    const message = ref("")
    const results = ref("")

    const lastSubmittedValue = ref(1000)
    const displayValue = computed(() => {
      return sliderValue.value
    })
    const displayResults = computed(() => {
      return results.value
    })

    const timer = new PollingTimer(1000, 0);
    timer.setRunCallback(async () => {
      if(displayValue.value !== lastSubmittedValue.value) {
        lastSubmittedValue.value = displayValue.value;
        await postDelay(lastSubmittedValue.value);
      }
      setResults();
    });
    timer.start();

    async function postDelay(delayMillis) {
      await fetch('/v1/load', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({
          delay: parseInt(delayMillis),
        })
      })
      .then(response => {
        if(response.status === 200) {
          message.value = "Firing every " + lastSubmittedValue.value + " ms.";
        }
        else {
          message.value = "Failed to generate load.";
        }
      })
      .catch(reason => {
        message.value = "Exception: " + reason;
      });
    }

    async function setResults() {
      await fetch('/v1/results', { method: 'GET' })
          .then(response => {
            if(response.status === 200) {
              return response.json();
            }
            else {
              message.value = "Failed to get results.";
              throw new Error("Failed to get results.")
            }
          })
          .then(r => {
            results.value = r;
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
      lastSubmittedValue,
      displayValue,
      displayResults,
      message
    }
  }
}

</script>

<style scoped>
</style>