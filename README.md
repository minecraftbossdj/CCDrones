# CC: Drones+
Drones for CC: Tweaked

Fork of CC: Drones, Originally created by [JSJBDEV](https://github.com/JSJBDEV)

currently drones can:

- Go forward (using `drone.engineOn(true)`)
- Turn (using `drone.left(number)` and `drone.right(number)`)
- Go up and down (using `drone.up(number)` and `drone.down(number)`)
- Look forward and back (`drone.lookForward()` `drone.lookBack()`)
- see the Drones rotation (`drone.rotation()`)
- see if the drone is colliding (`drone.isColliding()`)
- turn the drones hover function on (`drone.hoverOn(true)`)

## Drone Upgrades:
### Mining Upgrade:
- Mine forward (`drone.breakForward()`)
### Carry Upgrade:
- pick up blocks (and keep there data) below (`drone.pickupBlock()`) 
- drop the block (`drone.dropBlock()`)
- pick up entities below (`drone.pickUpEntity()`)
- drop the entity below (`drone.dropEntity()`)
### Modem Upgrade:
- get drone's pos (`drone.getPos()`)

Survery Upgrade has no uses yet, but it eventually will.

## Programming
using the Drone Workbench as a peripheral you can use two commands, Or you can craft the Drone Controller.

(let `a` be the wrap e.g `a = peripheral.wrap("left")`)

- `a.api()` will reboot the computer after installing the drone api for ease of programming
- `a.export(path)` will take the lua program at `path` and send it to the first drone within 2 blocks of the workbench, this will then reboot the drone and start it executing

