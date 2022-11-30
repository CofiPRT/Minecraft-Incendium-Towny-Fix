scoreboard players set #distance in.fire_laser 1000
scoreboard players set #distance2 in.fire_laser 1000
execute positioned ~ ~0.1 ~ run summon minecraft:firework_rocket ~ ~ ~ {Silent:1b,Motion:[0.0,0.0,0.0],ShotAtAngle:1,LifeTime:0,FireworksItem:{id:"firework_rocket",Count:1,tag:{Fireworks:{Flight:1,Explosions:[{Type:0,Flicker:0,Trail:0,Colors:[I;16777215]}]}}},Tags:["in.voltaic_trident_firework"]}
#data modify entity @e[type=firework_rocket,distance=..4,limit=1,sort=nearest] Owner set from entity @s data.player.UUID

execute as @e[type=firework_rocket,tag=in.voltaic_trident_firework,tag=!in.checked,distance=..20] run function incendium:misc/firework

tag @s add laser
