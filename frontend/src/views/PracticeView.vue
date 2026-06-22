<template>
  <div class="practice-view">
    <PracticeGuide
      v-if="step === 'guide'"
      :practice="practice"
      @start="step = 'game'"
    />

    <div v-if="step === 'game' && currentAction" class="game-step">
      <div class="step-indicator">
        步骤 {{ stepIndex + 1 }} / {{ totalSteps }}
      </div>

      <!-- quiz -->
      <div v-if="currentAction.action === 'quiz'" class="quiz-step">
        <p class="instruction">{{ currentAction.instruction }}</p>
        <div class="options-list">
          <button
            v-for="(opt, i) in currentAction.options" :key="i"
            class="option-btn"
            :class="{ correct: answered && i === currentAction.correct, wrong: answered && i === selectedAnswer && i !== currentAction.correct }"
            :disabled="answered"
            @click="answerQuiz(i)"
          >
            {{ opt }}
          </button>
        </div>
        <div v-if="answered" class="feedback">
          {{ selectedAnswer === currentAction.correct ? '✅ 正确！' : '❌ 再想想哦' }}
        </div>
        <button v-if="answered" class="next-btn" @click="nextStep">继续 →</button>
      </div>

      <!-- identify -->
      <div v-if="currentAction.action === 'identify'" class="identify-step">
        <p class="instruction">{{ currentAction.instruction }}</p>
        <div class="identify-options">
          <div
            v-for="(opt, i) in currentAction.options" :key="i"
            class="identify-card"
            :class="{
              selected: selectedItems.includes(i),
              correct: answered && correctAnswers.includes(i),
              wrong: answered && selectedItems.includes(i) && !correctAnswers.includes(i)
            }"
            @click="toggleSelect(i)"
          >
            <span class="identify-icon">{{ opt }}</span>
          </div>
        </div>
        <p v-if="!answered" class="hint">点击选择（可多选），选好后确认</p>
        <div v-if="answered" class="feedback">
          {{ identifyCorrect ? '✅ 完全正确！' : '⚠️ 有对有错' }}
        </div>
        <button v-if="!answered" class="confirm-btn" @click="answerIdentify">确认选择</button>
        <button v-if="answered" class="next-btn" @click="nextStep">继续 →</button>
      </div>

      <!-- simulate -->
      <div v-if="currentAction.action === 'simulate'" class="simulate-step">
        <p class="instruction">{{ currentAction.instruction }}</p>
        <p class="sim-desc">{{ currentAction.description }}</p>
        <button class="action-btn" @click="completeSimulate">{{
          simulating ? '操作中...' : '完成操作'
        }}</button>
      </div>

      <!-- minigame -->
      <div v-if="currentAction.action === 'minigame'" class="minigame-step">
        <p class="instruction">{{ currentAction.instruction }}</p>
        <component
          :is="minigameComponent"
          v-if="minigameComponent"
          @result="onMinigameResult"
        />
      </div>
    </div>

    <PracticeScore
      v-if="step === 'score'"
      :score="score" :total="totalSteps"
      :unlocked="unlockedKnowledge"
      @retry="retry"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, computed, markRaw } from 'vue'
import { useRoute } from 'vue-router'
import { getPracticeById } from '../api/practice'
import PracticeGuide from '../components/practice/PracticeGuide.vue'
import PracticeScore from '../components/practice/PracticeScore.vue'
import AphidGame from '../components/minigame/AphidGame.vue'

const route = useRoute()
const practice = ref(null)
const step = ref('guide')
const stepIndex = ref(0)
const score = ref(0)
const unlockedKnowledge = ref([])
const answered = ref(false)
const selectedAnswer = ref(-1)
const selectedItems = ref([])
const simulating = ref(false)
const minigameComponent = ref(null)

const totalSteps = computed(() => practice.value?.steps?.length || 0)

const currentAction = computed(() =>
  step.value === 'game' ? practice.value?.steps[stepIndex.value] : null
)

const correctAnswers = computed(() => {
  const action = currentAction.value
  if (!action) return []
  if (action.correctOptions) return action.correctOptions
  if (action.correct !== undefined) return [action.correct]
  return []
})

const identifyCorrect = computed(() => {
  const correct = correctAnswers.value
  const selected = selectedItems.value
  if (correct.length !== selected.length) return false
  return correct.every(c => selected.includes(c))
})

const gameMap = { aphid: AphidGame }

onMounted(async () => {
  practice.value = await getPracticeById(route.params.type)
  if (!practice.value) {
    practice.value = { id: route.params.type, name: '未知副本', steps: [] }
  }
  // 检查是否有 minigame 步骤，预加载组件
  const minigameStep = practice.value.steps?.find(s => s.action === 'minigame')
  if (minigameStep) {
    minigameComponent.value = markRaw(gameMap[minigameStep.gameType] || null)
  }
})

