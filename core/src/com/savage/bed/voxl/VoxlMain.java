package com.savage.bed.voxl;

import com.badlogic.gdx.*;
import com.savage.bed.voxl.screen.*;

public class VoxlMain extends Game
{

	@Override
	public void create()
	{
		VoxlSettings.calcCubeTex();
		setScreen(new MainScreen(this));
	}	
}
