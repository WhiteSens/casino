package com.casino.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class MenuScreen extends BaseCasinoScreen {
	// координаты относительно левого верхнего угла menu_panel.png (490x602)
	private static final float BX = 47, BW = 388, BH = 88;
	private static final float[] BY = {87, 200, 313, 427};
	private static final float BOT_Y = 647, BOT_W = 241, BOT_H = 50;
	private static final float BACK_X = -14, CLOSE_X = 259;
	private static final float KEY_Y = 705, KEY_W = 514, KEY_H = 44;

	private boolean waitingKey;

	public MenuScreen() {
		super("Казино", -14, 514, 749, 0.36f, null);
	}

	@Override
	protected void drawDesign(DrawContext ctx, float mx, float my, float delta) {
		tex(ctx, id("menu_panel"), 490, 602, 0, 0, 490, 602);
		for (float y : BY) {
			hover(ctx, mx, my, BX, y, BW, BH);
		}
		tex(ctx, id("btn_back_small"), 241, 50, BACK_X, BOT_Y, BOT_W, BOT_H);
		tex(ctx, id("btn_close_small"), 241, 50, CLOSE_X, BOT_Y, BOT_W, BOT_H);
		hover(ctx, mx, my, BACK_X, BOT_Y, BOT_W, BOT_H);
		hover(ctx, mx, my, CLOSE_X, BOT_Y, BOT_W, BOT_H);

		// кнопка смены клавиши открытия меню
		tex(ctx, id("btn_blank_wide"), 515, 51, BACK_X, KEY_Y, KEY_W, KEY_H);
		hover(ctx, mx, my, BACK_X, KEY_Y, KEY_W, KEY_H);
		String label = waitingKey
				? "Нажмите клавишу... (Esc - отмена)"
				: "Клавиша открытия: " + CasinoMod.openKey.getBoundKeyLocalizedText().getString();
		textCentered(ctx, label, BACK_X + KEY_W / 2f, KEY_Y + (KEY_H - 9f * 1.2f / s) / 2f, 1.2f, 0xFFFFFFFF, true);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (waitingKey) {
			waitingKey = false;
			if (keyCode != GLFW.GLFW_KEY_ESCAPE) {
				CasinoMod.openKey.setBoundKey(InputUtil.fromKeyCode(keyCode, scanCode));
				KeyBinding.updateKeysByCode();
				client.options.write();
			}
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	protected boolean onDesignClick(float x, float y) {
		if (waitingKey) {
			waitingKey = false;
			return true;
		}
		if (in(x, y, BACK_X, KEY_Y, KEY_W, KEY_H)) {
			click();
			waitingKey = true;
			return true;
		}
		if (in(x, y, BACK_X, BOT_Y, BOT_W, BOT_H) || in(x, y, CLOSE_X, BOT_Y, BOT_W, BOT_H)) {
			click();
			close();
			return true;
		}
		for (int i = 0; i < BY.length; i++) {
			if (in(x, y, BX, BY[i], BW, BH)) {
				click();
				open(i);
				return true;
			}
		}
		return false;
	}

	private void open(int i) {
		Screen next = null;
		switch (i) {
			case 0 -> next = new RouletteScreen(this);
			case 1 -> next = new FortuneScreen(this);
			case 2 -> next = new HorseScreen(this);
			default -> next = new DynamiteScreen(this);
		}
		client.setScreen(next);
	}
}
