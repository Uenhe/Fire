package fire.gen;

import mindustry.gen.Player;
import mindustry.gen.PlayerSpawnCallPacket;
import mindustry.world.Tile;

import static mindustry.Vars.net;

public class FireCall{

    /** see {@link mindustry.gen.Call#playerSpawn(Tile, Player)} */
    public static void playerSpawn(Tile tile, Player player){
        if(net.server() || !net.active()){
            fire.world.blocks.units.MechPad.playerSpawn(tile, player);
        }
        if(net.server()) {
            var packet = new PlayerSpawnCallPacket();
            packet.tile = tile;
            packet.player = player;
            net.send(packet, true);
        }
    }
}
