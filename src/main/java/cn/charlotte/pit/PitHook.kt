package cn.charlotte.pit
import cn.charlotte.pit.ThePit
import cn.charlotte.pit.data.CDKData
import cn.charlotte.pit.events.genesis.GenesisCombatListener
import cn.charlotte.pit.util.hologram.packet.PacketHologramRunnable
import com.comphenix.protocol.ProtocolLibrary
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory
import dev.rollczi.litecommands.bukkit.LiteBukkitMessages
import dev.rollczi.litecommands.meta.Meta
import dev.rollczi.litecommands.validator.ValidatorScope
import io.lumine.xikage.mythicmobs.legacy.commands.DebugCommands
import cn.charlotte.pit.actionbar.ActionBarManager
import cn.charlotte.pit.command.*
import cn.charlotte.pit.command.handler.HandHasItem
import cn.charlotte.pit.command.handler.HandHasItemValidator
import cn.charlotte.pit.command.handler.metaKey
import cn.charlotte.pit.config.NewConfiguration
import cn.charlotte.pit.config.TabConfiguration
import cn.charlotte.pit.data.operator.ProfileOperator
import cn.charlotte.pit.enchantment.type.addon.AngelArmsEnchant
import cn.charlotte.pit.enchantment.type.aqua.ClubRodEnchant
import cn.charlotte.pit.enchantment.type.aqua.GrandmasterEnchant
import cn.charlotte.pit.enchantment.type.aqua.LuckOfPondEnchant
import cn.charlotte.pit.enchantment.type.aqua.RogueEnchant
import cn.charlotte.pit.enchantment.type.auction.FractionalReserveEnchant
import cn.charlotte.pit.enchantment.type.auction.rare.PaparazziEnchant
import cn.charlotte.pit.enchantment.type.auction.rare.PitMBAEnchant
import cn.charlotte.pit.enchantment.type.dark_normal.*
import cn.charlotte.pit.enchantment.type.dark_rare.*
import cn.charlotte.pit.enchantment.type.dj.BadApple
import cn.charlotte.pit.enchantment.type.dj.EverybodyDanceNow
import cn.charlotte.pit.enchantment.type.dj.FlowerDance
import cn.charlotte.pit.enchantment.type.dj.FugueInDMinor
import cn.charlotte.pit.enchantment.type.dj.GerudoValley
import cn.charlotte.pit.enchantment.type.dj.GirlsBandCry
import cn.charlotte.pit.enchantment.type.dj.ICantWait
import cn.charlotte.pit.enchantment.type.dj.MortalKombat
import cn.charlotte.pit.enchantment.type.dj.RainbowTylenol
import cn.charlotte.pit.enchantment.type.dj.zaijian
import cn.charlotte.pit.enchantment.type.genesis.*
import cn.charlotte.pit.enchantment.type.normal.*
import cn.charlotte.pit.enchantment.type.op.*
import cn.charlotte.pit.enchantment.type.rage.*
import cn.charlotte.pit.enchantment.type.ragerare.Angel
import cn.charlotte.pit.enchantment.type.ragerare.End
import cn.charlotte.pit.enchantment.type.ragerare.EnergyStorage
import cn.charlotte.pit.enchantment.type.ragerare.EvasionEnchant
import cn.charlotte.pit.enchantment.type.ragerare.Plutocrat
import cn.charlotte.pit.enchantment.type.ragerare.Regularity
import cn.charlotte.pit.enchantment.type.ragerare.ThinkOfThePeopleEnchant
import cn.charlotte.pit.enchantment.type.rare.*
import cn.charlotte.pit.enchantment.type.sewer_normal.AegisEnchant
import cn.charlotte.pit.enchantment.type.sewer_normal.EliminatePowerEnchant
import cn.charlotte.pit.enchantment.type.sewer_normal.SpywareEnchant
import cn.charlotte.pit.enchantment.type.sewer_rare.HiddenGemsEnchant
import cn.charlotte.pit.enchantment.type.sewer_rare.TrashPandaEnchant
import cn.charlotte.pit.enchantment.type.special.SoulRipperEnchant
import cn.charlotte.pit.events.impl.*
import cn.charlotte.pit.events.impl.major.*
import cn.charlotte.pit.hologram.HologramListener
import cn.charlotte.pit.hook.ItemPapiHook
import cn.charlotte.pit.hook.PitPapiHook
import cn.charlotte.pit.impl.PitInternalImpl.loaded
import cn.charlotte.pit.impl.PlayerPointsAPI
import cn.charlotte.pit.item.factory.ItemFactory
import cn.charlotte.pit.item.type.*
import cn.charlotte.pit.item.type.egg.SpeedEggs
import cn.charlotte.pit.item.type.mythic.MythicBowItem
import cn.charlotte.pit.item.type.mythic.MythicLeggingsItem
import cn.charlotte.pit.item.type.mythic.MythicSwordItem
import cn.charlotte.pit.item.type.perk.Sceptre
import cn.charlotte.pit.item.type.sewers.Milk
import cn.charlotte.pit.item.type.sewers.Rubbish
import cn.charlotte.pit.listener.*
import cn.charlotte.pit.map.kingsquests.KingsQuests
import cn.charlotte.pit.menu.shop.button.type.BowBundleShopButton
import cn.charlotte.pit.menu.shop.button.type.CombatSpadeShopButton
import cn.charlotte.pit.menu.shop.button.type.PantsBundleShopButton
import cn.charlotte.pit.menu.shop.button.type.SwordBundleShopButton
import cn.charlotte.pit.menu.trade.TradeListener
import cn.charlotte.pit.nametag.NameTagImpl
import cn.charlotte.pit.npc.type.*
import cn.charlotte.pit.npc.type.custom.SewersNpc
import cn.charlotte.pit.npc.type.custom.WarehouseNPC
import cn.charlotte.pit.park.Parker
import cn.charlotte.pit.perk.type.boost.*
import cn.charlotte.pit.perk.type.prestige.*
import cn.charlotte.pit.perk.type.shop.*
import cn.charlotte.pit.perk.type.streak.beastmode.BeastModeMegaStreak
import cn.charlotte.pit.perk.type.streak.beastmode.RAndRKillStreak
import cn.charlotte.pit.perk.type.streak.beastmode.TacticalRetreatKillStreak
import cn.charlotte.pit.perk.type.streak.beastmode.ToughSkinKillStreak
import cn.charlotte.pit.perk.type.streak.grandfinale.ApostleForTheGesusKillStreak
import cn.charlotte.pit.perk.type.streak.grandfinale.AssuredStrikeKillStreak
import cn.charlotte.pit.perk.type.streak.grandfinale.GrandFinaleMegaStreak
import cn.charlotte.pit.perk.type.streak.grandfinale.LeechKillStreak
import cn.charlotte.pit.perk.type.streak.hermit.*
import cn.charlotte.pit.perk.type.streak.highlander.GoldNanoFactoryKillStreak
import cn.charlotte.pit.perk.type.streak.highlander.HighlanderMegaStreak
import cn.charlotte.pit.perk.type.streak.highlander.KhanateKillStreak
import cn.charlotte.pit.perk.type.streak.highlander.WitherCraftKillStreak
import cn.charlotte.pit.perk.type.streak.nonpurchased.*
import cn.charlotte.pit.perk.type.streak.tothemoon.GoldStack
import cn.charlotte.pit.perk.type.streak.tothemoon.SuperStreaker
import cn.charlotte.pit.perk.type.streak.tothemoon.ToTheMoonMegaStreak
import cn.charlotte.pit.perk.type.streak.tothemoon.XPStack
import cn.charlotte.pit.perk.type.streak.uber.UberStreak
import cn.charlotte.pit.quest.type.*
import cn.charlotte.pit.runnable.*
import cn.charlotte.pit.scoreboard.Scoreboard
import cn.charlotte.pit.sound.impl.*
import cn.charlotte.pit.tab.TabHandle
import cn.charlotte.pit.trash.TrashManager
import cn.charlotte.pit.util.menu.ButtonListener
import cn.charlotte.pit.util.nametag.NametagHandler
import cn.charlotte.pit.util.scoreboard.Assemble
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.PluginDescriptionFile
import cn.charlotte.pit.register.EnchantedConstructor
import cn.charlotte.pit.register.ItemConstructor
import cn.charlotte.pit.register.PerkConstructor
import cn.charlotte.pit.movement.iSpigot

