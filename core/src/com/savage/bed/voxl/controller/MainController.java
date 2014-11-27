package com.savage.bed.voxl.controller;

import com.badlogic.gdx.*;
import com.savage.bed.voxl.VoxlSettings;
import com.savage.bed.voxl.object.CubeManager;

public class MainController extends InputAdapter
{
	public boolean gamePaused, updatePlayer, create, destroy, showInventory;
	private final PlayerController pController;
	public int selected;
	
	public MainController(PlayerController pController)
	{
		Gdx.input.setCursorCatched(true);
		updatePlayer = true;
		this.pController = pController;
	}
	
	@Override
	public boolean keyDown(int keycode)
	{
		if(keycode == VoxlSettings.PAUSEKEY)
		{
			gamePaused = !gamePaused;
			showInventory = false;
			Gdx.input.setCursorCatched(!gamePaused);
			Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
			
			return true;
		}
		if(keycode == VoxlSettings.INVENTORY && !gamePaused)
		{
			showInventory = !showInventory;
			Gdx.input.setCursorCatched(!showInventory);
			Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
			
			return true;
		}
		if(keycode == VoxlSettings.CHANGEMODE && pController.editAvailable)
		{
			updatePlayer = !updatePlayer;
			return true;
		}
		if(keycode == VoxlSettings.EXITKEY)
		{
			Gdx.app.exit();
			return true;
		}
		if(keycode == VoxlSettings.ISELECTNEXT && showInventory)
		{
			selected += 1;
			if(selected >= CubeManager.types.size())
				selected = CubeManager.types.size() - 1;
			return true;
		}
		if(keycode == VoxlSettings.ISELECTPREVIOUS && showInventory)
		{
			selected -= 1;
			if(selected < 0)
				selected = 0;
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
		if(button == VoxlSettings.CREATEBLOCK && !destroy)
			create = true;
		if(button == VoxlSettings.DESTROYBLOCK && !create)
			destroy = true;
		
		return false;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button)
	{
		if(button == VoxlSettings.CREATEBLOCK)
			create = false;
		if(button == VoxlSettings.DESTROYBLOCK)
			destroy = false;
		
		return true;
	}
	
	@Override
	public boolean scrolled(int amount)
	{
		if(showInventory | !updatePlayer)
			selected += amount;
		if(selected >= CubeManager.types.size())
			selected = CubeManager.types.size() - 1;
		if(selected < 0)
			selected = 0;
		return true;
	}
}
