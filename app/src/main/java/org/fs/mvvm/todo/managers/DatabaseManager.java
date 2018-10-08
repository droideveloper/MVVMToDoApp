/*
 * todos Copyright (C) 2016 Fatih.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fs.mvvm.todo.managers;

import android.content.Context;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import io.reactivex.Observable;
import io.reactivex.Single;
import java.sql.SQLException;
import java.util.List;
import java8.util.function.Predicate;
import org.fs.mvvm.todo.BuildConfig;
import org.fs.mvvm.todo.R;
import org.fs.mvvm.todo.entities.Entry;

public final class DatabaseManager extends AbstractOrmliteHelper
    implements IDatabaseManager {

  private final static String DB_NAME = "todos";
  private final static int DB_VERSION = 1;

  private RuntimeExceptionDao<Entry, Integer> entryDao;

  public DatabaseManager(Context context) {
    super(context, DB_NAME, DB_VERSION, R.raw.ormlite_config);
  }

  @Override protected void createTables(ConnectionSource cs) throws SQLException {
    TableUtils.createTableIfNotExists(cs, Entry.class);
  }

  @Override protected void dropTables(ConnectionSource cs) throws SQLException {
    TableUtils.dropTable(cs, Entry.class, true);
  }

  @Override public Observable<List<Entry>> all() {
    return Observable.just(createIfEntryDaoIsNull())
        .map(RuntimeExceptionDao::queryForAll);
  }

  @Override public Single<List<Entry>> all(Predicate<Entry> filter) {
    return Observable.just(createIfEntryDaoIsNull())
        .map(RuntimeExceptionDao::queryForAll)
        .flatMap(Observable::fromIterable)
        .filter(filter::test)
        .toList();
  }

  @Override public Single<Entry> firstOrDefault(Predicate<Entry> filter) {
    return all()
        .flatMap(Observable::fromIterable)
        .filter(filter::test)
        .firstOrError();
  }

  @Override public Observable<Boolean> create(Entry entry) {
    return Observable.just(createIfEntryDaoIsNull())
        .map(dao -> dao.createOrUpdate(entry).isCreated());
  }

  @Override public Observable<Boolean> update(Entry entry) {
    return Observable.just(createIfEntryDaoIsNull())
        .map(dao -> dao.createOrUpdate(entry).isUpdated());
  }

  @Override public Observable<Boolean> delete(Entry entry) {
    return Observable.just(createIfEntryDaoIsNull())
        .map(dao -> dao.delete(entry) == 1);
  }

  @Override protected boolean isLogEnabled() {
    return BuildConfig.DEBUG;
  }

  @Override protected String getClassTag() {
    return DatabaseManager.class.getSimpleName();
  }

  private RuntimeExceptionDao<Entry, Integer> createIfEntryDaoIsNull() {
    if (entryDao == null) {
      entryDao = getRuntimeExceptionDao(Entry.class);
    }
    return entryDao;
  }
}