object PitHook {
    @JvmStatic
    val gitVersion = "4a3f7b"

    @JvmStatic
    val itemVersion = "golf_uuid"
    fun init() {
        loadConfig()
        loadParker()
        loadOperator()
        loadItemFactory()
        loadActionBar()
        loadEnchants()
        filter()
        loadPerks()
        loadItems()
        loadNameTag()
        loadScoreBoard()
        loadQuests()
        loadEvents()
        registerListeners()
        loadRunnable()
        registerSounds()

        loadCommands()
        loadTab()
        loadNpcs()

        TrashManager.start()

        Bukkit.getPluginManager().getPlugin("PlaceholderAPI")?.let {
            PitPapiHook.register()
            ItemPapiHook.register()
        }

        val description = ThePit.getInstance().description

        val field = PluginDescriptionFile::class.java.getDeclaredField("version")
        field.isAccessible = true
        field.set(description, gitVersion)

        ActionBarDisplayRunnable.start()

        KingsQuests.enable()

        CDKData.loadAllCDKFromData()

        Bukkit.getPluginManager().registerEvents(SewersRunnable, ThePit.getInstance())
        SewersRunnable.runTaskTimer(ThePit.getInstance(), 10L, 10L)
        //CleanupDupeEnch0525Runnable.runTaskTimer(ThePit.getInstance(), 20L, 20L)
        //SpecialPlayerRunnable.runTaskTimer(ThePit.getInstance(), 1L, 1L)
        //PrivatePlayerRunnable.runTaskTimer(ThePit.getInstance(),1L,1L)
        PlayerPointsAPI.init()
        println("Done")
        Bukkit.getScheduler().runTaskLater(ThePit.getInstance(), { checkBlackList() }, 40L)
        Bukkit.getScheduler().runTaskLater(ThePit.getInstance(), { loaded = true }, 20L)

    }

