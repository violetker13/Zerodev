package org.example.world;

import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;

public class GenWorld {
   public static void init(InstanceContainer instance){
       instance.setChunkSupplier(LightingChunk::new);


       for(int i = 0; i < 32; i++) {
           for(int j = 0; j < 32; j++) {
               instance.setBlock(i, 40, j, Block.GRASS_BLOCK);
           }
       }

   }
}
