import api from './index'

const fallbackPractices = [
  { id: 'weeding', name: '除草副本', icon: '🌿', description: '学习识别杂草和菜苗，掌握除草技巧' },
  { id: 'pest', name: '灭虫副本', icon: '🐛', description: '识别蚜虫、蜗牛等害虫，学习天然防治方法' },
  { id: 'watering', name: '浇水副本', icon: '💧', description: '学习判断土壤干湿度与正确浇水方法' },
  { id: 'fertilizing', name: '施肥副本', icon: '🧪', description: '分辨肥料类型，学习科学施肥' },
  { id: 'framing', name: '搭架副本', icon: '🏗️', description: '学习为藤蔓蔬菜搭建支架' },
  { id: 'topping', name: '打顶副本', icon: '✂️', description: '学习打顶打岔，控制植株生长' },
  { id: 'harvesting', name: '采收副本', icon: '🧺', description: '学习判断成熟度与正确采收方法' }
]

const fallbackDetail = {
  weeding: {
    id: 'weeding', name: '除草副本', icon: '🌿',
    description: '学习识别杂草和菜苗，掌握除草技巧',
    steps: [
      { instruction: '找出田里的杂草', action: 'identify', options: ['稗草', '油菜苗', '马齿苋', '狗尾草'], correctOptions: [0, 2, 3], knowledgeId: 1001 },
      { instruction: '连根拔起稗草', action: 'simulate', description: '长按1.5秒，模拟连根拔起的动作', knowledgeId: 1002 },
      { instruction: '除草最佳时机是？', action: 'quiz', options: ['晴天中午', '雨后土壤松软时', '傍晚'], correct: 1, knowledgeId: 1003 }
    ]
  },
  pest: {
    id: 'pest', name: '灭虫副本', icon: '🐛',
    description: '识别蚜虫、蜗牛等害虫，学习天然防治方法',
    steps: [
      { instruction: '识别图中的害虫', action: 'identify', options: ['蚜虫', '瓢虫', '蜜蜂'], correct: 0, knowledgeId: 2001 },
      { instruction: '选择正确的防治方法', action: 'quiz', options: ['喷洒农药', '小苏打兑水喷洒', '不管它'], correct: 1, knowledgeId: 2002 },
      { instruction: '消灭所有蚜虫', action: 'minigame', gameType: 'aphid', knowledgeId: 2003 }
    ]
  },
  watering: {
    id: 'watering', name: '浇水副本', icon: '💧',
    description: '学习判断土壤干湿度与正确浇水方法',
    steps: [
      { instruction: '判断土壤干湿度', action: 'simulate', description: '观察土壤颜色和植株状态，如果土表发白、叶片微蔫则需要浇水', knowledgeId: 3001 },
      { instruction: '最佳浇水时间是？', action: 'quiz', options: ['中午12点', '早上7点', '下午3点'], correct: 1, knowledgeId: 3002 },
      { instruction: '正确浇水方法', action: 'simulate', description: '浇透根部土壤，避免浇到叶片上', knowledgeId: 3003 }
    ]
  },
  fertilizing: {
    id: 'fertilizing', name: '施肥副本', icon: '🧪',
    description: '分辨肥料类型，学习科学施肥',
    steps: [
      { instruction: '以下哪些是肥料？', action: 'identify', options: ['有机肥', '化肥', '农药', '小苏打'], correctOptions: [0, 1], knowledgeId: 4001 },
      { instruction: '哪种肥料对土壤更好？', action: 'quiz', options: ['化肥，见效快', '有机肥，养地又养菜', '都一样'], correct: 1, knowledgeId: 4002 }
    ]
  },
  framing: {
    id: 'framing', name: '搭架副本', icon: '🏗️',
    description: '学习为藤蔓蔬菜搭建支架',
    steps: [
      { instruction: '哪些蔬菜需要搭架？', action: 'quiz', options: ['菠菜', '黄瓜', '胡萝卜', '白菜'], correct: 1, knowledgeId: 5001 },
      { instruction: '为黄瓜搭一个支架', action: 'simulate', description: '将3根竹竿交叉捆绑，形成三角架', knowledgeId: 5002 },
      { instruction: '把藤蔓轻轻绑到支架上', action: 'simulate', description: '用软绳将藤蔓松松地绑在支架上，留出生长空间', knowledgeId: 5003 }
    ]
  },
  topping: {
    id: 'topping', name: '打顶副本', icon: '✂️',
    description: '学习打顶打岔，控制植株生长',
    steps: [
      { instruction: '识别番茄的侧枝和主茎', action: 'simulate', description: '侧枝长在主茎和叶片之间的夹角处，需要摘除', knowledgeId: 6001 },
      { instruction: '剪掉侧枝（打岔）', action: 'simulate', description: '只剪侧枝，保留主茎顶端', knowledgeId: 6002 },
      { instruction: '剪掉主茎顶端会怎样？', action: 'quiz', options: ['没事', '不结果了', '长得更好'], correct: 1, knowledgeId: 6003 },
      { instruction: '白菜能不能打顶？', action: 'quiz', options: ['能', '不能，打顶即不长'], correct: 1, knowledgeId: 6004 }
    ]
  },
  harvesting: {
    id: 'harvesting', name: '采收副本', icon: '🧺',
    description: '学习判断成熟度与正确采收方法',
    steps: [
      { instruction: '判断黄瓜是否达到可采大小', action: 'simulate', description: '黄瓜长到15-20厘米，表面鲜绿有光泽即可采收', knowledgeId: 7001 },
      { instruction: '采收黄瓜', action: 'simulate', description: '用剪刀剪断瓜柄，留1厘米瓜柄，不要硬拽', knowledgeId: 7002 },
      { instruction: '过老的黄瓜不采会怎样？', action: 'quiz', options: ['没关系', '抢养分影响新瓜', '会自动掉落'], correct: 1, knowledgeId: 7003 }
    ]
  }
}

export async function getPractices() {
  try {
    const res = await api.get('/practice')
    return Array.isArray(res.data) ? res.data : (res.data.data || fallbackPractices)
  } catch {
    return fallbackPractices
  }
}

export async function getPracticeById(id) {
  try {
    const res = await api.get(`/practice/${id}`)
    return res.data && res.data.steps ? res.data : (fallbackDetail[id] || null)
  } catch {
    return fallbackDetail[id] || null
  }
}
