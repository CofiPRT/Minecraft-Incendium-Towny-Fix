# from: entity/other/main

execute if predicate incendium:random/60 run particle minecraft:soul ~ ~1 ~ .125 .125 .125 .02 1 normal

#execute if block ~ ~-0.7 ~ #incendium:nether_blocks_no_air run function incendium:item/restless_nature/place

data modify storage incendium:temp player.UUID set from entity @s Owner

execute if block ~ ~-0.7 ~ #incendium:nether_blocks_no_air run summon firework_rocket ~ ~ ~ {Silent:1b,Motion:[0.0,0.0,0.0],ShotAtAngle:1,LifeTime:0,FireworksItem:{id:fire_charge,Count:1,tag:{Fireworks:{Flight:1,Explosions:[{Type:0,Flicker:0,Trail:0,Colors:[I;4971507]}]}}},Tags:["in.restless_nature_firework"]}

execute as @e[type=firework_rocket,tag=in.restless_nature_firework,tag=!in.checked,distance=..20] run function incendium:misc/firework

execute if block ~ ~-0.05 ~ #incendium:nether_blocks_no_air run kill @s