    private fun checkBlackList() {
    }


    private fun loadParker() {
        ThePit.getInstance().parker = Parker();
    }

    private fun filter() {
        NewConfiguration.forbidEnchant.forEach { i ->
            val toString = i
            ThePit.getInstance().sendLogs("Unregistering $toString");
            ThePit.getInstance().enchantmentFactor.unregister(null, toString);
        }
    }

    private fun loadActionBar() {
        ThePit.getInstance().actionBarManager = ActionBarManager();
    }

    private fun loadItemFactory() {
        ThePit.getInstance().itemFactory = ItemFactory();
    }

    private fun loadOperator() {
        ThePit.getInstance().profileOperator = ProfileOperator(ThePit.getInstance());
    }

    private fun loadCommands() {

        val plugin = ThePit.getInstance()
        Bukkit.getPluginCommand("view")?.tabCompleter =
            org.bukkit.command.TabCompleter { sender, command, alias, args ->
                if (args.size == 1) {
                    Bukkit.getOnlinePlayers()
                        .map { it.name }
                        .filter { it.startsWith(args[0], ignoreCase = true) }
                        .toList()
                } else {
                    emptyList()
                }
            }

        LiteBukkitFactory.builder()
            .commands(
                PitAdminSimpleCommand(),
                PitAdminCommands(),
                PitCommands(),
                PitItemCommands(),
                PitAdminDupeFixCommands(),
                PitTrashCommands(),
                DebugCommands()
            )
            .settings {
                it.nativePermissions(true)
            }
            .message(LiteBukkitMessages.INVALID_USAGE) { inv, ctx ->
                return@message "§c用法: ".plus(buildString {
                    if (ctx.schematic.isOnlyFirst) {
                        append(ctx.schematic.first())
                    } else {
                        appendLine()
                        ctx.schematic.all().forEach {
                            appendLine(" §c$it")
                        }
                    }
                })
            }
            .message(LiteBukkitMessages.INVALID_NUMBER) { input ->
                "§c错误的数字: $input"
            }
            .message(LiteBukkitMessages.MISSING_PERMISSIONS, "Unknown command. Type \"/help\" for help.")
            .message(LiteBukkitMessages.PLAYER_NOT_FOUND) { input ->
                "§c未找到名为 $input 的玩家"
            }
            .message(LiteBukkitMessages.PLAYER_ONLY, "§cOnly Player Use")
            .validator(ValidatorScope.of(HandHasItemValidator::class.java), HandHasItemValidator())
            .annotations {
                it.processor { invoker ->
                    invoker.on(HandHasItem::class.java) { handHasItem, metaHolder/*, executorProvider*/ ->
                        metaHolder.meta().also { meta ->
                            meta.put(metaKey, handHasItem)
                            meta.listEditor(Meta.VALIDATORS).add(HandHasItemValidator::class.java).apply()
                        }
                    }
                }
            }.build()
    }

