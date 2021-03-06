package xzeroair.trinkets.items.trinkets;

import java.util.UUID;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xzeroair.trinkets.api.TrinketHelper;
import xzeroair.trinkets.items.base.AccessoryBase;
import xzeroair.trinkets.items.effects.EffectsDamageShield;
import xzeroair.trinkets.util.TrinketsConfig;

public class TrinketDamageShield extends AccessoryBase {

	public TrinketDamageShield(String name) {
		super(name);
	}

	protected final UUID uuid = UUID.fromString("c0885371-20dd-4c56-86eb-78f24d9fe777");
	protected ResourceLocation background = null;
	protected boolean hasResist = false;

	@Override
	public void eventPlayerTick(ItemStack stack, EntityPlayer player) {
		if(!player.isPotionActive(MobEffects.RESISTANCE)) {
			player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE,200,0,false,false));
			hasResist = false;
		}
		if(player.isPotionActive(MobEffects.RESISTANCE)) {
			final int dur = player.getActivePotionEffect(MobEffects.RESISTANCE).getDuration();
			final int amp = player.getActivePotionEffect(MobEffects.RESISTANCE).getAmplifier();
			if((dur < 200) && !(amp > 0) && (hasResist == false)) {
				player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE,200,0,false,false));
				hasResist = false;
			}
			if(((dur > 200) && (amp < 2)) && (hasResist == false)) {
				player.getActivePotionEffect(MobEffects.RESISTANCE).combine(new PotionEffect(MobEffects.RESISTANCE,dur+1,amp+1,false,false));
				hasResist = true;
			}
		}
	}

	@Override
	public void eventPlayerHurt(LivingHurtEvent event, ItemStack stack, EntityLivingBase player) {
		EffectsDamageShield.eventPlayerHurt(event, stack, player);
	}

	@Override
	public void eventLivingDamageAttacked(LivingDamageEvent event, ItemStack stack, EntityLivingBase player) {
		EffectsDamageShield.eventLivingDamageAttacked(event, stack, player);
	}

	@Override
	public boolean playerCanEquip(ItemStack stack, EntityLivingBase player) {
		if(TrinketHelper.AccessoryCheck(player, stack.getItem())) {
			return false;
		} else {
			return super.playerCanEquip(stack, player);
		}
	}

	@Override
	public void playerEquipped(ItemStack stack, EntityLivingBase player) {
		player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, .75F, 1.9f);
		super.playerEquipped(stack, player);
	}

	@Override
	public void playerUnequipped(ItemStack stack, EntityLivingBase player) {
		if(!TrinketHelper.AccessoryCheck(player, stack.getItem())) {
			player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, .75F, 2f);
			if(player.isPotionActive(MobEffects.RESISTANCE)) {
				player.removeActivePotionEffect(MobEffects.RESISTANCE);
				hasResist = false;
			}
		}
		super.playerUnequipped(stack, player);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void playerRender(ItemStack stack, EntityLivingBase player, float partialTicks, boolean isBauble) {
		if(!TrinketsConfig.CLIENT.DAMAGE_SHIELD.doRender) {
			return;
		}
		final float scale = 0.2F;
		GlStateManager.pushMatrix();
		GlStateManager.rotate(180F, 1F, 0F, 0F);
		GlStateManager.translate(0.15F, -0.22F, 0.14F);
		if(player.isSneaking()) {
			GlStateManager.translate(0F, -0.24F, -0.07F);
			GlStateManager.rotate(90F / (float) Math.PI, 1.0F, 0.0F, 0.0F);
		}
		if (player.hasItemInSlot(EntityEquipmentSlot.CHEST)) {
			GlStateManager.translate(0F, 0F, 0.06F);
		}
		GlStateManager.scale(scale, scale, scale);
		Minecraft.getMinecraft().getRenderItem().renderItem(getDefaultInstance(), ItemCameraTransforms.TransformType.NONE);
		GlStateManager.popMatrix();

		if(player.getUniqueID().toString().contentEquals("7f184d63-9f9c-47a7-be03-8382145fb2c2") || player.getUniqueID().toString().contentEquals("cdfccefb-1a2e-4fb8-a3b5-041da27fde61")) {// || player.getUniqueID().toString().contentEquals("f5f28614-4e8b-4788-ae78-b020493dc5cb") || player.getName().contentEquals("Airgre")) {
			background = new ResourceLocation("xat:textures/awesomesauce/damage_shield.png");
			GlStateManager.pushMatrix();
			if (player.hasItemInSlot(EntityEquipmentSlot.CHEST)) {
				GlStateManager.translate(0F, 0F, -0.07F);
			}
			GlStateManager.translate(0, 0, -0.95F);
			Minecraft.getMinecraft().renderEngine.bindTexture(background);
			final float size = 0.165f;
			Draw(0.065, 0.14,  0.8, 0, 0, size, size, 6.0f, 1, 1, 1, 1);
			GlStateManager.popMatrix();
		}
	}

	public static void Draw(double x, double y, double z, int texX, int texY, float width, float height, float scale, float r, float g, float b, float a) {
		GlStateManager.pushMatrix();
		//		GlStateManager.color(r, g, b, a);
		final int l1 = 240;
		final int l2 = 0;
		final Tessellator tes = Tessellator.getInstance();
		final BufferBuilder buffer = tes.getBuffer();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
		//Bottom Left
		buffer.pos(x + 0, y + height, z).tex((texX + 0) * scale, (texY + height) * scale).color(r, g, b, a).endVertex();
		//		buffer.pos(x + 0, y + height, 0).color(r, g, b, a).lightmap(l1, l2).endVertex();
		//bottom right
		buffer.pos(x + width, y + height, z).tex((texX + width) * scale, (texY + height) * scale).color(r, g, b, a).endVertex();
		//		buffer.pos(x + width, y + height, 0).color(r, g, b, a).lightmap(l1, l2).endVertex();
		//top right
		buffer.pos(x + width, y + 0, z).tex((texX + width) * scale, (texY + 0) * scale).color(r, g, b, a).endVertex();
		//		buffer.pos(x + width, y + 0, 0).color(r, g, b, a).lightmap(l1, l2).endVertex();
		//top left
		buffer.pos(x + 0, y + 0, z).tex((texX + 0) * scale, (texY + 0) * scale).color(r, g, b, a).endVertex();
		//		buffer.pos(x + 0, y + 0, 0).color(r, g, b, a).lightmap(l1, l2).endVertex();
		tes.draw();
		GlStateManager.popMatrix();
	}

	@Override
	public boolean ItemEnabled() {
		return TrinketsConfig.SERVER.DAMAGE_SHIELD.enabled;
	}

	@Override
	public boolean hasDiscription(ItemStack stack) {
		return true;
	}
}