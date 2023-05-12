package com.nimbusbg.audiobookcanvas.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

import java.util.List;

public abstract class BaseDao<T> {

    private String tableName;
    private Class<T> entityClass;


    public BaseDao(String tableName, Class<T> entityClass) {
        this.tableName = tableName;
        this.entityClass = entityClass;
    }

    /**
     * Insert an object in the database.
     *
     * @param entity the object to be inserted.
     * @return the new rowId for the inserted item
     */
    @Insert
    public abstract long insert(T entity);

    /**
     * Insert an array of objects in the database.
     *
     * @param entities the objects to be inserted.
     * @return array or a collection of long values instead, with each value as the rowId for one of the inserted items
     */
    @Insert
    public abstract long insert(List<T> entities);

    /**
     * Update an object from the database.
     *
     * @param entity the object to be updated
     */
    @Update
    public abstract void update(T entity);

    /**
     * Update an object from the database.
     *
     * @param entities the list of objects to be updated
     * @return the number of entities affected
     */
    @Update
    public abstract int update(List<T> entities);

    /**
     * Delete an object from the database
     *
     * @param entity the object to be deleted
     */
    @Delete
    public abstract void delete(T entity);

    /**
     * Delete an object from the database
     *
     * @param entities the list of objects to be deleted
     * @return the number of entities deleted
     */
    @Delete
    public abstract int delete(List<T> entities);

    @RawQuery(observedEntities = entityClass)
    protected abstract int deleteAll(SupportSQLiteQuery query);
    public int deleteAll() {
        SimpleSQLiteQuery query = new SimpleSQLiteQuery("DELETE FROM $tableName");
        return deleteAll(query);
    }

    @RawQuery (observedEntities = entityClass)
    protected abstract LiveData<List<T>> getAllEntities(SupportSQLiteQuery query);
    public LiveData<List<T>> getAllEntities()
    {
        SimpleSQLiteQuery query = new SimpleSQLiteQuery("SELECT * from $tableName");

        return getAllEntities(query);
    }

    @RawQuery (observedEntities = entityClass)
    protected abstract LiveData<T> getEntityById(SupportSQLiteQuery query);
    public LiveData<T> getEntityById(int id)
    {
        SimpleSQLiteQuery query = new SimpleSQLiteQuery("SELECT * from $tableName WHERE id = :id");
        return getEntityById(query);
    }

    @RawQuery (observedEntities = entityClass)
    protected abstract LiveData<List<T>> getEntitiesByIds(SupportSQLiteQuery query);
    public LiveData<List<T>> getEntitiesByIds(int[] ids)
    {
        SimpleSQLiteQuery query = new SimpleSQLiteQuery("SELECT * from $tableName WHERE id IN (:ids)");
        return getEntitiesByIds(query);
    }
}
