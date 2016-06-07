package com.shadowhawk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mumfrey.liteloader.Configurable;
import com.mumfrey.liteloader.HUDRenderListener;
import com.mumfrey.liteloader.PostRenderListener;
import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.modconfig.ConfigPanel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

public class LiteModBiomeBordersRevived implements PostRenderListener, HUDRenderListener, Tickable, Configurable
{
    public static final String NAME = "BiomeBordersRevived";
    public static final String VERSION = "1.0.2";
    public static KeyBinding keyBinding;
    public static LiteModBiomeBordersRevived instance;
    public static HashMap<String, Object> parse(String json)
    {
        JsonParser parser = new JsonParser();
        JsonElement elm = parser.parse(json);

        if (elm != null && !(elm instanceof JsonNull))
        {
            JsonObject object = (JsonObject)elm;
            Set<?> set = object.entrySet();
            Iterator<?> iterator = set.iterator();
            HashMap<String, Object> map = new HashMap<String, Object>();

            while (iterator.hasNext())
            {
                Entry<?, ?> entry = (Entry<?, ?>)iterator.next();
                String key = (String)entry.getKey();
                JsonElement value = (JsonElement)entry.getValue();

                if (!value.isJsonPrimitive())
                {
                    map.put(key, parse(value.toString()));
                }
                else
                {
                    map.put(key, value.getAsString());
                }
            }

            return map;
        }
        else
        {
            return null;
        }
    }
    public BiomeBorderRenderer renderer = new BiomeBorderRenderer();

    public String confpath;

    @Override
	public Class <? extends ConfigPanel > getConfigPanelClass()
    {
        return ConfigBiomeBorder.class;
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
        this.confpath = configPath.getPath();
        this.initfields(this.confpath);
        instance = this;
        keyBinding = new KeyBinding("Toggle Biome Borders", 48, "LiteMods");
        LiteLoader.getInput().registerKeyBinding(keyBinding);
    }

    private void initfields(String configPath)
    {
        HashMap<String, Object> json = this.loadjson(configPath);

        if (json != null)
        {
            Object obj;

            if (json.containsKey("radius"))
            {
                obj = json.get("radius");

                try
                {
                    this.renderer.radius = Integer.parseInt(obj.toString());
                }
                catch (NumberFormatException var6)
                {
                    ;
                }
            }

            if (json.containsKey("lineheight"))
            {
                obj = json.get("lineheight");

                try
                {
                    this.renderer.lineheight = Float.parseFloat(obj.toString());
                }
                catch (NumberFormatException var5)
                {
                    ;
                }
            }

            if (json.containsKey("unlimited"))
            {
                obj = json.get("unlimited");
                this.renderer.unlimited = Boolean.parseBoolean(obj.toString());
            }
        }
    }

    private HashMap<String, Object> loadjson(String configPath)
    {
        String jsonname = configPath + "\\biomeborder.json";
        File file = new File(jsonname);

        if (file.exists() && file.canRead())
        {
            try
            {
                byte[] e = Files.readAllBytes(Paths.get(jsonname, new String[0]));
                String buff = new String(e, Charset.defaultCharset());
                return parse(buff);
            }
            catch (FileNotFoundException var6)
            {
                var6.printStackTrace();
            }
            catch (IOException var7)
            {
                var7.printStackTrace();
            }
        }

        return null;
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
            this.renderer.toggleChunkBorders();
        }
    }

    @Override
	public void upgradeSettings(String version, File configPath, File oldConfigPath) {}
}
