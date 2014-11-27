package com.savage.bed.voxl.object;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Array;
import com.savage.bed.voxl.object.CubeManager.IgnoreFaceFlag;
import com.savage.bed.voxl.object.CubeManager.Cube;

public class Chunk
{
	public static final int CHUNK_XZ = 16;
	public static final int CHUNK_HEIGHT = 96;
	public Cube[][][] cubes;
	
	public final int cx, cz, x, z;
	public boolean modelInvalid;
	private static final ModelBuilder moBuild = new ModelBuilder();
	private static final MeshBuilder meBuild = new MeshBuilder();
	private ModelInstance chunk;
	public static ChunkManager parent;
	public static final Cube sunlight = new Cube("sunlight", 0, 0, 0, 10, true);
	
	/**Please give in Chunk coordinates, not World!*/
	public Chunk(int x, int z)
	{
		cubes = new Cube[CHUNK_XZ][CHUNK_HEIGHT][CHUNK_XZ];
		this.x = x * CHUNK_XZ;
		this.z = z * CHUNK_XZ;
		this.cx = x;
		this.cz = z;
	}
	
	public boolean inBoundsAbs(int absX, int absY, int absZ)
	{
		absX -= x;
		absZ -= z;
		return absX >= 0 && absY >= 0 && absZ >= 0 && absX < CHUNK_XZ && absY < CHUNK_HEIGHT && absZ < CHUNK_XZ;
	}
	
	public Cube getCubeRel(int relX, int relY, int relZ)
	{
		if(relX >= 0 && relY >= 0 && relZ >= 0 && relX < CHUNK_XZ && relY < CHUNK_HEIGHT && relZ < CHUNK_XZ)
			return cubes[relX][relY][relZ];
		else
			return null;
	}
	
	public Cube getCubeFastRel(int relX, int relY, int relZ)
	{
		return cubes[relX][relY][relZ];
	}
	
	public Cube getCubeFastAbs(int absX, int absY, int absZ)
	{
		return cubes[absX - x][absY][absZ - z];
	}
	
	public Cube getCubeAbs(int absX, int absY, int absZ)
	{
		absX -= x;
		absZ -= z;
		if(absX >= 0 && absY >= 0 && absZ >= 0 && absX < CHUNK_XZ && absY < CHUNK_HEIGHT && absZ < CHUNK_XZ)
			return cubes[absX][absY][absZ];
		
		return null;
	}
	
	public void addCubeFastRel(Cube cube)
	{
		int u = cube.x();
		int v = cube.y();
		int w = cube.z();
		cube.setPosition(u + x, v, w + z);
		cubes[u][v][w] = cube;
		
		modelInvalid = true;
	}
	
	public void addCubeRel(Cube cube)
	{
		int u = cube.x();
		int v = cube.y();
		int w = cube.z();
		if(u >= 0 && v >= 0 && w >= 0 && u < CHUNK_XZ && v < CHUNK_HEIGHT && w < CHUNK_XZ)
		{
			cube.setPosition(u + x, v, w + z);
			cubes[u][v][w] = cube;
			
			modelInvalid = true;
		}
	}
	
	public void addAllFastRel(Iterable<Cube> cubes)
	{
		for(Cube cube : cubes)
			addCubeFastRel(cube);
	}
	
	public void addAllRel(Iterable<Cube> cubes)
	{
		for(Cube cube : cubes)
			addCubeRel(cube);
	}
	
	public void addCubeFastAbs(Cube cube)
	{
		int u = cube.x();
		int v = cube.y();
		int w = cube.z();
		
		cubes[u - x][v][w - z] = cube;
		
		modelInvalid = true;
	}
	
	public void addCubeAbs(Cube cube)
	{
		int u = cube.x() - x;
		int v = cube.y();
		int w = cube.z() - z;
		if(u >= 0 && v >= 0 && w >= 0 && u < CHUNK_XZ && v < CHUNK_HEIGHT && w < CHUNK_XZ)
		{
			cubes[u][v][w] = cube;
			
			modelInvalid = true;
		}
	}
	
	public void addAllFastAbs(Iterable<Cube> cubes)
	{
		for(Cube cube : cubes)
			addCubeFastAbs(cube);
	}
	
