package com.nimbusbg.audiobookcanvas.data.repository;

import androidx.room.TypeConverter;

import com.nimbusbg.audiobookcanvas.data.local.entities.BlockState;

import java.util.Date;

public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
    
    
    @TypeConverter
    public static String blockStateToString(BlockState state) {
        return state.toString();
    }
    
    @TypeConverter
    public static BlockState blockStateFromString(String state)
    {
        return BlockState.valueOf(state);
    }
}
