package de.peacepunkt.tda2plugin.game.command;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

public class SpawnWorldGenerator extends ChunkGenerator {
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return Arrays.asList(new BlockPopulator[0]);
    }

    public boolean canSpawn(World world, int x, int z) {
        return true;
    }

    public byte[] generate(World world, Random rand, int chunkx, int chunkz) {
        return new byte[32768];
    }

    public Location getFixedSpawnLocation(World world, Random random) {
        return new Location(world, 0, 128, 0);
    }
    
    @Override
    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
    	ChunkData chunk = createChunkData(world);
    	if(chunkX == 0 && chunkZ == 0) {
    		chunk.setBlock(0, 150, 0, Material.BEDROCK);
    	}
    	return chunk;
    }
    
    /*private class BlankPopulator extends BlockPopulator {
		@Override
		public void populate(World arg0, Random arg1, Chunk source) {
			if(source.getX() == 0 && source.getZ() == 0) {
				source.getBlock(0, 150, 0).setType(Material.BEDROCK);
			}
			
		}
	}*/

}
