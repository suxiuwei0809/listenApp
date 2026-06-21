import re, sys
path = r'c:\Users\chu\AndroidStudioProjects\listenApp\fitness-app-prototype\index.html'
c = open(path, encoding='utf-8').read()
# rgba hex values
c = c.replace('rgba(255,120,0,', 'rgba(255,45,117,')
c = c.replace('rgba(255,170,0,', 'rgba(0,212,255,')
c = c.replace('rgba(255,157,0,', 'rgba(0,255,136,')
c = c.replace('rgba(224,64,32,', 'rgba(180,77,255,')
# CSS variables that got renamed
c = c.replace('var(--primary)', 'var(--neon-pink)')
c = c.replace('var(--accent)', 'var(--neon-blue)')
c = c.replace('var(--ember)', 'var(--neon-purple)')
c = c.replace('var(--forge)', 'var(--neon-green)')
c = c.replace('var(--hot)', 'var(--neon-orange)')
c = c.replace('var(--gold)', 'var(--neon-yellow)')
# hardcoded hex
c = c.replace('#0d0d16', '#0d0d25')
c = c.replace('#1a1a1e', '#1a1a3a')
c = c.replace('rgba(8,8,16,', 'rgba(10,10,26,')
c = c.replace('#e88000', '#00cc6a')
# chart bar colors
c = c.replace(
    "['linear-gradient(180deg,#ff7800,#e04020)','linear-gradient(180deg,#ffaa00,#e04020)','linear-gradient(180deg,#ff9d00,#ffaa00)','linear-gradient(180deg,#ff4a00,#ff7800)','linear-gradient(180deg,#e04020,#ff7800)','linear-gradient(180deg,#ffaa00,#ff9d00)','linear-gradient(180deg,#ff7800,#ff4a00)']",
    "['linear-gradient(180deg,#ff2d75,#b44dff)','linear-gradient(180deg,#00d4ff,#b44dff)','linear-gradient(180deg,#00ff88,#00d4ff)','linear-gradient(180deg,#ff6b35,#ff2d75)','linear-gradient(180deg,#b44dff,#ff2d75)','linear-gradient(180deg,#00d4ff,#00ff88)','linear-gradient(180deg,#ff2d75,#ff6b35)']"
)
# weight chart gradients
c = c.replace('stop-color="#ffaa00" stop-opacity="0.3"/><stop offset="100%" stop-color="#ffaa00" stop-opacity="0"',
              'stop-color="#00d4ff" stop-opacity="0.3"/><stop offset="100%" stop-color="#00d4ff" stop-opacity="0"')
c = c.replace('<stop offset="0%" stop-color="#ff7800"/><stop offset="100%" stop-color="#ffaa00"/>',
              '<stop offset="0%" stop-color="#ff2d75"/><stop offset="100%" stop-color="#00d4ff"/>')
c = c.replace('fill="#ffaa00" stroke="#0a0a1a" stroke-width="2" style="filter:drop-shadow(0 0 4px #ffaa00)"',
              'fill="#00d4ff" stroke="#0a0a1a" stroke-width="2" style="filter:drop-shadow(0 0 4px #00d4ff)"')
# plank ring SVG gradient
c = c.replace('<stop offset="0%" stop-color="#ffaa00"/><stop offset="50%" stop-color="#e04020"/><stop offset="100%" stop-color="#ff9d00"/>',
              '<stop offset="0%" stop-color="#00d4ff"/><stop offset="50%" stop-color="#b44dff"/><stop offset="100%" stop-color="#00ff88"/>')
# particle strokeStyle
c = c.replace("strokeStyle=`rgba(255,170,0,${", "strokeStyle=`rgba(0,212,255,${")
# ring-progress CS
c = c.replace('filter:drop-shadow(0 0 8px rgba(255,170,0', 'filter:drop-shadow(0 0 8px rgba(0,212,255')
c = c.replace('filter:drop-shadow(0 0 15px rgba(255,170,0', 'filter:drop-shadow(0 0 15px rgba(0,212,255')
c = c.replace('text-shadow:0 0 20px rgba(255,170,0', 'text-shadow:0 0 20px rgba(0,212,255')
c = c.replace('text-shadow:0 0 40px rgba(255,170,0', 'text-shadow:0 0 40px rgba(0,212,255')
c = c.replace('text-shadow:0 0 60px rgba(255,170,0', 'text-shadow:0 0 60px rgba(0,212,255')
c = c.replace('text-shadow:0 0 10px rgba(255,170,0', 'text-shadow:0 0 10px rgba(0,212,255')
c = c.replace('0 0 40px rgba(255,170,0,.8)', '0 0 40px rgba(0,212,255,.8)')
# encouragement
c = c.replace("background:linear-gradient(135deg,rgba(255,170,0,.1),rgba(224,64,32,.1))",
              "background:linear-gradient(135deg,rgba(0,212,255,.1),rgba(180,77,255,.1))")
c = c.replace('border:1px solid rgba(255,170,0,.2);border-radius:14px;padding:14px 22px;margin:20px auto;max-width:300px;font-size:15px;color:var(--neon-blue);font-weight:500;min-height:50px;display:flex;align-items:center;justify-content:center;text-shadow:0 0 10px rgba(0,212,255,.3)',
              'border:1px solid rgba(0,212,255,.2);border-radius:14px;padding:14px 22px;margin:20px auto;max-width:300px;font-size:15px;color:var(--neon-blue);font-weight:500;min-height:50px;display:flex;align-items:center;justify-content:center;text-shadow:0 0 10px rgba(0,212,255,.3)')
# rest-timer
c = c.replace('color:var(--neon-blue);text-shadow:0 0 40px rgba(255,170,0,.8)',
              'color:var(--neon-blue);text-shadow:0 0 40px rgba(0,212,255,.8)')
# exercise-detail-sheet  
c = c.replace('background:linear-gradient(180deg,#1a1a1e,#0d0d16)',
              'background:linear-gradient(180deg,#1a1a3a,#0d0d25)')
c = c.replace('border-top:1px solid rgba(255,170,0,.2);box-shadow:0 -10px 40px rgba(255,170,0,.1)',
              'border-top:1px solid rgba(0,212,255,.2);box-shadow:0 -10px 40px rgba(0,212,255,.1)')
# modal-sheet
c = c.replace('border-top:1px solid rgba(255,170,0,.2);box-shadow:0 -10px 40px rgba(255,170,0,.1)',
              'border-top:1px solid rgba(0,212,255,.2);box-shadow:0 -10px 40px rgba(0,212,255,.1)')

open(path, encoding='utf-8', mode='w').write(c)
print('done: all colors reverted to neon')
