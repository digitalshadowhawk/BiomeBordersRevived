package com.shadowhawk.biomeborders;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public class BiomeBorderRenderer
{
    public static double getDistanceSq(double x1, double z1, double x2, double z2)
    {
        double xs = x1 - x2;
        double zs = z1 - z2;
        return xs * xs + zs * zs;
    }
    public int radius = 4;
    public float lineheight = 0.25F;
    public boolean unlimited = false;
    private boolean visible = false;
    private float lineRed = 1.0F;
    private float lineGreen = 0.0F;

    private float lineBlue = 0.0F;

    public BlockPos getTopLiquidOrSolidBlock(WorldClient world, BlockPos pos)
    {
        Chunk chunk = world.getChunkFromBlockCoords(pos);
        BlockPos var3;
        BlockPos var4;

        for (var3 = new BlockPos(pos.getX(), chunk.getTopFilledSegment() + 16, pos.getZ()); var3.getY() >= 0; var3 = var4)
        {
            var4 = var3.offset(EnumFacing.DOWN);
            Material material = chunk.getBlockState(var4).getMaterial();

            if (material == Material.WATER || material == Material.LAVA || material.blocksMovement() && material != Material.LEAVES)
            {
                break;
            }
        }

        return var3;
    }

    public BlockPos getTopLiquidOrSolidBlock2(WorldClient world, BlockPos pos)
    {
        Chunk chunk = world.getChunkFromBlockCoords(pos);
        boolean inBlock = true;
        BlockPos var3;
        BlockPos var4;

        for (var3 = new BlockPos(pos.getX(), pos.getY() + 3, pos.getZ()); var3.getY() >= 0; var3 = var4)
        {
            var4 = var3.offset(EnumFacing.DOWN);
            Material material;
            boolean bool;

            if (inBlock)
            {
                material = chunk.getBlockState(var3).getMaterial();
                bool = material == Material.WATER || material == Material.LAVA || material.blocksMovement() && material != Material.LEAVES;

                if (bool)
                {
                    continue;
                }
            }

            inBlock = false;
            material = chunk.getBlockState(var4).getMaterial();
            bool = material == Material.WATER || material == Material.LAVA || material.blocksMovement() && material != Material.LEAVES;

            if (bool)
            {
                break;
            }
        }

        return var3;
    }

    Vector3f HSV2RGB(float h, float s, float v)
    {
        float r = v;
        float g = v;
        float b = v;

        if (s > 0.0F)
        {
            h *= 6.0F;
            int i = (int)h;
            float f = h - i;

            switch (i)
            {
                case 0:
                default:
                    g = v * (1.0F - s * (1.0F - f));
                    b = v * (1.0F - s);
                    break;

                case 1:
                    r = v * (1.0F - s * f);
                    b = v * (1.0F - s);
                    break;

                case 2:
                    r = v * (1.0F - s);
                    b = v * (1.0F - s * (1.0F - f));
                    break;

                case 3:
                    r = v * (1.0F - s);
                    g = v * (1.0F - s * f);
                    break;

                case 4:
                    r = v * (1.0F - s * (1.0F - f));
                    g = v * (1.0F - s);
                    break;

                case 5:
                    g = v * (1.0F - s);
                    b = v * (1.0F - s * f);
            }
        }

        return new Vector3f(r, g, b);
    }

    public boolean isPosInRenderableArea(EntityPlayerSP player, int posX, int posZ)
    {
        return getDistanceSq(player.posX, player.posZ, posX, posZ) < 36864.0D;
    }

    public void render(Minecraft minecraft, float partialTicks)
    {
        if (this.visible)
        {
            EntityPlayerSP player = minecraft.player;
            WorldClient world = minecraft.world;
            double x = player.prevPosX + (player.posX - player.prevPosX) * partialTicks;
            double y = player.prevPosY + (player.posY - player.prevPosY) * partialTicks;
            double z = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks;
            RenderHelper.disableStandardItemLighting();
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.0F);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glLineWidth(1.0F);
            GL11.glDepthMask(false);
            GL11.glPushMatrix();
            Tessellator tesselator = Tessellator.getInstance();
            BufferBuilder render = tesselator.getBuffer();
            Vector3f hsv = this.RGB2HSV(this.lineRed, this.lineGreen, this.lineBlue);
            Vector3f rgb = this.HSV2RGB((hsv.x + 0.01F) % 1.0F, hsv.y, hsv.z);
            this.lineRed = rgb.x;
            this.lineGreen = rgb.y;
            this.lineBlue = rgb.z;
            GL11.glColor4f(rgb.x, rgb.y, rgb.z, 0.35F);
            render.begin(GL11.GL_QUADS,DefaultVertexFormats.POSITION);
            int rad = 16 * this.radius;
            int hrf = rad / 2;

            for (int xx = 0; xx < rad; ++xx)
            {
                for (int zz = 0; zz < rad; ++zz)
                {
                    if (zz % 2 != xx % 2 && this.isPosInRenderableArea(player, (int)player.posX - hrf + xx, (int)player.posZ - hrf + zz))
                    {
                        int posX = (int)player.posX - hrf + xx;
                        int posZ = (int)player.posZ - hrf + zz;
                        BlockPos pos = new BlockPos(posX, player.posY, posZ);
                        BlockPos posN = new BlockPos(posX, player.posY, posZ - 1);
                        BlockPos posE = new BlockPos(posX + 1, player.posY, posZ);
                        BlockPos posW = new BlockPos(posX - 1, player.posY, posZ);
                        BlockPos posS = new BlockPos(posX, player.posY, posZ + 1);
                        int C = Biome.getIdForBiome(world.getBiome(pos));
                        int N = Biome.getIdForBiome(world.getBiome(posN));
                        int E = Biome.getIdForBiome(world.getBiome(posE));
                        int W = Biome.getIdForBiome(world.getBiome(posW));
                        int S = Biome.getIdForBiome(world.getBiome(posS));
                        double yy = 1.0D - y;
                        double yy2 = 256.0D - y;
                        
                        if (!this.unlimited)
                        {
                            pos = this.getTopLiquidOrSolidBlock2(world, pos);
                            posN = this.getTopLiquidOrSolidBlock2(world, posN);
                            posE = this.getTopLiquidOrSolidBlock2(world, posE);
                            posW = this.getTopLiquidOrSolidBlock2(world, posW);
                            posS = this.getTopLiquidOrSolidBlock2(world, posS);
                        }

                        if (N != C)
                        {
                            if (!this.unlimited)
                            {
                                yy = Math.max(pos.getY(), posN.getY()) - y;
                                yy2 = yy + this.lineheight;
                            }

                            render.pos(pos.getX() - x, yy, pos.getZ() - z).endVertex();
                            render.pos(pos.getX() + 1.0D - x, yy, pos.getZ() - z).endVertex();
                            render.pos(pos.getX() + 1.0D - x, yy2, pos.getZ() - z).endVertex();
                            render.pos(pos.getX() - x, yy2, pos.getZ() - z).endVertex();
                        }

                        if (E != C)
                        {
                            if (!this.unlimited)
                            {
                                yy = Math.max(pos.getY(), posE.getY()) - y;
                                yy2 = yy + this.lineheight;
                            }

                            render.pos(pos.getX() + 1.0D - x, yy, pos.getZ() - z).endVertex();
                            render.pos(pos.getX() + 1.0D - x, yy, pos.getZ() + 1.0D - z).endVertex();
                            render.pos(pos.getX() + 1.0D - x, yy2, pos.getZ() + 1.0D - z).endVertex();
                            render.pos(pos.getX() + 1.0D - x, yy2, pos.getZ() - z).endVertex();
                        }

                        if (W != C)
                        {
                            if (!this.unlimited)
                            {
                                yy = Math.max(pos.getY(), posW.getY()) - y;
                                yy2 = yy + this.lineheight;
                            }

                            render.pos(pos.getX() - x, yy, pos.getZ() - z).endVertex();
                            render.pos(pos.getX() - x, yy, pos.getZ() + 1.0D - z).endVertex();
                            render.pos(pos.getX() - x, yy2, pos.getZ() + 1.0D - z).endVertex();
                            render.pos(pos.getX() - x, yy2, pos.getZ() - z).endVertex();
                        }

                        if (S != C)
                        {
                            if (!this.unlimited)
                            {
                                yy = Math.max(pos.getY(), posS.getY()) - y;
                                yy2 = yy + this.lineheight;
                            }

                            render.pos(pos.getX() - x, yy, pos.getZ() + 1.0D - z).endVertex();
                            render.pos(pos.getX() + 1.0D - x, yy, pos.getZ() + 1.0D - z).endVertex();
                            render.pos(pos.getX() + 1.0D - x, yy2, pos.getZ() + 1.0D - z).endVertex();
                            render.pos(pos.getX() - x, yy2, pos.getZ() + 1.0D - z).endVertex();
                        }
                    }
                }
            }

            tesselator.draw();
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glDepthFunc(GL11.GL_LEQUAL);
            GL11.glPopMatrix();
            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
            RenderHelper.enableStandardItemLighting();
        }
    }

    public void renderBiomeName(int screenWidth, int screenHeight)
    {
        if (this.visible)
        {
            Minecraft minecraft = Minecraft.getMinecraft();
            WorldClient world = minecraft.world;

            if (minecraft.objectMouseOver != null && minecraft.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK && minecraft.objectMouseOver.getBlockPos() != null)
            {
                BlockPos pos = minecraft.objectMouseOver.getBlockPos();
                String str = "Looking block Biome: " + world.getBiome(pos).getBiomeName();
                FontRenderer fontRenderer = minecraft.fontRenderer;
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                int height = fontRenderer.FONT_HEIGHT;
                int width = fontRenderer.getStringWidth(str);
                int posY = screenHeight / 2 - 28;
                int posX = (screenWidth - width) / 2;
                Gui.drawRect(posX - 1, posY - 1, posX + width + 1, posY + height - 1, -1873784752);
                fontRenderer.drawStringWithShadow(str, posX, posY, 16777215);
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
        }
    }

    Vector3f RGB2HSV(float r, float g, float b)
    {
        float max = Math.max(r, g);
        max = Math.max(max, b);
        float min = Math.min(r, g);
        min = Math.min(min, b);
        float h = max - min;

        if (h > 0.0F)
        {
            if (max == r)
            {
                h = (g - b) / h;

                if (h < 0.0F)
                {
                    h += 6.0F;
                }
            }
            else if (max == g)
            {
                h = 2.0F + (b - r) / h;
            }
            else
            {
                h = 4.0F + (r - g) / h;
            }
        }

        h /= 6.0F;
        float s = max - min;

        if (max != 0.0F)
        {
            s /= max;
        }

        return new Vector3f(h, s, max);
    }

    public void setLineheight(float newVal)
    {
        this.lineheight = newVal;
    }

    public void setRadius(int newVal)
    {
        this.radius = newVal;
    }

    public void setUnlimited(boolean newVal)
    {
        this.unlimited = newVal;
    }

    @Deprecated
    public boolean toggleBiomeBorders()
    {
        this.visible = !this.visible;
        return this.visible;
    }
    
    public void setVisible(boolean visible) {
    	this.visible = visible;
    }
}
