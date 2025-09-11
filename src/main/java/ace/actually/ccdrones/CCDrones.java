package ace.actually.ccdrones;

import ace.actually.ccdrones.blocks.DroneWorkbenchBlock;
import ace.actually.ccdrones.blocks.DroneWorkbenchBlockEntity;
import ace.actually.ccdrones.blocks.DroneWorkbenchPeripheral;
import ace.actually.ccdrones.entities.DroneAPI;
import ace.actually.ccdrones.entities.DroneEntity;
import ace.actually.ccdrones.menu.DroneMenu;
import ace.actually.ccdrones.entities.nanodrone.NanodroneAPI;
import ace.actually.ccdrones.entities.nanodrone.NanodroneEntity;
import ace.actually.ccdrones.items.*;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.component.ComputerComponent;
import dan200.computercraft.api.peripheral.PeripheralLookup;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import dan200.computercraft.shared.network.container.ContainerData;
import dan200.computercraft.shared.platform.PlatformHelper;
import dan200.computercraft.shared.platform.RegistrationHelper;
import dan200.computercraft.shared.platform.RegistryEntry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class CCDrones implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("ccdrones");

    public static final CreativeModeTab TAB = FabricItemGroup.builder()
            .icon(() -> new ItemStack(CCDrones.DRONE_ITEM))
            .title(Component.empty().append("CC: Drones"))
            .build();

    public static HashMap<String,Item> UPGRADE_MAP  = new HashMap<>();
    public static final TagKey<Block> SURVEYABLE = TagKey.create(Registries.BLOCK, new ResourceLocation("ccdrones", "surveyable"));
    @Override
    public void onInitialize() {

        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB,new ResourceLocation("ccdrones","tab"),TAB);
        registerBlocks();
        registerEntities();
        registerPeripherals();
        registerItems();
        registerAPIs();
        registerMenu();
    }

    public static final DroneWorkbenchBlock DRONE_WORKBENCH_BLOCK = new DroneWorkbenchBlock(BlockBehaviour.Properties.of());

    private void registerBlocks()
    {
        Registry.register(BuiltInRegistries.BLOCK,new ResourceLocation("ccdrones","drone_workbench"),DRONE_WORKBENCH_BLOCK);
    }

    public static final DroneItem DRONE_ITEM = new DroneItem(new Item.Properties());
    public static final NanodroneItem NANODRONE_ITEM = new NanodroneItem(new Item.Properties());
    public static final CrowbarItem CROWBAR_ITEM = new CrowbarItem();
    private void registerItems() {
        int v = BuiltInRegistries.ITEM.size();
        Registry.register(BuiltInRegistries.ITEM,new ResourceLocation("ccdrones","drone_workbench"),new BlockItem(DRONE_WORKBENCH_BLOCK,new Item.Properties()));
        Registry.register(BuiltInRegistries.ITEM,new ResourceLocation("ccdrones","drone_item"),DRONE_ITEM);
        Registry.register(BuiltInRegistries.ITEM,new ResourceLocation("ccdrones","nanodrone_item"),NANODRONE_ITEM);
        Registry.register(BuiltInRegistries.ITEM,new ResourceLocation("ccdrones","crowbar"),CROWBAR_ITEM);
        registerUpgrades();
        for (int i = v; i < BuiltInRegistries.ITEM.size(); i++) {
            int finalI = i;
            ItemGroupEvents.modifyEntriesEvent(BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(TAB).get()).register(a->
            {
                a.accept(BuiltInRegistries.ITEM.byId(finalI));
            });
        }
    }


    public static final Item MINE_UPGRADE = new Item(new Item.Properties());
    public static final Item CARRY_UPGRADE = new Item(new Item.Properties());
    public static final Item SURVEY_UPGRADE = new Item(new Item.Properties());
    public static final Item MODEM_UPGRADE = new Item(new Item.Properties());

    private void registerUpgrades() {
        Registry.register(BuiltInRegistries.ITEM,new ResourceLocation("ccdrones","mine_upgrade"),MINE_UPGRADE);
        Registry.register(BuiltInRegistries.ITEM,new ResourceLocation("ccdrones","carry_upgrade"),CARRY_UPGRADE);
        Registry.register(BuiltInRegistries.ITEM,new ResourceLocation("ccdrones","survey_upgrade"),SURVEY_UPGRADE);
        Registry.register(BuiltInRegistries.ITEM,new ResourceLocation("ccdrones","modem_upgrade"),MODEM_UPGRADE);
    }

    private void registerEntities()
    {
        FabricDefaultAttributeRegistry.register(DRONE, DroneEntity.createMobAttributes());
        FabricDefaultAttributeRegistry.register(NANODRONE, NanodroneEntity.createMobAttributes());
    }

    private void registerPeripherals()
    {
        PeripheralLookup.get().registerForBlockEntity((a,b)->new DroneWorkbenchPeripheral(a),DRONE_WORKBENCH_BE);
    }

    //Components
    public static final ComputerComponent<DroneEntity> DRONEAPI = ComputerComponent.create("cclink", "drone");
    public static final ComputerComponent<NanodroneEntity> NANODRONEAPI = ComputerComponent.create("cclink", "nanodrone");

    private void registerAPIs() {
        ComputerCraftAPI.registerAPIFactory(computer -> {
            var entity = computer.getComponent(DRONEAPI);
            return entity == null ? null : new DroneAPI(entity);
        });

        ComputerCraftAPI.registerAPIFactory(computer -> {
            var entity = computer.getComponent(NANODRONEAPI);
            return entity == null ? null : new NanodroneAPI(entity);
        });
    }

    private static final RegistrationHelper<MenuType<?>> REGISTRY = PlatformHelper.get().createRegistrationHelper(Registries.MENU);

    public static final RegistryEntry<MenuType<DroneMenu>> DRONE_MENU = REGISTRY.register("drone",
            () -> ContainerData.toType(ComputerContainerData::new, DroneMenu::ofMenuData));

    public static void registerMenu() {
        REGISTRY.register();
    }

    //entities
    public static final EntityType<DroneEntity> DRONE = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            new ResourceLocation("ccdrones", "drone"),
            FabricEntityTypeBuilder.create(MobCategory.MISC,DroneEntity::new).dimensions(EntityDimensions.fixed(0.5f,0.5f)).build()
    );

    public static final EntityType<NanodroneEntity> NANODRONE = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            new ResourceLocation("ccdrones", "nanodrone"),
            FabricEntityTypeBuilder.create(MobCategory.MISC, NanodroneEntity::new).dimensions(EntityDimensions.fixed(0.25f,0.25f)).build()
    );

    public static BlockEntityType<DroneWorkbenchBlockEntity> DRONE_WORKBENCH_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            new ResourceLocation("ccdrones", "drone_workbench_block_entity"),
            FabricBlockEntityTypeBuilder.create(DroneWorkbenchBlockEntity::new, DRONE_WORKBENCH_BLOCK).build()
    );
}