	public void addAllAbs(Iterable<Cube> cubes)
	{
		for(Cube cube : cubes)
			addCubeAbs(cube);
	}
	
	public void deleteCubeFastRel(int relX, int relY, int relZ)
	{
		//Cube cube = cubes[relX][relY][relZ];
		cubes[relX][relY][relZ] = null;
		
		modelInvalid = true;
	}
	
	public void deleteCubeFastAbs(int absX, int absY, int absZ)
	{
		//Cube cube = cubes[absX - x][absY][absZ - z];
		cubes[absX - x][absY][absZ - z] = null;
		
		modelInvalid = true;
	}
	
	public void deleteCubeRel(int relX, int relY, int relZ)
	{
		if(relX >= 0 && relY >= 0 && relZ >= 0 && relX < CHUNK_XZ && relY < CHUNK_HEIGHT && relZ < CHUNK_XZ)
			deleteCubeFastRel(relX, relY, relZ);
	}
	
	public int getHeightRel(int relX, int relZ)
	{
		for(int v = CHUNK_HEIGHT - 1; v >= 0; v--)
			if(cubes[relX][v][relZ] != null)
				return v;
		return CHUNK_HEIGHT;
	}
	
	public int getHeightAbs(int absX, int absZ)
	{
		absX -= x;
		absZ -= z;
		for(int v = 0; v < CHUNK_HEIGHT; v++)
			if(cubes[absX][v][absZ] == null)
				return v;
		return CHUNK_HEIGHT;
	}
	
	public void render(ModelBatch batch, Environment env)
	{
		if(chunk != null)
			batch.render(chunk, env);
		else
			modelInvalid = true;
	}
	
	public void render(ModelBatch batch)
	{
		if(chunk != null)
			batch.render(chunk);
		else
			modelInvalid = true;
	}
	
	public void buildModel()
	{
		moBuild.begin();
		meBuild.begin(VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
		meBuild.part("main", GL20.GL_TRIANGLES);
		
		IgnoreFaceFlag ignore = new IgnoreFaceFlag();
		
		for(int u = 0; u < CHUNK_XZ; u++)
		{
			for(int v = 0; v < CHUNK_HEIGHT; v++)
			{
				for(int w = 0; w < CHUNK_XZ; w++)
				{
					if(cubes[u][v][w] != null)
					{
						ignore.resetFlags();
						if(u > 0)
						{
							if(cubes[u - 1][v][w] == null)
								ignore.left = false;
							else
								ignore.left = true;
						}
						if(u < CHUNK_XZ - 1)
						{
							if(cubes[u + 1][v][w] == null)
								ignore.right = false;
							else
								ignore.right = true;
						}
						if(v > 0)
						{
							if(cubes[u][v - 1][w] == null)
								ignore.bottom = false;
							else
								ignore.bottom = true;
						}
						if(v < CHUNK_HEIGHT - 1)
						{
							if(cubes[u][v + 1][w] == null)
								ignore.top = false;
							else
								ignore.top = true;
						}
						if(w > 0)
						{
							if(cubes[u][v][w - 1] == null)
								ignore.back = false;
							else
								ignore.back = true;
						}
						if(w < CHUNK_XZ - 1)
						{
							if(cubes[u][v][w + 1] == null)
								ignore.front = false;
							else
								ignore.front = true;
						}
						
						CubeManager.genCube(cubes[u][v][w], meBuild, ignore);
					}
				}
			}
		}
		
		moBuild.part(meBuild.getMeshPart(), CubeManager.staticMaterial);
		meBuild.end();
		if(chunk != null)
			chunk.model.dispose();
		chunk = new ModelInstance(moBuild.end());
		modelInvalid = false;
	}
	
	public void getNearby(int x, int y, int z, Array<Cube> out)
	{
		x -= this.x;
		z -= this.z;
		
		for(int u = -3; u <= 3; u++)
		{
			for(int v = -3; v <= 3; v++)
			{
				for(int w = -3; w <= 3; w++)
				{
					if(x + u < 0 | x + u > CHUNK_XZ - 1 | y + v < 0 | y + v > CHUNK_HEIGHT - 1 | z + w < 0 | z + w > CHUNK_XZ - 1)
						continue;
					if(cubes[x + u][y + v][z + w] != null)
						out.add(cubes[x + u][y + v][z + w]);
				}
			}
		}
	}
}
