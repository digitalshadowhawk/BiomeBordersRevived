package com.shadowhawk;

import com.mumfrey.liteloader.client.gui.GuiCheckbox;
import com.mumfrey.liteloader.modconfig.ConfigPanel;
import com.mumfrey.liteloader.modconfig.ConfigPanelHost;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;

public class ConfigBiomeBorder implements ConfigPanel
{
    public static final int MIN_RADIUS = 0;
    public static final int MAX_RADIUS = 16;
    public static final float MIN_HEIGHT = 0.05F;
    public static final float MAX_HEIGHT = 16.0F;
    private BiomeBorderRenderer renderer;
    private GuiButton lessRadius;
    private GuiButton moreRadius;
    private GuiTextField radiusDisplay;
    private GuiButton lessLineH;
    private GuiButton moreLineH;
    private GuiTextField LineHDisplay;
    private GuiCheckbox unlimited;
    private Minecraft minecraft = Minecraft.getMinecraft();

    @Override
	public void drawPanel(ConfigPanelHost host, int mouseX, int mouseY, float partialTicks)
    {
        this.minecraft.fontRendererObj.drawString("Drawing radius (chunks)", 20, 25, -1);
        this.lessRadius.drawButton(this.minecraft, mouseX, mouseY);
        this.radiusDisplay.drawTextBox();
        this.moreRadius.drawButton(this.minecraft, mouseX, mouseY);
        this.minecraft.fontRendererObj.drawString("Border line height (blocks)", 20, 70, -1);
        this.lessLineH.drawButton(this.minecraft, mouseX, mouseY);
        this.LineHDisplay.drawTextBox();
        this.moreLineH.drawButton(this.minecraft, mouseX, mouseY);
        this.unlimited.drawButton(this.minecraft, mouseX, mouseY);
    }

    @Override
	public int getContentHeight()
    {
        return -1;
    }

    @Override
	public String getPanelTitle()
    {
        return "BiomeBorders Settings";
    }

    @Override
	public void keyPressed(ConfigPanelHost host, char keyChar, int keyCode) {}

    @Override
	public void mouseMoved(ConfigPanelHost host, int mouseX, int mouseY) {}

    @Override
	public void mousePressed(ConfigPanelHost host, int mouseX, int mouseY, int mouseButton)
    {
        int val;

        if (this.lessRadius.mousePressed(this.minecraft, mouseX, mouseY))
        {
            val = Integer.parseInt(this.radiusDisplay.getText()) - 1;

            if (val >= 0)
            {
                this.renderer.setRadius(val);
                this.radiusDisplay.setText(String.valueOf(val));
            }
        }

        if (this.moreRadius.mousePressed(this.minecraft, mouseX, mouseY))
        {
            val = Integer.parseInt(this.radiusDisplay.getText()) + 1;

            if (val <= 16)
            {
                this.renderer.setRadius(val);
                this.radiusDisplay.setText(String.valueOf(val));
            }
        }

        float val1;

        if (this.lessLineH.mousePressed(this.minecraft, mouseX, mouseY))
        {
            val1 = Float.parseFloat(this.LineHDisplay.getText()) - 0.05F;
            //String debug = " Fix Java 6 Bug problem of 0.5 as 0.49999999999999994 ";
            val1 = (val1 + 0.001F) * 100.0F;
            val1 = ((int)val1);
            val1 /= 100.0F;

            if (val1 >= 0.05F)
            {
                this.renderer.setLineheight(val1);
                this.LineHDisplay.setText(String.valueOf(val1));
            }
        }

        if (this.moreLineH.mousePressed(this.minecraft, mouseX, mouseY))
        {
            val1 = Float.parseFloat(this.LineHDisplay.getText()) + 0.05F;
            val1 = (val1 + 0.001F) * 100.0F;
            val1 = ((int)val1);
            val1 /= 100.0F;

            if (val1 <= 16.0F)
            {
                this.renderer.setLineheight(val1);
                this.LineHDisplay.setText(String.valueOf(val1));
            }
        }

        if (this.unlimited.mousePressed(this.minecraft, mouseX, mouseY))
        {
            this.unlimited.checked = !this.unlimited.checked;
            this.renderer.setUnlimited(this.unlimited.checked);
        }
    }

    @Override
	public void mouseReleased(ConfigPanelHost host, int mouseX, int mouseY, int mouseButton) {}

    @Override
	public void onPanelHidden() {}

    @Override
	public void onPanelResize(ConfigPanelHost host) {}

    @Override
	public void onPanelShown(ConfigPanelHost host)
    {
        this.renderer = LiteModBiomeBordersRevived.instance.renderer;
        this.lessRadius = new GuiButton(0, 20, 40, 30, 20, "<");
        this.radiusDisplay = new GuiTextField(0, this.minecraft.fontRendererObj, 60, 40, 40, 20);
        this.radiusDisplay.setText(String.valueOf(this.renderer.radius));
        this.radiusDisplay.setFocused(false);
        this.moreRadius = new GuiButton(1, 110, 40, 30, 20, ">");
        this.lessLineH = new GuiButton(2, 20, 85, 30, 20, "<");
        this.LineHDisplay = new GuiTextField(1, this.minecraft.fontRendererObj, 60, 85, 40, 20);
        this.LineHDisplay.setText(String.valueOf(this.renderer.lineheight));
        this.moreLineH = new GuiButton(3, 110, 85, 30, 20, ">");
        this.unlimited = new GuiCheckbox(4, 20, 110, "Unlimited height y=0 to y=256");
        this.unlimited.checked = this.renderer.unlimited;
    }

    @Override
	public void onTick(ConfigPanelHost host) {}
}
