package me.sirlich.AsmodeusRpg;

import me.sirlich.AsmodeusRpg.abilities.AbilitiesEditor;
import me.sirlich.AsmodeusRpg.abilities.AbilitiesHandler;
import me.sirlich.AsmodeusRpg.cancellers.*;
import me.sirlich.AsmodeusRpg.core.*;
import me.sirlich.AsmodeusRpg.items.AttackEventHandler;
import me.sirlich.AsmodeusRpg.items.ItemHandler;
import me.sirlich.AsmodeusRpg.mobs.entityHandling.RpgSummon;
import me.sirlich.AsmodeusRpg.mobs.monsters.*;
import me.sirlich.AsmodeusRpg.mobs.npcs.*;
import me.sirlich.AsmodeusRpg.regions.Region;
import me.sirlich.AsmodeusRpg.regions.RegionUtils;
import me.sirlich.AsmodeusRpg.testing.*;
import me.sirlich.AsmodeusRpg.utilities.AsmodeusCommand;
import me.sirlich.AsmodeusRpg.utilities.NMSUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandMap;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class AsmodeusRpg extends JavaPlugin
{
    static CommandMap map;
    private static AsmodeusRpg instance;
    private int regenTickrate = 20;
    private String world = "AsmodeusRpg";

    public AsmodeusRpg()
    {
        instance = this;
    }

    public static AsmodeusRpg getInstance()
    {
        return instance;
    }

    public static void register(AsmodeusCommand... cmds)
    {
        try {
            final Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            f.setAccessible(true);
            map = (CommandMap) f.get(Bukkit.getServer());

            for (AsmodeusCommand cmd : cmds) {
                map.register(cmd.getName(), cmd);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void handleConfigData(){
        //World
        this.world = this.getConfig().getString("main-world");
        System.out.println("World is: " + world);
        //Regen tickrate
        this.regenTickrate = Integer.parseInt(this.getConfig().getString("regen-tickrate"));
        System.out.println("Tick Refresh Rate is: " + regenTickrate);
    }



    @Override
    public void onEnable()
    {
        handleConfigData();
        RegionUtils.loadFiles();
        RpgPassiveRegen.startTicker();

        register(new GetItem());
        register(new testDamageCommand());
        register(new getPlayerHealthCommand());
        register(new PrintGameValues());
        register(new RpgSummon());
        register(new RecalculateEntityList());
        register(new SetGameValues());
        register(new RenameItem());


        NMSUtils.registerEntity("rpg_lich",NMSUtils.Type.SKELETON, RpgLich.class,false);
        NMSUtils.registerEntity("rpg_critter",NMSUtils.Type.SILVERFISH, RpgCritter.class,false);

        listener(new BlacksmithHandler());
        listener(new CivilianHandler());
        listener(new AbilitiesHandler());
        listener(new PlayerJoinHandler());
        listener(new CancelPassiveRegeneration());
        listener(new PlayerLeaveHandler());
        listener(new AbilitiesEditor());
        listener(new CancelHunger());
        //listener(new RPGDamage());
        listener(new PlayerRespawnHandler());
        listener(new PlayerAttackEntityHandler());
        listener(new CancelMobDrops());
        listener(new CancelMobSunDamage());
        listener(new DebugStick());
        listener(new CancelFallDamage());

        initStationaryMobs();

        AttackEventHandler.loadEvents();
        ItemHandler.loadItems();

        /*RPGWeapon primaryTest = new RPGWeapon("primary");
        primaryTest
                .primaryEvent(new DamageReaction().setDamage(1, 5).setRange(10.0).setStamina(0).setKnockback(2).setKnockup(1).finish())
                .texture(Texture.PRIMARY)
                .rarity(0)
                .level(10)
                .name("Primary Test")
                .description("&9This weapon was created to", "&9test the functionalities of the", "&9Item System.")
                .finish();

        RPGWeapon secondaryTest = new RPGWeapon("secondary");
        secondaryTest
                .secondaryEvent(new Heal().setRecovery(10).setCooldown(2).finish())
                .texture(Texture.SECONDARY)
                .rarity(1)
                .level(25)
                .name("Secondary Test")
                .description("&9This weapon was created to", "&9test the functionalities of the", "&9Item System.")
                .finish();

        RPGWeapon doubleTest = new RPGWeapon("dual");
        doubleTest
                .primaryEvent(new DamageReaction().setDamage(5, 8).setRange(15).setStamina(0).setKnockback(1).setKnockup(2).finish())
                .secondaryEvent(new Heal().setRecovery(40).setCooldown(4).finish())
                .texture(Texture.DUAL)
                .rarity(2)
                .level(69)
                .name("Dual Test")
                .description("&9This weapon was created to", "&9test the functionalities of the", "&9Item System.", "&9Wait, is that two abilities?")
                .finish();*/

        /*new RPGWeapon(Texture.WOOD_SWORD, "Test Sword", "weapon_test-sword", RPGWeapon.Rarity.COMMON, true,
                1, 5, 5, 0, false, 0, 0, 0,
                0, null, null, null);*/
    }

    @Override
    public void onDisable()
    {

        //Kick players
        for(Player player : Bukkit.getOnlinePlayers()){
            player.kickPlayer("AsmodeusRpg is reloading. Please login again.");
        }

        //Kill mobs
        for(Entity entity : Bukkit.getWorld(AsmodeusRpg.getInstance().getWorld()).getEntities()){
            entity.remove();
        }
        System.out.println("Asmodeus disabled");
    }

    public String getWorld(){
        return world;
    }

    public int getRegenTickrate(){
        return regenTickrate;
    }

    public void setWorld(String s){
        this.world = s;
    }

    public void setRegenTickrate(int i){
        this.regenTickrate = i;
    }
    private void initStationaryMobs()
    {
        System.out.println("Begin mob spawning...");

        //Spawn blacksmiths
        try {
            BufferedReader br = new BufferedReader(new FileReader(getDataFolder() + "/blacksmith.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                List<String> arr = Arrays.asList(line.split(","));
                World world = Bukkit.getServer().getWorld(AsmodeusRpg.getInstance().getWorld());
                Location loc = new Location(world, Double.parseDouble(arr.get(0)), Double.parseDouble(arr.get(1)), Double.parseDouble(arr.get(2)));
                Blacksmith keeper = new Blacksmith(((CraftWorld) world).getHandle());
                keeper.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
                ((CraftWorld) world).addEntity(keeper, CreatureSpawnEvent.SpawnReason.CUSTOM);
                System.out.println("Blacksmith successfully added.");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Spawn Civilians
        try {
            BufferedReader br = new BufferedReader(new FileReader(getDataFolder() + "/civilians/civilians.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                civilianLoader(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void civilianLoader(String s)
    {
        try {
            BufferedReader br = new BufferedReader(new FileReader(getDataFolder() + "/civilians/" + s + ".txt"));

            World world = Bukkit.getWorld(AsmodeusRpg.getInstance().getWorld());
            String name = br.readLine();
            int profession = Integer.parseInt(br.readLine());
            List<String> locs = Arrays.asList(br.readLine().split(","));
            Location loc = new Location(world, Double.parseDouble(locs.get(0)), Double.parseDouble(locs.get(1)), Double.parseDouble(locs.get(2)));
            String regionID = br.readLine();
            Region region = RegionUtils.getRegion(regionID);
            System.out.println("Region name: " + region.getName());
            List<String> quotes = Arrays.asList(br.readLine().split(">"));
            System.out.println("1");
            System.out.println("Region name: " + region.getName());
            Civilian civilian = new Civilian(((CraftWorld) world).getHandle(), name, profession, region);
            System.out.println("2");
            CivilianList.addEntity(civilian.getBukkitEntity(), quotes, loc, regionID);
            System.out.println("3");
            civilian.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
            System.out.println("4");
            ((CraftWorld) world).addEntity(civilian, CreatureSpawnEvent.SpawnReason.CUSTOM);
            System.out.println("5");
            System.out.println("Civilian successfully added.");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listener(Listener... listeners)
    {
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }
}
