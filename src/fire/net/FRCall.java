package fire.net;

import fire.world.blocks.units.MechPad;
import mindustry.gen.Call;
import mindustry.gen.Player;
import mindustry.gen.PlayerSpawnCallPacket;
import mindustry.world.Tile;

import static mindustry.Vars.net;

public class FRCall{

    /** @see Call#playerSpawn(Tile, Player) */
    public static void playerSpawn(Tile tile, Player player){
        if(net.server() || !net.active())
            MechPad.playerSpawn(tile, player);

        if(net.server()){
            var packet = new PlayerSpawnCallPacket();
            packet.tile = tile;
            packet.player = player;
            net.send(packet, true);
        }
    }
}
