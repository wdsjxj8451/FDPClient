package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.Render3DEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.client.HUD
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.cos
import kotlin.math.sin

@ModuleInfo(name = "ChinaHat", category = ModuleCategory.RENDER)
class ChinaHat : Module() {

    private val heightValue = FloatValue("Height", 0.3f, 0.1f, 0.7f)
    private val radiusValue = FloatValue("Radius", 0.7f, 0.3f, 1.5f)
    private val yPosValue = FloatValue("YPos", 0f, -1f, 1f)
    private val rotateSpeedValue = FloatValue("RotateSpeed", 2f, 0f, 5f)
    private val onlyThirdPersonValue = BoolValue("OnlyThirdPerson", true)
    private val colorRedValue = IntegerValue("R", 255, 0, 255).displayable { !colorRainbow.get() }
    private val colorGreenValue = IntegerValue("G", 255, 0, 255).displayable { !colorRainbow.get() }
    private val colorBlueValue = IntegerValue("B", 255, 0, 255).displayable { !colorRainbow.get() }
    private val colorAlphaValue = IntegerValue("Alpha", 200, 0, 255)
    private val colorRainbow = BoolValue("Rainbow", false)

    @EventTarget
    fun onRender3d(event: Render3DEvent) {
        if(onlyThirdPersonValue.get() && mc.gameSettings.thirdPersonView == 0)
            return

        GL11.glPushMatrix()
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_DEPTH_TEST)
        GL11.glDepthMask(false)
        GL11.glColor4f(colorRedValue.get() / 255f, colorGreenValue.get() / 255f, colorBlueValue.get() / 255f, colorAlphaValue.get() / 255f)
        GL11.glTranslatef(0f, mc.thePlayer.height + yPosValue.get(), 0f)
        GL11.glDisable(GL11.GL_CULL_FACE)
        GL11.glRotatef((mc.thePlayer.ticksExisted + mc.timer.renderPartialTicks) * rotateSpeedValue.get(), 0f, 1f, 0f)

        GL11.glBegin(GL11.GL_TRIANGLE_FAN)
        GL11.glVertex3d(0.0, heightValue.get().toDouble(), 0.0)
        val radius = radiusValue.get().toDouble()
        for(i in 0..360 step 5) {
            if(colorRainbow.get()) {
                RenderUtils.glColor(Color.getHSBColor(if (i <180) { HUD.rainbowStart.get() + (HUD.rainbowStop.get() - HUD.rainbowStart.get()) * (i / 180f) } else { HUD.rainbowStart.get() + (HUD.rainbowStop.get() - HUD.rainbowStart.get()) * (-(i-360) / 180f) }, 0.7f, 1.0f), colorAlphaValue.get() / 255f)
            }
            GL11.glVertex3d(cos(i.toDouble() * Math.PI / 180.0) * radius, 0.0, sin(i.toDouble() * Math.PI / 180.0) * radius)
        }
        GL11.glVertex3d(0.0, heightValue.get().toDouble(), 0.0)
        GL11.glEnd()

        GL11.glEnable(GL11.GL_CULL_FACE)
        GlStateManager.resetColor()
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glDepthMask(true)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glPopMatrix()
    }
}