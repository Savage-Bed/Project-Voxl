package com.savage.bed.voxl.controller;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.*;
import com.badlogic.gdx.utils.Array;
import com.savage.bed.voxl.VoxlSettings;
import com.savage.bed.voxl.object.CubeManager.Cube;

@SuppressWarnings("deprecation")
public class PlayerController extends InputAdapter
{
	private BoundingBox box, tmpBox;
	private PerspectiveCamera camera;
	private Vector3 dir, camDir, tmp, tmp2;
	protected static final float rotMul = 0.3f;
	protected boolean w, a, s, d, e, r, f, c, x, econt, jump, jumpable;
	private int dyt;
	private float jumpdelta, gravAccel;
	public boolean editAvailable;
	
	public PlayerController(PerspectiveCamera camera)
	{
		this.camera = camera;
		tmp = new Vector3();
		tmp2 = new Vector3();
		dir = new Vector3(0, 0, 1);
		camDir = new Vector3(0, 0, 1);
		camera.direction.set(0, 0, 1);
		box = new BoundingBox(tmp.setZero(), tmp2.set(0.4f, 1.9f, 0.4f));
		box.getCenter().set(0, 38, 0);
		tmpBox = new BoundingBox(tmp.setZero(), tmp2.set(1, 1, 1));
	}
	
	@Override
	public boolean keyDown(int keycode)
	{
		if(keycode == VoxlSettings.FORWARDKEY)
			w = true;
		if(keycode == VoxlSettings.LEFTKEY)
			a = true;
		if(keycode == VoxlSettings.BACKWARDKEY)
			s = true;
		if(keycode == VoxlSettings.RIGHTKEY)
			d = true;
		if(keycode == VoxlSettings.RUNKEY)
			e = true;
		if(keycode == VoxlSettings.UPKEY)
			r = true;
		if(keycode == VoxlSettings.DOWNKEY)
			f = true;
		if(keycode == VoxlSettings.EFORWARDKEY)
			c = true;
		if(keycode == VoxlSettings.EBACKWARDKEY)
			x = true;
		if(keycode == VoxlSettings.ECONTROLMODE)
			econt = !econt;
		
		if(keycode == VoxlSettings.JUMPKEY && jumpable)
		{
			jump = true;
			jumpdelta = 0;
		}
		return true;
	}
	
	@Override
	public boolean keyUp(int keycode)
	{
		if(keycode == VoxlSettings.FORWARDKEY)
			w = false;
		if(keycode == VoxlSettings.LEFTKEY)
			a = false;
		if(keycode == VoxlSettings.BACKWARDKEY)
			s = false;
		if(keycode == VoxlSettings.RIGHTKEY)
			d = false;
		if(keycode == VoxlSettings.RUNKEY)
			e = false;
		if(keycode == VoxlSettings.UPKEY)
			r = false;
		if(keycode == VoxlSettings.DOWNKEY)
			f = false;
		if(keycode == VoxlSettings.EFORWARDKEY)
			c = false;
		if(keycode == VoxlSettings.EBACKWARDKEY)
			x = false;
		
		return true;
	}
	
	public boolean notInPos(Vector3 vec)
	{
		return !box.contains(vec);
	}
	
	public boolean cubeNotInPos(float x, float y, float z)
	{
		tmpBox.getCenter().set(x, y, z);
		return !box.intersects(tmpBox);
	}
	
	public int x()
	{
		return Math.round(camera.position.x);
	}
	
	public int y()
	{
		return Math.round(camera.position.y - 1);
	}
	
	public int z()
	{
		return Math.round(camera.position.z);
	}
	
	protected void rotateCam()
	{
		int dx = -Gdx.input.getDeltaX();
		camDir.rotate(Vector3.Y, dx * rotMul);
		tmp.set(camDir).crs(Vector3.Y).nor();
		int dy = -Gdx.input.getDeltaY();
		if(dyt > 360)
			dyt = 360;
		if(dyt < -360)
			dyt = -360;
		if(dy > 360)
			dy = 360;
		if(dy < -360)
			dy = -360;
		if(dyt + dy > 360)
			dy = 360 - dyt;
		if(dyt + dy < -360)
			dy = -360 - dyt;

		dyt += dy;
		
		if(!(dy < 0 && camDir.y < -0.99f) && !(dy > 0 && camDir.y > 0.99f))
			camDir.rotate(tmp, dy * rotMul);	
		camera.direction.set(camDir);
		dir.set(camDir).y = 0;
		dir.nor();
	}
	
	public void update(Array<Cube> nearby, float delta)
	{
		rotateCam();
				
		if(jump)
		{
			jumpable = false;
			jumpdelta += delta;
			tmp.set(0, 6 * delta, 0);		
			box.getCenter().add(tmp);
			
			if(jumpdelta >= 1f / 4.5f)
			{
				jump = false;
			}
			
			for(Cube cube : nearby)
			{
				if(cube.box().intersects(box))
				{
					box.getCenter().y = cube.box().getCenter().y - 1.5f;
					jump = false;
					break;
				}
			}
		}
		else if(jump == false)
		{
			gravAccel -= delta / 5;
			if(gravAccel < -35)
				gravAccel = -35;
			tmp.set(0, gravAccel - 5 * delta, 0);		
			box.getCenter().add(tmp);
			
			for(Cube cube : nearby)
			{
				if(cube.box().intersects(box))
				{
					box.getCenter().y = cube.box().getCenter().y + 1.5f;
					gravAccel = 0;
					jumpable = true;
					break;
				}
				jumpable = false;
			}
		}
		tmp.setZero();
		
		if(w)
			tmp.add(dir.x, 0, 0);
		if(a)
			tmp.add(dir.z, 0, 0);
		if(s)
			tmp.add(-dir.x, 0, 0);
		if(d)
			tmp.add(-dir.z, 0, 0);
		
		tmp.scl(delta * (e ? 6 : 3));		
		box.getCenter().add(tmp);
		
		for(Cube cube : nearby)
		{
			if(cube.box().intersects(box))
			{
				box.getCenter().x = cube.box().getCenter().x + ((tmp.x > 0 ? -1 : 1) * (0.71f));
				break;
			}
		}
		
		tmp.setZero();
		
		if(w)
			tmp.add(0, 0, dir.z);
		if(a)
			tmp.add(0, 0, -dir.x);
		if(s)
			tmp.add(0, 0, -dir.z);
		if(d)
			tmp.add(0, 0, dir.x);
		
		tmp.scl(delta * (e ? 6 : 3));
		box.getCenter().add(tmp);
		
		for(Cube cube : nearby)
		{
			if(cube.box().intersects(box))
			{
				box.getCenter().z = cube.box().getCenter().z + ((tmp.z > 0 ? -1 : 1) * (0.71f));
				break;
			}
		}
		
		if(box.getCenter().y < -30)
			Gdx.app.exit();
	}
	
	public void updateCamPos()
	{
		camera.position.set(box.getCenter()).add(0, 0.5f, 0);
		
		camera.update();
	}
}