    private fun loadRunnable() {


        //AnnouncementRunnable.runTaskTimerAsynchronously(ThePit.getInstance(), 0, 40 * 60)
        TickHandler().runTaskTimer(ThePit.getInstance(), 1, 1)
        AsyncTickHandler().runTaskTimerAsynchronously(ThePit.getInstance(), 1, 1)
        GoldDropRunnable().runTaskTimer(ThePit.getInstance(), 20, 20)

         ProtectRunnable().runTaskTimer(ThePit.getInstance(), 20, 20)

        FreeExpRunnable().runTaskTimer(ThePit.getInstance(), 20 * 60 * 15, 20 * 60 * 15)
        NightVisionRunnable().runTaskTimer(ThePit.getInstance(), 20, 20)

        PacketHologramRunnable().runTaskTimerAsynchronously(ThePit.getInstance(), 20, 20)

        LeaderBoardRunnable().runTaskTimerAsynchronously(ThePit.getInstance(), 0, 10 * 20 * 60)
        BountyRunnable().runTaskTimerAsynchronously(
            ThePit.getInstance(),
            5,
            NewConfiguration.bountyTickInterval.toLong()
        )
    }


    fun loadConfig() {
        try {
            println("Loaded config...")

            NewConfiguration.loadFile()
            NewConfiguration.load()
            TabConfiguration.loadFile()
            TabConfiguration.load()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadTab() {
        if (TabConfiguration.enable) {
            TabHandle().fetchTab()
        }
    }

    private fun loadItems() {
        val clazzList = mutableListOf(
            MythicBowItem::class.java,
            MythicSwordItem::class.java,
            MythicLeggingsItem::class.java,
            AngelChestplate::class.java,
            ArmageddonBoots::class.java,
            BountySolventPotion::class.java,
            ChunkOfVileItem::class.java,
            FunkyFeather::class.java,
            GoldenHelmet::class.java,
            LuckyGem::class.java,
            JewelSword::class.java,
            JumpBoostPotion::class.java,
            MythicRepairKit::class.java,
            Sceptre::class.java,
            PitCactus::class.java,
            SpireSword::class.java,
            MusicalRune::class.java,
            SpireArmor::class.java,
            SuperPackage::class.java,
            TotallyLegitGem::class.java,
            GlobalAttentionGem::class.java,
            UberDrop::class.java,
            MythicEnchantingTable::class.java,
            //下水道
            Rubbish::class.java,
            Milk::class.java,
            SpeedEggs::class.java,
        )
        try {
            var itemFactor = ThePit.getInstance().itemFactor
            itemFactor.registerItems(clazzList)
            Bukkit.getScheduler().runTaskLater(ThePit.getInstance(), {
                itemFactor.registerItems(ItemConstructor.getItems())
            }, 10)
        } catch (e: Exception) {
            println("An error occurred: ${e.message}")
        }
    }

}

private fun loadEnchants() {
    val enchantmentFactor = ThePit.getInstance().enchantmentFactor

    val classes = mutableListOf(
        //addon
        AngelArmsEnchant::class.java,
        //new
        VengeanceSpiritEnchant::class.java,
        SacredShieldEnchant::class.java,
        GuardianAngelEnchant::class.java,
        EnduringWillEnchant::class.java,
        ThornsReflectEnchant::class.java,
        ElementalFuryEnchant::class.java,
        zaijian::class.java,
        BattlefieldMedicEnchant::class.java,
        ThunderArrowEnchant::class.java,
        UndeadArrowEnchant::class.java,
        LastShadowLeapForward::class.java,
        //end
        ComboUnpredictablyEnchant::class.java,
        ComboDazzlingGoldEnchant::class.java,
        NightFallEnchant::class.java,
        MysticRealmEnchant::class.java,
        TheSwiftWindEnchant::class.java,
        SoulEarterEnchant::class.java,
        ICantWait::class.java,
        PinDownEnchant::class.java,
        PhantomShieldEnchant::class.java,
        KingKillersEnchant::class.java,
        //end
        ClubRodEnchant::class.java,
        GrandmasterEnchant::class.java,
        LuckOfPondEnchant::class.java,
        RogueEnchant::class.java,
        Plutocrat::class.java,
        Regularity::class.java,
        ThinkOfThePeopleEnchant::class.java,
        NewDealEnchant::class.java,
        ReallyToxicEnchant::class.java,
        SingularityEnchant::class.java,
        CurseEnchant::class.java,
        GrimReaperEnchant::class.java,
        HedgeFundEnchant::class.java,
        MindAssaultEnchant::class.java,
        MiseryEnchant::class.java,
        SanguisugeEnchant::class.java,
        SomberEnchant::class.java,
        SpiteEnchant::class.java,
        ComboVenomEnchant::class.java,
        DeathKnellEnchant::class.java,
        GoldenHandcuffsEnchant::class.java,
        EvilWithinEnchant::class.java,
        GuardianEnchant::class.java,
        JerryEnchant::class.java,
        JerryEnchant2::class.java,
        JerryEnchant3::class.java,
        JerryEnchant4::class.java,
        JerryEnchant5::class.java,
        JerryEnchant6::class.java,
        JerryEnchant7::class.java,
        AntiAbsorptionEnchant::class.java,
        AntiBowSpammerEnchantP::class.java,
        AntiBowSpammerEnchantW::class.java,
        AntiMythicismEnchant::class.java,
        ArrowArmoryEnchant::class.java,
        BerserkerEnchant::class.java,
        BillyEnchant::class.java,
        BooBooEnchant::class.java,
        BountyHunterEnchant::class.java,
        BreakTheMirror::class.java,
        BowComboEnchant::class.java,
        BruiserEnchant::class.java,
        BulletTimeEnchant::class.java,
        ComboDamageEnchant::class.java,
        ComboHealEnchant::class.java,
        ComboSwiftEnchant::class.java,
        CounterJanitorEnchant::class.java,
        CounterOffensiveEnchant::class.java,
        CreativeEnchant::class.java,
        CriticallyFunkyEnchant::class.java,
        CriticallyRichEnchant::class.java,
        CrushEnchant::class.java,
        EnergyStorage::class.java,
        EvasionEnchant::class.java,
        DavidAndGoliathEnchant::class.java,
        DiamondAllergyEnchant::class.java,
        DiamondBreakerEnchant::class.java,
        ElectrolytesEnchant::class.java,
        EndlessQuiverEnchant::class.java,
        FractionalReserveEnchant::class.java,
        GoldExplorerEnchant::class.java,
        GutsEnchant::class.java,
        HermesEnchant::class.java,
        HuntTheHunterEnchant::class.java,
        LifeStealEnchant::class.java,
        LureEnchant::class.java,
        MirrorEnchant::class.java,
        MixedCombatEnchant::class.java,
        NotGladiatorEnchant::class.java,
        OverHealEnchant::class.java,
        PantsRadarEnchant::class.java,
        ParasiteEnchant::class.java,
        PebbleEnchant::class.java,
        PeroxideEnchant::class.java,
        PitMBAEnchant::class.java,
        PitPocketEnchant::class.java,
        PowerEnchant::class.java,
        ProtectionEnchant::class.java,
        PurpleGoldEnchant::class.java,
        ReaperEnchant::class.java,
        ResentmentEnchant::class.java,
        RespawnAbsorptionEnchant::class.java,
        RespawnResistanceEnchant::class.java,
        RustBowEnchant::class.java,
        Endeavor::class.java,
        GravityBog::class.java,
        ListEnchant::class.java,
        Outnumbered::class.java,

        ProtectResentment::class.java,
        CriticalEnchant::class.java,
        DoomPact::class.java,
        SerpentBlade::class.java,
        Verdict::class.java,
        Scurry::class.java,
        Winter::class.java,
        SuperPeroxide::class.java,
        Thorns::class.java,
        UniversalNature::class.java,
        WeatherSpector::class.java,
        DesperationEnchant::class.java,
        SelfCheckoutEnchant::class.java,
        SharkEnchant::class.java,
        SharpnessEnchant::class.java,
        SierraEnchant::class.java,
        BadApple::class.java,
        FugueInDMinor::class.java,
        EverybodyDanceNow::class.java,
        SniperEnchant::class.java,
        Coward::class.java,
        SpeedyKillEnchant::class.java,
        SprintDrainEnchant::class.java,
        StrikeGoldEnchant::class.java,
        BowVomit::class.java,
        FlowerDance::class.java,
        GerudoValley::class.java,
        MortalKombat::class.java,
        ThornsEnchant::class.java,
        GirlsBandCry::class.java,
        RainbowTylenol::class.java,
        ThumpEnchant::class.java,
        TNTEnchant::class.java,
        TrueDamageArrowEnchant::class.java,
        UnBreakEnchant::class.java,
        WaspEnchant::class.java,
        WisdomEnchant::class.java,
        BlazingAngelEnchant::class.java,
        DJBundlePVZ::class.java,
        EchoOfSnowlandPEnchant::class.java,
        EchoOfSnowlandWEnchant::class.java,
        EmergencyColonyEnchant::class.java,
        PrimordialStrikerEnchant::class.java,
        KFCBoomerEnchant::class.java,
        LaserEnchant::class.java,
        MultiExchangeLocationEnchant::class.java,
        OPDamageEnchant::class.java,
        VerminEnchant::class.java,
        PowerAngelEnchant::class.java,
        StarJudgementEnchant::class.java,
        SuperLaserEnchant::class.java,
        SuperSlimeEnchant::class.java,
        TestEnchant::class.java,
        AbsorptionEnchant::class.java,
        ArchangelEnchant::class.java,
        AssassinEnchant::class.java,
        BloodFeatherEnchant::class.java,
        BillionaireEnchant::class.java,
        ComboStrikeEnchant::class.java,
        ComboStunEnchant::class.java,
        DivineMiracleEnchant::class.java,
        EnderBowEnchant::class.java,
        ExecutionerEnchant::class.java,
        FightOrDieEnchant::class.java,
        GambleEnchant::class.java,
        BounceBowEnchant::class.java,
        Angel::class.java,

        GomrawsHeartEnchant::class.java,
        HealerEnchant::class.java,
        HealShieldEnchant::class.java,
        HemorrhageEnchant::class.java,
        LuckyShotEnchant::class.java,
        MegaLongBowEnchant::class.java,
        PaparazziEnchant::class.java,
        PullBowEnchant::class.java,
        SlimeEnchant::class.java,
        SnowballsEnchant::class.java,
        BrokenStringEnchant::class.java,
        AntiAssassinationEnchant::class.java,
        BloodDonationEnchant::class.java,
        SolitudeEnchant::class.java,
        SpeedyHitEnchant::class.java,
        ThePunchEnchant::class.java,
        VolleyEnchant::class.java,
        End::class.java,
        SoulRipperEnchant::class.java,
        AceOfSpades::class.java,
        Brakes::class.java,
        ComboStolen::class.java,
        BreachingChargeEnchant::class.java,
        WitheredAndPiercingThroughTheHeart::class.java,
        TrotEnchant::class.java,
        ComboLadder::class.java,
        ComboComa::class.java,
        ExchangeEnchant::class.java,
        FantasyEnchant::class.java,
        FatalTempoEnchant::class.java,
        JudgmentStrike::class.java,
        ExplosiveCrossbowEnchant::class.java,
        Angel::class.java,
        CoinGloriousEnchant::class.java,
        LocExchange::class.java,
        LunarDeity::class.java,
        MoreWisdomEnchant::class.java,
        NightMareEnchant::class.java,
        RadiantShootingEnchant::class.java,
        RedShark::class.java,
        RetroGravityMicrocosmEnchant::class.java,
        SacredArrowEnchant::class.java,
        ShockerEnchant::class.java,
        StrangeEnchant::class.java,
        VitalEchoEnchant::class.java,

        //new
        DemonHenEnchant::class.java,
        //下水道附魔
        AegisEnchant::class.java,
        TrashPandaEnchant::class.java,
        HiddenGemsEnchant::class.java,
        EliminatePowerEnchant::class.java,
        SpywareEnchant::class.java,
    )
    music.apply {
        add(JerryEnchant())
        add(JerryEnchant2())
        add(JerryEnchant3())
        add(JerryEnchant4())
        add(JerryEnchant5())
        add(JerryEnchant6())
        add(JerryEnchant7())
    }
    enchantmentFactor.init(classes)
    EnchantedConstructor.apply {
        Bukkit.getScheduler().runTaskLater(ThePit.getInstance(), {
            enchantmentFactor.init(getEnchantments())
        }, 10);
    }
}

private fun loadScoreBoard() {
    val assemble = Assemble(ThePit.getInstance(), Scoreboard())
    assemble.ticks = 1
}

private fun loadNameTag() {
    val nametagHandler = NametagHandler(ThePit.getInstance(), NameTagImpl())
    nametagHandler.ticks = 20
}

private fun loadPerks() {
    val perkFactory = ThePit.getInstance().perkFactory
    val classes = mutableListOf(
        BowBoostPerk::class.java,
        BuildBattlerBoostPerk::class.java,
        CoinBoostPerk::class.java,
        CoinContractBoostPerk::class.java,
        CoinPrestigeBoostPerk::class.java,
        DmgReduceBoostPerk::class.java,
        ElGatoBoostPerk::class.java,
        MeleeBoostPerk::class.java,
        XPBoostPerk::class.java,
        XPContractBoostPerk::class.java,
        XPPrestigeBoostPerk::class.java,
        ArrowArmoryPerk::class.java,
        AssistantToTheStreakerPerk::class.java,
        AutoBuyPerk::class.java,
        BarbarianPerk::class.java,
        BeastModeBundlePerk::class.java,
        BountySolventShopPerk::class.java,
        CelebrityPerk::class.java,
        CloakRoomPerk::class.java,
        CombatSpadePerk::class.java,
        ContractorPerk::class.java,
        CoolPerk::class.java,
        CoopCatPerk::class.java,
        DiamondLeggingsShopPerk::class.java,
        DirtyPerk::class.java,
        DivineInterventionPerk::class.java,
        ExtraEnderchestPerk::class.java,
        ExtraHeartPerk::class.java,
        ExtraKillStreakSlotPerk::class.java,
        ExtraPerkSlotPerk::class.java,
        FastPassPerk::class.java,
        GoingFurtherPerk::class.java,
        FirstAidEggPerk::class.java,
        FirstStrikePerk::class.java,
        FishClubPerk::class.java,
        GoldPickaxePerk::class.java,
        GrandFinaleBundlePerk::class.java,
        HeresyPerk::class.java,
        HermitBundlePerk::class.java,
        HighlanderBundlePerk::class.java,
        ImpatientPerk::class.java,
        IronPackShopPerk::class.java,
        JumpBoostShopPerk::class.java,
        KungFuKnowledgePerk::class.java,
        MarathonPerk::class.java,
        MonsterPerk::class.java,
        MythicismPerk::class.java,
        MythicDropPerk::class.java,
        ObsidianStackShopPerk::class.java,
        OlympusPerk::class.java,
        PantsBundleShopPerk::class.java,
        SwrodBundleShopPerk::class.java,
        BowBundleShopPerk::class.java,
        PromotionPerk::class.java,
        PureRage::class.java,
        RamboPerk::class.java,
        RawNumbersPerk::class.java,
        ReconPerk::class.java,
        ScamArtistPerk::class.java,
        SelfConfidencePerk::class.java,
        TacticalInsertionsPerk::class.java,
        TastySoupPerk::class.java,
        TenacityPerk::class.java,
        TheWayPerk::class.java,
        ThickPerk::class.java,
        ToTheMoonBundle::class.java,
        YummyPerk::class.java,
        ArrowRecoveryPerk::class.java,
        BountyHunterPerk::class.java,
        FishingRodPerk::class.java,
        GladiatorPerk::class.java,
        GoldenHeadPerk::class.java,
        GoldMinerPerk::class.java,
        LuckyDiamondPerk::class.java,
        MinerPerk::class.java,
        OverHealPerk::class.java,
        SafetyFirstPerk::class.java,
        SafetySecondPerk::class.java,
        StrengthPerk::class.java,
        TrickleDownPerk::class.java,
        VampirePerk::class.java,
        BeastModeMegaStreak::class.java,
        //   MonsterKillStreak::class.java,
        RAndRKillStreak::class.java,
        TacticalRetreatKillStreak::class.java,
        ToughSkinKillStreak::class.java,
        ApostleForTheGesusKillStreak::class.java,
        AssuredStrikeKillStreak::class.java,
        GrandFinaleMegaStreak::class.java,
        LeechKillStreak::class.java,
        AuraOfProtectionKillStreak::class.java,
        GlassPickaxeKillStreak::class.java,
        HermitMegaStreak::class.java,
        IceCubeKillStreak::class.java,
        PungentKillStreak::class.java,
        GoldNanoFactoryKillStreak::class.java,
        HighlanderMegaStreak::class.java,
        KhanateKillStreak::class.java,
        WitherCraftKillStreak::class.java,
        ArquebusierKillStreak::class.java,
        ExpliciousKillStreak::class.java,
        FightOrFlightKillStreak::class.java,
        OverDriveMegaStreak::class.java,
        SecondGappleKillStreak::class.java,
        SpongeSteveKillStreak::class.java,
        UberStreak::class.java,
        ToTheMoonMegaStreak::class.java,
        GoldStack::class.java,
        XPStack::class.java,
        SuperStreaker::class.java,
//        Despot::class.java,
    )

    perkFactory.init(classes as Collection<Class<*>>?)
    Bukkit.getScheduler().runTaskLater(ThePit.getInstance(), {
        perkFactory.init(PerkConstructor.getPerks())
    }, 10)
}

private fun registerSounds() {
    listOf(
        DoubleStreakSound,
        TripleStreakSound,
        QuadraStreakSound,
        StreakSound,
        GemsSound,
        CherrySound,
        SuccessfullySound
    ).forEach {
        ThePit.getInstance().soundFactory.registerSound(it)
    }
}

private fun loadNpcs() {
    val npc = ThePit.getInstance().npcFactory
    Bukkit.getServer().pluginManager.registerEvents(npc, ThePit.getInstance())
    npc.init(
        listOf(
            GenesisAngelNpc::class.java,
            GenesisDemonNpc::class.java,
            KeeperNPC::class.java,
            MailNpc::class.java,
            PerkNPC::class.java,
            PrestigeNPC::class.java,
            QuestNpc::class.java,
            ShopNPC::class.java,
            StatusNPC::class.java
        )
    )
    println("load Npc...")

    loadCustomEntityNpcs()
}

private fun loadCustomEntityNpcs() {
    val customNpcFactory = ThePit.getInstance().customEntityNPCFactory
    Bukkit.getServer().pluginManager.registerEvents(customNpcFactory, ThePit.getInstance())
    customNpcFactory.init(
        listOf(
            SewersNpc::class.java,
            WarehouseNPC::class.java,
        )
    )
    println("load Custom Entity NPCs...")
}


private fun loadQuests() {
    val questFactory = ThePit.getInstance().questFactory
    val classes = listOf<Class<*>>(
        DeepInfiltration::class.java,
        DestoryArmor::class.java,
        DryBlood::class.java,
        HighValueTarget::class.java,
        KeepSilence::class.java,
        LowEfficiency::class.java,
        LowHealth::class.java,
        SinkingMoonlight::class.java
    )
    questFactory.init(classes)
}

private fun loadEvents() {
    val eventFactory = ThePit.getInstance().eventFactory
    val classes = listOf<Class<*>>(
        HamburgerEvent::class.java,
        RagePitEvent::class.java,
        RedVSBlueEvent::class.java,
//        BlockHeadEvent::class.java,
//        SpireEvent::class.java,
        AuctionEvent::class.java,
        CakeEvent::class.java,
        DragonEggsEvent::class.java,
        HuntEvent::class.java,

        CarePackageEvent::class.java,
        EveOneBountyEvent::class.java,
        QuickMathEvent::class.java,
        SquadsEvent::class.java,
        /*        DoubleRewardsEvent::class.java,*/
        RespawnFamilyEvent::class.java
    )

    eventFactory.init(classes)
}

private fun registerListeners() {
    val classes = listOf<Class<*>>(

        CombatListener::class.java,
        ExpireListener::class.java,
        GameEffectListener::class.java,
        DataListener::class.java,
        EnderChestListener::class.java,
        ChatListener::class.java,
        ShopItemListener::class.java,
        PlayerListener::class.java,
        ProtectListener::class.java,
        PantsBundleShopButton::class.java,
        SwordBundleShopButton::class.java,
        BowBundleShopButton::class.java,
        CombatSpadeShopButton::class.java,
        MailSendListener::class.java,
        ButtonListener::class.java,
        GenesisCombatListener::class.java,
        TradeListener::class.java,
        WarehouseListener::class.java,
        TrashListener::class.java,
        iSpigot::class.java,
        HologramListener::class.java,
    )
    for (aClass in classes) {
        try {
            val o = aClass.getConstructor().newInstance()
            Bukkit.getPluginManager().registerEvents(o as Listener, ThePit.getInstance())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    ProtocolLibrary.getProtocolManager().asynchronousManager
        .registerAsyncHandler(PacketListener()).start()

    if (Bukkit.getPluginManager().getPlugin("MythicMobs") != null) {
        Bukkit.getPluginManager().registerEvents(MythicMobListener, ThePit.getInstance());
    }
}