function answerQuiz(i) {
  if (answered.value) return
  selectedAnswer.value = i
  answered.value = true
  if (i === currentAction.value.correct) {
    score.value++
  }
  unlockKnowledge(currentAction.value.knowledgeId)
}

function toggleSelect(i) {
  if (answered.value) return
  const idx = selectedItems.value.indexOf(i)
  if (idx >= 0) {
    selectedItems.value.splice(idx, 1)
  } else {
    selectedItems.value.push(i)
  }
}

function answerIdentify() {
  answered.value = true
  if (identifyCorrect.value) {
    score.value++
  }
  unlockKnowledge(currentAction.value.knowledgeId)
}

function completeSimulate() {
  simulating.value = true
  setTimeout(() => {
    simulating.value = false
    score.value++
    unlockKnowledge(currentAction.value.knowledgeId)
    nextStep()
  }, 1000)
}

function onMinigameResult(result) {
  if (result.success) score.value++
  unlockKnowledge(currentAction.value.knowledgeId)
  nextStep()
}

function unlockKnowledge(knowledgeId) {
  if (knowledgeId && !unlockedKnowledge.value.includes(knowledgeId)) {
    unlockedKnowledge.value.push(knowledgeId)
  }
}

function nextStep() {
  answered.value = false
  selectedAnswer.value = -1
  selectedItems.value = []

  if (stepIndex.value < totalSteps.value - 1) {
    const next = stepIndex.value + 1
    // 检查下一步是否是 minigame
    const nextStepData = practice.value?.steps[next]
    if (nextStepData?.action === 'minigame') {
      minigameComponent.value = markRaw(gameMap[nextStepData.gameType] || null)
    }
    stepIndex.value = next
  } else {
    step.value = 'score'
  }
}

function retry() {
  step.value = 'guide'
  stepIndex.value = 0
  score.value = 0
  unlockedKnowledge.value = []
  answered.value = false
  selectedAnswer.value = -1
  selectedItems.value = []
}
</script>

<style scoped>
.practice-view {
  min-height: 100vh;
}
.game-step {
  padding: 20px; padding-top: 60px;
  min-height: 100vh;
  background: linear-gradient(180deg, #e3f2fd, #bbdefb);
}
.step-indicator {
  text-align: center; font-size: 14px; color: #666; margin-bottom: 20px;
  background: rgba(255,255,255,0.6); border-radius: 12px;
  padding: 4px 12px; display: inline-block;
}
.instruction {
  font-size: 18px; font-weight: bold; margin-bottom: 20px; text-align: center;
}
.options-list {
  display: flex; flex-direction: column; gap: 10px; max-width: 300px;
  margin: 0 auto 16px;
}
.option-btn {
  padding: 14px; border-radius: 10px; border: 2px solid #ddd;
  background: white; font-size: 16px; cursor: pointer;
  transition: background 0.2s;
}
.option-btn:hover:not(:disabled) { background: #e3f2fd; }
.option-btn.correct { background: #c8e6c9; border-color: #4caf50; }
.option-btn.wrong { background: #ffcdd2; border-color: #f44336; }
.option-btn:disabled { cursor: default; }
.identify-options {
  display: grid; grid-template-columns: repeat(2, 1fr); gap: 12px;
  max-width: 320px; margin: 0 auto 16px;
}
.identify-card {
  padding: 16px; border-radius: 10px; border: 2px solid #ddd;
  background: white; text-align: center; cursor: pointer;
  transition: background 0.2s;
}
.identify-card.selected { border-color: #2196f3; background: #e3f2fd; }
.identify-card.correct { background: #c8e6c9; border-color: #4caf50; }
.identify-card.wrong { background: #ffcdd2; border-color: #f44336; }
.identify-icon { font-size: 16px; }
.hint { text-align: center; color: #888; font-size: 13px; margin-bottom: 12px; }
.feedback { text-align: center; font-size: 16px; margin-bottom: 12px; }
.confirm-btn, .next-btn, .action-btn {
  display: block; margin: 0 auto; padding: 12px 32px;
  border: none; border-radius: 10px; font-size: 16px; cursor: pointer;
}
.confirm-btn { background: #2196f3; color: white; }
.next-btn { background: #4caf50; color: white; }
.action-btn { background: #ff9800; color: white; margin-top: 20px; }
.simulate-step { text-align: center; }
.sim-desc {
  font-size: 14px; color: #555; margin-bottom: 24px;
  background: rgba(255,255,255,0.7); border-radius: 8px;
  padding: 12px; max-width: 320px; margin-left: auto; margin-right: auto;
}
</style>
