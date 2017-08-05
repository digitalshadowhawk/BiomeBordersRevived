package com.shadowhawk.biomeborders;

import java.io.File;

import org.lwjgl.input.Keyboard;

import com.google.gson.annotations.Expose;
import com.mumfrey.liteloader.Configurable;
import com.mumfrey.liteloader.HUDRenderListener;
import com.mumfrey.liteloader.PostRenderListener;
import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.modconfig.ConfigPanel;
import com.mumfrey.liteloader.modconfig.ConfigStrategy;
import com.mumfrey.liteloader.modconfig.ExposableOptions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;

@ExposableOptions(
	    strategy = ConfigStrategy.Unversioned,
	    filename = "biomeborders.json",
	    aggressive = true
	)
public class LiteModBiomeBordersRevived implements PostRenderListener, HUDRenderListener, Tickable, Configurable
{
	private static KeyBinding keyBinding = new KeyBinding(I18n.format("biomeborders.control.toggle"), Keyboard.KEY_B, I18n.format("biomeborders.config.name"));
	
	@Expose
    public int radius = 4;
    @Expose
    public float lineheight = 0.25F;
    @Expose
    public boolean unlimited = false;
    @Expose
    public boolean visible = false;
    public static final String NAME = "Biome Borders Revived";
    public static final String VERSION = "1.1.0";
    public static LiteModBiomeBordersRevived instance;
    
    public BiomeBorderRenderer renderer = new BiomeBorderRenderer();

    @Override
	public Class <? extends ConfigPanel > getConfigPanelClass()
    {
        return BiomeBorderConfigPanel.class;
    }

    @Override
	public String getName()
    {
        return NAME;
    }

    @Override
	public String getVersion()
    {
        return VERSION;
    }

    @Override
	public void init(File configPath)
    {
        instance = this;
        LiteLoader.getInput().registerKeyBinding(keyBinding);
        
        this.renderer.setRadius(this.radius);
        this.renderer.setLineheight(this.lineheight);
        this.renderer.setUnlimited(this.unlimited);
        this.renderer.setVisible(this.visible);
    }

    


    @Override
	public void onPostRender(float partialTicks) {}

    @Override
	public void onPostRenderEntities(float partialTicks)
    {
        this.renderer.render(Minecraft.getMinecraft(), partialTicks);
    }

    @Override
	public void onPostRenderHUD(int screenWidth, int screenHeight)
    {
        this.renderer.renderBiomeName(screenWidth, screenHeight);
    }

    @Override
	public void onPreRenderHUD(int screenWidth, int screenHeight) {}

    @Override
	public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock)
    {
        if (keyBinding.isPressed())
        {
            toggleVisible();
        }
    }

    @Override
	public void upgradeSettings(String version, File configPath, File oldConfigPath) {}

	public int getRadius() {
		return this.radius;
	}
	
	public void toggleVisible() {
		this.visible = !this.visible;
        this.renderer.setVisible(this.visible);
        LiteLoader.getInstance().writeConfig(this);
	}

	public void setRadius(int val) {
		this.radius = val;
		renderer.setRadius(val);
		LiteLoader.getInstance().writeConfig(this);
	}
	
	public float getLineHeight() {
		return this.lineheight;
	}
	
	public void setLineHeight(float val) {
		this.lineheight = val;
		renderer.setLineheight(val);
		LiteLoader.getInstance().writeConfig(this);
	}
	
	public boolean getUnlimited() {
		return this.unlimited;
	}
	
	public void setUnlimited(boolean val) {
		this.unlimited = val;
		renderer.setUnlimited(val);
		LiteLoader.getInstance().writeConfig(this);
	}
}
