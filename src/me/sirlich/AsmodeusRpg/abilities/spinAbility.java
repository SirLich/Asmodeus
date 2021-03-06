package me.sirlich.AsmodeusRpg.abilities;

import me.sirlich.AsmodeusRpg.core.RpgPlayerList;
import me.sirlich.AsmodeusRpg.core.RpgPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class spinAbility extends Ability
{
    public spinAbility(Player p)
    {
        super(p);
        setName("Spin");
        setRechargeRate(100);
    }

    @Override
    public void run()
    {
        new BukkitRunnable()
        {
            public void run()
            {
                //Use cancel(); if you want to close this repeating task.
            }
        }.run();
    }

    public void spin()
    {
        Player player = getPlayer();
        RpgPlayer rpgPlayer = RpgPlayerList.getRpgPlayer(player);

    }
}
