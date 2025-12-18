# 目前你可以在TODO.md观察我们的进度
## 更新计划
目前阶段以收到bob正式肯定作为1.20.1移植版为目标，在这之前，需要完成三个任务：
1. 简单的装配产线
    - 第一批机器：高炉、电炉、砧、坩埚、装配机、火力发电机、斯特林发电机
    - 物流系统：输入输出器、传送带
    - 电力输送装置
    - 机器对应的配方
2. 核爆
    - MK5和MK3爆炸，蘑菇云是否需要待定
    - 手榴弹的运动和爆炸
3. 辐射
    - 优先物品辐射，比如铀锭等





## 配方
blast furnace 高炉
anvil
shredder
assembler
chemplant
arcwelder

## 方块实体
blast furnace * 

## 预期计划
2024/11/2 火焰手榴弹
2024/11/3 破片手榴弹


预期：
- 黑洞
- 线缆卷：练习连线的渲染方式，电力输送暂可不做

{
"loader": "forge:obj",
"flip_v": true,
"model": "hbmxx:models/block/cracking_tower/machine_cracking_tower.obj",
"textures": {
"texture0": "hbmxx:block/machine_cracking_tower"
},
"ambientocclusion": true,
"display": {
"firstperson_righthand": {
"scale": [0.05,0.05,0.05],
"rotation": [0,-22.5,22.5]
},
"firstperson_lefthand": {
"scale": [0.05,0.05,0.05],
"rotation": [0,-22.5,-22.5]
},
"thirdperson_righthand": {
"scale": [0.05,0.05,0.05],
"rotation": [0,-22.5,0]
},
"gui": {
"scale": [0.06,0.06,0.06],
"rotation": [0,45,0],
"translation": [0,-6,0]
}
}
}

"display": {
"gui": {
"gui_scale": [0.06,0.06,0.06]
},
"ground": {
"ground_scale": [0.024,0.024,0.024]
},
"fixed": {
"fixed_scale": [0.05,0.05,0.05]
},
"thirdperson_righthand": {
"hand_scale": [0.04,0.04,0.04]
},
"firstperson_righthand": {
"hand_scale": [0.04,0.04,0.04]
},
"firstperson_lefthand": {
"hand_scale": [0.04,0.04,0.04]
}
}
