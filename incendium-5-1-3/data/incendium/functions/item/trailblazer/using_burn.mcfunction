# from: ./using
# @s: player using trailblazer

function incendium:misc/store_uuid

execute anchored eyes positioned ^ ^ ^0.2 run summon firework_rocket ~ ~ ~ {Silent:1b,Motion:[0.0,0.0,0.0],ShotAtAngle:1,LifeTime:0,FireworksItem:{id:fire_charge,Count:1,tag:{Fireworks:{Flight:1,Explosions:[{Type:0,Flicker:0,Trail:0,Colors:[I;4971507]}]}}},Tags:["in.trailblazer_firework_burn"]}

execute as @e[type=firework_rocket,tag=in.trailblazer_firework_burn,tag=!in.checked,distance=..20] run function incendium:misc/firework