package com.bergerkiller.bukkit.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.nbt.CommonTag;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.nbt.CommonTagList;
import com.bergerkiller.bukkit.common.nbt.NBTTagInfo;
import com.bergerkiller.bukkit.common.reflection.classes.NBTRef;

import net.minecraft.server.v1_5_R2.Entity;
import net.minecraft.server.v1_5_R2.FoodMetaData;
import net.minecraft.server.v1_5_R2.InventoryEnderChest;
import net.minecraft.server.v1_5_R2.MobEffect;
import net.minecraft.server.v1_5_R2.NBTCompressedStreamTools;
import net.minecraft.server.v1_5_R2.NBTTagCompound;
import net.minecraft.server.v1_5_R2.NBTTagList;
import net.minecraft.server.v1_5_R2.PlayerInventory;

/**
 * Contains utility functions for dealing with NBT data
 */
public class NBTUtil {
	/**
	 * Creates an NBT Tag handle to store the data specified in<br>
	 * All primitive types, including byte[] and int[], and list/maps are supported
	 * 
	 * @param name of the handle
	 * @param data to store in this handle initially
	 * @return new handle
	 */
	public static Object createHandle(String name, Object data) {
		return NBTTagInfo.findInfo(data).createHandle(name, data);
	}

	/**
	 * Obtains the raw data from an NBT Tag handle.
	 * NBTTagList and NBTTagCompound return a List and Map of NBT Tags respectively.
	 * If a null handle is specified, null is returned to indicate no data.
	 * 
	 * @param nbtTagHandle to get the value of
	 * @return the NBTTag data
	 */
	public static Object getData(Object nbtTagHandle) {
		if (nbtTagHandle == null) {
			return null;
		}
		return NBTTagInfo.findInfo(nbtTagHandle).getData(nbtTagHandle);
	}

	/**
	 * Gets the type Id of the tag used to identify it
	 * 
	 * @param nbtTagHandle to read from
	 * @return tag type id
	 */
	public static byte getTypeId(Object nbtTagHandle) {
		if (nbtTagHandle == null) {
			return (byte) 0;
		}
		return NBTRef.getTypeId.invoke(nbtTagHandle).byteValue();
	}

	/**
	 * Reads an NBTTagCompound handle from an input stream
	 * 
	 * @param stream to read from
	 * @return NBTTagCompound
	 * @throws IOException
	 */
	public static Object readCompound(InputStream stream) throws IOException {
		return NBTCompressedStreamTools.a(stream);
	}

	/**
	 * Writes an NBTTagCompound to an output stream
	 * 
	 * @param compound to write
	 * @param stream to write to
	 * @throws IOException
	 */
	public static void writeCompound(Object compound, OutputStream stream) throws IOException {
		NBTCompressedStreamTools.a((NBTTagCompound) compound, stream);
	}

	/**
	 * Reads a mob effect from an NBT Tag Compound
	 * 
	 * @param compound to read a mob effect from
	 * @return Loaded MobEffect
	 */
	public static Object loadMobEffect(CommonTagCompound compound) {
		return MobEffect.b((NBTTagCompound) compound.getHandle());
	}

	/**
	 * Saves entity data to the Tag Compound specified
	 * 
	 * @param entity to save
	 * @param compound to save to, use null to save to a new compound
	 * @return the compound to which was saved
	 */
	public static CommonTagCompound saveEntity(org.bukkit.entity.Entity entity, CommonTagCompound compound) {
		if (compound == null) {
			compound = new CommonTagCompound();
		}
		((Entity) Conversion.toEntityHandle.convert(entity)).e((NBTTagCompound) compound.getHandle());
		return compound;
	}

	/**
	 * Loads an entity with data from the Tag Compound specified
	 * 
	 * @param entity to load
	 * @param compound to load from
	 */
	public static void loadEntity(org.bukkit.entity.Entity entity, CommonTagCompound compound) {
		((Entity) Conversion.toEntityHandle.convert(entity)).f((NBTTagCompound) compound.getHandle());
	}

	/**
	 * Saves food meta data to the NBT Tag Compound specified
	 * 
	 * @param foodMetaData to save
	 * @param compound to save to, use null to save to a new compound
	 * @return the compound to which was saved
	 */
	public static CommonTagCompound saveFoodMetaData(Object foodMetaData, CommonTagCompound compound) {
		if (compound == null) {
			compound = new CommonTagCompound();
		}
		((FoodMetaData) foodMetaData).b((NBTTagCompound) compound.getHandle());
		return compound;
	}

	/**
	 * Loads Food Meta Data with data from the Tag Compound specified
	 * 
	 * @param foodMetaData to load
	 * @param compound to load from
	 */
	public static void loadFoodMetaData(Object foodMetaData, CommonTagCompound compound) {
		((FoodMetaData) foodMetaData).a((NBTTagCompound) compound.getHandle());
	}

	/**
	 * Saves inventory data to the tag list specified
	 * 
	 * @param inventory to save
	 * @param list to save to, use null for a new list
	 * @return the saved tag list
	 */
	public static CommonTagList saveInventory(org.bukkit.inventory.Inventory inventory, CommonTagList list) {
		final Object inventoryHandle = Conversion.toInventoryHandle.convert(inventory);
		if (inventoryHandle == null) {
			throw new IllegalArgumentException("This kind of inventory lacks a handle to load");
		}
		if (inventoryHandle instanceof PlayerInventory) {
			if (list == null) {
				list = new CommonTagList();
			}
			((PlayerInventory) inventoryHandle).a((NBTTagList) list.getHandle());
		} else if (inventoryHandle instanceof InventoryEnderChest) {			
			Object handle = ((InventoryEnderChest) inventoryHandle).h();
			if (list == null) {
				return (CommonTagList) CommonTag.create(handle);
			} else {
				List<?> data = (List<?>) getData(handle);
				for (Object elem : data) {
					list.addValue(elem);
				}
				return list;
			}
		} else {
			throw new IllegalArgumentException("This kind of inventory has an unknown type of handle: " + inventoryHandle.getClass().getName());
		}
		return list;
	}

	/**
	 * Loads inventory data from the Tag List specified
	 * 
	 * @param inventory to load
	 * @param list to load from
	 */
	public static void loadInventory(org.bukkit.inventory.Inventory inventory, CommonTagList list) {
		final Object inventoryHandle = Conversion.toInventoryHandle.convert(inventory);
		NBTTagList nbt = (NBTTagList) list.getHandle();
		if (inventoryHandle == null) {
			throw new IllegalArgumentException("This kind of inventory lacks a handle to save");
		} else if (inventoryHandle instanceof PlayerInventory) {
			((PlayerInventory) inventoryHandle).b(nbt);
		} else if (inventoryHandle instanceof InventoryEnderChest) {
			((InventoryEnderChest) inventoryHandle).a(nbt);
		} else {
			throw new IllegalArgumentException("This kind of inventory has an unknown type of handle: " + inventoryHandle.getClass().getName());
		}
	}
}
