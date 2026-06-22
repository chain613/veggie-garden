import api from './index'

export async function getDialogApi(triggerEvent, vegetableId, season) {
  try {
    const params = { event: triggerEvent }
    if (vegetableId) params.vegetableId = vegetableId
    if (season) params.season = season
    const res = await api.get('/knowledge/dialog', { params })
    return res.data || { found: false }
  } catch {
    return fallbackDialog(triggerEvent)
  }
}

export async function unlockKnowledgeApi(knowledgeId) {
  try {
    await api.post(`/knowledge/unlock/${knowledgeId}`)
  } catch {
    // 静默失败，本地记录
  }
}

export async function getUnlockedKnowledge() {
  try {
    const res = await api.get('/knowledge/unlocked')
    return res.data || []
  } catch {
    return []
  }
}

export async function getAllKnowledge() {
  try {
    const res = await api.get('/knowledge')
    return res.data || []
  } catch {
    return []
  }
}

function fallbackDialog(event) {
  const dialogs = {
    '日常问候': { found: true, dialog: { dialogText: '嗨！今天天气真不错，来看看菜园吗？有什么问题随时问我。', knowledgeLinks: [] } },
    '除草指导': { found: true, dialog: { dialogText: '杂草要和菜苗区分清楚哦。像稗草、马齿苋这些都是常见的杂草，雨后土壤松软时除草最省力！', knowledgeLinks: [{ id: 1001, title: '常见杂草识别' }] } },
    '灭虫指导': { found: true, dialog: { dialogText: '看到蚜虫别慌！小苏打兑水喷洒就能有效防治，纯天然无污染。', knowledgeLinks: [{ id: 2001, title: '天然防虫方法' }] } },
    '施肥指导': { found: true, dialog: { dialogText: '有机肥是最好的选择，既能养地又能养菜。化肥虽然见效快，但用多了土壤会板结。', knowledgeLinks: [{ id: 4001, title: '有机肥 vs 化肥' }] } },
    '采收指导': { found: true, dialog: { dialogText: '采收要看时机！黄瓜长到15-20厘米、表面鲜绿有光泽就可以摘了。过老的瓜会抢养分，影响新瓜生长。', knowledgeLinks: [{ id: 7001, title: '采收时机判断' }] } }
  }
  return dialogs[event] || { found: true, dialog: { dialogText: '嗯，这个问题问得好！不过我得想想怎么回答...', knowledgeLinks: [] } }
}
