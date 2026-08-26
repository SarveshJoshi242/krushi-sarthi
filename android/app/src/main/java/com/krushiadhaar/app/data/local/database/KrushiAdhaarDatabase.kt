package com.krushiadhaar.app.data.local.database
import androidx.room.Database
import androidx.room.RoomDatabase
import com.krushiadhaar.app.data.local.entity.*
import com.krushiadhaar.app.data.local.dao.*

@Database(entities = [MarketplaceListingEntity::class, OrderEntity::class, WeatherEntity::class, DiseaseScanEntity::class, FarmEntity::class, FieldEntity::class, CropCycleEntity::class], version = 1, exportSchema = false)
abstract class KrushiAdhaarDatabase : RoomDatabase() {
    abstract fun farmDao(): FarmDao
    abstract fun fieldDao(): FieldDao
    abstract fun cropCycleDao(): CropCycleDao
    abstract fun weatherDao(): WeatherDao
    abstract fun diseaseScanDao(): DiseaseScanDao
    abstract fun marketplaceDao(): MarketplaceDao
}
