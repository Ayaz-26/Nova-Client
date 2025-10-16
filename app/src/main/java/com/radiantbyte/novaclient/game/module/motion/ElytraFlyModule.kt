package com.radiantbyte.novaclient.game.module.motion

import com.radiantbyte.novaclient.game.InterceptablePacket
import com.radiantbyte.novaclient.game.Module
import com.radiantbyte.novaclient.game.ModuleCategory
import com.radiantbyte.novaclient.game.ModuleManager
import org.cloudburstmc.protocol.bedrock.data.PlayerAuthInputData
import org.cloudburstmc.protocol.bedrock.packet.PlayerAuthInputPacket

class ElytraFlyModule : Module("elytra_fly", ModuleCategory.Motion) {

    private var isGliding = false

    override fun beforePacketBound(interceptablePacket: InterceptablePacket) {
        val packet = interceptablePacket.packet

        if (packet is PlayerAuthInputPacket) {
            // Only activate elytra flying if BOTH ElytraFly and MotionFly are enabled
            val motionFly = ModuleManager.getModule(MotionFlyModule::class.java)
            val shouldGlide = isEnabled && motionFly != null && motionFly.isEnabled

            if (shouldGlide) {
                // Add elytra gliding state
                if (!packet.inputData.contains(PlayerAuthInputData.START_GLIDING)) {
                    packet.inputData.add(PlayerAuthInputData.START_GLIDING)
                }
                
                // Keep gliding active
                if (!isGliding) {
                    isGliding = true
                }
            } else {
                // Remove gliding if either module is disabled
                packet.inputData.remove(PlayerAuthInputData.START_GLIDING)
                if (isGliding) {
                    isGliding = false
                }
            }
        }
    }

    override fun onDisable() {
        // Reset gliding state
        isGliding = false
    }
}