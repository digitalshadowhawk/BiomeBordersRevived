package com.shadowhawk.biomeborders;

import org.lwjgl.input.Keyboard;

import com.mumfrey.liteloader.client.gui.GuiCheckbox;
import com.mumfrey.liteloader.modconfig.AbstractConfigPanel;
import com.mumfrey.liteloader.modconfig.ConfigPanelHost;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

public class BiomeBorderConfigPanel extends AbstractConfigPanel
{
	public static final int MIN_RADIUS = 0;
    public static final int MAX_RADIUS = 16;
    public static final float MIN_HEIGHT = 0.05F;
    public static final float MAX_HEIGHT = 16.0F;
    ConfigTextField radius;
    ConfigTextField height;
    LiteModBiomeBordersRevived mod;
	
	@Override
    protected void addOptions(ConfigPanelHost host)
    {
        mod = host.<LiteModBiomeBordersRevived>getMod();
        
        int id = 0;
        
        this.addLabel(id++, 20, 0, 200, 32, 0xFFFF55, I18n.format("biomeborders.config.radius"));
        radius = this.addTextField(id++, 55, 30, 45, 20).setText(String.valueOf(mod.getRadius()));
        this.addControl(new GuiButton(id++, 20, 30, 30, 20, "<"), new ConfigOptionListener<GuiButton>()
        {
        	@Override
        	public void actionPerformed(GuiButton control)
        	{
        		//int val = mod.getRadius() - 1;
        		int val = Integer.parseInt(radius.getText()) -1;

        		val = getBoundedRadius(val);
        		mod.setRadius(val);
        		radius.setText(String.valueOf(val));
        	}
        });
        this.addControl(new GuiButton(id++, 110, 30, 30, 20, ">"), new ConfigOptionListener<GuiButton>()
        {
        	@Override
        	public void actionPerformed(GuiButton control)
        	{
        		int val = Integer.parseInt(radius.getText()) + 1;

        		val = getBoundedRadius(val);
        		mod.setRadius(val);
        		radius.setText(String.valueOf(val));
        	}
        });
        this.addLabel(id++, 20, 53, 200, 32, 0xFFFF55, I18n.format("biomeborders.config.lineheight"));
        height = this.addTextField(id++, 55, 83, 45, 20).setText(String.valueOf(mod.getLineHeight()));
        this.addControl(new GuiButton(id++, 20, 83, 30, 20, "<"), new ConfigOptionListener<GuiButton>()
        {
        	@Override
        	public void actionPerformed(GuiButton control)
        	{
        		float val = Float.parseFloat(height.getText()) - 0.05F;

        		val = getBoundedHeight(val);
        		mod.setLineHeight(val);
        		height.setText(String.valueOf(val));
        	}
        });
        this.addControl(new GuiButton(id++, 110, 83, 30, 20, ">"), new ConfigOptionListener<GuiButton>()
        {
        	@Override
        	public void actionPerformed(GuiButton control)
        	{
        		float val = Float.parseFloat(height.getText()) + 0.05F;

        		val = getBoundedHeight(val);
        		mod.setLineHeight(val);
        		height.setText(String.valueOf(val));
        	}
        });
        this.addControl(new GuiCheckbox(id++, 20, 110, I18n.format("biomeborders.config.unlimited")), new ConfigOptionListener<GuiCheckbox>()
        {
        	@Override
        	public void actionPerformed(GuiCheckbox control)
        	{
        		mod.setUnlimited(!mod.getUnlimited());
        		control.checked = !control.checked;
        	}
        }).checked = mod.getUnlimited();
    }
    
	public int getBoundedRadius(int num)
	{
		return Math.min(Math.max(0, num), 16);
	}
	
	public float getBoundedHeight(float num)
	{
		//Fix Java 6 Bug problem of 0.5 as 0.49999999999999994
		//still persists in Java 8 at time of config panel rewrite
        num = (num + 0.001F) * 100.0F;
        num = ((int)num);
        num /= 100.0F;
		
		return Math.min(Math.max(0.05F, num), 16.0F);
	}
	
	public void keyPressed(ConfigPanelHost host, char keyChar, int keyCode)
	{
		if (keyCode == Keyboard.KEY_RETURN)
		{
			try{
				mod.setRadius(getBoundedRadius((int)Float.parseFloat(radius.getText())));
				radius.setText(Integer.toString(mod.getRadius()));
				mod.setLineHeight(getBoundedHeight(Float.parseFloat(height.getText())));
				height.setText(Float.toString(mod.getLineHeight()));
			} catch (NumberFormatException e) {
				radius.setText(Integer.toString(mod.getRadius()));
				height.setText(Float.toString(mod.getLineHeight()));
			} catch (NullPointerException e) {
				radius.setText(Integer.toString(mod.getRadius()));
				height.setText(Float.toString(mod.getLineHeight()));
			}
		}
		super.keyPressed(host, keyChar, keyCode);
	}

	@Override
    public String getPanelTitle()
    {
        return I18n.format("biomeborders.config.title");
    }

    @Override
    public void onPanelHidden() {}
}